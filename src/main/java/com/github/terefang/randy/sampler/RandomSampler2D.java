package com.github.terefang.randy.sampler;

import com.github.terefang.randy.RandyUtil;
import com.github.terefang.randy.rng.ArcRandom;

import java.util.List;
import java.util.Vector;

public class RandomSampler2D implements Sampler<double[]>
{
    ArcRandom _rng = new ArcRandom();

    public void setSeed(String s) {
        _rng.setSeed(s);
    }

    int dimensions = 2;
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

    public void init() {
        this._slot = this.w/Math.sqrt(this.dimensions);
        this._x = new int[(int)(Math.floor((this.end[0]-this.start[0])/_slot)+1)][(int)(Math.floor((this.end[1]-this.start[1])/_slot)+1)];
        this._preseed = new Vector<>();
    }

    private double _slot;
    private int[][] _x;

    private List<double[]> _preseed;

    public void preseed(double[] _p)
    {
        this._preseed.add(_p);
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
                    _ps[_i] = (int)((_p[_i]-this.start[_i])/_slot);
                }
                _ret.add(_p);
                _x[_ps[0]][_ps[1]]=_ret.size();
            }
        }
        else
        {
            /*
            for(int _i=0; _i<this.dimensions; _i++)
            {
                _point[_i] = this.start[_i]+((this.end[_i]-this.start[_i])/2.);
                _ps[_i] = (int)((_point[_i]-this.start[_i])/_slot);
            }
            _ret.add(_point);
            _x[_ps[0]][_ps[1]]=1;

             */
        }

        int _bb = 0;
        while((_ret.size()-_preseed.size())<this.num)
        {
            _point = new double[this.dimensions];
            for(int _i=0; _i<this.dimensions; _i++)
            {
                _point[_i] = this.start[_i]+(_rng.nextDouble()*(this.end[_i]-this.start[_i]));
                _ps[_i] = (int)Math.floor((_point[_i]-this.start[_i])/_slot);
            }

            if(_x[_ps[0]][_ps[1]]==0)
            {
                boolean _ok = true;
                for(int _mx = Math.max(0,_ps[0]-2); _mx<Math.min(_x.length,_ps[0]+2); _mx++)
                {
                    for(int _my = Math.max(0,_ps[1]-2); _my<Math.min(_x.length,_ps[1]+2); _my++)
                    {
                        if(_x[_mx][_my]>0)
                        {
                            double[] _test = _ret.get(_x[_mx][_my]-1);
                            double _rw = RandyUtil.calculateDistance(_test,_point);
                            if(_rw<=this.w)
                            {
                                _ok=false;
                            }
                        }

                    }
                }

                if(_ok)
                {
                    _ret.add(_point);
                    _x[_ps[0]][_ps[1]]=_ret.size();
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
