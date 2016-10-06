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
import java.util.Arrays;
import java.util.List;


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

			byte [] theRest = new byte[0x14];
			buffer.get(theRest);

			readSystemSectionPage(buffer, sectionPageMapAddress, sectionMapId);

		}
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




        // Is this 4.5????

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

                // Was pageSize for last param below.
                byte [] expandedData = new Expander(compressedData, maxDecompressedSize).result;

                ByteBuffer headerBuffer = ByteBuffer.wrap(expandedData);
                headerBuffer.order(ByteOrder.LITTLE_ENDIAN);

                // 8 Data section AcDb:Header (HEADER VARIABLES), page 68

                // The signature
                byte [] signature = new byte[16];
                headerBuffer.get(signature);
                if (!Arrays.equals(signature, new byte [] { (byte)0xCF,0x7B,0x1F,0x23,(byte)0xFD,(byte)0xDE,0x38,(byte)0xA9,0x5F,0x7C,0x68,(byte)0xB8,0x4E,0x6D,0x33,0x5F })) {
                    throw new RuntimeException("bad signature: ");
                }

                BitBuffer bitClasses = BitBuffer.wrap(expandedData);

                bitClasses.position(16*8);

                Header header = new Header(bitClasses, fileVersion);

            } else if (sectionName.equals("AcDb:Objects")) {
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
                int pageSize = buffer.getInt() ^ secMask;  // classData.sectionSize
                int startOffset2 = buffer.getInt() ^ secMask;
                int pageHeaderChecksum = buffer.getInt() ^ secMask;
                int dataChecksum = buffer.getInt() ^ secMask;
                int unknown = buffer.getInt() ^ secMask;

                byte [] compressedData = new byte[dataSize2];
                buffer.get(compressedData);

                // Was pageSize for last param below.
                byte [] expandedData = new Expander(compressedData, maxDecompressedSize).result;

                ByteBuffer classesBuffer = ByteBuffer.wrap(expandedData);
                classesBuffer.order(ByteOrder.LITTLE_ENDIAN);

                // 5.8 AcDb:Classes Section

                // The signature
                byte [] sig6 = new byte[16];
                classesBuffer.get(sig6);
                if (!Arrays.equals(sig6, new byte [] { (byte)0x8D, (byte)0xA1, (byte)0xC4, (byte)0xB8, (byte)0xC4, (byte)0xA9, (byte)0xF8, (byte)0xC5, (byte)0xC0, (byte)0xDC, (byte)0xF4, (byte)0x5F, (byte)0xE7, (byte)0xCF, (byte)0xB6, (byte)0x8A})) {
                    throw new RuntimeException("bad signature: ");
                }

                BitBuffer bitClasses = BitBuffer.wrap(expandedData);

                bitClasses.position(16*8);

                int sizeOfClassDataArea = bitClasses.getRL();



                int unknown75 = bitClasses.getRL();
                int totalSizeInBits = bitClasses.getRL();

//                                int maximumClassNumber = bitClasses.getBL();
                int maximumClassNumber = bitClasses.getBL();
                boolean unknownBool = bitClasses.getB();

                // Here starts the class data (repeating)

                BitBuffer bitClassesStrings = BitBuffer.wrap(expandedData);

				/*
				 * Find the string section. We do this by reading the buffer
				 * backwards from the end. The size of the string data area is
				 * stored as either a 15 bit number or a 31 bit number at the
				 * end of the buffer. Once we have the size, move back from
				 * there to get the start of the string data area.
				 */

                /**
                 * totalSizeInBits does not include the signature and sizeOfClassDataArea at
                 * the start of the buffer, so add those.
                 */
                int endDataPosition = 24*8 + totalSizeInBits;

                /*
                 * The last bit indicates if there is a string stream.
                 * All versions 2007+ have a string stream, and we don't support
                 * prior versions, so this bit should always be set.
                 */
                endDataPosition -= 1;
                bitClassesStrings.position(endDataPosition);
                boolean endBit = bitClassesStrings.getB();
                assert endBit;

                endDataPosition -= 16;
                bitClassesStrings.position(endDataPosition);
                int strDataSize = bitClassesStrings.getRS();
                if ((strDataSize & 0x8000) != 0) {
                	endDataPosition -= 16;
                    bitClassesStrings.position(endDataPosition);
                    int hiSize = bitClassesStrings.getRS();
                    strDataSize = (strDataSize & 0x7FFF) | (hiSize << 15);
                }

                bitClassesStrings.setEndOffset(endDataPosition);

                endDataPosition -= strDataSize;
                bitClassesStrings.position(endDataPosition);

                bitClasses.setEndOffset(endDataPosition);

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
