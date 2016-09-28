import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;


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

			byte [] randomData = new byte [] { 0x29, 0x23, (byte)0xBE, (byte)0x84, (byte)0xE1, 0x6C, (byte)0xD6, (byte)0xAE, 0x52, (byte)0x90, 0x49, (byte)0xF1, (byte)0xF1, (byte)0xBB, (byte)0xE9, (byte)0xEB,
					(byte)0xB3, (byte)0xA6, (byte)0xDB, 0x3C, (byte)0x87, 0x0C, 0x3E, (byte)0x99, 0x24, 0x5E, 0x0D, 0x1C, 0x06, (byte)0xB7, 0x47, (byte)0xDE,
					(byte)0xB3, 0x12, 0x4D, (byte)0xC8, 0x43, (byte)0xBB, (byte)0x8B, (byte)0xA6, 0x1F, 0x03, 0x5A, 0x7D, 0x09, 0x38, 0x25, 0x1F,
					0x5D, (byte)0xD4, (byte)0xCB, (byte)0xFC, (byte)0x96, (byte)0xF5, 0x45, 0x3B, 0x13, 0x0D, (byte)0x89, 0x0A, 0x1C, (byte)0xDB, (byte)0xAE, 0x32,
					0x20, (byte)0x9A, 0x50, (byte)0xEE, 0x40, 0x78, 0x36, (byte)0xFD, 0x12, 0x49, 0x32, (byte)0xF6, (byte)0x9E, 0x7D, 0x49, (byte)0xDC,
					(byte)0xAD, 0x4F, 0x14, (byte)0xF2, 0x44, 0x40, 0x66, (byte)0xD0, 0x6B, (byte)0xC4, 0x30, (byte)0xB7 };


			buffer.position(128);
			byte [] decryptedData = new byte[108];
			buffer.get(decryptedData);
			for (int i = 0; i < 92; i++) {
				decryptedData[i] ^= randomData[i];
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

			System.out.println(theRest);
			
		}
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
}
