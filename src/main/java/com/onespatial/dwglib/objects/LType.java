package com.onespatial.dwglib.objects;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

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
        public String textToBeDrawn;

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
            shapefileForDashHandle = handleStream.getHandle();
        }

        public CadObject getShapefileForDash() {
            CadObject result = objectMap.parseObjectPossiblyNull(shapefileForDashHandle);
            return (CadObject) result;
        }

        public void extractTextFromTextArea(int[] textArea) {
            int textLength = 0;
            for (int i = complexShapecode; i < textArea.length && textArea[i] != 0; i++) {
                textLength++;
            }
            byte[] extractedBytes = new byte[textLength];
            for (int j = 0; j < textLength; j++) {
                extractedBytes[j] = (byte)textArea[complexShapecode+j];
            }
            try {
                textToBeDrawn = new String(extractedBytes, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                objectMap.getIssues().addWarning("Invalid text in LType text area.  The text is not valid UTF-8.");
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

		boolean isTextAreaPresent = false;
		
		dashes = new Dash[numDashes];
        for (int i = 0; i < numDashes; i++) {
            dashes[i] = new Dash();
            dashes[i].readFromDataStream(dataStream);
            
            isTextAreaPresent = isTextAreaPresent || ((dashes[i].shapeFlag & 0x02) != 0);
        }
		
		// The 512 byte area
        if (isTextAreaPresent) {
            int[] textArea = dataStream.getBytes(512);
            
            for (int i = 0; i < numDashes; i++) {
                if (((dashes[i].shapeFlag & 0x02) != 0)) {
                    dashes[i].extractTextFromTextArea(textArea);
                }
            }
        }

		// The handles.
		
		externalReferenceBlockHandle = handleStream.getHandle();
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
