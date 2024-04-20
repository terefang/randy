package com.github.terefang.randy.fractal.impl;

import com.github.terefang.randy.fractal.AbstractFractal;
import com.github.terefang.randy.fractal.IFractal;
import com.github.terefang.randy.noise.INoise;

public class SamplerFractal extends AbstractFractal implements IFractal
{

    static double F_SAMPLE_DECAY = .36;
    @Override
    public double _fractal1(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x) {
        return samplerFractal(_noise,octaves,frequency,lacunarity,gain,_vseed, this.isFractalSpiral(), x);
    }

    @Override
    public double _fractal2(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y) {
        return samplerFractal(_noise,octaves,frequency,lacunarity,gain,_vseed, this.isFractalSpiral(),x,y);
    }

    @Override
    public double _fractal3(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z) {
        return samplerFractal(_noise,octaves,frequency,lacunarity,gain,_vseed, this.isFractalSpiral(),x,y,z);
    }

    @Override
    public double _fractal4(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z, double u) {
        return samplerFractal(_noise,octaves,frequency,lacunarity,gain,_vseed, this.isFractalSpiral(),x,y,z,u);
    }

    @Override
    public double _fractal5(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z, double u, double v) {
        return samplerFractal(_noise,octaves,frequency,lacunarity,gain,_vseed, this.isFractalSpiral(),x,y,z,u,v);
    }

    @Override
    public double _fractal6(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z, double u, double v, double w)
    {
        return samplerFractal(_noise,octaves,frequency,lacunarity,gain,_vseed, this.isFractalSpiral(),x,y,z,u,v,w);
    }

    // ---------------------------------------------------------------------------------------------------------

    public static double samplerFractal(INoise _noise, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, boolean fractalSpiral, double x)
    {
        x *= frequency;
        double y = 0;

        double amp = 1.;
        double adj = 1.;
        double sum = 0.;;
        for (int i = 0; i < octaves; i++)
        {
            sum += _noise.noise1((_vseed ? i : 0), x) * amp;
            amp *= gain;
            if(fractalSpiral)
            {
                final double x2 = rotateX2D(x, y);
                final double y2 = rotateY2D(x, y);
                x = x2; y = y2;
            }
            x /= lacunarity;
            y /= lacunarity;
            adj += amp;
        }
        return sum/adj;
    }

    public static double samplerFractal(INoise _noise, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, boolean fractalSpiral, double x, double y)
    {
        x *= frequency;
        y *= frequency;

        double amp = 1.;
        double adj = 1.;
        double sum = 0.;;
        for (int i = 0; i < octaves; i++) {
            sum += _noise.noise2((_vseed ? i : 0), x, y) * amp;
            amp *= gain;
            if(fractalSpiral)
            {
                final double x2 = rotateX2D(x, y);
                final double y2 = rotateY2D(x, y);
                x = x2; y = y2;
            }
            x /= lacunarity;
            y /= lacunarity;
            adj += amp;
        }
        return sum/adj;
    }

    public static double samplerFractal(INoise _noise, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, boolean fractalSpiral, double x, double y, double z)
    {
        x *= frequency;
        y *= frequency;
        z *= frequency;

        double amp = 1.;
        double adj = 1.;
        double sum = 0.;;
        for (int i = 0; i < octaves; i++) {
            sum += _noise.noise3((_vseed ? i : 0), x, y, z) * amp;
            amp *= gain;
            if(fractalSpiral)
            {
                final double x2 = rotateX2D(x, y);
                final double y2 = rotateY2D(x, y);
                x = x2; y = y2;
            }
            x /= lacunarity;
            y /= lacunarity;
            z /= lacunarity;
            adj += amp;
        }
        return sum/adj;
    }

    public static double samplerFractal(INoise _noise, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, boolean fractalSpiral, double x, double y, double z, double u)
    {
        x *= frequency;
        y *= frequency;
        z *= frequency;
        u *= frequency;

        double amp = 1.;
        double adj = 1.;
        double sum = 0.;;
        for (int i = 0; i < octaves; i++) {
            sum += _noise.noise4((_vseed ? i : 0), x, y, z, u) * amp;
            amp *= gain;
            if(fractalSpiral)
            {
                final double x2 = rotateX2D(x, y);
                final double y2 = rotateY2D(x, y);
                x = x2; y = y2;
            }
            x /= lacunarity;
            y /= lacunarity;
            z /= lacunarity;
            u /= lacunarity;
            adj += amp;
        }
        return sum/adj;
    }

    public static double samplerFractal(INoise _noise, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, boolean fractalSpiral, double x, double y, double z, double u, double v)
    {
        x *= frequency;
        y *= frequency;
        z *= frequency;
        u *= frequency;
        v *= frequency;

        double amp = 1.;
        double adj = 1.;
        double sum = 0.;;
        for (int i = 0; i < octaves; i++) {
            sum += _noise.noise5((_vseed ? i : 0), x, y, z, u, v) * amp;
            amp *= gain;
            if(fractalSpiral)
            {
                final double x2 = rotateX2D(x, y);
                final double y2 = rotateY2D(x, y);
                x = x2; y = y2;
            }
            x /= lacunarity;
            y /= lacunarity;
            z /= lacunarity;
            u /= lacunarity;
            v /= lacunarity;
            adj += amp;
        }
        return sum/adj;
    }

    public static double samplerFractal(INoise _noise, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, boolean fractalSpiral, double x, double y, double z, double u, double v, double w)
    {
        x *= frequency;
        y *= frequency;
        z *= frequency;
        w *= frequency;
        u *= frequency;
        v *= frequency;

        double amp = 1.;
        double adj = 1.;
        double sum = 0.;;
        for (int i = 0; i < octaves; i++) {
            sum += _noise.noise6((_vseed ? i : 0), x, y, z, u, v, w) * amp;
            amp *= gain;
            if(fractalSpiral)
            {
                final double x2 = rotateX2D(x, y);
                final double y2 = rotateY2D(x, y);
                x = x2; y = y2;
            }
            x /= lacunarity;
            y /= lacunarity;
            z /= lacunarity;
            w /= lacunarity;
            u /= lacunarity;
            v /= lacunarity;
            adj += amp;
        }
        return sum/adj;
    }

}
