package com.github.terefang.randy.fractal.impl;

import com.github.terefang.randy.fractal.AbstractFractal;
import com.github.terefang.randy.fractal.IFractal;
import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.NoiseUtil;

public class TurbulenceFractal extends AbstractFractal implements IFractal
{
    static int _START_OCTAVE = 1;
    @Override
    public double _fractal1(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x) {
        x *= frequency;

        double _value = _noise.noise1(x);
        _value = NoiseUtil.clamp(_value, 0., 1.);

        double _o = gain;
        double _l = lacunarity;

        for (int i = _START_OCTAVE; i <= octaves; i++) {

            _value += _noise.noise1((_vseed ? i : 0), x*_l) * _o;

            _l *= lacunarity;
            _o *= gain;
        }
        return _value;
    }

    @Override
    public double _fractal2(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y) {
        x *= frequency;
        y *= frequency;

        double _value = _noise.noise2(x, y);
        _value = NoiseUtil.clamp(_value, 0., 1.);

        double _o = gain;
        double _l = lacunarity;

        for (int i = _START_OCTAVE; i <= octaves; i++) {

            _value += _noise.noise2((_vseed ? i : 0), x*_l, y*_l) * _o;

            _l *= lacunarity;
            _o *= gain;

            if(fractalSpiral)
            {
                final double x2 = rotateX2D(x, y);
                final double y2 = rotateY2D(x, y);
                x = x2; y = y2;
            }
        }
        return _value;
    }

    @Override
    public double _fractal3(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z) {
        x *= frequency;
        y *= frequency;
        z *= frequency;

        double _value = _noise.noise3(x, y, z);
        _value = NoiseUtil.clamp(_value, 0., 1.);

        double _o = gain;
        double _l = lacunarity;

        for (int i = _START_OCTAVE; i <= octaves; i++) {

            _value += _noise.noise3((_vseed ? i : 0), x*_l, y*_l, z*_l) * _o;

            _l *= lacunarity;
            _o *= gain;

            if(fractalSpiral)
            {
                final double x2 = rotateX2D(x, y);
                final double y2 = rotateY2D(x, y);
                x = x2; y = y2;
            }
        }
        return _value;
    }

    @Override
    public double _fractal4(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z, double u) {
        x *= frequency;
        y *= frequency;
        z *= frequency;
        u *= frequency;

        double _value = _noise.noise4(x, y, z, u);
        _value = NoiseUtil.clamp(_value, 0., 1.);

        double _o = gain;
        double _l = lacunarity;

        for (int i = _START_OCTAVE; i <= octaves; i++) {

            _value += _noise.noise4((_vseed ? i : 0), x*_l, y*_l, z*_l, u*_l) * _o;

            _l *= lacunarity;
            _o *= gain;

            if(fractalSpiral)
            {
                final double x2 = rotateX2D(x, y);
                final double y2 = rotateY2D(x, y);
                x = x2; y = y2;
            }
        }
        return _value;
    }

    @Override
    public double _fractal5(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z, double u, double v) {
        x *= frequency;
        y *= frequency;
        z *= frequency;
        u *= frequency;
        v *= frequency;

        double _value = _noise.noise5(x, y, z, u, v);
        _value = NoiseUtil.clamp(_value, 0., 1.);

        double _o = gain;
        double _l = lacunarity;

        for (int i = _START_OCTAVE; i <= octaves; i++) {

            _value += _noise.noise5((_vseed ? i : 0), x*_l, y*_l, z*_l, u*_l, v*_l) * _o;

            _l *= lacunarity;
            _o *= gain;

            if(fractalSpiral)
            {
                final double x2 = rotateX2D(x, y);
                final double y2 = rotateY2D(x, y);
                x = x2; y = y2;
            }
        }
        return _value;
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

        double _value = _noise.noise6(x, y, z, u, v, w);
        _value = NoiseUtil.clamp(_value, 0., 1.);

        double _o = gain;
        double _l = lacunarity;

        for (int i = _START_OCTAVE; i <= octaves; i++) {

            _value += _noise.noise6((_vseed ? i : 0), x*_l, y*_l, z*_l, u*_l, v*_l, w*_l) * _o;

            _l *= lacunarity;
            _o *= gain;

            if(fractalSpiral)
            {
                final double x2 = rotateX2D(x, y);
                final double y2 = rotateY2D(x, y);
                x = x2; y = y2;
            }
        }
        return _value;
    }
}
