package com.onespatial.dwglib.objects;

import java.util.AbstractList;
import java.util.List;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.Handle;

public class PolylineMesh extends EntityObject {

    public int flags;

    public int curveType;

    public int mVertexCount;

    public int nVertexCount;
    
    public int mDensity;

    public int nDensity;

    private Handle[] vertexHandles;

    private Handle seqEndHandle;

	public PolylineMesh(ObjectMap objectMap) {
        super(objectMap);
    }

    @Override
    public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
        // 19.4.32 POLYLINE (MESH) (30) page 128

        flags = dataStream.getBS();
        curveType = dataStream.getBS();
        mVertexCount = dataStream.getBS();
        nVertexCount = dataStream.getBS();
        mDensity = dataStream.getBS();
        nDensity = dataStream.getBS();
        int ownedObjectCount = dataStream.getBL();
        
        // The handles
        
        vertexHandles = new Handle[ownedObjectCount];
        for (int i = 0; i < ownedObjectCount; i++) {
            vertexHandles[i] = handleStream.getHandle(handleOfThisObject);
        }
        seqEndHandle = handleStream.getHandle();
        
        handleStream.advanceToByteBoundary();

        dataStream.assertEndOfStream();
        stringStream.assertEndOfStream();
        handleStream.assertEndOfStream();
    }

    public String toString() {
		return "POLYLINE (MESH)";
	}

    public List<EntityObject> getOwnedObjects()
    {
        return new AbstractList<EntityObject>() {

            @Override
            public EntityObject get(int index)
            {
                CadObject result = objectMap.parseObject(vertexHandles[index]);
                return (EntityObject) result;
            }

            @Override
            public int size()
            {
                return vertexHandles.length;
            }
        };
    }

    public CadObject getSeqEnd() {
        CadObject result = objectMap.parseObjectPossiblyNull(seqEndHandle);
        return (CadObject) result;
    }

}
