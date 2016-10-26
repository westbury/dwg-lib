package bitstreams;

public class Handle
{

    public final int code;

    public final int offset;

    public Handle(int code, int[] handle)
    {
        this.code = code;
        
        int value = 0;
        int shift = 0;
        for (int i = handle.length-1; i >= 0; i--) {
        	value |= handle[i] << shift;
        	shift += 8;
        }
        
        this.offset = value;
    }

    public Handle(int code, int value)
    {
        this.code = code;
        this.offset = value;
    }

    public String toString() {
    	return Integer.toString(code) + ": " + offset;
    }
}
