package com.github.terefang.randy.rng.impl;

/**
 * A random number generator with five 64-bit states; does not use multiplication, only add, subtract, XOR, and rotate
 * operations. Has a state that runs like a counter, guaranteeing a minimum period of 2 to the 64. This passes roughly
 * 180 petabytes of intensive testing on the GPU with ReMort, as well as 64TB of PractRand's broad spectrum of tests.
 * It is very fast on modern JDKs (such as HotSpot or Graal, compatible with Java 16 or later), able to generate 1.75
 * billion longs per second with {@link #nextLong()} on a mid-grade laptop. To compare,
 * {@link java.util.Random#nextLong()} is only able to generate 64 million longs per second on the same machine.
 * <br>
 * The name comes from the 52 cards (excluding jokers, but including aces) in a standard playing card deck, since this
 * uses a left rotation by exactly 52 as one of its critical components. Rotations by anything else I tried didn't pass
 * testing as well, or even at all.
 * <br>
 * The algorithm here is (to my knowledge) novel; it was first published as AceRandom by Tommy Ettinger in
 * <a href="https://github.com/tommyettinger/juniper">the juniper library</a>.
 * <br>
 */
public class RandomAce320 {

    /**
     * The first state; can be any long.
     * <br>
     * This is a public field to match the style used by libGDX and to make changes easier.
     */
    public long stateA;
    /**
     * The second state; can be any long.
     * <br>
     * This is a public field to match the style used by libGDX and to make changes easier.
     */
    public long stateB;
    /**
     * The third state; can be any long.
     * <br>
     * This is a public field to match the style used by libGDX and to make changes easier.
     */
    public long stateC;
    /**
     * The fourth state; can be any long.
     * <br>
     * This is a public field to match the style used by libGDX and to make changes easier.
     */
    public long stateD;
    /**
     * The fifth state; can be any long. The first call to {@link #nextLong()} will return this verbatim, if no other
     * methods have been called.
     * <br>
     * This is a public field to match the style used by libGDX and to make changes easier.
     */
    public long stateE;

    /**
     * Creates a new RandomAce320 with a random state.
     */
    public RandomAce320() {
        this(System.nanoTime());
    }

    /**
     * Creates a new RandomAce320 with the given seed; all {@code long} values are permitted.
     * The seed will be passed to {@link #setSeed(long)} to attempt to adequately distribute the seed randomly.
     *
     * @param seed any {@code long} value
     */
    public RandomAce320(long seed) {
        setSeed(seed);
    }


    /**
     * This initializes all 5 states of the generator to random values based on the given seed.
     * (2 to the 64) possible initial generator states can be produced here, all with a different
     * first value returned by {@link #nextLong()}.
     *
     * @param seed the initial seed; may be any long
     */
    public void setSeed (long seed) {
        seed = (seed ^ 0x1C69B3F74AC4AE35L) * 0x3C79AC492BA7B653L; // an XLCG
        stateA = seed ^ ~0xC6BC279692B5C323L;
        seed ^= seed >>> 32;
        stateB = seed ^ 0xD3833E804F4C574BL;
        seed *= 0xBEA225F9EB34556DL;                               // MX3 unary hash
        seed ^= seed >>> 29;
        stateC = seed ^ ~0xD3833E804F4C574BL;                      // updates are spread across the MX3 hash
        seed *= 0xBEA225F9EB34556DL;
        seed ^= seed >>> 32;
        stateD = seed ^ 0xC6BC279692B5C323L;;
        seed *= 0xBEA225F9EB34556DL;
        seed ^= seed >>> 29;
        stateE = seed;
    }

    public long nextLong () {
        final long fa = stateA;
        final long fb = stateB;
        final long fc = stateC;
        final long fd = stateD;
        final long fe = stateE;
        stateA = fa + 0x9E3779B97F4A7C15L;
        stateB = fa ^ fe;
        stateC = fb + fd;
        stateD = (fc << 52 | fc >>> 12);
        return stateE = fb - fc;
    }
}
