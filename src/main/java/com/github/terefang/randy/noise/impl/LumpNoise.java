package com.github.terefang.randy.noise.impl;

import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.NoiseUtil;

public class LumpNoise  extends NoiseUtil implements INoise
{

    @Override
    public double _noise1(long seed, double x, int interpolation) {
        return singleLump(false, makeSeedInt(seed), this.getHarshness(), x, this.getMutation());
    }

    @Override
    public double _noise2(long seed, double x, double y, int interpolation) {
        return singleLump(false, makeSeedInt(seed), this.getHarshness(), x,y, this.getMutation());
    }

    @Override
    public double _noise3(long seed, double x, double y, double z, int interpolation) {
        return singleLump(false, makeSeedInt(seed), this.getHarshness(), x,y,z, this.getMutation());
    }

    @Override
    public double _noise4(long seed, double x, double y, double z, double u, int interpolation) {
        return singleLump(false, makeSeedInt(seed), this.getHarshness(), x,y,z,u, this.getMutation());
    }

    @Override
    public double _noise5(long seed, double x, double y, double z, double u, double v, int interpolation) {
        return singleLump(false, makeSeedInt(seed), this.getHarshness(), x,y,z,u,v, this.getMutation());
    }

    @Override
    public double _noise6(long seed, double x, double y, double z, double u, double v, double w, int interpolation) {
        return singleLump(false, makeSeedInt(seed), this.getHarshness(), x,y,z,u,v,w+this.getMutation());
    }

    @Override
    public double _noise1n(long seed, double x, int interpolation) {
        return singleLump(true, makeSeedInt(seed), this.getHarshness(), x, this.getMutation());
    }

    @Override
    public double _noise2n(long seed, double x, double y, int interpolation) {
        return singleLump(true, makeSeedInt(seed), this.getHarshness(), x,y, this.getMutation());
    }

    @Override
    public double _noise3n(long seed, double x, double y, double z, int interpolation) {
        return singleLump(true, makeSeedInt(seed), this.getHarshness(), x,y,z, this.getMutation());
    }

    @Override
    public double _noise4n(long seed, double x, double y, double z, double u, int interpolation) {
        return singleLump(true, makeSeedInt(seed), this.getHarshness(), x,y,z,u, this.getMutation());
    }

    @Override
    public double _noise5n(long seed, double x, double y, double z, double u, double v, int interpolation) {
        return singleLump(true, makeSeedInt(seed), this.getHarshness(), x,y,z,u,v, this.getMutation());
    }

    @Override
    public double _noise6n(long seed, double x, double y, double z, double u, double v, double w, int interpolation) {
        return singleLump(true, makeSeedInt(seed), this.getHarshness(), x,y,z,u,v,w+this.getMutation());
    }

    // ------------------------------------------------------------------------------------------------

    public static final double singleLump(boolean normalize, int seed, double _harshness, double x, double y)
    {
        double t = (x + y) * F2;
        int i = fastFloor(x + t);
        int j = fastFloor(y + t);

        t = (i + j) * G2;
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

        double x1 = x0 - i1 + G2;
        double y1 = y0 - j1 + G2;
        double x2 = x0 - 1 + H2;
        double y2 = y0 - 1 + H2;

        double n0, n1, n2;

        n0 = LIMIT2 - x0 * x0 - y0 * y0;
        if (n0 > 0) {
            n0 *= n0;
            n0 *= n0 * gradCoord2D(seed, i, j, x0, y0);
        }
        else n0 = 0.0;

        n1 = LIMIT2 - x1 * x1 - y1 * y1;
        if (n1 > 0) {
            n1 *= n1;
            n1 *= n1 * gradCoord2D(seed, i + i1, j + j1, x1, y1);
        }
        else n1 = 0.0;

        n2 = LIMIT2 - x2 * x2 - y2 * y2;
        if (n2 > 0)  {
            n2 *= n2;
            n2 *= n2 * gradCoord2D(seed, i + 1, j + 1, x2, y2);
        }
        else n2 = 0.0;

        double ex = Math.exp((n0 + n1 + n2) * 100.0 * _harshness);
        double _v = (double) ((ex - 1.0) / (ex + 1.0));
        if(normalize) _v = (_v*.5)+.5;
        return _v;
    }

    public static final double singleLump(boolean normalize, int seed, double _harshness, double x, double y, double z)
    {
        final double s = (x + y + z) * F3;
        final int i = fastFloor(x + s),
                j = fastFloor(y + s),
                k = fastFloor(z + s);

        final double t = (i + j + k) * G3;
        final double X0 = i - t, Y0 = j - t, Z0 = k - t,
                x0 = x - X0, y0 = y - Y0, z0 = z - Z0;

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
            } else {
                i1 = 0;
                j1 = 0;
                k1 = 1;
                i2 = 1;
                j2 = 0;
                k2 = 1;
            }
        } else {
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
            } else {
                i1 = 0;
                j1 = 1;
                k1 = 0;
                i2 = 1;
                j2 = 1;
                k2 = 0;
            }
        }

        double x1 = x0 - i1 + G3; // Offsets for second corner in (x,y,z) coords
        double y1 = y0 - j1 + G3;
        double z1 = z0 - k1 + G3;
        double x2 = x0 - i2 + F3; // Offsets for third corner in (x,y,z) coords
        double y2 = y0 - j2 + F3;
        double z2 = z0 - k2 + F3;
        double x3 = x0 - 0.5; // Offsets for last corner in (x,y,z) coords
        double y3 = y0 - 0.5;
        double z3 = z0 - 0.5;

        // Calculate the contribution from the four corners
        double n0 = LIMIT3 - x0 * x0 - y0 * y0 - z0 * z0;
        if (n0 > 0) {
            n0 *= n0;
            n0 *= n0 * gradCoord3D(seed, i, j, k, x0, y0, z0);
        }
        else n0 = 0.0;

        double n1 = LIMIT3 - x1 * x1 - y1 * y1 - z1 * z1;
        if (n1 > 0) {
            n1 *= n1;
            n1 *= n1 * gradCoord3D(seed, i + i1, j + j1, k + k1, x1, y1, z1);
        }
        else n1 = 0.0;

        double n2 = LIMIT3 - x2 * x2 - y2 * y2 - z2 * z2;
        if (n2 > 0) {
            n2 *= n2;
            n2 *= n2 * gradCoord3D(seed, i + i2, j + j2, k + k2, x2, y2, z2);
        }
        else n2 = 0.0;
        double n3 = LIMIT3 - x3 * x3 - y3 * y3 - z3 * z3;
        if (n3 > 0) {
            n3 *= n3;
            n3 *= n3 * gradCoord3D(seed, i + 1, j + 1, k + 1, x3, y3, z3);
        }
        else n3 = 0.0;

        double ex = Math.exp((n0 + n1 + n2 + n3) * 45.0 * _harshness);
        double _v = (double) ((ex - 1.0) / (ex + 1.0));
        if(normalize) _v = (_v*.5)+.5;
        return _v;
    }

    public static final double singleLump(boolean normalize, int seed, double _harshness, double x, double y, double z, double w)
    {
        double n0, n1, n2, n3, n4;
        double t = (x + y + z + w) * F4;
        int i = fastFloor(x + t);
        int j = fastFloor(y + t);
        int k = fastFloor(z + t);
        int l = fastFloor(w + t);
        t = (i + j + k + l) * G4;
        double X0 = i - t;
        double Y0 = j - t;
        double Z0 = k - t;
        double W0 = l - t;
        double x0 = x - X0;
        double y0 = y - Y0;
        double z0 = z - Z0;
        double w0 = w - W0;

        int rankx = 0;
        int ranky = 0;
        int rankz = 0;
        int rankw = 0;

        if (x0 > y0) rankx++; else ranky++;
        if (x0 > z0) rankx++; else rankz++;
        if (x0 > w0) rankx++; else rankw++;

        if (y0 > z0) ranky++; else rankz++;
        if (y0 > w0) ranky++; else rankw++;

        if (z0 > w0) rankz++; else rankw++;

        int i1 = 2 - rankx >>> 31;
        int j1 = 2 - ranky >>> 31;
        int k1 = 2 - rankz >>> 31;
        int l1 = 2 - rankw >>> 31;

        int i2 = 1 - rankx >>> 31;
        int j2 = 1 - ranky >>> 31;
        int k2 = 1 - rankz >>> 31;
        int l2 = 1 - rankw >>> 31;

        int i3 = -rankx >>> 31;
        int j3 = -ranky >>> 31;
        int k3 = -rankz >>> 31;
        int l3 = -rankw >>> 31;

        double x1 = x0 - i1 + G4;
        double y1 = y0 - j1 + G4;
        double z1 = z0 - k1 + G4;
        double w1 = w0 - l1 + G4;

        double x2 = x0 - i2 + 2 * G4;
        double y2 = y0 - j2 + 2 * G4;
        double z2 = z0 - k2 + 2 * G4;
        double w2 = w0 - l2 + 2 * G4;

        double x3 = x0 - i3 + 3 * G4;
        double y3 = y0 - j3 + 3 * G4;
        double z3 = z0 - k3 + 3 * G4;
        double w3 = w0 - l3 + 3 * G4;

        double x4 = x0 - 1 + 4 * G4;
        double y4 = y0 - 1 + 4 * G4;
        double z4 = z0 - 1 + 4 * G4;
        double w4 = w0 - 1 + 4 * G4;

        double t0 = LIMIT4 - x0 * x0 - y0 * y0 - z0 * z0 - w0 * w0;
        if(t0 > 0) {
            final int h0 = (hash256(i, j, k, l, seed) & 0xFC);
            t0 *= t0;
            n0 = t0 * t0 * (x0 * GRAD_4D[h0] + y0 * GRAD_4D[h0 | 1] + z0 * GRAD_4D[h0 | 2] + w0 * GRAD_4D[h0 | 3]);
        }
        else n0 = 0;
        double t1 = LIMIT4 - x1 * x1 - y1 * y1 - z1 * z1 - w1 * w1;
        if (t1 > 0) {
            final int h1 = (hash256(i + i1, j + j1, k + k1, l + l1, seed) & 0xFC);
            t1 *= t1;
            n1 = t1 * t1 * (x1 * GRAD_4D[h1] + y1 * GRAD_4D[h1 | 1] + z1 * GRAD_4D[h1 | 2] + w1 * GRAD_4D[h1 | 3]);
        }
        else n1 = 0;
        double t2 = LIMIT4 - x2 * x2 - y2 * y2 - z2 * z2 - w2 * w2;
        if (t2 > 0) {
            final int h2 = (hash256(i + i2, j + j2, k + k2, l + l2, seed) & 0xFC);
            t2 *= t2;
            n2 = t2 * t2 * (x2 * GRAD_4D[h2] + y2 * GRAD_4D[h2 | 1] + z2 * GRAD_4D[h2 | 2] + w2 * GRAD_4D[h2 | 3]);
        }
        else n2 = 0;
        double t3 = LIMIT4 - x3 * x3 - y3 * y3 - z3 * z3 - w3 * w3;
        if (t3 > 0) {
            final int h3 = (hash256(i + i3, j + j3, k + k3, l + l3, seed) & 0xFC);
            t3 *= t3;
            n3 = t3 * t3 * (x3 * GRAD_4D[h3] + y3 * GRAD_4D[h3 | 1] + z3 * GRAD_4D[h3 | 2] + w3 * GRAD_4D[h3 | 3]);
        }
        else n3 = 0;
        double t4 = LIMIT4 - x4 * x4 - y4 * y4 - z4 * z4 - w4 * w4;
        if (t4 > 0) {
            final int h4 = (hash256(i + 1, j + 1, k + 1, l + 1, seed) & 0xFC);
            t4 *= t4;
            n4 = t4 * t4 * (x4 * GRAD_4D[h4] + y4 * GRAD_4D[h4 | 1] + z4 * GRAD_4D[h4 | 2] + w4 * GRAD_4D[h4 | 3]);
        }
        else n4 = 0;

        // debug code, for finding what constant should be used for 14.75
//        final double ret =  (n0 + n1 + n2 + n3 + n4) * (14.7279);
//        if(ret < -1 || ret > 1) {
//            System.out.println(ret + " is out of bounds! seed=" + seed + ", x=" + x + ", y=" + y + ", z=" + z + ", w=" + w);
//            return ret * -0.5;
//        }
//        return ret;
        // normal return code
        double ex = Math.exp((n0 + n1 + n2 + n3 + n4) * 15.0 * _harshness);
        double _v = (double) ((ex - 1.0) / (ex + 1.0));
        if(normalize) _v = (_v*.5)+.5;
        return _v;
    }

    public static final double singleLump(boolean normalize, int seed, double _harshness, double x, double y, double z, double w, double u)
    {
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

        double ex = Math.exp((n0 + n1 + n2 + n3 + n4 + n5) * 15.0 * _harshness);
        double _v = (double) ((ex - 1.0) / (ex + 1.0));
        if(normalize) _v = (_v*.5)+.5;
        return _v;
    }

    public static final double singleLump(boolean normalize, int seed, double _harshness, double x, double y, double z, double w, double u, double v)
    {
        double n0, n1, n2, n3, n4, n5, n6;
        double t = (x + y + z + w + u + v) * F6;
        int i = fastFloor(x + t);
        int j = fastFloor(y + t);
        int k = fastFloor(z + t);
        int l = fastFloor(w + t);
        int h = fastFloor(u + t);
        int g = fastFloor(v + t);
        t = (i + j + k + l + h + g) * G6;
        double X0 = i - t;
        double Y0 = j - t;
        double Z0 = k - t;
        double W0 = l - t;
        double U0 = h - t;
        double V0 = g - t;
        double x0 = x - X0;
        double y0 = y - Y0;
        double z0 = z - Z0;
        double w0 = w - W0;
        double u0 = u - U0;
        double v0 = v - V0;

        int rankx = 0;
        int ranky = 0;
        int rankz = 0;
        int rankw = 0;
        int ranku = 0;
        int rankv = 0;

        if (x0 > y0) rankx++; else ranky++;
        if (x0 > z0) rankx++; else rankz++;
        if (x0 > w0) rankx++; else rankw++;
        if (x0 > u0) rankx++; else ranku++;
        if (x0 > v0) rankx++; else rankv++;

        if (y0 > z0) ranky++; else rankz++;
        if (y0 > w0) ranky++; else rankw++;
        if (y0 > u0) ranky++; else ranku++;
        if (y0 > v0) ranky++; else rankv++;

        if (z0 > w0) rankz++; else rankw++;
        if (z0 > u0) rankz++; else ranku++;
        if (z0 > v0) rankz++; else rankv++;

        if (w0 > u0) rankw++; else ranku++;
        if (w0 > v0) rankw++; else rankv++;

        if (u0 > v0) ranku++; else rankv++;

        int i1 = 4 - rankx >>> 31;
        int j1 = 4 - ranky >>> 31;
        int k1 = 4 - rankz >>> 31;
        int l1 = 4 - rankw >>> 31;
        int h1 = 4 - ranku >>> 31;
        int g1 = 4 - rankv >>> 31;

        int i2 = 3 - rankx >>> 31;
        int j2 = 3 - ranky >>> 31;
        int k2 = 3 - rankz >>> 31;
        int l2 = 3 - rankw >>> 31;
        int h2 = 3 - ranku >>> 31;
        int g2 = 3 - rankv >>> 31;

        int i3 = 2 - rankx >>> 31;
        int j3 = 2 - ranky >>> 31;
        int k3 = 2 - rankz >>> 31;
        int l3 = 2 - rankw >>> 31;
        int h3 = 2 - ranku >>> 31;
        int g3 = 2 - rankv >>> 31;

        int i4 = 1 - rankx >>> 31;
        int j4 = 1 - ranky >>> 31;
        int k4 = 1 - rankz >>> 31;
        int l4 = 1 - rankw >>> 31;
        int h4 = 1 - ranku >>> 31;
        int g4 = 1 - rankv >>> 31;

        int i5 = -rankx >>> 31;
        int j5 = -ranky >>> 31;
        int k5 = -rankz >>> 31;
        int l5 = -rankw >>> 31;
        int h5 = -ranku >>> 31;
        int g5 = -rankv >>> 31;

        double x1 = x0 - i1 + G6;
        double y1 = y0 - j1 + G6;
        double z1 = z0 - k1 + G6;
        double w1 = w0 - l1 + G6;
        double u1 = u0 - h1 + G6;
        double v1 = v0 - g1 + G6;

        double x2 = x0 - i2 + 2 * G6;
        double y2 = y0 - j2 + 2 * G6;
        double z2 = z0 - k2 + 2 * G6;
        double w2 = w0 - l2 + 2 * G6;
        double u2 = u0 - h2 + 2 * G6;
        double v2 = v0 - g2 + 2 * G6;

        double x3 = x0 - i3 + 3 * G6;
        double y3 = y0 - j3 + 3 * G6;
        double z3 = z0 - k3 + 3 * G6;
        double w3 = w0 - l3 + 3 * G6;
        double u3 = u0 - h3 + 3 * G6;
        double v3 = v0 - g3 + 3 * G6;

        double x4 = x0 - i4 + 4 * G6;
        double y4 = y0 - j4 + 4 * G6;
        double z4 = z0 - k4 + 4 * G6;
        double w4 = w0 - l4 + 4 * G6;
        double u4 = u0 - h4 + 4 * G6;
        double v4 = v0 - g4 + 4 * G6;

        double x5 = x0 - i5 + 5 * G6;
        double y5 = y0 - j5 + 5 * G6;
        double z5 = z0 - k5 + 5 * G6;
        double w5 = w0 - l5 + 5 * G6;
        double u5 = u0 - h5 + 5 * G6;
        double v5 = v0 - g5 + 5 * G6;

        double x6 = x0 - 1 + 6 * G6;
        double y6 = y0 - 1 + 6 * G6;
        double z6 = z0 - 1 + 6 * G6;
        double w6 = w0 - 1 + 6 * G6;
        double u6 = u0 - 1 + 6 * G6;
        double v6 = v0 - 1 + 6 * G6;

        n0 = LIMIT6 - x0 * x0 - y0 * y0 - z0 * z0 - w0 * w0 - u0 * u0 - v0 * v0;
        if (n0 <= 0.0) n0 = 0.0;
        else
        {
            final int hash = hash256(i, j, k, l, h, g, seed) * 6;
            n0 *= n0;
            n0 *= n0 * (GRAD_6D[hash] * x0 + GRAD_6D[hash + 1] * y0 + GRAD_6D[hash + 2] * z0 +
                    GRAD_6D[hash + 3] * w0 + GRAD_6D[hash + 4] * u0 + GRAD_6D[hash + 5] * v0);
        }

        n1 = LIMIT6 - x1 * x1 - y1 * y1 - z1 * z1 - w1 * w1 - u1 * u1 - v1 * v1;
        if (n1 <= 0.0) n1 = 0.0;
        else
        {
            final int hash = hash256(i + i1, j + j1, k + k1, l + l1, h + h1, g + g1, seed) * 6;
            n1 *= n1;
            n1 *= n1 * (GRAD_6D[hash] * x1 + GRAD_6D[hash + 1] * y1 + GRAD_6D[hash + 2] * z1 +
                    GRAD_6D[hash + 3] * w1 + GRAD_6D[hash + 4] * u1 + GRAD_6D[hash + 5] * v1);
        }

        n2 = LIMIT6 - x2 * x2 - y2 * y2 - z2 * z2 - w2 * w2 - u2 * u2 - v2 * v2;
        if (n2 <= 0.0) n2 = 0.0;
        else
        {
            final int hash = hash256(i + i2, j + j2, k + k2, l + l2, h + h2, g + g2, seed) * 6;
            n2 *= n2;
            n2 *= n2 * (GRAD_6D[hash] * x2 + GRAD_6D[hash + 1] * y2 + GRAD_6D[hash + 2] * z2 +
                    GRAD_6D[hash + 3] * w2 + GRAD_6D[hash + 4] * u2 + GRAD_6D[hash + 5] * v2);
        }

        n3 = LIMIT6 - x3 * x3 - y3 * y3 - z3 * z3 - w3 * w3 - u3 * u3 - v3 * v3;
        if (n3 <= 0.0) n3 = 0.0;
        else
        {
            final int hash = hash256(i + i3, j + j3, k + k3, l + l3, h + h3, g + g3, seed) * 6;
            n3 *= n3;
            n3 *= n3 * (GRAD_6D[hash] * x3 + GRAD_6D[hash + 1] * y3 + GRAD_6D[hash + 2] * z3 +
                    GRAD_6D[hash + 3] * w3 + GRAD_6D[hash + 4] * u3 + GRAD_6D[hash + 5] * v3);
        }

        n4 = LIMIT6 - x4 * x4 - y4 * y4 - z4 * z4 - w4 * w4 - u4 * u4 - v4 * v4;
        if (n4 <= 0.0) n4 = 0.0;
        else
        {
            final int hash = hash256(i + i4, j + j4, k + k4, l + l4, h + h4, g + g4, seed) * 6;
            n4 *= n4;
            n4 *= n4 * (GRAD_6D[hash] * x4 + GRAD_6D[hash + 1] * y4 + GRAD_6D[hash + 2] * z4 +
                    GRAD_6D[hash + 3] * w4 + GRAD_6D[hash + 4] * u4 + GRAD_6D[hash + 5] * v4);
        }

        n5 = LIMIT6 - x5 * x5 - y5 * y5 - z5 * z5 - w5 * w5 - u5 * u5 - v5 * v5;
        if (n5 <= 0.0) n5 = 0.0;
        else
        {
            final int hash = hash256(i + i5, j + j5, k + k5, l + l5, h + h5, g + g5, seed) * 6;
            n5 *= n5;
            n5 *= n5 * (GRAD_6D[hash] * x5 + GRAD_6D[hash + 1] * y5 + GRAD_6D[hash + 2] * z5 +
                    GRAD_6D[hash + 3] * w5 + GRAD_6D[hash + 4] * u5 + GRAD_6D[hash + 5] * v5);
        }

        n6 = LIMIT6 - x6 * x6 - y6 * y6 - z6 * z6 - w6 * w6 - u6 * u6 - v6 * v6;
        if (n6 <= 0.0) n6 = 0.0;
        else
        {
            final int hash = hash256(i + 1, j + 1, k + 1, l + 1, h + 1, g + 1, seed) * 6;
            n6 *= n6;
            n6 *= n6 * (GRAD_6D[hash] * x6 + GRAD_6D[hash + 1] * y6 + GRAD_6D[hash + 2] * z6 +
                    GRAD_6D[hash + 3] * w6 + GRAD_6D[hash + 4] * u6 + GRAD_6D[hash + 5] * v6);
        }

        double ex = Math.exp((n0 + n1 + n2 + n3 + n4 + n5 + n6) * 25.0 * _harshness);
        double _v = (double) ((ex - 1.0) / (ex + 1.0));
        if(normalize) _v = (_v*.5)+.5;
        return _v;
    }
}
