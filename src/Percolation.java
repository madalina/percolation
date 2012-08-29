import edu.princeton.cs.algs4.WeightedQuickUnionUF;

/**
 * A program to estimate the value of the percolation threshold via Monte Carlo
 * simulation. If sites are independently set to be open with probability p (and
 * therefore blocked with probability 1 − p), what is the probability that the
 * system percolates? When p equals 0, the system does not percolate; when p
 * equals 1, the system percolates.
 * 
 * @author Madalina Mutihac Note: by convention, i and j are integers between 1
 *         and N; (1,1) means top-left
 * 
 */
public class Percolation {
    private int n;
    private WeightedQuickUnionUF uf;
    private int[][] grid;
    private int[] opened;
    private boolean[] ctb; // connected to bottom
    private int virtualTop;
    private int virtualBottom;

    /**
     * Create N-by-N grid, with all sites blocked
     */
    public Percolation(int n) {
        this.n = n;
        virtualTop = n * n;
        virtualBottom = n * n + 1;
        grid = new int[n][n];
        uf = new WeightedQuickUnionUF(n * n + 2);
        opened = new int[n * n];
        ctb = new boolean[n * n + 2];

        int index = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                grid[i][j] = index;
                if (i == 0)
                    uf.union(virtualTop, grid[i][j]);
                else if (i == n - 1)
                    uf.union(virtualBottom, grid[i][j]);
                index++;
            }
        }

    }

    /**
     * Open site (row i, column j) if it is not already
     */
    public void open(int i, int j) {
        if (isIndexOutOfBounds(i, j))
            throw new IndexOutOfBoundsException();

        int ii = i - 1;
        int jj = j - 1;

        opened[xyTo1D(ii, jj)] = 1;

        int croot = grid[ii][jj];
        if (ii - 1 >= 0) {
            connectInternal(croot, ii - 1, jj);
        }
        if (jj - 1 >= 0) {
            connectInternal(croot, ii, jj - 1);
        }

        if (ii + 1 < n) {
            connectInternal(croot, ii + 1, jj);
        }

        if (jj + 1 < n) {
            connectInternal(croot, ii, jj + 1);
        }

        if (i == n)
            ctb[uf.find(xyTo1D(ii, jj))] = true;
        // debug();
    }

    private void connectInternal(int croot, int i, int j) {
        if (isOpenInternal(i, j)) {
            uf.union(croot, grid[i][j]);
            int root = uf.find(grid[i][j]);
            if (ctb[uf.find(croot)])
                ctb[root] = true;
        }
    }

    private int xyTo1D(int i, int j) {
        return n * i + j;
    }

    /**
     * Is site (row i, column j) open?
     */
    public boolean isOpen(int i, int j) {
        if (isIndexOutOfBounds(i, j))
            throw new IndexOutOfBoundsException();
        return isOpenInternal(i - 1, j - 1);
    }

    private boolean isOpenInternal(int i, int j) {
        return opened[xyTo1D(i, j)] == 1;
    }

    /**
     * Is site (row i, column j) full? (an open site that can be connected to an
     * open site in the top row via a chain of neighbouring open sites)
     */
    public boolean isFull(int i, int j) {
        if (isIndexOutOfBounds(i, j))
            throw new IndexOutOfBoundsException();

        if (!isOpen(i, j))
            return false;

        if (uf.connected(grid[i - 1][j - 1], virtualTop))
            return true;
        return false;
    }

    private void debug() {
        System.out.println();
        for (int k = 0; k < n; k++) {
            for (int l = 0; l < n; l++) {
                int x = grid[k][l];
                String space = " ";
                if (x < 10)
                    space = "  ";
                if (isOpenInternal(k, l))
                    System.out.print("(" + x + ")" + space);
                else
                    System.out.print(x + space);

            }
            System.out.print("\t");
            for (int l = 0; l < n; l++) {
                int x = uf.find(grid[k][l]);
                String space = " ";
                if (x < 10)
                    space = "  ";
                System.out.print(x + space);

            }

            System.out.println();
        }

        System.out.println();
    }

    private boolean isIndexOutOfBounds(int i, int j) {
        return (i < 1 || j < 1 || i > n || j > n);
    }

    /**
     * Does the system percolate? (We say the system percolates if there is a
     * full site in the bottom row. In other words, a system percolates if we
     * fill all open sites connected to the top row and that process fills some
     * open site on the bottom row.)
     */
    public boolean percolates() {
        if (n == 1 && isOpen(1, 1))
            return true;
        return uf.connected(virtualTop, virtualBottom);
    }

    public static void main(String[] args) {
        Percolation perc = new Percolation(3);
        perc.open(1, 3);
        perc.open(2, 3);
        perc.open(3, 3);
        perc.open(3, 1);
        System.out.println(perc.isFull(3, 1));
        perc.open(2, 1);
        perc.open(1, 1);
        System.out.println("System percolates: " + perc.percolates());
    }
}