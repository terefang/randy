package planet;

import com.github.terefang.randy.RandyUtil;
import com.github.terefang.randy.fractal.IFractal;
import com.github.terefang.randy.fractal.RidgedMultiFractal;
import com.github.terefang.randy.map.ColorRamp;
import com.github.terefang.randy.map.ColorRampDynImpl;
import com.github.terefang.randy.noise.NoiseUtil;
import com.github.terefang.randy.planetj.PlanetHelper;
import com.github.terefang.randy.planetj.codec.ImageCodec;
import com.github.terefang.randy.planetj.projection.IProjectionCallback;
import com.github.terefang.randy.planetj.projection.PlanetJProjectionContext;
import lombok.SneakyThrows;
import com.github.terefang.randy.planetj.PlanetJ;

public class TestWorldMap {

    @SneakyThrows
    public static void main_(String[] args) {
        PlanetJ planet = new PlanetJ();
        planet.init();
        planet.setSeed(0.900000016); //0.6 // 0.900000016

        int _rr = 2048;

        planet.setWidth(_rr);
        planet.setHeight(_rr);

        planet.setView(PlanetJ.PROJ_VIEW_SQUARE);

        planet.setUseAlternativeColors(true);
        planet.setNumberOfColors(32);
        planet.setLatitudeColors(true);
        //planet.setInitialAltitude(0);

        ColorRamp cr = ColorRamp.getHard();
        planet.setColorRamp(cr);
        planet.setColorRampSeaMin(-0.1);
        planet.setColorRampLandMax(0.06);

        planet.setWrinkleContribution(-1);
        //planet.setAltitudeWeight(-0.5);

        double[] lats = { 45.*3./2., 45./2., -45./2., -45.*3./2. };
        double[] longs = { 0, 45, 90, 135, 180, 225, 270, 315 };

        for(double _long : longs)
        {
            for(double _lat : lats)
            {
                planet.setBaseLatitude(_lat/2.);
                planet.setBaseLongitude(_long+70);;
                planet.setScale(8);

                planet.setup();
                planet.process();

                //	planet.morphologicalErode();
                //	planet.save_GXF0(out+".gxf");
                planet.save(String.format("out/planet/test_world_%03d_%03d.png", (int)_long, (int)(_lat*10)));
                ImageCodec.saveWLD(planet, String.format("out/planet/test_world_%03d_%03d.wld", (int)_long, (int)(_lat*10)), 360-(45./2.)-70, 0);
                //planet.save_WLD("test.wld");
                //planet.save_TER(out+".ter");
                //	planet.save_VTP_BT(out+".bt");
            }
        }

    }

    @SneakyThrows
    public static void main__(String[] args) {
        PlanetJ planet = new PlanetJ();
        planet.init();
        planet.setSeed(0.900000016); //0.6 // 0.900000016

        planet.setWidth(1024);
        planet.setHeight(1024);

        planet.setView(PlanetJ.PROJ_VIEW_ORTHOGRAPHIC);
        planet.setBaseLatitude(0);
        planet.setBaseLongitude(150);;
        planet.setScale(1./2.);

        planet.setUseAlternativeColors(true);
        planet.setNumberOfColors(32);
        planet.setLatitudeColors(true);

        ColorRamp cr = ColorRamp.getHard();
        planet.setColorRamp(cr);
        planet.setColorRampSeaMin(-0.1);
        planet.setColorRampLandMax(0.06);
        planet.setWrinkleContribution(-1);

        for(int _i = 0; _i<360; _i+=5)
        {
            planet.setBaseLatitude(0);
            planet.setBaseLongitude(_i);
            planet.setup();
            planet.process();
            planet.save(String.format("out/planet/test-%04d.png", _i));
        }
        // convert -delay 25 test-*.png -loop 0 test.gif
    }

    @SneakyThrows
    public static void main/*_long_lat*/(String[] args)
    {
        String _NAME = "LONGLAT_E";
        PlanetJ planet = new PlanetJ();
        planet.init();
        planet.setSeed(0.900000016); //0.6 // 0.900000016
        //planet.setAltitudeAdjustment(.00012);
        //planet.setInitialAltitude(44);

        int _rr = 2048;
        planet.setWidth(_rr);
        planet.setHeight(_rr/2);

        planet.setView(PlanetJ.PROJ_VIEW_LONGLAT);
        planet.setBaseLatitude(0);
        planet.setBaseLongitude(-50);
        planet.setScale(1);

        planet.setUseAlternativeColors(true);
        planet.setNumberOfColors(32);
        planet.setLatitudeColors(true);
        //planet.setNonLinear(false);
        //planet.setWrinkleContribution(-1);

        //planet.setDoshade(true);
        //planet.setDoWaterShade(true);


        ColorRamp cr = ColorRampDynImpl.getEarth();
        planet.setColorRamp(cr);
        planet.setColorRampSeaMin(-0.1);
        planet.setColorRampLandMax(0.1);

        planet.setTemperatureBase(0.);
        planet.setTemperatureVariationFactor(0.02);
        planet.setTemperatureVariationFrequency(12.34567);

        planet.setRainfallBase(.0066);
        planet.setRainfallVariationFactor(0.033);
        planet.setRainfallVariationFrequency(12.34567);

        planet.setFractalOverlay(PlanetHelper.createFractalOverlay());
        //planet.setFractalOverlay(null);
        //planet.setWaterLandPercentage(40);

        planet.setup();
        planet.process();

        planet.save(String.format("out/planet/test-%s-full.png", _NAME));
        ImageCodec.saveWLD(planet, String.format("out/planet/test-%s-full.wld", _NAME));
        planet.saveH(String.format("out/planet/test-%s-hf.png", _NAME));
        ImageCodec.saveBiome(planet, String.format("out/planet/test-%s-full-biome.png", _NAME));
        ImageCodec.saveWLD(planet, String.format("out/planet/test-%s-full-biome.wld", _NAME));
        ImageCodec.saveRainfall(planet, String.format("out/planet/test-%s-full-rain.png", _NAME));
        ImageCodec.saveRainAdj(planet, String.format("out/planet/test-%s-full-rain-adj.png", _NAME));
        ImageCodec.saveTemperature(planet, String.format("out/planet/test-%s-full-temp.png", _NAME));
        ImageCodec.saveTempAdj(planet, String.format("out/planet/test-%s-full-temp-adj.png", _NAME));
        planet.save_GXF0(String.format("out/planet/test-%s-full-0.gxf", _NAME), -1.);
        planet.save_GXF(String.format("out/planet/test-%s-full.gxf", _NAME), -1.);
        planet.saveWaterflux(String.format("out/planet/test-%s-waterflux.png", _NAME));
    }

    @SneakyThrows
    public static void main_Lefebvre2_long_lat(String[] args)
    {
        String _NAME = "LONGLAT";
        PlanetJ planet = new PlanetJ();
        planet.init();
        planet.setSeed(0.900000016); //0.6 // 0.900000016
        //planet.setAltitudeAdjustment(.00012);
        //planet.setInitialAltitude(44);

        int _rr = 2048;
        planet.setWidth(_rr);
        planet.setHeight(_rr/2);

        planet.setView(PlanetJ.PROJ_VIEW_LONGLAT);
        planet.setBaseLatitude(0);
        planet.setBaseLongitude(-50);
        planet.setScale(1);

        planet.setUseAlternativeColors(true);
        planet.setNumberOfColors(32);
        planet.setLatitudeColors(true);
        planet.setNonLinear(false);
        planet.setWrinkleContribution(-1);

        planet.setDoshade(true);
        planet.setDoWaterShade(true);


        ColorRamp cr = ColorRamp.getLefebvre2();
        planet.setColorRamp(cr);
        planet.setColorRampSeaMin(-0.1);
        planet.setColorRampLandMax(0.1);

        planet.setTemperatureBase(0.);
        planet.setTemperatureVariationFactor(0.02);
        planet.setTemperatureVariationFrequency(12.34567);

        planet.setRainfallBase(.0066);
        planet.setRainfallVariationFactor(0.033);
        planet.setRainfallVariationFrequency(12.34567);

        planet.setFractalOverlay(PlanetHelper.createFractalOverlay());
        //planet.setFractalOverlay(null);
        //planet.setWaterLandPercentage(40);

        planet.setup();
        planet.process();

        planet.save(String.format("out/planet/test-%s-full.png", _NAME));
        ImageCodec.saveWLD(planet, String.format("out/planet/test-%s-full.wld", _NAME));
        planet.saveH(String.format("out/planet/test-%s-hf.png", _NAME));
        ImageCodec.saveBiome(planet, String.format("out/planet/test-%s-full-biome.png", _NAME));
        ImageCodec.saveWLD(planet, String.format("out/planet/test-%s-full-biome.wld", _NAME));
        ImageCodec.saveRainfall(planet, String.format("out/planet/test-%s-full-rain.png", _NAME));
        ImageCodec.saveRainAdj(planet, String.format("out/planet/test-%s-full-rain-adj.png", _NAME));
        ImageCodec.saveTemperature(planet, String.format("out/planet/test-%s-full-temp.png", _NAME));
        ImageCodec.saveTempAdj(planet, String.format("out/planet/test-%s-full-temp-adj.png", _NAME));
        planet.save_GXF0(String.format("out/planet/test-%s-full-0.gxf", _NAME), -1.);
        planet.save_GXF(String.format("out/planet/test-%s-full.gxf", _NAME), -1.);
        planet.saveWaterflux(String.format("out/planet/test-%s-waterflux.png", _NAME));
    }

    @SneakyThrows
    public static void main_spole/*SOUTH POLE*/(String[] args) {
        PlanetJ planet = new PlanetJ();
        planet.init();
        planet.setSeed(0.900000016); //0.6 // 0.900000016
        int _rr = 4096;
        planet.setWidth(_rr);
        planet.setHeight(_rr/2);

        planet.setView(PlanetJ.PROJ_VIEW_ORTHOGRAPHIC);
        planet.setBaseLatitude(-90);
        planet.setBaseLongitude(150);;
        planet.setScale(1);

        planet.setUseAlternativeColors(true);
        planet.setNumberOfColors(32);
        planet.setLatitudeColors(true);
        //planet.setNonLinear(true);

        ColorRamp cr = ColorRamp.getHard();
        planet.setColorRamp(cr);
        planet.setColorRampSeaMin(-0.1);
        planet.setColorRampLandMax(0.06);
        planet.setWrinkleContribution(-1);

        planet.setTemperatureBase(0.);
        planet.setTemperatureVariationFactor(0.02);
        planet.setTemperatureVariationFrequency(2.345);

        planet.setRainfallBase(0.);
        planet.setRainfallVariationFactor(0.066);
        planet.setRainfallVariationFrequency(12.34567);

        planet.setup();
        planet.process();

        planet.save("out/planet/test-spole.png");
        planet.saveBiome("out/planet/test-spole-biome.png");
        planet.saveRainfall("out/planet/test-spole-rain.png");
        planet.saveTemperature("out/planet/test-spole-temp.png");
    }

    @SneakyThrows
    public static void main_x(String[] args)
    {
        PlanetJ planet = new PlanetJ();
        planet.init();
        planet.setSeed(0.900000016); //0.6 // 0.900000016

        int _rr = 512;
        planet.setWidth(_rr);
        planet.setHeight(_rr);
        //planet.setHeight(_rr/2);
        planet.setView(PlanetJ.PROJ_VIEW_STEREOGRAPHIC);
        planet.setScale(.5);

        planet.setUseAlternativeColors(true);
        planet.setNumberOfColors(32);
        planet.setLatitudeColors(true);
        planet.setNonLinear(false);
        planet.setWrinkleContribution(-1);

        planet.setDoshade(true);
        planet.setDoWaterShade(true);

        planet.setTemperatureBase(0.);
        planet.setTemperatureVariationFactor(0.02);
        planet.setTemperatureVariationFrequency(12.34567);

        planet.setRainfallBase(.0066);
        planet.setRainfallVariationFactor(0.033);
        planet.setRainfallVariationFrequency(12.34567);

        planet.setHgrid(15);
        planet.setVgrid(15);

        int [] _longs = { 0, 45, 90, 135, 180, 225, 270, 315 };

        for(int _l : _longs)
        {
            planet.setBaseLongitude(_l);

            planet.setup();
            planet.process();

            planet.saveOutline(String.format("out/planet/xtest-outline-%03d.png", _l));
            planet.save(String.format("out/planet/xtest-%03d.png", _l));
            planet.saveBiome(String.format("out/planet/xtest-biome-%03d.png", _l));
        }
        planet.setBaseLongitude(0);
        {
            planet.setBaseLatitude(90);

            planet.setup();
            planet.process();

            planet.saveOutline("out/planet/xtest-outline-north.png");
            planet.save("out/planet/xtest-north.png");
            planet.saveBiome("out/planet/xtest-biome-north.png");
        }
        {
            planet.setBaseLatitude(-90);

            planet.setup();
            planet.process();

            planet.saveOutline("out/planet/xtest-outline-south.png");
            planet.save("out/planet/xtest-south.png");
            planet.saveBiome("out/planet/xtest-biome-south.png");
        }
    }

    public static void main_square_full(String[] args)
    {
        PlanetJ planet = new PlanetJ();
        planet.init();
        planet.setSeed(0.900000016); //0.6 // 0.900000016
        //planet.setAltitudeAdjustment(.00012);
        //planet.setInitialAltitude(44);

        int _rr = 2048;
        planet.setWidth(_rr);
        planet.setHeight(_rr/2);
        //planet.setHeight(_rr);

        //planet.setView(PlanetJ.PROJ_VIEW_SQUARE);
        //planet.setView(PlanetJ.PROJ_VIEW_STEREOGRAPHIC);
        planet.setView(PlanetJ.PROJ_VIEW_LONGLAT);
        //planet.setBaseLatitude(40);
        planet.setBaseLongitude(-90);;
        // STEREOGRAPHIC w/scale=1 -> 1/4 circumference
        //planet.setScale(2);

        planet.setUseAlternativeColors(true);
        planet.setNumberOfColors(32);
        planet.setLatitudeColors(true);
        planet.setNonLinear(false);
        planet.setWrinkleContribution(-1);

        planet.setDoshade(true);
        planet.setDoWaterShade(true);


        //if(false)
        {
            ColorRamp cr = ColorRamp.getLefebvre2();
            planet.setColorRamp(cr);
            planet.setColorRampSeaMin(-0.1);
            planet.setColorRampLandMax(0.1);
        }

        //if(false)
        {
            planet.setHgrid(10);
            planet.setVgrid(10);
        }

        planet.setTemperatureBase(0.);
        planet.setTemperatureVariationFactor(0.02);
        planet.setTemperatureVariationFrequency(12.34567);

        planet.setRainfallBase(.0066);
        planet.setRainfallVariationFactor(0.033);
        planet.setRainfallVariationFrequency(12.34567);

        planet.setHgrid(15);
        planet.setVgrid(15);
        //planet.setWaterLandPercentage(50);

        planet.setup();
        planet.process();

        /*
        for (int i = 0; i < planet.Width; i++)
        {
            for (int j = 0; j < planet.Height; j++)
            {
                planet.planet0_waterflux(i,j,0,0,0,1);
            }
        }
        planet.saveWaterflux("out/planet/test-full-flux.png");
        */

        planet.save("out/planet/test-full.png");
        planet.saveBiome("out/planet/test-full-biome.png");
        planet.saveTemperature("out/planet/test-full-temp.png");
        planet.saveRainfall("out/planet/test-full-rain.png");
        planet.saveOutline("out/planet/test-full-outline.png");
        planet.saveH("out/planet/test-full-h.png");
        //planet.save_TER("out/planet/test-full.ter");
        planet.save_MDR("out/planet/test-full.mdr", -1);
        //planet.save_GXF0("out/planet/test-full.gxf", -1.);
    }
}
