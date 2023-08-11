package com.github.terefang.randy.planetj.proc;

import com.github.terefang.randy.planetj.PlanetJ;

public class PetersProc implements Runnable
{
	PlanetJ main = null;
	
	double y,scale1,cos2,theta1;
	int i,j,k;
	boolean b;

	public static PetersProc create(PlanetJ that, int m_j, int m_k, boolean m_b) { return new PetersProc(that, m_j, m_k, m_b); }
	
	public PetersProc(PlanetJ that, int m_j, int m_k,boolean m_b)
	{
		main = that;
		j=m_j;
		k=m_k;
		b=m_b;
	}
	
	@Override
	public void run() {
		double y,scale1,theta1,cos2;
		
		y = 0.5*main.PI*(2.0*(j-k)-main.Height)/main.Width/main.scale;
		if (Math.abs(y)>1.0)
			for (i = 0; i < main.Width ; i++) {
				main.heights[i][j] = Double.NaN;
				main.col[i][j] = main.BACK;
				if (main.doshade) main.shades[i][j] = 255;
			}
		else {
			cos2 = Math.sqrt(1.0-y*y);
			if (cos2>0.0) {
				scale1 = main.scale*main.Width/main.Height/cos2/main.PI;
				int sDepth = 3*((int)(main.log_2(scale1*main.Height)))+3;
				for (i = 0; i < main.Width ; i++) {
					theta1 = main.baseLongitude-0.5*main.PI+main.PI*(2.0*i-main.Width)/main.Width/main.scale;
					main.planet_main(i,j,Math.cos(theta1)*cos2,y,-Math.sin(theta1)*cos2, sDepth, b);
				}
			}
		}
		main.tickH(j);
	}
	
}
