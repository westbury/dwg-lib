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

/**
 * Uncompresses a byte array that has been compressed using the LZ77 compression
 * algorithm used by parts of the DWG format.
 *  
 * @author Nigel Westbury
 */
public class Expander {

	byte[] compressedData;

	byte[] result;

	private int inputPosition = 0;

	private int outputPosition = 0;

	public Expander(byte[] compressedData, int uncompressedSize) {
		this.compressedData = compressedData;
		result = expand(uncompressedSize);
	}

	private byte[] expand(int uncompressedSize) {
		byte[] outputBytes = new byte[uncompressedSize];

		int initialLitCount = getLitLength();
		for (int i = 0; i < initialLitCount; i++) {
			outputBytes[outputPosition++] = compressedData[inputPosition++];
		}

		int opcode1;

		while ((opcode1 = getUnsignedByte()) != 0x11) {

			if (opcode1 < 0x10) {
				throw new RuntimeException("bad data???");
			}

			int compressedBytes;
			int compOffset;
			int litCount;

			if (opcode1 == 0x10) {
				compressedBytes = getLongCompressionOffset() + 9;
				TwoByteOffset twoByteOffset = getTwoByteOffset();
				compOffset = twoByteOffset.reducedCompOffset + 0x3FFF;
				litCount = twoByteOffset.litCount;
			} else if (opcode1 <= 0x1F) {
				assert opcode1 >= 0x12 && opcode1 <= 0x1F;
				compressedBytes = (opcode1 & 0x0F) + 2;
				TwoByteOffset twoByteOffset = getTwoByteOffset();
				compOffset = twoByteOffset.reducedCompOffset + 0x3FFF;
				litCount = twoByteOffset.litCount;
			} else if (opcode1 == 0x20) {
				compressedBytes = getLongCompressionOffset() + 0x21;
				TwoByteOffset twoByteOffset = getTwoByteOffset();
				compOffset = twoByteOffset.reducedCompOffset;
				litCount = twoByteOffset.litCount;
			} else if (opcode1 <= 0x3F) {
				assert opcode1 >= 0x21 && opcode1 <= 0x3F;
				compressedBytes = opcode1 - 0x1E;
				TwoByteOffset twoByteOffset = getTwoByteOffset();
				compOffset = twoByteOffset.reducedCompOffset;
				litCount = twoByteOffset.litCount;
			} else {
				assert opcode1 >= 0x40;
				compressedBytes = ((opcode1 & 0xF0) >> 4) - 1;
				int opcode2 = getUnsignedByte();
				compOffset =  (opcode2 << 2) | ((opcode1 & 0x0C) >> 2);
				litCount = opcode1 & 0x03;
				// If last two bits are 0x00 then we must read a full litCount
				if (litCount == 0) {
					litCount = getLitLength();
				}
			}

			// Copy the duplicated bytes from previous part of output buffer.
			// Note that the length could exceed the back-offset, which means we
			// will be duplicating the previous bytes more than once.
			int duplicateFromPosition = outputPosition - compOffset - 1;
			for (int i = 0; i < compressedBytes; i++) {
				outputBytes[outputPosition++] = outputBytes[duplicateFromPosition++];
			}

			// Copy the literal bytes from the input buffer.  Note that litCount may
			// be zero, but this is not really a special case.
			for (int i = 0; i < litCount; i++) {
				outputBytes[outputPosition++] = compressedData[inputPosition++];
			}
		}

		return outputBytes;
	}

	private int getLitLength() {
		int opcode = peekUnsignedByte();

		// There may be no literal data between compressed opcodes.  That is
		// treated the same as zero length literal data.
		if (opcode > 0x0F) {
			return 0;
		}

		getUnsignedByte();

		int runningTotal = 0;
		if (opcode == 0x00) {
			runningTotal += 0x0F;
			while ((opcode = getUnsignedByte()) == 0x00) {
				runningTotal += 0xFF;
			}
		}

		return runningTotal + opcode + 3;
	}

	private int getLongCompressionOffset() {
		int runningTotal = 0;
		int opcode;
		while ((opcode = getUnsignedByte()) == 0x00) {
			runningTotal += 0xFF;
		}
		return runningTotal + opcode;
	}

	private TwoByteOffset getTwoByteOffset() {
		int firstByte = getUnsignedByte();
		int secondByte = getUnsignedByte();
		int reducedCompOffset = (firstByte >> 2) | (secondByte << 6);
		int litCount = (firstByte & 0x03);
		// If the litCount part is 0b00 then we must read a full litCount
		if (litCount == 0) {
			litCount = getLitLength();
		}

		return new TwoByteOffset(reducedCompOffset, litCount);
	}

	private int getUnsignedByte() {
		return Byte.toUnsignedInt(compressedData[inputPosition++]);
	}

	private int peekUnsignedByte() {
		return Byte.toUnsignedInt(compressedData[inputPosition]);
	}
	
	private class TwoByteOffset {
		public final int reducedCompOffset;
		public final int litCount;

		public TwoByteOffset(int reducedCompOffset, int litCount) {
			this.reducedCompOffset = reducedCompOffset;
			this.litCount = litCount;
		}
	}

}