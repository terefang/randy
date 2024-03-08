package com.github.terefang.randy.noise;

import com.github.terefang.randy.transf.ITransform;

public abstract class AbstractNoise implements INoise
{
    long _seed = 0x1ee7b33f;
    int interpolation = NoiseUtil.LINEAR;

    ITransform transform;

    public ITransform getTransform() {
        return transform;
    }

    public void setTransform(ITransform transform) {
        this.transform = transform;
    }

    @Override
    public void setInterpolation(int _i) {
        this.interpolation = _i;
    }

    @Override
    public void setSeed(long s) {
        this._seed = s;
    }

    @Override
    public int getInterpolation() {
        return this.interpolation;
    }

    @Override
    public long getSeed() {
        return this._seed;
    }

    double mutation = NoiseUtil.BASE_MUTATION;
    double sharpness = NoiseUtil.BASE_SHARPNESS;
    double harshness = NoiseUtil.BASE_HARSHNESS;

    public double getMutation() {
        return mutation;
    }

    public void setMutation(double mutation) {
        this.mutation = mutation;
    }

    public double getSharpness() {
        return sharpness;
    }

    public void setSharpness(double sharpness) {
        this.sharpness = sharpness;
    }

    public double getHarshness() {
        return harshness;
    }

    public void setHarshness(double harshness) {
        this.harshness = harshness;
    }

    @Override public double _noise1(long seed, double x, int interpolation) {return 0;}
    @Override public double _noise2(long seed, double x, double y, int interpolation) {return 0;}
    @Override public double _noise3(long seed, double x, double y, double z, int interpolation) {return 0;}
    @Override public double _noise4(long seed, double x, double y, double z, double u, int interpolation) {return 0;}
    @Override public double _noise5(long seed, double x, double y, double z, double u, double v, int interpolation) {return 0;}
    @Override public double _noise6(long seed, double x, double y, double z, double u, double v, double w, int interpolation) {return 0;}
    @Override public double _noise1n(long seed, double x, int interpolation) {return 0;}
    @Override public double _noise2n(long seed, double x, double y, int interpolation) {return 0;}
    @Override public double _noise3n(long seed, double x, double y, double z, int interpolation) {return 0;}
    @Override public double _noise4n(long seed, double x, double y, double z, double u, int interpolation) {return 0;}
    @Override public double _noise5n(long seed, double x, double y, double z, double u, double v, int interpolation) {return 0;}
    @Override public double _noise6n(long seed, double x, double y, double z, double u, double v, double w, int interpolation) {return 0;}
}
