package noise;

import com.github.terefang.randy.RandyUtil;
import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.NoiseUtil;
import util.TestUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class TestCellular {
    public static void main(String[] args)
    {
        long _seed = 0x1ee7b33fL;

        for(INoise _rng : Arrays.asList(
                RandyUtil.cellMergeNoise(_seed, NoiseUtil.EUCLIDEAN, NoiseUtil.BASE_SHARPNESS),
                RandyUtil.cellMergeNoise(_seed, NoiseUtil.MANHATTAN, NoiseUtil.BASE_SHARPNESS),
                RandyUtil.cellMergeNoise(_seed, NoiseUtil.NATURAL, NoiseUtil.BASE_SHARPNESS),


                RandyUtil.cellularNoise(_seed, NoiseUtil.EUCLIDEAN, NoiseUtil.DISTANCE_2),
                RandyUtil.cellularNoise(_seed, NoiseUtil.NATURAL, NoiseUtil.DISTANCE_2),
                RandyUtil.cellularNoise(_seed, NoiseUtil.MANHATTAN, NoiseUtil.DISTANCE_2),

                RandyUtil.cellularNoise(_seed, NoiseUtil.EUCLIDEAN, NoiseUtil.DISTANCE_2_ADD),
                RandyUtil.cellularNoise(_seed, NoiseUtil.NATURAL, NoiseUtil.DISTANCE_2_ADD),
                RandyUtil.cellularNoise(_seed, NoiseUtil.MANHATTAN, NoiseUtil.DISTANCE_2_ADD),

                RandyUtil.cellularNoise(_seed, NoiseUtil.EUCLIDEAN, NoiseUtil.DISTANCE_2_SUB),
                RandyUtil.cellularNoise(_seed, NoiseUtil.NATURAL, NoiseUtil.DISTANCE_2_SUB),
                RandyUtil.cellularNoise(_seed, NoiseUtil.MANHATTAN, NoiseUtil.DISTANCE_2_SUB),

                RandyUtil.cellularNoise(_seed, NoiseUtil.EUCLIDEAN, NoiseUtil.DISTANCE_2_MUL),
                RandyUtil.cellularNoise(_seed, NoiseUtil.NATURAL, NoiseUtil.DISTANCE_2_MUL),
                RandyUtil.cellularNoise(_seed, NoiseUtil.MANHATTAN, NoiseUtil.DISTANCE_2_MUL),

                RandyUtil.cellularNoise(_seed, NoiseUtil.EUCLIDEAN, NoiseUtil.DISTANCE_2_DIV),
                RandyUtil.cellularNoise(_seed, NoiseUtil.NATURAL, NoiseUtil.DISTANCE_2_DIV),
                RandyUtil.cellularNoise(_seed, NoiseUtil.MANHATTAN, NoiseUtil.DISTANCE_2_DIV),

                RandyUtil.cellularNoise(_seed, NoiseUtil.EUCLIDEAN, NoiseUtil.DISTANCE),
                RandyUtil.cellularNoise(_seed, NoiseUtil.EUCLIDEAN, NoiseUtil.CELL_VALUE),
                RandyUtil.cellularNoise(_seed, NoiseUtil.EUCLIDEAN, NoiseUtil.NOISE_LOOKUP),
                RandyUtil.cellularNoise(_seed, NoiseUtil.NATURAL, NoiseUtil.DISTANCE),
                RandyUtil.cellularNoise(_seed, NoiseUtil.NATURAL, NoiseUtil.CELL_VALUE),
                RandyUtil.cellularNoise(_seed, NoiseUtil.NATURAL, NoiseUtil.NOISE_LOOKUP),
                RandyUtil.cellularNoise(_seed, NoiseUtil.MANHATTAN, NoiseUtil.DISTANCE),
                RandyUtil.cellularNoise(_seed, NoiseUtil.MANHATTAN, NoiseUtil.CELL_VALUE),
                RandyUtil.cellularNoise(_seed, NoiseUtil.MANHATTAN, NoiseUtil.NOISE_LOOKUP)
        ))
        {
            TestUtil.testAll(_rng);
            TestUtil.test2d(_rng,"cellularNoise");
        }
    }
}
