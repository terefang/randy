package com.github.terefang.randy.map;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;
import java.util.Vector;

import com.github.terefang.randy.noise.NoiseUtil;
import com.github.terefang.randy.utils.ColorUtil;

/**
 * Created by fredo on 20.11.15.
 */
public interface ColorRamp {

    public static ColorRamp getFile(String path)
    {
        ColorRampStaticImpl r = new ColorRampStaticImpl();
        r.load(new File(path));
        return r;
    }

    public static ColorRamp getFile(File path)
    {
        ColorRampStaticImpl r = new ColorRampStaticImpl();
        r.load(path);
        return r;
    }

    public void load(File file);


    public static ColorRamp getHard()
    {
        ColorRampStaticImpl r = new ColorRampStaticImpl();
        r.seaHardRamp = true;
        r.landHardRamp = false;
        r.addSeaColor(Color.BLACK,0);
        r.addSeaColor(new Color(16,32,64),0);
        r.addSeaColor(new Color(32,64,128),0);
        r.addSeaColor(new Color(64,64,160),0);
        r.addSeaColor(new Color(64,96,216),0);
        r.addSeaColor(new Color(64,128,255),0);

        r.addLandColor(new Color(83, 194, 108),0);
        r.addLandColor(new Color(144, 193,  58),0);
        r.addLandColor(new Color(64, 216,  64),0);
        r.addLandColor(new Color(216, 224,  96),0);
        r.addLandColor(new Color(219, 151, 119),0);
        r.addLandColor(new Color(230, 200, 130),0);
        r.addLandColor(new Color(255, 255, 255),0);
        return r;
    }

    public static ColorRamp getReds()
    {
        return getBase(Color.RED);
    }

    public static ColorRamp getBase(Color _base)
    {
        ColorRampStaticImpl r = new ColorRampStaticImpl();
        r.seaHardRamp = true;
        r.landHardRamp = true;
        r.addSeaColor(Color.WHITE,0);
        r.addSeaColor(ColorUtil.adjSaturation(_base, .1),0);
        r.addSeaColor(ColorUtil.adjSaturation(_base, .3),0);
        r.addSeaColor(ColorUtil.adjSaturation(_base, .5),0);
        r.addSeaColor(ColorUtil.adjSaturation(_base, .7),0);

        r.addLandColor(_base,0);
        r.addLandColor(ColorUtil.setValue(_base, 90.),0);
        r.addLandColor(ColorUtil.setValue(_base, 70.),0);
        r.addLandColor(ColorUtil.setValue(_base, 50.),0);
        r.addLandColor(ColorUtil.setValue(_base, 25.),0);
        r.addLandColor(ColorUtil.setValue(_base, 5.),0);

        return r;
    }

    public static ColorRamp getBase(Color _base1, Color _base2, double _steps)
    {
        ColorRampStaticImpl r = new ColorRampStaticImpl();
        r.seaHardRamp = true;
        r.landHardRamp = true;

        r.addSeaColor(_base1,0);
        for(double _i = 1; _i<_steps-1; _i++)
            r.addSeaColor(ColorUtil.colorLerp(_base1, _base2,_i/_steps),0);
        r.addSeaColor(_base2,0);

        r.addLandColor(_base2,0);
        for(double _i = 1; _i<_steps-1; _i++)
            r.addLandColor(ColorUtil.colorLerp(_base2,Color.WHITE,Math.sqrt(_i/_steps)),0);
        r.addLandColor(Color.WHITE,0);
        return r;
    }

    public static ColorRamp getDefault()
    {
        return ColorRampDynImpl.getDefault();
    }
    public static ColorRamp getComplex()
    {
        return ColorRampDynImpl.getDefault();
    }


    public static ColorRamp getAdvanced()
    {
        ColorRampStaticImpl r = new ColorRampStaticImpl();
        r.addSeaColor(ColorUtil.rgb_pct(41, 97, 156, 0.7),0.);
        r.addSeaColor(ColorUtil.rgb_pct(89, 148, 204, 0.7),0.);
        r.addSeaColor(ColorUtil.rgb_pct(115, 166, 224, 0.7),0.);
        r.addSeaColor(ColorUtil.rgb_pct(133, 179, 235, 0.7),0.);
        r.addSeaColor(ColorUtil.rgb_pct(148, 194, 247, 0.7),0.);
        r.addSeaColor(ColorUtil.rgb_pct(166, 206, 245, 0.7),0.);
        r.addSeaColor(ColorUtil.rgb_pct(186, 222, 255, 0.7),0.);

        r.addLandColor(ColorUtil.rgb_pct( 84, 229, 151, 0.9),0.);
        r.addLandColor(ColorUtil.rgb_pct(158, 254, 135, 0.9),0.);
        r.addLandColor(ColorUtil.rgb_pct(207, 254, 144, 0.9),0.);
        r.addLandColor(ColorUtil.rgb_pct(247, 254, 154, 0.9),0.);
        r.addLandColor(ColorUtil.rgb_pct(254, 238, 146, 0.9),0.);
        r.addLandColor(ColorUtil.rgb_pct(254, 215, 121, 0.9),0.);
        r.addLandColor(ColorUtil.rgb_pct(244, 197, 136, 0.9),0.);
        r.addLandColor(ColorUtil.rgb_pct(235, 194, 164, 0.9),0.);
        r.addLandColor(ColorUtil.rgb_pct(224, 207, 194, 0.9),0.);
        r.addLandColor(ColorUtil.rgb_pct(239, 231, 225, 0.9),0.);
        r.addLandColor(ColorUtil.rgb_pct(255, 255, 255, 0.9),0.);
        return r;
    }

    public static ColorRamp getLefebvre()
    {
        ColorRampDynImpl r = new ColorRampDynImpl();
        r.addSeaColor(new Color(  0, 16, 32),1f);
        r.addSeaColor(new Color(  0, 53, 83),1f-.3f);
        r.addSeaColor(new Color(  5, 70,107),1f-.67f);
        r.addSeaColor(new Color( 17, 85,124),1f-.8f);
        r.addSeaColor(new Color(104,176,196),1f-.96f);
        r.addSeaColor(new Color(128,196,224),0f);

        r.addLandColor(new Color(  8, 68, 34),0f);
        r.addLandColor(new Color( 50,101, 50),.23f);
        r.addLandColor(new Color(118,141, 69),.48f);
        r.addLandColor(new Color(165,184,105),.64f);
        r.addLandColor(new Color(205,207,162),.84f);
        r.addLandColor(new Color(235,243,248),.96f);
        r.addLandColor(new Color(255,255,255),1f);

        r.seaHardRamp=false;
        r.landHardRamp=false;
        return r;
    }
    public static ColorRamp getLefebvre2()
    {
        ColorRampDynImpl r = new ColorRampDynImpl();
        r.addSeaColor(new Color(  0, 16, 32),1f);
        r.addSeaColor(new Color(  0, 32, 64),.9f);
        r.addSeaColor(new Color(  0, 53, 83),.75f);
        r.addSeaColor(new Color(  5, 70,107),.5f);
        r.addSeaColor(new Color( 17, 85,124),.25f);
        r.addSeaColor(new Color( 64,130,160),.125f);
        r.addSeaColor(new Color(104,176,196),0f);

        r.addLandColor(new Color(  8, 68, 34),0f);
        r.addLandColor(new Color( 50,101, 50),.25f);
        r.addLandColor(new Color(118,141, 69),.5f);
        r.addLandColor(new Color(165,184,105),.625f);
        r.addLandColor(new Color(205,207,162),.75f);
        r.addLandColor(new Color(235,243,248),.875f);
        r.addLandColor(new Color(255,255,255),1f);

        r.seaHardRamp=true;
        r.landHardRamp=false;
        return r;
    }

    public boolean isNonlinear();
    
    public Color mapHeight(double h, double seaMin, double landMax);
    public String toHtml(double seaMin, double landMax);
}
