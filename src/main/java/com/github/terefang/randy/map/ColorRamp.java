package com.github.terefang.randy.map;

import java.awt.*;
import java.io.File;
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

    public static ColorRamp getHard()
    {
        ColorRampStaticImpl r = new ColorRampStaticImpl();
        r.seaHardRamp = true;
        r.landHardRamp = false;
        r.SEA_COLOR = new Color[] {
                Color.BLACK,
                new Color(16,32,64),
                new Color(32,64,128),
                new Color(64,64,160),
                new Color(64,96,216),
                new Color(64,128,255),
        };
        r.LAND_COLOR = new Color[] {
                new Color(83, 194, 108),
                new Color(144, 193,  58),
                new Color(64, 216,  64),
                new Color(216, 224,  96),
                new Color(219, 151, 119),
                new Color(230, 200, 130),
                new Color(255, 255, 255)
        };
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
        r.SEA_COLOR = new Color[] {
                Color.WHITE,
                ColorUtil.adjSaturation(_base, .1),
                ColorUtil.adjSaturation(_base, .3),
                ColorUtil.adjSaturation(_base, .5),
                ColorUtil.adjSaturation(_base, .7),
        };
        r.LAND_COLOR = new Color[] {
                _base,
                ColorUtil.setValue(_base, 90.),
                ColorUtil.setValue(_base, 70.),
                ColorUtil.setValue(_base, 50.),
                ColorUtil.setValue(_base, 25.),
                ColorUtil.setValue(_base, 5.)
        };
        return r;
    }

    public static ColorRamp getBase(Color _base1, Color _base2)
    {
        ColorRampStaticImpl r = new ColorRampStaticImpl();
        r.seaHardRamp = true;
        r.landHardRamp = true;
        r.SEA_COLOR = new Color[] {
                _base1,
                ColorUtil.colorLerp(Color.BLACK, _base1,.7),
                ColorUtil.colorLerp(Color.BLACK, _base1,.5),
                ColorUtil.colorLerp(Color.BLACK, _base1,.3),
                ColorUtil.colorLerp(Color.BLACK, _base1, .1),
                Color.BLACK,
        };
        r.LAND_COLOR = new Color[] {
                _base2,
                ColorUtil.colorLerp(_base2,Color.WHITE,.8),
                ColorUtil.colorLerp(_base2,Color.WHITE,.6),
                ColorUtil.colorLerp(_base2,Color.WHITE,.4),
                ColorUtil.colorLerp(_base2,Color.WHITE, .2),
                Color.WHITE,
        };
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
        r.SEA_COLOR = new Color[] {
                ColorUtil.rgb_pct(41, 97, 156, 0.7),
                ColorUtil.rgb_pct(89, 148, 204, 0.7),
                ColorUtil.rgb_pct(115, 166, 224, 0.7),
                ColorUtil.rgb_pct(133, 179, 235, 0.7),
                ColorUtil.rgb_pct(148, 194, 247, 0.7),
                ColorUtil.rgb_pct(166, 206, 245, 0.7),
                ColorUtil.rgb_pct(186, 222, 255, 0.7),
        };
        r.LAND_COLOR = new Color[] {
                ColorUtil.rgb_pct( 84, 229, 151, 0.9),
                ColorUtil.rgb_pct(158, 254, 135, 0.9),
                ColorUtil.rgb_pct(207, 254, 144, 0.9),
                ColorUtil.rgb_pct(247, 254, 154, 0.9),
                ColorUtil.rgb_pct(254, 238, 146, 0.9),
                ColorUtil.rgb_pct(254, 215, 121, 0.9),
                ColorUtil.rgb_pct(244, 197, 136, 0.9),
                ColorUtil.rgb_pct(235, 194, 164, 0.9),
                ColorUtil.rgb_pct(224, 207, 194, 0.9),
                ColorUtil.rgb_pct(239, 231, 225, 0.9),
                ColorUtil.rgb_pct(255, 255, 255, 0.9)
        };
        return r;
    }

    public static ColorRamp getLefebvre()
    {
        ColorRampDynImpl r = new ColorRampDynImpl();
        r.SEA_COLOR = new ColorRampDynImpl.ColorDef[] {
                ColorRampDynImpl.rgbt(new Color(  0, 16, 32),1f),
                ColorRampDynImpl.rgbt(new Color(  0, 53, 83),1f-.3f),
                ColorRampDynImpl.rgbt(new Color(  5, 70,107),1f-.67f),
                ColorRampDynImpl.rgbt(new Color( 17, 85,124),1f-.8f),
                ColorRampDynImpl.rgbt(new Color(104,176,196),1f-.96f),
                ColorRampDynImpl.rgbt(new Color(128,196,224),0f),
        };
        r.LAND_COLOR = new ColorRampDynImpl.ColorDef[] {
                ColorRampDynImpl.rgbt(new Color(  8, 68, 34),0f),
                ColorRampDynImpl.rgbt(new Color( 50,101, 50),.23f),
                ColorRampDynImpl.rgbt(new Color(118,141, 69),.48f),
                ColorRampDynImpl.rgbt(new Color(165,184,105),.64f),
                ColorRampDynImpl.rgbt(new Color(205,207,162),.84f),
                ColorRampDynImpl.rgbt(new Color(235,243,248),.96f),
                ColorRampDynImpl.rgbt(new Color(255,255,255),1f),
        };

        r.seaHardRamp=false;
        r.landHardRamp=false;
        return r;
    }
    public static ColorRamp getLefebvre2()
    {
        ColorRampDynImpl r = new ColorRampDynImpl();
        r.SEA_COLOR = new ColorRampDynImpl.ColorDef[] {
                ColorRampDynImpl.rgbt(new Color(  0, 16, 32),1f),
                ColorRampDynImpl.rgbt(new Color(  0, 32, 64),.9f),
                ColorRampDynImpl.rgbt(new Color(  0, 53, 83),.75f),
                ColorRampDynImpl.rgbt(new Color(  5, 70,107),.5f),
                ColorRampDynImpl.rgbt(new Color( 17, 85,124),.25f),
                ColorRampDynImpl.rgbt(new Color( 64,130,160),.125f),
                ColorRampDynImpl.rgbt(new Color(104,176,196),0f),
        };
        r.LAND_COLOR = new ColorRampDynImpl.ColorDef[] {
                ColorRampDynImpl.rgbt(new Color(  8, 68, 34),0f),
                ColorRampDynImpl.rgbt(new Color( 50,101, 50),.25f),
                ColorRampDynImpl.rgbt(new Color(118,141, 69),.5f),
                ColorRampDynImpl.rgbt(new Color(165,184,105),.625f),
                ColorRampDynImpl.rgbt(new Color(205,207,162),.75f),
                ColorRampDynImpl.rgbt(new Color(235,243,248),.875f),
                ColorRampDynImpl.rgbt(new Color(255,255,255),1f),
        };

        r.seaHardRamp=true;
        r.landHardRamp=false;
        return r;
    }

    public boolean isNonlinear();
    
    public Color mapHeight(double h, double seaMin, double landMax);
    public String toHtml(double seaMin, double landMax);
}
