package nfield;

import com.github.terefang.randy.RandyUtil;
import com.github.terefang.randy.nfield.NoiseField;
import com.github.terefang.randy.nfield.NoiseFieldUtil;
import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.transf.ITransform;
import com.github.terefang.randy.transf.TypeTransform;
import lombok.SneakyThrows;
import util.TestUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class TestLongLatCyclicNoise
{
    @SneakyThrows
    public static void main(String[] args)
    {
        NoiseField _nf = new NoiseField(1024, 1024);

        INoise _noise = RandyUtil.cyclicNoise(0x2, 5, 8.0);
        _noise.setTransform(TypeTransform.from(ITransform.TransformType.T_LEVEL8));

        _nf.setProjection(-1,-1, 1,1, -1);
        _nf.applyNoise(_noise);

        _nf.normalize(0, 1);

        _nf.applyFunction((nf,_x,_y,_v)->{
            double dx = 1-(Math.abs(_x-512.)/512.);
            double dy = 1-(Math.abs(_y-512.)/512.);
            //double d = dx < dy ? dx : dy;
            double d = dx*dy;
            return _v*d*d-(1-d);
        });

        _nf.normalize(-1, 1);

        NoiseFieldUtil.saveHFEImage(_nf,"./out/nfield/CyclicNoiseCube/CyclicNoiseCube.png");
    }

}
