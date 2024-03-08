package com.github.terefang.randy.rng.impl;

/**
 * A variant on Java 8's SplittableRandom algorithm, removing the splittable quality so this has one possible stream.
 * You'd typically use this when you want every output of {@link #nextLong()} from one generator to be a different,
 * unique number until every {@code long} has been generated, such as for generating unique seeds or IDs. The reasons
 * for removing the splittable quality are a little complicated, but it's enough to say that by having a fixed stream,
 * this is a little faster, and it avoids the possibility of some streams being lower-quality. This uses Pelle Evensen's
 * <a href="https://mostlymangling.blogspot.com/2019/12/stronger-better-morer-moremur-better.html">Moremur mixer</a>
 * instead of SplittableRandom's Variant 13, which should give it roughly equivalent performance but somewhat higher
 * statistical quality. Like many variations on SplittableRandom and its SplitMix64 algorithm, this changes its state
 * by a simple counter with a large increment; one of the best increments seems to be (2 to the 64) divided by the
 * golden ratio, plus or minus 1 to make it odd. This number, 0x9E3779B97F4A7C15L or -7046029254386353131L when stored
 * in a signed long, shows up a lot in random number generation and hashing fields because the golden ratio has some
 * unique and helpful properties. The increment is sometimes called the "gamma," and this particular gamma is known to
 * be high-quality, but of the over 9 quintillion possible odd-number gammas, not all are have such nice properties
 * (for instance, {@code 1} would make a terrible gamma if it were used in this generator, because it's so small). We
 * only allow one gamma here, so we can be sure it works.
 * <br>
 * The SplitMix64 algorithm is derived from <a href="https://gee.cs.oswego.edu/dl/papers/oopsla14.pdf">this paper</a> by
 * Guy L. Steele Jr., Doug Lea, and Christine H. Flood. Moremur was written by Pelle Evensen, and improves upon the
 * MurmurHash3 mixer written by Austin Appleby.
 * <br>
 */
public class RandomDistinct64 {
    /**
     * The only state variable; can be any {@code long}.
     * <br>
     * This is a public field to match the style used by libGDX and to make changes easier.
     */
    public long state;

    /**
     * Creates a new RandomDistinct64 with a random state.
     */
    public RandomDistinct64() {
        this(System.nanoTime());
    }

    /**
     * Creates a new RandomDistinct64 with the given state; all {@code long} values are permitted.
     *
     * @param state any {@code long} value
     */
    public RandomDistinct64(long state)
    {
        this.state = state;
    }

    /**
     * Sets the only state, which can be given any long value; this seed value
     * will not be altered.
     *
     * @param seed the exact value to use for the state; all longs are valid
     */
    public void setSeed (long seed) {
        state = seed;
    }

    public long nextLong () {
        long x = (state += 0x9E3779B97F4A7C15L);
        x ^= x >>> 27;
        x *= 0x3C79AC492BA7B653L;
        x ^= x >>> 33;
        x *= 0x1C69B3F74AC4AE35L;
        return x ^ x >>> 27;
    }
}
