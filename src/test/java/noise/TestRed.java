package noise;

import com.github.terefang.randy.RandyUtil;
import com.github.terefang.randy.nfield.NoiseField;
import com.github.terefang.randy.noise.INoise;
import util.TestUtil;

public class TestRed {
    public static void main(String[] args) {

        TestUtil.setupNoises(0x1ee7b33f).forEach((_rng) -> {
            long _t0 = System.currentTimeMillis();
            main_simple(_rng,4);
            //main_pyramid(_rng);
            //main_cone(_rng);
            //main_gauss(_rng);
            long _t1 = System.currentTimeMillis();
            System.err.println("ms = "+(_t1-_t0));
        });
    }
    public static void main_simple0(INoise _rng) {
        NoiseField _nf = new NoiseField(TestUtil._SIZE, TestUtil._SIZE);
        TestUtil.test2d_field(_rng, _nf, 3.45f);

        for(int _i = 0 ; _i<256; _i ++)
        {
            _nf.simpleBlur(1);
            _nf.normalize(-1., +1.);
        }
        TestUtil.test2d_image(_rng, _nf, 3.45f, "~Red256~Simple3x3","redNoise", "RedNoise~Simple");
    }
    public static void main_pyramid(INoise _rng) {
        NoiseField _nf = new NoiseField(TestUtil._SIZE, TestUtil._SIZE);
        TestUtil.test2d_field(_rng, _nf, 3.45f);

        for(int _i = 0 ; _i<256; _i ++)
        {
            _nf.pyramidalFilter(1);
            _nf.normalize(-1., +1.);
        }
        TestUtil.test2d_image(_rng, _nf, 3.45f, "~Red256~Pyramid5x5","redNoise", "RedNoise~Pyramid");
    }
    public static void main_cone(INoise _rng) {
        NoiseField _nf = new NoiseField(TestUtil._SIZE, TestUtil._SIZE);
        TestUtil.test2d_field(_rng, _nf, 3.45f);

        for(int _i = 0 ; _i<256; _i ++)
        {
            _nf.coneFilter(1);
            _nf.normalize(-1., +1.);
        }
        TestUtil.test2d_image(_rng, _nf, 3.45f, "~Red256~Cone5x5","redNoise", "RedNoise~Cone");
    }

    public static void main_gauss0(INoise _rng) {
        NoiseField _nf = new NoiseField(TestUtil._SIZE>>4, TestUtil._SIZE>>4);
        TestUtil.test2d_field(_rng, _nf, 3.45f);

        double[] _k = _nf.gaussKernel(31, 1.8);
        for(int _i = 0 ; _i<4; _i ++)
        {
            _nf.scaleUp();
            _nf.filterKernel(31, _k);
            _nf.normalize(-1., +1.);
        }
        _nf.scaleDown(TestUtil._SIZE, TestUtil._SIZE);
        TestUtil.test2d_image(_rng, _nf, 3.45f, "~Red4~Gauss31x31","redNoise", "RedNoise~Gauss");
    }
    public static void main_gauss(INoise _rng) {
        NoiseField _nf = new NoiseField(TestUtil._SIZE>>4, TestUtil._SIZE>>4);
        TestUtil.test2d_field(_rng, _nf, 3.45f);

        double[] _k = _nf.gaussKernel(7, 1.8);
        for(int _i = 0 ; _i<4; _i ++)
        {
            _nf.scaleUp();
            _nf.filterKernel(7, _k);
            _nf.normalize(-1., +1.);
        }
        _nf.scaleDown(TestUtil._SIZE, TestUtil._SIZE);
        TestUtil.test2d_image(_rng, _nf, 3.45f, "~Red4~Gauss7x7","redNoise", "RedNoise~Gauss7");
    }

    public static void main_simple4(INoise _rng) {
        NoiseField _nf = new NoiseField(TestUtil._SIZE>>4, TestUtil._SIZE>>4);
        TestUtil.test2d_field(_rng, _nf, 3.45f);

        for(int _i = 0 ; _i<4; _i ++)
        {
            _nf.scaleUp();
            _nf.simpleBlur(1);
            _nf.normalize(-1., +1.);
        }
        _nf.scaleDown(TestUtil._SIZE, TestUtil._SIZE);
        TestUtil.test2d_image(_rng, _nf, 3.45f, "~Red4~Simple3x3","redNoise", "RedNoise~Simple4");
    }

    public static void main_simple5(INoise _rng) {
        NoiseField _nf = new NoiseField(TestUtil._SIZE>>5, TestUtil._SIZE>>5);
        TestUtil.test2d_field(_rng, _nf, 3.45f);

        for(int _i = 0 ; _i<5; _i ++)
        {
            _nf.scaleUp();
            _nf.simpleBlur(1);
            _nf.normalize(-1., +1.);
        }
        _nf.scaleDown(TestUtil._SIZE, TestUtil._SIZE);
        TestUtil.test2d_image(_rng, _nf, 3.45f, "~Red5~Simple3x3","redNoise", "RedNoise~Simple5");
    }

    public static void main_simple(INoise _rng, int _step) {
        NoiseField _nf = new NoiseField(TestUtil._SIZE>>_step, TestUtil._SIZE>>_step);
        TestUtil.test2d_field(_rng, _nf, (float) (3.45f*Math.pow((float)2, (float)_step-1)));
        NoiseField _nf2 = _nf.clone();

        _nf.simpleBlur(1);
        _nf.normalize(-1., +1.);
        for(int _i = 0 ; _i<_step; _i ++)
        {
            _nf.scaleUp();
            _nf2.scaleUp();
            _nf.simpleBlur(1);
            _nf.normalize(-1., +1.);
        }
        _nf.scaleDown(TestUtil._SIZE, TestUtil._SIZE);
        TestUtil.test2d_image(_rng, _nf, 3.45f, "~Red"+_step+"~Simple3x3","redNoise", _rng.name()+"~Simple"+_step);
        _nf2.normalize(-1., +1.);
        _nf2.scaleDown(TestUtil._SIZE, TestUtil._SIZE);
        TestUtil.test2d_image(_rng, _nf2, 3.45f, "~Red0","redNoise", _rng.name()+"~Simple0");
    }

}
