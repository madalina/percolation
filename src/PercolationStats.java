import edu.princeton.cs.algs4.Stopwatch;
import edu.princeton.cs.introcs.StdOut;
import edu.princeton.cs.introcs.StdRandom;
import edu.princeton.cs.introcs.StdStats;

public class PercolationStats {
    private int N;
    private int T;
    private double[] thresholds;
    private int numExperiments = 0;

    // perform T independent computational experiments on an N-by-N grid
    public PercolationStats(int N, int T) {
        this.N = N;
        this.T = T;
        if (N <= 0 || T <= 0)
            throw new IllegalArgumentException();
        this.thresholds = new double[T];
        Stopwatch sw = new Stopwatch();
        while (numExperiments < T) {
            performExperiment();
            numExperiments++;
        }
        System.out.println("\n" + sw.elapsedTime() + " seconds elapsed");
    }

    private void performExperiment() {
        Percolation perc = new Percolation(N);
        int numOpenSites = 0;
        while (!perc.percolates()) {
            int rand1 = StdRandom.uniform(1, N + 1);
            int rand2 = StdRandom.uniform(1, N + 1);
            if (!perc.isOpen(rand1, rand2)) {
                perc.open(rand1, rand2);
                numOpenSites++;
            }
        }
        this.thresholds[numExperiments] = (double) numOpenSites / (N * N);
    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(thresholds);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(thresholds);
    }

    private static double[] confidenceInterval(double mean, double stddev, int T) {
        double[] res = new double[2];
        res[0] = mean - 1.96 * stddev / Math.sqrt(T);
        res[1] = mean + 1.96 * stddev / Math.sqrt(T);
        return res;
    }

    /*
     * Takes two command-line arguments N and T, performs T independent
     * computational experiments on an N-by-N grid, and prints out the mean,
     * standard deviation, and the 95% confidence interval for the percolation
     * threshold.
     */
    public static void main(String[] args) {
        int N = Integer.parseInt(args[0]);
        int T = Integer.parseInt(args[1]);

        PercolationStats ps = new PercolationStats(N, T);
        StdOut.println("Values for N = " + N + " and T = " + T + ":");
        StdOut.println("mean: " + ps.mean());
        StdOut.println("stddev: " + ps.stddev());
        double[] ci = ps.confidenceInterval(ps.mean(), ps.stddev(), T);
        StdOut.println("95% confidence interval = " + ci[0] + ", " + ci[1]);
    }
}