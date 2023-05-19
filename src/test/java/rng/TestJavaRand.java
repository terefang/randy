package rng;

import com.github.terefang.randy.rng.JavaRandom;
import util.TestUtil;
public class TestJavaRand {
    public static void main(String[] args) {
        TestUtil.testAll(new JavaRandom(), "javarand");
    }
}
