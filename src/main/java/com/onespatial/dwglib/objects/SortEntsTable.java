package com.onespatial.dwglib.objects;

import java.util.AbstractList;
import java.util.List;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.Handle;

public class SortEntsTable extends NonEntityObject {

    private Handle[] sortHandles;
    private Handle ownerHandle;
    private Handle[] entityHandles;

    public SortEntsTable(ObjectMap objectMap) {
        super(objectMap);
    }

    @Override
    public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
        // 19.4.90 SORTENTSTABLE page 207

        int numEntries = dataStream.getBL();

        sortHandles = new Handle[numEntries];
        for (int i = 0; i < numEntries; i++) {
            sortHandles[i] = dataStream.getHandle();
        }

        // The handles

        ownerHandle = handleStream.getHandle(handleOfThisObject);

        entityHandles = new Handle[numEntries];
        for (int i = 0; i < numEntries; i++) {
            entityHandles[i] = handleStream.getHandle(handleOfThisObject);
        }

        handleStream.advanceToByteBoundary();

        dataStream.assertEndOfStream();
        stringStream.assertEndOfStream();
        handleStream.assertEndOfStream();
    }

    public List<CadObject> getSortObjects()
    {
        return new AbstractList<CadObject>() {

            @Override
            public CadObject get(int index)
            {
                /*
                 * The sort values may contain handles that actually do not
                 * exist any more in the handle table.
                 */
                CadObject result = objectMap.parseObjectPossiblyOrphaned(sortHandles[index]);
                return result;
            }

            @Override
            public int size()
            {
                return sortHandles.length;
            }
        };
    }

    public CadObject getOwner() {
        CadObject result = objectMap.parseObject(ownerHandle);
        return result;
    }

    public List<CadObject> getEntities()
    {
        return new AbstractList<CadObject>() {

            @Override
            public CadObject get(int index)
            {
                /*
                 * The sort entities may contain handles that actually do not
                 * exist any more in the handle table.
                 */
                CadObject result = objectMap.parseObjectPossiblyOrphaned(entityHandles[index]);
                return result;
            }

            @Override
            public int size()
            {
                return entityHandles.length;
            }
        };
    }

    @Override
    public String toString() {
        return "SORTENTSTABLE";
    }

}
