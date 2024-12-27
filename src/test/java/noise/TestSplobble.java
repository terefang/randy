package noise;

import com.github.terefang.randy.RandyUtil;
import com.github.terefang.randy.noise.AbstractNoise;
import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.NoiseUtil;
import com.github.terefang.randy.transf.ITransform;
import com.github.terefang.randy.transf.TypeTransform;
import util.TestUtil;

import java.util.Arrays;

public class TestSplobble {
    static double terraceHeight = 8.;
    public static void main(String[] args)
    {
        final INoise _noise1 = RandyUtil.simplexNoise(0x1ee7d3ad);
        final INoise _rng = new AbstractNoise() {
            @Override
            public double _noise2(long _seed, double x, double y, int interpolation) {
                return _noise1._noise2(_seed,x,y, interpolation);
            }
        };
        TestUtil.test2d(_rng,Arrays.asList(
                ITransform.TransformType.T_0NONE
                , ITransform.TransformType.T_CLAMP01
                , ITransform.TransformType.T_LEVEL8
        ),"splobbleNoise", true, false, (_nf) -> {
            _nf.add(1.);
            _nf.morphologicalErode(20, .5);
            _nf.add(-1.);
        });
    }

    public static void main_1(String[] args)
    {
        final INoise _noise1 = RandyUtil.foamNoise(0x1ee7b33f, 1.);
        final INoise _noise2 = RandyUtil.cubicNoise(0xc1f3a77e, .5);
        //final INoise _noise2 = RandyUtil.simplexNoise(0xc1f3a77e);
        final INoise _noise3 = RandyUtil.simplexNoise(0x1ee7d3ad);
        //final INoise _noise3 = RandyUtil.foamNoise(0x1ee7d3ad, .5);
        final INoise _rng = new AbstractNoise() {
            @Override
            public double _noise2(long _seed, double x, double y, int interpolation) {
                double _n1 = _noise1._noise2(_seed,x,y, interpolation);
                double _n2 = _noise2._noise2n(_seed+1,x/2.,y/2., interpolation);
                double _n3 = _noise3._noise2n(_seed+2,x/(terraceHeight/2.),y/(terraceHeight/2.), interpolation)/terraceHeight;
                double _f = _n1;//NoiseUtil.fastFloor(_n1*terraceHeight)/terraceHeight;
                double _i = NoiseUtil.clampValue(NoiseUtil.hermiteInterpolator(_n2), .3 , 1.);
                double _i1 = NoiseUtil.clampValue(_i, .3,.6);
                double _i2 = NoiseUtil.clampValue(_i, .6,.8);
                return NoiseUtil.lerp(NoiseUtil.lerp(_f, _n1, _i1), NoiseUtil.lerp(_n1, _n3, _i2), _i);
            }
        };
        TestUtil.test2d(_rng,Arrays.asList(
                ITransform.TransformType.T_0NONE
                , ITransform.TransformType.T_CLAMP01
                , ITransform.TransformType.T_LEVEL8
        ),"splobbleNoise", true, false, (_nf) -> {
            _nf.add(1.);
            _nf.morphologicalErode(10, 0.5);
            _nf.add(-1.);
        });
    }
}
