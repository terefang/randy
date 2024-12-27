package com.github.terefang.randy.utils;

public interface LogSink
{
    void log(String message);
    void logProgress(int _pct, String message);
    void logConsole(String message);
}
