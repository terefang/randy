package com.github.terefang.randy.planetj.projection;

public interface IProjection<T>
{
	public void projectCall(T _context, int _i, int _j, IProjectionCallback<T> _cb);
}
