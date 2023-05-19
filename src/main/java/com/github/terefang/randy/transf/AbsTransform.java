package com.github.terefang.randy.transf;

public class AbsTransform implements ITransform
{
    @Override
    public double transform(double x) {
        return Math.abs(x);
    }

    @Override
    public double transform(double a, double b) {
        return Math.abs(a)+Math.abs(b);
    }

    @Override
    public double transform(double a, double b, double c) {
        return Math.abs(a)+Math.abs(b)+Math.abs(c);
    }
}
