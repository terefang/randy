package com.github.terefang.randy.noise.impl;

import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.NoiseUtil;

public class SimplexNoise extends NoiseUtil implements INoise
{
    @Override
    public double _noise1(long seed, double x, int interpolation) {
        return singleSimplex(false, makeSeedInt(seed), x, this.getMutation());
    }

    @Override
    public double _noise2(long seed, double x, double y, int interpolation) {
        return singleSimplex(false, makeSeedInt(seed), x,y, this.getMutation());
    }

    @Override
    public double _noise3(long seed, double x, double y, double z, int interpolation) {
        return singleSimplex(false, makeSeedInt(seed), x,y,z, this.getMutation());
    }

    @Override
    public double _noise4(long seed, double x, double y, double z, double u, int interpolation) {
        return singleSimplex(false, makeSeedInt(seed), x,y,z,u, this.getMutation());
    }

    @Override
    public double _noise5(long seed, double x, double y, double z, double u, double v, int interpolation) {
        return singleSimplex(false, makeSeedInt(seed), x,y,z,u,v, this.getMutation());
    }

    @Override
    public double _noise6(long seed, double x, double y, double z, double u, double v, double w, int interpolation) {
        return singleSimplex(false, makeSeedInt(seed), x,y,z,u,v,w+this.getMutation());
    }

    @Override
    public double _noise1n(long seed, double x, int interpolation) {
        return singleSimplex(true, makeSeedInt(seed), x, this.getMutation());
    }

    @Override
    public double _noise2n(long seed, double x, double y, int interpolation) {
        return singleSimplex(true, makeSeedInt(seed), x,y, this.getMutation());
    }

    @Override
    public double _noise3n(long seed, double x, double y, double z, int interpolation) {
        return singleSimplex(true, makeSeedInt(seed), x,y,z, this.getMutation());
    }

    @Override
    public double _noise4n(long seed, double x, double y, double z, double u, int interpolation) {
        return singleSimplex(true, makeSeedInt(seed), x,y,z,u, this.getMutation());
    }

    @Override
    public double _noise5n(long seed, double x, double y, double z, double u, double v, int interpolation) {
        return singleSimplex(true, makeSeedInt(seed), x,y,z,u,v, this.getMutation());
    }

    @Override
    public double _noise6n(long seed, double x, double y, double z, double u, double v, double w, int interpolation) {
        return singleSimplex(true, makeSeedInt(seed), x,y,z,u,v,w+this.getMutation());
    }


    // ----------------------------------------------------------------------------
    // 2d simplex

    public static final double singleSimplex(boolean normalize, int seed, double x, double y) {
        double t = (x + y) * F2f;
        int i = fastFloor(x + t);
        int j = fastFloor(y + t);

        t = (i + j) * G2f;
        double X0 = i - t;
        double Y0 = j - t;

        double x0 = x - X0;
        double y0 = y - Y0;

        int i1, j1;
        if (x0 > y0) {
            i1 = 1;
            j1 = 0;
        } else {
            i1 = 0;
            j1 = 1;
        }

        double x1 = x0 - i1 + G2f;
        double y1 = y0 - j1 + G2f;
        double x2 = x0 - 1 + H2f;
        double y2 = y0 - 1 + H2f;

        double n = 0f;

        t = 0.5f - x0 * x0 - y0 * y0;
        if (t >= 0) {
            t *= t;
            n += t * t * gradCoord2D(seed, i, j, x0, y0);
        }

        t = 0.5f - x1 * x1 - y1 * y1;
        if (t > 0) {
            t *= t;
            n += t * t * gradCoord2D(seed, i + i1, j + j1, x1, y1);
        }

        t = 0.5f - x2 * x2 - y2 * y2;
        if (t > 0)  {
            t *= t;
            n += t * t * gradCoord2D(seed, i + 1, j + 1, x2, y2);
        }

        double _v = 99.20689070704672f * n; //9.11f * n;
        if(normalize) _v = (_v*.5)+.5;
        return _v;
    }

    // 3d simplex
    public static final double singleSimplex(boolean normalize, int seed, double x, double y, double z) {
        double t = (x + y + z) * F3f;
        int i = fastFloor(x + t);
        int j = fastFloor(y + t);
        int k = fastFloor(z + t);

        t = (i + j + k) * G3f;
        double x0 = x - (i - t);
        double y0 = y - (j - t);
        double z0 = z - (k - t);

        int i1, j1, k1;
        int i2, j2, k2;

        if (x0 >= y0) {
            if (y0 >= z0) {
                i1 = 1;
                j1 = 0;
                k1 = 0;
                i2 = 1;
                j2 = 1;
                k2 = 0;
            } else if (x0 >= z0) {
                i1 = 1;
                j1 = 0;
                k1 = 0;
                i2 = 1;
                j2 = 0;
                k2 = 1;
            } else // x0 < z0
            {
                i1 = 0;
                j1 = 0;
                k1 = 1;
                i2 = 1;
                j2 = 0;
                k2 = 1;
            }
        } else // x0 < y0
        {
            if (y0 < z0) {
                i1 = 0;
                j1 = 0;
                k1 = 1;
                i2 = 0;
                j2 = 1;
                k2 = 1;
            } else if (x0 < z0) {
                i1 = 0;
                j1 = 1;
                k1 = 0;
                i2 = 0;
                j2 = 1;
                k2 = 1;
            } else // x0 >= z0
            {
                i1 = 0;
                j1 = 1;
                k1 = 0;
                i2 = 1;
                j2 = 1;
                k2 = 0;
            }
        }

        double x1 = x0 - i1 + G3f;
        double y1 = y0 - j1 + G3f;
        double z1 = z0 - k1 + G3f;
        double x2 = x0 - i2 + F3f;
        double y2 = y0 - j2 + F3f;
        double z2 = z0 - k2 + F3f;
        double x3 = x0 - 0.5f;
        double y3 = y0 - 0.5f;
        double z3 = z0 - 0.5f;

        double n = 0;

        t = 0.6f - x0 * x0 - y0 * y0 - z0 * z0;
        if (t > 0) {
            t *= t;
            n += t * t * gradCoord3D(seed, i, j, k, x0, y0, z0);
        }

        t = 0.6f - x1 * x1 - y1 * y1 - z1 * z1;
        if (t > 0) {
            t *= t;
            n += t * t * gradCoord3D(seed, i + i1, j + j1, k + k1, x1, y1, z1);
        }

        t = 0.6f - x2 * x2 - y2 * y2 - z2 * z2;
        if (t > 0) {
            t *= t;
            n += t * t * gradCoord3D(seed, i + i2, j + j2, k + k2, x2, y2, z2);
        }

        t = 0.6f - x3 * x3 - y3 * y3 - z3 * z3;
        if (t > 0)  {
            t *= t;
            n += t * t * gradCoord3D(seed, i + 1, j + 1, k + 1, x3, y3, z3);
        }

        double _v = 31.5f * n;
        if(normalize) _v = (_v*.5)+.5;
        return _v;
    }

    // 4d simplex
    public static final double singleSimplex(boolean normalize, int seed, double x, double y, double z, double w) {
        double n = 0f;
        double t = (x + y + z + w) * F4f;
        int i = fastFloor(x + t);
        int j = fastFloor(y + t);
        int k = fastFloor(z + t);
        int l = fastFloor(w + t);
        t = (i + j + k + l) * G4f;
        final double X0 = i - t;
        final double Y0 = j - t;
        final double Z0 = k - t;
        final double W0 = l - t;
        final double x0 = x - X0;
        final double y0 = y - Y0;
        final double z0 = z - Z0;
        final double w0 = w - W0;

        final int c = (x0 > y0 ? 128 : 0) | (x0 > z0 ? 64 : 0) | (y0 > z0 ? 32 : 0) | (x0 > w0 ? 16 : 0) | (y0 > w0 ? 8 : 0) | (z0 > w0 ? 4 : 0);
        final int ip = SIMPLEX_4D[c];
        final int jp = SIMPLEX_4D[c + 1];
        final int kp = SIMPLEX_4D[c + 2];
        final int lp = SIMPLEX_4D[c + 3];

        final int i1 = ip >> 2;
        final int i2 = ip >> 1 & 1;
        final int i3 = ip & 1;
        final int j1 = jp >> 2;
        final int j2 = jp >> 1 & 1;
        final int j3 = jp & 1;
        final int k1 = kp >> 2;
        final int k2 = kp >> 1 & 1;
        final int k3 = kp & 1;
        final int l1 = lp >> 2;
        final int l2 = lp >> 1 & 1;
        final int l3 = lp & 1;

        final double x1 = x0 - i1 + G4f;
        final double y1 = y0 - j1 + G4f;
        final double z1 = z0 - k1 + G4f;
        final double w1 = w0 - l1 + G4f;
        final double x2 = x0 - i2 + 2 * G4f;
        final double y2 = y0 - j2 + 2 * G4f;
        final double z2 = z0 - k2 + 2 * G4f;
        final double w2 = w0 - l2 + 2 * G4f;
        final double x3 = x0 - i3 + 3 * G4f;
        final double y3 = y0 - j3 + 3 * G4f;
        final double z3 = z0 - k3 + 3 * G4f;
        final double w3 = w0 - l3 + 3 * G4f;
        final double x4 = x0 - 1 + 4 * G4f;
        final double y4 = y0 - 1 + 4 * G4f;
        final double z4 = z0 - 1 + 4 * G4f;
        final double w4 = w0 - 1 + 4 * G4f;

        t = 0.5f - x0 * x0 - y0 * y0 - z0 * z0 - w0 * w0;
        if (t > 0) {
            t *= t;
            n = t * t * gradCoord4D(seed, i, j, k, l, x0, y0, z0, w0);
        }
        t = 0.5f - x1 * x1 - y1 * y1 - z1 * z1 - w1 * w1;
        if (t > 0) {
            t *= t;
            n += t * t * gradCoord4D(seed, i + i1, j + j1, k + k1, l + l1, x1, y1, z1, w1);
        }
        t = 0.5f - x2 * x2 - y2 * y2 - z2 * z2 - w2 * w2;
        if (t > 0) {
            t *= t;
            n += t * t * gradCoord4D(seed, i + i2, j + j2, k + k2, l + l2, x2, y2, z2, w2);
        }
        t = 0.5f - x3 * x3 - y3 * y3 - z3 * z3 - w3 * w3;
        if (t > 0) {
            t *= t;
            n += t * t * gradCoord4D(seed, i + i3, j + j3, k + k3, l + l3, x3, y3, z3, w3);
        }
        t = 0.5f - x4 * x4 - y4 * y4 - z4 * z4 - w4 * w4;
        if (t > 0) {
            t *= t;
            n += t * t * gradCoord4D(seed, i + 1, j + 1, k + 1, l + 1, x4, y4, z4, w4);
        }

        double _v = 4.9f * n;
        if(normalize) _v = (_v*.5)+.5;
        return _v;
    }

    // 5D Simplex
    private static final double
            F5 = (double) ((Math.sqrt(6.0) - 1.0) / 5.0),
            G5 = (double) ((6.0 - Math.sqrt(6.0)) / 30.0),
            LIMIT5 = 0.7f;

    public static final double singleSimplex(boolean normalize, int seed, double x, double y, double z, double w, double u) {
        double n0, n1, n2, n3, n4, n5;
        double t = (x + y + z + w + u) * F5;
        int i = fastFloor(x + t);
        int j = fastFloor(y + t);
        int k = fastFloor(z + t);
        int l = fastFloor(w + t);
        int h = fastFloor(u + t);
        t = (i + j + k + l + h) * G5;
        double X0 = i - t;
        double Y0 = j - t;
        double Z0 = k - t;
        double W0 = l - t;
        double U0 = h - t;
        double x0 = x - X0;
        double y0 = y - Y0;
        double z0 = z - Z0;
        double w0 = w - W0;
        double u0 = u - U0;

        int rankx = 0;
        int ranky = 0;
        int rankz = 0;
        int rankw = 0;
        int ranku = 0;

        if (x0 > y0) rankx++; else ranky++;
        if (x0 > z0) rankx++; else rankz++;
        if (x0 > w0) rankx++; else rankw++;
        if (x0 > u0) rankx++; else ranku++;

        if (y0 > z0) ranky++; else rankz++;
        if (y0 > w0) ranky++; else rankw++;
        if (y0 > u0) ranky++; else ranku++;

        if (z0 > w0) rankz++; else rankw++;
        if (z0 > u0) rankz++; else ranku++;

        if (w0 > u0) rankw++; else ranku++;

        int i1 = 3 - rankx >>> 31;
        int j1 = 3 - ranky >>> 31;
        int k1 = 3 - rankz >>> 31;
        int l1 = 3 - rankw >>> 31;
        int h1 = 3 - ranku >>> 31;

        int i2 = 2 - rankx >>> 31;
        int j2 = 2 - ranky >>> 31;
        int k2 = 2 - rankz >>> 31;
        int l2 = 2 - rankw >>> 31;
        int h2 = 2 - ranku >>> 31;

        int i3 = 1 - rankx >>> 31;
        int j3 = 1 - ranky >>> 31;
        int k3 = 1 - rankz >>> 31;
        int l3 = 1 - rankw >>> 31;
        int h3 = 1 - ranku >>> 31;

        int i4 = -rankx >>> 31;
        int j4 = -ranky >>> 31;
        int k4 = -rankz >>> 31;
        int l4 = -rankw >>> 31;
        int h4 = -ranku >>> 31;

        double x1 = x0 - i1 + G5;
        double y1 = y0 - j1 + G5;
        double z1 = z0 - k1 + G5;
        double w1 = w0 - l1 + G5;
        double u1 = u0 - h1 + G5;

        double x2 = x0 - i2 + 2 * G5;
        double y2 = y0 - j2 + 2 * G5;
        double z2 = z0 - k2 + 2 * G5;
        double w2 = w0 - l2 + 2 * G5;
        double u2 = u0 - h2 + 2 * G5;

        double x3 = x0 - i3 + 3 * G5;
        double y3 = y0 - j3 + 3 * G5;
        double z3 = z0 - k3 + 3 * G5;
        double w3 = w0 - l3 + 3 * G5;
        double u3 = u0 - h3 + 3 * G5;

        double x4 = x0 - i4 + 4 * G5;
        double y4 = y0 - j4 + 4 * G5;
        double z4 = z0 - k4 + 4 * G5;
        double w4 = w0 - l4 + 4 * G5;
        double u4 = u0 - h4 + 4 * G5;

        double x5 = x0 - 1 + 5 * G5;
        double y5 = y0 - 1 + 5 * G5;
        double z5 = z0 - 1 + 5 * G5;
        double w5 = w0 - 1 + 5 * G5;
        double u5 = u0 - 1 + 5 * G5;

        t = LIMIT5 - x0 * x0 - y0 * y0 - z0 * z0 - w0 * w0 - u0 * u0;
        if (t < 0) n0 = 0;
        else
        {
            t *= t;
            n0 = t * t * gradCoord5D(seed, i, j, k, l, h, x0, y0, z0, w0, u0);
        }

        t = LIMIT5 - x1 * x1 - y1 * y1 - z1 * z1 - w1 * w1 - u1 * u1;
        if (t < 0) n1 = 0;
        else
        {
            t *= t;
            n1 = t * t * gradCoord5D(seed, i + i1, j + j1, k + k1, l + l1, h + h1, x1, y1, z1, w1, u1);
        }

        t = LIMIT5 - x2 * x2 - y2 * y2 - z2 * z2 - w2 * w2 - u2 * u2;
        if (t < 0) n2 = 0;
        else
        {
            t *= t;
            n2 = t * t * gradCoord5D(seed, i + i2, j + j2, k + k2, l + l2, h + h2, x2, y2, z2, w2, u2);
        }

        t = LIMIT5 - x3 * x3 - y3 * y3 - z3 * z3 - w3 * w3 - u3 * u3;
        if (t < 0) n3 = 0;
        else
        {
            t *= t;
            n3 = t * t * gradCoord5D(seed, i + i3, j + j3, k + k3, l + l3, h + h3, x3, y3, z3, w3, u3);
        }

        t = LIMIT5 - x4 * x4 - y4 * y4 - z4 * z4 - w4 * w4 - u4 * u4;
        if (t < 0) n4 = 0;
        else
        {
            t *= t;
            n4 = t * t * gradCoord5D(seed, i + i4, j + j4, k + k4, l + l4, h + h4, x4, y4, z4, w4, u4);
        }

        t = LIMIT5 - x5 * x5 - y5 * y5 - z5 * z5 - w5 * w5 - u5 * u5;
        if (t < 0) n5 = 0;
        else
        {
            t *= t;
            n5 = t * t * gradCoord5D(seed, i + 1, j + 1, k + 1, l + 1, h + 1, x5, y5, z5, w5, u5);
        }

        double _v = (n0 + n1 + n2 + n3 + n4 + n5) * 10f;
        if(normalize) _v = (_v*.5)+.5;
        return _v;
    }

    // 6D Simplex
    private static final double
            F6 = (double) ((Math.sqrt(7.0) - 1.0) / 6.0),
            G6 = F6 / (1f + 6f * F6),
            LIMIT6 = 0.8375f;

    public static final double singleSimplex(boolean normalize, int seed, double x, double y, double z, double w, double u, double v) {
        final double[] m = {0, 0, 0, 0, 0, 0}, cellDist = {0, 0, 0, 0, 0, 0};
        final int[] distOrder = {0, 0, 0, 0, 0, 0}, intLoc = {0, 0, 0, 0, 0, 0};
        final double s = (x + y + z + w + u + v) * F6;

        final int skewX = fastFloor(x + s), skewY = fastFloor(y + s), skewZ = fastFloor(z + s),
                skewW = fastFloor(w + s), skewU = fastFloor(u + s), skewV = fastFloor(v + s);
        intLoc[0] = skewX;
        intLoc[1] = skewY;
        intLoc[2] = skewZ;
        intLoc[3] = skewW;
        intLoc[4] = skewU;
        intLoc[5] = skewV;

        final double unskew = (skewX + skewY + skewZ + skewW + skewU + skewV) * G6;
        cellDist[0] = x - skewX + unskew;
        cellDist[1] = y - skewY + unskew;
        cellDist[2] = z - skewZ + unskew;
        cellDist[3] = w - skewW + unskew;
        cellDist[4] = u - skewU + unskew;
        cellDist[5] = v - skewV + unskew;

        int o0 = (cellDist[0]<cellDist[1]?1:0)+(cellDist[0]<cellDist[2]?1:0)+(cellDist[0]<cellDist[3]?1:0)+(cellDist[0]<cellDist[4]?1:0)+(cellDist[0]<cellDist[5]?1:0);
        int o1 = (cellDist[1]<=cellDist[0]?1:0)+(cellDist[1]<cellDist[2]?1:0)+(cellDist[1]<cellDist[3]?1:0)+(cellDist[1]<cellDist[4]?1:0)+(cellDist[1]<cellDist[5]?1:0);
        int o2 = (cellDist[2]<=cellDist[0]?1:0)+(cellDist[2]<=cellDist[1]?1:0)+(cellDist[2]<cellDist[3]?1:0)+(cellDist[2]<cellDist[4]?1:0)+(cellDist[2]<cellDist[5]?1:0);
        int o3 = (cellDist[3]<=cellDist[0]?1:0)+(cellDist[3]<=cellDist[1]?1:0)+(cellDist[3]<=cellDist[2]?1:0)+(cellDist[3]<cellDist[4]?1:0)+(cellDist[3]<cellDist[5]?1:0);
        int o4 = (cellDist[4]<=cellDist[0]?1:0)+(cellDist[4]<=cellDist[1]?1:0)+(cellDist[4]<=cellDist[2]?1:0)+(cellDist[4]<=cellDist[3]?1:0)+(cellDist[4]<cellDist[5]?1:0);
        int o5 = 15-(o0+o1+o2+o3+o4);

        distOrder[o0]=0;
        distOrder[o1]=1;
        distOrder[o2]=2;
        distOrder[o3]=3;
        distOrder[o4]=4;
        distOrder[o5]=5;

        double n = 0;
        double skewOffset = 0;

        for (int c = -1; c < 6; c++) {
            if (c != -1) intLoc[distOrder[c]]++;

            m[0] = cellDist[0] - (intLoc[0] - skewX) + skewOffset;
            m[1] = cellDist[1] - (intLoc[1] - skewY) + skewOffset;
            m[2] = cellDist[2] - (intLoc[2] - skewZ) + skewOffset;
            m[3] = cellDist[3] - (intLoc[3] - skewW) + skewOffset;
            m[4] = cellDist[4] - (intLoc[4] - skewU) + skewOffset;
            m[5] = cellDist[5] - (intLoc[5] - skewV) + skewOffset;

            double tc = LIMIT6;

            for (int d = 0; d < 6; d++) {
                tc -= m[d] * m[d];
            }

            if (tc > 0) {
                tc *= tc;
                n += gradCoord6D(seed, intLoc[0], intLoc[1], intLoc[2], intLoc[3], intLoc[4], intLoc[5],
                        m[0], m[1], m[2], m[3], m[4], m[5]) * tc * tc;
            }
            skewOffset += G6;
        }
        double _v = 7.5f * n;
        if(normalize) _v = (_v*.5)+.5;
        return _v;
    }


    private static final int[] SIMPLEX_4D = {
            0, 1, 3, 7, 0, 1, 7, 3,
            0, 0, 0, 0, 0, 3, 7, 1,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 1, 3, 7, 0,
            0, 3, 1, 7, 0, 0, 0, 0,
            0, 7, 1, 3, 0, 7, 3, 1,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 1, 7, 3, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            1, 3, 0, 7, 0, 0, 0, 0,
            1, 7, 0, 3, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            3, 7, 0, 1, 3, 7, 1, 0,
            1, 0, 3, 7, 1, 0, 7, 3,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 3, 0, 7, 1,
            0, 0, 0, 0, 3, 1, 7, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            3, 0, 1, 7, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            7, 0, 1, 3, 7, 0, 3, 1,
            0, 0, 0, 0, 7, 1, 3, 0,
            3, 1, 0, 7, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            7, 1, 0, 3, 0, 0, 0, 0,
            7, 3, 0, 1, 7, 3, 1, 0,
    };

    static double _sqrt2 = Math.sqrt(2.);

    protected static double gradCoord5D(int seed, int x, int y, int z, int w, int u,
                                        double xd, double yd, double zd, double wd, double ud) {
        final int hash = hash256(x, y, z, w, u, seed) << 3;
        return xd * _sqrt2 * GRADIENTS_5D[hash] + yd * _sqrt2 * GRADIENTS_5D[hash+1] + zd * _sqrt2 * GRADIENTS_5D[hash+2] + wd * _sqrt2 * GRADIENTS_5D[hash+3] + ud * _sqrt2 * GRADIENTS_5D[hash+4];
    }

    /**
     * Randomly selected points on the 5D unit hypersphere (mathematically, the '4-sphere').
     * Each point is stored in 5 floats, and there are 3 floats of padding after each point
     * (to allow easier access to points using bitwise operations). The distance from each
     * point to the origin should be 1.0, subject to rounding error; this is implied by the
     * point being on the unit hypersphere, but earlier gradient vectors used a different
     * distance to the origin.
     * <br>
     * These points were randomly selected, but 10 million sets of candidate points were tried
     * (all also random) and this set had the greatest minimum distance between any two points.
     * For every point on the sphere present here, the polar opposite of that point is also
     * present, ensuring there is no bias along any particular axis. The points were also
     * shuffled, in case they are retrieved by a less-random method (such as iteration).
     * <br>
     * This particular set of gradient vectors is almost certainly not optimal, but finding a
     * better one is very, very slow right now.
     */
    private static final double[] GRADIENTS_5D = {
            -0.7317531488f, -0.5457445300f, -0.3402074959f, +0.0473371427f, +0.2207222052f, 0.0f, 0.0f, 0.0f,
            -0.6403930783f, -0.0600652787f, +0.5519209931f, -0.4497233127f, +0.2818173647f, 0.0f, 0.0f, 0.0f,
            +0.6670350269f, +0.0831250542f, +0.5291879393f, -0.2559099233f, +0.4501385723f, 0.0f, 0.0f, 0.0f,
            -0.4791160276f, +0.0594813779f, -0.3657537709f, -0.7298187767f, -0.3170150311f, 0.0f, 0.0f, 0.0f,
            -0.1979725611f, +0.2185413808f, +0.7239656148f, -0.3953069660f, -0.4823408765f, 0.0f, 0.0f, 0.0f,
            +0.3760635139f, +0.3518950304f, -0.5265468095f, -0.5567847035f, +0.3840382436f, 0.0f, 0.0f, 0.0f,
            -0.0110312176f, +0.2566742059f, +0.2913437739f, -0.7619276089f, -0.5182487708f, 0.0f, 0.0f, 0.0f,
            -0.5947988211f, +0.6805773103f, +0.1936968685f, +0.2434525075f, -0.2936686685f, 0.0f, 0.0f, 0.0f,
            -0.4774024524f, -0.2984390650f, -0.0123754095f, +0.6080909387f, -0.5595473908f, 0.0f, 0.0f, 0.0f,
            -0.6936710105f, +0.5914497903f, -0.2536358587f, -0.0120015638f, +0.3233148441f, 0.0f, 0.0f, 0.0f,
            -0.5017986533f, -0.4516618892f, -0.2871519885f, -0.5008343077f, +0.4592476251f, 0.0f, 0.0f, 0.0f,
            -0.1592481994f, +0.0337156897f, -0.4090562619f, -0.5001420261f, -0.7456770021f, 0.0f, 0.0f, 0.0f,
            -0.6856085494f, +0.6102167551f, +0.1468328792f, -0.1862635518f, -0.3183118341f, 0.0f, 0.0f, 0.0f,
            +0.6896313153f, +0.1556943945f, -0.0093845359f, -0.6312371893f, -0.3187780510f, 0.0f, 0.0f, 0.0f,
            +0.6243140980f, +0.2510565670f, -0.2327672116f, -0.6620421891f, -0.2339274938f, 0.0f, 0.0f, 0.0f,
            +0.4662361046f, +0.0671357592f, +0.4199531625f, -0.7103056564f, +0.3118042663f, 0.0f, 0.0f, 0.0f,
            +0.0003869206f, -0.5418766004f, -0.7867388149f, +0.2956416699f, +0.0027641210f, 0.0f, 0.0f, 0.0f,
            -0.4511807817f, -0.4866109050f, -0.6810530650f, +0.2472500102f, -0.1862253595f, 0.0f, 0.0f, 0.0f,
            +0.3526828880f, -0.1545338839f, -0.6460653067f, +0.0485706383f, +0.6572477248f, 0.0f, 0.0f, 0.0f,
            -0.9374979192f, +0.0003634480f, +0.1956638355f, -0.0529505789f, -0.2828593628f, 0.0f, 0.0f, 0.0f,
            +0.5991521291f, +0.1048415611f, +0.7630327309f, -0.0510920987f, +0.2125926206f, 0.0f, 0.0f, 0.0f,
            +0.7676538380f, -0.0741530582f, -0.3421862228f, -0.5274981454f, +0.0993136674f, 0.0f, 0.0f, 0.0f,
            -0.1321620314f, -0.4456852942f, -0.4164761004f, -0.5466460887f, +0.5582325031f, 0.0f, 0.0f, 0.0f,
            -0.4289785384f, +0.1501554146f, -0.6463958554f, -0.0831576367f, +0.6071968137f, 0.0f, 0.0f, 0.0f,
            +0.6856085494f, -0.6102167551f, -0.1468328792f, +0.1862635518f, +0.3183118341f, 0.0f, 0.0f, 0.0f,
            +0.8123843032f, -0.2998053331f, -0.1066994074f, -0.4269862876f, -0.2375846226f, 0.0f, 0.0f, 0.0f,
            +0.4469277417f, -0.6440228067f, +0.2873544669f, -0.3771169201f, +0.4008746152f, 0.0f, 0.0f, 0.0f,
            -0.6226475025f, -0.1728884784f, +0.1386060079f, -0.1420063907f, +0.7369139850f, 0.0f, 0.0f, 0.0f,
            +0.0083948129f, +0.8565270917f, -0.0040509600f, +0.4588772677f, -0.2360214212f, 0.0f, 0.0f, 0.0f,
            +0.0266474585f, +0.9099592284f, -0.1452471139f, +0.3196855139f, -0.2190172682f, 0.0f, 0.0f, 0.0f,
            +0.3739018407f, -0.0146680802f, -0.1849091719f, +0.8933930292f, +0.1662520813f, 0.0f, 0.0f, 0.0f,
            +0.4115741800f, -0.6857744205f, +0.3223597835f, +0.2621709591f, -0.4332097606f, 0.0f, 0.0f, 0.0f,
            -0.6544506463f, +0.0759438002f, -0.2849515054f, -0.3407187112f, -0.6071575497f, 0.0f, 0.0f, 0.0f,
            +0.4791160276f, -0.0594813779f, +0.3657537709f, +0.7298187767f, +0.3170150311f, 0.0f, 0.0f, 0.0f,
            +0.3186461637f, +0.0830825713f, -0.0812808765f, -0.2169079666f, +0.9153721985f, 0.0f, 0.0f, 0.0f,
            -0.3228950515f, -0.5585644808f, +0.0951667811f, -0.6472052826f, -0.3947316969f, 0.0f, 0.0f, 0.0f,
            -0.4251901341f, +0.1298843219f, +0.2982846722f, +0.1283226009f, -0.8348071498f, 0.0f, 0.0f, 0.0f,
            +0.3184277537f, +0.4992514448f, +0.7579226334f, -0.1629259610f, -0.2199094667f, 0.0f, 0.0f, 0.0f,
            +0.5387050998f, -0.3854407235f, +0.2145881143f, +0.6755891483f, +0.2424118561f, 0.0f, 0.0f, 0.0f,
            +0.0356243820f, +0.0899453022f, +0.8119656333f, +0.5756195218f, -0.0038369850f, 0.0f, 0.0f, 0.0f,
            -0.5741187365f, +0.1235841833f, -0.3191536767f, -0.6695194417f, -0.3240359145f, 0.0f, 0.0f, 0.0f,
            -0.3996376969f, +0.5672556347f, -0.5716426644f, +0.4365635276f, +0.0338778186f, 0.0f, 0.0f, 0.0f,
            +0.4722350727f, -0.2110465218f, +0.2864663987f, -0.0013033655f, -0.8064668036f, 0.0f, 0.0f, 0.0f,
            +0.1733696301f, -0.3889110020f, -0.2874650667f, +0.0563998095f, -0.8560806626f, 0.0f, 0.0f, 0.0f,
            -0.2364553968f, +0.0354267569f, -0.9081222085f, +0.0966304021f, -0.3298642297f, 0.0f, 0.0f, 0.0f,
            -0.6098045696f, -0.6041828606f, +0.1039300939f, -0.1933295546f, +0.4635986161f, 0.0f, 0.0f, 0.0f,
            +0.1036635152f, +0.4444784468f, +0.6951697454f, -0.5258681645f, -0.1785902704f, 0.0f, 0.0f, 0.0f,
            +0.2331392129f, -0.3738057089f, +0.0177873006f, -0.4611190867f, +0.7700442839f, 0.0f, 0.0f, 0.0f,
            -0.5631546358f, -0.3597560646f, +0.4967548000f, -0.5246818757f, +0.1771327981f, 0.0f, 0.0f, 0.0f,
            -0.8543583759f, -0.3149601564f, -0.3506643506f, -0.0884130717f, +0.2002236435f, 0.0f, 0.0f, 0.0f,
            -0.0580365955f, +0.4679895624f, -0.6580709820f, -0.5757251976f, +0.1144578634f, 0.0f, 0.0f, 0.0f,
            +0.0700079328f, -0.8802164250f, -0.0473880978f, +0.0802563114f, +0.4600339414f, 0.0f, 0.0f, 0.0f,
            -0.3064628296f, -0.2762642492f, -0.3781335091f, +0.8047256834f, +0.1979652053f, 0.0f, 0.0f, 0.0f,
            +0.3410266288f, +0.4735573790f, -0.1135481796f, -0.3647196339f, +0.7166105266f, 0.0f, 0.0f, 0.0f,
            +0.3996376969f, -0.5672556347f, +0.5716426644f, -0.4365635276f, -0.0338778186f, 0.0f, 0.0f, 0.0f,
            -0.4423719461f, +0.4858350333f, -0.0640319867f, +0.1253802074f, -0.7405748375f, 0.0f, 0.0f, 0.0f,
            +0.0243107363f, -0.4068117636f, -0.8310269678f, +0.3457269075f, -0.1542084994f, 0.0f, 0.0f, 0.0f,
            -0.2967691780f, -0.3679242707f, -0.0543021981f, +0.5199193389f, +0.7094328287f, 0.0f, 0.0f, 0.0f,
            -0.3279518542f, -0.2950803178f, +0.4969318956f, -0.7226519086f, -0.1902842545f, 0.0f, 0.0f, 0.0f,
            +0.1592481994f, -0.0337156897f, +0.4090562619f, +0.5001420261f, +0.7456770021f, 0.0f, 0.0f, 0.0f,
            -0.1159050432f, +0.8426776301f, -0.1932970642f, -0.4654900474f, +0.1497187145f, 0.0f, 0.0f, 0.0f,
            -0.6215493103f, -0.0844785631f, +0.3922776381f, +0.5507219936f, +0.3864755720f, 0.0f, 0.0f, 0.0f,
            -0.5251151197f, +0.2737767426f, +0.3066796017f, -0.1047373479f, -0.7377520696f, 0.0f, 0.0f, 0.0f,
            -0.5875639603f, -0.1105468365f, -0.0690731524f, +0.6307173914f, -0.4898698411f, 0.0f, 0.0f, 0.0f,
            -0.3007193437f, +0.5282908180f, -0.0714975290f, -0.4075860072f, -0.6776713348f, 0.0f, 0.0f, 0.0f,
            -0.0414044431f, +0.5892146057f, -0.0188300210f, -0.7299152917f, +0.3434835045f, 0.0f, 0.0f, 0.0f,
            -0.3183884533f, +0.5387109733f, +0.3577739192f, +0.3699112519f, +0.5861593371f, 0.0f, 0.0f, 0.0f,
            +0.5370279383f, -0.2085470664f, +0.5306943632f, +0.1094139593f, -0.6119650259f, 0.0f, 0.0f, 0.0f,
            +0.8543583759f, +0.3149601564f, +0.3506643506f, +0.0884130717f, -0.2002236435f, 0.0f, 0.0f, 0.0f,
            +0.0336566015f, +0.1414530353f, +0.5225261705f, -0.3135734937f, -0.7794205137f, 0.0f, 0.0f, 0.0f,
            -0.0336566015f, -0.1414530353f, -0.5225261705f, +0.3135734937f, +0.7794205137f, 0.0f, 0.0f, 0.0f,
            +0.0580365955f, -0.4679895624f, +0.6580709820f, +0.5757251976f, -0.1144578634f, 0.0f, 0.0f, 0.0f,
            +0.2180198852f, +0.4461829567f, +0.2598732896f, +0.3761713610f, +0.7378001622f, 0.0f, 0.0f, 0.0f,
            -0.9699449962f, -0.0972661391f, -0.1937354525f, +0.0313763560f, -0.1059627347f, 0.0f, 0.0f, 0.0f,
            -0.4469277417f, +0.6440228067f, -0.2873544669f, +0.3771169201f, -0.4008746152f, 0.0f, 0.0f, 0.0f,
            -0.2733435724f, +0.3535085518f, +0.2919628458f, +0.6344494035f, -0.5590587145f, 0.0f, 0.0f, 0.0f,
            -0.4796140497f, -0.7530615014f, -0.3925098940f, +0.1549720436f, -0.1574432827f, 0.0f, 0.0f, 0.0f,
            -0.1036635152f, -0.4444784468f, -0.6951697454f, +0.5258681645f, +0.1785902704f, 0.0f, 0.0f, 0.0f,
            -0.3783872335f, +0.5282608896f, -0.3713871691f, -0.4541142750f, -0.4833376976f, 0.0f, 0.0f, 0.0f,
            +0.2364553968f, -0.0354267569f, +0.9081222085f, -0.0966304021f, +0.3298642297f, 0.0f, 0.0f, 0.0f,
            -0.1669437536f, +0.8511358836f, -0.0210572806f, +0.0560353560f, -0.4940790631f, 0.0f, 0.0f, 0.0f,
            -0.5387050998f, +0.3854407235f, -0.2145881143f, -0.6755891483f, -0.2424118561f, 0.0f, 0.0f, 0.0f,
            -0.2659733911f, +0.3227547739f, -0.7753782516f, +0.3026878303f, +0.3636703940f, 0.0f, 0.0f, 0.0f,
            -0.5991521291f, -0.1048415611f, -0.7630327309f, +0.0510920987f, -0.2125926206f, 0.0f, 0.0f, 0.0f,
            +0.0199227415f, +0.2621236373f, -0.5210354975f, +0.5687283797f, -0.5796242952f, 0.0f, 0.0f, 0.0f,
            -0.3760635139f, -0.3518950304f, +0.5265468095f, +0.5567847035f, -0.3840382436f, 0.0f, 0.0f, 0.0f,
            +0.4146015293f, +0.4748863909f, +0.6466748337f, -0.3316148401f, -0.2728218190f, 0.0f, 0.0f, 0.0f,
            +0.0810230502f, -0.2559380867f, -0.1517698042f, +0.6108262601f, -0.7292380734f, 0.0f, 0.0f, 0.0f,
            -0.1016181919f, +0.5055248971f, -0.5282233256f, -0.6399481990f, +0.2134589945f, 0.0f, 0.0f, 0.0f,
            +0.1669886837f, +0.1997980982f, +0.1875214577f, -0.3224965425f, +0.8905207367f, 0.0f, 0.0f, 0.0f,
            +0.3098729021f, -0.5704474803f, +0.2651382288f, -0.7104915405f, +0.0589232311f, 0.0f, 0.0f, 0.0f,
            +0.7846360140f, +0.1040084479f, -0.1952084120f, +0.4366358103f, +0.3804883879f, 0.0f, 0.0f, 0.0f,
            +0.4960350002f, -0.2774056126f, -0.0746351707f, -0.7530668270f, -0.3229788693f, 0.0f, 0.0f, 0.0f,
            -0.0027208883f, -0.1040175758f, +0.2693323631f, -0.6234912066f, -0.7265615832f, 0.0f, 0.0f, 0.0f,
            -0.3184277537f, -0.4992514448f, -0.7579226334f, +0.1629259610f, +0.2199094667f, 0.0f, 0.0f, 0.0f,
            +0.7364268321f, +0.1568550346f, -0.2069638005f, -0.6155933147f, +0.1062208795f, 0.0f, 0.0f, 0.0f,
            +0.4423719461f, -0.4858350333f, +0.0640319867f, -0.1253802074f, +0.7405748375f, 0.0f, 0.0f, 0.0f,
            -0.0135345216f, -0.1200560585f, -0.6939166455f, -0.4294035455f, -0.5652394570f, 0.0f, 0.0f, 0.0f,
            -0.0824129794f, -0.1341388287f, -0.0421122228f, +0.2457282465f, -0.9555412419f, 0.0f, 0.0f, 0.0f,
            +0.0110312176f, -0.2566742059f, -0.2913437739f, +0.7619276089f, +0.5182487708f, 0.0f, 0.0f, 0.0f,
            +0.6581577775f, +0.2527752789f, +0.0399505088f, -0.3391680436f, +0.6215319729f, 0.0f, 0.0f, 0.0f,
            -0.3310433422f, -0.0758738580f, -0.5479486552f, -0.2514805152f, -0.7219163975f, 0.0f, 0.0f, 0.0f,
            +0.3666952779f, +0.3244098848f, -0.1442918413f, -0.6128804810f, +0.6031999506f, 0.0f, 0.0f, 0.0f,
            -0.3186461637f, -0.0830825713f, +0.0812808765f, +0.2169079666f, -0.9153721985f, 0.0f, 0.0f, 0.0f,
            +0.1016181919f, -0.5055248971f, +0.5282233256f, +0.6399481990f, -0.2134589945f, 0.0f, 0.0f, 0.0f,
            -0.3739018407f, +0.0146680802f, +0.1849091719f, -0.8933930292f, -0.1662520813f, 0.0f, 0.0f, 0.0f,
            +0.3310433422f, +0.0758738580f, +0.5479486552f, +0.2514805152f, +0.7219163975f, 0.0f, 0.0f, 0.0f,
            -0.1366170084f, -0.1060270097f, -0.8171374427f, +0.5443545961f, +0.0778366213f, 0.0f, 0.0f, 0.0f,
            -0.4662361046f, -0.0671357592f, -0.4199531625f, +0.7103056564f, -0.3118042663f, 0.0f, 0.0f, 0.0f,
            +0.5251151197f, -0.2737767426f, -0.3066796017f, +0.1047373479f, +0.7377520696f, 0.0f, 0.0f, 0.0f,
            -0.0494481376f, +0.5711262647f, +0.6070297254f, -0.4720750164f, -0.2828953214f, 0.0f, 0.0f, 0.0f,
            -0.1733696301f, +0.3889110020f, +0.2874650667f, -0.0563998095f, +0.8560806626f, 0.0f, 0.0f, 0.0f,
            +0.1366170084f, +0.1060270097f, +0.8171374427f, -0.5443545961f, -0.0778366213f, 0.0f, 0.0f, 0.0f,
            +0.8542368401f, +0.2712174555f, -0.2046590684f, -0.3340540935f, -0.2079496123f, 0.0f, 0.0f, 0.0f,
            +0.5947988211f, -0.6805773103f, -0.1936968685f, -0.2434525075f, +0.2936686685f, 0.0f, 0.0f, 0.0f,
            +0.4806347313f, -0.1390663366f, +0.8235132652f, -0.1853716195f, +0.1926501333f, 0.0f, 0.0f, 0.0f,
            +0.2468495306f, -0.0295790314f, +0.3902117978f, -0.6053260008f, +0.6476925009f, 0.0f, 0.0f, 0.0f,
            -0.0418311302f, -0.4405209406f, -0.4111821686f, -0.4440455815f, +0.6617735286f, 0.0f, 0.0f, 0.0f,
            +0.5814030674f, +0.6682078433f, +0.1405981744f, +0.3089247578f, +0.3166486992f, 0.0f, 0.0f, 0.0f,
            +0.5017986533f, +0.4516618892f, +0.2871519885f, +0.5008343077f, -0.4592476251f, 0.0f, 0.0f, 0.0f,
            -0.6542839468f, +0.1575186273f, -0.5489770163f, -0.3756894662f, -0.3233914957f, 0.0f, 0.0f, 0.0f,
            -0.6243140980f, -0.2510565670f, +0.2327672116f, +0.6620421891f, +0.2339274938f, 0.0f, 0.0f, 0.0f,
            +0.6936710105f, -0.5914497903f, +0.2536358587f, +0.0120015638f, -0.3233148441f, 0.0f, 0.0f, 0.0f,
            -0.3411215455f, +0.1327195907f, +0.4364951307f, -0.8089827248f, +0.1450536221f, 0.0f, 0.0f, 0.0f,
            +0.6343639111f, +0.7014484613f, +0.1339618421f, +0.0876272832f, +0.2827157026f, 0.0f, 0.0f, 0.0f,
            +0.6542839468f, -0.1575186273f, +0.5489770163f, +0.3756894662f, +0.3233914957f, 0.0f, 0.0f, 0.0f,
            -0.3552524760f, -0.4929038884f, +0.4464062884f, -0.4344889361f, +0.4927293630f, 0.0f, 0.0f, 0.0f,
            -0.1454318267f, +0.8589946472f, -0.1994096561f, +0.0748170601f, -0.4422849495f, 0.0f, 0.0f, 0.0f,
            +0.5850540573f, +0.3118514215f, -0.1843621110f, -0.6087015092f, +0.3949095159f, 0.0f, 0.0f, 0.0f,
            -0.3410266288f, -0.4735573790f, +0.1135481796f, +0.3647196339f, -0.7166105266f, 0.0f, 0.0f, 0.0f,
            -0.7846360140f, -0.1040084479f, +0.1952084120f, -0.4366358103f, -0.3804883879f, 0.0f, 0.0f, 0.0f,
            -0.7676538380f, +0.0741530582f, +0.3421862228f, +0.5274981454f, -0.0993136674f, 0.0f, 0.0f, 0.0f,
            -0.6581577775f, -0.2527752789f, -0.0399505088f, +0.3391680436f, -0.6215319729f, 0.0f, 0.0f, 0.0f,
            +0.3183884533f, -0.5387109733f, -0.3577739192f, -0.3699112519f, -0.5861593371f, 0.0f, 0.0f, 0.0f,
            -0.7364268321f, -0.1568550346f, +0.2069638005f, +0.6155933147f, -0.1062208795f, 0.0f, 0.0f, 0.0f,
            +0.0414044431f, -0.5892146057f, +0.0188300210f, +0.7299152917f, -0.3434835045f, 0.0f, 0.0f, 0.0f,
            +0.5679042849f, +0.2662144553f, -0.3267935036f, +0.3128015879f, +0.6340155831f, 0.0f, 0.0f, 0.0f,
            +0.2659733911f, -0.3227547739f, +0.7753782516f, -0.3026878303f, -0.3636703940f, 0.0f, 0.0f, 0.0f,
            -0.5370279383f, +0.2085470664f, -0.5306943632f, -0.1094139593f, +0.6119650259f, 0.0f, 0.0f, 0.0f,
            +0.4289785384f, -0.1501554146f, +0.6463958554f, +0.0831576367f, -0.6071968137f, 0.0f, 0.0f, 0.0f,
            -0.5693701067f, +0.4512427473f, +0.6318342144f, +0.0898531201f, +0.2547736385f, 0.0f, 0.0f, 0.0f,
            +0.2449157836f, +0.0996707878f, -0.1175195402f, +0.6797789757f, +0.6739226178f, 0.0f, 0.0f, 0.0f,
            +0.2733435724f, -0.3535085518f, -0.2919628458f, -0.6344494035f, +0.5590587145f, 0.0f, 0.0f, 0.0f,
            -0.8568123132f, +0.1320160436f, -0.4076860536f, -0.1024245958f, -0.2678538929f, 0.0f, 0.0f, 0.0f,
            +0.6098045696f, +0.6041828606f, -0.1039300939f, +0.1933295546f, -0.4635986161f, 0.0f, 0.0f, 0.0f,
            -0.8123843032f, +0.2998053331f, +0.1066994074f, +0.4269862876f, +0.2375846226f, 0.0f, 0.0f, 0.0f,
            -0.2449157836f, -0.0996707878f, +0.1175195402f, -0.6797789757f, -0.6739226178f, 0.0f, 0.0f, 0.0f,
            +0.1754584797f, +0.0620723767f, +0.3035594339f, -0.2246207245f, +0.9070603851f, 0.0f, 0.0f, 0.0f,
            -0.6896313153f, -0.1556943945f, +0.0093845359f, +0.6312371893f, +0.3187780510f, 0.0f, 0.0f, 0.0f,
            +0.0027208883f, +0.1040175758f, -0.2693323631f, +0.6234912066f, +0.7265615832f, 0.0f, 0.0f, 0.0f,
            -0.5104164139f, +0.1581040607f, -0.1425483366f, +0.2160766265f, +0.8046546176f, 0.0f, 0.0f, 0.0f,
            -0.4560347585f, +0.3934753406f, +0.6923535392f, -0.2127570363f, -0.3355450427f, 0.0f, 0.0f, 0.0f,
            +0.7317531488f, +0.5457445300f, +0.3402074959f, -0.0473371427f, -0.2207222052f, 0.0f, 0.0f, 0.0f,
            +0.3279518542f, +0.2950803178f, -0.4969318956f, +0.7226519086f, +0.1902842545f, 0.0f, 0.0f, 0.0f,
            +0.2776991719f, -0.3138801886f, -0.7100216859f, +0.5650102392f, -0.0315441308f, 0.0f, 0.0f, 0.0f,
            -0.4146015293f, -0.4748863909f, -0.6466748337f, +0.3316148401f, +0.2728218190f, 0.0f, 0.0f, 0.0f,
            -0.3666952779f, -0.3244098848f, +0.1442918413f, +0.6128804810f, -0.6031999506f, 0.0f, 0.0f, 0.0f,
            +0.6226475025f, +0.1728884784f, -0.1386060079f, +0.1420063907f, -0.7369139850f, 0.0f, 0.0f, 0.0f,
            -0.0858027864f, -0.1957805361f, +0.6813968134f, -0.4148907519f, +0.5638013038f, 0.0f, 0.0f, 0.0f,
            +0.8568123132f, -0.1320160436f, +0.4076860536f, +0.1024245958f, +0.2678538929f, 0.0f, 0.0f, 0.0f,
            +0.9374979192f, -0.0003634480f, -0.1956638355f, +0.0529505789f, +0.2828593628f, 0.0f, 0.0f, 0.0f,
            +0.5693701067f, -0.4512427473f, -0.6318342144f, -0.0898531201f, -0.2547736385f, 0.0f, 0.0f, 0.0f,
            -0.3526828880f, +0.1545338839f, +0.6460653067f, -0.0485706383f, -0.6572477248f, 0.0f, 0.0f, 0.0f,
            -0.1669886837f, -0.1997980982f, -0.1875214577f, +0.3224965425f, -0.8905207367f, 0.0f, 0.0f, 0.0f,
            -0.3336001748f, -0.3887744822f, -0.2137748617f, +0.8236107157f, +0.1163229250f, 0.0f, 0.0f, 0.0f,
            -0.0173829966f, -0.0229197095f, -0.6848040140f, +0.6141215810f, -0.3912424625f, 0.0f, 0.0f, 0.0f,
            -0.2268239952f, -0.3504695788f, -0.5260490489f, -0.1214912078f, -0.7309132877f, 0.0f, 0.0f, 0.0f,
            +0.2537673658f, -0.2813458145f, -0.3740187016f, -0.6483153974f, -0.5442828429f, 0.0f, 0.0f, 0.0f,
            +0.4560347585f, -0.3934753406f, -0.6923535392f, +0.2127570363f, +0.3355450427f, 0.0f, 0.0f, 0.0f,
            +0.6900520820f, +0.4979398564f, -0.3459070338f, -0.3951490255f, +0.0094654680f, 0.0f, 0.0f, 0.0f,
            -0.0266474585f, -0.9099592284f, +0.1452471139f, -0.3196855139f, +0.2190172682f, 0.0f, 0.0f, 0.0f,
            +0.3783872335f, -0.5282608896f, +0.3713871691f, +0.4541142750f, +0.4833376976f, 0.0f, 0.0f, 0.0f,
            +0.1034289093f, +0.1695835602f, +0.9023321561f, -0.2069184740f, -0.3217534803f, 0.0f, 0.0f, 0.0f,
            -0.6670350269f, -0.0831250542f, -0.5291879393f, +0.2559099233f, -0.4501385723f, 0.0f, 0.0f, 0.0f,
            -0.6598751932f, +0.2384411457f, +0.5963873719f, -0.3195006317f, +0.2234994370f, 0.0f, 0.0f, 0.0f,
            +0.6403930783f, +0.0600652787f, -0.5519209931f, +0.4497233127f, -0.2818173647f, 0.0f, 0.0f, 0.0f,
            -0.2943711245f, +0.4820782130f, +0.4748478057f, +0.4454077004f, -0.5070283813f, 0.0f, 0.0f, 0.0f,
            +0.0824129794f, +0.1341388287f, +0.0421122228f, -0.2457282465f, +0.9555412419f, 0.0f, 0.0f, 0.0f,
            +0.5104164139f, -0.1581040607f, +0.1425483366f, -0.2160766265f, -0.8046546176f, 0.0f, 0.0f, 0.0f,
            -0.0810230502f, +0.2559380867f, +0.1517698042f, -0.6108262601f, +0.7292380734f, 0.0f, 0.0f, 0.0f,
            -0.2180198852f, -0.4461829567f, -0.2598732896f, -0.3761713610f, -0.7378001622f, 0.0f, 0.0f, 0.0f,
            -0.3972755980f, -0.7055927183f, +0.0821529013f, +0.4314577979f, +0.3891093479f, 0.0f, 0.0f, 0.0f,
            +0.3972755980f, +0.7055927183f, -0.0821529013f, -0.4314577979f, -0.3891093479f, 0.0f, 0.0f, 0.0f,
            +0.5631546358f, +0.3597560646f, -0.4967548000f, +0.5246818757f, -0.1771327981f, 0.0f, 0.0f, 0.0f,
            +0.5499490734f, -0.4371269155f, -0.5828941150f, -0.3039922557f, +0.2725788611f, 0.0f, 0.0f, 0.0f,
            -0.5293786340f, -0.4909090151f, -0.5391190019f, +0.0656801703f, +0.4287230082f, 0.0f, 0.0f, 0.0f,
            -0.0003869206f, +0.5418766004f, +0.7867388149f, -0.2956416699f, -0.0027641210f, 0.0f, 0.0f, 0.0f,
            -0.0700079328f, +0.8802164250f, +0.0473880978f, -0.0802563114f, -0.4600339414f, 0.0f, 0.0f, 0.0f,
            +0.1979725611f, -0.2185413808f, -0.7239656148f, +0.3953069660f, +0.4823408765f, 0.0f, 0.0f, 0.0f,
            -0.2776991719f, +0.3138801886f, +0.7100216859f, -0.5650102392f, +0.0315441308f, 0.0f, 0.0f, 0.0f,
            +0.0494481376f, -0.5711262647f, -0.6070297254f, +0.4720750164f, +0.2828953214f, 0.0f, 0.0f, 0.0f,
            -0.4115741800f, +0.6857744205f, -0.3223597835f, -0.2621709591f, +0.4332097606f, 0.0f, 0.0f, 0.0f,
            -0.0295827750f, -0.7131176337f, -0.1760862600f, -0.0094558934f, -0.6778586247f, 0.0f, 0.0f, 0.0f,
            -0.6155992250f, -0.0004820688f, +0.0717591671f, -0.6981067683f, -0.3585176756f, 0.0f, 0.0f, 0.0f,
            -0.4722350727f, +0.2110465218f, -0.2864663987f, +0.0013033655f, +0.8064668036f, 0.0f, 0.0f, 0.0f,
            +0.4251901341f, -0.1298843219f, -0.2982846722f, -0.1283226009f, +0.8348071498f, 0.0f, 0.0f, 0.0f,
            -0.5814030674f, -0.6682078433f, -0.1405981744f, -0.3089247578f, -0.3166486992f, 0.0f, 0.0f, 0.0f,
            +0.3411215455f, -0.1327195907f, -0.4364951307f, +0.8089827248f, -0.1450536221f, 0.0f, 0.0f, 0.0f,
            -0.0243107363f, +0.4068117636f, +0.8310269678f, -0.3457269075f, +0.1542084994f, 0.0f, 0.0f, 0.0f,
            +0.5875639603f, +0.1105468365f, +0.0690731524f, -0.6307173914f, +0.4898698411f, 0.0f, 0.0f, 0.0f,
            +0.4774024524f, +0.2984390650f, +0.0123754095f, -0.6080909387f, +0.5595473908f, 0.0f, 0.0f, 0.0f,
            -0.5850540573f, -0.3118514215f, +0.1843621110f, +0.6087015092f, -0.3949095159f, 0.0f, 0.0f, 0.0f,
            +0.3291566012f, +0.3400853363f, +0.7291248631f, -0.4202379008f, +0.2603361993f, 0.0f, 0.0f, 0.0f,
            -0.5679042849f, -0.2662144553f, +0.3267935036f, -0.3128015879f, -0.6340155831f, 0.0f, 0.0f, 0.0f,
            +0.6215493103f, +0.0844785631f, -0.3922776381f, -0.5507219936f, -0.3864755720f, 0.0f, 0.0f, 0.0f,
            +0.0418311302f, +0.4405209406f, +0.4111821686f, +0.4440455815f, -0.6617735286f, 0.0f, 0.0f, 0.0f,
            -0.1754584797f, -0.0620723767f, -0.3035594339f, +0.2246207245f, -0.9070603851f, 0.0f, 0.0f, 0.0f,
            -0.1034289093f, -0.1695835602f, -0.9023321561f, +0.2069184740f, +0.3217534803f, 0.0f, 0.0f, 0.0f,
            -0.2537673658f, +0.2813458145f, +0.3740187016f, +0.6483153974f, +0.5442828429f, 0.0f, 0.0f, 0.0f,
            +0.6544506463f, -0.0759438002f, +0.2849515054f, +0.3407187112f, +0.6071575497f, 0.0f, 0.0f, 0.0f,
            +0.3336001748f, +0.3887744822f, +0.2137748617f, -0.8236107157f, -0.1163229250f, 0.0f, 0.0f, 0.0f,
            +0.5075123667f, -0.1077255418f, +0.5373598588f, +0.1899298775f, +0.6371792754f, 0.0f, 0.0f, 0.0f,
            -0.4960350002f, +0.2774056126f, +0.0746351707f, +0.7530668270f, +0.3229788693f, 0.0f, 0.0f, 0.0f,
            +0.3007193437f, -0.5282908180f, +0.0714975290f, +0.4075860072f, +0.6776713348f, 0.0f, 0.0f, 0.0f,
            +0.0395594178f, +0.2198250293f, +0.3956837940f, -0.3965115381f, -0.7976997833f, 0.0f, 0.0f, 0.0f,
            +0.0135345216f, +0.1200560585f, +0.6939166455f, +0.4294035455f, +0.5652394570f, 0.0f, 0.0f, 0.0f,
            -0.7573186412f, -0.3071959067f, -0.5062785000f, -0.0721981344f, +0.2656476245f, 0.0f, 0.0f, 0.0f,
            -0.3098729021f, +0.5704474803f, -0.2651382288f, +0.7104915405f, -0.0589232311f, 0.0f, 0.0f, 0.0f,
            -0.5075123667f, +0.1077255418f, -0.5373598588f, -0.1899298775f, -0.6371792754f, 0.0f, 0.0f, 0.0f,
            -0.4806347313f, +0.1390663366f, -0.8235132652f, +0.1853716195f, -0.1926501333f, 0.0f, 0.0f, 0.0f,
            +0.1159050432f, -0.8426776301f, +0.1932970642f, +0.4654900474f, -0.1497187145f, 0.0f, 0.0f, 0.0f,
            -0.0356243820f, -0.0899453022f, -0.8119656333f, -0.5756195218f, +0.0038369850f, 0.0f, 0.0f, 0.0f,
            +0.2268239952f, +0.3504695788f, +0.5260490489f, +0.1214912078f, +0.7309132877f, 0.0f, 0.0f, 0.0f,
            -0.0972670569f, -0.1728278133f, -0.8985885333f, -0.2066553374f, -0.3324182394f, 0.0f, 0.0f, 0.0f,
            +0.1454318267f, -0.8589946472f, +0.1994096561f, -0.0748170601f, +0.4422849495f, 0.0f, 0.0f, 0.0f,
            -0.0199227415f, -0.2621236373f, +0.5210354975f, -0.5687283797f, +0.5796242952f, 0.0f, 0.0f, 0.0f,
            -0.3291566012f, -0.3400853363f, -0.7291248631f, +0.4202379008f, -0.2603361993f, 0.0f, 0.0f, 0.0f,
            -0.0395594178f, -0.2198250293f, -0.3956837940f, +0.3965115381f, +0.7976997833f, 0.0f, 0.0f, 0.0f,
            -0.8542368401f, -0.2712174555f, +0.2046590684f, +0.3340540935f, +0.2079496123f, 0.0f, 0.0f, 0.0f,
            -0.6900520820f, -0.4979398564f, +0.3459070338f, +0.3951490255f, -0.0094654680f, 0.0f, 0.0f, 0.0f,
            +0.5293786340f, +0.4909090151f, +0.5391190019f, -0.0656801703f, -0.4287230082f, 0.0f, 0.0f, 0.0f,
            -0.0083948129f, -0.8565270917f, +0.0040509600f, -0.4588772677f, +0.2360214212f, 0.0f, 0.0f, 0.0f,
            +0.0858027864f, +0.1957805361f, -0.6813968134f, +0.4148907519f, -0.5638013038f, 0.0f, 0.0f, 0.0f,
            +0.2943711245f, -0.4820782130f, -0.4748478057f, -0.4454077004f, +0.5070283813f, 0.0f, 0.0f, 0.0f,
            +0.2967691780f, +0.3679242707f, +0.0543021981f, -0.5199193389f, -0.7094328287f, 0.0f, 0.0f, 0.0f,
            -0.1221618192f, -0.2931057828f, +0.1974980792f, +0.8226626364f, +0.4282361328f, 0.0f, 0.0f, 0.0f,
            +0.1321620314f, +0.4456852942f, +0.4164761004f, +0.5466460887f, -0.5582325031f, 0.0f, 0.0f, 0.0f,
            +0.4511807817f, +0.4866109050f, +0.6810530650f, -0.2472500102f, +0.1862253595f, 0.0f, 0.0f, 0.0f,
            -0.2468495306f, +0.0295790314f, -0.3902117978f, +0.6053260008f, -0.6476925009f, 0.0f, 0.0f, 0.0f,
            +0.0173829966f, +0.0229197095f, +0.6848040140f, -0.6141215810f, +0.3912424625f, 0.0f, 0.0f, 0.0f,
            -0.6343639111f, -0.7014484613f, -0.1339618421f, -0.0876272832f, -0.2827157026f, 0.0f, 0.0f, 0.0f,
            -0.5499490734f, +0.4371269155f, +0.5828941150f, +0.3039922557f, -0.2725788611f, 0.0f, 0.0f, 0.0f,
            +0.6598751932f, -0.2384411457f, -0.5963873719f, +0.3195006317f, -0.2234994370f, 0.0f, 0.0f, 0.0f,
            +0.3064628296f, +0.2762642492f, +0.3781335091f, -0.8047256834f, -0.1979652053f, 0.0f, 0.0f, 0.0f,
            +0.3228950515f, +0.5585644808f, -0.0951667811f, +0.6472052826f, +0.3947316969f, 0.0f, 0.0f, 0.0f,
            -0.2331392129f, +0.3738057089f, -0.0177873006f, +0.4611190867f, -0.7700442839f, 0.0f, 0.0f, 0.0f,
            +0.7573186412f, +0.3071959067f, +0.5062785000f, +0.0721981344f, -0.2656476245f, 0.0f, 0.0f, 0.0f,
            +0.0295827750f, +0.7131176337f, +0.1760862600f, +0.0094558934f, +0.6778586247f, 0.0f, 0.0f, 0.0f,
            +0.1669437536f, -0.8511358836f, +0.0210572806f, -0.0560353560f, +0.4940790631f, 0.0f, 0.0f, 0.0f,
            +0.0972670569f, +0.1728278133f, +0.8985885333f, +0.2066553374f, +0.3324182394f, 0.0f, 0.0f, 0.0f,
            +0.4796140497f, +0.7530615014f, +0.3925098940f, -0.1549720436f, +0.1574432827f, 0.0f, 0.0f, 0.0f,
            +0.5741187365f, -0.1235841833f, +0.3191536767f, +0.6695194417f, +0.3240359145f, 0.0f, 0.0f, 0.0f,
            +0.1221618192f, +0.2931057828f, -0.1974980792f, -0.8226626364f, -0.4282361328f, 0.0f, 0.0f, 0.0f,
            +0.3552524760f, +0.4929038884f, -0.4464062884f, +0.4344889361f, -0.4927293630f, 0.0f, 0.0f, 0.0f,
            +0.9699449962f, +0.0972661391f, +0.1937354525f, -0.0313763560f, +0.1059627347f, 0.0f, 0.0f, 0.0f,
            +0.6155992250f, +0.0004820688f, -0.0717591671f, +0.6981067683f, +0.3585176756f, 0.0f, 0.0f, 0.0f,
    };

    /**
     * Randomly selected points on the 6D unit hypersphere (mathematically, the '5-sphere').
     * Each point is stored in 6 floats, and there are 2 floats of padding after each point
     * (to allow easier access to points using bitwise operations). The distance from each
     * point to the origin should be 1.0, subject to rounding error; this is implied by the
     * point being on the unit hypersphere, but earlier gradient vectors used a different
     * distance to the origin.
     * <br>
     * These points were randomly selected, but 10 million sets of candidate points were tried
     * (all also random) and this set had the greatest minimum distance between any two points.
     * For every point on the sphere present here, the polar opposite of that point is also
     * present, ensuring there is no bias along any particular axis. The points were also
     * shuffled, in case they are retrieved by a less-random method (such as iteration).
     * <br>
     * This particular set of gradient vectors is almost certainly not optimal, but finding a
     * better one is very, very slow right now.
     */
    public static final float[] GRADIENTS_6D = {
            +0.2361096740f, +0.1233933419f, -0.6382903457f, -0.7079907060f, -0.0242310166f, +0.1406176984f, 0.0f, 0.0f,
            -0.3627503514f, -0.2439851165f, +0.4784145355f, -0.3871673346f, +0.6099373698f, -0.2409997582f, 0.0f, 0.0f,
            -0.4950783253f, +0.4415829182f, +0.0724574476f, +0.5293629169f, +0.2511994541f, -0.4597013891f, 0.0f, 0.0f,
            -0.2361096740f, -0.1233933419f, +0.6382903457f, +0.7079907060f, +0.0242310166f, -0.1406176984f, 0.0f, 0.0f,
            -0.3466404676f, -0.2081464678f, +0.2455648631f, -0.6579040289f, -0.3333091438f, +0.4819549322f, 0.0f, 0.0f,
            -0.0422446728f, +0.7614569068f, +0.2642603517f, -0.0495838635f, -0.2838862836f, +0.5152812600f, 0.0f, 0.0f,
            -0.9274544716f, -0.2464881837f, -0.1358041912f, -0.0156343132f, +0.2332948744f, +0.0771873295f, 0.0f, 0.0f,
            +0.5589281321f, +0.5154735446f, +0.2994868755f, -0.2944864929f, -0.1651475579f, +0.4671169519f, 0.0f, 0.0f,
            +0.4638033509f, +0.0876800269f, -0.1701443195f, +0.3358871043f, +0.7762178779f, -0.1814254969f, 0.0f, 0.0f,
            +0.0392329693f, -0.8251270056f, -0.2681472301f, +0.3655394912f, -0.3323317766f, +0.0407329947f, 0.0f, 0.0f,
            +0.5949144363f, +0.1796900481f, +0.3663012981f, +0.6571399570f, +0.0730532557f, +0.2060140520f, 0.0f, 0.0f,
            +0.3132215142f, +0.2635257244f, +0.2291566879f, -0.0430522338f, +0.7744936943f, +0.4221841693f, 0.0f, 0.0f,
            -0.2446261644f, -0.2116619051f, +0.4678121209f, +0.1871315241f, -0.8004072905f, +0.0289557725f, 0.0f, 0.0f,
            +0.1831395626f, -0.0101975575f, -0.1991053820f, +0.3416745067f, +0.7237075567f, +0.5349943638f, 0.0f, 0.0f,
            +0.5933209658f, -0.0324823931f, -0.2682549655f, -0.7198925018f, -0.2308724374f, -0.0583732501f, 0.0f, 0.0f,
            -0.3588180542f, -0.5312285423f, +0.2981200218f, +0.6241783500f, -0.1631361246f, +0.2897554040f, 0.0f, 0.0f,
            -0.0539913177f, +0.0640245378f, +0.4019110799f, +0.5301620960f, -0.6863521934f, +0.2816056013f, 0.0f, 0.0f,
            +0.1709820032f, +0.8575593829f, -0.4348439872f, +0.1401913762f, +0.0862796009f, +0.1384580582f, 0.0f, 0.0f,
            +0.4950783253f, -0.4415829182f, -0.0724574476f, -0.5293629169f, -0.2511994541f, +0.4597013891f, 0.0f, 0.0f,
            +0.3677458763f, -0.4902043641f, +0.0169921108f, -0.0033596605f, +0.1875487566f, -0.7674553394f, 0.0f, 0.0f,
            -0.4825593829f, +0.1224169955f, +0.2779754698f, +0.4667891264f, -0.6621223688f, +0.1363160312f, 0.0f, 0.0f,
            -0.0602706075f, +0.5346590281f, -0.1774500459f, -0.7179715037f, -0.4037035108f, +0.0236485079f, 0.0f, 0.0f,
            +0.0414444208f, +0.1226754040f, +0.7657818198f, +0.2057385147f, -0.1131953746f, -0.5845252872f, 0.0f, 0.0f,
            -0.5949144363f, -0.1796900481f, -0.3663012981f, -0.6571399570f, -0.0730532557f, -0.2060140520f, 0.0f, 0.0f,
            -0.5126454830f, -0.6959911585f, +0.1778003275f, -0.4295687377f, +0.1168115735f, -0.1516701877f, 0.0f, 0.0f,
            -0.5933209658f, +0.0324823931f, +0.2682549655f, +0.7198925018f, +0.2308724374f, +0.0583732501f, 0.0f, 0.0f,
            +0.9274544716f, +0.2464881837f, +0.1358041912f, +0.0156343132f, -0.2332948744f, -0.0771873295f, 0.0f, 0.0f,
            +0.5409727097f, -0.3918426335f, -0.3426568210f, +0.1661424041f, -0.4204911888f, +0.4816412926f, 0.0f, 0.0f,
            -0.4719250202f, +0.7658368945f, +0.3563901186f, +0.0386263728f, -0.1846246868f, +0.1678942144f, 0.0f, 0.0f,
            -0.1161904335f, +0.4921432137f, +0.6903575659f, -0.4057750702f, +0.0998100638f, +0.3050991893f, 0.0f, 0.0f,
            -0.5234503746f, +0.0566369891f, +0.1219781265f, -0.6776737571f, +0.4131974280f, -0.2791761458f, 0.0f, 0.0f,
            -0.3624292612f, -0.7189853787f, -0.1423764974f, -0.2605557442f, +0.4935359955f, -0.1413051486f, 0.0f, 0.0f,
            -0.1889401674f, -0.2686024904f, +0.0862365812f, -0.6600202322f, +0.5365858078f, +0.4014549553f, 0.0f, 0.0f,
            +0.7287732363f, -0.4028936923f, +0.5133012533f, -0.0260427296f, -0.0955726206f, -0.1824158877f, 0.0f, 0.0f,
            -0.2995333672f, -0.1206774935f, -0.5473484993f, -0.4573812485f, +0.6214010715f, +0.0280943736f, 0.0f, 0.0f,
            +0.1069881916f, -0.5648230910f, +0.1126850992f, +0.0417897739f, -0.5995839834f, +0.5436751842f, 0.0f, 0.0f,
            -0.1772633791f, +0.4716815948f, -0.3651756644f, -0.6627026200f, -0.4137061238f, +0.0491255745f, 0.0f, 0.0f,
            -0.3105410337f, -0.5140929222f, +0.1801727712f, +0.3434116244f, +0.4031203389f, -0.5712903738f, 0.0f, 0.0f,
            +0.3860855103f, +0.2530701756f, +0.5932257771f, -0.2437774539f, -0.3796317577f, -0.4810706377f, 0.0f, 0.0f,
            +0.5484406948f, -0.4846430421f, -0.2744789720f, -0.3737654388f, -0.3866026998f, +0.3159637153f, 0.0f, 0.0f,
            +0.2995333672f, +0.1206774935f, +0.5473484993f, +0.4573812485f, -0.6214010715f, -0.0280943736f, 0.0f, 0.0f,
            -0.0354040861f, +0.6180121303f, -0.5046198368f, -0.1901258677f, +0.1310741454f, -0.5557319522f, 0.0f, 0.0f,
            -0.2715435028f, -0.4970937967f, +0.7158309221f, +0.1022432148f, -0.3923885226f, -0.0482235029f, 0.0f, 0.0f,
            -0.3231120110f, -0.2775039077f, -0.8490881324f, +0.2941025496f, +0.1055530682f, +0.0014762878f, 0.0f, 0.0f,
            +0.4734349251f, -0.2138127685f, -0.2288961262f, -0.0197854638f, +0.0250411443f, -0.8226370215f, 0.0f, 0.0f,
            -0.1432034373f, +0.2634466588f, +0.7834610939f, -0.4089841545f, +0.3103615344f, -0.1807889789f, 0.0f, 0.0f,
            -0.2043955326f, -0.1053923890f, +0.2659108937f, +0.6500580311f, -0.4346653819f, -0.5146814585f, 0.0f, 0.0f,
            -0.5510939360f, -0.1074754298f, +0.6826416254f, +0.1793766916f, -0.4288653135f, -0.0514160022f, 0.0f, 0.0f,
            +0.0388053656f, +0.5901203752f, +0.4632027149f, +0.6381313801f, +0.0113432556f, +0.1683882773f, 0.0f, 0.0f,
            -0.4638033509f, -0.0876800269f, +0.1701443195f, -0.3358871043f, -0.7762178779f, +0.1814254969f, 0.0f, 0.0f,
            -0.8904361725f, +0.2107102573f, -0.2656660378f, -0.0167762488f, -0.0947417915f, +0.2879030704f, 0.0f, 0.0f,
            +0.4932592511f, -0.2775544822f, -0.3170401752f, -0.5724651814f, -0.4923670888f, -0.0948817953f, 0.0f, 0.0f,
            +0.3134736419f, +0.6652622223f, +0.1646185219f, -0.1535007954f, -0.0616347678f, -0.6361600757f, 0.0f, 0.0f,
            +0.4992538095f, +0.5172593594f, +0.4479877949f, -0.2761235833f, +0.2453980297f, +0.3821397424f, 0.0f, 0.0f,
            -0.7402177453f, -0.4702273011f, +0.4595199227f, -0.1163212508f, +0.0493032336f, +0.0619992018f, 0.0f, 0.0f,
            -0.7374488115f, +0.4766946137f, +0.2159651220f, +0.1574023664f, -0.0695485398f, +0.3907400370f, 0.0f, 0.0f,
            +0.1470499039f, -0.4023058414f, +0.3263177872f, -0.0188799500f, -0.5032677650f, +0.6755800247f, 0.0f, 0.0f,
            +0.5689454079f, -0.2034867257f, +0.3256599605f, -0.1142677292f, -0.6573780775f, +0.2891997695f, 0.0f, 0.0f,
            +0.3466404676f, +0.2081464678f, -0.2455648631f, +0.6579040289f, +0.3333091438f, -0.4819549322f, 0.0f, 0.0f,
            -0.4972498417f, -0.2589396238f, +0.2862729728f, +0.6852655411f, +0.0640327111f, +0.3606265187f, 0.0f, 0.0f,
            +0.2357217669f, -0.1797172874f, -0.6605231166f, -0.2593510747f, +0.6376796961f, -0.0441335738f, 0.0f, 0.0f,
            -0.6424818039f, +0.0607392043f, -0.7081311941f, -0.2684156001f, +0.0481144935f, +0.0878378749f, 0.0f, 0.0f,
            +0.5958921313f, -0.4717123508f, -0.0752419680f, -0.4298895001f, -0.4804389477f, +0.0333465002f, 0.0f, 0.0f,
            -0.7117182016f, +0.5409841537f, -0.1857579052f, +0.3074691594f, +0.2385217398f, +0.1218909472f, 0.0f, 0.0f,
            +0.4877843857f, -0.2255152762f, -0.3093829453f, -0.7473223805f, +0.1044918522f, -0.2146674544f, 0.0f, 0.0f,
            +0.3914494514f, -0.5846970677f, -0.3664770424f, +0.1338133067f, +0.5571625233f, -0.2055606246f, 0.0f, 0.0f,
            +0.1889401674f, +0.2686024904f, -0.0862365812f, +0.6600202322f, -0.5365858078f, -0.4014549553f, 0.0f, 0.0f,
            +0.0354040861f, -0.6180121303f, +0.5046198368f, +0.1901258677f, -0.1310741454f, +0.5557319522f, 0.0f, 0.0f,
            +0.3590784669f, -0.4270640016f, +0.7617442012f, -0.2343474478f, +0.1221706048f, -0.1964193135f, 0.0f, 0.0f,
            +0.5404145718f, -0.2441326976f, +0.2600440085f, +0.6839744449f, -0.2383713871f, +0.2368254960f, 0.0f, 0.0f,
            -0.3657615185f, -0.3721697330f, -0.3731113076f, +0.0704583451f, -0.5061950684f, +0.5720996261f, 0.0f, 0.0f,
            +0.0271241665f, +0.3681741953f, +0.2618570924f, +0.5696759224f, +0.6852018833f, -0.0333189964f, 0.0f, 0.0f,
            -0.3134736419f, -0.6652622223f, -0.1646185219f, +0.1535007954f, +0.0616347678f, +0.6361600757f, 0.0f, 0.0f,
            -0.1387164593f, +0.3075677454f, +0.1653710902f, -0.4419374466f, +0.7947620153f, -0.1784853339f, 0.0f, 0.0f,
            -0.1470499039f, +0.4023058414f, -0.3263177872f, +0.0188799500f, +0.5032677650f, -0.6755800247f, 0.0f, 0.0f,
            -0.3558551073f, +0.3234240413f, +0.1513571739f, +0.3045706153f, -0.7846769094f, +0.1933220029f, 0.0f, 0.0f,
            -0.5484406948f, +0.4846430421f, +0.2744789720f, +0.3737654388f, +0.3866026998f, -0.3159637153f, 0.0f, 0.0f,
            -0.6869664192f, -0.0752433687f, +0.1640594602f, +0.3079922497f, -0.5473255515f, +0.3179238737f, 0.0f, 0.0f,
            -0.4284339547f, +0.7401829362f, -0.0111486688f, -0.3739085793f, -0.0887513459f, -0.3475124538f, 0.0f, 0.0f,
            +0.2031803131f, +0.6117979288f, -0.0339146703f, -0.7503009439f, +0.0490158200f, -0.1338536441f, 0.0f, 0.0f,
            -0.4271706939f, +0.3553755581f, -0.2822435498f, +0.3818562031f, +0.0151495337f, +0.6822965145f, 0.0f, 0.0f,
            -0.0220623016f, +0.9578628540f, -0.1544855237f, -0.0689527690f, -0.2270242125f, +0.0430330858f, 0.0f, 0.0f,
            -0.1893571615f, -0.7649993896f, -0.4938068092f, -0.1215827763f, +0.2958166599f, -0.1810640395f, 0.0f, 0.0f,
            -0.2357217669f, +0.1797172874f, +0.6605231166f, +0.2593510747f, -0.6376796961f, +0.0441335738f, 0.0f, 0.0f,
            -0.1747523546f, +0.0225779116f, +0.3918288350f, -0.2371454239f, -0.7704305649f, +0.4069653749f, 0.0f, 0.0f,
            -0.7756626606f, +0.3521449566f, -0.1992188394f, -0.4065485597f, -0.2126327157f, +0.1554318368f, 0.0f, 0.0f,
            -0.4932592511f, +0.2775544822f, +0.3170401752f, +0.5724651814f, +0.4923670888f, +0.0948817953f, 0.0f, 0.0f,
            -0.0729080439f, -0.4134978652f, +0.6914373636f, -0.3964928985f, +0.4105882943f, +0.1408144683f, 0.0f, 0.0f,
            -0.1385977864f, -0.3741669357f, +0.3895216286f, -0.6117170453f, -0.5562658310f, -0.0737079382f, 0.0f, 0.0f,
            +0.6368057728f, -0.5852853656f, +0.1691146046f, -0.0111941472f, -0.4602321982f, +0.1066793352f, 0.0f, 0.0f,
            -0.2336148024f, -0.5854524970f, -0.4901580215f, -0.1786131561f, -0.1114856005f, -0.5639878511f, 0.0f, 0.0f,
            -0.6368057728f, +0.5852853656f, -0.1691146046f, +0.0111941472f, +0.4602321982f, -0.1066793352f, 0.0f, 0.0f,
            -0.3540483713f, +0.3773740530f, +0.2607714236f, +0.1622319221f, -0.4917523861f, +0.6293624043f, 0.0f, 0.0f,
            -0.3914494514f, +0.5846970677f, +0.3664770424f, -0.1338133067f, -0.5571625233f, +0.2055606246f, 0.0f, 0.0f,
            +0.1385977864f, +0.3741669357f, -0.3895216286f, +0.6117170453f, +0.5562658310f, +0.0737079382f, 0.0f, 0.0f,
            +0.2715435028f, +0.4970937967f, -0.7158309221f, -0.1022432148f, +0.3923885226f, +0.0482235029f, 0.0f, 0.0f,
            +0.6908330917f, -0.1780716926f, -0.3039638698f, -0.4520987868f, +0.4351642132f, -0.0698899478f, 0.0f, 0.0f,
            +0.2142882943f, -0.4175734222f, -0.0801376030f, -0.8255137205f, +0.1757739186f, +0.2468228340f, 0.0f, 0.0f,
            +0.4972498417f, +0.2589396238f, -0.2862729728f, -0.6852655411f, -0.0640327111f, -0.3606265187f, 0.0f, 0.0f,
            -0.1125470996f, -0.5160606503f, +0.7015146017f, +0.2644234002f, -0.0722578913f, +0.3921106160f, 0.0f, 0.0f,
            +0.3105410337f, +0.5140929222f, -0.1801727712f, -0.3434116244f, -0.4031203389f, +0.5712903738f, 0.0f, 0.0f,
            -0.2875063419f, -0.5249719620f, -0.2788235247f, +0.7007287741f, +0.1739048660f, +0.2067318261f, 0.0f, 0.0f,
            -0.3083657026f, -0.4026551843f, -0.2894597054f, -0.2734915018f, +0.2776434124f, +0.7121157646f, 0.0f, 0.0f,
            -0.3860855103f, -0.2530701756f, -0.5932257771f, +0.2437774539f, +0.3796317577f, +0.4810706377f, 0.0f, 0.0f,
            -0.4877843857f, +0.2255152762f, +0.3093829453f, +0.7473223805f, -0.1044918522f, +0.2146674544f, 0.0f, 0.0f,
            +0.5274492502f, -0.6480730772f, -0.2022777200f, -0.3210950792f, +0.0445882082f, +0.3947041035f, 0.0f, 0.0f,
            -0.6264783144f, +0.0403335243f, +0.2235517502f, -0.6567904949f, +0.0000801347f, +0.3529140949f, 0.0f, 0.0f,
            +0.3601060510f, -0.0173940696f, -0.4522676468f, +0.0003153421f, -0.6793802381f, -0.4515717030f, 0.0f, 0.0f,
            -0.0653846264f, -0.1638934165f, -0.5282983780f, +0.1669099480f, -0.5811017156f, -0.5694084764f, 0.0f, 0.0f,
            +0.6344519854f, +0.1686923653f, +0.3569357693f, -0.4069772363f, -0.4713703692f, -0.2319264263f, 0.0f, 0.0f,
            +0.0653846264f, +0.1638934165f, +0.5282983780f, -0.1669099480f, +0.5811017156f, +0.5694084764f, 0.0f, 0.0f,
            -0.5274492502f, +0.6480730772f, +0.2022777200f, +0.3210950792f, -0.0445882082f, -0.3947041035f, 0.0f, 0.0f,
            +0.2336148024f, +0.5854524970f, +0.4901580215f, +0.1786131561f, +0.1114856005f, +0.5639878511f, 0.0f, 0.0f,
            +0.4284339547f, -0.7401829362f, +0.0111486688f, +0.3739085793f, +0.0887513459f, +0.3475124538f, 0.0f, 0.0f,
            -0.3968776464f, +0.0452770144f, -0.6518512368f, -0.2939636707f, +0.5544922948f, +0.1471449733f, 0.0f, 0.0f,
            +0.1747523546f, -0.0225779116f, -0.3918288350f, +0.2371454239f, +0.7704305649f, -0.4069653749f, 0.0f, 0.0f,
            -0.0506657362f, +0.5966653824f, -0.2066967785f, -0.3269529939f, +0.0096813068f, -0.7012186050f, 0.0f, 0.0f,
            -0.1962176561f, +0.4818023741f, +0.0385698676f, -0.0716460049f, +0.3555234969f, +0.7722356915f, 0.0f, 0.0f,
            -0.1875544786f, -0.4011857510f, -0.4994053543f, +0.1535858214f, -0.4234359264f, +0.5929421782f, 0.0f, 0.0f,
            -0.2051967382f, -0.1370881498f, -0.5879518390f, +0.2051874995f, +0.1080432683f, -0.7346010208f, 0.0f, 0.0f,
            +0.1125470996f, +0.5160606503f, -0.7015146017f, -0.2644234002f, +0.0722578913f, -0.3921106160f, 0.0f, 0.0f,
            +0.6208398342f, -0.2892217338f, +0.2854861617f, -0.3069616854f, +0.5959448814f, +0.0055578575f, 0.0f, 0.0f,
            +0.0752323866f, +0.8312731385f, -0.1501965374f, -0.3180757463f, -0.2649124265f, -0.3307793438f, 0.0f, 0.0f,
            +0.4271706939f, -0.3553755581f, +0.2822435498f, -0.3818562031f, -0.0151495337f, -0.6822965145f, 0.0f, 0.0f,
            +0.1432034373f, -0.2634466588f, -0.7834610939f, +0.4089841545f, -0.3103615344f, +0.1807889789f, 0.0f, 0.0f,
            +0.3540483713f, -0.3773740530f, -0.2607714236f, -0.1622319221f, +0.4917523861f, -0.6293624043f, 0.0f, 0.0f,
            +0.3624292612f, +0.7189853787f, +0.1423764974f, +0.2605557442f, -0.4935359955f, +0.1413051486f, 0.0f, 0.0f,
            +0.8904361725f, -0.2107102573f, +0.2656660378f, +0.0167762488f, +0.0947417915f, -0.2879030704f, 0.0f, 0.0f,
            -0.1831395626f, +0.0101975575f, +0.1991053820f, -0.3416745067f, -0.7237075567f, -0.5349943638f, 0.0f, 0.0f,
            +0.3275060654f, -0.3492375016f, +0.6319241524f, +0.2661213875f, +0.0758186281f, -0.5430244803f, 0.0f, 0.0f,
            +0.5981240273f, +0.4928845763f, +0.2797962427f, +0.0233513489f, -0.5124615431f, +0.2405499667f, 0.0f, 0.0f,
            -0.6908330917f, +0.1780716926f, +0.3039638698f, +0.4520987868f, -0.4351642132f, +0.0698899478f, 0.0f, 0.0f,
            -0.5981240273f, -0.4928845763f, -0.2797962427f, -0.0233513489f, +0.5124615431f, -0.2405499667f, 0.0f, 0.0f,
            -0.1709820032f, -0.8575593829f, +0.4348439872f, -0.1401913762f, -0.0862796009f, -0.1384580582f, 0.0f, 0.0f,
            +0.4825593829f, -0.1224169955f, -0.2779754698f, -0.4667891264f, +0.6621223688f, -0.1363160312f, 0.0f, 0.0f,
            +0.2101343870f, +0.1003255695f, +0.7621865273f, +0.5886649489f, -0.0345680416f, +0.1308745295f, 0.0f, 0.0f,
            +0.4440541267f, +0.1010112613f, -0.2699742317f, -0.5694370866f, -0.6091700792f, +0.1561378837f, 0.0f, 0.0f,
            +0.0729080439f, +0.4134978652f, -0.6914373636f, +0.3964928985f, -0.4105882943f, -0.1408144683f, 0.0f, 0.0f,
            -0.3782497644f, -0.5520551205f, -0.1029367074f, +0.4473035634f, -0.5725648999f, +0.1168551221f, 0.0f, 0.0f,
            -0.0271241665f, -0.3681741953f, -0.2618570924f, -0.5696759224f, -0.6852018833f, +0.0333189964f, 0.0f, 0.0f,
            -0.0414444208f, -0.1226754040f, -0.7657818198f, -0.2057385147f, +0.1131953746f, +0.5845252872f, 0.0f, 0.0f,
            -0.2865228653f, -0.4973852336f, -0.3639766872f, -0.2210492790f, -0.6378838420f, -0.2868358195f, 0.0f, 0.0f,
            -0.4057656527f, +0.0675129145f, -0.3113776147f, -0.1400330365f, +0.3804084063f, -0.7546656132f, 0.0f, 0.0f,
            +0.1962176561f, -0.4818023741f, -0.0385698676f, +0.0716460049f, -0.3555234969f, -0.7722356915f, 0.0f, 0.0f,
            +0.3231120110f, +0.2775039077f, +0.8490881324f, -0.2941025496f, -0.1055530682f, -0.0014762878f, 0.0f, 0.0f,
            +0.3782497644f, +0.5520551205f, +0.1029367074f, -0.4473035634f, +0.5725648999f, -0.1168551221f, 0.0f, 0.0f,
            -0.1913313866f, -0.1291478425f, -0.2650260925f, +0.6990799308f, +0.3726894259f, -0.4988626242f, 0.0f, 0.0f,
            +0.1002062559f, +0.3825446069f, -0.5027132630f, -0.1887389123f, -0.6597163677f, +0.3464810848f, 0.0f, 0.0f,
            +0.6869664192f, +0.0752433687f, -0.1640594602f, -0.3079922497f, +0.5473255515f, -0.3179238737f, 0.0f, 0.0f,
            -0.0981146097f, +0.0949064344f, -0.4293571413f, +0.7165017724f, -0.4791451097f, +0.2325162143f, 0.0f, 0.0f,
            +0.2865228653f, +0.4973852336f, +0.3639766872f, +0.2210492790f, +0.6378838420f, +0.2868358195f, 0.0f, 0.0f,
            -0.0752323866f, -0.8312731385f, +0.1501965374f, +0.3180757463f, +0.2649124265f, +0.3307793438f, 0.0f, 0.0f,
            +0.3863718510f, -0.0679746270f, -0.8122249246f, -0.2599541545f, +0.3066318035f, -0.1574404836f, 0.0f, 0.0f,
            +0.4897602201f, +0.0430196971f, +0.4058110714f, -0.3589518666f, +0.1849599928f, +0.6561590433f, 0.0f, 0.0f,
            +0.0981146097f, -0.0949064344f, +0.4293571413f, -0.7165017724f, +0.4791451097f, -0.2325162143f, 0.0f, 0.0f,
            +0.5126454830f, +0.6959911585f, -0.1778003275f, +0.4295687377f, -0.1168115735f, +0.1516701877f, 0.0f, 0.0f,
            -0.6208398342f, +0.2892217338f, -0.2854861617f, +0.3069616854f, -0.5959448814f, -0.0055578575f, 0.0f, 0.0f,
            -0.1666934490f, +0.3643845320f, +0.5206363797f, +0.2300730050f, +0.2217174023f, +0.6828490496f, 0.0f, 0.0f,
            -0.7287732363f, +0.4028936923f, -0.5133012533f, +0.0260427296f, +0.0955726206f, +0.1824158877f, 0.0f, 0.0f,
            -0.5909053087f, +0.2556146383f, -0.1990545541f, -0.4859477282f, +0.5559396744f, +0.0255956054f, 0.0f, 0.0f,
            -0.4187184572f, -0.7771410942f, -0.2486240566f, -0.2100264579f, -0.3384027481f, -0.0168800466f, 0.0f, 0.0f,
            -0.0774455070f, -0.8057125211f, -0.4132475257f, -0.2681514621f, -0.0444436595f, -0.3165046573f, 0.0f, 0.0f,
            -0.3025460243f, +0.0755243599f, +0.2778039575f, +0.1002607122f, -0.2079829574f, -0.8787936568f, 0.0f, 0.0f,
            -0.1167320013f, +0.6225264072f, -0.2972928286f, -0.2955351174f, -0.0675526559f, -0.6469517946f, 0.0f, 0.0f,
            +0.2043955326f, +0.1053923890f, -0.2659108937f, -0.6500580311f, +0.4346653819f, +0.5146814585f, 0.0f, 0.0f,
            +0.8643671274f, -0.0076993853f, +0.4316996634f, +0.1537042260f, +0.1975279748f, +0.0616711080f, 0.0f, 0.0f,
            +0.7402177453f, +0.4702273011f, -0.4595199227f, +0.1163212508f, -0.0493032336f, -0.0619992018f, 0.0f, 0.0f,
            -0.2101343870f, -0.1003255695f, -0.7621865273f, -0.5886649489f, +0.0345680416f, -0.1308745295f, 0.0f, 0.0f,
            +0.6264783144f, -0.0403335243f, -0.2235517502f, +0.6567904949f, -0.0000801347f, -0.3529140949f, 0.0f, 0.0f,
            -0.4440541267f, -0.1010112613f, +0.2699742317f, +0.5694370866f, +0.6091700792f, -0.1561378837f, 0.0f, 0.0f,
            -0.3093979359f, -0.1141849309f, -0.6130901575f, +0.0985352397f, +0.2539614141f, -0.6641908288f, 0.0f, 0.0f,
            +0.3025460243f, -0.0755243599f, -0.2778039575f, -0.1002607122f, +0.2079829574f, +0.8787936568f, 0.0f, 0.0f,
            +0.1387164593f, -0.3075677454f, -0.1653710902f, +0.4419374466f, -0.7947620153f, +0.1784853339f, 0.0f, 0.0f,
            +0.7374488115f, -0.4766946137f, -0.2159651220f, -0.1574023664f, +0.0695485398f, -0.3907400370f, 0.0f, 0.0f,
            -0.5689454079f, +0.2034867257f, -0.3256599605f, +0.1142677292f, +0.6573780775f, -0.2891997695f, 0.0f, 0.0f,
            +0.2785867453f, +0.4532285035f, -0.3385916054f, -0.6351627707f, +0.2770298421f, -0.3495021462f, 0.0f, 0.0f,
            +0.1200233698f, +0.5771817565f, +0.6937184334f, -0.2557889819f, +0.2929005325f, +0.1413915455f, 0.0f, 0.0f,
            +0.1893571615f, +0.7649993896f, +0.4938068092f, +0.1215827763f, -0.2958166599f, +0.1810640395f, 0.0f, 0.0f,
            -0.1813635826f, -0.4789541960f, +0.6832291484f, -0.0411973447f, +0.4065423310f, -0.3223886788f, 0.0f, 0.0f,
            -0.2421034575f, -0.2793081105f, -0.1206686795f, -0.1781552732f, +0.6608430743f, -0.6167324185f, 0.0f, 0.0f,
            +0.1161904335f, -0.4921432137f, -0.6903575659f, +0.4057750702f, -0.0998100638f, -0.3050991893f, 0.0f, 0.0f,
            -0.3132215142f, -0.2635257244f, -0.2291566879f, +0.0430522338f, -0.7744936943f, -0.4221841693f, 0.0f, 0.0f,
            -0.4992538095f, -0.5172593594f, -0.4479877949f, +0.2761235833f, -0.2453980297f, -0.3821397424f, 0.0f, 0.0f,
            -0.3601060510f, +0.0173940696f, +0.4522676468f, -0.0003153421f, +0.6793802381f, +0.4515717030f, 0.0f, 0.0f,
            -0.5404145718f, +0.2441326976f, -0.2600440085f, -0.6839744449f, +0.2383713871f, -0.2368254960f, 0.0f, 0.0f,
            +0.7117182016f, -0.5409841537f, +0.1857579052f, -0.3074691594f, -0.2385217398f, -0.1218909472f, 0.0f, 0.0f,
            -0.2142882943f, +0.4175734222f, +0.0801376030f, +0.8255137205f, -0.1757739186f, -0.2468228340f, 0.0f, 0.0f,
            -0.3677458763f, +0.4902043641f, -0.0169921108f, +0.0033596605f, -0.1875487566f, +0.7674553394f, 0.0f, 0.0f,
            +0.5510939360f, +0.1074754298f, -0.6826416254f, -0.1793766916f, +0.4288653135f, +0.0514160022f, 0.0f, 0.0f,
            -0.0342935324f, +0.3289054036f, +0.9079316258f, -0.1073937044f, -0.2050102949f, +0.1128836200f, 0.0f, 0.0f,
            +0.3968776464f, -0.0452770144f, +0.6518512368f, +0.2939636707f, -0.5544922948f, -0.1471449733f, 0.0f, 0.0f,
            +0.0602706075f, -0.5346590281f, +0.1774500459f, +0.7179715037f, +0.4037035108f, -0.0236485079f, 0.0f, 0.0f,
            +0.3558551073f, -0.3234240413f, -0.1513571739f, -0.3045706153f, +0.7846769094f, -0.1933220029f, 0.0f, 0.0f,
            -0.2094173431f, -0.3208656609f, +0.0221391916f, +0.1584120095f, +0.4314405322f, -0.8009146452f, 0.0f, 0.0f,
            +0.4057656527f, -0.0675129145f, +0.3113776147f, +0.1400330365f, -0.3804084063f, +0.7546656132f, 0.0f, 0.0f,
            -0.0964937806f, -0.4573700428f, +0.5160994530f, -0.3812451363f, +0.0945625007f, -0.6007101536f, 0.0f, 0.0f,
            -0.3275060654f, +0.3492375016f, -0.6319241524f, -0.2661213875f, -0.0758186281f, +0.5430244803f, 0.0f, 0.0f,
            +0.2051967382f, +0.1370881498f, +0.5879518390f, -0.2051874995f, -0.1080432683f, +0.7346010208f, 0.0f, 0.0f,
            +0.0506657362f, -0.5966653824f, +0.2066967785f, +0.3269529939f, -0.0096813068f, +0.7012186050f, 0.0f, 0.0f,
            -0.4734349251f, +0.2138127685f, +0.2288961262f, +0.0197854638f, -0.0250411443f, +0.8226370215f, 0.0f, 0.0f,
            +0.2297585607f, +0.3368583322f, +0.4422554076f, -0.7742177248f, -0.1592402756f, -0.1156569123f, 0.0f, 0.0f,
            +0.0342935324f, -0.3289054036f, -0.9079316258f, +0.1073937044f, +0.2050102949f, -0.1128836200f, 0.0f, 0.0f,
            -0.5958921313f, +0.4717123508f, +0.0752419680f, +0.4298895001f, +0.4804389477f, -0.0333465002f, 0.0f, 0.0f,
            +0.1813635826f, +0.4789541960f, -0.6832291484f, +0.0411973447f, -0.4065423310f, +0.3223886788f, 0.0f, 0.0f,
            -0.2297585607f, -0.3368583322f, -0.4422554076f, +0.7742177248f, +0.1592402756f, +0.1156569123f, 0.0f, 0.0f,
            +0.1772633791f, -0.4716815948f, +0.3651756644f, +0.6627026200f, +0.4137061238f, -0.0491255745f, 0.0f, 0.0f,
            +0.1033464670f, +0.3029784858f, -0.1485645622f, -0.4428629875f, +0.7978770733f, +0.2066790760f, 0.0f, 0.0f,
            +0.0539913177f, -0.0640245378f, -0.4019110799f, -0.5301620960f, +0.6863521934f, -0.2816056013f, 0.0f, 0.0f,
            +0.6986821890f, +0.0010490716f, -0.4708707333f, -0.4784072042f, +0.1942024380f, +0.1534098834f, 0.0f, 0.0f,
            -0.0392329693f, +0.8251270056f, +0.2681472301f, -0.3655394912f, +0.3323317766f, -0.0407329947f, 0.0f, 0.0f,
            +0.4719250202f, -0.7658368945f, -0.3563901186f, -0.0386263728f, +0.1846246868f, -0.1678942144f, 0.0f, 0.0f,
            +0.2875063419f, +0.5249719620f, +0.2788235247f, -0.7007287741f, -0.1739048660f, -0.2067318261f, 0.0f, 0.0f,
            -0.0044209957f, +0.9019955397f, +0.3258308768f, -0.0947957933f, -0.1306882948f, +0.2327074558f, 0.0f, 0.0f,
            -0.1069881916f, +0.5648230910f, -0.1126850992f, -0.0417897739f, +0.5995839834f, -0.5436751842f, 0.0f, 0.0f,
            +0.3093979359f, +0.1141849309f, +0.6130901575f, -0.0985352397f, -0.2539614141f, +0.6641908288f, 0.0f, 0.0f,
            +0.2421034575f, +0.2793081105f, +0.1206686795f, +0.1781552732f, -0.6608430743f, +0.6167324185f, 0.0f, 0.0f,
            +0.0044209957f, -0.9019955397f, -0.3258308768f, +0.0947957933f, +0.1306882948f, -0.2327074558f, 0.0f, 0.0f,
            -0.1200233698f, -0.5771817565f, -0.6937184334f, +0.2557889819f, -0.2929005325f, -0.1413915455f, 0.0f, 0.0f,
            +0.3657615185f, +0.3721697330f, +0.3731113076f, -0.0704583451f, +0.5061950684f, -0.5720996261f, 0.0f, 0.0f,
            +0.0422446728f, -0.7614569068f, -0.2642603517f, +0.0495838635f, +0.2838862836f, -0.5152812600f, 0.0f, 0.0f,
            +0.2446261644f, +0.2116619051f, -0.4678121209f, -0.1871315241f, +0.8004072905f, -0.0289557725f, 0.0f, 0.0f,
            -0.3863718510f, +0.0679746270f, +0.8122249246f, +0.2599541545f, -0.3066318035f, +0.1574404836f, 0.0f, 0.0f,
            +0.3507099152f, +0.0542697608f, +0.4209029078f, -0.3597576618f, +0.7461711764f, +0.1034438759f, 0.0f, 0.0f,
            +0.1167320013f, -0.6225264072f, +0.2972928286f, +0.2955351174f, +0.0675526559f, +0.6469517946f, 0.0f, 0.0f,
            -0.2785867453f, -0.4532285035f, +0.3385916054f, +0.6351627707f, -0.2770298421f, +0.3495021462f, 0.0f, 0.0f,
            -0.5183033943f, +0.0121638700f, +0.3773007393f, -0.2930837572f, +0.4909642339f, -0.5117750764f, 0.0f, 0.0f,
            -0.3590784669f, +0.4270640016f, -0.7617442012f, +0.2343474478f, -0.1221706048f, +0.1964193135f, 0.0f, 0.0f,
            +0.3588180542f, +0.5312285423f, -0.2981200218f, -0.6241783500f, +0.1631361246f, -0.2897554040f, 0.0f, 0.0f,
            +0.2961451411f, -0.7347037792f, +0.4256847203f, -0.2592857480f, +0.1652536094f, -0.3110674024f, 0.0f, 0.0f,
            -0.5409727097f, +0.3918426335f, +0.3426568210f, -0.1661424041f, +0.4204911888f, -0.4816412926f, 0.0f, 0.0f,
            +0.0220623016f, -0.9578628540f, +0.1544855237f, +0.0689527690f, +0.2270242125f, -0.0430330858f, 0.0f, 0.0f,
            -0.8643671274f, +0.0076993853f, -0.4316996634f, -0.1537042260f, -0.1975279748f, -0.0616711080f, 0.0f, 0.0f,
            -0.2961451411f, +0.7347037792f, -0.4256847203f, +0.2592857480f, -0.1652536094f, +0.3110674024f, 0.0f, 0.0f,
            -0.0388053656f, -0.5901203752f, -0.4632027149f, -0.6381313801f, -0.0113432556f, -0.1683882773f, 0.0f, 0.0f,
            +0.3083657026f, +0.4026551843f, +0.2894597054f, +0.2734915018f, -0.2776434124f, -0.7121157646f, 0.0f, 0.0f,
            -0.1002062559f, -0.3825446069f, +0.5027132630f, +0.1887389123f, +0.6597163677f, -0.3464810848f, 0.0f, 0.0f,
            +0.4187184572f, +0.7771410942f, +0.2486240566f, +0.2100264579f, +0.3384027481f, +0.0168800466f, 0.0f, 0.0f,
            -0.5589281321f, -0.5154735446f, -0.2994868755f, +0.2944864929f, +0.1651475579f, -0.4671169519f, 0.0f, 0.0f,
            +0.3627503514f, +0.2439851165f, -0.4784145355f, +0.3871673346f, -0.6099373698f, +0.2409997582f, 0.0f, 0.0f,
            -0.2031803131f, -0.6117979288f, +0.0339146703f, +0.7503009439f, -0.0490158200f, +0.1338536441f, 0.0f, 0.0f,
            -0.6344519854f, -0.1686923653f, -0.3569357693f, +0.4069772363f, +0.4713703692f, +0.2319264263f, 0.0f, 0.0f,
            -0.4897602201f, -0.0430196971f, -0.4058110714f, +0.3589518666f, -0.1849599928f, -0.6561590433f, 0.0f, 0.0f,
            -0.6986821890f, -0.0010490716f, +0.4708707333f, +0.4784072042f, -0.1942024380f, -0.1534098834f, 0.0f, 0.0f,
            +0.5234503746f, -0.0566369891f, -0.1219781265f, +0.6776737571f, -0.4131974280f, +0.2791761458f, 0.0f, 0.0f,
            +0.6424818039f, -0.0607392043f, +0.7081311941f, +0.2684156001f, -0.0481144935f, -0.0878378749f, 0.0f, 0.0f,
            +0.5909053087f, -0.2556146383f, +0.1990545541f, +0.4859477282f, -0.5559396744f, -0.0255956054f, 0.0f, 0.0f,
            +0.0964937806f, +0.4573700428f, -0.5160994530f, +0.3812451363f, -0.0945625007f, +0.6007101536f, 0.0f, 0.0f,
            +0.1913313866f, +0.1291478425f, +0.2650260925f, -0.6990799308f, -0.3726894259f, +0.4988626242f, 0.0f, 0.0f,
            +0.1666934490f, -0.3643845320f, -0.5206363797f, -0.2300730050f, -0.2217174023f, -0.6828490496f, 0.0f, 0.0f,
            +0.1875544786f, +0.4011857510f, +0.4994053543f, -0.1535858214f, +0.4234359264f, -0.5929421782f, 0.0f, 0.0f,
            +0.5183033943f, -0.0121638700f, -0.3773007393f, +0.2930837572f, -0.4909642339f, +0.5117750764f, 0.0f, 0.0f,
            -0.3507099152f, -0.0542697608f, -0.4209029078f, +0.3597576618f, -0.7461711764f, -0.1034438759f, 0.0f, 0.0f,
            -0.1033464670f, -0.3029784858f, +0.1485645622f, +0.4428629875f, -0.7978770733f, -0.2066790760f, 0.0f, 0.0f,
            +0.0774455070f, +0.8057125211f, +0.4132475257f, +0.2681514621f, +0.0444436595f, +0.3165046573f, 0.0f, 0.0f,
            +0.7756626606f, -0.3521449566f, +0.1992188394f, +0.4065485597f, +0.2126327157f, -0.1554318368f, 0.0f, 0.0f,
            +0.2094173431f, +0.3208656609f, -0.0221391916f, -0.1584120095f, -0.4314405322f, +0.8009146452f, 0.0f, 0.0f,
    };

}
