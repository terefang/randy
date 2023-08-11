package noise;

import com.github.terefang.randy.RandyUtil;
import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.NoiseUtil;
import util.TestUtil;

import java.util.Arrays;

public class TestCubic {
    public static void main(String[] args)
    {
        for(INoise _rng : Arrays.asList(RandyUtil.cubicNoise(0x1ee7b33f, NoiseUtil.BASE_HARSHNESS)))
        {
            TestUtil.testAll(_rng,"cubicNoise");
            TestUtil.test2d(_rng,"cubicNoise");
        }
    }
}
