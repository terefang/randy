package com.github.terefang.randy.noise.impl;

import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.NoiseUtil;

public class HoneyNoise   extends NoiseUtil implements INoise
{
    @Override
    public String name()
    {
        switch (this.getInterpolation())
        {
            case COSINE:
                return super.name()+"Cosine";
            case QUINTIC:
                return super.name()+"Quintic";
            case HERMITE:
                return super.name()+"Hermite";
            case LINEAR:
            default:
                return super.name()+"Linear";
        }
    }

    @Override
    public double _noise1(long seed, double x, int interpolation) {
        return singleHoney(false, interpolation, makeSeedInt(seed), x, this.getMutation());
    }

    @Override
    public double _noise2(long seed, double x, double y, int interpolation) {
        return singleHoney(false, interpolation, makeSeedInt(seed), x,y, this.getMutation());
    }

    @Override
    public double _noise3(long seed, double x, double y, double z, int interpolation) {
        return singleHoney(false, interpolation, makeSeedInt(seed), x,y,z, this.getMutation());
    }

    @Override
    public double _noise4(long seed, double x, double y, double z, double u, int interpolation) {
        return singleHoney(false, interpolation, makeSeedInt(seed), x,y,z,u, this.getMutation());
    }

    @Override
    public double _noise5(long seed, double x, double y, double z, double u, double v, int interpolation) {
        return singleHoney(false, interpolation, makeSeedInt(seed), x,y,z,u,v, this.getMutation());
    }

    @Override
    public double _noise6(long seed, double x, double y, double z, double u, double v, double w, int interpolation) {
        return singleHoney(false, interpolation, makeSeedInt(seed), x,y,z,u,v,w+this.getMutation());
    }

    @Override
    public double _noise1n(long seed, double x, int interpolation) {
        return singleHoney(true, interpolation, makeSeedInt(seed), x, this.getMutation());
    }

    @Override
    public double _noise2n(long seed, double x, double y, int interpolation) {
        return singleHoney(true, interpolation, makeSeedInt(seed), x,y, this.getMutation());
    }

    @Override
    public double _noise3n(long seed, double x, double y, double z, int interpolation) {
        return singleHoney(true, interpolation, makeSeedInt(seed), x,y,z, this.getMutation());
    }

    @Override
    public double _noise4n(long seed, double x, double y, double z, double u, int interpolation) {
        return singleHoney(true, interpolation, makeSeedInt(seed), x,y,z,u, this.getMutation());
    }

    @Override
    public double _noise5n(long seed, double x, double y, double z, double u, double v, int interpolation) {
        return singleHoney(true, interpolation, makeSeedInt(seed), x,y,z,u,v, this.getMutation());
    }

    @Override
    public double _noise6n(long seed, double x, double y, double z, double u, double v, double w, int interpolation) {
        return singleHoney(true, interpolation, makeSeedInt(seed), x,y,z,u,v,w+ this.getMutation());
    }
    
    // ----------------------------------------------------------------------------

    public static final double singleHoney(boolean normalize, int interpolation, int seed, double x, double y){
        final double result = (SimplexNoise.singleSimplex(false, seed, x, y)
                + ValueNoise.singleValue(false, interpolation, seed ^ 0x9E3779B9, x, y)) * 0.5f + 1f;
        double _v = (result <= 1f) ? result * result - 1f : (result - 2f) * -(result - 2f) + 1f;
        if(normalize) _v = (_v*.5)+.5;
        return _v;
    }

    public static final double singleHoney(boolean normalize, int interpolation, int seed, double x, double y, double z){
        final double result = (SimplexNoise.singleSimplex(false, seed, x, y, z)
                + ValueNoise.singleValue(false, interpolation, seed ^ 0x9E3779B9, x, y, z)) * 0.5f + 1f;
        double _v = (result <= 1f) ? result * result - 1f : (result - 2f) * -(result - 2f) + 1f;
        if(normalize) _v = (_v*.5)+.5;
        return _v;
    }

    public static final double singleHoney(boolean normalize, int interpolation, int seed, double x, double y, double z, double w) {
        final double result = (SimplexNoise.singleSimplex(false, seed, x, y, z, w)
                + ValueNoise.singleValue(false, interpolation, seed ^ 0x9E3779B9, x, y, z, w)) * 0.5f + 1f;
        double _v = (result <= 1f) ? result * result - 1f : (result - 2f) * -(result - 2f) + 1f;
        if(normalize) _v = (_v*.5)+.5;
        return _v;
    }

    public static final double singleHoney(boolean normalize, int interpolation, int seed, double x, double y, double z, double w, double u) {
        final double result = (SimplexNoise.singleSimplex(false, seed, x, y, z, w, u)
                + ValueNoise.singleValue(false, interpolation, seed ^ 0x9E3779B9, x, y, z, w, u)) * 0.5f + 1f;
        double _v = (result <= 1f) ? result * result - 1f : (result - 2f) * -(result - 2f) + 1f;
        if(normalize) _v = (_v*.5)+.5;
        return _v;
    }

    public static final double singleHoney(boolean normalize, int interpolation, int seed, double x, double y, double z, double w, double u, double v)
    {
        final double result = (SimplexNoise.singleSimplex(false, seed, x, y, z, w, u, v)
                + ValueNoise.singleValue(false, interpolation, seed ^ 0x9E3779B9, x, y, z, w, u, v)) * 0.5f + 1f;
        double _v = (result <= 1f) ? result * result - 1f : (result - 2f) * -(result - 2f) + 1f;
        if(normalize) _v = (_v*.5)+.5;
        return _v;
    }

}
