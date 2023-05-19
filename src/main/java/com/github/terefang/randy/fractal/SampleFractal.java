package com.github.terefang.randy.fractal;

import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.NoiseUtil;

public class SampleFractal extends AbstractFractal implements IFractal
{

    static double F_SAMPLE_DECAY = .36;
    @Override
    public double _fractal1(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x) {
        x *= frequency;

        double amp = 2f * (double) Math.pow(lacunarity, -H);
        double sum = _noise.noise1(x) * .5f;
        for (int i = 0; i < octaves; i++) {
            lacunarity *= F_SAMPLE_DECAY;
            x *= lacunarity;
            sum += _noise.noise1((_vseed ? i : 0), x) * amp;
            amp *= gain;
        }
        return sum;
    }

    @Override
    public double _fractal2(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y) {
        x *= frequency;
        y *= frequency;

        double amp = 2f * (double) Math.pow(lacunarity, -H);
        double sum = _noise.noise2(x, y) * .5f;
        for (int i = 0; i < octaves; i++) {
            lacunarity *= F_SAMPLE_DECAY;
            x *= lacunarity;
            y *= lacunarity;
            sum += _noise.noise2((_vseed ? i : 0), x, y) * amp;
            amp *= gain;
        }
        return sum;
    }

    @Override
    public double _fractal3(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z) {
        x *= frequency;
        y *= frequency;
        z *= frequency;

        double amp = 2f * (double) Math.pow(lacunarity, -H);
        double sum = _noise.noise3(x, y, z) * .5f;
        for (int i = 0; i < octaves; i++) {
            lacunarity *= F_SAMPLE_DECAY;
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            sum += _noise.noise3((_vseed ? i : 0), x, y, z) * amp;
            amp *= gain;
        }
        return sum;
    }

    @Override
    public double _fractal4(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z, double u) {
        x *= frequency;
        y *= frequency;
        z *= frequency;
        u *= frequency;

        double amp = 2f * (double) Math.pow(lacunarity, -H);
        double sum = _noise.noise4(x, y, z, u) * .5f;
        for (int i = 0; i < octaves; i++) {
            lacunarity *= F_SAMPLE_DECAY;
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            u *= lacunarity;
            sum += _noise.noise4((_vseed ? i : 0), x, y, z, u) * amp;
            amp *= gain;
        }
        return sum;
    }

    @Override
    public double _fractal5(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z, double u, double v) {
        x *= frequency;
        y *= frequency;
        z *= frequency;
        u *= frequency;
        v *= frequency;

        double amp = 2f * (double) Math.pow(lacunarity, -H);
        double sum = _noise.noise5(x, y, z, u, v) * .5f;
        for (int i = 0; i < octaves; i++) {
            lacunarity *= F_SAMPLE_DECAY;
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            u *= lacunarity;
            v *= lacunarity;
            sum += _noise.noise5((_vseed ? i : 0), x, y, z, u, v) * amp;
            amp *= gain;
        }
        return sum;
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

        double amp = 2f * (double) Math.pow(lacunarity, -H);
        double sum = _noise.noise6(x, y, z, u, v, w) * .5f;
        for (int i = 0; i < octaves; i++) {
            lacunarity *= F_SAMPLE_DECAY;
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            u *= lacunarity;
            v *= lacunarity;
            sum += _noise.noise6((_vseed ? i : 0), x, y, z, u, v, w) * amp;
            amp *= gain;
        }
        return sum;
    }
}
