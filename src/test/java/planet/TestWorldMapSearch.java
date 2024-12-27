package planet;

import com.github.terefang.randy.map.ColorRamp;
import com.github.terefang.randy.map.ColorRampDynImpl;
import com.github.terefang.randy.nfield.NoiseField;
import com.github.terefang.randy.nfield.NoiseFieldUtil;
import com.github.terefang.randy.planetj.PlanetHelper;
import com.github.terefang.randy.planetj.PlanetJ;
import com.github.terefang.randy.planetj.codec.ImageCodec;
import com.github.terefang.randy.utils.LogSink;
import lombok.SneakyThrows;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class TestWorldMapSearch {

    @SneakyThrows
    public static void main(String[] args)
    {
        ExecutorService _exe = Executors.newFixedThreadPool(16);

        NoiseField searchField = NoiseFieldUtil.readHF("/mnt/nas/fredos/_rpg/d100/RoleMaster/ShadowWorld/maps/geo/Kulthea_Search.png");
        searchField.normalize(0., 1.);
        final AtomicInteger _ctask = new AtomicInteger(0);
        final AtomicReference<Double> _lseed = new AtomicReference<>(0.);
        final AtomicInteger _lerr = new AtomicInteger(Integer.MAX_VALUE);
        for(double _si = 0.; _si<=1.; _si+=.00001)
        {
            _ctask.getAndIncrement();
            final double _s = _si;
            _exe.execute(()->{
                PlanetJ planet = new PlanetJ();
                planet.setLogSink(new LogSink() {
                    @Override
                    public void log(String message) {

                    }

                    @Override
                    public void logProgress(int _pct, String message) {

                    }

                    @Override
                    public void logConsole(String message) {

                    }
                });
                planet.init();
                planet.setSeed(_s);

                planet.setWidth(searchField.getWidth());
                planet.setHeight(searchField.getHeight());
                planet.setView(PlanetJ.PROJ_VIEW_SQUARE);
                planet.setUseAlternativeColors(true);
                planet.setNumberOfColors(32);
                planet.setLatitudeColors(true);
                planet.setWrinkleContribution(-1);
                planet.setBaseLatitude(0);
                planet.setBaseLongitude(0);
                planet.setup();
                planet.process();
                NoiseField _test = NoiseFieldUtil.toNoiseField(planet, true);

                int _err = NoiseFieldUtil.compareTo(searchField, _test,0.01, true);
                int _pct = (100*_err/searchField.getWidth())/searchField.getHeight();
                //System.err.printf("seed = %f\n", _s);

                synchronized (_lerr)
                {
                    if(_err<_lerr.get())
                    {
                        _lseed.set(_s);
                        _lerr.set(_err);
                    }
                }
                _ctask.getAndDecrement();
            });
        }

        _exe.shutdown();

        while(!_exe.isTerminated())
        {
            Thread.sleep(1000L);
            System.err.printf(new Date().toString()+" current = %d, lseed = %f, lerror = %d | %d%%\n", _ctask.get(), _lseed.get(), _lerr.get(), (100*_lerr.get()/searchField.getWidth())/searchField.getHeight());
        }
        System.err.printf("\n\nlseed = %f, lerror = %d | %d%%\n", _lseed.get(), _lerr.get(), (100*_lerr.get()/searchField.getWidth())/searchField.getHeight());
    }

}
