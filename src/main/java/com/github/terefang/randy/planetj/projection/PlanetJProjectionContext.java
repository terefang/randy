package com.github.terefang.randy.planetj.projection;


import com.github.terefang.randy.planetj.PlanetJ;
import lombok.Data;

@Data
public class PlanetJProjectionContext
{
	PlanetJ main;
	boolean altitudeFirst;

	public static PlanetJProjectionContext create(PlanetJ _that, boolean _k)
	{
		PlanetJProjectionContext _c = new PlanetJProjectionContext();
		_c.main = _that;
		_c.altitudeFirst = _k;
		return  _c;
	}
}
