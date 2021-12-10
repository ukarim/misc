use std::io::{self, BufRead};

fn main() {
    let stdin = io::stdin();
    let mut lines = stdin.lock().lines();
    if let Some(line) = lines.next() {
        let mut timers: Vec<u8> = line
            .unwrap()
            .split(",")
            .map(|part| part.parse::<u8>().unwrap())
            .collect();
        let mut timers_part2 = timers.to_owned();
        part_1(&mut timers);
        part_2(&mut timers_part2);
    }
}

fn part_1(timers: &mut Vec<u8>) {
    for _ in 0..80 {
        let mut new_gen_timers: Vec<u8> = Vec::new();
        for i in 0..timers.len() {
            let timer = timers[i];
            if timer == 0 {
                timers[i] = 6;
                new_gen_timers.push(8);
            } else {
                timers[i] = timer - 1;
            }
        }
        timers.append(&mut new_gen_timers);
    }
    println!("result1: {}", timers.len());
}

fn part_2(timers: &mut Vec<u8>) {
    println!("not implemented yet");
    println!("{:?}", timers);
}
