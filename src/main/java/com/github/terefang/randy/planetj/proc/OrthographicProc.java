package com.github.terefang.randy.planetj.proc;

import com.github.terefang.randy.planetj.PlanetJ;
public class OrthographicProc implements Runnable
{
	PlanetJ main = null;
	
	double y,scale1,cos2,theta1;
	int i,j;
	boolean b;

	public static OrthographicProc create(PlanetJ that, int m_j, boolean m_b) { return new OrthographicProc(that, m_j, m_b); }
	
	public OrthographicProc(PlanetJ that, int m_j, boolean m_b)
	{
		main = that;
		j=m_j;
		b=m_b;
	}
	
	@Override
	public void run() {
		double y,x,z,x1,y1,z1;
		
		for (i = 0; i < main.Width ; i++) {
			x = (2.0*i-main.Width)/main.Height/main.scale;
			y = (2.0*j-main.Height)/main.Height/main.scale;
			if (x*x+y*y>1.0)
			{
				main.heights[i][j] = Double.NaN;
				main.col[i][j] = main.BACK;
				if (main.doshade) main.shades[i][j] = 255;
			} else {
				z = Math.sqrt(1.0-x*x-y*y);
				x1 = main.clo*x+main.slo*main.sla*y+main.slo*main.cla*z;
				y1 = main.cla*y-main.sla*z;
				z1 = -main.slo*x+main.clo*main.sla*y+main.clo*main.cla*z;

				main.planet_main(i,j,x1,y1,z1, main.Depth, b);
			}
		}
		main.tickH(j);
	}
	
}
