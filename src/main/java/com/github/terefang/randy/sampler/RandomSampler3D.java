package com.github.terefang.randy.sampler;

import com.github.terefang.randy.RandyUtil;
import com.github.terefang.randy.rng.ArcRandom;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Vector;

public class RandomSampler3D implements Sampler<double[]>
{
    ArcRandom _rng = new ArcRandom();

    public void setSeed(String s) {
        _rng.setSeed(s);
    }

    int dimensions = 3;
    double[] start;
    double[] end;

    int num;

    int b = 33;

    double w;

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public int getNum()
    {
        return num;
    }

    public void setNum(int num)
    {
        this.num = num;
    }

    public double getW() {
        return w;
    }

    public void setW(double w) {
        this.w = w;
    }

    public double[] getStart() {
        return start;
    }

    public void setStart(double... start) {
        this.start = start;
    }

    public double[] getEnd() {
        return end;
    }

    public void setEnd(double... end) {
        this.end = end;
    }

    @Override
    public List<double[]> samples()
    {
        List<double[]> _ret = new Vector<>();
        double[] _point = new double[this.dimensions];
        int[] _ps = new int[this.dimensions];
        double _slot = this.w/Math.sqrt(this.dimensions);

        int[] _d = new int[this.dimensions];
        for(int _i=0; _i<this.dimensions; _i++)
        {
            _point[_i] = this.start[_i]+((this.end[_i]-this.start[_i])/2.);
            _ps[_i] = (int)((_point[_i]-this.start[_i])/_slot);
        }
        _ret.add(_point);

        int[][][] _x = new int[(int)(Math.floor((this.end[0]-this.start[0])/_slot)+1)][(int)(Math.floor((this.end[1]-this.start[1])/_slot)+1)][(int)(Math.floor((this.end[2]-this.start[2])/_slot)+1)];
        _x[_ps[0]][_ps[1]][_ps[2]]=1;

        int _bb = 0;
        while(_ret.size()<this.num)
        {
            _point = new double[this.dimensions];
            for(int _i=0; _i<this.dimensions; _i++)
            {
                _point[_i] = this.start[_i]+(_rng.nextDouble()*(this.end[_i]-this.start[_i]));
                _ps[_i] = (int)Math.floor((_point[_i]-this.start[_i])/_slot);
            }

            if(_x[_ps[0]][_ps[1]][_ps[2]]==0)
            {
                boolean _ok = true;
                for(int _mx = Math.max(0,_ps[0]-2); _mx<Math.min(_x.length,_ps[0]+2); _mx++)
                {
                    for(int _my = Math.max(0,_ps[1]-2); _my<Math.min(_x.length,_ps[1]+2); _my++)
                    {
                        for(int _mz = Math.max(0,_ps[2]-2); _mz<Math.min(_x.length,_ps[2]+2); _mz++)
                        {
                            if (_x[_mx][_my][_mz] > 0)
                            {
                                double[] _test = _ret.get(_x[_mx][_my][_mz] - 1);
                                double _rw = RandyUtil.calculateDistance(_test, _point);
                                if (_rw <= this.w)
                                {
                                    _ok = false;
                                }
                            }
                        }
                    }
                }

                if(_ok)
                {
                    _ret.add(_point);
                    _x[_ps[0]][_ps[1]][_ps[2]]=_ret.size();
                    _bb=0;
                    continue;
                }
            }

            if(_bb++ > this.b) break;
        }

        return _ret;
    }
}
