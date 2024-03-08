package com.github.terefang.randy.fractal;

import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.NoiseUtil;

public class DistortFractal extends AbstractFractal implements IFractal
{

    @Override
    public double _fractal1(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x) {
        x *= frequency;
        double y = 0;
        double sum = 0;
        double amp = 1f;
        double max = 1f;

        double _dx = _noise.noise1(NoiseUtil.BASE_SEED1, x);
        for (int i = 0; i < octaves; i++) {
            sum += _noise.noise1((_vseed ? i : 0), x+_dx) * amp;
            sum -= _noise.noise1(0x123-(_vseed ? i : 0), x+_dx) * amp;
            if(fractalSpiral)
            {
                final double x2 = rotateX2D(x, y);
                final double y2 = rotateY2D(x, y);
                x = x2; y = y2;
            }
            x *= lacunarity;
            y *= lacunarity;
            amp *= gain;
            max += amp;
        }
        return sum / max;
    }

    @Override
    public double _fractal2(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y) {
        x *= frequency;
        y *= frequency;

        double sum = 0;
        double amp = 1f;
        double max = 1f;

        double _dx = _noise.noise2(NoiseUtil.BASE_SEED1, x, y);
        double _dy = _noise.noise2(NoiseUtil.BASE_SEED2, y, x);
        for (int i = 0; i < octaves; i++) {
            sum += _noise.noise2((_vseed ? i : 0), x+_dx, y-_dy) * amp;
            sum -= _noise.noise2(0x123-(_vseed ? i : 0), x+_dx, y-_dy) * amp;
            if(fractalSpiral)
            {
                final double x2 = rotateX2D(x, y);
                final double y2 = rotateY2D(x, y);
                x = x2; y = y2;
            }
            x *= lacunarity;
            y *= lacunarity;
            amp *= gain;
            max += amp;
        }
        return sum / max;
    }

    @Override
    public double _fractal3(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z) {
        x *= frequency;
        y *= frequency;
        z *= frequency;

        double sum = 0;
        double amp = 1f;
        double max = 1f;

        double _dx = _noise.noise3(NoiseUtil.BASE_SEED1, x, y, z);
        double _dy = _noise.noise3(NoiseUtil.BASE_SEED2, y, z, x);
        double _dz = _noise.noise3(NoiseUtil.BASE_SEED3, z, x, y);
        for (int i = 0; i < octaves; i++) {
            sum += _noise.noise3((_vseed ? i : 0), x+_dx, y-_dy, z+_dz) * amp;
            sum -= _noise.noise3(0x123-(_vseed ? i : 0), x+_dx, y-_dy, z+_dz) * amp;
            if(fractalSpiral)
            {
                final double x2 = rotateX2D(x, y);
                final double y2 = rotateY2D(x, y);
                x = x2; y = y2;
            }
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            amp *= gain;
            max += amp;
        }
        return sum / max;
    }

    @Override
    public double _fractal4(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z, double u) {
        x *= frequency;
        y *= frequency;
        z *= frequency;
        u *= frequency;

        double sum = 0;
        double amp = 1f;
        double max = 1f;

        double _dx = _noise.noise4(NoiseUtil.BASE_SEED1, x, y, z, u);
        double _dy = _noise.noise4(NoiseUtil.BASE_SEED2, y, z, u, x);
        double _dz = _noise.noise4(NoiseUtil.BASE_SEED3, z, u, x, y);
        double _du = _noise.noise4(NoiseUtil.BASE_SEED1, u, x, y, z);
        for (int i = 0; i < octaves; i++) {
            sum += _noise.noise4((_vseed ? i : 0), x+_dx, y-_dy, z+_dz, u+_du) * amp;
            sum -= _noise.noise4(0x123-(_vseed ? i : 0), x+_dx, y-_dy, z+_dz, u+_du) * amp;
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
            amp *= gain;
            max += amp;
        }
        return sum / max;
    }

    @Override
    public double _fractal5(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z, double u, double v) {
        x *= frequency;
        y *= frequency;
        z *= frequency;
        u *= frequency;
        v *= frequency;

        double sum = 0;
        double amp = 1f;
        double max = 1f;

        double _dx = _noise.noise6(NoiseUtil.BASE_SEED1, x, y, z, u, v);
        double _dy = _noise.noise6(NoiseUtil.BASE_SEED2, y, z, u, v, x);
        double _dz = _noise.noise6(NoiseUtil.BASE_SEED3, z, u, v, x, y);
        double _du = _noise.noise6(NoiseUtil.BASE_SEED1, u, v, x, y, z);
        double _dv = _noise.noise6(NoiseUtil.BASE_SEED2, u, v, x, y, z);
        for (int i = 0; i < octaves; i++) {
            sum += _noise.noise6((_vseed ? i : 0), x+_dx, y-_dy, z+_dz, u+_du, v-_dv) * amp;
            sum -= _noise.noise6(0x123-(_vseed ? i : 0), x+_dx, y-_dy, z+_dz, u+_du, v-_dv) * amp;
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
            amp *= gain;
            max += amp;
        }
        return sum / max;
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

        double sum = 0;
        double amp = 1f;
        double max = 1f;

        double _dx = _noise.noise6(NoiseUtil.BASE_SEED1, x, y, z, w, u, v);
        double _dy = _noise.noise6(NoiseUtil.BASE_SEED2, y, z, w, u, v, x);
        double _dz = _noise.noise6(NoiseUtil.BASE_SEED3, z, w, u, v, x, y);
        double _dw = _noise.noise6(NoiseUtil.BASE_SEED1, w, u, v, x, y, z);
        double _du = _noise.noise6(NoiseUtil.BASE_SEED2, u, v, x, y, z, w);
        double _dv = _noise.noise6(NoiseUtil.BASE_SEED3, v, x, y, z, w, u);
        for (int i = 0; i < octaves; i++) {
            sum += _noise.noise6((_vseed ? i : 0), x+_dx, y-_dy, z+_dz, w-_dw, u+_du, v-_dv) * amp;
            sum -= _noise.noise6(0x123-(_vseed ? i : 0), x+_dx, y-_dy, z+_dz, w-_dw, u+_du, v-_dv) * amp;
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
            amp *= gain;
            max += amp;
        }
        return sum / max;
    }
}
