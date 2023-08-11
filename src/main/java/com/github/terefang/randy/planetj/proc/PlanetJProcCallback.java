package com.github.terefang.randy.planetj.proc;

import com.github.terefang.randy.planetj.projection.IProjectionCallback;
import com.github.terefang.randy.planetj.projection.PlanetJProjectionContext;

public class PlanetJProcCallback implements IProjectionCallback<PlanetJProjectionContext>
{

	public static PlanetJProcCallback create() { return new PlanetJProcCallback(); }

	@Override
	public void projectCallback(PlanetJProjectionContext _context, int _i, int _j, double _x, double _y, double _z, int _d, boolean _valid)
	{
		if(_valid)
		{
			_context.getMain().planet_main(_i, _j, _x, _y, _z, _d, _context.isAltitudeFirst());
		}
		else
		{
			_context.getMain().col[_i][_j] = _context.getMain().BACK;
			if (_context.getMain().doshade) _context.getMain().shades[_i][_j] = 255;
		}
	}

}
