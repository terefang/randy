package com.github.terefang.randy.noise;

import com.github.terefang.randy.transf.ITransform;

public class BlendingNoise
        implements INoise
{
    interface BlendFunction
    {
        double blend(double _a, double _b, double _f);
    }
    
    INoise noiseA;
    INoise noiseB;
    
    double blendfactor;
    BlendFunction blendFunction;
    
    public BlendFunction getBlendFunction()
    {
        return blendFunction;
    }
    
    public void setBlendFunction(BlendFunction _blendFunction)
    {
        blendFunction = _blendFunction;
    }
    
    public INoise getNoiseA()
    {
        return noiseA;
    }
    
    public void setNoiseA(INoise _noiseA)
    {
        noiseA = _noiseA;
    }
    
    public INoise getNoiseB()
    {
        return noiseB;
    }
    
    public void setNoiseB(INoise _noiseB)
    {
        noiseB = _noiseB;
    }
    
    public double getBlendfactor()
    {
        return blendfactor;
    }
    
    public void setBlendfactor(double _blendfactor)
    {
        blendfactor = _blendfactor;
    }
    
    @Override
    public String name()
    {
        return String.format("BlendingNoise~%s~%s~i=%f", noiseA.name(), noiseB.name(), blendfactor);
    }
    
    
    @Override
    public ITransform getTransform()
    {
        return noiseA.getTransform();
    }
    
    @Override
    public void setTransform(ITransform transform)
    {
        noiseA.setTransform(transform);
        noiseB.setTransform(transform);
    }
    
    @Override
    public void setInterpolation(int _i)
    {
        noiseA.setInterpolation(_i);
        noiseB.setInterpolation(_i);
    }
    
    @Override
    public int getInterpolation()
    {
        return noiseA.getInterpolation();
    }
    
    @Override
    public void setSeed(long s)
    {
        noiseA.setSeed(s);
        noiseB.setSeed(s);
    }
    
    @Override
    public long getSeed()
    {
        return noiseA.getSeed();
    }
    
    @Override
    public void setSeed(String _text)
    {
        noiseA.setSeed(_text);
        noiseB.setSeed(_text);
    }
    
    @Override
    public void setSeed(byte[] _text)
    {
        noiseA.setSeed(_text);
        noiseB.setSeed(_text);
    }
    
    @Override
    public double getMutation()
    {
        return noiseA.getMutation();
    }
    
    @Override
    public void setMutation(double mutation)
    {
        noiseA.setMutation(mutation);
        noiseB.setMutation(mutation);
    }
    
    @Override
    public double getSharpness()
    {
        return noiseA.getSharpness();
    }
    
    @Override
    public void setSharpness(double foamSharpness)
    {
        noiseA.setSharpness(foamSharpness);
        noiseB.setSharpness(foamSharpness);
    }
    
    @Override
    public double getHarshness()
    {
        return noiseA.getHarshness();
    }
    
    @Override
    public void setHarshness(double harshness)
    {
        noiseA.setHarshness(harshness);
        noiseB.setHarshness(harshness);
    }
    
    @Override
    public double _noise1(long seed, double x, int interpolation)
    {
        return blendFunction.blend(noiseA._noise1(seed, x, interpolation), noiseB._noise1(seed, x, interpolation), blendfactor);
    }
    
    @Override
    public double _noise2(long seed, double x, double y, int interpolation)
    {
        return blendFunction.blend(noiseA._noise2(seed, x, y, interpolation), noiseB._noise2(seed, x,y, interpolation), blendfactor);
    }
    
    @Override
    public double _noise3(long seed, double x, double y, double z, int interpolation)
    {
        return blendFunction.blend(noiseA._noise3(seed, x, y, z, interpolation), noiseB._noise3(seed, x,y,z, interpolation), blendfactor);
    }
    
    @Override
    public double _noise4(long seed, double x, double y, double z, double u, int interpolation)
    {
        return blendFunction.blend(noiseA._noise4(seed, x, y, z,u, interpolation), noiseB._noise4(seed, x,y,z,u, interpolation), blendfactor);
    }
    
    @Override
    public double _noise5(long seed, double x, double y, double z, double u, double v, int interpolation)
    {
        return blendFunction.blend(noiseA._noise5(seed, x, y, z,u,v, interpolation), noiseB._noise5(seed, x,y,z,u,v, interpolation), blendfactor);
    }
    
    @Override
    public double _noise6(long seed, double x, double y, double z, double u, double v, double w, int interpolation)
    {
        return blendFunction.blend(noiseA._noise6(seed, x, y, z,u,v,w, interpolation), noiseB._noise6(seed, x,y,z,u,v,w, interpolation), blendfactor);
    }
    
    @Override
    public double _noise1n(long seed, double x, int interpolation)
    {
        return blendFunction.blend(noiseA._noise1n(seed, x, interpolation), noiseB._noise1n(seed, x, interpolation), blendfactor);
    }
    
    @Override
    public double _noise2n(long seed, double x, double y, int interpolation)
    {
        return blendFunction.blend(noiseA._noise2n(seed, x, y, interpolation), noiseB._noise2n(seed, x,y, interpolation), blendfactor);
    }
    
    @Override
    public double _noise3n(long seed, double x, double y, double z, int interpolation)
    {
        return blendFunction.blend(noiseA._noise3n(seed, x, y, z, interpolation), noiseB._noise3n(seed, x,y,z, interpolation), blendfactor);
    }
    
    @Override
    public double _noise4n(long seed, double x, double y, double z, double u, int interpolation)
    {
        return blendFunction.blend(noiseA._noise4n(seed, x, y, z,u, interpolation), noiseB._noise4n(seed, x,y,z,u, interpolation), blendfactor);
    }
    
    @Override
    public double _noise5n(long seed, double x, double y, double z, double u, double v, int interpolation)
    {
        return blendFunction.blend(noiseA._noise5n(seed, x, y, z,u,v, interpolation), noiseB._noise5n(seed, x,y,z,u,v, interpolation), blendfactor);
    }
    
    @Override
    public double _noise6n(long seed, double x, double y, double z, double u, double v, double w, int interpolation)
    {
        return blendFunction.blend(noiseA._noise6n(seed, x, y, z,u,v,w, interpolation), noiseB._noise6n(seed, x,y,z,u,v,w, interpolation), blendfactor);
    }
    
}
