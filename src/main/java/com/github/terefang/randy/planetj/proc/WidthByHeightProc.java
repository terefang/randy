package com.github.terefang.randy.planetj.proc;

import com.github.terefang.randy.planetj.PlanetJ;
import com.github.terefang.randy.planetj.projection.IProjection;
import com.github.terefang.randy.planetj.projection.IProjectionCallback;
import com.github.terefang.randy.planetj.projection.PlanetJProjectionContext;
import com.github.terefang.randy.utils.LogSink;

public class WidthByHeightProc implements Runnable
{
	private final LogSink _log;

	public static WidthByHeightProc create(PlanetJ planetJ, LogSink _log, IProjection<PlanetJProjectionContext> _p, IProjectionCallback<PlanetJProjectionContext> _cb, int j, boolean k)
	{
		return new WidthByHeightProc(planetJ, _log, _p, _cb, k, j);
	}


	PlanetJProjectionContext _context = null;
	IProjection<PlanetJProjectionContext> _projection;
	IProjectionCallback<PlanetJProjectionContext> _cb;
	int _j;

	public WidthByHeightProc(PlanetJ _that, LogSink _log, IProjection<PlanetJProjectionContext> _p, IProjectionCallback<PlanetJProjectionContext> _cb, boolean m_k, int m_j)
	{
		_context = PlanetJProjectionContext.create(_that, m_k);
		this._j=m_j;
		this._projection = _p;
		this._cb = _cb;
		this._log = _log;
	}

	@Override
	public void run() {
		for (int _i = 0; _i < _context.getMain().Width; _i++)
		{
			this._projection.projectCall(_context, _i, _j, this._cb);
		}
		_context.getMain().tickH(this._j, this._log);
	}

}