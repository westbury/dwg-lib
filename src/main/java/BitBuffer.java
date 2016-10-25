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
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.JulianFields;

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
    private byte[] byteArray;

    private int currentOffset;

    private int bitOffset;

    private int currentByte;

	private int endOffset;

    private BitBuffer(byte[] byteArray)
    {
        this.byteArray = byteArray;
        currentOffset = 0;
        currentByte = byteArray[0];
        bitOffset = 0;

        // TODO: must set actual last bit here
        this.endOffset = byteArray.length * 8;
    }

    public static BitBuffer wrap(byte[] byteArray)
    {
        return new BitBuffer(byteArray);
    }

    public void position(int offset)
    {
        this.currentOffset = offset / 8;
        currentByte = byteArray[currentOffset];
        this.bitOffset = (offset % 8);
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
    	if (!hasMoreData()) {
    		throw new RuntimeException("end of stream reached unexpectedly");
    	}
    	
    	int mask = 0x80 >> bitOffset;
        boolean result = ((currentByte & mask) != 0);
        if (bitOffset == 7) {
            currentByte = byteArray[++currentOffset];
            bitOffset = 0;
        } else {
            bitOffset++;
        }
        return result;
    }

    /**
     * raw char (not compressed)
     * @return
     */
    public int getRC()
    {
        boolean isNegative = getB();
        int value = getBitsUnsigned(7);

        return isNegative ? -value : value;
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

    /**
     *   bitlonglong 64
     *   See 2.4 BITLONGLONG
     *
     * @return
     */
    public long getBLL()
    {
        int length = get3B();

        long result = 0;
        int shift = 0;
        while (length-- != 0) {
            result |= (getBitsUnsigned(8) << shift);
            shift += 8;
        }

        return result;
    }

    /**
     *   bitdouble
     *   See 2.5 BITDOUBLE
     *
     * @return
     */
    public double getBD()
    {
        int code = getBitsUnsigned(2);
        switch (code) {
        case 0:
            return getRD();
        case 1:
            return 1.0;
        case 2:
            return 0.0;
        case 3:
            throw new RuntimeException("unknown bitdouble code of 0b11");
        default:
            throw new RuntimeException("can't happen");
        }
    }

    /**
     * raw double (not compressed)
     * @return
     */
    public double getRD()
    {
        long byte0 = getBitsUnsigned(8);
        long byte1 = getBitsUnsigned(8);
        long byte2 = getBitsUnsigned(8);
        long byte3 = getBitsUnsigned(8);
        long byte4 = getBitsUnsigned(8);
        long byte5 = getBitsUnsigned(8);
        long byte6 = getBitsUnsigned(8);
        long byte7 = getBitsUnsigned(8);

        return Double.longBitsToDouble((byte7 << 56)
                | (byte6 << 48)
                | (byte5 << 40)
                | (byte4 << 32)
                | (byte3 << 24)
                | (byte2 << 16)
                | (byte1 << 8)
                | (byte0));
    }

    /**
     * 3B - Paragraph 2.1
     *
     * @return
     */
    public int get3B()
    {
        if (getB()) {
            if (getB()) {
                if (getB()) {
                    return 7;
                } else {
                    return 6;
                }
            } else {
                return 2;
            }
        } else {
            return 0;
        }
    }

    /**
     * handle
     * see 2.13 Handle References
     *
     * @return
     */
    public Handle getHandle()
    {
        int code = getBitsUnsigned(4);
        int counter = getBitsUnsigned(4);

        int [] handle = new int[counter];
        for (int i = 0; i < counter; i++) {
            handle[i] = getBitsUnsigned(8);
        }

        return new Handle(code, handle);
    }

	public Handle getHandle(Handle baseHandle) {
        int code = getBitsUnsigned(4);
        int counter = getBitsUnsigned(4);

        int [] handle = new int[counter];
        for (int i = 0; i < counter; i++) {
            handle[i] = getBitsUnsigned(8);
        }

        Handle tempHandle = new Handle(code, handle);

        int thisOffset;
		switch (tempHandle.code) {
		case 2:
		case 3:
		case 4:
		case 5:
			thisOffset = tempHandle.offset;
			break;
		case 6:
			thisOffset = baseHandle.offset + 1;
			break;
		case 8:
			thisOffset = baseHandle.offset - 1;
			break;
		case 0xA:
			thisOffset = baseHandle.offset + tempHandle.offset;
			break;
		case 0xC:
			thisOffset = baseHandle.offset - tempHandle.offset;
			break;
		default:
			throw new RuntimeException("bad case");
		}

        return new Handle(5, thisOffset);
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
        return currentOffset*8 + bitOffset < endOffset;
    }

    public void B(Value<Boolean> value)
    {
        value.set(getB());
    }

    public void BS(Value<Integer> value)
    {
        value.set(getBS());
    }

    public void BL(Value<Integer> value)
    {
        value.set(getBL());
    }

    public void BD(Value<Double> value)
    {
        value.set(getBD());
    }

    public void RC(Value<Integer> value)
    {
        value.set(getRC());
    }

    public void expectBD(double expected)
    {
        double actual = getBD();
        if (actual != expected) {
            throw new RuntimeException();
        }
    }

	public void localDateTime(Value<LocalDateTime> localDateTime) {
		int julianDay = getBL();
		int millisecondsIntoDay = getBL();

		LocalDate datePart = LocalDate.MIN.with(JulianFields.JULIAN_DAY, julianDay);
		LocalTime timePart = LocalTime.ofNanoOfDay(1000000L * millisecondsIntoDay);
		LocalDateTime value = LocalDateTime.of(datePart,timePart);

		localDateTime.set(value);

		// This code writes back....
//		LocalDate dd = value.toLocalDate();
//		LocalTime tttt = value.toLocalTime();
//		long jj = dd.getLong(JulianFields.JULIAN_DAY);
//		
//		int mss = tttt.get(ChronoField.MILLI_OF_DAY);
		
	}

	public void duration(Value<Duration> duration) {
		int days = getBL();
		int millisecondsIntoDay = getBL();

		Duration wholeDaysPart = Duration.ofDays(days);
		Duration partOfDayPart = Duration.ofMillis(millisecondsIntoDay);
		Duration value = wholeDaysPart.plus(partOfDayPart);
		
		duration.set(value);
	}

	public void CMC(Value<CmColor> color) {
		int expectZero = getBS();
		int rgbValue = getBL();
		int colorByte = getRC();
		if ((colorByte & 0x01) != 0) {
			// Read color name
		}
		if ((colorByte & 0x02) != 0) {
			// Read book name
		}
		
		color.set(new CmColor(rgbValue, colorByte));
	}

	public void H(Value<Handle> value) {
		Handle handle = getHandle();
		value.set(handle);
	}

	public void H(Value<Handle> value, HandleType type) {
		Handle handle = getHandle();
		value.set(handle);
	}

	public void threeBD(Value<Point3D> field) {
		double x = getBD();
		double y = getBD();
		double z = getBD();
		field.set(new Point3D(x, y, z));
	}

	// Two raw doubles
	public void twoRD(Value<Point2D> field) {
		double x = getRD();
		double y = getRD();
		field.set(new Point2D(x, y));
	}

	public void TU(Value<String> field) {
		String text = getTU();
		field.set(text);
	}

	/**
	 * Paragraph 2.12 Object Type
	 * 
	 * @return
	 */
	public int getOT() {
        int code = getBitsUnsigned(2);
        switch (code) {
        case 0:
        	return getBitsUnsigned(8);
        case 1:
        	return getBitsUnsigned(8) + 0x01F0;
        case 3:
        	// TODO issue warning and fall thru to case 2
        case 2:
        	return getBitsUnsigned(16);
       	default:
       		throw new RuntimeException("cannot happen");
        }
	}

	public void advanceToByteBoundary() {
		if (bitOffset != 0) {
            currentByte = byteArray[++currentOffset];
            bitOffset = 0;
		}
	}

	public void assertEndOfStream() {
		int currentBitOffset = currentOffset*8 + bitOffset;
		if (currentBitOffset != endOffset) {
			throw new RuntimeException("not at end of stream");
		}
	}

    public CMC getCMC()
    {
        int colorIndex = getBS();
        if (colorIndex == 0) {
            int rgbValue = getBL();
            int colorByte = getRC();
        } else {
            // no more fields
        }
        return new CMC();
    }

}
