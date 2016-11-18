package com.onespatial.dwglib.objects;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;

public class Style extends NonEntityObject {

    @Override
	public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
        // 19.4.54 SHAPEFILE (53)
        
        
        
        String entryName = stringStream.getTU();
        String fontName = stringStream.getTU();
        String bigFontName = stringStream.getTU();
	}

	public String toString() {
		return "STYLE";
	}

}
