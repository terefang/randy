package com.github.terefang.randy.rng.impl;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class Lehmer32
{
    long pstate = 0;
    int rstate = 0;
    
    int xstate = 0;
    
    public void init(long _s)
    {
        this.pstate = _s;
        this.rstate = 0;
        this.xstate = 0;
    }
    
    static long MAGIC_1 = 0xe120fc15L;
    static long MAGIC_2 = 0x4a39b70dL;
    static long MAGIC_3 = 0x12fad5c9L;
    
    public int nextByte()
    {
        try
        {
            if((this.rstate)%4 == 0)
            {
                this.xstate = this.nextInt();
            }
            
            switch(this.rstate%4)
            {
                case 3:
                    return (int) ((this.xstate >>> 24) & 0xff);
                case 2:
                    return (int) ((this.xstate >>> 16) & 0xff);
                case 1:
                    return (int) ((this.xstate >>> 8) & 0xff);
                case 0:
                default:
                    return (int) (this.xstate & 0xff);
            }
        }
        finally
        {
            this.rstate++;
        }
    }
    
    public int nextInt()
    {
        this.pstate = (this.pstate + MAGIC_1);
        this.pstate = (this.pstate >>> 32) ^ (this.pstate & 0xffffffffL);
        this.pstate = (this.pstate & 0xffffffffL);

        long _tmp =this.pstate * MAGIC_2;
        _tmp = (_tmp >>> 32) ^ (_tmp & 0xffffffffL);
        _tmp = _tmp * MAGIC_3;
        _tmp = (_tmp >>> 32) ^ (_tmp & 0xffffffffL);
        return (int) (_tmp & 0xffffffffL);
    }
    
    static long MAGIC_4 = 0x0123456789L;
    
    public static Lehmer32 from(long... _seed)
    {
        Lehmer32 _rand = new Lehmer32();
        _rand.init(MAGIC_4);
        for(long _t : _seed)
        {
            _rand.pstate += _t;
        }
        return _rand;
    }
    
    public static Lehmer32 from(int... _seed)
    {
        Lehmer32 _rand = new Lehmer32();
        _rand.init(MAGIC_4);
        for(long _t : _seed)
        {
            _rand.pstate += _t;
        }
        return _rand;
    }
    
    public static Lehmer32 from(byte[] _seed)
    {
        UUID    _uuid = UUID.nameUUIDFromBytes(_seed);
        return Lehmer32.from(_uuid.getLeastSignificantBits(), _uuid.getMostSignificantBits());
    }
    
    public static Lehmer32 from(String _text)
    {
        UUID    _uuid = UUID.nameUUIDFromBytes(_text.getBytes(StandardCharsets.UTF_8));
        return Lehmer32.from(_uuid.getLeastSignificantBits(), _uuid.getMostSignificantBits());
    }
}
