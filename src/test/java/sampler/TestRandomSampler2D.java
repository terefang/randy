package sampler;

import com.github.terefang.randy.nfield.NoiseFieldUtil;
import com.github.terefang.randy.sampler.RandomSampler2D;
import util.TestUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Vector;

public class TestRandomSampler2D
{
    static Color[] _COLORS = { Color.BLUE, new Color(128,128,255), Color.CYAN, Color.WHITE, Color.YELLOW, Color.ORANGE, Color.RED };
    public static void main(String[] args) {
        RandomSampler2D _S = new RandomSampler2D();
        _S.setSeed("leet2");
        _S.setStart(0.,0.);
        _S.setEnd(1023.,1023.);
        _S.setB(1024);
        _S.setNum(2);
        BufferedImage _bI = TestUtil.setupImage();
        Graphics2D _g = (Graphics2D) _bI.getGraphics();
        _g.setColor(Color.DARK_GRAY);
        for(int _i=1024/16; _i<1024; _i+=1024/16)
        {
            _g.drawLine(0,_i, 1024, _i);
            _g.drawLine(_i,0, _i, 1024);
        }
        int _i = 1;
        int _w = 758;
        List<double[]> _seeds = new Vector<>();
        for(Color _c : _COLORS)
        {
            _S.setW(_w);
            _S.init();
            _g.setColor(_c);

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
                _g.fillOval((int)_p[0]-2, (int)_p[1]-2, 4, 4);
                _seeds.add(_p);
                System.err.printf("(%06d %06d)\n", (int)_p[0], (int)_p[1]);
                //if(_i%10 == 0)
                    NoiseFieldUtil.savePNG(_bI, String.format("./out/sampler/TestRandomSampler2D-%06d.png", _i));
                _i++;
            }
            _w>>=1;
            _S.setNum(_S.getNum()<<1);
        }
        NoiseFieldUtil.savePNG(_bI, String.format("./out/sampler/TestRandomSampler2D-%06d.png", _i));


        _g.dispose();

    }

}
