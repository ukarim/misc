use std::collections::HashMap;
use std::env;
use std::fs;
use std::path::{Path, PathBuf};
use std::process::exit;

macro_rules! exit_with_error {
    ($($arg:tt)*) => {
        eprintln!($($arg)*);
        exit(1);
    };
}

const OUT_DIR: &str = "out/";
const PAGE_EXTENSION: &str = "html";

const FILE_IGNORE_PATTERNS: [&str; 6] = [
    "./.git",
    "./.gitignore",
    "./out",
    "./htmlbuild.rs",
    "./htmlbuild",
    "./layout.html",
];

fn main() {
    // create out directory if needed
    let out_path = Path::new(OUT_DIR);
    if !out_path.exists() {
        if let Err(err) = fs::create_dir(out_path) {
            exit_with_error!("Cannot create 'out' directory. Reason: {}", err);
        }
    }

    // read the layout template
    let layout_str = match fs::read_to_string("./layout.html") {
        Ok(layout_str) => layout_str,
        Err(err) => {
            exit_with_error!("Cannot read 'layout.html' file. Reason: {}", err);
        }
    };

    let args: Vec<String> = env::args().skip(1).collect(); // skip executable name

    // if script has been invoked without command line params then scan entire project dir
    // otherwise process only given files
    let file_path_list = if args.is_empty() {
        walk_dir_recursive(&Path::new(".").to_path_buf()) // start from current directory
    } else {
        args.iter()
            .map(|arg| {
                let mut path_buf = PathBuf::with_capacity(arg.len());
                path_buf.push(arg);
                if !path_buf.exists() {
                    exit_with_error!("File {} doesn't exists", path_buf.display());
                }
                path_buf
            })
            .collect()
    };

    for file_path in file_path_list {
        if let Some(ext) = file_path.extension() {
            if ext == PAGE_EXTENSION {
                let parse_result = parse_page(&file_path);
                substitute_layout(file_path, &layout_str, parse_result);
                continue;
            }
        }
        copy_to_out(file_path);
    }
}

fn copy_to_out(path: PathBuf) {
    let mut out_path = PathBuf::new();
    out_path.push(OUT_DIR);
    out_path.push(path.as_path());

    // create subdirs in out folder if necessary
    if let Some(parent_path) = out_path.parent() {
        if let Err(err) = fs::create_dir_all(&parent_path) {
            exit_with_error!(
                "Cannot create directory {}. Reason: {}",
                parent_path.display(),
                err
            );
        }
    }

    println!("copy {} to {}", path.display(), OUT_DIR);

    if let Err(err) = fs::copy(&path, out_path) {
        exit_with_error!("Cannot copy file {}. Reason: {}", path.display(), err);
    }
}

fn parse_page(path: &PathBuf) -> HashMap<String, String> {
    let page_str = match fs::read_to_string(&path) {
        Ok(page_str) => page_str,
        Err(err) => {
            exit_with_error!("Cannot read page file {}. Reason: {}", path.display(), err);
        }
    };

    let mut parse_result = HashMap::new();

    match page_str.split_once("---") {
        Some((header, content)) => {
            // parse page header for variables
            for header_line in header.lines() {
                if let Some((var_name, var_value)) = header_line.split_once(":") {
                    parse_result.insert(var_name.trim().to_string(), var_value.trim().to_string());
                }
            }

            match content.split_once("---") {
                Some((css_styles, html_content)) => {
                    parse_result.insert("css_styles".to_string(), css_styles.to_string());
                    parse_result.insert("content".to_string(), html_content.to_string());
                }
                None => {
                    parse_result.insert("content".to_string(), content.to_string());
                }
            }
        }
        None => {
            parse_result.insert("content".to_string(), page_str);
        }
    }
    return parse_result;
}

fn substitute_layout(path: PathBuf, layout_str: &String, params: HashMap<String, String>) {
    let mut out_html = String::with_capacity(layout_str.len()); // at least as layout.html
    let mut chars = layout_str.chars().peekable();
    let mut line_counter = 1;
    while let Some(ch) = chars.next() {
        match ch {
            '{' => {
                let next_ch = chars.next();
                // if next char is not { then don't need to perform variable substitution
                if next_ch != Some('{') {
                    if next_ch == Some('\n') {
                        line_counter = line_counter + 1;
                    }
                    out_html.push(ch);
                    out_html.push(next_ch.unwrap());
                    continue;
                }

                // consume variable name
                let mut var_name = String::new();
                while let Some(ch) = chars.next() {
                    match ch {
                        '}' => {
                            let next_ch = chars.next();
                            if next_ch == Some('}') {
                                break;
                            } else if next_ch == Some('\n') {
                                exit_with_error!("Wrong variable definition in layout.html at line {}. No closing brackets", line_counter);
                            } else {
                                var_name.push(ch);
                                var_name.push(next_ch.unwrap());
                            }
                        }
                        '\n' => {
                            exit_with_error!("Wrong variable definition in layout.html at line {}. No closing brackets", line_counter);
                        }
                        ' ' | '\t' => {}
                        _ => {
                            var_name.push(ch);
                        }
                    }
                }
                if let Some(var_value) = params.get(&var_name) {
                    out_html.push_str(var_value);
                }
            }
            '\n' => {
                line_counter = line_counter + 1;
                out_html.push(ch);
            }
            _ => {
                out_html.push(ch);
            }
        }
    }

    let mut out_path = PathBuf::new();
    out_path.push(OUT_DIR);
    out_path.push(&path);
    out_path.set_extension("html");

    match fs::write(&out_path, out_html) {
        Ok(()) => {}
        Err(err) => {
            exit_with_error!(
                "Cannot write to output file {}. Reason: {}",
                &out_path.display(),
                err
            );
        }
    }
    println!("page processed: {}", path.display());
}

fn walk_dir_recursive(root: &PathBuf) -> Vec<PathBuf> {
    let read_dir = match fs::read_dir(root) {
        Ok(read_dir) => read_dir,
        Err(err) => {
            exit_with_error!(
                "Cannot traverse directory {}. Reason: {}",
                root.display(),
                err
            );
        }
    };

    let mut files: Vec<PathBuf> = Vec::new();

    for entry in read_dir {
        match entry {
            Ok(entry) => {
                let path = entry.path();
                if ignore_file(&path) {
                    continue;
                }
                if path.is_dir() {
                    files.append(&mut walk_dir_recursive(&path)); // recursive call
                } else {
                    files.push(path);
                }
            }
            Err(err) => {
                exit_with_error!(
                    "Cannot read entry of {} directory. Reason: {}",
                    root.display(),
                    err
                );
            }
        }
    }
    return files;
}

fn ignore_file(path: &PathBuf) -> bool {
    let mut ignore = false;
    for pattern in &FILE_IGNORE_PATTERNS {
        ignore = path.starts_with(pattern);
        if ignore {
            break;
        }
    }
    return ignore;
}
