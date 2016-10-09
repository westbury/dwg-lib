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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class BitStreams {

	private byte[] byteArray;
	private int endDataPosition;
	private int stringStreamEnd;
	private int stringStreamStart;

	public BitStreams(byte[] byteArray, byte[] signature) {
		this.byteArray = byteArray;

		ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
		byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

		// The signature
		byte [] actualSignature = new byte[16];
		byteBuffer.get(actualSignature);
		if (!Arrays.equals(actualSignature, signature)) {
			throw new RuntimeException("bad signature: ");
		}

		int sizeOfDataArea = byteBuffer.getInt();

		int unknown75 = byteBuffer.getInt();
		int totalSizeInBits = byteBuffer.getInt();

		/*
		 * Find the string section. We do this by reading the buffer
		 * backwards from the end. The size of the string data area is
		 * stored as either a 15 bit number or a 31 bit number at the
		 * end of the buffer. Once we have the size, move back from
		 * there to get the start of the string data area.
		 */

		BitBuffer bitBuffer = BitBuffer.wrap(byteArray);

		endDataPosition = 24*8 + totalSizeInBits;

		/*
		 * The last bit indicates if there is a string stream.
		 * All versions 2007+ have a string stream, and we don't support
		 * prior versions, so this bit should always be set.
		 */
		int position = endDataPosition;
		position -= 1;
		bitBuffer.position(position);
		boolean endBit = bitBuffer.getB();
		assert endBit;

		position -= 16;
		bitBuffer.position(position);
		int strDataSize = bitBuffer.getRS();
		if ((strDataSize & 0x8000) != 0) {
			position -= 16;
			bitBuffer.position(position);
			int hiSize = bitBuffer.getRS();
			strDataSize = (strDataSize & 0x7FFF) | (hiSize << 15);
		}

		stringStreamEnd = position;
		stringStreamStart = position - strDataSize;
	}

	public BitBuffer getDataStream() {
		BitBuffer dataStream = BitBuffer.wrap(byteArray);
		dataStream.position(28*8);
		dataStream.setEndOffset(stringStreamStart);
		return dataStream;
	}

	public BitBuffer getStringStream() {
		BitBuffer stringStream = BitBuffer.wrap(byteArray);
		stringStream.position(stringStreamStart);
		stringStream.setEndOffset(stringStreamEnd);
		return stringStream;
	}

	public BitBuffer getHandleStream() {
		BitBuffer handleStream = BitBuffer.wrap(byteArray);
		handleStream.position(endDataPosition);
		return handleStream;
	}

}
