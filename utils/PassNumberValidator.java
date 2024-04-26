// See https://en.wikipedia.org/wiki/Machine-readable_passport
// 

public class PassNumberValidator {

  public static void main(String[] args) {
    String passNum = "N167xxxxxx";
    System.out.printf("Validity of passport num %s is %s\n", passNum, isValidPassNum(passNum));
  }

  private static boolean isValidPassNum(String passNum) {
    char[] chars = passNum.toCharArray();
    int checksum = 0;
    int[] weights = new int[] { 7, 3, 1 };
    for (int i = 0; i < chars.length - 1; i++) {
      char ch = chars[i];
      int val;
      if (isNum(ch)) {
        val = toNum(ch);
      } else {
        val = letterToInt(ch);
      }
      int weight = weights[i % weights.length];
      checksum += (val * weight);
    }
    return toNum(chars[chars.length - 1]) == (checksum % 10);
  }

  private static boolean isNum(char ch) {
    return ch >= '0' && ch <= '9';
  }

  private static int toNum(char ch) {
    return ch - '0';
  }

  // special conversion for letters
  // A = 10, ... , Z = 35
  private static int letterToInt(char ch) {
    return (ch - 'A') + 10;
  }
}