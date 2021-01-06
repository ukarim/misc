/*
 * https://www.hackerrank.com/challenges/small-triangles-large-triangles/problem
 */

#include <stdio.h>
#include <stdlib.h>
#include <math.h>

struct triangle {
	int a;
	int b;
	int c;
};

typedef struct triangle triangle;

float area(triangle tr);

float area(triangle tr) {
  int a = tr.a;
  int b = tr.b;
  int c = tr.c;
  float p = ((float) (a + b + c)) / 2;
  return sqrt(p* (p - a) * (p - b) * (p - c));
}


void sort_by_area(triangle *tr, int n);

void sort_by_area(triangle *tr, int n) {
  float areas[n];
  // calc areas only once
  for (int i = 0; i < n; i++) {
    areas[i] = area(tr[i]);
  }
  for (int i = 0; i < n; i++) {
    for (int j = 0; j < n - i - 1; j++) {
      triangle t1 = tr[j];
      triangle t2 = tr[j + 1];
      float a1 = areas[j];
      float a2 = areas[j + 1];
      if (a1 > a2) {
        tr[j] = t2;
        tr[j + 1] = t1;
        areas[j] = a2;
        areas[j + 1] = a1;
      }
    }
  }
}

int main(void) {
	int n;
	scanf("%d", &n);
	triangle *tr = malloc(n * sizeof(triangle));
	for (int i = 0; i < n; i++) {
		scanf("%d%d%d", &tr[i].a, &tr[i].b, &tr[i].c);
	}
	sort_by_area(tr, n);
	for (int i = 0; i < n; i++) {
		printf("%d %d %d\n", tr[i].a, tr[i].b, tr[i].c);
	}
	return 0;
}

