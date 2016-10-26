import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.junit.BeforeClass;
import org.junit.Test;

import bitstreams.BitStreams;

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
		assertEquals("2010", reader.getVersion());
		
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

	@Test
	public void testGetMC1() throws Exception {
		byte[] testArray = new byte[] { (byte)0b10000010, 0b00100100 };
		ByteBuffer testBuffer = ByteBuffer.wrap(testArray);
		int result = BitStreams.getMC(testBuffer);
		assertEquals(4610, result);
	}

	@Test
	public void testGetMC2() throws Exception {
		byte[] testArray = new byte[] { (byte)0b11101001, (byte)0b10010111, (byte)0b11100110, 0b00110101 };
		ByteBuffer testBuffer = ByteBuffer.wrap(testArray);
		int result = BitStreams.getMC(testBuffer);
		assertEquals(112823273, result);
	}

	@Test
	public void testGetNegativeMC() throws Exception {
		byte[] testArray = new byte[] { (byte)0b10000101, 0b01001011 };
		ByteBuffer testBuffer = ByteBuffer.wrap(testArray);
		int result = BitStreams.getMC(testBuffer);
		assertEquals(-1413, result);  
	}

	@Test
	public void testGetMS() throws Exception {
		byte[] testArray = new byte[] { (byte)0b00110001, (byte)0b11110100, (byte)0b10001101, 0b00000000 };
		ByteBuffer testBuffer = ByteBuffer.wrap(testArray);
		testBuffer.order(ByteOrder.LITTLE_ENDIAN);
		int result = BitStreams.getMS(testBuffer);
		assertEquals(4650033, result);
	}

}
