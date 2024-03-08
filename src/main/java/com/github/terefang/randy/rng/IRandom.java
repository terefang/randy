package com.github.terefang.randy.rng;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

// many defaults adapted from here:
// https://github.com/tommyettinger/cringe/ ... GdxRandom.java
public interface IRandom
{
    default public String name() {
        return this.getClass().getSimpleName();
    }

    /**
     * Sets the seed of this random number generator using a single seed.
     *
     * @param _s the initial seed
     */
    public void setSeed(long _s);

    /**
     * Sets the seed of this random number generator using a single seed.
     *
     * @param _s the initial seed
     */
    default public void setSeed(String _s) {
        setSeed(UUID.nameUUIDFromBytes(_s.getBytes(StandardCharsets.UTF_8)).getMostSignificantBits());
    }

    /**
     * Sets the seed of this random number generator using a single seed.
     *
     * @param _s the initial seed
     */
    default public void setSeed(byte[] _s) {
        setSeed(UUID.nameUUIDFromBytes(_s).getMostSignificantBits());
    }

    /**
     * Returns the next pseudorandom, uniformly distributed {@code byte}
     * value from this random number generator's sequence. All 2<sup>8</sup> possible
     * {@code byte} values are produced with (approximately) equal probability.
     *
     * @return the next pseudorandom, uniformly distributed {@code byte}
     * value from this random number generator's sequence.
     */
    public byte nextByte();

    /**
     * Generates the next pseudorandom number with a specific maximum size in bits (not a max number).
     * If you want to get a random number in a range, you should usually use {@code #nextInt(int)} instead.
     * For some specific cases, this method is more efficient and less biased than {@code #nextInt(int)}.
     * For {@code bits} values between 1 and 30, this should be similar in effect to
     * {@code nextInt(1 << bits)}; though it won't typically produce the same values, they will have
     * the correct range. If {@code bits} is 31, this can return any non-negative {@code int}; note that
     * {@code nextInt(1 << 31)} won't behave this way because {@code 1 << 31} is negative. If
     * {@code bits} is 32 (or 0), this can return any {@code int}.
     *
     * @param bits the amount of random bits to request, from 1 to 32
     * @return the next pseudorandom value from this random number
     * generator's sequence
     */
    default public int next (int bits) {
        return (int)nextLong() >>> 32 - bits;
    }

    /**
     * Returns the next pseudorandom, uniformly distributed {@code int}
     * value from this random number generator's sequence. The general
     * contract of {@code nextInt} is that one {@code int} value is
     * pseudorandomly generated and returned. All 2<sup>32</sup> possible
     * {@code int} values are produced with (approximately) equal probability.
     *
     * @return the next pseudorandom, uniformly distributed {@code int}
     * value from this random number generator's sequence
     */
    default public int nextInt()
    {
        return (int)nextLong();
    }

    /**
     * Returns a pseudorandom, uniformly distributed {@code int} value
     * between 0 (inclusive) and the specified value (exclusive), drawn from
     * this random number generator's sequence.  The general contract of
     * {@code nextInt} is that one {@code int} value in the specified range
     * is pseudorandomly generated and returned.  All {@code bound} possible
     * {@code int} values are produced with (approximately) equal
     * probability.
     * <br>
     * It should be mentioned that the technique this uses has some bias, depending
     * on {@code bound}, but it typically isn't measurable without specifically looking
     * for it. Using the method this does allows this method to always advance the state
     * by one step, instead of a varying and unpredictable amount with the more typical
     * ways of rejection-sampling random numbers and only using numbers that can produce
     * an int within the bound without bias.
     * See <a href="https://www.pcg-random.org/posts/bounded-rands.html">M.E. O'Neill's
     * blog about random numbers</a> for discussion of alternative, unbiased methods.
     *
     * @param bound the upper bound (exclusive). If negative or 0, this always returns 0.
     * @return the next pseudorandom, uniformly distributed {@code int}
     * value between zero (inclusive) and {@code bound} (exclusive)
     * from this random number generator's sequence
     */
    default public int nextInt (int bound)
    {
        return (int)(bound * (nextLong() & 0xFFFFFFFFL) >> 32) & ~(bound >> 31);
    }

    /**
     * Returns a pseudorandom, uniformly distributed {@code int} value between an
     * inner bound of 0 (inclusive) and the specified {@code outerBound} (exclusive).
     * This is meant for cases where the outer bound may be negative, especially if
     * the bound is unknown or may be user-specified. A negative outer bound is used
     * as the lower bound; a positive outer bound is used as the upper bound. An outer
     * bound of -1, 0, or 1 will always return 0, keeping the bound exclusive (except
     * for outer bound 0). This method is slightly slower than {@code #nextInt(int)}.
     *
     * @param outerBound the outer exclusive bound; may be any int value, allowing negative
     * @return a pseudorandom int between 0 (inclusive) and outerBound (exclusive)
     * @see #nextInt(int) Here's a note about the bias present in the bounded generation.
     */
    default public int nextSignedInt (int outerBound)
    {
        outerBound = (int)(outerBound * (nextLong() & 0xFFFFFFFFL) >> 32);
        return outerBound + (outerBound >>> 31);
    }

    /**
     * Returns a pseudorandom, uniformly distributed {@code int} value between the
     * specified {@code innerBound} (inclusive) and the specified {@code outerBound}
     * (exclusive). This is meant for cases where either bound may be negative,
     * especially if the bounds are unknown or may be user-specified. It is slightly
     * slower than {@code #nextInt(int, int)}, and significantly slower than
     * {@code #nextInt(int)} or {@code #nextSignedInt(int)}. This last part is
     * because this handles even ranges that go from large negative numbers to large
     * positive numbers, and since that range is larger than the largest possible int,
     * this has to use {@code #nextSignedLong(long, long)}.
     *
     * @param innerBound the inclusive inner bound; may be any int, allowing negative
     * @param outerBound the exclusive outer bound; may be any int, allowing negative
     * @return a pseudorandom int between innerBound (inclusive) and outerBound (exclusive)
     * @see #nextInt(int) Here's a note about the bias present in the bounded generation.
     */
    default public int nextSignedInt (int innerBound, int outerBound) {
        return (int)nextSignedLong(innerBound, outerBound);
    }

    /**
     * Returns a pseudorandom, uniformly distributed {@code int} value between the
     * specified {@code innerBound} (inclusive) and the specified {@code outerBound}
     * (exclusive). If {@code outerBound} is less than or equal to {@code innerBound},
     * this always returns {@code innerBound}. This is significantly slower than
     * {@code #nextInt(int)} or {@code #nextSignedInt(int)},
     * because this handles even ranges that go from large negative numbers to large
     * positive numbers, and since that would be larger than the largest possible int,
     * this has to use {@code #nextLong(long, long)}.
     *
     * <br> For any case where outerBound might be valid but less than innerBound, you
     * can use {@code #nextSignedInt(int, int)}. If outerBound is less than innerBound
     * here, this simply returns innerBound.
     *
     * @param innerBound the inclusive inner bound; may be any int, allowing negative
     * @param outerBound the exclusive outer bound; must be greater than innerBound (otherwise this returns innerBound)
     * @return a pseudorandom int between innerBound (inclusive) and outerBound (exclusive)
     * @see #nextInt(int) Here's a note about the bias present in the bounded generation.
     */
    default public int nextInt (int innerBound, int outerBound) {
        return (int)nextLong(innerBound, outerBound);
    }

    default public long nextLong()
    {
        return (long)((nextByte()&0xffL)
                | ((nextByte()&0xffL)<<8)
                | ((nextByte()&0xffL)<<16)
                | ((nextByte()&0xffL)<<24)
                | ((nextByte()&0xffL)<<32)
                | ((nextByte()&0xffL)<<40)
                | ((nextByte()&0xffL)<<48)
                | ((nextByte()&0xffL)<<56));
    }

    /**
     * Returns a pseudorandom, uniformly distributed {@code long} value
     * between 0 (inclusive) and the specified value (exclusive), drawn from
     * this random number generator's sequence.  The general contract of
     * {@code nextLong} is that one {@code long} value in the specified range
     * is pseudorandomly generated and returned.  All {@code bound} possible
     * {@code long} values are produced with (approximately) equal
     * probability, though there is a small amount of bias depending on the bound.
     *
     * @param bound the upper bound (exclusive). If negative or 0, this always returns 0.
     * @return the next pseudorandom, uniformly distributed {@code long}
     * value between zero (inclusive) and {@code bound} (exclusive)
     * from this random number generator's sequence
     * @see #nextInt(int) Here's a note about the bias present in the bounded generation.
     */
    default public long nextLong (long bound) {
        return nextLong(0L, bound);
    }
    
    /**
     * Returns a pseudorandom, uniformly distributed {@code long} value between the
     * specified {@code inner} bound (inclusive) and the specified {@code outer} bound
     * (exclusive). If {@code outer} is less than or equal to {@code inner},
     * this always returns {@code inner}.
     *
     * <br> For any case where outer might be valid but less than inner, you
     * can use {@code #nextSignedLong(long, long)}.
     *
     * @param inner the inclusive inner bound; may be any long, allowing negative
     * @param outer the exclusive outer bound; must be greater than inner (otherwise this returns inner)
     * @return a pseudorandom long between inner (inclusive) and outer (exclusive)
     * @see #nextInt(int) Here's a note about the bias present in the bounded generation.
     */
    default public long nextLong (long inner, long outer) {
        final long rand = nextLong();
        if (inner >= outer)
            return inner;
        final long bound = outer - inner;
        final long randLow = rand & 0xFFFFFFFFL;
        final long boundLow = bound & 0xFFFFFFFFL;
        final long randHigh = (rand >>> 32);
        final long boundHigh = (bound >>> 32);
        return inner + (randHigh * boundLow >>> 32) + (randLow * boundHigh >>> 32) + randHigh * boundHigh;
    }

    /**
     * Returns a pseudorandom, uniformly distributed {@code long} value between an
     * inner bound of 0 (inclusive) and the specified {@code outerBound} (exclusive).
     * This is meant for cases where the outer bound may be negative, especially if
     * the bound is unknown or may be user-specified. A negative outer bound is used
     * as the lower bound; a positive outer bound is used as the upper bound. An outer
     * bound of -1, 0, or 1 will always return 0, keeping the bound exclusive (except
     * for outer bound 0).
     *
     * <p>Note that this advances the state by the same amount as a single call to
     * {@code #nextLong()}, which allows methods like {@code #skip(long)} to function
     * correctly, but introduces some bias when {@code bound} is very large. This
     * method should be about as fast as {@code #nextLong(long)} , unlike the speed
     * difference between {@code #nextInt(int)} and {@code #nextSignedInt(int)}.
     *
     * @param outerBound the outer exclusive bound; may be any long value, allowing negative
     * @return a pseudorandom long between 0 (inclusive) and outerBound (exclusive)
     * @see #nextInt(int) Here's a note about the bias present in the bounded generation.
     */
    default public long nextSignedLong (long outerBound) {
        return nextSignedLong(0L, outerBound);
    }

    /**
     * Returns a pseudorandom, uniformly distributed {@code long} value between the
     * specified {@code inner} bound (inclusive) and the specified {@code outer} bound
     * (exclusive). This is meant for cases where either bound may be negative,
     * especially if the bounds are unknown or may be user-specified.
     *
     * @param inner the inclusive inner bound; may be any long, allowing negative
     * @param outer the exclusive outer bound; may be any long, allowing negative
     * @return a pseudorandom long between inner (inclusive) and outer (exclusive)
     * @see #nextInt(int) Here's a note about the bias present in the bounded generation.
     */
    default public long nextSignedLong (long inner, long outer) {
        final long rand = nextLong();
        if (outer < inner) {
            long t = outer;
            outer = inner + 1L;
            inner = t + 1L;
        }
        final long bound = outer - inner;
        final long randLow = rand & 0xFFFFFFFFL;
        final long boundLow = bound & 0xFFFFFFFFL;
        final long randHigh = (rand >>> 32);
        final long boundHigh = (bound >>> 32);
        return inner + (randHigh * boundLow >>> 32) + (randLow * boundHigh >>> 32) + randHigh * boundHigh;
    }

    /**
     * Generates random bytes and places them into a user-supplied
     * byte array.  The number of random bytes produced is equal to
     * the length of the byte array.
     *
     * @param dest the byte array to fill with random bytes
     */
    default public void nextBytes(byte[] dest)
    {
        nextBytes(dest, dest.length);
    }

    default public void nextBytes(byte[] dest, int dest_len)
    {
        for(int _i = 0; _i<dest_len; _i++)
        {
            dest[_i] = nextByte();
        }
    }

    default public byte[] nextBytes(int dest_len)
    {
        byte[] dest = new byte[dest_len];
        for(int _i = 0; _i<dest_len; _i++)
        {
            dest[_i] = nextByte();
        }
        return dest;
    }

    /**
     * Returns the next pseudorandom, uniformly distributed
     * {@code boolean} value from this random number generator's
     * sequence. The general contract of {@code nextBoolean} is that one
     * {@code boolean} value is pseudorandomly generated and returned.  The
     * values {@code true} and {@code false} are produced with
     * (approximately) equal probability.
     * <br>
     * The public implementation simply returns a sign check on {@code #nextLong()},
     * returning true if the generated long is negative. This is typically the safest
     * way to implement this method; many types of generators have less statistical
     * quality on their lowest bit, so just returning based on the lowest bit isn't
     * always a good idea.
     *
     * @return the next pseudorandom, uniformly distributed
     * {@code boolean} value from this random number generator's
     * sequence
     */
    default public boolean nextBoolean ()
    {
        return nextLong() < 0L;
    }

    /**
     * Returns the next pseudorandom, uniformly distributed {@code float}
     * value between {@code 0.0} (inclusive) and {@code 1.0} (exclusive)
     * from this random number generator's sequence.
     *
     * <p>The general contract of {@code nextFloat} is that one
     * {@code float} value, chosen (approximately) uniformly from the
     * range {@code 0.0f} (inclusive) to {@code 1.0f} (exclusive), is
     * pseudorandomly generated and returned. All 2<sup>24</sup> possible
     * {@code float} values of the form <i>m&nbsp;x&nbsp;</i>2<sup>-24</sup>,
     * where <i>m</i> is a positive integer less than 2<sup>24</sup>, are
     * produced with (approximately) equal probability.
     *
     * <p>The public implementation uses the upper 24 bits of {@code #nextLong()},
     * with an unsigned right shift and a multiply by a very small float
     * ({@code 5.9604645E-8f} or {@code 0x1p-24f}). It tends to be fast if
     * nextLong() is fast, but alternative implementations could use 24 bits of
     * {@code #nextInt()} (or just {@code #next(int)}, giving it {@code 24})
     * if that generator doesn't efficiently generate 64-bit longs.<p>
     *
     * @return the next pseudorandom, uniformly distributed {@code float}
     * value between {@code 0.0} and {@code 1.0} from this
     * random number generator's sequence
     */
    default public float nextFloat ()
    {
        return (nextLong() >>> 40) * 0x1p-24f;
    }

    /**
     * Gets a pseudo-random float between 0 (inclusive) and {@code outerBound} (exclusive).
     * The outerBound may be positive or negative.
     * Exactly the same as {@code nextFloat() * outerBound}.
     *
     * @param outerBound the exclusive outer bound
     * @return a float between 0 (inclusive) and {@code outerBound} (exclusive)
     */
    default public float nextFloat (float outerBound)
    {
        return nextFloat() * outerBound;
    }

    /**
     * Gets a pseudo-random float between {@code innerBound} (inclusive) and {@code outerBound} (exclusive).
     * Either, neither, or both of innerBound and outerBound may be negative; this does not change which is
     * inclusive and which is exclusive.
     *
     * @param innerBound the inclusive inner bound; may be negative
     * @param outerBound the exclusive outer bound; may be negative
     * @return a float between {@code innerBound} (inclusive) and {@code outerBound} (exclusive)
     */
    default public float nextFloat (float innerBound, float outerBound)
    {
        return innerBound + nextFloat() * (outerBound - innerBound);
    }

    /**
     * Returns the next pseudorandom, uniformly distributed
     * {@code double} value between {@code 0.0} (inclusive) and {@code 1.0}
     * (exclusive) from this random number generator's sequence.
     *
     * <p>The general contract of {@code nextDouble} is that one
     * {@code double} value, chosen (approximately) uniformly from the
     * range {@code 0.0d} (inclusive) to {@code 1.0d} (exclusive), is
     * pseudorandomly generated and returned.
     *
     * <p>The public implementation uses the upper 53 bits of {@code #nextLong()},
     * with an unsigned right shift and a multiply by a very small double
     * ({@code 1.1102230246251565E-16}, or {@code 0x1p-53}). It should perform well
     * if nextLong() performs well, and is expected to perform less well if the
     * generator naturally produces 32 or fewer bits at a time.<p>
     *
     * @return the next pseudorandom, uniformly distributed {@code double}
     * value between {@code 0.0} and {@code 1.0} from this
     * random number generator's sequence
     */
    default public double nextDouble ()
    {
        return (nextLong() >>> 11) * 0x1.0p-53;
    }

    /**
     * Gets a pseudo-random double between 0 (inclusive) and {@code outerBound} (exclusive).
     * The outerBound may be positive or negative.
     * Exactly the same as {@code nextDouble() * outerBound}.
     *
     * @param outerBound the exclusive outer bound
     * @return a double between 0 (inclusive) and {@code outerBound} (exclusive)
     */
    default public double nextDouble (double outerBound) {
        return nextDouble() * outerBound;
    }

    /**
     * Gets a pseudo-random double between {@code innerBound} (inclusive) and {@code outerBound} (exclusive).
     * Either, neither, or both of innerBound and outerBound may be negative; this does not change which is
     * inclusive and which is exclusive.
     *
     * @param innerBound the inclusive inner bound; may be negative
     * @param outerBound the exclusive outer bound; may be negative
     * @return a double between {@code innerBound} (inclusive) and {@code outerBound} (exclusive)
     */
    default public double nextDouble (double innerBound, double outerBound) {
        return innerBound + nextDouble() * (outerBound - innerBound);
    }

    /**
     * This is just like {@code #nextDouble()}, returning a double between 0 and 1, except that it is inclusive on both
     * 0.0 and 1.0. It returns 1.0 extremely rarely, 0.000000000000011102230246251565404236316680908203125% of the time
     * if there is no bias in the generator, but it can happen. This uses similar code to {@code #nextExclusiveDouble()}
     * internally, and retains its quality of having approximately uniform distributions for every mantissa bit, unlike
     * most ways of generating random floating-point numbers.
     *
     * @return a double between 0.0, inclusive, and 1.0, inclusive
     */
    default public double nextInclusiveDouble () {
        final long bits = nextLong();
        return Double.longBitsToDouble(1022L - Long.numberOfTrailingZeros(bits) << 52 | bits >>> 12) + 0x1p-12 - 0x1p-12;
    }

    /**
     * Just like {@code #nextDouble(double)}, but this is inclusive on both 0.0 and {@code outerBound}.
     * It may be important to note that it returns outerBound on only 0.000000000000011102230246251565% of calls.
     *
     * @param outerBound the outer inclusive bound; may be positive or negative
     * @return a double between 0.0, inclusive, and {@code outerBound}, inclusive
     */
    default public double nextInclusiveDouble (double outerBound) {
        return nextInclusiveDouble() * outerBound;
    }

    /**
     * Just like {@code #nextDouble(double, double)}, but this is inclusive on both {@code innerBound} and {@code outerBound}.
     * It may be important to note that it returns outerBound on only 0.000000000000011102230246251565% of calls, if it can
     * return it at all because of floating-point imprecision when innerBound is a larger number.
     *
     * @param innerBound the inner inclusive bound; may be positive or negative
     * @param outerBound the outer inclusive bound; may be positive or negative
     * @return a double between {@code innerBound}, inclusive, and {@code outerBound}, inclusive
     */
    default public double nextInclusiveDouble (double innerBound, double outerBound) {
        return innerBound + nextInclusiveDouble() * (outerBound - innerBound);
    }

    /**
     * This is just like {@code #nextFloat()}, returning a float between 0 and 1, except that it is inclusive on both
     * 0.0 and 1.0. It returns 1.0 rarely, 0.00000596046412226771% of the time if there is no bias in the generator, but
     * it can happen. This method does not return purely-equidistant floats, because there the resolution of possible
     * floats it can generate is higher as it approaches 0.0 . The smallest non-zero float this can return is
     * 5.421011E-20f (0x1p-64f in hex), and the largest non-one float this can return is 0.9999999f (0x1.fffffcp-1f in
     * hex). This uses nearly identical code to {@code #nextExclusiveFloat()}, but carefully adds and subtracts a small
     * number to force rounding at 0.0 and 1.0 . This retains the exclusive version's quality of having approximately
     * uniform distributions for every mantissa bit, unlike most ways of generating random floating-point numbers.
     *
     * @return a float between 0.0, inclusive, and 1.0, inclusive
     */
    default public float nextInclusiveFloat () {
        final long bits = nextLong();
        return Float.intBitsToFloat(126 - Long.numberOfTrailingZeros(bits) << 23 | (int)(bits >>> 41)) + 0x1p-22f - 0x1p-22f;
    }

    /**
     * Just like {@code #nextFloat(float)}, but this is inclusive on both 0.0 and {@code outerBound}.
     * It may be important to note that it returns outerBound on only 0.00000596046412226771% of calls.
     *
     * @param outerBound the outer inclusive bound; may be positive or negative
     * @return a float between 0.0, inclusive, and {@code outerBound}, inclusive
     */
    default public float nextInclusiveFloat (float outerBound) {
        return nextInclusiveFloat() * outerBound;
    }

    /**
     * Just like {@code #nextFloat(float, float)}, but this is inclusive on both {@code innerBound} and {@code outerBound}.
     * It may be important to note that it returns outerBound on only 0.00000596046412226771% of calls, if it can return
     * it at all because of floating-point imprecision when innerBound is a larger number.
     *
     * @param innerBound the inner inclusive bound; may be positive or negative
     * @param outerBound the outer inclusive bound; may be positive or negative
     * @return a float between {@code innerBound}, inclusive, and {@code outerBound}, inclusive
     */
    default public float nextInclusiveFloat (float innerBound, float outerBound) {
        return innerBound + nextInclusiveFloat() * (outerBound - innerBound);
    }

    /**
     * Gets a random double between 0.0 and 1.0, exclusive at both ends; this method is also more uniform than
     * {@code #nextDouble()} if you use the bit-patterns of the returned doubles. This is a simplified version of
     * <a href="https://allendowney.com/research/rand/">this algorithm by Allen Downey</a>. This can return double
     * values between 2.710505431213761E-20 and 0.9999999999999999, or 0x1.0p-65 and 0x1.fffffffffffffp-1 in hex
     * notation. It cannot return 0 or 1. Some cases can prefer {@code #nextExclusiveDoubleEquidistant()}, which is
     * implemented more traditionally but may have slower performance. This method can also return doubles that
     * are extremely close to 0, but can't return doubles that are as close to 1, due to how floating-point numbers
     * work. However, nextExclusiveDoubleEquidistant() can return only a minimum value that is as distant from 0 as its
     * maximum value is distant from 1.
     * <br>
     * To compare, nextDouble() and nextExclusiveDoubleEquidistant() are less likely to produce a "1" bit for their
     * lowest 5 bits of mantissa/significand (the least significant bits numerically, but potentially important
     * for some uses), with the least significant bit produced half as often as the most significant bit in the
     * mantissa. As for this method, it has approximately the same likelihood of producing a "1" bit for any
     * position in the mantissa.
     * <br>
     * The implementation may have different performance characteristics than {@code #nextDouble()}, because this
     * doesn't perform any floating-point multiplication or division, and instead assembles bits obtained by one call to
     * {@code #nextLong()}. This uses {@code Double#longBitsToDouble(long)} and
     * {@code Long#numberOfTrailingZeros(long)}, both of which typically have optimized intrinsics on HotSpot, and this
     * is branchless and loopless, unlike the original algorithm by Allen Downey. When compared with
     * {@code #nextExclusiveDoubleEquidistant()}, this method performs better on at least HotSpot JVMs. On GraalVM 17,
     * this is over twice as fast as nextExclusiveDoubleEquidistant().
     *
     * @return a random uniform double between 2.710505431213761E-20 and 0.9999999999999999 (both inclusive)
     */
    default public double nextExclusiveDouble () {
        final long bits = nextLong();
        return Double.longBitsToDouble(1022L - Long.numberOfTrailingZeros(bits) << 52 | bits >>> 12);
    }

    /**
     * Gets a random double between 0.0 and 1.0, exclusive at both ends. This can return double
     * values between 1.1102230246251565E-16 and 0.9999999999999999, or 0x1.0p-53 and 0x1.fffffffffffffp-1 in hex
     * notation. It cannot return 0 or 1, and its minimum and maximum results are equally distant from 0 and from
     * 1, respectively. Many usages may prefer {@code #nextExclusiveDouble()}, which is better-distributed if you
     * consider the bit representation of the returned doubles, tends to perform better, and can return doubles that
     * much closer to 0 than this can.
     * <br>
     * The implementation simply uses {@code nextLong(long)} to get a uniformly-chosen long between 1 and
     * (2 to the 53) - 1, both inclusive, and multiplies it by (2 to the -53). Using larger values than (2 to the
     * 53) would cause issues with the double math.
     *
     * @return a random uniform double between 0 and 1 (both exclusive)
     */
    default public double nextExclusiveDoubleEquidistant () {
        return (nextLong(0x1FFFFFFFFFFFFFL) + 1L) * 0x1p-53;
    }

    /**
     * Just like {@code #nextDouble(double)}, but this is exclusive on both 0.0 and {@code outerBound}.
     * Like {@code #nextExclusiveDouble()}, which this uses, this may have better bit-distribution of
     * double values, and it may also be better able to produce very small doubles when {@code outerBound} is large.
     * It should typically be a little faster than {@code #nextDouble(double)}.
     *
     * @param outerBound the outer exclusive bound; may be positive or negative
     * @return a double between 0.0, exclusive, and {@code outerBound}, exclusive
     */
    default public double nextExclusiveDouble (double outerBound) {
        return nextExclusiveDouble() * outerBound;
    }

    /**
     * Just like {@code #nextDouble(double, double)}, but this is exclusive on both {@code innerBound} and {@code outerBound}.
     * Like {@code #nextExclusiveDouble()}, which this uses,, this may have better bit-distribution of double values,
     * and it may also be better able to produce doubles close to innerBound when {@code outerBound - innerBound} is large.
     * It should typically be a little faster than {@code #nextDouble(double, double)}.
     *
     * @param innerBound the inner exclusive bound; may be positive or negative
     * @param outerBound the outer exclusive bound; may be positive or negative
     * @return a double between {@code innerBound}, exclusive, and {@code outerBound}, exclusive
     */
    default public double nextExclusiveDouble (double innerBound, double outerBound) {
        return innerBound + nextExclusiveDouble() * (outerBound - innerBound);
    }

    /**
     * Gets a random double that may be positive or negative, but cannot be 0, and always has a magnitude less than 1.
     * <br>
     * This is a modified version of <a href="https://allendowney.com/research/rand/">this
     * algorithm by Allen Downey</a>. This version can return double values between -0.9999999999999999 and
     * -5.421010862427522E-20, as well as between 2.710505431213761E-20 and 0.9999999999999999, or -0x1.fffffffffffffp-1
     * to -0x1.0p-64 as well as between 0x1.0p-65 and 0x1.fffffffffffffp-1 in hex notation. It cannot return -1, 0 or 1.
     * It has much more uniform bit distribution across its mantissa/significand bits than {@code Random#nextDouble()},
     * especially when the result of nextDouble() is expanded to the -1.0 to 1.0 range (such as with
     * {@code 2.0 * (nextDouble() - 0.5)}). Where the given example code is unable to produce a "1" bit for its lowest
     * bit of mantissa (the least significant bits numerically, but potentially important for some uses), this has
     * approximately the same likelihood of producing a "1" bit for any positions in the mantissa, and also equal odds
     * for the sign bit.
     * @return a random uniform double between -1 and 1 with a tiny hole around 0 (all exclusive)
     */
    default public double nextExclusiveSignedDouble(){
        final long bits = nextLong();
        return Double.longBitsToDouble(1022L - Long.numberOfLeadingZeros(bits) << 52 | ((bits << 63 | bits >>> 1) & 0x800FFFFFFFFFFFFFL));
    }

    /**
     * Gets a random float between 0.0 and 1.0, exclusive at both ends. This method is also more uniform than
     * {@code #nextFloat()} if you use the bit-patterns of the returned floats. This is a simplified version of
     * <a href="https://allendowney.com/research/rand/">this algorithm by Allen Downey</a>. This version can
     * return float values between 2.7105054E-20 to 0.99999994, or 0x1.0p-65 to 0x1.fffffep-1 in hex notation.
     * It cannot return 0 or 1. To compare, nextFloat() is less likely to produce a "1" bit for its
     * lowest 5 bits of mantissa/significand (the least significant bits numerically, but potentially important
     * for some uses), with the least significant bit produced half as often as the most significant bit in the
     * mantissa. As for this method, it has approximately the same likelihood of producing a "1" bit for any
     * position in the mantissa.
     * <br>
     * The implementation may have different performance characteristics than {@code #nextFloat()},
     * because this doesn't perform any floating-point multiplication or division, and instead assembles bits
     * obtained by one call to {@code #nextLong()}. This uses {@code NumberUtils#intBitsToFloat(int)} and
     * {@code Long#numberOfTrailingZeros(long)}, both of which typically have optimized intrinsics on HotSpot,
     * and this is branchless and loopless, unlike the original algorithm by Allen Downey. When compared with
     * {@code #nextExclusiveFloatEquidistant()}, this method performs better on at least HotSpot JVMs. On GraalVM 17,
     * this is over twice as fast as nextExclusiveFloatEquidistant().
     *
     * @return a random uniform float between 0 and 1 (both exclusive)
     */
    default public float nextExclusiveFloat () {
        final long bits = nextLong();
        return Float.intBitsToFloat(126 - Long.numberOfTrailingZeros(bits) << 23 | (int)(bits >>> 41));
    }

    /**
     * Gets a random float between 0.0 and 1.0, exclusive at both ends. This can return float
     * values between 5.9604645E-8 and 0.99999994, or 0x1.0p-24 and 0x1.fffffep-1 in hex notation.
     * It cannot return 0 or 1, and its minimum and maximum results are equally distant from 0 and from
     * 1, respectively. Most usages might prefer {@code #nextExclusiveFloat()}, which is
     * better-distributed if you consider the bit representation of the returned floats, tends to perform
     * better, and can return floats that much closer to 0 than this can.
     * <br>
     * The implementation simply uses {@code #nextInt(int)} to get a uniformly-chosen int between 1 and
     * (2 to the 24) - 1, both inclusive, and multiplies it by (2 to the -24). Using larger values than (2 to the
     * 24) would cause issues with the float math.
     *
     * @return a random uniform float between 0 and 1 (both exclusive)
     */
    default public float nextExclusiveFloatEquidistant () {
        return (nextInt(0xFFFFFF) + 1) * 0x1p-24f;
    }

    /**
     * Just like {@code #nextFloat(float)}, but this is exclusive on both 0.0 and {@code outerBound}.
     * Like {@code #nextExclusiveFloat()}, this may have better bit-distribution of float values, and
     * it may also be better able to produce very small floats when {@code outerBound} is large.
     * It should be a little faster than {@code #nextFloat(float)}.
     *
     * @param outerBound the outer exclusive bound; may be positive or negative
     * @return a float between 0.0, exclusive, and {@code outerBound}, exclusive
     */
    default public float nextExclusiveFloat (float outerBound) {
        return nextExclusiveFloat() * outerBound;
    }

    /**
     * Just like {@code #nextFloat(float, float)}, but this is exclusive on both {@code innerBound} and {@code outerBound}.
     * Like {@code #nextExclusiveFloat()}, this may have better bit-distribution of float values, and
     * it may also be better able to produce floats close to innerBound when {@code outerBound - innerBound} is large.
     * It should be a little faster than {@code #nextFloat(float, float)}.
     *
     * @param innerBound the inner exclusive bound; may be positive or negative
     * @param outerBound the outer exclusive bound; may be positive or negative
     * @return a float between {@code innerBound}, exclusive, and {@code outerBound}, exclusive
     */
    default public float nextExclusiveFloat (float innerBound, float outerBound) {
        return innerBound + nextExclusiveFloat() * (outerBound - innerBound);
    }

    /**
     * Gets a random float that may be positive or negative, but cannot be 0, and always has a magnitude less than 1.
     * <br>
     * This is a modified version of <a href="https://allendowney.com/research/rand/">this
     * algorithm by Allen Downey</a>. This version can return double values between -0.99999994 and -1.1641532E-10, as
     * well as between 2.7105054E-20 and 0.99999994, or -0x1.fffffep-1 to -0x1.0p-33 as well as between 0x1.0p-65 and
     * 0x1.fffffep-1 in hex notation. It cannot return -1, 0 or 1. It has much more uniform bit distribution across its
     * mantissa/significand bits than {@code Random#nextDouble()}, especially when the result of nextDouble() is
     * expanded to the -1.0 to 1.0 range (such as with {@code 2.0 * (nextDouble() - 0.5)}). Where the given example code
     * is unable to produce a "1" bit for its lowest bit of mantissa (the least significant bits numerically, but
     * potentially important for some uses), this has approximately the same likelihood of producing a "1" bit for any
     * positions in the mantissa, and also equal odds for the sign bit.
     * @return a random uniform double between -1 and 1 with a tiny hole around 0 (all exclusive)
     */
    default public float nextExclusiveSignedFloat(){
        final long bits = nextLong();
        return Float.intBitsToFloat(126 - Long.numberOfLeadingZeros(bits) << 23 | ((int)bits & 0x807FFFFF));
    }


    /**
     * Returns the next pseudorandom, Gaussian ("normally") distributed
     * {@code double} value with mean {@code 0.0} and standard
     * deviation {@code 1.0} from this random number generator's sequence.
     * <p>
     * The general contract of {@code nextGaussian} is that one
     * {@code double} value, chosen from (approximately) the usual
     * normal distribution with mean {@code 0.0} and standard deviation
     * {@code 1.0}, is pseudorandomly generated and returned.
     * <p>
     * This uses {@code #probit(double)} to "reshape" a random double into the
     * normal distribution. This requests exactly one long from the generator's
     * sequence (using {@code #nextExclusiveDouble()}). This makes it different
     * from code like java.util.Random's nextGaussian() method, which can (rarely)
     * fetch a higher number of random doubles.
     * <p>
     * The lowest value this can return is {@code -9.155293773112453}, while
     * the highest value this can return is {@code 8.209536145151493}. The
     * asymmetry is due to how IEEE 754 doubles work; doubles can be closer to
     * 0.0 than they can be to 1.0, and {@code #probit(double)} takes a double
     * between 0.0 and 1.0 .
     *
     * @return the next pseudorandom, Gaussian ("normally") distributed
     * {@code double} value with mean {@code 0.0} and standard deviation
     * {@code 1.0} from this random number generator's sequence
     */
    default public double nextGaussian () {
        return probit(nextExclusiveDouble());
    }

    /**
     * Returns the next pseudorandom, Gaussian ("normally") distributed {@code double}
     * value with the specified mean and standard deviation from this random number generator's sequence.
     * <br>
     * This defaults to simply returning {@code mean + stddev * nextGaussian()}.
     *
     * @param mean the mean of the Gaussian distribution to be drawn from
     * @param stddev the standard deviation (square root of the variance)
     *        of the Gaussian distribution to be drawn from
     *
     * @return a Gaussian distributed {@code double} with the specified mean and standard deviation
     */
    default public double nextGaussian(double mean, double stddev) {
        return mean + stddev * nextGaussian();
    }

    /**
     * A way of taking a double in the (0.0, 1.0) range and mapping it to a Gaussian or normal distribution, so high
     * inputs correspond to high outputs, and similarly for the low range. This is centered on 0.0 and its standard
     * deviation seems to be 1.0 (the same as {@code Random#nextGaussian()}). If this is given an input of 0.0
     * or less, it returns -38.5, which is slightly less than the result when given {@code Double#MIN_VALUE}. If it is
     * given an input of 1.0 or more, it returns 38.5, which is significantly larger than the result when given the
     * largest double less than 1.0 (this value is further from 1.0 than {@code Double#MIN_VALUE} is from 0.0). If
     * given {@code Double#NaN}, it returns whatever {@code Math#copySign(double, double)} returns for the arguments
     * {@code 38.5, Double.NaN}, which is implementation-dependent. It uses an algorithm by Peter John Acklam, as
     * implemented by Sherali Karimov.
     * <a href="https://web.archive.org/web/20150910002142/http://home.online.no/~pjacklam/notes/invnorm/impl/karimov/StatUtil.java">Original source</a>.
     * <a href="https://web.archive.org/web/20151030215612/http://home.online.no/~pjacklam/notes/invnorm/">Information on the algorithm</a>.
     * <a href="https://en.wikipedia.org/wiki/Probit_function">Wikipedia's page on the probit function</a> may help, but
     * is more likely to just be confusing.
     * <br>
     * Acklam's algorithm and Karimov's implementation are both quite fast. This appears faster than generating
     * Gaussian-distributed numbers using either the Box-Muller Transform or Marsaglia's Polar Method, though it isn't
     * as precise and can't produce as extreme min and max results in the extreme cases they should appear. If given
     * a typical uniform random {@code double} that's exclusive on 1.0, it won't produce a result higher than
     * {@code 8.209536145151493}, and will only produce results of at least {@code -8.209536145151493} if 0.0 is
     * excluded from the inputs (if 0.0 is an input, the result is {@code -38.5}). A chief advantage of using this with
     * a random number generator is that it only requires one random double to obtain one Gaussian value;
     * {@code Random#nextGaussian()} generates at least two random doubles for each two Gaussian values, but
     * may rarely require much more random generation.
     * <br>
     * This can be used both as an optimization for generating Gaussian random values, and as a way of generating
     * Gaussian values that match a pattern present in the inputs (which you could have by using a sub-random sequence
     * as the input, such as those produced by a van der Corput, Halton, Sobol or R2 sequence). Most methods of generating
     * Gaussian values (e.g. Box-Muller and Marsaglia polar) do not have any way to preserve a particular pattern.
     *
     * @param d should be between 0 and 1, exclusive, but other values are tolerated
     * @return a normal-distributed double centered on 0.0; all results will be between -38.5 and 38.5, both inclusive
     */
    public static double probit (final double d) {
        if (d <= 0 || d >= 1) {
            return Math.copySign(38.5, d - 0.5);
        } else if (d < 0.02425) {
            final double q = Math.sqrt(-2.0 * Math.log(d));
            return (((((-7.784894002430293e-03 * q - 3.223964580411365e-01) * q - 2.400758277161838e+00) * q - 2.549732539343734e+00) * q + 4.374664141464968e+00) * q + 2.938163982698783e+00) / (
                    (((7.784695709041462e-03 * q + 3.224671290700398e-01) * q + 2.445134137142996e+00) * q + 3.754408661907416e+00) * q + 1.0);
        } else if (0.97575 < d) {
            final double q = Math.sqrt(-2.0 * Math.log(1 - d));
            return -(((((-7.784894002430293e-03 * q - 3.223964580411365e-01) * q - 2.400758277161838e+00) * q - 2.549732539343734e+00) * q + 4.374664141464968e+00) * q + 2.938163982698783e+00) / (
                    (((7.784695709041462e-03 * q + 3.224671290700398e-01) * q + 2.445134137142996e+00) * q + 3.754408661907416e+00) * q + 1.0);
        }
        final double q = d - 0.5;
        final double r = q * q;
        return (((((-3.969683028665376e+01 * r + 2.209460984245205e+02) * r - 2.759285104469687e+02) * r + 1.383577518672690e+02) * r - 3.066479806614716e+01) * r + 2.506628277459239e+00) * q / (
                ((((-5.447609879822406e+01 * r + 1.615858368580409e+02) * r - 1.556989798598866e+02) * r + 6.680131188771972e+01) * r - 1.328068155288572e+01) * r + 1.0);
    }

    default public double nextGaussian(double _bound, double _factor, double _base) {
        double _ret = -2.0*_bound;
        while(Math.abs(_ret)>=_bound)
        {
            _ret = this.nextGaussian();
        }
        return (_ret*_factor)+_base;
    }

    default public double nextVariation(double _number, double _variation)
    {
        return _number + (_variation * (2.*nextDouble()-1.));
    }

    default public float nextVariation(float _number, float _variation)
    {
        return _number + (_variation * (2f*nextFloat()-1f));
    }

    /**
     * Returns true if a random value between 0 and 1 is less than the specified value.
     *
     * @param chance a float between 0.0 and 1.0; higher values are more likely to result in true
     * @return a boolean selected with the given {@code chance} of being true
     */
    default public boolean nextBoolean (float chance) {
        return nextFloat() < chance;
    }

    /**
     * Returns -1 or 1, randomly.
     *
     * @return -1 or 1, selected with approximately equal likelihood
     */
    default public int nextSign () {
        return 1 | nextInt() >> 31;
    }


    default public double nextAngleDegrees()
    {
        return nextDouble()*360.;
    }

    default public double nextAngleRadians()
    {
        return nextDouble()*Math.PI*2.;
    }

    /**
     * Returns a triangularly distributed random number between -1.0 (exclusive) and 1.0 (exclusive), where values around zero are
     * more likely. Advances the state twice.
     * <p>
     * This is an optimized version of {@code nextTriangular(-1, 1, 0)}
     */
    default public double nextTriangular () {
        return nextDouble() - nextDouble();
    }

    /**
     * Returns a triangularly distributed random number between {@code -max} (exclusive) and {@code max} (exclusive), where values
     * around zero are more likely. Advances the state twice.
     * <p>
     * This is an optimized version of {@code nextTriangular(-max, max, 0)}
     *
     * @param _max the upper limit
     */
    default public double nextTriangular (double _max) {
        return (nextDouble() - nextDouble()) * _max;
    }

    /**
     * Returns a triangularly distributed random number between {@code min} (inclusive) and {@code max} (exclusive), where the
     * {@code mode} argument defaults to the midpoint between the bounds, giving a symmetric distribution. Advances the state once.
     * <p>
     * This method is equivalent to {@code nextTriangular(min, max, (min + max) * 0.5f)}
     *
     * @param _min the lower limit
     * @param _max the upper limit
     */
    default public double nextTriangular (double _min, double _max) {
        return nextTriangular(_min, _max, (_min + _max) * .5);
    }

    /**
     * Returns a triangularly distributed random number between {@code min} (inclusive) and {@code max} (exclusive), where values
     * around {@code mode} are more likely. Advances the state once.
     *
     * @param _min  the lower limit
     * @param _max  the upper limit
     * @param _mode the point around which the values are more likely
     */
    default public double nextTriangular (double _min, double _max, double _mode) {
        double _u = nextDouble();
        double _d = _max - _min;
        if (_u <= (_mode - _min) / _d) return _min + Math.sqrt(_u * _d * (_mode - _min));
        return _max - Math.sqrt((1 - _u) * _d * (_max - _mode));
    }

    /**
     * Returns something like a triangularly distributed random number between -1.0 (exclusive) and 1.0 (exclusive), where values around zero are
     * more likely. Advances the state twice.
     * <p>
     * This method is equivalent to {@code nextTriangularCubic(-1, 1, 0)}
     */
    default public double nextTriangularCubic () {
        return nextTriangularCubic(-1., 1., 0.);
    }

    /**
     * Returns something like a triangularly distributed random number between {@code -max} (exclusive) and {@code max} (exclusive), where values
     * around zero are more likely. Advances the state twice.
     * <p>
     * This method is equivalent to {@code nextTriangularCubic(-max, max, 0)}
     *
     * @param _max the upper limit
     */
    default public double nextTriangularCubic (double _max) {
        return nextTriangularCubic(-_max, _max, 0.);
    }

    /**
     * Returns something like a triangularly distributed random number between {@code min} (inclusive) and {@code max} (exclusive), where the
     * {@code mode} argument defaults to the midpoint between the bounds, giving a symmetric distribution. Advances the state once.
     * <p>
     * This method is equivalent to {@code nextTriangularCubic(min, max, (min + max) * 0.5)}
     *
     * @param _min the lower limit
     * @param _max the upper limit
     */
    default public double nextTriangularCubic (double _min, double _max) {
        return nextTriangularCubic(_min, _max, (_min + _max) * .5);
    }

    /**
     * Returns something like a triangularly distributed random number between {@code min} (inclusive) and {@code max}
     * (exclusive), where values around {@code mode} are more likely. Advances the state once.
     *
     * @param _min  the lower limit
     * @param _max  the upper limit
     * @param _mode the point around which the values are more likely
     */
    default public double nextTriangularCubic (double _min, double _max, double _mode) {
        double _u = nextDouble();
        double _d = _max - _min;
        if (_u <= (_mode - _min) / _d) return _min + Math.cbrt(_u * _d * (_mode - _min));
        return _max - Math.cbrt((1. - _u) * _d * (_max - _mode));
    }

    /**
     * Returns the minimum result of {@code trials} calls to {@code #nextSignedInt(int, int)} using the given {@code innerBound}
     * and {@code outerBound}. The innerBound is inclusive; the outerBound is exclusive.
     * The higher trials is, the lower the average value this returns.
     *
     * @param innerBound the inner inclusive bound; may be positive or negative
     * @param outerBound the outer exclusive bound; may be positive or negative
     * @param trials     how many random numbers to acquire and compare
     * @return the lowest random number between innerBound (inclusive) and outerBound (exclusive) this found
     */
    default public int minIntOf (int innerBound, int outerBound, int trials) {
        int v = nextSignedInt(innerBound, outerBound);
        for (int i = 1; i < trials; i++) {
            v = Math.min(v, nextSignedInt(innerBound, outerBound));
        }
        return v;
    }

    /**
     * Returns the maximum result of {@code trials} calls to {@code #nextSignedInt(int, int)} using the given {@code innerBound}
     * and {@code outerBound}. The innerBound is inclusive; the outerBound is exclusive.
     * The higher trials is, the higher the average value this returns.
     *
     * @param innerBound the inner inclusive bound; may be positive or negative
     * @param outerBound the outer exclusive bound; may be positive or negative
     * @param trials     how many random numbers to acquire and compare
     * @return the highest random number between innerBound (inclusive) and outerBound (exclusive) this found
     */
    default public int maxIntOf (int innerBound, int outerBound, int trials) {
        int v = nextSignedInt(innerBound, outerBound);
        for (int i = 1; i < trials; i++) {
            v = Math.max(v, nextSignedInt(innerBound, outerBound));
        }
        return v;
    }

    /**
     * Returns the minimum result of {@code trials} calls to {@code #nextSignedLong(long, long)} using the given {@code innerBound}
     * and {@code outerBound}. The innerBound is inclusive; the outerBound is exclusive.
     * The higher trials is, the lower the average value this returns.
     *
     * @param innerBound the inner inclusive bound; may be positive or negative
     * @param outerBound the outer exclusive bound; may be positive or negative
     * @param trials     how many random numbers to acquire and compare
     * @return the lowest random number between innerBound (inclusive) and outerBound (exclusive) this found
     */
    default public long minLongOf (long innerBound, long outerBound, int trials) {
        long v = nextSignedLong(innerBound, outerBound);
        for (int i = 1; i < trials; i++) {
            v = Math.min(v, nextSignedLong(innerBound, outerBound));
        }
        return v;
    }

    /**
     * Returns the maximum result of {@code trials} calls to {@code #nextSignedLong(long, long)} using the given {@code innerBound}
     * and {@code outerBound}. The innerBound is inclusive; the outerBound is exclusive.
     * The higher trials is, the higher the average value this returns.
     *
     * @param innerBound the inner inclusive bound; may be positive or negative
     * @param outerBound the outer exclusive bound; may be positive or negative
     * @param trials     how many random numbers to acquire and compare
     * @return the highest random number between innerBound (inclusive) and outerBound (exclusive) this found
     */
    default public long maxLongOf (long innerBound, long outerBound, int trials) {
        long v = nextSignedLong(innerBound, outerBound);
        for (int i = 1; i < trials; i++) {
            v = Math.max(v, nextSignedLong(innerBound, outerBound));
        }
        return v;
    }

    /**
     * Returns the minimum result of {@code trials} calls to {@code #nextDouble(double, double)} using the given {@code innerBound}
     * and {@code outerBound}. The innerBound is inclusive; the outerBound is exclusive.
     * The higher trials is, the lower the average value this returns.
     *
     * @param innerBound the inner inclusive bound; may be positive or negative
     * @param outerBound the outer exclusive bound; may be positive or negative
     * @param trials     how many random numbers to acquire and compare
     * @return the lowest random number between innerBound (inclusive) and outerBound (exclusive) this found
     */
    default public double minDoubleOf (double innerBound, double outerBound, int trials) {
        double v = nextDouble(innerBound, outerBound);
        for (int i = 1; i < trials; i++) {
            v = Math.min(v, nextDouble(innerBound, outerBound));
        }
        return v;
    }

    /**
     * Returns the maximum result of {@code trials} calls to {@code #nextDouble(double, double)} using the given {@code innerBound}
     * and {@code outerBound}. The innerBound is inclusive; the outerBound is exclusive.
     * The higher trials is, the higher the average value this returns.
     *
     * @param innerBound the inner inclusive bound; may be positive or negative
     * @param outerBound the outer exclusive bound; may be positive or negative
     * @param trials     how many random numbers to acquire and compare
     * @return the highest random number between innerBound (inclusive) and outerBound (exclusive) this found
     */
    default public double maxDoubleOf (double innerBound, double outerBound, int trials) {
        double v = nextDouble(innerBound, outerBound);
        for (int i = 1; i < trials; i++) {
            v = Math.max(v, nextDouble(innerBound, outerBound));
        }
        return v;
    }

    /**
     * Returns the minimum result of {@code trials} calls to {@code #nextFloat(float, float)} using the given {@code innerBound}
     * and {@code outerBound}. The innerBound is inclusive; the outerBound is exclusive.
     * The higher trials is, the lower the average value this returns.
     *
     * @param innerBound the inner inclusive bound; may be positive or negative
     * @param outerBound the outer exclusive bound; may be positive or negative
     * @param trials     how many random numbers to acquire and compare
     * @return the lowest random number between innerBound (inclusive) and outerBound (exclusive) this found
     */
    default public float minFloatOf (float innerBound, float outerBound, int trials) {
        float v = nextFloat(innerBound, outerBound);
        for (int i = 1; i < trials; i++) {
            v = Math.min(v, nextFloat(innerBound, outerBound));
        }
        return v;
    }

    /**
     * Returns the maximum result of {@code trials} calls to {@code #nextFloat(float, float)} using the given {@code innerBound}
     * and {@code outerBound}. The innerBound is inclusive; the outerBound is exclusive.
     * The higher trials is, the higher the average value this returns.
     *
     * @param innerBound the inner inclusive bound; may be positive or negative
     * @param outerBound the outer exclusive bound; may be positive or negative
     * @param trials     how many random numbers to acquire and compare
     * @return the highest random number between innerBound (inclusive) and outerBound (exclusive) this found
     */
    default public float maxFloatOf (float innerBound, float outerBound, int trials) {
        float v = nextFloat(innerBound, outerBound);
        for (int i = 1; i < trials; i++) {
            v = Math.max(v, nextFloat(innerBound, outerBound));
        }
        return v;
    }

    /**
     * Gets a randomly-selected item from the given array, which must be non-null and non-empty
     *
     * @param array a non-null, non-empty array of {@code T} items
     * @param <T>   any reference type
     * @return a random item from {@code array}
     * @throws NullPointerException      if array is null
     * @throws IndexOutOfBoundsException if array is empty
     */
    default public <T> T randomElement (T[] array) {
        return array[nextInt(array.length)];
    }

    /**
     * Gets a randomly selected item from the given List, such as an ArrayList.
     * If the List is empty, this throws an IndexOutOfBoundsException.
     *
     * @param list    a non-empty implementation of List, such as ArrayList
     * @param <T>     the type of items
     * @return a randomly-selected item from list
     */
    default public <T> T randomElement (List<T> list) {
        return list.get(nextInt(list.size()));
    }

    // Shuffling arrays and Arrays.

    /**
     * Shuffles the given array in-place pseudo-randomly, using this to determine how to shuffle.
     *
     * @param items an int array; must be non-null
     */
    default public void shuffle (int[] items) {
        shuffle(items, 0, items.length);
    }

    /**
     * Shuffles a section of the given array in-place pseudo-randomly, using this to determine how to shuffle.
     *
     * @param items  an int array; must be non-null
     * @param offset the index of the first element of the array that can be shuffled
     * @param length the length of the section to shuffle
     */
    default public void shuffle (int[] items, int offset, int length) {
        offset = Math.min(Math.max(0, offset), items.length);
        length = Math.min(items.length - offset, Math.max(0, length));
        for (int i = offset + length - 1; i > offset; i--) {
            int ii = nextInt(offset, i + 1);
            int temp = items[i];
            items[i] = items[ii];
            items[ii] = temp;
        }
    }

    /**
     * Shuffles the given array in-place pseudo-randomly, using this to determine how to shuffle.
     *
     * @param items a long array; must be non-null
     */
    default public void shuffle (long[] items) {
        shuffle(items, 0, items.length);
    }

    /**
     * Shuffles a section of the given array in-place pseudo-randomly, using this to determine how to shuffle.
     *
     * @param items  a long array; must be non-null
     * @param offset the index of the first element of the array that can be shuffled
     * @param length the length of the section to shuffle
     */
    default public void shuffle (long[] items, int offset, int length) {
        offset = Math.min(Math.max(0, offset), items.length);
        length = Math.min(items.length - offset, Math.max(0, length));
        for (int i = offset + length - 1; i > offset; i--) {
            int ii = nextInt(offset, i + 1);
            long temp = items[i];
            items[i] = items[ii];
            items[ii] = temp;
        }
    }

    /**
     * Shuffles the given array in-place pseudo-randomly, using this to determine how to shuffle.
     *
     * @param items a float array; must be non-null
     */
    default public void shuffle (float[] items) {
        shuffle(items, 0, items.length);
    }

    /**
     * Shuffles a section of the given array in-place pseudo-randomly, using this to determine how to shuffle.
     *
     * @param items  a float array; must be non-null
     * @param offset the index of the first element of the array that can be shuffled
     * @param length the length of the section to shuffle
     */
    default public void shuffle (float[] items, int offset, int length) {
        offset = Math.min(Math.max(0, offset), items.length);
        length = Math.min(items.length - offset, Math.max(0, length));
        for (int i = offset + length - 1; i > offset; i--) {
            int ii = nextInt(offset, i + 1);
            float temp = items[i];
            items[i] = items[ii];
            items[ii] = temp;
        }
    }

    /**
     * Shuffles the given array in-place pseudo-randomly, using this to determine how to shuffle.
     *
     * @param items a char array; must be non-null
     */
    default public void shuffle (char[] items) {
        shuffle(items, 0, items.length);
    }

    /**
     * Shuffles a section of the given array in-place pseudo-randomly, using this to determine how to shuffle.
     *
     * @param items  a char array; must be non-null
     * @param offset the index of the first element of the array that can be shuffled
     * @param length the length of the section to shuffle
     */
    default public void shuffle (char[] items, int offset, int length) {
        offset = Math.min(Math.max(0, offset), items.length);
        length = Math.min(items.length - offset, Math.max(0, length));
        for (int i = offset + length - 1; i > offset; i--) {
            int ii = nextInt(offset, i + 1);
            char temp = items[i];
            items[i] = items[ii];
            items[ii] = temp;
        }
    }

    /**
     * Shuffles the given array in-place pseudo-randomly, using this to determine how to shuffle.
     *
     * @param items a byte array; must be non-null
     */
    default public void shuffle (byte[] items) {
        shuffle(items, 0, items.length);
    }

    /**
     * Shuffles a section of the given array in-place pseudo-randomly, using this to determine how to shuffle.
     *
     * @param items  a byte array; must be non-null
     * @param offset the index of the first element of the array that can be shuffled
     * @param length the length of the section to shuffle
     */
    default public void shuffle (byte[] items, int offset, int length) {
        offset = Math.min(Math.max(0, offset), items.length);
        length = Math.min(items.length - offset, Math.max(0, length));
        for (int i = offset + length - 1; i > offset; i--) {
            int ii = nextInt(offset, i + 1);
            byte temp = items[i];
            items[i] = items[ii];
            items[ii] = temp;
        }
    }

    /**
     * Shuffles the given array in-place pseudo-randomly, using this to determine how to shuffle.
     *
     * @param items a double array; must be non-null
     */
    default public void shuffle (double[] items) {
        shuffle(items, 0, items.length);
    }

    /**
     * Shuffles a section of the given array in-place pseudo-randomly, using this to determine how to shuffle.
     *
     * @param items  a double array; must be non-null
     * @param offset the index of the first element of the array that can be shuffled
     * @param length the length of the section to shuffle
     */
    default public void shuffle (double[] items, int offset, int length) {
        offset = Math.min(Math.max(0, offset), items.length);
        length = Math.min(items.length - offset, Math.max(0, length));
        for (int i = offset + length - 1; i > offset; i--) {
            int ii = nextInt(offset, i + 1);
            double temp = items[i];
            items[i] = items[ii];
            items[ii] = temp;
        }
    }

    /**
     * Shuffles the given array in-place pseudo-randomly, using this to determine how to shuffle.
     *
     * @param items a short array; must be non-null
     */
    default public void shuffle (short[] items) {
        shuffle(items, 0, items.length);
    }

    /**
     * Shuffles a section of the given array in-place pseudo-randomly, using this to determine how to shuffle.
     *
     * @param items  a short array; must be non-null
     * @param offset the index of the first element of the array that can be shuffled
     * @param length the length of the section to shuffle
     */
    default public void shuffle (short[] items, int offset, int length) {
        offset = Math.min(Math.max(0, offset), items.length);
        length = Math.min(items.length - offset, Math.max(0, length));
        for (int i = offset + length - 1; i > offset; i--) {
            int ii = nextInt(offset, i + 1);
            short temp = items[i];
            items[i] = items[ii];
            items[ii] = temp;
        }
    }

    /**
     * Shuffles the given array in-place pseudo-randomly, using this to determine how to shuffle.
     *
     * @param items a boolean array; must be non-null
     */
    default public void shuffle (boolean[] items) {
        shuffle(items, 0, items.length);
    }

    /**
     * Shuffles a section of the given array in-place pseudo-randomly, using this to determine how to shuffle.
     *
     * @param items  a boolean array; must be non-null
     * @param offset the index of the first element of the array that can be shuffled
     * @param length the length of the section to shuffle
     */
    default public void shuffle (boolean[] items, int offset, int length) {
        offset = Math.min(Math.max(0, offset), items.length);
        length = Math.min(items.length - offset, Math.max(0, length));
        for (int i = offset + length - 1; i > offset; i--) {
            int ii = nextInt(offset, i + 1);
            boolean temp = items[i];
            items[i] = items[ii];
            items[ii] = temp;
        }
    }

    /**
     * Shuffles the given array in-place pseudo-randomly, using this to determine how to shuffle.
     *
     * @param items an array of some reference type; must be non-null but may contain null items
     */
    default public <T> void shuffle (T[] items) {
        shuffle(items, 0, items.length);
    }

    /**
     * Shuffles a section of the given array in-place pseudo-randomly, using this to determine how to shuffle.
     *
     * @param items  an array of some reference type; must be non-null but may contain null items
     * @param offset the index of the first element of the array that can be shuffled
     * @param length the length of the section to shuffle
     */
    default public <T> void shuffle (T[] items, int offset, int length) {
        offset = Math.min(Math.max(0, offset), items.length);
        length = Math.min(items.length - offset, Math.max(0, length));
        for (int i = offset + length - 1; i > offset; i--) {
            int ii = nextInt(offset, i + 1);
            T temp = items[i];
            items[i] = items[ii];
            items[ii] = temp;
        }
    }

    /**
     * Shuffles the given List in-place pseudo-randomly, using this to determine how to shuffle.
     *
     * @param items a List of some type {@code T}; must be non-null but may contain null items
     */
    default public <T> void shuffle (List<T> items) {
        shuffle(items, 0, items.size());
    }

    default public <T> T nextItem (List<T> items)
    {
        return items.get(nextInt(items.size()));
    }

    default public <T> T nextItem (T[] items)
    {
        return items[nextInt(items.length)];
    }

    default public <T> List<T> shuffleAndPick (List<T> items, int num)
    {
        List<T> _ret = new ArrayList<>(num);
        int[] _idx = new int[items.size()];
        for(int _i=0; _i< _idx.length; _i++)
        {
            _idx[_i]=_i;
        }
        shuffle(_idx);
        for(int _i=0; _i< num; _i++)
        {
            _ret.add(items.get(_idx[_i]));
        }
        return _ret;
    }

    default public <T> List<T> shuffleAndPick (T[] items, int num)
    {
        List<T> _ret = new ArrayList<>(num);
        int[] _idx = new int[items.length];
        for(int _i=0; _i< _idx.length; _i++)
        {
            _idx[_i]=_i;
        }
        shuffle(_idx);
        for(int _i=0; _i< num; _i++)
        {
            _ret.add(items[_idx[_i]]);
        }
        return _ret;
    }

    /**
     * Shuffles a section of the given List in-place pseudo-randomly, using this to determine how to shuffle.
     *
     * @param items  a List of some type {@code T}; must be non-null but may contain null items
     * @param offset the index of the first element of the array that can be shuffled
     * @param length the length of the section to shuffle
     */
    default public <T> void shuffle (List<T> items, int offset, int length) {
        offset = Math.min(Math.max(0, offset), items.size());
        length = Math.min(items.size() - offset, Math.max(0, length));
        for (int i = offset + length - 1; i > offset; i--) {
            int ii = nextInt(offset, i + 1);
            T temp = items.get(i);
            items.set(i, items.get(ii));
            items.set(ii, temp);
        }
    }


    // Randomized geometry methods.

    /**
     * Gets a random value (usually an angle) between 0 and TAU (which is {@code Math.PI * 2f}),
     * inclusive on 0 and exclusive on tau.
     * @return a random float between 0 (inclusive) and TAU (exclusive)
     */
    default public double nextRadians() {
        return nextDouble() * Math.PI*2.;
    }

    /**
     * Gets a random value (usually an angle) between 0 and 360, inclusive on 0 and exclusive on 360.
     * @return a random float between 0 (inclusive) and 360 (exclusive)
     */
    default public double nextDegrees() {
        return nextDouble() * 360f;
    }

    /**
     * Returns a new Vector2 that has a random angle and the specified length.
     * @param length the length that {@code vec} should have after changes
     * @return a new Vector2 with a random angle and the specified length
     */
    default public double[] nextVector2(double length) {
        double angle = nextRadians();
        return new double[]{Math.cos(angle) * length, Math.sin(angle) * length};
    }

    /**
     * Returns a new Vector2 that has a random angle and a length between {@code minLength}
     * (inclusive) and {@code maxLength} (exclusive).
     * @param minLength the minimum inclusive length that {@code vec} is permitted to have
     * @param maxLength the maximum exclusive length that {@code vec} is permitted to have
     * @return a new Vector2 with a random angle and a random length in the given range
     */
    default public double[] nextVector2(double minLength, double maxLength) {
        return nextVector2(nextDouble(minLength, maxLength));
    }

    /**
     * Returns a new Vector3 that has a random angle and the specified length.
     * @param length the length that {@code vec} should have after changes
     * @return a new Vector3 with a random angle and the specified length
     */
    default public double[] nextVector3(double length)
    {
        double azim = nextRadians();
        double polar = Math.acos((nextDouble() - 0.5) * 2.);
        double cosAzim = Math.cos(azim);
        double sinAzim = Math.sin(azim);
        double cosPolar = Math.cos(polar) * length;
        double sinPolar = Math.sin(polar) * length;
        return new double[]{cosAzim * sinPolar, sinAzim * sinPolar, cosPolar};
    }

    /**
     * Returns a new Vector3 that has a random angle and a length between {@code minLength}
     * (inclusive) and {@code maxLength} (exclusive).
     * @param minLength the minimum inclusive length that {@code vec} is permitted to have
     * @param maxLength the maximum exclusive length that {@code vec} is permitted to have
     * @return a new Vector3 with a random angle and a random length in the given range
     */
    default public double[] nextVector3(double minLength, double maxLength) {
        return nextVector3(nextDouble(minLength, maxLength));
    }

    // Randomized UUIDs.

    /**
     * Obtains a random {@code UUID} and returns it.
     * This calls {@code #nextLong()} twice and modifies one byte of each long to fit the UUID format.
     * This does not require initializing a SecureRandom instance, which makes this different from
     * {@code UUID#randomUUID()}. This should be used primarily with {@code RandomDistinct64}, because
     * the other implementations here are (theoretically) capable of returning the same UUID if this is
     * called many times over the course of the generator's period, while RandomDistinct64 cannot return
     * the same UUID, making the UUIDs actually unique until all (2 to the 63) UUIDs that RandomDistinct64
     * can return are exhausted.
     * @return a new random {@code UUID}
     */
    default public UUID nextUUID()
    {
        long msb = nextLong(), lsb = nextLong();
        msb &= 0xFF0FFFFFFFFFFFFFL;
        msb |= 0x0040000000000000L;
        lsb &= 0xFFFFFFFFFFFFFF3FL;
        lsb |= 0x0000000000000080L;
        return new UUID(msb, lsb);
    }

    default public String nextUUIDString()
    {
        return nextUUID().toString();
    }

    /**
     * Obtains a random {@code XUID} and returns it.
     *
     * @return a new random {@code XUID}
     */
    default public String nextXUID()
    {
        long msb1 = System.currentTimeMillis(), lsb1 = nextLong();
        long msb2 = System.nanoTime(), lsb2 = nextLong();
        return String.format("%s-%s-%s-%s",
                Long.toString(msb1,36),
                Long.toString(msb2,36),
                Long.toString(lsb1,36),
                Long.toString(lsb2,36));
    }

    // Color nextColor(): Random RGB, but A is always 1.
    //Color nextColor(min, max): Instead of 0-1, min-max it.
    //(Would be fun to have something like HSL but only H is random)

}