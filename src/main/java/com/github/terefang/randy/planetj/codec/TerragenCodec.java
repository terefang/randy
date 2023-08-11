package com.github.terefang.randy.planetj.codec;

import com.github.terefang.randy.utils.LEDataOutputStream;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TerragenCodec
{
	public static final void writeTER(File f, int w, int h, double[] z) throws IOException
	{
		LEDataOutputStream out = new LEDataOutputStream(new BufferedOutputStream(new FileOutputStream(f), 8192<<8));
		out.write("TERRAGEN".getBytes());
		out.write("TERRAIN ".getBytes());
		
		//	"SIZE" 4-byte marker, necessary. Must appear before any altitude data.
		//		The "SIZE" marker is followed by a 2-byte integer value equal to n - 1, followed by 2 bytes
		//		of padding. In square terrains, n is the number of data points on a side of the elevation
		//		image. In non-square terrains, n is equal to the number of data points along the shortest side.
		//		Example: a terrain with a heightfield 300 points in the x-direction and 400 points in the y-
		//		direction would have a size value of 299.
		out.write("SIZE".getBytes());
		out.writeShort(((w>h) ? h : w)-1);
		out.writeShort(0);
		
		//	"XPTS" 4-byte marker. Must appear after the "SIZE" marker. Must appear before any altitude data.
		//		The "XPTS" marker is followed by a 2-byte integer value xpts, followed by 2 bytes of
		//		padding. xpts is equal to the number of data points in the x-direction in the elevation image.
		out.write("XPTS".getBytes());
		out.writeShort(w);
		out.writeShort(0);
		
		//	"YPTS" 4-byte marker. Must appear after the "SIZE" marker. Must appear before any altitude data.
		//		The "YPTS" marker is followed by a 2-byte integer value ypts, followed by 2 bytes of
		//		padding. ypts is equal to the number of data points in the y-direction in the elevation image.
		out.write("YPTS".getBytes());
		out.writeShort(h);
		out.writeShort(0);
		
		//	"SCAL" 4-byte marker, optional. Must appear before any altitude data.
		//		The "SCAL" marker is followed by three intel-ordered 4-byte floating point values (x,y,z). It
		//		represents the scale of the terrain in metres per terrain unit. Default scale is currently
		//		(30,30,30). At present, Terragen can not use non-uniform scaling, so x, y and z must be equal.
		out.write("SCAL".getBytes());
		out.writeFloat(30f);
		out.writeFloat(30f);
		out.writeFloat(30f);
		
		//	"CRAD" 4-byte marker, optional. Must appear before any altitude data.
		//		The "CRAD" marker is followed by one intel-ordered 4-byte floating point value. It
		//		represents the radius of the planet being rendered and is measured in kilometres. The
		//		default value is 6370, which is the approximate radius of the Earth.
		out.write("CRAD".getBytes());
		out.writeFloat(6370f);
		
		//	"ALTW" 4-byte marker. Must appear after the "SIZE" marker. Must appear after the "XPTS" and
		//	"YPTS" markers (if they exist).
		//		ALTW stands for 'Altitude in 16-bit Words'. After The "ALTW" marker, the following appear
		//		in order:
		//			HeightScale, a 2-byte signed integer value.
		//			BaseHeight, a 2-byte signed integer value.
		//			Elevations, a sequence of 2-byte signed integers.
		//		There are (xpts * ypts) elevation integers, where xpts and ypts will have been set earlier in
		//		the "SIZE" chunk or the "XPTS" and "YPTS" chunks. The elevations are ordered such that
		//		the first row (y = 0) is read first from left to right, then the second (y = 1), and so on. The
		//		values in Elevations are not absolute altitudes. The absolute altitude of a particular point
		//		(in the same scale as x and y) is equal to BaseHeight Elevation * HeightScale / 65536.
		out.write("ALTW".getBytes());
		out.writeShort(Short.MAX_VALUE);
		out.writeShort(0);
		for(int y=0; y<z.length; y++)
		{
			out.writeShort((int) (z[y]/10.0));
		}
		
		out.write("EOF ".getBytes());
		out.flush();
		out.close();
	}
}
