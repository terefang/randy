package com.github.terefang.randy.noise.impl;

import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.NoiseUtil;

public class CellularMergeNoise extends NoiseUtil implements INoise
{
    public String name()
    {
        switch (this.getCellularDistanceFunction())
        {
            case NATURAL:
                return super.name()+"Natural";
            case MANHATTAN:
                return super.name()+"Manhattan";
            case EUCLIDEAN:
            default:
                return super.name()+"Euclidean";
        }
    }

    int cellularDistanceFunction = EUCLIDEAN;

    public int getCellularDistanceFunction() {
        return cellularDistanceFunction;
    }

    public void setCellularDistanceFunction(int cellularDistanceFunction) {
        this.cellularDistanceFunction = cellularDistanceFunction;
    }

    // ------------------------------------------------------------------------------------------------------------------


    @Override
    public double _noise1(long seed, double x, int interpolation) {
        return singleCellularMerge(false, this.getCellularDistanceFunction(), this.getSharpness(),makeSeedInt(seed), x, this.getMutation());
    }

    @Override
    public double _noise2(long seed, double x, double y, int interpolation) {
        return singleCellularMerge(false, this.getCellularDistanceFunction(), this.getSharpness(),makeSeedInt(seed), x, y+this.getMutation());
    }

    @Override
    public double _noise3(long seed, double x, double y, double z, int interpolation) {
        return singleCellularMerge(false, this.getCellularDistanceFunction(), this.getSharpness(),makeSeedInt(seed), x, y, z+this.getMutation());
    }

    @Override
    public double _noise4(long seed, double x, double y, double z, double u, int interpolation) {
        return singleCellularMerge(false, this.getCellularDistanceFunction(), this.getSharpness(),makeSeedInt(seed), x, y, z+this.getMutation());
    }

    @Override
    public double _noise5(long seed, double x, double y, double z, double u, double v, int interpolation) {
        return singleCellularMerge(false, this.getCellularDistanceFunction(), this.getSharpness(),makeSeedInt(seed), x, y, z+this.getMutation());
    }

    @Override
    public double _noise6(long seed, double x, double y, double z, double u, double v, double w, int interpolation) {
        return singleCellularMerge(false, this.getCellularDistanceFunction(), this.getSharpness(),makeSeedInt(seed), x, y, z+this.getMutation());
    }

    @Override
    public double _noise1n(long seed, double x, int interpolation) {
        return singleCellularMerge(true, this.getCellularDistanceFunction(), this.getSharpness(),makeSeedInt(seed), x, this.getMutation());
    }

    @Override
    public double _noise2n(long seed, double x, double y, int interpolation) {
        return singleCellularMerge(true, this.getCellularDistanceFunction(), this.getSharpness(),makeSeedInt(seed), x, y+this.getMutation());
    }

    @Override
    public double _noise3n(long seed, double x, double y, double z, int interpolation) {
        return singleCellularMerge(true, this.getCellularDistanceFunction(), this.getSharpness(),makeSeedInt(seed), x, y, z+this.getMutation());
    }

    @Override
    public double _noise4n(long seed, double x, double y, double z, double u, int interpolation) {
        return singleCellularMerge(true, this.getCellularDistanceFunction(), this.getSharpness(),makeSeedInt(seed), x, y, z+this.getMutation());
    }

    @Override
    public double _noise5n(long seed, double x, double y, double z, double u, double v, int interpolation) {
        return singleCellularMerge(true, this.getCellularDistanceFunction(), this.getSharpness(),makeSeedInt(seed), x, y, z+this.getMutation());
    }

    @Override
    public double _noise6n(long seed, double x, double y, double z, double u, double v, double w, int interpolation) {
        return singleCellularMerge(true, this.getCellularDistanceFunction(), this.getSharpness(),makeSeedInt(seed), x, y, z+this.getMutation());
    }

    // ------------------------------------------------------------------------------------------------------------------
    public static final double singleCellularMerge(boolean normalize, int cellularDistanceFunction, double _sharpness, int seed, double x, double y)
    {
        int xr = fastRound(x);
        int yr = fastRound(y);

        double sum = 0f;
        int hash;

        switch (cellularDistanceFunction)
        {
            default:
            case EUCLIDEAN:
                for (int xi = xr - 1; xi <= xr + 1; xi++) {
                    for (int yi = yr - 1; yi <= yr + 1; yi++) {
                        hash = hashAll(xi, yi, seed);
                        double2 vec = CELL_2D[hash & 255];

                        double vecX = xi - x + vec.x;
                        double vecY = yi - y + vec.y;

                        double distance = (_sharpness*1.) - (vecX * vecX + vecY * vecY);

                        if (distance > 0f) {
                            distance *= 3f;
                            sum += ((hash >>> 28) - (hash >>> 24 & 15)) * distance * distance * distance;
                        }
                    }
                }
                break;
            case MANHATTAN:
                for (int xi = xr - 1; xi <= xr + 1; xi++) {
                    for (int yi = yr - 1; yi <= yr + 1; yi++) {
                        hash = hashAll(xi, yi, seed);
                        double2 vec = CELL_2D[hash & 255];

                        double vecX = xi - x + vec.x;
                        double vecY = yi - y + vec.y;

                        double distance = (_sharpness*1.) -  (Math.abs(vecX) + Math.abs(vecY));

                        if (distance > 0f) {
                            distance *= 3f;
                            sum += ((hash >>> 28) - (hash >>> 24 & 15)) * distance * distance * distance;
                        }
                    }
                }
                break;
            case NATURAL:
                for (int xi = xr - 1; xi <= xr + 1; xi++) {
                    for (int yi = yr - 1; yi <= yr + 1; yi++) {
                        hash = hashAll(xi, yi, seed);
                        double2 vec = CELL_2D[hash & 255];

                        double vecX = xi - x + vec.x;
                        double vecY = yi - y + vec.y;

                        double distance = (_sharpness*2.) - ((Math.abs(vecX) + Math.abs(vecY)) + (vecX * vecX + vecY * vecY));

                        if (distance > 0f) {
                            distance *= 3f;
                            sum += ((hash >>> 28) - (hash >>> 24 & 15)) * distance * distance * distance * 0.125f;
                        }
                    }
                }
                break;
        }

        double _v = sum / (64f + Math.abs(sum));
        if(normalize) _v = (_v*.5)+.5;
        return _v;
    }

    public static final double singleCellularMerge(boolean normalize, int cellularDistanceFunction, double _sharpness, int seed, double x, double y, double z)
    {
        int xr = fastRound(x);
        int yr = fastRound(y);
        int zr = fastRound(z);

        double sum = 0f;
        int hash;

        switch (cellularDistanceFunction) {
            case EUCLIDEAN:
                for (int xi = xr - 1; xi <= xr + 1; xi++) {
                    for (int yi = yr - 1; yi <= yr + 1; yi++) {
                        for (int zi = zr - 1; zi <= zr + 1; zi++) {
                            hash = hashAll(xi, yi, zi, seed);
                            double3 vec = CELL_3D[hash & 255];

                            double vecX = xi - x + vec.x;
                            double vecY = yi - y + vec.y;
                            double vecZ = zi - z + vec.z;

                            double distance = (_sharpness*1.) - (vecX * vecX + vecY * vecY + vecZ * vecZ);

                            if (distance > 0.) {
                                distance *= 3.;
                                sum += ((hash >>> 28) - (hash >>> 24 & 15)) * distance * distance * distance;
                            }
                        }
                    }
                }
                break;
            case MANHATTAN:
                for (int xi = xr - 1; xi <= xr + 1; xi++) {
                    for (int yi = yr - 1; yi <= yr + 1; yi++) {
                        for (int zi = zr - 1; zi <= zr + 1; zi++) {
                            hash = hashAll(xi, yi, zi, seed);
                            double3 vec = CELL_3D[hash & 255];

                            double vecX = xi - x + vec.x;
                            double vecY = yi - y + vec.y;
                            double vecZ = zi - z + vec.z;

                            double distance = (_sharpness*1.) - (Math.abs(vecX) + Math.abs(vecY) + Math.abs(vecZ));

                            if (distance > 0.) {
                                distance *= 3.;
                                sum += ((hash >>> 28) - (hash >>> 24 & 15)) * distance * distance * distance;
                            }
                        }
                    }
                }
                break;
            case NATURAL:
                for (int xi = xr - 1; xi <= xr + 1; xi++) {
                    for (int yi = yr - 1; yi <= yr + 1; yi++) {
                        for (int zi = zr - 1; zi <= zr + 1; zi++) {
                            hash = hashAll(xi, yi, zi, seed);
                            double3 vec = CELL_3D[hash & 255];

                            double vecX = xi - x + vec.x;
                            double vecY = yi - y + vec.y;
                            double vecZ = zi - z + vec.z;

                            double distance = (_sharpness*2.) - ((Math.abs(vecX) + Math.abs(vecY) + Math.abs(vecZ)) + (vecX * vecX + vecY * vecY + vecZ * vecZ));

                            if (distance > 0.) {
                                distance *= 3.;
                                sum += ((hash >>> 28) - (hash >>> 24 & 15)) * distance * distance * distance * 0.125f;
                            }
                        }
                    }
                }
                break;
        }

        double _v = sum / (64f + Math.abs(sum));
        if(normalize) _v = (_v*.5)+.5;
        return _v;
    }
}
