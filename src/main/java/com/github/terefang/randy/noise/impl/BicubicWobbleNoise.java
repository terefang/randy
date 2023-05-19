package com.github.terefang.randy.noise.impl;

import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.NoiseUtil;

public class BicubicWobbleNoise extends NoiseUtil implements INoise
{
    @Override
    public double _noise1(long seed, double x, int interpolation) {
        return bicubicWobble(interpolation, makeSeedInt(seed), x, this.getMutation());
    }

    @Override
    public double _noise2(long seed, double x, double y, int interpolation) {
        return bicubicWobble(interpolation, makeSeedInt(seed), x,y, this.getMutation());
    }

    @Override
    public double _noise3(long seed, double x, double y, double z, int interpolation) {
        return bicubicWobble(interpolation, makeSeedInt(seed), x,y,z, this.getMutation());
    }

    @Override
    public double _noise4(long seed, double x, double y, double z, double u, int interpolation) {
        return bicubicWobble(interpolation, makeSeedInt(seed), x,y,z,u, this.getMutation());
    }

    @Override
    public double _noise5(long seed, double x, double y, double z, double u, double v, int interpolation) {
        return bicubicWobble(interpolation, makeSeedInt(seed), x,y,z,u,v, this.getMutation());
    }

    @Override
    public double _noise6(long seed, double x, double y, double z, double u, double v, double w, int interpolation) {
        return bicubicWobble(interpolation, makeSeedInt(seed), x,y,z,u,v,w+this.getMutation());
    }

    @Override
    public double _noise1n(long seed, double x, int interpolation) {
        return bicubicWobble(interpolation, makeSeedInt(seed), x, this.getMutation());
    }

    @Override
    public double _noise2n(long seed, double x, double y, int interpolation) {
        return bicubicWobble(interpolation, makeSeedInt(seed), x,y, this.getMutation());
    }

    @Override
    public double _noise3n(long seed, double x, double y, double z, int interpolation) {
        return bicubicWobble(interpolation, makeSeedInt(seed), x,y,z, this.getMutation());
    }

    @Override
    public double _noise4n(long seed, double x, double y, double z, double u, int interpolation) {
        return bicubicWobble(interpolation, makeSeedInt(seed), x,y,z,u, this.getMutation());
    }

    @Override
    public double _noise5n(long seed, double x, double y, double z, double u, double v, int interpolation) {
        return bicubicWobble(interpolation, makeSeedInt(seed), x,y,z,u,v, this.getMutation());
    }

    @Override
    public double _noise6n(long seed, double x, double y, double z, double u, double v, double w, int interpolation) {
        return bicubicWobble(interpolation, makeSeedInt(seed), x,y,z,u,v,w+this.getMutation());
    }


    // ----------------------------------------------------------------------------

    // 1d
    public static double bicubicWobble(int interpolation, int seed, double x) {
        return bicubicWobble(seed, x);
    }

    public static double bicubicWobble(int interpolation, int seed, double x, double y) {
        return bicubicWobble(seed, x);
    }

    public static double bicubicWobble(int interpolation, int seed, double x, double y, double z) {
        return bicubicWobble(seed, x);
    }

    public static double bicubicWobble(int interpolation, int seed, double x, double y, double z, double u) {
        return bicubicWobble(seed, x);
    }

    public static double bicubicWobble(int interpolation, int seed, double x, double y, double z, double u, double v) {
        return bicubicWobble(seed, x);
    }

    public static double bicubicWobble(int interpolation, int seed, double x, double y, double z, double u, double v, double w) {
        return bicubicWobble(seed, x);
    }

    public static double bicubicWobble(int seed, double t) {
        final int floor = ((int)(t + 0x1p14) - 0x4000);
        long s = seed + 0x9E3779B97F4A7C15L;
        s = (s ^ (s << 21 | s >>> 43) ^ (s << 50 | s >>> 14)) + floor;
        final long m = s * 0xD1B54A32D192ED03L;
        final long n = s * 0xABC98388FB8FAC03L;
        final long o = s * 0x8CB92BA72F3D8DD7L;
        final double a = (m ^ n ^ o) * 4.8186754E-20f;
        final double b = (m + 0xD1B54A32D192ED03L ^ n + 0xABC98388FB8FAC03L ^ o + 0x8CB92BA72F3D8DD7L) * 4.8186754E-20f;
        final double c = (m + 0xA36A9465A325DA06L ^ n + 0x57930711F71F5806L ^ o + 0x1972574E5E7B1BAEL) * 4.8186754E-20f;
        final double d = (m + 0x751FDE9874B8C709L ^ n + 0x035C8A9AF2AF0409L ^ o + 0xA62B82F58DB8A985L) * 4.8186754E-20f;
        t -= floor;

        return cubicLerp(a, b, c, d, t);
    }
}