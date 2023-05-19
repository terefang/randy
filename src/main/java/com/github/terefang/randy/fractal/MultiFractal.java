package com.github.terefang.randy.fractal;

import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.NoiseUtil;

public class MultiFractal extends AbstractFractal implements IFractal
{

    @Override
    public double _fractal1(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x) {
        x *= frequency;

        double sum = 0f, correction = 0f;
        for (int i = 0; i < octaves; i++) {
            correction += gain;
            sum += _noise.noise1((_vseed ? i : 0), x) * gain;
            x *= lacunarity;
        }
        return sum/correction;
    }

    @Override
    public double _fractal2(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y) {
        x *= frequency;
        y *= frequency;

        double sum = 0f, correction = 0f;
        for (int i = 0; i < octaves; i++) {
            correction += gain;
            sum += _noise.noise2((_vseed ? i : 0), x, y) * gain;
            x *= lacunarity;
            y *= lacunarity;
        }
        return sum/correction;
    }

    @Override
    public double _fractal3(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z) {
        x *= frequency;
        y *= frequency;
        z *= frequency;

        double sum = 0f, correction = 0f;
        for (int i = 0; i < octaves; i++) {
            correction += gain;
            sum += _noise.noise3((_vseed ? i : 0), x, y, z) * gain;
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
        }
        return sum/correction;
    }

    @Override
    public double _fractal4(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z, double u) {
        x *= frequency;
        y *= frequency;
        z *= frequency;
        u *= frequency;

        double sum = 0f, correction = 0f;
        for (int i = 0; i < octaves; i++) {
            correction += gain;
            sum += _noise.noise4((_vseed ? i : 0), x, y, z, u) * gain;
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            u *= lacunarity;
        }
        return sum/correction;
    }

    @Override
    public double _fractal5(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z, double u, double v) {
        x *= frequency;
        y *= frequency;
        z *= frequency;
        u *= frequency;
        v *= frequency;

        double sum = 0f, correction = 0f;
        for (int i = 0; i < octaves; i++) {
            correction += gain;
            sum += _noise.noise5((_vseed ? i : 0), x, y, z, u, v) * gain;
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            u *= lacunarity;
            v *= lacunarity;
        }
        return sum/correction;
    }

    @Override
    public double _fractal6(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z, double u, double v, double w)
    {
        x *= frequency;
        y *= frequency;
        z *= frequency;
        w *= frequency;
        u *= frequency;
        v *= frequency;

        double sum = 0f, correction = 0f;
        for (int i = 0; i < octaves; i++) {
            correction += gain;
            sum += _noise.noise6((_vseed ? i : 0), x, y, z, u, v, w) * gain;
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            u *= lacunarity;
            v *= lacunarity;
        }
        return sum/correction;
    }
}
