package noise;

import com.github.terefang.randy.RandyUtil;
import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.NoiseUtil;
import util.TestUtil;

import java.util.Arrays;

public class TestBicubicWobble {
    public static void main(String[] args) {
        INoise _rng = RandyUtil.bicubicWobbleNoise(0x1ee7b33f);
        for(int _i : Arrays.asList(NoiseUtil.LINEAR, NoiseUtil.HERMITE, NoiseUtil.QUINTIC))
        {
            _rng.setInterpolation(_i);
            TestUtil.testAll(_rng, "bicubicWobble");
            TestUtil.test2d(_rng, "bicubicWobble");
        }
    }
}
