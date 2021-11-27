import java.math.BigInteger;

/**
 * Contains methods for IBAN validation and formatting
 */
public final class IbanUtils {

    private static final int MAX_LEN = 34;

    private static final int KZ_IBAN_LENGTH = 20;

    private IbanUtils() {}

    public static boolean isValidIban(String iban) {
        if (iban == null || iban.length() == 0) {
            return false;
        }

        int ibanLength = iban.length();

        if (ibanLength > MAX_LEN) {
            return false;
        }

        // first 4 characters are moved to the end of the string
        // letters are transformed to the integers using following algorithm:
        // A = 10, B = 11, ..., Z = 35
        // resulting string is converted to integer and divided by 97
        // if modulo is 1 then IBAN is valid

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < ibanLength; i++) {

            // start processing from fifth character and process first 4 characters at the end
            int idx;
            if (i > (ibanLength - 5)) {
                idx = i - (ibanLength - 4);
            } else {
                idx = i + 4;
            }

            char ch = iban.charAt(idx);
            if (isNum(ch)) {
                builder.append(ch);
            } else if (isUpperCaseLetter(ch)) {
                int num = ibanLetterToInt(ch);
                builder.append(num);
            } else {
                // must be digit or uppercase letter
                return false;
            }
        }

        return new BigInteger(builder.toString())
                .mod(new BigInteger("97"))
                .equals(new BigInteger("1"));
    }

    // Check that country code is KZ, length is 20, and validate checksum

    public static boolean isValidKzIban(String iban) {
        if (iban == null) {
            return false;
        }
        if (iban.length() != KZ_IBAN_LENGTH) {
            return false;
        }
        if (!iban.startsWith("KZ")) {
            return false;
        }
        return isValidIban(iban);
    }

    // Format IBAN in groups of four characters separated by a single space

    public static String printableFormat(String iban) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < iban.length(); i++) {
            if ((i != 0) && (i % 4 == 0)) {
                // add space after every fourth character
                builder.append(" ");
            }
            builder.append(iban.charAt(i));
        }
        return builder.toString();
    }

    private static boolean isNum(char ch) {
        return ch >= '0' && ch <= '9';
    }

    private static boolean isUpperCaseLetter(char ch) {
        return ch >= 'A' && ch <= 'Z';
    }

    // special conversion for IBAN letters
    // A = 10, ... , Z = 35
    private static int ibanLetterToInt(char ch) {
        return (ch - 'A') + 10;
    }
}
