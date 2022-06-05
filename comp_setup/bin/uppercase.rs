use std::env;
use std::io::{self, BufRead};

fn main() {
    let args: Vec<String> = env::args().skip(1).collect(); // skip executable name
    if !args.is_empty() {
        for arg in args {
            println!("{}", arg.to_uppercase());
        }
    } else {
        // read input from stdin
        let stdin = io::stdin();
        let mut lines = stdin.lock().lines();
        let mut out_buf = String::new();
        while let Some(line) = lines.next() {
            let lowercase = line.unwrap().to_uppercase();
            out_buf.push_str(&lowercase);
            out_buf.push('\n');
        }
        print!("{}", out_buf);
    }
}
