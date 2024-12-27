package nfield;

import com.github.terefang.randy.RandyUtil;
import com.github.terefang.randy.fractal.AbstractOctavationModeFractal;
import com.github.terefang.randy.nfield.NoiseField;
import com.github.terefang.randy.nfield.NoiseFieldUtil;
import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.NoiseUtil;
import com.github.terefang.randy.rng.impl.ArcRand;
import util.TestUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class TestEroder
{
    public static void main(String[] args)
    {
        long _seed = 0x1ee7b33fL;
        ArcRand _rng = ArcRand.from("s3cr3t");
        AbstractOctavationModeFractal _fr = RandyUtil.octRidgedMultiFractal(
                RandyUtil.simplexNoise(_seed), 9, 12, 2, true);
        INoise _n = RandyUtil.fractalAsNoise(_fr);
        //foamNoise(
        //        RandyUtil.lumpNoise(_seed, NoiseUtil.BASE_HARSHNESS),
        //        _seed, NoiseUtil.BASE_SHARPNESS);
        
        NoiseField _nf = new NoiseField(512,512);
        _nf.setProjection(-1,-10.,10.,-10.,10.);
        _nf.applyNoise(_n);
        _nf.normalize(0.,1.);
        NoiseFieldUtil.saveHFImage(_nf, "out/erosion/org.png");
        _nf.normalize(0.,6000.);
        NoiseField.er_sim_params _p = NoiseField.er_sim_params.predef();
        _p._n = 1<<16;
        _p._gravity = 8.;
        _p._ttl=128;
        _nf.eroder(_rng, _p);
        //_nf.normalize(0.,1.);
        _nf.normalize((x)->{ return x/6000.; });
        NoiseFieldUtil.saveHFImage(_nf, "out/erosion/eroded.png");
    }
}
