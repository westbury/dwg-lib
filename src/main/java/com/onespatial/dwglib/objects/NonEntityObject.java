package com.onespatial.dwglib.objects;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.Issues;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.Handle;
import com.onespatial.dwglib.writer.BitWriter;

public abstract class NonEntityObject extends CadObject {

    public Handle parentHandle;

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

        readObjectTypeSpecificData(dataStream, stringStream, handleStream, fileVersion);
    }

    @Override
    protected void writePostCommonFields(byte[] byteArray, BitWriter dataStream, BitWriter stringStream,
            BitWriter handleStream, Issues issues) {
        // Non-entity objects are not updatable, so can't be dirty, so this
        // method should
        // never be called.
        throw new UnsupportedOperationException();
    }

}
