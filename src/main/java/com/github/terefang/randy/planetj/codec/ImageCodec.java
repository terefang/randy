package com.github.terefang.randy.planetj.codec;

import com.github.terefang.randy.nfield.NoiseFieldUtil;
import com.github.terefang.randy.planetj.PlanetJ;
import com.github.terefang.randy.utils.BmpFont;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImageCodec
{
    public static void saveOutline(PlanetJ _p, String _f, int _pct)
    {
        saveAs(_f, _p.makeRgbImageOutline(_pct));
    }

    public static void saveOutline(PlanetJ _p, String _f, int _pct, String _comment)
    {
        BufferedImage _pix = _p.makeRgbImageOutline(_pct);
        BmpFont _fnt = BmpFont.default6x12();
        Graphics2D _g = (Graphics2D) _pix.getGraphics();
        _fnt.drawString(_g, 0, 0, _comment, Color.WHITE, Color.BLACK);
        _g.dispose();
        saveAs(_f,_pix);
    }

    public static void saveWLD(PlanetJ _p, String f, double rh, double rw, double scale, double lng, double lat, double lngOff, double latOff)
    {
        try
        {
            File outFile = new File(f);
            PrintStream out = new PrintStream(outFile);

            double sx = (360.0/(rw))/scale;
            double sy = sx;
            double sr = (180.0/(rh))/scale;

            lng += lngOff;
            while(lng>180.) lng -= 360.;
            out.printf("%.21f\n", sx);
            out.printf("%.21f\n", 0f);
            out.printf("%.21f\n", 0f);
            out.printf("%.21f\n", -sy);
            //out.printf("%.5f\n", (-sr*rh)+lng+(sr/2.0));
            out.printf("%.21f\n", (-sr*rh)+lng+sr);
            //out.printf("%.5f\n", (sr*rh)+(lat*2.0)-(sr/2.0));
            out.printf("%.21f\n", (sr*rh/2.0)+(lat*2.0)-sr+latOff);
            out.close();
        }
        catch(Exception xe)
        {
            xe.printStackTrace();
        }
    }

    public static void saveWLD(PlanetJ _p, String f, double lngOff, double latOff)
    {
        saveWLD(_p, f, (double)_p.Height, (double)_p.Width, _p.scale, _p.baseLongitude*180.0/Math.PI, _p.baseLatitude*180.0/Math.PI, lngOff, latOff);
    }

    public static void saveWLD(PlanetJ _p, String f)
    {
        saveWLD(_p, f, (double)_p.Height, (double)_p.Width, _p.scale, _p.baseLongitude*180.0/Math.PI, _p.baseLatitude*180.0/Math.PI, 0, 0);
    }

    public static void saveBiome(PlanetJ _p, String f)
    {
        saveBiome(_p, f, true);
    }

    public static void saveBiome(PlanetJ _p, String f, boolean _comment)
    {
        if(_comment)
        {
            BufferedImage _pix = _p.makeRgbImageBiome();
            BmpFont _fnt = BmpFont.default6x12();
            Graphics2D _g = (Graphics2D) _pix.getGraphics();

            int _l = 10;
            for(Character _c : PlanetJ.biomeText.keySet())
            {
                Color _col = PlanetJ.biomeColors.get(_c);
                _fnt.drawString(_g, _p.getWidth()-(PlanetJ.biomeText.get(_c).length()*6), _l, PlanetJ.biomeText.get(_c), Color.BLACK, _col);
                _l+=14;
            }
            _g.dispose();
            saveAs(f,_pix);
        }
        else
        {
            saveAs(f, _p.makeRgbImageBiome());
        }
    }

    public static void saveRainfall(PlanetJ _p, String f)
    {
        saveAs(f, _p.makeRgbImageRainfall());
    }

    public static void saveTemperature(PlanetJ _p, String f)
    {
        saveAs(f, _p.makeRgbImageTemperature());
    }

    public static void saveTempAdj(PlanetJ _p, String f)
    {
        saveAs(f, _p.makeRgbImageTempAdj());
    }

    public static void saveRainAdj(PlanetJ _p, String f)
    {
        saveAs(f, _p.makeRgbImageRainAdj());
    }

    public static void saveAs(String f, BufferedImage bufferedImage)
    {
        String image_type = "jpg";
        if(f.toLowerCase().endsWith(".jpg") || f.toLowerCase().endsWith(".jpeg"))
        {
            image_type = "jpg";
        }
        else if(f.toLowerCase().endsWith(".png"))
        {
            image_type = "png";
        }
        else if(f.toLowerCase().endsWith(".gif"))
        {
            image_type = "gif";
        }
        else
        {
            throw new IllegalArgumentException("Unknown image extension: "+f);
        }

        try
        {
            File _file = new File(f);
            _file.getParentFile().mkdirs();
            BufferedOutputStream fh = new BufferedOutputStream(new FileOutputStream(_file), 8192<<8);
            ImageIO.write(bufferedImage, image_type, fh);
            fh.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void saveWaterflux(PlanetJ _p, String f) {
        saveAs(f, _p.makeRgbImageWaterFlux());
    }
}
