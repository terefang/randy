package planet;

import com.github.terefang.randy.RandyUtil;
import com.github.terefang.randy.fractal.IFractal;
import com.github.terefang.randy.fractal.RidgedMultiFractal;
import com.github.terefang.randy.map.ColorRamp;
import com.github.terefang.randy.noise.NoiseUtil;
import com.github.terefang.randy.planetj.PlanetHelper;
import com.github.terefang.randy.planetj.codec.ImageCodec;
import com.github.terefang.randy.planetj.projection.IProjectionCallback;
import com.github.terefang.randy.planetj.projection.PlanetJProjectionContext;
import lombok.SneakyThrows;
import com.github.terefang.randy.planetj.PlanetJ;

public class TestWorldMapHex {


    @SneakyThrows
    public static void main/*_long_lat*/(String[] args)
    {
        String _NAME = "HEXA";
        PlanetJ planet = new PlanetJ();
        planet.init();
        planet.setSeed(0.900000016); //0.6 // 0.900000016
        //planet.setAltitudeAdjustment(.00012);
        //planet.setInitialAltitude(44);

        int _rr = 2048;
        planet.setWidth(_rr);
        planet.setHeight(_rr/2);

        planet.setView(PlanetJ.PROJ_VIEW_HEXAGONAL);
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
        //ColorRamp cr = ColorRamp.getBase(Color.ORANGE, Color.MAGENTA);
        planet.setColorRamp(cr);
        planet.setColorRampSeaMin(-0.1);
        planet.setColorRampLandMax(0.1);

        planet.setTemperatureBase(0.);
        planet.setTemperatureVariationFactor(0.02);
        planet.setTemperatureVariationFrequency(2.345678);

        planet.setRainfallBase(.0066);
        planet.setRainfallVariationFactor(0.033);
        planet.setRainfallVariationFrequency(12.3456789);

        planet.setFractalOverlay(PlanetHelper.createFractalOverlay());
        planet.setFractalOverlay(null);
        //planet.setWaterLandPercentage(40);

        planet.setup();
        planet.process();

        planet.save(String.format("out/planet/test-%s-full.png", _NAME));
        ImageCodec.saveWLD(planet, String.format("out/planet/test-%s-full.wld", _NAME));
        planet.saveH(String.format("out/planet/test-%s-hf.png", _NAME));
        planet.saveHRidge(String.format("out/planet/test-%s-hR.png", _NAME));
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

}
