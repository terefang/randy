package com.github.terefang.randy.transf;

public interface ITransform
{
    default public String name() {
        return this.getClass().getSimpleName();
    }
    public double transform(double x);
    public double transform(double a, double b);
    public double transform(double a, double b, double c);

    default public double transformn(double _result)
    {
        // result comes in 0..1 output must be 0..1
        _result = (_result * 2f) - 1f;
        _result = transform(_result);
        _result = (_result*.5f) + .5f;
        return _result;
    }
    default public double transformn(double _result, double _1)
    {
        // result comes in 0..1 output must be 0..1
        _result = (_result * 2f) - 1f;
        _result = transform(_result, _1);
        _result = (_result*.5f) + .5f;
        return _result;
    }
    default public double transformn(double _result, double _1, double _2)
    {
        // result comes in 0..1 output must be 0..1
        _result = (_result * 2f) - 1f;
        _result = transform(_result, _1, _2);
        _result = (_result*.5f) + .5f;
        return _result;
    }

    public static enum TransformType
    {
        T_0NONE,
        T_SINE,
        T_COSINE,
        T_SINE_2,
        T_COSINE_2,
        T_SQ_SINE,
        T_SQ_COSINE,
        T_SQUARE_ROOT,
        T_CUBE_ROOT,
        T_SQUARE,
        T_CUBE,
        T_QUART,
        T_ABS,
        T_ABS1M,
        T_INVERT,
        T_INVNORM,
        T_EXP,
        T_EX,
        T_IHERMITE,
        T_IQUINTIC,
        T_IBARRON,
        T_ISPLOBBLE,
        T_ISPLOBBLEQ,
        T_QMF,
        T_BINARY,
        T_CLAMP01,
        T_CLAMP10,
        T_LEVEL5,
        T_LEVEL10,
        T_LEVEL4,
        T_LEVEL8,
        T_LEVEL16,
        T_LEVEL32,
        T_COLLATZ_1K,
        T_COLLATZ_4K,
        T_COLLATZ_SINE_4K,
        T_COLLATZ_COSINE_4K,
        T_COLLATZ_INVNORM_4K,
        T_NEAR
        ;
    }
}
