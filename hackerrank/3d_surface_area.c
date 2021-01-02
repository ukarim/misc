/*
 * https://www.hackerrank.com/challenges/3d-surface-area/problem
 */

#include <stdio.h>

int main(void) {
  int h, w;
  scanf("%d%d", &h, &w);

  int matrix[h][w];

  for (int i = 0; i < h; i++) {
    for (int j = 0; j < w; j++) {
      int n;
      scanf("%d", &n);
      matrix[i][j] = n;
    }
  }

  long int area = 0;

  for (int i = 0; i < h; i++) {
    for (int j = 0; j < w; j++) {
      area += 2; // top and bottom

      int cur = matrix[i][j];

      // up
      if (i == 0) {
        area += cur;
      } else {
        int up = matrix[i-1][j];
        if (cur > up) {
          area += (cur - up);
        }
      }

      // down
      if (i == h - 1) {
        area += cur;
      } else {
        int down = matrix[i+1][j];
        if (cur > down) {
          area += (cur - down);
        }
      }

      // left
      if (j == 0) {
        area += cur;
      } else {
        int left = matrix[i][j-1];
        if (cur > left) {
          area += (cur - left);
        }
      }

      // right
      if (j == w - 1) {
        area += cur;
      } else {
        int right = matrix[i][j+1];
        if (cur > right) {
          area += (cur - right);
        }
      }
    }
  }

  printf("%ld\n", area);
}

