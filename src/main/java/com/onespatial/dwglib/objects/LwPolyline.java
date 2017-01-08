package com.onespatial.dwglib.objects;

import java.util.ArrayList;
import java.util.List;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.Handle;
import com.onespatial.dwglib.bitstreams.Point2D;

public class LwPolyline extends EntityObject {

    public Handle referencedHandle;

    public List<VertexOfLwPolyline> points;

    public Double constantWidth;

    public boolean isClosed;
    
    public LwPolyline(ObjectMap objectMap) {
        super(objectMap);
    }

    @Override
    public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {

        boolean b1 = dataStream.getB();
        boolean b2 = dataStream.getB();

        int numberOfPoints;
        int numberOfVariableWidths = 0;
        int numberOfBulges = 0;
        
        if (b1) {
            // It seems that the polyline is always closed when b1 is set.
            isClosed = true;

            numberOfPoints = dataStream.getBS();
        } else {
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

                if (!b2) {
                    int flags17 = dataStream.getBitsUnsigned(8);
                    if (flags17 != 2) {
                        throw new RuntimeException("new case");
                    }

                    isClosed = true;
                    
                    if (hasWidth) {
                        constantWidth = dataStream.getBD();
                        numberOfPoints = dataStream.getBS();
                        if (numberOfPoints == 0) {
                            System.out.println("it's gone wrong");
                        }
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
                        if (bitC) {
                            // When this bit is set there appears to be an extra
                            // bit-double.  TODO compare to DXF to determine what
                            // this is.
                            double x = dataStream.getBD();
                        }
                        numberOfPoints = dataStream.getBS();
                        if (numberOfPoints == 0) {
                            System.out.println("it's gone wrong");
                        }
                        numberOfVariableWidths = 0;
                        numberOfBulges = 0;
                    }
                } else {
                    isClosed = false;
                    
                    constantWidth = dataStream.getBD();
                    numberOfPoints = dataStream.getBS();
                    if (numberOfPoints == 0) {
                        System.out.println("it's gone wrong");
                    }

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
                isClosed = false;
                
                if (!b2) {
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
