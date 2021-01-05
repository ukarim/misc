/*
 * https://www.hackerrank.com/challenges/sorting-array-of-strings/problem
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <unistd.h>

#define BUF_LEN 2501
#define ALPHABET_SIZE 26


void print_arr(char **arr, const int len);

bool compare_by_lex_ord(const char *s1, const char *s2);

bool compare_by_length(const char *s1, const char *s2);

bool compare_by_lex_ord_reverse(const char *s1, const char *s2);

bool compare_by_dist_chars(const char *s1, const char *s2);

void string_sort(char **arr, const int len, bool (*compare)(const char *s1, const char *s2));


void print_arr(char **arr, const int len) {
  for (int i = 0; i < len; i++) {
    printf("%s\n", arr[i]);
  }
  printf("\n");
}

bool compare_by_lex_ord(const char *s1, const char *s2) {
  size_t len1 = strlen(s1);
  size_t len2 = strlen(s2);
  size_t min_len = len1 < len2 ? len1 : len2;

  bool is_prefix = false;
  bool need_swap = false;

  // find first non-equal characters pair and compare them
  for (size_t i = 0; i < min_len; i++) {
    if (s1[i] == s2[i]) {
      is_prefix = true;
      continue;
    }
    need_swap = s1[i] > s2[i];
    is_prefix = false;
    break;
  }
  if (is_prefix && (len1 > len2)) {
    need_swap = true;
  }
  return need_swap;
}

bool compare_by_lex_ord_reverse(const char *s1, const char *s2) { 
  size_t len1 = strlen(s1);
  size_t len2 = strlen(s2);
  size_t min_len = len1 < len2 ? len1 : len2;
  bool need_swap = false;

  // find first non-equal characters pair and compare them
  for (size_t i = 0; i < min_len; i++) {
    if (s1[i] == s2[i]) {
      continue;
    }
    need_swap = s1[i] < s2[i];
    break;
  }
  if (!need_swap && (len1 < len2)) {
    need_swap = true;
  }
  return need_swap;
}

bool compare_by_dist_chars(const char *s1, const char *s2) {
  size_t len1 = strlen(s1);
  size_t len2 = strlen(s2);
  size_t max_len = len1 > len2 ? len1 : len2;

  // char counters for latin letters
  char arr1[ALPHABET_SIZE] = {0};
  char arr2[ALPHABET_SIZE] = {0};

  for (size_t i = 0; i < max_len; i++) {
    if (i < len1) {
      int idx = s1[i] - 'a';
      arr1[idx] = 1;
    }
    if (i < len2) {
      int idx = s2[i] - 'a';
      arr2[idx] = 1;
    }
  }

  int c1 = 0;
  int c2 = 0;
  for (int i = 0; i < ALPHABET_SIZE; i++) {
    c1 += arr1[i];
    c2 += arr2[i];
  }

  bool need_swap = c1 > c2;
  if (c1 == c2) {
    need_swap = compare_by_lex_ord(s1, s2);
  }

  return need_swap;
}

bool compare_by_length(const char *s1, const char *s2) {
  size_t len1 = strlen(s1);
  size_t len2 = strlen(s2);
  bool need_swap = false;
  need_swap = len1 > len2;
  if (len1 == len2) {
    // if length of string is equals then compare lexicographically
    need_swap = compare_by_lex_ord(s1, s2);
  }
  return need_swap;
}


void string_sort(char **arr, const int len, bool (*compare_func)(const char *s1, const char *s2)) {
  for (int i = 0; i < len; i++) {
    for (int j = 0; j < len - i - 1; j++) {
      char *a = arr[j];
      char *b = arr[j + 1];
      if (compare_func(a, b)) {
        arr[j] = b;
        arr[j + 1] = a;
      }
    }
  }
}

int main(void) {
  int n;
  scanf("%d", &n);

  char *arr[n];

  for (int i = 0; i < n; i++) {
    char *buf = malloc(BUF_LEN * sizeof(char));
    if (buf == NULL) {
      printf("cannot allocate memory for input buffer\n");
      exit(1);
    }
    scanf("%s", buf);
    arr[i] = buf;
  }

  string_sort(arr, n, compare_by_lex_ord);
  print_arr(arr, n);

  string_sort(arr, n, compare_by_lex_ord_reverse);
  print_arr(arr, n);

  string_sort(arr, n, compare_by_length);
  print_arr(arr, n);

  string_sort(arr, n, compare_by_dist_chars);
  print_arr(arr, n);
}

