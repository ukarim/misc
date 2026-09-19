import java.security.SecureRandom;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/// # Task1
/// Use -Xint flag to disable JIT (important)
///
/// ## Results of test run:
/// - 1 threads: sum = 499487160, time ns = 6175459
/// - 2 threads: sum = 499487160, time ns = 3175125
/// - 4 threads: sum = 499487160, time ns = 1726459
/// - 8 threads: sum = 499487160, time ns = 1472709
///
/// ## Speedup
/// - S_2 = 1.94
/// - S_4 = 3.58
/// - S_8 = 4.19
///
/// ## Effectiveness
/// - E_2 = 0.97
/// - E_4 = 0.49
/// - E_8 = 0.24
///
/// ## Theoretical (when P = 0.95)
/// - S_2 = 1.90
/// - S_4 = 3.47
/// - S_8 = 5.92

class Task1 {

    // just to be sure we work without overflows
    private static final int MAX_NUM = 1000;
    private static final int MAX_LEN = 1_000_000;

    public static void main(String[] args) throws Exception {
        var arr = arrWithRandomInts();

        long t1 = sumNThreads(arr, 1);
        long t2 = sumNThreads(arr, 2);
        long t4 = sumNThreads(arr, 4);
        long t8 = sumNThreads(arr, 8);

        double s2 = ((double) t1) / t2;
        double s4 = ((double) t1) / t4;
        double s8 = ((double) t1) / t8;

        System.out.printf("S_2 = %.2f\n", s2);
        System.out.printf("S_4 = %.2f\n", s4);
        System.out.printf("S_8 = %.2f\n", s8);

        double e2 = s2 / 2;
        double e4 = s2 / 4;
        double e8 = s2 / 8;

        System.out.printf("E_2 = %.2f\n", e2);
        System.out.printf("E_4 = %.2f\n", e4);
        System.out.printf("E_8 = %.2f\n", e8);
    }

    private static long sumNThreads(int[] arr, int nThreads) throws Exception {
        var sum = new AtomicLong();
        var startLatch = new CountDownLatch(1);
        var finishLatch = new CountDownLatch(nThreads);

        for (int i = 0; i < nThreads; i++) {
            new Thread(new SummatorTask(arr, sum, i, nThreads, startLatch, finishLatch))
                    .start();
        }

        startLatch.countDown();     // signal to start summation in workers
        long t1 = System.nanoTime();
        finishLatch.await();        // wait for workers complete
        long t2 = System.nanoTime();
        System.out.printf("%s threads: sum = %s, time ns = %s\n", nThreads, sum.get(), t2 - t1);
        return t2 - t1;
    }

    private static int[] arrWithRandomInts() {
        var randG = new SecureRandom();
        int[] arr = new int[MAX_LEN];
        for (var i = 0; i < arr.length; i++) {
            arr[i] = randG.nextInt(MAX_NUM);
        }
        return arr;
    }

    record SummatorTask(
            int[] arr,
            AtomicLong collector,
            int initial,
            int shift,
            CountDownLatch startLatch,
            CountDownLatch finishLatch
    ) implements Runnable {

        @Override
        public void run() {
            try {
                startLatch.await(); // wait for signal to start
                long sum = 0;
                for (int j = initial; j < arr.length; j += shift) {
                    sum += arr[j];
                }
                collector.addAndGet(sum);
                finishLatch.countDown();
            } catch (Exception e) {
                System.err.printf("Error in worker thread: %s\n", e.getMessage());
                System.exit(1);
            }
        }
    }
}
