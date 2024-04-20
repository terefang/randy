package com.github.terefang.randy.transf;

import com.github.terefang.randy.noise.NoiseUtil;

public class TypeTransform implements ITransform
{
    @Override
    public String name() {
        return ITransform.super.name()+"~"+this.transformType.name();
    }

    public static TypeTransform from(TransformType _t)
    {
        TypeTransform _tt = new TypeTransform();
        _tt.setTransformType(_t);
        return _tt;
    }

    TransformType transformType;

    public TransformType getTransformType() {
        return transformType;
    }

    public void setTransformType(TransformType transformType) {
        this.transformType = transformType;
    }

    @Override
    public double transform(double _result)
    {
        switch (this.transformType)
        {
            case T_NEAR:
                return (_result/(1.+Math.abs(_result)))*2.;
            case T_EX: try { _result = ((_result-1.) / (_result+1.)); break; } catch (Exception _xe) { _result = Double.MAX_VALUE; break; }
            case T_EXP: _result = -Math.log(0.5 - _result * 0.5) / 2.0; break;
            case T_SINE: _result = Math.sin(_result*Math.PI); break;
            case T_COSINE: _result = Math.cos(_result*Math.PI); break;
            case T_SINE_2: _result = Math.sin(_result*Math.PI/2.); break;
            case T_COSINE_2: _result = Math.cos(_result*Math.PI/2.); break;
            case T_SQ_SINE: _result = Math.sin(_result*_result); break;
            case T_SQ_COSINE: _result = Math.cos(_result*_result); break;
            case T_INVERT: _result = -_result; break;
            case T_SQUARE_ROOT: _result = Math.pow(_result, .5); break;
            case T_CUBE_ROOT: _result = Math.pow(_result, 1./3.); break;
            case T_SQUARE: _result = (_result*_result); break;
            case T_CUBE: _result = (_result*_result*_result); break;
            case T_QUART: _result = (_result*_result*_result*_result); break;
            case T_ABS: _result = Math.abs(_result); break;
            case T_ABS1M: _result = 1.-Math.abs(_result); break;
            case T_IHERMITE: _result = (NoiseUtil.hermiteInterpolator(0.5+_result*0.5) * 2.) - 1.; break;
            case T_IQUINTIC: _result = (NoiseUtil.quinticInterpolator(0.5+_result*0.5) * 2.) - 1.; break;
            case T_IBARRON: _result = (NoiseUtil.barronSpline(0.5+(_result*0.5), .5, .5) * 2.) - 1.; break;
            case T_INVNORM:
                _result = NoiseUtil.inverseNormalization(_result); break;
            case T_QMF:
                _result = NoiseUtil.minkowskiQMF(_result); break;
            case T_ISPLOBBLE:
                _result = NoiseUtil.splobble(_result); break;
            case T_ISPLOBBLEQ:
                _result = NoiseUtil.splobbleQuintic(_result); break;
            case T_BINARY: _result = (_result<0.) ? -.999887f : .999887f; break;
            case T_LEVEL5: _result = ((int)(_result*5.))/5.; break;
            case T_LEVEL10: _result = ((int)(_result*10.))/10.; break;
            case T_LEVEL4: _result = ((int)(_result*4.))/4.; break;
            case T_LEVEL8: _result = ((int)(_result*8.))/8.; break;
            case T_LEVEL16: _result = ((int)(_result*16.))/16.; break;
            case T_LEVEL32: _result = ((int)(_result*32.))/32.; break;
            case T_CLAMP10: {
                if(_result<0.)
                {
                    _result = 0.;
                }
                else
                if(_result>1.)
                {
                    _result = 1.;
                }
                _result = 1 - _result;
                break;
            }
            case T_CLAMP01: {
                if(_result<0.)
                {
                    _result = 0.;
                }
                else
                if(_result>1.)
                {
                    _result = 1.;
                }
                break;
            }
            case T_0NONE: break;
            case T_COLLATZ_1K: _result = NoiseUtil.collatzMutation(_result, 1024); break;
            case T_COLLATZ_4K: _result = NoiseUtil.collatzMutation(_result, 4096); break;
            case T_COLLATZ_SINE_4K: _result = Math.sin(NoiseUtil.collatzMutation(_result, 4096)*Math.PI); break;
            case T_COLLATZ_COSINE_4K: _result = Math.cos(NoiseUtil.collatzMutation(_result, 4096)*Math.PI); break;
            case T_COLLATZ_INVNORM_4K: _result = NoiseUtil.inverseNormalization(NoiseUtil.collatzMutation(_result, 4096)); break;
            default: break;
        }
        return _result;
    }

    @Override
    public double transform(double _result, double _arg1)
    {
        switch (this.transformType)
        {
            case T_NEAR:
                return (_result/(1.+Math.abs(_result)))*2.;
            case T_EX: try { _result = ((_result-1.) / (_result+1.)); break; } catch (Exception _xe) { _result = Double.MAX_VALUE; break; }
            case T_EXP: _result = -Math.log(0.5 - _result * 0.5) / 2.0; break;
            case T_SINE: _result = Math.sin(_result*Math.PI); break;
            case T_COSINE: _result = Math.cos(_result*Math.PI); break;
            case T_SINE_2: _result = Math.sin(_result*Math.PI/2.); break;
            case T_COSINE_2: _result = Math.cos(_result*Math.PI/2.); break;
            case T_SQ_SINE: _result = Math.sin(_result*_result); break;
            case T_SQ_COSINE: _result = Math.cos(_result*_result); break;
            case T_INVERT: _result = -_result; break;
            case T_SQUARE_ROOT: _result = Math.pow(_result, .5); break;
            case T_CUBE_ROOT: _result = Math.pow(_result, 1./3.); break;
            case T_SQUARE: _result = (_result*_result); break;
            case T_CUBE: _result = (_result*_result*_result); break;
            case T_QUART: _result = (_result*_result*_result*_result); break;
            case T_ABS: _result = Math.abs(_result); break;
            case T_ABS1M: _result = 1.-Math.abs(_result); break;
            case T_IHERMITE: _result = (NoiseUtil.hermiteInterpolator(0.5+_result*0.5) * 2.) - 1.; break;
            case T_IQUINTIC: _result = (NoiseUtil.quinticInterpolator(0.5+_result*0.5) * 2.) - 1.; break;
            case T_IBARRON: _result = (NoiseUtil.barronSpline(0.5+(_result*0.5), _arg1, .5) * 2.) - 1.; break;
            case T_QMF:
                _result = NoiseUtil.minkowskiQMF(_result); break;
            case T_INVNORM:
                _result = NoiseUtil.inverseNormalization(_result); break;
            case T_ISPLOBBLE:
                _result = NoiseUtil.splobble(0xb33f1ee7, _result); break;
            case T_ISPLOBBLEQ:
                _result = NoiseUtil.splobbleQuintic(0xb33f1ee7, _result); break;
            case T_BINARY: _result = (_result<0.) ? -.999887f : .999887f; break;
            case T_LEVEL5: _result = ((int)(_result*5.))/5.; break;
            case T_LEVEL10: _result = ((int)(_result*10.))/10.; break;
            case T_LEVEL4: _result = ((int)(_result*4.))/4.; break;
            case T_LEVEL8: _result = ((int)(_result*8.))/8.; break;
            case T_LEVEL16: _result = ((int)(_result*16.))/16.; break;
            case T_LEVEL32: _result = ((int)(_result*32.))/32.; break;
            case T_CLAMP10: {
                if(_result<0.)
                {
                    _result = 0.;
                }
                else
                if(_result>1.)
                {
                    _result = 1.;
                }
                _result = 1 - _result;
                break;
            }
            case T_CLAMP01: {
                if(_result<0.)
                {
                    _result = 0.;
                }
                else
                if(_result>1.)
                {
                    _result = 1.;
                }
                break;
            }
            case T_COLLATZ_1K: _result = NoiseUtil.collatzMutation(_result, 1024); break;
            case T_COLLATZ_4K: _result = NoiseUtil.collatzMutation(_result, 4096); break;
            case T_COLLATZ_SINE_4K: _result = Math.sin(NoiseUtil.collatzMutation(_result, 4096)*Math.PI); break;
            case T_COLLATZ_COSINE_4K: _result = Math.cos(NoiseUtil.collatzMutation(_result, 4096)*Math.PI); break;
            case T_COLLATZ_INVNORM_4K: _result = NoiseUtil.inverseNormalization(NoiseUtil.collatzMutation(_result, 4096)); break;
            case T_0NONE:
            default: break;
        }
        return _result;
    }

    @Override
    public double transform(double _result, double _arg1, double _arg2)
    {
        switch (this.transformType)
        {
            case T_NEAR:
                return (_result/(1.+Math.abs(_result)))*2.;
            case T_EX: try { _result = ((_result-1.) / (_result+1.)); break; } catch (Exception _xe) { _result = Double.MAX_VALUE; break; }
            case T_EXP: _result = -Math.log(0.5 - _result * 0.5) / 2.0; break;
            case T_SINE: _result = Math.sin(_result*Math.PI); break;
            case T_COSINE: _result = Math.cos(_result*Math.PI); break;
            case T_SINE_2: _result = Math.sin(_result*Math.PI/2.); break;
            case T_COSINE_2: _result = Math.cos(_result*Math.PI/2.); break;
            case T_SQ_SINE: _result = Math.sin(_result*_result); break;
            case T_SQ_COSINE: _result = Math.cos(_result*_result); break;
            case T_INVERT: _result = -_result; break;
            case T_SQUARE_ROOT: _result = Math.pow(_result, .5); break;
            case T_CUBE_ROOT: _result = Math.pow(_result, 1./3.); break;
            case T_SQUARE: _result = (_result*_result); break;
            case T_CUBE: _result = (_result*_result*_result); break;
            case T_QUART: _result = (_result*_result*_result*_result); break;
            case T_ABS: _result = Math.abs(_result); break;
            case T_ABS1M: _result = 1.-Math.abs(_result); break;
            case T_IHERMITE: _result = (NoiseUtil.hermiteInterpolator(0.5+_result*0.5) * 2.) - 1.; break;
            case T_IQUINTIC: _result = (NoiseUtil.quinticInterpolator(0.5+_result*0.5) * 2.) - 1.; break;
            case T_IBARRON: _result = (NoiseUtil.barronSpline(0.5+(_result*0.5), _arg1, _arg2) * 2.) - 1.; break;
            case T_INVNORM:
                _result = NoiseUtil.inverseNormalization(_result); break;
            case T_QMF:
                _result = NoiseUtil.minkowskiQMF(_result); break;
            case T_ISPLOBBLE:
                _result = NoiseUtil.splobble(0xb33f1ee7, _result); break;
            case T_ISPLOBBLEQ:
                _result = NoiseUtil.splobbleQuintic(0xb33f1ee7, _result); break;
            case T_BINARY: _result = (_result<0.) ? -.999887f : .999887f; break;
            case T_LEVEL5: _result = ((int)(_result*5.))/5.; break;
            case T_LEVEL10: _result = ((int)(_result*10.))/10.; break;
            case T_LEVEL4: _result = ((int)(_result*4.))/4.; break;
            case T_LEVEL8: _result = ((int)(_result*8.))/8.; break;
            case T_LEVEL16: _result = ((int)(_result*16.))/16.; break;
            case T_LEVEL32: _result = ((int)(_result*32.))/32.; break;
            case T_CLAMP10: {
                if(_result<0.)
                {
                    _result = 0.;
                }
                else
                if(_result>1.)
                {
                    _result = 1.;
                }
                _result = 1 - _result;
                break;
            }
            case T_CLAMP01: {
                if(_result<0.)
                {
                    _result = 0.;
                }
                else
                if(_result>1.)
                {
                    _result = 1.;
                }
                break;
            }
            case T_COLLATZ_1K: _result = NoiseUtil.collatzMutation(_result, 1024); break;
            case T_COLLATZ_4K: _result = NoiseUtil.collatzMutation(_result, 4096); break;
            case T_COLLATZ_SINE_4K: _result = Math.sin(NoiseUtil.collatzMutation(_result, 4096)*Math.PI); break;
            case T_COLLATZ_COSINE_4K: _result = Math.cos(NoiseUtil.collatzMutation(_result, 4096)*Math.PI); break;
            case T_COLLATZ_INVNORM_4K: _result = NoiseUtil.inverseNormalization(NoiseUtil.collatzMutation(_result, 4096)); break;
            case T_0NONE:
            default: break;
        }
        return _result;
    }
}
