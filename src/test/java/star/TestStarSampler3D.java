package star;

import com.github.terefang.randy.nfield.NoiseFieldUtil;
import com.github.terefang.randy.rng.impl.ArcRand;
import com.github.terefang.randy.sampler.RandomSampler3D;
import com.github.terefang.randy.starsys.model.SolarContext;
import com.github.terefang.randy.starsys.model.SystemContext;
import lombok.SneakyThrows;
import util.TestUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Vector;

public class TestStarSampler3D
{
    @SneakyThrows
    public static void main(String[] args)
    {
        PrintStream _ps = new PrintStream(new BufferedOutputStream(new FileOutputStream("./out/"+TestStarSampler3D.class.getSimpleName()+".txt"), 8192));
        ArcRand _rng = ArcRand.from("beef");
        RandomSampler3D _S = new RandomSampler3D();
        _S.setSeed("leet2");
        _S.setSize(16.,16.,16.);
        _S.setNum(2);


        double _d = .579;
        int _i = 1;
        double _w = .3/_d/_d/_d/_d/_d/_d/_d;
        List<double[]> _seeds = new Vector<>();
        int _x = 0;
        for(_x = 0; _x < _BIAS.length; _x++)
        {
            System.err.printf("W=%f\n", _w);
            _S.setW(_w);
            _S.init();

            if(_seeds.size()>0)
            {
                for(double[] _p : _seeds)
                {
                    _S.preseed(_p);
                }
            }

            List<double[]> _samples = _S.samples();
            for(double[] _p : _samples)
            {
                _seeds.add(_p);
                _ps.printf("(%f %f %f)\n", _p[0],_p[1],_p[2]);
                int _seed = ((int)_p[0]<<24)|((int)_p[1]<<16)|((int)_p[2]<<8);
                SolarContext _sol = SolarContext.randomType(_seed|_i, _BIAS[_x]);
                _sol.randomizeFromMk(_seed);
                SystemContext _sc = SystemContext.from(_seed, _i, _sol, false);
                _sc.outputInformation(_ps);
                _i++;
            }
            _w*=_d;
            _S.setNum(_S.getNum()<<1);
        }
        _ps.flush();
        _ps.close();
    }

    static double[] _BIAS = { -.9, -.6, -.3, 0., .3, .5, .7, .9 };

}
