package com.github.terefang.randy.fractal;

import com.github.terefang.randy.noise.INoise;

public abstract class AbstractOctavationModeFractal extends AbstractFractal implements IFractal
{
    public static AbstractOctavationModeFractal fromFBM()
    {
        return new AbstractOctavationModeFractal()
        {
            @Override
            public double fractalFunction(double _x)
            {
                return _x;
            }
            
            @Override
            public String name()
            {
                return "OctFbm";
            }
        };
    }
    
    public static AbstractOctavationModeFractal fromBILLOW()
    {
        return new AbstractOctavationModeFractal()
        {
            @Override
            public double fractalFunction(double _x)
            {
                return Math.abs((_x*2.)-1.);
            }
            @Override
            public String name()
            {
                return "OctBillow";
            }
        };
    }
    
    public static AbstractOctavationModeFractal fromTURBULENCE()
    {
        return new AbstractOctavationModeFractal()
        {
            @Override
            public double fractalFunction(double _x)
            {
                return Math.abs(_x);
            }
            @Override
            public String name()
            {
                return "OctTurbulence";
            }
        };
    }
    
    public static AbstractOctavationModeFractal fromRIDGEDMULTI()
    {
        return new AbstractOctavationModeFractal()
        {
            @Override
            public double fractalFunction(double _x)
            {
                return (1.-Math.abs(_x))*(1.-Math.abs(_x));
            }
            @Override
            public String name()
            {
                return "OctRidgedMulti";
            }
        };
    }
    
    public static double calsFractalBounding(double _gain, int _octaves)
    {
        double fractalBounding = 0;
        double amplitude = _gain;
        for (int i = 0; i < _octaves; i++) {
            fractalBounding += amplitude;
            amplitude *= _gain;
        }
        return fractalBounding;
    }
    
    public abstract double fractalFunction(double _x);
    
    @Override
    public double _fractal1(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x)
    {
        double amplitude = gain;
        double output = 0;
        if (_vseed) {
            long seed = _noise.getSeed();
            for (int i = 0; i < octaves; i++) {
                output += amplitude * fractalFunction(_noise._noise1(seed++,frequency * x,_noise.getInterpolation()));
                frequency *= lacunarity;
                amplitude *= gain;
            }
        } else {
            for (int i = 0; i < octaves; i++) {
                output += amplitude * fractalFunction(_noise.noise1(frequency * x));
                frequency *= lacunarity;
                amplitude *= gain;
            }
        }
        return output / calsFractalBounding(gain,octaves);
    }
    
    @Override
    public double _fractal2(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y)
    {
        double amplitude = gain;
        double output = 0;
        if (_vseed) {
            long seed = _noise.getSeed();
            for (int i = 0; i < octaves; i++) {
                output += amplitude * fractalFunction(_noise._noise2(seed++,frequency * x,frequency * y,_noise.getInterpolation()));
                frequency *= lacunarity;
                amplitude *= gain;
            }
        } else {
            for (int i = 0; i < octaves; i++) {
                output += amplitude * fractalFunction(_noise.noise2(frequency * x, frequency * y));
                frequency *= lacunarity;
                amplitude *= gain;
            }
        }
        return output / calsFractalBounding(gain,octaves);
    }
    
    @Override
    public double _fractal3(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z)
    {
        double amplitude = gain;
        double output = 0;
        if (_vseed) {
            long seed = _noise.getSeed();
            for (int i = 0; i < octaves; i++) {
                output += amplitude * fractalFunction(_noise._noise3(seed++,frequency * x,frequency * y,frequency * z,_noise.getInterpolation()));
                frequency *= lacunarity;
                amplitude *= gain;
            }
        } else {
            for (int i = 0; i < octaves; i++) {
                output += amplitude * fractalFunction(_noise.noise3(frequency * x, frequency * y, frequency * z));
                frequency *= lacunarity;
                amplitude *= gain;
            }
        }
        return output / calsFractalBounding(gain,octaves);
    }
    
    @Override
    public double _fractal4(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z, double u)
    {
        double amplitude = gain;
        double output = 0;
        if (_vseed) {
            long seed = _noise.getSeed();
            for (int i = 0; i < octaves; i++) {
                output += amplitude * fractalFunction(_noise._noise4(seed++,frequency * x,frequency * y,frequency * z,frequency * u,_noise.getInterpolation()));
                frequency *= lacunarity;
                amplitude *= gain;
            }
        } else {
            for (int i = 0; i < octaves; i++) {
                output += amplitude * fractalFunction(_noise.noise4(frequency * x, frequency * y, frequency * z, frequency * u));
                frequency *= lacunarity;
                amplitude *= gain;
            }
        }
        return output / calsFractalBounding(gain,octaves);
    }
    
    @Override
    public double _fractal5(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z, double u, double v)
    {
        double amplitude = gain;
        double output = 0;
        if (_vseed) {
            long seed = _noise.getSeed();
            for (int i = 0; i < octaves; i++) {
                output += amplitude * fractalFunction(_noise._noise5(seed++,frequency * x,frequency * y,frequency * z,frequency * u,frequency * v,_noise.getInterpolation()));
                frequency *= lacunarity;
                amplitude *= gain;
            }
        } else {
            for (int i = 0; i < octaves; i++) {
                output += amplitude * fractalFunction(_noise.noise5(frequency * x, frequency * y, frequency * z, frequency * u, frequency * v));
                frequency *= lacunarity;
                amplitude *= gain;
            }
        }
        return output / calsFractalBounding(gain,octaves);
    }
    
    @Override
    public double _fractal6(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z, double u, double v, double w)
    {
        double amplitude = gain;
        double output = 0;
        if (_vseed) {
            long seed = _noise.getSeed();
            for (int i = 0; i < octaves; i++) {
                output += amplitude * fractalFunction(_noise._noise6(seed++,frequency * x,frequency * y,frequency * z,frequency * u,frequency * v, frequency * w,_noise.getInterpolation()));
                frequency *= lacunarity;
                amplitude *= gain;
            }
        } else {
            for (int i = 0; i < octaves; i++) {
                output += amplitude * fractalFunction(_noise.noise6(frequency * x, frequency * y, frequency * z, frequency * u, frequency * v, frequency * w));
                frequency *= lacunarity;
                amplitude *= gain;
            }
        }
        return output / calsFractalBounding(gain,octaves);
    }
}
