package com.github.terefang.randy.fractal;

import com.github.terefang.randy.noise.INoise;

public class AbstractFractal
{
    double offset;
    double H;
    int octaves;
    double frequency;
    double lacunarity;
    double gain;
    boolean vseed;

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
