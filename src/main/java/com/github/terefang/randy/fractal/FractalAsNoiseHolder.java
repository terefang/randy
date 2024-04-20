package com.github.terefang.randy.fractal;

import com.github.terefang.randy.noise.AbstractNoise;
import com.github.terefang.randy.noise.INoise;

public class FractalAsNoiseHolder extends AbstractNoise implements INoise
{
    IFractal fractal;

    public IFractal getFractal() {
        return fractal;
    }

    public void setFractal(IFractal fractal) {
        this.fractal = fractal;
    }

    @Override
    public void setSeed(long s) {
        this.fractal.getNoise().setSeed(s);
    }

    @Override
    public double _noise1(long seed, double x, int interpolation) {
        return this.fractal.fractal1(x);
    }

    @Override
    public double _noise2(long seed, double x, double y, int interpolation) {
        return this.fractal.fractal2(x, y);
    }

    @Override
    public double _noise3(long seed, double x, double y, double z, int interpolation) {
        return this.fractal.fractal3(x, y, z);
    }

    @Override
    public double _noise4(long seed, double x, double y, double z, double u, int interpolation) {
        return this.fractal.fractal4(x, y, z, u);
    }

    @Override
    public double _noise5(long seed, double x, double y, double z, double u, double v, int interpolation) {
        return this.fractal.fractal5(x, y, z, u, v);
    }

    @Override
    public double _noise6(long seed, double x, double y, double z, double u, double v, double w, int interpolation) {
        return this.fractal.fractal6(x, y, z, u, v, w);
    }

    @Override
    public double _noise1n(long seed, double x, int interpolation) {
        return this.fractal.fractal1n(x);
    }

    @Override
    public double _noise2n(long seed, double x, double y, int interpolation) {
        return this.fractal.fractal2n(x, y);
    }

    @Override
    public double _noise3n(long seed, double x, double y, double z, int interpolation) {
        return this.fractal.fractal3n(x, y, z);
    }

    @Override
    public double _noise4n(long seed, double x, double y, double z, double u, int interpolation) {
        return this.fractal.fractal4n(x, y, z, u);
    }

    @Override
    public double _noise5n(long seed, double x, double y, double z, double u, double v, int interpolation) {
        return this.fractal.fractal5n(x, y, z, u, v);
    }

    @Override
    public double _noise6n(long seed, double x, double y, double z, double u, double v, double w, int interpolation) {
        return this.fractal.fractal6n(x, y, z, u, v, w);
    }
}
