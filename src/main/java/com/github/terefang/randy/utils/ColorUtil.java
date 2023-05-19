package com.github.terefang.randy.utils;

import com.github.terefang.randy.noise.NoiseUtil;

import java.awt.*;

/**
 * Created by fredo on 18.10.15.
 */
public class ColorUtil
{
    public static Color rgb_pct(int r, int g, int b, double p)
    {
        r = NoiseUtil.lerp(0,r,p);
        g = NoiseUtil.lerp(0,g,p);
        b = NoiseUtil.lerp(0,b,p);
        return new Color(r,g,b);
    }

    public static Color from(int _r, int _g, int _b)
    {
        return new Color(_r, _g, _b);
    }
    public static Color from(int _rgb)
    {
        return new Color(_rgb);
    }
    public static Color from(int _rgba, boolean _ha)
    {
        return new Color(_rgba, _ha);
    }

    public static Color fromHSL(double h, double s, double l)
    {
        h = h % 360.0f;
        h /= 360f;
        s /= 100f;
        l /= 100f;

        double q = 0;

        if (l < 0.5)
            q = l * (1 + s);
        else
            q = (l + s) - (s * l);

        double p = 2 * l - q;

        double r = Math.max(0, HueToRGB(p, q, h + (1.0f / 3.0f)));
        double g = Math.max(0, HueToRGB(p, q, h));
        double b = Math.max(0, HueToRGB(p, q, h - (1.0f / 3.0f)));

        r = Math.min(r, 1.0f)*255;
        g = Math.min(g, 1.0f)*255;
        b = Math.min(b, 1.0f)*255;

        return from((int)r, (int)g, (int)b);
    }

    static double RgbToHsvV(double _r, double _g, double _b) {
        return Math.max(_r,Math.max(_g,_b));
    }

    static double RgbToHsvS(double _r, double _g, double _b) {
        double rgbMin = Math.min(_r,Math.min(_g,_b));
        double rgbMax = Math.max(_r,Math.max(_g,_b));
        double chroma = rgbMax - rgbMin;
        double L = (rgbMax+rgbMin)/2f;
        double V = L + (chroma/2f);
        if(V<=0f) return 0f;
        return (chroma/V);
    }

    static double RgbToHsvH(double _r, double _g, double _b) {
        double rgbMin = Math.min(_r,Math.min(_g,_b));
        double rgbMax = Math.max(_r,Math.max(_g,_b));
        double chroma = rgbMax - rgbMin;
        double L = (rgbMax+rgbMin)/2f;
        double V = rgbMax;

        double _H = 0f;
        if(chroma <= 0f) return _H;
        if(_r == V)
        {
            _H = (((_g-_b)/chroma) + (_g < _b ? 6 : 0)) % 6;
        }
        else
        if(_g == V)
        {
            _H = ((_b-_r)/chroma) + 2;
        }
        else
        if(_b == V)
        {
            _H = ((_r-_g)/chroma) + 4;
        }

        return 60f * _H;
    }

    static double HueToRGB(double p, double q, double h) {
        if (h < 0)
            h += 1;

        if (h > 1)
            h -= 1;

        if (6 * h < 1) {
            return p + ((q - p) * 6 * h);
        }

        if (2 * h < 1) {
            return q;
        }

        if (3 * h < 2) {
            return p + ((q - p) * 6 * ((2.0f / 3.0f) - h));
        }

        return p;
    }

    public static Color fromHSV(double h, double s, double v) {
        h /= 60f;
        s /= 100f;
        v /= 100f;
        int hi = (int) (Math.floor(h) % 6);

        double f = (double) (h - Math.floor(h));
        int p = (int) (255 * v * (1 - s));
        int q = (int) (255 * v * (1 - (s * f)));
        int t = (int) (255 * v * (1 - (s * (1 - f))));
        v *= 255;

        switch (hi) {
            case 1:
                return from(q, (int)v, p);
            case 2:
                return from(p, (int)v, t);
            case 3:
                return from(p, q, (int)v);
            case 4:
                return from(t, p, (int)v);
            case 5:
                return from((int)v, p, q);
            case 0:
            default:
                return from((int)v, t, p);
        }
    }

    // http://dev.w3.org/csswg/css-color/#hwb-to-rgb
    public static Color fromHWB(double h, double wh, double bl)
    {
        h /= 360f;
        wh /= 100f;
        bl /= 100f;

        double ratio = wh + bl;
        int i;
        double v;
        double f;
        double n;

        double r;
        double g;
        double b;

        // wh + bl cant be > 1
        if (ratio > 1) {
            wh /= ratio;
            bl /= ratio;
        }

        i = (int) Math.floor(6 * h);
        v = 1 - bl;
        f = 6 * h - i;

        if ((i & 0x01) != 0)
        {
            f = 1 - f;
        }

        n = wh + f * (v - wh); // linear interpolation

        switch (i) {
            default:
            case 6:
            case 0: r = v; g = n; b = wh; break;
            case 1: r = n; g = v; b = wh; break;
            case 2: r = wh; g = v; b = n; break;
            case 3: r = wh; g = n; b = v; break;
            case 4: r = n; g = wh; b = v; break;
            case 5: r = v; g = wh; b = n; break;
        }

        return from((int)(r * 255), (int)(g * 255), (int)(b * 255));
    }

    public static Color fromHCG(double h, double c, double g)
    {
        h /= 360f;
        c /= 100f;
        g /= 100f;

        if (c == 0.0) {
            return from((int)(g * 255), (int)(g * 255), (int)(g * 255));
        }

        double[] pure = new double[3];
        double hi = (h % 1) * 6;
        double v = hi % 1;
        double w = 1 - v;
        double mg = 0;

        switch ((int) Math.floor(hi)) {
            case 0:
                pure[0] = 1; pure[1] = v; pure[2] = 0; break;
            case 1:
                pure[0] = w; pure[1] = 1; pure[2] = 0; break;
            case 2:
                pure[0] = 0; pure[1] = 1; pure[2] = v; break;
            case 3:
                pure[0] = 0; pure[1] = w; pure[2] = 1; break;
            case 4:
                pure[0] = v; pure[1] = 0; pure[2] = 1; break;
            default:
                pure[0] = 1; pure[1] = 0; pure[2] = w;
        }

        mg = (1f - c) * g;

        return from(
                (int)((c * pure[0] + mg) * 255),
                (int)((c * pure[1] + mg) * 255),
                (int)((c * pure[2] + mg) * 255)
        );
    }

    public static Color fromXYZ(double x, double y, double z)
    {
        x /= 100f;
        y /= 100f;
        z /= 100f;
        double r;
        double g;
        double b;

        r = (x * 3.2406f) + (y * -1.5372f) + (z * -0.4986f);
        g = (x * -0.9689f) + (y * 1.8758f) + (z * 0.0415f);
        b = (x * 0.0557f) + (y * -0.2040f) + (z * 1.0570f);

        // assume sRGB
        r = r > 0.0031308f
                ? (double) ((1.055f * Math.pow(r, 1.0f / 2.4f)) - 0.055f)
                : r * 12.92f;

        g = g > 0.0031308f
                ? (double) ((1.055f * Math.pow(g, 1.0f / 2.4f)) - 0.055f)
                : g * 12.92f;

        b = b > 0.0031308f
                ? (double) ((1.055f * Math.pow(b, 1.0f / 2.4f)) - 0.055f)
                : b * 12.92f;

        r = Math.min(Math.max(0, r), 1);
        g = Math.min(Math.max(0, g), 1);
        b = Math.min(Math.max(0, b), 1);

        return from(
                (int)(r * 255),
                (int)(g * 255),
                (int)(b * 255)
        );
    }

    public static Color fromLAB(double l, double a, double b)
    {
        double x;
        double y;
        double z;

        y = (l + 16f) / 116f;
        x = a / 500f + y;
        z = y - b / 200f;

        double y2 = (double) Math.pow(y, 3);
        double x2 = (double) Math.pow(x, 3);
        double z2 = (double) Math.pow(z, 3);
        y = y2 > 0.008856f ? y2 : (y - 16f / 116f) / 7.787f;
        x = x2 > 0.008856f ? x2 : (x - 16f / 116f) / 7.787f;
        z = z2 > 0.008856f ? z2 : (z - 16f / 116f) / 7.787f;

        x *= 95.047f;
        y *= 100f;
        z *= 108.883f;

        return fromXYZ(x, y, z);
    }

    public static Color fromLCH(double l, double c, double h)
    {
        double hr = (double) (h / 360f * 2f * Math.PI);
        double a = (double) (c * Math.cos(hr));
        double b = (double) (c * Math.sin(hr));

        return fromLAB(l, a, b);
    }

    public static Color setSaturation(Color _cl, double _s)
    {
        double _r = _cl.getRed()/255f;
        double _g = _cl.getGreen()/255f;
        double _b = _cl.getBlue()/255f;
        double _V = RgbToHsvV(_r,_g,_b)*100.;
        double _H = RgbToHsvH(_r,_g,_b);
        return fromHSV(_H, _s, _V);
    }

    public static Color adjSaturation(Color _cl, double _s)
    {
        double _r = _cl.getRed()/255f;
        double _g = _cl.getGreen()/255f;
        double _b = _cl.getBlue()/255f;
        double _V = RgbToHsvV(_r,_g,_b)*100.;
        double _S = RgbToHsvS(_r,_g,_b)*100.;
        double _H = RgbToHsvH(_r,_g,_b);
        return fromHSV(_H, _S*_s, _V);
    }

    public static Color biasSaturation(Color _cl, double _s)
    {
        double _r = _cl.getRed()/255f;
        double _g = _cl.getGreen()/255f;
        double _b = _cl.getBlue()/255f;
        double _V = RgbToHsvV(_r,_g,_b)*100.;
        double _S = RgbToHsvS(_r,_g,_b)*100.;
        double _H = RgbToHsvH(_r,_g,_b);
        return fromHSV(_H, _S+_s, _V);
    }

    public static Color setValue(Color _cl, double _v)
    {
        double _r = _cl.getRed()/255f;
        double _g = _cl.getGreen()/255f;
        double _b = _cl.getBlue()/255f;
        double _S = RgbToHsvS(_r,_g,_b)*100.;
        double _H = RgbToHsvH(_r,_g,_b);
        return fromHSV(_H, _S, _v);
    }

    public static Color adjValue(Color _cl, double _v)
    {
        double _r = _cl.getRed()/255f;
        double _g = _cl.getGreen()/255f;
        double _b = _cl.getBlue()/255f;
        double _V = RgbToHsvV(_r,_g,_b);
        double _S = RgbToHsvS(_r,_g,_b);
        double _H = RgbToHsvH(_r,_g,_b);
        return fromHSV(_H, _S, _V*_v);
    }

    public static Color biasValue(Color _cl, double _v)
    {
        double _r = _cl.getRed()/255f;
        double _g = _cl.getGreen()/255f;
        double _b = _cl.getBlue()/255f;
        double _V = RgbToHsvV(_r,_g,_b);
        double _S = RgbToHsvS(_r,_g,_b);
        double _H = RgbToHsvH(_r,_g,_b);
        return fromHSV(_H, _S, _V+_v);
    }

    public static Color setHue(Color _cl, double _h)
    {
        double _r = _cl.getRed()/255f;
        double _g = _cl.getGreen()/255f;
        double _b = _cl.getBlue()/255f;
        double _V = RgbToHsvV(_r,_g,_b);
        double _S = RgbToHsvS(_r,_g,_b);
        return fromHSV(_h, _S, _V);
    }

    public static Color biasHue(Color _cl, double _h)
    {
        double _r = _cl.getRed()/255f;
        double _g = _cl.getGreen()/255f;
        double _b = _cl.getBlue()/255f;
        double _V = RgbToHsvV(_r,_g,_b);
        double _S = RgbToHsvS(_r,_g,_b);
        double _H = RgbToHsvH(_r,_g,_b);
        return fromHSV(_H+_h, _S, _V);
    }

    public static Color colorLerp(Color a, Color b, double t)
    {
        return from(
                NoiseUtil.lerp(a.getRed(), b.getRed(), t),
                NoiseUtil.lerp(a.getGreen(), b.getGreen(), t),
                NoiseUtil.lerp(a.getBlue(), b.getBlue(), t)
        );
    }

    public static Color hsvLerp(Color a, Color b, double t)
    {
        double _ar = a.getRed()/255f;
        double _ag = a.getGreen()/255f;
        double _ab = a.getBlue()/255f;
        double _br = b.getRed()/255f;
        double _bg = b.getGreen()/255f;
        double _bb = b.getBlue()/255f;
        double _ah = RgbToHsvH(_ar,_ag, _ab);
        double _bh = RgbToHsvH(_br,_bg, _bb);
        double _ch = NoiseUtil.lerp(_ah,_bh, t);
        double _as = RgbToHsvS(_ar,_ag, _ab);
        double _bs = RgbToHsvS(_br,_bg, _bb);
        double _cs = NoiseUtil.lerp(_as,_bs, t);
        double _av = RgbToHsvV(_ar,_ag, _ab);
        double _bv = RgbToHsvV(_br,_bg, _bb);
        double _cv = NoiseUtil.lerp(_av,_bv, t);
        return fromHSV(_ch, _cs*100f, _cv*100f);
    }
}
