package objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bitstreams.BitBuffer;
import bitstreams.Handle;
import dwglib.FileVersion;

public class Appid extends NonEntityObject {

    public String entryName;

    public Handle theAppControl;

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

        theAppControl = handleStream.getHandle(handleOfThisObject);

        List<Handle> reactorHandles = new ArrayList<>();
        for (int i = 0; i< numReactors; i++) {
            Handle reactorHandle = handleStream.getHandle(handleOfThisObject);
            reactorHandles.add(reactorHandle);
        }

        if (!xDicMissingFlag) {
            xdicobjhandle = handleStream.getHandle();
        }

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
