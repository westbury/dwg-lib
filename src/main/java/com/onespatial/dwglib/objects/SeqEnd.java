package com.onespatial.dwglib.objects;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.Handle;

public class SeqEnd extends EntityObject {

	public SeqEnd(ObjectMap objectMap) {
        super(objectMap);
    }

    @Override
    public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
        // 19.4.8 SEQEND (6) page 111

        // These objects contain no data specific to the object type.

        // Occasionally a SEQEND will have a null handle.  The meaning is unknown.
        try {
        Handle nullHandle = handleStream.getHandle();
        if (nullHandle.offset != 0) {
            throw new RuntimeException("Unexpected handle in SeqEnd object, and it is not null.");
        }
        } catch (Exception e) {
            // This is the normal case.
        }
        
        handleStream.advanceToByteBoundary();

        dataStream.assertEndOfStream();
        stringStream.assertEndOfStream();
        handleStream.assertEndOfStream();
    }

    public String toString() {
		return "SEQEND";
	}
}
