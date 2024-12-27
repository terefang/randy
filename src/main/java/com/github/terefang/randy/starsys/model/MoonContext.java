package com.github.terefang.randy.starsys.model;

import com.github.terefang.randy.rng.impl.ArcRand;
import com.github.terefang.randy.starsys.StarSysUtil;
import lombok.Data;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Vector;

@Data
public class MoonContext {
    int seed;
    double mass;
    double massRel;
    double diameter;
    double density;
    double orbit;
    int orbital;
    double gravity;
    double period;
    double rotation;
    PlanetContext _planet;

    public static MoonContext from(double _moon_mass) {
        MoonContext _ctx = new MoonContext();
        _ctx.mass = _moon_mass;
        return _ctx;
    }

    public static MoonContext from(int _seed, double _moon_mass) {
        MoonContext _ctx = new MoonContext();
        _ctx.mass = _moon_mass;
        _ctx.seed = _seed;
        return _ctx;
    }

    public static List<MoonContext> generate(int _seed, PlanetContext _planet) {
        double _moon_max = StarSysUtil.getFloat(StarSysUtil.merge("planet_table", _planet.type, "max_num_moons"));
        double _moon_mass_max = StarSysUtil.getFloat(StarSysUtil.merge("planet_table", _planet.type, "max_combined_mass_of_moons"));

        ArcRand _rng = ArcRand.from(_seed);

        double _moon_masses = _planet.getPlanetMass() * _rng.nextBounds(1, (float) _moon_mass_max, 0.0001f) / 100;

        int _x = 0;

        List<MoonContext> _list = new Vector<>();

        while ((_moon_masses > 0) && (_x <= _moon_max * 2)) {
            _x++;
            double _moon_mass;
            if ("H".equalsIgnoreCase(_planet.getType())
                    || "G".equalsIgnoreCase(_planet.getType())
                    || "L".equalsIgnoreCase(_planet.getType())) {
                _moon_mass = _moon_masses * _rng.nextBounds(-0.15f, 0.45f, 0.0001f);
                _moon_masses = (_moon_masses - Math.abs(_moon_mass)) / ((_x == _moon_max) ? 1 : (Math.abs(_moon_max - _x) / 2));
                //if (_rng.next(11) > 9) {
                //    _moon_mass *= _rng.nextBounds(2, (float) (_planet.getPlanetMass() * 10f), 0.000001f);
                //}
            } else {
                _moon_mass = _moon_masses * (-30 + _rng.next(90)) / 100;
            }

            _moon_masses -= Math.abs(_moon_mass);

            _moon_mass *= 5.98e+24;
            if((_moon_mass > 0) && (_rng.next(10)>6)) {
                _list.add(MoonContext.from(_seed, _moon_mass));
            }
        }

        double _base_radius = 2.1 + (_rng.next(3800) / 1000);
        double _roche = 2.* StarSysUtil.calcRocheLimit(1., _planet.getPlanetDensity(), 1000.);
        if(_base_radius < _roche)
        {
            _base_radius = _roche;
        }

        double _base_step = ((_rng.next(8) + 3) / 20);

        double _moon_radian_last = 0;
        double _moon_diameter_last = 0;
        //while (@moon_mass_list && ($moon_radian_last<15900000)) {
        _x = 0;
        List<MoonContext> _rem = new Vector<>();
        for (MoonContext _ctx : _list) {
            if (_moon_radian_last < 1) {
                _moon_radian_last = _roche * _planet.getPlanetDiameter();
            }
            double _moon_radian = (_base_radius + (_base_step * Math.pow(1.9, _x))) * _planet.getPlanetDiameter();
            if (_x == 0) {
                _ctx.randomize(_seed | (_x++), _moon_radian, _planet);
                _moon_radian = _planet.getPlanetDiameter() * (1.5246 * Math.pow((_planet.getPlanetDensity() / _ctx.getDensity()), 0.5));
            } else {
                _ctx.randomize(_seed | (_x++), _moon_radian, _planet);
                _moon_radian = (_ctx.getDiameter() + _moon_diameter_last + _moon_radian_last) * Math.pow(_rng.nextBounds(1.1f, 2f, 0.0001f), 2);
            }

            double _mol = StarSysUtil.calcMoleculeLimit(_ctx.getMass(), _ctx.getDiameter(), _planet.getExoTemp());
            if(_ctx.getDensity() < _mol)
            {
                _rem.add(_ctx);
            }

            _ctx.setOrbital(_x);

            if (_moon_radian < _moon_radian_last) {
                //System.err.println(String.format("moon_radian[%d] = %f - %f", _x , _moon_radian_last, _moon_radian));
                _moon_radian = _moon_radian_last * 1.25;
            }

            if ((_moon_radian_last > 0) && (_moon_radian < (_moon_radian_last + _ctx.getDiameter() + _moon_diameter_last))) {
                _moon_radian = _moon_radian_last + (_moon_radian / 4) + _ctx.getDiameter() + _moon_diameter_last;
            }

            double _rradian = StarSysUtil.calcRocheLimit(_ctx.getDiameter(),_ctx.getMassRel());
            if(_rradian>_moon_radian)
            {
                _moon_radian=_rradian+(.5*_moon_radian);
            }

            // wobble limit
            double _wobbleLimit = _planet.getPlanetDiameter() /(1+(_planet.getPlanetMass()/_ctx.getMass()));
            while(_wobbleLimit>_moon_radian)
            {
                _moon_radian *= 1.1;
            }

            _ctx.setOrbit(_moon_radian);
            _ctx.recalculatePeriodTime(null);

            _moon_diameter_last = _ctx.getDiameter();
            _moon_radian_last = _moon_radian;
            if (_moon_radian_last > 15900000) break;
            if (_moon_radian_last > _planet.getMoonLimitAU()*StarSysUtil.AU_TO_KM) break;
        }

        if (_list.size() > _x) {
            _list = _list.subList(0, _x);
        }
        _list.removeAll(_rem);
        return _list;
    }

    public void randomize(int _seed, double _orbit, PlanetContext _planet) {
        this._planet = _planet;
        this.seed = _seed;
        this.orbit = _orbit;
        ArcRand _rng = ArcRand.from(_seed);

        this.massRel = (this.mass / 7.36e+22);
        this.density = _rng.nextBounds(1600, 5500, 0.001f);

        this.diameter = StarSysUtil.calcMoonDiameter(this.getMassRel(), this.density);
        //this.density = StarSysUtil.calcVolumeDensity(this.mass, this.diameter);
        // this.diameter = StarSysUtil.calcKothariDiameter(this.getMass(), _rng.nextBounds(0.65f, 1.3f, 0.001f));

        this.gravity = (this.diameter * 1.2 / 12500) * (this.density / 5220);

        recalculatePeriodTime(_rng);
    }

    public void recalculatePeriodTime(ArcRand _rng) {
        this.period = 27.32 * Math.sqrt(Math.pow((this.orbit / 384000), 3) / this._planet.getPlanetMass());
        this.rotation = Math.pow(((2 * Math.PI) / (0.19 * 6.667e-11 * this.density)), 0.5) / (60 * 60 * 24);
        if (_rng != null) {
            this.rotation *= Math.pow(_rng.nextBounds(9, (float) Math.pow((this.period * 40 / this.rotation), 0.5), 0.001f), 2);
        } else {
            this.rotation *= Math.pow(((9 + Math.pow((this.period * 40 / this.rotation), 0.5)) / 2), 2);
        }
    }

    @SneakyThrows
    public void outputInformation(Appendable _fh) {
        _fh.append("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - \n");
        _fh.append(String.format("             Seed: 0x%X\n", this.getSeed()));
        _fh.append(String.format("       Moon Orbit: % 12.3f km, %.3f AU (%d)\n", this.getOrbit(), this.getOrbit()/StarSysUtil.AU_TO_KM, this.getOrbital()));
        _fh.append(String.format("   Orbital Period: % 12.3f terran days\n", this.getPeriod()));
        _fh.append(String.format("    Moon Diameter: % 12.3f km\n", this.getDiameter()));
        if (this.getMassRel() < 40) {
            _fh.append(String.format("        Moon Mass: % 12.8f moon\n", this.getMassRel()));
        } else {
            _fh.append(String.format("        Moon Mass: % 12.8f earth\n", this.getMassRel() / 81.30059));
        }
        _fh.append(String.format("     Moon Density: % 12.3f kg/m3\n", this.getDensity()));
        if (this.rotation > (this.period * 24)) {
            _fh.append("  Rotation Period:  Tidal Lock (Same Facing) \n");
        } else if (this.rotation > 55) {
            _fh.append(String.format("  Rotation Period: % 12.3f terran days \n", this.getRotation() / 24f));
        } else {
            _fh.append(String.format("  Rotation Period: % 12.3f hours \n", this.getRotation()));
        }
        _fh.append(String.format("          Gravity: % 12.8f g\n", this.getGravity()));
    }

    @SneakyThrows
	public void outputInformationShort(Appendable _fh) {
        _fh.append(String.format("%% %.3f km, %.3f AU (%d)\n", this.getOrbit(), this.getOrbit()/StarSysUtil.AU_TO_KM, this.getOrbital()));
        _fh.append(String.format("+    p=%.3f terran days\n", this.getPeriod()));
        _fh.append(String.format("+    d=%.3f km\n", this.getDiameter()));
	}
}

