package objects;

import java.util.ArrayList;
import java.util.List;

import bitstreams.BitBuffer;
import bitstreams.Handle;
import dwglib.FileVersion;

/**
 * Parent is a null handle.
 * 
 * @author Nigel Westbury
 *
 */
public class LTypeControlObj extends NonEntityObject {

	public List<Handle> lineTypeHandles = new ArrayList<>();
	public Handle bylayerLinetypeHandle;
	public Handle byblockLinetypeHandle;

	@Override
	public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
		// 19.4.55 LINETYPE CONTROL (56)
		
		int numEntries = dataStream.getBL();

		dataStream.assertEndOfStream();
		
		// Here starts the handle area
		
		for (int i =0; i< numEntries; i++){
			Handle lineTypeHandle = handleStream.getHandle(handleOfThisObject);
			lineTypeHandles.add(lineTypeHandle);
		}

		bylayerLinetypeHandle = handleStream.getHandle();
		byblockLinetypeHandle = handleStream.getHandle();
		
		handleStream.advanceToByteBoundary();
		handleStream.assertEndOfStream();
	}

	public String toString() {
		return "LTYPE CONTROL OBJ";
	}

}
