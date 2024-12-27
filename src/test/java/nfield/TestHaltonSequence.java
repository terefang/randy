package nfield;

import com.github.terefang.randy.RandyUtil;
import lombok.SneakyThrows;
import util.TestUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

public class TestHaltonSequence
{
    @SneakyThrows
    public static void main(String[] args)
    {
        BufferedImage _bI = new BufferedImage(1024,1024,BufferedImage.TYPE_INT_RGB);
        Graphics2D _g = _bI.createGraphics();
        _g.setColor(Color.YELLOW);
        int _i = 1;
        for(double[] _item : RandyUtil.haltonCoordinates( 2, 4096))
        {
            int _px = (int) ((1024-(1024*_item[0])) % 1024);
            int _py = (int) ((1024-(1024*_item[1])) % 1024);

            _g.drawOval(_px-1, _py-1, 2, 2);
            File _file = new File("./out/nfield/TestHaltonSequence/TestHaltonSequence~Coords~"+_i+".png");
            _file.getParentFile().mkdirs();
            ImageIO.write(_bI,"png", _file);
            _i++;
        }
    }

    static double _PC_TO_LJ = 3.26156;
    public static Color[] _COLOR = { Color.CYAN, Color.WHITE, Color.YELLOW, Color.ORANGE, Color.RED, Color.MAGENTA, Color.BLUE };
    @SneakyThrows
    public static void main_6(String[] args)
    {
        int _seed=0xde3dcafe;
        for(double[] _item : RandyUtil.haltonCoordinates( 3, 32))
        {
            double _px = (8-(((int)((0x1000 * _item[0])+_seed) & 0xfff)/256.))*_PC_TO_LJ;
            double _py = (8-(((int)((0x1000 * _item[1])+(_seed>>>10)) & 0xfff)/256.))*_PC_TO_LJ;
            double _pz = (8-(((int)((0x1000 * _item[2])+(_seed>>>20)) & 0xfff)/256.))*_PC_TO_LJ;
            System.out.println(String.format("x=%.0f, y=%.0f, z=%.0f", _px, _py, _pz));
        }
    }

    @SneakyThrows
    public static void main_5(String[] args)
    {
        int[] _test = new int[256];
        int _seed=0xd33dcafe;
        int _i=0;
        int _c=0;
        double _x = .5;
        double _y = .5;
        BufferedImage _bI = new BufferedImage(1024,1024,BufferedImage.TYPE_INT_RGB);
        for(int _kk=0x10; _kk<(1<<24); _kk<<=3)
        {
            for(; _i<_kk; _i++) {
                _x +=(.5-RandyUtil.radicalInverse(2, _i));
                _y +=(.5-RandyUtil.radicalInverse(3, _i));
                int _px = (int) ((int)(0x10 * _x) & 0xF);
                int _py = (int) ((int)(0x10 * _y) & 0xF);
                _test[(((_px<<4) | _py)+_seed) & 0xff]++;
            }
            TestUtil.print(_test, _COLOR[_c], _bI);
            _c++;
        }
        File _file = new File("./out/nfield/TestHaltonSequence/TestHaltonSequence~Variance.png");
        _file.getParentFile().mkdirs();
        ImageIO.write(_bI,"png", _file);
    }

    @SneakyThrows
    public static void main_4(String[] args)
    {
        BufferedImage _bi = new BufferedImage(1024,1024,BufferedImage.TYPE_INT_RGB);
        int _i=0;
        double _x = .5;
        double _y = .5;
        for(int _kk=1; _kk<(1<<12); _kk++)
        {
            Graphics2D _g = _bi.createGraphics();
            _g.setColor(Color.YELLOW);
            for(; _i<_kk; _i++)
            {
                _x +=(.5-RandyUtil.radicalInverse(2, _i));
                _y +=(.5-RandyUtil.radicalInverse(3, _i));
                int _px = (int) (((int)(512. * _x) <<1) % 1024);
                int _py = (int) (((int)(512. * _y) <<1) % 1024);
                _g.drawOval(_px-1, _py-1, 2, 2);
            }
            _g.dispose();
            File _file = new File(String.format("./out/nfield/TestHaltonSequence/TestHaltonSequence~Alt~%08d.png", _kk));
            _file.getParentFile().mkdirs();
            ImageIO.write(_bi,"png", _file);
        }
    }

    @SneakyThrows
    public static void main_3(String[] args)
    {
        for(int _k=0; _k<20; _k++)
        {
            int _kk= 1<<_k;
            BufferedImage _bi = new BufferedImage(1024,1024,BufferedImage.TYPE_INT_RGB);
            Graphics2D _g = _bi.createGraphics();
            _g.setColor(Color.YELLOW);
            double _x = 0;
            double _y = 0;
            for(int _i=0; _i<_kk; _i++)
            {
                _x += RandyUtil.haltonSequence(RandyUtil._PRIMES_1024[2], _i)+.5;
                _y += RandyUtil.haltonSequence(RandyUtil._PRIMES_1024[3], _i)+.5;
                int _px = (int) ((1024. * _x) % 1024);
                int _py = (int) ((1024. * _y) % 1024);
                _g.drawOval(_px-1, _py-1, 2, 2);
            }
            _g.dispose();
            File _file = new File(String.format("./out/nfield/TestHaltonSequence/TestHaltonSequence~%08d.png", _kk));
            _file.getParentFile().mkdirs();
            ImageIO.write(_bi,"png", _file);
        }
    }

    @SneakyThrows
    public static void main_2(String[] args)
    {
        BufferedImage _bi = new BufferedImage(1024,1024,BufferedImage.TYPE_INT_RGB);
        int _i=0;
        double _x = .5;
        double _y = .5;
        for(int _kk=1; _kk<(1<<12); _kk++)
        {
            Graphics2D _g = _bi.createGraphics();
            _g.setColor(Color.YELLOW);
            for(; _i<_kk; _i++)
            {
                _x = _y + (.5-RandyUtil.haltonSequence(RandyUtil._PRIMES_1024[2], _i));
                _y = _x + (.5-RandyUtil.haltonSequence(RandyUtil._PRIMES_1024[3], _i));
                int _px = (int) ((1024. * _x) % 1024);
                int _py = (int) ((1024. * _y) % 1024);
                _g.drawOval(_px-1, _py-1, 2, 2);
            }
            _g.dispose();
            File _file = new File(String.format("./out/nfield/TestHaltonSequence/TestHaltonSequence~Alt~%08d.png", _kk));
            _file.getParentFile().mkdirs();
            ImageIO.write(_bi,"png", _file);
        }
    }
}
