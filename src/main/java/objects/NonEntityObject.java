package objects;

import bitstreams.BitBuffer;
import dwglib.FileVersion;

public abstract class NonEntityObject extends CadObject {

    @Override
    public void readPostCommonFields(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
        numReactors = dataStream.getBL();

        xDicMissingFlag = dataStream.getB();
        if (fileVersion.is2013OrLater()) {
            boolean hasBinaryData = dataStream.getB();
        }

        this.readObjectTypeSpecificData(dataStream, stringStream, handleStream, fileVersion);
    }

}
