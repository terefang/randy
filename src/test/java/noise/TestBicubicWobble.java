package noise;

import com.github.terefang.randy.RandyUtil;
import util.TestUtil;

public class TestBicubicWobble {
    public static void main(String[] args) {
        TestUtil.testAll(RandyUtil.bicubicWobbleNoise(0x1ee7b33f), "bicubicWobble");
    }
}
