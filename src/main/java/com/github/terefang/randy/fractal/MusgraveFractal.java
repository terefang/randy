package com.github.terefang.randy.fractal;

import com.github.terefang.randy.noise.INoise;

public class MusgraveFractal extends AbstractFractal implements IFractal
{

    @Override
    public double _fractal1(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x) {
        x *= frequency;
        double y = 0;
        double value = 0.0f;
        double pwr = 1.0f;
        double pwHL = (double) Math.pow(lacunarity, -H);

        for (int i = 0; i < octaves; i++)
        {
            value += (_noise.noise1((_vseed ? i : 0), x) + offset) * pwr;
            pwr *= pwHL;
            if(fractalSpiral)
            {
                final double x2 = rotateX2D(x, y);
                final double y2 = rotateY2D(x, y);
                x = x2; y = y2;
            }
            x *= lacunarity;
            y *= lacunarity;
        }

        return value/Math.pow(pwHL,octaves);
    }

    @Override
    public double _fractal2(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y) {
        x *= frequency;
        y *= frequency;

        double value = 0.0f;
        double pwr = 1.0f;
        double pwHL = (double) Math.pow(lacunarity, -H);

        for (int i = 0; i < octaves; i++)
        {
            value += (_noise.noise2((_vseed ? i : 0), x,y) + offset) * pwr;
            pwr *= pwHL;
            if(fractalSpiral)
            {
                final double x2 = rotateX2D(x, y);
                final double y2 = rotateY2D(x, y);
                x = x2; y = y2;
            }
            x *= lacunarity;
            y *= lacunarity;
        }

        return value/Math.pow(pwHL,octaves);
    }

    @Override
    public double _fractal3(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z) {
        x *= frequency;
        y *= frequency;
        z *= frequency;

        double value = 0.0f;
        double pwr = 1.0f;
        double pwHL = (double) Math.pow(lacunarity, -H);

        for (int i = 0; i < octaves; i++)
        {
            value += (_noise.noise3((_vseed ? i : 0), x,y,z) + offset) * pwr;
            pwr *= pwHL;
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

        return value/Math.pow(pwHL,octaves);
    }

    @Override
    public double _fractal4(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z, double u) {
        x *= frequency;
        y *= frequency;
        z *= frequency;
        u *= frequency;

        double value = 0.0f;
        double pwr = 1.0f;
        double pwHL = (double) Math.pow(lacunarity, -H);

        for (int i = 0; i < octaves; i++)
        {
            value += (_noise.noise4((_vseed ? i : 0), x,y,z,u) + offset) * pwr;
            pwr *= pwHL;
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

        return value/Math.pow(pwHL,octaves);
    }

    @Override
    public double _fractal5(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z, double u, double v) {
        x *= frequency;
        y *= frequency;
        z *= frequency;
        u *= frequency;
        v *= frequency;

        double value = 0.0f;
        double pwr = 1.0f;
        double pwHL = (double) Math.pow(lacunarity, -H);

        for (int i = 0; i < octaves; i++)
        {
            value += (_noise.noise5((_vseed ? i : 0), x,y,z,u,v) + offset) * pwr;
            pwr *= pwHL;
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

        return value/Math.pow(pwHL,octaves);
    }

    @Override
    public double _fractal6(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z, double u, double v, double w) {
        x *= frequency;
        y *= frequency;
        z *= frequency;
        u *= frequency;
        v *= frequency;
        w *= frequency;

        double value = 0.0f;
        double pwr = 1.0f;
        double pwHL = (double) Math.pow(lacunarity, -H);

        for (int i = 0; i < octaves; i++)
        {
            value += (_noise.noise6((_vseed ? i : 0), x,y,z,u,v,w) + offset) * pwr;
            pwr *= pwHL;
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
            w *= lacunarity;
        }

        return value/Math.pow(pwHL,octaves);
    }
}
