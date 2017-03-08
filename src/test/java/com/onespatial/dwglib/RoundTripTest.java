package com.onespatial.dwglib;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.junit.BeforeClass;
import org.junit.Test;

import com.onespatial.dwglib.bitstreams.BitStreams;
import com.onespatial.dwglib.bitstreams.BitWriters;
import com.onespatial.dwglib.bitstreams.Point3D;
import com.onespatial.dwglib.objects.Arc;
import com.onespatial.dwglib.objects.BlockControlObj;
import com.onespatial.dwglib.objects.BlockHeader;
import com.onespatial.dwglib.objects.CadObject;
import com.onespatial.dwglib.objects.Circle;
import com.onespatial.dwglib.objects.EntityObject;
import com.onespatial.dwglib.objects.Insert;
import com.onespatial.dwglib.objects.Layer;
import com.onespatial.dwglib.objects.Line;
import com.onespatial.dwglib.objects.LwPolyline;
import com.onespatial.dwglib.objects.LwPolyline.VertexOfLwPolyline;
import com.onespatial.dwglib.objects.Point;
import com.onespatial.dwglib.objects.Text;
import com.onespatial.dwglib.objects.TwoDPolyline;
import com.onespatial.dwglib.writer.BitWriter;

public class RoundTripTest {

    /**
     * a sample file, available from Autodesk, in 2010 format
     */
    static File testFile = new File("visualization_-_aerial.dwg");


    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        if (!testFile.exists()) {

            URL urlToTestFile = new URL("http://download.autodesk.com/us/samplefiles/acad/visualization_-_aerial.dwg");
            try (ReadableByteChannel inputChannel = Channels.newChannel(urlToTestFile.openStream());
                    FileOutputStream outputStream = new FileOutputStream(testFile);) {
                outputStream.getChannel().transferFrom(inputChannel, 0, Long.MAX_VALUE);
            }
        }
    }

    @Test
    public void updatePointTest() throws Exception {

        try (Reader reader = new Reader(testFile) {
            @Override
            protected CadObject parseObjectAtGivenOffset(int offsetIntoObjectMap) {
                CadObject cadObject = super.parseObjectAtGivenOffset(offsetIntoObjectMap);

                BitWriter dataStream = new BitWriter(issues);
                BitWriter stringStream = new BitWriter(issues);
                BitWriter handleStream = new BitWriter(issues);

                /*
                 * Even though we have not in fact made any changes, set this
                 * flag on so more stuff is serialized from scratch rather than
                 * just copied from the input buffer.
                 */
                if (cadObject instanceof Point
                        || cadObject instanceof TwoDPolyline) {
                    cadObject.isDirty = true;

                    dataStream.putOT(cadObject.getObjectType());
                    cadObject.write(objectBuffer, dataStream, stringStream, handleStream, issues);

                    BitWriters writers = new BitWriters(dataStream, stringStream, handleStream, issues);
                    byte[] byteArray = writers.getByteArray();

                    ByteBuffer objectsBuffer = ByteBuffer.wrap(objectBuffer);
                    objectsBuffer.order(ByteOrder.LITTLE_ENDIAN);
                    objectsBuffer.position(offsetIntoObjectMap);

                    int sizeOfObject = BitStreams.getMS(objectsBuffer);

                    for (int i = 0; i < sizeOfObject; i++) {
                        if (byteArray[i] != objectBuffer[offsetIntoObjectMap + i]) {
                            System.out.println("mismatch");
                        }
                        assertEquals("at offset " + i + " is a mismatch", byteArray[i],
                                objectBuffer[offsetIntoObjectMap + i]);
                    }
                }

                return cadObject;
            }
        }) {
            BlockControlObj blockControl = reader.getBlockControlObject();
            BlockHeader ms = (BlockHeader) blockControl.getModelSpace();
            if (ms != null) {
                for (CadObject entity : ms.getOwnedObjects()) {
                    if (entity instanceof EntityObject) {
                        traceEntity("", (EntityObject) entity, null);
                    } else {
                        System.out.println(entity.getClass().getSimpleName());
                    }
                }
            }

            reader.save();
        }
    }

    private void traceEntity(String indent, EntityObject entity, Layer parentLayer) {
        Layer layer = entity.getLayer();
        if (layer.entryName.equals("0")) {
            /*
             * Block layer, so inherit from parent if there is a parent. Some
             * DWG files have blocks in layer "0" with no parent. In those cases
             * we leave the block in layer "0".
             */
            if (parentLayer != null) {
                layer = parentLayer;
            }
        }

        if (entity instanceof Point) {
            Point point = (Point) entity;

            Point3D originalPoint = point.getPoint();
            Point3D updatedPoint = new Point3D(originalPoint.x + 100, originalPoint.y, originalPoint.z);
            point.setPoint(updatedPoint);
        } else if (entity instanceof Line) {
            Line line = (Line) entity;
            System.out.println(indent + "    Line: " + line.start + " to " + line.end);
        } else if (entity instanceof LwPolyline) {
            LwPolyline polyline = (LwPolyline) entity;

            System.out.print(indent + "    LwPolyline: ");
            for (VertexOfLwPolyline point : polyline.points) {
                System.out.print(" " + point.vertex);
            }
            System.out.println("");
        } else if (entity instanceof Arc) {
            Arc arc = (Arc) entity;
            System.out.println(indent + "    Arc: " + arc.center + " with radius " + arc.radius + ", angle "
                    + arc.startAngle + " to " + arc.endAngle);
        } else if (entity instanceof Circle) {
            Circle arc = (Circle) entity;
            System.out.println(indent + "    Circle: " + arc.center + " with radius " + arc.radius);
        } else if (entity instanceof TwoDPolyline) {
            TwoDPolyline polyline = (TwoDPolyline) entity;
            System.out.println(indent + "    2D Polyline: " + polyline.getOwnedObjects().size() + " vertexes");
        } else if (entity instanceof Text) {
            Text textObject = (Text) entity;
            System.out.println(indent + "    Text: " + textObject.textValue);
        } else if (entity instanceof Insert) {
            Insert insert = (Insert) entity;
            BlockHeader block = insert.getBlockHeader();
            System.out.println(indent + "    Insert block " + block.entryName + ": scale is (" + insert.xScaleFactor
                    + ", " + insert.yScaleFactor + "), rotation is " + insert.rotation);

            for (CadObject child : block.getOwnedObjects()) {
                if (child instanceof EntityObject) {
                    traceEntity("    " + indent, (EntityObject) child, layer);
                } else {
                    System.out.println(indent + "    " + child.getClass().getSimpleName());
                }
            }
        } else {
            System.out.println(indent + "    " + entity.getClass().getSimpleName());
        }
    }


}
