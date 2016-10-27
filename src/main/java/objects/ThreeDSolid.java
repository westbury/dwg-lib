package objects;

import java.io.UnsupportedEncodingException;

import bitstreams.BitBuffer;

public class ThreeDSolid extends EntityObject {

	public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream) {
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
                double point1 = dataStream.getBD();
                double point2 = dataStream.getBD();
                double point3 = dataStream.getBD();
            }
            int numIsoLines = dataStream.getBL();
            boolean isoPresent = dataStream.getB();
            int numWires = dataStream.getBL();

            for (int i = 0; i < numWires; i++) {
                int wireType = dataStream.getRC();
                int wireSelectionMarker = dataStream.getBL();
                int wireColor = dataStream.getBS();
                int wireAcisIndex = dataStream.getBL();
                int wireNumberOfPoints = dataStream.getBL();
                for (int j = 0; j < wireNumberOfPoints; j++) {
                    double point1 = dataStream.getBD();
                    double point2 = dataStream.getBD();
                    double point3 = dataStream.getBD();
                }
                boolean transformPresent = dataStream.getB();
                if (transformPresent) {
                    double xAxis1 = dataStream.getBD();
                    double xAxis2 = dataStream.getBD();
                    double xAxis3 = dataStream.getBD();
                    double yAxis1 = dataStream.getBD();
                    double yAxis2 = dataStream.getBD();
                    double yAxis3 = dataStream.getBD();
                    double zAxis1 = dataStream.getBD();
                    double zAxis2 = dataStream.getBD();
                    double zAxis3 = dataStream.getBD();
                    double translation1 = dataStream.getBD();
                    double translation2 = dataStream.getBD();
                    double translation3 = dataStream.getBD();
                    double scale = dataStream.getBD();
                    boolean hasRotation = dataStream.getB();
                    boolean hasReflection = dataStream.getB();
                    boolean hasShear = dataStream.getB();
                }
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
        
        dataStream.assertEndOfStream();
	}

	public String toString() {
		return "3DSOLID";
	}

}
