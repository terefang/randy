package com.github.terefang.randy.fractal.impl;

import com.github.terefang.randy.fractal.AbstractFractal;
import com.github.terefang.randy.fractal.IFractal;
import com.github.terefang.randy.noise.INoise;

// https://thingonitsown.blogspot.com/2019/02/exo-terrain.html
/*
function getheight(ix,iy) {
    striation1 = 8*noise(ix/400,iy/400)
    distort1 = noise(ix/40,iy/40)
    noise1 = noise(ix/100,iy/100,striation1+distort1)
    striation2 = 8*noise(ix/800,iy/800)
    distort2 = noise(ix/80,iy/80)
    noise2 = noise(ix/200,iy/200,striation2+distort2)*1.5
    roughness = noise(ix/600,iy/600)-.3
    bumpdistort = noise(ix/20,iy/20)
    bumpnoise = noise(ix/50,iy/50,2*bumpdistort)
    return(noise1+sq(sq(noise2))+roughness*bumpnoise-.8)
}
*/
public class ExoTerrainFractal extends AbstractFractal implements IFractal
{
    boolean doBumps = true;

    public boolean isBumps() {
        return doBumps;
    }

    public void setBumps(boolean dobumps) {
        this.doBumps = dobumps;
    }

    @Override
    public String name() {
        return super.name() + (doBumps ? "" : "NoBumps");
    }

    @Override
    public double _fractal1(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x)
    {
        x *= frequency;

        double pwr = gain;
        double pwHL = Math.pow(lacunarity, -H);

        double _striation1 = 8.*_noise.noise1(_noise.getSeed()+1,x/4.);
        double _distort1 = _noise.noise1(_noise.getSeed()+2,x/.4);
        double _noise1 = _noise.noise2(x,_striation1+_distort1);

        for (int i = 1; i < octaves; i++) {
            double _striation2 = 8.*_noise.noise1(_noise.getSeed()+3,x/8.);
            double _distort2 = _noise.noise1(_noise.getSeed()+4,x/.8);
            double _noise2 = _noise.noise2(_noise.getSeed()+5,x/2.,_striation2+_distort2)*1.5;
            if(doBumps)
            {
                double _roughness = _noise.noise1(_noise.getSeed()+6,x/6.)-.3;
                double _bumpdistort = _noise.noise1(_noise.getSeed()+7,x/.2);
                double _bumpnoise = _noise.noise2(_noise.getSeed()+8,x/.5,2.*_bumpdistort);
                _noise1+=(_noise2*_noise2*_noise2*_noise2+_roughness*_bumpnoise+offset)*pwr;
            }
            else
            {
                _noise1+=(_noise2*_noise2*_noise2*_noise2+offset)*pwr;
            }
            x *= lacunarity;
            pwr /= -pwHL;
        }

        return(_noise1);
    }

    @Override
    public double _fractal2(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y) {
        x *= frequency;
        y *= frequency;

        double pwr = gain;
        double pwHL = Math.pow(lacunarity, -H);

        double _striation1 = 8.*_noise.noise2(_noise.getSeed()+1,x/4., y/4.);
        double _distort1 = _noise.noise2(_noise.getSeed()+2,x/.4, y/.4);
        double _noise1 = _noise.noise3(x,y,_striation1+_distort1);

        for (int i = 1; i < octaves; i++) {
            double _striation2 = 8.*_noise.noise2(_noise.getSeed()+3,x/8.,y/8.);
            double _distort2 = _noise.noise2(_noise.getSeed()+4,x/.8,y/.8);
            double _noise2 = _noise.noise3(_noise.getSeed()+5,x/2.,y/2.,_striation2+_distort2)*1.5;
            if(doBumps)
            {
                double _roughness = _noise.noise2(_noise.getSeed()+6,x/6.,y/6.)-.3;
                double _bumpdistort = _noise.noise2(_noise.getSeed()+7,x/.2,y/.2);
                double _bumpnoise = _noise.noise3(_noise.getSeed()+8,x/.5,y/.5,2.*_bumpdistort);
                _noise1+=(_noise2*_noise2*_noise2*_noise2+_roughness*_bumpnoise+offset)*pwr;
            }
            else
            {
                _noise1+=(_noise2*_noise2*_noise2*_noise2+offset)*pwr;
            }
            x *= lacunarity;
            y *= lacunarity;
            pwr /= -pwHL;
        }

        return(_noise1);
    }

    @Override
    public double _fractal3(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z) {
        x *= frequency;
        y *= frequency;
        z *= frequency;

        double pwr = gain;
        double pwHL = Math.pow(lacunarity, -H);

        double _striation1 = 8.*_noise.noise3(_noise.getSeed()+1,x/4., y/4., z/4.);
        double _distort1 = _noise.noise3(_noise.getSeed()+2,x/.4, y/.4, z/.4);
        double _noise1 = _noise.noise4(x,y,z,_striation1+_distort1);
        for (int i = 1; i < octaves; i++)
        {
            double _striation2 = 8.*_noise.noise3(_noise.getSeed()+3,x/8.,y/8.,z/8.);
            double _distort2 = _noise.noise3(_noise.getSeed()+4,x/.8,y/.8,z/.8);
            double _noise2 = _noise.noise4(_noise.getSeed()+5,x/2.,y/2.,z/2.,_striation2+_distort2)*1.5;
            if(doBumps)
            {
                double _roughness = _noise.noise3(_noise.getSeed()+6,x/6.,y/6.,z/6.)-.3;
                double _bumpdistort = _noise.noise3(_noise.getSeed()+7,x/.2,y/.2,z/.2);
                double _bumpnoise = _noise.noise4(_noise.getSeed()+8,x/.5,y/.5,z/.5,2.*_bumpdistort);
                _noise1+=(_noise2*_noise2*_noise2*_noise2+_roughness*_bumpnoise+offset)*pwr;
            }
            else
            {
                _noise1+=(_noise2*_noise2*_noise2*_noise2+offset)*pwr;
            }
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            pwr /= -pwHL;
        }

        return(_noise1);
    }

    @Override
    public double _fractal4(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z, double u) {
        x *= frequency;
        y *= frequency;
        z *= frequency;
        u *= frequency;

        double pwr = gain;
        double pwHL = Math.pow(lacunarity, -H);

        double _striation1 = 8.*_noise.noise4(x/4., y/4., z/4., u/4.);
        double _distort1 = _noise.noise4(x/.4, y/.4, z/.4, u/.4);
        double _noise1 = _noise.noise5(x,y,z,u,_striation1+_distort1);
        for (int i = 1; i < octaves; i++)
        {
            double _striation2 = 8.*_noise.noise4(x/8.,y/8.,z/8.,u/8.);
            double _distort2 = _noise.noise4(x/.8,y/.8,z/.8,u/.8);
            double _noise2 = _noise.noise5(x/2.,y/2.,z/2.,u/2.,_striation2+_distort2)*1.5;
            if(doBumps)
            {
                double _roughness = _noise.noise4(x/6.,y/6.,z/6.,u/6.)-.3;
                double _bumpdistort = _noise.noise4(x/.2,y/.2,z/.2,u/.2);
                double _bumpnoise = _noise.noise5(x/.5,y/.5,z/.5,u/.5,2.*_bumpdistort);
                _noise1+=(_noise2*_noise2*_noise2*_noise2+_roughness*_bumpnoise+offset)*pwr;
            }
            else
            {
                _noise1+=(_noise2*_noise2*_noise2*_noise2+offset)*pwr;
            }
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            u *= lacunarity;
            pwr /= -pwHL;
        }

        return(_noise1);
    }

    @Override
    public double _fractal5(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z, double u, double v) {
        x *= frequency;
        y *= frequency;
        z *= frequency;
        u *= frequency;
        v *= frequency;

        double pwr = gain;
        double pwHL = Math.pow(lacunarity, -H);

        double _striation1 = 8.*_noise.noise5(x/4., y/4., z/4., u/4., v/4.);
        double _distort1 = _noise.noise5(x/.4, y/.4, z/.4, u/.4, v/.4);
        double _noise1 = _noise.noise6(x,y,z,u,v,_striation1+_distort1);
        for (int i = 1; i < octaves; i++)
        {
            double _striation2 = 8.*_noise.noise5(x/8.,y/8.,z/8.,u/8.,v/8.);
            double _distort2 = _noise.noise5(x/.8,y/.8,z/.8,u/.8,v/.8);
            double _noise2 = _noise.noise6(x/2.,y/2.,z/2.,u/2.,v/2.,_striation2+_distort2)*1.5;
            if(doBumps)
            {
                double _roughness = _noise.noise5(x/6.,y/6.,z/6.,u/6.,v/6.)-.3;
                double _bumpdistort = _noise.noise5(x/.2,y/.2,z/.2,u/.2,v/.2);
                double _bumpnoise = _noise.noise6(x/.5,y/.5,z/.5,u/.5,v/.5,2.*_bumpdistort);
                _noise1+=(_noise2*_noise2*_noise2*_noise2+_roughness*_bumpnoise+offset)*pwr;
            }
            else
            {
                _noise1+=(_noise2*_noise2*_noise2*_noise2+offset)*pwr;
            }
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            u *= lacunarity;
            v *= lacunarity;
            pwr /= -pwHL;
        }

        return(_noise1);
    }

    @Override
    public double _fractal6(INoise _noise, double offset, double H, int octaves, double frequency, double lacunarity, double gain, boolean _vseed, double x, double y, double z, double u, double v, double w)
    {
        x *= frequency;
        y *= frequency;
        z *= frequency;
        w *= frequency;
        u *= frequency;
        v *= frequency;

        double pwr = gain;
        double pwHL = Math.pow(lacunarity, -H);

        double _striation1 = 8.*_noise.noise6(x/4., y/4., z/4., u/4., v/4., w/4.);
        double _distort1 = _noise.noise6(x/.4, y/.4, z/.4, u/.4, v/.4, w/.4);
        double _noise1 = _noise.noise6(x,y,z,u,v,w+_striation1+_distort1);
        for (int i = 1; i < octaves; i++)
        {
            double _striation2 = 8.*_noise.noise6(x/8.,y/8.,z/8.,u/8.,v/8.,w/8.);
            double _distort2 = _noise.noise6(x/.8,y/.8,z/.8,u/.8,v/.8,w/.8);
            double _noise2 = _noise.noise6(x/2.,y/2.,z/2.,u/2.,v/2.,(w/2.)+_striation2+_distort2)*1.5;
            double _roughness = _noise.noise6(x/6.,y/6.,z/6.,u/6.,v/6.,w/6.)-.3;
            double _bumpdistort = _noise.noise6(x/.2,y/.2,z/.2,u/.2,v/.2,w/.2);
            double _bumpnoise = _noise.noise6(x/.5,y/.5,z/.5,u/.5,v/.5,(w/.5)+2.*_bumpdistort);
            _noise1+=(_noise2*_noise2*_noise2*_noise2+_roughness*_bumpnoise+offset)*pwr;
            x *= lacunarity;
            y *= lacunarity;
            z *= lacunarity;
            u *= lacunarity;
            v *= lacunarity;
            w *= lacunarity;
            pwr /= -pwHL;
        }

        return(_noise1);
    }
}
