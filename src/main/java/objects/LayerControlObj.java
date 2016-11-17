package objects;

import java.util.AbstractList;
import java.util.List;

import bitstreams.BitBuffer;
import bitstreams.Handle;
import dwglib.FileVersion;

public class LayerControlObj extends NonEntityObject {

    public Handle[] layerObjectHandles;

    public LayerControlObj(ObjectMap objectMap) {
        super(objectMap);
    }
    
    @Override
    public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
        // 19.4.51 LAYER CONTROL (50) page 157

        int numentries = dataStream.getBL();
        
        // The handles
        
        layerObjectHandles = new Handle[numentries];
        for (int i = 0; i < numentries; i++) {
            layerObjectHandles[i] = handleStream.getHandle(handleOfThisObject);
        }

        handleStream.advanceToByteBoundary();

        dataStream.assertEndOfStream();
        stringStream.assertEndOfStream();
        handleStream.assertEndOfStream();
    }

	public String toString() {
		return "LAYER CONTROL OBJ";
	}

    public List<Layer> getLayers()
    {
        return new AbstractList<Layer>() {

            @Override
            public Layer get(int index)
            {
                CadObject result = objectMap.parseObject(layerObjectHandles[index]);
                return (Layer) result;
            }

            @Override
            public int size()
            {
                return layerObjectHandles.length;
            }
        };
    }

}
