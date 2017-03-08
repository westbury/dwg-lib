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

package com.onespatial.dwglib;

import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.writer.BitWriter;

public class Streamable {

    protected int dataStreamStart;

    protected int stringStreamStart;

    protected int handleStreamStart;

    protected int dataStreamEnd;

    protected int stringStreamEnd;

    protected int handleStreamEnd;

    protected boolean isDirty = false;

    /**
     * This is the default implementation for streamables that are not
     * updatable. If any part of the data covered by a streamable object can be
     * updated then this method must be overridden.
     *
     * @param byteArray
     * @param dataStream
     * @param stringStream
     * @param handleStream
     * @param issues
     */
    public void write(byte[] byteArray, BitWriter dataStream, BitWriter stringStream, BitWriter handleStream,
            Issues issues) {
        BitBuffer originalStream = BitBuffer.wrap(byteArray, issues);

        originalStream.position(dataStreamStart);
        for (int position = dataStreamStart; position < dataStreamEnd; position++) {
            dataStream.putB(originalStream.getB());
        }

        originalStream.position(stringStreamStart);
        for (int position = stringStreamStart; position < stringStreamEnd; position++) {
            stringStream.putB(originalStream.getB());
        }

        originalStream.position(handleStreamStart);
        for (int position = handleStreamStart; position < handleStreamEnd; position++) {
            handleStream.putB(originalStream.getB());
        }
    }

}
