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
public class LonLatProc implements Runnable
{
    PlanetJ main = null;
    LogSink _log;

    int j;
    boolean k;

    public LonLatProc(PlanetJ that,LogSink _log, boolean m_k, int m_j)
    {
        main = that;
        k=m_k;
        j=m_j;
        this._log = _log;
    }
    
    public static LonLatProc create(PlanetJ planetJ,LogSink _log, int j, boolean k)
    {
        return new LonLatProc(planetJ, _log,k, j);
    }
    
    @Override
    public void run() {
        int sDepth = (int) (3*((int)(main.log_2(main.scale*main.Height))));
        sDepth = (sDepth<main.Depth) ? sDepth : main.Depth;

        double _lat = (main.PI*((((double)j)/((double)main.Height-1))));
        _lat = (_lat-main.PI_2)/main.scale;
        _lat = (_lat+main.PI_2);
        _lat -= main.baseLatitude;
        for (int i = 0; i < main.Width; i++)
        {
            double _lon = +(2*main.PI*((double)(i))/((double)main.Width-1));
            _lon = ((_lon-main.PI)/main.scale);
            _lon = (_lon+(2*main.PI));
            _lon += main.baseLongitude;
            double _y = -Math.cos(_lat);
            double _ysin = Math.sin(_lat);
            double _x = Math.sin(_lon)*_ysin;
            double _z = Math.cos(_lon)*_ysin;
            main.planet_main(i,j,_x,_y,_z, sDepth, k);
        }
        main.tickH(j,_log);
    }
    
}
