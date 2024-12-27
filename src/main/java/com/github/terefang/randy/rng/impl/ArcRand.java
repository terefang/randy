package com.github.terefang.randy.rng.impl;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ArcRand {
    int[] _ctx;
    int _a, _b;

    boolean vmpcMode=true;

    public boolean isVmpcMode() {
        return vmpcMode;
    }

    public void setVmpcMode(boolean vmpcMode) {
        this.vmpcMode = vmpcMode;
    }

    public static ArcRand from(long... _seed)
    {
        ArcRand _rand = new ArcRand();
        _rand.init();
        _rand.bitfeed(_seed);
        return _rand;
    }

    public static ArcRand from(int... _seed)
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
        byte[] _buf = new byte[_seeds.length*4];
        for(int _i = 0; _i<_seeds.length; _i++)
        {
            _buf[(_i*4)] = (byte) ((_seeds[_i]>>>24) & 0xff);
            _buf[(_i*4)+1] = (byte) ((_seeds[_i]>>>16) & 0xff);
            _buf[(_i*4)+2] = (byte) ((_seeds[_i]>>>8) & 0xff);
            _buf[(_i*4)+3] = (byte) ((_seeds[_i]) & 0xff);
        }
        this.ksa(_buf);
    }

    public void bitfeed(long... _seeds)
    {
        byte[] _buf = new byte[_seeds.length*8];
        for(int _i = 0; _i<_seeds.length; _i++)
        {
            _buf[(_i*8)] = (byte) ((_seeds[_i]>>>56) & 0xff);
            _buf[(_i*8)+1] = (byte) ((_seeds[_i]>>>48) & 0xff);
            _buf[(_i*8)+2] = (byte) ((_seeds[_i]>>>40) & 0xff);
            _buf[(_i*8)+3] = (byte) ((_seeds[_i]>>>32) & 0xff);
            _buf[(_i*8)+4] = (byte) ((_seeds[_i]>>>24) & 0xff);
            _buf[(_i*8)+5] = (byte) ((_seeds[_i]>>>16) & 0xff);
            _buf[(_i*8)+6] = (byte) ((_seeds[_i]>>>8) & 0xff);
            _buf[(_i*8)+7] = (byte) ((_seeds[_i]) & 0xff);
        }
        this.ksa(_buf);
    }

    public void bitfeed(byte[] _seeds)
    {
        this.ksa(_seeds);
    }

    public void init()
    {
        this._ctx = new int[256];
        for(int _i = 0 ; _i<256; _i++)
        {
            this._ctx[_i] = _i;
        }
    }

    public void ksa(byte[] key)
    {
        if(this.vmpcMode)
        {
            this._a = 0;
            for(int _i=0 ; _i<768; _i++)
            {
                this._a = this._ctx[(this._a + this._ctx[_i & 0xff]+ key[_i % key.length]) & 0xff];
                int _t = this._ctx[_i & 0xff];
                this._ctx[_i & 0xff] = this._ctx[this._a & 0xff];
                this._ctx[this._a & 0xff] = _t;
            }
            this._b = 0;
        }
        else
        {
            int _j = 0;
            for(int _i=0 ; _i<256; _i++)
            {
                _j = (_j + this._ctx[_i] + key[_i % key.length]) & 0xff;
                int _t = this._ctx[_i];
                this._ctx[_i] = this._ctx[_j];
                this._ctx[_j] = _t;
            }
        }
    }

    public void ksax(byte[] key)
    {
        if(this.vmpcMode)
        {
            this._a = 0;
            for(int _i=0 ; _i<768; _i++)
            {
                this._a = this._ctx[(this._a + this._ctx[_i & 0xff]+ key[_i % key.length]) & 0xff];
                int _t = this._ctx[_i & 0xff];
                this._ctx[_i & 0xff] = this._ctx[this._a & 0xff];
                this._ctx[this._a & 0xff] = _t;
            }
            this._b = 0;
        }
        else
        {
            int _j = 0;
            for(int _i=0 ; _i<4096; _i++)
            {
                _j = (_j + this._ctx[_i & 0xff] + key[_i % key.length]) & 0xff;
                int _t = this._ctx[_i & 0xff];
                this._ctx[_i & 0xff] = this._ctx[_j];
                this._ctx[_j] = _t;
            }
        }
    }

    public int next()
    {
        if(this.vmpcMode)
        {
            this._a = this._ctx[(this._a + this._ctx[this._b & 0xff]) & 0xff];
            int _ret = this._ctx[(this._ctx[this._ctx[this._a & 0xff] & 0xff]+1) & 0xff];
            int _t = this._ctx[this._b & 0xff];
            this._ctx[this._b & 0xff] = this._ctx[this._a & 0xff];
            this._ctx[this._a & 0xff] = _t;

            this._b = (this._b+1) & 0xff;
            return _ret;
        }
        else
        {
            this._a = (this._a+1) & 0xff;
            this._b= (this._b+this._ctx[this._a]) & 0xff;

            int _t = this._ctx[this._b];
            this._ctx[this._b] = this._ctx[this._a];
            this._ctx[this._a] = _t;

            int _ret = this._ctx[(this._ctx[this._b] + this._ctx[this._a]) & 0xff];
            return _ret;
        }
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
    
    public long next64() { return ((long) next32() <<32L)|(~next32() & 0xffffffffffffL); }
    public double nextDouble()
    {
        return (next64() & 0x7ffffffffffffffL)/((double)0x800000000000000L);
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