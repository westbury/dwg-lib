package com.onespatial.dwglib.objects;

import java.util.HashMap;
import java.util.Map;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.Handle;

public class Dictionary extends NonEntityObject {

    public Map<String, Handle> dictionaryMap;

    public Dictionary(ObjectMap objectMap) {
        super(objectMap);
    }
    
    @Override
	public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
        // 19.4.42 DICTIONARY (42)

        int numItems = dataStream.getBL();

        int cloningFlag = dataStream.getBS();
        int hardOwnerFlag = dataStream.getRC();
    
        // The handles
        
        dictionaryMap = new HashMap<>();
        for (int i = 0; i < numItems; i++) {
            String key = stringStream.getTU();
            Handle handle = handleStream.getHandle(handleOfThisObject);
            dictionaryMap.put(key, handle);
        }
        
        handleStream.advanceToByteBoundary();

        dataStream.assertEndOfStream();
        stringStream.assertEndOfStream();
        handleStream.assertEndOfStream();
	}

	public String toString() {
		return "DICTIONARY";
	}

    public CadObject lookupObject(String key)
    {
        Handle handle = dictionaryMap.get(key);
        return objectMap.parseObject(handle);
    }

}
