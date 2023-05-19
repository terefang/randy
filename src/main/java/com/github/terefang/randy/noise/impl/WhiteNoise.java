package com.github.terefang.randy.noise.impl;

import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.NoiseUtil;

public class WhiteNoise extends NoiseUtil implements INoise
{

    @Override
    public double _noise1(long seed, double x, int interpolation) {
        return singleWhiteNoise(false, makeSeedInt(seed), x, this.getMutation());
    }

    @Override
    public double _noise2(long seed, double x, double y, int interpolation) {
        return singleWhiteNoise(false, makeSeedInt(seed), x,y, this.getMutation());
    }

    @Override
    public double _noise3(long seed, double x, double y, double z, int interpolation) {
        return singleWhiteNoise(false, makeSeedInt(seed), x,y,z, this.getMutation());
    }

    @Override
    public double _noise4(long seed, double x, double y, double z, double u, int interpolation) {
        return singleWhiteNoise(false, makeSeedInt(seed), x,y,z,u, this.getMutation());
    }

    @Override
    public double _noise5(long seed, double x, double y, double z, double u, double v, int interpolation) {
        return singleWhiteNoise(false, makeSeedInt(seed), x,y,z,u,v, this.getMutation());
    }

    @Override
    public double _noise6(long seed, double x, double y, double z, double u, double v, double w, int interpolation) {
        return singleWhiteNoise(false, makeSeedInt(seed), x,y,z,u,v,w, this.getMutation());
    }

    @Override
    public double _noise1n(long seed, double x, int interpolation) {
        return singleWhiteNoise(true, makeSeedInt(seed), x, this.getMutation());
    }

    @Override
    public double _noise2n(long seed, double x, double y, int interpolation) {
        return singleWhiteNoise(true, makeSeedInt(seed), x,y, this.getMutation());
    }

    @Override
    public double _noise3n(long seed, double x, double y, double z, int interpolation) {
        return singleWhiteNoise(true, makeSeedInt(seed), x,y,z, this.getMutation());
    }

    @Override
    public double _noise4n(long seed, double x, double y, double z, double u, int interpolation) {
        return singleWhiteNoise(true, makeSeedInt(seed), x,y,z,u, this.getMutation());
    }

    @Override
    public double _noise5n(long seed, double x, double y, double z, double u, double v, int interpolation) {
        return singleWhiteNoise(true, makeSeedInt(seed), x,y,z,u,v, this.getMutation());
    }

    @Override
    public double _noise6n(long seed, double x, double y, double z, double u, double v, double w, int interpolation) {
        return singleWhiteNoise(true, makeSeedInt(seed), x,y,z,u,v,w, this.getMutation());
    }
// ----------------------------------------------------------------------------

    // White Noise

    public static final double singleWhiteNoise(boolean normalize, int seed, double x, double y) {
        int xi = floatToIntMixed((float)x);
        int yi = floatToIntMixed((float)y);

        double _v = valCoord2D(seed, xi, yi);
        if(normalize) _v = (_v*.5)+.5;
        return _v;
    }

    public static final double singleWhiteNoise(boolean normalize, int seed, double x, double y, double z) {
        int xi = floatToIntMixed((float)x);
        int yi = floatToIntMixed((float)y);
        int zi = floatToIntMixed((float)z);

        double _v = valCoord3D(seed, xi, yi, zi);
        if(normalize) _v = (_v*.5)+.5;
        return _v;
    }

    public static final double singleWhiteNoise(boolean normalize, int seed, double x, double y, double z, double w) {
        int xi = floatToIntMixed((float)x);
        int yi = floatToIntMixed((float)y);
        int zi = floatToIntMixed((float)z);
        int wi = floatToIntMixed((float)w);

        double _v = valCoord4D(seed, xi, yi, zi, wi);
        if(normalize) _v = (_v*.5)+.5;
        return _v;
    }

    public static final double singleWhiteNoise(boolean normalize, int seed, double x, double y, double z, double w, double u) {
        int xi = floatToIntMixed((float)x);
        int yi = floatToIntMixed((float)y);
        int zi = floatToIntMixed((float)z);
        int wi = floatToIntMixed((float)w);
        int ui = floatToIntMixed((float)u);

        double _v = valCoord5D(seed, xi, yi, zi, wi, ui);
        if(normalize) _v = (_v*.5)+.5;
        return _v;
    }

    public static final double singleWhiteNoise(boolean normalize, int seed, double x, double y, double z, double w, double u, double v) {
        int xi = floatToIntMixed((float)x);
        int yi = floatToIntMixed((float)y);
        int zi = floatToIntMixed((float)z);
        int wi = floatToIntMixed((float)w);
        int ui = floatToIntMixed((float)u);
        int vi = floatToIntMixed((float)v);

        double _v = valCoord6D(seed, xi, yi, zi, wi, ui, vi);
        if(normalize) _v = (_v*.5)+.5;
        return _v;
    }

    public static final double singleWhiteNoise(boolean normalize, int seed, double x, double y, double z, double w, double u, double v, double m) {
        int xi = floatToIntMixed((float)x);
        int yi = floatToIntMixed((float)y);
        int zi = floatToIntMixed((float)z);
        int wi = floatToIntMixed((float)w);
        int ui = floatToIntMixed((float)u);
        int vi = floatToIntMixed((float)(v+m));

        double _v = valCoord6D(seed, xi, yi, zi, wi, ui, vi);
        if(normalize) _v = (_v*.5)+.5;
        return _v;
    }


}
