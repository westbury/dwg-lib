import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;


public class Reader {

	private String fileVersionAsString;
	private Issues issues = new Issues();
	private int summaryInfoAddress;
	private int vbaProjectAddress;
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

	public Reader(File inputFile) throws IOException {
		try(FileInputStream inputStream = new FileInputStream(inputFile)) {
			FileChannel channel = inputStream.getChannel();
			ByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());

			buffer.order(ByteOrder.LITTLE_ENDIAN);

			byte [] versionAsByteArray = new byte[6];
			buffer.get(versionAsByteArray);
			fileVersionAsString = new String(versionAsByteArray);

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
