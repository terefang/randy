package com.github.terefang.randy.transf;

import com.github.terefang.randy.noise.NoiseUtil;

public class CosineTransform implements ITransform
{
    @Override
    public double transform(double x) {
        return NoiseUtil.cos(x);
    }

    @Override
    public double transform(double a, double b) {
        return NoiseUtil.cos(a)+NoiseUtil.cos(b);
    }

    @Override
    public double transform(double a, double b, double c) {
        return NoiseUtil.cos(a)+NoiseUtil.cos(b)+NoiseUtil.cos(c);
    }
}
