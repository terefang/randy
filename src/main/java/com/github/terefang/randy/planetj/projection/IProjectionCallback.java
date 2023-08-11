package com.github.terefang.randy.planetj.projection;

public interface IProjectionCallback <T>
{
	public void projectCallback(T _context, int _i, int _j, double _x, double _y, double _z, int _depth, boolean _valid);
}
