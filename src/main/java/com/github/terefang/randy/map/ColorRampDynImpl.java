package com.github.terefang.randy.map;

import com.github.terefang.randy.noise.NoiseUtil;

import java.awt.*;

/**
 * Created by fredo on 21.11.15.
 */
public class ColorRampDynImpl implements ColorRamp
{
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

    static class ColorDef extends Color
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

    public static ColorRamp getEarth()
    {
        ColorRampDynImpl r = new ColorRampDynImpl();
        r.SEA_COLOR = new ColorDef[] {
                rgbt(new Color(0x08,0x57,0x81), 1.0f),
                rgbt(new Color(0x28,0x87,0xa1), 1.0f-.33330f),
                rgbt(new Color(0x79,0x97,0xac), 1.0f-.66670f),
                rgbt(new Color(0xa5,0xb8,0xd8), 1.0f-.83330f),
                rgbt(new Color(0x95,0xa8,0xd8), .000f)
        };
        r.LAND_COLOR = new ColorDef[] {
                rgbt(new Color(0x68,0xca,0x82), .00000f),
                rgbt(new Color(0xed,0xea,0xc2), .3f),
                rgbt(new Color(0xd6,0xbd,0x8d), .5f),
                rgbt(new Color(0xbd,0x92,0x5a), .7f),
                rgbt(new Color(0xA1,0x69,0x28), .89f),
                rgbt(new Color(255,255,255), 1.f)
        };
        r.seaHardRamp=false;
        r.landHardRamp=false;
        return r;
    }
    public static ColorRamp getDefault()
    {
        ColorRampDynImpl r = new ColorRampDynImpl();
        r.SEA_COLOR = new ColorDef[] {
                rgbt(new Color(121,178,222), 1.0f),
                rgbt(new Color(121,178,222), 1.0f-.33330f),
                rgbt(new Color(141,193,234), 1.0f-.50000f),
                rgbt(new Color(161,210,247), 1.0f-.66670f),
                rgbt(new Color(185,227,255), 1.0f-.83330f),
                rgbt(new Color(216,242,254), .000f)
        };
        r.LAND_COLOR = new ColorDef[] {
                rgbt(new Color(128,208,128), .00000f),
                rgbt(new Color(172,208,165), .00880f),
                rgbt(new Color(239,235,192), .09660f),
                rgbt(new Color(222,214,163), .18450f),
                rgbt(new Color(211,202,157), .27230f),
                rgbt(new Color(202,185,130), .36020f),
                rgbt(new Color(195,167,107), .44800f),
                rgbt(new Color(185,152, 90), .53580f),
                rgbt(new Color(170,135, 83), .62370f),
                rgbt(new Color(170,159,141), .71150f),
                rgbt(new Color(224,222,216), .88f),
                rgbt(new Color(255,255,255), 1.f)
        };
        r.seaHardRamp=false;
        r.landHardRamp=true;
        return r;
    }

    public ColorDef[] SEA_COLOR = null;
    public ColorDef[] LAND_COLOR = null;

    public boolean seaHardRamp = true;
    public boolean landHardRamp = true;
    
    @Override
    public boolean isNonlinear()
    {
        return this.landHardRamp || this.seaHardRamp;
    }
    
    public Color mapHeight(double h, double seaMin, double landMax)
    {
        h = NoiseUtil.clampValue(h, seaMin, landMax);
        if(h<=seaMin)
        {
            return SEA_COLOR[0];
        }
        else
        if(h>=landMax)
        {
            return LAND_COLOR[LAND_COLOR.length-1];
        }
        else
        if(h<=0.0)
        {
            double ht = (double)(h/seaMin);
            for(int i = 1; i < SEA_COLOR.length; i++)
            {
                if(ht>=SEA_COLOR[i].getThreshold())
                {
                    if(this.seaHardRamp)
                    {
                        return SEA_COLOR[i];
                    }
                    else
                    {
                        double norm = SEA_COLOR[i-1].getThreshold()-SEA_COLOR[i].getThreshold();
                        double diff = (ht-SEA_COLOR[i].getThreshold())/norm;
                        return NoiseUtil.lerp(SEA_COLOR[i],SEA_COLOR[i-1], diff);
                    }
                }
            }
            return SEA_COLOR[0];
        }
        else
        {
            double ht = (double)(h/landMax);
            for(int i = 1; i < LAND_COLOR.length; i++)
            {
                if(ht<=LAND_COLOR[i].getThreshold())
                {
                    if(this.landHardRamp)
                    {
                        return LAND_COLOR[i-1];
                    }
                    else
                    {
                        double norm = LAND_COLOR[i].getThreshold()-LAND_COLOR[i-1].getThreshold();
                        double diff = (ht-LAND_COLOR[i-1].getThreshold())/norm;
                        return NoiseUtil.lerp(LAND_COLOR[i-1],LAND_COLOR[i], diff);
                    }
                }
            }
            return LAND_COLOR[LAND_COLOR.length-1];
        }
    }

    public String toHtml(double seaMin, double landMax)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("<div>");
        for(ColorDef c : SEA_COLOR)
        {
            sb.append("<div>");
            sb.append("<i style='background-color:#"+String.format("%06X", c.getRGB()&0xffffff)+"'>&nbsp; &nbsp; &nbsp;</i>");
            sb.append("</div>");
        }
        for(ColorDef c : LAND_COLOR)
        {
            sb.append("<div>");
            sb.append("<i style='background-color:#"+String.format("%06X", c.getRGB()&0xffffff)+"'>&nbsp; &nbsp; &nbsp;</i>");
            sb.append("</div>");
        }
        sb.append("</div>");
        return sb.toString();
    }
}
