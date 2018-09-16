package com.onespatial.dwglib.objects;
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


import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.Issues;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.Point3D;
import com.onespatial.dwglib.writer.BitWriter;

public class Point extends EntityObject {

    private Point3D point;

    public double thickness;

    public Point3D extrusion;

    public double xAxisAngle;

    public Point(ObjectMap objectMap) {
        super(objectMap);
    }

    @Override
    public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {

        // 19.4.29 POINT (27) page 125

        point = dataStream.get3BD();
        thickness = dataStream.getBT();
        extrusion = dataStream.getBE();
        xAxisAngle = dataStream.getBD();

        handleStream.advanceToByteBoundary();

        dataStream.assertEndOfStream();
        stringStream.assertEndOfStream();
        handleStream.assertEndOfStream();
    }

    @Override
    protected void writeObjectTypeSpecificData(byte[] byteArray, BitWriter dataStream, BitWriter stringStream,
            BitWriter handleStream, Issues issues) {

        // 19.4.29 POINT (27) page 125

        dataStream.put3BD(point);
        dataStream.putBT(thickness);
        dataStream.putBE(extrusion);
        dataStream.putBD(xAxisAngle);
    }

    @Override
    public String toString() {
        return "POINT";
    }

    public Point3D getPoint() {
        return point;
    }

    public void setPoint(Point3D point) {
        this.point = point;

        if (!isDirty) {
            isDirty = true;
            objectMap.addToDirtyList(this);
        }
    }
}