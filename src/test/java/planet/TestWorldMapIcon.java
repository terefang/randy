package planet;

import com.github.terefang.randy.map.ColorRamp;
import com.github.terefang.randy.planetj.PlanetHelper;
import com.github.terefang.randy.planetj.PlanetJ;
import com.github.terefang.randy.planetj.codec.ImageCodec;
import lombok.SneakyThrows;

import java.awt.*;

public class TestWorldMapIcon {


    @SneakyThrows
    public static void main(String[] args)
    {
        String _NAME = "SHPR";
        PlanetJ planet = new PlanetJ();
        planet.init();
        planet.setSeed(0.900000016); //0.6 // 0.900000016
        //planet.setAltitudeAdjustment(.00012);
        //planet.setInitialAltitude(44);

        int _rr = 512;
        planet.setWidth(_rr);
        planet.setHeight(_rr);

        planet.setView(PlanetJ.PROJ_VIEW_ORTHOGRAPHIC);
        planet.setBaseLatitude(0);
        planet.setBaseLongitude(0);
        planet.setScale(1);

        planet.setUseAlternativeColors(true);
        planet.setNumberOfColors(32);
        planet.setLatitudeColors(true);
        planet.setNonLinear(false);
        planet.setWrinkleContribution(-1);

        planet.setDoshade(false);
        planet.setDoWaterShade(false);

        //ColorRamp cr = ColorRamp.getLefebvre2();
        ColorRamp cr = ColorRamp.getBase(Color.ORANGE, Color.MAGENTA,50);
        planet.setColorRamp(cr);
        planet.setColorRampSeaMin(-0.1);
        planet.setColorRampLandMax(0.1);

        planet.setFractalOverlay(PlanetHelper.createFractalOverlay());

        planet.setup();
        planet.process();

        planet.save(String.format("out/planet/test-%s-full.png", _NAME));
    }

}
