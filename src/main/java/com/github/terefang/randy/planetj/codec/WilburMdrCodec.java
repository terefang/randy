package com.github.terefang.randy.planetj.codec;


import com.github.terefang.randy.utils.LEDataOutputStream;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class WilburMdrCodec {

    public static final void writeMDR(File _f, int _w, int _h, double _res, double _min, double _max, double[] z) throws IOException
    {
        LEDataOutputStream _out = new LEDataOutputStream(new BufferedOutputStream(new FileOutputStream(_f), 8192<<8));
//// make sure we're aligned on 2-byte boundaries for this declaration
//#pragma pack(push)
//#pragma pack(2)
//
//// map structure definitions
//struct MDRHeader
//{
//
//    union
//    {
//
//        struct
//        {
//
//00000000  57 4c 42 52 00 04 00 00  00 00 00 00 00 00 00 00  |WLBR............|
//00000010  00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00  |................|
//*
//00000100  00 00 00 00 00 00 00 00  -- -- -- -- -- -- -- --  |................|
//            char   Chunk[4];    // name of chunk - should be "WLBR"
//            int    Next;        // offset in file to next data (unused)
//
//            char   Name[256];   // name associated with the data (unused)
        _out.write("WLBR".getBytes());
        _out.writeInt(0x400);
        _out.writeZero(256);
//
//00000100  -- -- -- -- -- -- -- --  00 00 00 00 00 00 f0 bf  |................|
//00000110  00 00 00 00 00 00 f0 3f  00 00 00 00 00 00 f0 3f  |.......?.......?|
//00000120  00 00 00 00 00 00 f0 bf  00 00 00 00 00 40 8f c0  |.............@..|
//00000130  00 00 00 40 ce d7 a8 40  00 00 00 00 00 00 40 3f  |...@...@......@?|
//00000140  00 00 00 00 00 00 40 3f  -- -- -- -- -- -- -- --  |......@?........|
//            double Left;        // min longitude
//            double Right;       // max longitude
//            double Top;         // max latitude
//            double Bottom;      // min latitude
        _out.writeDouble(-1);
        _out.writeDouble(1);
        _out.writeDouble(1);
        _out.writeDouble(-1);
//
//            double Min;         // lowest point on surface
//            double Max;         // highest point on surface
        _out.writeDouble(_min);
        _out.writeDouble(_max);
//
//            double XRes;        // distance between X points (right - left) / XSize
//            double YRes;        // distance between Y points (top - bottom) / YSize
        _out.writeDouble(_res);
        _out.writeDouble(_res);
//
//00000140  -- -- -- -- -- -- -- --  00 00 00 00 00 01 00 00  |......@?........|
//            int    Version;     // version number (unused, should be 0)
//            int    TypeInfo;    // type information (should always be 0)
//                  0 -> 32 bit float
//                  1 -> 16 bit int
//                  2 -> 8 bit int
        _out.writeInt(0);
        _out.writeInt(0);
//
//00000150  00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00  |................|
//00000160  00 00 00 00 00 00 00 00  -- -- -- -- -- -- -- --  |................|
//            char   SurfName[8]; // name of data block (unused)
//            void** SurfData;    // pointer to surface data (unused)
//
//            char   PicName[8];  // name of picture block (unused)
//            void** PicData;     // pointer to image data (unused)
        _out.writeLong(0L);
        _out.writeLong(0L);
        _out.writeLong(0L);
//
// 00000160  -- -- -- -- -- -- -- --  00 10 00 00 00 10 00 00  |................|
//            int    XSize;       // width of blocks
//            int    YSize;       // height of blocks
        _out.writeInt(_w);
        _out.writeInt(_h);
//
//00000170  20 fe 6f c2 39 9f 82 45  00 00 00 00 00 00 00 00  | .o.9..E........|
        _out.writeLong(0x45829f39c26ffe20L);
        _out.writeLong(0L);
//
//00000180  00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00  |................|
//*
//
//
//        }; // struct
//
//        struct
//        {
//            char Padding[1024]; // make the structure 1024 bytes long
//        };
        for(int _i=0x400-0x180; _i>0; _i-=0x10)
        {
            _out.writeLong(0L);
            _out.writeLong(0L);
        }
//
//    }; // union
//00000400 ....
//
//}; // MDRHeader
        for(int _y=0; _y<z.length; _y++)
        {
            _out.writeFloat((float) z[_y]);
        }

        _out.flush();
        _out.close();
    }

}
