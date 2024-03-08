### Algorithms

https://www.decarpentier.nl/scape-procedural-extensions

```java
float saturate(float x)
{
  return max(0, min(1, x));
}

// -------------------------------------------------

float3 cross(float3 a, float3 b)
{
  return a.yzx * b.zxy - a.zxy * b.yzx;
}

// -------------------------------------------------
// https://www.decarpentier.nl/scape-procedural-extensions

float swissTurbulence(float2 p, float seed, int octaves,
                      float lacunarity = 2.0, float gain = 0.5,
                      float warp = 0.15>)
{
     float sum = 0;
     float freq = 1.0, amp = 1.0;
     float2 dsum = float2(0,0);
     for(int i=0; i < octaves; i++)
     {
         float3 n = perlinNoiseDeriv((p + warp * dsum)*freq, seed + i);
         sum += amp * (1 - abs(n.x));
         dsum += amp * n.yz * -n.x;
         freq *= lacunarity;
         amp *= gain * saturate(sum);
    }
    return sum;
}

// -------------------------------------------------



// -------------------------------------------------
// https://www.decarpentier.nl/scape-procedural-extensions

float jordanTurbulence(float2 p, float seed, int octaves, float lacunarity = 2.0,
                       float gain1 = 0.8, float gain = 0.5,
                       float warp0 = 0.4, float warp = 0.35,
                       float damp0 = 1.0, float damp = 0.8,
                       float damp_scale = 1.0)
{
    float3 n = perlinNoiseDeriv(p, seed);
    float3 n2 = n * n.x;
    float sum = n2.x;
    float2 dsum_warp = warp0*n2.yz;
    float2 dsum_damp = damp0*n2.yz;

    float amp = gain1;
    float freq = lacunarity;
    float damped_amp = amp * gain;

    for(int i=1; i < octaves; i++)
    {
        n = perlinNoiseDeriv(p * freq + dsum_warp.xy, seed + i / 256.0);
        n2 = n * n.x;
        sum += damped_amp * n2.x;
        dsum_warp += warp * n2.yz;
        dsum_damp += damp * n2.yz;
        freq *= lacunarity;
        amp *= gain;
        damped_amp = amp * (1-damp_scale/(1+dot(dsum_damp,dsum_damp)));
    }
    return sum;
}

// -------------------------------------------------

uniform sampler2D samplerPerlinPerm2D;
uniform sampler2D samplerPerlinGrad2D;

float perlinNoise(float2 p, float seed)
{
    // Calculate 2D integer coordinates i and fraction p.
    float2 i = floor(p);
    float2 f = p - i;

    // Get weights from the coordinate fraction
    float2 w = f * f * f * (f * (f * 6 - 15) + 10);
    float4 w4 = float4(1, w.x, w.y, w.x * w.y);

    // Get the four randomly permutated indices from the noise lattice nearest to
    // p and offset these numbers with the seed number.
    float4 perm = tex2D(samplerPerlinPerm2D, i / 256) + seed;

    // Permutate the four offseted indices again and get the 2D gradient for each
    // of the four permutated coordinates-seed pairs.
    float4 g1 = tex2D(samplerPerlinGrad2D, perm.xy) * 2 - 1;
    float4 g2 = tex2D(samplerPerlinGrad2D, perm.zw) * 2 - 1;

    // Evaluate the four lattice gradients at p
    float a = dot(g1.xy, f);
    float b = dot(g2.xy, f + float2(-1,  0));
    float c = dot(g1.zw, f + float2( 0, -1));
    float d = dot(g2.zw, f + float2(-1, -1));

    // Bi-linearly blend between the gradients, using w4 as blend factors.
    float4 grads = float4(a, b - a, c - a, a - b - c + d);
    float n = dot(grads, w4);

    // Return the noise value, roughly normalized in the range [-1, 1]
    return n * 1.5;
}

// -------------------------------------------------

/* fBM */ float turbulence(float2 p, float seed, int octaves,
                 float lacunarity = 2.0, float gain = 0.5)
{
    float sum = 0;
    float freq = 1.0, amp = 1.0;
    for (int i=0; i < octaves; i++)
    {
        float n = perlinNoise(p*freq, seed + i / 256.0);
        sum += n*amp;
        freq *= lacunarity;
        amp *= gain;
    }
    return sum;
}

// -------------------------------------------------

float billowedNoise(float2 p, float seed)
{
    return abs(perlinNoise(p, seed));
}

// -------------------------------------------------

float ridgedNoise(float2 p, float seed)
{
    return 1.0f-abs(perlinNoise(p, seed));
}

// -------------------------------------------------

/* I. Quillez */
float iqTurbulence(float2 p, float seed, int octaves,
                   float lacunarity = 2.0, float gain = 0.5)
{
    float sum = 0.5;
    float freq = 1.0, amp = 1.0;
    float2 dsum = float2(0,0);
    for (int i=0; i < octaves; i++)
    {
        float3 n = perlinNoisePseudoDeriv(p*freq, seed + i / 256.0);
        dsum += n.yz;
        sum += amp * n.x / (1 + dot(dsum, dsum));
        freq *= lacunarity;
        amp *= gain;
    }
    return sum;
}

```