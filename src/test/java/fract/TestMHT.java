package fract;

import com.github.terefang.randy.fractal.IFractal;
import com.github.terefang.randy.fractal.MusgraveHeteroTerrainFractal;
import com.github.terefang.randy.nfield.NoiseField;
import com.github.terefang.randy.nfield.NoiseFieldUtil;
import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.NoiseUtil;
import com.github.terefang.randy.transf.ITransform;
import com.github.terefang.randy.transf.TypeTransform;
import lombok.SneakyThrows;
import com.github.terefang.randy.utils.BmpFont;
import util.TestUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestMHT {

    public static int _SIZE = 1024;
    //public static int[] _OCT = { 1,2,4,6,8,10,12 };
    public static int[] _OCT = { 1,2,4,8 };
    //public static float[] _FREQ = { 1.23f, 2.34f, 3.45f, 4.56f, 5.67f, 6.78f };
    public static float[] _FREQ = { 6.78f };
    //public static float[] _H = { .5f, .77f, 1.f, 1.23f, 1.5f };
    public static double[] _H = { .25, .5, .75, 1., 1.25, 1.5 };

    @SneakyThrows
    public static void main(String[] args) {
        ExecutorService _EX = Executors.newFixedThreadPool(4);
        long _seed = NoiseUtil.BASE_SEED1;
        for(final double _h : _H)
        {
            for(final float _freq : _FREQ)
            {
                final ITransform.TransformType _ttrans = ITransform.TransformType.T_0NONE;
                //for(final ITransform.TransformType _ttrans : ITransform.TransformType.values())
                {
                    for(final INoise _type : TestUtil.setupNoises(_seed))
                    {
                        _EX.execute(() -> {
                            final IFractal _ftype = new MusgraveHeteroTerrainFractal();
                            final ITransform _trans = TypeTransform.from(_ttrans);
                            _type.setTransform(_trans);
                            _ftype.setNoise(_type);
                            _ftype.setFrequency(_freq);
                            _ftype.setH(_h);
                            _ftype.setVseed(true);
                            _ftype.setLacunarity(NoiseUtil.BASE_LACUNARITY);
                            String _name = _ftype.name() + "/" + _type.name() + String.format("~fq=%04d,H=%02d", (int) (_freq*10), (int)(_h*10));
                            System.err.println("-START "+_name);
                            NoiseField _nf = new NoiseField(_SIZE, _SIZE);

                            for (int _o = 0; _o < _OCT.length; _o++)
                            {
                                _ftype.setOctaves(_OCT[_o]);
                                int _yo=((_SIZE>>1)*_o)%_SIZE;
                                int _xo=(_SIZE>>1)*(_o/2);
                                for (int _y = 0; _y < (_SIZE>>1); _y++)
                                {
                                    for (int _x = 0; _x < (_SIZE>>1); _x++)
                                    {
                                        float _fx = ((((float) _x) / ((float) (_SIZE>>1))) - .5f)*2f;
                                        float _fy = ((((float) _y) / ((float) (_SIZE>>1))) - .5f)*2f;
                                        double _value = _ftype.fractal2(_fx,_fy); // /((double)_oct*_oct);
                                        _nf.setPoint(_xo+_x, _yo+_y, _value);
                                    }
                                }
                            }

                            _nf.normalize(-1., 1., 0,(_SIZE>>1)-1, 0,(_SIZE>>1)-1);
                            _nf.normalize(-1., 1., 0,(_SIZE>>1)-1, (_SIZE>>1),_SIZE-1);
                            _nf.normalize(-1., 1., (_SIZE>>1),_SIZE-1, 0,(_SIZE>>1)-1);
                            _nf.normalize(-1., 1., (_SIZE>>1),_SIZE-1, (_SIZE>>1),_SIZE-1);

                            BufferedImage _bi = NoiseFieldUtil.getHFEImage(_nf, -1., 1.);
                            Graphics2D _g = (Graphics2D) _bi.getGraphics();
                            BmpFont _font = BmpFont.defaultInstance();
                            _font.drawString(_g,10, 10,"F = "+_ftype.name(), Color.YELLOW,Color.BLACK);
                            _font.drawString(_g,10, 30,"N = "+_type.name(), Color.YELLOW,Color.BLACK);
                            _font.drawString(_g,10, 50,"FREQ = "+_freq, Color.YELLOW,Color.BLACK);
                            _font.drawString(_g,10, 70,"H = "+_h, Color.YELLOW,Color.BLACK);
                            _font.drawString(_g,10, 90,"OCT = 1 2 4 8", Color.YELLOW,Color.BLACK);

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
