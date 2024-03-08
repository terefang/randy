package com.github.terefang.randy.starsys.model;

import com.github.terefang.randy.rng.impl.ArcRand;
import com.github.terefang.randy.starsys.StarSysUtil;
import lombok.Data;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Vector;

@Data
public class SystemContext
{
	long id;
	int seed;
	boolean gaia;

	SolarContext context;
	List<PlanetContext> planets = new Vector<>();
	private double _surface_distance_au;
	private double _base_radius;
	private ArcRand _rng;
	private double _base_step;

	public static SystemContext from(long id, int seed)
	{
		SystemContext _ctx = new SystemContext();
		_ctx.id = id;
		_ctx.seed = seed;
		_ctx.init();
		return _ctx;
	}

	public static SystemContext from(long id, int seed, SolarContext _star, boolean gaia)
	{
		SystemContext _ctx = new SystemContext();
		_ctx.id = id;
		_ctx.seed = seed;
		_ctx.context = _star;
		_ctx.gaia = gaia;
		_ctx.init();
		return _ctx;
	}

	public void init(double _mass)
	{
		this.context = SolarContext.fromMass(_mass, true);
		this.init();
	}

	public void init_from_context(int _seed)
	{
		if(this.context==null)
		{
			this.context = SolarContext.tabledMK(_seed);
			this.context.randomizeFromMk(_seed);
		}

		this._surface_distance_au = this.context.getSurfaceDistance()*StarSysUtil.SOL_TO_AU;

		this._base_radius=_surface_distance_au*(1+((int)_rng.next(7900)+10)/1000f); // (1+int(rand(7900)+10)/1000)

		if (_base_radius<_surface_distance_au) _base_radius=_surface_distance_au*1.1;

		this._base_step=((int)_rng.next(70)+10)/200f;


	}

	public void init_planets(int _seed)
	{
		double _lastAU = this._surface_distance_au;
		try
		{
			int _num_rad = StarSysUtil.getInt(StarSysUtil.merge("stellar_type", this.context.getSpectra(), this.context.getRsize(),"orbitals"));
			float _planet_chance = StarSysUtil.getFloat(StarSysUtil.merge("stellar_type",this.context.getSpectra(),this.context.getRsize(),"planet_chance"));
			for(int _orbital = 0; _orbital <_num_rad; _orbital++)
			{
				double _orbit = _base_radius+(_base_step*Math.pow(1.8,_orbital));

				if(_planet_chance < _rng.next(111)) continue;

				if (_orbit > this.context.getOuterPlanetaryLimit()) break;

				PlanetContext _planet = PlanetContext.tabled(_seed^_orbital, this.context, _orbit, _orbital, this.gaia);
				_planet.randomizeFrom(_seed, _orbital%2==0);

				this.planets.add(_planet);
				//_planet.outputPlanetInformation(_afh);

				_planet.setMoonLimitAU((_planet.getOrbit()-_lastAU)/4.);
				if((_rng.next(100) > 15) && !"A".equalsIgnoreCase(_planet.getType()))
				{
					_planet.moons = MoonContext.generate((_seed<<8)|_orbital, _planet);
					//for(MoonContext _moon : _planet.moons)
					//{
					//	_moon.outputInformation(_afh);
					//}
				}
				_lastAU = _planet.getOrbit();
			}
		}
		catch (Exception _xe)
		{
			_xe.printStackTrace(System.out);
		}
		//_fh.println("\n\n\n\n\n");
		//_fh.flush();
	}
	public void init()
	{
		int _seed = (int) (this.seed ^ ((this.id>>>32) & 0x7fffffff) ^ (this.id & 0x7fffffff));
		this._rng = ArcRand.from(_seed-1);

		init_from_context(_seed);

		if("!".equalsIgnoreCase(this.context.getSpectra())) return;

		init_planets(_seed);
	}

	@SneakyThrows
	public void outputInformation(Appendable _afh)
	{
		this.getContext().outputStarInformation(_afh);
		for(PlanetContext _p : this.getPlanets())
		{
			_p.outputPlanetInformation(_afh);
			if(_p.moons != null)
			for(MoonContext _m : _p.moons)
			{
				_m.outputInformation(_afh);
			}
		}
		_afh.append('\n');
		_afh.append('\n');
		_afh.append('\n');
		_afh.append('\n');
		_afh.append('\n');
		_afh.append('\n');
		_afh.append('\n');
	}
}
