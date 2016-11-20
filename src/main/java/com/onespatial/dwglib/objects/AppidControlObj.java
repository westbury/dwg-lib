package com.onespatial.dwglib.objects;

import java.util.AbstractList;
import java.util.List;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.Handle;

public class AppidControlObj extends NonEntityObject {

    public Handle[] appidObjectHandles;

	public AppidControlObj(ObjectMap objectMap) {
        super(objectMap);
    }

    @Override
    public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
        // 19.4.63 APPID CONTROL (66) page 172

        int numentries = dataStream.getBL();
        
        // The handles
        
        appidObjectHandles = new Handle[numentries];
        for (int i = 0; i < numentries; i++) {
            appidObjectHandles[i] = handleStream.getHandle(handleOfThisObject);
        }

        handleStream.advanceToByteBoundary();

        dataStream.assertEndOfStream();
        stringStream.assertEndOfStream();
        handleStream.assertEndOfStream();
    }

    public String toString() {
		return "APPID CONTROL OBJ";
	}

    public List<Appid> getAppids()
    {
        return new AbstractList<Appid>() {
            @Override
            public Appid get(int index)
            {
                CadObject result = objectMap.parseObject(appidObjectHandles[index]);
                return (Appid) result;
            }

            @Override
            public int size()
            {
                return appidObjectHandles.length;
            }
        };
    }

}
