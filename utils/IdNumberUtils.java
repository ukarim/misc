
/**
 * Contains methods for IIN/BIN validating
 */
public final class IdNumberUtils {

    private static final int ID_NUM_LENGTH = 12;

    // numbers order for first checksum generation
    private static final int[] FIRST_CHECKSUM_INDEXES = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

    // numbers order for second checksum generation
    private static final int[] SECOND_CHECKSUM_INDEXES = new int[] {2, 3, 4, 5, 6, 7, 8, 9, 10, 0, 1};

    // if checksum equals to this value then we should retry
    private static final int RETRY_CHECKSUM = 10;


    private IdNumberUtils() {}

    // Validate IIN (Individual identification number)

    public static boolean isValidIin(String iin) {
        return isValidIdNum(iin, true);
    }

    // Validate BIN (Business identification number)

    public static boolean isValidBin(String bin) {
        return isValidIdNum(bin, false);
    }

    private static boolean isValidIdNum(String input, boolean isCheckForIIN) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }

        if (input.length() != ID_NUM_LENGTH) {
            return false;
        }

        char[] charArray = input.toCharArray();
        int[] numArray = new int[ID_NUM_LENGTH - 1];

        // fill numbers array
        for (int i = 0; i < ID_NUM_LENGTH - 1; i++) {
            char ch = charArray[i];

            if (!isNum(ch)) {
                // must contain only numbers
                // invalid IIN
                return false;
            }

            numArray[i] = toNum(charArray[i]);
        }

        if (isCheckForIIN && numArray[4] > 3) {
            // fifth num for IIN must be 0, 1, 2 or 3
            return false;
        }

        if (!isCheckForIIN && (numArray[4] < 4 || numArray[4] > 6)) {
            // fifth num for BIN must be 4, 5 or 6
            return false;
        }

        int checksum = checksum(numArray, FIRST_CHECKSUM_INDEXES);

        if (checksum == RETRY_CHECKSUM) {
            // second try
            checksum = checksum(numArray, SECOND_CHECKSUM_INDEXES);
        }

        if (checksum == RETRY_CHECKSUM) {
            // no more tries
            return false;
        }

        // compare checksum with latest number
        return toNum(charArray[ID_NUM_LENGTH - 1]) == checksum;
    }

    private static int checksum(int[] nums, int[] indexes) {
        int checksum = 0;
        for (int i = 0; i < indexes.length; i++) {
            int coef = i + 1;
            int idx = indexes[i];
            checksum = checksum + (coef * nums[idx]);
        }
        return checksum % 11;
    }

    private static boolean isNum(char ch) {
        return ch >= '0' && ch <= '9';
    }

    private static int toNum(char ch) {
        return ch - '0';
    }
}
