package com.github.terefang.randy.rng;

import com.github.terefang.randy.rng.impl.MersenneTwister;
import lombok.SneakyThrows;

import java.security.MessageDigest;

public class MTRandom implements IRandom
{
    private MersenneTwister MT;

    @Override
    public void setSeed(long s)
    {
        if(MT==null)
        {
            MT = new MersenneTwister();
        }
        MT.setSeed(s);
    }

    @Override
    public byte nextByte()
    {
        return MT.nextByte();
    }

}
