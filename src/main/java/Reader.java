/*
 * Copyright (c) 2016, 1Spatial Group Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Reads a DWG format file.
 *
 * @author Nigel Westbury
 */
public class Reader {

	private Issues issues = new Issues();


	// The following fields are extracted from the first 128 bytes of the file.

	private FileVersion fileVersion;
	private byte maintenanceReleaseVersion;
	private int previewAddress;
	private byte dwgVersion;
	private byte applicationMaintenanceReleaseVersion;
	private short codePage;
	private byte unknown1;
	private byte unknown2;
	private byte unknown3;
	private boolean areDataEncrypted;
	private boolean arePropertiesEncrypted;
	private boolean signData;
	private boolean addTimestamp;
	private int summaryInfoAddress;
	private int vbaProjectAddress;

	List<Section> sections = new ArrayList<>();

	List<ClassData> classes = new ArrayList<>();


	private Header header;


	private byte[] objectBuffer;


	private List<ObjectMapSection> objectMapSections;


	private static Set<Long> doneObjects = new HashSet<>();

	public Reader(File inputFile) throws IOException {
		try(FileInputStream inputStream = new FileInputStream(inputFile)) {
			FileChannel channel = inputStream.getChannel();
			ByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());

			buffer.order(ByteOrder.LITTLE_ENDIAN);

			byte [] versionAsByteArray = new byte[6];
			buffer.get(versionAsByteArray);
			String fileVersionAsString = new String(versionAsByteArray);
			fileVersion = new FileVersion(fileVersionAsString);

			expect(buffer, new byte[] { 0, 0, 0, 0, 0});
			maintenanceReleaseVersion = buffer.get();
			expectAnyOneOf(buffer, new byte[] { 0, 1, 3});
			previewAddress = buffer.getInt();
			dwgVersion = buffer.get();
			applicationMaintenanceReleaseVersion = buffer.get();
			codePage = buffer.getShort();
			unknown1 = buffer.get();
			unknown2 = buffer.get();
			unknown3 = buffer.get();
			int securityFlags = buffer.getInt();

			areDataEncrypted = (securityFlags & 0x0001) != 0;
			arePropertiesEncrypted = (securityFlags & 0x0002) != 0;
			signData = (securityFlags & 0x0010) != 0;
			addTimestamp = (securityFlags & 0x0020) != 0;

			buffer.position(32);

			summaryInfoAddress = buffer.getInt();
			vbaProjectAddress = buffer.getInt();
			expect(buffer, new byte[] { (byte)0x80, 0, 0, 0});

			// 4.1 R2004 File Header

			byte [] p = new byte [108];
			int q = 0;
			long sz = 0x6c;
			int randseed = 1;
			while (sz-- != 0)
			{
				randseed *= 0x343fd;
				randseed += 0x269ec3;
				p[q++] = (byte)((randseed >> 0x10) & 0xFF);
			}

			buffer.position(128);
			byte [] decryptedData = new byte[108];
			buffer.get(decryptedData);
			for (int i = 0; i < 108; i++) {
				decryptedData[i] ^= p[i];
			}

			ByteBuffer decryptedBuffer = ByteBuffer.wrap(decryptedData);

			byte [] signatureAsBytes = new byte[11];
			decryptedBuffer.get(signatureAsBytes);
			String signature = new String(signatureAsBytes, "UTF-8");
			if (!signature.equals("AcFssFcAJMB")) {
				throw new RuntimeException("signature is incorrect");
			}
			byte nullTerminator = decryptedBuffer.get();

			decryptedBuffer.order(ByteOrder.LITTLE_ENDIAN);

			decryptedBuffer.position(24);
			int rootTreeNodeGap = decryptedBuffer.getInt();
			int lowermostLeftTreeNodeGap = decryptedBuffer.getInt();
			int lowermostRightTreeNodeGap = decryptedBuffer.getInt();
			int unknown = decryptedBuffer.getInt();
			int lastSectionPageId = decryptedBuffer.getInt();
			long lastSectionPageEndAddress = decryptedBuffer.getLong();
			long repeatedHeaderData = decryptedBuffer.getLong();
			int gapAmount = decryptedBuffer.getInt();
			int sectionPageAmount = decryptedBuffer.getInt();
			expectInt(decryptedBuffer, 0x20);
			expectInt(decryptedBuffer, 0x80);
			expectInt(decryptedBuffer, 0x40);
			int sectionPageMapId = decryptedBuffer.getInt();
			long sectionPageMapAddress = decryptedBuffer.getLong();
			int sectionMapId = decryptedBuffer.getInt();
			int sectionPageArraySize = decryptedBuffer.getInt();
			int gapArraySize = decryptedBuffer.getInt();
			int crc = decryptedBuffer.getInt();

			decryptedData[0x68] = 0;
			decryptedData[0x69] = 0;
			decryptedData[0x6A] = 0;
			decryptedData[0x6B] = 0;
			int calculatedCrc = crc(decryptedData, 0x6C, 0);
			if (crc != calculatedCrc) {
				this.issues.addWarning("CRC does not match");
			}

			byte [] theRest = new byte[0x14];
			buffer.get(theRest);

			readSystemSectionPage(buffer, sectionPageMapAddress, sectionMapId);

//			Handle h = header.CLAYER.get();
			{


				// Let's try a few handles and see what we get...

							for (Handle h : new Handle [] { 
									header.CLAYER.get(), 
									header.TEXTSTYLE.get(), 
									header.CELTYPE.get(), 
									header.CMATERIAL.get(), 
									header.DIMSTYLE.get(), 
									header.CMLSTYLE.get(), 
									header.DIMTXSTY.get(), 
									header.DIMLDRBLK.get(), 
									header.DIMBLK.get(), 
									header.DIMBLK1.get(), 
									header.DIMBLK2.get(), 
									header.DIMLTYPE.get(), 
									header.DIMLTEX1.get(), 
									header.DIMLTEX2.get(), 
									header.BLOCK_CONTROL_OBJECT.get(), 
									header.LAYER_CONTROL_OBJECT.get(), 
									header.STYLE_CONTROL_OBJECT.get(), 
									header.LINETYPE_CONTROL_OBJECT.get(), 
									header.VIEW_CONTROL_OBJECT.get(), 
									header.UCS_CONTROL_OBJECT.get(), 
									header.VPORT_CONTROL_OBJECT.get(), 
									header.APPID_CONTROL_OBJECT.get(), 
									header.DIMSTYLE_CONTROL_OBJECT.get(), 
									header.DICTIONARY_ACAD_GROUP.get(), 
									header.DICTIONARY_ACAD_MLINESTYLE.get(), 
									header.DICTIONARY_NAMED_OBJECTS.get(), 
									header.DICTIONARY_LAYOUTS.get(), 
									header.DICTIONARY_PLOTSETTINGS.get(), 
									header.DICTIONARY_PLOTSTYLES.get(), 
									header.DICTIONARY_MATERIALS.get(), 
									header.DICTIONARY_COLORS.get(), 
									header.DICTIONARY_VISUALSTYLE.get(), 
									header.UNKNOWN.get(), 
									header.CPSNID.get(), 
									header.BLOCK_RECORD_PAPER_SPACE.get(), 
									header.BLOCK_RECORD_MODEL_SPACE.get(), 
									header.LTYPE_BYLAYER.get(), 
									header.LTYPE_BYBLOCK.get(), 
									header.LTYPE_CONTINUOUS.get(), 
									header.INTERFEREOBJVS.get(), 
									header.INTERFEREVPVS.get(), 
									header.DRAGVS.get()
							} ) {
				
								if (h == null) continue;
								if (h.offset == 0) continue;  // I assume this is what is known as a null handle?
				parseObject(h);
							}
			}
		}
	}

	private void parseObject(Handle h) {
		Long offsetIntoObjectMap = null;
		for (ObjectMapSection section : this.objectMapSections) {
			int offset = h.offset;
			offsetIntoObjectMap = section.locationMap.get(offset);
			if (offsetIntoObjectMap != null) {
				break;
			}
		}
		
		if (offsetIntoObjectMap == null)
		    System.out.println("");
		assert offsetIntoObjectMap != null;

		if (doneObjects.contains(offsetIntoObjectMap)) {
			return;
		}
		doneObjects.add(offsetIntoObjectMap);
		
		BitStreams bitStreams = new BitStreams(objectBuffer, offsetIntoObjectMap.intValue());
		BitBuffer dataStream = bitStreams.getDataStream();
		BitBuffer stringStream = bitStreams.getStringStream();
		BitBuffer handleStream = bitStreams.getHandleStream();
		
		int objectType = dataStream.getOT();

		
		if (objectType >= 500) {
			int classIndex = objectType - 500;
			ClassData thisClass = classes.get(classIndex);
			System.out.println("Object Type: " + thisClass.classdxfname);
		} else {
			String name = "<unknown>";
			switch (objectType) {
			case 3:
				name = "ATTDEF"; break;
			case 4:
				name = "BLOCK"; break;
			case 5:
				name = "ENDBLK"; break;
			case 38:
				name = "3DSOLID"; break;
			case 42:
				name = "DICTIONARY"; break;
			case 49:
				name = "BLOCK HEADER"; break;
			case 50:
				name = "LAYER CONTROL OBJ"; break;
			case 51:
				name = "LAYER"; break;
			case 52:
				name = "STYLE CONTROL OBJ"; break;
			case 53:
				name = "STYLE"; break;
			case 56:
				name = "LTYPE CONTROL OBJ"; break;
			case 57:
				name = "LTYPE"; break;
			case 60:
				name = "VIEW CONTROL OBJ"; break;
			case 61:
				name = "VIEW"; break;
			case 62:
				name = "UCS CONTROL OBJ"; break;
			case 63:
				name = "UCS"; break;
			case 64:
				name = "VPORT CONTROL OBJ"; break;
			case 65:
				name = "VPORT"; break;
			case 66:
				name = "APPID CONTROL OBJ"; break;
			case 67:
				name = "APPID"; break;
			case 69:
				name = "DIMSTYLE"; break;
			case 73:
				name = "MLINESTYLE"; break;
			case 79:
				name = "XRECORD"; break;
			case 80:
				name = "ACDBPLACEHOLDER"; break;
			case 82:
				name = "LAYOUT"; break;
			}
			System.out.println("Object Type: " + objectType + " = " + name);
		}

		Handle handleOfThisObject = dataStream.getHandle();

		// Page 254 Chapter 27 Extended Entity Data

		int sizeOfExtendedObjectData = dataStream.getBS();
		while (sizeOfExtendedObjectData != 0) {
		    Handle appHandle = dataStream.getHandle();
			for (int i = 0; i < sizeOfExtendedObjectData*8; i++) {
				dataStream.getB();
			}
	        sizeOfExtendedObjectData = dataStream.getBS();
		}

		// 19.4.55
		int numReactors = dataStream.getBL();

		boolean xDicMissingFlag = dataStream.getB();
		if (fileVersion.is2013OrLater()) {
			boolean hasBinaryData = dataStream.getB();
		}

		// Page 99 Object data (varies by type of object)
		
        if (objectType == 38) {  // 3DSOLID
            // 19.4.39 REGION (37), 3DSOLID (38), BODY (39) page 137

        	// TODO need to read as Common Entity Data is described in 19.4.1 page 104
        	// (Common Entity Format read above)
        	
            boolean acisEmptyBit = dataStream.getB();
            boolean unknownBit = dataStream.getB();

            int version = dataStream.getBS();

        } else if (objectType == 42) {  // DICTIONARY
            // 19.4.42 DICTIONARY (42)

            int numItems = dataStream.getBL();

            int cloningFlag = dataStream.getBS();
            int hardOwnerFlag = dataStream.getRC();
            
            Handle parentHandle = handleStream.getHandle(handleOfThisObject);

            List<Handle> reactorHandles = new ArrayList<>();
            for (int i = 0; i< numReactors; i++) {
                Handle reactorHandle = handleStream.getHandle(handleOfThisObject);
                reactorHandles.add(reactorHandle);
            }

            if (!xDicMissingFlag) {
                Handle xdicobjhandle = handleStream.getHandle();
            }
            
            Map<String, Handle> dictionaryMap = new HashMap<>();
            for (int i = 0; i < numItems; i++) {
                String key = stringStream.getTU();
                Handle handle = handleStream.getHandle(handleOfThisObject);
                dictionaryMap.put(key, handle);
            }
            
            handleStream.advanceToByteBoundary();

            dataStream.assertEndOfStream();
            stringStream.assertEndOfStream();
            handleStream.assertEndOfStream();

        } else if (objectType == 53) {  // SHAPEFILE or STYLE ??????
            // 19.4.54 SHAPEFILE (53)
        
            
            
            String entryName = stringStream.getTU();
            String fontName = stringStream.getTU();
            String bigFontName = stringStream.getTU();
            
            

        } else if (objectType == 51) {  // LAYER
			// 19.4.52 LAYER (51)
		
			int numEntries = dataStream.getBL();

			// TODO process remaining CRC data in data stream
//			dataStream.assertEndOfStream();
			
			
			Handle layerControlHandle = handleStream.getHandle(handleOfThisObject);

			List<Handle> reactorHandles = new ArrayList<>();
			for (int i = 0; i< numReactors; i++) {
				Handle reactorHandle = handleStream.getHandle(handleOfThisObject);
				reactorHandles.add(reactorHandle);
			}

			if (!xDicMissingFlag) {
				Handle xdicobjhandle = handleStream.getHandle();
			}

			Handle externalReferenceBlockHandle = handleStream.getHandle();
			Handle plotStyleHandle = handleStream.getHandle();
			Handle lineTypeHandle = handleStream.getHandle(handleOfThisObject);
			Handle materialHandle = handleStream.getHandle(handleOfThisObject);
			
			// We seem to have a handle too many.  TODO: check this out.
//			Handle nullHandle = handleStream.getHandle();

			handleStream.advanceToByteBoundary();
			handleStream.assertEndOfStream();

			for (Handle reactorHandle : reactorHandles) {
				if (reactorHandle.offset == 0) {
					System.out.println("Object: null");
				} else {
					parseObject(reactorHandle);
				}
			}
		} else if (objectType == 56) {
			// 19.4.55 LINETYPE CONTROL (56)
			
			int numEntries = dataStream.getBL();

			dataStream.assertEndOfStream();
			
			// Here starts the handle area
			
			Handle nullHandle = handleStream.getHandle();

			if (!xDicMissingFlag) {
				Handle xdicobjhandle = handleStream.getHandle();
			}

			List<Handle> lineTypeHandles = new ArrayList<>();
			for (int i =0; i< numEntries; i++){
				Handle lineTypeHandle = handleStream.getHandle(handleOfThisObject);
				lineTypeHandles.add(lineTypeHandle);
			}

			Handle bylayerLinetypeHandle = handleStream.getHandle();
			Handle byblockLinetypeHandle = handleStream.getHandle();
			
			handleStream.advanceToByteBoundary();
			handleStream.assertEndOfStream();
			
			for (Handle lineTypeHandle : lineTypeHandles) {
				parseObject(lineTypeHandle);
			}
			parseObject(bylayerLinetypeHandle);
			parseObject(byblockLinetypeHandle);
			
		} else if (objectType == 57) { // LTYPE
			// 19.4.56 LTYPE 57    

			String entryName = stringStream.getTU();
			
			boolean sixtyFourFlag = dataStream.getB();
			dataStream.getB();
			dataStream.getB();
			dataStream.getB();
//			int xRefOrdinal = dataStream.getBS();
//			boolean xDep = dataStream.getB();
//			String description = stringStream.getTU();
//			double patternLen = dataStream.getBD();
			int alignment = dataStream.getRC();
			int numDashes = dataStream.getRC();

			for (int i = 0; i < numDashes; i++) {
				double dashLength = dataStream.getBD();
				int complexShapecode = dataStream.getBS();
				double xOffset  = dataStream.getRD();
				double yOffset  = dataStream.getRD();
				double scale  = dataStream.getRD();
				double rotation  = dataStream.getRD();
				int shapeFlag = dataStream.getBS();
			}

			dataStream.assertEndOfStream();
			
			// No 512 byte area in sample file

			Handle lypeControlHandle = handleStream.getHandle(handleOfThisObject);
			for (int i = 0; i < numReactors; i++) {
				Handle reactorHandle = handleStream.getHandle(handleOfThisObject);
			}
			if (!xDicMissingFlag) {
				Handle xdicobjhandle = handleStream.getHandle();
			}
			Handle externalReferenceBlockHandle = handleStream.getHandle();
			
			for (int i = 0; i < numDashes; i++) {
				Handle shapefileForDashHandle = handleStream.getHandle();
				Handle shapefileForShapeHandle = handleStream.getHandle();
			}
			
			handleStream.advanceToByteBoundary();
			handleStream.assertEndOfStream();
			
			System.out.println("done objects");

		} else if (objectType == 65) { // VPORT
            // 19.4.62 VPORT 65 page 169    

		    // Similar to LTYPE 57
		    
            String entryName = stringStream.getTU();
            
            boolean sixtyFourFlag = dataStream.getB();
//            int xRefOrdinal = dataStream.getBS();
            boolean xDep = dataStream.getB();
          double viewHeight = dataStream.getBD();
          double aspectRatio = dataStream.getBD();
          double viewCenter1 = dataStream.getRD();
          double viewCenter2 = dataStream.getRD();
          double viewTarget1 = dataStream.getBD();
          double viewTarget2 = dataStream.getBD();
          double viewTarget3 = dataStream.getBD();
          double viewDir1 = dataStream.getBD();
          double viewDir2 = dataStream.getBD();
          double viewDir3 = dataStream.getBD();
          double viewTwist = dataStream.getBD();
          double lensLength = dataStream.getBD();
          double frontClip = dataStream.getBD();
          double backClip = dataStream.getBD();
          boolean viewMode0 = dataStream.getB();
          boolean viewMode1 = dataStream.getB();
          boolean viewMode2 = dataStream.getB();
          boolean viewMode3 = dataStream.getB();
          int renderMode = dataStream.getRC();
          boolean useDefaultLights = dataStream.getB();
          int defaultLightingType = dataStream.getRC();
          double brightness = dataStream.getBD();
          double contrast = dataStream.getBD();
          CMC ambientColor = dataStream.getCMC();
          double lowerLeft1 = dataStream.getRD(); 
          double lowerLeft2 = dataStream.getRD(); 
          double upperRight1 = dataStream.getRD(); 
          double upperRight2 = dataStream.getRD(); 
          boolean UCSFOLLOW = dataStream.getB();
          int circleZoom = dataStream.getBS(); 
          boolean fastZoom = dataStream.getB();
          boolean ucsIcon1 = dataStream.getB();
          boolean ucsIcon2 = dataStream.getB();
          boolean gridFlag = dataStream.getB();
          double gridSpacing1 = dataStream.getRD(); 
          double gridSpacing2 = dataStream.getRD(); 
          boolean snapFlag = dataStream.getB();
          boolean snapStyle = dataStream.getB();
          int snapIsopair = dataStream.getBS(); 
          double snapRot = dataStream.getBD(); 
          double snapBase1 = dataStream.getRD(); 
          double snapBase2 = dataStream.getRD(); 
          double snapSpacing1 = dataStream.getRD(); 
          double snapSpacing2 = dataStream.getRD(); 
          boolean unknown = dataStream.getB();
          boolean ucsPerViewport = dataStream.getB();
          double ucsOrigin1 = dataStream.getBD(); 
          double ucsOrigin2 = dataStream.getBD(); 
          double ucsOrigin3 = dataStream.getBD(); 
          double ucsXAxis1 = dataStream.getBD(); 
          double ucsXAxis2 = dataStream.getBD(); 
          double ucsXAxis3 = dataStream.getBD(); 
          double ucsYAxis1 = dataStream.getBD(); 
          double ucsYAxis2 = dataStream.getBD(); 
          double ucsYAxis3 = dataStream.getBD(); 
          double ucsElevation = dataStream.getBD(); 
          int ucsOrthographicType = dataStream.getBS(); 
          int gridFlags = dataStream.getBS(); 
          int gridMajor = dataStream.getBS(); 
          
          dataStream.assertEndOfStream();
          
          System.out.println("done vport");
            
		    
		} else {
			List<Handle> lineTypeHandles = new ArrayList<>();
			try {
			do {
				Handle referencedHandle = handleStream.getHandle(handleOfThisObject);
				lineTypeHandles.add(referencedHandle);
			} while (true);
			} catch (RuntimeException e) {
				
			}
			handleStream.advanceToByteBoundary();
			handleStream.assertEndOfStream();
			
			for (Handle referencedHandle : lineTypeHandles) {
				if (referencedHandle.offset == 0) {
					System.out.println("Object: null");
				} else {
					parseObject(referencedHandle);
				}
			}
			
		}
	}

	int crc32Table[] =
		{
		0x00000000, 0x77073096, 0xee0e612c, 0x990951ba,
		0x076dc419, 0x706af48f, 0xe963a535, 0x9e6495a3,
		0x0edb8832, 0x79dcb8a4, 0xe0d5e91e, 0x97d2d988,
		0x09b64c2b, 0x7eb17cbd, 0xe7b82d07, 0x90bf1d91,
		0x1db71064, 0x6ab020f2, 0xf3b97148, 0x84be41de,
		0x1adad47d, 0x6ddde4eb, 0xf4d4b551, 0x83d385c7,
		0x136c9856, 0x646ba8c0, 0xfd62f97a, 0x8a65c9ec,
		0x14015c4f, 0x63066cd9, 0xfa0f3d63, 0x8d080df5,
		0x3b6e20c8, 0x4c69105e, 0xd56041e4, 0xa2677172,
		0x3c03e4d1, 0x4b04d447, 0xd20d85fd, 0xa50ab56b,
		0x35b5a8fa, 0x42b2986c, 0xdbbbc9d6, 0xacbcf940,
		0x32d86ce3, 0x45df5c75, 0xdcd60dcf, 0xabd13d59,
		0x26d930ac, 0x51de003a, 0xc8d75180, 0xbfd06116,
		0x21b4f4b5, 0x56b3c423, 0xcfba9599, 0xb8bda50f,
		0x2802b89e, 0x5f058808, 0xc60cd9b2, 0xb10be924,
		0x2f6f7c87, 0x58684c11, 0xc1611dab, 0xb6662d3d,
		0x76dc4190, 0x01db7106, 0x98d220bc, 0xefd5102a,
		0x71b18589, 0x06b6b51f, 0x9fbfe4a5, 0xe8b8d433,
		0x7807c9a2, 0x0f00f934, 0x9609a88e, 0xe10e9818,
		0x7f6a0dbb, 0x086d3d2d, 0x91646c97, 0xe6635c01,
		0x6b6b51f4, 0x1c6c6162, 0x856530d8, 0xf262004e,
		0x6c0695ed, 0x1b01a57b, 0x8208f4c1, 0xf50fc457,
		0x65b0d9c6, 0x12b7e950, 0x8bbeb8ea, 0xfcb9887c,
		0x62dd1ddf, 0x15da2d49, 0x8cd37cf3, 0xfbd44c65,
		0x4db26158, 0x3ab551ce, 0xa3bc0074, 0xd4bb30e2,
		0x4adfa541, 0x3dd895d7, 0xa4d1c46d, 0xd3d6f4fb,
		0x4369e96a, 0x346ed9fc, 0xad678846, 0xda60b8d0,
		0x44042d73, 0x33031de5, 0xaa0a4c5f, 0xdd0d7cc9,
		0x5005713c, 0x270241aa, 0xbe0b1010, 0xc90c2086,
		0x5768b525, 0x206f85b3, 0xb966d409, 0xce61e49f,
		0x5edef90e, 0x29d9c998, 0xb0d09822, 0xc7d7a8b4,
		0x59b33d17, 0x2eb40d81, 0xb7bd5c3b, 0xc0ba6cad,
		0xedb88320, 0x9abfb3b6, 0x03b6e20c, 0x74b1d29a,
		0xead54739, 0x9dd277af, 0x04db2615, 0x73dc1683,
		0xe3630b12, 0x94643b84, 0x0d6d6a3e, 0x7a6a5aa8,
		0xe40ecf0b, 0x9309ff9d, 0x0a00ae27, 0x7d079eb1,
		0xf00f9344, 0x8708a3d2, 0x1e01f268, 0x6906c2fe,
		0xf762575d, 0x806567cb, 0x196c3671, 0x6e6b06e7,
		0xfed41b76, 0x89d32be0, 0x10da7a5a, 0x67dd4acc,
		0xf9b9df6f, 0x8ebeeff9, 0x17b7be43, 0x60b08ed5,
		0xd6d6a3e8, 0xa1d1937e, 0x38d8c2c4, 0x4fdff252,
		0xd1bb67f1, 0xa6bc5767, 0x3fb506dd, 0x48b2364b,
		0xd80d2bda, 0xaf0a1b4c, 0x36034af6, 0x41047a60,
		0xdf60efc3, 0xa867df55, 0x316e8eef, 0x4669be79,
		0xcb61b38c, 0xbc66831a, 0x256fd2a0, 0x5268e236,
		0xcc0c7795, 0xbb0b4703, 0x220216b9, 0x5505262f,
		0xc5ba3bbe, 0xb2bd0b28, 0x2bb45a92, 0x5cb36a04,
		0xc2d7ffa7, 0xb5d0cf31, 0x2cd99e8b, 0x5bdeae1d,
		0x9b64c2b0, 0xec63f226, 0x756aa39c, 0x026d930a,
		0x9c0906a9, 0xeb0e363f, 0x72076785, 0x05005713,
		0x95bf4a82, 0xe2b87a14, 0x7bb12bae, 0x0cb61b38,
		0x92d28e9b, 0xe5d5be0d, 0x7cdcefb7, 0x0bdbdf21,
		0x86d3d2d4, 0xf1d4e242, 0x68ddb3f8, 0x1fda836e,
		0x81be16cd, 0xf6b9265b, 0x6fb077e1, 0x18b74777,
		0x88085ae6, 0xff0f6a70, 0x66063bca, 0x11010b5c,
		0x8f659eff, 0xf862ae69, 0x616bffd3, 0x166ccf45,
		0xa00ae278, 0xd70dd2ee, 0x4e048354, 0x3903b3c2,
		0xa7672661, 0xd06016f7, 0x4969474d, 0x3e6e77db,
		0xaed16a4a, 0xd9d65adc, 0x40df0b66, 0x37d83bf0,
		0xa9bcae53, 0xdebb9ec5, 0x47b2cf7f, 0x30b5ffe9,
		0xbdbdf21c, 0xcabac28a, 0x53b39330, 0x24b4a3a6,
		0xbad03605, 0xcdd70693, 0x54de5729, 0x23d967bf,
		0xb3667a2e, 0xc4614ab8, 0x5d681b02, 0x2a6f2b94,
		0xb40bbe37, 0xc30c8ea1, 0x5a05df1b, 0x2d02ef8d 
		};
	
	int crc(byte[] p, int n, int seed)
	{
		int invertedCrc = ~seed;
		for (int index = 0; index < n; index++) {
			byte b = p[index];
			int i = (invertedCrc >> 8) & 0xFFFFFF;
			invertedCrc = i ^ crc32Table[(invertedCrc ^ b) & 0xff];
		} 
		return ~invertedCrc; 
	}
	
	/**
	 * Section 4.3 System section page
	 */
	private void readSystemSectionPage(ByteBuffer buffer, long sectionPageMapAddress, int sectionMapId) {
		if (0x100 + sectionPageMapAddress > Integer.MAX_VALUE) {
			throw new RuntimeException("sectionPageMapAddress is too big for us.");
		}
		buffer.position(0x100 + (int)sectionPageMapAddress);

		SectionPage sectionPage = readSystemSectionPage(buffer, 0x41630E3B);

		// 4.4 2004 Section page map

		ByteBuffer expandedBuffer = ByteBuffer.wrap(sectionPage.expandedData);

		expandedBuffer.order(ByteOrder.LITTLE_ENDIAN);
		int address = 0x100;
		do {
			int sectionPageNumber = expandedBuffer.getInt();
			int sectionSize = expandedBuffer.getInt();

			if (sectionPageNumber > 0) {
				sections .add(new Section(sectionPageNumber, address, sectionSize));
			} else {
				int parent = expandedBuffer.getInt();
				int left = expandedBuffer.getInt();
				int right = expandedBuffer.getInt();
				int hex00 = expandedBuffer.getInt();

				// Really only useful if writing files is supported
				// but add to our data structure so we are ready.
				sections.add(new SectionGap(sectionPageNumber, address, sectionSize, parent, left, right));
			}

			address += sectionSize;
		} while (expandedBuffer.position() != sectionPage.expandedData.length);


		Section sectionMap = null;
		for (Section eachSection : sections) {
			if (eachSection.sectionPageNumber == sectionMapId) {
				sectionMap = eachSection;
				break;
			}
		}

		buffer.position(sectionMap.address);


		// 4.3 (page 25) System section page:

		SectionPage sectionPage2 = readSystemSectionPage(buffer, 0x4163003B);

		// The expanded data is described in 4.5 2004 Data section map.

		ByteBuffer sectionPageBuffer = ByteBuffer.wrap(sectionPage2.expandedData);
		sectionPageBuffer.order(ByteOrder.LITTLE_ENDIAN);

		int numDescriptions = sectionPageBuffer.getInt();
		int hex02 = sectionPageBuffer.getInt();
		int hex7400 = sectionPageBuffer.getInt();
		int hex00 = sectionPageBuffer.getInt();
		int unknown2 = sectionPageBuffer.getInt();

		for (int i = 0; i < numDescriptions; i++) {
			long sizeOfSection = sectionPageBuffer.getLong();
			int pageCount = sectionPageBuffer.getInt();
			int maxDecompressedSize = sectionPageBuffer.getInt();
			int unknown3 = sectionPageBuffer.getInt();
			int compressed = sectionPageBuffer.getInt();
			int sectionId = sectionPageBuffer.getInt();
			int encrypted = sectionPageBuffer.getInt();

			boolean isCompressed;
			switch (compressed) {
			case 1:
				isCompressed = false;
				break;
			case 2:
				isCompressed = true;
				break;
			default:
				throw new RuntimeException("bad enum");
			}

			Boolean isEncrypted;
			switch (compressed) {
			case 0:
				isEncrypted = false;
				break;
			case 1:
				isEncrypted = true;
				break;
			case 2:
				isEncrypted = null; // Indicates unknown
				break;
			default:
				throw new RuntimeException("bad enum");
			}


			byte [] sectionNameAsBytes = new byte[64];
			sectionPageBuffer.get(sectionNameAsBytes);

			int index = 0;
			while (index != 64 && sectionNameAsBytes[index] != 0) {
				index++;
			}

			String sectionName;
			try {
				sectionName = new String(sectionNameAsBytes, 0, index, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}

			if (sectionName.equals("AcDb:Header")) {
				// Page 68

				int pageNumber = sectionPageBuffer.getInt();
				int dataSize = sectionPageBuffer.getInt();
				long startOffset = sectionPageBuffer.getLong();

				Section classesData = null;
				for (Section eachSection : sections) {
					if (eachSection.sectionPageNumber == pageNumber) {
						classesData = eachSection;
						break;
					}
				}

				buffer.position(classesData.address);

				int secMask = 0x4164536b ^ classesData.address;

				int typeA = buffer.getInt();
				int typeB = typeA ^ secMask;
				int sectionPageType = typeA ^ classesData.address;
				int sectionNumber = buffer.getInt() ^ secMask;
				int dataSize2 = buffer.getInt() ^ secMask;  // dataSize
				int pageSize = buffer.getInt() ^ secMask;  // classData.sectionSize
				int startOffset2 = buffer.getInt() ^ secMask;
				int pageHeaderChecksum = buffer.getInt() ^ secMask;
				int dataChecksum = buffer.getInt() ^ secMask;
				int unknown5 = buffer.getInt() ^ secMask;


				byte [] compressedData = new byte[dataSize2];
				buffer.get(compressedData);

				byte [] expandedData = new Expander(compressedData, maxDecompressedSize).result;

				ByteBuffer headerBuffer = ByteBuffer.wrap(expandedData);
				headerBuffer.order(ByteOrder.LITTLE_ENDIAN);

				// 8 Data section AcDb:Header (HEADER VARIABLES), page 68

				// The signature
				byte[] headerSignature = new byte [] { (byte)0xCF,0x7B,0x1F,0x23,(byte)0xFD,(byte)0xDE,0x38,(byte)0xA9,0x5F,0x7C,0x68,(byte)0xB8,0x4E,0x6D,0x33,0x5F };

				BitStreams bitStreams = new BitStreams(expandedData, headerSignature);
				double x = 1.0;
				long xx = Double.doubleToLongBits(x);
				for (int k=0; k < 8 ; k++) {
					System.out.println(xx & 0xFF);
					xx >>= 8;
				}

				header = new Header(bitStreams, fileVersion);

			} else if (sectionName.equals("AcDb:Handles")) {
				// Page 236 The Object Map

				int pageNumber = sectionPageBuffer.getInt();
				int dataSize = sectionPageBuffer.getInt();
				long startOffset = sectionPageBuffer.getLong();

				Section classesData = null;
				for (Section eachSection : sections) {
					if (eachSection.sectionPageNumber == pageNumber) {
						classesData = eachSection;
						break;
					}
				}

				buffer.position(classesData.address+32);

				byte [] compressedData = new byte[dataSize];
				buffer.get(compressedData);

				byte [] expandedData = new Expander(compressedData, maxDecompressedSize).result;


				ByteBuffer classesBuffer = ByteBuffer.wrap(expandedData);

				classesBuffer.order(ByteOrder.BIG_ENDIAN);

				objectMapSections = new ArrayList<>();

				int lastHandle = 0;
				long lastLoc = 0L;

				short sectionSize = classesBuffer.getShort();
				while (sectionSize != 2) {
					ObjectMapSection section = new ObjectMapSection();

					int endPosition = classesBuffer.position() - 2 + sectionSize; // Less length of two-byte CRC at end

					while (classesBuffer.position() != endPosition) {
						int handleOffset = getMC(classesBuffer);
						int locationOffset = getMC(classesBuffer);
						System.out.println("offset " + handleOffset + " = " + locationOffset);

						lastHandle += handleOffset;
						lastLoc += locationOffset;

						section.add(lastHandle, lastLoc);
					}

					int crc = classesBuffer.getShort();

					objectMapSections.add(section);

					sectionSize = classesBuffer.getShort();
				}

				System.out.println("Sections in handle map read");
			} else if (sectionName.equals("AcDb:AcDbObjects")) {
				List<byte[]> objectBuffers = new ArrayList<>();
				int totalSize = 0;

				for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
					int pageNumber = sectionPageBuffer.getInt();
					int dataSize = sectionPageBuffer.getInt();
					long startOffset = sectionPageBuffer.getLong();

					Section classesData = null;
					for (Section eachSection : sections) {
						if (eachSection.sectionPageNumber == pageNumber) {
							classesData = eachSection;
							break;
						}
					}

					buffer.position(classesData.address+32);

					byte [] compressedData = new byte[dataSize];
					buffer.get(compressedData);

					byte [] expandedData = new Expander(compressedData, maxDecompressedSize).result;

					objectBuffers.add(expandedData);

					totalSize += expandedData.length;
				}

				objectBuffer = new byte[totalSize];
				int offset = 0;
				for (byte[] objectBufferPart : objectBuffers) {
					for (byte b : objectBufferPart) {
						objectBuffer[offset++] = b;
					}
				}

				System.out.println("end of objects");

			} else if (sectionName.equals("AcDb:Classes")) {
				int pageNumber = sectionPageBuffer.getInt();
				int dataSize = sectionPageBuffer.getInt();
				long startOffset = sectionPageBuffer.getLong();

				Section classesData = null;
				for (Section eachSection : sections) {
					if (eachSection.sectionPageNumber == pageNumber) {
						classesData = eachSection;
						break;
					}
				}

				buffer.position(classesData.address);

				int secMask = 0x4164536b ^ classesData.address;

				int typeA = buffer.getInt();
				int typeB = typeA ^ secMask;
				int sectionPageType = typeA ^ classesData.address;
				int sectionNumber = buffer.getInt() ^ secMask;
				int dataSize2 = buffer.getInt() ^ secMask;  // dataSize
				int pageSize = buffer.getInt() ^ secMask;  // class section sectionSize
				int startOffset2 = buffer.getInt() ^ secMask;
				int pageHeaderChecksum = buffer.getInt() ^ secMask;
				int dataChecksum = buffer.getInt() ^ secMask;
				int unknown = buffer.getInt() ^ secMask;

				byte [] compressedData = new byte[dataSize2];
				buffer.get(compressedData);

				byte [] expandedData = new Expander(compressedData, maxDecompressedSize).result;

				// 5.8 AcDb:Classes Section

				byte[] classesSignature = new byte [] { (byte)0x8D, (byte)0xA1, (byte)0xC4, (byte)0xB8, (byte)0xC4, (byte)0xA9, (byte)0xF8, (byte)0xC5, (byte)0xC0, (byte)0xDC, (byte)0xF4, (byte)0x5F, (byte)0xE7, (byte)0xCF, (byte)0xB6, (byte)0x8A};

				BitStreams bitStreams = new BitStreams(expandedData, classesSignature);

				BitBuffer bitClasses = bitStreams.getDataStream();
				BitBuffer bitClassesStrings = bitStreams.getStringStream();

				int maximumClassNumber = bitClasses.getBL();
				boolean unknownBool = bitClasses.getB();

				// Here starts the class data (repeating)

				// Repeated until we exhaust the data
				do {
					ClassData classData = new ClassData(bitClasses, bitClassesStrings);
					classes.add(classData);
				} while (bitClasses.hasMoreData());

				/*
				 * If all goes to plan, we should at the same time exactly reach
				 * the end of both the data section and the string section.
				 */
				assert !bitClassesStrings.hasMoreData();

				int expectedNumberOfClasses = maximumClassNumber - 499;
				assert classes.size() == expectedNumberOfClasses;

			} else {
				for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
					int pageNumber = sectionPageBuffer.getInt();
					int dataSize = sectionPageBuffer.getInt();
					long startOffset = sectionPageBuffer.getLong();
				}
			}

		}

	}

	public static int getMC(ByteBuffer buffer) {
		int result = 0;
		int shift = 0;

		byte b = buffer.get();
		while ((b & 0x80) != 0) {
			int byteValue = (b & 0x7F);
			result |= (byteValue << shift);
			shift += 7;
			b = buffer.get();
		}

		boolean signBit = (b & 0x40) != 0;
		int byteValue = (b & 0x3F);
		result |= (byteValue << shift);

		if (signBit) {
			result = -result;
		}

		return result;
	}

	public static int getUnsignedMC(ByteBuffer buffer) {
		int result = 0;
		int shift = 0;

		boolean highBit;
		do {
			byte b = buffer.get();
			highBit = (b & 0x80) != 0;
			int byteValue = (b & 0x7F);
			result |= (byteValue << shift);
			shift += 7;
		} while (highBit);

		return result;
	}

	public static int getMS(ByteBuffer buffer) {
		int result = 0;
		int shift = 0;

		assert buffer.order() == ByteOrder.LITTLE_ENDIAN;

		boolean highBit;
		do {
			short word = buffer.getShort();
			highBit = (word & 0x8000) != 0;
			int wordValue = (word & 0x7FFF);
			result |= (wordValue << shift);
			shift += 15;
		} while (highBit);

		return result;
	}

	private SectionPage readSystemSectionPage(ByteBuffer buffer, int expectedPageType) {
		int pageType = buffer.getInt();
		int decompressedSize = buffer.getInt();
		int compressedSize = buffer.getInt();
		int compressionType = buffer.getInt();
		int sectionPageChecksum = buffer.getInt();

		byte [] compressedData = new byte[compressedSize];
		buffer.get(compressedData);

		int pageType2 = buffer.getInt();
		int decompressedSize2 = buffer.getInt();
		int compressedSize2 = buffer.getInt();
		int compressionType2 = buffer.getInt();
		int sectionPageChecksum2 = buffer.getInt();

		if (pageType != expectedPageType) {
			throw new RuntimeException();
		}

		byte [] expandedData = new Expander(compressedData, decompressedSize).result;

		SectionPage result = new SectionPage(pageType, expandedData);

		return result;
	}

	private void expectInt(ByteBuffer buffer, int expected) {
		int actual = buffer.getInt();
		if (actual != expected) {
			issues .addWarning("expected " + expected + " at position " + (buffer.position()-1) + " (4 bytes) but found " + actual + ".");
		}
	}

	private void expect(ByteBuffer buffer, byte[] expectedBytes) {
		for (byte expectedByte : expectedBytes) {
			expect(buffer, expectedByte);
		}
	}

	private void expect(ByteBuffer buffer, byte expectedByte) {
		byte actual = buffer.get();
		if (actual != expectedByte) {
			issues .addWarning("expected " + expectedByte + " at position " + (buffer.position()-1) + " but found " + actual + ".");
		}
	}

	private void expectAnyOneOf(ByteBuffer buffer, byte[] expectedBytes) {
		byte actual = buffer.get();
		for (byte expectedByte : expectedBytes) {
			if (actual == expectedByte) {
				return;
			}
		}
		issues .addWarning("expected one of " + expectedBytes.length + " options at position " + (buffer.position()-1) + " but found " + actual + ".");
	}

	public String getVersion() {
		return fileVersion.getVersionYear();
	}

	public class Section {
		final int sectionPageNumber;

		final int address;

		final int sectionSize;

		public Section(int sectionPageNumber, int address, int sectionSize) {
			this.sectionPageNumber = sectionPageNumber;
			this.address = address;
			this.sectionSize = sectionSize;
		}

	}

	public class SectionGap extends Section {
		final int parent;

		final int left;

		final int right;

		public SectionGap(int sectionPageNumber, int address, int sectionSize, int parent, int left, int right) {
			super(sectionPageNumber, address, sectionSize);
			this.parent = parent;
			this.left = left;
			this.right = right;
		}

	}

}
