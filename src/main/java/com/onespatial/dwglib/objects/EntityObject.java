package com.onespatial.dwglib.objects;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.CmColor;
import com.onespatial.dwglib.bitstreams.Handle;

public abstract class EntityObject extends CadObject {

    public Handle parentHandle;
    private Handle layerHandle;
    private Handle linetypeHandle;
    private Handle materialHandle;
    private Handle plotstyleHandle;

    public EntityObject(ObjectMap objectMap) {
        super(objectMap);
    }
    
    public void readPostCommonFields(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {

        // 19.4.1 Common entity data, page 104

        boolean graphicImageFlag = dataStream.getB();
        if (graphicImageFlag) {
            long sizeOfGraphicImageInBytes = dataStream.getBLL();

            for (int i = 0; i < sizeOfGraphicImageInBytes*8; i++) {
                dataStream.getB();
            }
        }

        // entMode is documented only for R13-R14 on page 101.  However its meaning
        // appears to be unchanged in 2010+.
        int entMode = dataStream.getBB();
        
        // Generally, entMode indicates whether or not the owner relative handle reference is present.
        boolean hasOwnerHandleReference;
        switch (entMode) {
        case 0: 
            // The owner relative handle reference is present.
            // Applies to the following:
            // VERTEX, ATTRIB, and SEQEND.
            // BLOCK, ENDBLK, and the defining entities in all
            // block defs except *MODEL_SPACE and *PAPER_SPACE.
            hasOwnerHandleReference = true;
            break;
        case 1: 
            // PSPACE entity without a owner relative handle ref.
            hasOwnerHandleReference = false;
            break;
        case 2: 
            // MSPACE entity without a owner relative handle ref.
            hasOwnerHandleReference = false;
            break;
        default:
            throw new RuntimeException("unexpected value for 'entMode': 3");
        }
        
        int numReactors = dataStream.getBS();
        
        // It appears that the xDicMissingFlag is not included.  As it has been known before for fields in the spec to actually not be there,
        // a process of skipping the reading of each field in turn was tried.  Perhaps the xdic is never there for entities.  
        // TODO This needs more investigation.

//        xDicMissingFlag = dataStream.getB();
        
        boolean hasBinaryData = false;
        if (fileVersion.is2013OrLater()) {
            hasBinaryData = dataStream.getB();
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

        // 19.4.2 Common Entity Handle Data page 105

        if (hasOwnerHandleReference) {
            parentHandle = handleStream.getHandle(handleOfThisObject);
        }
        
        reactorHandles = new Handle[numReactors];
        for (int i = 0; i< numReactors; i++) {
            Handle reactorHandle = handleStream.getHandle(handleOfThisObject);
            reactorHandles[i] = reactorHandle;
        }

        // Correct for 2013, may not be correct for 2010... 
        boolean xDicMissingFlag = hasBinaryData;
        if (!xDicMissingFlag) {
            Handle xdicobjhandle = handleStream.getHandle();
        }
        
        // This seems to not be present???
//        Handle colorBookColorHandle = handleStream.getHandle();
            
        layerHandle = handleStream.getHandle();
        CadObject myLayer = objectMap.parseObject(layerHandle);
        if (myLayer instanceof Dictionary) {
            // try next handle
            layerHandle = handleStream.getHandle();
            CadObject myLayer2 = objectMap.parseObject(layerHandle);
            System.out.println("  layer is " + myLayer2.toString());
        }
        
        if (linetypeFlag == 3) {
            linetypeHandle = handleStream.getHandle();
        }
        if (materialFlag == 3) {
            materialHandle = handleStream.getHandle();
        }
        if (plotstyleFlag == 3) {
            plotstyleHandle = handleStream.getHandle();
        }
        
        readObjectTypeSpecificData(dataStream, stringStream, handleStream, fileVersion);
    }

    public Layer getLayer()
    {
        CadObject result = objectMap.parseObject(layerHandle);
        if (this instanceof Insert && result instanceof Dictionary) {
            return null;
        }
        return (Layer) result;
    }

    public LType getLinetype() {
        if (linetypeHandle == null) {
            return null;
        } else {
            CadObject result = objectMap.parseObject(linetypeHandle);
            return (LType) result;
        }
    }

    public CadObject getMaterial() {
        if (materialHandle == null) {
            return null;
        } else {
            CadObject result = objectMap.parseObject(materialHandle);
            return (NonEntityObject) result;
        }
    }

    public CadObject getPlotstyle() {
        if (plotstyleHandle == null) {
            return null;
        } else {
            CadObject result = objectMap.parseObject(plotstyleHandle);
            return (NonEntityObject) result;
        }
    }
}
