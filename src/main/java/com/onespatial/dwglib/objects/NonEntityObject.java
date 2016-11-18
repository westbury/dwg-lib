package com.onespatial.dwglib.objects;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.Handle;

public abstract class NonEntityObject extends CadObject {

    public Handle parentHandle;

    // TODO we need to remove this.
    public NonEntityObject() {
        super(null);
    }

    public NonEntityObject(ObjectMap objectMap) {
        super(objectMap);
    }
    

    @Override
    public void readPostCommonFields(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
        int numReactors = dataStream.getBL();

        boolean xDicMissingFlag = dataStream.getB();
        if (fileVersion.is2013OrLater()) {
            boolean hasBinaryData = dataStream.getB();
        }

        parentHandle = handleStream.getHandle(handleOfThisObject);

        reactorHandles = new Handle[numReactors];
        for (int i = 0; i< numReactors; i++) {
            Handle reactorHandle = handleStream.getHandle(handleOfThisObject);
            reactorHandles[i] = reactorHandle;
        }

        if (!xDicMissingFlag) {
            xdicobjhandle = handleStream.getHandle();
        }

        
        this.readObjectTypeSpecificData(dataStream, stringStream, handleStream, fileVersion);
    }

}
