package com.github.terefang.randy.rng;

import com.github.terefang.randy.rng.impl.ArcRand;
import com.github.terefang.randy.rng.impl.Lehmer32;

public class Lehmer32Random implements IRandom
{
    Lehmer32 _rng = new Lehmer32();
    
    @Override
    public void setSeed(long s) {
        _rng = Lehmer32.from(s);
    }
    
    @Override
    public void setSeed(String s) {
        _rng = Lehmer32.from(s);
    }
    
    @Override
    public void setSeed(byte[] s) {
        _rng = Lehmer32.from(s);
    }
    
    @Override
    public byte nextByte() {
        return (byte) (_rng.nextByte()&0xff);
    }
}
