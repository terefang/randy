package noise;

import com.github.terefang.randy.RandyUtil;
import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.NoiseUtil;
import util.TestUtil;

import java.util.Arrays;

public class TestPerlin {
    public static void main(String[] args)
    {
        for(INoise _rng : Arrays.asList(
                RandyUtil.perlinNoise(0x1ee7b33f, NoiseUtil.RADIAN),
                //RandyUtil.perlinNoise(0x1ee7b33f, NoiseUtil.QUINTIC),
                //RandyUtil.perlinNoise(0x1ee7b33f, NoiseUtil.HERMITE),
                //RandyUtil.perlinNoise(0x1ee7b33f, NoiseUtil.COSINE),
                RandyUtil.perlinNoise(0x1ee7b33f, NoiseUtil.LINEAR)
                ))
        {
            TestUtil.testAll(_rng);
            TestUtil.test2d(_rng,"perlinNoise");
        }
    }
}
