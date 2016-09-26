import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


public class Reader {

	private String fileVersionAsString;

	public Reader(File inputFile) throws IOException {
		try(FileInputStream inputStream = new FileInputStream(inputFile)) {
			FileChannel channel = inputStream.getChannel();
			ByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());

			byte [] versionAsByteArray = new byte[6];
			buffer.get(versionAsByteArray);
			fileVersionAsString = new String(versionAsByteArray);
		}
	}

	public String getVersion() {
		return fileVersionAsString;
	}
}
