package objects;

import java.util.ArrayList;
import java.util.List;

import bitstreams.BitBuffer;
import bitstreams.CmColor;
import bitstreams.Handle;
import dwglib.FileVersion;

/**
 * Parent is LAYER OBJ CONTROL
 * 
 * @author Nigel Westbury
 *
 */
public class Layer extends NonEntityObject {

    public Handle externalReferenceBlockHandle;
    public Handle plotStyleHandle;
    public Handle lineTypeHandle;
    public Handle materialHandle;

    @Override
    public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
        // 19.4.52 LAYER (51) page 158

        String entryName = stringStream.getTU();
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
        CmColor color = dataStream.getCMC();
        
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

}
