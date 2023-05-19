package com.github.terefang.randy.noise;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public interface INoise
{
    default public String name() {
        return this.getClass().getSimpleName();
    }

    public void setInterpolation(int _i);
    public int getInterpolation();

    public void setSeed(long s);
    public long getSeed();
    default public void setSeed(String _text) {
        setSeed(UUID.nameUUIDFromBytes(_text.getBytes(StandardCharsets.UTF_8)).getMostSignificantBits());
    }

    default public void setSeed(byte[] _text) {
        setSeed(UUID.nameUUIDFromBytes(_text).getMostSignificantBits());
    }

    public double getMutation();
    public void setMutation(double mutation);
    public double getSharpness();
    public void setSharpness(double foamSharpness);
    public double getHarshness();
    public void setHarshness(double harshness);


    public double _noise1(long seed, double x, int interpolation);
    public double _noise2(long seed, double x, double y, int interpolation);
    public double _noise3(long seed, double x, double y, double z, int interpolation);
    public double _noise4(long seed, double x, double y, double z, double u, int interpolation);
    public double _noise5(long seed, double x, double y, double z, double u, double v, int interpolation);
    public double _noise6(long seed, double x, double y, double z, double u, double v, double w, int interpolation);

    public double _noise1n(long seed, double x, int interpolation);
    public double _noise2n(long seed, double x, double y, int interpolation);
    public double _noise3n(long seed, double x, double y, double z, int interpolation);
    public double _noise4n(long seed, double x, double y, double z, double u, int interpolation);
    public double _noise5n(long seed, double x, double y, double z, double u, double v, int interpolation);
    public double _noise6n(long seed, double x, double y, double z, double u, double v, double w, int interpolation);

    default public double noise1(double x) { return _noise1(this.getSeed(), x, this.getInterpolation()); }
    default public double noise2(double x, double y) { return _noise2(this.getSeed(), x,y, this.getInterpolation()); }
    default public double noise3(double x, double y, double z) { return _noise3(this.getSeed(), x,y,z, this.getInterpolation()); }
    default public double noise4(double x, double y, double z, double u) { return _noise4(this.getSeed(), x,y,z,u, this.getInterpolation()); }
    default public double noise5(double x, double y, double z, double u, double v) { return _noise5(this.getSeed(), x,y,z,u,v, this.getInterpolation()); }
    default public double noise6(double x, double y, double z, double u, double v, double w) { return _noise6(this.getSeed(), x,y,z,u,v,w, this.getInterpolation()); }

    default public double noise1n(double x) { return _noise1n(this.getSeed(), x, this.getInterpolation()); }
    default public double noise2n(double x, double y) { return _noise2n(this.getSeed(), x,y, this.getInterpolation()); }
    default public double noise3n(double x, double y, double z) { return _noise3n(this.getSeed(), x,y,z, this.getInterpolation()); }
    default public double noise4n(double x, double y, double z, double u) { return _noise4n(this.getSeed(), x,y,z,u, this.getInterpolation()); }
    default public double noise5n(double x, double y, double z, double u, double v) { return _noise5n(this.getSeed(), x,y,z,u,v, this.getInterpolation()); }
    default public double noise6n(double x, double y, double z, double u, double v, double w) { return _noise6n(this.getSeed(), x,y,z,u,v,w, this.getInterpolation()); }

    default public double noise1(long vs, double x) { return _noise1(this.getSeed()+vs, x, this.getInterpolation()); }
    default public double noise2(long vs, double x, double y) { return _noise2(this.getSeed()+vs, x,y, this.getInterpolation()); }
    default public double noise3(long vs, double x, double y, double z) { return _noise3(this.getSeed()+vs, x,y,z, this.getInterpolation()); }
    default public double noise4(long vs, double x, double y, double z, double u) { return _noise4(this.getSeed()+vs, x,y,z,u, this.getInterpolation()); }
    default public double noise5(long vs, double x, double y, double z, double u, double v) { return _noise5(this.getSeed()+vs, x,y,z,u,v, this.getInterpolation()); }
    default public double noise6(long vs, double x, double y, double z, double u, double v, double w) { return _noise6(this.getSeed()+vs, x,y,z,u,v,w, this.getInterpolation()); }

    default public double noise1n(long vs, double x) { return _noise1n(this.getSeed()+vs, x, this.getInterpolation()); }
    default public double noise2n(long vs, double x, double y) { return _noise2n(this.getSeed()+vs, x,y, this.getInterpolation()); }
    default public double noise3n(long vs, double x, double y, double z) { return _noise3n(this.getSeed()+vs, x,y,z, this.getInterpolation()); }
    default public double noise4n(long vs, double x, double y, double z, double u) { return _noise4n(this.getSeed()+vs, x,y,z,u, this.getInterpolation()); }
    default public double noise5n(long vs, double x, double y, double z, double u, double v) { return _noise5n(this.getSeed()+vs, x,y,z,u,v, this.getInterpolation()); }
    default public double noise6n(long vs, double x, double y, double z, double u, double v, double w) { return _noise6n(this.getSeed()+vs, x,y,z,u,v,w, this.getInterpolation()); }

}
