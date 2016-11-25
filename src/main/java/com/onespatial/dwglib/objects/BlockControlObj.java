package com.onespatial.dwglib.objects;

import java.util.AbstractList;
import java.util.List;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.Handle;

public class BlockControlObj extends NonEntityObject {

    private Handle[] blockObjectHandles;
    private Handle modelSpaceHandle;
    private Handle paperSpaceHandle;

	public BlockControlObj(ObjectMap objectMap) {
        super(objectMap);
    }

    @Override
    public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
        // 19.4.49 BLOCK CONTROL (48) page 154

        int numentries = dataStream.getBL();
        
        // The handles
        
        blockObjectHandles = new Handle[numentries];
        for (int i = 0; i < numentries; i++) {
            blockObjectHandles[i] = handleStream.getHandle(handleOfThisObject);
        }
        modelSpaceHandle = handleStream.getHandle();
        paperSpaceHandle = handleStream.getHandle();
        
        handleStream.advanceToByteBoundary();

        dataStream.assertEndOfStream();
        stringStream.assertEndOfStream();
        handleStream.assertEndOfStream();
    }

    public String toString() {
		return "BLOCK CONTROL OBJ";
	}

    public List<BlockHeader> getBlockHeaders()
    {
        return new AbstractList<BlockHeader>() {

            @Override
            public BlockHeader get(int index)
            {
                CadObject result = objectMap.parseObjectPossiblyNull(blockObjectHandles[index]);
                return (BlockHeader) result;
            }

            @Override
            public int size()
            {
                return blockObjectHandles.length;
            }
        };
    }

    public CadObject getModelSpace() {
        CadObject result = objectMap.parseObjectPossiblyNull(modelSpaceHandle);
        return (CadObject) result;
    }

    public CadObject getPaperSpace() {
        CadObject result = objectMap.parseObjectPossiblyNull(paperSpaceHandle);
        return (CadObject) result;
    }

}
