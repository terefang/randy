package com.github.terefang.randy.kernel;

import com.github.terefang.randy.nfield.NoiseField;

/**
 * Created by fredo on 08.12.15.
 */
public interface IKernel
{
	public int getWidth();
	public int getHeight();
	public String getName();
	public double calculateSample(NoiseField nf, int x, int y);
}
