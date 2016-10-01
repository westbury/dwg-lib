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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;


/**
 * Reads a DWG format file.
 *  
 * @author Nigel Westbury
 */
public class Reader {

	private Issues issues = new Issues();
	
	// The following fields are extracted from the first 128 bytes of the file.
	
	private String fileVersionAsString;
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

	public Reader(File inputFile) throws IOException {
		try(FileInputStream inputStream = new FileInputStream(inputFile)) {
			FileChannel channel = inputStream.getChannel();
			ByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());

			buffer.order(ByteOrder.LITTLE_ENDIAN);

			byte [] versionAsByteArray = new byte[6];
			buffer.get(versionAsByteArray);
			fileVersionAsString = new String(versionAsByteArray);

			switch (fileVersionAsString) {
			case "AC1021":
				throw new UnsupportedFileVersionException("Release 21 (2007) files are not supported.  Only release 22 (2010) and later are supported.");
			}
			
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

			readSystemSectionPage(buffer, sectionPageMapAddress);
			
		}
	}

	/**
	 * Section 4.3 System section page
	 */
	private void readSystemSectionPage(ByteBuffer buffer, long sectionPageMapAddress) {
		if (0x100 + sectionPageMapAddress > Integer.MAX_VALUE) {
			throw new RuntimeException("sectionPageMapAddress is too big for us.");
		}
		buffer.position(0x100 + (int)sectionPageMapAddress);

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
		
		switch (pageType) {
		case 0x41630E3B:
			break;
			default:
				throw new RuntimeException();
		}
		
		byte [] expandedData = new Expander(compressedData, decompressedSize).result;
		
		// 4.4 2004 Section page map

		ByteBuffer expandedBuffer = ByteBuffer.wrap(expandedData);
		
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
		} while (expandedBuffer.position() != expandedData.length);

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
		return fileVersionAsString;
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
