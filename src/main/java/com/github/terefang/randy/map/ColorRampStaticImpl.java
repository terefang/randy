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

/**
 * Created by fredo on 21.11.15.
 */
public class ColorRampStaticImpl implements ColorRamp
{
    public Color[] SEA_COLOR = null;
    public Color[] LAND_COLOR = null;

    public boolean seaHardRamp = true;
    public boolean landHardRamp = true;

    @Override
    public boolean isNonlinear()
    {
        return this.landHardRamp || this.seaHardRamp;
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

            List<String> sea = new Vector();
            for(int i = 0; properties.containsKey("sea."+i); i++)
            {
                sea.add(properties.getProperty("sea."+i));
            }

            if(this.seaHardRamp)
            {
                SEA_COLOR = new Color[(sea.size()*2)-1];
                for(int i = 0; i < (sea.size()-1); i++)
                {
                    String[] rgb1 = sea.get(i).split("[\\s,]+");
                    String[] rgb2 = sea.get(i+1).split("[\\s,]+");
                    SEA_COLOR[i*2]=new Color(Integer.parseInt(rgb1[0]), Integer.parseInt(rgb1[1]), Integer.parseInt(rgb1[2]));
                    SEA_COLOR[(i+1)*2]=new Color(Integer.parseInt(rgb2[0]), Integer.parseInt(rgb2[1]), Integer.parseInt(rgb2[2]));
                    SEA_COLOR[(i*2)+1]= NoiseUtil.lerp(SEA_COLOR[i*2], SEA_COLOR[(i+1)*2], 0.5f);
                }
            }
            else
            {
                SEA_COLOR = new Color[sea.size()];
                for(int i = 0; i < SEA_COLOR.length; i++)
                {
                    String[] rgb = sea.get(i).split("[\\s,]+");
                    SEA_COLOR[i]=new Color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
                }
            }


            List<String> land = new Vector();
            for(int i = 0; properties.containsKey("land."+i); i++)
            {
                land.add(properties.getProperty("land."+i));
            }

            if(this.landHardRamp)
            {
                LAND_COLOR = new Color[(land.size()*2)-1];
                for(int i = 0; i < (land.size()-1); i++)
                {
                    String[] rgb1 = land.get(i).split("[\\s,]+");
                    String[] rgb2 = land.get(i+1).split("[\\s,]+");
                    LAND_COLOR[i*2]=new Color(Integer.parseInt(rgb1[0]), Integer.parseInt(rgb1[1]), Integer.parseInt(rgb1[2]));
                    LAND_COLOR[(i+1)*2]=new Color(Integer.parseInt(rgb2[0]), Integer.parseInt(rgb2[1]), Integer.parseInt(rgb2[2]));
                    LAND_COLOR[(i*2)+1]=NoiseUtil.lerp(LAND_COLOR[i*2], LAND_COLOR[(i+1)*2], 0.5f);
                }
            }
            else
            {
                LAND_COLOR = new Color[land.size()];
                for(int i = 0; i < LAND_COLOR.length; i++)
                {
                    String[] rgb = land.get(i).split("[\\s,]+");
                    LAND_COLOR[i]=new Color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
                }
            }

        }
        catch(Exception xe)
        {
            xe.printStackTrace();
        }
    }



    public void loadFromTxt(Reader reader)
    {
        try
        {
            BufferedReader _br = new BufferedReader(reader, 1024);
            String _line = null;

            List<Color> _sea = new Vector();
            List<Color> _land = new Vector();
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
                    _sea.add(new Color(NoiseUtil.checkInt(_parts[1]),NoiseUtil.checkInt(_parts[2]),NoiseUtil.checkInt(_parts[3])));
                    if(NoiseUtil.checkInt(_parts[0])==0) _seenZero = true;
                }
                else if(_seenZero)
                {
                    String[] _parts = _line.split(",", 5);
                    _land.add(new Color(NoiseUtil.checkInt(_parts[1]),NoiseUtil.checkInt(_parts[2]),NoiseUtil.checkInt(_parts[3])));
                }
            }

            if(_sea.size()==1)
            {
                _sea.add(_sea.get(0));
            }

            SEA_COLOR = _sea.toArray(new Color[0]);

            if(this.landHardRamp)
            {
                LAND_COLOR = new Color[(_land.size()*2)-1];
                for(int i = 0; i < (_land.size()-1); i++)
                {
                    LAND_COLOR[i*2]= _land.get(i);
                    LAND_COLOR[(i+1)*2]= _land.get(i+1);
                    LAND_COLOR[(i*2)+1]=NoiseUtil.lerp(LAND_COLOR[i*2], LAND_COLOR[(i+1)*2], 0.5f);
                }
            }
            else
            {
                LAND_COLOR = _land.toArray(new Color[0]);
            }

        }
        catch(Exception xe)
        {
            xe.printStackTrace();
        }
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

    public Color mapHeight(double h, double seaMin, double landMax)
    {
        if(h<=seaMin)
        {
            return SEA_COLOR[0];
        }
        else
        if(h>=landMax)
        {
            return LAND_COLOR[LAND_COLOR.length-1];
        }

        h = NoiseUtil.clampValue(h, seaMin, landMax);

        if(h<0.0)
        {
            double hm = h*((double)SEA_COLOR.length-1.1)/seaMin;
            int hi = (int)Math.floor(hm);

            if(hi > (SEA_COLOR.length-1)) return SEA_COLOR[0];

            if(this.seaHardRamp)
            {
                return SEA_COLOR[SEA_COLOR.length-1-hi];
            }

            double fh = hm-(double)hi;
            return NoiseUtil.lerp(SEA_COLOR[SEA_COLOR.length-1-hi], SEA_COLOR[SEA_COLOR.length-2-hi], fh);
        }
        else
        {
            double hm = h*((double)LAND_COLOR.length-1.1)/landMax;
            int hi = (int)Math.floor(hm);

            if(hi >= LAND_COLOR.length-1) return LAND_COLOR[LAND_COLOR.length-1];

            if(this.landHardRamp)
            {
                return LAND_COLOR[hi];
            }

            double fh = hm-(double)hi;
            return NoiseUtil.lerp(LAND_COLOR[hi],LAND_COLOR[hi+1], fh);
        }
    }

    @Override
    public String toHtml(double seaMin, double landMax)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("<div>");
        for(Color c : SEA_COLOR)
        {
            sb.append("<div>");
            sb.append("<i style='background-color:#"+String.format("%06X", c.getRGB()&0xffffff)+"'>&nbsp; &nbsp; &nbsp;</i>");
            sb.append("</div>");
        }
        for(Color c : LAND_COLOR)
        {
            sb.append("<div>");
            sb.append("<i style='background-color:#"+String.format("%06X", c.getRGB()&0xffffff)+"'>&nbsp; &nbsp; &nbsp;</i>");
            sb.append("</div>");
        }
        sb.append("</div>");
        return sb.toString();
    }

}
