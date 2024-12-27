package com.github.terefang.randy.nfield;

import com.github.terefang.randy.fractal.IFractal;
import com.github.terefang.randy.kernel.IKernel;
import com.github.terefang.randy.noise.INoise;
import com.github.terefang.randy.noise.NoiseUtil;
import com.github.terefang.randy.rng.impl.ArcRand;
import com.github.terefang.randy.transf.ITransform;

import java.util.Vector;

public class NoiseField
{
    public interface NoiseFieldProcess
    {
        public void process(NoiseField _nf);
    }
    /* FIELD_PROJECTION */
    public static final int FP_NONE = 0; // direct mapping in XYZ
    public static final int FP_SINCOS = 1;
    public static final int FP_EQUIRECTANGULAR = 1;
    public static final int FP_EQUIDISTANT_CYLINDRICAL = 1;
    public static final int FP_GEOGRAPHIC = 1;

    private int currProjection;
    private double projLeft=-1;
    private double projRight=1;
    private double projTop=1;
    private double projBottom=-1;
    private double vF[] = null;
    private int fH,fW, fHoff,fWoff;

    public NoiseField clone()
    {
        NoiseField nf = new NoiseField(fW,fH);
        System.arraycopy(vF,0,nf.vF,0,fH*fW);
        nf.projLeft=projLeft;
        nf.projRight=projRight;
        nf.projTop=projTop;
        nf.projBottom=projBottom;
        nf.currProjection=currProjection;
        return nf;
    }

    public static double[] PhiRhoToXYZ(double p, double r)
    {
        double v[]=new double[3];
        v[2]= Math.sin(r);
        double coZ= Math.cos(r);
        v[0]= Math.cos(p) *coZ;
        v[1]= Math.cos(p+Math.PI/2.0) *coZ;
        return v;
    }

    public NoiseField(int x, int y)
    {
        fH=y;
        fW=x;
        vF = new double[fH*fW];
        currProjection=FP_NONE;
        fHoff=0;
        fWoff=0;
    }

    public NoiseField()
    {
        fH=0;
        fW=0;
        vF = null;
        currProjection=FP_NONE;
        fHoff=0;
        fWoff=0;
    }
    
    public static class er_vec2
    {
        public double x;
        public double y;
        
        public static er_vec2 from(double _1, double _2)
        {
            er_vec2 _v = new er_vec2();
            _v.x=_1;
            _v.y=_2;
            return _v;
        }
        
        public er_vec2 add(er_vec2 _v)
        {
            return from(this.x+_v.x,this.y+_v.y);
        }
        
        public er_vec2 sub(er_vec2 _v)
        {
            return from(this.x-_v.x,this.y-_v.y);
        }
        
        public er_vec2 mul(double _v)
        {
            return from(this.x*_v,this.y*_v);
        }
        
        public er_vec2 clone()
        {
            return from(this.x,this.y);
        }
        
        public er_vec2 normalize()
        {
            double _l = Math.sqrt(this.x*this.x+this.y*this.y);
            if(_l<=0.) return from(this.x,this.y);
            return from(this.x/_l,this.y/_l);
        }
        
        @Override
        public String toString()
        {
            return String.format("[ %f, %f ]", this.x, this.y);
        }
    }
    
    public static class er_particle
    {
        public er_vec2 pos;
        public er_vec2 dir;
        public double vel;
        public double sediment;
        public double water;
    }
    
    /*
     * gradient & height tuple.
     */
    public static class er_hg_tuple
    {
        public er_vec2 gradient;
        public double height;
        
        public static er_hg_tuple from(er_vec2 gradient,double height)
        {
            er_hg_tuple _v = new er_hg_tuple();
            _v.gradient = gradient;
            _v.height = height;
            return _v;
        }
    }
    public static class er_sim_params
    {
        public int _n;
        public int _ttl;
        public int _radius;
        public double _enertia;
        public double _capacity;
        public double _gravity;
        public double _evaporation;
        public double _erosion;
        public double _deposition;
        public double _min_slope;
        
        public static er_sim_params from(int _n,
                                         int _ttl,
                                         int _radius,
                                         double _enertia,
                                         double _capacity,
                                         double _gravity,
                                         double _evaporation,
                                         double _erosion,
                                         double _deposition,
                                         double _min_slope)
        {
            er_sim_params _param = new er_sim_params();
            _param._n = _n;
            _param._ttl = _ttl;
            _param._radius = _radius;
            _param._enertia = _enertia;
            _param._capacity = _capacity;
            _param._gravity = _gravity;
            _param._evaporation = _evaporation;
            _param._erosion =_erosion;
            _param._deposition = _deposition;
            _param._min_slope = _min_slope;
            return  _param;
        }
        
        public static er_sim_params predef()
        {
            return from(70000, 30, 2, 0.1, 10, 4, 0.1, 0.1, 1, 0.0001);
        }
    }
    
    public static void erode(NoiseField _nf, er_vec2 _pos, double amount, int radius)
    {
        erode(_nf,_pos.x,_pos.y,amount,radius);
    }
    
    public static void erode(NoiseField _nf, double _px, double _py, double amount, int radius)
    {
        if(radius < 1){
            deposit(_nf,_px,_py, -amount);
            return;
        }
        
        int x0 = (int)_px - radius;
        int y0 = (int)_py - radius;
        int x_start = Math.max(0, x0);
        int y_start = Math.max(0, y0);
        int x_end = Math.min(_nf.getWidth(), x0+2*radius+1);
        int y_end = Math.min(_nf.getHeight(), y0+2*radius+1);
        
        // construct erosion/deposition kernel.
        double[][] _kernel = new double[2*radius + 1][2*radius + 1];
        double kernel_sum = 0;
        for(int y = y_start; y < y_end; y++) {
            for(int x = x_start; x < x_end; x++) {
                double d_x = x - _px;
                double d_y = y - _py;
                double distance = Math.sqrt(d_x*d_x + d_y*d_y);
                double w = Math.max(0, radius - distance);
                kernel_sum += w;
                _kernel[y-y0][x-x0] = w;
            }
        }
        
        // normalize weights and apply changes on heighmap.
        for(int y = y_start; y < y_end; y++) {
            for(int x = x_start; x < x_end; x++) {
                _kernel[y-y0][x-x0] /= kernel_sum;
                _nf.setPoint(x,y,_nf.getPoint(x,y)-(amount * _kernel[y-y0][x-x0]));
            }
        }
    }
    
    public static er_vec2 gradient_at(NoiseField _nf,  int x, int y) {
        return er_vec2.from(
                _nf.getPoint(x+1,y) - _nf.getPoint(x,y),
                _nf.getPoint(x,y+1) - _nf.getPoint(x,y)
        );
    }
    public static er_hg_tuple height_gradient_at(NoiseField _nf, er_vec2 _pos)
    {
        return height_gradient_at(_nf,_pos.x,_pos.y);
    }
    
    public static er_hg_tuple height_gradient_at(NoiseField _nf, double _px, double _py)
    {
        er_vec2 ul, ur, ll, lr, ipl_l, ipl_r;

        int x_i = (int)_px;
        int y_i = (int)_py;
        double u = _px - x_i;
        double v = _py - y_i;

        ul = gradient_at(_nf, x_i, y_i);
        ur = gradient_at(_nf, x_i + 1, y_i);
        ll = gradient_at(_nf, x_i, y_i + 1);
        lr = gradient_at(_nf, x_i + 1, y_i + 1);

        ipl_l = ul.mul(1 - v).add(ll.mul(v));
        ipl_r = ur.mul(1 - v).add(lr.mul(v));

        return er_hg_tuple.from(
                ipl_l.mul(1 - u).add(ipl_r.mul(u)),
                bil_interpolate_map_double(_nf, _px,_py)
        );
    }
    
    public static void simulate_particles(NoiseField _nf, ArcRand _rng, er_sim_params _params)
    {
        boolean[][] _poisson = new boolean[_nf.getWidth()][_nf.getHeight()];
        for(int i = 0; i < _params._n; i++) {
            if(((i+1) % 100)==0)
                System.err.printf("Particles simulated: %d\n", i+1);
            
            // spawn particle.
            er_particle p = new er_particle();
            
            p.pos = er_vec2.from(_rng.nextDouble()*(double)_nf.getWidth(), _rng.nextDouble()*(double)_nf.getHeight());
            if(_poisson[(int)p.pos.x][(int)p.pos.y])
            {
                i--;
                continue;
            }
            _poisson[(int)p.pos.x][(int)p.pos.y] = true;
            p.dir = er_vec2.from(0, 0);
            p.vel = 0;
            p.sediment = 0;
            p.water = 1;
            
            for(int j = 0; j < _params._ttl; j++) {
                // interpolate gradient g and height h_old at p's position.
                er_vec2 pos_old = p.pos.clone();
                er_hg_tuple hg = height_gradient_at(_nf, pos_old);
                er_vec2 g = hg.gradient.clone();
                double h_old = hg.height;
                
                // calculate new dir vector
                p.dir = p.dir.mul(_params._enertia).sub(g.mul(1 - _params._enertia ));
                
                p.dir = p.dir.normalize();
                
                // calculate new pos
                p.pos = p.pos.add(p.dir);
                
                // check bounds
                er_vec2 pos_new = p.pos.clone();
                if(pos_new.x > (_nf.getWidth()-1) || pos_new.x < 0 ||
                        pos_new.y > (_nf.getHeight()-1) || pos_new.y < 0)
                    break;
                
                // new height
                double h_new = bil_interpolate_map_double(_nf, pos_new);
                double h_diff = h_new - h_old;
                
                // sediment capacity
                double c = Math.max(-h_diff, _params._min_slope) * p.vel * p.water * _params._capacity;
                
                // decide whether to erode or deposit depending on particle properties
                if(h_diff > 0 || p.sediment > c)
                {
                    double to_deposit = (h_diff > 0) ?
                            Math.min(p.sediment, h_diff) :
                            (p.sediment - c) * _params._deposition;
                    p.sediment -= to_deposit;
                    deposit(_nf, pos_old, to_deposit);
                }
                else
                {
                    double to_erode = Math.min((c - p.sediment) * _params._erosion, -h_diff);
                    p.sediment += to_erode;
                    erode(_nf, pos_old, to_erode, _params._radius);
                }
                
                // update `vel` and `water`
                p.vel = Math.sqrt(Math.max(p.vel*p.vel + (-h_diff)*_params._gravity,0));
                p.water *= (1 - _params._evaporation);
            }
        }
    }
    
    public static void deposit(NoiseField _nf, er_vec2 _pos, double amount)
    {
        deposit(_nf, _pos.x, _pos.y,amount);
    }

    public static void deposit(NoiseField _nf, double _px, double _py, double amount)
    {
        int x_i = (int)_px;
        int y_i = (int)_py;
        double u = _px - x_i;
        double v = _py - y_i;
        
        _nf.setPoint(x_i,y_i,_nf.getPoint(x_i,y_i)+(amount * (1 - u) * (1 - v)));
        _nf.setPoint(x_i+1,y_i,_nf.getPoint(x_i+1,y_i)+(amount * u * (1 - v)));
        _nf.setPoint(x_i,y_i+1,_nf.getPoint(x_i,y_i+1)+(amount * (1 - u) * v));
        _nf.setPoint(x_i+1,y_i+1,_nf.getPoint(x_i+1,y_i+1)+(amount * u * v));
    }

    public static double bil_interpolate_map_double(NoiseField _nf, er_vec2 _pos)
    {
        return bil_interpolate_map_double(_nf, _pos.x, _pos.y);
    }
    
    public static double bil_interpolate_map_double(NoiseField _nf, double _px, double _py)
    {
        double u, v, ul, ur, ll, lr, ipl_l, ipl_r;
        int x_i = (int)_px;
        int y_i = (int)_py;
        u = _px - x_i;
        v = _py - y_i;
        
        ul = _nf.getPoint(x_i,y_i);
        ur = _nf.getPoint(x_i+1,y_i);
        ll = _nf.getPoint(x_i,y_i+1);
        lr = _nf.getPoint(x_i+1,y_i+1);
        
        ipl_l = (1 - v) * ul + v * ll;
        ipl_r = (1 - v) * ur + v * lr;
        
        return (1 - u) * ipl_l + u * ipl_r;
    }
    
    public static void eroder(NoiseField _nf, ArcRand _rng, int _n, int _ttl, int _radius, double _enertia, double _capacity, double _gravity, double _evaporation, double _erosion, double _deposition, double _min_slope)
    {
        er_sim_params _param = er_sim_params.from(_n, _ttl, _radius, _enertia, _capacity, _gravity, _evaporation, _erosion, _deposition, _min_slope);
        eroder(_nf, _rng, _param);
    }
    
    public static void eroder(NoiseField _nf, ArcRand _rng, er_sim_params _param)
    {
        if(_param == null) _param = er_sim_params.predef();
        
        simulate_particles(_nf, _rng,_param);
    }
    
    public void eroder(ArcRand _rng, int _n, int _ttl, int _radius, double _enertia, double _capacity, double _gravity, double _evaporation, double _erosion, double _deposition, double _min_slope)
    {
        eroder(this, _rng,_n, _ttl, _radius, _enertia, _capacity, _gravity, _evaporation, _erosion, _deposition, _min_slope);
    }
    
    public void eroder(ArcRand _rng, er_sim_params _param)
    {
        if(_param == null) _param = er_sim_params.predef();
        
        simulate_particles(this, _rng,_param);
    }
    
    public void add(NoiseField noisefield)
    {
        this.add(noisefield, false);
    }

    public void add(NoiseField noisefield, boolean replacePositive)
    {
        if(noisefield.getHeight()==this.getHeight() && noisefield.getWidth()==this.getWidth())
        {
            for(int i = this.vF.length-1; i>=0; i--)
            {
                if(replacePositive && noisefield.vF[i]>0)
                {
                    this.vF[i] = noisefield.vF[i];
                }
                else
                {
                    this.vF[i] += noisefield.vF[i];
                }
            }
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }

    public void add(double k)
    {
        for(int i = this.vF.length-1; i>=0; i--)
        {
            this.vF[i]+=k;
        }
    }

    public void setXOff(int o)
    {
        fWoff=o;
    }

    public void setYOff(int o)
    {
        fHoff=o;
    }

    public int getWidth() { return fW; }

    public int getHeight() { return fH; }

    public double[] getData() { return vF; }

    public double getPoint(int x, int y)
    {
        if(x>=fW) x=fW-1;
        if(y>=fH) y=fH-1;
        if(x<0) x=0;
        if(y<0) y=0;
        return vF[fW*y+x];
    }

    public void setPoint(int x, int y, double v)
    {
        if(x>=fW) x=fW-1;
        if(y>=fH) y=fH-1;
        if(x<0) x=0;
        if(y<0) y=0;
        vF[fW*y+x]=v;
    }

    public double getPointT(int x, int y)
    {
        while(x<0) x+=fW;
        while(y<0) y+=fH;
        x%=fW;
        y%=fH;
        return vF[fW*y+x];
    }

    public void setPointT(int x, int y, double v)
    {
        while(x<0) x+=fW;
        while(y<0) y+=fH;
        x%=fW;
        y%=fH;
        vF[fW*y+x]=v;
    }

    public void scaleUpX()
    {
        int tH=(2*fH)-1;
        int tW=(2*fW)-1;
        double tF[] = new double[tH*tW];

        for(int y=fH-1; y>=0 ; y--)
        {
            for(int x=fW-1; x>=0 ; x--)
            {
                int tx=x*2;
                int ty=y*2;
                tF[tW*ty+tx]=this.getPoint(x, y);
                if(x!=0 && y!=0)
                {
                    tF[tW*(ty-1)+tx-1]=tF[tW*ty+tx]+this.getPoint(x-1, y-1);
                    tF[tW*(ty-1)+tx-1]/=2.0;
                }
                if(x!=0)
                {
                    tF[tW*ty+tx-1]=tF[tW*ty+tx]+this.getPoint(x-1, y);
                    tF[tW*ty+tx-1]/=2.0;
                }
                if(y!=0)
                {
                    tF[tW*(ty-1)+tx]=tF[tW*ty+tx]+this.getPoint(x, y-1);
                    tF[tW*(ty-1)+tx]/=2.0;
                }
            }
        }
        vF=tF;
        fH=tH;
        fW=tW;
    }


    public void scaleUp()
    {
        double kA = (Math.PI/5.0);//0.5;
        double kB = 1.0-kA;

        int tH=(2*fH)-1;
        int tW=(2*fW)-1;
        double tF[] = new double[tH*tW];

        for(int y=fH-1; y>0 ; y--)
        {
            for(int x=fW-1; x>0 ; x--)
            {
                int tx=x*2;
                int ty=y*2;

                double xy = this.getPoint(x, y);
                double x1y = this.getPoint(x - 1, y);
                double xy1 = this.getPoint(x, y-1);
                double x1y1 = this.getPoint(x-1, y-1);

                tF[tW*ty+tx] = xy;
                tF[tW*(ty)+(tx-2)] = x1y;
                tF[tW*(ty-2)+(tx)] = xy1;
                tF[tW*(ty-2)+(tx-2)] = x1y1;

                tF[tW*(ty-1)+(tx-2)] = NoiseUtil.lerp(x1y1, x1y, kA);
                tF[tW*(ty-2)+(tx-1)] = NoiseUtil.lerp(x1y1, xy1, kB);

                tF[tW*(ty-1)+(tx)] = NoiseUtil.lerp(xy, xy1, kA);
                tF[tW*(ty)+(tx-1)] = NoiseUtil.lerp(xy, x1y, kB);

                tF[tW*(ty-1)+(tx-1)] = (
                        tF[tW*ty+tx]
                                +tF[tW*(ty)+(tx-2)]
                                +tF[tW*(ty-2)+(tx)]
                                +tF[tW*(ty-2)+(tx-2)]

                                +tF[tW*(ty-1)+(tx-2)]
                                +tF[tW*(ty-2)+(tx-1)]

                                +tF[tW*(ty-1)+(tx)]
                                +tF[tW*(ty)+(tx-1)]
                )/8.0;

            }
        }

        vF=tF;
        fH=tH;
        fW=tW;
    }

    public void scaleDown(int nW, int nH)
    {
        double tF[] = new double[nH*nW];
        double dW=((double)fW/(double)nW);
        double dH=((double)fH/(double)nH);
        int sW=(fW/nW)+1;
        int sH=(fH/nH)+1;

        for(int y=nH-1; y>=0 ; y--)
        {
            for(int x=nW-1; x>=0 ; x--)
            {
                double value=0.0;
                double count=0;
                for(int j=0; j<sH ; j++)
                {
                    for(int i=0; i<sW ; i++)
                    {
                        value+=getPoint((int)(x*dW+i), (int)(y*dH+j));
                        count+=1.0000001;
                    }
                }
                tF[nW*y+x]=value/count;
            }
        }

        vF=tF;
        fH=nH;
        fW=nW;
    }

    public void scaleDownByKernel(IKernel kernel)
    {
        int tW = (this.fW/kernel.getWidth());
        int tH = (this.fH/kernel.getHeight());

        int dW = (kernel.getWidth()/2);
        int dH = (kernel.getHeight()/2);

        double tF[] = new double[tW*tH];

        for(int y=0; y<tH; y++)
        {
            for(int x=0; x<tW ; x++)
            {
                tF[tW*y+x]=kernel.calculateSample(this, ((x*kernel.getHeight())+dW), ((y*kernel.getWidth())+dH));
            }
        }

        vF=tF;
        fH=tH;
        fW=tW;
    }

    public void morphologicalErode(int samples)
    {
        this.morphologicalErode(samples, 0.25);
    }

    /**************************************************************************
     * Realtime Procedural Terrain Generation
     * Realtime Synthesis of Eroded Fractal Terrain for Use in Computer Games
     * Jacob Olsen, xenorg@imada.sdu.dk
     * Department of Mathematics And Computer Science (IMADA)
     * University of Southern Denmark
     * October 31, 2004
     */
    public void fastErode(int erosionMask, int cycles, int samples, double sampleScale, double contribution)
    {
        double T = 16.0/(((double)samples)*sampleScale);
        samples/=2;
        while(cycles>0)
        {
            double tV[]=new double[fH*fW];
            for(int i=0; i<fH*fW; i++)
            {
                tV[i] = vF[i];
            }
            for(int y=0; y<fH ; y++)
                for(int x=0; x<fW ; x++)
                {
                    int xy0 = fW*y+x;
                    int xyM = xy0;
                    double h0 = this.getPointT(x,y);
                    if((erosionMask == 0) || ((erosionMask<0) && (h0<0.0)) || ((erosionMask>0) && (h0>0.0)))
                    {
                        double dM = 0.0;
                        for(int j=-samples; j<=samples ; j++)
                            for(int i=-samples; i<=samples ; i++)
                            {
                                double hS = this.getPointT(x+i,y+j);
                                int xy1 = fW*(y+j)+(x+i);
                                double dS = h0-hS;
                                if((dS > dM) && (xy1>=0) && (xy1<tV.length))
                                {
                                    dM=dS;
                                    xyM=xy1;
                                }
                            }
                        if((dM>0.0) && (T>=dM))
                        {
                            double dH = contribution*dM;
                            tV[xy0]-=dH;
                            tV[xyM]+=dH;
                        }
                    }
                }
            vF=tV;
            cycles--;
        }
    }

    /**************************************************************************
     * Procedural Fractal Terrains
     * F. Kenton Musgrave
     * 2.6 Slumping: Forming Talus Slopes
     */
    public void talusErode(int erosionMask, int cycles, double T, double sampleScale, double contribution)
    {
        while(cycles>0)
        {
            double tV[]=new double[fH*fW];
            for(int i=0; i<fH*fW; i++)
            {
                tV[i] = vF[i];
            }
            for(int y=0; y<fH ; y++)
                for(int x=0; x<fW ; x++)
                {
                    int xy0 = fW*y+x;
                    int xyM = xy0;
                    double h0 = this.getPointT(x,y);
                    if((erosionMask == 0) || ((erosionMask<0) && (h0<0.0)) || ((erosionMask>0) && (h0>0.0)))
                    {
                        double dM = 0.0;
                        for(int j=-1; j<=1; j++)
                            for(int i=-1; i<=1 ; i++)
                            {
                                double hS = this.getPointT(x+i,y+j);
                                int xy1 = fW*(y+j)+(x+i);
                                double dS = h0-hS;
                                if((dS > dM) && (xy1>=0) && (xy1<tV.length))
                                {
                                    dM=dS;
                                    xyM=xy1;
                                }
                            }
                        double delta = dM * sampleScale;
                        double dT = delta - T;
                        if(dT > 0.0)
                        {
                            dT*= contribution/sampleScale;
                            tV[xy0]-=dT;
                            tV[xyM]+=dT;
                        }
                    }
                }
            vF=tV;
            cycles--;
        }
    }

    public void morphologicalErode(int samples, double amount)
    {
        double tV[]=new double[fH*fW];
        for(int j=0; j<samples ; j++)
        {
            for(int y=0; y<fH ; y++)
                for(int x=0; x<fW ; x++)
                {
                    tV[fW*y+x]= getPoint(x,y);
                    Vector<Double> list = new Vector();
                    list.add(getPoint(x, y + 1));
                    list.add(getPoint(x,y-1));
                    list.add(getPoint(x+1,y));
                    list.add(getPoint(x+1,y+1));
                    list.add(getPoint(x+1,y-1));
                    list.add(getPoint(x-1,y));
                    list.add(getPoint(x-1,y+1));
                    list.add(getPoint(x-1,y-1));

                    for(double tt : list)
                    {
                        if((tt>=0) && (tt<tV[fW*y+x]))
                        {
                            tV[fW*y+x]-=amount*(tV[fW*y+x]-tt);
                        }
                    }
                }
            // copy back
            for(int i=0 ; i<(fH*fW) ; i++)
                vF[i]=tV[i];
        }
    }

    public void morphologicalErodeT(int samples)
    {
        morphologicalErode(samples, 1.);
    }

    public void morphologicalDilate(int samples)
    {
        double tV[]=new double[fH*fW];
        for(int j=0; j<samples ; j++)
        {
            for(int y=0; y<fH ; y++)
                for(int x=0; x<fW ; x++)
                {
                    tV[fW*y+x]=getPoint(x,y);
                    tV[fW*y+x]=getPoint(x,y+1)>tV[fW*y+x] ? getPoint(x,y+1) : tV[fW*y+x];
                    tV[fW*y+x]=getPoint(x,y-1)>tV[fW*y+x] ? getPoint(x,y-1) : tV[fW*y+x];

                    tV[fW*y+x]=getPoint(x+1,y+1)>tV[fW*y+x] ? getPoint(x+1,y+1) : tV[fW*y+x];
                    tV[fW*y+x]=getPoint(x+1,y  )>tV[fW*y+x] ? getPoint(x+1,y  ) : tV[fW*y+x];
                    tV[fW*y+x]=getPoint(x+1,y-1)>tV[fW*y+x] ? getPoint(x+1,y-1) : tV[fW*y+x];

                    tV[fW*y+x]=getPoint(x-1,y+1)>tV[fW*y+x] ? getPoint(x-1,y+1) : tV[fW*y+x];
                    tV[fW*y+x]=getPoint(x-1,y  )>tV[fW*y+x] ? getPoint(x-1,y  ) : tV[fW*y+x];
                    tV[fW*y+x]=getPoint(x-1,y-1)>tV[fW*y+x] ? getPoint(x-1,y-1) : tV[fW*y+x];
                }
            // copy back
            for(int i=0; i<(fH*fW) ; i++)
                vF[i]=tV[i];
        }
    }

    public void morphologicalDilateT(int samples)
    {
        double tV[]=new double[fH*fW];
        for(int j=0; j<samples ; j++)
        {
            for(int y=0; y<fH ; y++)
                for(int x=0; x<fW ; x++)
                {
                    tV[fW*y+x]=this.getPointT(x, y);
                    tV[fW*y+x]=this.getPointT(x, y + 1)>tV[fW*y+x] ? this.getPointT(x, y + 1) : tV[fW*y+x];
                    tV[fW*y+x]=this.getPointT(x, y - 1)>tV[fW*y+x] ? this.getPointT(x,y-1) : tV[fW*y+x];

                    tV[fW*y+x]=this.getPointT(x + 1, y + 1)>tV[fW*y+x] ? this.getPointT(x + 1,y+1) : tV[fW*y+x];
                    tV[fW*y+x]=this.getPointT(x + 1, y)>tV[fW*y+x]     ? this.getPointT(x + 1,y  ) : tV[fW*y+x];
                    tV[fW*y+x]=this.getPointT(x + 1, y - 1)>tV[fW*y+x] ? this.getPointT(x + 1,y-1) : tV[fW*y+x];

                    tV[fW*y+x]=this.getPointT(x - 1, y + 1)>tV[fW*y+x] ? this.getPointT(x - 1,y+1) : tV[fW*y+x];
                    tV[fW*y+x]=this.getPointT(x - 1, y)>tV[fW*y+x]     ? this.getPointT(x - 1,y  ) : tV[fW*y+x];
                    tV[fW*y+x]=this.getPointT(x - 1, y - 1)>tV[fW*y+x] ? this.getPointT(x - 1,y-1) : tV[fW*y+x];
                }
            // copy back
            for(int i=0; i<(fH*fW) ; i++)
                this.vF[i]=tV[i];
        }
    }

    public void filterKernel(int size, double[] kernel)
    {
        filterKernel(size, kernel, kernel[size*size]);
    }

    public void filterKernel(int size, double[] kernel, double scale)
    {
        double tV[]=new double[fH*fW];
        int b=size>>1;
        for(int i=0; i<(fH*fW) ; i++)
        {
            tV[i]=0.0;
        }
        for(int y=0; y<fH ; y++)
        {
            for(int x=0; x<fW ; x++)
            {
                double tscale=scale;
                for(int j=-b; j<=b ; j++)
                {
                    for(int i=-b; i<=b ; i++)
                    {
                        if((x+i < fW) && (y+j < fH) && (x+i >= 0) && (y+j >= 0))
                        {
                            tV[fW*y+x]+=this.getPointT(x + i, y + j)*kernel[(b+j)*size+(b+i)];
                        }
                        else
                        {
                            tscale-=kernel[(b+j)*size+(b+i)];
                        }
                    }
                }
                tV[fW*y+x]/=tscale;
            }
        }
        // copy back
        for(int i=0; i<(fH*fW) ; i++)
        {
            this.vF[i]=tV[i];
        }
    }

    public void simpleBlur(int samples)
    {
        final double kernelSB[]={
                1.0, 1.0, 1.0,
                1.0, 2.0, 1.0,
                1.0, 1.0, 1.0 };

        for(int j=0 ; j<samples ; j++)
        {
            this.filterKernel(3, kernelSB, 10.0);
        }
    }

    public void pyramidalFilter(int samples)
    {
        final double kernelPF[]={
                1.0,2.0,3.0,2.0,1.0,
                2.0,4.0,6.0,4.0,2.0,
                3.0,6.0,9.0,6.0,3.0,
                2.0,4.0,6.0,4.0,2.0,
                1.0,2.0,3.0,2.0,1.0 };

        for(int j=0; j<samples ; j++)
            this.filterKernel(5, kernelPF, 81.0);
    }

    public void gaussFilter(int _size, int samples)
    {
        gaussFilter(_size, 1.8, samples);
    }
    public void gaussFilter(int _size, double sigma, int samples)
    {
        _size = _size|1;
        final double kernelPF[]= gaussKernel(_size, sigma);
        // sum is for normalization
        double sum = kernelPF[_size*_size];

        for(int j=0; j<samples ; j++)
            this.filterKernel(_size, kernelPF, sum);
    }

    public double[] gaussKernel(int _size, double sigma)
    {
        // initialising standard deviation to 1.0
        double r, s = 2.0 * sigma * sigma;

        // sum is for normalization
        double sum = 0.0;

        _size = _size|1;
        int _rsize = (_size >> 1);
        double kernelPF[]= new double[_size*_size+1];

        // generating 5x5 kernel
        for (int x = -_rsize; x <= _rsize; x++) {
            for (int y = -_rsize; y <= _rsize; y++) {
                r = Math.sqrt(x * x + y * y);
                kernelPF[(x + _rsize) + _size*(y + _rsize)] = (Math.exp(-(r * r) / s)) / (Math.PI * s);
                sum += kernelPF[(x + _rsize) + _size*(y + _rsize)];
            }
        }

        kernelPF[_size*_size] = sum;
        return kernelPF;
    }

    public void coneFilter(int samples)
    {
        final double kernelCF[]={
                0.0,0.0,1.0,0.0,0.0,
                0.0,2.0,2.0,2.0,0.0,
                1.0,2.0,5.0,2.0,1.0,
                0.0,2.0,2.0,2.0,0.0,
                0.0,0.0,1.0,0.0,0.0 };

        for(int j=0; j<samples ; j++)
            this.filterKernel(5, kernelCF, 25.0);
    }

    public void mathExponent(double sealevel, double land_exp, double sea_exp)
    {
        for(int i=0; i<fH*fW ; i++)
        {
            if(this.vF[i]<=sealevel)
            {
                this.vF[i]= -Math.pow(Math.abs(this.vF[i]-sealevel),sea_exp) +sealevel;
            }
            else
            {
                this.vF[i]= Math.pow(this.vF[i]-sealevel,land_exp) +sealevel;
            }
        }
    }

    public void mathScale(double sealevel, double land, double sea)
    {
        for(int i=0; i<fH*fW ; i++)
        {
            if(this.vF[i]<=sealevel)
            {
                this.vF[i]= -Math.abs(this.vF[i]-sealevel)*sea +sealevel;
            }
            else
            {
                this.vF[i]= (this.vF[i]-sealevel)*land +sealevel;
            }
        }
    }

    public void mathClip(double seaMax, double landMax)
    {
        for(int i=0; i<fH*fW ; i++)
        {
            if(this.vF[i]<seaMax)
            {
                this.vF[i]=seaMax;
            }
            else if(this.vF[i]>landMax)
            {
                this.vF[i]=landMax;
            }
        }
    }

    public void clear()
    {
        for(int i=0; i<fH*fW ; i++)
            this.vF[i]=0.0;
    }

    public void dispose() {
        this.vF=null;
    }

    public interface NormalizingFunction
    {
        double normalize(double v);
    }


    public void normalize(NormalizingFunction p)
    {
        for(int i=0; i<fH*fW ; i++)
        {
            this.vF[i]=p.normalize(this.vF[i]);
        }
    }

    public void normalize(double ratio)
    {
        double span=7654.3;
        normalize(-span, span);
        double nN=0, nM=0;
        for(int i=0; i<fH*fW ; i++)
        {
            if(this.vF[i]<0.0)
            {
                nN++;
            }
            else
            {
                nM++;
            }
        }
        nN= Math.pow(nN,0.5);
        nM= Math.pow(nM,0.5);
        //System.err.println("N( "+nN+" : "+nM+" )");
        double f=ratio;
        double fN=0.5*(nM-nN)/(nM+nN);
        if(fN<0)
        {
            f-= Math.pow(Math.abs(fN),2.0);
        }
        else
        {
            f+= Math.pow(fN,2.0);
        }
        //System.err.println("F( "+f+" : "+fN+" )");
        for(int i=0; i<fH*fW ; i++)
        {
            this.vF[i]=((this.vF[i]+span)*(1-ratio))-(span*f);
        }
    }

    public void normalize(double _min, double _max)
    {
        double _amax,_amin,_adist,_vdist;
        double _distance=_max-_min;
        _amax=_amin=this.vF[0];
        for(int i=0; i<fH*fW ; i++)
        {
            if(this.vF[i]>_amax) _amax=this.vF[i];
            if(this.vF[i]<_amin) _amin=this.vF[i];
        }
        _adist=_amax-_amin;
        if(_adist==0.0)
        {
            _amin-=1.0;
            _adist=2.0;
        }
        _vdist=_distance/_adist;
        for(int i=0; i<fH*fW ; i++)
        {
            this.vF[i]=_min+(this.vF[i]-_amin)*_vdist;
        }
    }

    public double normalize(double _min, double _max, int _x1, int _x2, int _y1, int _y2)
    {
        double _amax,_amin,_adist,_vdist;
        double _distance=_max-_min;
        _amax=_amin=this.getPoint(_x1,_y1);

        for(int _x=_x1; _x<=_x2 ; _x++)
        {
            for(int _y=_y1; _y<=_y2 ; _y++) {
                double _p = this.getPoint(_x,_y);
                if (_p > _amax) _amax = _p;
                if (_p < _amin) _amin = _p;
            }
        }

        _adist=_amax-_amin;
        if(_adist==0.0)
        {
            _amin-=1.0;
            _adist=2.0;
        }
        _vdist=_distance/_adist;

        for(int _x=_x1; _x<=_x2 ; _x++)
        {
            for(int _y=_y1; _y<=_y2 ; _y++) {
                double _p = this.getPoint(_x,_y);
                this.setPoint(_x,_y,_min+(_p-_amin)*_vdist);
            }
        }
        return _adist;
    }


    public void setProjection(int p, double p_left, double p_right, double p_top, double p_bottom)
    {
        currProjection=p;
        projLeft=p_left;
        projRight=p_right;
        projTop=p_top;
        projBottom=p_bottom;
    }

    public void setProjection(int p)
    {
        switch(p)
        {
            case FP_NONE:
                this.setProjection(p, 0, 0, 0, 0);
                break;
            case FP_SINCOS:
                this.setProjection(p, -Math.PI, Math.PI, Math.PI / 2.0, -Math.PI / 2.0);
                break;
        }
    }

    public double[] getProjectedCoords(int nx, int ny)
    {
        double v[] = null;
        switch(currProjection)
        {
            case -1:
            {
                v=new double[3];
                v[0]=projLeft+((nx%fW)/(double)fW)*(projRight-projLeft);
                v[1]=projTop+((ny%fH)/(double)fH)*(projBottom-projTop);
                break;
            }
            case FP_SINCOS:
            {
                double mX=projLeft+((nx%fW)/(double)fW)*(projRight-projLeft);
                double mY=projTop+((ny%fH)/(double)fH)*(projBottom-projTop);
                v=PhiRhoToXYZ(mX, mY);
                break;
            }
            case FP_NONE:
            default:
            {
                v=new double[3];
                v[0]=nx;
                v[1]=ny;
                v[2]=0.0;
                break;
            }
        }
        return v;
    }

    public void applyNoise(ITransform op, IFractal proc, double scale)
    {
        double tV[]=new double[fH*fW];
        double p[];

        for(int y=0 ; y<fH ; ++y)
            for(int x=0 ; x<fW ; ++x)
            {
                p=this.getProjectedCoords(x + fWoff, y + fHoff);
                p[0]*=scale; p[1]*=scale; p[2]*=scale;
                tV[y*fW+x]=proc.fractal3(p[0], p[1], p[2]);
            }

        for(int i=0 ; i<fH*fW ; i++)
        {
            this.vF[i]=op.transform(this.vF[i],tV[i]);
        }
    }

    public void applyNoise(ITransform op, double gain)
    {
        double tV[]=new double[fH*fW];

        double p_n=this.vF[0];
        double p_m=this.vF[0];
        for(int i=0 ; i<fH*fW ; i++)
        {
            tV[i]=this.vF[i];
            if(tV[i]>p_m) p_m=tV[i];
            if(tV[i]<p_n) p_n=tV[i];
        }

        double p_D=p_m-p_n;
        if(p_D==0.0)
        {
            p_n-=1.0;
            p_m+=1.0;
            p_D=2.0;
        }

        for(int i=0 ; i<fH*fW ; i++)
        {
            tV[i]=gain*((tV[i]-p_n)/p_D);

            this.vF[i]=op.transform(this.vF[i],tV[i]);
        }
    }

    public void applyNoise(INoise _noise)
    {
        for(int y = 0; y < fH; ++y) {
            for (int x = 0; x < fW; ++x) {
                double[] p = this.getProjectedCoords(x + fWoff, y + fHoff);
                if(p.length==3)
                {
                    this.vF[y * fW + x] = _noise.noise3(p[0], p[1], p[2]);
                }
                else
                {
                    this.vF[y * fW + x] = _noise.noise2(p[0], p[1]);
                }
            }
        }
    }

    public interface IFunction{
        double function(NoiseField _nf, int _x,int _y,double _v);
    }
    public void applyFunction(IFunction _func)
    {
        for(int y = 0; y < fH; ++y) {
            for (int x = 0; x < fW; ++x)
            {
                this.vF[y * fW + x] = _func.function(this,x,y,this.vF[y * fW + x]);
            }
        }
    }

    public void applyDistortion(INoise N, double scale, double deviation)
    {
        scale/=(fH > fW) ? fH : fW;
        double tV[] = new double[fH * fW];
        double p[];
        for(int y = 0; y < fH; ++y)
            for(int x = 0; x < fW; ++x)
            {
                p = this.getProjectedCoords(x + fWoff, y + fHoff);
                p[0] *= scale;
                p[1] *= scale;
                p[2] *= scale;
                int s = (int)(deviation*N.noise3(p[0], p[1], p[2]));
                int t = (int)(deviation*N.noise3(p[0]+1.5, p[1]+6.7, p[2]+3.4));
                tV[y * fW + x] = this.getPointT(x+s,y+t);
            }
        this.vF=tV;
    }


}
