package com.github.terefang.randy.fractal.impl;

import com.github.terefang.randy.fractal.AbstractFractal;
import com.github.terefang.randy.fractal.IFractal;
import com.github.terefang.randy.noise.INoise;

public class BillowFractal extends AbstractFractal implements IFractal
{

    @Override
    public double _fractal1(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x) {
        x *= frequency;

        double sum = ((Math.abs(_noise.noise1(x)) * 2) - 1);
        double amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;

            amp *= gain;
            sum += ((Math.abs(_noise.noise1((_vseed ? i : 0), x)) * 2) - 1) * amp;
        }

        amp = gain;
        double ampFractal = 1;
        for (int i = 1; i < octaves; i++) {
            ampFractal += amp;
            amp *= gain;
        }

        return sum / ampFractal;
    }

    @Override
    public double _fractal2(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y) {
        x *= frequency;
        y *= frequency;

        double sum = ((Math.abs(_noise.noise2(x, y)) * 2) - 1);
        double amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;

            amp *= gain;
            sum += ((Math.abs(_noise.noise2((_vseed ? i : 0), x, y)) * 2) - 1) * amp;
        }

        amp = gain;
        double ampFractal = 1;
        for (int i = 1; i < octaves; i++) {
            ampFractal += amp;
            amp *= gain;
        }

        return sum / ampFractal;
    }

    @Override
    public double _fractal3(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z) {
        x *= frequency;
        y *= frequency;
        z *= frequency;

        double sum = ((Math.abs(_noise.noise3(x, y, z)) * 2) - 1);
        double amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;

            amp *= gain;
            sum += ((Math.abs(_noise.noise3((_vseed ? i : 0), x, y, z)) * 2) - 1) * amp;
        }

        amp = gain;
        double ampFractal = 1;
        for (int i = 1; i < octaves; i++) {
            ampFractal += amp;
            amp *= gain;
        }

        return sum / ampFractal;
    }

    @Override
    public double _fractal4(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z, double u) {
        x *= frequency;
        y *= frequency;
        z *= frequency;
        u *= frequency;

        double sum = ((Math.abs(_noise.noise4(x, y, z, u)) * 2) - 1);
        double amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            u *= lacunarity;

            amp *= gain;
            sum += ((Math.abs(_noise.noise4((_vseed ? i : 0), x, y, z, u)) * 2) - 1) * amp;
        }

        amp = gain;
        double ampFractal = 1;
        for (int i = 1; i < octaves; i++) {
            ampFractal += amp;
            amp *= gain;
        }

        return sum / ampFractal;
    }

    @Override
    public double _fractal5(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z, double u, double v) {
        x *= frequency;
        y *= frequency;
        z *= frequency;
        u *= frequency;
        v *= frequency;

        double sum = ((Math.abs(_noise.noise5(x, y, z, u, v)) * 2) - 1);
        double amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            u *= lacunarity;
            v *= lacunarity;

            amp *= gain;
            sum += ((Math.abs(_noise.noise5((_vseed ? i : 0), x, y, z, u, v)) * 2) - 1) * amp;
        }

        amp = gain;
        double ampFractal = 1;
        for (int i = 1; i < octaves; i++) {
            ampFractal += amp;
            amp *= gain;
        }

        return sum / ampFractal;
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

        double sum = ((Math.abs(_noise.noise6(x, y, z, w, u, v)) * 2) - 1);
        double amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            w *= lacunarity;
            u *= lacunarity;
            v *= lacunarity;

            amp *= gain;
            sum += ((Math.abs(_noise.noise6((_vseed ? i : 0), x, y, z, w, u, v)) * 2) - 1) * amp;
        }

        amp = gain;
        double ampFractal = 1;
        for (int i = 1; i < octaves; i++) {
            ampFractal += amp;
            amp *= gain;
        }

        return sum / ampFractal;
    }
}
