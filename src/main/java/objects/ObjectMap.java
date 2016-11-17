package objects;

import com.onespatial.dwglib.Reader;

import bitstreams.Handle;

// This is a place-holder class.  Passes thru everything to reader for time being.
public class ObjectMap
{
    private Reader reader;
    
    public ObjectMap(Reader reader) {
        this.reader = reader;
    }
    
    public CadObject parseObject(Handle handle)
    {
        return reader.parseObject(handle);
    }

    public CadObject parseObjectPossiblyNull(Handle handle)
    {
        if (handle.offset == 0) {
            return null;
        } else {
            return reader.parseObject(handle);
        }
    }

}
