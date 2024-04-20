package com.github.terefang.randy.starsys;

import com.github.terefang.randy.RandyUtil;
import com.github.terefang.randy.fractal.IFractal;
import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.NoiseUtil;
import lombok.Data;

import java.lang.reflect.Array;

@Data
public abstract class AbstractStarCube<T>
{
	T [][][] data = null;

	INoise noiseType = RandyUtil.simplexNoise(NoiseUtil.BASE_SEED1);
	INoise secondaryNoiseType = RandyUtil.foamNoise(RandyUtil.valueNoise(NoiseUtil.BASE_SEED1, NoiseUtil.HERMITE), NoiseUtil.BASE_SEED2, NoiseUtil.BASE_SHARPNESS);
	INoise tertiaryNoiseType = RandyUtil.perlinNoise(NoiseUtil.BASE_SEED3, NoiseUtil.HERMITE);

	IFractal fractalType;

	int bits;
	int size;
	int mask;
	long seed;
	float offset = 0f;
	double H = NoiseUtil.BASE_H;
	int octaves = NoiseUtil.BASE_OCTAVES;
	double frequency = NoiseUtil.BASE_FREQUENCY;
	double secondaryFrequency = 1.;
	double tertiaryFrequency = 1.;
	double lacunarity = NoiseUtil.BASE_LACUNARITY;
	double gain = NoiseUtil.BASE_GAIN;
	double harshness = NoiseUtil.BASE_HARSHNESS;
	double mutation = NoiseUtil.BASE_MUTATION;
	double sharpness = NoiseUtil.BASE_SHARPNESS;
	boolean variableSeed = true;

	public void init() {
		this.data = (T[][][]) Array.newInstance(this.clazz(), this.size, this.size, this.size);

		for(int _ix = 0; _ix<this.size; _ix++) {
			for(int _iy = 0; _iy<this.size; _iy++) {
				for(int _iz = 0; _iz<this.size; _iz++) {
					data[_ix][_iy][_iz] = this.calculate(_ix, _iy, _iz);
				}
			}
		}
	}

	public abstract Class<T> clazz();

	public abstract T calculate(int _ix, int _iy, int _iz);
    /*
		NoiseUtil.f_ridged_multi(
			this.noiseType,
			(double)_ix/(double)this.size,
			(double)_iy/(double)this.size,
			(double)_iz/(double)this.size,
			(int)this.seed,
			this.offset,
			this.H,
			this.octaves,
			this.frequency,
			this.lacunarity,
			this.gain,
			this.harshness,
			this.mutation,
			this.sharpness,
			this.variableSeed);
	*/
}