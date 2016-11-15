package objects;

import java.util.ArrayList;
import java.util.List;

import bitstreams.BitBuffer;
import bitstreams.Handle;
import dwglib.FileVersion;

public abstract class NonEntityObject extends CadObject {

    public Handle parentHandle;

    @Override
    public void readPostCommonFields(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
        int numReactors = dataStream.getBL();

        boolean xDicMissingFlag = dataStream.getB();
        if (fileVersion.is2013OrLater()) {
            boolean hasBinaryData = dataStream.getB();
        }

        parentHandle = handleStream.getHandle(handleOfThisObject);

        reactorHandles = new ArrayList<>();
        for (int i = 0; i< numReactors; i++) {
            Handle reactorHandle = handleStream.getHandle(handleOfThisObject);
            reactorHandles.add(reactorHandle);
        }

        if (!xDicMissingFlag) {
            xdicobjhandle = handleStream.getHandle();
        }

        
        this.readObjectTypeSpecificData(dataStream, stringStream, handleStream, fileVersion);
    }

}
