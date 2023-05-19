package com.github.terefang.randy.noise.impl;

import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.NoiseUtil;
import com.github.terefang.randy.rng.impl.ArcRand;

public class SolidNoise extends NoiseUtil implements INoise
{
    @Override
    public String name()
    {
        switch (this.getInterpolation())
        {
            case QUINTIC:
                return super.name()+"Quintic";
            case HERMITE:
                return super.name()+"Hermite";
            case LINEAR:
            default:
                return super.name()+"Linear";
        }
    }

    @Override
    public double _noise1(long seed, double x, int interpolation) {
        return singleSolid(false, interpolation, makeSeedInt(seed), x, this.getMutation());
    }

    @Override
    public double _noise2(long seed, double x, double y, int interpolation) {
        return singleSolid(false, interpolation, makeSeedInt(seed), x,y, this.getMutation());
    }

    @Override
    public double _noise3(long seed, double x, double y, double z, int interpolation) {
        return singleSolid(false, interpolation, makeSeedInt(seed), x,y,z, this.getMutation());
    }

    @Override
    public double _noise4(long seed, double x, double y, double z, double u, int interpolation) {
        return singleSolid(false, interpolation, makeSeedInt(seed), x,y,z/*,u,v,w*/, this.getMutation());
    }

    @Override
    public double _noise5(long seed, double x, double y, double z, double u, double v, int interpolation) {
        return singleSolid(false, interpolation, makeSeedInt(seed), x,y,z/*,u,v,w*/, this.getMutation());
    }

    @Override
    public double _noise6(long seed, double x, double y, double z, double u, double v, double w, int interpolation) {
        return singleSolid(false, interpolation, makeSeedInt(seed), x,y,z/*,u,v,w*/, this.getMutation());
    }

    @Override
    public double _noise1n(long seed, double x, int interpolation) {
        return singleSolid(true, interpolation, makeSeedInt(seed), x, this.getMutation());
    }

    @Override
    public double _noise2n(long seed, double x, double y, int interpolation) {
        return singleSolid(true, interpolation, makeSeedInt(seed), x,y, this.getMutation());
    }

    @Override
    public double _noise3n(long seed, double x, double y, double z, int interpolation) {
        return singleSolid(true, interpolation, makeSeedInt(seed), x,y,z, this.getMutation());
    }

    @Override
    public double _noise4n(long seed, double x, double y, double z, double u, int interpolation) {
        return singleSolid(true, interpolation, makeSeedInt(seed), x,y,z,u, this.getMutation());
    }

    @Override
    public double _noise5n(long seed, double x, double y, double z, double u, double v, int interpolation) {
        return singleSolid(true, interpolation, makeSeedInt(seed), x,y,z,u,v, this.getMutation());
    }

    @Override
    public double _noise6n(long seed, double x, double y, double z, double u, double v, double w, int interpolation) {
        return singleSolid(true, interpolation, makeSeedInt(seed), x,y,z,u,v,w+this.getMutation());
    }


    // ----------------------------------------------------------------------------

    // 1d Solid
    public static final double singleSolid(boolean normalize, int interpolation, int seed, double x)
    {
        mkTable();

        int x0 = fastFloor(x);
        int x1 = x0 + 1;

        double xs, ys;
        switch (interpolation) {
            default:
            case LINEAR:
                xs = x - x0;
                break;
            case HERMITE:
                xs = hermiteInterpolator(x - x0);
                break;
            case QUINTIC:
                xs = quinticInterpolator(x - x0);
                break;
        }

        double xd0 = x - x0;
        double xd1 = xd0 - 1;

        double _v = lerp(table1D(seed, x0, xd0), table1D(seed, x1, xd1), xs);
        if(normalize) _v = (_v*.5)+.5;
        return _v;
    }

    private static double table1D(int _seed, int _x0, double _xd)
    {
        int _hash = hash256(_x0, _x0+1, _seed) & -2 ;
        double _n0 = 128f-((double)TABLE1[_hash]);
        double _n2 = 128f-((double)TABLE1[_hash+1]);

        return _xd*(_n0/128f)-(_n2/255f);
    }

    // 2d
    public static synchronized void mkTable()
    {
        if(TABLE1==null)
        {
            ArcRand _arc = ArcRand.from(0xd1ceBeafCaf34ff3L);
            TABLE1 = _arc.getContext();
            _arc = ArcRand.from(0xcaf34ff3D1ceBeafL);
            TABLE2 = _arc.getContext();
            _arc = ArcRand.from(0x123456789abcdefL);
            TABLE3 = _arc.getContext();
        }
    }

    static int[] TABLE1 = null;
    static int[] TABLE2 = null;
    static int[] TABLE3 = null;

    // 2d
    public static final double singleSolid(boolean normalize, int interpolation, int seed, double x, double y)
    {
        mkTable();

        int x0 = fastFloor(x);
        int y0 = fastFloor(y);
        int x1 = x0 + 1;
        int y1 = y0 + 1;

        double xs, ys;
        switch (interpolation) {
            default:
            case LINEAR:
                xs = x - x0;
                ys = y - y0;
                break;
            case HERMITE:
                xs = hermiteInterpolator(x - x0);
                ys = hermiteInterpolator(y - y0);
                break;
            case QUINTIC:
                xs = quinticInterpolator(x - x0);
                ys = quinticInterpolator(y - y0);
                break;
        }

        double xd0 = x - x0;
        double yd0 = y - y0;
        double xd1 = xd0 - 1;
        double yd1 = yd0 - 1;

        double xf0 = lerp(table2D(seed, x0, y0, xd0, yd0), table2D(seed, x1, y0, xd1, yd0), xs);
        double xf1 = lerp(table2D(seed, x0, y1, xd0, yd1), table2D(seed, x1, y1, xd1, yd1), xs);

        double _v = lerp(xf0, xf1, ys);
        if(normalize) _v = (_v*.5)+.5;
        return _v;
    }

    private static double table2D(int _seed, int _x0, int _y0, double _xd, double _yd)
    {
        int _hash = hash256(_x0, _y0, _seed) & -2 ;
        double _n0 = 128f-((double)TABLE1[_hash]);
        double _n1 = 128f-((double)TABLE2[_hash]);
        double _n2 = 128f-((double)TABLE1[_hash+1]);
        double _n3 = 128f-((double)TABLE2[_hash+1]);

        return _xd*(_n0/128f)+_yd*(_n1/128f)-(_n2/255f)+(_n3/511f);
    }

    public static final double singleSolid(boolean normalize, int interpolation, int seed, double x, double y, double z)
    {
        mkTable();

        int x0 = fastFloor(x);
        int y0 = fastFloor(y);
        int z0 = fastFloor(z);
        int x1 = x0 + 1;
        int y1 = y0 + 1;
        int z1 = z0 + 1;

        double xs, ys, zs;
        switch (interpolation) {
            default:
            case LINEAR:
                xs = x - x0;
                ys = y - y0;
                zs = z - z0;
                break;
            case HERMITE:
                xs = hermiteInterpolator(x - x0);
                ys = hermiteInterpolator(y - y0);
                zs = hermiteInterpolator(z - z0);
                break;
            case QUINTIC:
                xs = quinticInterpolator(x - x0);
                ys = quinticInterpolator(y - y0);
                zs = quinticInterpolator(z - z0);
                break;
        }

        final double xd0 = x - x0;
        final double yd0 = y - y0;
        final double zd0 = z - z0;
        final double xd1 = xd0 - 1;
        final double yd1 = yd0 - 1;
        final double zd1 = zd0 - 1;

        final double xf00 = lerp(table3D(seed, x0, y0, z0, xd0, yd0, zd0), table3D(seed, x1, y0, z0, xd1, yd0, zd0), xs);
        final double xf10 = lerp(table3D(seed, x0, y1, z0, xd0, yd1, zd0), table3D(seed, x1, y1, z0, xd1, yd1, zd0), xs);
        final double xf01 = lerp(table3D(seed, x0, y0, z1, xd0, yd0, zd1), table3D(seed, x1, y0, z1, xd1, yd0, zd1), xs);
        final double xf11 = lerp(table3D(seed, x0, y1, z1, xd0, yd1, zd1), table3D(seed, x1, y1, z1, xd1, yd1, zd1), xs);

        final double yf0 = lerp(xf00, xf10, ys);
        final double yf1 = lerp(xf01, xf11, ys);

        double _v = lerp(yf0, yf1, zs);
        if(normalize) _v = (_v*.5)+.5;
        return _v;
    }

    private static double table3D(int _seed, int _x0, int _y0, int _z0, double _xd, double _yd, double _zd)
    {
        int _hash = hash256(_x0, _y0, _z0, _seed) & -2 ;
        double _n0 = 128f-((double)TABLE1[_hash]);
        double _n1 = 128f-((double)TABLE2[_hash]);
        double _n2 = 128f-((double)TABLE3[_hash]);
        double _n3 = 128f-((double)TABLE1[_hash+1]);
        double _n4 = 128f-((double)TABLE2[_hash+1]);
        double _n5 = 128f-((double)TABLE3[_hash+1]);

        return _xd*(_n0/128f)+_yd*(_n1/128f)+_zd*(_n2/128f)-(_n3/255f)+(_n4/511f)-(_n5/1023f);
    }

    public static final double singleSolid(boolean normalize, int interpolation, int seed, double x, double y, double z, double u)
    {
        mkTable();

        int u0 = fastFloor(u);

        double us = u - u0;
        switch (interpolation) {
            case HERMITE:
                us = hermiteInterpolator(us);
                break;
            case QUINTIC:
                us = quinticInterpolator(us);
                break;
        }

        double ud0 = u - u0;

        double _v = lerp(table1D(seed, u0, ud0), singleSolid(false, interpolation, seed, x, y, z), us);
        if(normalize) _v = (_v*.5)+.5;
        return _v;
    }

    public static final double singleSolid(boolean normalize, int interpolation, int seed, double x, double y, double z, double u, double v)
    {
        mkTable();

        int v0 = fastFloor(v);

        double vs = v - v0;
        switch (interpolation) {
            case HERMITE:
                vs = hermiteInterpolator(vs);
                break;
            case QUINTIC:
                vs = quinticInterpolator(vs);
                break;
        }

        double vd0 = v - v0;

        double _v = lerp(table1D(seed, v0, vd0), singleSolid(false, interpolation, seed, x, y, z, u), vs);
        if(normalize) _v = (_v*.5)+.5;
        return _v;
    }

    public static final double singleSolid(boolean normalize, int interpolation, int seed, double x, double y, double z, double u, double v, double w)
    {
        mkTable();

        int w0 = fastFloor(w);

        double ws = w - w0;
        switch (interpolation) {
            case HERMITE:
                ws = hermiteInterpolator(ws);
                break;
            case QUINTIC:
                ws = quinticInterpolator(ws);
                break;
        }

        double wd0 = w - w0;

        double _v = lerp(table1D(seed, w0, wd0), singleSolid(false, interpolation, seed, x, y, z, u, v), ws);
        if(normalize) _v = (_v*.5)+.5;
        return _v;
    }

}
