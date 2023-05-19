package noise;

import com.github.terefang.randy.RandyUtil;
import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.NoiseUtil;
import util.TestUtil;

import java.util.Arrays;

public class TestValue {
    public static void main(String[] args) {
        for(INoise _rng : Arrays.asList(RandyUtil.valueNoise(0x1ee7b33f, NoiseUtil.LINEAR),
            RandyUtil.valueNoise(0x1ee7b33f, NoiseUtil.HERMITE),
            RandyUtil.valueNoise(0x1ee7b33f, NoiseUtil.QUINTIC)))
        {
            TestUtil.testAll(_rng);
            TestUtil.test2d(_rng);
        }
    }
}
