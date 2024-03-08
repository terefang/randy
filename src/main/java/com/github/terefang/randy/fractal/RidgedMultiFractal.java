package com.github.terefang.randy.fractal;

import com.github.terefang.randy.noise.INoise;

public class RidgedMultiFractal extends AbstractFractal implements IFractal
{

    @Override
    public double _fractal1(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x) {
        x *= frequency;
        double y = 0;
        double sum = 0f, exp = 2f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(_noise.noise1((_vseed ? i : 0), x));
            correction += (exp *= 0.5);
            sum += spike * exp;
            if(fractalSpiral)
            {
                final double x2 = rotateX2D(x, y);
                final double y2 = rotateY2D(x, y);
                x = x2; y = y2;
            }
            x *= lacunarity;
            y *= lacunarity;
        }
        return sum * 2f / correction - 1f;
    }

    @Override
    public double _fractal2(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y) {
        x *= frequency;
        y *= frequency;

        double sum = 0f, exp = 2f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(_noise.noise2((_vseed ? i : 0), x, y));
            correction += (exp *= 0.5);
            sum += spike * exp;
            if(fractalSpiral)
            {
                final double x2 = rotateX2D(x, y);
                final double y2 = rotateY2D(x, y);
                x = x2; y = y2;
            }
            x *= lacunarity;
            y *= lacunarity;
        }
        return sum * 2f / correction - 1f;
    }

    @Override
    public double _fractal3(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z) {
        x *= frequency;
        y *= frequency;
        z *= frequency;

        double sum = 0f, exp = 2f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(_noise.noise3((_vseed ? i : 0), x, y, z));
            correction += (exp *= 0.5);
            sum += spike * exp;
            if(fractalSpiral)
            {
                final double x2 = rotateX2D(x, y);
                final double y2 = rotateY2D(x, y);
                x = x2; y = y2;
            }
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
        }
        return sum * 2f / correction - 1f;
    }

    @Override
    public double _fractal4(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z, double u) {
        x *= frequency;
        y *= frequency;
        z *= frequency;
        u *= frequency;

        double sum = 0f, exp = 2f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(_noise.noise5((_vseed ? i : 0), x, y, z, u));
            correction += (exp *= 0.5);
            sum += spike * exp;
            if(fractalSpiral)
            {
                final double x2 = rotateX2D(x, y);
                final double y2 = rotateY2D(x, y);
                x = x2; y = y2;
            }
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            u *= lacunarity;
        }
        return sum * 2f / correction - 1f;
    }

    @Override
    public double _fractal5(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z, double u, double v) {
        x *= frequency;
        y *= frequency;
        z *= frequency;
        u *= frequency;
        v *= frequency;

        double sum = 0f, exp = 2f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(_noise.noise5((_vseed ? i : 0), x, y, z, u, v));
            correction += (exp *= 0.5);
            sum += spike * exp;
            if(fractalSpiral)
            {
                final double x2 = rotateX2D(x, y);
                final double y2 = rotateY2D(x, y);
                x = x2; y = y2;
            }
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            u *= lacunarity;
            v *= lacunarity;
        }
        return sum * 2f / correction - 1f;
    }

    @Override
    public double _fractal6(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z, double u, double v, double w) {
        x *= frequency;
        y *= frequency;
        z *= frequency;
        w *= frequency;
        u *= frequency;
        v *= frequency;

        double sum = 0f, exp = 2f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(_noise.noise6((_vseed ? i : 0), x, y, z, u, v, w));
            correction += (exp *= 0.5);
            sum += spike * exp;
            if(fractalSpiral)
            {
                final double x2 = rotateX2D(x, y);
                final double y2 = rotateY2D(x, y);
                x = x2; y = y2;
            }
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            u *= lacunarity;
            v *= lacunarity;
        }
        return sum * 2f / correction - 1f;
    }
}
