package objects;

import java.util.ArrayList;
import java.util.List;

import bitstreams.BitBuffer;
import bitstreams.Handle;
import bitstreams.Point2D;
import dwglib.FileVersion;

public class LwPolyline extends EntityObject {

    public Handle referencedHandle;

    public List<Point2D> points;

    public Double firstNumber;

    public Double x;

    public Double y;

    public Double littleDouble;

    public Double unknownDouble1;

    public Double unknownDouble2;

    @Override
    public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {

        boolean b1 = dataStream.getB();

        if (b1) {
            dataStream.expectB(false);

            int numberOfPoints = dataStream.getBS();

            points = new ArrayList<>(numberOfPoints);

            double x = dataStream.getRD();
            double y = dataStream.getRD();
            points.add(new Point2D(x, y));
            for (int index = 1; index < numberOfPoints; index++) {
                x = dataStream.getDD(x);
                y = dataStream.getDD(y);
                points.add(new Point2D(x, y));
            }

            dataStream.assertEndOfStream();
        } else {
            int p = dataStream.position();

            boolean bit0 = dataStream.getB();
            boolean bit1 = dataStream.getB();
            boolean bit2 = dataStream.getB();
            boolean bit3 = dataStream.getB();

            if (!bit3) {
                boolean bitA = dataStream.getB();
                dataStream.expectB(false);
                boolean bitC = dataStream.getB();
                dataStream.expectB(false);
                dataStream.expectB(false);
                dataStream.expectB(false);
                dataStream.expectB(false);
                int flagsC = dataStream.getBitsUnsigned(5);
                switch (flagsC) {
                case 0:
                case 1:
                case 15:
                case 31:
                case 23:
                    break;
                default:
                    throw new RuntimeException("new flag set");
                }

                dataStream.expectB(false);

                if (!bit0) {
                    int flags17 = dataStream.getBitsUnsigned(10);
                    double y = 0;
                    switch (flags17) {
                    case 0:
                        firstNumber = dataStream.getRD();
                        break;
                    case 260:
                        unknownDouble1 = dataStream.getRD();
                        unknownDouble2 = dataStream.getRD();
                        break;
                    default:
                        throw new RuntimeException("unexpected");
                    }

                    System.out.println(", " + firstNumber + ", " + y + ", pos now " + (dataStream.position()-p));

                } else {

                    try {
                        boolean b3a = dataStream.getB();
                        boolean b4a = dataStream.getB();
                        firstNumber = dataStream.getRD();
                        if (!bitA) {
                            boolean b3 = dataStream.getB();
                            boolean b4 = dataStream.getB();
                            x = dataStream.getRD();
                            y = dataStream.getRD();
                            System.out.println("case A: " + flagsC + "(" + firstNumber + ", " + x + ", "+y+")");
                        } else {
                            // TODO investigate layout from here.
                            System.out.println("case B: " + flagsC + "(" + firstNumber + "), now at " + (dataStream.position()-p));
                        }
                    } catch (Exception e) {

                    }
                    int flags2 = dataStream.getBitsUnsigned(6);
                }
            } else {
                if (!bit0) {
                    // Actually this path is never hit in the test files.
                    System.out.println("this case fails");
                }

                int flags = dataStream.getBitsUnsigned(25);

                switch (flags) {
                case 264450:
                    break;
                default:
                    throw new RuntimeException("new flag set");
                }

                try {
                    x = dataStream.getRD();
                    y = dataStream.getRD();
                    boolean b3 = dataStream.getB();
                    boolean isLittleDoublePresent = dataStream.getB();
                    if (isLittleDoublePresent) {
                        littleDouble = dataStream.getRD();
                    }
                    System.out.println("case C: " + flags + "(" + x + ", " + y + ", "+ littleDouble +")");
                } catch (Exception e) {

                }
            }

            System.out.println("handles: " + genericHandles.size());
        }

        handleStream.advanceToByteBoundary();

        stringStream.assertEndOfStream();
    }

    public String toString() {
        return "LWPOLYLINE";
    }
}
