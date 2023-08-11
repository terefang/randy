/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.terefang.randy.planetj.proc;


import com.github.terefang.randy.planetj.PlanetJ;

/**
 *
 * @author fredo
 */
public class MercatorProc implements Runnable
{
    PlanetJ main = null;
    
    double y,scale1,cos2,theta1;
    int i,j,k;
    boolean b;
    public MercatorProc(PlanetJ that, int m_k, int m_j, boolean m_b)
    {
        main = that;
        k=m_k;
        j=m_j;
        b=m_b;
    }
    
    public static MercatorProc create(PlanetJ planetJ, int j, int k,boolean b)
    {
        return new MercatorProc(planetJ, k, j, b);
    }
    
    @Override
    public void run() {
        y = main.PI*(2.0*(j-k)-main.Height)/main.Width/main.scale;
        y = Math.exp(2.*y);
        y = (y-1.)/(y+1.);
        scale1 = main.scale*main.Width/main.Height/Math.sqrt(1.0-y*y)/main.PI;
        cos2 = Math.sqrt(1.0-y*y);
        int sDepth = 3*((int)(main.log_2(scale1*main.Height)))+3;
        for (i = 0; i < main.Width ; i++) 
        {
                theta1 = main.baseLongitude-0.5*main.PI+main.PI*(2.0*i-main.Width)/main.Width/main.scale;
                if(false)
                {
                    double alt = main.planet1(Math.cos(theta1)*cos2,y,-Math.sin(theta1)*cos2, sDepth);
                    main.col[i][j] = main.alt2color(alt, Math.cos(theta1)*cos2,y,-Math.sin(theta1)*cos2);
                    main.heights[i][j] = (int) alt;
                    if(!main.doWaterShade && alt<=0.0)
                    {
                        main.shades[i][j] = main.waterShade;
                    }
                    else if(main.doshade)
                    {
                        main.shades[i][j] = main.shade;
                    }
                }
            main.planet_main(i,j,Math.cos(theta1)*cos2,y,-Math.sin(theta1)*cos2, sDepth, b);
        }
        main.tickH(j);
    }
    
}
