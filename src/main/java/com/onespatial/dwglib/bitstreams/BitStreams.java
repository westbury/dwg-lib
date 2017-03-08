package com.onespatial.dwglib.bitstreams;
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

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.Issues;

public class BitStreams {

    private byte[] byteArray;
    private Issues issues;
    private int dataStreamStart;
    private int stringStreamStart;
    private int stringStreamEnd;
    private int endDataPosition;
    private int handleStreamEnd;

    public BitStreams(byte[] byteArray, byte[] signature, FileVersion fileVersion, Issues issues) {
        this.byteArray = byteArray;
        this.issues = issues;

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

        // Position in bit buffer at same place as where we got to
        // in the byte buffer.
        int position = byteBuffer.position();
        dataStreamStart = position * 8;

        endDataPosition = 24*8 + totalSizeInBits;

        /*
         * Find the string section. We do this by reading the buffer
         * backwards from the end. The size of the string data area is
         * stored as either a 15 bit number or a 31 bit number at the
         * end of the buffer. Once we have the size, move back from
         * there to get the start of the string data area.
         */

        BitBuffer bitBuffer = BitBuffer.wrap(byteArray, issues);
        identifyStringStream(bitBuffer);

        handleStreamEnd = byteArray.length * 8;
    }

    /**
     * Reads the streams inside a CAD object.
     *
     * @param objectBuffer
     * @param byteOffset
     * @param issues
     */
    public BitStreams(byte[] objectBuffer, int byteOffset, Issues issues) {
        byteArray = objectBuffer;
        this.issues = issues;

        ByteBuffer objectsBuffer = ByteBuffer.wrap(objectBuffer);
        objectsBuffer.order(ByteOrder.LITTLE_ENDIAN);
        objectsBuffer.position(byteOffset);

        int sizeOfObject = getMS(objectsBuffer);
        int bitSizeOfHandleStream = getUnsignedMC(objectsBuffer);

        dataStreamStart = objectsBuffer.position() * 8;

        int bitSizeOfObjectData = sizeOfObject * 8 - bitSizeOfHandleStream;

        endDataPosition = dataStreamStart + bitSizeOfObjectData;

        handleStreamEnd = endDataPosition + bitSizeOfHandleStream;


        /*
         * Find the string section. We do this by reading the buffer
         * backwards from the end. The size of the string data area is
         * stored as either a 15 bit number or a 31 bit number at the
         * end of the buffer. Once we have the size, move back from
         * there to get the start of the string data area.
         */

        BitBuffer bitBuffer = BitBuffer.wrap(byteArray, issues);
        identifyStringStream(bitBuffer);
    }

    /**
     * Given the position of the end of the combined data and string streams, being also
     * the start of the handle stream, we work backwards to identify the start and end of
     * the string stream.
     * <P>
     * <code>stringStreamStart</code> and <code>stringStreamEnd</code> are set by this method.
     *
     * @param bitBuffer
     */
    private void identifyStringStream(BitBuffer bitBuffer) {
        /*
         * The last bit indicates if there is a string stream.
         * All versions 2007+ have a string stream, and we don't support
         * prior versions, so this bit should always be set, except in the
         * objects in the objects section where it may not be set.
         */
        int position = endDataPosition;
        position -= 1;
        bitBuffer.position(position);
        boolean endBit = bitBuffer.getB();

        int strDataSize;

        if (endBit) {

            position -= 16;
            bitBuffer.position(position);
            strDataSize = bitBuffer.getRS();
            if ((strDataSize & 0x8000) != 0) {
                position -= 16;
                bitBuffer.position(position);
                int hiSize = bitBuffer.getRS();
                int loSize = -strDataSize;
                strDataSize = hiSize << 15 | loSize;
            }
        } else {
            strDataSize = 0;
        }
        stringStreamEnd = position;
        stringStreamStart = position - strDataSize;
    }

    public BitBuffer getDataStream() {
        BitBuffer dataStream = BitBuffer.wrap(byteArray, issues);
        dataStream.position(dataStreamStart);
        dataStream.setEndOffset(stringStreamStart);
        return dataStream;
    }

    public BitBuffer getStringStream() {
        BitBuffer stringStream = BitBuffer.wrap(byteArray, issues);
        stringStream.position(stringStreamStart);
        stringStream.setEndOffset(stringStreamEnd);
        return stringStream;
    }

    public BitBuffer getHandleStream() {
        BitBuffer handleStream = BitBuffer.wrap(byteArray, issues);
        handleStream.position(endDataPosition);
        handleStream.setEndOffset(handleStreamEnd);
        return handleStream;
    }

    public static int getMC(ByteBuffer buffer) {
        int result = 0;
        int shift = 0;

        byte b = buffer.get();
        while ((b & 0x80) != 0) {
            int byteValue = b & 0x7F;
            result |= byteValue << shift;
            shift += 7;
            b = buffer.get();
        }

        boolean signBit = (b & 0x40) != 0;
        int byteValue = b & 0x3F;
        result |= byteValue << shift;

        if (signBit) {
            result = -result;
        }

        return result;
    }

    public static int getUnsignedMC(ByteBuffer buffer) {
        int result = 0;
        int shift = 0;

        boolean highBit;
        do {
            byte b = buffer.get();
            highBit = (b & 0x80) != 0;
            int byteValue = b & 0x7F;
            result |= byteValue << shift;
            shift += 7;
        } while (highBit);

        return result;
    }

    public static int getMS(ByteBuffer buffer) {
        int result = 0;
        int shift = 0;

        assert buffer.order() == ByteOrder.LITTLE_ENDIAN;

        boolean highBit;
        do {
            short word = buffer.getShort();
            highBit = (word & 0x8000) != 0;
            int wordValue = word & 0x7FFF;
            result |= wordValue << shift;
            shift += 15;
        } while (highBit);

        return result;
    }

}
