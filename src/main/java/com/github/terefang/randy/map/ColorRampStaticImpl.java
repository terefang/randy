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
public class ColorRampStaticImpl extends AbstractColorRamp implements ColorRamp
{

    @Override
    public boolean isNonlinear()
    {
        return this.landHardRamp || this.seaHardRamp;
    }

    public Color mapHeight(double h, double seaMin, double landMax)
    {
        if(h<=seaMin)
        {
            return SEA_COLOR.get(0);
        }
        else
        if(h>=landMax)
        {
            return LAND_COLOR.get(LAND_COLOR.size()-1);
        }

        h = NoiseUtil.clampValue(h, seaMin, landMax);

        if(h<0.0)
        {
            double hm = h*((double)SEA_COLOR.size()-1.1)/seaMin;
            int hi = (int)Math.floor(hm);

            if(hi > (SEA_COLOR.size()-1)) return SEA_COLOR.get(0);

            if(this.seaHardRamp)
            {
                return SEA_COLOR.get(SEA_COLOR.size()-1-hi);
            }

            double fh = hm-(double)hi;
            return NoiseUtil.lerp(SEA_COLOR.get(SEA_COLOR.size()-1-hi), SEA_COLOR.get(SEA_COLOR.size()-2-hi), fh);
        }
        else
        {
            double hm = h*((double)LAND_COLOR.size()-1.1)/landMax;
            int hi = (int)Math.floor(hm);

            if(hi >= LAND_COLOR.size()-1) return LAND_COLOR.get(LAND_COLOR.size()-1);

            if(this.landHardRamp)
            {
                return LAND_COLOR.get(hi);
            }

            double fh = hm-(double)hi;
            return NoiseUtil.lerp(LAND_COLOR.get(hi),LAND_COLOR.get(hi+1), fh);
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
