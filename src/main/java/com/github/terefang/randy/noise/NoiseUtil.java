package com.github.terefang.randy.noise;

import java.awt.*;
import java.util.UUID;

import static com.github.terefang.randy.noise.GradientVectors.*;

public abstract class NoiseUtil extends AbstractNoise
{
    /**
     * Given inputs as {@code x} in the range -1.0 to 1.0 that are too biased towards 0.0, this "squashes" the range
     * softly to widen it and spread it away from 0.0 without increasing bias anywhere else.
     * <br>
     * This starts with a common sigmoid function, {@code x / sqrt(x * x + add)}, but instead of approaching -1 and 1
     * but never reaching them, this multiplies the result so the line crosses -1 when x is -1, and crosses 1 when x is
     * 1. It has a smooth derivative, if that matters to you.
     *
     * @param x between -1 and 1
     * @param add if greater than 1, this will have nearly no effect; the lower this goes below 1, the more this will
     *           separate results near the center of the range. This must be greater than or equal to 0.0
     * @param mul typically the result of calling {@code Math.sqrt(add + 1f)}
     * @return a float with a slightly different distribution from {@code x}, but still between -1 and 1
     */
    public static double equalize(double x, double add, double mul) {
        return x * mul / Math.sqrt(x * x + add);
    }

    // ----------------------------------------------------------------------------

    public static double value(double _x, double _y)
    {
        return Math.sqrt(_x*_x+_y*_y);
    }

    public static double value(double _x, double _y, double _z)
    {
        return Math.sqrt(_x*_x+_y*_y+_z*_z);
    }

    public static long toSeed(String _seed)
    {
        UUID _u = UUID.nameUUIDFromBytes(_seed.getBytes());
        return _u.getMostSignificantBits() ^ _u.getLeastSignificantBits();
    }

    public static long toSeed(byte[] _seed)
    {
        UUID _u = UUID.nameUUIDFromBytes(_seed);
        return _u.getMostSignificantBits() ^ _u.getLeastSignificantBits();
    }

    public static double[] singleGradientPerturb2(int interpolation, int seed, double perturbAmp, double frequency, double[] v2)
    {
        double xf = v2[0] * frequency;
        double yf = v2[1] * frequency;

        int x0 = fastFloor(xf);
        int y0 = fastFloor(yf);
        int x1 = x0 + 1;
        int y1 = y0 + 1;

        double xs, ys;
        switch (interpolation) {
            default:
            case LINEAR:
                xs = xf - x0;
                ys = yf - y0;
                break;
            case HERMITE:
                xs = hermiteInterpolator(xf - x0);
                ys = hermiteInterpolator(yf - y0);
                break;
            case RADIAN:
                xs = radianInterpolator(xf - x0);
                ys = radianInterpolator(yf - y0);
                break;
            case QUINTIC:
                xs = quinticInterpolator(xf - x0);
                ys = quinticInterpolator(yf - y0);
                break;
        }

        double2 vec0 = CELL_2D[hash256(x0, y0, seed)];
        double2 vec1 = CELL_2D[hash256(x1, y0, seed)];

        double lx0x = lerp(vec0.x, vec1.x, xs);
        double ly0x = lerp(vec0.y, vec1.y, xs);

        vec0 = CELL_2D[hash256(x0, y1, seed)];
        vec1 = CELL_2D[hash256(x1, y1, seed)];

        double lx1x = lerp(vec0.x, vec1.x, xs);
        double ly1x = lerp(vec0.y, vec1.y, xs);

        v2[0] += lerp(lx0x, lx1x, ys) * perturbAmp;
        v2[1] += lerp(ly0x, ly1x, ys) * perturbAmp;

        return v2;
    }

    public static double[] singleGradientPerturb3(int interpolation, int seed, double perturbAmp, double frequency, double[] v3)
    {
        double xf = v3[0] * frequency;
        double yf = v3[1] * frequency;
        double zf = v3[2] * frequency;

        int x0 = fastFloor(xf);
        int y0 = fastFloor(yf);
        int z0 = fastFloor(zf);
        int x1 = x0 + 1;
        int y1 = y0 + 1;
        int z1 = z0 + 1;

        double xs, ys, zs;
        switch (interpolation) {
            default:
            case LINEAR:
                xs = xf - x0;
                ys = yf - y0;
                zs = zf - z0;
                break;
            case HERMITE:
                xs = hermiteInterpolator(xf - x0);
                ys = hermiteInterpolator(yf - y0);
                zs = hermiteInterpolator(zf - z0);
                break;
            case RADIAN:
                xs = radianInterpolator(xf - x0);
                ys = radianInterpolator(yf - y0);
                zs = radianInterpolator(zf - z0);
                break;
            case QUINTIC:
                xs = quinticInterpolator(xf - x0);
                ys = quinticInterpolator(yf - y0);
                zs = quinticInterpolator(zf - z0);
                break;
        }

        double3 vec0 = CELL_3D[hash256(x0, y0, z0, seed)];
        double3 vec1 = CELL_3D[hash256(x1, y0, z0, seed)];

        double lx0x = lerp(vec0.x, vec1.x, xs);
        double ly0x = lerp(vec0.y, vec1.y, xs);
        double lz0x = lerp(vec0.z, vec1.z, xs);

        vec0 = CELL_3D[hash256(x0, y1, z0, seed)];
        vec1 = CELL_3D[hash256(x1, y1, z0, seed)];

        double lx1x = lerp(vec0.x, vec1.x, xs);
        double ly1x = lerp(vec0.y, vec1.y, xs);
        double lz1x = lerp(vec0.z, vec1.z, xs);

        double lx0y = lerp(lx0x, lx1x, ys);
        double ly0y = lerp(ly0x, ly1x, ys);
        double lz0y = lerp(lz0x, lz1x, ys);

        vec0 = CELL_3D[hash256(x0, y0, z1, seed)];
        vec1 = CELL_3D[hash256(x1, y0, z1, seed)];

        lx0x = lerp(vec0.x, vec1.x, xs);
        ly0x = lerp(vec0.y, vec1.y, xs);
        lz0x = lerp(vec0.z, vec1.z, xs);

        vec0 = CELL_3D[hash256(x0, y1, z1, seed)];
        vec1 = CELL_3D[hash256(x1, y1, z1, seed)];

        lx1x = lerp(vec0.x, vec1.x, xs);
        ly1x = lerp(vec0.y, vec1.y, xs);
        lz1x = lerp(vec0.z, vec1.z, xs);

        v3[0] += lerp(lx0y, lerp(lx0x, lx1x, ys), zs) * perturbAmp;
        v3[1] += lerp(ly0y, lerp(ly0x, ly1x, ys), zs) * perturbAmp;
        v3[2] += lerp(lz0y, lerp(lz0x, lz1x, ys), zs) * perturbAmp;

        return v3;
    }

    // ----------------------------------------------------------------------------
    // bilinear
    // z=f(x,y)=(1−x)(1−y)v00+x*(1−y)*v10+(1−x)*y*v01+x*y*v11

    public static double interpolation4(int interpolation, double _v00,double _v10,double _v01,double _v11,double _x,double _y)
    {
        switch (interpolation) {
            default:
            case LINEAR:
                return ((1.-_x)*(1.-_y)*_v00)
                        +(_x*(1.-_y)*_v10)
                        +((1.-_x)*_y*_v01)
                        +(_x*_y*_v11);
            case HERMITE:
                return (hermiteInterpolator(1.-_x)*hermiteInterpolator(1.-_y)*_v00)
                        +(hermiteInterpolator(_x)*hermiteInterpolator(1.-_y)*_v10)
                        +(hermiteInterpolator(1.-_x)*hermiteInterpolator(_y)*_v01)
                        +(hermiteInterpolator(_x)*hermiteInterpolator(_y)*_v11);
            case RADIAN:
                return (radianInterpolator(1.-_x)*radianInterpolator(1.-_y)*_v00)
                        +(radianInterpolator(_x)*radianInterpolator(1.-_y)*_v10)
                        +(radianInterpolator(1.-_x)*radianInterpolator(_y)*_v01)
                        +(radianInterpolator(_x)*radianInterpolator(_y)*_v11);
            case QUINTIC:
                return (quinticInterpolator(1.-_x)*quinticInterpolator(1.-_y)*_v00)
                        +(quinticInterpolator(_x)*quinticInterpolator(1.-_y)*_v10)
                        +(quinticInterpolator(1.-_x)*quinticInterpolator(_y)*_v01)
                        +(quinticInterpolator(_x)*quinticInterpolator(_y)*_v11);
        }
    }
    // ----------------------------------------------------------------------------

    public static int makeSeedInt(long s) {
        return (int) ((s & 0xffffffffL) ^ ((s>>>32) & 0xffffffffL));
    }
    // ----------------------------------------------------------------------------

    /** from "com/github/tommyettinger/NoiseGen.java"
     * A generalization on bias and gain functions that can represent both; this version is branch-less.
     * This is based on <a href="https://arxiv.org/abs/2010.09714">this micro-paper</a> by Jon Barron, which
     * generalizes the earlier bias and gain rational functions by Schlick. The second and final page of the
     * paper has useful graphs of what the s (shape) and t (turning point) parameters do; shape should be 0
     * or greater, while turning must be between 0 and 1, inclusive. This effectively combines two different
     * curving functions so they continue into each other when x equals turning. The shape parameter will
     * cause this to imitate "smoothstep-like" splines when greater than 1 (where the values ease into their
     * starting and ending levels), or to be the inverse when less than 1 (where values start like square
     * root does, taking off very quickly, but also end like square does, landing abruptly at the ending
     * level). You should only give x values between 0 and 1, inclusive.
     * @param x progress through the spline, from 0 to 1, inclusive
     * @param shape must be greater than or equal to 0; values greater than 1 are "normal interpolations"
     * @param turning a value between 0.0 and 1.0, inclusive, where the shape changes
     * @return a double between 0 and 1, inclusive
     */
    public static double barronSpline(final double x, final double shape, final double turning) {
        final double d = turning - x;
        final int f = Float.floatToRawIntBits((float)d) >> 31, n = f | 1;
        return ((turning * n - f) * (x + f)) / (Float.MIN_NORMAL - f + (x + shape * d) * n) - f;
    }

    // ----------------------------------------------------------------------------

    /**
     * A line-wobbling function that uses {@link MathTools#barronSpline(float, float, float)} to
     * interpolate between peaks/valleys, with the shape and turning point determined like the other values.
     * This can be useful when you want a curve to seem more "natural," without the similarity between every peak or
     * every valley in {@link #wobble(long, float)}. This can produce both fairly sharp turns and very gradual curves.
     * @param seed a seed that will determine the pattern of peaks and valleys this will generate as value changes; this should not change between calls
     * @param value a that typically changes slowly, by less than 2.0, with direction changes at integer inputs
     * @return a pseudo-random float between -1f and 1f (both exclusive), smoothly changing with value
     */
    public static double splobble(long seed, double value)
    {
        final long floor = ((long)(value + 0x1p14) - 0x4000);
        final long z = seed + floor * 0x6C8E9CF570932BD5L;
        final long startBits = ((z ^ 0x9E3779B97F4A7C15L) * 0xC6BC279692B5C323L ^ 0x9E3779B97F4A7C15L),
                endBits = ((z + 0x6C8E9CF570932BD5L ^ 0x9E3779B97F4A7C15L) * 0xC6BC279692B5C323L ^ 0x9E3779B97F4A7C15L),
                mixBits = startBits + endBits;
        final double start = startBits * 0x0.ffffffp-63,
                end = endBits  * 0x0.ffffffp-63;
        value -= floor;
        value = barronSpline(value, (mixBits & 0xFFFFFFFFL) * 0x1p-30 + 0.5, (mixBits & 0xFFFFL) * 0x1.8p-17 + 0.125);
        value *= value * (3. - 2. * value);
        return (1. - value) * start + value * end;
    }
    // ----------------------------------------------------------------------------

    public static double splobbleQuintic(long seed, double value)
    {
        final long floor = ((long)(value + 0x1p14) - 0x4000);
        final long z = seed + floor * 0x6C8E9CF570932BD5L;
        final long startBits = ((z ^ 0x9E3779B97F4A7C15L) * 0xC6BC279692B5C323L ^ 0x9E3779B97F4A7C15L),
                endBits = ((z + 0x6C8E9CF570932BD5L ^ 0x9E3779B97F4A7C15L) * 0xC6BC279692B5C323L ^ 0x9E3779B97F4A7C15L),
                mixBits = startBits + endBits;
        final double start = startBits * 0x0.ffffffp-63,
                end = endBits  * 0x0.ffffffp-63;
        value -= floor;
        value = barronSpline(value, (mixBits & 0xFFFFFFFFL) * 0x1p-30 + 0.5, (mixBits & 0xFFFFL) * 0x1.8p-17 + 0.125);
        value *= value * value * (value * (value * 6f - 15f) + 9.999998f);
        return (1. - value) * start + value * end;
    }

    public static double splobble(double value)
    {
        return splobble(0x1ee7cafeL, value);
    }

    public static double splobbleQuintic(double value)
    {
        return splobbleQuintic(0x1ee7cafeL, value);
    }

    // ----------------------------------------------------------------------------

    static class Permutation {

        public static Permutation create(long state)
        {
            Permutation _p = new Permutation();
            _p.stateA = state;
            return _p;
        }
        /**
         * Can be any long.
         */
        long stateA = 12345678987654321L; //System.nanoTime();
        /**
         * Must be odd.
         */
        long stateB = 0x1337DEADBEEFL;

        /**
         * It's a weird RNG. Returns a slightly-biased pseudo-random int between 0 inclusive and bound exclusive. The bias comes from
         * not completely implementing Daniel Lemire's fastrange algorithm, but it should only be relevant for huge bounds. The number
         * generator itself passes PractRand without anomalies, has a state size of 127 bits, and a period of 2 to the 127.
         * @param bound upper exclusive bound
         * @return an int between 0 (inclusive) and bound (exclusive)
         */
        public int nextIntBounded (int bound) {
            final long s = (stateA += 0xC6BC279692B5C323L);
            final long z = ((s < 0x800000006F17146DL) ? stateB : (stateB += 0x9479D2858AF899E6L)) * (s ^ s >>> 31);
            return (int)(bound * ((z ^ z >>> 25) & 0xFFFFFFFFL) >>> 32);
        }

        private static void swap(int[] arr, int pos1, int pos2) {
            final int tmp = arr[pos1];
            arr[pos1] = arr[pos2];
            arr[pos2] = tmp;
        }

        /**
         * Fisher-Yates and/or Knuth shuffle, done in-place on an int array.
         * @param elements will be modified in-place by a relatively fair shuffle
         */
        public void shuffleInPlace(int[] elements) {
            final int size = elements.length;
            for (int i = size; i > 1; i--) {
                swap(elements, i - 1, nextIntBounded(i));
            }
        }

        public int [] makePermutation(int _size, int _mask, int _shuffle)
        {
            int[] _vars = new int[_size];
            for(int _i=0; _i< _vars.length; _i++)
            {
                _vars[_i]=_i & _mask;
            }

            for(int _i=0; _i< _shuffle; _i++)
            {
                shuffleInPlace(_vars);
            }
            return _vars;
        }
    }

    public static final int[] PERM = Permutation.create(0xdeadbeef).makePermutation(1024, 0xff, 1024);

    // ----------------------------------------------------------------------------
    /**
     * Simple linear interpolation. May result in artificial-looking noise.
     */
    public static final int LINEAR = 0;
    /**
     * Cubic interpolation via Hermite spline, more commonly known as "smoothstep".
     * Can be very natural-looking, but can also have problems in higher dimensions
     * (including 3D when used with normals) with seams appearing.
     */
    public static final int HERMITE = 1;
    /**
     * Quintic interpolation, sometimes known as "smootherstep".
     * This has somewhat steeper transitions than {@link #HERMITE}, but doesn't
     * have any issues with seams.
     */
    public static final int QUINTIC = 2;
    public static final int COSINE = 3;
    public static final int RADIAN = 4;


    // ----------------------------------------------------------------------------

    /**
     * Measures distances "as the crow flies."
     * All points at an equal distance from the origin form a circle.
     * Used only with {@link #CELLULAR} noise.
     * Meant to be used with {@link #setCellularDistanceFunction(int)}.
     */
    public static final int EUCLIDEAN = 0;
    /**
     * Measures distances on a grid, as if allowing only orthogonal movement (with no diagonals).
     * All points at an equal distance from the origin form a diamond shape.
     * Used only with {@link #CELLULAR} noise.
     * Meant to be used with {@link #setCellularDistanceFunction(int)}.
     */
    public static final int MANHATTAN = 1;
    /**
     * Measures distances with an approximation of Euclidean distance that's not 100% accurate.
     * All points at an equal distance from the origin form a rough octagon.
     * Used only with {@link #CELLULAR} noise.
     * Meant to be used with {@link #setCellularDistanceFunction(int)}.
     */
    public static final int NATURAL = 2;

    /**
     * Meant to be used with {@link #setCellularReturnType(int)}.
     */
    public static final int CELL_VALUE = 0;
    /**
     * Meant to be used with {@link #setCellularReturnType(int)}. Note that this does not allow configuring an extra
     * Noise value to use for lookup (anymore); it always uses 3 octaves of {@link #SIMPLEX_FRACTAL} with {@link #FBM}.
     */
    public static final int NOISE_LOOKUP = 1;
    /**
     * Meant to be used with {@link #setCellularReturnType(int)}.
     */
    public static final int DISTANCE = 2;
    /**
     * Meant to be used with {@link #setCellularReturnType(int)}.
     */
    public static final int DISTANCE_2 = 3;
    /**
     * Meant to be used with {@link #setCellularReturnType(int)}.
     */
    public static final int DISTANCE_2_ADD = 4;
    /**
     * Meant to be used with {@link #setCellularReturnType(int)}.
     */
    public static final int DISTANCE_2_SUB = 5;
    /**
     * Meant to be used with {@link #setCellularReturnType(int)}.
     */
    public static final int DISTANCE_2_MUL = 6;
    /**
     * Meant to be used with {@link #setCellularReturnType(int)}.
     */
    public static final int DISTANCE_2_DIV = 7;


    public static int fastFloor(double f) {
        return (f >= 0 ? ((int) f) : ((int) (f - 1.)));
    }

    protected static int fastRound(double f) {
        return (f >= 0) ? (int) (f + 0.5) : (int) (f - 0.5);
    }

    /**
     * Calculates the linear interpolation between a and b with the given
     * percent
     *
     * @param a
     * @param b
     * @param percent
     * @return
     */
    public static int lerp(int a, int b, double percent) {
        return (int) ((1 - percent) * a + percent * b);
    }

    public static double lerp(double a, double b, double t) {
        return a + t * (b - a);
    }

    public static int cosineLerp(int _a, int _b, double _t)
    {
        return (int) cosineLerp((double)_a, (double)_b, _t);
    }

    public static double cosineLerp(double _a, double _b, double _t)
    {
        double _ft = _t*Math.PI;
        double _f = (1.- Math.cos(_ft))*.5;
        return (_a*(1.-_f)) + (_b * _f);
    }

    public static double cosineInterpolator(double t) {
        return cosineLerp(0., 1., t);
    }

    public static double quadLerp(double a, double b, double t) { return a + (t * t * (3. - 2. * t)) * (b - a); }

    public static double hermiteInterpolator(double t) {
        return quadLerp(0., 1., t);
    }

    public static double quinticLerp(double a, double b, double t) {
        return a + (t * t * t * (t * (t * 6. - 15.) + 10.)) * (b - a);
    }

    public static double quintic2Lerp(double a, double b, double t) {
        return a + (t * t * t * (t * (t * 6. - 15.) + 9.999998)) * (b - a);
    }

    public static double quinticInterpolator(double t) {
        return quinticLerp(0., 1., t);
    }

    public static double quintic2Interpolator(double t) {
        return quintic2Lerp(0., 1., t);
    }

    public static double radianInterpolator(double t) {
        return radianLerp(0., 1., t);
    }

    protected static double cubicLerp(double a, double b, double c, double d, double t) {
        double p = (d - c) - (a - b);
        return t * (t * t * p + t * ((a - b) - p) + (c - a)) + b;
    }

    public static double radianLerp(double a, double b, double t)
    {
        t = (clamp(t,-1.,1.)-.5)* Math.PI;
        t = (Math.sin(t)/2.)+.5;
        return lerp(a,b,t);
    }

    public static int radianLerp(int a, int b, double t)
    {
        t = (clamp(t,-1.,1.)-.5)* Math.PI;
        t = (Math.sin(t)/2.)+.5;
        return lerp(a,b,t);
    }

    public static double lerp3D(double _v000, double _v001, double _v010, double _v011, double _v100, double _v101, double _v110, double _v111, double _xs, double _ys, double _zs)
    {
        double _v00y = lerp(_v000, _v001, _ys);
        double _v01y = lerp(_v010, _v011, _ys);
        double _v10y = lerp(_v100, _v101, _ys);
        double _v11y = lerp(_v110, _v111, _ys);

        double _v0xy = lerp(_v00y, _v01y, _xs);
        double _v1xy = lerp(_v10y, _v11y, _xs);

        return lerp(_v0xy, _v1xy, _zs);
    }

    /**
     * A fairly-close approximation of {@link Math#sin(double)} that can be significantly faster (between 8x and 80x
     * faster sin() calls in benchmarking, and both takes and returns doubles; if you have access to libGDX you should
     * consider its more-precise and sometimes-faster MathUtils.sin() method. Because this method doesn't rely on a
     * lookup table, where libGDX's MathUtils does, applications that have a bottleneck on memory may perform better
     * with this method than with MathUtils. Takes the same arguments Math.sin() does, so one angle in radians,
     * which may technically be any double (but this will lose precision on fairly large doubles, such as those that are
     * larger than {@link Integer#MAX_VALUE}, because those doubles themselves will lose precision at that scale). The
     * difference between the result of this method and {@link Math#sin(double)} should be under 0.0011 at
     * all points between -pi and pi, with an average difference of about 0.0005; not all points have been checked for
     * potentially higher errors, though.
     * <br>
     * Unlike in previous versions of this method, the sign of the input doesn't affect performance here, at least not
     * by a measurable amount.
     * <br>
     * The technique for sine approximation is mostly from
     * <a href="https://web.archive.org/web/20080228213915/http://devmaster.net/forums/showthread.php?t=5784">this archived DevMaster thread</a>,
     * with credit to "Nick". Changes have been made to accelerate wrapping from any double to the valid input range.
     * @param radians an angle in radians as a double, often from 0 to pi * 2, though not required to be.
     * @return the sine of the given angle, as a double between -1f and 1f (both inclusive)
     */
    public static final double sin(double radians)
    {
        radians *= 0.6366197723675814f;
        final int floor = (radians >= 0.0 ? (int) radians : (int) radians - 1) & -2;
        radians -= floor;
        radians *= 2f - radians;
        return radians * (-0.775f - 0.225f * radians) * ((floor & 2) - 1);
    }

    /**
     * A fairly-close approximation of {@link Math#cos(double)} that can be significantly faster (between 8x and 80x
     * faster cos() calls in benchmarking, and both takes and returns doubles; if you have access to libGDX you should
     * consider its more-precise and sometimes-faster MathUtils.cos() method. Because this method doesn't rely on a
     * lookup table, where libGDX's MathUtils does, applications that have a bottleneck on memory may perform better
     * with this method than with MathUtils. Takes the same arguments Math.cos() does, so one angle in radians,
     * which may technically be any double (but this will lose precision on fairly large doubles, such as those that are
     * larger than {@link Integer#MAX_VALUE}, because those doubles themselves will lose precision at that scale). The
     * difference between the result of this method and {@link Math#cos(double)} should be under 0.0011 at
     * all points between -pi and pi, with an average difference of about 0.0005; not all points have been checked for
     * potentially higher errors, though.
     * <br>
     * Unlike in previous versions of this method, the sign of the input doesn't affect performance here, at least not
     * by a measurable amount.
     * <br>
     * The technique for cosine approximation is mostly from
     * <a href="https://web.archive.org/web/20080228213915/http://devmaster.net/forums/showthread.php?t=5784">this archived DevMaster thread</a>,
     * with credit to "Nick". Changes have been made to accelerate wrapping from any double to the valid input range.
     * @param radians an angle in radians as a double, often from 0 to pi * 2, though not required to be.
     * @return the cosine of the given angle, as a double between -1f and 1f (both inclusive)
     */
    public static final double cos(double radians)
    {
        radians = radians * 0.6366197723675814f + 1f;
        final int floor = (radians >= 0.0 ? (int) radians : (int) radians - 1) & -2;
        radians -= floor;
        radians *= 2f - radians;
        return radians * (-0.775f - 0.225f * radians) * ((floor & 2) - 1);
    }
    /**
     * A variation on {@link Math#sin(double)} that takes its input as a fraction of a turn instead of in radians (it
     * also takes and returns a double); one turn is equal to 360 degrees or two*PI radians. This can be useful as a
     * building block for other measurements; to make a sine method that takes its input in grad (with 400 grad equal to
     * 360 degrees), you would just divide the grad value by 400.0 (or multiply it by 0.0025) and pass it to this
     * method. Similarly for binary degrees, also called brad (with 256 brad equal to 360 degrees), you would divide by
     * 256.0 or multiply by 0.00390625 before passing that value here. The brad case is especially useful because you
     * can use a byte for any brad values, and adding up those brad values will wrap correctly (256 brad goes back to 0)
     * while keeping perfect precision for the results (you still divide by 256.0 when you pass the brad value to this
     * method).
     * <br>
     * The technique for sine approximation is mostly from
     * <a href="https://web.archive.org/web/20080228213915/http://devmaster.net/forums/showthread.php?t=5784">this archived DevMaster thread</a>,
     * with credit to "Nick". Changes have been made to accelerate wrapping from any double to the valid input range.
     * @param turns an angle as a fraction of a turn as a double, with 0.5 here equivalent to PI radians in {@link #sin(double)}
     * @return the sine of the given angle, as a double between -1.0 and 1.0 (both inclusive)
     */
    public static final double sinTurns(double turns)
    {
        turns *= 4f;
        final long floor = (turns >= 0.0 ? (long) turns : (long) turns - 1L) & -2L;
        turns -= floor;
        turns *= 2f - turns;
        return turns * (-0.775f - 0.225f * turns) * ((floor & 2L) - 1L);
    }

    /**
     * A variation on {@link Math#cos(double)} that takes its input as a fraction of a turn instead of in radians (it
     * also takes and returns a double); one turn is equal to 360 degrees or two*PI radians. This can be useful as a
     * building block for other measurements; to make a cosine method that takes its input in grad (with 400 grad equal
     * to 360 degrees), you would just divide the grad value by 400.0 (or multiply it by 0.0025) and pass it to this
     * method. Similarly for binary degrees, also called brad (with 256 brad equal to 360 degrees), you would divide by
     * 256.0 or multiply by 0.00390625 before passing that value here. The brad case is especially useful because you
     * can use a byte for any brad values, and adding up those brad values will wrap correctly (256 brad goes back to 0)
     * while keeping perfect precision for the results (you still divide by 256.0 when you pass the brad value to this
     * method).
     * <br>
     * The technique for cosine approximation is mostly from
     * <a href="https://web.archive.org/web/20080228213915/http://devmaster.net/forums/showthread.php?t=5784">this archived DevMaster thread</a>,
     * with credit to "Nick". Changes have been made to accelerate wrapping from any double to the valid input range.
     * @param turns an angle as a fraction of a turn as a double, with 0.5 here equivalent to PI radians in {@link #cos(double)}
     * @return the cosine of the given angle, as a double between -1.0 and 1.0 (both inclusive)
     */
    public static final double cosTurns(double turns)
    {
        turns = turns * 4f + 1f;
        final long floor = (turns >= 0.0 ? (long) turns : (long) turns - 1L) & -2L;
        turns -= floor;
        turns *= 2f - turns;
        return turns * (-0.775f - 0.225f * turns) * ((floor & 2L) - 1L);
    }

    public static final double swayRandomized(int seed, double value) {
        final int floor = value >= 0f ? (int) value : (int) value - 1;
        final double start = ((((seed += floor) ^ 0xD1B54A35) * 0x1D2473 & 0x1FFFFF) - 0x100000) * 0x1p-20f,
                end = (((seed + 1 ^ 0xD1B54A35) * 0x1D2473 & 0x1FFFFF) - 0x100000) * 0x1p-20f;
        value -= floor;
        value *= value * (3f - 2f * value);
        return (1f - value) * start + value * end;
    }


    // ----------------------------------------------------------------------------

    /**
     * A 32-bit point hash that smashes x and y into s using XOR and multiplications by harmonious numbers,
     * then runs a simple unary hash on s and returns it. Has better performance than HastyPointHash, especially for
     * ints, and has slightly fewer collisions in a hash table of points. GWT-optimized. Inspired by Pelle Evensen's
     * rrxmrrxmsx_0 unary hash, though this doesn't use its code or its full algorithm. The unary hash used here has
     * been stripped down heavily, both for speed and because unless points are selected specifically to target
     * flaws in the hash, it doesn't need the intense resistance to bad inputs that rrxmrrxmsx_0 has.
     * @param x x position, as an int
     * @param y y position, as an int
     * @param s any int, a seed to be able to produce many hashes for a given point
     * @return 32-bit hash of the x,y point with the given state s
     */
    public static final int hashAll(int x, int y, int s) {
        s ^= x * 0x1827F5 ^ y * 0x123C21;
        return (s = (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493) ^ s >>> 11;
    }
    /**
     * A 32-bit point hash that smashes x, y, and z into s using XOR and multiplications by harmonious numbers,
     * then runs a simple unary hash on s and returns it. Has better performance than HastyPointHash, especially for
     * ints, and has slightly fewer collisions in a hash table of points. GWT-optimized. Inspired by Pelle Evensen's
     * rrxmrrxmsx_0 unary hash, though this doesn't use its code or its full algorithm. The unary hash used here has
     * been stripped down heavily, both for speed and because unless points are selected specifically to target
     flaws in the hash, it doesn't need the intense resistance to bad inputs that rrxmrrxmsx_0 has.
     * @param x x position, as an int
     * @param y y position, as an int
     * @param z z position, as an int
     * @param s any int, a seed to be able to produce many hashes for a given point
     * @return 32-bit hash of the x,y,z point with the given state s
     */
    public static final int hashAll(int x, int y, int z, int s) {
        s ^= x * 0x1A36A9 ^ y * 0x157931 ^ z * 0x119725;
        return (s = (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493) ^ s >>> 11;
    }

    /**
     * A 32-bit point hash that smashes x, y, z, and w into s using XOR and multiplications by harmonious numbers,
     * then runs a simple unary hash on s and returns it. Has better performance than HastyPointHash, especially for
     * ints, and has slightly fewer collisions in a hash table of points. GWT-optimized. Inspired by Pelle Evensen's
     * rrxmrrxmsx_0 unary hash, though this doesn't use its code or its full algorithm. The unary hash used here has
     * been stripped down heavily, both for speed and because unless points are selected specifically to target
     * flaws in the hash, it doesn't need the intense resistance to bad inputs that rrxmrrxmsx_0 has.
     * @param x x position, as an int
     * @param y y position, as an int
     * @param z z position, as an int
     * @param w w position, as an int
     * @param s any int, a seed to be able to produce many hashes for a given point
     * @return 32-bit hash of the x,y,z,w point with the given state s
     */
    public static final int hashAll(int x, int y, int z, int w, int s) {
        s ^= x * 0x1B69E1 ^ y * 0x177C0B ^ z * 0x141E5D ^ w * 0x113C31;
        return (s = (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493) ^ s >>> 11;
    }
    /**
     * A 32-bit point hash that smashes x, y, z, w, and u into s using XOR and multiplications by harmonious
     * numbers, then runs a simple unary hash on s and returns it. Has better performance than HastyPointHash,
     * especially for ints, and has slightly fewer collisions in a hash table of points. GWT-optimized. Inspired by
     * Pelle Evensen's rrxmrrxmsx_0 unary hash, though this doesn't use its code or its full algorithm. The unary
     * hash used here has been stripped down heavily, both for speed and because unless points are selected
     * specifically to target flaws in the hash, it doesn't need the intense resistance to bad inputs that
     * rrxmrrxmsx_0 has.
     * @param x x position, as an int
     * @param y y position, as an int
     * @param z z position, as an int
     * @param w w position, as an int
     * @param u u position, as an int
     * @param s any int, a seed to be able to produce many hashes for a given point
     * @return 32-bit hash of the x,y,z,w,u point with the given state s
     */
    public static final int hashAll(int x, int y, int z, int w, int u, int s) {
        s ^= x * 0x1C3360 ^ y * 0x18DA3A ^ z * 0x15E6DA ^ w * 0x134D28 ^ u * 0x110280;
        return (s = (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493) ^ s >>> 11;
    }

    /**
     * A 32-bit point hash that smashes x, y, z, w, u, and v into s using XOR and multiplications by harmonious
     * numbers, then runs a simple unary hash on s and returns it. Has better performance than HastyPointHash,
     * especially for ints, and has slightly fewer collisions in a hash table of points. GWT-optimized. Inspired by
     * Pelle Evensen's rrxmrrxmsx_0 unary hash, though this doesn't use its code or its full algorithm. The unary
     * hash used here has been stripped down heavily, both for speed and because unless points are selected
     * specifically to target flaws in the hash, it doesn't need the intense resistance to bad inputs that
     * rrxmrrxmsx_0 has.
     * @param x x position, as an int
     * @param y y position, as an int
     * @param z z position, as an int
     * @param w w position, as an int
     * @param u u position, as an int
     * @param v v position, as an int
     * @param s any int, a seed to be able to produce many hashes for a given point
     * @return 32-bit hash of the x,y,z,w,u,v point with the given state s
     */
    public static final int hashAll(int x, int y, int z, int w, int u, int v, int s) {
        s ^= x * 0x1CC1C5 ^ y * 0x19D7AF ^ z * 0x173935 ^ w * 0x14DEAF ^ u * 0x12C139 ^ v * 0x10DAA3;
        return (s = (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493) ^ s >>> 11;
    }

    /**
     * A 32-bit point hash that needs 2 dimensions pre-multiplied by constants {@link #X_2} and {@link #Y_2}, as
     * well as an int seed.
     * @param x x position, as an int pre-multiplied by {@link #X_2}
     * @param y y position, as an int pre-multiplied by {@link #Y_2}
     * @param s any int, a seed to be able to produce many hashes for a given point
     * @return 8-bit hash of the x,y point with the given state s, shifted for {@link GradientVectors#GRADIENTS_2D}
     */
    public static int hash2All(int x, int y, int s) {
        final int h = (s ^ x ^ y) * 0x125493;
        return (h ^ (h << 11 | h >>> 21) ^ (h << 23 | h >>> 9));
    }
    /**
     * A 32-bit point hash that needs 3 dimensions pre-multiplied by constants {@link #X_3} through {@link #Z_3}, as
     * well as an int seed.
     * @param x x position, as an int pre-multiplied by {@link #X_3}
     * @param y y position, as an int pre-multiplied by {@link #Y_3}
     * @param z z position, as an int pre-multiplied by {@link #Z_3}
     * @param s any int, a seed to be able to produce many hashes for a given point
     * @return 8-bit hash of the x,y,z point with the given state s, shifted for {@link GradientVectors#GRADIENTS_3D}
     */
    public static int hash2All(int x, int y, int z, int s) {
        final int h = (s ^ x ^ y ^ z) * 0x125493;
        return (h ^ (h << 11 | h >>> 21) ^ (h << 23 | h >>> 9));
    }

    /**
     * A 32-bit point hash that needs 4 dimensions pre-multiplied by constants {@link #X_4} through {@link #W_4}, as
     * well as an int seed.
     * @param x x position, as an int pre-multiplied by {@link #X_4}
     * @param y y position, as an int pre-multiplied by {@link #Y_4}
     * @param z z position, as an int pre-multiplied by {@link #Z_4}
     * @param w w position, as an int pre-multiplied by {@link #W_4}
     * @param s any int, a seed to be able to produce many hashes for a given point
     * @return 8-bit hash of the x,y,z,w point with the given state s, shifted for {@link GradientVectors#GRADIENTS_4D}
     */
    public static int hash2All(int x, int y, int z, int w, int s) {
        final int h = (s ^ x ^ y ^ z ^ w) * 0x125493;
        return (h ^ (h << 11 | h >>> 21) ^ (h << 23 | h >>> 9));
    }
    /**
     * A 32-bit point hash that needs 5 dimensions pre-multiplied by constants {@link #X_5} through {@link #U_5}, as
     * well as an int seed.
     * @param x x position, as an int pre-multiplied by {@link #X_5}
     * @param y y position, as an int pre-multiplied by {@link #Y_5}
     * @param z z position, as an int pre-multiplied by {@link #Z_5}
     * @param w w position, as an int pre-multiplied by {@link #W_5}
     * @param u u position, as an int pre-multiplied by {@link #U_5}
     * @param s any int, a seed to be able to produce many hashes for a given point
     * @return 8-bit hash of the x,y,z,w,u point with the given state s, shifted for {@link GradientVectors#GRADIENTS_5D}
     */
    public static int hash2All(int x, int y, int z, int w, int u, int s) {
        final int h = (s ^ x ^ y ^ z ^ w ^ u) * 0x125493;
        return (h ^ (h << 11 | h >>> 21) ^ (h << 23 | h >>> 9));
    }

    /**
     * A 32-bit point hash that needs 6 dimensions pre-multiplied by constants {@link #X_6} through {@link #V_6}, as
     * well as an int seed.
     * @param x x position, as an int pre-multiplied by {@link #X_6}
     * @param y y position, as an int pre-multiplied by {@link #Y_6}
     * @param z z position, as an int pre-multiplied by {@link #Z_6}
     * @param w w position, as an int pre-multiplied by {@link #W_6}
     * @param u u position, as an int pre-multiplied by {@link #U_6}
     * @param v v position, as an int pre-multiplied by {@link #V_6}
     * @param s any int, a seed to be able to produce many hashes for a given point
     * @return 8-bit hash of the x,y,z,w,u,v point with the given state s
     */
    public static int hash2All(int x, int y, int z, int w, int u, int v, int s) {
        final int h = (s ^ x ^ y ^ z ^ w ^ u ^ v) * 0x125493;
        return (h ^ (h << 11 | h >>> 21) ^ (h << 23 | h >>> 9));
    }

    protected static double grad2Coord2D(int seed, int x, int y,
                                       double xd, double yd) {
        final int h = hash2All(x, y, seed);
        final int hash = h & (255 << 1);
        return (h * 0x1p-32f) + xd * GRADIENTS_2D[hash] + yd * GRADIENTS_2D[hash + 1];
    }
    protected static double grad2Coord3D(int seed, int x, int y, int z, double xd, double yd, double zd) {
        final int h = hash2All(x, y, z, seed);
        final int hash = h & (255 << 2);
        return (h * 0x1p-32f) + xd * GRADIENTS_3D[hash] + yd * GRADIENTS_3D[hash + 1] + zd * GRADIENTS_3D[hash + 2];
    }
    protected static double grad2Coord4D(int seed, int x, int y, int z, int w,
                                       double xd, double yd, double zd, double wd) {
        final int h = hash2All(x, y, z, w, seed);
        final int hash = h & (255 << 2);
        return (h * 0x1p-32f) + xd * GRADIENTS_4D[hash] + yd * GRADIENTS_4D[hash + 1] + zd * GRADIENTS_4D[hash + 2] + wd * GRADIENTS_4D[hash + 3];
    }
    protected static double grad2Coord5D(int seed, int x, int y, int z, int w, int u,
                                       double xd, double yd, double zd, double wd, double ud) {
        final int h = hash2All(x, y, z, w, u, seed);
        final int hash = h & (255 << 3);
        return (h * 0x1p-32) + xd * GRADIENTS_5D[hash] + yd * GRADIENTS_5D[hash + 1] + zd * GRADIENTS_5D[hash + 2]
                + wd * GRADIENTS_5D[hash + 3] + ud * GRADIENTS_5D[hash + 4];
    }
    protected static double grad2Coord6D(int seed, int x, int y, int z, int w, int u, int v,
                                       double xd, double yd, double zd, double wd, double ud, double vd) {
        final int h = hash2All(x, y, z, w, u, v, seed);
        final int hash = h & (255 << 3);
        return (h * 0x1p-32) + xd * GRADIENTS_6D[hash] + yd * GRADIENTS_6D[hash + 1] + zd * GRADIENTS_6D[hash + 2]
                + wd * GRADIENTS_6D[hash + 3] + ud * GRADIENTS_6D[hash + 4] + vd * GRADIENTS_6D[hash + 5];
    }
    /**
     * A 8-bit point hash that smashes x and y into s using XOR and multiplications by harmonious numbers,
     * then runs a simple unary hash on s and returns it. Has better performance than HastyPointHash, especially for
     * ints, and has slightly fewer collisions in a hash table of points. GWT-optimized. Inspired by Pelle Evensen's
     * rrxmrrxmsx_0 unary hash, though this doesn't use its code or its full algorithm. The unary hash used here has
     * been stripped down heavily, both for speed and because unless points are selected specifically to target
     * flaws in the hash, it doesn't need the intense resistance to bad inputs that rrxmrrxmsx_0 has.
     * @param x x position, as an int
     * @param y y position, as an int
     * @param s any int, a seed to be able to produce many hashes for a given point
     * @return 8-bit hash of the x,y point with the given state s
     */
    public static final int hash256(int x, int y, int s) {
        s ^= x * 0x1827F5 ^ y * 0x123C21;
        return (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493 >>> 24;
    }
    /**
     * A 8-bit point hash that smashes x, y, and z into s using XOR and multiplications by harmonious numbers,
     * then runs a simple unary hash on s and returns it. Has better performance than HastyPointHash, especially for
     * ints, and has slightly fewer collisions in a hash table of points. GWT-optimized. Inspired by Pelle Evensen's
     * rrxmrrxmsx_0 unary hash, though this doesn't use its code or its full algorithm. The unary hash used here has
     * been stripped down heavily, both for speed and because unless points are selected specifically to target
     flaws in the hash, it doesn't need the intense resistance to bad inputs that rrxmrrxmsx_0 has.
     * @param x x position, as an int
     * @param y y position, as an int
     * @param z z position, as an int
     * @param s any int, a seed to be able to produce many hashes for a given point
     * @return 8-bit hash of the x,y,z point with the given state s
     */
    public static final int hash256(int x, int y, int z, int s) {
        s ^= x * 0x1A36A9 ^ y * 0x157931 ^ z * 0x119725;
        return (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493 >>> 24;
    }

    /**
     * A 8-bit point hash that smashes x, y, z, and w into s using XOR and multiplications by harmonious numbers,
     * then runs a simple unary hash on s and returns it. Has better performance than HastyPointHash, especially for
     * ints, and has slightly fewer collisions in a hash table of points. GWT-optimized. Inspired by Pelle Evensen's
     * rrxmrrxmsx_0 unary hash, though this doesn't use its code or its full algorithm. The unary hash used here has
     * been stripped down heavily, both for speed and because unless points are selected specifically to target
     * flaws in the hash, it doesn't need the intense resistance to bad inputs that rrxmrrxmsx_0 has.
     * @param x x position, as an int
     * @param y y position, as an int
     * @param z z position, as an int
     * @param w w position, as an int
     * @param s any int, a seed to be able to produce many hashes for a given point
     * @return 8-bit hash of the x,y,z,w point with the given state s
     */
    public static final int hash256(int x, int y, int z, int w, int s) {
        s ^= x * 0x1B69E1 ^ y * 0x177C0B ^ z * 0x141E5D ^ w * 0x113C31;
        return (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493 >>> 24;
    }

    /**
     * An 8-bit point hash that smashes x, y, z, w, and u into s using XOR and multiplications by harmonious
     * numbers, then runs a simple unary hash on s and returns it. Has better performance than HastyPointHash,
     * especially for ints, and has slightly fewer collisions in a hash table of points. GWT-optimized. Inspired by
     * Pelle Evensen's rrxmrrxmsx_0 unary hash, though this doesn't use its code or its full algorithm. The unary
     * hash used here has been stripped down heavily, both for speed and because unless points are selected
     * specifically to target flaws in the hash, it doesn't need the intense resistance to bad inputs that
     * rrxmrrxmsx_0 has.
     * @param x x position, as an int
     * @param y y position, as an int
     * @param z z position, as an int
     * @param w w position, as an int
     * @param u u position, as an int
     * @param s any int, a seed to be able to produce many hashes for a given point
     * @return 8-bit hash of the x,y,z,w,u point with the given state s
     */
    public static final int hash256(int x, int y, int z, int w, int u, int s) {
        s ^= x * 0x1C3360 ^ y * 0x18DA3A ^ z * 0x15E6DA ^ w * 0x134D28 ^ u * 0x110280;
        return (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493 >>> 24;
    }

    /**
     * A 8-bit point hash that smashes x, y, z, w, u, and v into s using XOR and multiplications by harmonious
     * numbers, then runs a simple unary hash on s and returns it. Has better performance than HastyPointHash,
     * especially for ints, and has slightly fewer collisions in a hash table of points. GWT-optimized. Inspired by
     * Pelle Evensen's rrxmrrxmsx_0 unary hash, though this doesn't use its code or its full algorithm. The unary
     * hash used here has been stripped down heavily, both for speed and because unless points are selected
     * specifically to target flaws in the hash, it doesn't need the intense resistance to bad inputs that
     * rrxmrrxmsx_0 has.
     * @param x x position, as an int
     * @param y y position, as an int
     * @param z z position, as an int
     * @param w w position, as an int
     * @param u u position, as an int
     * @param v v position, as an int
     * @param s any int, a seed to be able to produce many hashes for a given point
     * @return 8-bit hash of the x,y,z,w,u,v point with the given state s
     */
    public static final int hash256(int x, int y, int z, int w, int u, int v, int s) {
        s ^= x * 0x1CC1C5 ^ y * 0x19D7AF ^ z * 0x173935 ^ w * 0x14DEAF ^ u * 0x12C139 ^ v * 0x10DAA3;
        return (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493 >>> 24;
    }

    /**
     * A 5-bit point hash that smashes x and y into s using XOR and multiplications by harmonious numbers,
     * then runs a simple unary hash on s and returns it. Has better performance than HastyPointHash, especially for
     * ints, and has slightly fewer collisions in a hash table of points. GWT-optimized. Inspired by Pelle Evensen's
     * rrxmrrxmsx_0 unary hash, though this doesn't use its code or its full algorithm. The unary hash used here has
     * been stripped down heavily, both for speed and because unless points are selected specifically to target
     * flaws in the hash, it doesn't need the intense resistance to bad inputs that rrxmrrxmsx_0 has.
     * @param x x position, as an int
     * @param y y position, as an int
     * @param s any int, a seed to be able to produce many hashes for a given point
     * @return 5-bit hash of the x,y point with the given state s
     */
    public static final int hash32(int x, int y, int s) {
        s ^= x * 0x1827F5 ^ y * 0x123C21;
        return (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493 >>> 27;
    }
    /**
     * A 5-bit point hash that smashes x, y, and z into s using XOR and multiplications by harmonious numbers,
     * then runs a simple unary hash on s and returns it. Has better performance than HastyPointHash, especially for
     * ints, and has slightly fewer collisions in a hash table of points. GWT-optimized. Inspired by Pelle Evensen's
     * rrxmrrxmsx_0 unary hash, though this doesn't use its code or its full algorithm. The unary hash used here has
     * been stripped down heavily, both for speed and because unless points are selected specifically to target
     flaws in the hash, it doesn't need the intense resistance to bad inputs that rrxmrrxmsx_0 has.
     * @param x x position, as an int
     * @param y y position, as an int
     * @param z z position, as an int
     * @param s any int, a seed to be able to produce many hashes for a given point
     * @return 5-bit hash of the x,y,z point with the given state s
     */
    public static final int hash32(int x, int y, int z, int s) {
        s ^= x * 0x1A36A9 ^ y * 0x157931 ^ z * 0x119725;
        return (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493 >>> 27;
    }

    /**
     * A 5-bit point hash that smashes x, y, z, and w into s using XOR and multiplications by harmonious numbers,
     * then runs a simple unary hash on s and returns it. Has better performance than HastyPointHash, especially for
     * ints, and has slightly fewer collisions in a hash table of points. GWT-optimized. Inspired by Pelle Evensen's
     * rrxmrrxmsx_0 unary hash, though this doesn't use its code or its full algorithm. The unary hash used here has
     * been stripped down heavily, both for speed and because unless points are selected specifically to target
     * flaws in the hash, it doesn't need the intense resistance to bad inputs that rrxmrrxmsx_0 has.
     * @param x x position, as an int
     * @param y y position, as an int
     * @param z z position, as an int
     * @param w w position, as an int
     * @param s any int, a seed to be able to produce many hashes for a given point
     * @return 5-bit hash of the x,y,z,w point with the given state s
     */
    public static final int hash32(int x, int y, int z, int w, int s) {
        s ^= x * 0x1B69E1 ^ y * 0x177C0B ^ z * 0x141E5D ^ w * 0x113C31;
        return (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493 >>> 27;
    }

    /**
     * An 5-bit point hash that smashes x, y, z, w, and u into s using XOR and multiplications by harmonious
     * numbers, then runs a simple unary hash on s and returns it. Has better performance than HastyPointHash,
     * especially for ints, and has slightly fewer collisions in a hash table of points. GWT-optimized. Inspired by
     * Pelle Evensen's rrxmrrxmsx_0 unary hash, though this doesn't use its code or its full algorithm. The unary
     * hash used here has been stripped down heavily, both for speed and because unless points are selected
     * specifically to target flaws in the hash, it doesn't need the intense resistance to bad inputs that
     * rrxmrrxmsx_0 has.
     * @param x x position, as an int
     * @param y y position, as an int
     * @param z z position, as an int
     * @param w w position, as an int
     * @param u u position, as an int
     * @param s any int, a seed to be able to produce many hashes for a given point
     * @return 5-bit hash of the x,y,z,w,u point with the given state s
     */
    public static final int hash32(int x, int y, int z, int w, int u, int s) {
        s ^= x * 0x1C3360 ^ y * 0x18DA3A ^ z * 0x15E6DA ^ w * 0x134D28 ^ u * 0x110280;
        return (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493 >>> 24;
    }

    /**
     * A 5-bit point hash that smashes x, y, z, w, u, and v into s using XOR and multiplications by harmonious
     * numbers, then runs a simple unary hash on s and returns it. Has better performance than HastyPointHash,
     * especially for ints, and has slightly fewer collisions in a hash table of points. GWT-optimized. Inspired by
     * Pelle Evensen's rrxmrrxmsx_0 unary hash, though this doesn't use its code or its full algorithm. The unary
     * hash used here has been stripped down heavily, both for speed and because unless points are selected
     * specifically to target flaws in the hash, it doesn't need the intense resistance to bad inputs that
     * rrxmrrxmsx_0 has.
     * @param x x position, as an int
     * @param y y position, as an int
     * @param z z position, as an int
     * @param w w position, as an int
     * @param u u position, as an int
     * @param v v position, as an int
     * @param s any int, a seed to be able to produce many hashes for a given point
     * @return 5-bit hash of the x,y,z,w,u,v point with the given state s
     */
    public static final int hash32(int x, int y, int z, int w, int u, int v, int s) {
        s ^= x * 0x1CC1C5 ^ y * 0x19D7AF ^ z * 0x173935 ^ w * 0x14DEAF ^ u * 0x12C139 ^ v * 0x10DAA3;
        return (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493 >>> 27;
    }

    /**
     * Gets the bit representation of a float with {@link float#floatToIntBits(double)} and mixes its
     * typically-more-varied high bits with its low bits, returning an int. NOTE: if you target GWT,
     * this method will be unnecessarily slow because of GWT's poor implementation of doubleToIntBits.
     * If you use libGDX and want to use the white noise methods here, you should extend this class
     * and override this method like so:
     * <pre><code>
     * public int doubleToIntMixed(final double f) {
     *     final int i = com.badlogic.gdx.utils.NumberUtils.doubleToIntBits(f);
     *     return i ^ i >>> 16;
     * }
     * </code></pre>
     * @param f can be any double except for NaN, though this will technically work on NaN
     * @return a slightly-mixed version of the bits that make up {@code f}, as an int
     */
    public static final int floatToIntMixed(final float f) {
        final int i = Float.floatToIntBits(f);
        return i ^ i >>> 16;
    }

    public static final long doubleToIntMixed(final double f) {
        final long i = Double.doubleToLongBits(f);
        return i ^ i >>> 32;
    }



    // Value Noise
    //x should be premultiplied by 0xD1B55
    //y should be premultiplied by 0xABC99
    public static final int hashPart1024(final int x, final int y, int s) {
        s += x ^ y;
        return (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493 >> 22;
    }
    //x should be premultiplied by 0xDB4F1
    //y should be premultiplied by 0xBBE05
    //z should be premultiplied by 0xA0F2F
    public static final int hashPart1024(final int x, final int y, final int z, int s) {
        s += x ^ y ^ z;
        return (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493 >> 22;
    }
    //x should be premultiplied by 0xE19B1
    //y should be premultiplied by 0xC6D1D
    //z should be premultiplied by 0xAF36D
    //w should be premultiplied by 0x9A695
    public static final int hashPart1024(final int x, final int y, final int z, final int w, int s) {
        s += x ^ y ^ z ^ w;
        return (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493 >> 22;
    }

    //x should be premultiplied by 0xE60E3
    //y should be premultiplied by 0xCEBD7
    //z should be premultiplied by 0xB9C9B
    //w should be premultiplied by 0xA6F57
    //u should be premultiplied by 0x9609D
    public static final int hashPart1024(final int x, final int y, final int z, final int w, final int u, int s) {
        s += x ^ y ^ z ^ w ^ u;
        return (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493 >> 22;
    }

    //x should be premultiplied by 0xE95E1
    //y should be premultiplied by 0xD4BC7
    //z should be premultiplied by 0xC1EDB
    //w should be premultiplied by 0xB0C8B
    //u should be premultiplied by 0xA127B
    //v should be premultiplied by 0x92E85
    public static final int hashPart1024(final int x, final int y, final int z, final int w, final int u, final int v, int s) {
        s += x ^ y ^ z ^ w ^ u ^ v;
        return (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493 >> 22;
    }

    //x should be premultiplied by 0xEBEDF 0xD96EB 0xC862B 0xB8ACD 0xAA323 0x9CDA5 0x908E3
    //y should be premultiplied by 0xD96EB
    //z should be premultiplied by 0xC862B
    //w should be premultiplied by 0xB8ACD
    //u should be premultiplied by 0xAA323
    //v should be premultiplied by 0x9CDA5
    //m should be premultiplied by 0x908E3
    public static final int hashPart1024(final int x, final int y, final int z, final int w, final int u, final int v, final int m, int s) {
        s += x ^ y ^ z ^ w ^ u ^ v ^ m;
        return (s ^ (s << 19 | s >>> 13) ^ (s << 5 | s >>> 27) ^ 0xD1B54A35) * 0x125493 >> 22;
    }



    public static final double F2f = 0.3660254f;
    public static final double G2f = 0.21132487f;
    public static final double H2f = 0.42264974f;
    public static final double F3f = 0.33333334f;
    public static double G3f = 0.16666667f;

    public static final double F4f = (double) ((2.23606797 - 1.0) / 4.0);
    public static final double G4f = (double) ((5.0 - 2.23606797) / 20.0);


    public static final double F2 = 0.36602540378443864676372317075294,
            G2 = 0.21132486540518711774542560974902,
            H2 = G2 * 2.0,
            F3 = 1.0 / 3.0,
            G3 = 1.0 / 6.0,
            F4 = (Math.sqrt(5.0) - 1.0) * 0.25,
            G4 = (5.0 - Math.sqrt(5.0)) * 0.05,
            F5 = (Math.sqrt(6.0) - 1.0) / 5.0,
            G5 = (6.0 - Math.sqrt(6.0)) / 30.0,
            F6 = (Math.sqrt(7.0) - 1.0) / 6.0,
            G6 = F6 / (1.0 + 6.0 * F6),

    LIMIT2 = 0.5,
            LIMIT3 = 0.6,
            LIMIT4 = 0.62,
            LIMIT5 = 0.7,
            LIMIT6 = 0.775;

//            LIMIT2 = 0.6,
//            LIMIT3 = 0.7,
//            LIMIT4 = 0.72,
//            LIMIT5 = 0.8,
//            LIMIT6 = 0.9375;

    protected static final double HARSH = 2.5;


    /**
     * This gradient vector array was quasi-randomly generated after a lot of rejection sampling. Each gradient should
     * have a magnitude of 2.0, matching the magnitude of the center of an edge of a 5D hypercube.
     * This may bias slightly in some directions. The sums of the x, y, z, w, and u components of all 256 vectors are:
     * <br>
     * x: +0.52959638973427, y: +0.31401370534460, z: -0.14792091580658, w: -0.00781214643439, u: -0.58206620017072
     */
    protected static final double[] GRAD_5D = {
            -1.6797903571f, -0.0690921662f, -0.7098031356f, -0.5887570823f, +0.5683970756f, 0f, 0f, 0f,
            -1.0516780588f, -0.2945340815f, -1.4440603796f, +0.7418854274f, -0.4141480030f, 0f, 0f, 0f,
            +1.0641252713f, -1.5650070200f, +0.4156350353f, +0.1130875224f, +0.4825444684f, 0f, 0f, 0f,
            +0.8695556873f, +1.0264500068f, -0.3870691013f, -1.1785230203f, -0.8071767413f, 0f, 0f, 0f,
            +0.4036843343f, +0.2265883553f, -1.6373381485f, +0.7147763885f, +0.7706589242f, 0f, 0f, 0f,
            +0.1852080234f, -0.7234241829f, +1.3347979534f, -0.7398257504f, -1.0551434605f, 0f, 0f, 0f,
            -0.1798280717f, -0.9172834905f, -0.1660562308f, +1.5451496683f, +0.8433212279f, 0f, 0f, 0f,
            -0.5376087193f, +1.4095478895f, -1.2573362952f, +0.1736254636f, -0.3363201621f, 0f, 0f, 0f,
            -0.8831071523f, +0.4890748406f, +0.7809592873f, -0.9126098448f, +1.2402311964f, 0f, 0f, 0f,
            -1.7880012565f, -0.0832774541f, -0.0688806429f, +0.8681275071f, -0.1942330063f, 0f, 0f, 0f,
            +1.1634898551f, -0.5052769528f, -0.7356836999f, -0.2313504020f, +1.3402361893f, 0f, 0f, 0f,
            +0.5846946797f, -1.2424919047f, +0.6407004403f, +1.3053017243f, -0.0060293368f, 0f, 0f, 0f,
            +0.4938778800f, +0.7783935437f, +0.0680362272f, +0.1949810810f, -1.7628220502f, 0f, 0f, 0f,
            +0.3495453088f, +0.3175464510f, -1.2837807206f, -1.4389420883f, +0.2415265131f, 0f, 0f, 0f,
            -0.0814475545f, -0.3645019914f, +1.2414338549f, +0.7877420883f, -1.3033836658f, 0f, 0f, 0f,
            -0.6130443974f, -1.7598572531f, +0.3278510206f, -0.4244846722f, +0.4892908001f, 0f, 0f, 0f,
            -0.4462734487f, +0.7987181596f, -0.3741235663f, +1.6266729545f, -0.6138859559f, 0f, 0f, 0f,
            -1.1190041124f, +0.4387897882f, +1.5187622470f, +0.2310331368f, +0.4419029812f, 0f, 0f, 0f,
            +1.7898523809f, -0.0730765445f, +0.2593137052f, -0.6196725486f, -0.5829670729f, 0f, 0f, 0f,
            +1.2710361476f, -0.7953333027f, -0.5194550615f, +0.9617110332f, +0.7464518582f, 0f, 0f, 0f,
            +0.3919460233f, -1.2475586928f, -1.4706983192f, -0.1307051020f, -0.3315693791f, 0f, 0f, 0f,
            +0.2652336693f, +0.6189864328f, +0.3777315952f, -1.7165368300f, +0.6762596023f, 0f, 0f, 0f,
            +0.1369902659f, +0.5491538637f, -1.0396634959f, +0.9490333448f, -1.3031113639f, 0f, 0f, 0f,
            -0.2401683431f, -0.3733848671f, -1.4613950663f, -0.7227050436f, +1.0700115833f, 0f, 0f, 0f,
            -0.6698938436f, -1.3422700176f, +0.7466878175f, +1.0575187021f, -0.2714128520f, 0f, 0f, 0f,
            -0.8847555645f, +1.1306623120f, -0.1640964357f, -0.1686079479f, +1.3723899034f, 0f, 0f, 0f,
            -1.1982151304f, +0.3128615080f, -0.8450972304f, -1.3226474382f, -0.0530339816f, 0f, 0f, 0f,
            +0.8151064240f, -0.0707387889f, +0.4722986821f, +0.1916252778f, -1.7523730337f, 0f, 0f, 0f,
            +1.2690966076f, -1.1058707966f, -0.0729186016f, -1.0707270924f, +0.1211195821f, 0f, 0f, 0f,
            +0.2853585791f, -1.5643353649f, -0.5748320773f, +0.5808419374f, -0.8964463588f, 0f, 0f, 0f,
            +0.2535726091f, +1.1620185372f, +1.5502829093f, -0.2230925697f, +0.3636845578f, 0f, 0f, 0f,
            -0.1259274379f, +0.1397280645f, +0.0818804260f, -1.6542088566f, -1.1052180794f, 0f, 0f, 0f,
            -0.7748098968f, -0.7541305772f, -1.3684352844f, +0.6640618209f, +0.7192798250f, 0f, 0f, 0f,
            -0.7154067153f, -1.0897763229f, +1.1541033599f, -0.5995215703f, -0.7805127283f, 0f, 0f, 0f,
            -1.2205329558f, +1.1140489716f, +0.2019395367f, +0.9671922075f, +0.5412521130f, 0f, 0f, 0f,
            +1.7763124224f, +0.3884232272f, -0.5590859360f, -0.0997516807f, -0.6093554733f, 0f, 0f, 0f,
            +0.7941439015f, -0.1125633933f, +1.2801756800f, -1.1687349208f, +0.5931895645f, 0f, 0f, 0f,
            +1.0158348693f, -1.2589195605f, +0.5779670539f, +0.6776054453f, -0.7681184828f, 0f, 0f, 0f,
            +0.2112048908f, +1.7680263830f, -0.3219879142f, -0.4419318676f, +0.7283510216f, 0f, 0f, 0f,
            -0.0026910087f, +0.5409839017f, -1.7270071907f, +0.8213951690f, -0.2237974892f, 0f, 0f, 0f,
            -0.4138014120f, +0.1597450584f, +0.6839984196f, -0.0929507291f, +1.8239397555f, 0f, 0f, 0f,
            -0.7659506384f, -0.5475010929f, -0.3720789651f, -1.7162535971f, -0.1720261813f, 0f, 0f, 0f,
            -0.7070622912f, -0.8458704904f, -1.0146426125f, +0.3071423194f, +1.2886931343f, 0f, 0f, 0f,
            -1.6125362501f, +0.9425610444f, +0.5399791622f, -0.4685942374f, +0.0121435146f, 0f, 0f, 0f,
            +1.0263600815f, +0.3094855666f, -0.1357539876f, +0.9416267863f, -1.3948883530f, 0f, 0f, 0f,
            +1.0884856898f, -0.2412950015f, -1.6426714098f, -0.0397577982f, +0.2388002976f, 0f, 0f, 0f,
            +0.3883496101f, -0.7333843774f, +0.7553963021f, -1.1941140952f, -1.1466472386f, 0f, 0f, 0f,
            +0.1101824785f, +1.9193422531f, -0.0349560249f, +0.4586533562f, +0.3039741964f, 0f, 0f, 0f,
            -0.2151896625f, +0.8619434800f, -1.1688233084f, -0.6467741803f, -1.1942705221f, 0f, 0f, 0f,
            -0.5440612093f, +0.1020041479f, +1.1614695684f, +1.4233071754f, +0.5646040033f, 0f, 0f, 0f,
            -1.3903047596f, -0.7781814736f, +0.1518957001f, +0.0172015182f, -1.1992156077f, 0f, 0f, 0f,
            -1.1352909369f, -1.0508611233f, -0.5994729301f, -0.9722493258f, +0.5496988654f, 0f, 0f, 0f,
            +1.3336722136f, +0.8735367803f, +1.0383655970f, +0.4365890905f, -0.4352456471f, 0f, 0f, 0f,
            +1.3114501486f, +0.4918768452f, +0.3084333813f, -0.6495376384f, +1.2333391190f, 0f, 0f, 0f,
            +0.6896294960f, -0.2419287464f, -0.7141267659f, +1.6588951215f, -0.4516321269f, 0f, 0f, 0f,
            +0.2176968344f, -0.7421851123f, +1.5213707725f, +0.0438834617f, +1.0417651183f, 0f, 0f, 0f,
            -0.0434372972f, +1.6845774504f, +0.3229918793f, -1.0108819828f, -0.1846777672f, 0f, 0f, 0f,
            -0.3651204958f, +0.6939929190f, -0.4562428562f, +0.6199070461f, +1.6711129711f, 0f, 0f, 0f,
            -0.5890165438f, +0.0561767268f, -1.8733437161f, -0.3722429586f, -0.0438427600f, 0f, 0f, 0f,
            -0.7545212813f, -0.3365185970f, +0.3380918399f, +0.9776020270f, -1.4991467755f, 0f, 0f, 0f,
            -1.7417773586f, -0.9568393557f, -0.2040755992f, +0.0614347980f, +0.0724499544f, 0f, 0f, 0f,
            +0.8480496705f, +0.7472072627f, -1.0543920416f, -0.7610320599f, -1.0156676077f, 0f, 0f, 0f,
            +1.1550078136f, +0.5368673805f, +1.0697388270f, +1.0270433372f, +0.4225768470f, 0f, 0f, 0f,
            +0.6091830897f, -0.3632960094f, -0.2588786131f, -0.6327424895f, -1.7405547329f, 0f, 0f, 0f,
            +0.0677925852f, -0.7943979716f, -1.0479221567f, +1.4543495597f, +0.3886676471f, 0f, 0f, 0f,
            -0.2061357682f, +1.6481340611f, +0.7904935004f, +0.1201597286f, -0.7757859417f, 0f, 0f, 0f,
            -0.7481241996f, +0.8815306333f, -0.0389302309f, -1.3935543711f, +0.8483540397f, 0f, 0f, 0f,
            -1.1501637940f, +0.0500560844f, -1.1550196052f, +0.8588373495f, -0.7764958172f, 0f, 0f, 0f,
            -1.4576210450f, -0.4980765043f, +0.9775175852f, -0.3244367280f, +0.7526359448f, 0f, 0f, 0f,
            +1.0804925776f, -1.0462781211f, +0.0745691035f, +1.2771082010f, -0.3182325797f, 0f, 0f, 0f,
            +0.9560363853f, +1.0747532707f, -0.7908249620f, +0.1795273343f, +1.1283907359f, 0f, 0f, 0f,
            +0.5870023920f, +0.3518098165f, +1.5130869695f, -1.0689826362f, -0.3154393619f, 0f, 0f, 0f,
            +0.2461487893f, -0.3086153639f, +0.2921558695f, +0.9112883678f, +1.7112468522f, 0f, 0f, 0f,
            -0.1666414465f, -1.6148302394f, -1.0133051505f, -0.5432021594f, -0.2066349729f, 0f, 0f, 0f,
            -0.2082660083f, +0.8616008908f, +0.9278341202f, +1.0618169303f, +1.1072207669f, 0f, 0f, 0f,
            -1.4200071139f, +1.1449937745f, +0.7148016266f, +0.3951739916f, +0.0739270175f, 0f, 0f, 0f,
            -1.0824868745f, +0.0130967819f, -0.3737068064f, -0.7706672311f, -1.4472269630f, 0f, 0f, 0f,
            +1.3772509463f, -0.3564008886f, -1.3081930141f, +0.4995798772f, +0.1233256728f, 0f, 0f, 0f,
            +0.9497908429f, -1.3263097649f, +0.4502084198f, -0.2307263072f, -1.0406140073f, 0f, 0f, 0f,
            +0.4549745216f, +0.6615623933f, -0.1955222409f, +1.8045985192f, +0.2460256534f, 0f, 0f, 0f,
            +0.3671055129f, +0.3148111115f, -1.6182062419f, +0.2769362348f, -1.0348151463f, 0f, 0f, 0f,
            +0.0481966276f, -0.4532364953f, +1.1128663911f, -1.3414977121f, +0.8684273419f, 0f, 0f, 0f,
            -0.3576449008f, -1.2810416482f, -0.2006980071f, +1.1378443353f, -0.9466007601f, 0f, 0f, 0f,
            -0.5489241973f, +1.4436359278f, -1.0580643935f, -0.2111030853f, +0.6712173717f, 0f, 0f, 0f,
            -0.7396913767f, +0.4241285251f, +0.6373931479f, -1.6490546808f, -0.3838232614f, 0f, 0f, 0f,
            -1.7438367476f, -0.0103026532f, -0.0174746056f, +0.2859053214f, +0.9364187303f, 0f, 0f, 0f,
            +1.4125223773f, -0.6136774864f, -0.9382744610f, -0.7882620843f, -0.3556183326f, 0f, 0f, 0f,
            +0.6333525580f, -1.2469837002f, +0.8203449431f, +0.6945417557f, +0.9426251178f, 0f, 0f, 0f,
            +0.8639745852f, +1.7229496217f, +0.2131097409f, -0.3490329851f, -0.3431511780f, 0f, 0f, 0f,
            +0.1160084005f, +0.1925427348f, -0.5469449523f, -1.4198630543f, +1.2784011391f, 0f, 0f, 0f,
            -0.1960368134f, -0.4241632531f, +1.8889399989f, +0.4605830623f, -0.0377362652f, 0f, 0f, 0f,
            -0.3716846054f, -0.8276497199f, +0.2058886823f, -0.5926340109f, -1.6683049107f, 0f, 0f, 0f,
            -0.7995956039f, +1.4545513458f, -0.5567146701f, +0.9584702276f, +0.1277922200f, 0f, 0f, 0f,
            -0.9905083489f, +0.4012227581f, +1.3537558791f, -0.1090892883f, -1.0066568711f, 0f, 0f, 0f,
            +1.4450754379f, -0.0281787255f, +0.3053200605f, -1.3288357283f, +0.2278995524f, 0f, 0f, 0f,
            +1.2162147152f, -0.7478839823f, -0.4936637037f, +0.4427814597f, -1.2335850364f, 0f, 0f, 0f,
            +0.4288156741f, -1.2286191885f, -1.4078773154f, -0.4695345709f, +0.3225379959f, 0f, 0f, 0f,
            +0.3329858839f, +1.0484961431f, +0.6324502386f, +1.2260808594f, -0.9415458889f, 0f, 0f, 0f,
            -0.0430825232f, +0.6204968828f, -0.7408650600f, -0.2917703779f, +1.7260117393f, 0f, 0f, 0f,
            -0.2831108338f, -0.2973701593f, -1.2778575475f, -1.3826667300f, -0.5354736652f, 0f, 0f, 0f,
            -0.7626701307f, -1.2292796278f, +0.8192695846f, +0.4886037879f, +0.9986338441f, 0f, 0f, 0f,
            -1.1212378397f, +1.4564460164f, -0.1452464147f, -0.6418766528f, -0.4341526800f, 0f, 0f, 0f,
            -1.4371859530f, +0.3591868101f, -0.7832229698f, +0.7741764284f, +0.7698662281f, 0f, 0f, 0f,
            +1.6195535741f, -0.0783305926f, +1.1220763529f, -0.0880739971f, -0.3226424776f, 0f, 0f, 0f,
            +0.6736622539f, -0.5801267229f, -0.0064584923f, -1.2469663463f, +1.2863379696f, 0f, 0f, 0f,
            +0.3808337389f, -1.7282317745f, -0.8266342493f, +0.4213073506f, -0.0857702241f, 0f, 0f, 0f,
            +0.0748521918f, +0.5865055185f, +0.7547226638f, -0.3937892986f, +1.7104771601f, 0f, 0f, 0f,
            -0.3050023119f, +0.3332256435f, +0.2039469964f, +1.9348633092f, +0.1031690730f, 0f, 0f, 0f,
            -0.5486929801f, -0.3926995085f, -0.7835797197f, -0.0323895314f, -1.7116298814f, 0f, 0f, 0f,
            -0.7373648248f, -0.9164391411f, +1.1634541527f, -1.1082134698f, +0.1861981626f, 0f, 0f, 0f,
            -1.2396832556f, +1.1286466143f, +0.2193465590f, +0.4244818926f, -0.9803287488f, 0f, 0f, 0f,
            +1.7118249987f, +0.5111342927f, -0.5816150480f, -0.5527569748f, +0.4056853108f, 0f, 0f, 0f,
            +0.7213413610f, -0.0659398302f, +1.4422534178f, +0.9666694057f, -0.6788032989f, 0f, 0f, 0f,
            +0.9873966195f, -1.2334566504f, +0.7110411579f, +0.0172849954f, +0.9988765230f, 0f, 0f, 0f,
            +0.1849030939f, -1.6262998800f, -0.3182014494f, -0.9668115017f, -0.5338379006f, 0f, 0f, 0f,
            -0.0537861903f, +0.7112275325f, -1.6810226484f, +0.4784138168f, +0.6607159134f, 0f, 0f, 0f,
            -0.7517873085f, +0.3686878741f, +1.1316388506f, -0.9931706665f, -1.0158201777f, 0f, 0f, 0f,
            -0.7479636489f, -0.4087729589f, -0.2801205440f, +1.4488805036f, +1.0467725708f, 0f, 0f, 0f,
            -1.0753364436f, -1.0487010364f, -1.2861467341f, +0.0451559898f, -0.2960830697f, 0f, 0f, 0f,
            -1.6717166425f, +0.6193692618f, +0.3444359164f, -0.5570386011f, +0.6267512114f, 0f, 0f, 0f,
            +1.6653427265f, +0.6514011681f, -0.1843800816f, +0.8463999253f, -0.2278624001f, 0f, 0f, 0f,
            +0.6180555713f, -0.0980890088f, -0.9637326948f, -0.3818490941f, +1.5917903189f, 0f, 0f, 0f,
            +0.3828037090f, -0.7608509481f, +0.9360620892f, +1.5486593545f, -0.0030206309f, 0f, 0f, 0f,
            +0.0416485569f, -1.5762523250f, +0.0019777673f, +0.0585731018f, -1.2289260701f, 0f, 0f, 0f,
            -0.2886712161f, +0.9630135494f, -1.0923275687f, -1.3265794576f, +0.1904763974f, 0f, 0f, 0f,
            -0.5764811629f, +0.1590907789f, +1.1606879290f, +0.6689389883f, -1.3592953154f, 0f, 0f, 0f,
            -1.6356922055f, -0.7138956424f, +0.2340692949f, -0.6808182666f, +0.5445751314f, 0f, 0f, 0f,
            -1.1383732794f, -0.8340752557f, -0.4924316867f, +1.1297774686f, -0.6996703867f, 0f, 0f, 0f,
            +1.2119764801f, +1.0042477319f, +1.1627125168f, +0.1052984231f, +0.3995138920f, 0f, 0f, 0f,
            +1.0848959808f, +0.5299382966f, +0.3338775173f, -1.2410743362f, -0.9436240820f, 0f, 0f, 0f,
            +0.8223389027f, -0.2257269798f, -0.8855454083f, +1.1320984930f, +1.0986211320f, 0f, 0f, 0f,
            +0.1696512818f, -0.6844004252f, +1.7720906716f, -0.3171057932f, -0.5118135090f, 0f, 0f, 0f,
            -0.0617271001f, +1.6228010367f, +0.2362036330f, +1.0239074576f, +0.5084564115f, 0f, 0f, 0f,
            -0.8016909939f, +1.4462165555f, -0.7627188444f, +0.3252216885f, -0.7604209640f, 0f, 0f, 0f,
            -0.6115306073f, +0.1014550431f, -1.4858078470f, -0.7519599396f, +0.9179697607f, 0f, 0f, 0f,
            -1.5359735435f, -0.5360812013f, +0.6803716202f, +0.9022898547f, -0.2763506754f, 0f, 0f, 0f,
            +1.4311848509f, -0.8591027804f, -0.1752995920f, -0.2145555860f, +1.0662496372f, 0f, 0f, 0f,
            +0.7410642280f, +0.7990758023f, -0.9368640780f, +1.3900908545f, -0.0472735412f, 0f, 0f, 0f,
            +0.4550755889f, +0.2813149456f, +0.5064435170f, +0.1454080862f, -1.8536827027f, 0f, 0f, 0f,
            +0.6584368336f, -0.3398656764f, -0.2473926483f, -1.8321141033f, +0.1819534238f, 0f, 0f, 0f,
            +0.0159960331f, -0.7374889492f, -1.0065472324f, +0.7388568967f, -1.3772462858f, 0f, 0f, 0f,
            -0.2299702397f, +1.8176358053f, +0.7442497214f, -0.2206381235f, +0.2018042090f, 0f, 0f, 0f,
            -0.4069426745f, +0.4769186078f, +0.0089269758f, +1.7464025964f, -0.7462871978f, 0f, 0f, 0f,
            -1.4305778226f, +0.1421159811f, -1.2165719887f, +0.3471454458f, +0.5767952644f, 0f, 0f, 0f,
            -1.4621197220f, -0.3747993576f, +0.9054068790f, -0.6585117031f, -0.6843479237f, 0f, 0f, 0f,
            +1.2555507001f, -1.2133185727f, +0.1361145959f, +0.7938459453f, +0.5502107892f, 0f, 0f, 0f,
            +0.9623281537f, +1.3224211051f, -0.8148529505f, -0.2708155140f, -0.7666815323f, 0f, 0f, 0f,
            +0.3174348857f, +0.2633414906f, +1.0144165277f, -1.5786067523f, +0.5557393117f, 0f, 0f, 0f,
            +0.4312067006f, -0.5747179681f, +0.8536422312f, +0.8761256911f, -1.4097725891f, 0f, 0f, 0f,
            -0.1886268643f, -1.0208135472f, -0.6506500504f, -0.9477019512f, +1.2652569429f, 0f, 0f, 0f,
            -0.3048749941f, +1.3023137339f, +1.3472498676f, +0.5983791689f, -0.1946544138f, 0f, 0f, 0f,
            -0.9288706884f, +0.7613446467f, +0.4729501186f, -0.2114483296f, +1.5129974760f, 0f, 0f, 0f,
            -1.1557323498f, +0.0638806278f, -0.3210150212f, -1.5950470819f, -0.1139129657f, 0f, 0f, 0f,
            +1.0864354794f, -0.3052283529f, -1.1052395274f, +0.2022026495f, +1.2099806929f, 0f, 0f, 0f,
            +1.0414087896f, -1.4163018217f, +0.5940404283f, -0.7457758569f, +0.0221635650f, 0f, 0f, 0f,
            +0.5070316235f, +0.9137533277f, -0.2073217572f, +0.8288949911f, -1.4757793099f, 0f, 0f, 0f,
            +0.3763094088f, +0.4850535903f, -1.8754774606f, -0.2080484396f, +0.2498287114f, 0f, 0f, 0f,
            -0.0253081105f, -0.1921838222f, +0.6575303806f, -1.5122491502f, -1.1149803515f, 0f, 0f, 0f,
            -0.6196419069f, -1.6338762858f, -0.2048715266f, +0.7010005938f, +0.6427425729f, 0f, 0f, 0f,
            -0.5308926042f, +1.4556534130f, -0.8522869910f, -0.5344412052f, -0.7662934602f, 0f, 0f, 0f,
            -1.1271692683f, +0.6619484351f, +0.9450688957f, +1.0599681920f, +0.5240476355f, 0f, 0f, 0f,
            -1.8934489402f, +0.0438491543f, +0.0205347023f, -0.0947675875f, -0.6352368005f, 0f, 0f, 0f,
            +0.5103230547f, +1.3058156973f, +0.1990338991f, -0.7882347287f, +1.1719587297f, 0f, 0f, 0f,
            +0.1384792574f, +0.4610276778f, -0.9727270207f, +1.5951805055f, -0.5267620653f, 0f, 0f, 0f,
            -0.2073797520f, -0.2507461010f, +1.5291534160f, -0.0725161583f, +1.2452113349f, 0f, 0f, 0f,
            -0.5725773198f, -1.0055906561f, +0.3247380428f, -1.5826348743f, -0.2252880459f, 0f, 0f, 0f,
            -0.6862103326f, +1.2996571076f, -0.3961010577f, +0.3505477796f, +1.2490904645f, 0f, 0f, 0f,
            -1.0941521107f, +0.4477460716f, +1.5583661596f, -0.4156823874f, -0.0325219850f, 0f, 0f, 0f,
            +1.0615422136f, +0.0168716535f, +0.2909809882f, +0.7952955764f, -1.4682229009f, 0f, 0f, 0f,
            +0.3529574716f, -0.9860437746f, -1.1438219776f, -0.8624789958f, -0.9224640715f, 0f, 0f, 0f,
            +0.3425330274f, +1.5160688884f, +0.9006480000f, +0.7732736314f, +0.4184343698f, 0f, 0f, 0f,
            -0.1182208812f, +0.4689801454f, -0.3711656837f, -0.8412805777f, -1.7089659070f, 0f, 0f, 0f,
            -0.3895150255f, -0.2763904657f, -1.3594381746f, +1.3110052175f, +0.4528570686f, 0f, 0f, 0f,
            -0.8866701020f, -1.1592070785f, +0.9217069399f, +0.0108062128f, -1.0101458419f, 0f, 0f, 0f,
            -0.9839606799f, +1.3163966058f, -0.0810864936f, -1.0154752113f, +0.5110346685f, 0f, 0f, 0f,
            +1.7393575679f, +0.3972242300f, -0.7097572208f, +0.3707578686f, -0.4190840636f, 0f, 0f, 0f,
            +1.2992926783f, -0.0003032116f, +1.0675928831f, -0.5467297666f, +0.9344358954f, 0f, 0f, 0f,
            +0.3309152609f, -1.5010777228f, -0.7884782610f, +0.0452028175f, +1.0067370548f, 0f, 0f, 0f,
            +0.0527154815f, +0.9848513540f, +1.2271602344f, -1.2005994995f, -0.2827145013f, 0f, 0f, 0f,
            -1.1072848983f, -0.5733937749f, -1.2917946615f, -0.8540935843f, -0.2166343341f, 0f, 0f, 0f,
            -0.5785672345f, -0.5892745270f, +0.9002794950f, +0.8827318293f, +1.3146470384f, 0f, 0f, 0f,
            +1.1323242306f, +0.4385085158f, -0.3984529066f, -0.8482583731f, -1.2834504790f, 0f, 0f, 0f,
            +0.6832479100f, -0.0203722774f, +1.8021714033f, +0.5087858832f, +0.1614695700f, 0f, 0f, 0f,
            +0.6295136760f, -0.7957220411f, +0.5735752524f, -0.5094408070f, -1.5433795577f, 0f, 0f, 0f,
            +0.1464145243f, -1.4152600929f, -0.2997028564f, +1.3388224398f, +0.3055066758f, 0f, 0f, 0f,
            -0.1117532528f, +0.8429678828f, -1.5895178521f, +0.1184502189f, -0.8580902647f, 0f, 0f, 0f,
            -0.6186591707f, +0.3491930628f, +0.8652060160f, -1.4602096806f, +0.7839204512f, 0f, 0f, 0f,
            -1.1893740310f, -0.4873888685f, -0.3368700002f, +1.0489488764f, -1.0649255199f, 0f, 0f, 0f,
            -1.1495757072f, -0.9135567011f, -1.1488759605f, -0.3139079113f, +0.6522543198f, 0f, 0f, 0f,
            +1.2507068251f, +0.9082986588f, +0.4849121115f, +1.1269927255f, -0.3247670313f, 0f, 0f, 0f,
            +1.3812528182f, +0.6859061245f, -0.1144675881f, +0.2171605156f, +1.2495646931f, 0f, 0f, 0f,
            +0.8074888914f, -0.0650160121f, -1.3097078722f, -1.2004749134f, -0.4327353465f, 0f, 0f, 0f,
            +0.3228920807f, -0.6888576407f, +1.0170445092f, +0.7876568168f, +1.3290722555f, 0f, 0f, 0f,
            +0.0052441537f, -1.9617941884f, +0.0477654540f, -0.3352049620f, -0.1915519670f, 0f, 0f, 0f,
            -0.2001799378f, +0.5900368361f, -0.5616998042f, +1.3787410489f, +1.1812497512f, 0f, 0f, 0f,
            -0.9015314851f, +0.3110012919f, +1.7320694688f, +0.2992817832f, -0.0297480605f, 0f, 0f, 0f,
            -0.8591940915f, -0.2863601066f, +0.1461357370f, -0.7274398339f, -1.6214990092f, 0f, 0f, 0f,
            +0.9395832683f, +0.9730323926f, +1.0982291200f, -0.1930711401f, -0.9628123284f, 0f, 0f, 0f,
            +0.5731182373f, +0.3581009267f, +0.2258645391f, +1.8565151569f, +0.2136255940f, 0f, 0f, 0f,
            +0.7011674479f, -0.1226870736f, -0.7909781480f, +0.3959247471f, -1.6464839070f, 0f, 0f, 0f,
            +0.0954972405f, -0.5011463729f, +1.8032529962f, -0.6086202714f, +0.3429177553f, 0f, 0f, 0f,
            -0.1412424735f, -1.6893856796f, +0.3886472390f, +0.7238267164f, -0.6716061327f, 0f, 0f, 0f,
            -0.7766531806f, +1.3490341636f, -0.5674058616f, -0.3739667103f, +1.0559906015f, 0f, 0f, 0f,
            -1.5471155376f, -0.4117408550f, +0.6692645122f, +0.3161027907f, +0.9429035051f, 0f, 0f, 0f,
            +1.5334460025f, -1.0006420984f, -0.1888257316f, -0.6902112872f, -0.3677118033f, 0f, 0f, 0f,
            +0.7809057187f, +1.0330833001f, -1.0077018800f, +0.7218704992f, +0.8867722690f, 0f, 0f, 0f,
            +1.0334456008f, +0.8361364463f, +1.3880171764f, -0.3382417163f, -0.4380261325f, 0f, 0f, 0f,
            -0.0634231536f, -1.1102290519f, -1.5755978089f, +0.5124396730f, -0.1351520699f, 0f, 0f, 0f,
            -0.1846156117f, -1.2027985685f, +0.5261837867f, -0.3886987023f, +1.4461108606f, 0f, 0f, 0f,
            -1.4795808324f, -0.2528893855f, +0.7657021415f, -1.0677045314f, +0.1435088265f, 0f, 0f, 0f,
            +0.8358974012f, +1.4130062170f, -0.7246852387f, -0.7614331388f, +0.4469226390f, 0f, 0f, 0f,
            +0.3586931337f, +0.4076326318f, +1.4558997393f, +0.9580949406f, -0.8170586927f, 0f, 0f, 0f,
            +0.2457835444f, -0.3744186486f, +0.9525361175f, -0.3232545651f, +1.6696055091f, 0f, 0f, 0f,
            -0.2213847655f, -0.7780999043f, -0.5024501129f, -1.6139364700f, -0.6987862901f, 0f, 0f, 0f,
            -0.2574375805f, -1.3890644186f, +1.3509472519f, +0.2010518329f, +0.3724857264f, 0f, 0f, 0f,
            -1.2190443421f, +1.0117162629f, +0.6237377737f, -0.8273041068f, -0.6456626053f, 0f, 0f, 0f,
            -1.4202182379f, +0.1260515345f, -0.3099756452f, +1.0152805943f, +0.9166305590f, 0f, 0f, 0f,
            +1.3394545490f, -0.3743458036f, -1.4096086888f, -0.0615786809f, -0.2737483172f, 0f, 0f, 0f,
            +0.7171369574f, -0.9616513483f, +0.4897305876f, -1.1232009395f, +1.0293322446f, 0f, 0f, 0f,
            +0.1779667703f, +0.3504282910f, -1.0568440088f, -0.4869239513f, +1.5784529288f, 0f, 0f, 0f,
            -0.1204364412f, -0.2136700341f, +1.1047461721f, +1.6490450828f, +0.0051371575f, 0f, 0f, 0f,
            -0.3871281276f, -0.7735057325f, -0.0665288715f, -0.0311266269f, -1.8017840428f, 0f, 0f, 0f,
            -1.1000913946f, +0.6549589413f, +0.8947793392f, +0.4521499773f, -1.1643702335f, 0f, 0f, 0f,
            +1.9292603742f, +0.0932676759f, +0.0543343169f, -0.4404212957f, +0.2689468598f, 0f, 0f, 0f,
            +1.0366997829f, -0.4235317472f, -0.7352650119f, +1.1718961062f, -0.9120961013f, 0f, 0f, 0f,
            +0.3604156986f, +1.2092120205f, +0.2110514542f, -1.3105326841f, -0.8036592445f, 0f, 0f, 0f,
            +0.0668638830f, +0.6759071640f, -1.0954065614f, +0.9579351192f, +1.1921088455f, 0f, 0f, 0f,
            -0.2878226515f, -0.1988228335f, +1.7896272052f, -0.5362533838f, -0.6223297975f, 0f, 0f, 0f,
            -0.5998366869f, -0.8396322334f, +0.3455361034f, +1.4029135129f, +0.9206802585f, 0f, 0f, 0f,
            -0.8343248750f, +1.7431140670f, -0.3975040210f, +0.0398856933f, -0.3253537111f, 0f, 0f, 0f,
            +1.7378988722f, +0.1069270956f, +0.5947543807f, +0.7345800753f, -0.2737397409f, 0f, 0f, 0f,
            +0.3669190706f, -1.0350628357f, -1.2227443172f, +1.1386733496f, -0.0483183134f, 0f, 0f, 0f,
            -0.4256376886f, -0.1980842267f, -1.1814821082f, +0.6186059704f, -1.4145748049f, 0f, 0f, 0f,
            -1.0694433220f, -1.1388959357f, +1.0788632536f, -0.5297257984f, +0.3386025507f, 0f, 0f, 0f,
            -0.8783535738f, +1.2475299432f, -0.0376993977f, +1.0653029541f, -0.7320330673f, 0f, 0f, 0f,
            +1.6644650041f, +0.5820689456f, -0.8613458094f, +0.1111061909f, +0.3694466184f, 0f, 0f, 0f,
            +1.0607200718f, +0.0620356569f, +1.0296431488f, -1.0302379349f, -0.8657189441f, 0f, 0f, 0f,
            -0.8817724023f, +0.9515735227f, +0.6010913410f, +0.4766382991f, -1.3147206521f, 0f, 0f, 0f,
            -0.7611137823f, -0.2756268185f, -0.7300242585f, -1.1275552035f, +1.2411363795f, 0f, 0f, 0f,
            -1.3207783071f, +1.1561698454f, +0.2299470218f, -0.2072522588f, +0.9071862105f, 0f, 0f, 0f,
            -1.1816771520f, -0.7596862015f, -0.9827823279f, -0.6774291571f, -0.7757219970f, 0f, 0f, 0f,
            +1.2474994489f, +1.2266679741f, +0.6167132624f, +0.6372268146f, +0.3906885524f, 0f, 0f, 0f,
            +1.4101961346f, +0.8763908320f, -0.0679690545f, -0.3381071150f, -1.0603536005f, 0f, 0f, 0f,
            +0.4303889934f, +0.0075456308f, -0.7318402639f, -1.7280562703f, +0.5412390715f, 0f, 0f, 0f,
            -1.0150772094f, -0.2501828730f, +0.1938295376f, -1.6850991645f, -0.1729095290f, 0f, 0f, 0f,
            -0.2491682380f, -1.8343103261f, +0.5570892947f, +0.4096496582f, +0.3083171940f, 0f, 0f, 0f,
            +0.6707055360f, +0.7050912787f, +1.0951484850f, -0.8144527819f, +1.0910164227f, 0f, 0f, 0f,
            -0.1253944377f, -0.8069577491f, -1.1981624979f, -0.0909347438f, +1.3744936985f, 0f, 0f, 0f,
            +0.4979431688f, +1.0477297741f, -0.4424841168f, -0.9992478515f, -1.2083155460f, 0f, 0f, 0f,
            +0.3391283580f, +0.5297397571f, +1.8127693422f, +0.5200000016f, +0.2187122697f, 0f, 0f, 0f,
            +0.1729941911f, +0.5513060812f, -1.3295779972f, -1.3236932093f, -0.3823522614f, 0f, 0f, 0f,
            -0.1759985101f, -0.1116624120f, +1.0347327507f, +0.7188695866f, +1.5391915677f, 0f, 0f, 0f,
            +1.3834109634f, -0.5319875518f, -1.0053750542f, +0.8686683761f, +0.1944212023f, 0f, 0f, 0f,
            +0.2655537132f, +1.2074447952f, +0.2300093933f, +1.5279397437f, +0.2899208694f, 0f, 0f, 0f,
            -0.7650007456f, -1.7462692514f, -0.2985746155f, -0.2497276182f, +0.4623925569f, 0f, 0f, 0f,
            +1.5421515027f, +0.1809242613f, +0.6454387145f, +0.2020302919f, +1.0637799497f, 0f, 0f, 0f,
    };

    protected static final double[] GRAD_6D = {
            +0.31733186f, +0.04359915f, -0.63578104f, +0.60224147f, -0.06199565f, +0.35587048f, +0f, +0f,
            -0.54645425f, -0.75981513f, -0.03514434f, +0.13137365f, +0.29650029f, +0.13289887f, +0f, +0f,
            +0.72720729f, -0.01705130f, +0.10403853f, +0.57016794f, +0.10006650f, -0.35348266f, +0f, +0f,
            +0.05248672f, +0.16599786f, -0.49406271f, +0.51847470f, +0.63927166f, -0.21933445f, +0f, +0f,
            -0.57224122f, -0.08998594f, +0.44829955f, +0.53836681f, -0.05129933f, -0.41352093f, +0f, +0f,
            -0.35034584f, -0.37367516f, -0.52676009f, +0.12379417f, +0.42566489f, +0.51345191f, +0f, +0f,
            +0.40936909f, +0.33036021f, +0.46771483f, +0.15073372f, +0.51541333f, -0.46491971f, +0f, +0f,
            -0.64339751f, -0.29341468f, -0.50841617f, -0.08065981f, -0.46873502f, -0.12345817f, +0f, +0f,
            +0.46950904f, +0.41685007f, -0.33378791f, -0.39617029f, +0.54659770f, +0.19662896f, +0f, +0f,
            -0.49213884f, +0.50450587f, -0.00732472f, +0.57958418f, +0.39591449f, +0.10272980f, +0f, +0f,
            +0.34572956f, +0.62770109f, +0.12165109f, +0.35267248f, +0.34842369f, -0.47527514f, +0f, +0f,
            +0.07628223f, +0.56461194f, -0.39242673f, -0.20639693f, +0.33197602f, +0.60711436f, +0f, +0f,
            +0.46792592f, -0.38434666f, -0.46719345f, -0.40169520f, -0.06134349f, +0.49993117f, +0f, +0f,
            -0.25398819f, -0.82255018f, +0.40372967f, +0.21051604f, +0.02038482f, +0.22621006f, +0f, +0f,
            +0.23269489f, -0.42234243f, -0.18886779f, +0.44290933f, -0.40895242f, +0.60695810f, +0f, +0f,
            -0.13615585f, +0.26142849f, +0.68738606f, +0.42914965f, +0.26332301f, +0.43256061f, +0f, +0f,
            +0.06145597f, -0.25432792f, +0.65050463f, +0.35622065f, -0.52670947f, -0.32259598f, +0f, +0f,
            -0.28027055f, +0.30275296f, +0.39083872f, +0.17564171f, +0.25278203f, +0.76307625f, +0f, +0f,
            -0.62937098f, -0.24958587f, +0.11855057f, +0.52714220f, +0.47759151f, -0.14687496f, +0f, +0f,
            +0.68607574f, +0.28465344f, +0.57132493f, +0.11365238f, -0.32111327f, -0.07635256f, +0f, +0f,
            +0.42669573f, -0.16439965f, -0.54881376f, -0.56551221f, +0.40271560f, -0.08788072f, +0f, +0f,
            -0.30211042f, -0.47278547f, +0.05013786f, +0.46804387f, -0.39450159f, +0.55497099f, +0f, +0f,
            +0.31255895f, +0.03447891f, -0.07923299f, +0.39803160f, +0.82281399f, +0.24369695f, +0f, +0f,
            -0.55243216f, +0.49350231f, +0.52530668f, +0.25362578f, +0.26218499f, -0.20557247f, +0f, +0f,
            +0.06076301f, -0.02393840f, +0.36557410f, +0.55368747f, +0.25557899f, -0.70014279f, +0f, +0f,
            +0.36398574f, +0.04911046f, -0.24289511f, -0.18733973f, +0.02013080f, +0.87784000f, +0f, +0f,
            -0.62385490f, +0.02094759f, -0.44548631f, -0.21069894f, -0.60559127f, +0.02780938f, +0f, +0f,
            +0.51562840f, -0.27416131f, -0.14365580f, -0.46525735f, +0.16338488f, +0.62862302f, +0f, +0f,
            +0.52085189f, +0.51359303f, +0.02184478f, +0.53521775f, -0.23767218f, -0.34858599f, +0f, +0f,
            +0.12263603f, +0.53912951f, +0.57550729f, -0.10335514f, +0.57524709f, +0.14662748f, +0f, +0f,
            +0.40942178f, +0.17197663f, -0.02523801f, -0.20104824f, -0.60303014f, +0.63094779f, +0f, +0f,
            +0.05168570f, +0.23577798f, -0.19154992f, -0.67743578f, -0.51070301f, +0.43047548f, +0f, +0f,
            +0.21373839f, -0.44348268f, +0.34347986f, -0.49945694f, +0.45888698f, -0.42382317f, +0f, +0f,
            -0.60376535f, -0.06530087f, +0.49448067f, +0.12358559f, +0.58623743f, -0.16656623f, +0f, +0f,
            +0.44140930f, -0.41692548f, -0.23774988f, -0.27542786f, +0.39264397f, +0.58717642f, +0f, +0f,
            -0.67860697f, +0.20709913f, -0.12832398f, -0.58381216f, +0.24050209f, +0.28540774f, +0f, +0f,
            -0.02132450f, +0.00986587f, +0.26949011f, +0.42580554f, -0.82903198f, -0.24128534f, +0f, +0f,
            -0.20344882f, +0.51719618f, +0.24379623f, +0.11303683f, -0.46058654f, -0.63777957f, +0f, +0f,
            +0.15686479f, -0.67777169f, -0.04974608f, +0.51313211f, +0.49928667f, -0.03086314f, +0f, +0f,
            +0.53527130f, -0.50102597f, -0.60754472f, -0.25235098f, +0.13490559f, +0.10708155f, +0f, +0f,
            -0.20613512f, +0.39533044f, -0.34422306f, +0.47921455f, -0.19178040f, -0.64521804f, +0f, +0f,
            +0.33047796f, +0.49148538f, -0.30004348f, +0.33473309f, +0.31079743f, +0.59208027f, +0f, +0f,
            -0.52688857f, +0.40250311f, +0.38833191f, +0.50432308f, -0.33327489f, -0.21015252f, +0f, +0f,
            -0.30306420f, -0.34460825f, -0.26894228f, -0.58579646f, -0.51178483f, +0.33464319f, +0f, +0f,
            -0.20258582f, -0.29195675f, +0.11887973f, +0.91211540f, +0.03411881f, -0.16269371f, +0f, +0f,
            +0.61207678f, -0.21883722f, -0.23415725f, +0.00414476f, -0.34019274f, +0.63788273f, +0f, +0f,
            +0.11272999f, -0.54780877f, -0.62497664f, -0.41373740f, +0.33306010f, +0.12039112f, +0f, +0f,
            +0.24918468f, -0.06873428f, -0.42234580f, +0.12235329f, -0.26545138f, +0.81815148f, +0f, +0f,
            +0.32048708f, -0.40233908f, +0.24633289f, -0.37087758f, -0.55466799f, -0.47908728f, +0f, +0f,
            -0.33748729f, -0.45507986f, -0.50597645f, -0.28637016f, -0.54041997f, -0.22120318f, +0f, +0f,
            -0.23520314f, +0.82195093f, -0.22661283f, +0.16382454f, -0.41400232f, -0.13959354f, +0f, +0f,
            -0.30495751f, -0.47964557f, -0.68490238f, -0.43240776f, -0.13521732f, -0.05088770f, +0f, +0f,
            -0.56629250f, +0.19768903f, -0.08007522f, -0.29952637f, +0.09597442f, -0.73136356f, +0f, +0f,
            -0.21316607f, +0.47585902f, -0.49429850f, -0.24146904f, +0.45631329f, +0.46610972f, +0f, +0f,
            +0.12647584f, -0.10203700f, +0.20801341f, +0.66418891f, -0.65219775f, -0.25261414f, +0f, +0f,
            -0.69345279f, +0.30149980f, -0.46870940f, +0.20092958f, -0.21817920f, +0.34721422f, +0f, +0f,
            -0.69001417f, +0.09722776f, -0.37852252f, -0.24995374f, +0.24829304f, +0.49701266f, +0f, +0f,
            -0.82278510f, +0.05074883f, -0.39347330f, +0.00029980f, -0.34677214f, -0.21301870f, +0f, +0f,
            -0.51821811f, -0.22147302f, +0.53524316f, +0.12892242f, -0.55439554f, -0.26821451f, +0f, +0f,
            -0.21006612f, +0.26079212f, -0.02187063f, +0.72402587f, -0.27651658f, +0.53544979f, +0f, +0f,
            -0.09974428f, -0.45342128f, +0.71954978f, -0.31082396f, -0.26933824f, +0.31233586f, +0f, +0f,
            -0.48121951f, -0.43051247f, -0.50384151f, +0.12342710f, +0.03746782f, -0.55909965f, +0f, +0f,
            -0.51180831f, -0.07995548f, -0.53046702f, +0.48748209f, +0.16148937f, -0.43191028f, +0f, +0f,
            -0.38131649f, +0.46242477f, +0.46416075f, -0.20634110f, -0.53778490f, +0.30582118f, +0f, +0f,
            +0.62450430f, +0.14316692f, -0.14361038f, +0.27519251f, -0.60467865f, -0.35708047f, +0f, +0f,
            +0.52425890f, -0.20390682f, -0.33609142f, +0.51803372f, +0.28921536f, +0.46756035f, +0f, +0f,
            -0.44551641f, +0.31831805f, +0.24217750f, +0.49821219f, -0.47209418f, +0.41285649f, +0f, +0f,
            -0.01585731f, -0.45214512f, -0.14591363f, +0.74070676f, +0.00988742f, -0.47463489f, +0f, +0f,
            +0.24260837f, +0.44639366f, +0.31528570f, +0.45334773f, -0.47964168f, -0.45484996f, +0f, +0f,
            +0.47123463f, +0.64525048f, -0.06425763f, -0.18737730f, -0.11735335f, -0.55549853f, +0f, +0f,
            -0.02519722f, -0.25796327f, +0.26277107f, -0.58236203f, -0.41893538f, +0.59086294f, +0f, +0f,
            -0.48940330f, +0.33728563f, -0.05763492f, +0.44862021f, -0.40048256f, +0.53080564f, +0f, +0f,
            +0.73350664f, -0.02148298f, +0.01656814f, +0.00219059f, +0.49384961f, +0.46619710f, +0f, +0f,
            -0.25151229f, -0.62009962f, -0.26948657f, +0.31711936f, -0.35081923f, +0.50592112f, +0f, +0f,
            +0.00942985f, -0.35925999f, +0.47529205f, -0.26709475f, -0.53352146f, +0.53754630f, +0f, +0f,
            -0.59485495f, -0.53195924f, -0.09438376f, -0.41704491f, -0.41397531f, -0.09463944f, +0f, +0f,
            -0.74917126f, -0.24166385f, +0.22864554f, +0.31721357f, +0.06066292f, -0.47303041f, +0f, +0f,
            -0.33003960f, -0.08758658f, -0.09672609f, -0.39607089f, +0.55566932f, +0.63906648f, +0f, +0f,
            -0.58933068f, -0.38176870f, +0.46748019f, -0.06135883f, +0.36268480f, -0.39127879f, +0f, +0f,
            -0.06655669f, -0.73863083f, -0.32153946f, +0.57454599f, -0.09085689f, -0.09082394f, +0f, +0f,
            -0.36335404f, -0.41643677f, -0.57839830f, -0.03095988f, +0.59897925f, -0.01658256f, +0f, +0f,
            +0.23126668f, +0.21077907f, -0.14272193f, -0.29232225f, -0.48451339f, -0.74934159f, +0f, +0f,
            +0.48188197f, -0.04021475f, -0.15667971f, +0.16054853f, -0.60839754f, -0.58796308f, +0f, +0f,
            +0.31319356f, -0.19280657f, +0.76136690f, -0.08450623f, +0.47687867f, -0.22472488f, +0f, +0f,
            +0.67504537f, +0.36920158f, +0.40321048f, +0.03443604f, -0.29332731f, +0.39774172f, +0f, +0f,
            -0.14591598f, -0.59726183f, -0.03638422f, -0.65093487f, +0.39515711f, -0.20198429f, +0f, +0f,
            +0.60092128f, +0.18110182f, +0.25794919f, -0.39594768f, +0.15112959f, +0.59995268f, +0f, +0f,
            -0.42310244f, -0.26937197f, +0.07470001f, +0.53119510f, +0.41614374f, +0.53618944f, +0f, +0f,
            +0.00716054f, -0.69599782f, -0.05313860f, -0.00054500f, +0.69533871f, +0.17092634f, +0f, +0f,
            +0.12447149f, +0.33265313f, +0.35070015f, +0.53879932f, +0.37648083f, +0.56463759f, +0f, +0f,
            +0.29540077f, +0.04954124f, -0.48345087f, +0.72758494f, +0.07006910f, +0.37718664f, +0f, +0f,
            +0.48824142f, +0.45135801f, +0.48450857f, -0.26042407f, -0.42513580f, +0.27310535f, +0f, +0f,
            -0.49806371f, -0.47197596f, +0.02964708f, -0.13788472f, -0.45346141f, -0.55104701f, +0f, +0f,
            -0.53595119f, -0.53585470f, +0.17710362f, -0.45377632f, +0.41838964f, +0.11527149f, +0f, +0f,
            -0.36846431f, -0.46533180f, +0.65800816f, -0.28691297f, +0.31521457f, +0.18178647f, +0f, +0f,
            -0.29243126f, -0.43529565f, -0.58895978f, -0.49649471f, +0.29271342f, +0.21433587f, +0f, +0f,
            +0.05625669f, -0.50387710f, +0.48145041f, +0.44723671f, -0.55771174f, -0.00924491f, +0f, +0f,
            -0.40973125f, -0.73147173f, -0.09407630f, +0.43033451f, +0.01433427f, -0.32066459f, +0f, +0f,
            +0.26752725f, +0.50477344f, +0.06506951f, +0.36001097f, +0.59393393f, -0.43247366f, +0f, +0f,
            +0.48945720f, +0.60433156f, +0.12458128f, -0.48327805f, -0.25681943f, +0.28316179f, +0f, +0f,
            -0.45182760f, +0.21574002f, -0.31462623f, +0.25279349f, +0.44865729f, -0.62058075f, +0f, +0f,
            +0.44017304f, +0.43789555f, +0.58423563f, +0.41842994f, -0.26836655f, +0.16143005f, +0f, +0f,
            -0.67897032f, -0.32730885f, -0.02439973f, +0.40649244f, +0.47711065f, -0.19596475f, +0f, +0f,
            +0.57441588f, +0.09386994f, +0.28400793f, +0.59394229f, +0.45349906f, +0.14881354f, +0f, +0f,
            -0.33937399f, -0.54929055f, +0.26209493f, +0.07338003f, +0.56557076f, +0.43492125f, +0f, +0f,
            +0.05000799f, +0.74652764f, -0.36432144f, -0.20993543f, -0.13520410f, +0.49508839f, +0f, +0f,
            -0.04133215f, -0.20655741f, +0.52511282f, +0.04724863f, -0.62761217f, -0.53268446f, +0f, +0f,
            -0.18894911f, +0.05188976f, -0.45677123f, +0.42884456f, +0.61612085f, -0.43526216f, +0f, +0f,
            -0.65873541f, -0.09477005f, +0.40844030f, +0.35536013f, -0.16940065f, +0.48506226f, +0f, +0f,
            -0.45779281f, -0.46052673f, +0.34138050f, -0.54943270f, +0.37140594f, -0.14826175f, +0f, +0f,
            -0.06937871f, -0.14845488f, -0.73991837f, +0.41519184f, -0.11098464f, -0.49088356f, +0f, +0f,
            +0.46422563f, +0.46130716f, -0.44207791f, +0.12050605f, +0.34969556f, -0.48933493f, +0f, +0f,
            -0.35482925f, +0.28146983f, -0.35356606f, -0.38774754f, +0.35979702f, -0.62454776f, +0f, +0f,
            -0.48343191f, +0.41492185f, -0.50175316f, +0.21953122f, -0.54083165f, +0.04104095f, +0f, +0f,
            -0.51280508f, -0.54131124f, -0.00992871f, +0.23788701f, +0.43503332f, +0.44505087f, +0f, +0f,
            +0.22538373f, -0.30117119f, +0.46587685f, -0.46672901f, -0.59182069f, +0.27086737f, +0f, +0f,
            +0.43015756f, -0.06785111f, -0.26917802f, -0.57731860f, -0.53950120f, -0.33696522f, +0f, +0f,
            +0.20858352f, +0.63695057f, +0.49453142f, -0.04623537f, -0.54436247f, -0.08807572f, +0f, +0f,
            -0.35626464f, +0.06753954f, -0.18142793f, -0.49044207f, +0.55423882f, +0.53654796f, +0f, +0f,
            +0.52238539f, +0.55175875f, +0.29070268f, -0.14119026f, -0.55841587f, -0.08002963f, +0f, +0f,
            -0.02598800f, +0.46612949f, -0.56880970f, -0.44824563f, -0.03000049f, +0.50663523f, +0f, +0f,
            +0.04728458f, -0.26595723f, +0.21032033f, +0.52986834f, -0.52245334f, -0.57365347f, +0f, +0f,
            -0.31924244f, -0.13888420f, +0.30725800f, +0.49792332f, +0.61035592f, -0.40487771f, +0f, +0f,
            +0.03875857f, -0.53813545f, -0.56167256f, +0.46815373f, -0.14142713f, +0.39276248f, +0f, +0f,
            -0.19936871f, +0.12488860f, -0.62990029f, -0.29296146f, +0.49734531f, +0.46335923f, +0f, +0f,
            -0.07882670f, -0.15548800f, +0.57456768f, +0.55588544f, -0.56893054f, -0.08240882f, +0f, +0f,
            +0.11678856f, +0.53358760f, +0.49302489f, -0.53981846f, -0.23791336f, -0.33251226f, +0f, +0f,
            +0.39126928f, -0.39416116f, -0.35778844f, -0.39395609f, +0.50270356f, -0.39448759f, +0f, +0f,
            -0.17961290f, +0.34239532f, -0.21870225f, -0.23322835f, +0.75997835f, +0.41317237f, +0f, +0f,
            +0.29699501f, +0.17195435f, -0.34903627f, -0.31751884f, -0.59661546f, +0.55102732f, +0f, +0f,
            -0.22372913f, -0.51254305f, -0.31277318f, +0.54270199f, -0.34885011f, +0.41616819f, +0f, +0f,
            +0.53534023f, +0.45905986f, -0.20308675f, +0.01952364f, +0.33785805f, +0.58898336f, +0f, +0f,
            -0.04503846f, -0.52553334f, -0.60985458f, +0.46226027f, -0.36069029f, +0.07798443f, +0f, +0f,
            -0.40129033f, +0.39526722f, -0.20379584f, +0.45466492f, +0.46504795f, -0.46712669f, +0f, +0f,
            -0.43845831f, -0.59284534f, +0.05024190f, -0.36494839f, +0.32363879f, +0.46458051f, +0f, +0f,
            -0.46057360f, -0.34584626f, -0.12264748f, +0.48835437f, +0.21102526f, +0.60843919f, +0f, +0f,
            -0.08604754f, -0.16981605f, -0.37222833f, +0.45158609f, -0.55710254f, +0.55759406f, +0f, +0f,
            +0.54697451f, -0.45070837f, +0.03296252f, -0.48584332f, -0.28055687f, +0.42642516f, +0f, +0f,
            +0.34061925f, +0.38443007f, +0.61614808f, -0.55774172f, -0.07566037f, +0.19938218f, +0f, +0f,
            +0.30626924f, -0.05793904f, -0.10461119f, -0.43956387f, -0.57307193f, +0.60849886f, +0f, +0f,
            -0.52519951f, -0.42567534f, -0.19896500f, +0.48819483f, +0.12539008f, +0.49932157f, +0f, +0f,
            -0.10173361f, -0.07873850f, +0.37135540f, +0.65889542f, +0.63411890f, +0.09641423f, +0f, +0f,
            +0.60342393f, +0.05761737f, +0.35558841f, +0.20766418f, +0.03067018f, -0.67974377f, +0f, +0f,
            -0.07197105f, -0.44567383f, +0.65917594f, +0.44113802f, -0.29627117f, +0.28160739f, +0f, +0f,
            +0.38284479f, +0.43552320f, -0.42823684f, -0.54809258f, -0.27202273f, +0.32551612f, +0f, +0f,
            -0.74755699f, -0.20979308f, +0.19268299f, +0.27864013f, -0.39085278f, +0.36001727f, +0f, +0f,
            -0.64575536f, +0.59253747f, +0.04088551f, -0.20167391f, -0.43481684f, -0.02212841f, +0f, +0f,
            +0.45874103f, -0.00665875f, -0.30494054f, +0.52731059f, -0.64443887f, +0.05626427f, +0f, +0f,
            +0.61573773f, -0.00074622f, +0.25455659f, +0.30670278f, -0.18573195f, +0.65383825f, +0f, +0f,
            -0.08991956f, -0.28968403f, -0.60618287f, +0.53370861f, +0.37921556f, -0.33450055f, +0f, +0f,
            -0.47481167f, +0.38992741f, -0.10479631f, +0.45545456f, +0.12142073f, +0.62397625f, +0f, +0f,
            +0.59154225f, -0.10812441f, -0.46858345f, -0.36007270f, -0.10123747f, +0.52812407f, +0f, +0f,
            -0.01292122f, -0.23607532f, -0.57680411f, -0.44955815f, -0.31913443f, -0.55448100f, +0f, +0f,
            +0.54231398f, -0.31845386f, -0.38636423f, +0.22187979f, -0.63464258f, -0.05659949f, +0f, +0f,
            -0.41950690f, -0.45780289f, +0.31139813f, +0.39787962f, -0.20885901f, +0.56172180f, +0f, +0f,
            -0.03140488f, +0.56267475f, -0.55568153f, +0.33075363f, +0.39071115f, +0.33402949f, +0f, +0f,
            -0.51485161f, -0.34037011f, -0.46826090f, -0.60086679f, -0.07506940f, +0.18202033f, +0f, +0f,
            -0.49669644f, +0.13236483f, +0.53440735f, +0.47201200f, -0.05992551f, -0.47306929f, +0f, +0f,
            -0.32796852f, +0.65593302f, +0.20800030f, -0.38965914f, -0.51564565f, -0.03463672f, +0f, +0f,
            -0.30473794f, +0.12584230f, +0.63911213f, +0.11269477f, +0.62944339f, +0.27191006f, +0f, +0f,
            -0.53642197f, +0.50742224f, -0.22907820f, +0.47022559f, -0.19141256f, +0.38019261f, +0f, +0f,
            -0.28865425f, +0.76169672f, -0.36166127f, -0.30555403f, -0.12541657f, -0.31081403f, +0f, +0f,
            +0.00259784f, +0.37371464f, -0.31515119f, +0.62032810f, +0.60524642f, -0.09939888f, +0f, +0f,
            -0.40019833f, +0.15931480f, -0.61653030f, -0.49479441f, -0.02151791f, -0.43481713f, +0f, +0f,
            -0.26445143f, -0.48401155f, +0.27737058f, -0.12537486f, -0.46956235f, +0.61859207f, +0f, +0f,
            -0.49776294f, +0.65095132f, -0.20147785f, +0.26022926f, +0.39526195f, -0.25288299f, +0f, +0f,
            +0.20792543f, +0.67255995f, +0.01329671f, +0.06908240f, -0.37233547f, +0.60070560f, +0f, +0f,
            -0.60329265f, +0.40708027f, -0.17229997f, -0.52997954f, +0.22211745f, -0.33229784f, +0f, +0f,
            +0.61826884f, -0.62582169f, +0.33820439f, +0.23870919f, -0.20670655f, -0.10953969f, +0f, +0f,
            -0.63678168f, -0.51101649f, -0.19131817f, -0.49493417f, -0.22614515f, +0.02582853f, +0f, +0f,
            +0.70684625f, +0.07293280f, -0.30827034f, -0.52659704f, -0.33954839f, +0.08614532f, +0f, +0f,
            -0.52429050f, +0.39091424f, +0.52819210f, -0.16569162f, +0.44719167f, +0.25667977f, +0f, +0f,
            +0.85033978f, -0.37311666f, -0.03158551f, -0.06354692f, -0.35026506f, +0.09992363f, +0f, +0f,
            -0.43149574f, +0.16017753f, -0.36624037f, +0.49372029f, -0.60067103f, +0.22238962f, +0f, +0f,
            -0.43599537f, -0.36065835f, -0.42475053f, -0.52301759f, +0.03945453f, +0.47362064f, +0f, +0f,
            -0.35793170f, -0.43917817f, -0.49072242f, -0.32880277f, -0.38509560f, -0.42636724f, +0f, +0f,
            -0.04367964f, +0.74697226f, -0.40732954f, -0.48088968f, +0.18029290f, -0.10220931f, +0f, +0f,
            -0.05890257f, +0.00825952f, +0.71365961f, -0.53043791f, +0.22906331f, +0.39155822f, +0f, +0f,
            +0.43459649f, +0.18964470f, +0.15217427f, +0.59694624f, +0.05378658f, +0.62671041f, +0f, +0f,
            -0.48833575f, +0.06890988f, +0.60168404f, -0.05545504f, -0.62426261f, -0.04446193f, +0f, +0f,
            -0.71822145f, +0.05449495f, +0.25733756f, -0.42706881f, -0.44024663f, +0.19687748f, +0f, +0f,
            +0.47232210f, +0.63009683f, +0.21662569f, +0.31063720f, +0.07945588f, +0.47974409f, +0f, +0f,
            -0.39506538f, +0.42517729f, +0.29375773f, +0.04450363f, -0.46173213f, +0.60139575f, +0f, +0f,
            -0.40354126f, +0.41304136f, -0.29533980f, -0.45300699f, +0.23702354f, -0.56385297f, +0f, +0f,
            -0.62315380f, -0.42397903f, +0.53044082f, +0.37874432f, +0.05492271f, +0.06395219f, +0f, +0f,
            +0.41959045f, -0.83420441f, -0.25505372f, +0.25012310f, +0.01097423f, +0.01767574f, +0f, +0f,
            -0.25231575f, -0.17034034f, -0.00222544f, -0.49677710f, +0.43184899f, -0.68850194f, +0f, +0f,
            -0.18528128f, -0.48330898f, +0.13528868f, +0.15202104f, +0.57661281f, -0.59848767f, +0f, +0f,
            +0.64287473f, -0.30923674f, +0.22234318f, +0.09924896f, +0.64370450f, +0.13206961f, +0f, +0f,
            -0.49018899f, +0.68654120f, -0.27238863f, -0.08583242f, +0.44161945f, +0.10856057f, +0f, +0f,
            +0.48795432f, +0.42184193f, -0.43797315f, +0.35186997f, -0.46483432f, +0.22857392f, +0f, +0f,
            +0.52970834f, -0.50684486f, -0.39782161f, -0.39327093f, -0.34863027f, +0.16748196f, +0f, +0f,
            -0.46048505f, -0.38871269f, -0.68287320f, -0.18448530f, -0.25358256f, +0.26870280f, +0f, +0f,
            +0.68895573f, -0.31010227f, -0.35882194f, +0.30088738f, -0.03913954f, -0.45646277f, +0f, +0f,
            -0.21954767f, +0.40838837f, +0.23284186f, +0.30349649f, +0.57233263f, +0.55778817f, +0f, +0f,
            +0.57731035f, +0.09121830f, +0.70670016f, +0.01635803f, +0.39392452f, -0.05935263f, +0f, +0f,
            +0.50055570f, -0.02174979f, +0.56767851f, +0.50580176f, +0.34691320f, +0.22478399f, +0f, +0f,
            -0.37901911f, +0.53804099f, -0.46780195f, +0.51497346f, -0.27981005f, +0.06727844f, +0f, +0f,
            +0.67241900f, +0.07409958f, +0.43138117f, +0.05456751f, -0.37927768f, +0.45764946f, +0f, +0f,
            +0.14529189f, -0.23854982f, +0.45401647f, +0.25466539f, +0.46182069f, -0.66160446f, +0f, +0f,
            -0.15570980f, -0.38476787f, +0.37322840f, -0.43977613f, -0.61243005f, -0.34631643f, +0f, +0f,
            -0.19590302f, +0.42065974f, +0.43447548f, -0.10575548f, +0.70439951f, -0.29754920f, +0f, +0f,
            -0.13558865f, +0.14270734f, +0.49647494f, -0.65533234f, -0.11714854f, +0.52113213f, +0f, +0f,
            -0.62283747f, +0.20812698f, -0.16205154f, +0.20384566f, -0.59321895f, +0.38604941f, +0f, +0f,
            +0.44487837f, -0.37224943f, -0.22188447f, +0.48921538f, +0.41432418f, -0.45087099f, +0f, +0f,
            +0.66422841f, +0.21517761f, +0.09401257f, -0.43581590f, +0.22245680f, -0.51404116f, +0f, +0f,
            -0.11369362f, +0.32284689f, -0.38818285f, +0.49680024f, +0.04768486f, -0.69503480f, +0f, +0f,
            -0.51372007f, -0.50673230f, +0.32715252f, -0.26799714f, -0.47616510f, +0.27153195f, +0f, +0f,
            -0.47315177f, -0.45711495f, -0.31178280f, -0.51697763f, -0.14302372f, -0.42689944f, +0f, +0f,
            -0.05044203f, +0.23609184f, +0.38634880f, +0.56012774f, +0.38963669f, -0.57174382f, +0f, +0f,
            -0.15472134f, -0.15333579f, -0.14189768f, +0.03227926f, -0.66054298f, -0.70360180f, +0f, +0f,
            -0.10345191f, -0.30503725f, +0.31038263f, +0.36878846f, -0.76824774f, +0.27148306f, +0f, +0f,
            -0.06021286f, -0.41727554f, +0.39199300f, -0.44040104f, +0.24955102f, -0.64215903f, +0f, +0f,
            +0.25443195f, -0.01378958f, +0.44365000f, +0.53296203f, -0.55057750f, -0.38867053f, +0f, +0f,
            -0.36068564f, -0.65616661f, -0.48495997f, +0.24088316f, -0.18080297f, -0.33682435f, +0f, +0f,
            -0.53824550f, -0.09672890f, -0.52086198f, +0.33195321f, -0.03226394f, +0.56427315f, +0f, +0f,
            +0.40151657f, -0.44825725f, -0.54910020f, -0.09593627f, +0.57195639f, +0.00097783f, +0f, +0f,
            +0.21961099f, +0.62823723f, -0.01004593f, -0.66105648f, -0.17161595f, -0.30089924f, +0f, +0f,
            +0.27961471f, +0.05452339f, +0.61485903f, +0.11958885f, -0.61032561f, -0.39241856f, +0f, +0f,
            -0.30223065f, -0.23605925f, -0.09697276f, -0.46458104f, -0.37853464f, +0.69599203f, +0f, +0f,
            +0.00236355f, +0.62702100f, +0.49658954f, -0.20369645f, -0.56457560f, +0.00021299f, +0f, +0f,
            -0.64198493f, +0.59676262f, +0.46274573f, +0.08842191f, +0.09802999f, -0.01295307f, +0f, +0f,
            -0.05396543f, +0.13439533f, -0.33103493f, +0.55991756f, -0.58127599f, -0.46696041f, +0f, +0f,
            -0.43965993f, +0.07544961f, +0.15096395f, -0.38868406f, -0.00334360f, -0.79191533f, +0f, +0f,
            -0.21743914f, -0.32019630f, -0.56067107f, +0.02728491f, -0.49444926f, -0.53908992f, +0f, +0f,
            -0.36492599f, +0.52529904f, +0.18002253f, +0.14829474f, +0.17212619f, -0.71194315f, +0f, +0f,
            +0.00518762f, +0.50490293f, +0.24361032f, +0.13688117f, -0.61381291f, -0.53869971f, +0f, +0f,
            +0.66421180f, +0.21833854f, -0.08790993f, +0.15624552f, -0.68780724f, +0.07701505f, +0f, +0f,
            +0.52710630f, -0.42143671f, -0.06996455f, -0.24196341f, -0.68814841f, +0.08695091f, +0f, +0f,
            +0.62392249f, -0.23663281f, -0.59058622f, +0.22685863f, -0.36683948f, -0.14105848f, +0f, +0f,
            +0.18069852f, -0.08382855f, +0.66240167f, +0.16722813f, -0.25503640f, -0.65462662f, +0f, +0f,
            -0.37112528f, +0.43100319f, -0.11342774f, +0.14418808f, +0.57533269f, +0.55842502f, +0f, +0f,
            +0.55378724f, +0.21098160f, -0.32249766f, +0.31268307f, -0.37624695f, -0.55269271f, +0f, +0f,
            +0.26014658f, +0.56373458f, -0.21638357f, +0.41216916f, -0.25078072f, -0.57873208f, +0f, +0f,
            +0.11217864f, +0.54196554f, -0.31989128f, +0.54691221f, +0.24062434f, +0.48409277f, +0f, +0f,
            +0.08756442f, -0.12083081f, +0.69931172f, +0.35220575f, +0.28770484f, -0.53091668f, +0f, +0f,
            +0.33957021f, +0.04252094f, -0.30935928f, +0.61022210f, +0.54650816f, +0.34079124f, +0f, +0f,
            +0.32746112f, +0.32095220f, -0.61142534f, +0.32197324f, -0.38236071f, +0.40749411f, +0f, +0f,
            +0.58741915f, -0.30916030f, -0.57642977f, -0.03884619f, +0.04792671f, -0.47252657f, +0f, +0f,
            +0.02622438f, +0.03176890f, -0.12510902f, +0.36102734f, -0.72217212f, +0.57513252f, +0f, +0f,
            -0.27510374f, -0.51534021f, +0.02577402f, +0.59201067f, +0.40728366f, -0.37645913f, +0f, +0f,
            -0.29983338f, -0.61017291f, -0.18551919f, +0.50515945f, +0.18206593f, -0.46372136f, +0f, +0f,
            -0.64290893f, -0.34887011f, -0.55318606f, -0.21230198f, -0.19828983f, +0.27304198f, +0f, +0f,
            -0.32778879f, -0.09431729f, +0.57811170f, +0.54346692f, +0.17699503f, -0.47197676f, +0f, +0f,
            -0.07573870f, +0.53381750f, -0.13406342f, +0.71765386f, +0.34271060f, +0.24259408f, +0f, +0f,
            -0.30574273f, +0.17419449f, -0.78861555f, +0.43305678f, +0.06485332f, +0.25003806f, +0f, +0f,
            +0.43970359f, -0.51651518f, -0.39723461f, -0.34513492f, +0.32129829f, -0.39965829f, +0f, +0f,
            -0.25184899f, -0.35937572f, +0.15273239f, -0.51640931f, +0.42187157f, -0.58261460f, +0f, +0f,
            -0.57396000f, +0.19127861f, +0.45995634f, -0.43664716f, +0.46016301f, +0.14146310f, +0f, +0f,
            +0.11500068f, +0.05112652f, -0.25672855f, -0.54715738f, +0.67669928f, +0.40118355f, +0f, +0f,
            -0.45252668f, -0.40809988f, -0.06493154f, +0.19116562f, +0.76523014f, +0.04833740f, +0f, +0f,
            -0.08007565f, +0.75305314f, +0.34797424f, +0.29104493f, +0.00401859f, -0.46977598f, +0f, +0f,
            -0.38902576f, +0.49100041f, -0.17812126f, -0.43787557f, -0.46923187f, +0.40489108f, +0f, +0f,
            +0.37433236f, -0.29441766f, -0.06628513f, +0.33217472f, +0.73917165f, +0.33479099f, +0f, +0f,
            -0.02973230f, -0.51371026f, +0.34133522f, -0.41361792f, -0.51561746f, -0.42634124f, +0f, +0f,
            +0.51057171f, -0.23740201f, +0.26673587f, +0.55217673f, +0.16849318f, +0.52774964f, +0f, +0f,
    };

    protected static double valCoord2D(int seed, int x, int y) {
        return (hashAll(x, y, seed) >> 7) * 0x1.0p-24f;
    }

    protected static double valCoord3D(int seed, int x, int y, int z) {
        return (hashAll(x, y, z, seed) >> 7) * 0x1.0p-24f;
    }

    protected static double valCoord4D(int seed, int x, int y, int z, int w) {
        return (hashAll(x, y, z, w, seed) >> 7) * 0x1.0p-24f;
    }

    protected static double valCoord5D(int seed, int x, int y, int z, int w, int u) {
        return (hashAll(x, y, z, w, u, seed) >> 7) * 0x1.0p-24f;
    }

    protected static double valCoord6D(int seed, int x, int y, int z, int w, int u, int v) {
        return (hashAll(x, y, z, w, u, v, seed) >> 7) * 0x1.0p-24f;
    }

    protected static double gradCoord2D(int seed, int x, int y, double xd, double yd) {
        final int hash = hash256(x, y, seed) << 1;
        return xd * GRAD_2D[hash] + yd * GRAD_2D[hash + 1];
    }

    protected static double gradCoord3D(int seed, int x, int y, int z, double xd, double yd, double zd) {
        final int hash = hash256(x, y, z, seed) << 2;
        return xd * GradientVectors.GRADIENTS_3D[hash] + yd * GradientVectors.GRADIENTS_3D[hash+1] + zd * GradientVectors.GRADIENTS_3D[hash+2];
    }

    protected static double gradCoord4D(int seed, int x, int y, int z, int w, double xd, double yd, double zd, double wd) {
        final int hash = hash256(x, y, z, w, seed) & 0xFC;
        return xd * GRAD_4D[hash] + yd * GRAD_4D[hash + 1] + zd * GRAD_4D[hash + 2] + wd * GRAD_4D[hash + 3];
    }


    protected static double gradCoord5D(int seed, int x, int y, int z, int w, int u,
                                        double xd, double yd, double zd, double wd, double ud) {
        final int hash = hash256(x, y, z, w, u, seed) << 3;
        return xd * GRAD_5D[hash] + yd * GRAD_5D[hash+1] + zd * GRAD_5D[hash+2] + wd * GRAD_5D[hash+3] + ud * GRAD_5D[hash+4];
    }

    protected static double gradCoord6D(int seed, int x, int y, int z, int w, int u, int v,
                                        double xd, double yd, double zd, double wd, double ud, double vd) {
        final int hash = hash256(x, y, z, w, u, v, seed) << 3;
        return xd * GRAD_6D[hash] + yd * GRAD_6D[hash+1] + zd * GRAD_6D[hash+2] +
                wd * GRAD_6D[hash+3] + ud * GRAD_6D[hash+4] + vd * GRAD_6D[hash+5];
    }

    protected static final double[] GRAD_2D = {
            +0.6499429579167653f, +0.7599829941876370f,
            -0.1551483029088119f, +0.9878911904175052f,
            -0.8516180517334043f, +0.5241628506120981f,
            -0.9518580082090311f, -0.3065392833036837f,
            -0.3856887670108717f, -0.9226289476282616f,
            +0.4505066120763985f, -0.8927730912586049f,
            +0.9712959670388622f, -0.2378742197339624f,
            +0.8120673355833279f, +0.5835637432865366f,
            +0.0842989251943661f, +0.9964405106232257f,
            -0.7024883500032670f, +0.7116952424385647f,
            -0.9974536374007479f, -0.0713178886116052f,
            -0.5940875849508908f, -0.8044003613917750f,
            +0.2252075529515288f, -0.9743108118529653f,
            +0.8868317111719171f, -0.4620925405802277f,
            +0.9275724981153959f, +0.3736432265409930f,
            +0.3189067150428103f, +0.9477861083074618f,
            -0.5130301507665112f, +0.8583705868705491f,
            -0.9857873824221494f, +0.1679977281313266f,
            -0.7683809836504446f, -0.6399927061806058f,
            -0.0130202362193748f, -0.9999152331316848f,
            +0.7514561619680513f, -0.6597830223946701f,
            +0.9898275175279653f, +0.1422725748147741f,
            +0.5352066871710182f, +0.8447211386057674f,
            -0.2941198828144364f, +0.9557685360657266f,
            -0.9175289804081126f, +0.3976689202229027f,
            -0.8985631161871687f, -0.4388443075032474f,
            -0.2505005588110731f, -0.9681164547900940f,
            +0.5729409678802212f, -0.8195966369650838f,
            +0.9952584535626074f, -0.0972656702653466f,
            +0.7207814785200723f, +0.6931623620930514f,
            -0.0583247612407003f, +0.9982976621360060f,
            -0.7965970142012075f, +0.6045107087270838f,
            -0.9771604781144960f, -0.2125027058911242f,
            -0.4736001288089817f, -0.8807399831914728f,
            +0.3615343409387538f, -0.9323587937709286f,
            +0.9435535266854258f, -0.3312200813348966f,
            +0.8649775992346886f, +0.5018104750024599f,
            +0.1808186720712497f, +0.9835164502083277f,
            -0.6299339540895539f, +0.7766487066139361f,
            -0.9996609468975833f, +0.0260382650694516f,
            -0.6695112313914258f, -0.7428019325774111f,
            +0.1293727267195084f, -0.9915960354807594f,
            +0.8376810167470904f, -0.5461597881403947f,
            +0.9595170289111490f, +0.2816506190824391f,
            +0.4095816551369482f, +0.9122734610714476f,
            -0.4271076040148479f, +0.9042008043530463f,
            -0.9647728141412515f, +0.2630844295924223f,
            -0.8269869890664444f, -0.5622210596507540f,
            -0.1102159255238020f, -0.9939076666174438f,
            +0.6837188597775012f, -0.7297455178242300f,
            +0.9989724417383330f, +0.0453217458550843f,
            +0.6148313475439905f, +0.7886586169422362f,
            -0.1997618324529528f, +0.9798444827088829f,
            -0.8744989400706802f, +0.4850274258382270f,
            -0.9369870231562731f, -0.3493641630687752f,
            -0.3434772946489506f, -0.9391609809082988f,
            +0.4905057254335028f, -0.8714379687143274f,
            +0.9810787787756657f, -0.1936089611460388f,
            +0.7847847614201463f, +0.6197684069414349f,
            +0.0390518795551629f, +0.9992371844077906f,
            -0.7340217731995672f, +0.6791259356474049f,
            -0.9931964444524306f, -0.1164509455824639f,
            -0.5570202966000876f, -0.8304988796955420f,
            +0.2691336060685578f, -0.9631028512493016f,
            +0.9068632806061000f, -0.4214249521425399f,
            +0.9096851999779008f, +0.4152984913783901f,
            +0.2756236986873733f, +0.9612656119522284f,
            -0.5514058359842319f, +0.8342371389734039f,
            -0.9923883787916933f, +0.1231474954645637f,
            -0.7385858406439617f, -0.6741594440488484f,
            +0.0323110469045428f, -0.9994778618098213f,
            +0.7805865154410089f, -0.6250477517051506f,
            +0.9823623706068018f, +0.1869870926448790f,
            +0.4963724943556111f, +0.8681096398768929f,
            -0.3371347561867868f, +0.9414564016304079f,
            -0.9346092156607797f, +0.3556762769737983f,
            -0.8777506000588920f, -0.4791178185960681f,
            -0.2063664269701996f, -0.9784747813917093f,
            +0.6094977881394418f, -0.7927877687333024f,
            +0.9986440175043460f, -0.0520588734297966f,
            +0.6886255051458764f, +0.7251171723677399f,
            -0.1035094220814735f, +0.9946284731196666f,
            -0.8231759450656516f, +0.5677863713275190f,
            -0.9665253951623188f, -0.2565709658288005f,
            -0.4331968034012919f, -0.9012993562201753f,
            +0.4034189716368784f, -0.9150153732716426f,
            +0.9575954428121146f, -0.2881162402667889f,
            +0.8413458575409575f, +0.5404971304259356f,
            +0.1360581877502697f, +0.9907008476558967f,
            -0.6644857355505560f, +0.7473009482463117f,
            -0.9998138366647180f, -0.0192948701414780f,
            -0.6351581891853917f, -0.7723820781910558f,
            +0.1741806522163015f, -0.9847137149413040f,
            +0.8615731658120597f, -0.5076334109892543f,
            +0.9457661714829020f, +0.3248481935898273f,
            +0.3678149601703667f, +0.9298990026206456f,
            -0.4676486851245607f, +0.8839144230643990f,
            -0.9757048995218635f, +0.2190889067228882f,
            -0.8006563717736747f, -0.5991238388999518f,
            -0.0650570415691071f, -0.9978815467490495f,
            +0.7160896397121960f, -0.6980083293893113f,
            +0.9958918787052943f, +0.0905503502413954f,
            +0.5784561871098056f, +0.8157134543418942f,
            -0.2439648281544816f, +0.9697840804135497f,
            -0.8955826311865743f, +0.4448952131872543f,
            -0.9201904205900768f, -0.3914710587696841f,
            -0.3005599364234082f, -0.9537629289384008f,
            +0.5294967923694863f, -0.8483119396014800f,
            +0.9888453593035162f, -0.1489458135829932f,
            +0.7558893631265085f, +0.6546993743025888f,
            -0.0062754222469803f, +0.9999803093439501f,
            -0.7640466961212760f, +0.6451609459244744f,
            -0.9868981170802014f, -0.1613446822909051f,
            -0.5188082666339063f, -0.8548906260290385f,
            +0.3125065582647844f, -0.9499156020623616f,
            +0.9250311403279032f, -0.3798912863223621f,
            +0.8899283927548960f, +0.4561002694240463f,
            +0.2317742435145519f, +0.9727696027545563f,
            -0.5886483179573486f, +0.8083892365475831f,
            -0.9969499014064180f, +0.0780441803450664f,
            -0.7072728176724660f, -0.7069407057042696f,
            +0.0775759270620736f, -0.9969864470194466f,
            +0.8081126726681943f, -0.5890279350532263f,
            +0.9728783545459001f, +0.2313173302112532f,
            +0.4565181982253288f, +0.8897140746830408f,
            -0.3794567783511009f, +0.9252094645881026f,
            -0.9497687200714887f, +0.3129526775309106f,
            -0.8551342041690687f, -0.5184066867432686f,
            -0.1618081880753845f, -0.9868222283024238f,
            +0.6448020194233159f, -0.7643496292585048f,
            +0.9999772516247822f, -0.0067450895432855f,
            +0.6550543261176665f, +0.7555817823601425f,
            -0.1484813589986064f, +0.9889152066936411f,
            -0.8480631534437840f, +0.5298951667745091f,
            -0.9539039899003245f, -0.3001119425351840f,
            -0.3919032080850608f, -0.9200064540494471f,
            +0.4444745293405786f, -0.8957914895596358f,
            +0.9696693887216105f, -0.2444202867526717f,
            +0.8159850520735595f, +0.5780730012658526f,
            +0.0910180879994953f, +0.9958492394217692f,
            -0.6976719213969089f, +0.7164173993520435f,
            -0.9979119924958648f, -0.0645883521459785f,
            -0.5994998228898376f, -0.8003748886334786f,
            +0.2186306161766729f, -0.9758076929755208f,
            +0.8836946816279001f, -0.4680637880274058f,
            +0.9300716543684309f, +0.3673781672069940f,
            +0.3252923626016029f, +0.9456134933645286f,
            -0.5072286936943775f, +0.8618114946396893f,
            -0.9846317976415725f, +0.1746431306210620f,
            -0.7726803123417516f, -0.6347953488483143f,
            -0.0197644578133314f, -0.9998046640256011f,
            +0.7469887719961158f, -0.6648366525032559f,
            +0.9907646418168752f, +0.1355928631067248f,
            +0.5408922318074902f, +0.8410919055432124f,
            -0.2876664477065717f, +0.9577306588304888f,
            -0.9148257956391065f, +0.4038486890325085f,
            -0.9015027194859215f, -0.4327734358292892f,
            -0.2570248925062563f, -0.9664047830139022f,
            +0.5673996816983953f, -0.8234425306046317f,
            +0.9945797473944409f, -0.1039765650173647f,
            +0.7254405241129018f, +0.6882848581617921f,
            -0.0515898273251730f, +0.9986683582233687f,
            -0.7925014140531963f, +0.6098700752813540f,
            -0.9785715990807187f, -0.2059068368767903f,
            -0.4795300252265173f, -0.8775254725113429f,
            +0.3552372730694574f, -0.9347761656258549f,
            +0.9412979532686209f, -0.3375768996425928f,
            +0.8683426789873530f, +0.4959647082697184f,
            +0.1874484652642005f, +0.9822744386728669f,
            -0.6246810590458048f, +0.7808800000444446f,
            -0.9994625758058275f, +0.0327804753409776f,
            -0.6745062666468870f, -0.7382691218343610f,
            +0.1226813796500722f, -0.9924461089082646f,
            +0.8339780641890598f, -0.5517975973592748f,
            +0.9613949601033843f, +0.2751721837101493f,
            +0.4157257040026583f, +0.9094900433932711f,
            -0.4209989726203348f, +0.9070611142875780f,
            -0.9629763390922247f, +0.2695859238694348f,
            -0.8307604078465821f, -0.5566301687427484f,
            -0.1169174144996730f, -0.9931416405461567f,
            +0.6787811074228051f, -0.7343406622310046f,
            +0.9992554159724470f, +0.0385825562881973f,
            +0.6201369341201711f, +0.7844935837468874f,
            -0.1931481494214682f, +0.9811696042861612f,
            -0.8712074932224428f, +0.4909149659086258f,
            -0.9393222007870077f, -0.3430361542296271f,
            -0.3498042060103595f, -0.9368228314134226f,
            +0.4846166400948296f, -0.8747266499559725f,
            +0.9797505510481769f, -0.2002220210685972f,
            +0.7889473022428521f, +0.6144608647291752f,
            +0.0457909354721791f, +0.9989510449609544f,
            -0.7294243101497431f, +0.6840615292227530f,
            -0.9939593229024027f, -0.1097490975607407f,
            -0.5626094146025390f, -0.8267228354174018f,
            +0.2626312687452330f, -0.9648962724963078f,
            +0.9040001019019392f, -0.4275322394408211f,
            +0.9124657316291773f, +0.4091531358824348f,
            +0.2821012513235693f, +0.9593846381935018f,
            -0.5457662881946498f, +0.8379374431723614f,
            -0.9915351626845509f, +0.1298384425357957f,
            -0.7431163048326799f, -0.6691622803863227f,
            +0.0255687442062853f, -0.9996730662170076f,
            +0.7763527553119807f, -0.6302986588273021f,
            +0.9836012681423212f, +0.1803567168386515f,
            +0.5022166799422209f, +0.8647418148718223f,
            -0.3307768791887710f, +0.9437089891455613f,
            -0.9321888864830543f, +0.3619722087639923f,
            -0.8809623252471085f, -0.4731864130500873f,
            -0.2129616324856343f, -0.9770605626515961f,
            +0.6041364985661350f, -0.7968808512571063f,
            +0.9982701582127194f, -0.0587936324949578f,
            +0.6935008202914851f, +0.7204558364362367f,
            -0.0967982092968079f, +0.9953040272584711f,
            -0.8193274492343137f, +0.5733258505694586f,
            -0.9682340024187017f, -0.2500458289199430f,
            -0.4392662937408502f, -0.8983569018954422f,
            +0.3972379338845546f, -0.9177156552457467f,
            +0.9556302892322005f, -0.2945687530984589f,
            +0.8449724198323217f, +0.5348098818484104f,
            +0.1427374585755972f, +0.9897605861618151f,
            -0.6594300077680133f, +0.7517659641504648f,
            -0.9999212381512442f, -0.0125505973595986f,
            -0.6403535266476091f, -0.7680803088935230f,
            +0.1675347077076747f, -0.9858661784001437f,
            +0.8581295336101056f, -0.5134332513054668f,
            +0.9479357869928937f, +0.3184615263075951f,
            +0.3740788450165170f, +0.9273969040875156f,
            -0.4616759649446430f, +0.8870486477034012f,
            -0.9742049295269273f, +0.2256651397213017f,
            -0.8046793020829978f, -0.5937097108850584f,
            -0.0717863620135296f, -0.9974200309943962f,
            +0.7113652211526822f, -0.7028225395748172f,
            +0.9964799940037152f, +0.0838309104707540f,
            +0.5839450884626246f, +0.8117931594072332f,
            -0.2374179978909748f, +0.9714075840127259f,
            -0.8925614000865144f, +0.4509258775847768f,
            -0.9228099950981292f, -0.3852553866553855f,
            -0.3069863155319683f, -0.9517139286971200f,
            +0.5237628071845146f, -0.8518641451605984f,
            +0.9878182118285335f, -0.1556122758007173f,
            +0.7602881737752754f, +0.6495859395164404f,
            +0.0004696772366984f, +0.9999998897016406f,
            -0.7596776469502666f, +0.6502998329417794f,
            -0.9879639510809196f, -0.1546842957917130f,
            -0.5245627784110601f, -0.8513717704420726f,
            +0.3060921834538644f, -0.9520018777441807f,
            +0.9224476966294768f, -0.3861220622846781f,
            +0.8929845854878761f, +0.4500872471877493f,
            +0.2383303891026603f, +0.9711841358002995f,
            -0.5831822693781987f, +0.8123413326200348f,
            -0.9964008074312266f, +0.0847669213219385f,
            -0.7120251067268070f, -0.7021540054650968f,
            +0.0708493994771745f, -0.9974870237721009f,
            +0.8041212432524677f, -0.5944653279629567f,
            +0.9744164792492415f, +0.2247499165016809f,
            +0.4625090142797330f, +0.8866145790082576f,
    };
    private static final double[] GRAD_3D =
            {
                    -0.448549002408981f, +1.174316525459290f, +0.000000000000001f, +0.0f,
                    +0.000000000000001f, +1.069324374198914f, +0.660878777503967f, +0.0f,
                    +0.448549002408981f, +1.174316525459290f, +0.000000000000001f, +0.0f,
                    +0.000000000000001f, +1.069324374198914f, -0.660878777503967f, +0.0f,
                    -0.725767493247986f, +0.725767493247986f, -0.725767493247986f, +0.0f,
                    -1.069324374198914f, +0.660878777503967f, +0.000000000000001f, +0.0f,
                    -0.725767493247986f, +0.725767493247986f, +0.725767493247986f, +0.0f,
                    +0.725767493247986f, +0.725767493247986f, +0.725767493247986f, +0.0f,
                    +1.069324374198914f, +0.660878777503967f, +0.000000000000000f, +0.0f,
                    +0.725767493247986f, +0.725767493247986f, -0.725767493247986f, +0.0f,
                    -0.660878777503967f, +0.000000000000003f, -1.069324374198914f, +0.0f,
                    -1.174316525459290f, +0.000000000000003f, -0.448549002408981f, +0.0f,
                    +0.000000000000000f, +0.448549002408981f, -1.174316525459290f, +0.0f,
                    -0.660878777503967f, +0.000000000000001f, +1.069324374198914f, +0.0f,
                    +0.000000000000001f, +0.448549002408981f, +1.174316525459290f, +0.0f,
                    -1.174316525459290f, +0.000000000000001f, +0.448549002408981f, +0.0f,
                    +0.660878777503967f, +0.000000000000001f, +1.069324374198914f, +0.0f,
                    +1.174316525459290f, +0.000000000000001f, +0.448549002408981f, +0.0f,
                    +0.660878777503967f, +0.000000000000001f, -1.069324374198914f, +0.0f,
                    +1.174316525459290f, +0.000000000000001f, -0.448549002408981f, +0.0f,
                    -0.725767493247986f, -0.725767493247986f, -0.725767493247986f, +0.0f,
                    -1.069324374198914f, -0.660878777503967f, -0.000000000000001f, +0.0f,
                    -0.000000000000001f, -0.448549002408981f, -1.174316525459290f, +0.0f,
                    -0.000000000000001f, -0.448549002408981f, +1.174316525459290f, +0.0f,
                    -0.725767493247986f, -0.725767493247986f, +0.725767493247986f, +0.0f,
                    +0.725767493247986f, -0.725767493247986f, +0.725767493247986f, +0.0f,
                    +1.069324374198914f, -0.660878777503967f, +0.000000000000001f, +0.0f,
                    +0.725767493247986f, -0.725767493247986f, -0.725767493247986f, +0.0f,
                    -0.000000000000004f, -1.069324374198914f, -0.660878777503967f, +0.0f,
                    -0.448549002408981f, -1.174316525459290f, -0.000000000000003f, +0.0f,
                    -0.000000000000003f, -1.069324374198914f, +0.660878777503967f, +0.0f,
                    +0.448549002408981f, -1.174316525459290f, +0.000000000000003f, +0.0f,
            };


    protected static final double[] GRAD_4D =
            {
                    -0.5875167f, +1.4183908f, +1.4183908f, +1.4183908f,
                    -0.5875167f, +1.4183908f, +1.4183908f, -1.4183908f,
                    -0.5875167f, +1.4183908f, -1.4183908f, +1.4183908f,
                    -0.5875167f, +1.4183908f, -1.4183908f, -1.4183908f,
                    -0.5875167f, -1.4183908f, +1.4183908f, +1.4183908f,
                    -0.5875167f, -1.4183908f, +1.4183908f, -1.4183908f,
                    -0.5875167f, -1.4183908f, -1.4183908f, +1.4183908f,
                    -0.5875167f, -1.4183908f, -1.4183908f, -1.4183908f,
                    +1.4183908f, -0.5875167f, +1.4183908f, +1.4183908f,
                    +1.4183908f, -0.5875167f, +1.4183908f, -1.4183908f,
                    +1.4183908f, -0.5875167f, -1.4183908f, +1.4183908f,
                    +1.4183908f, -0.5875167f, -1.4183908f, -1.4183908f,
                    -1.4183908f, -0.5875167f, +1.4183908f, +1.4183908f,
                    -1.4183908f, -0.5875167f, +1.4183908f, -1.4183908f,
                    -1.4183908f, -0.5875167f, -1.4183908f, +1.4183908f,
                    -1.4183908f, -0.5875167f, -1.4183908f, -1.4183908f,
                    +1.4183908f, +1.4183908f, -0.5875167f, +1.4183908f,
                    +1.4183908f, +1.4183908f, -0.5875167f, -1.4183908f,
                    +1.4183908f, -1.4183908f, -0.5875167f, +1.4183908f,
                    +1.4183908f, -1.4183908f, -0.5875167f, -1.4183908f,
                    -1.4183908f, +1.4183908f, -0.5875167f, +1.4183908f,
                    -1.4183908f, +1.4183908f, -0.5875167f, -1.4183908f,
                    -1.4183908f, -1.4183908f, -0.5875167f, +1.4183908f,
                    -1.4183908f, -1.4183908f, -0.5875167f, -1.4183908f,
                    +1.4183908f, +1.4183908f, +1.4183908f, -0.5875167f,
                    +1.4183908f, +1.4183908f, -1.4183908f, -0.5875167f,
                    +1.4183908f, -1.4183908f, +1.4183908f, -0.5875167f,
                    +1.4183908f, -1.4183908f, -1.4183908f, -0.5875167f,
                    -1.4183908f, +1.4183908f, +1.4183908f, -0.5875167f,
                    -1.4183908f, +1.4183908f, -1.4183908f, -0.5875167f,
                    -1.4183908f, -1.4183908f, +1.4183908f, -0.5875167f,
                    -1.4183908f, -1.4183908f, -1.4183908f, -0.5875167f,
                    +0.5875167f, +1.4183908f, +1.4183908f, +1.4183908f,
                    +0.5875167f, +1.4183908f, +1.4183908f, -1.4183908f,
                    +0.5875167f, +1.4183908f, -1.4183908f, +1.4183908f,
                    +0.5875167f, +1.4183908f, -1.4183908f, -1.4183908f,
                    +0.5875167f, -1.4183908f, +1.4183908f, +1.4183908f,
                    +0.5875167f, -1.4183908f, +1.4183908f, -1.4183908f,
                    +0.5875167f, -1.4183908f, -1.4183908f, +1.4183908f,
                    +0.5875167f, -1.4183908f, -1.4183908f, -1.4183908f,
                    +1.4183908f, +0.5875167f, +1.4183908f, +1.4183908f,
                    +1.4183908f, +0.5875167f, +1.4183908f, -1.4183908f,
                    +1.4183908f, +0.5875167f, -1.4183908f, +1.4183908f,
                    +1.4183908f, +0.5875167f, -1.4183908f, -1.4183908f,
                    -1.4183908f, +0.5875167f, +1.4183908f, +1.4183908f,
                    -1.4183908f, +0.5875167f, +1.4183908f, -1.4183908f,
                    -1.4183908f, +0.5875167f, -1.4183908f, +1.4183908f,
                    -1.4183908f, +0.5875167f, -1.4183908f, -1.4183908f,
                    +1.4183908f, +1.4183908f, +0.5875167f, +1.4183908f,
                    +1.4183908f, +1.4183908f, +0.5875167f, -1.4183908f,
                    +1.4183908f, -1.4183908f, +0.5875167f, +1.4183908f,
                    +1.4183908f, -1.4183908f, +0.5875167f, -1.4183908f,
                    -1.4183908f, +1.4183908f, +0.5875167f, +1.4183908f,
                    -1.4183908f, +1.4183908f, +0.5875167f, -1.4183908f,
                    -1.4183908f, -1.4183908f, +0.5875167f, +1.4183908f,
                    -1.4183908f, -1.4183908f, +0.5875167f, -1.4183908f,
                    +1.4183908f, +1.4183908f, +1.4183908f, +0.5875167f,
                    +1.4183908f, +1.4183908f, -1.4183908f, +0.5875167f,
                    +1.4183908f, -1.4183908f, +1.4183908f, +0.5875167f,
                    +1.4183908f, -1.4183908f, -1.4183908f, +0.5875167f,
                    -1.4183908f, +1.4183908f, +1.4183908f, +0.5875167f,
                    -1.4183908f, +1.4183908f, -1.4183908f, +0.5875167f,
                    -1.4183908f, -1.4183908f, +1.4183908f, +0.5875167f,
                    -1.4183908f, -1.4183908f, -1.4183908f, +0.5875167f,
            };

    protected static final double2[] CELL_2D =
            {
                    new double2(-0.4313539279f, 0.1281943404f), new double2(-0.1733316799f, 0.415278375f), new double2(-0.2821957395f, -0.3505218461f), new double2(-0.2806473808f, 0.3517627718f), new double2(0.3125508975f, -0.3237467165f), new double2(0.3383018443f, -0.2967353402f), new double2(-0.4393982022f, -0.09710417025f), new double2(-0.4460443703f, -0.05953502905f),
                    new double2(-0.302223039f, 0.3334085102f), new double2(-0.212681052f, -0.3965687458f), new double2(-0.2991156529f, 0.3361990872f), new double2(0.2293323691f, 0.3871778202f), new double2(0.4475439151f, -0.04695150755f), new double2(0.1777518f, 0.41340573f), new double2(0.1688522499f, -0.4171197882f), new double2(-0.0976597166f, 0.4392750616f),
                    new double2(0.08450188373f, 0.4419948321f), new double2(-0.4098760448f, -0.1857461384f), new double2(0.3476585782f, -0.2857157906f), new double2(-0.3350670039f, -0.30038326f), new double2(0.2298190031f, -0.3868891648f), new double2(-0.01069924099f, 0.449872789f), new double2(-0.4460141246f, -0.05976119672f), new double2(0.3650293864f, 0.2631606867f),
                    new double2(-0.349479423f, 0.2834856838f), new double2(-0.4122720642f, 0.1803655873f), new double2(-0.267327811f, 0.3619887311f), new double2(0.322124041f, -0.3142230135f), new double2(0.2880445931f, -0.3457315612f), new double2(0.3892170926f, -0.2258540565f), new double2(0.4492085018f, -0.02667811596f), new double2(-0.4497724772f, 0.01430799601f),
                    new double2(0.1278175387f, -0.4314657307f), new double2(-0.03572100503f, 0.4485799926f), new double2(-0.4297407068f, -0.1335025276f), new double2(-0.3217817723f, 0.3145735065f), new double2(-0.3057158873f, 0.3302087162f), new double2(-0.414503978f, 0.1751754899f), new double2(-0.3738139881f, 0.2505256519f), new double2(0.2236891408f, -0.3904653228f),
                    new double2(0.002967775577f, -0.4499902136f), new double2(0.1747128327f, -0.4146991995f), new double2(-0.4423772489f, -0.08247647938f), new double2(-0.2763960987f, -0.355112935f), new double2(-0.4019385906f, -0.2023496216f), new double2(0.3871414161f, -0.2293938184f), new double2(-0.430008727f, 0.1326367019f), new double2(-0.03037574274f, -0.4489736231f),
                    new double2(-0.3486181573f, 0.2845441624f), new double2(0.04553517144f, -0.4476902368f), new double2(-0.0375802926f, 0.4484280562f), new double2(0.3266408905f, 0.3095250049f), new double2(0.06540017593f, -0.4452222108f), new double2(0.03409025829f, 0.448706869f), new double2(-0.4449193635f, 0.06742966669f), new double2(-0.4255936157f, -0.1461850686f),
                    new double2(0.449917292f, 0.008627302568f), new double2(0.05242606404f, 0.4469356864f), new double2(-0.4495305179f, -0.02055026661f), new double2(-0.1204775703f, 0.4335725488f), new double2(-0.341986385f, -0.2924813028f), new double2(0.3865320182f, 0.2304191809f), new double2(0.04506097811f, -0.447738214f), new double2(-0.06283465979f, 0.4455915232f),
                    new double2(0.3932600341f, -0.2187385324f), new double2(0.4472261803f, -0.04988730975f), new double2(0.3753571011f, -0.2482076684f), new double2(-0.273662295f, 0.357223947f), new double2(0.1700461538f, 0.4166344988f), new double2(0.4102692229f, 0.1848760794f), new double2(0.323227187f, -0.3130881435f), new double2(-0.2882310238f, -0.3455761521f),
                    new double2(0.2050972664f, 0.4005435199f), new double2(0.4414085979f, -0.08751256895f), new double2(-0.1684700334f, 0.4172743077f), new double2(-0.003978032396f, 0.4499824166f), new double2(-0.2055133639f, 0.4003301853f), new double2(-0.006095674897f, -0.4499587123f), new double2(-0.1196228124f, -0.4338091548f), new double2(0.3901528491f, -0.2242337048f),
                    new double2(0.01723531752f, 0.4496698165f), new double2(-0.3015070339f, 0.3340561458f), new double2(-0.01514262423f, -0.4497451511f), new double2(-0.4142574071f, -0.1757577897f), new double2(-0.1916377265f, -0.4071547394f), new double2(0.3749248747f, 0.2488600778f), new double2(-0.2237774255f, 0.3904147331f), new double2(-0.4166343106f, -0.1700466149f),
                    new double2(0.3619171625f, 0.267424695f), new double2(0.1891126846f, -0.4083336779f), new double2(-0.3127425077f, 0.323561623f), new double2(-0.3281807787f, 0.307891826f), new double2(-0.2294806661f, 0.3870899429f), new double2(-0.3445266136f, 0.2894847362f), new double2(-0.4167095422f, -0.1698621719f), new double2(-0.257890321f, -0.3687717212f),
                    new double2(-0.3612037825f, 0.2683874578f), new double2(0.2267996491f, 0.3886668486f), new double2(0.207157062f, 0.3994821043f), new double2(0.08355176718f, -0.4421754202f), new double2(-0.4312233307f, 0.1286329626f), new double2(0.3257055497f, 0.3105090899f), new double2(0.177701095f, -0.4134275279f), new double2(-0.445182522f, 0.06566979625f),
                    new double2(0.3955143435f, 0.2146355146f), new double2(-0.4264613988f, 0.1436338239f), new double2(-0.3793799665f, -0.2420141339f), new double2(0.04617599081f, -0.4476245948f), new double2(-0.371405428f, -0.2540826796f), new double2(0.2563570295f, -0.3698392535f), new double2(0.03476646309f, 0.4486549822f), new double2(-0.3065454405f, 0.3294387544f),
                    new double2(-0.2256979823f, 0.3893076172f), new double2(0.4116448463f, -0.1817925206f), new double2(-0.2907745828f, -0.3434387019f), new double2(0.2842278468f, -0.348876097f), new double2(0.3114589359f, -0.3247973695f), new double2(0.4464155859f, -0.0566844308f), new double2(-0.3037334033f, -0.3320331606f), new double2(0.4079607166f, 0.1899159123f),
                    new double2(-0.3486948919f, -0.2844501228f), new double2(0.3264821436f, 0.3096924441f), new double2(0.3211142406f, 0.3152548881f), new double2(0.01183382662f, 0.4498443737f), new double2(0.4333844092f, 0.1211526057f), new double2(0.3118668416f, 0.324405723f), new double2(-0.272753471f, 0.3579183483f), new double2(-0.422228622f, -0.1556373694f),
                    new double2(-0.1009700099f, -0.4385260051f), new double2(-0.2741171231f, -0.3568750521f), new double2(-0.1465125133f, 0.4254810025f), new double2(0.2302279044f, -0.3866459777f), new double2(-0.3699435608f, 0.2562064828f), new double2(0.105700352f, -0.4374099171f), new double2(-0.2646713633f, 0.3639355292f), new double2(0.3521828122f, 0.2801200935f),
                    new double2(-0.1864187807f, -0.4095705534f), new double2(0.1994492955f, -0.4033856449f), new double2(0.3937065066f, 0.2179339044f), new double2(-0.3226158377f, 0.3137180602f), new double2(0.3796235338f, 0.2416318948f), new double2(0.1482921929f, 0.4248640083f), new double2(-0.407400394f, 0.1911149365f), new double2(0.4212853031f, 0.1581729856f),
                    new double2(-0.2621297173f, 0.3657704353f), new double2(-0.2536986953f, -0.3716678248f), new double2(-0.2100236383f, 0.3979825013f), new double2(0.3624152444f, 0.2667493029f), new double2(-0.3645038479f, -0.2638881295f), new double2(0.2318486784f, 0.3856762766f), new double2(-0.3260457004f, 0.3101519002f), new double2(-0.2130045332f, -0.3963950918f),
                    new double2(0.3814998766f, -0.2386584257f), new double2(-0.342977305f, 0.2913186713f), new double2(-0.4355865605f, 0.1129794154f), new double2(-0.2104679605f, 0.3977477059f), new double2(0.3348364681f, -0.3006402163f), new double2(0.3430468811f, 0.2912367377f), new double2(-0.2291836801f, -0.3872658529f), new double2(0.2547707298f, -0.3709337882f),
                    new double2(0.4236174945f, -0.151816397f), new double2(-0.15387742f, 0.4228731957f), new double2(-0.4407449312f, 0.09079595574f), new double2(-0.06805276192f, -0.444824484f), new double2(0.4453517192f, -0.06451237284f), new double2(0.2562464609f, -0.3699158705f), new double2(0.3278198355f, -0.3082761026f), new double2(-0.4122774207f, -0.1803533432f),
                    new double2(0.3354090914f, -0.3000012356f), new double2(0.446632869f, -0.05494615882f), new double2(-0.1608953296f, 0.4202531296f), new double2(-0.09463954939f, 0.4399356268f), new double2(-0.02637688324f, -0.4492262904f), new double2(0.447102804f, -0.05098119915f), new double2(-0.4365670908f, 0.1091291678f), new double2(-0.3959858651f, 0.2137643437f),
                    new double2(-0.4240048207f, -0.1507312575f), new double2(-0.3882794568f, 0.2274622243f), new double2(-0.4283652566f, -0.1378521198f), new double2(0.3303888091f, 0.305521251f), new double2(0.3321434919f, -0.3036127481f), new double2(-0.413021046f, -0.1786438231f), new double2(0.08403060337f, -0.4420846725f), new double2(-0.3822882919f, 0.2373934748f),
                    new double2(-0.3712395594f, -0.2543249683f), new double2(0.4472363971f, -0.04979563372f), new double2(-0.4466591209f, 0.05473234629f), new double2(0.0486272539f, -0.4473649407f), new double2(-0.4203101295f, -0.1607463688f), new double2(0.2205360833f, 0.39225481f), new double2(-0.3624900666f, 0.2666476169f), new double2(-0.4036086833f, -0.1989975647f),
                    new double2(0.2152727807f, 0.3951678503f), new double2(-0.4359392962f, -0.1116106179f), new double2(0.4178354266f, 0.1670735057f), new double2(0.2007630161f, 0.4027334247f), new double2(-0.07278067175f, -0.4440754146f), new double2(0.3644748615f, -0.2639281632f), new double2(-0.4317451775f, 0.126870413f), new double2(-0.297436456f, 0.3376855855f),
                    new double2(-0.2998672222f, 0.3355289094f), new double2(-0.2673674124f, 0.3619594822f), new double2(0.2808423357f, 0.3516071423f), new double2(0.3498946567f, 0.2829730186f), new double2(-0.2229685561f, 0.390877248f), new double2(0.3305823267f, 0.3053118493f), new double2(-0.2436681211f, -0.3783197679f), new double2(-0.03402776529f, 0.4487116125f),
                    new double2(-0.319358823f, 0.3170330301f), new double2(0.4454633477f, -0.06373700535f), new double2(0.4483504221f, 0.03849544189f), new double2(-0.4427358436f, -0.08052932871f), new double2(0.05452298565f, 0.4466847255f), new double2(-0.2812560807f, 0.3512762688f), new double2(0.1266696921f, 0.4318041097f), new double2(-0.3735981243f, 0.2508474468f),
                    new double2(0.2959708351f, -0.3389708908f), new double2(-0.3714377181f, 0.254035473f), new double2(-0.404467102f, -0.1972469604f), new double2(0.1636165687f, -0.419201167f), new double2(0.3289185495f, -0.3071035458f), new double2(-0.2494824991f, -0.3745109914f), new double2(0.03283133272f, 0.4488007393f), new double2(-0.166306057f, -0.4181414777f),
                    new double2(-0.106833179f, 0.4371346153f), new double2(0.06440260376f, -0.4453676062f), new double2(-0.4483230967f, 0.03881238203f), new double2(-0.421377757f, -0.1579265206f), new double2(0.05097920662f, -0.4471030312f), new double2(0.2050584153f, -0.4005634111f), new double2(0.4178098529f, -0.167137449f), new double2(-0.3565189504f, -0.2745801121f),
                    new double2(0.4478398129f, 0.04403977727f), new double2(-0.3399999602f, -0.2947881053f), new double2(0.3767121994f, 0.2461461331f), new double2(-0.3138934434f, 0.3224451987f), new double2(-0.1462001792f, -0.4255884251f), new double2(0.3970290489f, -0.2118205239f), new double2(0.4459149305f, -0.06049689889f), new double2(-0.4104889426f, -0.1843877112f),
                    new double2(0.1475103971f, -0.4251360756f), new double2(0.09258030352f, 0.4403735771f), new double2(-0.1589664637f, -0.4209865359f), new double2(0.2482445008f, 0.3753327428f), new double2(0.4383624232f, -0.1016778537f), new double2(0.06242802956f, 0.4456486745f), new double2(0.2846591015f, -0.3485243118f), new double2(-0.344202744f, -0.2898697484f),
                    new double2(0.1198188883f, -0.4337550392f), new double2(-0.243590703f, 0.3783696201f), new double2(0.2958191174f, -0.3391033025f), new double2(-0.1164007991f, 0.4346847754f), new double2(0.1274037151f, -0.4315881062f), new double2(0.368047306f, 0.2589231171f), new double2(0.2451436949f, 0.3773652989f), new double2(-0.4314509715f, 0.12786735f),
            };

    protected static final double3[] CELL_3D =
            {
                    new double3(0.1453787434f, -0.4149781685f, -0.0956981749f), new double3(-0.01242829687f, -0.1457918398f, -0.4255470325f), new double3(0.2877979582f, -0.02606483451f, -0.3449535616f), new double3(-0.07732986802f, 0.2377094325f, 0.3741848704f), new double3(0.1107205875f, -0.3552302079f, -0.2530858567f), new double3(0.2755209141f, 0.2640521179f, -0.238463215f), new double3(0.294168941f, 0.1526064594f, 0.3044271714f), new double3(0.4000921098f, -0.2034056362f, 0.03244149937f),
                    new double3(-0.1697304074f, 0.3970864695f, -0.1265461359f), new double3(-0.1483224484f, -0.3859694688f, 0.1775613147f), new double3(0.2623596946f, -0.2354852944f, 0.2796677792f), new double3(-0.2709003183f, 0.3505271138f, -0.07901746678f), new double3(-0.03516550699f, 0.3885234328f, 0.2243054374f), new double3(-0.1267712655f, 0.1920044036f, 0.3867342179f), new double3(0.02952021915f, 0.4409685861f, 0.08470692262f), new double3(-0.2806854217f, -0.266996757f, 0.2289725438f),
                    new double3(-0.171159547f, 0.2141185563f, 0.3568720405f), new double3(0.2113227183f, 0.3902405947f, -0.07453178509f), new double3(-0.1024352839f, 0.2128044156f, -0.3830421561f), new double3(-0.3304249877f, -0.1566986703f, 0.2622305365f), new double3(0.2091111325f, 0.3133278055f, -0.2461670583f), new double3(0.344678154f, -0.1944240454f, -0.2142341261f), new double3(0.1984478035f, -0.3214342325f, -0.2445373252f), new double3(-0.2929008603f, 0.2262915116f, 0.2559320961f),
                    new double3(-0.1617332831f, 0.006314769776f, -0.4198838754f), new double3(-0.3582060271f, -0.148303178f, -0.2284613961f), new double3(-0.1852067326f, -0.3454119342f, -0.2211087107f), new double3(0.3046301062f, 0.1026310383f, 0.314908508f), new double3(-0.03816768434f, -0.2551766358f, -0.3686842991f), new double3(-0.4084952196f, 0.1805950793f, 0.05492788837f), new double3(-0.02687443361f, -0.2749741471f, 0.3551999201f), new double3(-0.03801098351f, 0.3277859044f, 0.3059600725f),
                    new double3(0.2371120802f, 0.2900386767f, -0.2493099024f), new double3(0.4447660503f, 0.03946930643f, 0.05590469027f), new double3(0.01985147278f, -0.01503183293f, -0.4493105419f), new double3(0.4274339143f, 0.03345994256f, -0.1366772882f), new double3(-0.2072988631f, 0.2871414597f, -0.2776273824f), new double3(-0.3791240978f, 0.1281177671f, 0.2057929936f), new double3(-0.2098721267f, -0.1007087278f, -0.3851122467f), new double3(0.01582798878f, 0.4263894424f, 0.1429738373f),
                    new double3(-0.1888129464f, -0.3160996813f, -0.2587096108f), new double3(0.1612988974f, -0.1974805082f, -0.3707885038f), new double3(-0.08974491322f, 0.229148752f, -0.3767448739f), new double3(0.07041229526f, 0.4150230285f, -0.1590534329f), new double3(-0.1082925611f, -0.1586061639f, 0.4069604477f), new double3(0.2474100658f, -0.3309414609f, 0.1782302128f), new double3(-0.1068836661f, -0.2701644537f, -0.3436379634f), new double3(0.2396452163f, 0.06803600538f, -0.3747549496f),
                    new double3(-0.3063886072f, 0.2597428179f, 0.2028785103f), new double3(0.1593342891f, -0.3114350249f, -0.2830561951f), new double3(0.2709690528f, 0.1412648683f, -0.3303331794f), new double3(-0.1519780427f, 0.3623355133f, 0.2193527988f), new double3(0.1699773681f, 0.3456012883f, 0.2327390037f), new double3(-0.1986155616f, 0.3836276443f, -0.1260225743f), new double3(-0.1887482106f, -0.2050154888f, -0.353330953f), new double3(0.2659103394f, 0.3015631259f, -0.2021172246f),
                    new double3(-0.08838976154f, -0.4288819642f, -0.1036702021f), new double3(-0.04201869311f, 0.3099592485f, 0.3235115047f), new double3(-0.3230334656f, 0.201549922f, -0.2398478873f), new double3(0.2612720941f, 0.2759854499f, -0.2409749453f), new double3(0.385713046f, 0.2193460345f, 0.07491837764f), new double3(0.07654967953f, 0.3721732183f, 0.241095919f), new double3(0.4317038818f, -0.02577753072f, 0.1243675091f), new double3(-0.2890436293f, -0.3418179959f, -0.04598084447f),
                    new double3(-0.2201947582f, 0.383023377f, -0.08548310451f), new double3(0.4161322773f, -0.1669634289f, -0.03817251927f), new double3(0.2204718095f, 0.02654238946f, -0.391391981f), new double3(-0.1040307469f, 0.3890079625f, -0.2008741118f), new double3(-0.1432122615f, 0.371614387f, -0.2095065525f), new double3(0.3978380468f, -0.06206669342f, 0.2009293758f), new double3(-0.2599274663f, 0.2616724959f, -0.2578084893f), new double3(0.4032618332f, -0.1124593585f, 0.1650235939f),
                    new double3(-0.08953470255f, -0.3048244735f, 0.3186935478f), new double3(0.118937202f, -0.2875221847f, 0.325092195f), new double3(0.02167047076f, -0.03284630549f, -0.4482761547f), new double3(-0.3411343612f, 0.2500031105f, 0.1537068389f), new double3(0.3162964612f, 0.3082064153f, -0.08640228117f), new double3(0.2355138889f, -0.3439334267f, -0.1695376245f), new double3(-0.02874541518f, -0.3955933019f, 0.2125550295f), new double3(-0.2461455173f, 0.02020282325f, -0.3761704803f),
                    new double3(0.04208029445f, -0.4470439576f, 0.02968078139f), new double3(0.2727458746f, 0.2288471896f, -0.2752065618f), new double3(-0.1347522818f, -0.02720848277f, -0.4284874806f), new double3(0.3829624424f, 0.1231931484f, -0.2016512234f), new double3(-0.3547613644f, 0.1271702173f, 0.2459107769f), new double3(0.2305790207f, 0.3063895591f, 0.2354968222f), new double3(-0.08323845599f, -0.1922245118f, 0.3982726409f), new double3(0.2993663085f, -0.2619918095f, -0.2103333191f),
                    new double3(-0.2154865723f, 0.2706747713f, 0.287751117f), new double3(0.01683355354f, -0.2680655787f, -0.3610505186f), new double3(0.05240429123f, 0.4335128183f, -0.1087217856f), new double3(0.00940104872f, -0.4472890582f, 0.04841609928f), new double3(0.3465688735f, 0.01141914583f, -0.2868093776f), new double3(-0.3706867948f, -0.2551104378f, 0.003156692623f), new double3(0.2741169781f, 0.2139972417f, -0.2855959784f), new double3(0.06413433865f, 0.1708718512f, 0.4113266307f),
                    new double3(-0.388187972f, -0.03973280434f, -0.2241236325f), new double3(0.06419469312f, -0.2803682491f, 0.3460819069f), new double3(-0.1986120739f, -0.3391173584f, 0.2192091725f), new double3(-0.203203009f, -0.3871641506f, 0.1063600375f), new double3(-0.1389736354f, -0.2775901578f, -0.3257760473f), new double3(-0.06555641638f, 0.342253257f, -0.2847192729f), new double3(-0.2529246486f, -0.2904227915f, 0.2327739768f), new double3(0.1444476522f, 0.1069184044f, 0.4125570634f),
                    new double3(-0.3643780054f, -0.2447099973f, -0.09922543227f), new double3(0.4286142488f, -0.1358496089f, -0.01829506817f), new double3(0.165872923f, -0.3136808464f, -0.2767498872f), new double3(0.2219610524f, -0.3658139958f, 0.1393320198f), new double3(0.04322940318f, -0.3832730794f, 0.2318037215f), new double3(-0.08481269795f, -0.4404869674f, -0.03574965489f), new double3(0.1822082075f, -0.3953259299f, 0.1140946023f), new double3(-0.3269323334f, 0.3036542563f, 0.05838957105f),
                    new double3(-0.4080485344f, 0.04227858267f, -0.184956522f), new double3(0.2676025294f, -0.01299671652f, 0.36155217f), new double3(0.3024892441f, -0.1009990293f, -0.3174892964f), new double3(0.1448494052f, 0.425921681f, -0.0104580805f), new double3(0.4198402157f, 0.08062320474f, 0.1404780841f), new double3(-0.3008872161f, -0.333040905f, -0.03241355801f), new double3(0.3639310428f, -0.1291284382f, -0.2310412139f), new double3(0.3295806598f, 0.0184175994f, -0.3058388149f),
                    new double3(0.2776259487f, -0.2974929052f, -0.1921504723f), new double3(0.4149000507f, -0.144793182f, -0.09691688386f), new double3(0.145016715f, -0.0398992945f, 0.4241205002f), new double3(0.09299023471f, -0.299732164f, -0.3225111565f), new double3(0.1028907093f, -0.361266869f, 0.247789732f), new double3(0.2683057049f, -0.07076041213f, -0.3542668666f), new double3(-0.4227307273f, -0.07933161816f, -0.1323073187f), new double3(-0.1781224702f, 0.1806857196f, -0.3716517945f),
                    new double3(0.4390788626f, -0.02841848598f, -0.09435116353f), new double3(0.2972583585f, 0.2382799621f, -0.2394997452f), new double3(-0.1707002821f, 0.2215845691f, 0.3525077196f), new double3(0.3806686614f, 0.1471852559f, -0.1895464869f), new double3(-0.1751445661f, -0.274887877f, 0.3102596268f), new double3(-0.2227237566f, -0.2316778837f, 0.3149912482f), new double3(0.1369633021f, 0.1341343041f, -0.4071228836f), new double3(-0.3529503428f, -0.2472893463f, -0.129514612f),
                    new double3(-0.2590744185f, -0.2985577559f, -0.2150435121f), new double3(-0.3784019401f, 0.2199816631f, -0.1044989934f), new double3(-0.05635805671f, 0.1485737441f, 0.4210102279f), new double3(0.3251428613f, 0.09666046873f, -0.2957006485f), new double3(-0.4190995804f, 0.1406751354f, -0.08405978803f), new double3(-0.3253150961f, -0.3080335042f, -0.04225456877f), new double3(0.2857945863f, -0.05796152095f, 0.3427271751f), new double3(-0.2733604046f, 0.1973770973f, -0.2980207554f),
                    new double3(0.219003657f, 0.2410037886f, -0.3105713639f), new double3(0.3182767252f, -0.271342949f, 0.1660509868f), new double3(-0.03222023115f, -0.3331161506f, -0.300824678f), new double3(-0.3087780231f, 0.1992794134f, -0.2596995338f), new double3(-0.06487611647f, -0.4311322747f, 0.1114273361f), new double3(0.3921171432f, -0.06294284106f, -0.2116183942f), new double3(-0.1606404506f, -0.358928121f, -0.2187812825f), new double3(-0.03767771199f, -0.2290351443f, 0.3855169162f),
                    new double3(0.1394866832f, -0.3602213994f, 0.2308332918f), new double3(-0.4345093872f, 0.005751117145f, 0.1169124335f), new double3(-0.1044637494f, 0.4168128432f, -0.1336202785f), new double3(0.2658727501f, 0.2551943237f, 0.2582393035f), new double3(0.2051461999f, 0.1975390727f, 0.3484154868f), new double3(-0.266085566f, 0.23483312f, 0.2766800993f), new double3(0.07849405464f, -0.3300346342f, -0.2956616708f), new double3(-0.2160686338f, 0.05376451292f, -0.3910546287f),
                    new double3(-0.185779186f, 0.2148499206f, 0.3490352499f), new double3(0.02492421743f, -0.3229954284f, -0.3123343347f), new double3(-0.120167831f, 0.4017266681f, 0.1633259825f), new double3(-0.02160084693f, -0.06885389554f, 0.4441762538f), new double3(0.2597670064f, 0.3096300784f, 0.1978643903f), new double3(-0.1611553854f, -0.09823036005f, 0.4085091653f), new double3(-0.3278896792f, 0.1461670309f, 0.2713366126f), new double3(0.2822734956f, 0.03754421121f, -0.3484423997f),
                    new double3(0.03169341113f, 0.347405252f, -0.2842624114f), new double3(0.2202613604f, -0.3460788041f, -0.1849713341f), new double3(0.2933396046f, 0.3031973659f, 0.1565989581f), new double3(-0.3194922995f, 0.2453752201f, -0.200538455f), new double3(-0.3441586045f, -0.1698856132f, -0.2349334659f), new double3(0.2703645948f, -0.3574277231f, 0.04060059933f), new double3(0.2298568861f, 0.3744156221f, 0.0973588921f), new double3(0.09326603877f, -0.3170108894f, 0.3054595587f),
                    new double3(-0.1116165319f, -0.2985018719f, 0.3177080142f), new double3(0.2172907365f, -0.3460005203f, -0.1885958001f), new double3(0.1991339479f, 0.3820341668f, -0.1299829458f), new double3(-0.0541918155f, -0.2103145071f, 0.39412061f), new double3(0.08871336998f, 0.2012117383f, 0.3926114802f), new double3(0.2787673278f, 0.3505404674f, 0.04370535101f), new double3(-0.322166438f, 0.3067213525f, 0.06804996813f), new double3(-0.4277366384f, 0.132066775f, 0.04582286686f),
                    new double3(0.240131882f, -0.1612516055f, 0.344723946f), new double3(0.1448607981f, -0.2387819045f, 0.3528435224f), new double3(-0.3837065682f, -0.2206398454f, 0.08116235683f), new double3(-0.4382627882f, -0.09082753406f, -0.04664855374f), new double3(-0.37728353f, 0.05445141085f, 0.2391488697f), new double3(0.1259579313f, 0.348394558f, 0.2554522098f), new double3(-0.1406285511f, -0.270877371f, -0.3306796947f), new double3(-0.1580694418f, 0.4162931958f, -0.06491553533f),
                    new double3(0.2477612106f, -0.2927867412f, -0.2353514536f), new double3(0.2916132853f, 0.3312535401f, 0.08793624968f), new double3(0.07365265219f, -0.1666159848f, 0.411478311f), new double3(-0.26126526f, -0.2422237692f, 0.2748965434f), new double3(-0.3721862032f, 0.252790166f, 0.008634938242f), new double3(-0.3691191571f, -0.255281188f, 0.03290232422f), new double3(0.2278441737f, -0.3358364886f, 0.1944244981f), new double3(0.363398169f, -0.2310190248f, 0.1306597909f),
                    new double3(-0.304231482f, -0.2698452035f, 0.1926830856f), new double3(-0.3199312232f, 0.316332536f, -0.008816977938f), new double3(0.2874852279f, 0.1642275508f, -0.304764754f), new double3(-0.1451096801f, 0.3277541114f, -0.2720669462f), new double3(0.3220090754f, 0.0511344108f, 0.3101538769f), new double3(-0.1247400865f, -0.04333605335f, -0.4301882115f), new double3(-0.2829555867f, -0.3056190617f, -0.1703910946f), new double3(0.1069384374f, 0.3491024667f, -0.2630430352f),
                    new double3(-0.1420661144f, -0.3055376754f, -0.2982682484f), new double3(-0.250548338f, 0.3156466809f, -0.2002316239f), new double3(0.3265787872f, 0.1871229129f, 0.2466400438f), new double3(0.07646097258f, -0.3026690852f, 0.324106687f), new double3(0.3451771584f, 0.2757120714f, -0.0856480183f), new double3(0.298137964f, 0.2852657134f, 0.179547284f), new double3(0.2812250376f, 0.3466716415f, 0.05684409612f), new double3(0.4390345476f, -0.09790429955f, -0.01278335452f),
                    new double3(0.2148373234f, 0.1850172527f, 0.3494474791f), new double3(0.2595421179f, -0.07946825393f, 0.3589187731f), new double3(0.3182823114f, -0.307355516f, -0.08203022006f), new double3(-0.4089859285f, -0.04647718411f, 0.1818526372f), new double3(-0.2826749061f, 0.07417482322f, 0.3421885344f), new double3(0.3483864637f, 0.225442246f, -0.1740766085f), new double3(-0.3226415069f, -0.1420585388f, -0.2796816575f), new double3(0.4330734858f, -0.118868561f, -0.02859407492f),
                    new double3(-0.08717822568f, -0.3909896417f, -0.2050050172f), new double3(-0.2149678299f, 0.3939973956f, -0.03247898316f), new double3(-0.2687330705f, 0.322686276f, -0.1617284888f), new double3(0.2105665099f, -0.1961317136f, -0.3459683451f), new double3(0.4361845915f, -0.1105517485f, 0.004616608544f), new double3(0.05333333359f, -0.313639498f, -0.3182543336f), new double3(-0.05986216652f, 0.1361029153f, -0.4247264031f), new double3(0.3664988455f, 0.2550543014f, -0.05590974511f),
                    new double3(-0.2341015558f, -0.182405731f, 0.3382670703f), new double3(-0.04730947785f, -0.4222150243f, -0.1483114513f), new double3(-0.2391566239f, -0.2577696514f, -0.2808182972f), new double3(-0.1242081035f, 0.4256953395f, -0.07652336246f), new double3(0.2614832715f, -0.3650179274f, 0.02980623099f), new double3(-0.2728794681f, -0.3499628774f, 0.07458404908f), new double3(0.007892900508f, -0.1672771315f, 0.4176793787f), new double3(-0.01730330376f, 0.2978486637f, -0.3368779738f),
                    new double3(0.2054835762f, -0.3252600376f, -0.2334146693f), new double3(-0.3231994983f, 0.1564282844f, -0.2712420987f), new double3(-0.2669545963f, 0.2599343665f, -0.2523278991f), new double3(-0.05554372779f, 0.3170813944f, -0.3144428146f), new double3(-0.2083935713f, -0.310922837f, -0.2497981362f), new double3(0.06989323478f, -0.3156141536f, 0.3130537363f), new double3(0.3847566193f, -0.1605309138f, -0.1693876312f), new double3(-0.3026215288f, -0.3001537679f, -0.1443188342f),
                    new double3(0.3450735512f, 0.08611519592f, 0.2756962409f), new double3(0.1814473292f, -0.2788782453f, -0.3029914042f), new double3(-0.03855010448f, 0.09795110726f, 0.4375151083f), new double3(0.3533670318f, 0.2665752752f, 0.08105160988f), new double3(-0.007945601311f, 0.140359426f, -0.4274764309f), new double3(0.4063099273f, -0.1491768253f, -0.1231199324f), new double3(-0.2016773589f, 0.008816271194f, -0.4021797064f), new double3(-0.07527055435f, -0.425643481f, -0.1251477955f),
            };


    public static int BASE_SEED0 = 0x12345678;
    public static int BASE_SEED1 = 0xdeadbeaf;
    public static int BASE_SEED2 = 0xcafeaffe;
    public static int BASE_SEED3 = 0xd1ceB33f;
    public static int BASE_OCTAVES = 8;
    public static double BASE_MUTATION = 0f;
    public static double BASE_SHARPNESS = .987654321;
    public static double BASE_FREQUENCY = 1.3125;
    public static double BASE_LACUNARITY = 1./1.987654321;
    public static double BASE_HARSHNESS = (double) HARSH;
    public static double BASE_H = 1.987654321;
    public static double BASE_GAIN = 0.54321;
    public static double BASE_OFFSET = 0f;
    public static boolean BASE_SEED_VARIATION = false;


    public static int collatzIteration(int value, int steps) {
        for(int _i=0; _i<steps; _i++)
        {
            if((value & 1)==1)
            {
                value = (((3 * value) +1) >> 1);
            }
            else
            {
                value >>= 1;
            }
        }
        return value;
    }

    public static double collatzMutation(double value, int resolution)
    {
        boolean minus = value < 0.;
        double _scale = Math.abs(value)*resolution;
        int _low = fastFloor(_scale);
        int _hi = _low+1;
        double _delta = _scale - _low;

        double _low_d = collatzIteration(_low, 7)/((double)resolution);
        double _hi_d = collatzIteration(_hi, 7)/((double)resolution);

        double _val = lerp(_low_d,_hi_d,_delta);

        return (minus ? -_val : _val);
    }

    public static double collatzMutation(double value, int resolution, int steps)
    {
        boolean minus = value < 0.;
        double _scale = Math.abs(value)*resolution;
        int _low = fastFloor(_scale);
        int _hi = _low+1;
        double _delta = _scale - _low;

        double _low_d = collatzIteration(_low, steps)/((double)resolution);
        double _hi_d = collatzIteration(_hi, steps)/((double)resolution);

        double _val = lerp(_low_d,_hi_d,_delta);

        return (minus ? -_val : _val);
    }

    public static int clampValue(int value, int lowerBound, int upperBound) {
        if (value < lowerBound) {
            return lowerBound;
        } else if (value > upperBound) {
            return upperBound;
        } else {
            return value;
        }
    }

    public static double clampValue(double value, double lowerBound, double upperBound)
    {
        if (value < lowerBound)
        {
            return lowerBound;
        }
        else if (value > upperBound)
        {
            return upperBound;
        }
        else
        {
            return value;
        }
    }

    public static Color lerp(Color a, Color b, double percent) {
        int red = lerp(a.getRed(), b.getRed(), percent);
        int blue = lerp(a.getBlue(), b.getBlue(), percent);
        int green = lerp(a.getGreen(), b.getGreen(), percent);
        int alpha = lerp(a.getAlpha(), b.getAlpha(), percent);
        return new Color(red, green, blue, alpha);
    }

    public static int checkInt(String _str)
    {
        if(_str==null)
        {
            return -1;
        }
        _str = _str.trim();

        if(_str.length()==0) return 0;

        if(_str.startsWith("0x"))
        {
            return Integer.parseInt(_str.substring(2),16);
        }
        return Integer.parseInt(_str);
    }

    public static Integer checkInteger(final Object _str)
    {
        if(_str==null)
        {
            return -1;
        }

        return checkInteger(_str.toString());
    }

    public static final double dot(double2 a,double2 b)
    {
        return a.x*b.x + a.y*b.y;
    }

    public static final double dot(double ax,double ay,double bx,double by)
    {
        return ax*bx + ay*by;
    }

    public static final double dot(double3 a,double3 b)
    {
        return a.x*b.x + a.y*b.y + a.z*b.z;
    }

    public static final double dot(double ax,double ay,double az,double bx,double by,double bz)
    {
        return ax*bx + ay*by + az*bz;
    }

    public static final double dot(double4 a,double4 b)
    {
        return a.x*b.x + a.y*b.y + a.z*b.z + a.w*b.w;
    }

    public static final double dot(double ax,double ay,double az,double aw,double bx,double by,double bz,double bw)
    {
        return ax*bx + ay*by + az*bz + aw*bw;
    }

    public static double smoothstep(double edge0, double edge1, double x) {
        x = clamp((x - edge0)/(edge1 - edge0), 0., 1.);
        return quadLerp(0., 1., x);
    }

    public static double[] smoothstep(double edge0, double edge1, double[] x) {
        double[] y = new double[x.length];
        for(int _i=0; _i<x.length; _i++)
        {
            y[_i] = smoothstep(edge0, edge1, x[_i]);
        }
        return y;
    }

    public static double smootherstep(double edge0, double edge1, double x) {
        x = clamp((x - edge0)/(edge1 - edge0), 0., 1.);
        return quinticLerp(0., 1., x);
    }

    public static double[] smootherstep(double edge0, double edge1, double[] x) {
        double[] y = new double[x.length];
        for(int _i=0; _i<x.length; _i++)
        {
            y[_i] = smootherstep(edge0, edge1, x[_i]);
        }
        return y;
    }

    public static double clamp(double x, double edge0, double edge1) {
        if(x<edge0) return edge0;
        if(x>edge1) return edge1;
        return x;
    }

    public static double[] clamp(double[] x, double edge0, double edge1)
    {
        double[] y = new double[x.length];
        for(int _i=0; _i<x.length; _i++)
        {
            y[_i] = clamp(x[_i], edge0, edge1);
        }
        return y;
    }
    public static final double3 cross(double3 a, double3 b)
    {
        return new double3(
                a.y * b.z - a.z * b.y,
                a.z * b.x - a.x * b.z,
                a.x * b.y - a.y * b.x
        );
    }

    public static final double3 cross(double ax,double ay,double az,double bx,double by,double bz)
    {
        return new double3(
                ay * bz - az * by,
                az * bx - ax * bz,
                ax * by - ay * bx
        );
    }

    public static final double3 lerp(double ax,double ay,double az,double bx,double by,double bz, double w)
    {
        return new double3(
                lerp(ax, bx, w),
                lerp(ay, by, w),
                lerp(az, bz, w)
        );
    }

    public static final double4 lerp(double ax,double ay,double az,double aw,double bx,double by,double bz,double bw, double w)
    {
        return new double4(
                lerp(ax, bx, w),
                lerp(ay, by, w),
                lerp(az, bz, w),
                lerp(aw, bw, w)
        );
    }

    public static final double2 lerp(double ax,double ay,double bx,double by, double w)
    {
        return new double2(
                lerp(ax, bx, w),
                lerp(ay, by, w)
        );
    }

    public static final double4 lerp(double4 a,double4 b, double w)
    {
        return new double4(
                lerp(a.x, b.x, w),
                lerp(a.y, b.y, w),
                lerp(a.z, b.z, w),
                lerp(a.w, b.w, w)
        );
    }

    public static final double3 lerp(double3 a,double3 b, double w)
    {
        return new double3(
                lerp(a.x, b.x, w),
                lerp(a.y, b.y, w),
                lerp(a.z, b.z, w)
        );
    }

    public static final double2 lerp(double2 a,double2 b, double w)
    {
        return new double2(
                lerp(a.x, b.x, w),
                lerp(a.y, b.y, w)
        );
    }

    public static final double lerp(double2 a, double w)
    {
        return lerp(a.x, a.y, w);
    }

    /**
     * Calculate Weierstrass function for given x, a, and b
     */
    public static final double weierstrass(double _a,double _b,double _x)
    {
        if (_b < 5.) _b = 5.;

        if (_a > 1.) _a = 1.;
        // Because 0 < a < 1, that value becomes smaller as n grows larger. For example,
        // if a = 0.9, pow(a, 100) is around 0.000027, so that term adds little to the total.
        // Because terms with larger values of n don't contribute much to the total, only
        // first 100 terms are used.

        double _y = 0;
        for(int _n = 0; _n < 100; _n++)
        {
            double _c = Math.cos(Math.pow(_b, _n) * Math.PI * _x);
            if ((_c > 1) || (_c < -1))
                _c = 0;
            _y += Math.pow(_a, _n) * _c;
        }

        return _y;
    }

    public static final double inverseNormalization(double _x)
    {
        boolean minus = _x < 0.;
        double _scale = Math.abs(_x);
        _scale = 2.*(_scale/(1.+_scale));
        return minus ? -_scale : _scale;
    }

    /**
     * Redistributes a noise value {@code n} using the given {@code mul}, {@code mix}, and {@code bias} parameters. This
     * is meant to push high-octave noise results from being centrally biased to being closer to uniform. Getting the
     * values right probably requires tweaking them manually; for {@link SimplexNoise}, using mul=2.3f, mix=0.75f, and
     * bias=1f works well with 2 or more octaves (and not at all well for one octave, which can use mix=0.0f to avoid
     * redistributing at all). This variation takes n in the -1f to 1f range, inclusive, and returns a value in the same
     * range. You can give different bias values at different times to make noise that is more often high (when bias is
     * above 1) or low (when bias is between 0 and 1). Using negative bias has undefined results. Bias should typically
     * be calculated only when its value needs to change. If you have a variable {@code favor} that can have
     * any float value and high values for favor should produce higher results from this function, you can get bias with
     * {@code bias = (float)Math.exp(-favor);} .
     * @param n a prepared noise value, between -1f and 1f inclusive
     * @param mul a positive multiplier where higher values make extreme results more likely; often around 2.3f
     * @param mix a blending amount between 0f and 1f where lower values keep {@code n} more; often around 0.75f
     * @param bias should be 1 to have no bias, between 0 and 1 for lower results, and above 1 for higher results
     * @return a noise value between -1f and 1f, inclusive
     */
    public static double redistribute(double n, double mul, double mix, double bias)
    {
        final double xx = n * n * mul, axx = 0.1400122886866665 * xx;
        final double denormal = Math.copySign((float) Math.sqrt(1.0 - Math.exp(xx * (-1.2732395447351628 - axx) / (1.0 + axx))), n);
        return (Math.pow(lerp(n, denormal, mix) * 0.5 + 0.5, bias) - 0.5) * 2.;
    }
    /**
     * Minkowski's question-mark function
     */
    public static final double minkowskiQMF(double _x)
    {
        long _p = fastFloor(_x);
        long _q = 1, _r = _p + 1, _s = 1, _m, _n;
        double _d = 1, _y = _p;

        if (_x < _p || (_p < 0) ^ (_r <= 0)) {
            return _x; /* out of range ?(x) =~ x */
        }

        for (;;) {
            /* invariants: q * r - p * s == 1 && p / q <= x && x < r / s */
            _d /= 2;

            if (_y + _d == _y)
                break; /* reached max possible precision */

            _m = _p + _r;

            if ((_m < 0) ^ (_p < 0))
                break; /* sum overflowed */

            _n = _q + _s;

            if (_n < 0)
                break; /* sum overflowed */

            if (_x < (double)_m / _n)
            {
                _r = _m;
                _s = _n;
            }
            else
            {
                _y += _d;
                _p = _m;
                _q = _n;
            }
        }
        return _y + _d; /* final round-off */
    }


}
