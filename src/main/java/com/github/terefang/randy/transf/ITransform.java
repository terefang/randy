package com.github.terefang.randy.transf;

public interface ITransform
{
    default public String name() {
        return this.getClass().getSimpleName();
    }
    public double transform(double x);
    public double transform(double a, double b);
    public double transform(double a, double b, double c);
}
