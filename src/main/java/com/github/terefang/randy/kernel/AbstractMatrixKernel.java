package com.github.terefang.randy.kernel;


import com.github.terefang.randy.nfield.NoiseField;

/**
 * Created by fredo on 08.12.15.
 */
public class AbstractMatrixKernel extends AbstractKernel
{
	double[] kernel = null;

	public double[] getKernel()
	{
		return kernel;
	}

	public void setKernel(double[] kernel)
	{
		this.kernel = kernel;
	}

	public double calculateSample(NoiseField nf, int x, int y)
	{
		int px = x - (this.getWidth()/2);
		if(px<0) px=0;
		int py = y - (this.getHeight()/2);
		if(py<0) py=0;
		double sample = 0;
		for(int iy=0; iy<this.getHeight(); iy++)
		{
			for(int ix=0; ix<this.getWidth(); ix++)
			{
				sample += kernel[iy*this.getWidth()+ix]*nf.getPoint(px+ix, py+iy);
			}
		}
		return sample;
	}
}
