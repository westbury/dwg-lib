package objects;

import bitstreams.BitBuffer;
import bitstreams.Handle;
import dwglib.FileVersion;

public class LType extends NonEntityObject {

    @Override
	public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
		// 19.4.56 LTYPE 57    

		String entryName = stringStream.getTU();
		
		boolean sixtyFourFlag = dataStream.getB();
		dataStream.getB();
		dataStream.getB();
		dataStream.getB();
//		int xRefOrdinal = dataStream.getBS();
//		boolean xDep = dataStream.getB();
//		String description = stringStream.getTU();
//		double patternLen = dataStream.getBD();
		int alignment = dataStream.getRC();
		int numDashes = dataStream.getRC();

		for (int i = 0; i < numDashes; i++) {
			double dashLength = dataStream.getBD();
			int complexShapecode = dataStream.getBS();
			double xOffset  = dataStream.getRD();
			double yOffset  = dataStream.getRD();
			double scale  = dataStream.getRD();
			double rotation  = dataStream.getRD();
			int shapeFlag = dataStream.getBS();
		}

//		dataStream.assertEndOfStream();
		
		// No 512 byte area in sample file

		Handle lypeControlHandle = handleStream.getHandle(handleOfThisObject);
		for (int i = 0; i < numReactors; i++) {
			Handle reactorHandle = handleStream.getHandle(handleOfThisObject);
		}
		if (!xDicMissingFlag) {
			Handle xdicobjhandle = handleStream.getHandle();
		}
		Handle externalReferenceBlockHandle = handleStream.getHandle();
		
		for (int i = 0; i < numDashes; i++) {
			Handle shapefileForDashHandle = handleStream.getHandle();
			Handle shapefileForShapeHandle = handleStream.getHandle();
		}
		
		handleStream.advanceToByteBoundary();
//		handleStream.assertEndOfStream();
	}

	public String toString() {
		return "LTYPE";
	}

}
