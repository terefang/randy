import com.github.terefang.randy.rng.MTRandom;

public class TestMTRand {
    public static void main(String[] args) {
        TestUtil.testAll(new MTRandom(), "mtrand");
    }
}
