import com.github.terefang.randy.rng.ArcRandom;
import com.github.terefang.randy.rng.GoldenRatioRandom;

public class TestGRRand {
    public static void main(String[] args) {
        TestUtil.testAll(new GoldenRatioRandom(), "goldenrand");
    }
}
