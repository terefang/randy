package com.github.terefang.randy.rng.impl;

/**
 * A random number generator that guarantees 4-dimensional equidistribution (except for the quartet with four
 * zeroes in a row, every quartet of long results is produced exactly once over the period). This particular generator
 * is nearly identical to Xoshiro256** (or StarStar), but instead of using the fast but weak StarStar "scrambler,"
 * it runs output through the MX3 unary hash, which is slower but extremely strong. It has a period of
 * (2 to the 256) - 1, which would take millennia to exhaust on current-generation hardware (at least).
 * This isn't a cryptographic generator, but the only issue I know of with Xoshiro and the StarStar scrambler should be
 * fully resolved here. The only invalid state is the one with 0 in each state variable, and this won't ever
 * occur in the normal period of that contains all other states. You can seed this with either {@link #setSeed(long)}
 * or {@link #setState(long, long, long, long)} without encountering problems past the first 4 or so outputs. If you
 * pass very similar initial states to two different generators with {@link #setState(long)}, their output will likely
 * be similar for the first 3 or 4 outputs, and will then diverge rapidly.
 * <br>
 * Xoshiro256** was written in 2018 by David Blackman and Sebastiano Vigna. You can consult their paper for technical details:
 * <a href="https://vigna.di.unimi.it/ftp/papers/ScrambledLinear.pdf">PDF link here</a>. The MX3 unary hash was written
 * 2020 by Jon Maiga, <a href="https://github.com/jonmaiga/mx3">GitHub repo here</a>.
 */
public class RandomXMX256
{
    /**
     * The first state; can be any long, as long as all states are not 0.
     * <br>
     * This is a public field to match the style used by libGDX and to make changes easier.
     */
    public long stateA;
    /**
     * The second state; can be any long, as long as all states are not 0.
     * This is the state that is scrambled and returned; if it is 0 before a number
     * is generated, then the next number returned by {@link #nextLong()} will be 0.
     * <br>
     * This is a public field to match the style used by libGDX and to make changes easier.
     */
    public long stateB;
    /**
     * The third state; can be any long, as long as all states are not 0.
     * <br>
     * This is a public field to match the style used by libGDX and to make changes easier.
     */
    public long stateC;
    /**
     * The fourth state; can be any long, as long as all states are not 0.
     * <br>
     * This is a public field to match the style used by libGDX and to make changes easier.
     */
    public long stateD;

    /**
     * Creates a new RandomXMX256 with a random state.
     */
    public RandomXMX256() {
        this(System.nanoTime());;
    }

    /**
     * Creates a new RandomXMX256 with the given seed; all {@code long} values are permitted.
     * The seed will be passed to {@link #setSeed(long)} to attempt to adequately distribute the seed randomly.
     *
     * @param seed any {@code long} value
     */
    public RandomXMX256(long seed) {
        setSeed(seed);
    }

    /**
     * This initializes all 4 states of the generator to random values based on the given seed.
     * (2 to the 64) possible initial generator states can be produced here, all with a different
     * first value returned by {@link #nextLong()} (because {@code stateB} is guaranteed to be
     * different for every different {@code seed}).
     *
     * @param seed the initial seed; may be any long
     */
    public void setSeed (long seed) {
        long x = (seed + 0x9E3779B97F4A7C15L);
        x ^= x >>> 27;
        x *= 0x3C79AC492BA7B653L;
        x ^= x >>> 33;
        x *= 0x1C69B3F74AC4AE35L;
        stateA = x ^ x >>> 27;
        x = (seed + 0x3C6EF372FE94F82AL);
        x ^= x >>> 27;
        x *= 0x3C79AC492BA7B653L;
        x ^= x >>> 33;
        x *= 0x1C69B3F74AC4AE35L;
        stateB = x ^ x >>> 27;
        x = (seed + 0xDAA66D2C7DDF743FL);
        x ^= x >>> 27;
        x *= 0x3C79AC492BA7B653L;
        x ^= x >>> 33;
        x *= 0x1C69B3F74AC4AE35L;
        stateC = x ^ x >>> 27;
        x = (seed + 0x78DDE6E5FD29F054L);
        x ^= x >>> 27;
        x *= 0x3C79AC492BA7B653L;
        x ^= x >>> 33;
        x *= 0x1C69B3F74AC4AE35L;
        stateD = x ^ x >>> 27;
    }

    public long nextLong () {
        long result = stateB;
        long t = stateB << 17;
        stateC ^= stateA;
        stateD ^= stateB;
        stateB ^= stateC;
        stateA ^= stateD;
        stateC ^= t;
        stateD = (stateD << 45 | stateD >>> 19);
        result ^= result >>> 32;
        result *= 0xBEA225F9EB34556DL;
        result ^= result >>> 29;
        result *= 0xBEA225F9EB34556DL;
        result ^= result >>> 32;
        result *= 0xBEA225F9EB34556DL;
        return result ^ result >>> 29;
    }
}
