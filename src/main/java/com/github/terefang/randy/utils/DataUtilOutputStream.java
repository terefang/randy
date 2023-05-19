package com.github.terefang.randy.utils;

import java.io.*;

public class DataUtilOutputStream extends DataOutputStream {

	public DataUtilOutputStream(OutputStream out) {
		super(out);
	}

	public void writeLeChar(int v) throws IOException
	{
		writeByte(v & 0xff);
		writeByte((v>>>8) & 0xff);
	}
     
	public void writeLeChar(char v) throws IOException
	{
		writeByte(v & 0xff);
		writeByte((v>>>8) & 0xff);
	}
     
	public void writeLeChars(String s) throws IOException
	{
		for(int i=0; i<s.length(); i++)
		{
			writeLeChar(s.charAt(i));
		}
	}
	
	public void writeLeDouble(double v) throws IOException
	{
		writeLeLong(Double.doubleToRawLongBits(v));
	}
	
	public void writeLeFloat(float v) throws IOException
	{
		writeLeInt(Float.floatToRawIntBits(v));
	}

	public void writeLeInt(int v) throws IOException
	{
		writeByte(v & 0xff);
		writeByte((v>>>8) & 0xff);
		writeByte((v>>>16) & 0xff);
		writeByte((v>>>24) & 0xff);
	}

	public void writeLeLong(long v) throws IOException
	{
		writeByte((int)(v & 0xff));
		writeByte((int)((v>>>8) & 0xff));
		writeByte((int)((v>>>16) & 0xff));
		writeByte((int)((v>>>24) & 0xff));

		writeByte((int)((v>>>32) & 0xff));
		writeByte((int)((v>>>40) & 0xff));
		writeByte((int)((v>>>48) & 0xff));
		writeByte((int)((v>>>56) & 0xff));
	}

	public void writeLeShort(int v) throws IOException
	{
		writeByte(v & 0xff);
		writeByte((v>>>8) & 0xff);
	}

	public void writeAscii(String s) throws IOException
	{
		for(int i=0; i<s.length(); i++)
		{
			writeByte(s.charAt(i) & 0xff);
		}
	}
	
	public void writeAsciiZ(String s) throws IOException
	{
		writeAscii(s);
		writeByte(0);
	}
	
	public void writePascalString(String s) throws IOException
	{
		if(s.length()<256)
		{
			writeByte(s.length() & 0xff);
			writeAscii(s);
		}
		else
		{
			throw new IOException("String to long for Pascal Encoding");
		}
	}

	public void writePascalShortString(String s) throws IOException
	{
		if(s.length()<65536)
		{
			writeLeShort(s.length() & 0xffff);
			writeAscii(s);
		}
		else
		{
			throw new IOException("String to long for Pascal Encoding");
		}
	}

	public void writePascalLongString(String s) throws IOException
	{
		writeLeLong(s.length());
		writeAscii(s);
	}
	
	public static DataUtilOutputStream createChunkFile(File file, int fourccSkyb, int i, byte[] bs) throws IOException 
	{
		DataUtilOutputStream fd = new DataUtilOutputStream(new FileOutputStream(file));
		fd.writeInt(fourccSkyb);
		fd.writeLeInt(i);
		fd.writeLeInt(bs.length);
		if(bs.length>0)
			fd.write(bs);
		return fd;
	}
	
	public void writeVersionedChunk(int fourcc, int version, byte[] bs) throws IOException 
	{
		writeInt(fourcc | (1<<31));
		writeLeInt(version);
		writeLeInt(bs.length);
		write(bs);
	}

	public void writeChunk(int fourcc, byte[] bs) throws IOException 
	{
		writeInt(fourcc);
		writeLeInt(bs.length);
		write(bs);
	}

	public void writeVersionedChunk(int fourcc, int version, int len) throws IOException 
	{
		writeInt(fourcc);
		writeLeInt(version);
		writeLeInt(len);
	}

	public void writeChunk(int fourcc, int len) throws IOException 
	{
		writeInt(fourcc);
		writeLeInt(len);
	}

}
