package com.onespatial.dwglib;

public class SectionPage
{

    public SectionPage(int pageType, byte[] expandedData)
    {
        this.pageType = pageType;
        this.expandedData = expandedData;
    }

    public final int pageType;

    public final byte[] expandedData;

}
