package com.github.terefang.randy.utils;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DataUtilInputStream extends DataInputStream {

	public DataUtilInputStream(InputStream in) {
		super(in);
	}

	public int readLeChar() throws IOException
	{
		int v = read();
		v|= read()<<8;
		return v;
	}
     
	public char[] readLeChars(int len) throws IOException
	{
		char[] v = new char[len];
		for(int i=0; i<len; i++)
		{
			v[i]=(char)readLeChar();
		}
		return v;
	}
	
	public double readLeDouble() throws IOException
	{
		return Double.longBitsToDouble(readLeLong());
	}
	
	public double readLeFloat() throws IOException
	{
		return Float.intBitsToFloat(readLeInt());
	}

	public int readLeInt() throws IOException
	{
		int v = (readByte()& 0xff);
		v |= (readByte()& 0xff)<<8;
		v |= (readByte()& 0xff)<<16;
		v |= (readByte()& 0xff)<<24;
		return v;
	}

	public long readLeLong() throws IOException
	{
		long v = (readByte()& 0xff);
		v |= (readByte()& 0xff)<<8;
		v |= (readByte()& 0xff)<<16;
		v |= (readByte()& 0xff)<<24;

		v |= (readByte()& 0xff)<<32;
		v |= (readByte()& 0xff)<<40;
		v |= (readByte()& 0xff)<<48;
		v |= (readByte()& 0xff)<<56;
		return v;
	}

	public int readLeShort() throws IOException
	{
		int v = (readByte()& 0xff);
		v |= readByte()<<8;
		return v;
	}

	public String readAscii(int len) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<len; i++)
		{
			sb.append((char)readByte());
		}
		return sb.toString();
	}
	
	public String readAsciiZ(int len) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		int i=0;
		while((i = readByte())>0)
		{
			sb.append((char)i);
		}
		return sb.toString();
	}
	
	public String readPascalString() throws IOException
	{
		StringBuilder sb = new StringBuilder();
		int i=readByte();
		while(i-- > 0)
		{
			sb.append((char)readByte());
		}
		return sb.toString();
	}

	public String readPascalShortString() throws IOException
	{
		StringBuilder sb = new StringBuilder();
		int i=readLeShort();
		while(i-- > 0)
		{
			sb.append((char)readByte());
		}
		return sb.toString();
	}

	public String readPascalLongString() throws IOException
	{
		StringBuilder sb = new StringBuilder();
		int i=readLeInt();
		while(i-- > 0)
		{
			sb.append((char)readByte());
		}
		return sb.toString();
	}

	
}
