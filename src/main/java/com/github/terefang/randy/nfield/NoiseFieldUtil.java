package com.github.terefang.randy.nfield;

import com.github.terefang.randy.map.ColorRamp;
import com.github.terefang.randy.planetj.PlanetJ;
import com.github.terefang.randy.utils.LEDataInputStream;
import com.github.terefang.randy.utils.LEDataOutputStream;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class NoiseFieldUtil
{
    static class SColor extends Color
    {
        public SColor(float r, float g, float b)
        { super(r, g, b); }
        public SColor(float r, float g, float b, float a)
        { super(r, g, b, a); }
        public SColor(int rgb)
        { super(rgb); }
        public SColor(int rgba, boolean hasalpha)
        { super(rgba, hasalpha); }
        public SColor(int r, int g, int b)
        { super(r, g, b); }
        public SColor(int r, int g, int b, int a)
        { super(r, g, b, a); }
        public SColor interpolate(SColor other, double d)
        {
            double inv = 1.0 - d;
            int a = (int) (other.getAlpha()*d + getAlpha()*inv);
            int r = (int) (other.getRed()*d + getRed()*inv);
            int g = (int) (other.getGreen()*d + getGreen()*inv);
            int b = (int) (other.getBlue()*d + getBlue()*inv);
            return new SColor(r,g,b,a);
        }
    }

    protected static int C2U(int r, int g, int b)
    {
        return (0xff000000|(r<<16)|(g<<8)|(b));
    }

    protected static int C2U(int r, int g, int b, int a)
    {
        return ((a<<24)|(r<<16)|(g<<8)|(b));
    }

    protected static int I2U(int x)
    {
        return ((x<<24)|(x<<16)|(x<<8)|(x));
    }

    public static void saveBImage(NoiseField nf, String fileName, String iType, double seaMax, double landMax)
    {
        // Create a buffered image in which to draw
        BufferedImage bufferedImage = new BufferedImage(nf.getWidth(), nf.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for(int y=0 ; y<nf.getHeight() ; ++y)
        {
            for(int x=0 ; x<nf.getWidth() ; ++x)
            {
                if(nf.getPoint(x,y)>=landMax)
                {
                    bufferedImage.setRGB(x,y,C2U(255,255,255));
                }
                else
                if(seaMax>=0.0)
                {
                    if(nf.getPoint(x,y)<0.0)
                    {
                        bufferedImage.setRGB(x,y,C2U(0, 0, 0));
                    }
                    else
                    {
                        double c = (255.0*(nf.getPoint(x,y)/landMax));
                        bufferedImage.setRGB(x,y,C2U((int)c, (int)c, (int)c));
                    }
                }
                else
                {
                    if(nf.getPoint(x,y)<=seaMax)
                    {
                        bufferedImage.setRGB(x,y,C2U(0,0,0));
                    }
                    else
                    if(nf.getPoint(x,y)<0.0)
                    {
                        double c = 127.0*(-nf.getPoint(x,y)/seaMax)+127.0;
                        bufferedImage.setRGB(x,y,C2U((int)c, (int)c, (int)c));
                    }
                    else
                    {
                        double c = (127.0*(nf.getPoint(x,y)/landMax))+127.0;
                        bufferedImage.setRGB(x,y,C2U((int)c, (int)c, (int)c));
                    }
                }
            }
        }

        if("png".equalsIgnoreCase(iType)
                || "jpg".equalsIgnoreCase(iType)
                || "bmp".equalsIgnoreCase(iType))
        {
            File file = new File(fileName);
            file.getParentFile().mkdirs();
            try
            {
                ImageIO.write(bufferedImage, iType, file);
            }
            catch(Exception e)
            {
                System.err.println(e.toString());
            }
        }
    }

    public static void saveTImage(NoiseField nf, String fileName, String iType, double seaMax, double landMax)
    {
        int landColor[][]={
                { 84, 194, 108 },
                {125, 196,  67 },
                {168, 186,  53 },
                {200, 169,  68 },
                {217, 154, 106 },
                {219, 147, 154 },
                {209, 152, 197 },
                {199, 168, 225 },
                {197, 190, 235 },
                {209, 215, 235 },
                {255, 255, 255 } };

        int seaColor[][]={
                { 97,  65, 247 },
                { 71,  89, 254 },
                { 53, 116, 243 },
                { 45, 143, 217 },
                { 0,  0, 160 } };

        // Create a buffered image in which to draw
        BufferedImage bufferedImage = new BufferedImage(nf.getWidth(), nf.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for(int y=0 ; y<nf.getHeight() ; ++y)
        {
            for(int x=0 ; x<nf.getWidth() ; ++x)
            {
                if(nf.getPoint(x,y)>=0.0)
                {
                    int sI=(int) ((nf.getPoint(x,y)/landMax)*(landColor.length-1));
                    if(sI>=(landColor.length-1))
                    {
                        bufferedImage.setRGB(x,y,C2U(landColor[(landColor.length-1)][0],landColor[(landColor.length-1)][1],landColor[(landColor.length-1)][2]));
                    }
                    else
                    {
                        SColor c1 = new SColor(landColor[sI][0],landColor[sI][1],landColor[sI][2]);
                        SColor c2 = new SColor(landColor[sI+1][0],landColor[sI+1][1],landColor[sI+1][2]);
                        SColor c3 = c1.interpolate(c2,(Math.abs(nf.getPoint(x,y))-Math.abs(sI*landMax/(landColor.length-1)))/(landMax/(landColor.length-1)));
                        bufferedImage.setRGB(x,y,c3.getRGB());
                    }
                }
                else
                {
                    int sI=(int)((nf.getPoint(x,y)/seaMax)*(seaColor.length-1));
                    if(sI>=(seaColor.length-1))
                    {
                        bufferedImage.setRGB(x,y,C2U(seaColor[seaColor.length-1][0],seaColor[seaColor.length-1][1],seaColor[seaColor.length-1][2]));
                    }
                    else
                    {
                        SColor c1 = new SColor(seaColor[sI][0],seaColor[sI][1],seaColor[sI][2]);
                        SColor c2 = new SColor(seaColor[sI+1][0],seaColor[sI+1][1],seaColor[sI+1][2]);
                        SColor c3 = c1.interpolate(c2,(Math.abs(sI*seaMax/(seaColor.length-1))-Math.abs(nf.getPoint(x,y)))/(seaMax/(seaColor.length-1)));
                        bufferedImage.setRGB(x,y,c3.getRGB());
                    }
                }
            }
        }

        if("png".equalsIgnoreCase(iType)
                || "jpg".equalsIgnoreCase(iType)
                || "bmp".equalsIgnoreCase(iType))
        {
            File file = new File(fileName);
            file.getParentFile().mkdirs();
            try
            {
                ImageIO.write(bufferedImage, iType, file);
            }
            catch(Exception e)
            {
                System.err.println(e.toString());
            }
        }
    }

    /*
     * function CloudExpCurve(v)
     *
     *   c = v - CloudCover
     *   if c < 0 then c=0
     *
     *   CloudDensity = 255 - ((CloudSharpness c) * 255)
     *
     *   return CloudDensity
     * end function
     */
    public static void saveCloudMap(NoiseField nf, String fileName, double cloudCover, double cloudSharpness)
    {
        NoiseField nf1 = nf.clone();
        nf1.normalize(0, 1);

        int blockStat[] = new int[256];
        for(int j=0; j<nf.getHeight(); j++)
        {
            for(int i=0; i<nf.getWidth(); i++)
            {
                blockStat[(int)(nf1.getPoint(i, j) * 0xff)]++;
            }
        }
        int sN=0;
        for(int j=255; j>=0; j--)
        {
            sN+=blockStat[j];
            blockStat[j]=sN;
        }

        double cloudCut=1;
        for(int j=255; j>=0; j--)
        {
            if((double)blockStat[j]/(double)sN > cloudCover)
            {
                cloudCut=1-((double)j/255.0);
                j=-1;
            }
        }

        BufferedImage bufferedImage = new BufferedImage(nf.getWidth(), nf.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for(int j=0; j<nf.getHeight(); j++)
        {
            for(int i=0; i<nf.getWidth(); i++)
            {
                double gx=nf1.getPoint(i, j)+cloudCut-1;
                if(gx<=0)
                {
                    gx=0;
                }
                else
                {
                    gx=255f* Math.pow(gx,cloudSharpness);
                }
                bufferedImage.setRGB(i, j, (
                        (((int)gx & 0xff) << 24)
                                |(((int)gx & 0xff) << 16)
                                |(((int)gx & 0xff) << 8)
                                |((int)gx & 0xff)));
            }
        }

        if(fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".bmp"))
        {
            File file = new File(fileName);
            file.getParentFile().mkdirs();
            try
            {
                ImageIO.write(bufferedImage, fileName.substring(fileName.length()-3), file);
            }
            catch(Exception e)
            {
                System.err.println(e.toString());
            }
        }
    }

    public static void saveNormalMap(NoiseField nf, String fileName, double bumpNess)
    {
        NoiseField nf1 = nf.clone();
        NoiseField nf2 = nf.clone();
        double fx[] = { 1, 0,-1,
                2, 0,-2,
                1, 0,-1 };
        double fy[] = { 1, 2, 1,
                0, 0, 0,
                -1,-2,-1 };

        nf1.normalize(0, 1);
        nf1.filterKernel(3, fx, 1.0);
        nf1.normalize(0, 1);

        nf2.normalize(0, 1);
        nf2.filterKernel(3, fy, 1.0);
        nf1.normalize(0, 1);

        BufferedImage bufferedImage = new BufferedImage(nf.getWidth(), nf.getHeight(), BufferedImage.TYPE_INT_RGB);
        for(int j=0; j<nf.getHeight(); j++)
        {
            for(int i=0; i<nf.getWidth(); i++)
            {
                double gx=nf1.getPoint(i, j);
                double gx2=gx*gx;
                double gy=nf2.getPoint(i, j);
                double gy2=gy*gy;

                double gz = bumpNess*Math.sqrt(1.0 - gx2 - gy2);
                double g = Math.sqrt(gx2 + gy2 + gz*gz);
                gx=256*gx/g;
                gy=256*gy/g;
                gz=256*gz/g;
                bufferedImage.setRGB(i, j, (((int)gx & 0xff) << 16)|(((int)gy & 0xff) << 8)|((int)gz & 0xff));
            }
        }

        if(fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".bmp"))
        {
            File file = new File(fileName);
            file.getParentFile().mkdirs();
            try
            {
                ImageIO.write(bufferedImage, fileName.substring(fileName.length()-3), file);
            }
            catch(Exception e)
            {
                System.err.println(e.toString());
            }
        }
    }

    public static void saveTImage(NoiseField nf, String fileName, double seaMax, double landMax)
    {
        saveTImage(nf, fileName, "png", seaMax, landMax);
    }

    public static void saveHF(NoiseField nf, String fileName)
    {
        try
        {
            File file = new File(fileName);
            file.getParentFile().mkdirs();
            FileOutputStream fh = new FileOutputStream(file);
            LEDataOutputStream dh = new LEDataOutputStream(fh);

            // HEAD_SIZE 16
            dh.writeBytes("BSTF");
            dh.writeInt(nf.getWidth()); // u32 SizeX;
            dh.writeInt(nf.getHeight()); // u32 SizeY;
            dh.writeFloat(30.0f); // f32 Scale;

            for(int i=0 ; i<nf.getWidth()*nf.getHeight() ; i++)
            {
                dh.writeFloat((float)nf.getData()[i]);
            }
            dh.writeBytes("EOF ");
            dh.close();
            fh.close();
        }
        catch(Exception e)
        {
            System.err.println(e.toString());
        }
    }

    public static NoiseField toNoiseField(PlanetJ _pl, boolean _n)
    {
        NoiseField nf = new NoiseField(_pl.getWidth(), _pl.getHeight());
        for(int _x=0 ; _x<nf.getWidth() ; _x++)
        {
            for(int _y=0 ; _y<nf.getHeight() ; _y++)
            {
                double _v = _pl.heights[_x][_y];
                if(Double.isNaN(_v)) _v=0.;
                nf.setPoint(_x,_y,_v);
            }
        }
        if(_n) nf.normalize(-1.,1.);
        return nf;
    }

    public static NoiseField readHF(String fileName)
    {
        NoiseField nf = null;
        try
        {
            BufferedImage _img = ImageIO.read(new File(fileName));
            nf = new NoiseField(_img.getWidth(), _img.getHeight());

            for(int _x=0 ; _x<nf.getWidth() ; _x++)
            {
                for(int _y=0 ; _y<nf.getHeight() ; _y++)
                {
                    int _rgb =_img.getRGB(_x,_y);
                    double _value = ((_rgb >>> 16) & 0xff)/255.;
                    _value += ((_rgb >>> 8) & 0xff)/255.;
                    _value += (_rgb & 0xff)/255.;
                    _value /= 3.;
                    nf.setPoint(_x,_y,_value);
                }
            }
        }
        catch(Exception e)
        {
            System.err.println(e.toString());
        }
        return nf;
    }

    public static int compareTo(NoiseField _nf1, NoiseField _nf2, double _delta, boolean _fakeNeg)
    {
        int _error = 0;
        try
        {
            for(int _x=0 ; _x<_nf1.getWidth() ; _x++)
            {
                for(int _y=0 ; _y<_nf1.getHeight() ; _y++)
                {
                    double _v1 = _nf1.getPoint(_x,_y);
                    double _v2 = _nf2.getPoint(_x,_y);
                    double _df = Math.abs(Math.min(_v1,_v2)-Math.max(_v1,_v2));
                    if(_fakeNeg && _v1<=0. && _v2<=0.)
                    {
                        // IGNORE
                    }
                    else if(_df > _delta)
                    {
                        _error++;
                    }
                }
            }
        }
        catch(Exception e)
        {
            System.err.println(e.toString());
        }
        return _error;
    }

    public static void saveBT(NoiseField nf, String fileName)
    {
        try
        {
            File file = new File(fileName);
            file.getParentFile().mkdirs();
            OutputStream fh = new FileOutputStream(file);
            OutputStream bh = new BufferedOutputStream(fh, 1024*1024);
            if(fileName.endsWith(".gz") || fileName.endsWith(".btz"))
            {
                GZIPOutputStream gh = new GZIPOutputStream(bh);
                bh=gh;
            }
            LEDataOutputStream dh = new LEDataOutputStream(bh);

            // HEAD_SIZE 16
            //	Byte(Offset)	Length		Contents
            //		0			10			"binterr1.3"
            dh.writeBytes("binterr1.3");
            //		10			4 (int)		Columns
            dh.writeInt(nf.getWidth());
            //		14			4 (int)		Rows
            dh.writeInt(nf.getHeight());
            //		18			2 (short)	Data size (2,4 bytes)
            dh.writeShort(4);
            //		20			2 (short)	doubleing-point flag (1 = double)
            dh.writeShort(1);
            //		22			2 (short)	Horizontal units
            //								0: Degrees
            //								1: Meters
            //								2: Feet (international foot = .3048 meters)
            //								3: Feet (U.S. survey foot = 1200/3937 meters)
            dh.writeShort(0);
            //		24			2 (short)	UTM zone
            dh.writeShort(0);
            // 		26			2 (short)	Datum
            dh.writeShort(0);
            //		28			8 (double)	Left extent
            dh.writeDouble(-1);
            //		36			8 (double)	Right extent
            dh.writeDouble(1);
            //		44			8 (double)	Bottom extent
            dh.writeDouble(-1);
            //		52			8 (double)	Top extent
            dh.writeDouble(1);
            //		60			2 (short)	External Projection (1 = extern .prj file)
            dh.writeShort(0);
            //		62			4 (double)	Vertical Units (0.0 => 1.0 compat)
            dh.writeInt(0);
            //		66-255		190			0x00 PAD
            for(int i=190; i>0; i--)
            {
                dh.writeByte(0);
            }

            for(int i=0; i<nf.getWidth(); i++)
            {
                for(int j=0; j<nf.getHeight(); j++)
                {
                    double gx = nf.getPoint(i, nf.getHeight()-(j+1));
                    dh.writeFloat((float)gx);
                }
            }
            dh.flush();
            bh.flush();
            dh.close();
            fh.close();
        }
        catch(Exception e)
        {
            System.err.println(e.toString());
        }
    }

    public static NoiseField loadBT(String fileName)
    {
        try
        {
            NoiseField nf;
            File file = new File(fileName);
            file.getParentFile().mkdirs();
            InputStream fh = new FileInputStream(file);
            InputStream bh = new BufferedInputStream(fh, 1024*1024);
            if(fileName.endsWith(".gz") || fileName.endsWith(".btz"))
            {
                GZIPInputStream gh = new GZIPInputStream(bh);
                bh=gh;
            }
            LEDataInputStream dh = new LEDataInputStream(bh);

            // HEAD_SIZE 16
            //	Byte(Offset)	Length		Contents
            //		0			10			"binterr1.3"
            byte[] buf = new byte[10];
            dh.readFully(buf);
            if(!"binterr1.3".equalsIgnoreCase(new String(buf)))
            {
                return null;
            }
            //		10			4 (int)		Columns
            int fW = dh.readInt();
            //		14			4 (int)		Rows
            int fH = dh.readInt();
            nf = new NoiseField(fW, fH);
            //		18			2 (short)	Data size (2,4 bytes)
            int dS = dh.readShort();
            //		20			2 (short)	doubleing-point flag (1 = double)
            int fF = dh.readShort();
            //		22			2 (short)	Horizontal units
            //								0: Degrees
            //								1: Meters
            //								2: Feet (international foot = .3048 meters)
            //								3: Feet (U.S. survey foot = 1200/3937 meters)
            dh.readShort();
            //		24			2 (short)	UTM zone
            dh.readShort();
            // 		26			2 (short)	Datum
            dh.readShort();
            //		28			8 (double)	Left extent
            dh.readLong();
            //		36			8 (double)	Right extent
            dh.readLong();
            //		44			8 (double)	Bottom extent
            dh.readLong();
            //		52			8 (double)	Top extent
            dh.readLong();
            //		60			2 (short)	External Projection (1 = extern .prj file)
            dh.readShort();
            //		62			4 (double)	Vertical Units (0.0 => 1.0 compat)
            dh.readInt();
            //		66-255		190			0x00 PAD
            for(int i=190; i>0; i--)
            {
                dh.readByte();
            }

            for(int i=0; i<nf.getWidth(); i++)
            {
                for(int j=0; j<nf.getHeight(); j++)
                {
                    if(fF==1)
                    {
                        nf.setPoint(i, nf.getHeight() - (j + 1), dh.readFloat());
                    }
                    else if(dS==4)
                    {
                        nf.setPoint(i, nf.getHeight() - (j + 1), (double)dh.readInt());
                    }
                    else if(dS==2)
                    {
                        nf.setPoint(i, nf.getHeight() - (j + 1), (double)dh.readShort());
                    }
                }
            }
            dh.close();
            fh.close();
            return nf;
        }
        catch(Exception e)
        {
            System.err.println(e.toString());
        }
        return null;
    }

    @SneakyThrows
    public static void saveCRImage(NoiseField nf, String pngFileName, ColorRamp cRamp, double seaMax, double landMax)
    {
        BufferedImage bufferedImage = new BufferedImage(nf.getWidth(), nf.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for(int y=0 ; y<nf.getHeight() ; ++y)
        {
            for(int x=0 ; x<nf.getWidth() ; ++x)
            {
                Color col = cRamp.mapHeight(nf.getPoint(x,y), seaMax, landMax);
                bufferedImage.setRGB(x,y, col.getRGB());
            }
        }

        savePNG(bufferedImage, pngFileName);
    }

    @SneakyThrows
    public static void saveHFImage(NoiseField nf, String pngFileName)
    {
        saveHFImage(nf,1f,pngFileName);
    }

    @SneakyThrows
    public static void saveHFImage(NoiseField nf, float sscale, String pngFileName)
    {
        BufferedImage bufferedImage = new BufferedImage(nf.getWidth(), nf.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for(int y=0 ; y<nf.getHeight() ; ++y)
        {
            for(int x=0 ; x<nf.getWidth() ; ++x)
            {
                float _h = (float) nf.getPoint(x,y);
                Color col = null;
                if(_h>=1.)
                {
                    col = Color.WHITE;
                }
                else
                if(_h<=0.)
                {
                    col = Color.BLACK;
                }
                else
                {
                    col = new Color(_h/sscale,_h/sscale,_h/sscale);
                }
            
                bufferedImage.setRGB(x,y, col.getRGB());
            }
        }

        savePNG(bufferedImage, pngFileName);
    }

    @SneakyThrows
    public static void saveHFEImage(NoiseField nf, String pngFileName)
    {
        saveHFEImage(nf,-1f, 1f,pngFileName);
    }

    @SneakyThrows
    public static void saveHFEImage(NoiseField nf, double _min, double _max, String pngFileName)
    {
        savePNG(getHFEImage(nf,_min, _max), pngFileName);
    }

    @SneakyThrows
    public static void saveHFEImage(NoiseField nf, double _min, double _max, int _bands, String pngFileName)
    {
        savePNG(getHFEImage(nf,_min, _max, _bands), pngFileName);
    }

    @SneakyThrows
    public static void savePNG(BufferedImage bufferedImage, String pngFileName)
    {
        File file = new File(pngFileName);
        file.getParentFile().mkdirs();
        FileOutputStream fo = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(fo, 1024*1024);
        ImageIO.write(bufferedImage, "png", bos);
        bos.close();
    }

    @SneakyThrows
    public static BufferedImage getHFEImage(NoiseField nf, double _min, double _max)
    {
        BufferedImage bufferedImage = new BufferedImage(nf.getWidth(), nf.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for(int y=0 ; y<nf.getHeight() ; ++y)
        {
            for(int x=0 ; x<nf.getWidth() ; ++x)
            {
                float _h = (float) nf.getPoint(x,y);
                Color col = null;
                if(_h<_min)
                {
                    col = Color.BLUE;
                }
                else
                if(_h>_max)
                {
                    col = Color.RED;
                }
                else
                {
                    _h = (float)((_h-_min)/(_max-_min));
                    col = new Color(_h,_h,_h);
                }
                bufferedImage.setRGB(x,y, col.getRGB());
            }
        }

        return bufferedImage;
    }

    @SneakyThrows
    public static BufferedImage getHFEImage(NoiseField nf, double _min, double _max, int _bands)
    {
        BufferedImage bufferedImage = new BufferedImage(nf.getWidth(), nf.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for(int y=0 ; y<nf.getHeight() ; ++y)
        {
            for(int x=0 ; x<nf.getWidth() ; ++x)
            {
                float _h = (float) nf.getPoint(x,y);
                Color col = null;
                if(_h<_min)
                {
                    col = Color.BLUE;
                }
                else
                if(_h>_max)
                {
                    col = Color.RED;
                }
                else
                {
                    _h = (int)((float)((_h-_min)/(_max-_min))*_bands);
                    _h = _h / ((float)_bands);
                    col = new Color(_h,_h,_h);
                }
                bufferedImage.setRGB(x,y, col.getRGB());
            }
        }

        return bufferedImage;
    }

    @SneakyThrows
    public static void saveBLImage(NoiseField nf, double _thr, String pngFileName)
    {
        BufferedImage bufferedImage = new BufferedImage(nf.getWidth(), nf.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for(int y=0 ; y<nf.getHeight() ; ++y)
        {
            for(int x=0 ; x<nf.getWidth() ; ++x)
            {
                float _h = (float) nf.getPoint(x,y);
                Color col = null;
                if(_h<_thr)
                {
                    col = Color.BLACK;
                }
                else
                {
                    col = Color.WHITE;
                }
                bufferedImage.setRGB(x,y, col.getRGB());
            }
        }

        File file = new File(pngFileName);
        file.getParentFile().mkdirs();
        FileOutputStream fo = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(fo, 1024*1024);
        ImageIO.write(bufferedImage, "png", bos);
        bos.close();
    }

    @SneakyThrows
    public static void saveHistogramImage(NoiseField nf, String pngFileName)
    {
        saveHistogramImage(nf,-1f, 1f,pngFileName);
    }

    @SneakyThrows
    public static void saveHistogramImage(NoiseField nf, double _min, double _max, String pngFileName)
    {
        BufferedImage _bI = new BufferedImage(nf.getWidth(), nf.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D _g = _bI.createGraphics();
        int[] _hist = new int[256];
        int _u = 0;
        for(int y=0 ; y<nf.getHeight() ; ++y)
        {
            for(int x=0 ; x<nf.getWidth() ; ++x)
            {
                float _h = (float) nf.getPoint(x,y);
                int _idx = 0;
                if(_h<=_min)
                {
                    _idx = 0;
                }
                else
                if(_h>=_max)
                {
                    _idx = 255;
                }
                else
                {
                    _idx = (int) ((float)((_h-_min)/(_max-_min))*256);
                }
                _hist[_idx]++;
                if(_u < _hist[_idx]) _u = _hist[_idx];
            }
        }

        _g.setColor(Color.BLACK);
        _g.fillRect(0,0, nf.getWidth()-1, nf.getHeight()-1);

        _g.setColor(Color.YELLOW);
        _g.setStroke(new BasicStroke(2f));
        for(int _i = 1; _i < 256; _i++)
        {
            _g.drawLine((_i-1)*(nf.getWidth()/256), (nf.getHeight()-1)-(_hist[_i-1]*nf.getHeight()/_u), _i*(nf.getWidth()/256), (nf.getHeight()-1)-(_hist[_i]*nf.getHeight()/_u));
        }

        File file = new File(pngFileName);
        file.getParentFile().mkdirs();
        _g.dispose();
        ImageIO.write(_bI,"png", file);
    }

    @SneakyThrows
    public static void saveHistogramHFEImage(NoiseField nf, String pngFileName)
    {
        saveHistogramHFEImage(nf,-1f, 1f,pngFileName);
    }

    @SneakyThrows
    public static void saveHistogramHFEImage(NoiseField nf, double _min, double _max, String pngFileName)
    {
        BufferedImage _bI = new BufferedImage(nf.getWidth(), nf.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D _g = _bI.createGraphics();
        int[] _hist = new int[256];

        _g.setColor(Color.BLACK);
        _g.fillRect(0,0, nf.getWidth()-1, nf.getHeight()-1);

        int _u = 0;
        for(int y=0 ; y<nf.getHeight() ; ++y)
        {
            for(int x=0 ; x<nf.getWidth() ; ++x)
            {
                float _h = (float) nf.getPoint(x,y);
                Color col = null;
                int _idx = 0;
                if(_h<=_min)
                {
                    _idx = 0;
                    col = Color.BLUE;
                }
                else
                if(_h>=_max)
                {
                    _idx = 255;
                    col = Color.RED;
                }
                else
                {
                    _idx = (int) ((float)((_h-_min)/(_max-_min))*256);

                    _h = (float)((_h-_min)/(_max-_min));
                    col = new Color(_h,_h,_h);
                }

                if(_idx>255) {
                    _idx = 255;
                    col = Color.RED;
                }
                _hist[_idx]++;
                if(_u < _hist[_idx]) _u = _hist[_idx];

                _g.setColor(col);
                _g.drawRect(x,y, 1,1);
            }
        }

        _g.setColor(Color.BLACK);
        _g.setStroke(new BasicStroke(6f));
        for(int _i = 1; _i < 256; _i++)
        {
            _g.drawLine((_i-1)*(nf.getWidth()/256), (nf.getHeight()-1)-(_hist[_i-1]*nf.getHeight()/_u), _i*(nf.getWidth()/256), (nf.getHeight()-1)-(_hist[_i]*nf.getHeight()/_u));
        }

        _g.setColor(Color.YELLOW);
        _g.setStroke(new BasicStroke(2f));
        for(int _i = 1; _i < 256; _i++)
        {
            _g.drawLine((_i-1)*(nf.getWidth()/256), (nf.getHeight()-1)-(_hist[_i-1]*nf.getHeight()/_u), _i*(nf.getWidth()/256), (nf.getHeight()-1)-(_hist[_i]*nf.getHeight()/_u));
        }

        _g.dispose();
        savePNG(_bI,pngFileName);
    }
}
