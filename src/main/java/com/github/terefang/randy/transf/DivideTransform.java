package com.github.terefang.randy.transf;

public class DivideTransform implements ITransform
{
    @Override
    public double transform(double x) {
        return 1./x;
    }

    @Override
    public double transform(double a, double b) {
        if(b==0.0) b=0.00128;
        return a/b;
    }

    @Override
    public double transform(double a, double b, double c) {
        if(c==0.0) c=0.00128;
        return transform(a,b)/c;
    }
}
