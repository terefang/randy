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

    public static int SIZE = 38;
    public static double GAMMA = 2.4;
    public static double EPSILON = 0.00000001;

    public static double[] SPD_C = {0.96853629, 0.96855103, 0.96859338, 0.96877345, 0.96942204, 0.97143709, 0.97541862, 0.98074186, 0.98580992, 0.98971194, 0.99238027, 0.99409844, 0.995172, 0.99576545, 0.99593552, 0.99564041, 0.99464769, 0.99229579, 0.98638762, 0.96829712, 0.89228016, 0.53740239, 0.15360445, 0.05705719, 0.03126539, 0.02205445, 0.01802271, 0.0161346, 0.01520947, 0.01475977, 0.01454263, 0.01444459, 0.01439897, 0.0143762, 0.01436343, 0.01435687, 0.0143537, 0.01435408};
    public static double[] SPD_M = {0.51567122, 0.5401552, 0.62645502, 0.75595012, 0.92826996, 0.97223624, 0.98616174, 0.98955255, 0.98676237, 0.97312575, 0.91944277, 0.32564851, 0.13820628, 0.05015143, 0.02912336, 0.02421691, 0.02660696, 0.03407586, 0.04835936, 0.0001172, 0.00008554, 0.85267882, 0.93188793, 0.94810268, 0.94200977, 0.91478045, 0.87065445, 0.78827548, 0.65738359, 0.59909403, 0.56817268, 0.54031997, 0.52110241, 0.51041094, 0.50526577, 0.5025508, 0.50126452, 0.50083021};
    public static double[] SPD_Y = {0.02055257, 0.02059936, 0.02062723, 0.02073387, 0.02114202, 0.02233154, 0.02556857, 0.03330189, 0.05185294, 0.10087639, 0.24000413, 0.53589066, 0.79874659, 0.91186529, 0.95399623, 0.97137099, 0.97939505, 0.98345207, 0.98553736, 0.98648905, 0.98674535, 0.98657555, 0.98611877, 0.98559942, 0.98507063, 0.98460039, 0.98425301, 0.98403909, 0.98388535, 0.98376116, 0.98368246, 0.98365023, 0.98361309, 0.98357259, 0.98353856, 0.98351247, 0.98350101, 0.98350852};
    public static double[] SPD_R = {0.03147571, 0.03146636, 0.03140624, 0.03119611, 0.03053888, 0.02856855, 0.02459485, 0.0192952, 0.01423112, 0.01033111, 0.00765876, 0.00593693, 0.00485616, 0.00426186, 0.00409039, 0.00438375, 0.00537525, 0.00772962, 0.0136612, 0.03181352, 0.10791525, 0.46249516, 0.84604333, 0.94275572, 0.96860996, 0.97783966, 0.98187757, 0.98377315, 0.98470202, 0.98515481, 0.98537114, 0.98546685, 0.98550011, 0.98551031, 0.98550741, 0.98551323, 0.98551563, 0.98551547};
    public static double[] SPD_G = {0.49108579, 0.46944057, 0.4016578, 0.2449042, 0.0682688, 0.02732883, 0.013606, 0.01000187, 0.01284127, 0.02636635, 0.07058713, 0.70421692, 0.85473994, 0.95081565, 0.9717037, 0.97651888, 0.97429245, 0.97012917, 0.9425863, 0.99989207, 0.99989891, 0.13823139, 0.06968113, 0.05628787, 0.06111561, 0.08987709, 0.13656016, 0.22169624, 0.32176956, 0.36157329, 0.4836192, 0.46488579, 0.47440306, 0.4857699, 0.49267971, 0.49625685, 0.49807754, 0.49889859};
    public static double[] SPD_B = {0.97901834, 0.97901649, 0.97901118, 0.97892146, 0.97858555, 0.97743705, 0.97428075, 0.96663223, 0.94822893, 0.89937713, 0.76070164, 0.4642044, 0.20123039, 0.08808402, 0.04592894, 0.02860373, 0.02060067, 0.01656701, 0.01451549, 0.01357964, 0.01331243, 0.01347661, 0.01387181, 0.01435472, 0.01479836, 0.0151525, 0.01540513, 0.01557233, 0.0156571, 0.01571025, 0.01571916, 0.01572133, 0.01572502, 0.01571717, 0.01571905, 0.01571059, 0.01569728, 0.0157002};
    public static double[] CIE_CMF_X = {0.00006469, 0.00021941, 0.00112057, 0.00376661, 0.01188055, 0.02328644, 0.03455942, 0.03722379, 0.03241838, 0.02123321, 0.01049099, 0.00329584, 0.00050704, 0.00094867, 0.00627372, 0.01686462, 0.02868965, 0.04267481, 0.05625475, 0.0694704, 0.08305315, 0.0861261, 0.09046614, 0.08500387, 0.07090667, 0.05062889, 0.03547396, 0.02146821, 0.01251646, 0.00680458, 0.00346457, 0.00149761, 0.0007697, 0.00040737, 0.00016901, 0.00009522, 0.00004903, 0.00002};
    public static double[] CIE_CMF_Y = {0.00000184, 0.00000621, 0.00003101, 0.00010475, 0.00035364, 0.00095147, 0.00228226, 0.00420733, 0.0066888, 0.0098884, 0.01524945, 0.02141831, 0.03342293, 0.05131001, 0.07040208, 0.08783871, 0.09424905, 0.09795667, 0.09415219, 0.08678102, 0.07885653, 0.0635267, 0.05374142, 0.04264606, 0.03161735, 0.02088521, 0.01386011, 0.00810264, 0.0046301, 0.00249138, 0.0012593, 0.00054165, 0.00027795, 0.00014711, 0.00006103, 0.00003439, 0.00001771, 0.00000722};
    public static double[] CIE_CMF_Z = {0.00030502, 0.00103681, 0.00531314, 0.01795439, 0.05707758, 0.11365162, 0.17335873, 0.19620658, 0.18608237, 0.13995048, 0.08917453, 0.04789621, 0.02814563, 0.01613766, 0.0077591, 0.00429615, 0.00200551, 0.00086147, 0.00036904, 0.00019143, 0.00014956, 0.00009231, 0.00006813, 0.00002883, 0.00001577, 0.00000394, 0.00000158, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    public static double[][] XYZ_RGB = {{3.24306333, -1.53837619, -0.49893282}, {-0.96896309, 1.87542451, 0.04154303}, {0.05568392, -0.20417438, 1.05799454}};

    public static double linear_to_concentration(double l1, double l2, double t)
    {
        double t1 = l1 * (1 - t) * (1 - t);
        double t2 = l2 * t * t;

        return t2 / (t1 + t2);
    }
}
