package com.onespatial.dwglib.objects;

import java.util.AbstractList;
import java.util.List;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.Handle;

/**
 * Parent is a null handle.
 * 
 * @author Nigel Westbury
 *
 */
public class LTypeControlObj extends NonEntityObject {

	private Handle[] lineTypeHandles;
	private Handle bylayerLinetypeHandle;
	private Handle byblockLinetypeHandle;

	public LTypeControlObj(ObjectMap objectMap) {
        super(objectMap);
    }

    @Override
	public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
		// 19.4.55 LINETYPE CONTROL (56)
		
		int numEntries = dataStream.getBL();

		dataStream.assertEndOfStream();
		
		// Here starts the handle area
		
		lineTypeHandles = new Handle[numEntries];
		for (int i = 0; i < numEntries; i++){
		    lineTypeHandles[i] = handleStream.getHandle(handleOfThisObject);
		}

		bylayerLinetypeHandle = handleStream.getHandle();
		byblockLinetypeHandle = handleStream.getHandle();
		
		handleStream.advanceToByteBoundary();
		handleStream.assertEndOfStream();
	}

	public String toString() {
		return "LTYPE CONTROL OBJ";
	}

    public List<LType> getLineTypes() {
        return new AbstractList<LType>() {

            @Override
            public LType get(int index)
            {
                return (LType)objectMap.parseObject(lineTypeHandles[index]);
            }

            @Override
            public int size()
            {
                return lineTypeHandles.length;
            }
        };
    }

    public LType getBylayerLinetype() {
        CadObject result = objectMap.parseObject(bylayerLinetypeHandle);
        return (LType) result;
    }

    public LType getByblockLinetype() {
        CadObject result = objectMap.parseObject(byblockLinetypeHandle);
        return (LType) result;
    }

}
