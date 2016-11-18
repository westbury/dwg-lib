package com.onespatial.dwglib.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.Handle;

public class Appid extends NonEntityObject {

    public String entryName;

    public Handle externalRefBlockHandle;

    @Override
    public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
        // 19.4.64 APPID (67) page 173

        entryName = stringStream.getTU();
        boolean sixtyFourFlag = dataStream.getB();

        // This is documented in the spec but does not appear to
        // be in the data.
//      int xRefOrdinal = dataStream.getBS();
        boolean xDep = dataStream.getB();
        int unknown = dataStream.getRC();

        externalRefBlockHandle = handleStream.getHandle(handleOfThisObject);

        handleStream.advanceToByteBoundary();

        dataStream.assertEndOfStream();
        stringStream.assertEndOfStream();
        handleStream.assertEndOfStream();
    }

    public String toString() {
        return "APPID";
    }

}
