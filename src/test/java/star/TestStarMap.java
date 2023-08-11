package star;

import com.github.terefang.randy.rng.ArcRandom;
import com.github.terefang.randy.starsys.SimpleStarCube;
import com.github.terefang.randy.starsys.model.*;
import lombok.SneakyThrows;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Vector;

public class TestStarMap
{

    @SneakyThrows
    public static void main(String[] args)
    {
        PrintStream _fh = new PrintStream(new FileOutputStream("./out/star-cube.txt"));
        Appendable _afh = new Appendable() {
            @Override
            public Appendable append(CharSequence csq) throws IOException {
                _fh.print(csq);
                return this;
            }

            @Override
            public Appendable append(CharSequence csq, int start, int end) throws IOException {
                _fh.print(csq.subSequence(start, end));
                return this;
            }

            @Override
            public Appendable append(char c) throws IOException {
                _fh.print(c);
                return this;
            }
        };

        //for(double _f = 0.25; _f<4.; _f+=.25)
        //{
        //    doit(_f, _afh);
            doit(1.6, _afh);
        //}
        _fh.close();
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
