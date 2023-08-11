package com.github.terefang.randy.planetj;

import com.github.terefang.randy.RandyUtil;
import com.github.terefang.randy.fractal.IFractal;
import com.github.terefang.randy.fractal.MultiFractal;
import com.github.terefang.randy.fractal.RidgedMultiFractal;
import com.github.terefang.randy.fractal.SamplerFractal;
import com.github.terefang.randy.noise.NoiseUtil;
import com.github.terefang.randy.planetj.projection.IProjectionCallback;
import com.github.terefang.randy.planetj.projection.PlanetJProjectionContext;

public class PlanetHelper
{
	public static IProjectionCallback<PlanetJProjectionContext> createFractalOverlay()
	{
		return new IProjectionCallback<PlanetJProjectionContext>()
		{
			IFractal _fractal;

			@Override
			public void projectCallback(PlanetJProjectionContext _context, int _i, int _j, double _x, double _y, double _z, int _depth, boolean _valid)
			{
				if(_fractal==null)
				{
					_fractal = new SamplerFractal();
					_fractal.setFrequency(NoiseUtil.BASE_FREQUENCY);
					_fractal.setOctaves(8);
					_fractal.setH(NoiseUtil.BASE_H);
					_fractal.setGain(NoiseUtil.BASE_GAIN);
					_fractal.setLacunarity(NoiseUtil.BASE_LACUNARITY);
					_fractal.setVseed(true);
					//_fractal.setNoise(RandyUtil.perlinNoise(NoiseUtil.BASE_SEED1, NoiseUtil.HERMITE));
					_fractal.setNoise(RandyUtil.honeyNoise(NoiseUtil.BASE_SEED1, NoiseUtil.HERMITE));
				}

				if(_valid)
				{
					//double _value = (.5-_fractal.fractal3n( _x, _y, _z))/64.;
					double _value = _fractal.fractal3( _x, _y, _z)/64.;
					if(false)
					{
						_value = Math.abs(_value);
						if(_context.getMain().heights[_i][_j]>0.)
						{
							_context.getMain().heights[_i][_j] += _value;
						}
						else
						{
							_context.getMain().heights[_i][_j] -= _value;
						}
					}
					else
					{
						_context.getMain().heights[_i][_j] += _value;
					}
				}
			}
		};
	}
	public static double planetaryTemperature(double _x, double _y, double _z, int _lvl, double _alt, double _tAdj)
	{
		double temp;

		/* calculate temperature based on altitude and latitude */
		/* scale: -0.1 to 0.1 corresponds to -30 to +30 degrees Celsius */
		double sun = Math.sqrt(1.0-_y*_y); /* approximate amount of sunlight at
			     					latitude ranged from 0.1 to 1.1 */
		if (_alt < 0)
		{
			temp = (sun / 8.0) - (_alt * 0.3); /* deep water colder */
		}
		else {
			temp = (sun / 8.0) - (_alt * 1.2); /* high altitudes colder */
		}

		temp += _tAdj;

		return temp;
	}

	public static double planetaryTemperature(double _latitude, double _alt, double _tAdj)
	{
		return planetaryTemperature(0., _latitude/90./*degrees*/, 0., 0, _alt/65535./*meter*/, _tAdj);
	}


	public static double calcTempAdjustmentCell(double x, double y, double z)
	{
		return calcTempAdjustmentCell(x,y,z,0x13371337,56., .001, -.0);
	}

	public static double calcTempAdjustmentCell(double x, double y, double z, int _seed)
	{
		return calcTempAdjustmentCell(x,y,z,_seed,56., .001, -.0);
	}

	static IFractal _tempFractal;
	public static double calcTempAdjustmentCell(double x, double y, double z, int _seed, double _temperatureVariationFrequency, double _temperatureVariationFactor, double _temperatureBase)
	{
		if(_tempFractal==null)
		{
			_tempFractal = new MultiFractal();
			_tempFractal.setH(NoiseUtil.BASE_H);
			_tempFractal.setOctaves(4);
			_tempFractal.setVseed(true);
			_tempFractal.setNoise(RandyUtil.cellularNoise(_seed, NoiseUtil.NATURAL,NoiseUtil.CELL_VALUE));
			_tempFractal.setFrequency(NoiseUtil.BASE_FREQUENCY);
			_tempFractal.setLacunarity(NoiseUtil.BASE_LACUNARITY);
			_tempFractal.setGain(NoiseUtil.BASE_GAIN);
		}

		double _tAdj = _tempFractal.fractal3(x*_temperatureVariationFrequency,z*_temperatureVariationFrequency,y*_temperatureVariationFrequency);

		_tAdj = (_temperatureVariationFactor * _tAdj) + _temperatureBase;

		return _tAdj;
	}

	public static double planetaryRain(double _x, double _y, double _z, int _lvl, double _alt, double _temp)
	{
		double _rAdj = calcRainAdjustmentCell(_x,_y,_z);

		return planetaryRain(_x, _y, _z, _lvl, _alt, _temp, _rAdj);
	}

	public static double planetaryRain(double _x, double _y, double _z, int _lvl, double _alt, double _temp, int _seed)
	{
		double _rAdj = calcRainAdjustmentCell(_x,_y,_z, _seed);

		return planetaryRain(_x, _y, _z, _lvl, _alt, _temp, _rAdj);
	}

	public static double planetaryRain(double _x, double _y, double _z, int _lvl, double _alt, double _temp, int _seed, double _rainfallVariationFrequency, double _rainShadow, double _rainfallBase, double _rainfallVariationFactor)
	{
		double _rAdj = calcRainAdjustmentCell(_x,_y,_z, _seed, _rainfallVariationFrequency, _rainShadow, _rainfallBase, _rainfallVariationFactor);

		return planetaryRain(_x, _y, _z, _lvl, _alt, _temp, _rAdj);
	}

	public static double planetaryRain(double _x, double _y, double _z, int _lvl, double _alt, double _temp, double _rAdj)
	{
		/* calculate rainfall based on temperature and latitude */
  		/* rainfall approximately proportional to temperature but reduced
     		near horse latitudes (+/- 30 degrees, y=0.5) and reduced for
     		rain shadow */

		double y2 = Math.abs(_y)-0.5;
		double rain = _temp*0.65 + 0.1 - 0.011/(y2*y2+0.1);

		rain += _rAdj;

		if (rain<0.0) rain = 0.0;

		return rain;
	}

	public static double calcRainAdjustmentCell(double x, double y, double z)
	{
		return calcRainAdjustmentCell(x,y,z,0x13371337, 123.456, 0, -.0, .001);
	}

	public static double calcRainAdjustmentCell(double x, double y, double z, int _seed)
	{
		return calcRainAdjustmentCell(x,y,z,_seed, 123.456, 0, -.0, .001);
	}

	static IFractal _rainFractal;

	public static double calcRainAdjustmentCell(double x, double y, double z, int _seed, double _rainfallVariationFrequency, double _rainShadow, double _rainfallBase, double _rainfallVariationFactor)
	{
		if(_rainFractal==null)
		{
			_rainFractal = new MultiFractal();
			_rainFractal.setH(NoiseUtil.BASE_H);
			_rainFractal.setOctaves(4);
			_rainFractal.setVseed(true);
			_rainFractal.setNoise(RandyUtil.cellularNoise(_seed, NoiseUtil.NATURAL,NoiseUtil.DISTANCE_2));
			_rainFractal.setFrequency(NoiseUtil.BASE_FREQUENCY);
			_rainFractal.setLacunarity(NoiseUtil.BASE_LACUNARITY);
			_rainFractal.setGain(NoiseUtil.BASE_GAIN);
		}

		double _rAdj = _rainFractal.fractal3(x*_rainfallVariationFrequency,z*_rainfallVariationFrequency,y*_rainfallVariationFrequency);
		_rAdj = (0.03*_rainShadow) + _rainfallBase + (_rainfallVariationFactor * _rAdj);
		return _rAdj;
	}

	public static char planetaryBiome(double _x, double _y, double _z, double _alt, double _temp, double _rain)
	{
		char _biome = '*';

		/* make biome colours */
		int tt = Math.min(44,Math.max(0,(int)(_rain*300.0-9)));
		int rr = Math.min(44,Math.max(0,(int)(_temp*300.0+10)));

		_biome = biomes[tt].charAt(rr);

		if ((_alt < 0.0) && (_biome != 'I')) {
			_biome = '*';
		}

		return  _biome;
	}

	/* Whittaker diagram */
	public static final String[] biomes = {
			"IIITTTTTGGGGGGGGDDDDDDDDDDDDDDDDDDDDDDDDDDDDD",
			"IIITTTTTGGGGGGGGDDDDGGDSDDSDDDDDDDDDDDDDDDDDD",
			"IITTTTTTTTTBGGGGGGGGGGGSSSSSSDDDDDDDDDDDDDDDD",
			"IITTTTTTTTBBBBBBGGGGGGGSSSSSSSSSWWWWWWWDDDDDD",
			"IITTTTTTTTBBBBBBGGGGGGGSSSSSSSSSSWWWWWWWWWWDD",
			"IIITTTTTTTBBBBBBFGGGGGGSSSSSSSSSSSWWWWWWWWWWW",
			"IIIITTTTTTBBBBBBFFGGGGGSSSSSSSSSSSWWWWWWWWWWW",
			"IIIIITTTTTBBBBBBFFFFGGGSSSSSSSSSSSWWWWWWWWWWW",
			"IIIIITTTTTBBBBBBBFFFFGGGSSSSSSSSSSSWWWWWWWWWW",
			"IIIIIITTTTBBBBBBBFFFFFFGGGSSSSSSSSWWWWWWWWWWW",
			"IIIIIIITTTBBBBBBBFFFFFFFFGGGSSSSSSWWWWWWWWWWW",
			"IIIIIIIITTBBBBBBBFFFFFFFFFFGGSSSSSWWWWWWWWWWW",
			"IIIIIIIIITBBBBBBBFFFFFFFFFFFFFSSSSWWWWWWWWWWW",
			"IIIIIIIIIITBBBBBBFFFFFFFFFFFFFFFSSEEEWWWWWWWW",
			"IIIIIIIIIITBBBBBBFFFFFFFFFFFFFFFFFFEEEEEEWWWW",
			"IIIIIIIIIIIBBBBBBFFFFFFFFFFFFFFFFFFEEEEEEEEWW",
			"IIIIIIIIIIIBBBBBBRFFFFFFFFFFFFFFFFFEEEEEEEEEE",
			"IIIIIIIIIIIIBBBBBBRFFFFFFFFFFFFFFFFEEEEEEEEEE",
			"IIIIIIIIIIIIIBBBBBRRRFFFFFFFFFFFFFFEEEEEEEEEE",
			"IIIIIIIIIIIIIIIBBBRRRRRFFFFFFFFFFFFEEEEEEEEEE",
			"IIIIIIIIIIIIIIIIIBRRRRRRRFFFFFFFFFFEEEEEEEEEE",
			"IIIIIIIIIIIIIIIIIRRRRRRRRRRFFFFFFFFEEEEEEEEEE",
			"IIIIIIIIIIIIIIIIIIRRRRRRRRRRRRFFFFFEEEEEEEEEE",
			"IIIIIIIIIIIIIIIIIIIRRRRRRRRRRRRRFRREEEEEEEEEE",
			"IIIIIIIIIIIIIIIIIIIIIRRRRRRRRRRRRRRRREEEEEEEE",
			"IIIIIIIIIIIIIIIIIIIIIIIRRRRRRRRRRRRRROOEEEEEE",
			"IIIIIIIIIIIIIIIIIIIIIIIIRRRRRRRRRRRROOOOOEEEE",
			"IIIIIIIIIIIIIIIIIIIIIIIIIIRRRRRRRRRROOOOOOEEE",
			"IIIIIIIIIIIIIIIIIIIIIIIIIIIRRRRRRRRROOOOOOOEE",
			"IIIIIIIIIIIIIIIIIIIIIIIIIIIIRRRRRRRROOOOOOOEE",
			"IIIIIIIIIIIIIIIIIIIIIIIIIIIIIRRRRRRROOOOOOOOE",
			"IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIRRRRROOOOOOOOOO",
			"IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIRROOOOOOOOOOO",
			"IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIROOOOOOOOOOO",
			"IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIROOOOOOOOOOO",
			"IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIOOOOOOOOOOO",
			"IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIOOOOOOOOOO",
			"IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIOOOOOOOOO",
			"IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIOOOOOOOOO",
			"IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIOOOOOOOO",
			"IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIOOOOOOOO",
			"IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIOOOOOOOO",
			"IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIOOOOOOOO",
			"IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIOOOOOOO",
			"IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIOOOOOOO"
	};
}
