/*
 * https://www.hackerrank.com/challenges/richie-rich/problem
 */

#include <stdio.h>
#include <string.h>
#include <stdbool.h>

#define BUF_SIZE 100001

int main(void) {
  int n, k = 0;
  scanf("%d", &n);
  scanf("%d", &k);

  char buf[BUF_SIZE] = {0};
  scanf("%s", buf);

  bool is_palindrom = true;

  for (int i = 0; i < n; i++) {
    int left_idx = i;
    int right_idx = (n-1) - i;

    if (left_idx > right_idx) {
      // crossed the middle. it's enough
      break;
    }

    char left = buf[left_idx];
    char right = buf[right_idx];

    if (k <= 0) {
      if (left != right) {
        // it's not palindrome, just show -1
        is_palindrom = false;
        break;
      } else {
        // it's may be palindrome. check next pair
        continue;
      }
    }

    if (left == '9' && right == '9') {
      // ideal case
      continue;
    }
    if (left == '9') {
      buf[right_idx] = '9';
      k--;
      continue;
    }
    if (right == '9') {
      buf[left_idx] = '9';
      k--;
      continue;
    }

    if (left_idx == right_idx) {
      // exact the middle.
      // for strings with odd value of length
      buf[left_idx] = '9';
      break;
    }

    // decide swap both left and right
    // or only one of them
    int swap_c = 0;
    for (int j = i+1; j < n; j++) {
      if (j >= ((n-1) - j)) {
        break;
      }
      if (buf[j] != buf[(n-1) - j]) {
        swap_c++;
      }
    }

    if ((k-1) > swap_c) {
      buf[left_idx] = '9';
      buf[right_idx] = '9';
      k = k - 2;
    } else {
      if (left > right) {
        buf[right_idx] = left;
        k--;
      }
      if (right > left) {
        buf[left_idx] = right;
        k--;
      }
    }
  }

  char *out = is_palindrom ? buf : "-1";
  printf("%s\n", out);
}

