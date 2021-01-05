/*
 * https://www.hackerrank.com/challenges/recursion-in-c/problem
 */

#include <stdio.h>


int find_nth_term(int n, int a, int b, int c);

int find_nth_term(int n, int a, int b, int c) {
  if (n < 0) {
    return c;
  }
  return find_nth_term((n - 3) - 1, b, c, a + b + c); // n - 3 because we already has first 3 inputs
}

int main(void) {
  int n, a, b, c;

  scanf("%d %d %d %d", &n, &a, &b, &c);
  int ans = find_nth_term(n, a, b, c);

  printf("%d\n", ans); 
  return 0;
}

