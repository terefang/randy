package com.github.terefang.randy.rng;

import java.util.Random;

public class JavaRandom implements IRandom
{
    Random _rng = new Random(0x1ee7b33f);
    @Override
    public void setSeed(long s) {
        _rng.setSeed(s);
    }

    @Override
    public byte nextByte() {
        return (byte) _rng.nextInt(256);
    }
}
