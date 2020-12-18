/*
 * https://www.hackerrank.com/challenges/sherlock-and-valid-string/problem
 */

#include <stdio.h>
#include <string.h>
#include <stdbool.h>


#define STR_MAX_LEN 100001
#define ALPHABET_LEN 26


int main(void) {
  char str[STR_MAX_LEN];
  // read input
  fgets(str, STR_MAX_LEN, stdin);
  size_t str_len = strlen(str);
  if (str[str_len - 1] == '\n') {
    str[str_len - 1] = '\0';
  }

  // stores count of chars [a-z]
  int map[ALPHABET_LEN] = {0};

  for (size_t i = 0; i < str_len; i++) {
    int map_idx = str[i] - 'a'; // convert to zero based array idx
    int num = map[map_idx];
    map[map_idx] = num + 1;
  }

  for (int i = 0; i < ALPHABET_LEN; i++) {
    printf("%c - %d\n", 'a' + i, map[i]);
  }

  int uniq_char_count = 0;
  for (int i = 0; i < ALPHABET_LEN; i++) {
    if (map[i] != 0) {
      uniq_char_count++;
    }
  }

  // bubble desc sort for simplicity (array always of size 26)
  for (int i = 0; i < ALPHABET_LEN; i++) {
    for (int j = 0; j < ALPHABET_LEN - 1; j++) {
      int cur = map[j];
      int next = map[j+1];
      if (cur < next) {
        map[j] = next;
        map[j+1] = cur;
      }
    }
  }

  int max_occ = map[0];
  int min_occ = map[uniq_char_count - 1];

  bool is_valid = false;

  if (max_occ == min_occ) {
    // the same occurance of chars
    is_valid = true;
  } else if ((max_occ - min_occ) == 1 && (max_occ > map[1])) {
    // remove one char from top: aaabbbcccc
    is_valid = true;
  } else if ((min_occ == 1) && (max_occ == map[uniq_char_count - 2])) {
    // max_occ is average
    // remove one char from bottom: aaabbbc
    is_valid = true;
  }

  printf("%s\n", is_valid ? "YES" : "NO");
}

