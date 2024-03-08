package com.github.terefang.randy.rng;

import com.github.terefang.randy.rng.impl.RandomDistinct64;

public class Distinct64Random implements IRandom
{
    RandomDistinct64 _rng = new RandomDistinct64();
    int _state1=0;
    long _state2=0;
    public void setSeed(long _s) {
        _rng.setSeed(_s);
    }

    public byte nextByte()
    {
        if((this._state1 % 8)==0)
        {
            this._state2 = this._rng.nextLong();
            this._state1 = 0;
        }
        else
        {
            this._state2 >>>= 8;
        }
        this._state1++;
        return (byte) (this._state2 & 0xff);
    }
}