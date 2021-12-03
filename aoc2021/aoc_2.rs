use std::io::{self, BufRead};
use std::process::exit;

enum Movement {
    Horizontal(i32),
    Vertical(i32),
}

fn main() {
    let stdin = io::stdin();
    let lines = stdin.lock().lines();
    let movements: Vec<Movement> = lines
        .map(|line| {
            let line = line.unwrap();
            match line.split_once(" ") {
                Some((part1, part2)) => {
                    let value = part2.parse::<i32>().unwrap();
                    return match part1 {
                        "forward" => Movement::Horizontal(value),
                        "down" => Movement::Vertical(value),
                        "up" => Movement::Vertical(0 - value),
                        _ => {
                            eprintln!("invalid command: {}", part1);
                            exit(1)
                        }
                    };
                }
                _ => {
                    eprintln!("invalid input: {}", line);
                    exit(1);
                }
            }
        })
        .collect();

    part_1(&movements);
    part_2(movements);
}

fn part_1(movements: &Vec<Movement>) {
    let mut horizontal_counter = 0;
    let mut vertical_counter = 0;

    for movement in movements {
        match movement {
            Movement::Horizontal(value) => horizontal_counter = horizontal_counter + value,
            Movement::Vertical(value) => vertical_counter = vertical_counter + value,
        }
    }

    println!("result1: {}", horizontal_counter * vertical_counter);
}

fn part_2(movements: Vec<Movement>) {
    let mut aim = 0;
    let mut horizontal_counter = 0;
    let mut vertical_counter = 0;

    for movement in movements {
        match movement {
            Movement::Vertical(value) => aim = aim + value,
            Movement::Horizontal(value) => {
                horizontal_counter = horizontal_counter + value;
                vertical_counter = vertical_counter + (aim * value);
            }
        }
    }

    println!("result2: {}", horizontal_counter * vertical_counter);
}
