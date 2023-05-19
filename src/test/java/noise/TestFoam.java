package noise;

import com.github.terefang.randy.RandyUtil;
import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.NoiseUtil;
import util.TestUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class TestFoam {
    public static void main(String[] args)
    {
        List<INoise> _types = new Vector<>();

        long _seed = 0x1ee7b33fL;

        Arrays.asList(
                RandyUtil.cellularNoise(_seed, NoiseUtil.EUCLIDEAN, NoiseUtil.DISTANCE),
                RandyUtil.cellularNoise(_seed, NoiseUtil.EUCLIDEAN, NoiseUtil.CELL_VALUE),
                RandyUtil.cellularNoise(_seed, NoiseUtil.EUCLIDEAN, NoiseUtil.NOISE_LOOKUP),
                RandyUtil.cellularNoise(_seed, NoiseUtil.NATURAL, NoiseUtil.DISTANCE),
                RandyUtil.cellularNoise(_seed, NoiseUtil.NATURAL, NoiseUtil.CELL_VALUE),
                RandyUtil.cellularNoise(_seed, NoiseUtil.NATURAL, NoiseUtil.NOISE_LOOKUP),
                RandyUtil.cellularNoise(_seed, NoiseUtil.MANHATTAN, NoiseUtil.DISTANCE),
                RandyUtil.cellularNoise(_seed, NoiseUtil.MANHATTAN, NoiseUtil.CELL_VALUE),
                RandyUtil.cellularNoise(_seed, NoiseUtil.MANHATTAN, NoiseUtil.NOISE_LOOKUP),
                RandyUtil.perlinNoise(_seed, NoiseUtil.QUINTIC),
                RandyUtil.perlinNoise(_seed, NoiseUtil.HERMITE),
                RandyUtil.perlinNoise(_seed, NoiseUtil.LINEAR),
                RandyUtil.simplexNoise(_seed),
                RandyUtil.cellMergeNoise(_seed, NoiseUtil.EUCLIDEAN, NoiseUtil.BASE_SHARPNESS*.8),
                RandyUtil.cellMergeNoise(_seed, NoiseUtil.MANHATTAN, NoiseUtil.BASE_SHARPNESS*.8),
                RandyUtil.cellMergeNoise(_seed, NoiseUtil.NATURAL, NoiseUtil.BASE_SHARPNESS*.8),
                RandyUtil.cubicNoise(_seed, NoiseUtil.BASE_HARSHNESS),
                RandyUtil.lumpNoise(_seed, NoiseUtil.BASE_HARSHNESS),
                RandyUtil.honeyNoise(_seed, NoiseUtil.QUINTIC),
                RandyUtil.honeyNoise(_seed, NoiseUtil.HERMITE),
                RandyUtil.honeyNoise(_seed, NoiseUtil.LINEAR),
                RandyUtil.solidNoise(_seed, NoiseUtil.QUINTIC),
                RandyUtil.solidNoise(_seed, NoiseUtil.HERMITE),
                RandyUtil.solidNoise(_seed, NoiseUtil.LINEAR),
                RandyUtil.valueNoise(_seed, NoiseUtil.QUINTIC),
                RandyUtil.valueNoise(_seed, NoiseUtil.HERMITE),
                RandyUtil.valueNoise(_seed, NoiseUtil.LINEAR)
        ).forEach((_t) -> { _types.add(RandyUtil.foamNoise(_t,-2L^_seed, NoiseUtil.BASE_SHARPNESS)); });
        for(INoise _rng : _types)
        {
            TestUtil.testAll(_rng);
            TestUtil.test2d(_rng);
        }
    }
}
