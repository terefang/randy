package com.github.terefang.randy.transf;

public class AdditionTransform implements ITransform
{
    @Override
    public double transform(double x) {
        return x;
    }

    @Override
    public double transform(double a, double b) {
        return a+b;
    }

    @Override
    public double transform(double a, double b, double c) {
        return a+b+c;
    }
}
