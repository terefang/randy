package com.github.terefang.randy.noise;

// Copyright (c) 2011, Giliam de Carpentier. All rights reserved.
// See http://www.decarpentier.nl/scape-procedural-basics/ for more info
//
// This file is licensed under the FreeBSD license:
//
// Redistribution and use in source and binary forms, with or without modification, are
// permitted provided that the following conditions are met:
//
//    1. Redistributions of source code must retain the above copyright notice, this list of
//       conditions and the following disclaimer.
//
//    2. Redistributions in binary form must reproduce the above copyright notice, this list
//       of conditions and the following disclaimer in the documentation and/or other materials
//       provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY GILIAM DE CARPENTIER 'AS IS' AND ANY EXPRESS OR IMPLIED
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
// FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL GILIAM DE CARPENTIER OR
// CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
// ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
// NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
// ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

// The following code can be used to generate the contents of the 256x256 R8G8B8A8
// permutation and gradient textures needed for Scape's perlinNoise() implementation.
// NOTE: Be sure to flip the image content vertically before using it in OpenGL.

public class GiliamDeCarpentierUtils {
    // ----------------------------------------------------------------------------
    // Original Ken Perlin's permutation table
    static int perlinPermTable[/*256*/]={151,160,137,91,90,15,
            131,13,201,95,96,53,194,233,7,225,140,36,103,30,69,142,8,99,37,240,21,10,23,
            190,6,148,247,120,234,75,0,26,197,62,94,252,219,203,117,35,11,32,57,177,33,
            88,237,149,56,87,174,20,125,136,171,168,68,175,74,165,71,134,139,48,27,166,
            77,146,158,231,83,111,229,122,60,211,133,230,220,105,92,41,55,46,245,40,244,
            102,143,54,65,25,63,161,255,216,80,73,209,76,132,187,208,89,18,169,200,196,
            135,130,116,188,159,86,164,100,109,198,173,186,3,64,52,217,226,250,124,123,
            5,202,38,147,118,126,255,82,85,212,207,206,59,227,47,16,58,17,182,189,28,42,
            223,183,170,213,119,248,152,2,44,154,163,70,221,153,101,155,167,43,172,9,
            129,22,39,253,19,98,108,110,79,113,224,232,178,185,112,104,218,246,97,228,
            251,34,242,193,238,210,144,12,191,179,162,241,81,51,145,235,249,14,239,107,
            49,192,214,31,181,199,106,157,184,84,204,176,115,121,50,45,127,4,150,254,
            138,236,205,93,222,114,67,29,24,72,243,141,128,195,78,66,215,61,156,180};

    // ----------------------------------------------------------------------------
    // 2D off-axis gradients table
    // (128*(1+cos(angle)), 128*(1+sin(angle)) pairs for
    // eight angles: 22.5, 67.5, 112.5, 157.5, 202.5, 247.5, 292.5, 337.5
    static int perlinGradTable[/*16*/] = {
            245, 176,
            176, 245,
            79, 245,
            10, 176,
            10, 79,
            79, 10,
            176, 10,
            245, 79};

    // ----------------------------------------------------------------------------
    public static int4[] createPerlinPerm2DTable()
    {
        int4[] perlinPerm2DTable = new int4[256*256];

        // create optimized noise permutation table
        // r = perm(perm(x    ) + (y))
        // g = perm(perm(x    ) + (y + 1))
        // b = perm(perm(x + 1) + (y))
        // a = perm(perm(x + 1) + (y + 1))


        for (int y = 0, _i = 0; y < 256; ++y)
        {
            for (int x = 0; x < 256; ++x)
            {
                int r = (perlinPermTable[ x      & 0xff] + y) & 0xff;
                int g = (perlinPermTable[ x      & 0xff] + (y+1)) & 0xff;
                int b = (perlinPermTable[(x + 1) & 0xff] + y) & 0xff;
                int a = (perlinPermTable[(x + 1) & 0xff] + (y+1)) & 0xff;
                perlinPerm2DTable[_i++] = int4.from(
                        perlinPermTable[r & 255],
                        perlinPermTable[g & 255],
                        perlinPermTable[b & 255],
                        perlinPermTable[a & 255]);
            }
        }
        return perlinPerm2DTable;
    }

    // ----------------------------------------------------------------------------
    public static double4[] createPerlinGrad2DTable()
    {
        double4[] perlinGrad2DTable = new double4[256*256];

        for (int y = 0, _i = 0; y < 256; ++y)
        {
            for (int x = 0; x < 256; ++x)
            {
                int px = perlinPermTable[x];
                int py = perlinPermTable[y];
                perlinGrad2DTable[_i++] =new double4(
                        (128.-perlinGradTable[((px & 7) << 1)])/128.,
                        (128.-perlinGradTable[((px & 7) << 1) + 1])/128.,
                        (128.-perlinGradTable[((py & 7) << 1)])/128.,
                        (128.-perlinGradTable[((py & 7) << 1) + 1])/128.);
            }
        }

        return perlinGrad2DTable;
    }

    private static int4[] samplerPerlinPerm2D;
    private static double4[] samplerPerlinGrad2D;

    public static final int4 lookupSamplerPerlinPerm2D(int _ix, int _iy, int seed)
    {
        if(samplerPerlinPerm2D==null)
        {
            samplerPerlinPerm2D = createPerlinPerm2DTable();
        }

        int4 _entry = samplerPerlinPerm2D[(((_iy&0xff)<<8)|(_ix&0xff))];
        return int4.from(_entry.x+seed,_entry.y+seed,_entry.z+seed,_entry.w+seed);
    }

    public static final double4 lookupSamplerPerlinGrad2D(int _ix, int _iy)
    {
        if(samplerPerlinGrad2D==null)
        {
            samplerPerlinGrad2D = createPerlinGrad2DTable();
        }
        return samplerPerlinGrad2D[((_ix&0xff)*256)+(_iy&0xff)];
    }

    // -------------------------------------------------
    // https://www.decarpentier.nl/scape-procedural-extensions

    public  static  double3 perlinNoisePseudoDeriv(double px, double py, int _seed)
    {
         // Calculate 2D integer coordinates i and fraction p.
        double2 p = double2.from(px, py);
        double2 i = double2.from( NoiseUtil.fastFloor(px), NoiseUtil.fastFloor(py));
        double2 f = p.sub(i);
        double2 j = i.add(1);
        double2 g = f.sub(1);

        {
            double a = NoiseUtil.gradCoord2D(_seed, (int)i.x, (int)i.y, f.x, f.y);
            double b = NoiseUtil.gradCoord2D(_seed, (int)j.x, (int)i.y, g.x, f.y);
            double c = NoiseUtil.gradCoord2D(_seed, (int)i.x, (int)j.y, f.x, g.y);
            double d = NoiseUtil.gradCoord2D(_seed, (int)j.x, (int)j.y, g.x, g.y);

            double2 w = f.mul(f).mul(f).mul(f.mul(f.mul(6).sub(15)).add(10));
            double4 grads = double4.from( a, b - a, c - a, a - b - c + d);
            double2 dw = f.mul(f).mul(f.mul(f.mul(30).sub(60)).add(30));
            double dx = dw.x * (grads.y + grads.w * w.y);
            double dy = dw.y * (grads.z + grads.w * w.x);

            return double3.from(
                    NoiseUtil.lerp(NoiseUtil.lerp(a,b,NoiseUtil.hermiteInterpolator(f.x)),NoiseUtil.lerp(c,d,NoiseUtil.hermiteInterpolator(f.x)) , NoiseUtil.hermiteInterpolator(f.y)),
                    dx,dy
            );
        }
        /*
        {
            // Get weights from the coordinate fraction
            double2 w = f.mul(f).mul(f).mul(f.mul(f.mul(6).sub(15)).add(10));
            double4 w4 = double4.from(1, w.x, w.y, w.x * w.y);

            // Get pseudo derivative weights
            double2 dw = f.mul(f).mul(f.mul(f.mul(30).sub(60)).add(30));

            // Get the four randomly permutated indices from the noise lattice nearest to
            // p and offset these numbers with the seed number.
            //double4 perm = tex2D(samplerPerlinPerm2D, i / 256) + seed;
            int4 perm = lookupSamplerPerlinPerm2D((int)i.x,(int)i.y,_seed);

            // Permutate the four offseted indices again and get the 2D gradient for each
            // of the four permutated coordinates-seed pairs.
            //float4 g1 = tex2D(samplerPerlinGrad2D, perm.xy) * 2 - 1;
            double4 g1 = lookupSamplerPerlinGrad2D(perm.x, perm.y);//.mul(2).sub(1);

            //float4 g2 = tex2D(samplerPerlinGrad2D, perm.zw) * 2 - 1;
            double4 g2 = lookupSamplerPerlinGrad2D(perm.z, perm.w);//.mul(2).sub(1);

            // Evaluate the four lattice gradients at p
            //float a = dot(g1.xy, f);
            double a = NoiseUtil.dot(g1.xy(), f);
            //float b = dot(g2.xy, f + float2(-1,  0));
            double b = NoiseUtil.dot(g2.xy(), f.add(double2.from(-1,0)));
            //float c = dot(g1.zw, f + float2( 0, -1));
            double c = NoiseUtil.dot(g1.zw().yx(), f.add(double2.from(0,-1)));
            //float d = dot(g2.zw, f + float2(-1, -1));
            double d = NoiseUtil.dot(g2.zw().yx(), f.add(double2.from(-1,-1)));

            // Bi-linearly blend between the gradients, using w4 as blend factors.
            //float4 grads = float4(a, b - a, c - a, a - b - c + d);
            double4 grads = double4.from( a, b - a, c - a, a - b - c + d);
            double n = NoiseUtil.dot(grads, w4);

            // Calculate pseudo derivates
            //float dx = dw.x * (grads.y + grads.w*w.y);
            double dx = dw.x * (grads.y + grads.w * w.y);
            //float dy = dw.y * (grads.z + grads.w*w.x);
            double dy = dw.y * (grads.z + grads.w * w.x);

            // Return the noise value, roughly normalized in the range [-1, 1]
            // Also return the pseudo dn/dx and dn/dy, scaled by the same factor
            //return double3.from(n, dx, dy).mul(1.5);
        }
        */
    }

    // -------------------------------------------------

    /* I. Quillez */
    public static double iQuillezTurbulence(double px, double py, int _seed, int octaves)
    {
        return iQuillezTurbulence(px,py,_seed,octaves, 2., .5);
    }
    public static double iQuillezTurbulence(double px, double py, int _seed, int octaves, double lacunarity, double gain)
    {
        double2 p = double2.from(px, py);
        double sum = 0.5;
        double freq = 1.0, amp = 1.0;
        double2 dsum = double2.from(0,0);
        for (int i=0; i < octaves; i++)
        {
            double2 pi = p.mul(freq);
            double3 n = perlinNoisePseudoDeriv(pi.x, pi.y, _seed + i);
            dsum = dsum.add(n.yz());
            sum += amp * n.x / (1 + NoiseUtil.dot(dsum, dsum));
            sum -= amp/4.;
            freq *= lacunarity;
            amp *= gain;
        }
        return sum;
    }

}
