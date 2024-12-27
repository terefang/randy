package com.github.terefang.randy.starsys;

import com.github.terefang.randy.rng.impl.ArcRand;
import com.github.terefang.randy.starsys.model.SolarContext;
import com.github.terefang.randy.utils.ClasspathResourceLoader;
import com.github.terefang.randy.utils.LdataParser;
import lombok.SneakyThrows;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

public class StarSysUtil {
    static Map<String,Object> _data;

    // Calculate the Roche limit for a fluid body on circular orbit
    public static double calcRocheLimit(double _diameter /* km */, double _dp, double _dm /* densties */)
    {
        return 2.44 * (_diameter/2.) * Math.pow(_dp/_dm, 1./3.);
    }

    public static double calcRocheLimit(double _diameter /* km */, double _q /* mass ratio < 1 */)
    {
        return 2.44 * (_diameter/2.) * Math.pow(_q, -1./3.);
    }

    public static int calcOrbitalZone0(double _orbit /* AU */, double _lum /* sol */)
    {
        if(_orbit < (0.8f*Math.sqrt(_lum)))
        {
            return 1;
        }
        else if(_orbit < (1.6f*Math.sqrt(_lum)))
        {
            return 2;
        }
        return 3;
    }

    public static int calcOrbitalZone(double _orbit /* AU */, double _lum /* sol */)
    {
        int _zone = calcOrbitalZone0(_orbit,_lum);
        double _f = _orbit/Math.sqrt(_lum);
        if(_f < 0.8f)
        {
            return 1;
        }
        else if(_f < 1.6f)
        {
            return 2;
        }
        return 3;
    }

    public static int calcOrbitalZone(double _orbit /* AU */, SolarContext _sol)
    {
        int _zone = calcOrbitalZone(_orbit, _sol.getLuminosity());
        int _czone = _zone;
        double _outer = 0.;
        if(_sol.isMulti())
        {
            double _f = 0.;
            for(int _i=_sol.getComponents().size()-1 ; _i>=0; _i--)
            {
                double _off = _sol.getComponentOffsets().get(_i);
                double _o = _orbit-_off;
                double _l = _sol.getComponents().get(_i).getLuminosity();
                _f += _o/Math.sqrt(_l);
                _outer += _off;
            }
            _outer+=1.6;

            if(_f < 0.8f)
            {
                _czone = 1;
            }
            else if(_f < _outer)
            {
                _czone = 2;
            }
            else
            {
                _czone = 3;
            }
        }
        return (_zone<_czone) ? _zone : _czone;
    }

    public static double calcPlanetaryEmpiricalDensity(double _mass, double _radius, double _lum)
    { // in earth_masses, AUs and solar_luminosity, returns kg/m3
        return  (Math.pow(_mass,0.125)*Math.pow((Math.sqrt(_lum)/_radius),0.25)*1200);
    }

    public static double calcPlanetaryDiameter(double _mass, double _density)
    { // in earth_masses and kg/m3, returns km
        return  (Math.pow((((_mass*5.9742E24)/_density)/(3.14159265359/6)),0.333333333333)/1000f);
    }

    public static double calcMoonDiameter(double _mass, double _density)
    { // in moon_masses and kg/m3, returns km
        return  (Math.pow((((_mass*7.34E22)/_density)/(3.14159265359/6)),0.333333333333)/1000f);
    }

    public static double calcKothariDiameter(double _mass, double  _zone)
    { //# in kg and ?, returns km
        _mass=_mass/1.989E30f;
        double _atomic_weight=10.f*_zone;
        double _atomic_num=7.f*_zone;
        double _temp=_atomic_weight*_atomic_num;
        _temp=  ((2.f*5.71E12*Math.pow(1.989E33,(1/3)))/(6.485E12*Math.pow(_temp,(1/3))));
        double _temp2=  ((4.0032E-8)*Math.pow(_atomic_weight,(4/3))*Math.pow(1.989E33,(2/3)));
        _temp2*=Math.pow(_mass,(2/3));
        _temp2=  (_temp2 / (6.485E12 * Math.pow(_atomic_num,2)));
        _temp2+=1.0;
        _temp=_temp/_temp2;
        _temp=  ((_temp*Math.pow(_mass,(1/3)))/1.0E5);
        return _temp*2;
    }

    public static final double SOL_TO_KG27 = 1989.1; // (1e+27)kg
    public static final double SOL_TO_JS = 382.8; // (1e+24)J/s
    public static final double AU_TO_KM = 149600000f; //AU
    public static final double SOL_TO_AU = 696000f/149600000f; //AU

    // Stefan–Boltzmann constant, W⋅m−2⋅K−4
    public static final double SIGMA = 5.670374419e-8;

    public static double calcSafeJumpDistanceAU(double _radiusAU, double _massSOL)
    {
        return (_radiusAU*100)+Math.pow(_massSOL,0.3);
    }

    /*
    *    Black-body based conversion between effective temperature and B-V color.

    Ballesteros 2012 (EPL 97, 34008) present a conversion between
    effective temperature and B-V color index based on a black body
    spectrum and the filter functions.
    */

    public static final double BALL_A = 0.92;
    public static final double BALL_B = 1.7;
    public static final double BALL_C = 0.62;
    public static final double BALL_T0 = 4600.;

    public static double calcStarTempFromBV_Ballesteros(double _bv)
    {
        return BALL_T0*(1.0/(BALL_A*_bv + BALL_B) + 1.0/(BALL_A*_bv + BALL_C));
    }


    public static double calcStarBVcolorIndex(double _temp)
    {
        return Math.pow((5601./_temp), 1.5) - .4;
    }

    public static double calcStarTempFromBV(double _bv)
    {
        /* lets average between the quick and black body method */
        return .5*(calcStarTempFromBV_Quick(_bv)+calcStarTempFromBV_Ballesteros(_bv));
    }

    public static double calcStarTempFromBV_Quick(double _bv)
    {

        return 5601./Math.pow((_bv +.4),(2./3.));
    }

    public static float calcStarSpectralNum(double _temp)
    {
        double num = 0;
        if(_temp >= 30000.)
        {
            // ignore
        }
        else
        if(_temp >= 10000.)
        {
            num = 9. * (1. - Math.pow((_temp-10000.)/(30000.-10000.), .5));
        }
        else
        if(_temp >= 7500.)
        {
            num = 9. * (1. - Math.pow((_temp-7500.)/(10000.-7500.), .5));
        }
        else
        if(_temp >= 6000.)
        {
            num = 9. * (1. - Math.pow((_temp-6000.)/(7500.-6000.), .5));
        }
        else
        if(_temp >= 5200.)
        {
            num = 9. * (1. - Math.pow((_temp-5200.)/(6000.-5200.), .5));
        }
        else
        if(_temp >= 3700.)
        {
            num = 9. * (1. - Math.pow((_temp-3700.)/(5200.-3700.), .5));
        }
        else
        if(_temp >= 2400.)
        {
            num = 9. * (1. - Math.pow((_temp-2400.)/(3700.-2400.), .5));
        }
        else
        if(_temp >= 1300.)
        {
            num = 9. * (1. - Math.pow((_temp-1300.)/(2400.-1300.), .5));
        }
        else
        if(_temp >= 550.)
        {
            num = 9. * (1. - Math.pow((_temp-550.)/(1300.-550.), .5));
        }
        else
        if(_temp >= 273.)
        {
            num = 9. * (1. - Math.pow((_temp-273.)/(550.-273.), .5));
        }

        if(num < 0f) num = 0f;
        if(num > 9.9f) num = 9.9f;
        return (float)num;
    }

    public static String calcStarSpectralClass(double _temp)
    {
        if(_temp >= 30000.)
        {
            return "O";
        }
        else
        if(_temp >= 10000.)
        {
            return "B";
        }
        else
        if(_temp >= 7500.)
        {
            return "A";
        }
        else
        if(_temp >= 6000.)
        {
            return "F";
        }
        else
        if(_temp >= 5200.)
        {
            return "G";
        }
        else
        if(_temp >= 3700.)
        {
            return "K";
        }
        else
        if(_temp >= 2400.)
        {
            return "M";
        }
        else
        if(_temp >= 1300.)
        {
            return "R"; // L
        }
        else
        if(_temp >= 550.)
        {
            return "N"; // T
        }
        else
        if(_temp >= 273.)
        {
            return "S"; // Y
        }
        return "!";
    }

    public static int calcStarGaiaSize(double _temp)
    {
        if(_temp >= 30000.)
        {
            return 3; // "O";
        }
        else
        if(_temp >= 10000.)
        {
            return 4; // "B";
        }
        else
        if(_temp >= 7500.)
        {
            return 4; // "A";
        }
        else
        if(_temp >= 6000.)
        {
            return 5; // "F";
        }
        else
        if(_temp >= 5200.)
        {
            return 5; // "G";
        }
        else
        if(_temp >= 3700.)
        {
            return 5; // "K";
        }
        else
        if(_temp >= 2400.)
        {
            return 5; // "M";
        }
        else
        if(_temp >= 1300.)
        {
            return 6; // "R"; // L
        }
        else
        if(_temp >= 550.)
        {
            return 6; // "N"; // T
        }
        else
        if(_temp >= 273.)
        {
            return 6; // "S"; // Y
        }
        return 7;
    }

    public static double calcStarOuterPlanetaryLimit(double _mass)
    {
        double _limit = (200 * Math.pow(Math.pow(_mass,2),0.3333333333333333));
        // new limit
        return 4 * Math.pow(_limit, .5);
    }
    public static double calcStarSurfaceTempKelvin(double _radius, double _luminosity)
    {
        double _lums_watt = _luminosity * SOL_TO_JS * 1e24;
        double _radius_meter = _radius * SOL_TO_AU * AU_TO_KM * 1000.;
        return Math.pow(_lums_watt / (4. * Math.PI * _radius_meter * _radius_meter * SIGMA), .25);
    }

    public static double calcStarMainSequenceAge(double _mass, double _luminosity)
    {
        return 1e10 * (_mass/_luminosity);
    }

    public static double calcStarRadiusMS(double _mass)
    {
        if(_mass <= 1.66)
        {
            return 1.06 * Math.pow(_mass, .945);
        }
        return 1.33 * Math.pow(_mass, .555);
    }

    public static double calcStarRadius(double _lum, double _temp)
    {
        return Math.pow((_lum/Math.pow((_temp/5806),4)),0.5);
    }

    public static double calcStarLuminosityFromMassAndMetalicity(double _Mass, double _metalicity, boolean _gaia)
    {
        // Populatiion I stars have currently metalicity of 0.6
        // L = 4.6 × M^3 × m^4
        // solving for sol L=M=1 -> m^4 = 1/4.6 -> m = 0.682826774
        return 4.6*Math.pow(_Mass,3.)*Math.pow(_metalicity,4.);
    }
    
    public static double calcStarLuminosityFromBolometricMagnitude(double _mbol)
    {
        // L = 85.5 x 0.4 ^ Mbol
        return 85.5 * Math.pow(0.4, _mbol);
    }
    
    public static double calcStarLuminosityFromMass(double _mass, boolean _gaia)
    {
        if(_gaia)
        {
            return Math.pow(_mass,3.5);
        }
        else if(_mass<0.7)
        {
            return Math.pow(_mass,(3.75*_mass+2.125));
        }
        else if (_mass<2)
        {
            return Math.pow(_mass,4.75);
        }
        else if (_mass<5)
        {
            return 1.4*Math.pow(_mass,4.75);
        }
        else if (_mass<10)
        {
            return 1.4*Math.pow(_mass,3.45);
        }
        else if (_mass<20)
        {
            return 1.4*Math.pow(_mass,3.05);
        }
        else if (_mass<50)
        {
            return 1.4*Math.pow(_mass,2.65);
        }
        else
        {
            return 1.4*Math.pow(_mass,2.35);
        }
    }

    public static double calcVolumeDensity(double _mass, double _diameter)
    { //# in kg and km, returns kg/m3
        return  (_mass/((4.f*3.14159265359f*Math.pow((_diameter*1000/2),3))/3.f));
    }

    public static double calcEscapeVelocity(double _mass, double _diameter)
    { //# in kg and km, returns km/sec
        return  (Math.sqrt(4*6.67259e-11*_mass/(_diameter*1000))/1000);
    }

    public static double calcSurfaceAccel(double _mass, double _diameter)
    { //# in kg and km, returns m/sec2
        return  (6.67259e-11*(_mass/Math.pow((_diameter*1000/2),2)));
        //# G = 6.67259 (± 0.00030) × 10-11 kg-1 m3 s-2
    }

    public static double calcExospericTemp(double _lum, double _orbit)
    {
        return (1273*_lum/Math.pow(_orbit,2));
    }

    public static double calcExospericTemp(SolarContext _sol, double _orbit)
    {
        if(_sol.isMulti())
        {
            double _t = 0;
            for(int _i=0 ; _i<_sol.getComponents().size(); _i++)
            {
                _t+= _sol.getComponents().get(_i).getLuminosity()/Math.pow(_orbit-_sol.getComponentOffsets().get(_i),2);
            }
            return (1273*_t);
        }
        return (1273*_sol.getLuminosity()/Math.pow(_orbit,2));
    }
    public static double calcRmsVelocity(double _mol, double _lum, double _orbit)
    { //# in molar_weight, solar_luminance, AU, returns m/sec
        double _exospheric_temp = calcExospericTemp(_lum, _orbit);
        return Math.sqrt((3.0 * 8314.41 * _exospheric_temp)/_mol);
    }

    public static double calcRmsVelocity(double _mol, double _exospheric_temp)
    { //# in molar_weight, solar_luminance, AU, returns m/sec
        return Math.sqrt((3.0 * 8314.41 * _exospheric_temp)/_mol);
    }

    public static double calcMoleculeLimit(double _mass, double _diameter, double _lum, double _orbit)
    { //# in kg and km, returns mol
        double _exospheric_temp = calcExospericTemp(_lum, _orbit);
        double _ev = calcEscapeVelocity(_mass,_diameter);
        return ((3*Math.pow(5,2)*8.31441*_exospheric_temp)/Math.pow((_ev*1000),2)*1000);
        //#return (3*(5**2)*8.31441*1273*$lum)/((escape_velo($mass,$diameter)*1000)**2)*1000;
    }

    public static double calcMoleculeLimit(double _mass, double _diameter, double _exospheric_temp)
    { //# in kg and km, returns mol
        double _ev = calcEscapeVelocity(_mass,_diameter);
        return ((3*Math.pow(5,2)*8.31441*_exospheric_temp)/Math.pow((_ev*1000),2)*1000);
        //#return (3*(5**2)*8.31441*1273*$lum)/((escape_velo($mass,$diameter)*1000)**2)*1000;
    }

    public static double calcGasInventory(double _mass, double _escape, double _rms, double _smass, int _zone, boolean _green)
    {
        double _velocity_ratio = _escape / _rms;
        double _const = 1;
        double _gasinventory = 0;
        if (_velocity_ratio >= 5) {
            if (_zone==1) { _const=1000; }
            if (_zone==2) { _const=750; }
            if (_zone==3) { _const=2.5; }
            _gasinventory=_const*_mass/_smass;
        }
        if (_green) { _gasinventory*=100; }
        return _gasinventory;
    }

    public static double calcOpacity(double _mol, double _pressure)
    {
        double _opticaldepth = 0.0;
        if (_mol < 10) {
            _opticaldepth+=3.0;
        } else if (_mol < 20) {
            _opticaldepth+=2.34;
        } else if (_mol < 30) {
            _opticaldepth+=1.0;
        } else if (_mol < 45) {
            _opticaldepth+=0.15;
        } else if (_mol < 100) {
            _opticaldepth+=0.05;
        }
        if (_pressure > 70) {
            _opticaldepth*=8.3333;
        } else if (_pressure > 50) {
            _opticaldepth*=6.6666;
        } else if (_pressure > 30) {
            _opticaldepth*=3.3333;
        } else if (_pressure > 10) {
            _opticaldepth*=2.0;
        } else if (_pressure > 5) {
            _opticaldepth*=1.5;
        } else if (_pressure > 1.2) {
            _opticaldepth*=1.1111;
        } else if (_pressure > 0.8) {
            _opticaldepth*=1;
        } else if (_pressure > 0.5) {
            _opticaldepth*=0.5;
        }
        return _opticaldepth;
    }

    public static double calcHydroFraction(double _gas, double _diameter)
    {
        double _temp = (0.6*_gas/1000.0)*Math.pow((2*6378/_diameter),2);
        return (_temp<1) ? _temp : 1;
    }

    public static double calcCloudFraction (double _temp, double _mol, double _diameter, double _hydro, double _htype)
    {
        if (_mol<18) {
            double _surf_area=Math.PI*Math.pow(_diameter,2);
            double _hydro_mass=_surf_area*_hydro*_htype;
            double _fraction=1.839e-8*(0.00000001*_hydro_mass)*Math.exp(0.0698*(_temp-288))/_surf_area;
            return (_fraction<1) ? _fraction : 1;
        } else {
            return 0;
        }
    }

    public static double calcIceFraction(double _temp, double _hydro)
    {
        if (_temp>328) _temp=328;
        double _fract=Math.pow(((328-_temp)/90.0),5);
        if (_fract>(1.5*_hydro)) { _fract=1.5*_hydro; }
        return (_fract<1) ? _fract : 1;
    }

    public static double calcAlbedo(double _hydro, double _cloud, double _ice, double _pressure)
    {
        double _rock_fract=1-_hydro-_ice;
        double _components = 0.0;
        if (_hydro > 0.0) _components++;
        if (_ice > 0.0) _components++ ;
        if (_rock_fract > 0.0) _components++ ;
        double _cloud_adjustment = _cloud/_components;
        _rock_fract= (_rock_fract>=_cloud_adjustment) ? (_rock_fract-_cloud_adjustment) : 0;
        _hydro= (_hydro>_cloud_adjustment) ? (_hydro-_cloud_adjustment) : 0;
        _ice= (_ice>_cloud_adjustment) ? (_ice-_cloud_adjustment) : 0;
        double _cloud_part = _cloud * 0.52; // rand_about(0.52,0.2);
        //my $rock_part = ($pressure<0.00000001) ? ($rock_fract*rand_about(0.07,0.3)) : ($rock_fract*rand_about(0.15,0.1)) ;
        double _rock_part = (_pressure<0.00000001) ? (_rock_fract*0.07) : (_rock_fract*0.15) ;
        //my $water_part = $water_fract * rand_about(0.04,0.2);
        double _water_part = _hydro * 0.04;
        //my $ice_part = ($pressure<0.00000001) ? ($ice_fract*rand_about(0.5,0.3)) : ($ice_fract*rand_about(0.7,0.1)) ;
        double _ice_part = (_pressure<0.00000001) ? (_ice*0.5) : (_ice*0.7) ;
        return (_cloud_part + _rock_part + _water_part + _ice_part);
    }

    public static double calcBondAlbedo(double _ice, double _pressure)
    {
        return calcAlbedo(0.,0.,_ice,_pressure);
    }

    /* Bond Albedo :
        Mercury	0.088
        Venus	0.76
        Earth	0.306
            Moon	0.11
        Mars 	0.25
        Jupiter	0.503
        Saturn	0.342
            Enceladus	0.81
        Uranus	0.300
        Neptune	0.290
        Pluto	0.41
            Charon	0.29
            Haumea	0.33
            Makemake	0.74
            Eris	0.99
    */

    public static double calcGreenRise(double _opt,double _temp,double _pressure)
    {
        return ((Math.pow((1+(_opt*0.75)),0.25)-1)*0.43*Math.pow((_pressure),0.25));
    }

    public static double calcEffectiveTemperature(double _lum,double _orbit,double _albedo)
    {
        return Math.sqrt(Math.sqrt(_lum)/_orbit)*Math.pow(((1-_albedo)/0.7),0.25)*300;
    }

    public static double calcSurfPressure(double _gas, double _diameter, double _gravity)
    {
        return _gas*_gravity/Math.pow((2*6378/_diameter),2);
    }

    public static List<String> getList(String[] _expr)
    {
        Object _obj = get(_expr);
        if(_obj == null) return null;
        if(_obj instanceof List) return ((List)_obj);
        return null;
    }

    public static synchronized List<String> getList(String[] _base, String _l)
    {
        String[] _k = new String[_base.length+1];
        System.arraycopy(_base,0,_k,0,_base.length);
        _k[_base.length] = _l;
        return getList(_k);
    }

    public static List<String> getKeys(String[] _expr)
    {
        Object _obj = get(_expr);
        if(_obj == null) return null;
        if(_obj instanceof Map) return (List) Arrays.asList(((Map)_obj).keySet().toArray());
        return null;
    }

    public static synchronized List<String> getKeys(String[] _base, String _l)
    {
        String[] _k = new String[_base.length+1];
        System.arraycopy(_base,0,_k,0,_base.length);
        _k[_base.length] = _l;
        return getKeys(_k);
    }

    public static String getString(String[] _expr)
    {
        Object _obj = get(_expr);
        if(_obj == null) return null;
        if(_obj instanceof String) return ((String)_obj);
        return _obj.toString();
    }

    public static String getString(String[] _expr, int _index)
    {
        Object _obj = getList(_expr).get(_index);
        if(_obj == null) return null;
        if(_obj instanceof String) return ((String)_obj);
        return _obj.toString();
    }

    public static synchronized String getString(String[] _base, String _l)
    {
        String[] _k = new String[_base.length+1];
        System.arraycopy(_base,0,_k,0,_base.length);
        _k[_base.length] = _l;
        return getString(_k);
    }

    public static Float getFloat(String[] _expr)
    {
        Object _obj = get(_expr);
        if(_obj == null) return null;
        if(_obj instanceof Double) return ((Double)_obj).floatValue();
        if(_obj instanceof Float) return ((Float)_obj).floatValue();
        if(_obj instanceof Long) return ((Long)_obj).floatValue();
        if(_obj instanceof Integer) return ((Integer)_obj).floatValue();
        return Float.parseFloat(_obj.toString());
    }

    public static double calcPlanetPeriodYr(double orbitAU, double starMass)
    {
        return Math.sqrt(Math.pow(orbitAU,3)/starMass);
    }

    public static synchronized Float getFloat(String[] _base, String _l)
    {
        String[] _k = new String[_base.length+1];
        System.arraycopy(_base,0,_k,0,_base.length);
        _k[_base.length] = _l;
        return getFloat(_k);
    }

    public static Long getLong(String[] _expr)
    {
        Object _obj = get(_expr);
        if(_obj == null) return null;
        if(_obj instanceof Double) return ((Double)_obj).longValue();
        if(_obj instanceof Float) return ((Float)_obj).longValue();
        if(_obj instanceof Long) return ((Long)_obj).longValue();
        if(_obj instanceof Integer) return ((Integer)_obj).longValue();
        return Long.parseLong(_obj.toString());
    }

    public static synchronized Long getLong(String[] _base, String _l)
    {
        String[] _k = new String[_base.length+1];
        System.arraycopy(_base,0,_k,0,_base.length);
        _k[_base.length] = _l;
        return getLong(_k);
    }

    public static Integer getInt(String[] _expr)
    {
        Object _obj = get(_expr);
        if(_obj == null) return null;
        if(_obj instanceof Double) return ((Double)_obj).intValue();
        if(_obj instanceof Float) return ((Float)_obj).intValue();
        if(_obj instanceof Long) return ((Long)_obj).intValue();
        if(_obj instanceof Integer) return ((Integer)_obj).intValue();
        return Integer.parseInt(_obj.toString());
    }

    public static synchronized Integer getInt(String[] _base, String _l)
    {
        String[] _k = new String[_base.length+1];
        System.arraycopy(_base,0,_k,0,_base.length);
        _k[_base.length] = _l;
        return getInt(_k);
    }

    public static String[] merge(String[] _base, String... _l)
    {
        String[] _k = new String[_base.length+_l.length];
        System.arraycopy(_base,0,_k,0,_base.length);
        System.arraycopy(_l,0,_k,_base.length,_l.length);
        return _k;
    }

    public static String[] merge(String... _l)
    {
        return _l;
    }

    public static synchronized Object get(String[] _expr)
    {
        if(_data == null)
        {
            _data = loadContext(new InputStreamReader(ClasspathResourceLoader.of("starsys.hson").getInputStream()));
        }
        Object _obj = _data;
        for(String _k : _expr)
        {
            _obj = ((Map<String,Object>) _obj).get(_k);
        }
        return _obj;
    }

    public static synchronized Object get(String[] _base, String _l)
    {
        String[] _k = new String[_base.length+1];
        System.arraycopy(_base,0,_k,0,_base.length);
        _k[_base.length] = _l;
        return get(_k);
    }

    @SneakyThrows
    public static Map<String, Object> loadContext(Reader _source)
    {
        return LdataParser.loadFrom(_source);
    }

    public static String lookupTabled(ArcRand _rng, String[] _base, String _lookup)
    {
        return lookupTabled(0., _rng, _base, _lookup);
    }

    public static String lookupTabled(double _bias, ArcRand _rng, String[] _base, String _lookup)
    {
        double _v = 0.;
        if(_bias>0.)
        {
            _v = _bias + _rng.next(1.-_bias);
        }
        else
        {
            _v = _rng.next(1.+_bias) - _bias;
        }
        return lookupTabled(_v, _base, _lookup);
    }

    public static String lookupTabled(double _rng, String[] _base, String _lookup)
    {
        if(_rng > 1.) _rng = (1./_rng);
        List<String> _list = new Vector<>();
        for(String _k : getKeys(_base, _lookup))
        {
            int _c = getInt(merge(_base, _lookup, _k));
            for(int _i=_c; _i>0; _i--)
            {
                _list.add(_k);
            }
        }
        return _list.get((int) (_list.size()*_rng));
    }

    public static double calcSimpleTemp(SolarContext _sol, double _orbit,double _planetEccent,double _rotationPeriod)
    {
        double _rellum = 0;
        if(_sol.isMulti())
        {
            for(int _i=0 ; _i<_sol.getComponents().size(); _i++)
            {
                _rellum += Math.sqrt(_sol.getComponents().get(_i).getLuminosity())/((_orbit-_sol.getComponentOffsets().get(_i))*(_planetEccent));
            }
        }
        else
        {
            _rellum = Math.sqrt(_sol.getLuminosity())/(_orbit*(_planetEccent));
        }
        return (Math.sqrt(_rellum)*340*Math.pow(_rotationPeriod, .2))-273.15;
    }
    
    
    
    
}
