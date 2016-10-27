package objects;

import bitstreams.BitBuffer;
import bitstreams.CmColor;
import dwglib.FileVersion;

public abstract class EntityObject extends CadObject {


    public void readPostCommonFields(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {

        // 19.4.1 Common entity data, page 104

        boolean graphicImageFlag = dataStream.getB();
        if (graphicImageFlag) {
            long sizeOfGraphicImageInBytes = dataStream.getBLL();

            for (int i = 0; i < sizeOfGraphicImageInBytes*8; i++) {
                dataStream.getB();
            }
        }

        int entMode = dataStream.getBB();
        numReactors = dataStream.getBS();
        
        // It appears that the xDicMissingFlag is not included.  As it has been known before for fields in the spec to actually not be there,
        // a process of skipping the reading of each field in turn was tried.  Perhaps the xdic is never there for entities.  
        // TODO This needs more investigation.

//        xDicMissingFlag = dataStream.getB();
        if (fileVersion.is2013OrLater()) {
            boolean hasBinaryData = dataStream.getB();
        }
        boolean areLinkersPresent = dataStream.getB();
        CmColor entityColor = dataStream.getENC();
        double linetypeScale = dataStream.getBD();
        int linetypeFlag = dataStream.getBB();
        int plotstyleFlag = dataStream.getBB();
        int materialFlag = dataStream.getBB();
        int shadowFlags = dataStream.getRC();
        boolean hasFullVisualStyle = dataStream.getB();
        boolean hasFaceVisualStyle = dataStream.getB();
        boolean hasEdgeVisualStyle = dataStream.getB();
        int isInvisible = dataStream.getBS();
        int entityLineweightFlag = dataStream.getRC();

        readObjectTypeSpecificData(dataStream, stringStream, handleStream);
    }    
}
