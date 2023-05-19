package rng;

import com.github.terefang.randy.rng.BlockRandom;
import util.TestUtil;
public class TestBlockRand {
    public static void main(String[] args) {
        TestUtil.testAll(new BlockRandom(), "blockrand");
    }
}
