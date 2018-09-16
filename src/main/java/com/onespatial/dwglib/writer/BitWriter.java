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
import com.onespatial.dwglib.bitstreams.Point3D;

public class BitWriter {

    private List<Byte> byteArray = new ArrayList<>();

    private Issues issues;

    private int currentOffset;

    private int bitOffset;

    private int currentByte;

    public BitWriter(Issues issues) {
        this.issues = issues;
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

    public void putBE(Point3D extrusion) {
        boolean extrusionBit = extrusion.x == 0.0 && extrusion.y == 0.0 && extrusion.z == 1.0;
        putB(extrusionBit);
        if (!extrusionBit) {
            put3BD(extrusion);
        }

    }

}
