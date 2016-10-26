package dwglib;

public class FileVersion
{
    private final int version;

    public FileVersion(String fileVersionAsString)
    {
        switch (fileVersionAsString) {
        case "AC1015":
            // Release 15
            throw new UnsupportedFileVersionException("This file was produced by AutoCAD 2000, 2000i, or 2002.  Only files produced by AutoCAD 2010 or later are supported.");
        case "AC1018":
            // Release 18
            throw new UnsupportedFileVersionException("This file was produced by AutoCAD 2004, 2005, or 2006.  Only files produced by AutoCAD 2010 or later are supported.");
        case "AC1021":
            // Release 21
            throw new UnsupportedFileVersionException("This file was produced by AutoCAD 2007, 2008, or 2009.  Only files produced by AutoCAD 2010 or later are supported.");
        case "AC1024":
            version = 2010;
            break;
        case "AC1027":
            version = 2013;
            break;
        default:
            throw new UnsupportedFileVersionException("DWG format " + fileVersionAsString + " files are not supported.  Only files produced by AutoCAD 2010 or later are supported.");
        }

    }

    public boolean is2013OrLater()
    {
        return version >= 2013;
    }

    public String getVersionYear()
    {
        return Integer.toString(version);
    }

}
