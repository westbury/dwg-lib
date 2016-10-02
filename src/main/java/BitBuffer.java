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

import java.io.UnsupportedEncodingException;
import java.util.BitSet;

/**
 * Reads from a byte array on a bit-by-bit basis.  Methods are being implemented here to
 * read fields as per the formats described in chapter 2, Bit Codes and Data Definitions,
 * of the file format specification.
 * 
 * @author Nigel Westbury
 *
 */
public class BitBuffer
{
    private BitSet bitSet = new BitSet();

    private int offset = 0;

	private int endOffset;

    private BitBuffer(byte[] byteArray)
    {
        this.bitSet = BitSet.valueOf(byteArray);

        for (int i = 0; i < byteArray.length; i++) {
            for (int j = 0; j < 4; j++) {
                int x = i*8 + j;
                int y = i*8 + 7 - j;
                boolean temp = bitSet.get(x);
                bitSet.set(x, bitSet.get(y));
                bitSet.set(y, temp);
            }
        }
        
        // TODO: must set actual last bit here
        this.endOffset = byteArray.length * 8;
    }

    public static BitBuffer wrap(byte[] byteArray)
    {
        return new BitBuffer(byteArray);
    }

    public void position(int offset)
    {
        this.offset = offset;
    }

    // TODO remove this method and set in constructor
    public void setEndOffset(int endOffset) {
    	this.endOffset = endOffset;
    }
    
    /**
     * bit (1 or 0)
     * 
     * @return
     */
    public boolean getB()
    {
        return bitSet.get(offset++);
    }

    /**
     * raw short (not compressed)
     * @return
     */
    public int getRS()
    {
        int a = getBitsUnsigned(8);
        boolean isNegative = getB();
        int b = getBitsUnsigned(7);

        int value = a + (b << 8);
        return isNegative ? -value : value;
    }

    /**
     * raw long (not compressed)
     * @return
     */
    public int getRL()
    {
        int a = getBitsUnsigned(8);
        int b = getBitsUnsigned(8);
        int c = getBitsUnsigned(8);
        boolean isNegative = getB();
        int d = getBitsUnsigned(7);

        int value = a + (b << 8) + (c << 16) + (d << 24);
        return isNegative ? -value : value;
    }

    /**
     *   bitshort 16
     *   See 2.2 Bitshort
     * 
     * @return
     */
    public int getBS()
    {
        int code = getBitsUnsigned(2);
        switch (code) {
        case 0:
            return getRS();
        case 1:
            return getBitsUnsigned(8);
        case 2:
            return 0;
        case 3:
            return 256;
        default:
            throw new RuntimeException("can't happen");
        }
    }

    /**
     *   bitlong 32
     *   See 2.3 Bitlong
     * 
     * @return
     */
    public int getBL()
    {
        int code = getBitsUnsigned(2);
        switch (code) {
        case 0:
            return getRL();
        case 1:
            return getBitsUnsigned(8);
        case 2:
            return 0;
        case 3:
            throw new RuntimeException("unknown bitlong code of 0b11");
        default:
            throw new RuntimeException("can't happen");
        }
    }

    private int getBitsUnsigned(int numberOfBits)
    {
        assert numberOfBits <= 31;
        int result = 0;
        for (int i = 0; i < numberOfBits; i++) {
            result <<= 1;
            if (this.getB()) {
                result += 1;
            }
        }
        return result;
    }

    private int getBitsSigned(int numberOfBits)
    {
        assert numberOfBits <= 32;
        boolean isNegative = this.getB();
        int result = 0;
        for (int i = 0; i < numberOfBits-1; i++) {
            result <<= 1;
            if (this.getB()) {
                result += 1;
            }
        }
        return isNegative ? -result : result;
    }

    /**
     * Unicode text
     * 
     * @return
     */
    public String getTU()
    {
        int length = getBS();

        byte[] x = new byte[length*2];
        for (int i = 0; i < length; i++) {
            x[2*i+1] = (byte)this.getBitsUnsigned(8);
            x[2*i] = (byte)this.getBitsUnsigned(8);
        }
        try
        {
            return new String(x, "UTF-16");
        } catch (UnsupportedEncodingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }  // TODO
    }

    public boolean hasMoreData()
    {
        return offset < endOffset;
    }

}
