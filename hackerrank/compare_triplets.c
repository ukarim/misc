/*
 * https://www.hackerrank.com/challenges/compare-the-triplets/problem
 */

#include <stdio.h>
#include <stdlib.h>


#define INPUT_SIZE 3


int* read_input(int input_size);

int* read_input(int input_size) {
  int* arr;
  arr = malloc(input_size * sizeof(int));
  if (arr == NULL) {
    printf("cannot allocate memory for array");
    exit(1);
  }
  for (int i = 0; i < input_size; i++) {
    int n;
    scanf("%d", &n);
    arr[i] = n;
  }
  return arr;
}


int main(void) {
  int* a;
  int* b;

  a = read_input(INPUT_SIZE);
  b = read_input(INPUT_SIZE);

  int a_point = 0;
  int b_point = 0;

  for (int i = 0; i < INPUT_SIZE; i++) {
    if (a[i] > b[i]) {
      a_point += 1;
    }
    if (b[i] > a[i]) {
      b_point += 1;
    }
  }

  printf("%d %d\n", a_point, b_point);
}

