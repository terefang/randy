package com.github.terefang.randy.rng.impl;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ArcRand {
    int[] _ctx;
    int _a, _b;

    public static ArcRand from(long _seed)
    {
        ArcRand _rand = new ArcRand();
        _rand.init();
        _rand.bitfeed(_seed);
        return _rand;
    }

    public static ArcRand from(int _seed)
    {
        ArcRand _rand = new ArcRand();
        _rand.init();
        _rand.bitfeed(_seed);
        return _rand;
    }

    public static ArcRand from(byte[] _seed)
    {
        ArcRand _rand = new ArcRand();
        _rand.init();
        _rand.bitfeed(_seed);
        return _rand;
    }

    public static ArcRand from(String _text)
    {
        UUID _uuid = UUID.nameUUIDFromBytes(_text.getBytes(StandardCharsets.UTF_8));
        ArcRand _rand = new ArcRand();
        _rand.init();
        _rand.bitfeed(_uuid.getLeastSignificantBits(), _uuid.getMostSignificantBits());
        return _rand;
    }

    public void bitfeed(int... _seeds)
    {
        for(int _seed : _seeds)
        {
            while((_seed & 0x7fffffff)>0)
            {
                this._a += (_seed & 0xf);
                for(int _i = 0 ; _i<256; _i++)
                {
                    this.next();
                }
                _seed>>>=4;
                this._b += (_seed & 0xf);
                for(int _i = 0 ; _i<256; _i++)
                {
                    this.next();
                }
                _seed>>>=4;
            }
        }
    }

    public void bitfeed(long... _seeds)
    {
        for(long _seed : _seeds)
        {
            while((_seed & (0x7fffffffffffffffL))>0)
            {
                this._a += (_seed & 0xf);
                for(int _i = 0 ; _i<256; _i++)
                {
                    this.next();
                }
                _seed>>>=4;
                this._b += (_seed & 0xf);
                for(int _i = 0 ; _i<256; _i++)
                {
                    this.next();
                }
                _seed>>>=4;
            }
        }
    }

    public void bitfeed(byte[] _seeds)
    {
        for(byte _seed : _seeds)
        {
            this._a += (_seed & 0xf);
            for(int _i = 0 ; _i<256; _i++)
            {
                this.next();
            }
            _seed>>>=4;
            this._b += (_seed & 0xf);
            for(int _i = 0 ; _i<256; _i++)
            {
                this.next();
            }
        }
    }

    public void init()
    {
        this._ctx = new int[256];
        for(int _i = 0 ; _i<256; _i++)
        {
            this._ctx[_i] = _i;
        }
    }

    public int next()
    {
        this._a = (this._a+1) & 0xff;
        this._b= (this._b+this._ctx[this._a]) & 0xff;

        int _t = this._ctx[this._b];
        this._ctx[this._b] = this._ctx[this._a];
        this._ctx[this._a] = _t;

        int _ret = this._ctx[(this._ctx[this._b] + this._ctx[this._a]) & 0xff];
        return _ret;
    }

    public int next16()
    {
        return (next()<<8) | (~next() & 0xff) &0xffff;
    }

    public int next32()
    {
        return (next16()<<16) | (~next16() & 0xffff);
    }

    public int nextInt(int _x) { return ((next32() & 0x7fffffff) % _x); }

    public float nextFloat()
    {
        return next()/256f;
    }

    public float nextFloat16()
    {
        return next16()/65536f;
    }

    public float nextFloat32()
    {
        return (next32() & 0x7fffffff)/((float)0x80000000L);
    }

    public float nextGauss()
    {
        return (nextFloat() - nextFloat());
    }

    public float nextGauss(int _d, int _t)
    {
        float _r = 1f;
        for(int _i = 0; _i<(_d*2); _i++) _r*=nextFloat()*(_t+1);
        for(int _i = 0; _i<_d; _i++) _r/=_t;
        return _r;
    }

    public float nextITF(float _d)
    {
        float _r = 0f;
        for(int _i = 0; _i<10; _i++) _r+=nextFloat()*_d;
        return _r/10f;
    }

    public int nextRange(int _s, int _e)
    {
        return (int)((nextFloat()*(_e-_s))+_s);
    }

    public int nextDice(int _d, int _t, int _b)
    {
        int _r = 0;
        for(int _i = 0; _i<_d; _i++) _r+=(nextFloat()*(_t))+1;
        return _r+_b;
    }

    public float next(float _v)
    {
        return nextFloat()*_v;
    }

    public double next(double _v)
    {
        return nextFloat()*_v;
    }

    public float nextBounds(float _x, float _y, float _g)
    {
        return _x+(((int)(next(_y-_x)/_g))*_g);
    }

    public float nextBounds(float _x, float _y)
    {
        return nextBounds(_x, _y, 1f);
    }

    public float nextVariant(float _x, float _y, float _g)
    {
        return _x+nextBounds(-_y,_y,_g);
    }

    public float nextVariant(float _x, float _y)
    {
        return nextVariant(_x,_y,1f);
    }

    public double nextVariant(double _x, double _y, double _g)
    {
        return _x+nextBounds((float)-_y,(float)_y,(float)_g);
    }

    public double nextVariant(double _x, double _y)
    {
        return nextVariant((double)_x,(double)_y,(double)1);
    }

    public float nextAbout(float _x, float _y, float _g)
    {
        return ((int)((_x+((next(_y*2f)-_y)*_x))/_g))*_g;
    }

    public float nextAbout(float _x, float _y)
    {
        return nextAbout(_x,_y, 0.00001f);
    }

    public static void main(String[] args)
    {
        int[] _slots = new int[256];
        ArcRand _rand = from(0x7f01);
        for(int _j = 0; _j<0x7000000; _j++)
        {
            _slots[_rand.next()]++;
        }

        for(int _i = 0; _i<_slots.length; _i++)
        {
            System.out.println(_i+" = "+_slots[_i]);
        }
        System.out.println();
    }

    public int[] getContext() {
        return _ctx;
    }
}