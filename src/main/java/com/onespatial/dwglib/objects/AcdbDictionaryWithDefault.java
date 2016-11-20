package com.onespatial.dwglib.objects;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.Handle;

public class AcdbDictionaryWithDefault extends Dictionary {

    private Handle defaultEntryHandle;

    public AcdbDictionaryWithDefault(ObjectMap objectMap) {
        super(objectMap);
    }
    
    @Override
    public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
        // 19.4.43 ACDBDICTIONARYWDFLT page 146

        super.readCommonDictionaryData(dataStream, stringStream, handleStream);

        defaultEntryHandle = handleStream.getHandle();
        
        handleStream.advanceToByteBoundary();

        dataStream.assertEndOfStream();
        stringStream.assertEndOfStream();
        handleStream.assertEndOfStream();
    }

    public CadObject getDefaultEntry() {
        CadObject result = objectMap.parseObject(defaultEntryHandle);
        return (CadObject) result;
    }

	public String toString() {
		return "ACDBDICTIONARYWDFLT";
	}

}
