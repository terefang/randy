package com.github.terefang.randy;

import com.github.terefang.randy.fractal.BillowFractal;
import com.github.terefang.randy.fractal.IFractal;
import com.github.terefang.randy.fractal.MusgraveHeteroTerrainFractal;
import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.impl.*;
import com.github.terefang.randy.rng.*;

public class RandyUtil {
    public static IRandom arcRng(long _seed)
    {
        ArcRandom _rng = new ArcRandom();
        _rng.setSeed(_seed);
        return _rng;
    }

    public static IRandom javaRng(long _seed)
    {
        JavaRandom _rng = new JavaRandom();
        _rng.setSeed(_seed);
        return _rng;
    }

    public static IRandom blockRng(long _seed)
    {
        BlockRandom _rng = new BlockRandom();
        _rng.setSeed(_seed);
        return _rng;
    }

    public static IRandom mtRng(long _seed)
    {
        MTRandom _rng = new MTRandom();
        _rng.setSeed(_seed);
        return _rng;
    }

    public static IRandom goldenRng(long _seed)
    {
        GoldenRatioRandom _rng = new GoldenRatioRandom();
        _rng.setSeed(_seed);
        return _rng;
    }

    public static IRandom golden2Rng(long _seed)
    {
        GoldenRatio2Random _rng = new GoldenRatio2Random();
        _rng.setSeed(_seed);
        return _rng;
    }

    public static INoise perlinNoise(long _seed, int _i)
    {
        PerlinNoise _rng = new PerlinNoise();
        _rng.setSeed(_seed);
        _rng.setInterpolation(_i);
        return _rng;
    }

    public static INoise simplexNoise(long _seed)
    {
        SimplexNoise _rng = new SimplexNoise();
        _rng.setSeed(_seed);
        return _rng;
    }

    public static INoise valueNoise(long _seed, int _i)
    {
        ValueNoise _rng = new ValueNoise();
        _rng.setSeed(_seed);
        _rng.setInterpolation(_i);
        return _rng;
    }

    public static INoise solidNoise(long _seed, int _i)
    {
        SolidNoise _rng = new SolidNoise();
        _rng.setSeed(_seed);
        _rng.setInterpolation(_i);
        return _rng;
    }

    public static INoise bicubicWobbleNoise(long _seed)
    {
        BicubicWobbleNoise _rng = new BicubicWobbleNoise();
        _rng.setSeed(_seed);
        return _rng;
    }

    public static INoise whiteNoise(long _seed)
    {
        WhiteNoise _rng = new WhiteNoise();
        _rng.setSeed(_seed);
        return _rng;
    }
    public static INoise pyramidNoise(long _seed, int _i)
    {
        PyramidNoise _rng = new PyramidNoise();
        _rng.setSeed(_seed);
        _rng.setInterpolation(_i);
        return _rng;
    }

    public static INoise honeyNoise(long _seed, int _i)
    {
        HoneyNoise _rng = new HoneyNoise();
        _rng.setSeed(_seed);
        _rng.setInterpolation(_i);
        return _rng;
    }

    public static INoise lumpNoise(long _seed, double _harsh)
    {
        LumpNoise _rng = new LumpNoise();
        _rng.setSeed(_seed);
        _rng.setHarshness(_harsh);
        return _rng;
    }

    public static INoise foamNoise(INoise _type, long _seed, double _foamSharpness)
    {
        FoamNoise _rng = new FoamNoise();
        _rng.setSeed(_seed);
        _rng.setSharpness(_foamSharpness);
        _rng.setType(_type);
        return _rng;
    }

    public static INoise cellMergeNoise(long _seed, int _d, double _sharpness)
    {
        CellularMergeNoise _rng = new CellularMergeNoise();
        _rng.setSeed(_seed);
        _rng.setCellularDistanceFunction(_d);
        _rng.setSharpness(_sharpness);
        return _rng;
    }

    public static INoise cellularNoise(long _seed, int _d, int _r)
    {
        CellularNoise _rng = new CellularNoise();
        _rng.setSeed(_seed);
        _rng.setCellularDistanceFunction(_d);
        _rng.setCellularReturnType(_r);
        return _rng;
    }

    public static INoise cubicNoise(long _seed, double _harshness)
    {
        CubicNoise _rng = new CubicNoise();
        _rng.setSeed(_seed);
        _rng.setHarshness(_harshness);
        return _rng;
    }

    public static BillowFractal billowFractal(INoise _type, double _freq, double _H, double _lacu)
    {
        BillowFractal _rng = new BillowFractal();
        _rng.setNoise(_type);
        _rng.setFrequency(_freq);
        _rng.setH(_H);
        _rng.setLacunarity(_lacu);
        return _rng;
    }

    public static MusgraveHeteroTerrainFractal musgraveHeteroTerrainFractal(INoise _type, double _freq, double _H, double _lacu)
    {
        MusgraveHeteroTerrainFractal _rng = new MusgraveHeteroTerrainFractal();
        _rng.setNoise(_type);
        _rng.setFrequency(_freq);
        _rng.setH(_H);
        _rng.setLacunarity(_lacu);
        return _rng;
    }
}
