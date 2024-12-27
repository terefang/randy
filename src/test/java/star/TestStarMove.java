package star;

import com.github.terefang.randy.RandyUtil;
import com.github.terefang.randy.fractal.IFractal;
import com.github.terefang.randy.nfield.NoiseFieldUtil;
import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.NoiseUtil;
import com.github.terefang.randy.rng.ArcRandom;
import com.github.terefang.randy.starsys.SimpleStarCube;
import com.github.terefang.randy.utils.BmpFont;
import com.github.terefang.randy.utils.ColorUtil;
import lombok.SneakyThrows;
import util.TestUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Vector;

public class TestStarMove
{

    @SneakyThrows
    public static void main(String[] args)
    {
        double _H=2.2;
        double _l=.3;
        if(false){
            IFractal _F = RandyUtil.exoFractal(RandyUtil.perlinNoise(NoiseUtil.toSeed("X"), NoiseUtil.HERMITE), 1.,_H,_l);
            _F.setOctaves(5);
            INoise _nx = RandyUtil.adaptAsNoise(_F);
            _F = RandyUtil.exoFractal(RandyUtil.perlinNoise(NoiseUtil.toSeed("Y"), NoiseUtil.HERMITE), 1.,_H,_l);
            _F.setOctaves(5);
            INoise _ny = RandyUtil.adaptAsNoise(_F);
            _F = RandyUtil.exoFractal(RandyUtil.perlinNoise(NoiseUtil.toSeed("Z"), NoiseUtil.HERMITE), 1.,_H,_l);
            _F.setOctaves(5);
            INoise _nz = RandyUtil.adaptAsNoise(_F);
        }
        INoise _nx = RandyUtil.perlinNoise(NoiseUtil.toSeed("X"), NoiseUtil.HERMITE);
        INoise _ny = RandyUtil.perlinNoise(NoiseUtil.toSeed("Y"), NoiseUtil.HERMITE);
        INoise _nz = RandyUtil.perlinNoise(NoiseUtil.toSeed("Z"), NoiseUtil.HERMITE);


        BufferedImage _bI = TestUtil.setupImage(1024);
        Graphics2D _g = (Graphics2D) _bI.getGraphics();
        BmpFont _font = BmpFont.defaultInstance();
        Color _col = Color.ORANGE;
        _g.setStroke(new BasicStroke(3f));

        int _step = 10;
        double _freq = .01;
        double _x=0.1,_y=0.1,_z=0.1;
        for(int _i = 0; _i<2560; _i+=_step)
        {
            double _ox=_x,_oy=_y,_oz=_z;
            double _dx = _nx.noise1((_i)*_freq);
            double _dy = _ny.noise1((_i)*_freq+.33);
            double _dz = _nz.noise1((_i)*_freq+.66);

            double _v = NoiseUtil.value(_dx,_dy,_dz);
            //if(_v>0.)
            if(false)
            {
                _dx/=_v;
                _dy/=_v;
                _dz/=_v;
            }

            _x=_dx*512;
            _y=_dy*512;
            _z=_dz*512;

            System.out.print(String.format("%d %d %d\n",(int)_x,(int)_y,(int)_z));

            _g.setColor(_col);
            _g.drawLine(512+(int)_ox,512+(int)_oy, 512+(int)_x, 512+(int)_y);

            _col = ColorUtil.biasHue(_col, 3.*(double)_step);
        }
        //_font.drawString(_g, 10,10,"H="+_H, Color.WHITE);
        //_font.drawString(_g, 10,30,"l="+_l, Color.WHITE);
        _g.dispose();


        NoiseFieldUtil.savePNG(_bI, "./out/"+TestStarMove.class.getSimpleName()+".png");

    }
    @SneakyThrows
    public static void doit(double _frq, Appendable _afh)
    {
        long seed = 0x1337L;
        int _bits = 4;
        int _size = 1 << _bits;
        int _mask = (1 << _bits)-1;
        float _fzxy = .33f;
        SimpleStarCube _sc = new SimpleStarCube();
        ArcRandom _arc = new ArcRandom();
        _arc.setSeed(seed);

        _sc.setSeed(seed);
        _sc.setBits(_bits);
        _sc.setSize(_size);
        _sc.setMask(_mask);
        _sc.setFrequency(_frq);
        _sc.setTertiaryFrequency(4.5);
        _sc.init();

        int _xy = (_size*200);
        int _k = 0;
        float _th=0.2f;
        List<Integer> _list = new Vector();
        {
            for (int _z = 0; _z < _size; ++_z) {
                for (int _y = 0; _y < _size; ++_y) {
                    for (int _x = 0; _x < _size; ++_x) {
                        SimpleStarCube.SimpleStar _h = _sc.getData()[_x][_y][_z];
                        double _test = _h.getNoise();
                        if (Math.abs(_test) >= _th) {
                            int _sx = (int) _h.getLocalX();
                            int _sy = (int) _h.getLocalY();
                            int _sz = (int) _h.getLocalZ();

                            System.out.print(String.format("TH=%f %f %f %f %f %f %f (%d %d %d) (%03X) M=%f\n",
                                    _h.getNoise(),
                                    _h.getNoiseX(), _h.getNoiseY(), _h.getNoiseZ(),
                                    _h.getNoiseU(), _h.getNoiseV(), _h.getNoiseW(),
                                    _sx, _sy, _sz,
                                    _h.getId(),
                                    _h.getNoiseX()/_h.getNoiseY()));
                            _afh.append(String.format("(%+d %+d %+d)\n", _sx, _sy, _sz));
                            _k++;
                            _list.add((int) (_h.getId() & 0x7fffffff));
                            _h.init((int) seed, -1);
                            _h.getContext().outputInformation(_afh);
                        }
                    }
                }
            }
        }
        _afh.append(String.format("count = %d\n", _k));

        /*
        KrushkalMST _mst = new KrushkalMST();
        int _a = 0;
        for(int _ia : _list)
        {
            int _b = 0;
            for(int _ib : _list)
            {
                if(_b > _a)
                {
                    int _ax = (_ia >>> (_bits*2)) & _mask;
                    int _ay = (_ia >>> _bits) & _mask;
                    int _az = _ia & _mask;
                    int _bx = (_ib >>> (_bits*2)) & _mask;
                    int _by = (_ib >>> _bits) & _mask;
                    int _bz = _ib & _mask;

                    SimpleStarCube.SimpleStar _ah = _sc.getData()[_ax][_ay][_az];
                    SimpleStarCube.SimpleStar _bh = _sc.getData()[_bx][_by][_bz];

                    int _asx = (int) _ah.getLocalX();
                    int _asy = (int) _ah.getLocalY();
                    int _asz = (int) _ah.getLocalZ();

                    int _bsx = (int) _bh.getLocalX();
                    int _bsy = (int) _bh.getLocalY();
                    int _bsz = (int) _bh.getLocalZ();

                    double _w = Math.pow(Math.abs(_asx - _bsx),2);
                    _w += Math.pow(Math.abs(_asy - _bsy),2);
                    _w += Math.pow(Math.abs(_asz - _bsz),2);
                    _w = Math.pow(_w, 1f/3f);
                    _mst.addEgde(_a, _b, _w);
                }
                _b++;
            }
            _a++;
        }

        _mst.setVertices(_list.size());

        if(_mst.getAllEdges().size()>0)
        {
            for(KrushkalMST.Edge _e :  _mst.kruskalMST())
            {
                int _ia = _list.get(_e.getSource());
                int _ib = _list.get(_e.getDestination());

                //System.out.println(String.format("%03X - %03X - %f", _ia, _ib, _e.getWeight()));

                int _ax = (_ia >>> (_bits*2)) & _mask;
                int _ay = (_ia >>> _bits) & _mask;
                int _az = _ia & _mask;
                int _bx = (_ib >>> (_bits*2)) & _mask;
                int _by = (_ib >>> _bits) & _mask;
                int _bz = _ib & _mask;

                SimpleStarCube.SimpleStar _ah = _sc.getData()[_ax][_ay][_az];
                SimpleStarCube.SimpleStar _bh = _sc.getData()[_bx][_by][_bz];

                int _asx = (int) _ah.getLocalX();
                int _asy = (int) _ah.getLocalY();
                int _asz = (int) _ah.getLocalZ();

                int _bsx = (int) _bh.getLocalX();
                int _bsy = (int) _bh.getLocalY();
                int _bsz = (int) _bh.getLocalZ();

                _img.gDashedLine(
                        (int) ((_xy/2)+((_asx)+(_asz*_fzxy))), (int) ((_xy/2)+((_asy)-(_asz*_fzxy))),
                        (int) ((_xy/2)+((_bsx)+(_bsz*_fzxy))), (int) ((_xy/2)+((_bsy)-(_bsz*_fzxy))),
                        ImageUtil.YELLOW, 3f);
            }
        }

        _img.savePng(String.format("./out/TestStarMap/star-cube-%.2f.png", _frq));
        */
    }
}
