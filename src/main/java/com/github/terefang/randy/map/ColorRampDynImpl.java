package com.github.terefang.randy.map;

import com.github.terefang.randy.noise.NoiseUtil;

import java.awt.Color;
import java.util.List;
import java.util.Vector;

/**
 * Created by fredo on 21.11.15.
 */
public class ColorRampDynImpl extends AbstractColorRamp implements ColorRamp
{

    public static ColorRamp getEarth()
    {
        ColorRampDynImpl r = new ColorRampDynImpl();
        r.addSeaColor(new Color(0x08,0x57,0x81), 1.0f);
        r.addSeaColor(new Color(0x28,0x87,0xa1), 1.0f-.33330f);
        r.addSeaColor(new Color(0x79,0x97,0xac), 1.0f-.66670f);
        r.addSeaColor(new Color(0xa5,0xb8,0xd8), 1.0f-.83330f);
        r.addSeaColor(new Color(0x95,0xa8,0xd8), .000f);

        r.addLandColor(new Color(0x68,0xca,0x82), .00000f);
        r.addLandColor(new Color(0xed,0xea,0xc2), .3f);
        r.addLandColor(new Color(0xd6,0xbd,0x8d), .5f);
        r.addLandColor(new Color(0xbd,0x92,0x5a), .7f);
        r.addLandColor(new Color(0xA1,0x69,0x28), .89f);
        r.addLandColor(new Color(255,255,255), 1.f);
        r.seaHardRamp=false;
        r.landHardRamp=false;
        return r;
    }
    public static ColorRamp getDefault()
    {
        ColorRampDynImpl r = new ColorRampDynImpl();
        r.addSeaColor(new Color(121,178,222), 1.0f);
        r.addSeaColor(new Color(121,178,222), 1.0f-.33330f);
        r.addSeaColor(new Color(141,193,234), 1.0f-.50000f);
        r.addSeaColor(new Color(161,210,247), 1.0f-.66670f);
        r.addSeaColor(new Color(185,227,255), 1.0f-.83330f);
        r.addSeaColor(new Color(216,242,254), .000f);
        r.addLandColor(new Color(128,208,128), .00000f);
        r.addLandColor(new Color(172,208,165), .00880f);
        r.addLandColor(new Color(239,235,192), .09660f);
        r.addLandColor(new Color(222,214,163), .18450f);
        r.addLandColor(new Color(211,202,157), .27230f);
        r.addLandColor(new Color(202,185,130), .36020f);
        r.addLandColor(new Color(195,167,107), .44800f);
        r.addLandColor(new Color(185,152, 90), .53580f);
        r.addLandColor(new Color(170,135, 83), .62370f);
        r.addLandColor(new Color(170,159,141), .71150f);
        r.addLandColor(new Color(224,222,216), .88f);
        r.addLandColor(new Color(255,255,255), 1.f);
        r.seaHardRamp=false;
        r.landHardRamp=true;
        return r;
    }

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
            return SEA_COLOR.get(0);
        }
        else
        if(h>=landMax)
        {
            return LAND_COLOR.get(LAND_COLOR.size()-1);
        }
        else
        if(h<=0.0)
        {
            double ht = (double)(h/seaMin);
            for(int i = 1; i < SEA_COLOR.size(); i++)
            {
                if(ht>=SEA_COLOR.get(i).getThreshold())
                {
                    if(this.seaHardRamp)
                    {
                        return SEA_COLOR.get(i);
                    }
                    else
                    {
                        double norm = SEA_COLOR.get(i-1).getThreshold()-SEA_COLOR.get(i).getThreshold();
                        double diff = (ht-SEA_COLOR.get(i).getThreshold())/norm;
                        return NoiseUtil.lerp(SEA_COLOR.get(i),SEA_COLOR.get(i-1), diff);
                    }
                }
            }
            return SEA_COLOR.get(0);
        }
        else
        {
            double ht = (double)(h/landMax);
            for(int i = 1; i < LAND_COLOR.size(); i++)
            {
                if(ht<=LAND_COLOR.get(i).getThreshold())
                {
                    if(this.landHardRamp)
                    {
                        return LAND_COLOR.get(i-1);
                    }
                    else
                    {
                        double norm = LAND_COLOR.get(i).getThreshold()-LAND_COLOR.get(i-1).getThreshold();
                        double diff = (ht-LAND_COLOR.get(i-1).getThreshold())/norm;
                        return NoiseUtil.lerp(LAND_COLOR.get(i-1),LAND_COLOR.get(i), diff);
                    }
                }
            }
            return LAND_COLOR.get(LAND_COLOR.size()-1);
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
