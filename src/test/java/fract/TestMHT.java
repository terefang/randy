package fract;

import com.github.terefang.randy.RandyUtil;
import com.github.terefang.randy.fractal.IFractal;
import com.github.terefang.randy.fractal.MusgraveHeteroTerrainFractal;
import com.github.terefang.randy.nfield.NoiseField;
import com.github.terefang.randy.nfield.NoiseFieldUtil;
import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.NoiseUtil;
import lombok.SneakyThrows;
import util.BmpFont;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestMHT {

    public static int _SIZE = 1024;
    //public static int[] _OCT = { 1,2,4,6,8,10,12 };
    public static int[] _OCT = { 4,8 };
    //public static float[] _FREQ = { 1.23f, 2.34f, 3.45f, 4.56f, 5.67f, 6.78f };
    public static float[] _FREQ = { 3.45f, 6.78f, 12.5f };
    //public static float[] _H = { .5f, .77f, 1.f, 1.23f, 1.5f };
    public static float[] _H = { .25f, .5f };

    @SneakyThrows
    public static void main(String[] args) {
        ExecutorService _EX = Executors.newFixedThreadPool(8);
        long _seed = NoiseUtil.BASE_SEED1;
        List<INoise> _stypes = Arrays.asList(
                RandyUtil.perlinNoise(_seed, NoiseUtil.QUINTIC),
                RandyUtil.perlinNoise(_seed, NoiseUtil.HERMITE),
                RandyUtil.perlinNoise(_seed, NoiseUtil.LINEAR),
                RandyUtil.simplexNoise(_seed),
                RandyUtil.cellMergeNoise(_seed, NoiseUtil.EUCLIDEAN, NoiseUtil.BASE_SHARPNESS*.8),
                RandyUtil.cellMergeNoise(_seed, NoiseUtil.MANHATTAN, NoiseUtil.BASE_SHARPNESS*.8),
                RandyUtil.cellMergeNoise(_seed, NoiseUtil.NATURAL, NoiseUtil.BASE_SHARPNESS*.8),
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
        List<INoise> _types = new Vector<>();
        _types.addAll(_stypes);
        _stypes.forEach((_t) -> { _types.add(RandyUtil.foamNoise(_t,-2L^_seed, NoiseUtil.BASE_SHARPNESS)); });
        //_stypes.forEach((_t) -> { _types.add(RandyUtil.blurNoise(_t, -2L^_seed, NoiseUtil.LINEAR, 1.8)); });
        //_stypes.forEach((_t) -> { _types.add(RandyUtil.blurNoise(_t, -2L^_seed, NoiseUtil.HERMITE, 1.8)); });
        //_stypes.forEach((_t) -> { _types.add(RandyUtil.blurNoise(_t, -2L^_seed, NoiseUtil.QUINTIC, 1.8)); });
        for(final float _h : _H)
        {
            for(final float _freq : _FREQ)
            {
                for(int _oct : _OCT)
                {
                    for(final INoise _type : _types)
                    {
                        final IFractal _ftype = new MusgraveHeteroTerrainFractal();
                        _ftype.setNoise(_type);
                        _ftype.setFrequency(_freq);
                        _ftype.setH(_h);
                        _ftype.setOctaves(_oct);
                        _ftype.setVseed(true);
                        _ftype.setLacunarity(NoiseUtil.BASE_LACUNARITY);
                        _EX.execute(() -> {
                            String _name = _ftype.name() + "/" + _type.name() + String.format("~fq=%04d,oct=%02d,H=%02d", (int) (_freq*10), _oct, (int)(_h*10));
                            System.err.println("-START "+_name);
                            NoiseField _nf = new NoiseField(_SIZE, _SIZE);
                            for (int _y = 0; _y < _SIZE; _y++)
                            {
                                for (int _x = 0; _x < _SIZE; _x++)
                                {
                                    float _fx = (((float) _x) / ((float) _SIZE)) - .5f;
                                    float _fy = (((float) _y) / ((float) _SIZE)) - .5f;
                                    double _value = _ftype.fractal2(_fx,_fy);
                                    //        _value = FastNoiseLite.singleTransform(_transform, _value,0,0,0);
                                    _nf.setPoint(_x, _y, _value);
                                }
                            }

                            //_nf.normalize(-1., 1.);

                            BufferedImage _bi = NoiseFieldUtil.getHFEImage(_nf, -1., 1.);
                            Graphics2D _g = (Graphics2D) _bi.getGraphics();
                            BmpFont _font = BmpFont.defaultInstance();
                            _font.drawString(_g,10, 10,"F = "+_ftype.name(), Color.YELLOW,Color.BLACK);
                            _font.drawString(_g,10, 30,"T = "+_type.name(), Color.YELLOW,Color.BLACK);
                            _font.drawString(_g,10, 50,"FREQ = "+_freq, Color.YELLOW,Color.BLACK);
                            _font.drawString(_g,10, 70,"OCT = "+_oct, Color.YELLOW,Color.BLACK);
                            _font.drawString(_g,10, 90,"H = "+_h, Color.YELLOW,Color.BLACK);

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
                            NoiseFieldUtil.savePNG(_bi, "./out/fract/" + _name + ".png");

                            System.err.println("-END "+_name);
                        });
                    }
                }
            }
        }
        _EX.shutdown();
        while(!_EX.isTerminated())
        {
            System.err.print(".");
            Thread.sleep(10000L);
        }
        System.exit(0);
    }
}
