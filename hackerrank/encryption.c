/*
 * https://www.hackerrank.com/challenges/encryption/problem
 */

#include <stdio.h>
#include <string.h>
#include <math.h>


#define STR_MAX_LEN 82 // reserve +1 for null separator


int main(void) {
  char input[STR_MAX_LEN];
  // read input
  fgets(input, STR_MAX_LEN, stdin);
  size_t input_len = strlen(input);
  if (input[input_len - 1] == '\n') {
    input[input_len - 1] = '\0';
  }

  char str[input_len];
  int idx = 0;
  for (size_t i = 0; i < input_len; i++) {
    char ch = input[i];
    if (ch != ' ') {
      str[idx++] = ch;
    }
  }
  str[idx] = '\0'; // !!!

  size_t str_len = strlen(str);
  double str_len_root = sqrt(str_len);
  int row = floor(str_len_root);
  int col = ceil(str_len_root);

  while ((row * col) < ((int) str_len)) {
    row++;
  }

  char grid[row][col];

  size_t char_idx = 0;
  for (int i = 0; i < row; i++) {
    for (int j = 0; j < col; j++) {
      char ch = '\0';
      if (char_idx < str_len) {
        ch = str[char_idx++];
      }
      grid[i][j] = ch;
    }
  }

  for (int i = 0; i < col; i++) {
    for (int j = 0; j < row; j++) {
      char ch = grid[j][i];
      if (ch != '\0') {
        printf("%c", ch);
      }
    }
    char *sep = i < (col - 1) ? " " : "\n";
    printf(sep);
  }
}

