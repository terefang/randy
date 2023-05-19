package com.github.terefang.randy.kernel;

import com.github.terefang.randy.nfield.NoiseField;

/**
 * Created by fredo on 08.12.15.
 */
public class KuwaharaKernel extends AbstractKernel implements IKernel
{
	public static KuwaharaKernel getKernel(int k)
	{
		KuwaharaKernel kk = new KuwaharaKernel();
		k/=2;
		k*=2;
		k++;
		kk.setHeight(k);
		kk.setWidth(k);
		return kk;
	}

	@Override
	public double calculateSample(NoiseField nf, int x, int y)
	{
		double n0 , m0 , a0 , aT = 0.0, v0 = 0.0;
		double vT = Double.MAX_VALUE;

		n0 = Double.MAX_VALUE;
		m0 = Double.MIN_VALUE;
		a0 = 0.0;
		int i = 0;
		for(int iy=-(this.getHeight()/2); iy<0; iy++)
		{
			for(int ix=-(this.getWidth()/2); ix<=0; ix++)
			{
				double s = nf.getPoint(x+ix, y+iy);
				if(n0>s) n0 = s;
				if(m0<s) m0 = s;
				a0+=s;
				i++;
			}
		}
		a0/=(double)i;
		v0 = Math.abs(m0-n0);
		if(vT>=v0)
		{
			vT=v0;
			aT=a0;
		}

		n0 = Double.MAX_VALUE;
		m0 = Double.MIN_VALUE;
		a0 = 0.0;
		i = 0;
		for(int iy=-(this.getHeight()/2); iy<=0; iy++)
		{
			for(int ix=1; ix<=(this.getWidth()/2); ix++)
			{
				double s = nf.getPoint(x+ix, y+iy);
				if(n0>s) n0 = s;
				if(m0<s) m0 = s;
				a0+=s;
				i++;
			}
		}
		a0/=(double)i;
		v0 = Math.abs(m0-n0);
		if(vT>=v0)
		{
			vT=v0;
			aT=a0;
		}

		n0 = Double.MAX_VALUE;
		m0 = Double.MIN_VALUE;
		a0 = 0.0;
		i = 0;
		for(int iy=1; iy<=(this.getHeight()/2); iy++)
		{
			for(int ix=0; ix<=(this.getWidth()/2); ix++)
			{
				double s = nf.getPoint(x+ix, y+iy);
				if(n0>s) n0 = s;
				if(m0<s) m0 = s;
				a0+=s;
				i++;
			}
		}
		a0/=(double)i;
		v0 = Math.abs(m0-n0);
		if(vT>=v0)
		{
			vT=v0;
			aT=a0;
		}

		n0 = Double.MAX_VALUE;
		m0 = Double.MIN_VALUE;
		a0 = 0.0;
		i = 0;
		for(int iy=-(this.getHeight()/2); iy<0; iy++)
		{
			for(int ix=1; ix<=(this.getWidth()/2); ix++)
			{
				double s = nf.getPoint(x+ix, y+iy);
				if(n0>s) n0 = s;
				if(m0<s) m0 = s;
				a0+=s;
				i++;
			}
		}
		a0/=(double)i;
		v0 = Math.abs(m0-n0);
		if(vT>=v0)
		{
			vT=v0;
			aT=a0;
		}

		return aT;
	}
}
