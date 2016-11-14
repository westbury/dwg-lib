package objects;

import java.util.ArrayList;
import java.util.List;

import bitstreams.BitBuffer;
import bitstreams.Handle;
import bitstreams.Point2D;
import dwglib.FileVersion;

public class LwPolyline extends EntityObject {

    public Handle referencedHandle;

    public List<VertexOfLwPolyline> points;

    public Double constantWidth;

    @Override
    public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {

        boolean b1 = dataStream.getB();

        int numberOfPoints;
        int numberOfVariableWidths = 0;
        int numberOfBulges = 0;

        if (b1) {
            dataStream.expectB(false);

            numberOfPoints = dataStream.getBS();
        } else {
            boolean bit0 = dataStream.getB();

            boolean bit1 = dataStream.getB();
            boolean bit2 = dataStream.getB();
            boolean areVariableWidthsPresent = dataStream.getB();
            boolean areBulgesPresent = dataStream.getB();
            dataStream.expectB(false);
            boolean bitC = dataStream.getB();
            dataStream.expectB(false);
            dataStream.expectB(false);

            assert !bit1;
            assert !bit2;

            if (!areVariableWidthsPresent) {

                if (areBulgesPresent != bitC) {
                    System.out.println("different");
                }

                boolean hasWidth = areBulgesPresent;

                if (!bit0) {
                    int flags17 = dataStream.getBitsUnsigned(8);
                    if (flags17 != 2) {
                        throw new RuntimeException("new case");
                    }

                    if (hasWidth) {
                        constantWidth = dataStream.getBD();
                        numberOfPoints = dataStream.getBS();

                        if (areVariableWidthsPresent == areBulgesPresent) {
                            System.out.println("here");
                        }
                        if (areVariableWidthsPresent) {
                            numberOfVariableWidths = dataStream.getBS();
                            if (numberOfPoints != numberOfVariableWidths) {
                                throw new RuntimeException();
                            }
                        }
                        if (areBulgesPresent) {
                            numberOfBulges = dataStream.getBS();
                            if (numberOfPoints != numberOfBulges) {
                                throw new RuntimeException();
                            }
                        }
                    } else {
                        numberOfPoints = dataStream.getBS();
                        numberOfVariableWidths = 0;
                        numberOfBulges = 0;
                    }
                } else {
                    constantWidth = dataStream.getBD();
                    numberOfPoints = dataStream.getBS();

                    numberOfVariableWidths = 0;
                    numberOfBulges = 0;
                    if (hasWidth) {
                        if (areVariableWidthsPresent == areBulgesPresent) {
                            System.out.println("");
                        }
                        if (areVariableWidthsPresent) {                      
                            numberOfVariableWidths = dataStream.getBS();
                            if (numberOfVariableWidths != numberOfPoints) {
                                throw new RuntimeException();
                            }
                        }
                        if (areBulgesPresent) {
                            numberOfBulges = dataStream.getBS();
                            if (numberOfBulges != numberOfPoints) {
                                throw new RuntimeException();
                            }
                        }
                    }
                }
            } else {
                report(dataStream, 100, 0.0);

                if (!bit0) {
                    // Actually this path is never hit in the test files.
                    System.out.println("this case fails");
                }

                if (areBulgesPresent || bitC)
                    System.out.println("here");



                numberOfPoints = dataStream.getBS();
                if (areVariableWidthsPresent) {                      
                    numberOfVariableWidths = dataStream.getBS();
                    if (numberOfVariableWidths != numberOfPoints) {
                        throw new RuntimeException();
                    }
                } else {
                    numberOfBulges = dataStream.getBS();
                    if (numberOfBulges != numberOfPoints) {
                        throw new RuntimeException();
                    }
                }
                if (numberOfPoints != 2 || numberOfVariableWidths != 2)
                    System.out.println("here");
            }

            System.out.println("handles: " + genericHandles.size());
        }




        points = new ArrayList<>(numberOfPoints);

        double x = dataStream.getRD();
        double y = dataStream.getRD();
        points.add(new VertexOfLwPolyline(x, y));
        for (int index = 1; index < numberOfPoints; index++) {
            x = dataStream.getDD(x);
            y = dataStream.getDD(y);
            points.add(new VertexOfLwPolyline(x, y));
        }

        report(dataStream, 100, 0.0);

        for (int index = 0; index < numberOfVariableWidths; index++) {
            double startingWidth = dataStream.getBD();
            double endingWidth = dataStream.getBD();
            points.get(index).setVariableWidth(startingWidth, endingWidth);
        }

        for (int index = 0; index < numberOfBulges; index++) {
            double bulge = dataStream.getBD();
            points.get(index).setBulge(bulge);
        }

        handleStream.advanceToByteBoundary();

        dataStream.assertEndOfStream();
        stringStream.assertEndOfStream();
        handleStream.assertEndOfStream();
    }

    public String toString() {
        return "LWPOLYLINE";
    }
    
    private void report(BitBuffer dataStream, int numberOfUnknownBits, double base)
    {
        int start = dataStream.position();

        int p =dataStream.position();
        for (int i=0; i<numberOfUnknownBits; i++) {
            try {
                dataStream.position(p);
                boolean b = dataStream.getB();
                System.out.print(" (" + i + ") " +(b ? "1" : "0"));
                try {
                    if (i <= numberOfUnknownBits-16) {
                        dataStream.position(p);
                        int s = dataStream.getRS();
                        System.out.print(" : " + s);
                        if (i <= numberOfUnknownBits-64) {
                            dataStream.position(p);
                            double x = dataStream.getRD();
                            System.out.print(", " + x);
                        }
                    }
                } catch (Exception e) {
                    System.out.println(" : <16 bit left");
                }

            } catch (Exception e) {
                break;
            }

            try {

                dataStream.position(p);
                double xu1 = dataStream.getDD(0.0);
                System.out.print(", from base 0: " + xu1);

                dataStream.position(p);
                double xu2 = dataStream.getDD(1.0);
                System.out.print(", from base 1: " + xu2);
            } catch (Exception e) {
                System.out.print(", failed");
            }

            System.out.println("");
            p++;
        }

        dataStream.position(start);
    }

    public static class VertexOfLwPolyline {

        public final Point2D vertex;
        public double startingWidth;
        public double endingWidth;
        public double bulge;
        
        public VertexOfLwPolyline(double x, double y) {
            this.vertex = new Point2D(x, y);
        }

        @Override
        public String toString() {
            return vertex.toString();
        }

        public void setVariableWidth(double startingWidth, double endingWidth) {
            this.startingWidth = startingWidth;
            this.endingWidth = endingWidth;
        }

        public void setBulge(double bulge) {
            this.bulge = bulge;
        }
    }
}
