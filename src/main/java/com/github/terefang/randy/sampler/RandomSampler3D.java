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
    double[] size;

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

    public double[] getSize() {
        return size;
    }

    public void setSize(double... s) {
        this.size = s;
    }

    private List<double[]> _preseed;

    public void preseed(double[] _p)
    {
        this._preseed.add(_p);
    }

    private double _slot;
    private int[][][] _x;

    public void init() {
        this._slot = this.w/Math.sqrt(this.dimensions);
        this._x = new int[(int)(Math.floor((this.size[0])/_slot)+1)][(int)(Math.floor((this.size[1])/_slot)+1)][(int)(Math.floor((this.size[2])/_slot)+1)];
        this._preseed = new Vector<>();
    }
    @Override
    public List<double[]> samples()
    {
        List<double[]> _ret = new Vector<>();
        double[] _point = new double[this.dimensions];
        int[] _ps = new int[this.dimensions];

        if(this._preseed.size()>0)
        {
            for(double[] _p : this._preseed)
            {
                for(int _i=0; _i<this.dimensions; _i++)
                {
                    _ps[_i] = (int)((_p[_i])/_slot);
                }
                _ret.add(_p);
                _x[_ps[0]][_ps[1]][_ps[2]]=_ret.size();
            }
        }

        int _bb = 0;
        while((_ret.size()-_preseed.size())<this.num)
        {
            _point = new double[this.dimensions];
            for(int _i=0; _i<this.dimensions; _i++)
            {
                _point[_i] = (_rng.nextDouble()*(this.size[_i]));
                _ps[_i] = (int)Math.floor(_point[_i]/_slot);
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

        if(this._preseed.size()>0)
        {
            return _ret.subList(this._preseed.size(), _ret.size());
        }
        return _ret;
    }
}
