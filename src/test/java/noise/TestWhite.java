package noise;

import com.github.terefang.randy.RandyUtil;
import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.NoiseUtil;
import util.TestUtil;

public class TestWhite {
    public static void main(String[] args) {

        INoise _rng = RandyUtil.whiteNoise(0x1ee7b33f);
        TestUtil.testAll(_rng);
        TestUtil.test2d(_rng,"whiteNoise");
    }
}
