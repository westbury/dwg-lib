/*
 * Copyright (c) 2017, 1Spatial Group Ltd.
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

package com.onespatial.dwglib.bitstreams;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.onespatial.dwglib.Issues;
import com.onespatial.dwglib.writer.BitWriter;

public class BitWriters {

    private final ByteBuffer byteBuffer;

    /**
     * Combines the three streams for a CAD object to produce a single byte
     * array for the object.
     *
     * @param dataStream
     * @param stringStream
     * @param handleStream
     * @param issues
     */
    public BitWriters(BitWriter dataStream, BitWriter stringStream, BitWriter handleStream, Issues issues) {

        // Determine bit size of the string stream length (which appears at the
        // end of the string stream)
        int stringStreamSize = stringStream.getBitSize();
        int bitSizeOfStringLength;
        if (stringStreamSize == 0) {
            bitSizeOfStringLength = 1;
        } else if (stringStreamSize <= 0x7FFF) {
            bitSizeOfStringLength = 17;
        } else {
            bitSizeOfStringLength = 33;
        }

        int bitLength = dataStream.getBitSize() + stringStream.getBitSize() + bitSizeOfStringLength
                + handleStream.getBitSize();

        /*
         * Calculate how many pad bits we need to append to get to a byte
         * boundary. These pad bits are considered a part of the handle stream,
         * i.e. they are included in the handle stream bit length.
         */
        int padBits = 7 - (bitLength + 7) % 8;

        int bitLengthWithPadding = bitLength + padBits;
        assert bitLengthWithPadding % 8 == 0;
        int byteLength = bitLengthWithPadding / 8;

        int bitSizeOfPaddedHandleStream = handleStream.getBitSize() + padBits;

        /*
         * We've now calculated all the sizes so we can now start writing the
         * data to the output byte array.
         */
        byte[] byteArray = new byte[byteLength + 20]; // Allow for the two
        byteBuffer = ByteBuffer.wrap(byteArray);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

        putMS(byteBuffer, byteLength);
        putUnsignedMC(byteBuffer, bitSizeOfPaddedHandleStream);

        BitWriter writer = new BitWriter(issues);
        dataStream.copyInto(writer);
        stringStream.copyInto(writer);

        switch (bitSizeOfStringLength) {
        case 1:
            writer.putB(false);
            break;
        case 17:
            assert (stringStreamSize & 0x8000) == 0;
            writer.putRS(stringStreamSize);
            writer.putB(true);
            break;
        case 33:
            writer.putRS(stringStreamSize >> 15);
            writer.putRS(0x8000 | stringStreamSize & 0x7FFF);
            writer.putB(true);
            break;
        default:
            assert false;
        }

        handleStream.copyInto(writer);

        for (int i = 0; i < padBits; i++) {
            writer.putB(false);
        }

        // Now copy bit writer into byte buffer
        Byte[] bitsAsByteArray = writer.getByteArray();
        assert writer.getBitSize() % 8 == 0;
        assert writer.getBitSize() / 8 == bitsAsByteArray.length;
        for (Byte element : bitsAsByteArray) {
            byteBuffer.put(element);
        }
    }

    public byte[] getByteArray() {
        return byteBuffer.array();
    }

    public static void putUnsignedMC(ByteBuffer buffer, int value) {
        int byteValue = value & 0x7F;
        int remainingValue = value >> 7;
        while (remainingValue != 0) {
            buffer.put((byte) (byteValue | 0x80));
            byteValue = remainingValue & 0x7F;
            remainingValue >>= 7;
        }
        buffer.put((byte) byteValue);
    }

    public static void putMS(ByteBuffer buffer, int value) {
        assert buffer.order() == ByteOrder.LITTLE_ENDIAN;

        int wordValue = value & 0x7FFF;
        int remainingValue = value >> 15;
        while (remainingValue != 0) {
            buffer.putShort((short) (wordValue | 0x8000));
            wordValue = remainingValue & 0x7FFF;
            remainingValue >>= 15;
        }
        buffer.putShort((short) wordValue);
    }

}
