use std::env;
use std::fs::File;
use std::io::{self, BufRead};
use std::path::Path;
use std::process::exit;

struct Point {
    x: usize,
    y: usize,
}

fn main() {
    if let Some(input_file) = env::args().skip(1).next() {
        // skip executable name
        let lines = match read_lines(&input_file) {
            Ok(lines) => lines,
            Err(err) => {
                eprintln!("Cannot read file {} due to {}", input_file, err);
                exit(1);
            }
        };
        let point_pairs: Vec<(Point, Point)> = lines
            .map(|line| to_point_pair(line.unwrap()))
            .filter(|(p1, p2)| p1.x == p2.x || p1.y == p2.y)
            .collect();

        part_1(&point_pairs);
    } else {
        eprintln!("Provide a path to input file as command line argument");
        exit(1);
    }
}

fn part_1(point_pairs: &Vec<(Point, Point)>) {
    let (x, y) = max_coordinates(&point_pairs);
    let mut diagram = create_diagram(x, y);

    for (p1, p2) in point_pairs {
        if p1.x == p2.x {
            // horizontal line
            let (start, end) = to_min_max(p1.y, p2.y);
            for i in start..=end {
                diagram[p1.x][i] = diagram[p1.x][i] + 1;
            }
        }
        if p1.y == p2.y {
            // vertical line
            let (start, end) = to_min_max(p1.x, p2.x);
            for i in start..=end {
                diagram[i][p1.y] = diagram[i][p1.y] + 1;
            }
        }
    }

    let mut counter = 0;
    for row in diagram {
        for col in row {
            if col > 1 {
                counter = counter + 1;
            }
        }
    }

    println!("result1: {}", counter);
}

fn create_diagram(x: usize, y: usize) -> Vec<Vec<usize>> {
    let mut diagram: Vec<Vec<usize>> = Vec::with_capacity(x + 1);
    for _ in 0..(x + 1) {
        let mut inner_vec = Vec::with_capacity(y + 1);
        for _ in 0..(y + 1) {
            inner_vec.push(0);
        }
        diagram.push(inner_vec);
    }
    return diagram;
}

fn to_point_pair(input_line: String) -> (Point, Point) {
    match input_line.split_once("->") {
        Some((part1, part2)) => (to_point(part1), to_point(part2)),
        None => {
            eprintln!("Invalid line {}", input_line);
            exit(1)
        }
    }
}

fn to_point(input: &str) -> Point {
    match input.trim().split_once(",") {
        Some((x, y)) => Point {
            x: x.parse::<usize>().unwrap(),
            y: y.parse::<usize>().unwrap(),
        },
        None => {
            eprintln!("Invalid pair {}", input);
            exit(1)
        }
    }
}

fn to_min_max(n1: usize, n2: usize) -> (usize, usize) {
    if n1 < n2 {
        (n1, n2)
    } else {
        (n2, n1)
    }
}

fn max_coordinates(vents: &Vec<(Point, Point)>) -> (usize, usize) {
    let mut max_x: usize = 0;
    let mut max_y: usize = 0;
    for (p1, p2) in vents {
        if p1.x > max_x {
            max_x = p1.x;
        }
        if p1.y > max_y {
            max_y = p1.y;
        }
        if p2.x > max_x {
            max_x = p2.x;
        }
        if p2.y > max_y {
            max_y = p2.y;
        }
    }
    return (max_x, max_y);
}

// copy paste from rust docs
// The output is wrapped in a Result to allow matching on errors
// Returns an Iterator to the Reader of the lines of the file.
fn read_lines<P>(filename: P) -> io::Result<io::Lines<io::BufReader<File>>>
where
    P: AsRef<Path>,
{
    let file = File::open(filename)?;
    Ok(io::BufReader::new(file).lines())
}

// fn print_diagram(diagram: &Vec<Vec<usize>>) {
//     for y in 0..(diagram[0].len()) {
//         for x in 0..(diagram.len()) {
//             print!("{} ", diagram[x][y]);
//         }
//         println!("");
//     }
// }
