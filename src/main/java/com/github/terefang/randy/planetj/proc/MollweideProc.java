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
public class MollweideProc implements Runnable
{
    PlanetJ main = null;

    int j;
    boolean k;

    public MollweideProc(PlanetJ that, boolean m_k, int m_j)
    {
        main = that;
        k=m_k;
        j=m_j;
    }

    public static MollweideProc create(PlanetJ planetJ, int j, boolean k)
    {
        return new MollweideProc(planetJ, k, j);
    }

    @Override
    public void run()
    {
        double y1 = 2*(2.0*j-main.Height)/main.Width/main.scale;
        if (Math.abs(y1)>=1.0) {
            for (int i = 0; i < main.Width; i++) {
                main.col[i][j] = main.BACK;
                main.heights[i][j] = Double.NaN;
                if (main.doshade) main.shades[i][j] = 255;
            }
        }
        else
        {
            double zz = Math.sqrt(1.0-y1*y1);
            double y = 2.0/main.PI*(y1*zz+Math.asin(y1));
            double cos2 = Math.sqrt(1.0-y*y);
            if (cos2>0.0) {
                double scale1 = main.scale*main.Width/main.Height/cos2/main.PI;
                int sDepth = 3*((int)(main.log_2(scale1*main.Height)))+3;
                for (int i = 0; i < main.Width ; i++) {
                    double theta1 = main.PI/zz*(2.0*i-main.Width)/main.Width/main.scale;
                    if (Math.abs(theta1)>main.PI) {
                        main.col[i][j] = main.BACK;
                        if (main.doshade) main.shades[i][j] = 255;
                    }
                    else
                    {
                        theta1 += main.baseLongitude-0.5*main.PI;
                        main.planet_main(i,j,Math.cos(theta1)*cos2,y,-Math.sin(theta1)*cos2, sDepth, k);
                    }
                }
            }
        }
        main.tickH(j);
    }
    
}
