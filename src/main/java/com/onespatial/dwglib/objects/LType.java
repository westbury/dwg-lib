package com.onespatial.dwglib.objects;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.Handle;

/**
 * Parent is LTYPE OBJ CONTROL.
 * 
 * @author Nigel Westbury
 *
 */
public class LType extends NonEntityObject {

    public class Dash
    {
        public double dashLength;
        public int complexShapecode;
        public double xOffset;
        public double yOffset;
        public double scale;
        public double rotation;
        public int shapeFlag;
        private Handle shapefileForDashHandle;
        private Handle shapefileForShapeHandle;

        public void readFromDataStream(BitBuffer dataStream) {
            dashLength = dataStream.getBD();
            complexShapecode = dataStream.getBS();
            xOffset = dataStream.getRD();
            yOffset = dataStream.getRD();
            scale = dataStream.getBD();
            rotation = dataStream.getBD();
            shapeFlag = dataStream.getBS();
        }
        
        public void readFromHandleStream(BitBuffer handleStream) {
            try {
                shapefileForDashHandle = handleStream.getHandle();
                } catch (Exception e) {
                    int ii = 34;
                }
            shapefileForShapeHandle = handleStream.getHandle();
        }

        public CadObject getShapefileForDash() {
            if (shapefileForDashHandle == null) {
                return null;
            } else {
                CadObject result = objectMap.parseObjectPossiblyNull(shapefileForDashHandle);
                return (CadObject) result;
            }
        }

        public CadObject getShapefileForShape() {
            if (shapefileForShapeHandle == null) {
                return null;
            } else {
                CadObject result = objectMap.parseObjectPossiblyNull(shapefileForShapeHandle);
                return (CadObject) result;
            }
        }
    }

    public String entryName;
    public boolean sixtyFourFlag;
    public boolean xDep;
    public String description;
    public double patternLen;
    public Dash[] dashes;
    private Handle externalReferenceBlockHandle;

    public LType(ObjectMap objectMap) {
        super(objectMap);
    }
    
    @Override
	public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
		// 19.4.56 LTYPE 57 page 162  

		entryName = stringStream.getTU();
		
		sixtyFourFlag = dataStream.getB();
		xDep = dataStream.getB();
		description = stringStream.getTU();
		patternLen = dataStream.getBD();
		dataStream.expectRC(65);
		int numDashes = dataStream.getRC();
		
		dashes = new Dash[numDashes];
        for (int i = 0; i < numDashes; i++) {
            dashes[i] = new Dash();
            dashes[i].readFromDataStream(dataStream);
        }
		
		// No 512 byte area in sample file

		// The handles.
		
		externalReferenceBlockHandle = handleStream.getHandle();

		    
		if (numDashes > 1) numDashes = 1;  // Hack test
		for (int i = 0; i < numDashes; i++) {
            dashes[i].readFromHandleStream(handleStream);
        }
		
		handleStream.advanceToByteBoundary();
        dataStream.assertEndOfStream();
        stringStream.assertEndOfStream();
		handleStream.assertEndOfStream();
	}

	public String toString() {
		return "LTYPE";
	}

    public CadObject getExternalReferenceBlock() {
        CadObject result = objectMap.parseObjectPossiblyNull(externalReferenceBlockHandle);
        return (CadObject) result;
    }

}
