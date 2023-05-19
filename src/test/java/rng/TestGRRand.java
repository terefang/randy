package rng;

import com.github.terefang.randy.rng.GoldenRatioRandom;
import util.TestUtil;
public class TestGRRand {
    public static void main(String[] args) {
        TestUtil.testAll(new GoldenRatioRandom(), "goldenrand");
    }
}
