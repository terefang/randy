package com.github.terefang.randy.utils;

import java.io.InputStream;

public class ClasspathResourceLoader
{
    String file;
    ClassLoader classLoader;

    public static ClasspathResourceLoader of(String _file)
    {
        return of(_file, ClasspathResourceLoader.class.getClassLoader());
    }

    public static ClasspathResourceLoader of(String _file, ClassLoader _cl)
    {
        if(_cl == null)
        {
            _cl = ClassLoader.getSystemClassLoader();
        }

        ClasspathResourceLoader _rl = new ClasspathResourceLoader();
        _rl.file = _file;
        _rl.classLoader = _cl;
        return _rl;
    }

    public String getName()
    {
        return "cp:"+this.file;
    }

    public InputStream getInputStream()
    {
        return this.classLoader.getResourceAsStream(this.file);
    }
}