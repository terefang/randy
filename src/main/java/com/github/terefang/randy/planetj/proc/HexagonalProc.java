/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.terefang.randy.planetj.proc;


import com.github.terefang.randy.planetj.PlanetJ;
import com.github.terefang.randy.utils.LogSink;

/**
 *
 * @author fredo
 */
public class HexagonalProc implements Runnable
{
    PlanetJ main = null;
    LogSink _log;
    int j;
    boolean k;

    public HexagonalProc(PlanetJ that,LogSink _log, boolean m_k, int m_j)
    {
        main = that;
        k=m_k;
        j=m_j;
        this._log=_log;
    }
    
    public static HexagonalProc create(PlanetJ planetJ,LogSink _log, int j, boolean k)
    {
        return new HexagonalProc(planetJ, _log, k, j);
    }
    
    @Override
    public void run()
    {
        double _lat = (main.PI*((double)j)/((double)main.Height-1))-main.baseLatitude;
        int _k = Math.abs((j-(main.Height/2))/2);
        int i;
        for (i = 0; i < _k; i++)
        {
            main.heights[i][j] = Double.NaN;
        }
        for (; i < main.Width-_k; i++)
        {
            double _lon = main.PI+(2*main.PI*((double)(i-_k))/((double)main.Width-1-(_k*2)))+main.baseLongitude;;
            // TODO
            double _y = -Math.cos(_lat);
            double _ysin = Math.sin(_lat);
            double _x = Math.sin(_lon)*_ysin;
            double _z = Math.cos(_lon)*_ysin;
            main.planet_main(i,j,_x,_y,_z, main.Depth, k);
        }
        for (; i < main.Width; i++)
        {
            main.heights[i][j] = Double.NaN;
        }
        main.tickH(j,this._log);
    }


}
