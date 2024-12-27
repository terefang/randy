package noise;

import com.github.terefang.randy.RandyUtil;
import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.NoiseUtil;
import util.TestUtil;

import java.util.Arrays;

public class TestPerlue {
    public static void main(String[] args)
    {
        for(INoise _rng : Arrays.asList(
                RandyUtil.perlueNoise(0x1ee7b33f, NoiseUtil.RADIAN),
                //RandyUtil.perlueNoise(0x1ee7b33f, NoiseUtil.QUINTIC),
                //RandyUtil.perlueNoise(0x1ee7b33f, NoiseUtil.HERMITE),
                //RandyUtil.perlueNoise(0x1ee7b33f, NoiseUtil.COSINE),
                RandyUtil.perlueNoise(0x1ee7b33f, NoiseUtil.LINEAR)))
        {
            TestUtil.testAll(_rng);
            TestUtil.test2d(_rng,"perlueNoise");
        }
    }
}
