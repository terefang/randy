package com.github.terefang.randy.rng;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public interface IRandom
{
    public void setSeed(long s);
    default public void setSeed(String _text) {
        setSeed(UUID.nameUUIDFromBytes(_text.getBytes(StandardCharsets.UTF_8)).getMostSignificantBits());
    }

    default public void setSeed(byte[] _text) {
        setSeed(UUID.nameUUIDFromBytes(_text).getMostSignificantBits());
    }
    public byte nextByte();
    default public short nextShort()
    {
        return (short)((nextByte()&0xff) | ((nextByte()&0xff)<<8));
    }
    default public int nextInt()
    {
        return (int)((nextByte()&0xff)
                | ((nextByte()&0xff)<<8)
                | ((nextByte()&0xff)<<16)
                | ((nextByte()&0xff)<<24));
    }
    default public long nextLong()
    {
        return (long)((nextByte()&0xffL)
                | ((nextByte()&0xffL)<<8)
                | ((nextByte()&0xffL)<<16)
                | ((nextByte()&0xffL)<<24)
                | ((nextByte()&0xffL)<<32)
                | ((nextByte()&0xffL)<<40)
                | ((nextByte()&0xffL)<<48)
                | ((nextByte()&0xffL)<<56));
    }
    default public void nextBytes(byte[] dest, int dest_len)
    {
        for(int _i = 0; _i<dest_len; _i++)
        {
            dest[_i] = nextByte();
        }
    }

    default public byte[] nextBytes(int dest_len)
    {
        byte[] dest = new byte[dest_len];
        for(int _i = 0; _i<dest_len; _i++)
        {
            dest[_i] = nextByte();
        }
        return dest;
    }

    static float _INT23 = (1<<23);
    static int _MASK23 = (1<<23)-1;
    default public float nextFloat()
    {
        int _i = nextInt();
        return ((float)(_i & _MASK23)) / _INT23;
    }
    default public float nextFloatX()
    {
        int _i = nextInt();
        boolean _s = (_i & 0x01000000) != 0;
        if(_s)
        {
            return -(((float)(_i & _MASK23)) / _INT23);
        }
        return ((float)(_i & _MASK23)) / _INT23;
    }

    static double _LONG52 = (1L<<52);
    static long _MASK52 = (1L<<52)-1L;
    default public double nextDouble()
    {
        long _l = nextLong();
        return ((double)(_l & _MASK52)) / _LONG52;
    }

    default public double nextDoubleX()
    {
        long _l = nextLong();
        boolean _s = (_l & 0x0100000000000000L) != 0L;
        if(_s)
        {
            return -(((double)(_l & _MASK52)) / _LONG52);
        }
        return ((double)(_l & _MASK52)) / _LONG52;
    }

    default public double nextGaussian()
    {
        long u = this.nextLong();
        final long c = Long.bitCount(u) - 32L << 32;
        u *= 0xC6AC29E4C6AC29E5L;
        return 0x1.fb760cp-35 * (c + (u & 0xFFFFFFFFL) - (u >>> 32));
    }

    default public double nextGaussian(double _bound, double _factor, double _base) {
        double _ret = -2.0*_bound;
        while(Math.abs(_ret)>=_bound)
        {
            _ret = this.nextGaussian();
        }
        return (_ret*_factor)+_base;
    }
}
