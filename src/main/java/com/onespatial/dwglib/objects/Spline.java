package com.onespatial.dwglib.objects;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.Point3D;

public class Spline extends EntityObject {

    public class CtrlPoint {

        private Point3D controlPoint;
        private Double weight;

        public void read(BitBuffer dataStream) {
            controlPoint = dataStream.get3BD();
            if (weightsPresent) {
                weight = dataStream.getBD();
            }
        }
    }

    public Point3D center;
    public double radius;
    public double thickness;
    public Point3D extrusion;

    private int scenario;
    private int splineFlags1;
    private int knotParameter;
    private int degree;

    private double fitTolerance;
    private Point3D beginningTangentVector;
    private Point3D endingTangentVector;

    private boolean rational;
    private boolean closed;
    private boolean periodic;
    private double knotTolerance;
    private double ctrlTolerance;
    private boolean weightsPresent;
    private double[] knotValues;
    private CtrlPoint[] ctrlPoints;
    private Point3D[] fitPoints;

    public Spline(ObjectMap objectMap) {
        super(objectMap);
    }

    @Override
    public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
        // 19.4.38 SPLINE (36) page 134

        scenario = dataStream.getBL();
        if (fileVersion.is2013OrLater()) {
            splineFlags1 = dataStream.getBL();
            knotParameter = dataStream.getBL();

            switch(knotParameter) {
            case 0:
            case 1:
            case 2:
                scenario = 1;
                break;
            case 15:
                scenario = 1;
                break;
            default:
                scenario = 1;
                knotParameter = 15;
                break;
            }
        }

        degree = dataStream.getBL();

        if (scenario == 2) {
            fitTolerance = dataStream.getBD();
            beginningTangentVector = dataStream.get3BD();
            endingTangentVector = dataStream.get3BD();
            int numberOfFitPoints = dataStream.getBL();

            fitPoints = new Point3D[numberOfFitPoints];
            for (int i = 0; i < numberOfFitPoints; i++) {
                fitPoints[i] = dataStream.get3BD();
            }
        }

        if (scenario == 1) {
            rational = dataStream.getB();
            closed = dataStream.getB();
            periodic = dataStream.getB();
            knotTolerance = dataStream.getBD();
            ctrlTolerance = dataStream.getBD();
            int numberOfKnots = dataStream.getBL();
            int numberOfCtrlPoints = dataStream.getBL();
            weightsPresent = dataStream.getB();

            knotValues = new double[numberOfKnots];
            for (int i = 0; i < numberOfKnots; i++) {
                knotValues[i] = dataStream.getBD();
            }

            ctrlPoints = new CtrlPoint[numberOfCtrlPoints];
            for (int i = 0; i < numberOfCtrlPoints; i++) {
                ctrlPoints[i] = new CtrlPoint();
                ctrlPoints[i].read(dataStream);
            }
        }

        handleStream.advanceToByteBoundary();

        dataStream.assertEndOfStream();
        stringStream.assertEndOfStream();
        handleStream.assertEndOfStream();
    }

    @Override
    public String toString() {
        return "SPLINE";
    }
}
