package com.github.terefang.randy.starsys.model;

import com.github.terefang.randy.rng.impl.ArcRand;
import com.github.terefang.randy.starsys.StarSysUtil;
import lombok.Data;
import lombok.SneakyThrows;

import java.math.BigInteger;
import java.util.List;

@Data
public class PlanetContext
{
    int zone;
    int seed;
    int rseed;
    double orbit;
    int orbital;
    String type;
    String description;
    double planetDiameter;
    double planetDensity;
    double planetAxialTilt;
    double planetMass;
    SolarContext _sol;
    double gravity;
    double escapeVelocity;
    double angularVelocity;
    double rotationPeriod;
    String rotationText;
    double planetYear;
    double exoTemp;
    double planetTemp;
    double planetTempAvg;
    double planetTempMin;
    double planetTempMax;
    float planetEccent;
    double rmsVelocity;
    double molLimit;
    double gasInventory;
    String atmosphereType;
    double surfacePressure;
    double opticalDepth;
    double planetTempAdj;
    double albedo;
    double hydroFraction;
    double cloudFraction;
    double iceFraction;
    double safeJumpDistance;
    double moonLimitAU;

    List<MoonContext> moons;

    public static PlanetContext tabled(int _seed, SolarContext _sol, double _orbit, int _orbital, boolean _gaia)
    {
        PlanetContext _ctx = new PlanetContext();
        ArcRand _rng = ArcRand.from(_seed);
        _ctx._sol = _sol;
        _ctx.seed = _seed;
        _ctx.orbit = _orbit;
        _ctx.orbital = _orbital;
        _ctx.rseed = _seed + _orbital;
        _ctx.zone = StarSysUtil.calcOrbitalZone(_orbit, _sol);

        String _lookup = StarSysUtil.getList(StarSysUtil.merge("planet_table",(_gaia?"gaia-":"")+"zones")).get(_ctx.zone-1);
        while(_lookup.startsWith("*"))
        {
            _lookup = StarSysUtil.lookupTabled(_rng, StarSysUtil.merge("planet_table"), _lookup);
        }

        _ctx.type = _lookup;
        _ctx.description = StarSysUtil.getString(StarSysUtil.merge("planet_table",_lookup,"description"));

        return _ctx;
    }

    public void randomizeFrom(int _seed, boolean _gaia)
    {
        this.rseed = _seed;

        double _rot_mul = StarSysUtil.getFloat(StarSysUtil.merge("planet_table",this.type,"rotational_multiplier"));
        double _dia_base = StarSysUtil.getFloat(StarSysUtil.merge("planet_table",this.type,"diameter_base"));
        double _dia_var = StarSysUtil.getFloat(StarSysUtil.merge("planet_table",this.type,"diameter_variation"));
        double _dia_mul = StarSysUtil.getFloat(StarSysUtil.merge("planet_table",this.type,"diameter_multiplier"));
        double _dens_base = StarSysUtil.getFloat(StarSysUtil.merge("planet_table",this.type,"density_base"));
        double _dens_var = StarSysUtil.getFloat(StarSysUtil.merge("planet_table",this.type,"density_variation"));
        double _axial_tilt_max = StarSysUtil.getFloat(StarSysUtil.merge("planet_table",this.type,"max_axial_tilt"));

        ArcRand _rng = ArcRand.from(_seed + "/"+this.seed+ "/"+this.type);
        this.planetDiameter=_rng.nextVariant(_dia_base,_dia_var,0.001)*_dia_mul;
        this.planetDensity=_rng.nextVariant(_dens_base,_dens_var,0.001)*1000;
        this.planetAxialTilt=((int)(_rng.next(Math.sqrt(_axial_tilt_max))*_rng.next(Math.sqrt(_axial_tilt_max))*100))/100f;

        this.planetYear=StarSysUtil.calcPlanetPeriodYr(this.orbit,_sol.getMass());
        this.planetEccent=_rng.nextBounds(0,0.88888f,0.0001f)*_rng.nextBounds(0,0.88888f,0.0001f);
        //# SPECIAL CALCULATIONS FOR GAIA/HUMANOID PREFERENCE UNIPERVERSE
        //#                   (RACIST BUT EFFECTIVE)
        if (_gaia && ("E".equalsIgnoreCase(this.type) || "T".equalsIgnoreCase(this.type)))
        {
            this.planetDiameter=((_rng.next(4)+10)*1000);
            this.planetDensity=(_rng.next(3)+4)*1000;
            this.planetAxialTilt=(_rng.next(8)*800)/100;
        }
        //# END SPECIAL

        this.planetMass=((3.1415/6)*Math.pow(this.planetDiameter,3))*1e9*(this.planetDensity/5.97E24);

        this.gravity=StarSysUtil.calcSurfaceAccel(this.planetMass*5.9742e24,this.planetDiameter)/9.81;

        this.safeJumpDistance = this.planetDiameter*100;
        if(this.gravity>1)
        {
            this.safeJumpDistance*=Math.pow(this.gravity,.3);
        }

        this.escapeVelocity=StarSysUtil.calcEscapeVelocity(this.planetMass*5.9742e24,this.planetDiameter);
        this.angularVelocity=Math.sqrt(1.18E-19*this.planetMass*5.98E27/(_rot_mul*Math.pow((this.planetDiameter*5E4),2)));
        double _aux1=6.28/(3600*this.angularVelocity);
        double _aux2=(Math.exp(0.27*Math.log(10/(this.orbit*10))))/_sol.getMass();
        this.rotationPeriod=_aux1*_aux2;
        this.rotationText="";
        if (this.orbit<(0.5*_sol.getMass()))
        {
            this.rotationPeriod=(this.planetYear*365.25*24)/(_rng.next(6)+1);
            this.rotationText="Tidal Lock";
        }
        else if (this.rotationPeriod>(this.planetYear*365.25*24))
        {
            this.rotationPeriod=(this.planetYear*365.25*24)/(_rng.next(6)+1);
            this.rotationText="Tidal Lock";
        }
        else if ("H".equalsIgnoreCase(this.type)
                || "G".equalsIgnoreCase(this.type)
                || "L".equalsIgnoreCase(this.type))
        {
            this.rotationPeriod *= this.zone;
        }
        else if (this.rotationPeriod<10 && ("R".equalsIgnoreCase(this.type)
                || "T".equalsIgnoreCase(this.type)
                || "E".equalsIgnoreCase(this.type)))
        {
            this.rotationPeriod = this.zone*100*Math.pow(((2*3.1415)/(0.19*6.667e-11*this.planetDensity)),0.5)/(60*60*24);
        }


        this.exoTemp=StarSysUtil.calcExospericTemp(_sol,this.orbit);
        this.planetTemp=StarSysUtil.calcSimpleTemp(_sol,this.orbit,1.,1.);



        this.planetTempMax=StarSysUtil.calcSimpleTemp(_sol,this.orbit,1-this.planetEccent,this.rotationPeriod/24.);
        this.planetTempMin=StarSysUtil.calcSimpleTemp(_sol,this.orbit,1+this.planetEccent,24./this.rotationPeriod);
        this.planetTempAvg=(this.planetTemp+this.planetTempMin+this.planetTempMax)/3;

        this.rmsVelocity = StarSysUtil.calcRmsVelocity(28, _sol.getLuminosity(), this.orbit);
        this.molLimit = StarSysUtil.calcMoleculeLimit(this.planetMass*5.9742e24, this.planetDiameter, _sol.getLuminosity(), this.orbit);
        this.gasInventory = StarSysUtil.calcGasInventory(this.planetMass,this.escapeVelocity,this.rmsVelocity/1000, _sol.getMass(), this.zone, false);
        if ("H".equalsIgnoreCase(this.type)
                || "A".equalsIgnoreCase(this.type)
                || "G".equalsIgnoreCase(this.type)
                || "L".equalsIgnoreCase(this.type)
                || "R".equalsIgnoreCase(this.type))
        {
            this.gasInventory = 0;
        }
        else if(_gaia)
        {
            this.gasInventory = _rng.nextAbout(1000, 0.3f);
        }

        String _lookup = "";
        if(this.gasInventory > 1)
        {
            _lookup = "*"+this.type;
            while(_lookup.startsWith("*"))
            {
                _lookup = StarSysUtil.lookupTabled(_rng, StarSysUtil.merge("atmos_table"), _lookup);
            }
        }

        this.atmosphereType = _lookup;

        if("None".equalsIgnoreCase(_lookup))
        {
            this.gasInventory = 0;
            if ("T".equalsIgnoreCase(this.type)
                    || "E".equalsIgnoreCase(this.type))
            {
                this.type = "R";
            }
        }


        if(this.gasInventory > 0)
        {
            this.surfacePressure = StarSysUtil.calcSurfPressure(this.gasInventory, this.planetDiameter, this.gravity);
            this.opticalDepth = StarSysUtil.calcOpacity(this.molLimit, this.surfacePressure/1000f);
            double _change = 0;
            this.planetTempAdj = this.planetTempAvg;
            int _iter = 0;
            do
            {
                this.hydroFraction = StarSysUtil.calcHydroFraction(this.gasInventory, this.planetDiameter);
                // $cloud_fract=cloud_fraction($planet_temp+273.15,$mol_ret,$planet_diameter,$hydro_fract,rand_about(3.83e15,0.7));
                this.cloudFraction = StarSysUtil.calcCloudFraction(this.planetTempAdj+273.15, this.molLimit, this.planetDiameter, this.hydroFraction, 3.83e15);
                this.iceFraction = StarSysUtil.calcIceFraction(this.planetTempAdj+273.15, this.hydroFraction);
                this.albedo = StarSysUtil.calcAlbedo(this.hydroFraction, this.cloudFraction, this.iceFraction, this.surfacePressure);
                this.planetTempAdj = StarSysUtil.calcEffectiveTemperature(_sol.getLuminosity(), this.orbit, this.albedo)-273.15;
                double _last_temp = this.planetTempAdj;
                double _rise = StarSysUtil.calcGreenRise(this.opticalDepth, this.planetTempAdj+273.15, this.surfacePressure/1000);
                if(this.atmosphereType.indexOf("Carbon")>0)
                {
                    _rise = Math.abs(_rise)*1.5;
                }
                this.planetTempAdj+=_rise;
                _change = Math.abs(_last_temp-this.planetTempAdj);
                _iter++;
            }
            while((_change>0.5) && (_iter < 100000));
        }
    }

    @SneakyThrows
    public void outputPlanetInformation(Appendable _fh)
    {
        outputInformation(_fh);
        if(!"A".equalsIgnoreCase(this.getType()))
        {
            if (this.getPlanetYear()<2.5) {
                _fh.append(String.format("   Orbital Period: %d terran days\n" , (int)(this.getPlanetYear()*365)));
            } else {
                _fh.append(String.format("   Orbital Period: % 12.3f terran years\n" , this.getPlanetYear()));
            }
            _fh.append(String.format("     Eccentricity: % 12.8f u\n"           , this.getPlanetEccent()));
            _fh.append(String.format("         Diameter: % 12.3f km\n"          , this.getPlanetDiameter()));
            _fh.append(String.format("             Mass: % 12.3f earth\n"       , this.getPlanetMass()));
            _fh.append(String.format("  Rotation Period: % 12.3f hours %s\n", this.getRotationPeriod(), this.getRotationText()));
            _fh.append(String.format("       Axial Tilt: % 12.3f degrees\n"     , this.getPlanetAxialTilt()));
            _fh.append(String.format("          Gravity: % 12.3f g\n"           , this.getGravity()));
            _fh.append(String.format("          Density: % 12.3f kg/m3\n"       , this.getPlanetDensity()));
            _fh.append(String.format("\n  Escape Velocity: % 12.6f km/sec\n"    , this.getEscapeVelocity()));
            _fh.append(String.format("     RMS Velocity: % 12.6f m/sec\n"       , this.getRmsVelocity()));

            if(this.getGasInventory()>100)
            {
                _fh.append(String.format("    Mol Retention: % 12.6f mol\n"         , this.getMolLimit()));
                _fh.append(String.format("\nAtmospheric Index: % 12.5f\n"           , this.getGasInventory()));
                _fh.append(String.format("Primary Component: %s \n"                 , this.getAtmosphereType()));
                _fh.append(String.format("         Pressure: % 12.5f bar\n"         , this.getSurfacePressure()/1000));
                _fh.append(String.format("    Optical Depth: % 12.5f\n"             , this.getOpticalDepth()));
                _fh.append(String.format("      Hydrography: %d %%\n"          , (int)(this.getHydroFraction()*100)));
                _fh.append(String.format("   Cloud Coverage: %d %%\n"          , (int)(this.getCloudFraction()*100)));
                _fh.append(String.format("     Ice Coverage: %d %%\n"          , (int)(this.getIceFraction()*100)));
                _fh.append(String.format("           Albedo: % 12.5f  \n"           , this.getAlbedo()));
                _fh.append(String.format(" det. Temperature: %+12.3f Celsius\n"     , this.getPlanetTempAdj()));
                _fh.append(String.format(" min. Temperature: %+12.3f C\n"     , this.getPlanetTempMin()));
                _fh.append(String.format(" avg. Temperature: %+12.3f C\n"     , this.getPlanetTempAvg()));
                _fh.append(String.format(" max. Temperature: %+12.3f C\n"     , this.getPlanetTempMax()));
            }
            else
            if(this.getGasInventory()>3)
            {
                _fh.append(String.format("    Mol Retention: % 12.6f mol\n"         , this.getMolLimit()));
                _fh.append(String.format("\nAtmospheric Index: % 12.5f\n"           , this.getGasInventory()));
                _fh.append(String.format("Primary Component: trace-gases\n"));
                _fh.append(String.format(" det. Temperature: %+12.3f Celsius\n", this.getPlanetTempAvg()));
                _fh.append(String.format(" avg. Temperature: %+12.3f C\n"     , this.getPlanetTempAvg()));
            }

            _fh.append(String.format(" exo. Temperature: %+12.3f Kelvin\n"     , this.getExoTemp()));
            _fh.append(String.format(" lum. Temperature: %+12.3f C\n"     , this.getPlanetTemp()));

            if(this.getSafeJumpDistance()>1000000)
            {
                _fh.append(String.format("\n  Safe Jump Limit: %.3f AU\n", this.getSafeJumpDistance()/149597870.700));
            }
            else
            {
                _fh.append(String.format("\n  Safe Jump Limit: %.3f km\n", this.getSafeJumpDistance()));
            }
        }

    }

    @SneakyThrows
    public void outputInformation(Appendable _fh) {
        _fh.append("----------------------------------------------------------\n");
        _fh.append(String.format("             Seed: 0x%X / 0x%X\n",     this.getSeed(), this.getRseed()));
        _fh.append(String.format("  Planetary Orbit: % 12.6f AU (%d)\n",     this.getOrbit(), this.getOrbital()+1));
        _fh.append(String.format("             Zone: %d\n"                  , this.getZone()));
        _fh.append(String.format("             Type: %s %s\n", this.getType(), this.getDescription()));

    }

    @SneakyThrows
    public void outputInformationShort(Appendable _fh)
    {
        _fh.append(String.format("\n%s %s @ %.3fAU Orbit=%d, Zonr=%d\n", this.getType(), this.getDescription(),     this.getOrbit(), this.getOrbital()+1, this.getZone()));
        if("T".equalsIgnoreCase(this.getType())
                || "R".equalsIgnoreCase(this.getType())
                || "E".equalsIgnoreCase(this.getType()))
        {
            _fh.append(String.format("+    d=%.3f km\n", this.getPlanetDiameter()));
            _fh.append(String.format("+    m=%.3f Me\n", this.getPlanetMass()));
            _fh.append(String.format("+    g=%.3f\n", this.getGravity()));
            _fh.append(String.format("+    t=%+.3f ºC\n", this.getPlanetTemp()));
        }

        if(this.moons != null)
        for(MoonContext _m : this.moons)
        {
            _m.outputInformationShort(_fh);
        }
    }
}
