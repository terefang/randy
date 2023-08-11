package sampler;

import com.github.terefang.randy.nfield.NoiseFieldUtil;
import com.github.terefang.randy.rng.impl.ArcRand;
import com.github.terefang.randy.sampler.RandomSampler3D;
import util.TestUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Vector;

public class TestRandomSampler3D
{
    public static void main(String[] args) {
        ArcRand _rng = ArcRand.from("beef");
        RandomSampler3D _S = new RandomSampler3D();
        _S.setSeed("leet2");
        _S.setSize(16.,16.,16.);
        _S.setNum(2);
        BufferedImage _bI = TestUtil.setupImage();
        Graphics2D _g = (Graphics2D) _bI.getGraphics();
        _g.setColor(Color.DARK_GRAY);
        for(int _i=1024/16; _i<1024; _i+=1024/16)
        {
            _g.drawLine(0,_i, 1024, _i);
            _g.drawLine(_i,0, _i, 1024);
        }
        _g.setColor(Color.YELLOW);

        double _d = .579;
        int _i = 1;
        double _w = .3/_d/_d/_d/_d/_d/_d/_d;
        List<double[]> _seeds = new Vector<>();
        int _x = 0;
        for(_x = 0; _x < _COLORS.length; _x++)
        {
            System.err.printf("W=%f\n", _w);
            _S.setW(_w);
            _S.init();
            _g.setColor(_COLORS[_x]);

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
                int _y = _rng.nextRange(_x/2, (_x/2)+3);
                int _z = 7-_y;
                _g.fillOval((int)(_p[0]*64)-_z, (int)(_p[1]*64)-_z, 2*_z, 2*_z);
                _seeds.add(_p);
                System.err.printf("(%03d %03d %03d) - %s0%s\n", ((int)(_p[0]*34)), ((int)(_p[1]*34)), ((int)(_p[2]*34)), _MK[_x], _GS[_y]);
                //if(_i%10 == 0)
                NoiseFieldUtil.savePNG(_bI, String.format("./out/sampler/TestRandomSampler3D-%06d.png", _i));
                _i++;
            }
            _w*=_d;
            _S.setNum(_S.getNum()<<1);
        }
        NoiseFieldUtil.savePNG(_bI, String.format("./out/sampler/TestRandomSampler3D-%06d.png", _i));


        _g.dispose();

    }

    static String[] _MK = { "O","B","A","F","G","K","M","R"};
    static String[] _GS = { "I","II","III","IV","V","VI","VII","VII"};
    static Color[] _COLORS = { Color.BLUE, new Color(128,128,255), Color.CYAN, Color.WHITE, Color.YELLOW, Color.ORANGE, Color.RED, Color.RED.darker().darker() };


}
