package com.github.terefang.randy.map;

import com.github.terefang.randy.noise.NoiseUtil;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

public abstract class AbstractColorRamp
{
    public boolean seaHardRamp = true;
    public boolean landHardRamp = true;

    public static class ColorDef extends Color
    {

        double threshold = 0.0f;
        public ColorDef(int r, int g, int b, double threshold)
        {
            super(r,g,b);
            this.threshold = threshold;
        }

        public double getThreshold() {
            return threshold;
        }

        public void setThreshold(double threshold) {
            this.threshold = threshold;
        }
    }

    public List<ColorRampDynImpl.ColorDef> SEA_COLOR = new Vector<>();
    public List<ColorDef> LAND_COLOR = new Vector<>();

    public static ColorDef rgbt(Color rgb, double threshold)
    {
        return rgbt(rgb.getRed(), rgb.getGreen(), rgb.getBlue(), (double)threshold);
    }

    public static ColorDef rgbt(float r, float g, float b, float threshold)
    {
        return rgbt((int) (r * 255), (int) (g * 255), (int) (b * 255), (double)threshold);
    }

    public static ColorDef rgbt(double r, double g, double b, double threshold)
    {
        return rgbt((int) (r * 255), (int) (g * 255), (int) (b * 255), threshold);
    }

    public static ColorDef rgbt(int r, int g, int b, double threshold)
    {
        return new ColorDef(r,g,b,threshold);
    }


    public void addSeaColor(Color rgb, double threshold)
    {
        SEA_COLOR.add(rgbt(rgb, threshold));
    }
    public void addLandColor(Color rgb, double threshold)
    {
        LAND_COLOR.add(rgbt(rgb, threshold));
    }

    public void load(File file)
    {
        if(file.getName().endsWith(".props") || file.getName().endsWith(".properties"))
        {
            try(FileReader fh = new FileReader(file);)
            {
                this.loadFromProperties(fh);
            }
            catch(Exception xe)
            {
                xe.printStackTrace();
            }
        }
        else
        if(file.getName().endsWith(".txt"))
        {
            try(FileReader fh = new FileReader(file);)
            {
                this.loadFromTxt(fh);
            }
            catch(Exception xe)
            {
                xe.printStackTrace();
            }
        }
    }

    public void loadFromTxt(Reader reader)
    {
        try
        {
            BufferedReader _br = new BufferedReader(reader, 1024);
            String _line = null;

            boolean _seenZero = false;
            while((_line = _br.readLine()) != null)
            {
                if(_line.trim().startsWith("#")) continue;
                if(_line.trim().startsWith("INTERPOLATION:"))
                {
                    if(_line.trim().endsWith(":DISCRETE"))
                    {
                        this.landHardRamp = true;
                        this.seaHardRamp = true;
                    }
                    else
                    {
                        this.landHardRamp = false;
                        this.seaHardRamp = false;
                    }
                }
                else if(!_seenZero)
                {
                    String[] _parts = _line.split( ",", 5);
                    SEA_COLOR.add(rgbt(new Color(NoiseUtil.checkInt(_parts[1]),NoiseUtil.checkInt(_parts[2]),NoiseUtil.checkInt(_parts[3])),0));
                    if(NoiseUtil.checkInt(_parts[0])==0) _seenZero = true;
                }
                else if(_seenZero)
                {
                    String[] _parts = _line.split(",", 5);
                    LAND_COLOR.add(rgbt(new Color(NoiseUtil.checkInt(_parts[1]),NoiseUtil.checkInt(_parts[2]),NoiseUtil.checkInt(_parts[3])),0));
                }
            }

            if(SEA_COLOR.size()==1)
            {
                SEA_COLOR.add(SEA_COLOR.get(0));
            }

            if(this.landHardRamp)
            {
                for(int i = LAND_COLOR.size()-1; i > 0; i--)
                {
                    Color _c = NoiseUtil.lerp(LAND_COLOR.get(i-1), LAND_COLOR.get(i), 0.5f);
                    LAND_COLOR.add(i, rgbt(_c,0));
                }
            }
        }
        catch(Exception xe)
        {
            xe.printStackTrace();
        }
    }

    public void loadFromProperties(Reader reader)
    {
        try
        {
            Properties properties = new Properties();
            properties.load(reader);

            this.landHardRamp=!Boolean.parseBoolean(properties.getProperty("softRamp", "false"));
            this.landHardRamp=!Boolean.parseBoolean(properties.getProperty("landSoftRamp", this.landHardRamp ? "false" : "true"));

            this.seaHardRamp=!Boolean.parseBoolean(properties.getProperty("softRamp", "false"));
            this.seaHardRamp=!Boolean.parseBoolean(properties.getProperty("landSoftRamp", this.seaHardRamp ? "false" : "true"));

            for(int i = 0; properties.containsKey("sea."+i); i++)
            {
                String[] rgb= properties.getProperty("sea."+i).split("[\\s,]+");
                double _th = 0;
                if(rgb.length>=4)
                {
                    _th = Double.parseDouble(rgb[3]);
                }
                SEA_COLOR.add(rgbt(new Color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2])),_th));
            }

            if(this.seaHardRamp)
            {
                for(int i = SEA_COLOR.size()-1; i > 0; i--)
                {
                    Color _c = NoiseUtil.lerp(SEA_COLOR.get(i-1), SEA_COLOR.get(i), 0.5f);
                    double _th = NoiseUtil.lerp(SEA_COLOR.get(i-1).getThreshold(), SEA_COLOR.get(i).getThreshold(), 0.5f);
                    SEA_COLOR.add(i, rgbt(_c,_th));
                }
            }

            for(int i = 0; properties.containsKey("land."+i); i++)
            {
                String[] rgb= properties.getProperty("land."+i).split("[\\s,]+");
                double _th = 0;
                if(rgb.length>=4)
                {
                    _th = Double.parseDouble(rgb[3]);
                }
                LAND_COLOR.add(rgbt(new Color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2])),_th));
            }

            if(this.landHardRamp)
            {
                for(int i = LAND_COLOR.size()-1; i > 0; i--)
                {
                    Color _c = NoiseUtil.lerp(LAND_COLOR.get(i-1), LAND_COLOR.get(i), 0.5f);
                    double _th = NoiseUtil.lerp(LAND_COLOR.get(i-1).getThreshold(), LAND_COLOR.get(i).getThreshold(), 0.5f);
                    LAND_COLOR.add(i, rgbt(_c,_th));
                }
            }
        }
        catch(Exception xe)
        {
            xe.printStackTrace();
        }
    }
}
