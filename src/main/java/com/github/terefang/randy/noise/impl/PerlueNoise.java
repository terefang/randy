package com.github.terefang.randy.noise.impl;

import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.NoiseUtil;

public class PerlueNoise extends NoiseUtil implements INoise
{

    @Override
    public String name()
    {
        switch (this.getInterpolation())
        {
            case COSINE:
                return super.name()+"Cosine";
            case QUINTIC:
                return super.name()+"Quintic";
            case HERMITE:
                return super.name()+"Hermite";
            case RADIAN:
                return super.name()+"Radian";
            case LINEAR:
            default:
                return super.name()+"Linear";
        }
    }

    @Override
    public double _noise1(long seed, double x, int interpolation) {
        return singlePerlue(interpolation, false, makeSeedInt(seed), x, this.getMutation());
    }

    @Override
    public double _noise2(long seed, double x, double y, int interpolation) {
        return singlePerlue(interpolation, false, makeSeedInt(seed), x,y, this.getMutation());
    }

    @Override
    public double _noise3(long seed, double x, double y, double z, int interpolation) {
        return singlePerlue(interpolation, false, makeSeedInt(seed), x,y,z, this.getMutation());
    }

    @Override
    public double _noise4(long seed, double x, double y, double z, double u, int interpolation) {
        return singlePerlue(interpolation, false, makeSeedInt(seed), x,y,z,u, this.getMutation());
    }

    @Override
    public double _noise5(long seed, double x, double y, double z, double u, double v, int interpolation) {
        return singlePerlue(interpolation, false, makeSeedInt(seed), x,y,z,u,v, this.getMutation());
    }

    @Override
    public double _noise6(long seed, double x, double y, double z, double u, double v, double w, int interpolation) {
        return singlePerlue(interpolation, false, makeSeedInt(seed), x,y,z,u,v,w+this.getMutation());
    }

    @Override
    public double _noise1n(long seed, double x, int interpolation) {
        return singlePerlue(interpolation, true, makeSeedInt(seed), x, this.getMutation());
    }

    @Override
    public double _noise2n(long seed, double x, double y, int interpolation) {
        return singlePerlue(interpolation, true, makeSeedInt(seed), x,y, this.getMutation());
    }

    @Override
    public double _noise3n(long seed, double x, double y, double z, int interpolation) {
        return singlePerlue(interpolation, true, makeSeedInt(seed), x,y,z, this.getMutation());
    }

    @Override
    public double _noise4n(long seed, double x, double y, double z, double u, int interpolation) {
        return singlePerlue(interpolation, true, makeSeedInt(seed), x,y,z,u, this.getMutation());
    }

    @Override
    public double _noise5n(long seed, double x, double y, double z, double u, double v, int interpolation) {
        return singlePerlue(interpolation, true, makeSeedInt(seed), x,y,z,u,v, this.getMutation());
    }

    @Override
    public double _noise6n(long seed, double x, double y, double z, double u, double v, double w, int interpolation) {
        return singlePerlue(interpolation, true, makeSeedInt(seed), x,y,z,u,v,w+this.getMutation());
    }


    // ----------------------------------------------------------------------------

    private static final int X_2 = 0x1827F5, Y_2 = 0x123C21;
    private static final int X_3 = 0x1A36A9, Y_3 = 0x157931, Z_3 = 0x119725;
    private static final int X_4 = 0x1B69E1, Y_4 = 0x177C0B, Z_4 = 0x141E5D, W_4 = 0x113C31;
    private static final int X_5 = 0x1C3361, Y_5 = 0x18DA39, Z_5 = 0x15E6DB, W_5 = 0x134D29, U_5 = 0x110281;
    private static final int X_6 = 0x1CC1C5, Y_6 = 0x19D7AF, Z_6 = 0x173935, W_6 = 0x14DEAF, U_6 = 0x12C139, V_6 = 0x10DAA3;

    // ----------------------------------------------------------------------------

    public static final double SCALE2 = 1.41421330; //towardsZero(1f/ (double) Math.sqrt(2f / 4f));
    public static final double SCALE3 = 1.15470030; //towardsZero(1f/ (double) Math.sqrt(3f / 4f));
    public static final double SCALE4 = 0.99999990; //towardsZero(1f)                            ;
    public static final double SCALE5 = 0.89442706; //towardsZero(1f/ (double) Math.sqrt(5f / 4f));
    public static final double SCALE6 = 0.81649643; //towardsZero(1f/ (double) Math.sqrt(6f / 4f));

    public static final double EQ_ADD_2 = 1.0 / 0.85;
    public static final double EQ_ADD_3 = 0.8 / 0.85;
    public static final double EQ_ADD_4 = 0.6 / 0.85;
    public static final double EQ_ADD_5 = 0.4 / 0.85;
    public static final double EQ_ADD_6 = 0.2 / 0.85;

    public static final double EQ_MUL_2 = 1.2535664;
    public static final double EQ_MUL_3 = 1.2071217;
    public static final double EQ_MUL_4 = 1.1588172;
    public static final double EQ_MUL_5 = 1.1084094;
    public static final double EQ_MUL_6 = 1.0555973;
    
    // 2d perlin
    public static final double singlePerlue(int interpolation, boolean normalize, int seed,  double x, double y) {
        final int
                xi = (int)Math.floor(x), x0 = xi * X_2,
                yi = (int)Math.floor(y), y0 = yi * Y_2;
        final double xf = x - xi, yf = y - yi;

        double xa, ya;
        switch (interpolation) {
            default:
            case LINEAR:
                xa = xf;
                ya = yf;
                break;
            case HERMITE:
                xa = hermiteInterpolator(xf);
                ya = hermiteInterpolator(yf);
                break;
            case QUINTIC:
                xa = quintic2Interpolator(xf);
                ya = quintic2Interpolator(yf);
                break;
            case RADIAN:
                xa = radianInterpolator(xf);
                ya = radianInterpolator(yf);
                break;
            case COSINE:
                xa = cosineInterpolator(xf);
                ya = cosineInterpolator(yf);
                break;
        }

        double _v = equalize(lerp(lerp(grad2Coord2D(seed, x0, y0, xf, yf), grad2Coord2D(seed, x0+X_2, y0, xf - 1, yf), xa),
                lerp(grad2Coord2D(seed, x0, y0+Y_2, xf, yf-1), grad2Coord2D(seed, x0+X_2, y0+Y_2, xf - 1, yf - 1), xa),
                ya) * SCALE2, EQ_ADD_2, EQ_MUL_2);//* 0.875;// * 1.4142;
        if(normalize) _v = (_v*.5)+.5;
        return _v;
    }

    // 3d perlin
    public static final double singlePerlue(int interpolation, boolean normalize, int seed,  double x, double y, double z) {
        final int
                xi = (int)Math.floor(x), x0 = xi * X_3,
                yi = (int)Math.floor(y), y0 = yi * Y_3,
                zi = (int)Math.floor(z), z0 = zi * Z_3;
        final double xf = x - xi, yf = y - yi, zf = z - zi;

        double xa, ya, za;
        switch (interpolation) {
            default:
            case LINEAR:
                xa = xf;
                ya = yf;
                za = zf;
                break;
            case HERMITE:
                xa = hermiteInterpolator(xf);
                ya = hermiteInterpolator(yf);
                za = hermiteInterpolator(zf);
                break;
            case QUINTIC:
                xa = quintic2Interpolator(xf);
                ya = quintic2Interpolator(yf);
                za = quintic2Interpolator(zf);
                break;
            case RADIAN:
                xa = radianInterpolator(xf);
                ya = radianInterpolator(yf);
                za = radianInterpolator(zf);
                break;
            case COSINE:
                xa = cosineInterpolator(xf);
                ya = cosineInterpolator(yf);
                za = cosineInterpolator(zf);
                break;
        }

        double _v = equalize(
                lerp(
                        lerp(
                                lerp(
                                        grad2Coord3D(seed, x0, y0, z0, xf, yf, zf),
                                        grad2Coord3D(seed, x0+X_3, y0, z0, xf - 1, yf, zf),
                                        xa),
                                lerp(
                                        grad2Coord3D(seed, x0, y0+Y_3, z0, xf, yf-1, zf),
                                        grad2Coord3D(seed, x0+X_3, y0+Y_3, z0, xf - 1, yf - 1, zf),
                                        xa),
                                ya),
                        lerp(
                                lerp(
                                        grad2Coord3D(seed, x0, y0, z0+Z_3, xf, yf, zf-1),
                                        grad2Coord3D(seed, x0+X_3, y0, z0+Z_3, xf - 1, yf, zf-1),
                                        xa),
                                lerp(
                                        grad2Coord3D(seed, x0, y0+Y_3, z0+Z_3, xf, yf-1, zf-1),
                                        grad2Coord3D(seed, x0+X_3, y0+Y_3, z0+Z_3, xf - 1, yf - 1, zf-1),
                                        xa),
                                ya),
                        za) * SCALE3, EQ_ADD_3, EQ_MUL_3); // 1.0625f

        if(normalize) _v = (_v*.5)+.5;
        return _v;
    }

    // 4d perlin
    public static final double singlePerlue(int interpolation, boolean normalize, int seed,  double x, double y, double z, double w) {
        final int
                xi = (int)Math.floor(x), x0 = xi * X_4,
                yi = (int)Math.floor(y), y0 = yi * Y_4,
                zi = (int)Math.floor(z), z0 = zi * Z_4,
                wi = (int)Math.floor(w), w0 = wi * W_4;
        final double xf = x - xi, yf = y - yi, zf = z - zi, wf = w - wi;

        double xa, ya, za, wa;
        switch (interpolation) {
            default:
            case LINEAR:
                xa = xf;
                ya = yf;
                za = zf;
                wa = wf;
                break;
            case HERMITE:
                xa = hermiteInterpolator(xf);
                ya = hermiteInterpolator(yf);
                za = hermiteInterpolator(zf);
                wa = hermiteInterpolator(wf);
                break;
            case QUINTIC:
                xa = quintic2Interpolator(xf);
                ya = quintic2Interpolator(yf);
                za = quintic2Interpolator(zf);
                wa = quintic2Interpolator(wf);
                break;
            case RADIAN:
                xa = radianInterpolator(xf);
                ya = radianInterpolator(yf);
                za = radianInterpolator(zf);
                wa = radianInterpolator(wf);
                break;
            case COSINE:
                xa = cosineInterpolator(xf);
                ya = cosineInterpolator(yf);
                za = cosineInterpolator(zf);
                wa = cosineInterpolator(wf);
                break;
        }


        double _v = equalize(
                lerp(
                        lerp(
                                lerp(
                                        lerp(
                                                grad2Coord4D(seed, x0, y0, z0, w0, xf, yf, zf, wf),
                                                grad2Coord4D(seed, x0+X_4, y0, z0, w0, xf - 1, yf, zf, wf),
                                                xa),
                                        lerp(
                                                grad2Coord4D(seed, x0, y0+Y_4, z0, w0, xf, yf-1, zf, wf),
                                                grad2Coord4D(seed, x0+X_4, y0+Y_4, z0, w0, xf - 1, yf - 1, zf, wf),
                                                xa),
                                        ya),
                                lerp(
                                        lerp(
                                                grad2Coord4D(seed, x0, y0, z0+Z_4, w0, xf, yf, zf-1, wf),
                                                grad2Coord4D(seed, x0+X_4, y0, z0+Z_4, w0, xf - 1, yf, zf-1, wf),
                                                xa),
                                        lerp(
                                                grad2Coord4D(seed, x0, y0+Y_4, z0+Z_4, w0, xf, yf-1, zf-1, wf),
                                                grad2Coord4D(seed, x0+X_4, y0+Y_4, z0+Z_4, w0, xf - 1, yf - 1, zf-1, wf),
                                                xa),
                                        ya),
                                za),
                        lerp(
                                lerp(
                                        lerp(
                                                grad2Coord4D(seed, x0, y0, z0, w0+W_4, xf, yf, zf, wf - 1),
                                                grad2Coord4D(seed, x0+X_4, y0, z0, w0+W_4, xf - 1, yf, zf, wf - 1),
                                                xa),
                                        lerp(
                                                grad2Coord4D(seed, x0, y0+Y_4, z0, w0+W_4, xf, yf-1, zf, wf - 1),
                                                grad2Coord4D(seed, x0+X_4, y0+Y_4, z0, w0+W_4, xf - 1, yf - 1, zf, wf - 1),
                                                xa),
                                        ya),
                                lerp(
                                        lerp(
                                                grad2Coord4D(seed, x0, y0, z0+Z_4, w0+W_4, xf, yf, zf-1, wf - 1),
                                                grad2Coord4D(seed, x0+X_4, y0, z0+Z_4, w0+W_4, xf - 1, yf, zf-1, wf - 1),
                                                xa),
                                        lerp(
                                                grad2Coord4D(seed, x0, y0+Y_4, z0+Z_4, w0+W_4, xf, yf-1, zf-1, wf - 1),
                                                grad2Coord4D(seed, x0+X_4, y0+Y_4, z0+Z_4, w0+W_4, xf - 1, yf - 1, zf-1, wf - 1),
                                                xa),
                                        ya),
                                za),
                        wa) * SCALE4, EQ_ADD_4, EQ_MUL_4);//0.555f);
        if(normalize) _v = (_v*.5)+.5;
        return _v;
    }

    // 5d perlin
    public static final double singlePerlue(int interpolation, boolean normalize, int seed,  double x, double y, double z, double w, double u) {
        final int
                xi = (int)Math.floor(x), x0 = xi * X_5,
                yi = (int)Math.floor(y), y0 = yi * Y_5,
                zi = (int)Math.floor(z), z0 = zi * Z_5,
                wi = (int)Math.floor(w), w0 = wi * W_5,
                ui = (int)Math.floor(u), u0 = ui * U_5;
        final double xf = x - xi, yf = y - yi, zf = z - zi, wf = w - wi, uf = u - ui;

        double xa, ya, za, wa, ua;
        switch (interpolation) {
            default:
            case LINEAR:
                xa = xf;
                ya = yf;
                za = zf;
                wa = wf;
                ua = uf;
                break;
            case HERMITE:
                xa = hermiteInterpolator(xf);
                ya = hermiteInterpolator(yf);
                za = hermiteInterpolator(zf);
                wa = hermiteInterpolator(wf);
                ua = hermiteInterpolator(uf);
                break;
            case QUINTIC:
                xa = quintic2Interpolator(xf);
                ya = quintic2Interpolator(yf);
                za = quintic2Interpolator(zf);
                wa = quintic2Interpolator(wf);
                ua = quintic2Interpolator(uf);
                break;
            case RADIAN:
                xa = radianInterpolator(xf);
                ya = radianInterpolator(yf);
                za = radianInterpolator(zf);
                wa = radianInterpolator(wf);
                ua = radianInterpolator(uf);
                break;
            case COSINE:
                xa = cosineInterpolator(xf);
                ya = cosineInterpolator(yf);
                za = cosineInterpolator(zf);
                wa = cosineInterpolator(wf);
                ua = cosineInterpolator(uf);
                break;
        }

        double _v = equalize(
                lerp(lerp(
                                lerp(
                                        lerp(
                                                lerp(grad2Coord5D(seed, x0, y0, z0, w0, u0, xf, yf, zf, wf, uf),
                                                        grad2Coord5D(seed, x0+X_5, y0, z0, w0, u0, xf-1, yf, zf, wf, uf), xa),
                                                lerp(grad2Coord5D(seed, x0, y0+Y_5, z0, w0, u0, xf, yf-1, zf, wf, uf),
                                                        grad2Coord5D(seed, x0+X_5, y0+Y_5, z0, w0, u0, xf-1, yf-1, zf, wf, uf), xa),
                                                ya),
                                        lerp(
                                                lerp(grad2Coord5D(seed, x0, y0, z0+Z_5, w0, u0, xf, yf, zf-1, wf, uf),
                                                        grad2Coord5D(seed, x0+X_5, y0, z0+Z_5, w0, u0, xf-1, yf, zf-1, wf, uf), xa),
                                                lerp(grad2Coord5D(seed, x0, y0+Y_5, z0+Z_5, w0, u0, xf, yf-1, zf-1, wf, uf),
                                                        grad2Coord5D(seed, x0+X_5, y0+Y_5, z0+Z_5, w0, u0, xf-1, yf-1, zf-1, wf, uf), xa),
                                                ya),
                                        za),
                                lerp(
                                        lerp(
                                                lerp(grad2Coord5D(seed, x0, y0, z0, w0+W_5, u0, xf, yf, zf, wf-1, uf),
                                                        grad2Coord5D(seed, x0+X_5, y0, z0, w0+W_5, u0, xf-1, yf, zf, wf-1, uf), xa),
                                                lerp(grad2Coord5D(seed, x0, y0+Y_5, z0, w0+W_5, u0, xf, yf-1, zf, wf-1, uf),
                                                        grad2Coord5D(seed, x0+X_5, y0+Y_5, z0, w0+W_5, u0, xf-1, yf-1, zf, wf-1, uf), xa),
                                                ya),
                                        lerp(
                                                lerp(grad2Coord5D(seed, x0, y0, z0+Z_5, w0+W_5, u0, xf, yf, zf-1, wf-1, uf),
                                                        grad2Coord5D(seed, x0+X_5, y0, z0+Z_5, w0+W_5, u0, xf-1, yf, zf-1, wf-1, uf), xa),
                                                lerp(grad2Coord5D(seed, x0, y0+Y_5, z0+Z_5, w0+W_5, u0, xf, yf-1, zf-1, wf-1, uf),
                                                        grad2Coord5D(seed, x0+X_5, y0+Y_5, z0+Z_5, w0+W_5, u0, xf-1, yf-1, zf-1, wf-1, uf), xa),
                                                ya),
                                        za),
                                wa),
                        lerp(
                                lerp(
                                        lerp(
                                                lerp(grad2Coord5D(seed, x0, y0, z0, w0, u0+U_5, xf, yf, zf, wf, uf-1),
                                                        grad2Coord5D(seed, x0+X_5, y0, z0, w0, u0+U_5, xf-1, yf, zf, wf, uf-1), xa),
                                                lerp(grad2Coord5D(seed, x0, y0+Y_5, z0, w0, u0+U_5, xf, yf-1, zf, wf, uf-1),
                                                        grad2Coord5D(seed, x0+X_5, y0+Y_5, z0, w0, u0+U_5, xf-1, yf-1, zf, wf, uf-1), xa),
                                                ya),
                                        lerp(
                                                lerp(grad2Coord5D(seed, x0, y0, z0+Z_5, w0, u0+U_5, xf, yf, zf-1, wf, uf-1),
                                                        grad2Coord5D(seed, x0+X_5, y0, z0+Z_5, w0, u0+U_5, xf-1, yf, zf-1, wf, uf-1), xa),
                                                lerp(grad2Coord5D(seed, x0, y0+Y_5, z0+Z_5, w0, u0+U_5, xf, yf-1, zf-1, wf, uf-1),
                                                        grad2Coord5D(seed, x0+X_5, y0+Y_5, z0+Z_5, w0, u0+U_5, xf-1, yf-1, zf-1, wf, uf-1), xa),
                                                ya),
                                        za),
                                lerp(
                                        lerp(
                                                lerp(grad2Coord5D(seed, x0, y0, z0, w0+W_5, u0+U_5, xf, yf, zf, wf-1, uf-1),
                                                        grad2Coord5D(seed, x0+X_5, y0, z0, w0+W_5, u0+U_5, xf-1, yf, zf, wf-1, uf-1), xa),
                                                lerp(grad2Coord5D(seed, x0, y0+Y_5, z0, w0+W_5, u0+U_5, xf, yf-1, zf, wf-1, uf-1),
                                                        grad2Coord5D(seed, x0+X_5, y0+Y_5, z0, w0+W_5, u0+U_5, xf-1, yf-1, zf, wf-1, uf-1), xa),
                                                ya),
                                        lerp(
                                                lerp(grad2Coord5D(seed, x0, y0, z0+Z_5, w0+W_5, u0+U_5, xf, yf, zf-1, wf-1, uf-1),
                                                        grad2Coord5D(seed, x0+X_5, y0, z0+Z_5, w0+W_5, u0+U_5, xf-1, yf, zf-1, wf-1, uf-1), xa),
                                                lerp(grad2Coord5D(seed, x0, y0+Y_5, z0+Z_5, w0+W_5, u0+U_5, xf, yf-1, zf-1, wf-1, uf-1),
                                                        grad2Coord5D(seed, x0+X_5, y0+Y_5, z0+Z_5, w0+W_5, u0+U_5, xf-1, yf-1, zf-1, wf-1, uf-1), xa),
                                                ya),
                                        za),
                                wa),
                        ua) * SCALE5, EQ_ADD_5, EQ_MUL_5);//0.7777777f);
        if(normalize) _v = (_v*.5)+.5;
        return _v;
    }

    // 6d perlin
    public static final double singlePerlue(int interpolation, boolean normalize, int seed,  double x, double y, double z, double w, double u, double v) {

        final int
                xi = (int)Math.floor(x), x0 = xi * X_6,
                yi = (int)Math.floor(y), y0 = yi * Y_6,
                zi = (int)Math.floor(z), z0 = zi * Z_6,
                wi = (int)Math.floor(w), w0 = wi * W_6,
                ui = (int)Math.floor(u), u0 = ui * U_6,
                vi = (int)Math.floor(v), v0 = vi * V_6;
        final double xf = x - xi, yf = y - yi, zf = z - zi, wf = w - wi, uf = u - ui, vf = v - vi;

        double xa, ya, za, wa, ua, va;
        switch (interpolation) {
            default:
            case LINEAR:
                xa = xf;
                ya = yf;
                za = zf;
                wa = wf;
                ua = uf;
                va = vf;
                break;
            case HERMITE:
                xa = hermiteInterpolator(xf);
                ya = hermiteInterpolator(yf);
                za = hermiteInterpolator(zf);
                wa = hermiteInterpolator(wf);
                ua = hermiteInterpolator(uf);
                va = hermiteInterpolator(vf);
                break;
            case QUINTIC:
                xa = quintic2Interpolator(xf);
                ya = quintic2Interpolator(yf);
                za = quintic2Interpolator(zf);
                wa = quintic2Interpolator(wf);
                ua = quintic2Interpolator(uf);
                va = quintic2Interpolator(vf);
                break;
            case RADIAN:
                xa = radianInterpolator(xf);
                ya = radianInterpolator(yf);
                za = radianInterpolator(zf);
                wa = radianInterpolator(wf);
                ua = radianInterpolator(uf);
                va = radianInterpolator(vf);
                break;
            case COSINE:
                xa = cosineInterpolator(xf);
                ya = cosineInterpolator(yf);
                za = cosineInterpolator(zf);
                wa = cosineInterpolator(wf);
                ua = cosineInterpolator(uf);
                va = cosineInterpolator(vf);
                break;
        }


        double _v = equalize(
                lerp(
                        lerp(
                                lerp(
                                        lerp(
                                                lerp(
                                                        lerp(grad2Coord6D(seed, x0, y0, z0, w0, u0, v0, xf, yf, zf, wf, uf, vf),
                                                                grad2Coord6D(seed, x0+X_6, y0, z0, w0, u0, v0, xf - 1, yf, zf, wf, uf, vf), xa),
                                                        lerp(grad2Coord6D(seed, x0, y0+Y_6, z0, w0, u0, v0, xf, yf - 1, zf, wf, uf, vf),
                                                                grad2Coord6D(seed, x0+X_6, y0+Y_6, z0, w0, u0, v0, xf - 1, yf - 1, zf, wf, uf, vf), xa),
                                                        ya),
                                                lerp(
                                                        lerp(grad2Coord6D(seed, x0, y0, z0+Z_6, w0, u0, v0, xf, yf, zf - 1, wf, uf, vf),
                                                                grad2Coord6D(seed, x0+X_6, y0, z0+Z_6, w0, u0, v0, xf - 1, yf, zf - 1, wf, uf, vf), xa),
                                                        lerp(grad2Coord6D(seed, x0, y0+Y_6, z0+Z_6, w0, u0, v0, xf, yf - 1, zf - 1, wf, uf, vf),
                                                                grad2Coord6D(seed, x0+X_6, y0+Y_6, z0+Z_6, w0, u0, v0, xf - 1, yf - 1, zf - 1, wf, uf, vf), xa),
                                                        ya),
                                                za),
                                        lerp(
                                                lerp(
                                                        lerp(grad2Coord6D(seed, x0, y0, z0, w0+W_6, u0, v0, xf, yf, zf, wf - 1, uf, vf),
                                                                grad2Coord6D(seed, x0+X_6, y0, z0, w0+W_6, u0, v0, xf - 1, yf, zf, wf - 1, uf, vf), xa),
                                                        lerp(grad2Coord6D(seed, x0, y0+Y_6, z0, w0+W_6, u0, v0, xf, yf - 1, zf, wf - 1, uf, vf),
                                                                grad2Coord6D(seed, x0+X_6, y0+Y_6, z0, w0+W_6, u0, v0, xf - 1, yf - 1, zf, wf - 1, uf, vf), xa),
                                                        ya),
                                                lerp(
                                                        lerp(grad2Coord6D(seed, x0, y0, z0+Z_6, w0+W_6, u0, v0, xf, yf, zf - 1, wf - 1, uf, vf),
                                                                grad2Coord6D(seed, x0+X_6, y0, z0+Z_6, w0+W_6, u0, v0, xf - 1, yf, zf - 1, wf - 1, uf, vf), xa),
                                                        lerp(grad2Coord6D(seed, x0, y0+Y_6, z0+Z_6, w0+W_6, u0, v0, xf, yf - 1, zf - 1, wf - 1, uf, vf),
                                                                grad2Coord6D(seed, x0+X_6, y0+Y_6, z0+Z_6, w0+W_6, u0, v0, xf - 1, yf - 1, zf - 1, wf - 1, uf, vf), xa),
                                                        ya),
                                                za),
                                        wa),
                                lerp(
                                        lerp(
                                                lerp(
                                                        lerp(grad2Coord6D(seed, x0, y0, z0, w0, u0+U_6, v0, xf, yf, zf, wf, uf - 1, vf),
                                                                grad2Coord6D(seed, x0+X_6, y0, z0, w0, u0+U_6, v0, xf - 1, yf, zf, wf, uf - 1, vf), xa),
                                                        lerp(grad2Coord6D(seed, x0, y0+Y_6, z0, w0, u0+U_6, v0, xf, yf - 1, zf, wf, uf - 1, vf),
                                                                grad2Coord6D(seed, x0+X_6, y0+Y_6, z0, w0, u0+U_6, v0, xf - 1, yf - 1, zf, wf, uf - 1, vf), xa),
                                                        ya),
                                                lerp(
                                                        lerp(grad2Coord6D(seed, x0, y0, z0+Z_6, w0, u0+U_6, v0, xf, yf, zf - 1, wf, uf - 1, vf),
                                                                grad2Coord6D(seed, x0+X_6, y0, z0+Z_6, w0, u0+U_6, v0, xf - 1, yf, zf - 1, wf, uf - 1, vf), xa),
                                                        lerp(grad2Coord6D(seed, x0, y0+Y_6, z0+Z_6, w0, u0+U_6, v0, xf, yf - 1, zf - 1, wf, uf - 1, vf),
                                                                grad2Coord6D(seed, x0+X_6, y0+Y_6, z0+Z_6, w0, u0+U_6, v0, xf - 1, yf - 1, zf - 1, wf, uf - 1, vf), xa),
                                                        ya),
                                                za),
                                        lerp(
                                                lerp(
                                                        lerp(grad2Coord6D(seed, x0, y0, z0, w0+W_6, u0+U_6, v0, xf, yf, zf, wf - 1, uf - 1, vf),
                                                                grad2Coord6D(seed, x0+X_6, y0, z0, w0+W_6, u0+U_6, v0, xf - 1, yf, zf, wf - 1, uf - 1, vf), xa),
                                                        lerp(grad2Coord6D(seed, x0, y0+Y_6, z0, w0+W_6, u0+U_6, v0, xf, yf - 1, zf, wf - 1, uf - 1, vf),
                                                                grad2Coord6D(seed, x0+X_6, y0+Y_6, z0, w0+W_6, u0+U_6, v0, xf - 1, yf - 1, zf, wf - 1, uf - 1, vf), xa),
                                                        ya),
                                                lerp(
                                                        lerp(grad2Coord6D(seed, x0, y0, z0+Z_6, w0+W_6, u0+U_6, v0, xf, yf, zf - 1, wf - 1, uf - 1, vf),
                                                                grad2Coord6D(seed, x0+X_6, y0, z0+Z_6, w0+W_6, u0+U_6, v0, xf - 1, yf, zf - 1, wf - 1, uf - 1, vf), xa),
                                                        lerp(grad2Coord6D(seed, x0, y0+Y_6, z0+Z_6, w0+W_6, u0+U_6, v0, xf, yf - 1, zf - 1, wf - 1, uf - 1, vf),
                                                                grad2Coord6D(seed, x0+X_6, y0+Y_6, z0+Z_6, w0+W_6, u0+U_6, v0, xf - 1, yf - 1, zf - 1, wf - 1, uf - 1, vf), xa),
                                                        ya),
                                                za),
                                        wa),
                                ua),
                        lerp(
                                lerp(
                                        lerp(
                                                lerp(
                                                        lerp(grad2Coord6D(seed, x0, y0, z0, w0, u0, v0+V_6, xf, yf, zf, wf, uf, vf - 1),
                                                                grad2Coord6D(seed, x0+X_6, y0, z0, w0, u0, v0+V_6, xf - 1, yf, zf, wf, uf, vf - 1), xa),
                                                        lerp(grad2Coord6D(seed, x0, y0+Y_6, z0, w0, u0, v0+V_6, xf, yf - 1, zf, wf, uf, vf - 1),
                                                                grad2Coord6D(seed, x0+X_6, y0+Y_6, z0, w0, u0, v0+V_6, xf - 1, yf - 1, zf, wf, uf, vf - 1), xa),
                                                        ya),
                                                lerp(
                                                        lerp(grad2Coord6D(seed, x0, y0, z0+Z_6, w0, u0, v0+V_6, xf, yf, zf - 1, wf, uf, vf - 1),
                                                                grad2Coord6D(seed, x0+X_6, y0, z0+Z_6, w0, u0, v0+V_6, xf - 1, yf, zf - 1, wf, uf, vf - 1), xa),
                                                        lerp(grad2Coord6D(seed, x0, y0+Y_6, z0+Z_6, w0, u0, v0+V_6, xf, yf - 1, zf - 1, wf, uf, vf - 1),
                                                                grad2Coord6D(seed, x0+X_6, y0+Y_6, z0+Z_6, w0, u0, v0+V_6, xf - 1, yf - 1, zf - 1, wf, uf, vf - 1), xa),
                                                        ya),
                                                za),
                                        lerp(
                                                lerp(
                                                        lerp(grad2Coord6D(seed, x0, y0, z0, w0+W_6, u0, v0+V_6, xf, yf, zf, wf - 1, uf, vf - 1),
                                                                grad2Coord6D(seed, x0+X_6, y0, z0, w0+W_6, u0, v0+V_6, xf - 1, yf, zf, wf - 1, uf, vf - 1), xa),
                                                        lerp(grad2Coord6D(seed, x0, y0+Y_6, z0, w0+W_6, u0, v0+V_6, xf, yf - 1, zf, wf - 1, uf, vf - 1),
                                                                grad2Coord6D(seed, x0+X_6, y0+Y_6, z0, w0+W_6, u0, v0+V_6, xf - 1, yf - 1, zf, wf - 1, uf, vf - 1), xa),
                                                        ya),
                                                lerp(
                                                        lerp(grad2Coord6D(seed, x0, y0, z0+Z_6, w0+W_6, u0, v0+V_6, xf, yf, zf - 1, wf - 1, uf, vf - 1),
                                                                grad2Coord6D(seed, x0+X_6, y0, z0+Z_6, w0+W_6, u0, v0+V_6, xf - 1, yf, zf - 1, wf - 1, uf, vf - 1), xa),
                                                        lerp(grad2Coord6D(seed, x0, y0+Y_6, z0+Z_6, w0+W_6, u0, v0+V_6, xf, yf - 1, zf - 1, wf - 1, uf, vf - 1),
                                                                grad2Coord6D(seed, x0+X_6, y0+Y_6, z0+Z_6, w0+W_6, u0, v0+V_6, xf - 1, yf - 1, zf - 1, wf - 1, uf, vf - 1), xa),
                                                        ya),
                                                za),
                                        wa),
                                lerp(
                                        lerp(
                                                lerp(
                                                        lerp(grad2Coord6D(seed, x0, y0, z0, w0, u0+U_6, v0+V_6, xf, yf, zf, wf, uf - 1, vf - 1),
                                                                grad2Coord6D(seed, x0+X_6, y0, z0, w0, u0+U_6, v0+V_6, xf - 1, yf, zf, wf, uf - 1, vf - 1), xa),
                                                        lerp(grad2Coord6D(seed, x0, y0+Y_6, z0, w0, u0+U_6, v0+V_6, xf, yf - 1, zf, wf, uf - 1, vf - 1),
                                                                grad2Coord6D(seed, x0+X_6, y0+Y_6, z0, w0, u0+U_6, v0+V_6, xf - 1, yf - 1, zf, wf, uf - 1, vf - 1), xa),
                                                        ya),
                                                lerp(
                                                        lerp(grad2Coord6D(seed, x0, y0, z0+Z_6, w0, u0+U_6, v0+V_6, xf, yf, zf - 1, wf, uf - 1, vf - 1),
                                                                grad2Coord6D(seed, x0+X_6, y0, z0+Z_6, w0, u0+U_6, v0+V_6, xf - 1, yf, zf - 1, wf, uf - 1, vf - 1), xa),
                                                        lerp(grad2Coord6D(seed, x0, y0+Y_6, z0+Z_6, w0, u0+U_6, v0+V_6, xf, yf - 1, zf - 1, wf, uf - 1, vf - 1),
                                                                grad2Coord6D(seed, x0+X_6, y0+Y_6, z0+Z_6, w0, u0+U_6, v0+V_6, xf - 1, yf - 1, zf - 1, wf, uf - 1, vf - 1), xa),
                                                        ya),
                                                za),
                                        lerp(
                                                lerp(
                                                        lerp(grad2Coord6D(seed, x0, y0, z0, w0+W_6, u0+U_6, v0+V_6, xf, yf, zf, wf - 1, uf - 1, vf - 1),
                                                                grad2Coord6D(seed, x0+X_6, y0, z0, w0+W_6, u0+U_6, v0+V_6, xf - 1, yf, zf, wf - 1, uf - 1, vf - 1), xa),
                                                        lerp(grad2Coord6D(seed, x0, y0+Y_6, z0, w0+W_6, u0+U_6, v0+V_6, xf, yf - 1, zf, wf - 1, uf - 1, vf - 1),
                                                                grad2Coord6D(seed, x0+X_6, y0+Y_6, z0, w0+W_6, u0+U_6, v0+V_6, xf - 1, yf - 1, zf, wf - 1, uf - 1, vf - 1), xa),
                                                        ya),
                                                lerp(
                                                        lerp(grad2Coord6D(seed, x0, y0, z0+Z_6, w0+W_6, u0+U_6, v0+V_6, xf, yf, zf - 1, wf - 1, uf - 1, vf - 1),
                                                                grad2Coord6D(seed, x0+X_6, y0, z0+Z_6, w0+W_6, u0+U_6, v0+V_6, xf - 1, yf, zf - 1, wf - 1, uf - 1, vf - 1), xa),
                                                        lerp(grad2Coord6D(seed, x0, y0+Y_6, z0+Z_6, w0+W_6, u0+U_6, v0+V_6, xf, yf - 1, zf - 1, wf - 1, uf - 1, vf - 1),
                                                                grad2Coord6D(seed, x0+X_6, y0+Y_6, z0+Z_6, w0+W_6, u0+U_6, v0+V_6, xf - 1, yf - 1, zf - 1, wf - 1, uf - 1, vf - 1), xa),
                                                        ya),
                                                za),
                                        wa),
                                ua),
                        va) * SCALE6, EQ_ADD_6, EQ_MUL_6);//1.61f);
        if(normalize) _v = (_v*.5)+.5;
        return _v;
    }



}
