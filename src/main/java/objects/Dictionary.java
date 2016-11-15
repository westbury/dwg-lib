package objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bitstreams.BitBuffer;
import bitstreams.Handle;
import dwglib.FileVersion;

public class Dictionary extends NonEntityObject {

    public Map<String, Handle> dictionaryMap;

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

}
