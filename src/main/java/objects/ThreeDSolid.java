package objects;

import bitstreams.BitBuffer;

public class ThreeDSolid extends EntityObject {

	public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream) {
        // 19.4.39 REGION (37), 3DSOLID (38), BODY (39) page 137

    	// TODO need to read as Common Entity Data is described in 19.4.1 page 104
    	// (Common Entity Format read above)
    	
        boolean acisEmptyBit = dataStream.getB();
        boolean unknownBit = dataStream.getB();

        int version = dataStream.getBS();

	}

	public String toString() {
		return "3DSOLID";
	}

}
