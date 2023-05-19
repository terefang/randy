package com.github.terefang.randy.rng;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public interface IRandomSeeded
{
    IRandom seeded(long _s);
}
