package com.github.terefang.randy.rng;

import lombok.SneakyThrows;

import java.security.MessageDigest;

public class BlockRandom implements IRandom
{
    private static final int BLOCKBUFF = 0x1000;
    private static final int BLOCKMASK = 0xfff;
    private byte cbuf[];
    private MessageDigest ctx;
    private int offs = 0;

    @Override
    public void setSeed(long s)
    {
        if(cbuf==null) cbuf = new byte[BLOCKBUFF];

        offs=0;
        for(int _i=0 ; _i<BLOCKBUFF ; _i++)
        {
            cbuf[_i]=(byte) (((s >>> (_i % 56))&0xff) ^ _i);
        }
        md5ify();
    }

    @Override
    public byte nextByte()
    {
        byte b=(byte) (cbuf[offs & BLOCKMASK] & 0xff);
        offs=(offs+1) & BLOCKMASK;
        if(offs==0) md5ify();
        return b;
    }

    @SneakyThrows
    private void md5ify()
    {
        if(ctx==null) ctx = MessageDigest.getInstance("MD5");

        for(int _i=0; _i<BLOCKBUFF; _i+=ctx.getDigestLength())
        {
            ctx.reset();
            ctx.update(cbuf, 0, BLOCKBUFF);
            ctx.digest(cbuf, _i, ctx.getDigestLength());
        }
    }
}
