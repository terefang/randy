package com.github.terefang.randy.rng;

public class GoldenRatio2Random extends GoldenRatioRandom
{
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
            this.stateC = Long.rotateLeft(fb, 47) - this.state;
            this.stateD = fb ^ fc;
        }
        return (byte) ((this.state>>>(this.offset*8)) & 0xff);
    }

}
