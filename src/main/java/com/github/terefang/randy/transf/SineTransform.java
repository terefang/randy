package com.github.terefang.randy.transf;

import com.github.terefang.randy.noise.NoiseUtil;

public class SineTransform implements ITransform
{
    @Override
    public double transform(double x) {
        return NoiseUtil.sin(x);
    }

    @Override
    public double transform(double a, double b) {
        return NoiseUtil.sin(a)+NoiseUtil.sin(b);
    }

    @Override
    public double transform(double a, double b, double c) {
        return NoiseUtil.sin(a)+NoiseUtil.sin(b)+NoiseUtil.sin(c);
    }
}
