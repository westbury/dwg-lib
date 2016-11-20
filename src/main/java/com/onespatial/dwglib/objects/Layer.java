package com.onespatial.dwglib.objects;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.CmColor;
import com.onespatial.dwglib.bitstreams.Handle;

/**
 * Parent is LAYER OBJ CONTROL
 * 
 * @author Nigel Westbury
 *
 */
public class Layer extends NonEntityObject {

    public String entryName;
    public CmColor color;
    private Handle externalReferenceBlockHandle;
    private Handle plotStyleHandle;
    private Handle lineTypeHandle;
    private Handle materialHandle;

    public Layer(ObjectMap objectMap) {
        super(objectMap);
    }
    
    @Override
    public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
        // 19.4.52 LAYER (51) page 158

        entryName = stringStream.getTU();
        boolean sixtyFourFlag = dataStream.getB();
        /*
         * At this point we always seem to be three bits prior to a word
         * that best matches the expected flags.  We get there reliably if
         * we don't read the xrefindex value.  TODO investigate this as this
         * code may not be correct.
         */
//        int xrefordinal = dataStream.getBS();
        boolean xdep = dataStream.getB();
        int flags = dataStream.getBS();
        color = dataStream.getCMC();
        
        // The handles
        
        externalReferenceBlockHandle = handleStream.getHandle();
        plotStyleHandle = handleStream.getHandle();
        materialHandle = handleStream.getHandle(handleOfThisObject);
        lineTypeHandle = handleStream.getHandle(handleOfThisObject);

        // It appears that, contrary to the specification, this handle is not present in 2010 (R24).
        if (fileVersion.is2013OrLater()) {
            Handle nullHandle = handleStream.getHandle();
        }

        handleStream.advanceToByteBoundary();

        dataStream.assertEndOfStream();
        stringStream.assertEndOfStream();
        handleStream.assertEndOfStream();
    }

    public String toString() {
        return "LAYER";
    }

    public CadObject getExternalReferenceBlock() {
        CadObject result = objectMap.parseObjectPossiblyNull(externalReferenceBlockHandle);
        return (CadObject) result;
    }

    public AcdbPlaceHolder getPlotStyle() {
        CadObject result = objectMap.parseObject(plotStyleHandle);
        return (AcdbPlaceHolder) result;
    }

    public CadObject getLineType() {
        CadObject result = objectMap.parseObject(lineTypeHandle);
        return (CadObject) result;
    }

    public CadObject getMaterial() {
        CadObject result = objectMap.parseObject(materialHandle);
        return (CadObject) result;
    }

}
