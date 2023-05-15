import com.github.terefang.randy.rng.ArcRandom;
import com.github.terefang.randy.rng.IRandom;
import lombok.SneakyThrows;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class TestUtil {
    static int[] testRandInt(IRandom _rng)
    {
        int[] _ret = new int[256];
        for(int _i =0; _i< 0x7ffffff; _i++)
        {
            _ret[_rng.nextInt()&0xff]++;
            _ret[(_rng.nextInt()>>>8)&0xff]++;
            _ret[(_rng.nextInt()>>>16)&0xff]++;
            _ret[(_rng.nextInt()>>>24)&0xff]++;
        }
        return _ret;
    }

    static int[] testRandLong(IRandom _rng)
    {
        int[] _ret = new int[256];
        for(int _i =0; _i< 0x7ffffff; _i++)
        {
            _ret[(int) (_rng.nextLong()&0xff)]++;
            _ret[(int) ((_rng.nextLong()>>>8)&0xff)]++;
            _ret[(int) ((_rng.nextLong()>>>16)&0xff)]++;
            _ret[(int) ((_rng.nextLong()>>>24)&0xff)]++;
            _ret[(int) ((_rng.nextLong()>>>32)&0xff)]++;
            _ret[(int) ((_rng.nextLong()>>>40)&0xff)]++;
            _ret[(int) ((_rng.nextLong()>>>48)&0xff)]++;
            _ret[(int) ((_rng.nextLong()>>>56)&0xff)]++;
        }
        return _ret;
    }

    static int[] testRandFloat(IRandom _rng)
    {
        int[] _ret = new int[256];
        for(int _i =0; _i< 0x7ffffff; _i++)
        {
            _ret[(int) (Math.abs(_rng.nextFloat())*256)]++;
        }
        return _ret;
    }

    static int[] testRandDouble(IRandom _rng)
    {
        int[] _ret = new int[256];
        for(int _i =0; _i< 0x7ffffff; _i++)
        {
            _ret[(int) (Math.abs(_rng.nextDouble())*256)]++;
        }
        return _ret;
    }

    static int[] testRandGauss(IRandom _rng)
    {
        int[] _ret = new int[256];
        for(int _i =0; _i< 0x7ffffff; _i++)
        {
            _ret[(int) (128+(_rng.nextGaussian(2d,.5d,0d)*128))]++;
        }
        return _ret;
    }

    public static void testAll(IRandom _rng, String _name) {
        _rng.setSeed(0x1337b33f);
        TestUtil.print(TestUtil.testRandInt(_rng), _name+"-ints");
        _rng.setSeed(0x1337b33f);
        TestUtil.print(TestUtil.testRandLong(_rng), _name+"-longs");
        _rng.setSeed(0x1337b33f);
        TestUtil.print(TestUtil.testRandFloat(_rng), _name+"-floats");
        _rng.setSeed(0x1337b33f);
        TestUtil.print(TestUtil.testRandDouble(_rng), _name+"-doubles");
        _rng.setSeed(0x1337b33f);
        TestUtil.print(TestUtil.testRandGauss(_rng), _name+"-gauss");
    }

    @SneakyThrows
    static void print(int[] _test, String _name)
    {
        BufferedImage _bI = new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_ARGB);
        Graphics2D _g = _bI.createGraphics();
        _g.setColor(Color.BLACK);
        _g.fillRect(0,0,1023,1023);

        float _last= _test[0];
        float _min= _test[0];
        float _max= _test[0];
        for(int _i =1; _i<_test.length; _i++)
        {
            _last += _test[_i];
            _last /= 2f;
            if(_min>_test[_i]) _min=_test[_i];
            if(_max<_test[_i]) _max=_test[_i];
            //System.err.println(String.format("1=%d 2=%d l=%d d=%d",_test[_i-1], _test[_i],(int)_last,_test[_i-1]-_test[_i]));
        }

        if(_last<_max) _last=_max;

        _g.setColor(Color.WHITE);
        for(int _i =1; _i<_test.length; _i++)
        {
            int _that1=1023- (int) (_test[_i-1]*512f/_last);
            int _that2=1023-  (int) (_test[_i]*512f/_last);
            _g.drawLine((_i-1)*4, _that1, (_i)*4,_that2);
        }
        new File("./out/").mkdirs();
        ImageIO.write(_bI,"png", new File("./out", _name+".png"));
    }
}
