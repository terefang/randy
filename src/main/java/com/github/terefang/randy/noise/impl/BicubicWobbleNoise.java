package com.github.terefang.randy.noise.impl;

import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.NoiseUtil;
import com.github.terefang.randy.noise.double2;

public class BicubicWobbleNoise extends NoiseUtil implements INoise
{
    @Override
    public String name()
    {
        switch (this.getInterpolation())
        {
            case QUINTIC:
                return super.name()+"Quintic";
            case HERMITE:
                return super.name()+"Hermite";
            case LINEAR:
            default:
                return super.name()+"Linear";
        }
    }

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
    public static double bicubicWobble(int interpolation, int seed, double x)
    {
        int x0 = fastFloor(x);
        int x1 = x0 + 1;

        double xs;
        switch (interpolation) {
            default:
            case LINEAR:
                xs = x - x0;
                break;
            case HERMITE:
                xs = hermiteInterpolator(x - x0);
                break;
            case QUINTIC:
                xs = quinticInterpolator(x - x0);
                break;
        }
        return lerp(bicubicWobble(seed, x0),bicubicWobble(seed, x1),xs);
    }

    public static double bicubicWobble(int interpolation, int seed, double x, double y) {
        int x0 = fastFloor(x);
        int y0 = fastFloor(y);
        int x1 = x0 + 1;
        int y1 = y0 + 1;

        double xs, ys;
        switch (interpolation) {
            default:
            case LINEAR:
                xs = x - x0;
                ys = y - y0;
                break;
            case HERMITE:
                xs = hermiteInterpolator(x - x0);
                ys = hermiteInterpolator(y - y0);
                break;
            case QUINTIC:
                xs = quinticInterpolator(x - x0);
                ys = quinticInterpolator(y - y0);
                break;
        }

        double _h00 = bicubicWobble(seed, x0)+bicubicWobble(seed+0x12345678, y0);
        double _h01 = bicubicWobble(seed, x1)+bicubicWobble(seed+0x12345678, y0);

        double _h10 = bicubicWobble(seed, x0)+bicubicWobble(seed+0x12345678, y1);
        double _h11 = bicubicWobble(seed, x1)+bicubicWobble(seed+0x12345678, y1);

        double _dx00 = lerp(_h00, _h01, xs);
        double _dx01 = lerp(_h10, _h11, xs);
        return lerp(_dx00, _dx01, ys)/2.;
    }

    public static double bicubicWobble(int interpolation, int seed, double x, double y, double z) {
        int x0 = fastFloor(x);
        int y0 = fastFloor(y);
        int z0 = fastFloor(z);
        int x1 = x0 + 1;
        int y1 = y0 + 1;
        int z1 = z0 + 1;

        double xs, ys, zs;
        switch (interpolation) {
            default:
            case LINEAR:
                xs = x - x0;
                ys = y - y0;
                zs = z - z0;
                break;
            case HERMITE:
                xs = hermiteInterpolator(x - x0);
                ys = hermiteInterpolator(y - y0);
                zs = hermiteInterpolator(z - z0);
                break;
            case QUINTIC:
                xs = quinticInterpolator(x - x0);
                ys = quinticInterpolator(y - y0);
                zs = quinticInterpolator(z - z0);
                break;
        }

        double _h000 = bicubicWobble(seed, x0)+bicubicWobble(seed+0x12345678, y0)+bicubicWobble(seed+0x23456781, z0);
        double _h001 = bicubicWobble(seed, x1)+bicubicWobble(seed+0x12345678, y0)+bicubicWobble(seed+0x23456781, z0);

        double _h010 = bicubicWobble(seed, x0)+bicubicWobble(seed+0x12345678, y1)+bicubicWobble(seed+0x23456781, z0);
        double _h011 = bicubicWobble(seed, x1)+bicubicWobble(seed+0x12345678, y1)+bicubicWobble(seed+0x23456781, z0);

        double _h100 = bicubicWobble(seed, x0)+bicubicWobble(seed+0x12345678, y0)+bicubicWobble(seed+0x23456781, z1);
        double _h101 = bicubicWobble(seed, x1)+bicubicWobble(seed+0x12345678, y0)+bicubicWobble(seed+0x23456781, z1);

        double _h110 = bicubicWobble(seed, x0)+bicubicWobble(seed+0x12345678, y1)+bicubicWobble(seed+0x23456781, z1);
        double _h111 = bicubicWobble(seed, x1)+bicubicWobble(seed+0x12345678, y1)+bicubicWobble(seed+0x23456781, z1);

        double _dx00 = lerp(_h000, _h001, xs);
        double _dx01 = lerp(_h010, _h011, xs);
        double _dx10 = lerp(_h100, _h101, xs);
        double _dx11 = lerp(_h110, _h111, xs);
        double2 _dy = lerp(_dx00, _dx10, _dx01, _dx11, ys);
        return lerp(_dy,zs)/3.;
    }

    public static double bicubicWobble(int interpolation, int seed, double x, double y, double z, double u) {
        double _wx = bicubicWobble(seed, x);
        double _wy = bicubicWobble(seed+0x12345678, y);
        double _wz = bicubicWobble(seed+0x23456781, z);
        double _wu = bicubicWobble(seed+0x34567812, u);
        return (_wx+_wy+_wz+_wu)/4.;
    }

    public static double bicubicWobble(int interpolation, int seed, double x, double y, double z, double u, double v) {
        double _wx = bicubicWobble(seed, x);
        double _wy = bicubicWobble(seed+0x12345678, y);
        double _wz = bicubicWobble(seed+0x23456781, z);
        double _wu = bicubicWobble(seed+0x34567812, u);
        double _wv = bicubicWobble(seed+0x45678123, v);
        return (_wx+_wy+_wz+_wu+_wv)/5.;
    }

    public static double bicubicWobble(int interpolation, int seed, double x, double y, double z, double u, double v, double w) {
        double _wx = bicubicWobble(seed, x);
        double _wy = bicubicWobble(seed+0x12345678, y);
        double _wz = bicubicWobble(seed+0x23456781, z);
        double _wu = bicubicWobble(seed+0x34567812, u);
        double _wv = bicubicWobble(seed+0x45678123, v);
        double _ww = bicubicWobble(seed+0x56781234, w);
        return (_wx+_wy+_wz+_wu+_wv+_ww)/6.;
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