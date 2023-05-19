package rng;

import com.github.terefang.randy.rng.GoldenRatio2Random;
import util.TestUtil;
public class TestGR2Rand {
    public static void main(String[] args) {
        TestUtil.testAll(new GoldenRatio2Random(), "golden2rand");
    }
}
