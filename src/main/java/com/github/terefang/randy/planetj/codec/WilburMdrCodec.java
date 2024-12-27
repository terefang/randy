package com.github.terefang.randy.planetj.codec;


import com.github.terefang.randy.planetj.PlanetJ;
import com.github.terefang.randy.utils.LEDataInputStream;
import com.github.terefang.randy.utils.LEDataOutputStream;
import lombok.SneakyThrows;

import java.io.*;

public class WilburMdrCodec {

    @SneakyThrows
    public static final PlanetJ importMDR(PlanetJ _j, File _f)
    {
        double[] _attr = peekMDR_Attr(_f);
        int[] _wh = peekMDR_HW(_f);
        int[] _info = peekMDR_Info(_f);
        double _scale;
        switch (_info[1])
        {
            case 1: // 16 bit int
                _scale = 1./32767.;
                break;
            case 2: // 8 bit int
                _scale = 1./127.;
                break;
            case 0: // 32 bit float
            case 4: // 64 bit double
            default:
                _scale = 1./65535.;
                break;
        }
        _j.setImported(true);
        _j.setWidth(_wh[0]);
        _j.setHeight(_wh[1]);
        double[] _z = readMDR(_f,_wh[0],_wh[1],_scale);
        _j.heights = new double[_wh[0]][_wh[1]];
        int _i = 0;
        for(int _h=_wh[1]-1; _h>=0; _h--)
        {
            for(int _w=0; _w<_wh[0]; _w++)
            {
                _j.heights[_w][_h] = _z[_i];
                _i++;
            }
        }
        return _j;
    }

    public static final double[] peekMDR_Attr(File _f) throws IOException
    {
        try (LEDataInputStream _in = new LEDataInputStream(new BufferedInputStream(new FileInputStream(_f)));)
        {
            double[] _ret = new double[8];
            if(_in.readInt() == 0x52424c57)
            {
                _in.skipBytes(0x104);
                for(int _i = 0; _i<_ret.length;_i++)
                {
                    _ret[_i] = _in.readDouble();
                }
                return _ret;
            }
        }
        catch (Exception _xe)
        {
            return null;
        }
        return null;
    }

    public static final int[] peekMDR_Info(File _f) throws IOException
    {
        try (LEDataInputStream _in = new LEDataInputStream(new BufferedInputStream(new FileInputStream(_f)));)
        {
            int[] _ret = new int[2];
            if(_in.readInt() == 0x52424c57)
            {
                _in.skipBytes(0x144);
                _ret[0] = _in.readInt();
                _ret[1] = _in.readInt();
                return _ret;
            }
        }
        catch (Exception _xe)
        {
            return null;
        }
        return null;
    }

    public static final int[] peekMDR_HW(File _f) throws IOException
    {
        try (LEDataInputStream _in = new LEDataInputStream(new BufferedInputStream(new FileInputStream(_f)));)
        {
            int[] _ret = new int[2];
            if(_in.readInt() == 0x52424c57)
            {
                _in.skipBytes(0x164);
                _ret[0] = _in.readInt();
                _ret[1] = _in.readInt();
                return _ret;
            }
        }
        catch (Exception _xe)
        {
            return null;
        }
        return null;
    }

    public static final double[] readMDR(File _f, int _w, int _h, double _scale) throws IOException
    {
        try (LEDataInputStream _in = new LEDataInputStream(new BufferedInputStream(new FileInputStream(_f)));)
        {
            double[] _ret = new double[_w*_h];
            if(_in.readInt() == 0x52424c57)
            {
                _in.skipBytes(0x148);
                int _type = _in.readInt();
                _in.skipBytes(0x18);
                int _wx = _in.readInt();
                int _hx = _in.readInt();
                if((_w == _wx) && (_h == _hx))
                {
                    _in.skipBytes(0x400-0x170);
                    for(int _i = 0; _i<_ret.length;_i++)
                    {
                        switch (_type)
                        {
                            case 0: // 32 bit float
                                _ret[_i] = _in.readFloat()*_scale;
                                break;
                            case 1: // 16 bit int
                                _ret[_i] = _in.readShort()*_scale;
                                break;
                            case 2: // 8 bit int
                                _ret[_i] = _in.readByte()*_scale;
                                break;
                            case 4: // 64 bit double
                            default:
                                _ret[_i] = _in.readDouble()*_scale;
                                break;
                        }
                    }
                    return _ret;
                }
            }
        }
        catch (Exception _xe)
        {
            return null;
        }
        return null;
    }

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
