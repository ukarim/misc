use std::env;
use std::fs;
use std::io;
use std::process::exit;
use std::collections::VecDeque;

const MAX_IDX: usize = 30000;

fn read_char() -> char {
    let mut line = String::new();
    match io::stdin().read_line(&mut line) {
        Ok(_) => {
            let first_char = line.chars().next().unwrap();
            return first_char;
        },
        Err(_) => {
            println!("Cannot read user input");
            exit(1);
        },
    }
}

fn main() {
    let args: Vec<String> = env::args().collect();
    if args.len() < 2 {
        println!("Filename doesn't provided");
        exit(1);
    }
    let filename = &args[1];
    let program_text =match fs::read_to_string(filename) {
        Ok(text) => text,
        Err(_) => {
            println!("Cannot open file {}", filename);
            exit(1)
        }
    };
    let program: Vec<char> = program_text.chars().collect();

    let mut stack: VecDeque<usize> = VecDeque::new();
    let mut jump_map: [usize; MAX_IDX] = [0; MAX_IDX];

    for (i, ch) in program.iter().enumerate() {
        if *ch == '[' {
            stack.push_back(i);
        }
        if *ch == ']' {
            match stack.pop_back() {
                Some(match_idx) => {
                    jump_map[i] = match_idx;
                    jump_map[match_idx] = i;
                },
                None => {
                    println!("Unmatched `]` at index {}", i);
                    exit(1);
                }
            }
        }
    }

    // state of the program
    let mut state_arr: [u8; MAX_IDX] = [0; MAX_IDX];
    let mut cur_idx = 0;

    let program_len = program.len();
    let mut cmd_idx: usize = 0;

    while cmd_idx < program_len {
        let cmd = program[cmd_idx];
        match cmd {
            '[' => {
                if state_arr[cur_idx] == 0 {
                    cmd_idx = jump_map[cmd_idx];
                }
            },
            ']' => {
                if state_arr[cur_idx] != 0 {
                    cmd_idx = jump_map[cmd_idx];
                }
            },
            '>' => {
                cur_idx = cur_idx + 1;
            },
            '<' => {
                cur_idx= cur_idx - 1;
            },
            '+' => {
                let b = state_arr[cur_idx];
                state_arr[cur_idx] = b + 1;
            },
            '-' => {
                let b = state_arr[cur_idx];
                state_arr[cur_idx] = b - 1;
            },
            '.' => {
                let b = state_arr[cur_idx];
                print!("{}", (b as char));
            },
            ',' => {
                let input = read_char();
                state_arr[cur_idx] = input as u8;
            },
            _ => {
                // Brainfuck ignores all characters except the eight commands +-<>[],.
            }
        }
        cmd_idx = cmd_idx + 1;
    }
}
