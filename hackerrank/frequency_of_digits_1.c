/*
 * https://www.hackerrank.com/challenges/frequency-of-digits-1/problem
 */

#include <stdio.h>
#include <string.h>

#define BUF_LEN 1500

int main(void) {
  char buf[BUF_LEN] = {0};
  scanf("%s", buf);
  size_t len = strlen(buf);

  int counters[10] = {0};

  for (size_t i = 0; i < len; i++) {
    char ch = buf[i];
    if (ch >= '0' && ch <= '9') {
      int idx = ch - '0';
      counters[idx]++;
    }
  }

  for (int i = 0; i < 10; i++) {
    printf("%d ", counters[i]);
  }
}

