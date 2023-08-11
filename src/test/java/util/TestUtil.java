package util;

import com.github.terefang.randy.RandyUtil;
import com.github.terefang.randy.fractal.IFractal;
import com.github.terefang.randy.fractal.MusgraveHeteroTerrainFractal;
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

public class TestUtil {
    static int _ITERATIONS = 0x7ffffff;
    static int _ITERATIONS1 = 0x7ff;
    static int _ITERATIONS2 = 0x7ffff - _ITERATIONS1;
    static int _ITERATIONS3 = 0x7ffffff - _ITERATIONS2;

    static int[] testRandInt(IRandom _rng, int _it)
    {
        int[] _ret = new int[256];
        testRandInt(_rng, _ret, _it);
        return _ret;
    }

    static void testRandInt(IRandom _rng, int[] _ret, int _it)
    {
        for(int _i =0; _i<_it ; _i++)
        {
            _ret[_rng.nextInt()&0xff]++;
            _ret[(_rng.nextInt()>>>8)&0xff]++;
            _ret[(_rng.nextInt()>>>16)&0xff]++;
            _ret[(_rng.nextInt()>>>24)&0xff]++;
        }
    }

    static int[] testRandLong(IRandom _rng, int _it)
    {
        int[] _ret = new int[256];
        for(int _i =0; _i<_it ; _i++)
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

    static int[] testRandFloat(IRandom _rng, int _it)
    {
        int[] _ret = new int[256];
        for(int _i =0; _i<_it ; _i++)
        {
            _ret[(int) (Math.abs(_rng.nextFloat())*256)]++;
        }
        return _ret;
    }

    static int[] testRandDouble(IRandom _rng, int _it)
    {
        int[] _ret = new int[256];
        for(int _i =0; _i<_it ; _i++)
        {
            _ret[(int) (Math.abs(_rng.nextDouble())*256)]++;
        }
        return _ret;
    }

    static int[] testRandGauss(IRandom _rng, int _it)
    {
        int[] _ret = new int[256];
        for(int _i =0; _i<_it ; _i++)
        {
            _ret[(int) (128+(_rng.nextGaussian(2d,.5d,0d)*128))]++;
        }
        return _ret;
    }

    @SneakyThrows
    public static void testAll(IRandom _rng)
    {
        BmpFont _font = BmpFont.defaultInstance();

        String _name = _rng.name();
        new File("./out/rand/").mkdirs();

        BufferedImage _bI = setupImage();
        _rng.setSeed(0x1337b33f);
        TestUtil.print(TestUtil.testRandGauss(_rng, _ITERATIONS1), Color.RED, _bI);
        TestUtil.print(TestUtil.testRandGauss(_rng, _ITERATIONS2), Color.YELLOW, _bI);
        TestUtil.print(TestUtil.testRandGauss(_rng, _ITERATIONS3), Color.WHITE, _bI);
        {
            Graphics2D _g = (Graphics2D) _bI.getGraphics();
            _font.drawString(_g,10, 10,"T = "+_rng.name(), Color.YELLOW,Color.BLACK);
            _font.drawString(_g,10, 30,"F = nextGaussian", Color.YELLOW,Color.BLACK);
            _font.drawString(_g,10, 50,"SAMPLE1 = "+_ITERATIONS1, Color.RED,Color.BLACK);
            _font.drawString(_g,10, 70,"SAMPLE2 = "+_ITERATIONS2, Color.YELLOW,Color.BLACK);
            _font.drawString(_g,10, 90,"SAMPLE3 = "+_ITERATIONS3, Color.WHITE,Color.BLACK);
            _g.dispose();
        }
        ImageIO.write(_bI,"png", new File("./out/rand/", _name+"-gauss.png"));

        _bI = setupImage();
        _rng.setSeed(0x1337b33f);
        TestUtil.print(TestUtil.testRandInt(_rng, _ITERATIONS1), Color.RED, _bI);
        TestUtil.print(TestUtil.testRandInt(_rng, _ITERATIONS2), Color.YELLOW, _bI);
        TestUtil.print(TestUtil.testRandInt(_rng, _ITERATIONS3), Color.WHITE, _bI);
        {
            Graphics2D _g = (Graphics2D) _bI.getGraphics();
            _font.drawString(_g,10, 10,"T = "+_rng.name(), Color.YELLOW,Color.BLACK);
            _font.drawString(_g,10, 30,"F = nextInt", Color.YELLOW,Color.BLACK);
            _font.drawString(_g,10, 50,"SAMPLE1 = "+_ITERATIONS1, Color.RED,Color.BLACK);
            _font.drawString(_g,10, 70,"SAMPLE2 = "+_ITERATIONS2, Color.YELLOW,Color.BLACK);
            _font.drawString(_g,10, 90,"SAMPLE3 = "+_ITERATIONS3, Color.WHITE,Color.BLACK);
            _g.dispose();
        }
        ImageIO.write(_bI,"png", new File("./out/rand/", _name+"-ints.png"));

        _bI = setupImage();
        _rng.setSeed(0x1337b33f);
        TestUtil.print(TestUtil.testRandLong(_rng, _ITERATIONS1), Color.RED, _bI);
        TestUtil.print(TestUtil.testRandLong(_rng, _ITERATIONS2), Color.YELLOW, _bI);
        TestUtil.print(TestUtil.testRandLong(_rng, _ITERATIONS3), Color.WHITE, _bI);
        {
            Graphics2D _g = (Graphics2D) _bI.getGraphics();
            _font.drawString(_g,10, 10,"T = "+_rng.name(), Color.YELLOW,Color.BLACK);
            _font.drawString(_g,10, 30,"F = nextLong", Color.YELLOW,Color.BLACK);
            _font.drawString(_g,10, 50,"SAMPLE1 = "+_ITERATIONS1, Color.RED,Color.BLACK);
            _font.drawString(_g,10, 70,"SAMPLE2 = "+_ITERATIONS2, Color.YELLOW,Color.BLACK);
            _font.drawString(_g,10, 90,"SAMPLE3 = "+_ITERATIONS3, Color.WHITE,Color.BLACK);
            _g.dispose();
        }
        ImageIO.write(_bI,"png", new File("./out/rand/", _name+"-longs.png"));

        _bI = setupImage();
        _rng.setSeed(0x1337b33f);
        TestUtil.print(TestUtil.testRandDouble(_rng, _ITERATIONS1), Color.RED, _bI);
        TestUtil.print(TestUtil.testRandDouble(_rng, _ITERATIONS2), Color.YELLOW, _bI);
        TestUtil.print(TestUtil.testRandDouble(_rng, _ITERATIONS3), Color.WHITE, _bI);
        {
            Graphics2D _g = (Graphics2D) _bI.getGraphics();
            _font.drawString(_g,10, 10,"T = "+_rng.name(), Color.YELLOW,Color.BLACK);
            _font.drawString(_g,10, 30,"F = nextDouble", Color.YELLOW,Color.BLACK);
            _font.drawString(_g,10, 50,"SAMPLE1 = "+_ITERATIONS1, Color.RED,Color.BLACK);
            _font.drawString(_g,10, 70,"SAMPLE2 = "+_ITERATIONS2, Color.YELLOW,Color.BLACK);
            _font.drawString(_g,10, 90,"SAMPLE3 = "+_ITERATIONS3, Color.WHITE,Color.BLACK);
            _g.dispose();
        }
        ImageIO.write(_bI,"png", new File("./out/rand/", _name+"-doubles.png"));

        _bI = setupImage();
        _rng.setSeed(0x1337b33f);
        TestUtil.print(TestUtil.testRandFloat(_rng, _ITERATIONS1), Color.RED, _bI);
        TestUtil.print(TestUtil.testRandFloat(_rng, _ITERATIONS2), Color.YELLOW, _bI);
        TestUtil.print(TestUtil.testRandFloat(_rng, _ITERATIONS3), Color.WHITE, _bI);
        {
            Graphics2D _g = (Graphics2D) _bI.getGraphics();
            _font.drawString(_g,10, 10,"T = "+_rng.name(), Color.YELLOW,Color.BLACK);
            _font.drawString(_g,10, 30,"F = nextFloat", Color.YELLOW,Color.BLACK);
            _font.drawString(_g,10, 50,"SAMPLE1 = "+_ITERATIONS1, Color.RED,Color.BLACK);
            _font.drawString(_g,10, 70,"SAMPLE2 = "+_ITERATIONS2, Color.YELLOW,Color.BLACK);
            _font.drawString(_g,10, 90,"SAMPLE3 = "+_ITERATIONS3, Color.WHITE,Color.BLACK);
            _g.dispose();
        }
        ImageIO.write(_bI,"png", new File("./out/rand/", _name+"-floats.png"));
    }

    public static BufferedImage setupImage()
    {
        BufferedImage _bI = new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_ARGB);
        Graphics2D _g = _bI.createGraphics();
        _g.setColor(Color.BLACK);
        _g.fillRect(0,0,1023,1023);
        _g.dispose();
        return _bI;
    }

    @SneakyThrows
    public static void print(int[] _test, Color _col, BufferedImage _bI)
    {
        Graphics2D _g = _bI.createGraphics();

        float _lastn= 0;
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
        if(0>_min)
        {
            _last=_last-_min;
            _lastn=_min;
        }

        _g.setColor(_col);
        for(int _i =1; _i<_test.length; _i++)
        {
            int _that1=1023- (int) ((_test[_i-1]-_lastn)*768f/_last);
            int _that2=1023-  (int) ((_test[_i]-_lastn)*768f/_last);
            _g.drawLine((_i-1)*4, _that1, (_i)*4,_that2);
        }
        _g.dispose();
    }

    @SneakyThrows
    static double print(double[] _test, Color _col, BufferedImage _bI)
    {
        Graphics2D _g = _bI.createGraphics();

        double _lastn= 0;
        double _last= _test[0];
        double _min= _test[0];
        double _max= _test[0];
        for(int _i =1; _i<_test.length; _i++)
        {
            _last += _test[_i];
            _last /= 2f;
            if(_min>_test[_i]) _min=_test[_i];
            if(_max<_test[_i]) _max=_test[_i];
            //System.err.println(String.format("1=%d 2=%d l=%d d=%d",_test[_i-1], _test[_i],(int)_last,_test[_i-1]-_test[_i]));
        }

        if(_last<_max) _last=_max;
        if(0>_min)
        {
            _last=_last-_min;
            _lastn=_min;
        }

        _g.setColor(_col);
        for(int _i =1; _i<_test.length; _i++)
        {
            int _that1= (int) (1023- ((_test[_i-1]-_lastn)*768d/_last));
            int _that2= (int) (1023-  ((_test[_i]-_lastn)*768d/_last));
            _g.drawLine((_i-1)*4, _that1, (_i)*4,_that2);
        }
        _g.dispose();

        return Math.log1p(Math.abs(_max-_min))*10.;
    }

    static double[] testNoise1(INoise _rng, double _freq)
    {
        double[] _ret = new double[256];
        for(int _i =0; _i<256 ; _i++)
        {
            double _x = ((-128d+((double)_i))/256d)*_freq;
            _ret[_i]= (_rng.noise1(_x));
            //System.err.println(String.format("1=%f 2=%f",_x, _ret[_i]));
        }
        return _ret;
    }

    @SneakyThrows
    public static void testAll(INoise _rng) {
        testAll(_rng, _rng.name());
    }

    @SneakyThrows
    public static void testAll(INoise _rng, String _name) {
        new File("./out/noise/").mkdirs();

        BufferedImage _bI = setupImage();
        double[] __freqs = { 1.234, 5.678, 12.34, 56.78};
        double[] _cM = new double[__freqs.length];
        for(int _k = 0; _k< __freqs.length; _k++)
        {
            double _min = Double.MAX_VALUE;
            double _max = Double.MIN_VALUE;
            int _kx = (_k % 2) * (_SIZE>>1);
            int _ky = (_k / 2) * (_SIZE>>1);
            for (int _y = 0; _y < (_SIZE>>1); _y++)
            {
                for (int _x = 0; _x < (_SIZE>>1); _x++)
                {
                    double _fx = (((double) _x) / ((double) (_SIZE>>1))) - .5f;
                    double _fy = (((double) _y) / ((double) (_SIZE>>1))) - .5f;
                    float _value = (float) ((_rng.noise2(_fx*__freqs[_k],_fy*__freqs[_k]) + 1.) * .5);
                    Color _c = Color.BLACK;
                    if(_value<0.f)
                    {
                        _c = Color.CYAN;
                    }
                    else
                    if(_value>1.f)
                    {
                        _c = Color.MAGENTA;
                    }
                    else
                    {
                        _c = new Color(_value, _value, _value);
                    }
                    _bI.setRGB(_kx+_x,_ky+_y,_c.getRGB());
                    if(_max<_value) _max=_value;
                    if(_min>_value) _min=_value;
                }
            }
            _cM[_k] = Math.log10(Math.abs(_max-_min))*100.;
        }
        //TestUtil.print(TestUtil.testNoise1(_rng, 1.234), Color.WHITE, _bI);
        //TestUtil.print(TestUtil.testNoise1(_rng, 5.678), Color.YELLOW, _bI);
        //TestUtil.print(TestUtil.testNoise1(_rng, 12.34), Color.ORANGE, _bI);
        //TestUtil.print(TestUtil.testNoise1(_rng, 56.78), Color.RED, _bI);

        Graphics2D _g = (Graphics2D) _bI.getGraphics();
        BmpFont _font = BmpFont.defaultInstance();
        _font.drawString(_g,10, 10,"T = "+_rng.name()+String.format(" : Seed = 0x%08X", _rng.getSeed()), Color.YELLOW,Color.BLACK);
        _font.drawString(_g,10, 30,String.format("FQ = 1.234 | cM = %.2f", _cM[0]), Color.WHITE,Color.BLACK);
        _font.drawString(_g,10, 50,String.format("FQ = 5.678 | cM = %.2f", _cM[1]), Color.YELLOW,Color.BLACK);
        _font.drawString(_g,10, 70,String.format("FQ = 12.34 | cM = %.2f", _cM[2]), Color.ORANGE,Color.BLACK);
        _font.drawString(_g,10, 90,String.format("FQ = 56.78 | cM = %.2f", _cM[3]), Color.RED,Color.BLACK);
        _g.dispose();

        NoiseFieldUtil.savePNG(_bI, "./out/noise/"+_name+"-n1.png");
    }

    public static int _SIZE = 1024;
    //public static int[] _OCT = { 1,2,4,6,8,10,12 };
    public static int[] _OCT = { 4,8 };
    //public static float[] _FREQ = { 1.23f, 2.34f, 3.45f, 4.56f, 5.67f, 6.78f };
    public static float[] _FREQ = { 3.45f, 6.78f, 12.5f, 24.6f, 48.7f, 96.8f };
    //public static float[] _H = { .5f, .77f, 1.f, 1.23f, 1.5f };
    public static float[] _H = { .25f, .5f };
    public static void test2d_field(INoise _type, NoiseField _nf, float _freq) {
        for (int _y = 0; _y < _nf.getHeight(); _y++)
        {
            for (int _x = 0; _x < _nf.getWidth(); _x++)
            {
                float _fx = (((float) _x) / ((float) _nf.getWidth())) - .5f;
                float _fy = (((float) _y) / ((float) _nf.getHeight())) - .5f;
                double _value = _type.noise2(_fx*_freq,_fy*_freq);
                _nf.setPoint(_x, _y, _value);
            }
        }
    }

    public static void test2d_image(INoise _type, NoiseField _nf, float _freq, String _suffix, String _category, String _name)
    {
        BufferedImage _bi = NoiseFieldUtil.getHFEImage(_nf, -1., 1.);
        Graphics2D _g = (Graphics2D) _bi.getGraphics();
        BmpFont _font = BmpFont.defaultInstance();
        _font.drawString(_g,10, 10,"T = "+_type.name()+_suffix+String.format(" : Seed = 0x%08X", _type.getSeed()), Color.YELLOW,Color.BLACK);
        _font.drawString(_g,10, 30,"FREQ = "+_freq, Color.YELLOW,Color.BLACK);

        int[] _histo = new int[256];
        int _max = 0;
        for(double _v : _nf.getData())
        {
            int _t = (int) (128+ (_v*128));
            if(_t<0 || _t>255) {
                //IGNORE
            }
            else {
                _histo[_t]++;
                if(_max<_histo[_t]) _max = _histo[_t];
            }
        }

        _g.setColor(Color.MAGENTA);
        _g.setStroke(new BasicStroke(2f));
        for(int _i =1; _i<256; _i++)
        {
            int _that1= (int) (1023- ((_histo[_i-1])*768d/_max));
            int _that2= (int) (1023-  ((_histo[_i])*768d/_max));
            _g.drawLine((_i-1)*4, _that1, (_i)*4,_that2);
        }

        _g.dispose();
        NoiseFieldUtil.savePNG(_bi, "./out/noise/" + _category +"/"+_name + ".png");
    }

    @SneakyThrows
    public static void test2d(INoise _type, String _category) {
        for(final float _freq : _FREQ)
        {
            for(final ITransform.TransformType _ttrans : ITransform.TransformType.values()) {
                _type.setTransform(TypeTransform.from(_ttrans));
                String _name = String.format("~xy-2d~fq=%04d", (int) (_freq * 10));
                System.err.println("-START " + _name);
                NoiseField _nf = new NoiseField(_SIZE, _SIZE);
                test2d_field(_type, _nf, _freq);

                test2d_image(_type, _nf, _freq, "", _category, _type.name()+_name);

                System.err.println("-END " + _name);
            }
        }
    }

    public static List<INoise> setupNoises(long _seed)
    {
        List<INoise> _types = new Vector<>();
        _types.addAll(setupBaseNoises(_seed));
        setupBaseNoises(_seed).forEach((_t) -> { _types.add(RandyUtil.foamNoise(_t,-2L^_seed, NoiseUtil.BASE_SHARPNESS)); });
        new Vector<INoise>(_types).forEach((_t) -> { _types.add(RandyUtil.dwxNoise(_seed, NoiseUtil.BASE_HARSHNESS, _t)); });
        _types.add(RandyUtil.dwxNoise(_seed, NoiseUtil.BASE_HARSHNESS));
        return _types;
    }
    public static List<INoise> setupBaseNoises(long _seed)
    {
        return Arrays.asList(
                RandyUtil.perlinNoise(_seed, NoiseUtil.QUINTIC),
                RandyUtil.perlinNoise(_seed, NoiseUtil.HERMITE),
                RandyUtil.perlinNoise(_seed, NoiseUtil.LINEAR),
                RandyUtil.simplexNoise(_seed),
                RandyUtil.cellMergeNoise(_seed, NoiseUtil.EUCLIDEAN, NoiseUtil.BASE_SHARPNESS*.8),
                RandyUtil.cellMergeNoise(_seed, NoiseUtil.MANHATTAN, NoiseUtil.BASE_SHARPNESS*.8),
                RandyUtil.cellMergeNoise(_seed, NoiseUtil.NATURAL, NoiseUtil.BASE_SHARPNESS*.8),

                RandyUtil.cellularNoise(_seed, NoiseUtil.EUCLIDEAN, NoiseUtil.DISTANCE_2),
                RandyUtil.cellularNoise(_seed, NoiseUtil.NATURAL, NoiseUtil.DISTANCE_2),
                RandyUtil.cellularNoise(_seed, NoiseUtil.MANHATTAN, NoiseUtil.DISTANCE_2),

                RandyUtil.cellularNoise(_seed, NoiseUtil.EUCLIDEAN, NoiseUtil.DISTANCE_2_ADD),
                RandyUtil.cellularNoise(_seed, NoiseUtil.NATURAL, NoiseUtil.DISTANCE_2_ADD),
                RandyUtil.cellularNoise(_seed, NoiseUtil.MANHATTAN, NoiseUtil.DISTANCE_2_ADD),

                RandyUtil.cellularNoise(_seed, NoiseUtil.EUCLIDEAN, NoiseUtil.DISTANCE_2_SUB),
                RandyUtil.cellularNoise(_seed, NoiseUtil.NATURAL, NoiseUtil.DISTANCE_2_SUB),
                RandyUtil.cellularNoise(_seed, NoiseUtil.MANHATTAN, NoiseUtil.DISTANCE_2_SUB),

                RandyUtil.cellularNoise(_seed, NoiseUtil.EUCLIDEAN, NoiseUtil.DISTANCE_2_MUL),
                RandyUtil.cellularNoise(_seed, NoiseUtil.NATURAL, NoiseUtil.DISTANCE_2_MUL),
                RandyUtil.cellularNoise(_seed, NoiseUtil.MANHATTAN, NoiseUtil.DISTANCE_2_MUL),

                RandyUtil.cellularNoise(_seed, NoiseUtil.EUCLIDEAN, NoiseUtil.DISTANCE_2_DIV),
                RandyUtil.cellularNoise(_seed, NoiseUtil.NATURAL, NoiseUtil.DISTANCE_2_DIV),
                RandyUtil.cellularNoise(_seed, NoiseUtil.MANHATTAN, NoiseUtil.DISTANCE_2_DIV),

                RandyUtil.cellularNoise(_seed, NoiseUtil.EUCLIDEAN, NoiseUtil.DISTANCE),
                RandyUtil.cellularNoise(_seed, NoiseUtil.EUCLIDEAN, NoiseUtil.CELL_VALUE),
                RandyUtil.cellularNoise(_seed, NoiseUtil.EUCLIDEAN, NoiseUtil.NOISE_LOOKUP),
                RandyUtil.cellularNoise(_seed, NoiseUtil.NATURAL, NoiseUtil.DISTANCE),
                RandyUtil.cellularNoise(_seed, NoiseUtil.NATURAL, NoiseUtil.CELL_VALUE),
                RandyUtil.cellularNoise(_seed, NoiseUtil.NATURAL, NoiseUtil.NOISE_LOOKUP),
                RandyUtil.cellularNoise(_seed, NoiseUtil.MANHATTAN, NoiseUtil.DISTANCE),
                RandyUtil.cellularNoise(_seed, NoiseUtil.MANHATTAN, NoiseUtil.CELL_VALUE),
                RandyUtil.cellularNoise(_seed, NoiseUtil.MANHATTAN, NoiseUtil.NOISE_LOOKUP),

                RandyUtil.cubicNoise(_seed, NoiseUtil.BASE_HARSHNESS),
                RandyUtil.lumpNoise(_seed, NoiseUtil.BASE_HARSHNESS),
                RandyUtil.honeyNoise(_seed, NoiseUtil.QUINTIC),
                RandyUtil.honeyNoise(_seed, NoiseUtil.HERMITE),
                RandyUtil.honeyNoise(_seed, NoiseUtil.LINEAR),
                RandyUtil.solidNoise(_seed, NoiseUtil.QUINTIC),
                RandyUtil.solidNoise(_seed, NoiseUtil.HERMITE),
                RandyUtil.solidNoise(_seed, NoiseUtil.LINEAR),
                RandyUtil.valueNoise(_seed, NoiseUtil.QUINTIC),
                RandyUtil.valueNoise(_seed, NoiseUtil.HERMITE),
                RandyUtil.valueNoise(_seed, NoiseUtil.LINEAR)
        );
    }
}
