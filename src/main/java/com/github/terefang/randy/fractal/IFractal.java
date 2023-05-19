package com.github.terefang.randy.fractal;

import com.github.terefang.randy.noise.INoise;

public interface IFractal
{
    default public String name() {
        return this.getClass().getSimpleName();
    }
    public double _fractal1(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x);
    public double _fractal2(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y);
    public double _fractal3(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z);
    public double _fractal4(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z, double u);
    public double _fractal5(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z, double u, double v);
    public double _fractal6(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z, double u, double v, double w);

    public double getOffset();
    public void setOffset(double offset);
    public double getH();
    public void setH(double h);
    public int getOctaves();
    public void setOctaves(int octaves);
    public double getFrequency();
    public void setFrequency(double frequency);
    public double getLacunarity();
    public void setLacunarity(double lacunarity);
    public double getGain();
    public void setGain(double gain);
    public boolean isVseed();
    public void setVseed(boolean vseed);
    public INoise getNoise();
    public void setNoise(INoise noise);


    default public double fractal1(double x)
    {
        return _fractal1(getNoise(), getOffset(), getH(),getOctaves(), getFrequency(), getLacunarity(),getGain(), isVseed(), x);
    }

    default public double fractal2(double x, double y)
    {
        return _fractal2(getNoise(), getOffset(), getH(),getOctaves(), getFrequency(), getLacunarity(),getGain(), isVseed(), x,y);
    }

    default public double fractal3(double x, double y, double z)
    {
        return _fractal3(getNoise(), getOffset(), getH(),getOctaves(), getFrequency(), getLacunarity(),getGain(), isVseed(), x,y,z);
    }

    default public double fractal4(double x, double y, double z, double u)
    {
        return _fractal4(getNoise(), getOffset(), getH(),getOctaves(), getFrequency(), getLacunarity(),getGain(), isVseed(), x,y,z,u);
    }

    default public double fractal5(double x, double y, double z, double u, double v)
    {
        return _fractal5(getNoise(), getOffset(), getH(),getOctaves(), getFrequency(), getLacunarity(),getGain(), isVseed(), x,y,z,u,v);
    }

    default public double fractal6(double x, double y, double z, double u, double v, double w)
    {
        return _fractal6(getNoise(), getOffset(), getH(),getOctaves(), getFrequency(), getLacunarity(),getGain(), isVseed(), x,y,z,u,v,w);
    }

}
