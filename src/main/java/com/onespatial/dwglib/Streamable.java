/*
 * Copyright (c) 1Spatial Group Ltd.
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
