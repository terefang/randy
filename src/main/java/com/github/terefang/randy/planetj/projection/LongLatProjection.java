package com.github.terefang.randy.planetj.projection;

public class LongLatProjection implements IProjection<PlanetJProjectionContext>
{
	public static LongLatProjection create() { return new LongLatProjection(); }

	@Override
	public void projectCall(PlanetJProjectionContext _context, int _i, int _j, IProjectionCallback<PlanetJProjectionContext> _cb)
	{
		int sDepth = 3*((int)(_context.main.log_2(_context.main.Height)))+3;
		sDepth = (sDepth<_context.main.Depth) ? sDepth : _context.main.Depth;

		double _lat = (Math.PI*((double)_j)/((double)_context.main.Height-1))-_context.main.baseLatitude;
		double _lon = Math.PI+(2*Math.PI*((double)(_i))/((double)_context.main.Width-1))+_context.main.baseLongitude;

		double _y = -Math.cos(_lat);
		double _ysin = Math.sin(_lat);
		double _x = Math.sin(_lon)*_ysin;
		double _z = Math.cos(_lon)*_ysin;

		_cb.projectCallback(_context, _i, _j, _x, _y, _z, sDepth, true);
	}
}
