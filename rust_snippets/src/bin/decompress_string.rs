fn main() {
    let input = "a1b2c3d4e5f1g1";
    let (mut output, _, _) = input
        .chars()
        .rev() // for inplace math when dealing with numbers
        .fold((Vec::new(), 0, 0), |(mut acc, num, pos), x| {
            if let Some(digit) = char::to_digit(x, 10) {
                let new_num = num + (digit * 10_u32.pow(pos));
                (acc, new_num, pos + 1)
            } else {
                for _ in 0..num {
                    acc.push(x);
                }
                (acc, 0, 0)
            }
        });
    output.reverse(); // don't forget to reverse
    println!("{}", String::from_iter(output));
}

