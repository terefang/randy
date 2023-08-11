package com.github.terefang.randy.planetj;

import com.github.terefang.randy.RandyUtil;
import com.github.terefang.randy.map.ColorRamp;
import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.planetj.codec.*;
import com.github.terefang.randy.planetj.proc.*;
import com.github.terefang.randy.planetj.projection.*;
import com.github.terefang.randy.utils.ColorUtil;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/* planet.c */
/* planet generating program */
/* Copyright 1988-2002 Torben AE. Mogensen */

/* version of June 19 2002 */

/* The program generates planet maps based on recursive spatial subdivision */
/* of a tetrahedron containing the globe. The output is a colour PPM bitmap. */

/* The colours may optionally be modified according to latitude to move the */
/* icecaps lower closer to the poles, with a corresponding change in land colours. */

/* The Mercator map at magnification 1 is scaled to fit the Width */
/* it uses the full height (it could extend infinitely) */
/* The orthographic projections are scaled so the full view would use the */
/* full Height. Areas outside the globe are coloured black. */
/* Stereographic and gnomic projections use the same scale as orthographic */
/* in the center of the picture, but distorts scale away from the center. */

/* It is assumed that pixels are square */
/* I have included procedures to print the maps as bmp or ppm */
/* bitmaps (portable pixel map) on standard output or specified files. */

/* I have tried to avoid using machine specific features, so it should */
/* be easy to port the program to any machine. Beware, though that due */
/* to different precision on different machines, the same seed numbers */
/* can yield very different planets. */
/* The primitive user interface is a result of portability concerns */

public class PlanetJ implements IPlanet
{

	private IProjectionCallback<PlanetJProjectionContext> _fractalOverlay;
	private PlanetJProjectionContext _fractalContext;

	public IProjectionCallback<PlanetJProjectionContext> getFractalOverlay() {
		return _fractalOverlay;
	}

	public void setFractalOverlay(IProjectionCallback<PlanetJProjectionContext> _fractalOverlay) {
		this._fractalOverlay = _fractalOverlay;
	}

	public static class vertex {
		public double h; /* altitude */
		public double s; /* seed */
		public double x,y,z; /* coordinates */
		public double shadow; /* approximate rain shadow */
	}
	public static final char PROJ_VIEW_MERCATOR = 'm';
	public static final char PROJ_VIEW_CONICAL = 'c';
	public static final char PROJ_VIEW_STEREOGRAPHIC = 's';
	public static final char PROJ_VIEW_MOLLWEIDE = 'M';
	public static final char PROJ_VIEW_SINUSOID = 'S';
	public static final char PROJ_VIEW_ORTHOGRAPHIC = 'o';
	public static final char PROJ_VIEW_GNOMONIC = 'g';
	public static final char PROJ_VIEW_AZIMUTAL = 'a';
	public static final char PROJ_VIEW_HEXAGONAL = 'H';
	public static final char PROJ_VIEW_LONGLAT = 'L';

	public static final char PROJ_VIEW_PETERS = 'p';
	public static final char PROJ_VIEW_AREA_PRESERVING_CYLINDRICAL = 'p';

	public static final char PROJ_VIEW_SQUARE = 'q';
	public static final char PROJ_VIEW_EQUIDISTANT_LATITUDES = 'q';

	public void saveBiome(String s)
	{
		ImageCodec.saveBiome(this, s);
	}

	public void saveRainfall(String s)
	{
		ImageCodec.saveRainfall(this, s);
	}

	public void saveWaterflux(String s)
	{
		ImageCodec.saveWaterflux(this, s);
	}

	public void saveTemperature(String s)
	{
		ImageCodec.saveTemperature(this, s);
	}

	ExecutorService exec = null;
	List<Future> tList = new Vector();
	
	ColorRamp colorRamp = null;
	double colorRampSeaMin = -1.0;
	double colorRampLandMax = 1.0;

	boolean nonLinear = false;

	public boolean isThreaded() {
		return threaded;
	}

	public void setThreaded(boolean threaded) {
		this.threaded = threaded;
	}

	private boolean threaded=false;

	int threads = 16;

	double temperatureBase = -.0;
	double temperatureVariationFactor = .001;
	double temperatureVariationFrequency = 56.;

	double rainfallBase = -.0;
	double rainfallVariationFactor = .001;
	double rainfallVariationFrequency = 123.456;

	public static int min(int x, int y)
	{ 
		return(x<y ? x : y); 
	}

	public static int max(int x,int y)
	{ 
		return(x<y ? y : x); 
	}
	
	public static double rand2(double p, double q)
	{
		double r;
		r = (p+3.14159265)*(q+3.14159265);
		return(2.*(r-(int)r)-1.);
	}
	
	public static double rand2_PI(double p, double q)
	{
		double r;
		r = (p+Math.PI)*(q+Math.PI);
		return(2.*(r-(int)r)-1.);
	}
	
	public static double rand2_E(double p, double q)
	{
		double r;
		r = (p+Math.E)*(q+Math.E);
		return(2.*(r-(int)r)-1.);
	}
	
	public static double log_2(double x)
	{ 
		return(Math.log(x)/Math.log(2.0)); 
	}

	public BufferedImage makeRgbImage()
	{
		BufferedImage bufferedImage = new BufferedImage(Width, Height, BufferedImage.TYPE_INT_RGB);
		if(this.colorRamp == null)
		{
			for(int ix=0; ix<Width; ix++)
			{
				for(int iy=0; iy<Height; iy++)
				{
					int n = col[ix][iy];
					int[] c = null;
					if(n>=0x11000000)
					{
						c = new int [] { ((n>>>16)&0xff), ((n>>>8)&0xff), (n&0xff) };
					}
					else
					if(n==-2)
					{
						c = new int [] { 0, 0, 0 };
					}
					else
					{
						c = new int [] { rtable[n], gtable[n], btable[n]  };
						if(doshade)
						{
							int s = shades[ix][iy];
							for(int q=0; q<3; q++)
							{
								c[q] = s*c[q]/150;
								if (c[q]>255) c[q]=255;
							}
						}
					}
					bufferedImage.getRaster().setPixel(ix, iy, c);
				}
			}
		}
		else
		{
			double mh = .0, nh = .0;
			for(int ix=0; ix<Width; ix++)
			{
				for(int iy=0; iy<Height; iy++)
				{
					double h = this.heights[ix][iy];
					
					if(nh>h) nh = h;
					if(mh<h) mh = h;
					
					int n = col[ix][iy];
					int[] c = null;
					if(n>=0x11000000)
					{
						c = new int [] { ((n>>>16)&0xff), ((n>>>8)&0xff), (n&0xff) };
					}
					else
					if(latitudeColors && n==WHITE)
					{
						c = new int [] { rtable[n], gtable[n], btable[n]  };
					}
					else
					if(n<=BLACK)
					{
						c = new int [] { rtable[BLACK], gtable[BLACK], btable[BLACK]  };
					}
					else
					{
						Color color = this.colorRamp.mapHeight(h, this.getColorRampSeaMin(), this.getColorRampLandMax());
						c = new int [] { color.getRed(), color.getGreen(), color.getBlue() };
						if(doshade)
						{
							int s = shades[ix][iy];
							for(int q=0; q<3; q++)
							{
								c[q] = s*c[q]/150;
								if (c[q]>255) c[q]=255;
							}
						}
					}
					bufferedImage.getRaster().setPixel(ix, iy, c);
				}
			}

		}

		return bufferedImage;
	}

	public BufferedImage makeRgbImageH()
	{
		BufferedImage bufferedImage = new BufferedImage(Width, Height, BufferedImage.TYPE_INT_RGB);

		for(int ix=0; ix<Width; ix++)
		{
			for(int iy=0; iy<Height; iy++)
			{
				int n =((int) (heights[ix][iy]*4096));
				if(n < 0)
				{
					n = 0xee + n;
					if(n < 0) n = 0;
					int[] c = new int [] { 0, 0, (n|0x1f) };
					bufferedImage.getRaster().setPixel(ix, iy, c);
				}
				else
				{
					if(n > 255) n = 255;
					int[] c = new int [] { n, n, n };
					bufferedImage.getRaster().setPixel(ix, iy, c);
				}
			}
		}

		return bufferedImage;
	}

	public BufferedImage makeRgbImageHRidge()
	{
		BufferedImage bufferedImage = new BufferedImage(Width, Height, BufferedImage.TYPE_INT_RGB);

		for(int ix=0; ix<Width; ix++)
		{
			for(int iy=0; iy<Height; iy++)
			{
				if(!Double.isNaN(heights[ix][iy]))
				{
					int n =((int) (heights[ix][iy]*4096));
					if(n < -255)
					{
						bufferedImage.setRGB(ix, iy, Color.BLACK.getRGB());
					}
					else
					if(n < 0)
					{
						bufferedImage.setRGB(ix, iy, new Color(0,0, (-n) | 0x1f).getRGB());
					}
					else
					if(n > 255)
					{
						bufferedImage.setRGB(ix, iy, Color.WHITE.getRGB());
					}
					else
					{
						bufferedImage.setRGB(ix, iy, new Color(n, 255, n).getRGB());
					}
					if(ix == Width-1) continue;
					if(iy == Height-1) continue;

					int nx =((int) (heights[ix+1][iy]*4096));
					int ny =((int) (heights[ix][iy+1]*4096));

					if(n < 0 && ( nx<n || ny<n))
					{
						//bufferedImage.setRGB(ix, iy, new Color(bufferedImage.getRGB(ix, iy)).darker().getRGB());
					}
					else if (nx<n || ny<n)
					{
						bufferedImage.setRGB(ix, iy, new Color(bufferedImage.getRGB(ix, iy)).darker().getRGB());
					}
				}
			}
		}

		return bufferedImage;
	}

	public BufferedImage makeRgbImageOutline(int _p)
	{
		BufferedImage bufferedImage = new BufferedImage(Width, Height, BufferedImage.TYPE_INT_RGB);

		double _adj = 0;

		int[] cr = new int [] { 255, 0, 0 };
		int[] cw = new int [] { 255, 255, 255 };
		int[] cg = new int [] { 32, 32, 96 };
		int[] cb = new int [] { 0, 0, 0 };
		for(int ix=0; ix<Width; ix++)
		{
			for(int iy=0; iy<Height; iy++)
			{
				//if(col[ix][iy] <= BLACK)
				//{
				//	bufferedImage.getRaster().setPixel(ix, iy, cr);
				//	continue;
				//}

				double h00 = heights[ix][iy] +_adj;

				if(ix==0 || iy==0)
				{
					if(h00<=0.)
					{
						bufferedImage.getRaster().setPixel(ix, iy, cg);
					}
					else
					{
						bufferedImage.getRaster().setPixel(ix, iy, cw);
					}
					continue;
				}

				double h01 = heights[ix][iy-1] +_adj;
				double h10 = heights[ix-1][iy] +_adj;
				double h11 = heights[ix-1][iy-1] +_adj;
				if(h00>=0. && h01<0.)
				{
					bufferedImage.getRaster().setPixel(ix, iy, cb);
				}
				else
				if(h00<=0. && h01>0.)
				{
					bufferedImage.getRaster().setPixel(ix, iy, cb);
				}
				else
				if(h00>=0. && h11<0.)
				{
					bufferedImage.getRaster().setPixel(ix, iy, cb);
				}
				else
				if(h00<=0. && h11>0.)
				{
					bufferedImage.getRaster().setPixel(ix, iy, cb);
				}
				else
				if(h00>=0. && h10<0.)
				{
					bufferedImage.getRaster().setPixel(ix, iy, cb);
				}
				else
				if(h00<=0. && h10>0.)
				{
					bufferedImage.getRaster().setPixel(ix, iy, cb);
				}
				else
				if(h00<=0.)
				{
					bufferedImage.getRaster().setPixel(ix, iy, cg);
				}
				else
				{
					bufferedImage.getRaster().setPixel(ix, iy, cw);
				}
			}
		}

		return bufferedImage;
	}

	public BufferedImage makeRgbImageRainfall()
	{
		BufferedImage bufferedImage = new BufferedImage(Width, Height, BufferedImage.TYPE_INT_RGB);

		for(int ix=0; ix<Width; ix++)
		{
			for(int iy=0; iy<Height; iy++)
			{
				int n =((int)(rainfall[ix][iy]*2000.));
				if(n <= 0)
				{
					n = 0;
				}
				else
				if(n > 255)
				{
					n = 255;
				}
				else
				{
					n |= 0x1f;
				}
				int[] c = new int [] { n, n, n };
				if(Double.isNaN(heights[ix][iy]))
				{
					c = new int [] { 0, 0, 0 };
				}
				else if(heights[ix][iy] < 0.)
				{
				c = new int [] { 0, 0, 128 };
				}
				bufferedImage.getRaster().setPixel(ix, iy, c);
			}
		}

		return bufferedImage;
	}

	public BufferedImage makeRgbImageWaterFlux()
	{
		BufferedImage bufferedImage = new BufferedImage(Width, Height, BufferedImage.TYPE_INT_RGB);

		for(int ix=0; ix<Width; ix++)
		{
			for(int iy=0; iy<Height; iy++)
			{
				int n =((int) (waterFlux[ix][iy]/rainMax));
				if(n < 0) n = 0;
				if(n > 255) n = 255;
				int[] c = new int [] { n, n, n };
				if(Double.isNaN(heights[ix][iy]))
				{
					c = new int [] { 0, 0, 0 };
				}
				else if(heights[ix][iy] < 0.)
				{
					c = new int [] { 0, 0, 128 };
				}
				bufferedImage.getRaster().setPixel(ix, iy, c);
			}
		}

		return bufferedImage;
	}

	public BufferedImage makeRgbImageTemperature()
	{
		BufferedImage bufferedImage = new BufferedImage(Width, Height, BufferedImage.TYPE_INT_RGB);
		double tmin = 0, tmax = 0;
		for(int ix=0; ix<Width; ix++)
		{
			for(int iy=0; iy<Height; iy++)
			{
				double t = temperature[ix][iy]*300.;
				if(tmin>t) tmin = t;
				if(tmax<t) tmax = t;
				int n = 0;
				int[] c = null;
				double cScale = 5.;

				if(Double.isNaN(heights[ix][iy]))
				{
					c = new int [] { 0, 0, 0 };
				}
				else if(heights[ix][iy] < 0.)
				{
					if(t<0.)
					{
						c = new int [] { 0, 64, 64 };
					}
					else
					{
						c = new int [] { 0, 0, 64 };
					}
				}
				/*
				else if(t<0.)
				{
					n = ((int) (Math.abs(t)/cScale));
					if(n >= _COLD_COLORS.length-1) n = _COLD_COLORS.length-1;
					c = new int [] { _COLD_COLORS[n].getRed(), _COLD_COLORS[n].getGreen(), _COLD_COLORS[n].getBlue() };
				}
				else
				{
					n = ((int) (t/cScale));
					if(n >= _WARM_COLORS.length-1)
					{
						n = _WARM_COLORS.length-1;
						c = new int [] { _WARM_COLORS[n].getRed(), _WARM_COLORS[n].getGreen(), _WARM_COLORS[n].getBlue() };
					}
					else
					{
						//c = new int [] { _WARM_COLORS[n].getRed(), _WARM_COLORS[n].getGreen(), _WARM_COLORS[n].getBlue() };
						Color _c = ColorUtil.colorLerp(_WARM_COLORS[n], _WARM_COLORS[n + 1], ((t/cScale) - n));
						c = new int [] { _c.getRed(), _c.getGreen(),_c.getBlue() };
					}
				}
				*/
				else if(t<0.)
				{
					c = new int [] { 0, 255, 255 };
				}
				else if(t<10.)
				{
					c = new int [] { 255, 255, 255 };
				}
				else if(t<20.)
				{
					c = new int [] { 255, 255, 128 };
				}
				else if(t<30.)
				{
					c = new int [] { 255, 192, 128 };
				}
				else if(t<35.)
				{
					c = new int [] { 255, 128, 128 };
				}
				else
				{
					c = new int [] { 128, 64, 64 };
				}
				bufferedImage.getRaster().setPixel(ix, iy, c);
			}
		}
		System.err.printf("temp min/max = %f / %f\n", tmin, tmax);
		return bufferedImage;
	}

	static Color[] _WARM_COLORS = {
			new Color(53,235,64),
			new Color(252,254,129),
			new Color(255,112,0),
			new Color(248,0,0),
			new Color(192,0,0),
			new Color(128,0,0),
			new Color(64,0,0),
	};

	static Color[] _COLD_COLORS = {
			new Color(64,255,255),
			new Color(96,255,255),
			new Color(128,255,255),
			new Color(192,255,255),
			new Color(216,255,255),
			new Color(255,255,255),
	};

	public BufferedImage makeRgbImageTempAdj()
	{
		BufferedImage bufferedImage = new BufferedImage(Width, Height, BufferedImage.TYPE_INT_RGB);
		double tmin = 0, tmax = 0;
		for(int ix=0; ix<Width; ix++)
		{
			for(int iy=0; iy<Height; iy++)
			{
				Color _cl = ColorUtil.fromHSV(360. - (tempAdjust[ix][iy] * 3600.), 100f, 100f);
				int[] c = new int [] { _cl.getRed(), _cl.getGreen(),_cl.getBlue() };
				bufferedImage.getRaster().setPixel(ix, iy, c);
			}
		}
		return bufferedImage;
	}

	public BufferedImage makeRgbImageRainAdj()
	{
		BufferedImage bufferedImage = new BufferedImage(Width, Height, BufferedImage.TYPE_INT_RGB);
		double tmin = 0, tmax = 0;
		for(int ix=0; ix<Width; ix++)
		{
			for(int iy=0; iy<Height; iy++)
			{
				Color _cl = ColorUtil.fromHSV(210. + (rainAdjust[ix][iy] * 3600.), 100f, 100f);
				int[] c = new int [] { _cl.getRed(), _cl.getGreen(),_cl.getBlue() };
				bufferedImage.getRaster().setPixel(ix, iy, c);
			}
		}
		return bufferedImage;
	}

	public BufferedImage makeRgbImageBiome()
	{
		BufferedImage bufferedImage = new BufferedImage(Width, Height, BufferedImage.TYPE_INT_RGB);

		for(int ix=0; ix<Width; ix++)
		{
			for(int iy=0; iy<Height; iy++)
			{
				int[] c = null;
				Color _c = biomeColors.get(this.biome[ix][iy]);
				if(col[ix][iy]<=0)
				{
					_c=null;
				}
				if(_c == null)
				{
					c =  new int [] { 0, 0, 0 };
				}
				else if(temperature[ix][iy]<0.)
				{
					c =  new int [] { 192, 255, 255 };
				}
				else
				{
					c =  new int [] { _c.getRed(), _c.getGreen(), _c.getBlue() };
				}
				bufferedImage.getRaster().setPixel(ix, iy, c);
			}
		}

		return bufferedImage;
	}

	public BufferedImage makeRgbImagePertub(BufferedImage bufferedImage)
	{
		BufferedImage bufferedImageP = new BufferedImage(Width, Height, BufferedImage.TYPE_INT_RGB);
		INoise noise = RandyUtil.simplexNoise(0x1ee7b33f);
		double pscale = .2;
		double fscale = 5;
		for(int ix = 0; ix < Width; ix++)
		{
			for(int iy = 0; iy < Height; iy++)
			{
				int px, py;
				px = (int) ((ix - (noise.noise3(((double)ix)*pscale, ((double)iy)*pscale, 0) * fscale)) % Width);
				if(px<0) px+=Width;
				py = (int) ((iy - (noise.noise3(((double)ix)*pscale + 1.5, ((double)iy)*pscale + 6.7, 0 + 3.4) * fscale)) % Height);
				if(py<0)
				{
					py = -py;
					px = (Width-px) % Width;
				}
				int[] c = bufferedImage.getRaster().getPixel(px, py, new int[3]);
				bufferedImageP.getRaster().setPixel(ix, iy, c);
			}
		}
		return bufferedImageP;
	}
	
	public void save(String f)
	{
		ImageCodec.saveAs(f, makeRgbImage());
	}

	public void saveH(String f)
	{
		BufferedImage bufferedImage = makeRgbImageH();

		String image_type = "jpg";
		if(f.toLowerCase().endsWith(".jpg") || f.toLowerCase().endsWith(".jpeg"))
		{
			image_type = "jpg";
		}
		else if(f.toLowerCase().endsWith(".png"))
		{
			image_type = "png";
		}
		else if(f.toLowerCase().endsWith(".gif"))
		{
			image_type = "gif";
		}
		else
		{
			throw new IllegalArgumentException("Unknown image extension: "+f);
		}

		try
		{
			BufferedOutputStream fh = new BufferedOutputStream(new FileOutputStream(new File(f)), 8192<<8);
			ImageIO.write(bufferedImage, image_type, fh);
			fh.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@SneakyThrows
	public void saveHRidge(String f)
	{
		BufferedImage bufferedImage = makeRgbImageHRidge();

		String image_type = "jpg";
		if(f.toLowerCase().endsWith(".jpg") || f.toLowerCase().endsWith(".jpeg"))
		{
			image_type = "jpg";
		}
		else if(f.toLowerCase().endsWith(".png"))
		{
			image_type = "png";
		}
		else if(f.toLowerCase().endsWith(".gif"))
		{
			image_type = "gif";
		}
		else
		{
			throw new IllegalArgumentException("Unknown image extension: "+f);
		}

		try
		{
			BufferedOutputStream fh = new BufferedOutputStream(Files.newOutputStream(new File(f).toPath()), 8192<<8);
			ImageIO.write(bufferedImage, image_type, fh);
			fh.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}


	public void save_VTP_BT(String f)
	{
		try
		{
			double z[] = new double[this.Height*this.Width];
			for(int iy=0; iy<this.Height; iy++)
			{
				for(int ix=0; ix<this.Width; ix++)
				{
					z[((this.Height-(iy+1))*this.Width)+ix] = this.heights[ix][iy]*(65535.0/2.0);
				}
			}
			BinaryRasterCodec.writeVTPBT(new File(f), this.Width, this.Height, this.baseLongitude, this.baseLatitude, z);
		}
		catch(Exception xe)
		{
			xe.printStackTrace();
		}
		
	}

	public void save_GXF0(String f, double _scale)
	{
		save_GXF0(f, _scale, true);
	}

	public void save_GXF0(String f, double _scale, boolean _onlyLand)
	{
		if(_scale<0.) _scale = 65536.;
		try
		{
			File outFile = new File(f);
			PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream(outFile), 8192<<8));
			
			double sx = (360.0/(this.Width))/scale;
			double sy = sx;
			double sr = (180.0/(this.Height))/scale;
			
			out.printf("#POINTS\n%d\n\n", this.Width);
			out.printf("#ROWS\n%d\n\n", this.Height);
			out.printf("#PTSEPARATION\n%.21f\n\n", sx);
			out.printf("#RWSEPARATION\n%.21f\n\n", sy);
			out.print("#SENSE\n-2\n\n");
			out.print("#UNIT_LENGTH\ndeg\n\n");
			out.printf("#XORIGIN\n%.21f\n\n", (-sr*this.Height)+(this.baseLongitude*180.0/PI)+sr);
			//out.printf("#YORIGIN\n%.21f\n\n", (sr*this.Width)+((this.baseLatitude*180.0/PI)*2.0)-sr);
			out.printf("#YORIGIN\n%.21f\n\n", 90.-sr);

			out.print("#GRID\n");
			for(int iy=0; iy<this.Height; iy++)
			{
				for(int ix=0; ix<this.Width; ix++)
				{
					if(_onlyLand && this.heights[ix][iy]<0.)
					{
						out.printf(" %.5f ", -1f);
					}
					else
					{
						out.printf(" %.5f ", (float) this.heights[ix][iy]*_scale);
					}
				}
				out.print("\n");
			}
			
			out.close();
		}
		catch(Exception xe)
		{
			xe.printStackTrace();
		}
	}

	public void save_GXF(String f, double _scale)
	{
		save_GXF0(f ,_scale, false);
	}

	public void save_TER(String f)
	{
		try
		{
			File outFile = new File(f);
			double z[] = new double[this.Height*this.Width];
			for(int iy=0; iy<this.Height; iy++)
			{
				for(int ix=0; ix<this.Width; ix++)
				{
					z[((this.Height-(iy+1))*this.Width)+ix] = this.heights[ix][iy]*(65535.0/2.0);
				}
			}
			TerragenCodec.writeTER(outFile, this.Width, this.Height, z);
		}
		catch(Exception xe)
		{
			xe.printStackTrace();
		}
	}

	public void save_MDR(String f, double _scale)
	{
		if(_scale<0.) _scale = 65535.0;
		try
		{
			File outFile = new File(f);

			double sr = 30; //this.calcPointSeparation();

			double _z[] = new double[this.Height*this.Width];
			double _min = 0;
			double _max = 0;
			//out.printf("#XORIGIN\n%.21f\n\n", (-sr*this.Height)+(this.baseLongitude*180.0/PI)+sr);
			//out.printf("#YORIGIN\n%.21f\n\n", (sr*this.Width)+((this.baseLatitude*180.0/PI)*2.0)-sr);
			int _i=0;
			for(int iy=0; iy<this.Height; iy++)
			{
				for(int ix=0; ix<this.Width; ix++)
				{
					_z[_i]=this.heights[ix][this.Height-iy-1]*_scale;
					if(_z[_i]>_max) _max=_z[_i];
					if(_z[_i]<=_min) _min=_z[_i];
					_i++;
				}
			}

			WilburMdrCodec.writeMDR(outFile,this.Width, this.Height, sr, _min, _max, _z);
		}
		catch(Exception xe)
		{
			xe.printStackTrace();
		}
	}

	public double calcPointSeparation()
	{
		return (360.0/(this.Width))/scale;
	}
	
	public double calcDegreeSeparation()
	{
		return (180.0/(this.Height))/scale;
	}
	
	public double calcXOrigin()
	{
		double sr = this.calcDegreeSeparation();
		return (-sr*this.Height)+(this.baseLongitude*180.0/PI)+sr;
	}
	
	public double calcYOrigin()
	{
		double sr = this.calcDegreeSeparation();
		return (sr*this.Width)+((this.baseLatitude*180.0/PI)*2.0)-sr;
	}

	public double pixelHeight(int x, int y)
	{
		if(x<0) x=0;
		if(x>=this.Width) x=this.Width-1;
		
		if(y<0) y=0;
		if(y>=this.Height) y=this.Height-1;
		
		return this.heights[x][y];
	}

	public void pixelHeight(int x, int y, double h)
	{
		if(x<0) x=0;
		if(x>=this.Width) x=this.Width-1;
		
		if(y<0) y=0;
		if(y>=this.Height) y=this.Height-1;
		
		this.heights[x][y] = h;
	}
	
	public void morphologicalErode()
	{
		double z[] = new double[this.Height*this.Width];
		for(int iy=0; iy<this.Height; iy++)
		{
			for(int ix=0; ix<this.Width; ix++)
			{
				int off = (iy*this.Width)+ix;
				z[off] = this.pixelHeight(ix, iy);
				for(int dy=-1; dy<2; dy++)
				{
					for(int dx=-1; dx<2; dx++)
					{
						if(z[off] > this.pixelHeight(dx+ix, dy+iy)) z[off] = this.pixelHeight(dx+ix, dy+iy);
					}
				}
			}
		}
		for(int iy=0; iy<this.Height; iy++)
		{
			for(int ix=0; ix<this.Width; ix++)
			{
				int off = (iy*this.Width)+ix;
				this.pixelHeight(ix, iy, z[off]);
			}
		}
	}
	
	public void saveTXT(String f)
	{
		try
		{
			File outFile = new File(f);
                PrintStream out = new PrintStream(outFile);
                out.println(this.Height);
                out.println(this.Width);
                for(double[] row : this.heights)
                {
                    for(double c : row)
                    {
                        out.println(c*125000.0);
                    }
                }
                out.close();
            }
            catch(Exception xe)
            {
                xe.printStackTrace();
            }
	}

	long start;

	public static final double PI = 3.14159265358979;
	public static final double PI_2 = PI/2.0;
	public static final double DEG2RAD = 0.0174532918661; /* pi/180 */
	
	public static final int BLACK = 0;
	public static final int WHITE = 1;
	public static final int BLUE0 = 2;

	public static final int MAXCOL =	9;
	
	public static int colors[][] =
	{{0,0,255},	    /* Dark blue depths		*/
		{0,128,255},   /* Light blue shores	*/
		{0,255,0},	    /* Light green lowlands	*/
		{64,192,16},   /* Dark green highlands	*/
		{64,192,16},   /* Dark green Mountains	*/
		{128,128,32},  /* Brown stoney peaks	*/
		{255,255,255}, /* White - peaks		*/
		{0,0,0},	    /* Black - Space		*/
		{0,0,0}};	    /* Black - Lines		*/

	public static final int std_colors[][] =
	{{0,0,255},	    /* Dark blue depths		*/
		{0,128,255},   /* Light blue shores	*/
		{0,255,0},	    /* Light green lowlands	*/
		{64,192,16},   /* Dark green highlands	*/
		{64,192,16},   /* Dark green Mountains	*/
		{128,128,32},  /* Brown stoney peaks	*/
		{255,255,255}, /* White - peaks		*/
		{0,0,0},	    /* Black - Space		*/
		{0,0,0}};	    /* Black - Lines		*/

	public static final int alt_colors[][] =
	{{0,0,192},	    /* Dark blue depths		*/
		{0,128,255},   /* Light blue shores	*/
		{0,96,0},	    /* Dark green Lowlands	*/
		{0,224,0},	    /* Light green Highlands	*/
		{128,176,0},   /* Brown mountainsides	*/
		{128,128,128}, /* Grey stoney peaks	*/
		{255,255,255}, /* White - peaks		*/
		{0,0,0},	    /* Black - Space		*/
		{0,0,0}};	    /* Black - Lines		*/

	public static double moll_table[] = {0.0, 0.0685055811, 0.1368109534, 0.2047150027,
		0.2720147303, 0.3385041213, 0.4039727534,
		0.4682040106, 0.5309726991, 0.5920417499,
		0.6511575166, 0.7080428038, 0.7623860881,
		0.8138239166, 0.8619100185, 0.9060553621,
		0.9453925506, 0.9783738403, 1.0, 1.0};


	/* these values can be changed to change world characteristica */

	//public static double initialAltitude  = -.02;   /* initial altitude (slightly below sea level) */
	public double initialAltitude  = -.011;   /* initial altitude (slightly below sea level) */
	private double altitudeAdjustment = 0.;

	public double altitudeWeight = 0.4;   /* weight for altitude difference */
	public double distanceWeight = 0.03;  /* weight for distance */
	public boolean debug;
	public boolean useAlternativeColors;
	public double baseLongitude,baseLatitude,scale;
	public double vgrid, hgrid;
	public char view;
	public int nocols = 256;
	public int lighter = 0; /* specifies lighter colours */
	public boolean latitudeColors; /* flag for latitude based colour */
	public boolean do_outline;
	public boolean do_bw;
	public boolean doshade;
	public boolean doWaterShade;
	public int waterShade = 128;
	public int Depth; /* depth of subdivisions */
	public double r1,r2,r3,r4; /* seeds */
	public double cla, sla, clo, slo;


	public int Width = 800, Height = 600;

	public int col[][];
	public double heights[][];
	public double temperature[][];
	public double tempAdjust[][];
	public double rainfall[][];
	public double rainAdjust[][];
	public double waterFlux[][];
	public char biome[][];

	double tempMin = 1000.0, tempMax = -1000.0;
	double rainMin = 1000.0, rainMax = -1000.0;
	double rainShadow = 0.0; /* approximate rain shadow */

	public double powA = 1.0;
	public double powD = .47;
	public int wrinkleContribution = 0;
	public int shade;
	public int shades[][];
	public double shadeAngle = 150.0; /* angle of "light" on bumpmap */
	public double rseed, increment = 0.00000001;


	public int BLUE1, LAND0, LAND1, LAND2, LAND4;
	public int GREEN1, BROWN0, GREY0;

	public int BACK = -2;

	public int rtable[] = new int[256], gtable[] = new int[256], btable[] = new int[256];

	public int best = 500000;
	public int weight[]= new int[30];
	public String filename;
	public String colorsname;

	public String world_file;

	public String ter_file;

	public static HashMap<Character,Color> biomeColors = new HashMap<>();
	public static HashMap<Character,String> biomeText = new LinkedHashMap<>();

	//makeBiomes = Boolean.parseBoolean(prop.getProperty("-z", prop.getProperty("make-biomes", "false")));
	//if(makeBiomes)
	static {
		biomeText.put('I', "Icecap");
		biomeText.put('T', "Tundra");
		biomeText.put('B', "Taiga & Boreal Forest");
		biomeText.put('G', "Grasslands");
		biomeText.put('R', "Temperate Rainforest");
		biomeText.put('F', "Temperate Forest");
		biomeText.put('W', "Xeric shrubland and dry forest");
		biomeText.put('D', "Desert");
		biomeText.put('S', "Savanna");
		biomeText.put('E', "Tropical Dry Forest");
		biomeText.put('O', "Tropical Rainforest");
		/* make biome colours */
		biomeColors.put('T', new Color(210,210,210));
		biomeColors.put('G', new Color(250,215,165));
		biomeColors.put('B', new Color(105,155,120));
		biomeColors.put('D', new Color(220,195,175));
		biomeColors.put('S', new Color(225,155,100));
		biomeColors.put('F', new Color(155,215,170));
		biomeColors.put('R', new Color(170,195,200));
		biomeColors.put('W', new Color(185,150,160));
		biomeColors.put('E', new Color(130,190,25));
		biomeColors.put('O', new Color(110,160,170));
		biomeColors.put('I', new Color(255,255,255));
		/*water*/
		biomeColors.put('*', new Color(32,64,192));
		//biomeColors.put('*', new Color(64,128,255));
	}

	vertex[] tetra = new vertex[4];

	public void init()
	{
		init(new Properties());
	}
	
	public void init(Properties prop)
	{
		{/* initialize vertices to slightly irregular tetrahedron */
			tetra[0] = new vertex();
			tetra[0].h = this.initialAltitude;
			tetra[0].x = 0.; // -Math.sqrt(3.0)-0.20;
			tetra[0].y = 0.; //-Math.sqrt(3.0)-0.22;
			tetra[0].z = 3.01; //-Math.sqrt(3.0)-0.23;

			tetra[1] = new vertex();
			tetra[1].h = this.initialAltitude;
			tetra[1].x = 0.; // -Math.sqrt(3.0)-0.19;
			tetra[1].y = Math.sqrt(8.0)+.01;// Math.sqrt(3.0)+0.18;
			tetra[1].z = -1.01; // Math.sqrt(3.0)+0.17;

			tetra[2] = new vertex();
			tetra[2].h = this.initialAltitude;
			tetra[2].x = -Math.sqrt(6.0)-.01;// Math.sqrt(3.0)+0.21;
			tetra[2].y = -Math.sqrt(2.0)-.01; // -Math.sqrt(3.0)-0.24;
			tetra[2].z = -1.01; // Math.sqrt(3.0)+0.15;

			tetra[3] = new vertex();
			tetra[3].h = this.initialAltitude;
			tetra[3].x = Math.sqrt(6.0)-.005; // Math.sqrt(3.0)+0.24;
			tetra[3].y = -Math.sqrt(2.0)-.005; // Math.sqrt(3.0)+0.22;
			tetra[3].z = -1.005; // -Math.sqrt(3.0)-0.25;

			tetra[0].shadow = 0.0;
			tetra[1].shadow = 0.0;
			tetra[2].shadow = 0.0;
			tetra[3].shadow = 0.0;

		}

		initialAltitude   = Double.parseDouble(prop.getProperty("-i", prop.getProperty("initial-altitude", "-.015")));

		altitudeWeight = Double.parseDouble(prop.getProperty("-v", prop.getProperty("altitude-weight",  "0.4")));
		
		distanceWeight = Double.parseDouble(prop.getProperty("-V", prop.getProperty("distance-weight",  "0.03")));

		debug = Boolean.parseBoolean(prop.getProperty("-X", prop.getProperty("debug", "false")));

		rseed = Double.parseDouble(prop.getProperty("-s", prop.getProperty("seed", ""+(new Random().nextDouble()))));

		Width=Integer.parseInt(prop.getProperty("-w", prop.getProperty("width", "512")));

		Height=Integer.parseInt(prop.getProperty("-h", prop.getProperty("height", "384")));
		
		scale = Double.parseDouble(prop.getProperty("-m", prop.getProperty("magnification", "1.0")));

		filename = prop.getProperty("-o", prop.getProperty("out-file", "false"));

		useAlternativeColors = Boolean.parseBoolean(prop.getProperty("-a", prop.getProperty("alt-colors", "false")));
		
		colorsname = prop.getProperty("-initialAltitude", prop.getProperty("map-file", "false"));
		world_file = prop.getProperty("-W", prop.getProperty("world-file", "false"));
		ter_file = prop.getProperty("-T", prop.getProperty("ter-file", "false"));
		
		baseLongitude = Double.parseDouble(prop.getProperty("-l", prop.getProperty("longitude", "0.0")));

		baseLatitude = Double.parseDouble(prop.getProperty("-L", prop.getProperty("latitude", "0.0")));

		vgrid = Double.parseDouble(prop.getProperty("-g", prop.getProperty("vgrid", "0.0")));

		hgrid = Double.parseDouble(prop.getProperty("-G", prop.getProperty("hgrid", "0.0")));

		latitudeColors = Boolean.parseBoolean(prop.getProperty("-c", prop.getProperty("latitude-colors", "false")));
		
		if(Boolean.parseBoolean(prop.getProperty("-E", prop.getProperty("edge", "false"))))
		{
			do_outline = true;
		}

		if(Boolean.parseBoolean(prop.getProperty("-O", prop.getProperty("outline", "false"))))
		{
			do_outline = true;
			do_bw = true;
		}

		if(Boolean.parseBoolean(prop.getProperty("-C", prop.getProperty("lighter", "false"))))
		{
			lighter += 1;
		}

		nocols = Integer.parseInt(prop.getProperty("-N", prop.getProperty("color-number", "32")));
		
		if (nocols<5) nocols = 5;
		if (nocols>256) nocols = 256;
		
		doshade = Boolean.parseBoolean(prop.getProperty("-B", prop.getProperty("shade", "false")));
		
		shadeAngle = Double.parseDouble(prop.getProperty("-A", prop.getProperty("shade-angle", "150.0")));

		view = prop.getProperty("-p", prop.getProperty("projection", "mercator")).charAt(0);

		wrinkleContribution = Integer.parseInt(prop.getProperty("-S", prop.getProperty("wrinkle-contribution", "-1")));
	}

	public void setup()
	{
		if(useAlternativeColors)
		{ 
			copyColors(alt_colors);
		}
		else
		{
			copyColors(std_colors);
		}
		
		if (baseLongitude>180) baseLongitude -= 360;
		
		baseLongitude = baseLongitude*DEG2RAD;
		baseLatitude = baseLatitude*DEG2RAD;

		sla = Math.sin(baseLatitude); 
		cla = Math.cos(baseLatitude);
		slo = Math.sin(baseLongitude); 
		clo = Math.cos(baseLongitude);

		heights = new double[Width][Height];
		temperature = new double[Width][Height];
		tempAdjust = new double[Width][Height];
		rainfall = new double[Width][Height];
		rainAdjust = new double[Width][Height];
		waterFlux = new double[Width][Height];
		biome = new char[Width][Height];
		col = new int[Width][Height];
		shades = new int[Width][Height];
		for(int ix=0; ix<Width; ix++)
		{
			for(int iy=0; iy<Height; iy++)
			{
				col[ix][iy]=-1;
			}
		}

		if (view == PROJ_VIEW_CONICAL) {
			if (baseLatitude == 0.0) view = PROJ_VIEW_MERCATOR;
			/* Conical approaches mercator when baseLatitude -> 0 */
			if (Math.abs(baseLatitude) >= PI - 0.000001) view = PROJ_VIEW_STEREOGRAPHIC;
			/* Conical approaches stereo when baseLatitude -> +/- 90 */
		}

		setcolours();

		for(int i=this.wrinkleContribution; i>0; i--)
		{
			altitudeWeight *= 2.0;
			powA /= 0.75;
		}

		Depth = 3*((int)(log_2(scale*Height)))+8;
		
		r1 = rseed;

		r1 = rand2(r1,r1);
		r2 = rand2(r1,r1);
		r3 = rand2(r1,r2);
		r4 = rand2(r2,r3);

		tetra[0].s = r1;
		tetra[1].s = r2;
		tetra[2].s = r3;
		tetra[3].s = r4;

		if(this.threaded)
		{
			exec = Executors.newFixedThreadPool(this.threads);
		}

		if(this._fractalOverlay!=null)
		{
			this._fractalContext = PlanetJProjectionContext.create(this, true);
		}
	}




	public void process()
	{
            
		start=System.currentTimeMillis();

		boolean[] _bl = { true, false};

		for(boolean _b : _bl)
		{
			switch (view) {

				case PROJ_VIEW_MERCATOR: /* Mercator projection */
					mercator(_b);
					break;

				case PROJ_VIEW_AREA_PRESERVING_CYLINDRICAL: /* Peters projection (area preserving cylindrical) */
					peter(_b);
					break;

				case PROJ_VIEW_EQUIDISTANT_LATITUDES: /* Square projection (equidistant latitudes) */
					squarep(_b);
					break;

				case PROJ_VIEW_MOLLWEIDE: /* Mollweide projection (area preserving) */
					mollweide(_b);
					break;

				case PROJ_VIEW_SINUSOID: /* Sinusoid projection (area preserving) */
					sinusoid(_b);
					break;

				case PROJ_VIEW_STEREOGRAPHIC: /* Stereographic projection */
					stereo(_b);
					break;

				case PROJ_VIEW_ORTHOGRAPHIC: /* Orthographic projection */
					orthographic(_b);
					break;

				case PROJ_VIEW_GNOMONIC: /* Gnomonic projection */
					gnomonic(_b);
					break;

				case PROJ_VIEW_AZIMUTAL: /* Area preserving azimuthal projection */
					azimuth(_b);
					break;

				case PROJ_VIEW_HEXAGONAL: /* hexagonal gamified */
					hexagonal(_b);
					break;

				case PROJ_VIEW_LONGLAT:
					longitudeLatitude(_b);
					break;

				default:
					mercator(_b);
					break;

			}
		}

		long s2 = System.currentTimeMillis();
		System.out.println("took = "+(s2-start));
		if(this.threaded)
		{
			exec.shutdownNow();
		}

		if (do_outline) makeoutline(do_bw);

		if (doshade) smoothshades();


		double _nh = Double.MAX_VALUE;
		double _mh = Double.MIN_VALUE;
		double _cw = 0;
		double _cl = 0;
		for(int ix=0; ix<Width; ix++)
		{
			for(int iy=0; iy<Height; iy++)
			{
				double _h = heights[ix][iy];

				if(_nh>_h) _nh=_h;
				if(_mh<_h) _mh=_h;

				if(_h<0.)
				{
					_cw++;
				}
				else
				{
					_cl++;
				}
			}
		}
		int _hc = (int) (100*(_cw/(Width*Height)));

		System.err.println(String.format("sea/land => %f / %f = %d%%",_nh,_mh,_hc));

	}
	
	void makeoutline(boolean doBw)
	{
		int i,j,k;

		int[] outx = new int[Width*Height];
		int[] outy = new int[Width*Height];


		if (doBw)
		{
			for (i=0; i<Width; i++)
			{
				for (j=0; j<Height; j++)
				{
					if(col[i][j] > BLACK)
					{
						col[i][j] = ( col[i][j] >= LAND0 ) ? (GREY0+nocols)/2 : WHITE;
					}
				}
			}
		}
		else
		{
			k=0;
			for (i=1; i<Width-1; i++)
			{
				for (j=1; j<Height-1; j++)
				{
					if ((col[i][j] >= BLUE0 && col[i][j] <= BLUE1) &&
							(col[i-1][j] >= LAND0 || col[i+1][j] >= LAND0 ||
							col[i][j-1] >= LAND0 || col[i][j+1] >= LAND0 ||
							col[i-1][j-1] >= LAND0 || col[i-1][j+1] >= LAND0 ||
							col[i+1][j-1] >= LAND0 || col[i+1][j+1] >= LAND0)) 
					{
						outx[k] = i; outy[k++] =j;
					}
				}
			}
			while (k-->0) col[outx[k]][outy[k]] = BLACK;
		}
	}
	
	public void smoothshades()
	{
		int i,j;

		for (i=0; i<Width-2; i++)
			for (j=0; j<Height-2; j++)
				shades[i][j] = (4*shades[i][j]+2*shades[i][j+1]
				             +2*shades[i+1][j]+shades[i+1][j+2]+4)/9;
	}
	
	void copyColors(int cTable[][])
	{
		int x, y;

		for (x = 0; x< MAXCOL; x++)
		{
			for (y = 0; y < 3; y++)
			{
				colors[x][y] = cTable[x][y];
			}
		}
	}
	
	void setcolours()
	{
		int i;

		if (useAlternativeColors) {
			int	    crow;

			if (nocols < 8)
				nocols = 8;

			/*
			 *	This color table tries to follow the coloring conventions of
			 *	several atlases.
			 *
			 *	The first two colors are reserved for black and white
			 *	1/4 of the colors are blue for the sea, dark being deep
			 *	3/4 of the colors are land, divided as follows:
			 *	 nearly 1/2 of the colors are greens, with the low being dark
			 *	 1/8 of the colors shade from green through brown to grey
			 *	 1/8 of the colors are shades of grey for the highest altitudes
			 *
			 *	The minimum color table is:
			 *	    0	Black
			 *	    1	White
			 *	    2	Blue
			 *	    3	Dark Green
			 *	    4	Green
			 *	    5	Light Green
			 *	    6	Brown
			 *	    7	Grey
			 *	and doesn't look very good. Somewhere between 24 and 32 colors
			 *	is where this scheme starts looking good. 256, of course, is best.
			 */

			LAND0 = max(nocols / 4, BLUE0 + 1);
			BLUE1 = LAND0 - 1;
			GREY0 = nocols - (nocols / 8);
			GREEN1 = min(LAND0 + (nocols / 2), GREY0 - 2);
			BROWN0 = (GREEN1 + GREY0) / 2;
			LAND1 = nocols - 1;

			if(nocols > (rtable.length+1))
			{
				rtable = new int[nocols];
				gtable = new int[nocols];
				btable = new int[nocols];
			}

			rtable[BLACK] = colors[7][0];
			gtable[BLACK] = colors[7][0];
			btable[BLACK] = colors[7][0];

			rtable[WHITE] = colors[6][0];
			gtable[WHITE] = colors[6][1];
			btable[WHITE] = colors[6][2];

			rtable[BLUE0] = colors[0][0];
			gtable[BLUE0] = colors[0][1];
			btable[BLUE0] = colors[0][2];

			for (i=BLUE0+1;i<=BLUE1;i++) {
				rtable[i] = (colors[0][0]*(BLUE1-i)+colors[1][0]*(i-BLUE0))/(BLUE1-BLUE0);
				gtable[i] = (colors[0][1]*(BLUE1-i)+colors[1][1]*(i-BLUE0))/(BLUE1-BLUE0);
				btable[i] = (colors[0][2]*(BLUE1-i)+colors[1][2]*(i-BLUE0))/(BLUE1-BLUE0);
			}
			for (i=LAND0;i<GREEN1;i++) {
				rtable[i] = (colors[2][0]*(GREEN1-i)+colors[3][0]*(i-LAND0))/(GREEN1-LAND0);
				gtable[i] = (colors[2][1]*(GREEN1-i)+colors[3][1]*(i-LAND0))/(GREEN1-LAND0);
				btable[i] = (colors[2][2]*(GREEN1-i)+colors[3][2]*(i-LAND0))/(GREEN1-LAND0);
			}
			for (i=GREEN1;i<BROWN0;i++) {
				rtable[i] = (colors[3][0]*(BROWN0-i)+colors[4][0]*(i-GREEN1))/(BROWN0-GREEN1);
				gtable[i] = (colors[3][1]*(BROWN0-i)+colors[4][1]*(i-GREEN1))/(BROWN0-GREEN1);
				btable[i] = (colors[3][2]*(BROWN0-i)+colors[4][2]*(i-GREEN1))/(BROWN0-GREEN1);
			}
			for (i=BROWN0;i<GREY0;i++) {
				rtable[i] = (colors[4][0]*(GREY0-i)+colors[5][0]*(i-BROWN0))/(GREY0-BROWN0);
				gtable[i] = (colors[4][1]*(GREY0-i)+colors[5][1]*(i-BROWN0))/(GREY0-BROWN0);
				btable[i] = (colors[4][2]*(GREY0-i)+colors[5][2]*(i-BROWN0))/(GREY0-BROWN0);
			}
			for (i=GREY0;i<nocols;i++) {
				rtable[i] = (colors[5][0]*(nocols-i)+(colors[6][0]+1)*(i-GREY0))/(nocols-GREY0);
				gtable[i] = (colors[5][1]*(nocols-i)+(colors[6][1]+1)*(i-GREY0))/(nocols-GREY0);
				btable[i] = (colors[5][2]*(nocols-i)+(colors[6][2]+1)*(i-GREY0))/(nocols-GREY0);
			}
		} else {
			rtable[BLACK] = 0;
			gtable[BLACK] = 0;
			btable[BLACK] = 0;

			rtable[WHITE] = 255;
			gtable[WHITE] = 255;
			btable[WHITE] = 255;

			while (lighter-->0) {
				int r, c;
				double x;

				for (r =	0; r < 7; r++)
					for (c = 0; c < 3; c++) {
						x = Math.sqrt((double)colors[r][c]/256.0);
						colors[r][c] = (int)(240.0*x+16);
					}
			}

			BLUE1 = (nocols-4)/2+BLUE0;
			if (BLUE1==BLUE0) {
				rtable[BLUE0] = colors[0][0];
				gtable[BLUE0] = colors[0][1];
				btable[BLUE0] = colors[0][2];
			} else
				for (i=BLUE0;i<=BLUE1;i++) {
					rtable[i] = (colors[0][0]*(BLUE1-i)+colors[1][0]*(i-BLUE0))/(BLUE1-BLUE0);
					gtable[i] = (colors[0][1]*(BLUE1-i)+colors[1][1]*(i-BLUE0))/(BLUE1-BLUE0);
					btable[i] = (colors[0][2]*(BLUE1-i)+colors[1][2]*(i-BLUE0))/(BLUE1-BLUE0);
				}
			LAND0 = BLUE1+1; LAND2 = nocols-2; LAND1 = (LAND0+LAND2+1)/2;
			for (i=LAND0;i<LAND1;i++) {
				rtable[i] = (colors[2][0]*(LAND1-i)+colors[3][0]*(i-LAND0))/(LAND1-LAND0);
				gtable[i] = (colors[2][1]*(LAND1-i)+colors[3][1]*(i-LAND0))/(LAND1-LAND0);
				btable[i] = (colors[2][2]*(LAND1-i)+colors[3][2]*(i-LAND0))/(LAND1-LAND0);
			}
			if (LAND1==LAND2) {
				rtable[LAND1] = colors[4][0];
				gtable[LAND1] = colors[4][1];
				btable[LAND1] = colors[4][2];
			} else
				for (i=LAND1;i<=LAND2;i++) {
					rtable[i] = (colors[4][0]*(LAND2-i)+colors[5][0]*(i-LAND1))/(LAND2-LAND1);
					gtable[i] = (colors[4][1]*(LAND2-i)+colors[5][1]*(i-LAND1))/(LAND2-LAND1);
					btable[i] = (colors[4][2]*(LAND2-i)+colors[5][2]*(i-LAND1))/(LAND2-LAND1);
				}
			LAND4 = nocols-1;
			rtable[LAND4] = colors[6][0];
			gtable[LAND4] = colors[6][1];
			btable[LAND4] = colors[6][2];
		}
	}

	public int alt2color(double alt, double x, double y, double z)
	{
		int colour;

		if (useAlternativeColors)
		{
			double snow = .125;
			double tree = snow * 0.5;
			double bare = (tree + snow) / 2.;

			if (latitudeColors) {
				snow -= (.13 * (y*y*y*y*y*y));
				bare -= (.12 * (y*y*y*y*y*y));
				tree -= (.11 * (y*y*y*y*y*y));
			}

			if (alt > 0) {		    /* Land */
				if (alt > snow) {		    /* Snow: White */
					colour = WHITE;
				} else if (alt > bare) {	    /* Snow: Grey - White */
					colour = GREY0+(int)((1+LAND1-GREY0) *
							(alt-bare)/(snow-bare));
					if (colour > LAND1) colour = LAND1;
				} else if (alt > tree) {	    /* Bare: Brown - Grey */
					colour = GREEN1+(int)((1+GREY0-GREEN1) *
							(alt-tree)/(bare-tree));
					if (colour > GREY0) colour = GREY0;
				} else {			    /* Green: Green - Brown */
					colour = LAND0+(int)((1+GREEN1-LAND0) *
							(alt)/(tree));
					if (colour > GREEN1) colour = GREEN1;
				}
			} else {			    /* Sea */
				alt = alt/2;
				if (alt > snow) {		    /* Snow: White */
					colour = WHITE;
				} else if (alt > bare) {
					colour = GREY0+(int)((1+LAND1-GREY0) *
							(alt-bare)/(snow-bare));
					if (colour > LAND1) colour = LAND1;
				} else {
					colour = BLUE1+(int)((BLUE1-BLUE0+1)*(25*alt));
					if (colour<BLUE0) colour = BLUE0;
				}
			}
		} else {
			/* calculate colour */
			if (alt <=0.) { /* if below sea level then */
				if (latitudeColors && y*y+alt >= 0.98)
					colour = LAND4;	 /* white if close to poles */
				else {
					colour = BLUE1+(int)((BLUE1-BLUE0+1)*(10*alt));	  /* blue scale otherwise */
					if (colour<BLUE0) colour = BLUE0;
				}
			}
			else {
				if (latitudeColors) alt += 0.10204*y*y;  /* altitude adjusted with latitude */
				if (alt >= 0.1) /* if high then */
					colour = LAND4;
				else {
					colour = LAND0+(int)((LAND2-LAND0+1)*(10*alt));
					/* else green to brown scale */
					if (colour>LAND2) colour = LAND2;
				}
			}
		}
		return(colour);
	}
	
	public void tick(int i) {
		System.err.println("- TICK:"+new Date()+" finished = "+i);
	}
	
	public void tickH(int j) {
		long curr=System.currentTimeMillis();
		
		if((j % (Height/10))==0)
		{
			System.err.println("- "+(j*100/Height)+"% ETA:"+new Date(System.currentTimeMillis()+((curr-start)*(Height-j+1)/(j+1))));
		}
	}
	
	public void tickW(int j) {
		long curr=System.currentTimeMillis();

		if((j % (Width/10))==0)
		{
			System.err.println("- "+(j*100/Width)+"% ETA:"+new Date(System.currentTimeMillis()+((curr-start)*(Width-j+1)/(j+1))));
		}
	}

	public void tryMakeWaterLandPercentage()
	{
		if(this.waterLandPercentage>0)
		{
			double _adj = 0.;
			/* user wants a height field with water pct */
			/* calculate a crude histogram to pick adjustment */
			int[] _histo = new int[2048];
			int _hc = 0;
			for(int ix=0; ix<Width; ix++)
			{
				for(int iy=0; iy<Height; iy++)
				{
					if(col[ix][iy]<0) continue;

					int _h = 1024+(int) (heights[ix][iy]*1024);
					if(_h<0) _h = 0;
					if(_h>=_histo.length) _h = _histo.length-1;
					_histo[_h]++;
					_hc++;
				}
			}
			_hc=_hc*this.waterLandPercentage;
			_hc=_hc/100;
			for(int _i=0; _i<_histo.length; _i++)
			{
				_hc-=_histo[_i];
				if(_hc<0) {
					_adj=-((_i-1024.)/1024.);
					break;
				}
			}

			/* finally adjust heightfield */
			for(int ix=0; ix<Width; ix++)
			{
				for(int iy=0; iy<Height; iy++)
				{
					if(col[ix][iy]<0) continue;
					heights[ix][iy]+=_adj;
				}
			}

			this.altitudeAdjustment = _adj;
		}
	}

	public void waitForExecution()
	{
		if(this.threaded)
		{
			boolean ended = false;
			int u=0;
			while(!ended)
			{
				tick(u);
				try
				{
					Thread.sleep(1000L);
				}
				catch(InterruptedException e)
				{ }
				ended = true;
				u=0;
				for(Future f : tList)
				{
					if(!f.isDone())
					{
						ended=false;
					}
					else
					{
						u++;
					}
				}
			}
		}
	}

	public void hexagonal(boolean _b)
	{
		if(!_b) {
			this.tryMakeWaterLandPercentage();
		}

		for (int j = 0; j < Height; j++)
		{
			HexagonalProc runfunc = HexagonalProc.create(this, j, _b);
			if(this.threaded)
			{
				tList.add(exec.submit(runfunc));
			}
			else
			{
				runfunc.run();
			}
		}

		waitForExecution();
	}

	public void longitudeLatitude(boolean _b)
	{
		if(!_b) {
			this.tryMakeWaterLandPercentage();
		}

		for (int j = 0; j < Height; j++)
		{
			//WidthByHeightProc runfunc = WidthByHeightProc.create(this, LongLatProjection.create(), PlanetJProcCallback.create(), j, _b);
			LonLatProc runfunc = LonLatProc.create(this, j, _b);
			if(this.threaded)
			{
				tList.add(exec.submit(runfunc));
			}
			else
			{
				runfunc.run();
			}
		}

		waitForExecution();

		if(!_b) {
			if (hgrid != 0.0)
			{ /* draw horisontal gridlines */
				double _latStart = 0;
				while(_latStart < 90) _latStart+=hgrid;
				double _latEnd = 0;
				while(_latEnd > -90) _latEnd-=hgrid;
				for (double _lat = _latStart; _lat>_latEnd; _lat-=hgrid)
				{
					int _j = (int) ((90.-(_lat-this.baseLatitude))*((double) this.Height/180.));
					if(_j<0) continue;
					if(_j>=this.Height) continue;

					for (int _i = 0; _i < this.Width ; _i++) col[_i][_j] = BLACK;
				}
			}
			if (vgrid != 0.0)
			{ /* draw vertical gridlines */
				double _lonStart = 0;
				while(_lonStart > -180) _lonStart-=vgrid;
				double _lonEnd = 0;
				while(_lonEnd < 180) _lonEnd+=vgrid;
				for (double _lon = _lonStart; _lon<_lonEnd; _lon+=vgrid)
				{
					int _i = (int) ((_lon-this.baseLongitude+180.)*((double) this.Width/360.));
					if(_i<0) continue;
					if(_i>=this.Width) continue;

					for (int _j = 0; _j < this.Height ; _j++) col[_i][_j] = BLACK;
				}
			}
		}
	}

	public void mercator(boolean _b)
	{
		double y,scale1,cos2,theta1;
		int i,j,k;

		if(!_b) {
			this.tryMakeWaterLandPercentage();
		}

		y = Math.sin(baseLatitude);
		y = (1.0+y)/(1.0-y);
		y = 0.5*Math.log(y);
		k = (int)(0.5*y*Width*scale/PI);
                
		for (j = 0; j < Height; j++) 
		{
			MercatorProc runfunc = MercatorProc.create(this, j, k, _b);
			if(this.threaded)
			{
				tList.add(exec.submit(runfunc));
			}
			else
			{
				runfunc.run();
			}
		}

		waitForExecution();

		if (hgrid != 0.0) { /* draw horisontal gridlines */
			for (theta1 = 0.0; theta1>-90.0; theta1-=hgrid);
			for (theta1 = theta1; theta1<90.0; theta1+=hgrid) {
				y = Math.sin(DEG2RAD*theta1);
				y = (1.0+y)/(1.0-y);
				y = 0.5*Math.log(y);
				j = Height/2+(int)(0.5*y*Width*scale/PI)+k;
				if (j>=0 && j<Height) for (i = 0; i < Width ; i++) col[i][j] = BLACK;
			}
		}
		if (vgrid != 0.0) { /* draw vertical gridlines */
			for (theta1 = 0.0; theta1>-360.0; theta1-=vgrid);
			for (theta1 = theta1; theta1<360.0; theta1+=vgrid) {
				i = (int)(0.5*Width*(1.0+scale*(DEG2RAD*theta1-baseLongitude)/PI));
				if (i>=0 && i<Width) for (j = 0; j < Height; j++) col[i][j] = BLACK;
			} 
		}
	}
	
	public void peter(boolean _b)
	{
		double y,cos2,theta1,scale1;
		int k,i,j;

		if(!_b) {
			this.tryMakeWaterLandPercentage();
		}

		y = 2.0*Math.sin(baseLatitude);
		k = (int)(0.5*y*Width*scale/PI);

		for (j = 0; j < Height; j++)
		{
			PetersProc runfunc = PetersProc.create(this, j, k, _b);
			if(this.threaded)
			{
				tList.add(exec.submit(runfunc));
			}
			else
			{
				runfunc.run();
			}
		}

		waitForExecution();

		if (hgrid != 0.0) { /* draw horisontal gridlines */
			for (theta1 = 0.0; theta1>-90.0; theta1-=hgrid);
			for (theta1 = theta1; theta1<90.0; theta1+=hgrid) {
				y = 2.0*Math.sin(DEG2RAD*theta1);
				j = Height/2+(int)(0.5*y*Width*scale/PI)+k;
				if (j>=0 && j<Height) for (i = 0; i < Width ; i++) col[i][j] = BLACK;
			}
		}
		if (vgrid != 0.0) { /* draw vertical gridlines */
			for (theta1 = 0.0; theta1>-360.0; theta1-=vgrid);
			for (theta1 = theta1; theta1<360.0; theta1+=vgrid) {
				i = (int)(0.5*Width*(1.0+scale*(DEG2RAD*theta1-baseLongitude)/PI));
				if (i>=0 && i<Width)
					for (j = max(0,Height/2-(int)(Width*scale/PI)+k);
						 j < min(Height,Height/2+(int)(Width*scale/PI)+k); j++)
						col[i][j] = BLACK;
			}
		}
	}

	public void mollweide(boolean _b)
	{
		double x,y,y1,theta1,theta2;
		int i,j,i1=1,k;

		if(!_b) {
			this.tryMakeWaterLandPercentage();
		}

		for (j = 0; j < Height; j++)
		{
			MollweideProc runfunc = MollweideProc.create(this, j, _b);
			if(this.threaded)
			{
				tList.add(exec.submit(runfunc));
			}
			else
			{
				runfunc.run();
			}
		}

		waitForExecution();

		if (hgrid != 0.0) { /* draw horisontal gridlines */
			for (theta1 = 0.0; theta1>-90.0; theta1-=hgrid);
			for (theta1 = theta1; theta1<90.0; theta1+=hgrid) {
				theta2 = Math.abs(theta1);
				x = Math.floor(theta2/5.0); y = theta2/5.0-x;
				y = (1.0-y)*moll_table[(int)x]+y*moll_table[(int)x+1];
				if (theta1<0.0) y = -y;
				j = Height/2+(int)(0.25*y*Width*scale);
				if (j>=0 && j< Height) {
					for (i = max(0,Width/2-(int)(0.5*Width*scale*Math.sqrt(1.0-y*y)));
					i < min(Width,Width/2+(int)(0.5*Width*scale*Math.sqrt(1.0-y*y))); i++)
						col[i][j] = BLACK;
				}
			}
		}
		if (vgrid != 0.0) { /* draw vertical gridlines */
			for (theta1 = 0.0; theta1>-360.0; theta1-=vgrid);
			for (theta1 = theta1; theta1<360.0; theta1+=vgrid) {
				if (DEG2RAD*theta1-baseLongitude+0.5*PI>-PI &&
				DEG2RAD*theta1-baseLongitude+0.5*PI<=PI) {
					x = 0.5*(DEG2RAD*theta1-baseLongitude+0.5*PI)*Width*scale/PI;
					j = max(0,Height/2-(int)(0.25*Width*scale));
					y = 2*(2.0*j-Height)/Width/scale;
					i = (int) (Width/2.0+x*Math.sqrt(1.0-y*y));
					for (; j <= min(Height,Height/2+(int)(0.25*Width*scale)); j++) {
						y1 = 2*(2.0*j-Height)/Width/scale;
						if (Math.abs(y1)<=1.0) {
							i1 = (int) (Width/2+x*Math.sqrt(1.0-y1*y1));
							if (i1>=0 && i1<Width && j>=0 && j<Height) col[i1][j] = BLACK;
						}
						if (Math.abs(y)<=1.0) {
							if (i<i1) {
								for (k=i+1; k<i1; k++)
									if (k>00 && k<Width && j>=0 && j<Height) col[k][j] = BLACK;
							}
							else if (i>i1) {
								for (k=i-1; k>i1; k--)
									if (k>=0 && k<Width && j>=0 && j<Height) col[k][j] = BLACK;
							}
						}
						y = y1;
						i = i1;
					}
				}
			}
		}
	}
	

	public void sinusoid(boolean _b)
	{
		double y,theta1,theta2,cos2,l1,i1,scale1;
		int k,i,j,l,c;

		if(!_b) {
			this.tryMakeWaterLandPercentage();
		}

		k = (int)(baseLatitude*Width*scale/PI);
		for (j = 0; j < Height; j++) {
			y = (2.0*(j-k)-Height)/Width/scale*PI;
			if (Math.abs(y)>=0.5*PI) for (i = 0; i < Width ; i++) {
				col[i][j] = BACK;
				if (doshade) shades[i][j] = 255;
			} else {
				cos2 = Math.cos(y);
				if (cos2>0.0) {
					scale1 = scale*Width/Height/cos2/PI;
					int sDepth = 3*((int)(log_2(scale1*Height)))+3;
					for (i = 0; i<Width; i++) {
						l = i*12/Width;
						l1 = l*Width/12.0;
						i1 = i-l1;
						theta2 = baseLongitude-0.5*PI+PI*(2.0*l1-Width)/Width/scale;
						theta1 = (PI*(2.0*i1-Width/12)/Width/scale)/cos2;
						if (Math.abs(theta1)>PI/12.0)
						{
							col[i][j] = BACK;
							if (doshade) shades[i][j] = 255;
						}
						else
						{
							planet_main(i,j,Math.cos(theta1+theta2)*cos2,Math.sin(y),
									-Math.sin(theta1+theta2)*cos2, sDepth, _b);
						}
					}
				}
			}
			tickH(j);
		}
		if (hgrid != 0.0) { /* draw horisontal gridlines */
			for (theta1 = 0.0; theta1>-90.0; theta1-=hgrid);
			for (; theta1<90.0; theta1+=hgrid) {
				y = DEG2RAD*theta1;
				cos2 = Math.cos(y);
				j = Height/2+(int)(0.5*y*Width*scale/PI)+k;
				if (j>=0 && j<Height)
					for (i = 0; i < Width ; i++) {
						l = i*12/Width;
						l1 = l*Width/12.0;
						i1 = i-l1;
						theta2 = (PI*(2.0*i1-Width/12)/Width/scale)/cos2;
						if (Math.abs(theta2)<=PI/12.0) col[i][j] = BLACK;
					}
			}
		}
		if (vgrid != 0.0) { /* draw vertical gridlines */
			for (theta1 = 0.0; theta1>-360.0; theta1-=vgrid);
			for (; theta1<360.0; theta1+=vgrid) {
				i = (int)(0.5*Width*(1.0+scale*(DEG2RAD*theta1-baseLongitude)/PI));
				if (i>=0 && i<Width)
					for (j = max(0,Height/2-(int)(0.25*PI*Width*scale/PI)+k);
						 j < min(Height,Height/2+(int)(0.25*PI*Width*scale/PI)+k); j++) {
						y = (2.0*(j-k)-Height)/Width/scale*PI;
						cos2 = Math.cos(y);
						l = i*12/Width;
						l1 = l*Width/12.0+Width/24.0;
						i1 = i-l1;
						c = (int) (l1+i1*cos2);
						if (c>=0 && c<Width) col[c][j] = BLACK;
					}
			}
		}
	}
	
	public void stereo(boolean _b)
	{
		double x,y,ymin,ymax,z,zz,x1,y1,z1,theta1,theta2;
		int i,j;

		if(!_b) {
			this.tryMakeWaterLandPercentage();
		}

		ymin = 2.0;
		ymax = -2.0;
		for (j = 0; j < Height; j++) {
			for (i = 0; i < Width ; i++) {
				x = (2.0*i-Width)/Height/scale;
				y = (2.0*j-Height)/Height/scale;
				z = x*x+y*y;
				zz = 0.25*(4.0+z);
				x = x/zz;
				y = y/zz;
				z = (1.0-0.25*z)/zz;
				x1 = clo*x+slo*sla*y+slo*cla*z;
				y1 = cla*y-sla*z;
				z1 = -slo*x+clo*sla*y+clo*cla*z;
				if (y1 < ymin) ymin = y1;
				if (y1 > ymax) ymax = y1;
				planet_main(i,j,x1,y1,z1, Depth, _b);
			}
			tickH(j);
		}
		if (hgrid != 0.0) { /* draw horisontal gridlines */
			for (theta1 = 0.0; theta1>-90.0; theta1-=hgrid);
			for (theta1 = theta1; theta1<90.0; theta1+=hgrid) {
				y = Math.sin(DEG2RAD*theta1);
				if (ymin <= y && y <= ymax) {
					zz = Math.sqrt(1-y*y);
					for (theta2=-PI; theta2<PI; theta2+=0.5/Width/scale) {
						x = Math.sin(theta2)*zz;
						z = Math.cos(theta2)*zz;
						x1 = clo*x+slo*z;
						y1 = slo*sla*x+cla*y-clo*sla*z;
						z1 = -slo*cla*x+sla*y+clo*cla*z;
						if (Math.abs(z1)<1.0){
							i = (int) (0.5*(Height*scale*2.0*x1*(1+z1)/(1.0-z1*z1)+Width));
							j = (int) (0.5*(Height*scale*2.0*y1*(1+z1)/(1.0-z1*z1)+Height));
							if (0<=i && i<Width && 0<=j && j<Height) col[i][j] = BLACK;
						}
					}
				}
			}
		}
		if (vgrid != 0.0) { /* draw vertical gridlines */
			for (theta2=-PI; theta2<PI; theta2+=0.5/Width/scale) {
				y = Math.sin(theta2);
				if (ymin <= y && y <= ymax) {
					for (theta1 = 0.0; theta1<360.0; theta1+=vgrid) {
						x = Math.sin(DEG2RAD*theta1)*Math.cos(theta2);
						z = Math.cos(DEG2RAD*theta1)*Math.cos(theta2);
						x1 = clo*x+slo*z;
						y1 = slo*sla*x+cla*y-clo*sla*z;
						z1 = -slo*cla*x+sla*y+clo*cla*z;
						if (Math.abs(z1)<1.0){
							i = (int) (0.5*(Height*scale*2.0*x1*(1+z1)/(1-z1*z1)+Width));
							j = (int) (0.5*(Height*scale*2.0*y1*(1+z1)/(1-z1*z1)+Height));
							if (0<=i && i<Width && 0<=j && j<Height) col[i][j] = BLACK;
						}
					}
				}
			}
		}
	}

	public void orthographic(boolean _b)
	{
		double x,y,z,x1,y1,z1,ymin,ymax,theta1,theta2,zz;
		int i,j;

		if(!_b) {
			this.tryMakeWaterLandPercentage();
		}

		for (j = 0; j < Height; j++) {
			OrthographicProc runfunc = OrthographicProc.create(this, j,_b);
			if(this.threaded)
			{
				tList.add(exec.submit(runfunc));
			}
			else
			{
				runfunc.run();
			}
		}
		waitForExecution();

		if (hgrid != 0.0) { /* draw horisontal gridlines */
			for (theta1 = 0.0; theta1>-90.0; theta1-=hgrid);
			for (theta1 = theta1; theta1<90.0; theta1+=hgrid) {
				y = Math.sin(DEG2RAD*theta1);
				zz = Math.sqrt(1-y*y);
				for (theta2=-PI; theta2<PI; theta2+=0.5/Width/scale) {
					x = Math.sin(theta2)*zz;
					z = Math.cos(theta2)*zz;
					x1 = clo*x+slo*z;
					y1 = slo*sla*x+cla*y-clo*sla*z;
					z1 = -slo*cla*x+sla*y+clo*cla*z;
					if (0.0>=z1){
						i = (int) (0.5*(Height*scale*x1+Width));
						j = (int) (0.5*(Height*scale*y1+Height));
						if (0<=i && i<Width && 0<=j && j<Height) col[i][j] = BLACK;
					}
				}
			}
		}
		if (vgrid != 0.0) { /* draw vertical gridlines */
			for (theta2=-PI; theta2<PI; theta2+=0.5/Width/scale) {
				y = Math.sin(theta2);
				for (theta1 = 0.0; theta1<360.0; theta1+=vgrid) {
					x = Math.sin(DEG2RAD*theta1)*Math.cos(theta2);
					z = Math.cos(DEG2RAD*theta1)*Math.cos(theta2);
					x1 = clo*x+slo*z;
					y1 = slo*sla*x+cla*y-clo*sla*z;
					z1 = -slo*cla*x+sla*y+clo*cla*z;
					if (0.0>=z1){
						i = (int) (0.5*(Height*scale*x1+Width));
						j = (int) (0.5*(Height*scale*y1+Height));
						if (0<=i && i<Width && 0<=j && j<Height) col[i][j] = BLACK;
					}
				}
			}
		}
	}

	public void gnomonic(boolean _b)
	{
		double x,y,z,x1,y1,z1,zz,theta1,theta2,ymin,ymax;
		int i,j;

		ymin = 2.0;
		ymax = -2.0;
		if(!_b) {
			this.tryMakeWaterLandPercentage();
		}
		for (j = 0; j < Height; j++) {
			for (i = 0; i < Width ; i++) {
				x = (2.0*i-Width)/Height/scale;
				y = (2.0*j-Height)/Height/scale;
				zz = Math.sqrt(1.0/(1.0+x*x+y*y));
				x = x*zz;
				y = y*zz;
				z = Math.sqrt(1.0-x*x-y*y);
				x1 = clo*x+slo*sla*y+slo*cla*z;
				y1 = cla*y-sla*z;
				z1 = -slo*x+clo*sla*y+clo*cla*z;
				if (y1 < ymin) ymin = y1;
				if (y1 > ymax) ymax = y1;
				planet_main(i,j,x1,y1,z1, Depth, _b);
			}
			tickH(j);
		}
		if(!_b) {
			if (hgrid != 0.0) { /* draw horisontal gridlines */
				for (theta1 = 0.0; theta1>-90.0; theta1-=hgrid);
				for (theta1 = theta1; theta1<90.0; theta1+=hgrid) {
					y = Math.sin(DEG2RAD*theta1);
					if (ymin <= y && y <= ymax) {
						zz = Math.sqrt(1-y*y);
						for (theta2=-PI; theta2<PI; theta2+=0.5/Width/scale) {
							x = Math.sin(theta2)*zz;
							z = Math.cos(theta2)*zz;
							x1 = clo*x-slo*z;
							y1 = slo*sla*x+cla*y+clo*sla*z;
							z1 = slo*cla*x-sla*y+clo*cla*z;
							if (z1!=0.0){
								i = (int) (0.5*(Height*scale*x1/z1+Width));
								j = (int) (0.5*(Height*scale*y1/z1+Height));
								if (0<=i && i<Width && 0<=j && j<Height) col[i][j] = BLACK;
							}
						}
					}
				}
			}
			if (vgrid != 0.0) { /* draw vertical gridlines */
				for (theta2=-PI; theta2<PI; theta2+=0.5/Width/scale) {
					y = Math.sin(theta2);
					if (ymin <= y && y <= ymax) {
						for (theta1 = 0.0; theta1<360.0; theta1+=vgrid) {
							x = Math.sin(DEG2RAD*theta1)*Math.cos(theta2);
							z = Math.cos(DEG2RAD*theta1)*Math.cos(theta2);
							x1 = clo*x-slo*z;
							y1 = slo*sla*x+cla*y+clo*sla*z;
							z1 = slo*cla*x-sla*y+clo*cla*z;
							if (z1!=0.0){
								i = (int) (0.5*(Height*scale*x1/z1+Width));
								j = (int) (0.5*(Height*scale*y1/z1+Height));
								if (0<=i && i<Width && 0<=j && j<Height) col[i][j] = BLACK;
							}
						}
					}
				}
			}
		}
	}


	@SneakyThrows
	public void azimuth(boolean _b)
	{
		double x,y,z,x1,y1,z1,zz,theta1,theta2,ymin,ymax;
		int i,j;

		ymin = 2.0;
		ymax = -2.0;
		if(!_b) {
			this.tryMakeWaterLandPercentage();
		}
		for (j = 0; j < Height; j++) {
			AzimuthProc runfunc = AzimuthProc.create(this, j,_b);
			runfunc.ymin = 2.0;
			runfunc.ymax = -2.0;
			if(this.threaded)
			{
				tList.add(exec.submit(runfunc));
			}
			else
			{
				runfunc.call();
				if(runfunc.ymin < ymin) ymin = runfunc.ymin;
				if(runfunc.ymax > ymax) ymax = runfunc.ymax;
			}
		}
		waitForExecution();
		if(this.threaded)
		{
			for(Future _t : tList)
			{
				AzimuthProc runfunc = (AzimuthProc) _t.get();
				if(runfunc.ymin < ymin) ymin = runfunc.ymin;
				if(runfunc.ymax > ymax) ymax = runfunc.ymax;
			}
		}

		if(!_b)
		{
			if (hgrid != 0.0) { /* draw horisontal gridlines */
				for (theta1 = 0.0; theta1>-90.0; theta1-=hgrid);
				for (theta1 = theta1; theta1<90.0; theta1+=hgrid) {
					y = Math.sin(DEG2RAD*theta1);
					if (ymin <= y && y <= ymax) {
						zz = Math.sqrt(1-y*y);
						for (theta2=-PI; theta2<PI; theta2+=0.5/Width/scale) {
							x = Math.sin(theta2)*zz;
							z = Math.cos(theta2)*zz;
							x1 = clo*x-slo*z;
							y1 = slo*sla*x+cla*y+clo*sla*z;
							z1 = slo*cla*x-sla*y+clo*cla*z;
							if (z1!=-1.0){
								i = (int) (0.5*(Height*scale*x1/Math.sqrt(0.5+0.5*z1)+Width));
								j = (int) (0.5*(Height*scale*y1/Math.sqrt(0.5+0.5*z1)+Height));
								if (0<=i && i<Width && 0<=j && j<Height) col[i][j] = BLACK;
							}
						}
					}
				}
			}
			if (vgrid != 0.0) { /* draw vertical gridlines */
				for (theta2=-PI; theta2<PI; theta2+=0.5/Width/scale) {
					y = Math.sin(theta2);
					if (ymin <= y && y <= ymax) {
						for (theta1 = 0.0; theta1<360.0; theta1+=vgrid) {
							x = Math.sin(DEG2RAD*theta1)*Math.cos(theta2);
							z = Math.cos(DEG2RAD*theta1)*Math.cos(theta2);
							x1 = clo*x-slo*z;
							y1 = slo*sla*x+cla*y+clo*sla*z;
							z1 = slo*cla*x-sla*y+clo*cla*z;
							if (z1!=-1.0){
								i = (int) (0.5*(Height*scale*x1/Math.sqrt(0.5+0.5*z1)+Width));
								j = (int) (0.5*(Height*scale*y1/Math.sqrt(0.5+0.5*z1)+Height));
								if (0<=i && i<Width && 0<=j && j<Height) col[i][j] = BLACK;
							}
						}
					}
				}
			}
		}
	}


	public void squarep(boolean _b)
	{
		double y,scale1,theta1,cos2;
		int k,i,j;

		if(!_b) {
			this.tryMakeWaterLandPercentage();
		}

		k = (int)(baseLatitude*Width*scale/PI);
		for (j = 0; j < Height; j++) {
			SquarepProc runfunc = SquarepProc.create(this, j, k, _b);
			if(this.threaded)
			{
				tList.add(exec.submit(runfunc));
			}
			else
			{
				runfunc.run();
			}
		}

		waitForExecution();

		if (hgrid != 0.0) { /* draw horisontal gridlines */
			for (theta1 = 0.0; theta1>-90.0; theta1-=hgrid);
			for (theta1 = theta1; theta1<90.0; theta1+=hgrid) {
				y = DEG2RAD*theta1;
				j = Height/2+(int)(0.5*y*Width*scale/PI)+k;
				if (j>=0 && j<Height) for (i = 0; i < Width ; i++) col[i][j] = BLACK;
			}
		}
		if (vgrid != 0.0) { /* draw vertical gridlines */
			for (theta1 = 0.0; theta1>-360.0; theta1-=vgrid);
			for (theta1 = theta1; theta1<360.0; theta1+=vgrid) {
				i = (int)(0.5*Width*(1.0+scale*(DEG2RAD*theta1-baseLongitude)/PI));
				if (i>=0 && i<Width)
					for (j = max(0,Height/2-(int)(0.25*PI*Width*scale/PI)+k);
						 j < min(Height,Height/2+(int)(0.25*PI*Width*scale/PI)+k); j++)
						col[i][j] = BLACK;
			}
		}
	}

	public void save_sint16(String path)
	{
		DataOutputStream dout = null;
		try
		{
			dout = new DataOutputStream(new FileOutputStream(path));
			for (int iy = 0; iy < this.Height; iy++)
			{
				for(int ix=0; ix<this.Width; ix++)
				{
					double h = this.heights[ix][iy];
					if(h<0.0)
					{
						dout.writeShort((int)(Short.MIN_VALUE*Math.abs(h)));
					}
					else
					{
						dout.writeShort((int)(Short.MAX_VALUE*h));
					}
				}
			}
		}
		catch(Exception xe)
		{
		}
		finally
		{
			try { dout.close(); } catch(Exception xe) { }
		}
	}

	public void saveTemp_sint16(String path)
	{
		DataOutputStream dout = null;
		try
		{
			dout = new DataOutputStream(new FileOutputStream(path));
			for (int iy = 0; iy < this.Height; iy++)
			{
				for(int ix=0; ix<this.Width; ix++)
				{
					double h = this.temperature[ix][iy]*300 /*celcius*/;
					if(h<0.0)
					{
						h = Math.max(-10000, h);
						dout.writeShort((int)h);
					}
					else
					{
						h = Math.min(10000, h);
						dout.writeShort((int)h);
					}
				}
			}
		}
		catch(Exception xe)
		{
		}
		finally
		{
			try { dout.close(); } catch(Exception xe) { }
		}
	}

	public void saveRainfall_sint16(String path)
	{
		DataOutputStream dout = null;
		try
		{
			dout = new DataOutputStream(new FileOutputStream(path));
			for (int iy = 0; iy < this.Height; iy++)
			{
				for(int ix=0; ix<this.Width; ix++)
				{
					double h = this.rainfall[ix][iy];
					if(h<0.0)
					{
						h = Math.max(-10000, h);
						dout.writeShort((int)h);
					}
					else
					{
						h = Math.min(10000, h);
						dout.writeShort((int)h);
					}
				}
			}
		}
		catch(Exception xe)
		{
		}
		finally
		{
			try { dout.close(); } catch(Exception xe) { }
		}
	}

	public void load_sint16(String path)
	{
		DataInputStream din = null;
		try
		{
			din = new DataInputStream(new FileInputStream(path));
			for (int iy = 0; iy < this.Height; iy++)
			{
				for(int ix=0; ix<this.Width; ix++)
				{
					double h = din.readShort()/((double)Short.MAX_VALUE);
					this.heights[ix][iy] = h;
					this.col[ix][iy] = alt2color(h, 0,0,0);
				}
			}
		}
		catch(Exception xe)
		{
		}
		finally
		{
			try { din.close(); } catch(Exception xe) { }
		}
	}

	public void load_sint16_tga(String path)
	{
		DataInputStream din = null;
		try
		{
			din = new DataInputStream(new FileInputStream(path));
			din.skipBytes(18);
			for (int iy = this.Height-1; iy >=0 ; iy--)
			{
				for(int ix=0; ix<this.Width; ix++)
				{
					din.readByte();
					short x = (short)(din.readByte() & 0xff);
					x |= (short)((din.readByte() & 0xff)<<8);
					double h = x/((double)Short.MAX_VALUE+1);
					this.heights[ix][iy] = h;
					this.col[ix][iy] = alt2color(h, 0,0,0);
				}
			}
		}
		catch(Exception xe)
		{
		}
		finally
		{
			try { din.close(); } catch(Exception xe) { }
		}
	}

	public void save()
	{
		if(filename!=null && !filename.equals("false"))
		{
			save(filename);
		}
		if(world_file!=null && !world_file.equals("false"))
		{
			ImageCodec.saveWLD(this, world_file);
		}
		if(ter_file!=null && !ter_file.equals("false"))
		{
			save_TER(ter_file);
		}
	}

	public void saveOutline(String _f, int _pct, String _comment)
	{
		ImageCodec.saveOutline(this, _f, _pct, _comment);
	}

	public void saveOutline(String _f, String _comment)
	{
		ImageCodec.saveOutline(this, _f, -1, _comment);
	}

	public void saveOutline(String _f, int _pct)
	{
		ImageCodec.saveOutline(this, _f, _pct);
	}

	public void saveOutline(String _f)
	{
		ImageCodec.saveOutline(this, _f, -1);
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public boolean isDoshade() {
		return doshade;
	}

	public void setDoshade(boolean doshade) {
		this.doshade = doshade;
	}

	public int getWidth() {
		return Width;
	}

	public void setWidth(int width) {
		Width = width;
	}
	
	public double getSeed() {
		return rseed;
	}

	public void setSeed(double s) {
		rseed = s;
	}

	public int getHeight() {
		return Height;
	}

	public void setHeight(int height) {
		Height = height;
	}

	public int getNumberOfColors() {
		return nocols;
	}

	public void setNumberOfColors(int n) {
		nocols = n;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public double getInitialAltitude() {
		return initialAltitude;
	}

	public void setInitialAltitude(double initialAltitude) {
		this.initialAltitude = initialAltitude;
	}

	public double getAltitudeWeight() {
		return altitudeWeight;
	}

	public void setAltitudeWeight(double altitudeWeight) {
		this.altitudeWeight = altitudeWeight;
	}

	public double getDistanceWeight() {
		return distanceWeight;
	}

	public void setDistanceWeight(double distanceWeight) {
		this.distanceWeight = distanceWeight;
	}

	public boolean isUseAlternativeColors() {
		return useAlternativeColors;
	}

	public void setUseAlternativeColors(boolean useAlternativeColors) {
		this.useAlternativeColors = useAlternativeColors;
	}

	public double getBaseLongitude() {
		return baseLongitude;
	}

	public void setBaseLongitude(double baseLongitude) {
		this.baseLongitude = baseLongitude;
	}

	public double getBaseLatitude() {
		return baseLatitude;
	}

	public void setBaseLatitude(double baseLatitude) {
		this.baseLatitude = baseLatitude;
	}

	public double getVgrid() {
		return vgrid;
	}

	public void setVgrid(double vgrid) {
		this.vgrid = vgrid;
	}

	public double getHgrid() {
		return hgrid;
	}

	public void setHgrid(double hgrid) {
		this.hgrid = hgrid;
	}

	public char getView() {
		return view;
	}

	public double getTemperatureVariationFactor() {
		return temperatureVariationFactor;
	}

	public void setTemperatureVariationFactor(double temperatureVariationFactor) {
		this.temperatureVariationFactor = temperatureVariationFactor;
	}

	public double getTemperatureVariationFrequency() {
		return temperatureVariationFrequency;
	}

	public void setTemperatureVariationFrequency(double temperatureVariationFrequency) {
		this.temperatureVariationFrequency = temperatureVariationFrequency;
	}

	public double getTemperatureBase() {
		return temperatureBase;
	}

	public void setTemperatureBase(double temperatureBase) {
		this.temperatureBase = temperatureBase;
	}

	public double getRainfallBase() {
		return rainfallBase;
	}

	public void setRainfallBase(double rainfallBase) {
		this.rainfallBase = rainfallBase;
	}

	public double getRainfallVariationFactor() {
		return rainfallVariationFactor;
	}

	public void setRainfallVariationFactor(double rainfallVariationFactor) {
		this.rainfallVariationFactor = rainfallVariationFactor;
	}

	public double getRainfallVariationFrequency() {
		return rainfallVariationFrequency;
	}

	public void setRainfallVariationFrequency(double rainfallVariationFrequency) {
		this.rainfallVariationFrequency = rainfallVariationFrequency;
	}

	public void setView(char view) {
		this.view = view;
	}

	public boolean isDoWaterShade() {
		return doWaterShade;
	}

	public void setDoWaterShade(boolean doWaterShade) {
		this.doWaterShade = doWaterShade;
	}

	public int getWaterShade() {
		return waterShade;
	}

	public void setWaterShade(int waterShade) {
		this.waterShade = waterShade;
	}

	public boolean isLatitudeColors() {
		return latitudeColors;
	}

	public void setLatitudeColors(boolean latitudeColors) {
		this.latitudeColors = latitudeColors;
	}

	public double getShadeAngle() {
		return shadeAngle;
	}

	public void setShadeAngle(double shadeAngle) {
		this.shadeAngle = shadeAngle;
	}

	public ColorRamp getColorRamp() {
		return colorRamp;
	}

	public void setColorRamp(ColorRamp colorRamp) {
		this.colorRamp = colorRamp;
	}

	public double getColorRampLandMax() {
		return colorRampLandMax;
	}

	public void setColorRampLandMax(double colorRampLandMax) {
		this.colorRampLandMax = colorRampLandMax;
	}

	public double getColorRampSeaMin() {
		return colorRampSeaMin;
	}

	public void setColorRampSeaMin(double colorRampSeaMin) {
		this.colorRampSeaMin = colorRampSeaMin;
	}

	public boolean isNonLinear()
	{
		return nonLinear;
	}

	public void setNonLinear(boolean nonLinear)
	{
		this.nonLinear = nonLinear;
	}

	public int getWrinkleContribution()
	{
		return wrinkleContribution;
	}

	public void setWrinkleContribution(int wrinkleContribution)
	{
		this.wrinkleContribution = wrinkleContribution;
	}

	public double getAltitudeAdjustment()
	{
		return altitudeAdjustment;
	}

	public void setAltitudeAdjustment(double altitudeAdjustment)
	{
		this.altitudeAdjustment = altitudeAdjustment;
	}

	/* distance squared between vertices */
	static double dist2(vertex a, vertex b)
	{
		double abx, aby, abz;
		abx = a.x-b.x; aby = a.y-b.y; abz = a.z-b.z;
		return abx*abx+aby*aby+abz*abz;
	}

	public void planet_main(int i, int j, double x, double y, double z, int lvl, boolean _altFirst)
	{
		// alternate modes ?
		if(this.waterLandPercentage>0)
		{
			if(_altFirst)
			{
				planet0_altitude(i,j,x,y,z,lvl);
				if(this._fractalOverlay!=null)
				{
					this._fractalOverlay.projectCallback(this._fractalContext, i, j, x ,y ,z, lvl, true);
				}
			}
			else
			{
				planet0_temp(i,j,x,y,z,lvl);

				planet0_rain(i,j,x,y,z,lvl);

				planet0_color(i,j,x,y,z,lvl);

				planet0_biome(i,j,x,y,z,lvl);

				planet0_waterflux(i,j,x,y,z,lvl);
			}
		}
		else if(_altFirst)
		{
			planet0_altitude(i,j,x,y,z,lvl);
			if(this._fractalOverlay!=null)
			{
				this._fractalOverlay.projectCallback(this._fractalContext, i, j, x ,y ,z, lvl, true);
			}

			planet0_temp(i,j,x,y,z,lvl);

			planet0_rain(i,j,x,y,z,lvl);

			planet0_color(i,j,x,y,z,lvl);

			planet0_biome(i,j,x,y,z,lvl);

			planet0_waterflux(i,j,x,y,z,lvl);
		}
	}

	static int[][][] _MASKS = {
			{
					{ -1, -1},
					{  0, -1},
					{  1, -1},

					{ -1, 0},
					{  1, 0},

					{ -1, 1},
					{  0, 1},
					{  1, 1}
			},
			{
					{ -2, -2},
					{  0, -2},
					{  2, -2},

					{ -2, 0},
					{  2, 0},

					{ -2, 2},
					{  0, 2},
					{  2, 2}
			}
	};


	public void planet0_waterflux(int i, int j, double x, double y, double z, int lvl)
	{
		double _value = heights[i][j];
		//int _z = (this.Height<this.Width ? this.Height : this.Width)>>4;
		int _z = 32;
		while(_value > 0. && _z>0)
		{
			_z--;
			int[] _target = null;
			_value = Double.MAX_VALUE;

			for(int[] _dxy : _MASKS[0])
			{
				int _x = i + _dxy[0];
				int _y = j + _dxy[1];

				if(_x<0 ||  _y<0 || _x>=this.Width || _y>=this.Height) continue;

				if(heights[_x][_y]<=_value && waterFlux[_x][_y]>=waterFlux[i][j])
				{
					_target = _dxy;
					_value=heights[_x][_y];
				}
			}

			if(_target==null)
			{
				for(int[] _dxy : _MASKS[1])
				{
					int _x = i + _dxy[0];
					int _y = j + _dxy[1];

					if(_x<0 ||  _y<0 || _x>=this.Width || _y>=this.Height) continue;

					if(heights[_x][_y]<=_value && waterFlux[_x][_y]>=waterFlux[i][j])
					{
						_target = _dxy;
						_value=heights[_x][_y];
					}
				}
			}

			if(_target==null) return;

			//waterFlux[i][j] += this.rainfall[i][j];
			waterFlux[_target[0]+i][_target[1]+j] += 1+this.rainfall[i][j];

			i+=_target[0];
			j+=_target[1];
			_value = heights[i][j];
		}
	}

	public void planet0_biome(int i, int j, double x, double y, double z, int lvl)
	{
		double alt = this.heights[i][j];

		this.biome[i][j] = PlanetHelper.planetaryBiome(x,y,z,alt,this.temperature[i][j],this.rainfall[i][j]);

		if(this.col[i][j] == WHITE)
		{
			this.biome[i][j] = 'I';
		}
	}

	public double planet0_rain(int i, int j, double x, double y, double z, int lvl)
	{
		double alt = this.heights[i][j];

		double _rAdj = PlanetHelper.calcRainAdjustmentCell(x,y,z,(int)Double.doubleToLongBits(this.getSeed()),this.rainfallVariationFrequency,this.rainShadow, this.rainfallBase, this.rainfallVariationFactor);

		double rain = PlanetHelper.planetaryRain(x, y, z, lvl, alt, this.temperature[i][j], _rAdj);

		if (rain<rainMin && alt >0) rainMin = rain;
		if (rain>rainMax && alt >0) rainMax = rain;

		this.rainAdjust[i][j] = _rAdj;
		//this.rainfall[i][j] = rain-0.02;
		this.rainfall[i][j] = rain;

		return rain;
	}

	public double planet0_temp(int i, int j, double x, double y, double z, int lvl)
	{
		double _alt = this.heights[i][j];
		double _tAdj = PlanetHelper.calcTempAdjustmentCell(x,y,z,
				(int)Double.doubleToLongBits(this.getSeed()),
				this.temperatureVariationFrequency,
				this.temperatureVariationFactor,
				this.temperatureBase);

		double temp = PlanetHelper.planetaryTemperature(x, y, z, lvl, _alt, _tAdj);

		if (temp<this.tempMin && _alt >0) this.tempMin = temp;
		if (temp>this.tempMax && _alt >0) this.tempMax = temp;
		this.tempAdjust[i][j] = _tAdj;
		//this.temperature[i][j] = temp-0.05;
		this.temperature[i][j] = temp;

		return temp;
	}

	public void planet0_color(int i, int j, double x, double y, double z, int lvl)
	{
		/* calculate colour */

		this.col[i][j] = this.alt2color(this.heights[i][j], x, y, z);

		/* store shading info */
		if(!this.doWaterShade && this.heights[i][j]<=0.0)
		{
			this.shades[i][j] = this.waterShade;
		}
		else if(this.doshade)
		{
			this.shades[i][j] = shade;
		}
	}

	public void planet0_altitude(int i, int j, double x, double y, double z, int lvl)
	{
		double alt = planet1(x, y, z, lvl) + this.altitudeAdjustment;

		if(this.nonLinear)
		{
			if(alt > 0.0)
			{
				alt = alt * alt * alt * 300.0;
			}
		}

		this.heights[i][j] = alt;
		this.col[i][j] = 0;
	}

	public double planet1(double x, double y, double z, int lvl)
	{
		ssa = ssb = ssc = ssd =
				ssas = ssbs = sscs = ssds =
						ssax = ssay = ssaz =
								ssbx = ssby = ssbz =
										sscx = sscy = sscz =
												ssdx = ssdy = ssdz = 0.0;
		
		return(planet(tetra[0], tetra[1], tetra[2], tetra[3], /* vertices of tetrahedron */
				x,y,z,
				/* coordinates of point we want colour of */
				lvl));
		/* subdivision depth */
	}

	public double ssa,ssb,ssc,ssd,
			ssas,ssbs,sscs,ssds,
			ssax,ssay,ssaz,
			ssbx,ssby,ssbz,
			sscx,sscy,sscz,
			ssdx,ssdy,ssdz;

	public double planet(
			vertex a, vertex b, vertex c, vertex d,
			/* altitudes of the 4 verticess */
			/* seeds of the 4 verticess */
			/* vertex coordinates */
			double x, double y, double z, /* goal point */
			int level /* levels to go */)
	{
		double abx,aby,abz, acx,acy,acz, adx,ady,adz;
		double bcx,bcy,bcz, bdx,bdy,bdz, cdx,cdy,cdz;
		double lab, lac, lad, lbc, lbd, lcd;
		vertex e = new vertex();
		double es1, es2, es3;
		double eax,eay,eaz, epx,epy,epz;
		double ecx,ecy,ecz, edx,edy,edz;
		double x1,y1,z1,x2,y2,z2,l1,tmp;
		
		if (level>0) {

			/* make sure ab is longest edge */
			lab = dist2(a,b);
			lac = dist2(a,c);
			lad = dist2(a,d);
			lbc = dist2(b,c);
			lbd = dist2(b,d);
			lcd = dist2(c,d);

			double maxlength = lab;
			if (lac > maxlength) maxlength = lac;
			if (lad > maxlength) maxlength = lad;
			if (lbc > maxlength) maxlength = lbc;
			if (lbd > maxlength) maxlength = lbd;
			if (lcd > maxlength) maxlength = lcd;

			if (lac == maxlength) return(planet(a,c,b,d, x,y,z, level));
			if (lad == maxlength) return(planet(a,d,b,c, x,y,z, level));
			if (lbc == maxlength) return(planet(b,c,a,d, x,y,z, level));
			if (lbd == maxlength) return(planet(b,d,a,c, x,y,z, level));
			if (lcd == maxlength) return(planet(c,d,a,b, x,y,z, level));

			if (level==11) {
				ssa=a.h; ssb=b.h; ssc=c.h; ssd=d.h; ssas=a.s; ssbs=b.s; sscs=c.s; ssds=d.s;
				ssax=a.x; ssay=a.y; ssaz=a.z; ssbx=b.x; ssby=b.y; ssbz=b.z;
				sscx=c.x; sscy=c.y; sscz=c.z; ssdx=d.x; ssdy=d.y; ssdz=d.z;
			}

			e.x = 0.5*(a.x+b.x);
			e.y = 0.5*(a.y+b.y);
			e.z = 0.5*(a.z+b.z);

			e.s = rand2(a.s,b.s);

			if (lab>1.0) lab = Math.pow(lab,0.75);

			e.h = 0.5*(a.h+b.h)
					+e.s*(
					this.altitudeWeight*Math.abs(a.h-b.h)
							+this.distanceWeight*Math.pow(lab,0.45));

			/* calculate approximate rain shadow for new point */
			if (e.h <= 0.0) e.shadow = 0.0;
			else {
				x1 = 0.5*(a.x+b.x);
				x1 = a.h*(x1-a.x)+b.h*(x1-b.x);
				y1 = 0.5*(a.y+b.y);
				y1 = a.h*(y1-a.y)+b.h*(y1-b.y);
				z1 = 0.5*(a.z+b.z);
				z1 = a.h*(z1-a.z)+b.h*(z1-b.z);
				l1 = Math.sqrt(x1*x1+y1*y1+z1*z1);
				if (l1==0.0) l1 = 1.0;
				tmp = Math.sqrt(1.0-y*y);
				if (tmp<0.0001) tmp = 0.0001;
				x2 = x*x1+y*y1+z*z1;
				z2 = -z/tmp*x1+x/tmp*z1;
				if (lab > 0.04)
					e.shadow = (a.shadow + b.shadow- Math.cos(PI*this.shadeAngle/180.0)*z2/l1)/3.0;
				else
					e.shadow = (a.shadow + b.shadow)/2.0;
			}

			eax = a.x-e.x; eay = a.y-e.y; eaz = a.z-e.z;
			epx = x-e.x; epy = y-e.y; epz = z-e.z;
			ecx = c.x-e.x; ecy = c.y-e.y; ecz = c.z-e.z;
			edx = d.x-e.x; edy = d.y-e.y; edz = d.z-e.z;

			if ((eax*ecy*edz+eay*ecz*edx+eaz*ecx*edy
					-eaz*ecy*edx-eay*ecx*edz-eax*ecz*edy)*
					(epx*ecy*edz+epy*ecz*edx+epz*ecx*edy
							-epz*ecy*edx-epy*ecx*edz-epx*ecz*edy)>0.0)
			{
				return (planet(c, d, a, e, x, y, z, level - 1));
			}
			else
			{
				return (planet(c, d, b, e, x, y, z, level - 1));
			}
		}
		else
		{
			if (this.doshade)
			{
				x1 = 0.25*(a.x+b.x+c.x+d.x);
				x1 = a.h*(x1-a.x)+b.h*(x1-b.x)+c.h*(x1-c.x)+d.h*(x1-d.x);
				y1 = 0.25*(a.y+b.y+c.y+d.y);
				y1 = a.h*(y1-a.y)+b.h*(y1-b.y)+c.h*(y1-c.y)+d.h*(y1-d.y);
				z1 = 0.25*(a.z+b.z+c.z+d.z);
				z1 = a.h*(z1-a.z)+b.h*(z1-b.z)+c.h*(z1-c.z)+d.h*(z1-d.z);
				l1 = Math.sqrt(x1*x1+y1*y1+z1*z1);
				if (l1==0.0) l1 = 1.0;
				tmp = Math.sqrt(1.0-y*y);
				if (tmp<0.0001) tmp = 0.0001;
				x2 = x*x1+y*y1+z*z1;
				y2 = -x*y/tmp*x1+tmp*y1-z*y/tmp*z1;
				z2 = -z/tmp*x1+x/tmp*z1;
				shade = (int)((-Math.sin(PI*this.shadeAngle/180.0)*y2-Math.cos(PI*this.shadeAngle/180.0)*z2)
						/l1*48.0+128.0);
				shade = (int)((shade - 128)/1.5)+128;
				if (shade<10) shade = 10;
				if (shade>255) shade = 255;
			}
			rainShadow  = 0.25*(a.shadow+b.shadow+c.shadow+d.shadow);
			return((a.h+b.h+c.h+d.h)/4);
		}
	}

	public static double[] xyzToPhiRho(double _x, double _y, double _z)
	{
		return new double[] { Math.atan2(_y, _x), Math.asin(_z) };
	}

	public static double[] phiRhoToXYZ(double _p, double _r)
	{
		double v[]=new double[3];
		v[2]= Math.sin(_r);
		double coZ= Math.cos(_r);
		v[0]= Math.cos(_p) *coZ;
		v[1]= Math.cos(_p+Math.PI/2.0) *coZ;
		return v;
	}

	public int getWaterLandPercentage() {
		return waterLandPercentage;
	}

	public void setWaterLandPercentage(int waterLandPercentage) {
		this.waterLandPercentage = waterLandPercentage;
	}

	int waterLandPercentage = -1;
}
