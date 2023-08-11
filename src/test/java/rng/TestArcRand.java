package rng;

import com.github.terefang.randy.rng.ArcRandom;
import util.TestUtil;
public class TestArcRand {
    public static void main(String[] args) {
        TestUtil.testAll(new ArcRandom());
    }
}
