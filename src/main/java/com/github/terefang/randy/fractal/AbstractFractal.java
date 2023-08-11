package com.github.terefang.randy.fractal;

import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.NoiseUtil;

public class AbstractFractal
{
    double offset;
    double H = NoiseUtil.BASE_H;
    int octaves = NoiseUtil.BASE_OCTAVES;
    double frequency = NoiseUtil.BASE_FREQUENCY;
    double lacunarity = NoiseUtil.BASE_LACUNARITY;
    double gain = NoiseUtil.BASE_GAIN;
    boolean vseed = true;

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
}
