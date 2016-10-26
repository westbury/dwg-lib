package objects;

import bitstreams.BitBuffer;

public class Style extends NonEntityObject {

	public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream) {
        // 19.4.54 SHAPEFILE (53)
        
        
        
        String entryName = stringStream.getTU();
        String fontName = stringStream.getTU();
        String bigFontName = stringStream.getTU();
	}

	public String toString() {
		return "STYLE";
	}

}
