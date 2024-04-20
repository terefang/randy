package com.github.terefang.randy.starsys.model;

import com.github.terefang.randy.rng.impl.ArcRand;
import com.github.terefang.randy.starsys.StarSysUtil;
import lombok.Data;
import lombok.SneakyThrows;

import java.text.MessageFormat;
import java.util.List;

@Data
public class SolarContext
{
    int seed;
    int size;
    String rsize;
    String spectra;
    float num;

    double surfaceDistance;
    double outerPlanetaryLimit;
    double mass;
    double luminosity;
    double surfaceTemperature;
    double safeJumpDistance;

    public static SolarContext randomType(int _seed, double _bias)
    {
        ArcRand _rng = ArcRand.from(_seed);
        String _lookup = StarSysUtil.getString(StarSysUtil.merge("star_table","default"));
        _lookup = StarSysUtil.lookupTabled(_bias, _rng, StarSysUtil.merge("star_table"), _lookup);
        while(_lookup.startsWith("*"))
        {
            _lookup = StarSysUtil.lookupTabled(_rng, StarSysUtil.merge("star_table"), _lookup);
        }
        String _spectra=StarSysUtil.getString(StarSysUtil.merge("star_table",_lookup,"spectra"));
        List<String> _list = StarSysUtil.getList(StarSysUtil.merge("star_table",_lookup,"sizes"));
        String _rsize = _list.get((int) _rng.next(_list.size()));
        float _num = _rng.next(100f)/10f;
        return from(_seed, _spectra, _rsize, _num);
    }

    public static SolarContext from(int _seed, String _spectra, String _rsize, float _num)
    {
        SolarContext _ctx = new SolarContext();
        _ctx.seed = _seed;
        _ctx.spectra = _spectra;
        _ctx.rsize = _rsize;
        if(!"!".equalsIgnoreCase(_ctx.spectra))
        {
            _ctx.size = StarSysUtil.getInt(StarSysUtil.merge("quantify","size",_ctx.rsize));
        }
        _ctx.num = _num;
        return _ctx;
    }

    public static SolarContext from(int _seed, double _mass, boolean _gaia)
    {
        return from(_seed, _mass, -1., -1., Double.MIN_VALUE, _gaia);
    }
    public static SolarContext from(int _seed, double _mass, double _radius, double _lum, double _bv, boolean _gaia)
    {
        SolarContext _ctx = new SolarContext();
        _ctx.seed = _seed;
        _ctx.mass = _mass;

        _ctx.luminosity= (_lum < 0.) ? StarSysUtil.calcStarLuminosityFromMass(_ctx.mass, _gaia) : _lum;
        _ctx.surfaceDistance = (_radius < 0.) ? StarSysUtil.calcStarRadiusMS(_ctx.mass) : _radius;

        _ctx.surfaceTemperature = (_bv == Double.MIN_VALUE) ?
                StarSysUtil.calcStarSurfaceTempKelvin(_ctx.surfaceDistance, _ctx.luminosity)
                : StarSysUtil.calcStarTempFromBV(_bv) ;

        _ctx.spectra = StarSysUtil.calcStarSpectralClass(_ctx.surfaceTemperature);
        _ctx.num = StarSysUtil.calcStarSpectralNum(_ctx.surfaceTemperature);

        _ctx.size = StarSysUtil.getInt(StarSysUtil.merge("quantify", "spectra", _ctx.spectra));
        _ctx.rsize = StarSysUtil.getString(StarSysUtil.merge("quantify", "rsize"), _ctx.size);

        _ctx.surfaceDistance *= (_radius < 0.) ? StarSysUtil.getFloat(StarSysUtil.merge("stellar_type",_ctx.spectra,_ctx.rsize,"surface_distance_adjustment")): 1.;

        _ctx.outerPlanetaryLimit = StarSysUtil.calcStarOuterPlanetaryLimit(_ctx.mass);
        _ctx.safeJumpDistance = StarSysUtil.calcSafeJumpDistanceAU(_ctx.surfaceDistance*StarSysUtil.SOL_TO_AU, _ctx.mass);

        return _ctx;
    }

    public static SolarContext randomMK(int _seed)
    {
        SolarContext _ctx = new SolarContext();
        _ctx.seed = _seed;

        ArcRand _rng = ArcRand.from(_seed);

        List<String> _list = StarSysUtil.getKeys(StarSysUtil.merge("quantify","spectra"));
        int _off = (int) _rng.nextDice(1, _list.size(), -1);
        _ctx.spectra = _list.get(_off);
        _ctx.num = _rng.next(100f)/10f;

        _list = StarSysUtil.getList(StarSysUtil.merge("quantify", "rsize"));
        _off = (int) _rng.nextDice(1, _list.size(), -1);
        _ctx.size = _off+1;
        _ctx.rsize = _list.get(_off);
        return _ctx;
    }

    public static SolarContext tabledMK(int _seed)
    {
        ArcRand _rng = ArcRand.from(_seed);
        String _lookup = StarSysUtil.getString(StarSysUtil.merge("star_table","default"));
        while(_lookup.startsWith("*"))
        {
            _lookup = StarSysUtil.lookupTabled(_rng, StarSysUtil.merge("star_table"), _lookup);
        }
        String _spectra=StarSysUtil.getString(StarSysUtil.merge("star_table",_lookup,"spectra"));
        List<String> _list = StarSysUtil.getList(StarSysUtil.merge("star_table",_lookup,"sizes"));
        String _rsize = _list.get((int) _rng.next(_list.size()));
        float _num = _rng.next(100f)/10f;
        return from(_seed, _spectra, _rsize, _num);
    }

    public static SolarContext tabledMK(int _seed, double _u, double _v, double _w)
    {
        if(_u>1.) _u = 1./_u;
        if(_v>1.) _v = 1./_v;
        if(_w>1.) _w = 1./_w;

        SolarContext _ctx = new SolarContext();
        _ctx.seed = _seed;
        ArcRand _rng = ArcRand.from(_seed);
        String _lookup = StarSysUtil.getString(StarSysUtil.merge("star_table","default"));
        while(_lookup.startsWith("*"))
        {
            _lookup = StarSysUtil.lookupTabled(_rng, StarSysUtil.merge("star_table"), _lookup);
        }
        _ctx.spectra=StarSysUtil.getString(StarSysUtil.merge("star_table",_lookup,"spectra"));
        List<String> _list = StarSysUtil.getList(StarSysUtil.merge("star_table", _lookup, "sizes"));
        _ctx.rsize = _list.get((int) _rng.next(_list.size()));
        if(!"!".equalsIgnoreCase(_ctx.spectra))
        {
            _ctx.size = StarSysUtil.getInt(StarSysUtil.merge("quantify","size", _ctx.rsize));
        }
        _ctx.num = _rng.next(100f)/10f;
        return _ctx;
    }

    public static SolarContext tabledCompanionMK(int _seed, String _rsize)
    {
        SolarContext _ctx = new SolarContext();
        _ctx.seed = _seed;
        ArcRand _rng = ArcRand.from(_seed);
        String _lookup = "*companion-"+_rsize.toLowerCase();
        while(_lookup.startsWith("*"))
        {
            _lookup = StarSysUtil.lookupTabled(_rng, StarSysUtil.merge("star_table"), _lookup);
        }
        _ctx.spectra=StarSysUtil.getString(StarSysUtil.merge("star_table",_lookup,"spectra"));
        List<String> _list = StarSysUtil.getList(StarSysUtil.merge("star_table", _lookup, "sizes"));
        _ctx.rsize = _list.get((int) _rng.next(_list.size()));
        if(!"!".equalsIgnoreCase(_ctx.spectra))
        {
            _ctx.size = StarSysUtil.getInt(StarSysUtil.merge("quantify","size",_ctx.rsize));
        }
        _ctx.num = _rng.next(100f)/10f;
        return _ctx;
    }

    public static SolarContext fromMass(double _mass, boolean _gaia)
    {
        SolarContext _ctx = new SolarContext();
        _ctx.randomizeFromMass(_mass, _gaia);
        return _ctx;
    }

    public static SolarContext quantifyMK(String _mk)
    {
        _mk = _mk.replaceAll("[ab]", "");
        String _mk1, _mk3;
        float _mk2;
        switch(_mk.charAt(0))
        {
            case 'O':
            case 'B':
            case 'A':
            case 'F':
            case 'G':
            case 'K':
            case 'M':
            case 'R':
            case 'N':
            case 'S':
            case 'C':
                _mk1 = _mk.substring(0,1);
                if(_mk.charAt(2)=='.')
                {
                    _mk2 = Float.parseFloat(_mk.substring(1,4));
                    _mk3 = _mk.substring(4);
                }
                else
                {
                    _mk2 = Float.parseFloat(_mk.substring(1,2));
                    _mk3 = _mk.substring(2);
                }
                break;
            default:
                _mk1="!";
                _mk2=0;
                _mk3="";
                break;
        }

        if(_mk3.equalsIgnoreCase(""))
        {
            _mk3 = "n";
        }

        SolarContext _ctx = new SolarContext();
        _ctx.spectra = _mk1;
        _ctx.num = _mk2;
        if("n".equalsIgnoreCase(_mk3))
        {
            _ctx.size = StarSysUtil.getInt(StarSysUtil.merge("quantify","spectra",_mk1));
        }
        else
        {
            _ctx.size = StarSysUtil.getInt(StarSysUtil.merge("quantify","size",_mk3));
        }
        _ctx.rsize = StarSysUtil.getString(StarSysUtil.merge("quantify","rsize",""+_ctx.size));

        return _ctx;
    }

    public static String formatStellarClass(SolarContext _ctx)
    {
        return MessageFormat.format("{0}{1}{2}", _ctx.spectra, _ctx.num, _ctx.rsize);
    }

    @SneakyThrows
    public void outputStarInformation(Appendable _fh)
    {
        _fh.append("======================================================\n");
        _fh.append(String.format("         Seed Value: 0x%X\n", this.getSeed()));
        _fh.append(String.format("      Stellar Class: %s%03.1f%s\n", this.getSpectra(), this.getNum(), this.getRsize()));
        _fh.append(String.format("               Mass: %.3f Sol, %.3f kg(1e+27)\n", this.getMass(), this.getMass()*StarSysUtil.SOL_TO_KG27));
        _fh.append(String.format("         Luminosity: %.3f Sol, %.3f J/s(1e+24)\n", this.getLuminosity(), this.getLuminosity()*StarSysUtil.SOL_TO_JS));
        _fh.append(String.format("Surface Temperature: %.3f K, %.3f C\n", this.getSurfaceTemperature(), this.getSurfaceTemperature()-273.15f));
        _fh.append(String.format("     Stellar Radius: %.3f sol, %.5f AU\n", this.getSurfaceDistance(), this.getSurfaceDistance()*StarSysUtil.SOL_TO_AU));
        _fh.append(String.format(" max. Orbital Limit: %.3f AU\n", this.getOuterPlanetaryLimit()));
        _fh.append(String.format("    Safe Jump Limit: %.3f AU\n", this.getSafeJumpDistance()));
    }

    public void randomizeFromMk(int _rand)
    {
        if("!".equalsIgnoreCase(this.spectra)) return;


        double _temp_base = StarSysUtil.getFloat(StarSysUtil.merge("stellar_type",this.spectra,"temperature_base"));
        double _temp_var = StarSysUtil.getFloat(StarSysUtil.merge("stellar_type",this.spectra,"temperature_variation"));
        double _mass_base = StarSysUtil.getFloat(StarSysUtil.merge("stellar_type",this.spectra,"mass_base"));
        double _mass_var = StarSysUtil.getFloat(StarSysUtil.merge("stellar_type",this.spectra,"mass_variation"));
        double _mass_adj = StarSysUtil.getFloat(StarSysUtil.merge("stellar_type",this.spectra,this.rsize,"mass_adjustment"));
        double _surf_dist_adj = StarSysUtil.getFloat(StarSysUtil.merge("stellar_type",this.spectra,this.rsize,"surface_distance_adjustment"));

        this.mass = _mass_base - ((this.num-1.8f)*_mass_var);
        if(_rand>0)
        {
            ArcRand _rng = ArcRand.from(_rand);
            this.mass *= _rng.nextAbout(1f, 0.12f);
        }
        this.mass*=_mass_adj;
        this.surfaceTemperature =  (5700f *Math.pow(_temp_base-(this.num*_temp_var), 0.5));

        this.luminosity=StarSysUtil.calcStarLuminosityFromMass(this.mass, true);

        //this.surfaceDistance =  Math.pow((this.luminosity/Math.pow((this.surfaceTemperature/5812),4)),0.5)*_surf_dist_adj;
        this.surfaceDistance =  Math.pow((this.luminosity/Math.pow((this.surfaceTemperature/5812),4)),0.5);

        this.outerPlanetaryLimit =  (200 * Math.pow(Math.pow(this.mass,2),0.3333333333333333));
        // new limit
        this.outerPlanetaryLimit = 4 * Math.pow(this.outerPlanetaryLimit, .5);

        this.safeJumpDistance = (this.surfaceDistance*StarSysUtil.SOL_TO_AU*100)+Math.pow(this.mass,0.3);
    }

    public void randomizeFromMass(double _mass, boolean _gaia)
    {
        this.mass = _mass;
        this.luminosity=StarSysUtil.calcStarLuminosityFromMass(this.mass, _gaia);

        this.surfaceDistance =  StarSysUtil.calcStarRadiusMS(this.mass);

        this.surfaceTemperature =  StarSysUtil.calcStarSurfaceTempKelvin(this.surfaceDistance, this.luminosity);

        if(_gaia) {
            //this.surfaceDistance =  Math.pow((this.luminosity/Math.pow((this.surfaceTemperature/5812),4)),0.5)*_surf_dist_adj;
            this.surfaceDistance = StarSysUtil.calcStarRadius(this.luminosity, this.surfaceTemperature);

            this.surfaceTemperature = StarSysUtil.calcStarSurfaceTempKelvin(this.surfaceDistance, this.luminosity);
        }

        this.spectra = StarSysUtil.calcStarSpectralClass(this.surfaceTemperature);
        this.num = StarSysUtil.calcStarSpectralNum(this.surfaceTemperature);

        if(_gaia)
        {
            this.size = StarSysUtil.calcStarGaiaSize(this.surfaceTemperature);
        }
        else
        {
            this.size = StarSysUtil.getInt(StarSysUtil.merge("quantify", "spectra", this.spectra));
        }
        this.rsize = StarSysUtil.getString(StarSysUtil.merge("quantify", "rsize"), this.size);
        this.outerPlanetaryLimit = StarSysUtil.calcStarOuterPlanetaryLimit(this.mass);

        this.safeJumpDistance = StarSysUtil.calcSafeJumpDistanceAU(this.surfaceDistance*StarSysUtil.SOL_TO_AU, this.mass);
    }

    public static void main(String[] args) {
        for(double _i=10. ; _i>.1; _i-=.25)
        {
            SystemContext.from(0,0,
            SolarContext.from(0xbeef, _i, true), true).outputInformation(System.err);
        }
    }
}
