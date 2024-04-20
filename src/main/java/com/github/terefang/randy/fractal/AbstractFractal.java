package com.github.terefang.randy.fractal;

import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.NoiseUtil;

public class AbstractFractal implements IFractal
{
    double offset;
    double H = NoiseUtil.BASE_H;
    int octaves = NoiseUtil.BASE_OCTAVES;
    double frequency = NoiseUtil.BASE_FREQUENCY;
    double lacunarity = NoiseUtil.BASE_LACUNARITY;
    double gain = NoiseUtil.BASE_GAIN;
    boolean vseed = true;

    protected boolean fractalSpiral = false;

    public boolean isFractalSpiral() {
        return fractalSpiral;
    }

    public void setFractalSpiral(boolean fractalSpiral) {
        this.fractalSpiral = fractalSpiral;
    }

    public double getOffset() {
        return offset;
    }

    public void setOffset(double offset) {
        this.offset = offset;
    }

    public double getH() {
        return H;
    }

    public void setH(double h) {
        H = h;
    }

    public int getOctaves() {
        return octaves;
    }

    public void setOctaves(int octaves) {
        this.octaves = octaves;
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public double getLacunarity() {
        return lacunarity;
    }

    public void setLacunarity(double lacunarity) {
        this.lacunarity = lacunarity;
    }

    public double getGain() {
        return gain;
    }

    public void setGain(double gain) {
        this.gain = gain;
    }

    public boolean isVseed() {
        return vseed;
    }

    public void setVseed(boolean vseed) {
        this.vseed = vseed;
    }

    INoise noise;

    public INoise getNoise() {
        return noise;
    }

    public void setNoise(INoise noise) {
        this.noise = noise;
    }

    protected static double rotateX2D(double x, double y){ return x * +0.6088885514347261f + y * -0.7943553508622062f; }
    protected static double rotateY2D(double x, double y){ return x * +0.7943553508622062f + y * +0.6088885514347261f; }


    @Override public double _fractal1(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x) { return 0; }
    @Override public double _fractal2(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y) { return 0; }
    @Override public double _fractal3(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z) { return 0; }
    @Override public double _fractal4(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z, double u) { return 0; }
    @Override public double _fractal5(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z, double u, double v) { return 0; }
    @Override public double _fractal6(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z, double u, double v, double w) { return 0; }

}
