use std::io::{self, BufRead};

fn main() {
    let stdin = io::stdin();
    let lines = stdin.lock().lines();
    let measurements: Vec<i32> = lines
        .map(|line| line.unwrap().parse::<i32>().unwrap())
        .collect();

    part_1(&measurements);

    part_2(measurements);
}

fn part_1(measurements: &Vec<i32>) {
    let mut counter = 0;
    let mut prev_depth: Option<i32> = None;

    for depth in measurements {
        if let Some(prev_depth) = prev_depth {
            if *depth > prev_depth {
                counter = counter + 1;
            }
        }
        prev_depth = Some(*depth);
    }

    println!("result: {}", counter);
}

fn part_2(measurements: Vec<i32>) {
    let length = measurements.len();
    let mut windows: Vec<i32> = Vec::new();
    for (i, v) in measurements.iter().enumerate() {
        if i > length - 3 {
            break;
        }
        let v1 = measurements.get(i + 1).unwrap();
        let v2 = measurements.get(i + 2).unwrap();
        windows.push(v + v1 + v2);
    }

    part_1(&windows);
}
