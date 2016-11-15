package objects;

import bitstreams.BitBuffer;
import dwglib.FileVersion;

public class Block extends NonEntityObject {

    public String blockName;

    @Override
    public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
        // 19.4.6 BLOCK (4) page 110

        blockName = stringStream.getTU();

        handleStream.advanceToByteBoundary();

        dataStream.assertEndOfStream();
        stringStream.assertEndOfStream();
        handleStream.assertEndOfStream();
    }

	public String toString() {
		return "BLOCK";
	}

}
