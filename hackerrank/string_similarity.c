/*
 * https://www.hackerrank.com/challenges/string-similarity/problem
 */


#include <stdio.h>
#include <string.h>

#define BUF_SIZE 100001


int calc_similarity(const char *str);

int calc_similarity(const char *str) {
  int res = 0;
  size_t len = strlen(str);
  res += len; // first suffix is whole string

  for (size_t i = 1; i < len; i++) {
    int sim = 0;
    for (size_t j = 0; j < len - i; j++) {
      char ch1 = str[j]; // from original string
      char ch2 = str[i+j]; // from suffix string
      if (ch1 == ch2) {
        sim++;
      } else {
        break;
      }
    }
    res += sim;
  }
  return res;
}


int main(void) {
  int t;
  scanf("%d", &t);
  
  int res[t];
  char buf[BUF_SIZE];
  for (int i = 0; i < t; i++) {
    memset(buf, 0, BUF_SIZE);
    scanf("%s", buf);
    res[i] = calc_similarity(buf);
  }

  for (int i = 0; i < t; i++) {
    printf("%d\n", res[i]);
  }
}

