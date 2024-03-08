package util;

import com.github.terefang.randy.RandyUtil;
import com.github.terefang.randy.nfield.NoiseField;
import com.github.terefang.randy.nfield.NoiseFieldUtil;
import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.NoiseUtil;
import com.github.terefang.randy.rng.IRandom;
import com.github.terefang.randy.transf.ITransform;
import com.github.terefang.randy.transf.TypeTransform;
import com.github.terefang.randy.utils.BmpFont;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class TestCollatz {
    public static void main(String[] args)
    {
        for(int _j=1; _j<256; _j++)
        {
            BufferedImage _bi = new BufferedImage(1024,1024,BufferedImage.TYPE_3BYTE_BGR);
            Graphics2D _g = (Graphics2D) _bi.getGraphics();
            _g.setColor(Color.BLACK);
            _g.setStroke(new BasicStroke(1f));
            _g.drawLine(512, 0, 512,1024);
            _g.drawLine(0, 512, 1024,512);

            _g.setColor(Color.ORANGE);
            _g.setStroke(new BasicStroke(2f));
            double _last = NoiseUtil.collatzMutation(-1., 1024);
            for(int _i=1; _i<256; _i++)
            {
                double _f = NoiseUtil.collatzMutation((_i-128.)/128., 1024, _j);
                _g.drawLine((_i-1)*4, (int) (512-(_last*512.)), _i*4, (int) (512-(_f*512.)));
                _last = _f;
            }

            BmpFont _font = BmpFont.defaultInstance();
            _font.drawString(_g,10, 10,"collatzMutation (-1 , +1) steps="+_j, Color.YELLOW,Color.BLACK);
            _g.dispose();
            NoiseFieldUtil.savePNG(_bi, "./out/collatzMutation/collatzMutation."+_j+".png");
        }
    }

}
