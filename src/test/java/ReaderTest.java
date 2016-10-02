import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.junit.BeforeClass;
import org.junit.Test;

public class ReaderTest {

	/**
	 * a sample file, available from Autodesk, in 2010 format
	 */
	static File testFile = new File("visualization_-_aerial.dwg");
	

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if (!testFile.exists()) {
			
			URL urlToTestFile = new URL("http://download.autodesk.com/us/samplefiles/acad/visualization_-_aerial.dwg");
			try (
					ReadableByteChannel inputChannel = Channels.newChannel(urlToTestFile.openStream());
					FileOutputStream outputStream = new FileOutputStream(testFile);
			) {
				outputStream.getChannel().transferFrom(inputChannel, 0, Long.MAX_VALUE);
			}
		}
	}

	@Test
	public void version() throws IOException {
		Reader reader = new Reader(testFile);
		assertEquals("AC1024", reader.getVersion());
		
		assertEquals(47, reader.sections.size());
		assertEquals(1, reader.sections.get(0).sectionPageNumber);
		assertEquals(160, reader.sections.get(0).sectionSize);
		assertEquals(42, reader.sections.get(46).sectionPageNumber);
		assertEquals(3840, reader.sections.get(46).sectionSize);
		
		assertEquals(22, reader.classes.size());
		assertEquals(1, reader.classes.get(0).numberOfObjects);
		assertEquals("ObjectDBX Classes", reader.classes.get(0).appname);
		assertEquals(1, reader.classes.get(21).numberOfObjects);
		assertEquals("SCENEOE", reader.classes.get(21).appname);
		
	}
}
