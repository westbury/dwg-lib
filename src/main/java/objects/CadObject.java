package objects;

import java.util.ArrayList;
import java.util.List;

import bitstreams.BitBuffer;
import bitstreams.Handle;
import dwglib.FileVersion;

public abstract class CadObject {

	protected Handle handleOfThisObject;
	protected int numReactors;
	protected boolean xDicMissingFlag;

	public List<Handle> genericHandles = new ArrayList<>();

	public void readFromStreams(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
		handleOfThisObject = dataStream.getHandle();

		// Page 254 Chapter 27 Extended Entity Data

		int sizeOfExtendedObjectData = dataStream.getBS();
		while (sizeOfExtendedObjectData != 0) {
		    Handle appHandle = dataStream.getHandle();
			for (int i = 0; i < sizeOfExtendedObjectData*8; i++) {
				dataStream.getB();
			}
	        sizeOfExtendedObjectData = dataStream.getBS();
		}

		numReactors = dataStream.getBL();

		xDicMissingFlag = dataStream.getB();
		if (fileVersion.is2013OrLater()) {
			boolean hasBinaryData = dataStream.getB();
		}

		this.readObjectTypeSpecificData(dataStream, stringStream, handleStream);
	}

	public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream) {
		// For the time being, provide this default implementation that
		// just reads all the handles.
		// Ultimately this should be an abstract method.

		try {
		do {
			Handle referencedHandle = handleStream.getHandle(handleOfThisObject);
			genericHandles.add(referencedHandle);
		} while (true);
		} catch (RuntimeException e) {
			
		}
		handleStream.advanceToByteBoundary();
		handleStream.assertEndOfStream();
	}

}
