// Rust Bytes Challenge Issue #87 String Compression

fn main() {
    let input = "abbcccddddeeeeefg";
    if input.is_empty() {
        println!();
        return;
    }
    let mut chars = input.chars();
    let mut prev = chars.next().unwrap(); // it's safe, string is not empty
    let mut count = 1;
    let mut output = String::new();
    for curr in chars {
        if prev == curr {
            count = count + 1;
        } else {
            output.push(prev);
            output.push_str(&count.to_string());
            prev = curr;
            count = 1;
        }
    }
    // don't forget about final char
    output.push(prev);
    output.push_str(&count.to_string());
    println!("{}", output);
}
