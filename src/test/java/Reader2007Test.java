import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.junit.BeforeClass;
import org.junit.Test;

public class Reader2007Test {

	/**
	 * a sample file, available from Autodesk, in 2007 format
	 */
	static File testFile = new File("air_suspension.dwg");
	

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if (!testFile.exists()) {
			
			URL urlToTestFile = new URL("http://download.autodesk.com/us/support/files/autocadlt_2010_sample_files/air_suspension.dwg");
			try (
					ReadableByteChannel inputChannel = Channels.newChannel(urlToTestFile.openStream());
					FileOutputStream outputStream = new FileOutputStream(testFile);
			) {
				outputStream.getChannel().transferFrom(inputChannel, 0, Long.MAX_VALUE);
			}
		}
	}

	@Test(expected=UnsupportedFileVersionException.class)
	public void version() throws IOException {
	     new Reader(testFile);
	}
}
