package com.github.terefang.randy.rng;

import com.github.terefang.randy.rng.impl.ArcRand;

public class ArcRandom implements IRandom
{
    ArcRand _rng = new ArcRand();

    @Override
    public void setSeed(long s) {
        _rng = ArcRand.from(s);
    }

    @Override
    public void setSeed(String s) {
        _rng = ArcRand.from(s);
    }

    @Override
    public void setSeed(byte[] s) {
        _rng = ArcRand.from(s);
    }

    @Override
    public byte nextByte() {
        return (byte) (_rng.next()&0xff);
    }
}
