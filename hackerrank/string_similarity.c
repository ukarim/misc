/*
 * https://www.hackerrank.com/challenges/string-similarity/problem
 */


#include <stdio.h>
#include <string.h>

#define BUF_SIZE 100001


long int calc_similarity(const char *str);

/* slow version O(n^2)
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
*/

// z algorithm O(n)

long int calc_similarity(const char *str) {
  size_t len = strlen(str);
  long int z[len];
  z[0] = len; // whole string is first suffix

  size_t l, r = 0;

  for (size_t i = 1; i < len; i++) {
    if (i > r) {
      // trivial search
      l = i;
      r = i;
      while (r < len && str[r - l] == str[r]) {
        r++;
      }
      z[i] = r - l;
      r--;
    } else {
      // inside previous suffix
      size_t k = i - l;
      if (z[k] < r - i + 1) {
        z[i] = z[k];
      } else {
        l = i;
        while (r < len && str[r - l] == str[r]){
          r++;
        }
        z[i] = r - l;
        r--;
      }
    }
  }

  long int res = 0;
  for (size_t i = 0; i < len; i++) {
    res += z[i];
  }
  return res;
}

int main(void) {
  int t;
  scanf("%d", &t);
  
  long int res[t];
  char buf[BUF_SIZE];
  for (int i = 0; i < t; i++) {
    memset(buf, 0, BUF_SIZE);
    scanf("%s", buf);
    res[i] = calc_similarity(buf);
  }

  for (int i = 0; i < t; i++) {
    printf("%ld\n", res[i]);
  }
}

