import com.github.terefang.randy.nfield.NoiseField;
import com.github.terefang.randy.nfield.NoiseFieldUtil;
import com.github.terefang.randy.rng.ArcRandom;
import com.github.terefang.randy.rng.impl.ArcRand;
import com.github.terefang.randy.sampler.RandomSampler2D;
import util.TestUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class TestVoronoiWalker {
    public static void main(String[] args)
    {
        ArcRand _rng = ArcRand.from("leet1");
        RandomSampler2D _S = new RandomSampler2D();
        _S.setSeed("seed2");
        _S.setStart(0.,0.);
        _S.setEnd(1023.,1023.);
        _S.setB(1024);
        _S.setNum(16);
        NoiseField _nf = new NoiseField(1024, 1024);
        _S.setW(1024>>4);
        _S.init();
        List<double[]> _samples = _S.samples();
        for(double[] _p : _samples)
        {
            _nf.setPoint((int)_p[0], (int)_p[1], 256.0);
        }

        int _x = 0;

        while(_samples.size()>0)
        {
            List<double[]> _add = new Vector<>();
            List<double[]> _rem = new Vector<>();

            Collections.shuffle(_samples);
            int _size = _samples.size()>>2;
            if(_size==0) _size = _samples.size();
            for(double[] _p : _samples.subList(0, _size))
            {
                double _v = _nf.getPoint((int)_p[0], (int)_p[1]);
                if(_v<1.) {
                    _rem.add(_p);
                    continue;
                }
                List<double[]> _tmp = new Vector<>();
                for(double[] _test: _xy)
                {
                    double[] _o = new double[] {(int)(_p[0]+_test[0]), (int)(_p[1]+_test[1]), _test[2]};
                    if(_o[0]<0) continue;
                    if(_o[1]<0) continue;
                    if(_o[0]>1023) continue;
                    if(_o[1]>1023) continue;
                    double _t = _nf.getPoint((int)_o[0],(int)_o[1]);
                    if(_t<(_v-2)) _tmp.add(_o);
                }
                if(_tmp.size()<=1) _rem.add(_p);
                if(_tmp.size()==0) continue;
                double[] _set = _tmp.get(_rng.nextInt(_tmp.size()));

                _nf.setPoint((int)_set[0], (int)_set[1], _v-(_rng.nextAbout(3f,1f)*_set[2]));
                _add.add(new double[] { _set[0], _set[1] } );
            }

            for(double[] _p : _rem)
            {
                _samples.remove(_p);
            }

            for(double[] _p : _add)
            {
                _samples.add(0,_p);
            }

            NoiseFieldUtil.saveHFEImage(_nf,0,256, 32,String.format("./out/sampler/TestVoronoiWalker-%d.png",_x));
            _x++;
            System.err.println(_x);
            System.err.println(_samples.size());
        }

    }

    public static double[][] _xy = {
            { -1, -1, 1.421},
            { +1, -1, 1.421},
            { -1, +1, 1.421},
            { +1, +1, 1.421},
            { -1, 0, 1},
            { +1, 0, 1},
            { 0, -1, 1},
            { 0, +1, 1},
    };
}

