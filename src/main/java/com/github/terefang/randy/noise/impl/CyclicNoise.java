package com.github.terefang.randy.noise.impl;

import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.NoiseUtil;
import com.github.terefang.randy.utils.RotationSupport;

import java.util.Arrays;

public class CyclicNoise extends NoiseUtil implements INoise
{

 
    @Override
    public double _noise1(long seed, double x, int interpolation) {
        this.setup();
        return singleCyclicNoise(interpolation, false, makeSeedInt(seed), this.start, this.frequency, this. total, this.octaves, this.rotations, this.inputs, this.outputs, x, this.getMutation());
    }

    @Override
    public double _noise2(long seed, double x, double y, int interpolation) {
        this.setup();
        return singleCyclicNoise(interpolation, false, makeSeedInt(seed), this.start, this.frequency, this. total, this.octaves, this.rotations, this.inputs, this.outputs, x, y);
    }

    @Override
    public double _noise3(long seed, double x, double y, double z, int interpolation) {
        this.setup();
        return singleCyclicNoise(interpolation, false, makeSeedInt(seed), this.start, this.frequency, this. total, this.octaves, this.rotations, this.inputs, this.outputs, x, y);
    }

    @Override
    public double _noise4(long seed, double x, double y, double z, double u, int interpolation) {
        this.setup();
        return singleCyclicNoise(interpolation, false, makeSeedInt(seed), this.start, this.frequency, this. total, this.octaves, this.rotations, this.inputs, this.outputs, x, y);
    }

    @Override
    public double _noise5(long seed, double x, double y, double z, double u, double v, int interpolation) {
        this.setup();
        return singleCyclicNoise(interpolation, false, makeSeedInt(seed), this.start, this.frequency, this. total, this.octaves, this.rotations, this.inputs, this.outputs, x, y);
    }

    @Override
    public double _noise6(long seed, double x, double y, double z, double u, double v, double w, int interpolation) {
        this.setup();
        return singleCyclicNoise(interpolation, false, makeSeedInt(seed), this.start, this.frequency, this. total, this.octaves, this.rotations, this.inputs, this.outputs, x, y);
    }

    @Override
    public double _noise1n(long seed, double x, int interpolation) {
        this.setup();
        return singleCyclicNoise(interpolation, false, makeSeedInt(seed), this.start, this.frequency, this. total, this.octaves, this.rotations, this.inputs, this.outputs, x, this.getMutation());
    }

    @Override
    public double _noise2n(long seed, double x, double y, int interpolation) {
        this.setup();
        return singleCyclicNoise(interpolation, false, makeSeedInt(seed), this.start, this.frequency, this. total, this.octaves, this.rotations, this.inputs, this.outputs, x, y);
    }

    @Override
    public double _noise3n(long seed, double x, double y, double z, int interpolation) {
        this.setup();
        return singleCyclicNoise(interpolation, false, makeSeedInt(seed), this.start, this.frequency, this. total, this.octaves, this.rotations, this.inputs, this.outputs, x, y);
    }

    @Override
    public double _noise4n(long seed, double x, double y, double z, double u, int interpolation) {
        this.setup();
        return singleCyclicNoise(interpolation, false, makeSeedInt(seed), this.start, this.frequency, this. total, this.octaves, this.rotations, this.inputs, this.outputs, x, y);
    }

    @Override
    public double _noise5n(long seed, double x, double y, double z, double u, double v, int interpolation) {
        this.setup();
        return singleCyclicNoise(interpolation, false, makeSeedInt(seed), this.start, this.frequency, this. total, this.octaves, this.rotations, this.inputs, this.outputs, x, y);
    }

    @Override
    public double _noise6n(long seed, double x, double y, double z, double u, double v, double w, int interpolation) {
        this.setup();
        return singleCyclicNoise(interpolation, false, makeSeedInt(seed), this.start, this.frequency, this. total, this.octaves, this.rotations, this.inputs, this.outputs, x, y);
    }


    // ----------------------------------------------------------------------------
    protected static final double LACUNARITY = 1.6f;
    protected static final double GAIN = 0.625f;

    protected int octaves;
    protected double total = 1f;
    protected double start = 1f;
    protected double frequency = 2f;

    boolean setup = false;

    protected transient double[][][] rotations = new double[6][4][];
    protected transient double[][] inputs = new double[][]{new double[2], new double[3], new double[4], new double[5], new double[6], new double[7]};
    protected transient double[][] outputs = new double[][]{new double[2], new double[3], new double[4], new double[5], new double[6], new double[7]};
    protected transient double[] gauss = new double[7], house = new double[49], large = new double[49], temp = new double[49];

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public int getOctaves() {
        return octaves;
    }

    public void setOctaves(int octaves) {
        this.octaves = Math.max(1, octaves);

        start = GAIN;
        total = 0f;
        for (int i = 0; i < this.octaves; i++) {
            start /= GAIN;
            total += start;
        }
        total = 1f / total;
    }

    public void setup()
    {
        if(!this.setup)
        {
            this.setOctaves(this.octaves);
            for (int i = 0, s = 2; i < 6; i++, s++) {
                for (int j = 0; j < 4; j++) {
                    rotations[i][j] = new double[s * s];
                }
            }
            this.setSeed(this.getSeed(), this.frequency);
        }
        this.setup=true;
    }

    /**
     * Sets the seed, and in doing so edits 24 rotation matrices for different dimensions to use. Note that this
     * may be considerably more expensive than a typical setter, because all matrices are set whenever the seed changes.
     * Also sets the frequency; the default is 2.
     * @param seed any long
     * @param frequency a multiplier that will apply to all coordinates; higher changes faster, lower changes slower
     */
    public void setSeed(long seed, double frequency) {
        this.setSeed(seed);
        this.frequency = frequency;
        for (int i = 0; i < 4; i++) {
            seed = this.getSeed() ^ i;
            RotationSupport.fillRandomRotation2D(seed, rotations[0][i]);
            System.arraycopy(RotationSupport.rotateStep(seed, rotations[0][i], 3, gauss, house, large, temp), 0, rotations[1][i], 0, 9);
            System.arraycopy(RotationSupport.rotateStep(seed, rotations[1][i], 4, gauss, house, large, temp), 0, rotations[2][i], 0, 16);
            System.arraycopy(RotationSupport.rotateStep(seed, rotations[2][i], 5, gauss, house, large, temp), 0, rotations[3][i], 0, 25);
            System.arraycopy(RotationSupport.rotateStep(seed, rotations[3][i], 6, gauss, house, large, temp), 0, rotations[4][i], 0, 36);
            System.arraycopy(RotationSupport.rotateStep(seed, rotations[4][i], 7, gauss, house, large, temp), 0, rotations[5][i], 0, 49);
        }
    }

    // 2d perlin
    public static final double singleCyclicNoise(int interpolation, boolean normalize, int seed,  double start, double frequency, double total, int octaves,
                                                 double[][][] rotations, double[][] inputs, double[][] outputs, double x, double y)
    {
        double noise = 0f;

        double amp = start;

        final double warp = 0.3f;
        double warpTrk = 1.2f;
        final double warpTrkGain = 1.5f;

        x *= frequency;
        y *= frequency;

        double xx, yy;
        for (int i = 0; i < octaves; i++) {
            xx = sin((x-2) * warpTrk) * warp;
            yy = sin((y-2) * warpTrk) * warp;

            inputs[0][0] = x + yy;
            inputs[0][1] = y + xx;
            Arrays.fill(outputs[0], 0f);
            RotationSupport.rotate(inputs[0], rotations[0][i & 3], outputs[0]);
            xx = outputs[0][0];
            yy = outputs[0][1];

            noise += sin((
                            cos(xx) * sin(yy) + cos(yy) * sin(xx)
                    ) * (Math.PI/2f)
            ) * amp;

            x = xx * LACUNARITY;
            y = yy * LACUNARITY;

            warpTrk *= warpTrkGain;
            amp *= GAIN;
        }
        return noise * total;
    }

    public static final double singleCyclicNoise(int interpolation, boolean normalize, int seed,  double start, double frequency, double total, int octaves,
                                                 double[][][] rotations, double[][] inputs, double[][] outputs, double x, double y, double z)
    {
        double noise = 0f;

        double amp = start;

        final double warp = 0.3f;
        double warpTrk = 1.2f;
        final double warpTrkGain = 1.5f;

        x *= frequency;
        y *= frequency;
        z *= frequency;

        double xx, yy, zz;
        for (int i = 0; i < octaves; i++) {
            xx = sin((x-2) * warpTrk) * warp;
            yy = sin((y-2) * warpTrk) * warp;
            zz = sin((z-2) * warpTrk) * warp;

            inputs[1][0] = x + zz;
            inputs[1][1] = y + xx;
            inputs[1][2] = z + yy;
            Arrays.fill(outputs[1], 0f);
            RotationSupport.rotate(inputs[1], rotations[1][i & 3], outputs[1]);
            xx = outputs[1][0];
            yy = outputs[1][1];
            zz = outputs[1][2];

            noise += sin((
                            cos(xx) * sin(zz) +
                                    cos(yy) * sin(xx) +
                                    cos(zz) * sin(yy)
                    ) * (Math.PI/3f)
            ) * amp;

            x = xx * LACUNARITY;
            y = yy * LACUNARITY;
            z = zz * LACUNARITY;

            warpTrk *= warpTrkGain;
            amp *= GAIN;
        }
        return noise * total;
    }
}
