/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.terefang.randy.planetj.proc;

import com.github.terefang.randy.planetj.PlanetJ;

import java.util.concurrent.Callable;

/**
 *
 * @author fredo
 */
public class AzimuthProc implements Callable<AzimuthProc>
{
    public double ymin;
    public double ymax;
    PlanetJ main = null;

    int j;
    boolean k;

    public AzimuthProc(PlanetJ that, boolean m_k, int m_j)
    {
        main = that;
        k=m_k;
        j=m_j;
    }
    
    public static AzimuthProc create(PlanetJ planetJ, int j, boolean k)
    {
        return new AzimuthProc(planetJ, k, j);
    }
    
    @Override
    public AzimuthProc call() throws Exception {
        for (int i = 0; i < main.Width ; i++) {
            double x = (2.0*i-main.Width)/main.Height/main.scale;
            double y = (2.0*j-main.Height)/main.Height/main.scale;
            double zz = x*x+y*y;
            double z = 1.0-0.5*zz;
            if (z<-1.0) {
                main.heights[i][j] = Double.NaN;
                main.col[i][j] = main.BACK;
                if (main.doshade) main.shades[i][j] = 255;
            } else {
                zz = Math.sqrt(1.0-0.25*zz);
                x = x*zz;
                y = y*zz;
                double x1 = main.clo*x+main.slo*main.sla*y+main.slo*main.cla*z;
                double y1 = main.cla*y-main.sla*z;
                double z1 = -main.slo*x+main.clo*main.sla*y+main.clo*main.cla*z;
                if (y1 < ymin) ymin = y1;
                if (y1 > ymax) ymax = y1;
                main.planet_main(i,j,x1,y1,z1, main.Depth, k);
            }
        }
        main.tickH(j);
        return this;
    }
}
