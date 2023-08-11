package com.github.terefang.randy.planetj.proc;

import com.github.terefang.randy.planetj.PlanetJ;
import com.github.terefang.randy.planetj.projection.IProjection;
import com.github.terefang.randy.planetj.projection.IProjectionCallback;
import com.github.terefang.randy.planetj.projection.PlanetJProjectionContext;

public class WidthByHeightProc implements Runnable
{
	public static WidthByHeightProc create(PlanetJ planetJ, IProjection<PlanetJProjectionContext> _p, IProjectionCallback<PlanetJProjectionContext> _cb, int j, boolean k)
	{
		return new WidthByHeightProc(planetJ, _p, _cb, k, j);
	}


	PlanetJProjectionContext _context = null;
	IProjection<PlanetJProjectionContext> _projection;
	IProjectionCallback<PlanetJProjectionContext> _cb;
	int _j;

	public WidthByHeightProc(PlanetJ _that, IProjection<PlanetJProjectionContext> _p, IProjectionCallback<PlanetJProjectionContext> _cb, boolean m_k, int m_j)
	{
		_context = PlanetJProjectionContext.create(_that, m_k);
		this._j=m_j;
		this._projection = _p;
		this._cb = _cb;
	}

	@Override
	public void run() {
		for (int _i = 0; _i < _context.getMain().Width; _i++)
		{
			this._projection.projectCall(_context, _i, _j, this._cb);
		}
		_context.getMain().tickH(this._j);
	}

}