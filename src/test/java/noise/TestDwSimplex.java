package noise;

import com.github.terefang.randy.RandyUtil;
import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.NoiseUtil;
import util.TestUtil;

import java.util.Arrays;

public class TestDwSimplex {
    public static void main(String[] args)
    {
        for(INoise _rng : Arrays.asList(
                RandyUtil.dwxNoise(0x1ee7b33f, 1.5)
        ))
        {
            TestUtil.test2d(_rng,"dwxNoise");
        }
        for(INoise _rng : Arrays.asList(
                RandyUtil.dwxNoise(0x1ee7b33f, 1.5),
                RandyUtil.dwxNoise(0x1ee7b33f, 2.5),
                RandyUtil.dwxNoise(0x1ee7b33f, 3.5),
                RandyUtil.dwxNoise(0x1ee7b33f, 4.5),
                RandyUtil.dwxNoise(0x1ee7b33f, 5.5),
                RandyUtil.dwxNoise(0x1ee7b33f, 6.5)
        ))
        {
            TestUtil.testAll(_rng, "DWX="+_rng.getHarshness());
        }
    }
}
