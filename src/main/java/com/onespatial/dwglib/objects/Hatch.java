package com.onespatial.dwglib.objects;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.Point2D;
import com.onespatial.dwglib.bitstreams.Point3D;

public class Hatch extends EntityObject {

    public static class DefLine {

        public double angle;
        public Point2D pt0;
        public Point2D offset;
        public double[] dashLengths;
        
        public void readFromDataStream(BitBuffer dataStream) {
            angle = dataStream.getBD();
            pt0 = dataStream.get2RD();
            offset = dataStream.get2BD();
            int numDashes = dataStream.getBL();
            dashLengths = new double[numDashes]; 
            for (int i = 0; i < numDashes; i++) {
                dashLengths[i] = dataStream.getBD();
            }
        }
    }

    public static class Fill {

        public double angle;
        public Point2D pt0;
        public double scaleOrSpacing;
        public boolean doubleHatch;
        private DefLine[] defLines;

        public void readFromDataStream(BitBuffer dataStream) {
            angle = dataStream.getBD();
            pt0 = dataStream.get2RD();
            scaleOrSpacing = dataStream.getBD();
            doubleHatch = dataStream.getB();
            int numDefLines = dataStream.getBS();
            defLines = new DefLine[numDefLines];
            for (int i = 0; i < numDefLines; i++) {
                defLines[i] = new DefLine();
                defLines[i].readFromDataStream(dataStream);
            }
        }

    }

    public static class GradientColor {
        double unknownDouble;
        int unknownShort;
        int rgbColor;
        int ignoredColorByte;
    }

    public static class ControlPoint {
        public Point2D pt0;
        public double weight;
    }

    public abstract class PathType {
        public abstract void readFromDataStream(BitBuffer dataStream, FileVersion fileVersion);
    }

    public class LinePathType extends PathType {
        public double pt0;
        public double pt1;

        public void readFromDataStream(BitBuffer dataStream, FileVersion fileVersion) {
            pt0 = dataStream.getRD();
            pt1 = dataStream.getRD();
        }
    }

    public class CircularArcPathType extends PathType {
        public double pt0;
        public double radius;
        public double startAngle;
        public double endAngle;
        public boolean isCcw;

        public void readFromDataStream(BitBuffer dataStream, FileVersion fileVersion) {
            pt0 = dataStream.getRD();
            radius = dataStream.getBD();
            startAngle = dataStream.getBD();
            endAngle = dataStream.getBD();
            isCcw = dataStream.getB();
        }
    }

    public class ElipticalArcPathType extends PathType {
        public Point2D pt0;
        public Point2D endPoint;
        public double minorMajorRatio;
        public double startAngle;
        public double endAngle;
        public boolean isCcw;

        public void readFromDataStream(BitBuffer dataStream, FileVersion fileVersion) {
            pt0 = dataStream.get2RD();
            endPoint = dataStream.get2RD();
            minorMajorRatio = dataStream.getBD();
            startAngle = dataStream.getBD();
            endAngle = dataStream.getBD();
            isCcw = dataStream.getB();
        }
    }

    public class SplinePathType extends PathType {

        public int degree;
        public boolean isPeriodic;
        public double[] knots;
        public ControlPoint[] controlPoints;
        public Point2D[] fitPoints;
        public Point2D startTangent;
        public Point2D endTanget;

        public void readFromDataStream(BitBuffer dataStream, FileVersion fileVersion) {
            degree = dataStream.getBL();
            boolean isRational = dataStream.getB();
            isPeriodic = dataStream.getB();
            int numKnots = dataStream.getBL();
            int numControlPoints = dataStream.getBL();
            knots = new double[numKnots];
            for (int i = 0; i < numKnots; i++) {
                knots[i] = dataStream.getBD();
            }
            controlPoints = new ControlPoint[numControlPoints];
            for (int i = 0; i < numControlPoints; i++) {
                controlPoints[i] = new ControlPoint();
                controlPoints[i].pt0 = dataStream.get2RD();
                if (isRational) {
                    controlPoints[i].weight = dataStream.getBD();
                }
            }
            if (!fileVersion.is2013OrLater()) {
                int numFitPoints = dataStream.getBL();
                fitPoints = new Point2D[numFitPoints];
                for (int i = 0; i < numFitPoints; i++) {
                    fitPoints[i] = dataStream.get2RD();
                }
                startTangent = dataStream.get2RD();
                endTanget = dataStream.get2RD();
            }
        }
    }


    public Point3D start;
    public Point3D end;
    public double thickness;
    public Point3D extrusion;
    public int isGradientFill;
    public int reserved;
    public double gradientAngle;
    public double gradientShift;
    public int singleColorGradient;
    public double gradientTint;
    private GradientColor[] gradientColors;
    public String gradientName;
    public double zCoordinate;
    public Point3D extrusion2;
    public String name;
    public boolean associative;
    public int style;
    public int patternType;
    public double pixelSize;
    private Point2D[] seedPoints;
    private Fill fill;

    public Hatch(ObjectMap objectMap) {
        super(objectMap);
    }

    @Override
    public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {

        // 19.4.73 HATCH (78) page 184

        isGradientFill = dataStream.getBL();
        reserved = dataStream.getBL();
        gradientAngle = dataStream.getBD();
        gradientShift = dataStream.getBD();
        singleColorGradient = dataStream.getBL();
        gradientTint = dataStream.getBD();
        int numberOfGradientColors = dataStream.getBL();
        gradientColors = new GradientColor[numberOfGradientColors];
        for (int i = 0; i < numberOfGradientColors; i++) {
            gradientColors[i].unknownDouble = dataStream.getBD();
            gradientColors[i].unknownShort = dataStream.getBS();
            gradientColors[i].rgbColor = dataStream.getBL();
            gradientColors[i].ignoredColorByte = dataStream.getRC();
        }
        gradientName = stringStream.getTU();
        zCoordinate = dataStream.getBD();
        extrusion2 = dataStream.get3BD();
        name = stringStream.getTU();
        boolean solidFill = dataStream.getB();
        associative = dataStream.getB();
        
        boolean pixelSizePresent = false;
        
        int numPaths = dataStream.getBL();
        for (int i = 0; i < numPaths; i++) {
            int pathFlag = dataStream.getBL();
            if ((pathFlag & 0x02) == 0) {
                int numPathSegs = dataStream.getBL();
                for (int j = 0; j < numPathSegs; j++) {
                    int pathTypeStatus = dataStream.getRC();
                    PathType pathType;
                    switch (pathTypeStatus) {
                    case 1: // LINE
                        pathType = new LinePathType();
                        break;
                    case 2: // CIRCULAR ARC
                        pathType = new CircularArcPathType();
                        break;
                    case 3: // ELIPTICAL ARC
                        pathType = new ElipticalArcPathType();
                        break;
                    case 4: // SPLINE
                        pathType = new SplinePathType();
                        break;
                        default:
                            throw new RuntimeException("unexpected case");
                    }
                    
                    pathType.readFromDataStream(dataStream, fileVersion);
                    
                }
            } else {
                // Polyline path
                boolean bulgesPresent = dataStream.getB();
                boolean closed = dataStream.getB();
                int numPathSegs = dataStream.getBL();
                for (int j = 0; j < numPathSegs; j++) {
                    Point2D pt0 = dataStream.get2RD();
                    if (bulgesPresent) {
                        double bulge = dataStream.getBD();
                    }
                }
            }
            
            int numBoundaryObjHandles = dataStream.getBL();
            
            pixelSizePresent |= ((pathFlag & 0x04) != 0); 
        }

        style = dataStream.getBS();
        patternType = dataStream.getBS();
        if (!solidFill) {
            fill = new Fill();
            fill.readFromDataStream(dataStream);
        }
        
        if (pixelSizePresent) {
            pixelSize = dataStream.getBD();
        }

        int numSeedPoints = dataStream.getBL();
        seedPoints = new Point2D[numSeedPoints];
        for (int i = 0; i < numSeedPoints; i++) {
            seedPoints[i] = dataStream.get2RD();
        }
        
        handleStream.advanceToByteBoundary();

        dataStream.assertEndOfStream();
        stringStream.assertEndOfStream();
        handleStream.assertEndOfStream();
    }

    public String toString() {
        return "HATCH";
    }
}
