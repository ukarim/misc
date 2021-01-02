/*
 * https://www.hackerrank.com/challenges/larrys-array/problem
 */

#include <stdio.h>
#include <stdbool.h>


void process_input(void);

void process_input(void) {
  int size;
  scanf("%d", &size);
  int arr[size];
  for (int i = 0; i < size; i++) {
    int n;
    scanf("%d", &n);
    arr[i] = n;
  }

  // the key to solution is to count swap number.
  // if swap count is even then return YES, otherwise NO
  int swap_c = 0;
  for (int i = 0; i < size; i++) {
    bool swapped = false;
    for (int j = 0; j < size-1; j++) {
      int a = arr[j];
      int b = arr[j+1];
      if (a > b) {
        arr[j] = b;
        arr[j+1] = a;
        swap_c++;
        swapped = true;
      }
    }
    if (!swapped) {
      break;
    }
  }
  char *out = swap_c % 2 == 0 ? "YES\n" : "NO\n";
  printf(out);
}

int main(void) {
  int input_c;
  scanf("%d", &input_c);

  for (int i = 0; i < input_c; i++) {
    process_input();
  }
}

