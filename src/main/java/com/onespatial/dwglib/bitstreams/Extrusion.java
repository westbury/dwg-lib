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

/**
 * The extrusion is essentially just a Point3D. However the point (0.0, 0.0,
 * 1.0) may be stored in the compressed form with the extrusion bit set, or it
 * may be stored as three double values. Both have been seen to occur. If we
 * stored the extrusions in the model as a Point3D then we would not be able to
 * exactly round-trip. We need to store the extrusion bit to ensure exact
 * round-trips.
 */
public class Extrusion {

    public final boolean extrusionBit;

    public final Point3D point;

    public Extrusion(boolean extrusionBit, Point3D point) {
        this.extrusionBit = extrusionBit;
        this.point = point;
    }

}
