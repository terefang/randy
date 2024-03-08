package noise;

import com.github.terefang.randy.noise.GiliamDeCarpentierUtils;
import com.github.terefang.randy.noise.NoiseUtil;
import com.github.terefang.randy.noise.double3;

public class TestPerlinNoisePseudoDeriv {
    public static void main(String[] args) {
        for(double _i = -1.; _i<=1.; _i+=0.1)
        {
            for(double _j = -1.; _j<=1.; _j+=0.1)
            {
                double3 _p = GiliamDeCarpentierUtils.perlinNoisePseudoDeriv(_i,_j, 0xd3adb3ef);
                System.out.printf("( %f , %f ) = %f ( %f , %f )\n",_i,_j,_p.x,_p.y,_p.z);
            }
        }

    }
}
