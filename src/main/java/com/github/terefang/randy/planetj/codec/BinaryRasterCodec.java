package com.github.terefang.randy.planetj.codec;

import com.github.terefang.randy.utils.LEDataOutputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class BinaryRasterCodec
{
	public static void writeVTPBT(File f, int w, int h, double glong, double glat, double[] z)
	{
		try
		{
			LEDataOutputStream buf = new LEDataOutputStream(new BufferedOutputStream(new FileOutputStream(f), 8192 << 8));
			
			//	Byte Offset	Length	Contents	Description
			//	0	10	"binterr1.3"	A marker which indicates that this is a BT 1.3 file
			buf.write("binterr1.3".getBytes());
			
			//	10	4 (int)	Columns	Width (east-west) dimension of the height grid.
			buf.writeInt(w);
			
			//	14	4 (int)	Rows	Height (north-south) dimension of the height grid.
			buf.writeInt(h);
			
			//	18	2 (short)	Data size	Bytes per elevation grid point, either 2 or 4.
			buf.writeShort(4);
			
			//	20	2 (short)	Floating-point flag	If 1, the data consists of floating point values (float), otherwise they are signed integers.
			buf.writeShort(1);
			
			//	22	2 (short)	Horizontal units	0: Degrees
			//		1: Meters
			//		2: Feet (international foot = .3048 meters)
			//		3: Feet (U.S. survey foot = 1200/3937 meters)
			buf.writeShort(1);
			
			//	24	2 (short)	UTM zone	Indicates the UTM zone (1-60) if the file is in UTM.  Negative zone numbers are used for the southern hemisphere.
			buf.writeShort(0);
			
			//	26	2 (short)	Datum	Indicates the Datum, see Datum Values below.
			buf.writeShort(6326);
			
			//	28	8 (double)	Left extent	The extents are specified in the coordinate space (projection) of the file.  For example, if the file is using UTM, then the extents are in UTM coordinates.
			//	36	8 (double)	Right extent
			//	44	8 (double)	Bottom extent
			//	52	8 (double)	Top extent
			double sx = (360.0/w);
			double sy = sx;
			double sr = (180.0/h);
			
			double vleft = ((-sr*h)+glong+sr);
			double vtop = ((sr*h)+(glat*2.0)-sr);
			double vright = vleft + (sx*w);
			double vbottom = vtop - (sy*h);
			
			buf.writeDouble(vleft);
			buf.writeDouble(vright);
			buf.writeDouble(vbottom);
			buf.writeDouble(vtop);
			
			//	60	2 (short)	External projection	0: Projection is fully described by this header
			//		1: Projection is specified in a external .prj file
			buf.writeShort(0);
			
			//	62	4 (float)	Scale (vertical units)	Vertical units in meters, usually 1.0.  The value 0.0 should be interpreted as 1.0 to allow for backward compatibility.
			buf.writeFloat(1.0f);
			
			//	66-255	190	unused	Bytes of value 0 are used to pad the rest of the header.
			buf.write(new byte[190]);
			
			//etc.	Raster data are stored in nrows rows from south to north, each row consisting of ncols values from west to east.
			//
			//Raster values should be 'little-endian' values either 32-bit float or 16-bit integer according to the header value.
			for(int iy=0; iy<h; iy++)
			{
				for(int ix=0; ix<w; ix++)
				{
				buf.writeFloat((float) z[(ix*h)+iy]);
				}
			}

			buf.flush();
			buf.close();
		}
		catch(Exception xe)
		{
			xe.printStackTrace();
		}
	}
}

