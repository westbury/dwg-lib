package com.onespatial.dwglib.objects;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.Handle;
import com.onespatial.dwglib.bitstreams.Point2D;
import com.onespatial.dwglib.bitstreams.Point3D;

public class Hatch extends EntityObject {

    public static class PolylinePathSegment {
        public Point2D pt0;
        public double bulge;
    }

    public class PolylinePath extends Path {
        public boolean bulgesPresent;
        public boolean closed;
        public PolylinePathSegment[] pathSegments;

        @Override
        public void readFromDataStream(BitBuffer dataStream, BitBuffer handleStream, FileVersion fileVersion) {
            bulgesPresent = dataStream.getB();
            closed = dataStream.getB();
            int numPathSegs = dataStream.getBL();
            pathSegments = new PolylinePathSegment[numPathSegs];
            for (int j = 0; j < numPathSegs; j++) {
                pathSegments[j].pt0 = dataStream.get2RD();
                if (bulgesPresent) {
                    pathSegments[j].bulge = dataStream.getBD();
                }
            }

            super.readBoundaryItemCountAndHandles(dataStream, handleStream);
        }
    }

    public class SegmentedPath extends Path {
        public PathType[] pathSegments;

        @Override
        public void readFromDataStream(BitBuffer dataStream, BitBuffer handleStream, FileVersion fileVersion) {
            int numPathSegs = dataStream.getBL();
            pathSegments = new PathType[numPathSegs];
            for (int j = 0; j < numPathSegs; j++) {
                int pathTypeStatus = dataStream.getRC();
                switch (pathTypeStatus) {
                case 1: // LINE
                    pathSegments[j] = new LinePathType();
                    break;
                case 2: // CIRCULAR ARC
                    pathSegments[j] = new CircularArcPathType();
                    break;
                case 3: // ELIPTICAL ARC
                    pathSegments[j] = new ElipticalArcPathType();
                    break;
                case 4: // SPLINE
                    pathSegments[j] = new SplinePathType();
                    break;
                default:
                    throw new RuntimeException("unexpected case");
                }

                pathSegments[j].readFromDataStream(dataStream, fileVersion);
            }

            super.readBoundaryItemCountAndHandles(dataStream, handleStream);
        }
    }

    public abstract class Path {
        private Handle[] boundaryObjHandles;

        public abstract void readFromDataStream(BitBuffer dataStream, BitBuffer handleStream, FileVersion fileVersion);

        public void readBoundaryItemCountAndHandles(BitBuffer dataStream, BitBuffer handleStream) {
            int numboundaryobjhandles = dataStream.getBL();
            boundaryObjHandles = new Handle[numboundaryobjhandles];
            for (int i = 0; i < numboundaryobjhandles; i++) {
                boundaryObjHandles[i] = handleStream.getHandle(handleOfThisObject);
            }
        }
    }

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
        public DefLine[] defLines;

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
        public double unknownDouble;
        public int unknownShort;
        public int rgbColor;
        public int ignoredColorByte;
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

        @Override
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

        @Override
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

        @Override
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

        @Override
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
    public GradientColor[] gradientColors;
    public String gradientName;
    public double zCoordinate;
    public Point3D extrusion2;
    public String name;
    public boolean associative;
    public Path[] paths;
    public int style;
    public int patternType;
    public Fill fill;
    public double pixelSize;
    public Point2D[] seedPoints;

    public Hatch(ObjectMap objectMap) {
        super(objectMap);
    }

    @Override
    public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream,
            FileVersion fileVersion) {

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
        paths = new Path[numPaths];
        for (int i = 0; i < numPaths; i++) {
            int pathFlag = dataStream.getBL();
            if ((pathFlag & 0x02) == 0) {
                paths[i] = new SegmentedPath();
            } else {
                // Polyline path
                paths[i] = new PolylinePath();
            }

            paths[i].readFromDataStream(dataStream, handleStream, fileVersion);

            // Extra, not documented in spec.
            // This field is not present when numPaths = 0 so
            // this is assumed to be a field that exists in each path.
            // known to appear in R27.
            int unknown = dataStream.getBL();

            pixelSizePresent |= (pathFlag & 0x04) != 0;
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

    @Override
    public String toString() {
        return "HATCH";
    }
}
