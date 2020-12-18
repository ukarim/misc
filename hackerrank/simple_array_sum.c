/*
 * Calculate sum of the array
 * First read the array size
 * Then get space separated integers from input and print the sum
 * https://www.hackerrank.com/challenges/simple-array-sum/problem
 */


#include <stdio.h>

int main(void) {
  int n;
  scanf("%d", &n);
  long long c = 0;

  // read n space separated integers from input
  for (int i = 0; i < n; i++) {
    int num;
    scanf("%d", &num); // reads till first space
    c += num; // calc in place
  }

  printf("%lld\n", c);
  return 0;
}

