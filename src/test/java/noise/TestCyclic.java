package noise;

import com.github.terefang.randy.RandyUtil;
import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.NoiseUtil;
import util.TestUtil;

import java.util.Arrays;

public class TestCyclic {
    public static void main(String[] args)
    {
        for(int _oct : new int[] { 2,4,8,16 }) {
            for (INoise _rng : Arrays.asList(RandyUtil.cyclicNoise(0xBEEF1E57, _oct, 0.2))) {
                TestUtil.testAll(_rng, "cyclicNoise" + _oct);
            }
        }
        for(int _oct : new int[] { 2,4,8,16 })
        {
            for(INoise _rng : Arrays.asList(RandyUtil.cyclicNoise(0xBEEF1E57, _oct, 0.2)))
            {
                TestUtil.test2d(_rng,"cyclicNoise"+_oct);
            }
        }
    }
}
