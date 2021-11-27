import java.util.Optional;

/**
 * Validation and formatting of MSISDNs (aka mobile subscribers number) according to E.164
 * Only for KZ specific msisdns
 */
public final class MsisdnUtils {

    private static final String VALID_MSISDN_REGEX = "^77[0-9]{9}$";

    private MsisdnUtils() {}


    /**
     * Check if string is valid MSISDN or not
     * @param msisdn MSISDN string
     * @return Is input a valid MSISDN or not
     */
    public static boolean isValidMsisdn(String msisdn) {
        if (msisdn == null) {
            return false;
        }
        return msisdn.matches(VALID_MSISDN_REGEX);
    }

    // Will try to convert input string to valid MSISDN eliminating the following characters: '-', '(', ')', space and leading '+'

    public static Optional<String> toMsisdn(String input) {
        String normalized = normalizeInput(input);

        if (isValidMsisdn(normalized)) {
            return Optional.of(normalized);
        }

        int inputLength = normalized.length();
        char[] inputChars = normalized.toCharArray();

        if (!onlyNumbers(inputChars)) {
            return Optional.empty();
        }

        if (inputLength == 11 && normalized.startsWith("87")) {
            return Optional.of("7" + normalized.substring(1));
        }

        return Optional.empty();
    }

    private static boolean onlyNumbers(char[] charArr) {
        boolean isNum = false;
        for (char c : charArr) {
            isNum = c >= '0' && c <= '9';
            if (!isNum) {
                break;
            }
        }
        return isNum;
    }

    // remove next characters from input:
    // * leading +
    // * (
    // * )
    // * -
    // * spaces
    private static String normalizeInput(String input) {
        char[] inputChars = input.toCharArray();
        StringBuilder builder = new StringBuilder();
        for (char ch : inputChars) {
            switch (ch) {
                case ' ':
                case '-':
                case '(':
                case ')':
                    break;
                default:
                    builder.append(ch);
            }
        }
        if (builder.length() > 0 && builder.charAt(0) == '+') {
            builder.deleteCharAt(0);
        }
        return builder.toString();
    }
}
