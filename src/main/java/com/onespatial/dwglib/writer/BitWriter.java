package com.onespatial.dwglib.writer;
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

import java.util.ArrayList;
import java.util.List;

import com.onespatial.dwglib.Issues;
import com.onespatial.dwglib.bitstreams.Extrusion;
import com.onespatial.dwglib.bitstreams.Handle;
import com.onespatial.dwglib.bitstreams.Point3D;

public class BitWriter {

    private List<Byte> byteArray = new ArrayList<>();

    private Issues issues;

    private int bitOffset;

    private int currentByte;

    public BitWriter(Issues issues) {
        this.issues = issues;
    }

    public void putBS(int value) {
        if (value == 0) {
            putBitsUnsigned(2, 2);
        } else if (value == 256) {
            putBitsUnsigned(3, 2);
        } else if (value < 256) {
            putBitsUnsigned(1, 2);
            putBitsUnsigned(value, 8);
        } else {
            putBitsUnsigned(0, 2);
            putRS(value);
        }
    }

    public void putBL(int value) {
        if (value == 0) {
            putBitsUnsigned(2, 2);
        } else if (value < 256) {
            putBitsUnsigned(1, 2);
            putBitsUnsigned(value, 8);
        } else {
            putBitsUnsigned(0, 2);
            putRL(value);
        }
    }

    public void putBD(double value) {
        if (value == 0.0) {
            putBitsUnsigned(2, 2);
        } else if (value == 1.0) {
            putBitsUnsigned(1, 2);
        } else {
            putBitsUnsigned(0, 2);
            putRD(value);
        }
    }

    private void putRD(double value) {
        long longBits = Double.doubleToRawLongBits(value);

        int byte0 = (int) (longBits & 0xFF);
        int byte1 = (int) (longBits >> 8 & 0xFF);
        int byte2 = (int) (longBits >> 16 & 0xFF);
        int byte3 = (int) (longBits >> 24 & 0xFF);
        int byte4 = (int) (longBits >> 32 & 0xFF);
        int byte5 = (int) (longBits >> 40 & 0xFF);
        int byte6 = (int) (longBits >> 48 & 0xFF);
        int byte7 = (int) (longBits >> 56 & 0xFF);

        putBitsUnsigned(byte0, 8);
        putBitsUnsigned(byte1, 8);
        putBitsUnsigned(byte2, 8);
        putBitsUnsigned(byte3, 8);
        putBitsUnsigned(byte4, 8);
        putBitsUnsigned(byte5, 8);
        putBitsUnsigned(byte6, 8);
        putBitsUnsigned(byte7, 8);
    }

    private void putBitsUnsigned(int value, int numberOfBits) {
        assert numberOfBits <= 31;
        for (int i = 0; i < numberOfBits; i++) {
            int shifted = value >> numberOfBits - i - 1;
        putB((shifted & 0x1) != 0);
        }
    }

    public void putB(boolean value) {
        int mask = 0x80 >> bitOffset;
        if (value) {
            currentByte |= mask;
        }

        if (bitOffset == 7) {
            byteArray.add((byte) currentByte);
            currentByte = 0;
            bitOffset = 0;
        } else {
            bitOffset++;
        }

    }

    public void putRS(int value) {
        putBitsUnsigned(value, 8);
        if (value < 0) {
            putB(true);
            putBitsUnsigned(value >> 8, 7);
        } else {
            putB(false);
            putBitsUnsigned(value >> 8, 7);
        }

    }

    public void putRL(int value) {
        putBitsUnsigned(value, 8);
        putBitsUnsigned(value >> 8, 8);
        putBitsUnsigned(value >> 16, 8);
        if (value < 0) {
            putB(true);
            putBitsUnsigned(value >> 24, 7);
        } else {
            putB(false);
            putBitsUnsigned(value >> 24, 7);
        }

    }

    public void put3BD(Point3D point) {
        putBD(point.x);
        putBD(point.y);
        putBD(point.z);
    }

    public void putBT(double thickness) {
        boolean thicknessBit = thickness == 0.0;
        putB(thicknessBit);
        if (!thicknessBit) {
            putBD(thickness);
        }
    }

    public void putBE(Extrusion extrusion) {
        putB(extrusion.extrusionBit);
        if (!extrusion.extrusionBit) {
            put3BD(extrusion.point);
        }
    }

    /**
     * Paragraph 2.12 Object Type, page 13
     */
    public void putOT(int objectType) {
        if (objectType <= 255) {
            putBitsUnsigned(0, 2);
            putBitsUnsigned(objectType, 8);
        } else if (objectType >= 0x01F0 && objectType < 0x02F0) {
            putBitsUnsigned(1, 2);
            putBitsUnsigned(objectType - 0x01F0, 8);
        } else {
            putBitsUnsigned(2, 2);
            putRS(objectType);
        }
    }

    public void putHandle(Handle handle) {
        int offsetRemainder = handle.offset;
        int[] bytes = new int[8];
        int numberOfBytes = 0;
        do {
            bytes[numberOfBytes++] = offsetRemainder & 0xFF;
            offsetRemainder >>= 8;
        } while (offsetRemainder != 0);

        putBitsUnsigned(handle.code, 4);
        putBitsUnsigned(numberOfBytes, 4);
        for (int j = numberOfBytes - 1; j >= 0; j--) {
            putBitsUnsigned(bytes[j], 8);
        }
    }

    public int getBitSize() {
        return byteArray.size() * 8 + bitOffset;
    }

    public void copyInto(BitWriter writer) {
        for (byte byteValue : byteArray) {
            writer.putBitsUnsigned(byteValue, 8);
        }
        for (int eachBitOffset = 0; eachBitOffset < bitOffset; eachBitOffset++) {
            writer.putB((currentByte & 0x80 >> eachBitOffset) != 0);
        }
    }

    public Byte[] getByteArray() {
        assert bitOffset == 0;
        return byteArray.toArray(new Byte[0]);
    }

    @Override
    public String toString() {
        return "at " + getBitSize();
    }

}
