package com.onespatial.dwglib.objects;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.Handle;
import com.onespatial.dwglib.bitstreams.Point3D;

/**
 * parent is BLOCK HEADER
 * 
 * @author Nigel Westbury
 *
 */
public class BlockHeader extends NonEntityObject {

    public String entryName;
    public boolean sixtyFourFlag;
    private boolean blockIsXref;
    private boolean xrefOverlaid;
    public boolean loadedBit;
    public Point3D basePoint;
    public String xrefPName;
    public String blockDescription;
    private int [] previewData;
    public int insertUnits;
    public boolean explodable;
    public int blockScaling;
    private Handle firstEntityHandle;
    private Handle lastEntityHandle;
    private Handle[] ownedObjectHandles;
    private Handle endBlockHandle;
    private List<List<Handle>> insertHandles;
    private Handle layoutHandle;

    public BlockHeader(ObjectMap objectMap) {
        super(objectMap);
    }
    
    @Override
    public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
        // 19.4.50 BLOCK HEADER (49) page 155

        entryName = stringStream.getTU();
        sixtyFourFlag = dataStream.getB();
        /*
         * At this point we always seem to be three bits prior to a word
         * that best matches the expected flags.  We get there reliably if
         * we don't read the xrefindex value.  TODO investigate this as this
         * code may not be correct.
         */
//        int xrefordinal = dataStream.getBS();
        boolean xdep = dataStream.getB();
        boolean anonymous = dataStream.getB();
        boolean hasAttributes = dataStream.getB();
        blockIsXref = dataStream.getB();
        xrefOverlaid = dataStream.getB();
        loadedBit = dataStream.getB();
        int ownedObjectCount = dataStream.getBL();
        basePoint = dataStream.get3BD();
        xrefPName = stringStream.getTU();
        
        List<Integer> insertCounts = new ArrayList<>();
        int thisInsertCount = dataStream.getRC();
        while (thisInsertCount != 0) {
            insertCounts.add(thisInsertCount);
            thisInsertCount = dataStream.getRC();
        }
        
        blockDescription = stringStream.getTU();
        int sizeOfPreviewData = dataStream.getBL();
        previewData = dataStream.getBytes(sizeOfPreviewData);
        insertUnits = dataStream.getBS();
        explodable = dataStream.getB();
        blockScaling = dataStream.getRC();

        // The handles
        
        if (!blockIsXref && !xrefOverlaid) {
            firstEntityHandle = handleStream.getHandle(handleOfThisObject);
            lastEntityHandle = handleStream.getHandle(handleOfThisObject);
        }

        ownedObjectHandles = new Handle[ownedObjectCount];
        for (int i = 0; i< ownedObjectCount; i++) {
            ownedObjectHandles[i] = handleStream.getHandle();
        }

        endBlockHandle = handleStream.getHandle();

        insertHandles = new ArrayList<>();
        for (Integer insertCount : insertCounts) {
            List<Handle> thisList = new ArrayList<>();
            for (int i = 0; i< insertCount; i++) {
                Handle insertHandle = handleStream.getHandle();
                thisList.add(insertHandle);
            }
            insertHandles.add(thisList);
        }

        layoutHandle = handleStream.getHandle();

        handleStream.advanceToByteBoundary();

        dataStream.assertEndOfStream();
        stringStream.assertEndOfStream();
        handleStream.assertEndOfStream();
    }

	public String toString() {
		return "BLOCK HEADER";
	}

    public CadObject getFirstEntity() {
        if (firstEntityHandle == null) {
            return null;
        } else {
            CadObject result = objectMap.parseObjectPossiblyNull(firstEntityHandle);
            return (CadObject) result;
        }
    }

    public CadObject getLastEntity() {
        if (lastEntityHandle == null) {
            return null;
        } else {
            CadObject result = objectMap.parseObjectPossiblyNull(lastEntityHandle);
            return (CadObject) result;
        }
    }

    public List<EntityObject> getOwnedObjects()
    {
        return new AbstractList<EntityObject>() {

            @Override
            public EntityObject get(int index)
            {
                CadObject result = objectMap.parseObject(ownedObjectHandles[index]);
                return (EntityObject) result;
            }

            @Override
            public int size()
            {
                return ownedObjectHandles.length;
            }
        };
    }

    public CadObject getEndBlock() {
        CadObject result = objectMap.parseObject(endBlockHandle);
        return (CadObject) result;
    }

    /**
     * This method returns all inserts in a flat list.  This no doubt
     * needs to be changed to return a two dimensional list in case
     * consumers need the extra information.
     */
    public List<CadObject> getInserts()
    {
        List<CadObject> result = new ArrayList<>();
        
        for (List<Handle> x : this.insertHandles) {
            for (Handle y : x) {
                CadObject insert = objectMap.parseObject(y);
                result.add(insert);
            }
        }
        return result;
    }

    public CadObject getLayout() {
        CadObject result = objectMap.parseObjectPossiblyNull(layoutHandle);
        return (CadObject) result;
    }

}
