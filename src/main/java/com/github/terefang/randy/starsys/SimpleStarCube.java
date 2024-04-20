package com.github.terefang.randy.starsys;

import com.github.terefang.randy.fractal.impl.RidgedMultiFractal;
import lombok.Data;
import com.github.terefang.randy.starsys.model.SystemContext;

public class SimpleStarCube extends AbstractStarCube<SimpleStarCube.SimpleStar>
{
	@Override
	public void init() {
		this.setFractalType(new RidgedMultiFractal());
		super.init();
	}

	@Override
	public SimpleStar calculate(int _ix, int _iy, int _iz)
	{
		SimpleStar _ret = new SimpleStar();

		_ret.setDX(this.getTertiaryNoiseType().noise3( ((_ix << 8) ^ (_iy << 16) ^ _iz), ((double)_ix)*this.getTertiaryFrequency()/(double)this.getSize(), ((double)_iy)*this.getTertiaryFrequency()/(double)this.getSize()));
		_ret.setDY(this.getTertiaryNoiseType().noise3( ((_iy << 8) ^ (_iz << 16) ^ _ix), ((double)_iz)*this.getTertiaryFrequency()/(double)this.getSize(), ((double)_iy)*this.getTertiaryFrequency()/(double)this.getSize()));
		_ret.setDZ(this.getTertiaryNoiseType().noise3( ((_iz << 8) ^ (_ix << 16) ^ _iy), ((double)_iz)*this.getTertiaryFrequency()/(double)this.getSize(), ((double)_ix)*this.getTertiaryFrequency()/(double)this.getSize()));

		_ret.setLocalX(50. + ((((double)_ix)+_ret.getDX()) - (this.getSize() / 2.)) * 100.);
		_ret.setLocalY(50. + ((((double)_iy)+_ret.getDY()) - (this.getSize() / 2.)) * 100.);
		_ret.setLocalZ(50. + ((((double)_iz)+_ret.getDZ()) - (this.getSize() / 2.)) * 100.);

		_ret.setNoiseX(Math.abs(this.getNoiseType().noise3(this.getSeed(), ((double)_ix)*this.getFrequency()/(double)this.getSize(), ((double)_iy)*this.getFrequency()/(double)this.getSize(), ((double)_iz)*this.getFrequency()/(double)this.getSize())));
		_ret.setNoiseY(Math.abs(this.getNoiseType().noise3(this.getSeed(), ((double)_iy)*this.getFrequency()/(double)this.getSize(), ((double)_iz)*this.getFrequency()/(double)this.getSize(), ((double)_ix)*this.getFrequency()/(double)this.getSize())));
		_ret.setNoiseZ(Math.abs(this.getNoiseType().noise3(this.getSeed(), ((double)_iz)*this.getFrequency()/(double)this.getSize(), ((double)_ix)*this.getFrequency()/(double)this.getSize(), ((double)_iy)*this.getFrequency()/(double)this.getSize())));
		_ret.setNoise(_ret.getNoiseX()+_ret.getNoiseY()+_ret.getNoiseZ());

		this.getFractalType().setNoise(this.getNoiseType());
		this.getFractalType().setGain(this.getGain());
		this.getFractalType().setH(this.getH());
		this.getFractalType().setOctaves(this.getOctaves());
		this.getFractalType().setFrequency(this.getFrequency());
		this.getFractalType().setLacunarity(this.getLacunarity());
		this.getFractalType().setOffset(this.getOffset());
		this.getFractalType().setVseed(this.isVariableSeed());

		_ret.setNoiseU(this.getFractalType().fractal3((double)_ix/(double)this.getSize(), (double)_iy/(double)this.getSize(), (double)_iz/(double)this.getSize()));
		_ret.setNoiseV(Math.abs(this.getSecondaryNoiseType().noise3(this.getSeed(), ((double)_iy)*this.getSecondaryFrequency()/(double)this.getSize(), ((double)_iz)*this.getSecondaryFrequency()/(double)this.getSize(), ((double)_ix)*this.getSecondaryFrequency()/(double)this.getSize())));
		_ret.setNoiseW(Math.abs(this.getSecondaryNoiseType().noise3(this.getSeed(), ((double)_iz)*this.getSecondaryFrequency()/(double)this.getSize(), ((double)_ix)*this.getSecondaryFrequency()/(double)this.getSize(), ((double)_iy)*this.getSecondaryFrequency()/(double)this.getSize())));

		_ret.setId((_iz & this.getMask()) | ((_iy & this.getMask())<<this.getBits()) | ((_ix & this.getMask())<<(this.getBits()*2)));
		_ret.init((int) (this.getSeed() ^ _ret.getId()), _ret.getNoise());
		return _ret;
	}

	@Override
	public Class<SimpleStar> clazz() {
		return SimpleStar.class;
	}

	@Data
	public static class SimpleStar
	{
		long id;
		double dX;
		double dY;
		double dZ;
		double localX;
		double localY;
		double localZ;

		double noiseX;
		double noiseY;
		double noiseZ;
		double noise;

		double noiseU;
		double noiseV;
		double noiseW;

		SystemContext context;

		public void init(int _seed, double _mass)
		{
			this.context = new SystemContext();
			this.context.setId(id);
			this.context.setSeed(_seed);
			if(_mass<0.)
			{
				this.context.init();
			}
			else
			{
				this.context.init(_mass);
			}
		}
	}
}
