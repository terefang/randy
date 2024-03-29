package com.github.terefang.randy.noise.impl;

import com.github.terefang.randy.RandyUtil;
import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.NoiseUtil;

public class InigoQuilezNoise extends NoiseUtil implements INoise
{
    INoise domain;

    public INoise getDomain() {
        return domain;
    }

    public void setDomain(INoise domain) {
        this.domain = domain;
    }

    @Override
    public void setSeed(long s) {
        super.setSeed(s);

        double _x = 5.23+(((double)s)/1000.);
        for(int _j = 0; _j<12; _j++)
        {
            for(int _i = 0; _i<6; _i++)
            {
                this._loot[_j][_i]=(_x%10.0)/10.;
                _x+=Math.PI;
            }
        }
    }

    double[][] _loot = new double[12][6];
    
    @Override
    public String name() {
        if(this.getTransform()!=null)
        {
            if(this.getDomain()!=null)
            {
                return this.getClass().getSimpleName()+"~"+this.getDomain().name()+"~"+this.getTransform().name();
            }
            return this.getClass().getSimpleName()+"~"+this.getTransform().name();
        }
        if(this.getDomain()!=null)
        {
            return this.getClass().getSimpleName()+"~"+this.getDomain().name();
        }
        return this.getClass().getSimpleName();
    }

    final SimplexNoise defaultDomain = (SimplexNoise) RandyUtil.simplexNoise(0);
    INoise mDomain() {
        if(this.domain!=null) return this.domain;
        defaultDomain.setSeed(this.getSeed());
        return defaultDomain;
    }

    @Override
    public double _noise1(long seed, double x, int interpolation) {
        double qx = this.mDomain().noise2(x+this._loot[0][0],this.getMutation());

        double rx = this.mDomain().noise2(x+(this.getHarshness()*qx)+this._loot[6][0],this.getMutation());

        return SimplexNoise.singleSimplex(false, makeSeedInt(seed),
                x+this.getHarshness()*rx,
                this.getMutation());
    }

    @Override
    public double _noise2(long seed, double x, double y, int interpolation) {
        double qx = this.mDomain().noise3(x+this._loot[0][0],y+this._loot[0][1],this.getMutation());
        double qy = this.mDomain().noise3(x+this._loot[1][0],y+this._loot[1][1],this.getMutation());

        double rx = this.mDomain().noise3(x+(this.getHarshness()*qx)+this._loot[6][ 0],y+(this.getHarshness()*qy)+this._loot[6][ 1],this.getMutation());
        double ry = this.mDomain().noise3(x+(this.getHarshness()*qx)+this._loot[7][ 0],y+(this.getHarshness()*qy)+this._loot[7][ 1],this.getMutation());

        return SimplexNoise.singleSimplex(false, makeSeedInt(seed),
                x+this.getHarshness()*rx,
                y+this.getHarshness()*ry,
                this.getMutation());
    }

    @Override
    public double _noise3(long seed, double x, double y, double z, int interpolation) {
        double qx = this.mDomain().noise4(x+this._loot[0][0],y+this._loot[0][1], z+this._loot[0][2], this.getMutation());
        double qy = this.mDomain().noise4(x+this._loot[1][0],y+this._loot[1][1], z+this._loot[1][2], this.getMutation());
        double qz = this.mDomain().noise4(x+this._loot[2][0],y+this._loot[2][1], z+this._loot[2][2], this.getMutation());

        double rx = this.mDomain().noise4(x+(this.getHarshness()*qx)+this._loot[6][ 0],y+(this.getHarshness()*qy)+this._loot[6][ 1],z+(this.getHarshness()*qz)+this._loot[6][ 2],this.getMutation());
        double ry = this.mDomain().noise4(x+(this.getHarshness()*qx)+this._loot[7][ 0],y+(this.getHarshness()*qy)+this._loot[7][ 1],z+(this.getHarshness()*qz)+this._loot[7][ 2],this.getMutation());
        double rz = this.mDomain().noise4(x+(this.getHarshness()*qx)+this._loot[8][ 0],y+(this.getHarshness()*qy)+this._loot[8][ 1],z+(this.getHarshness()*qz)+this._loot[8][ 2],this.getMutation());

        return SimplexNoise.singleSimplex(false, makeSeedInt(seed),
                x+this.getHarshness()*rx,
                y+this.getHarshness()*ry,
                z+this.getHarshness()*rz,
                this.getMutation());
    }

    @Override
    public double _noise4(long seed, double x, double y, double z, double u, int interpolation) {
        double qx = this.mDomain().noise5(x+this._loot[0][0],y+this._loot[0][1], z+this._loot[0][2], u+this._loot[0][3], this.getMutation());
        double qy = this.mDomain().noise5(x+this._loot[1][0],y+this._loot[1][1], z+this._loot[1][2], u+this._loot[1][3], this.getMutation());
        double qz = this.mDomain().noise5(x+this._loot[2][0],y+this._loot[2][1], z+this._loot[2][2], u+this._loot[2][3], this.getMutation());
        double qu = this.mDomain().noise5(x+this._loot[3][0],y+this._loot[3][1], z+this._loot[3][2], u+this._loot[3][3], this.getMutation());

        double rx = this.mDomain().noise5(x+(this.getHarshness()*qx)+this._loot[6][ 0],y+(this.getHarshness()*qy)+this._loot[6][ 1],z+(this.getHarshness()*qz)+this._loot[6][ 2],u+(this.getHarshness()*qu)+this._loot[6][ 3],this.getMutation());
        double ry = this.mDomain().noise5(x+(this.getHarshness()*qx)+this._loot[7][ 0],y+(this.getHarshness()*qy)+this._loot[7][ 1],z+(this.getHarshness()*qz)+this._loot[7][ 2],u+(this.getHarshness()*qu)+this._loot[7][ 3],this.getMutation());
        double rz = this.mDomain().noise5(x+(this.getHarshness()*qx)+this._loot[8][ 0],y+(this.getHarshness()*qy)+this._loot[8][ 1],z+(this.getHarshness()*qz)+this._loot[8][ 2],u+(this.getHarshness()*qu)+this._loot[8][ 3],this.getMutation());
        double ru = this.mDomain().noise5(x+(this.getHarshness()*qx)+this._loot[9][ 0],y+(this.getHarshness()*qy)+this._loot[9][ 1],z+(this.getHarshness()*qz)+this._loot[9][ 2],u+(this.getHarshness()*qu)+this._loot[9][ 3],this.getMutation());

        return SimplexNoise.singleSimplex(false, makeSeedInt(seed),
                x+this.getHarshness()*rx,
                y+this.getHarshness()*ry,
                z+this.getHarshness()*rz,
                u+this.getHarshness()*ru,
                this.getMutation());
    }

    @Override
    public double _noise5(long seed, double x, double y, double z, double u, double v, int interpolation) {
        double qx = this.mDomain().noise6(x+this._loot[0][0],y+this._loot[0][1], z+this._loot[0][2], u+this._loot[0][3], v+this._loot[0][4], this.getMutation());
        double qy = this.mDomain().noise6(x+this._loot[1][0],y+this._loot[1][1], z+this._loot[1][2], u+this._loot[1][3], v+this._loot[1][4], this.getMutation());
        double qz = this.mDomain().noise6(x+this._loot[2][0],y+this._loot[2][1], z+this._loot[2][2], u+this._loot[2][3], v+this._loot[2][4], this.getMutation());
        double qu = this.mDomain().noise6(x+this._loot[3][0],y+this._loot[3][1], z+this._loot[3][2], u+this._loot[3][3], v+this._loot[3][4], this.getMutation());
        double qv = this.mDomain().noise6(x+this._loot[4][0],y+this._loot[4][1], z+this._loot[4][2], u+this._loot[4][3], v+this._loot[4][4], this.getMutation());

        double rx = this.mDomain().noise6(x+(this.getHarshness()*qx)+this._loot[6][ 0],y+(this.getHarshness()*qy)+this._loot[6][ 1],z+(this.getHarshness()*qz)+this._loot[6][ 2],u+(this.getHarshness()*qu)+this._loot[6][ 3],v+(this.getHarshness()*qv)+this._loot[6][ 4],this.getMutation());
        double ry = this.mDomain().noise6(x+(this.getHarshness()*qx)+this._loot[7][ 0],y+(this.getHarshness()*qy)+this._loot[7][ 1],z+(this.getHarshness()*qz)+this._loot[7][ 2],u+(this.getHarshness()*qu)+this._loot[7][ 3],v+(this.getHarshness()*qv)+this._loot[7][ 4],this.getMutation());
        double rz = this.mDomain().noise6(x+(this.getHarshness()*qx)+this._loot[8][ 0],y+(this.getHarshness()*qy)+this._loot[8][ 1],z+(this.getHarshness()*qz)+this._loot[8][ 2],u+(this.getHarshness()*qu)+this._loot[8][ 3],v+(this.getHarshness()*qv)+this._loot[8][ 4],this.getMutation());
        double ru = this.mDomain().noise6(x+(this.getHarshness()*qx)+this._loot[9][ 0],y+(this.getHarshness()*qy)+this._loot[9][ 1],z+(this.getHarshness()*qz)+this._loot[9][ 2],u+(this.getHarshness()*qu)+this._loot[9][ 3],v+(this.getHarshness()*qv)+this._loot[9][ 4],this.getMutation());
        double rv = this.mDomain().noise6(x+(this.getHarshness()*qx)+this._loot[10][0],y+(this.getHarshness()*qy)+this._loot[10][1],z+(this.getHarshness()*qz)+this._loot[10][2],u+(this.getHarshness()*qu)+this._loot[10][3],v+(this.getHarshness()*qv)+this._loot[10][4],this.getMutation());

        return SimplexNoise.singleSimplex(false, makeSeedInt(seed),
                x+this.getHarshness()*rx,
                y+this.getHarshness()*ry,
                z+this.getHarshness()*rz,
                u+this.getHarshness()*ru,
                v+this.getHarshness()*rv,
                this.getMutation());
    }

    @Override
    public double _noise6(long seed, double x, double y, double z, double u, double v, double w, int interpolation) {
        double qx = this.mDomain().noise6(x+this._loot[0][0],y+this._loot[0][1], z+this._loot[0][2], u+this._loot[0][3], v+this._loot[0][4], w+this._loot[0][5]+this.getMutation());
        double qy = this.mDomain().noise6(x+this._loot[1][0],y+this._loot[1][1], z+this._loot[1][2], u+this._loot[1][3], v+this._loot[1][4], w+this._loot[1][5]+this.getMutation());
        double qz = this.mDomain().noise6(x+this._loot[2][0],y+this._loot[2][1], z+this._loot[2][2], u+this._loot[2][3], v+this._loot[2][4], w+this._loot[2][5]+this.getMutation());
        double qu = this.mDomain().noise6(x+this._loot[3][0],y+this._loot[3][1], z+this._loot[3][2], u+this._loot[3][3], v+this._loot[3][4], w+this._loot[3][5]+this.getMutation());
        double qv = this.mDomain().noise6(x+this._loot[4][0],y+this._loot[4][1], z+this._loot[4][2], u+this._loot[4][3], v+this._loot[4][4], w+this._loot[4][5]+this.getMutation());
        double qw = this.mDomain().noise6(x+this._loot[5][0],y+this._loot[5][1], z+this._loot[5][2], u+this._loot[5][3], v+this._loot[5][4], w+this._loot[5][5]+this.getMutation());

        double rx = this.mDomain().noise6(x+(this.getHarshness()*qx)+this._loot[6][ 0],y+(this.getHarshness()*qy)+this._loot[6][ 1],z+(this.getHarshness()*qz)+this._loot[6][ 2],u+(this.getHarshness()*qu)+this._loot[6][ 3],v+(this.getHarshness()*qv)+this._loot[6][ 4],w+(this.getHarshness()*qw)+this._loot[6][ 5]+this.getMutation());
        double ry = this.mDomain().noise6(x+(this.getHarshness()*qx)+this._loot[7][ 0],y+(this.getHarshness()*qy)+this._loot[7][ 1],z+(this.getHarshness()*qz)+this._loot[7][ 2],u+(this.getHarshness()*qu)+this._loot[7][ 3],v+(this.getHarshness()*qv)+this._loot[7][ 4],w+(this.getHarshness()*qw)+this._loot[7][ 5]+this.getMutation());
        double rz = this.mDomain().noise6(x+(this.getHarshness()*qx)+this._loot[8][ 0],y+(this.getHarshness()*qy)+this._loot[8][ 1],z+(this.getHarshness()*qz)+this._loot[8][ 2],u+(this.getHarshness()*qu)+this._loot[8][ 3],v+(this.getHarshness()*qv)+this._loot[8][ 4],w+(this.getHarshness()*qw)+this._loot[8][ 5]+this.getMutation());
        double ru = this.mDomain().noise6(x+(this.getHarshness()*qx)+this._loot[9][ 0],y+(this.getHarshness()*qy)+this._loot[9][ 1],z+(this.getHarshness()*qz)+this._loot[9][ 2],u+(this.getHarshness()*qu)+this._loot[9][ 3],v+(this.getHarshness()*qv)+this._loot[9][ 4],w+(this.getHarshness()*qw)+this._loot[9][ 5]+this.getMutation());
        double rv = this.mDomain().noise6(x+(this.getHarshness()*qx)+this._loot[10][0],y+(this.getHarshness()*qy)+this._loot[10][1],z+(this.getHarshness()*qz)+this._loot[10][2],u+(this.getHarshness()*qu)+this._loot[10][3],v+(this.getHarshness()*qv)+this._loot[10][4],w+(this.getHarshness()*qw)+this._loot[10][5]+this.getMutation());
        double rw = this.mDomain().noise6(x+(this.getHarshness()*qx)+this._loot[11][0],y+(this.getHarshness()*qy)+this._loot[11][1],z+(this.getHarshness()*qz)+this._loot[11][2],u+(this.getHarshness()*qu)+this._loot[11][3],v+(this.getHarshness()*qv)+this._loot[11][4],w+(this.getHarshness()*qw)+this._loot[11][5]+this.getMutation());

        return SimplexNoise.singleSimplex(false, makeSeedInt(seed),
                x+this.getHarshness()*rx,
                y+this.getHarshness()*ry,
                z+this.getHarshness()*rz,
                u+this.getHarshness()*ru,
                v+this.getHarshness()*rv,
                w+this.getHarshness()*rw+
                        this.getMutation());
    }

    @Override
    public double _noise1n(long seed, double x, int interpolation) {
        double qx = this.mDomain().noise2(x+this._loot[0][0], this.getMutation());

        double rx = this.mDomain().noise2(x+(this.getHarshness()*qx)+this._loot[6][ 0],this.getMutation());

        return SimplexNoise.singleSimplex(true, makeSeedInt(seed),
                x+this.getHarshness()*rx,
                this.getMutation());
    }

    @Override
    public double _noise2n(long seed, double x, double y, int interpolation) {
        double qx = this.mDomain().noise3(x+this._loot[0][0],y+this._loot[0][1], this.getMutation());
        double qy = this.mDomain().noise3(x+this._loot[1][0],y+this._loot[1][1], this.getMutation());
        double qz = this.mDomain().noise3(x+this._loot[2][0],y+this._loot[2][1], this.getMutation());

        double rx = this.mDomain().noise3(x+(this.getHarshness()*qx)+this._loot[6][ 0],y+(this.getHarshness()*qy)+this._loot[6][ 1],this.getMutation());
        double ry = this.mDomain().noise3(x+(this.getHarshness()*qx)+this._loot[7][ 0],y+(this.getHarshness()*qy)+this._loot[7][ 1],this.getMutation());
        double rz = this.mDomain().noise3(x+(this.getHarshness()*qx)+this._loot[8][ 0],y+(this.getHarshness()*qy)+this._loot[8][ 1],this.getMutation());

        return SimplexNoise.singleSimplex(true, makeSeedInt(seed),
                x+this.getHarshness()*rx,
                y+this.getHarshness()*ry,
                this.getMutation());
    }

    @Override
    public double _noise3n(long seed, double x, double y, double z, int interpolation) {
        double qx = this.mDomain().noise4(x+this._loot[0][0],y+this._loot[0][1], z+this._loot[0][2], this.getMutation());
        double qy = this.mDomain().noise4(x+this._loot[1][0],y+this._loot[1][1], z+this._loot[1][2], this.getMutation());
        double qz = this.mDomain().noise4(x+this._loot[2][0],y+this._loot[2][1], z+this._loot[2][2], this.getMutation());

        double rx = this.mDomain().noise4(x+(this.getHarshness()*qx)+this._loot[6][ 0],y+(this.getHarshness()*qy)+this._loot[6][ 1],z+(this.getHarshness()*qz)+this._loot[6][ 2],this.getMutation());
        double ry = this.mDomain().noise4(x+(this.getHarshness()*qx)+this._loot[7][ 0],y+(this.getHarshness()*qy)+this._loot[7][ 1],z+(this.getHarshness()*qz)+this._loot[7][ 2],this.getMutation());
        double rz = this.mDomain().noise4(x+(this.getHarshness()*qx)+this._loot[8][ 0],y+(this.getHarshness()*qy)+this._loot[8][ 1],z+(this.getHarshness()*qz)+this._loot[8][ 2],this.getMutation());

        return SimplexNoise.singleSimplex(true, makeSeedInt(seed),
                x+this.getHarshness()*rx,
                y+this.getHarshness()*ry,
                z+this.getHarshness()*rz,
                this.getMutation());
    }

    @Override
    public double _noise4n(long seed, double x, double y, double z, double u, int interpolation) {
        double qx = this.mDomain().noise5(x+this._loot[0][0],y+this._loot[0][1], z+this._loot[0][2], u+this._loot[0][3], this.getMutation());
        double qy = this.mDomain().noise5(x+this._loot[1][0],y+this._loot[1][1], z+this._loot[1][2], u+this._loot[1][3], this.getMutation());
        double qz = this.mDomain().noise5(x+this._loot[2][0],y+this._loot[2][1], z+this._loot[2][2], u+this._loot[2][3], this.getMutation());
        double qu = this.mDomain().noise5(x+this._loot[3][0],y+this._loot[3][1], z+this._loot[3][2], u+this._loot[3][3], this.getMutation());

        double rx = this.mDomain().noise5(x+(this.getHarshness()*qx)+this._loot[6][ 0],y+(this.getHarshness()*qy)+this._loot[6][ 1],z+(this.getHarshness()*qz)+this._loot[6][ 2],u+(this.getHarshness()*qu)+this._loot[6][ 3],this.getMutation());
        double ry = this.mDomain().noise5(x+(this.getHarshness()*qx)+this._loot[7][ 0],y+(this.getHarshness()*qy)+this._loot[7][ 1],z+(this.getHarshness()*qz)+this._loot[7][ 2],u+(this.getHarshness()*qu)+this._loot[7][ 3],this.getMutation());
        double rz = this.mDomain().noise5(x+(this.getHarshness()*qx)+this._loot[8][ 0],y+(this.getHarshness()*qy)+this._loot[8][ 1],z+(this.getHarshness()*qz)+this._loot[8][ 2],u+(this.getHarshness()*qu)+this._loot[8][ 3],this.getMutation());
        double ru = this.mDomain().noise5(x+(this.getHarshness()*qx)+this._loot[9][ 0],y+(this.getHarshness()*qy)+this._loot[9][ 1],z+(this.getHarshness()*qz)+this._loot[9][ 2],u+(this.getHarshness()*qu)+this._loot[9][ 3],this.getMutation());

        return SimplexNoise.singleSimplex(true, makeSeedInt(seed),
                x+this.getHarshness()*rx,
                y+this.getHarshness()*ry,
                z+this.getHarshness()*rz,
                u+this.getHarshness()*ru,
                this.getMutation());
    }

    @Override
    public double _noise5n(long seed, double x, double y, double z, double u, double v, int interpolation) {
        double qx = this.mDomain().noise6(x+this._loot[0][0],y+this._loot[0][1], z+this._loot[0][2], u+this._loot[0][3], v+this._loot[0][4], this.getMutation());
        double qy = this.mDomain().noise6(x+this._loot[1][0],y+this._loot[1][1], z+this._loot[1][2], u+this._loot[1][3], v+this._loot[1][4], this.getMutation());
        double qz = this.mDomain().noise6(x+this._loot[2][0],y+this._loot[2][1], z+this._loot[2][2], u+this._loot[2][3], v+this._loot[2][4], this.getMutation());
        double qu = this.mDomain().noise6(x+this._loot[3][0],y+this._loot[3][1], z+this._loot[3][2], u+this._loot[3][3], v+this._loot[3][4], this.getMutation());
        double qv = this.mDomain().noise6(x+this._loot[4][0],y+this._loot[4][1], z+this._loot[4][2], u+this._loot[4][3], v+this._loot[4][4], this.getMutation());

        double rx = this.mDomain().noise6(x+(this.getHarshness()*qx)+this._loot[6][ 0],y+(this.getHarshness()*qy)+this._loot[6][ 1],z+(this.getHarshness()*qz)+this._loot[6][ 2],u+(this.getHarshness()*qu)+this._loot[6][ 3],v+(this.getHarshness()*qv)+this._loot[6][ 4],this.getMutation());
        double ry = this.mDomain().noise6(x+(this.getHarshness()*qx)+this._loot[7][ 0],y+(this.getHarshness()*qy)+this._loot[7][ 1],z+(this.getHarshness()*qz)+this._loot[7][ 2],u+(this.getHarshness()*qu)+this._loot[7][ 3],v+(this.getHarshness()*qv)+this._loot[7][ 4],this.getMutation());
        double rz = this.mDomain().noise6(x+(this.getHarshness()*qx)+this._loot[8][ 0],y+(this.getHarshness()*qy)+this._loot[8][ 1],z+(this.getHarshness()*qz)+this._loot[8][ 2],u+(this.getHarshness()*qu)+this._loot[8][ 3],v+(this.getHarshness()*qv)+this._loot[8][ 4],this.getMutation());
        double ru = this.mDomain().noise6(x+(this.getHarshness()*qx)+this._loot[9][ 0],y+(this.getHarshness()*qy)+this._loot[9][ 1],z+(this.getHarshness()*qz)+this._loot[9][ 2],u+(this.getHarshness()*qu)+this._loot[9][ 3],v+(this.getHarshness()*qv)+this._loot[9][ 4],this.getMutation());
        double rv = this.mDomain().noise6(x+(this.getHarshness()*qx)+this._loot[10][0],y+(this.getHarshness()*qy)+this._loot[10][1],z+(this.getHarshness()*qz)+this._loot[10][2],u+(this.getHarshness()*qu)+this._loot[10][3],v+(this.getHarshness()*qv)+this._loot[10][4],this.getMutation());

        return SimplexNoise.singleSimplex(true, makeSeedInt(seed),
                x+this.getHarshness()*rx,
                y+this.getHarshness()*ry,
                z+this.getHarshness()*rz,
                u+this.getHarshness()*ru,
                v+this.getHarshness()*rv,
                this.getMutation());
    }

    @Override
    public double _noise6n(long seed, double x, double y, double z, double u, double v, double w, int interpolation) {
        double qx = this.mDomain().noise6(x+this._loot[0][0],y+this._loot[0][1], z+this._loot[0][2], u+this._loot[0][3], v+this._loot[0][4], w+this._loot[0][5]+this.getMutation());
        double qy = this.mDomain().noise6(x+this._loot[1][0],y+this._loot[1][1], z+this._loot[1][2], u+this._loot[1][3], v+this._loot[1][4], w+this._loot[1][5]+this.getMutation());
        double qz = this.mDomain().noise6(x+this._loot[2][0],y+this._loot[2][1], z+this._loot[2][2], u+this._loot[2][3], v+this._loot[2][4], w+this._loot[2][5]+this.getMutation());
        double qu = this.mDomain().noise6(x+this._loot[3][0],y+this._loot[3][1], z+this._loot[3][2], u+this._loot[3][3], v+this._loot[3][4], w+this._loot[3][5]+this.getMutation());
        double qv = this.mDomain().noise6(x+this._loot[4][0],y+this._loot[4][1], z+this._loot[4][2], u+this._loot[4][3], v+this._loot[4][4], w+this._loot[4][5]+this.getMutation());
        double qw = this.mDomain().noise6(x+this._loot[5][0],y+this._loot[5][1], z+this._loot[5][2], u+this._loot[5][3], v+this._loot[5][4], w+this._loot[5][5]+this.getMutation());

        double rx = this.mDomain().noise6(x+(this.getHarshness()*qx)+this._loot[6][ 0],y+(this.getHarshness()*qy)+this._loot[6][ 1],z+(this.getHarshness()*qz)+this._loot[6][ 2],u+(this.getHarshness()*qu)+this._loot[6][ 3],v+(this.getHarshness()*qv)+this._loot[6][ 4],w+(this.getHarshness()*qw)+this._loot[6][ 5]+this.getMutation());
        double ry = this.mDomain().noise6(x+(this.getHarshness()*qx)+this._loot[7][ 0],y+(this.getHarshness()*qy)+this._loot[7][ 1],z+(this.getHarshness()*qz)+this._loot[7][ 2],u+(this.getHarshness()*qu)+this._loot[7][ 3],v+(this.getHarshness()*qv)+this._loot[7][ 4],w+(this.getHarshness()*qw)+this._loot[7][ 5]+this.getMutation());
        double rz = this.mDomain().noise6(x+(this.getHarshness()*qx)+this._loot[8][ 0],y+(this.getHarshness()*qy)+this._loot[8][ 1],z+(this.getHarshness()*qz)+this._loot[8][ 2],u+(this.getHarshness()*qu)+this._loot[8][ 3],v+(this.getHarshness()*qv)+this._loot[8][ 4],w+(this.getHarshness()*qw)+this._loot[8][ 5]+this.getMutation());
        double ru = this.mDomain().noise6(x+(this.getHarshness()*qx)+this._loot[9][ 0],y+(this.getHarshness()*qy)+this._loot[9][ 1],z+(this.getHarshness()*qz)+this._loot[9][ 2],u+(this.getHarshness()*qu)+this._loot[9][ 3],v+(this.getHarshness()*qv)+this._loot[9][ 4],w+(this.getHarshness()*qw)+this._loot[9][ 5]+this.getMutation());
        double rv = this.mDomain().noise6(x+(this.getHarshness()*qx)+this._loot[10][0],y+(this.getHarshness()*qy)+this._loot[10][1],z+(this.getHarshness()*qz)+this._loot[10][2],u+(this.getHarshness()*qu)+this._loot[10][3],v+(this.getHarshness()*qv)+this._loot[10][4],w+(this.getHarshness()*qw)+this._loot[10][5]+this.getMutation());
        double rw = this.mDomain().noise6(x+(this.getHarshness()*qx)+this._loot[11][0],y+(this.getHarshness()*qy)+this._loot[11][1],z+(this.getHarshness()*qz)+this._loot[11][2],u+(this.getHarshness()*qu)+this._loot[11][3],v+(this.getHarshness()*qv)+this._loot[11][4],w+(this.getHarshness()*qw)+this._loot[11][5]+this.getMutation());

        return SimplexNoise.singleSimplex(true, makeSeedInt(seed),
                x+this.getHarshness()*rx,
                y+this.getHarshness()*ry,
                z+this.getHarshness()*rz,
                u+this.getHarshness()*ru,
                v+this.getHarshness()*rv,
                w+this.getHarshness()*rw+
                        this.getMutation());
    }


}
