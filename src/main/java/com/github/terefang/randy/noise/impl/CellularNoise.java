package com.github.terefang.randy.noise.impl;

import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.NoiseUtil;

public class CellularNoise extends NoiseUtil implements INoise
{
    public String name()
    {
        switch (this.getCellularDistanceFunction())
        {
            case NATURAL:
                return super.name()+"Natural"+this.rname();
            case MANHATTAN:
                return super.name()+"Manhattan"+this.rname();
            case EUCLIDEAN:
            default:
                return super.name()+"Euclidean"+this.rname();
        }
    }
    public String rname()
    {
        switch (this.getCellularReturnType())
        {
            case DISTANCE_2:
                return "2EdgeDistance2";
            case DISTANCE_2_ADD:
                return "2EdgeDistance2Add";
            case DISTANCE_2_SUB:
                return "2EdgeDistance2Sub";
            case DISTANCE_2_MUL:
                return "2EdgeDistance2Mul";
            case DISTANCE_2_DIV:
                return "2EdgeDistance2Div";
            case NOISE_LOOKUP:
                return "NoiseLookup";
            case DISTANCE:
                return "Distance";
            case CELL_VALUE:
            default:
                return "CellValue";
        }
    }

    int cellularReturnType = CELL_VALUE;

    public int getCellularReturnType() {
        return cellularReturnType;
    }

    public void setCellularReturnType(int cellularReturnType) {
        this.cellularReturnType = cellularReturnType;
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
        return singleCellular(this.getCellularDistanceFunction(), this.getCellularReturnType(), makeSeedInt(seed), x, this.getMutation());
    }

    @Override
    public double _noise2(long seed, double x, double y, int interpolation) {
        return singleCellular(this.getCellularDistanceFunction(), this.getCellularReturnType(), makeSeedInt(seed), x, y+this.getMutation());
    }

    @Override
    public double _noise3(long seed, double x, double y, double z, int interpolation) {
        return singleCellular(this.getCellularDistanceFunction(), this.getCellularReturnType(), makeSeedInt(seed), x, y, z+this.getMutation());
    }

    @Override
    public double _noise4(long seed, double x, double y, double z, double u, int interpolation) {
        return singleCellular(this.getCellularDistanceFunction(), this.getCellularReturnType(), makeSeedInt(seed), x, y, z+this.getMutation());
    }

    @Override
    public double _noise5(long seed, double x, double y, double z, double u, double v, int interpolation) {
        return singleCellular(this.getCellularDistanceFunction(), this.getCellularReturnType(), makeSeedInt(seed), x, y, z+this.getMutation());
    }

    @Override
    public double _noise6(long seed, double x, double y, double z, double u, double v, double w, int interpolation) {
        return singleCellular(this.getCellularDistanceFunction(), this.getCellularReturnType(), makeSeedInt(seed), x, y, z+this.getMutation());
    }

    @Override
    public double _noise1n(long seed, double x, int interpolation) {
        return singleCellularN(this.getCellularDistanceFunction(), this.getCellularReturnType(), makeSeedInt(seed), x, this.getMutation());
    }

    @Override
    public double _noise2n(long seed, double x, double y, int interpolation) {
        return singleCellularN(this.getCellularDistanceFunction(), this.getCellularReturnType(), makeSeedInt(seed), x, y+this.getMutation());
    }

    @Override
    public double _noise3n(long seed, double x, double y, double z, int interpolation) {
        return singleCellularN(this.getCellularDistanceFunction(), this.getCellularReturnType(), makeSeedInt(seed), x, y, z+this.getMutation());
    }

    @Override
    public double _noise4n(long seed, double x, double y, double z, double u, int interpolation) {
        return singleCellularN(this.getCellularDistanceFunction(), this.getCellularReturnType(), makeSeedInt(seed), x, y, z+this.getMutation());
    }

    @Override
    public double _noise5n(long seed, double x, double y, double z, double u, double v, int interpolation) {
        return singleCellularN(this.getCellularDistanceFunction(), this.getCellularReturnType(), makeSeedInt(seed), x, y, z+this.getMutation());
    }

    @Override
    public double _noise6n(long seed, double x, double y, double z, double u, double v, double w, int interpolation) {
        return singleCellularN(this.getCellularDistanceFunction(), this.getCellularReturnType(), makeSeedInt(seed), x, y, z+this.getMutation());
    }

    // ------------------------------------------------------------------------------------------------------------------

    public static final double singleCellular(int cellularDistanceFunction, int cellularReturnType, int seed, double x, double y)
    {
        int xr = fastRound(x);
        int yr = fastRound(y);

        double distance = 999999;
        double distance2 = 999999;
        int xc = 0, yc = 0;

        switch (cellularDistanceFunction) {
            default:
            case EUCLIDEAN:
                for (int xi = xr - 1; xi <= xr + 1; xi++) {
                    for (int yi = yr - 1; yi <= yr + 1; yi++) {
                        double2 vec = CELL_2D[hash256(xi, yi, seed)];

                        double vecX = xi - x + vec.x;
                        double vecY = yi - y + vec.y;

                        double newDistance = vecX * vecX + vecY * vecY;

                        distance2 = Math.max(Math.min(distance2, newDistance), distance);
                        if (newDistance < distance) {
                            distance = newDistance;
                            xc = xi;
                            yc = yi;
                        }
                    }
                }
                break;
            case MANHATTAN:
                for (int xi = xr - 1; xi <= xr + 1; xi++) {
                    for (int yi = yr - 1; yi <= yr + 1; yi++) {
                        double2 vec = CELL_2D[hash256(xi, yi, seed)];

                        double vecX = xi - x + vec.x;
                        double vecY = yi - y + vec.y;

                        double newDistance = (Math.abs(vecX) + Math.abs(vecY));

                        distance2 = Math.max(Math.min(distance2, newDistance), distance);
                        if (newDistance < distance) {
                            distance = newDistance;
                            xc = xi;
                            yc = yi;
                        }
                    }
                }
                break;
            case NATURAL:
                for (int xi = xr - 1; xi <= xr + 1; xi++) {
                    for (int yi = yr - 1; yi <= yr + 1; yi++) {
                        double2 vec = CELL_2D[hash256(xi, yi, seed)];

                        double vecX = xi - x + vec.x;
                        double vecY = yi - y + vec.y;

                        double newDistance = (Math.abs(vecX) + Math.abs(vecY)) + (vecX * vecX + vecY * vecY);

                        distance2 = Math.max(Math.min(distance2, newDistance), distance);
                        if (newDistance < distance) {
                            distance = newDistance;
                            xc = xi;
                            yc = yi;
                        }
                    }
                }
                break;
        }

        switch (cellularReturnType) {
            case DISTANCE_2:
                return distance2 - 1;
            case DISTANCE_2_ADD:
                return distance2 + distance - 1;
            case DISTANCE_2_SUB:
                return distance2 - distance - 1;
            case DISTANCE_2_MUL:
                return distance2 * distance - 1;
            case DISTANCE_2_DIV:
                return distance / distance2 - 1;
            case CELL_VALUE:
                return valCoord2D(0, xc, yc);

            case NOISE_LOOKUP:
                double2 vec = CELL_2D[hash256(xc, yc, seed)];
                return SimplexNoise.singleSimplex(false, seed+123,xc + vec.x, yc + vec.y);

            case DISTANCE:
                return distance - 1;
            default:
                return 0;
        }
    }

    public static final double singleCellular(int cellularDistanceFunction, int cellularReturnType, int seed, double x, double y, double z)
    {
        int xr = fastRound(x);
        int yr = fastRound(y);
        int zr = fastRound(z);

        double distance = 999999;
        double distance2 = 999999;
        int xc = 0, yc = 0, zc = 0;

        switch (cellularDistanceFunction) {
            case EUCLIDEAN:
                for (int xi = xr - 1; xi <= xr + 1; xi++) {
                    for (int yi = yr - 1; yi <= yr + 1; yi++) {
                        for (int zi = zr - 1; zi <= zr + 1; zi++) {
                            double3 vec = CELL_3D[hash256(xi, yi, zi, seed)];

                            double vecX = xi - x + vec.x;
                            double vecY = yi - y + vec.y;
                            double vecZ = zi - z + vec.z;

                            double newDistance = vecX * vecX + vecY * vecY + vecZ * vecZ;

                            distance2 = Math.max(Math.min(distance2, newDistance), distance);
                            if (newDistance < distance) {
                                distance = newDistance;
                                xc = xi;
                                yc = yi;
                                zc = zi;
                            }
                        }
                    }
                }
                break;
            case MANHATTAN:
                for (int xi = xr - 1; xi <= xr + 1; xi++) {
                    for (int yi = yr - 1; yi <= yr + 1; yi++) {
                        for (int zi = zr - 1; zi <= zr + 1; zi++) {
                            double3 vec = CELL_3D[hash256(xi, yi, zi, seed)];

                            double vecX = xi - x + vec.x;
                            double vecY = yi - y + vec.y;
                            double vecZ = zi - z + vec.z;

                            double newDistance = Math.abs(vecX) + Math.abs(vecY) + Math.abs(vecZ);

                            distance2 = Math.max(Math.min(distance2, newDistance), distance);
                            if (newDistance < distance) {
                                distance = newDistance;
                                xc = xi;
                                yc = yi;
                                zc = zi;
                            }
                        }
                    }
                }
                break;
            case NATURAL:
                for (int xi = xr - 1; xi <= xr + 1; xi++) {
                    for (int yi = yr - 1; yi <= yr + 1; yi++) {
                        for (int zi = zr - 1; zi <= zr + 1; zi++) {
                            double3 vec = CELL_3D[hash256(xi, yi, zi, seed)];

                            double vecX = xi - x + vec.x;
                            double vecY = yi - y + vec.y;
                            double vecZ = zi - z + vec.z;

                            double newDistance = (Math.abs(vecX) + Math.abs(vecY) + Math.abs(vecZ)) + (vecX * vecX + vecY * vecY + vecZ * vecZ);

                            distance2 = Math.max(Math.min(distance2, newDistance), distance);
                            if (newDistance < distance) {
                                distance = newDistance;
                                xc = xi;
                                yc = yi;
                                zc = zi;
                            }
                        }
                    }
                }
                break;
        }

        switch (cellularReturnType) {
            case DISTANCE_2:
                return distance2 - 1;
            case DISTANCE_2_ADD:
                return distance2 + distance - 1;
            case DISTANCE_2_SUB:
                return distance2 - distance - 1;
            case DISTANCE_2_MUL:
                return distance2 * distance - 1;
            case DISTANCE_2_DIV:
                return distance / distance2 - 1;
            case CELL_VALUE:
                return valCoord3D(0, xc, yc, zc);

            case NOISE_LOOKUP:
                double3 vec = CELL_3D[hash256(xc, yc, zc, seed)];
                return SimplexNoise.singleSimplex(false, seed+123,xc + vec.x, yc + vec.y, zc + vec.z);

            case DISTANCE:
                return distance - 1;
            default:
                return 0;
        }
    }

    public static final double singleCellularN(int cellularDistanceFunction, int cellularReturnType, int seed, double x, double y)
    {
        return (singleCellular(cellularDistanceFunction, cellularReturnType, seed, x, y)*.5)+.5;
    }

    public static final double singleCellularN(int cellularDistanceFunction, int cellularReturnType, int seed, double x, double y, double z)
    {
        return (singleCellular(cellularDistanceFunction, cellularReturnType, seed, x, y, z)*.5)+.5;
    }

}
