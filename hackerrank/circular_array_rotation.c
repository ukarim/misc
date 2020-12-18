/*
 * https://www.hackerrank.com/challenges/circular-array-rotation/problem
 */

#include <stdio.h>


int main(void) {
  int n; // num of elements in the array
  int k; // rotation count
  int q; // number of queries

  scanf("%d", &n);
  scanf("%d", &k);
  scanf("%d", &q);

  // read array
  int arr[n];
  for (int i = 0; i < n; i++) {
    int t;
    scanf("%d", &t);
    arr[i] = t;
  }

  // read query indicies
  int queries[q];
  for (int i = 0; i < q; i++) {
    int t;
    scanf("%d", &t);
    queries[i] = t;
  }

  // calc rotated arr
  int rotated_arr[n];
  for (int i = 0; i < n; i++) {
    int new_idx = (i + k) % n;
    rotated_arr[new_idx] = arr[i];
  }

  // print queries
  for (int i = 0; i < q; i++) {
    printf("%d\n", rotated_arr[queries[i]]);
  }

}

