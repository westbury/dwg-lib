package bitstreams;

public class CmColor {

	public final int rgbValue;
	
	public final int colorByte;

	public CmColor(int rgbValue, int colorByte) {
		this.rgbValue = rgbValue;
		this.colorByte = colorByte;
	}

    @Override
    public String toString() {
    	return "(rgb=" + rgbValue + ", colorByte=" + colorByte + ")";
    }
}