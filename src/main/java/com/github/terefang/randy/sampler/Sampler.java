package com.github.terefang.randy.sampler;

import java.util.List;

public interface Sampler<T>
{
    List<T> samples();
}
