package sampler;

import com.github.terefang.randy.nfield.NoiseFieldUtil;
import com.github.terefang.randy.sampler.RandomSampler3D;
import util.TestUtil;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TestRandomSampler3D
{
    public static void main(String[] args) {
        RandomSampler3D _S = new RandomSampler3D();
        _S.setStart(0.,0.,0.);
        _S.setEnd(1023.,1023.,1023.);
        _S.setW(16.);
        _S.setNum(1<<10);
        BufferedImage _bI = TestUtil.setupImage();
        Graphics2D _g = (Graphics2D) _bI.getGraphics();
        _g.setColor(Color.YELLOW);

        int _i = 1;
        for(double[] _p : _S.samples())
        {
            _g.drawOval((int)(_p[0]-1), (int)(_p[1]-1), 2, 2);
            NoiseFieldUtil.savePNG(_bI, "./out/sampler/TestRandomSampler3D-"+_i+".png");
            _i++;
        }
        _g.dispose();

    }

}
