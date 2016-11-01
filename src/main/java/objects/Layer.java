package objects;

import java.util.ArrayList;
import java.util.List;

import bitstreams.BitBuffer;
import bitstreams.Handle;
import dwglib.FileVersion;

public class Layer extends NonEntityObject {

	public List<Handle> reactorHandles = new ArrayList<>();

	@Override
	public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
		// 19.4.52 LAYER (51)
		
		int numEntries = dataStream.getBL();

		// TODO process remaining CRC data in data stream
//		dataStream.assertEndOfStream();
		
		
		Handle layerControlHandle = handleStream.getHandle(handleOfThisObject);

		for (int i = 0; i< numReactors; i++) {
			Handle reactorHandle = handleStream.getHandle(handleOfThisObject);
			reactorHandles.add(reactorHandle);
		}

		if (!xDicMissingFlag) {
			Handle xdicobjhandle = handleStream.getHandle();
		}

		Handle externalReferenceBlockHandle = handleStream.getHandle();
		Handle plotStyleHandle = handleStream.getHandle();
		Handle lineTypeHandle = handleStream.getHandle(handleOfThisObject);
		Handle materialHandle = handleStream.getHandle(handleOfThisObject);
		
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
