package com.onespatial.dwglib.objects;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.Handle;
import com.onespatial.dwglib.bitstreams.Point3D;

public class ThreeDSolid extends EntityObject {

    public static class Wire {

        public int wireType;
        public int wireSelectionMarker;
        public int wireColor;
        public int wireAcisIndex;
        public Point3D[] points;

        public void readFromStream(BitBuffer dataStream) {
            wireType = dataStream.getRC();
            wireSelectionMarker = dataStream.getBL();
            wireColor = dataStream.getBS();
            wireAcisIndex = dataStream.getBL();
            int wireNumberOfPoints = dataStream.getBL();
            points = new Point3D[wireNumberOfPoints];
            for (int j = 0; j < wireNumberOfPoints; j++) {
                points[j] = dataStream.get3BD();
            }
            boolean transformPresent = dataStream.getB();
            if (transformPresent) {
                Point3D xAxis = dataStream.get3BD();
                Point3D yAxis = dataStream.get3BD();
                Point3D zAxis = dataStream.get3BD();
                Point3D translation = dataStream.get3BD();
                double scale = dataStream.getBD();
                boolean hasRotation = dataStream.getB();
                boolean hasReflection = dataStream.getB();
                boolean hasShear = dataStream.getB();
            }
        }

    }

    public Point3D point;

    public Handle historyId;

    public Wire [] wires;

    @Override
	public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
        // 19.4.39 REGION (37), 3DSOLID (38), BODY (39) page 137

        // TODO need to read as Common Entity Data is described in 19.4.1 page 104
        // (Common Entity Format read above)
        
        boolean acisEmptyBit = dataStream.getB();
        boolean unknownBit = dataStream.getB();

        int version = dataStream.getBS();
        if (version == 1) {
            int blockSize = dataStream.getBL();
            for (int i = 0; i < blockSize; i++) {
                dataStream.getRC();
            }
        } else if (version == 2) {
            // process ACIS file
            
            StringBuffer header = new StringBuffer();
            byte [] h2 = new byte[15];
            for (int i = 0; i < 15; i++) {
                h2[i] = (byte)dataStream.getRC();
            }

            String str;
            try
            {
                str = new String(h2, "UTF-8");
            } catch (UnsupportedEncodingException e)
            {
                throw new RuntimeException(e);
            }
            
            if (str.equals("ACIS BinaryFile")) {
                System.out.println("is binary");

                byte[] endMarker;
                try
                {
                    endMarker = "End..of..ACIS..data".getBytes("UTF-8");
                } catch (UnsupportedEncodingException e)
                {
                    throw new RuntimeException(e);
                }

                endMarker[3] = 0x0E;
                endMarker[4] = 0x02;
                endMarker[7] = 0x0E;
                endMarker[8] = 0x04;
                endMarker[13] = 0x0D;
                endMarker[14] = 0x04;

                byte sourceByte = (byte)dataStream.getRC();
                do {
                    int numMatches = 0;
                    for (byte endMarkerByte : endMarker) {
                        if (sourceByte != endMarkerByte) {
                            break;
                        }
                        numMatches++;
                        if (numMatches < endMarker.length) {
                            sourceByte = (byte)dataStream.getRC();
                        }
                    }

                    if (numMatches == endMarker.length) {
                        break;
                    }

                    if (numMatches == 0) {
                        sourceByte = (byte)dataStream.getRC();
                    }
                } while (true);    

                // We should return a ByteBuffer for the binary data.
//                ByteBuffer.wrap(bigArray, offset, length).slice()
                
            } else {
                System.out.println("is text");
                
            }
        } else {
            throw new RuntimeException("Version must be 1 or 2");
        }
        
        // TODO check range of fields covered by this flag:
        // (need sample file with flag set off)
        boolean wireframeDataPresent = dataStream.getB();
        if (wireframeDataPresent) {
            boolean pointPresent = dataStream.getB();
            if (pointPresent) {
                point = dataStream.get3BD();
            }
            int numIsoLines = dataStream.getBL();
            boolean isoPresent = dataStream.getB();
            int numWires = dataStream.getBL();

            wires = new Wire[numWires];
            for (int i = 0; i < numWires; i++) {
                wires[i] = new Wire();
                wires[i].readFromStream(dataStream);
            }

            int numberOfSilhouettes = dataStream.getBL();
            for (int i = 0; i < numberOfSilhouettes; i++) {
                // TODO complete this.
                // (file reads without this because numberOfSilhouettes is zero)

            }
            
            for (int i = 0; i < numWires; i++) {
                // TODO complete this.
                // why num wires again?  And this data does not exist anyway.  Despite loads of wires
                // there are just enough bits left for a B and BL.  Probably this should be nested in the silhouettes
                // loop but need file with silhouettes to confirm.
                
            }
            
            boolean acisEmptyBit2 = dataStream.getB();
            int unknown = dataStream.getBL();
        }
        
        historyId = handleStream.getHandle();
        
        handleStream.advanceToByteBoundary();
        
        dataStream.assertEndOfStream();
        stringStream.assertEndOfStream();
        handleStream.assertEndOfStream();
	}

	public String toString() {
		return "3DSOLID";
	}

	private class ByteBufferAsInputStream extends InputStream {

	    private ByteBuffer byteBuffer;

	    ByteBufferAsInputStream(ByteBuffer byteBuffer) {
	        this.byteBuffer = byteBuffer;
	    }

	    public int read() throws IOException {
	        if (!byteBuffer.hasRemaining()) {
	            return -1;
	        }
	        return byteBuffer.get();
	    }

	    public int read(byte[] bytes, int offset, int length) throws IOException {
	        length = Math.min(byteBuffer.remaining(), length);
	        byteBuffer.get(bytes, offset, length);
	        return length;
	    }
	}
}
