package noise;

import com.github.terefang.randy.RandyUtil;
import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.NoiseUtil;
import util.TestUtil;

import java.util.Arrays;

public class TestPyramid {
    public static void main(String[] args)
    {
        for(INoise _rng : Arrays.asList(RandyUtil.pyramidNoise(0xd33dbee7, NoiseUtil.LINEAR),
                RandyUtil.pyramidNoise(0xd33dbee7, NoiseUtil.QUINTIC),
                RandyUtil.pyramidNoise(0xd33dbee7, NoiseUtil.HERMITE)))
        {
            TestUtil.testAll(_rng);
            TestUtil.test2d(_rng);
        }

    }
}
