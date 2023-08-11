package rng;

import com.github.terefang.randy.rng.MTRandom;
import util.TestUtil;
public class TestMTRand {
    public static void main(String[] args) {
        TestUtil.testAll(new MTRandom());
    }
}
