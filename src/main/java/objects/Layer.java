package objects;

import java.util.ArrayList;
import java.util.List;

import bitstreams.BitBuffer;
import bitstreams.Handle;
import dwglib.FileVersion;

public class Layer extends NonEntityObject {

    public Handle layerControlHandle;
    public List<Handle> reactorHandles = new ArrayList<>();
    public Handle externalReferenceBlockHandle;
    public Handle plotStyleHandle;
    public Handle lineTypeHandle;
    public Handle materialHandle;

    @Override
    public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
        // 19.4.52 LAYER (51)

        int numEntries = dataStream.getBL();

        // TODO process remaining CRC data in data stream
//		dataStream.assertEndOfStream();

        layerControlHandle = handleStream.getHandle(handleOfThisObject);

        for (int i = 0; i< numReactors; i++) {
            Handle reactorHandle = handleStream.getHandle(handleOfThisObject);
            reactorHandles.add(reactorHandle);
        }

        if (!xDicMissingFlag) {
            Handle xdicobjhandle = handleStream.getHandle();
        }

        externalReferenceBlockHandle = handleStream.getHandle();
        plotStyleHandle = handleStream.getHandle();
        lineTypeHandle = handleStream.getHandle(handleOfThisObject);
        materialHandle = handleStream.getHandle(handleOfThisObject);

        // It appears that, contrary to the specification, this handle is not present in 2010 (R24).
        if (fileVersion.is2013OrLater()) {
            Handle nullHandle = handleStream.getHandle();
        }

        handleStream.advanceToByteBoundary();
        handleStream.assertEndOfStream();
    }

    public String toString() {
        return "LAYER";
    }

}
