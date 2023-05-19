package com.github.terefang.randy.kernel;

import com.github.terefang.randy.nfield.NoiseField;

/**
 * Created by fredo on 08.12.15.
 */
public class GaussianBlurKernel extends AbstractMatrixKernel implements IKernel
{
	double sigma2 = 0.0;
	double scale = 0.0;
	double base1 = 0.0;
	double base2 = 0.0;
	double normal = 0.0;

	public double getSigma2()
	{
		return sigma2;
	}

	public void setSigma2(double sigma2)
	{
		this.sigma2 = sigma2;
	}

	public static GaussianBlurKernel getKernel(double scale, double sigma2)
	{
		return getKernel((int)(3.0*Math.sqrt(sigma2)), 0, 0, scale, sigma2);
	}

	public static GaussianBlurKernel getKernel(double sigma2)
	{
		return getKernel((int)(3.0*Math.sqrt(sigma2)), 0, 0, 1.0, sigma2);
	}

	public static GaussianBlurKernel getKernel(int k, double sigma2)
	{
		return getKernel(k, 0, 0, 1.0, sigma2);
	}

	public static GaussianBlurKernel getKernel(int k, double scale, double sigma2)
	{
		return getKernel(k, 0, 0, scale, sigma2);
	}

	public static GaussianBlurKernel getKernel(int k, int offs, double sigma2)
	{
		return getKernel(k, offs, offs, 1.0, sigma2);
	}

	public static GaussianBlurKernel getKernel(int k, int offs, double scale, double sigma2)
	{
		return getKernel(k, offs, offs, scale, sigma2);
	}

	public static GaussianBlurKernel getKernel(int k, int offx, int offy, double scale, double sigma2)
	{
		GaussianBlurKernel gbk = new GaussianBlurKernel();
		gbk.sigma2= sigma2;
		gbk.scale= scale;
		gbk.base1 = 1.0/(2.0*Math.PI*sigma2);
		gbk.base2 = 1.0/(2.0*sigma2);
		k/=2;
		k*=2;
		k++;
		double[] kernel = new double[k*k];
		int i=0;
		double scale2 = (scale*scale);
		for(int y=-(k/2)+offy; y<=(k/2)+offy; y++)
		{
			for(int x=-(k/2)+offx; x<=(k/2)+offx; x++)
			{
				kernel[i] = gbk.base1*Math.exp(-(((x*x)*scale2)+((y*y)*scale2))/gbk.base2);
				gbk.normal+=kernel[i];
				i++;
			}
		}
		gbk.setHeight(k);
		gbk.setWidth(k);
		gbk.setKernel(kernel);
		return gbk;
	}

	public String asString()
	{
		StringBuilder sb = new StringBuilder();
		int i=0;
		sb.append("[\n");
		for(int y=0; y<this.getHeight(); y++)
		{
			sb.append("[ ");
			for(int x=0; x<this.getWidth(); x++)
			{
				sb.append(" ");
				sb.append(kernel[i]);
				sb.append(" ");
				i++;
			}
			sb.append("]\n");
		}
		sb.append("] (k="+this.getWidth()+", sigma2="+this.sigma2+", n="+this.normal+")\n");
		return sb.toString();
	}

	@Override
	public double calculateSample(NoiseField nf, int x, int y)
	{
		return super.calculateSample(nf, x, y)/this.normal;
	}

	public static void main(String[] args)
	{
		GaussianBlurKernel gbk = GaussianBlurKernel.getKernel(5, 0.5, 1.0);
		System.err.println(gbk.asString());
	}

}
