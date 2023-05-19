package com.github.terefang.randy.rng;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class GoldenRatioRandom implements IRandom
{
    long stateA;
    long stateB;
    long stateC;
    long stateD;

    long state;
    int offset = -1;

    @Override
    public void setSeed(long _seed)
    {
        this.stateA = _seed ^= 0x9E3779B97F4A7C15L;
        this.stateB = 0x3243F6A8885A308DL;
        this.stateC = _seed += 0x9E3779B97F4A7C15L;
        this.stateD = _seed += 0x9E3779B97F4A7C15L;
        for(int _i = 0; _i<(_seed & 0xffff); _i++)
        {
            nextLong();
        }
    }

    @Override
    public byte nextByte()
    {
        this.offset++;
        if((this.offset % 8) == 0)
        {
            final long fa = this.stateA;
            final long fb = this.stateB;
            final long fc = this.stateC;
            this.state = this.stateD;

            this.stateA = 0xD1342543DE82EF95L * this.state;
            this.stateB = fa + 0xC6BC279692B5C323L;
            this.stateC = Long.rotateLeft(fb, 41);
            this.stateD = fb ^ fc;
        }
        return (byte) ((this.state>>>(this.offset*8)) & 0xff);
    }

}
