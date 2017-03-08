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

import java.util.ArrayList;
import java.util.List;

/**
 * Compresses a byte array using the LZ77 compression algorithm.
 *
 * <table summary="" border="1">
 * <tr>
 * <td>x10</td>
 * <td>>= 9</td>
 * <td>>= x3FFF</td>
 * <tr>
 * </tr>
 * <td>x12 to x1F</td>
 * <td>4 to 17</td>
 * <td>>= x3FFF</td>
 * </tr>
 * </tr>
 * <td>x20</td>
 * <td>>= 33</td>
 * <td><= x3FFF</td>
 * </tr>
 * </tr>
 * <td>x21 to x3F</td>
 * <td>3 to 33</td>
 * <td><= x3FFF</td>
 * </tr>
 * </tr>
 * <td>x40 to xFF</td>
 * <td>3 to 14</td>
 * <td><= x03FF</td>
 * </tr>
 * </table>
 *
 * @author Nigel Westbury
 */
public class Compressor {

    private interface CompressionStyle {
        void pushCompressionAndLitLength(int compressedBytes, int compOffset, int litCount);
    }

    private class Code10 implements CompressionStyle {
        @Override
        public void pushCompressionAndLitLength(int compressedBytes, int compOffset, int litCount) {
            assert compressedBytes >= 9;
            pushUnsignedByte(0x10);
            pushLongCompressionOffset(compressedBytes - 9);
            pushTwoByteOffset(compOffset - 0x3FFF, litCount);
        }
    }

    private class Code12to1F implements CompressionStyle {
        @Override
        public void pushCompressionAndLitLength(int compressedBytes, int compOffset, int litCount) {
            assert compressedBytes >= 4 && compressedBytes <= 17;
            pushUnsignedByte(compressedBytes + 14);
            pushTwoByteOffset(compOffset - 0x3FFF, litCount);
        }
    }

    private class Code20 implements CompressionStyle {
        @Override
        public void pushCompressionAndLitLength(int compressedBytes, int compOffset, int litCount) {
            assert compressedBytes >= 33;
            pushUnsignedByte(0x20);
            pushLongCompressionOffset(compressedBytes - 33);
            pushTwoByteOffset(compOffset, litCount);
        }
    }

    private class Code21to3F implements CompressionStyle {
        @Override
        public void pushCompressionAndLitLength(int compressedBytes, int compOffset, int litCount) {
            assert compressedBytes >= 3 && compressedBytes <= 33;
            pushUnsignedByte(compressedBytes + 30);
            pushTwoByteOffset(compOffset, litCount);
        }
    }

    private class Code40toFF implements CompressionStyle {
        @Override
        public void pushCompressionAndLitLength(int compressedBytes, int compOffset, int litCount) {
            assert compressedBytes >= 3 && compressedBytes <= 14;
            assert compOffset <= 0x03FF;

            int litCountTwoBits;
            if (litCount <= 3) {
                litCountTwoBits = litCount;
            } else {
                litCountTwoBits = 0;
            }

            int opcode1 = compressedBytes + 1 << 4 | (compOffset & 0x03) << 2 | litCountTwoBits;
            pushUnsignedByte(opcode1);

            int opcode2 = compOffset >> 2;
            pushUnsignedByte(opcode2);

            if (litCountTwoBits == 0) {
                pushLitLength(litCount);
            }
        }
    };



    byte[] expandedData;

    byte[] result;

    private int inputPosition = 0;

    private final List<Byte> outputBytes = new ArrayList<>();

    public Compressor(byte[] expandedData) {
        this.expandedData = expandedData;
        result = compress();
    }

    private byte[] compress() {
        // We should be compressing, so doubling the size is safe???
        // int compressedSize = expandedData.length * 2;

        List<Byte> literal = new ArrayList<>();

        // 4 is the minimum literal length
        int position = 4;
        for (int i = 0; i < position; i++) {
            literal.add(expandedData[i]);
        }

        // These 3 go together. Probably needs cleaning up.
        int compressedBytes = -1;
        int compOffset = -1;
        CompressionStyle style = null;

        do {
            if (position > 388) {
                System.out.println("");
            }
            // Brute force method.  Simply look for the longest match.
            int bestMatchLength = 0;
            int bestStart = -1;

            for (int start = 0; start < position; start++) {
                int matchLength = countMatches(start, position);
                if (matchLength >= bestMatchLength) {
                    bestMatchLength = matchLength;
                    bestStart = start;
                }
            }

            /*
             * We're only interested in matching lengths of at least 4. If the
             * offset is 0x3FFF or less then the encodings allow us to go down
             * to 3. However it is unnecessarily complex to distinguish.
             */
            if (bestMatchLength < 4) {
                literal.add(expandedData[position]);
                position++;
            } else {
                if (style == null) {
                    pushLitLength(literal.size());
                } else {
                    assert compressedBytes != -1;
                    assert compOffset != -1;
                    style.pushCompressionAndLitLength(compressedBytes, compOffset, literal.size());
                }
                outputBytes.addAll(literal);
                literal.clear();

                assert bestStart != -1;

                // Determine which compression code to use
                compressedBytes = bestMatchLength;
                compOffset = position - bestStart - 1;

                if (compOffset <= 0x3FFF) {
                    if (compOffset <= 0x03FF && compressedBytes <= 14) {
                        style = new Code40toFF();
                    } else if (compressedBytes <= 33) {
                        style = new Code21to3F();
                    } else {
                        style = new Code20();
                    }
                } else {
                    if (compressedBytes <= 17) {
                        style = new Code12to1F();
                    } else {
                        style = new Code10();
                    }
                }

                position += compressedBytes;
            }
        } while (position < expandedData.length);

        if (style == null) {
            pushLitLength(literal.size());
        } else {
            assert compressedBytes != -1;
            assert compOffset != -1;
            style.pushCompressionAndLitLength(compressedBytes, compOffset, literal.size());
        }
        outputBytes.addAll(literal);
        literal.clear();

        outputBytes.add((byte) 0x11);

        byte[] result = new byte[outputBytes.size()];
        int i = 0;
        for (Byte b : outputBytes) {
            result[i++] = b;
        }

        return result;
    }

    /**
     *
     * @param position1
     *            a position which must be before position2
     * @param position2
     *            a position which must be after position1
     * @return
     */
    private int countMatches(int position1, int position2) {
        assert position2 > position1;
        assert position1 >= 0;
        assert position2 <= expandedData.length;

        int count = 0;

        while (position2 + count != expandedData.length
                && expandedData[position1 + count] == expandedData[position2 + count]) {
            count++;
        }

        return count;
    }

    private void pushLitLength(int litLength) {
        /*
         * A zero literal length means nothing at all is written. Codes can be
         * distinguished from literal lengths so there is no ambiguity.
         */
        if (litLength > 0) {
            assert litLength >= 3;

            int remaining = litLength - 3;

            if (remaining <= 0x0F) {
                pushUnsignedByte(remaining);
            } else {
                pushUnsignedByte(0);
                remaining -= 15;

                while (remaining > 0xFF) {
                    pushUnsignedByte(0);
                    remaining -= 0xFF;
                }
                pushUnsignedByte(remaining);
            }
        }
    }

    private void pushLongCompressionOffset(int offset) {
        while (offset > 0xFF) {
            pushUnsignedByte(0);
            offset -= 0xFF;
        }
        pushUnsignedByte(offset);
    }

    private void pushTwoByteOffset(int reducedCompOffset, int litCount) {

        int litCountTwoBits;
        if (litCount <= 3) {
            litCountTwoBits = litCount;
        } else {
            litCountTwoBits = 0;
        }

        pushUnsignedByte((reducedCompOffset & 0x3F) << 2 | litCountTwoBits);
        pushUnsignedByte(reducedCompOffset >> 6);
        if (litCountTwoBits == 0) {
            pushLitLength(litCount);
        }
    }

    private void pushUnsignedByte(int value) {
        if (value > 255) {
            System.out.println("Here");
        }
        assert value <= 0xFF;
        if (value <= 0x7F) {
            outputBytes.add((byte) value);
        } else {
            outputBytes.add((byte) (value - 256));
        }
    }

}