package otc.math.random;

public class UniversalGenerator  implements RandomNumberGenerator{
	/**
     * Default seed.  <CODE>DEFAULT_RANDOM_SEED=54217137</CODE>
     */
    private static final int DEFAULT_RANDOM_SEED = 54217137;
    /**
     * The 46,009,220nd prime number,
     * The largest prime less than 9*10<SUP>8</SUP>.  Used as a modulus
     * because this version of <TT>random()</TT> needs a seed between 0
     * and 9*10<SUP>8</SUP> and <CODE>BIG_PRIME</CODE> isn't commensurate
     * with any regular period.
     * <CODE>BIG_PRIME = 899999963</CODE>
     */
    static final int BIG_PRIME = 899999963;
    private double c, cd, cm, u[];
    private int i97, j97;

    /**
     * Initialize Random with default seed.
     */
    public UniversalGenerator() {
        setSeed(DEFAULT_RANDOM_SEED);
    }

    /**
     * Initialize Random with a specified integer seed
     */
    public UniversalGenerator(int seed) {
        setSeed(seed);
    }

    /**
     * Initialize Random with a specified long seed
     */
    public UniversalGenerator(long seed) {
        setSeed(seed);
    }

    public void setSeed(long seed) {
        u = new double[97];

        int ijkl = Math.abs((int) (seed % BIG_PRIME));
        int ij = ijkl / 30082;
        int kl = ijkl % 30082;

        // Handle the seed range errors
        // First random number seed must be between 0 and 31328
        // Second seed must have a value between 0 and 30081
        if (ij < 0 || ij > 31328 || kl < 0 || kl > 30081) {
            ij = ij % 31329;
            kl = kl % 30082;
        }

        int i = ((ij / 177) % 177) + 2;
        int j = (ij % 177) + 2;
        int k = ((kl / 169) % 178) + 1;
        int l = kl % 169;

        int m;
        double s, t;
        for (int ii = 0; ii < 97; ii++) {
            s = 0.0;
            t = 0.5;
            for (int jj = 0; jj < 24; jj++) {
                m = (((i * j) % 179) * k) % 179;
                i = j;
                j = k;
                k = m;
                l = (53 * l + 1) % 169;
                if (((l * m) % 64) >= 32) {
                    s += t;
                }
                t *= 0.5;
            }
            u[ii] = s;
        }

        c = 362436.0 / 16777216.0;
        cd = 7654321.0 / 16777216.0;
        cm = 16777213.0 / 16777216.0;
        i97 = 96;
        j97 = 32;
    }

    public double nextDouble() {
        double uni;

        uni = u[i97] - u[j97];
        if (uni < 0.0) {
            uni += 1.0;
        }

        u[i97] = uni;
        if (--i97 < 0) {
            i97 = 96;
        }

        if (--j97 < 0) {
            j97 = 96;
        }

        c -= cd;
        if (c < 0.0) {
            c += cm;
        }

        uni -= c;
        if (uni < 0.0) {
            uni++;
        }

        return uni;
    }
    
    public void nextDoubles(double[] d) {
        int n = d.length;

        double uni;

        for (int i = 0; i < n; i++) {
            uni = u[i97] - u[j97];
            if (uni < 0.0) {
                uni += 1.0;
            }
            u[i97] = uni;
            if (--i97 < 0) {
                i97 = 96;
            }
            if (--j97 < 0) {
                j97 = 96;
            }
            c -= cd;
            if (c < 0.0) {
                c += cm;
            }
            uni -= c;
            if (uni < 0.0) {
                uni += 1.0;
            }
            d[i] = uni;
        }
    }

    public int next(int numbits) {
        return nextInt() >>> (32 - numbits);
    }
    
    public int nextInt() {
        return (int) Math.floor(Integer.MAX_VALUE * (2 * nextDouble() - 1.0));        
    }
    
    public int nextInt(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be positive");
        }
        
        // n is a power of 2
        if ((n & -n) == n) {
            return (int) ((n * (long) next(31)) >> 31);
        }

        int bits, val;
        do {
            bits = next(31);
            val = bits % n;
        } while (bits - val + (n - 1) < 0);
        
        return val;
    }
    
    public long nextLong() {
        return (long) Math.floor(Long.MAX_VALUE * (2 * nextDouble() - 1.0));        
    }

}
