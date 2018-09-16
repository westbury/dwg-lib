package com.onespatial.dwglib;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.onespatial.dwglib.bitstreams.Handle;
import com.onespatial.dwglib.bitstreams.Point3D;
import com.onespatial.dwglib.objects.AcdbDictionaryWithDefault;
import com.onespatial.dwglib.objects.Appid;
import com.onespatial.dwglib.objects.AppidControlObj;
import com.onespatial.dwglib.objects.Arc;
import com.onespatial.dwglib.objects.Attdef;
import com.onespatial.dwglib.objects.Attrib;
import com.onespatial.dwglib.objects.BlockControlObj;
import com.onespatial.dwglib.objects.BlockHeader;
import com.onespatial.dwglib.objects.CadObject;
import com.onespatial.dwglib.objects.Circle;
import com.onespatial.dwglib.objects.Dictionary;
import com.onespatial.dwglib.objects.EntityObject;
import com.onespatial.dwglib.objects.Insert;
import com.onespatial.dwglib.objects.LType;
import com.onespatial.dwglib.objects.LTypeControlObj;
import com.onespatial.dwglib.objects.Layer;
import com.onespatial.dwglib.objects.LayerControlObj;
import com.onespatial.dwglib.objects.Layout;
import com.onespatial.dwglib.objects.Line;
import com.onespatial.dwglib.objects.LwPolyline;
import com.onespatial.dwglib.objects.LwPolyline.VertexOfLwPolyline;
import com.onespatial.dwglib.objects.MText;
import com.onespatial.dwglib.objects.Point;
import com.onespatial.dwglib.objects.PolylineMesh;
import com.onespatial.dwglib.objects.PolylinePFace;
import com.onespatial.dwglib.objects.SortEntsTable;
import com.onespatial.dwglib.objects.Text;
import com.onespatial.dwglib.objects.TwoDPolyline;

public abstract class TraversalTest
{

    Set<CadObject> printed = new HashSet<>();

    Layer previousLayer = null;

    /**
     * the file under test, to be set in the @BeforeClass in the concrete test
     * class.
     */
    abstract protected File getTestFile();

    @Test
    public void traversalTest() throws Exception {
        File testFile = getTestFile();

        try (Reader reader = new Reader(testFile)) {
            BlockControlObj blockControl = reader.getBlockControlObject();
            BlockHeader ms = (BlockHeader)blockControl.getModelSpace();
            if (ms != null) {
                for (CadObject entity : ms.getOwnedObjects()) {
                    if (entity instanceof EntityObject)
                    {
                        traceEntity("", (EntityObject) entity, null);
                    }
                    else
                    {
                        System.out.println(entity.getClass().getSimpleName());
                    }
                }
            }
        }
    }

    private void traceEntity(String indent, EntityObject entity, Layer parentLayer)
    {
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
        } else {
            /*
             * Generally blocks have entities defined in layer '0'.
             * Occasionally the entities are in another layer. If a block
             * has its entities defined in the '0' layer then for most uses
             * one probably wants to expand the entities into the layer into
             * which the insert occurs.
             */
            if (layer != previousLayer) {
                System.out.println("Layer: " + (layer == null ? "null" : layer.entryName));
                previousLayer = layer;
            }
        }

        if (entity instanceof Point) {
            Point point = (Point)entity;
            System.out.println(indent + "    Point: " + point.getPoint());
        } else if (entity instanceof Line) {
            Line line = (Line)entity;
            System.out.println(indent + "    Line: " + line.start + " to " + line.end);
        } else if (entity instanceof LwPolyline) {
            LwPolyline polyline = (LwPolyline)entity;

            System.out.print(indent + "    LwPolyline: ");
            for (VertexOfLwPolyline point : polyline.points) {
                System.out.print(" " + point.vertex);
            }
            System.out.println("");
        } else if (entity instanceof Arc) {
            Arc arc = (Arc)entity;
            System.out.println(indent + "    Arc: " + arc.center + " with radius " + arc.radius + ", angle " + arc.startAngle + " to " + arc.endAngle);
        } else if (entity instanceof Circle) {
            Circle arc = (Circle)entity;
            System.out.println(indent + "    Circle: " + arc.center + " with radius " + arc.radius);
        } else if (entity instanceof TwoDPolyline) {
            TwoDPolyline polyline = (TwoDPolyline)entity;
            System.out.println(indent + "    2D Polyline: " + polyline.getOwnedObjects().size() + " vertexes");
        } else if (entity instanceof Text) {
            Text textObject = (Text)entity;
            System.out.println(indent + "    Text: " + textObject.textValue);
        } else if (entity instanceof Insert) {
            Insert insert = (Insert)entity;
            BlockHeader block = insert.getBlockHeader();
            System.out.println(indent + "    Insert block " + block.entryName + ": scale is (" + insert.xScaleFactor + ", " + insert.yScaleFactor + "), rotation is " + insert.rotation);

            for (CadObject child : block.getOwnedObjects())
            {
                if (child instanceof EntityObject)
                {
                    traceEntity("    " + indent, (EntityObject) child, layer);
                }
                else
                {
                    System.out.println(indent + "    " + child.getClass().getSimpleName());
                }
            }
        } else {
            System.out.println(indent + "    " + entity.getClass().getSimpleName());
        }
    }

    @Test
    public void traverseEverythingTest() throws Exception {
        File testFile = getTestFile();

        try (Reader reader = new Reader(testFile)) {
            Map<String, CadObject> rootObjects = new HashMap<>();
            rootObjects.put("CLAYER", reader.getCLayer());
            rootObjects.put("TEXTSTYLE", reader.getTextStyle());
            rootObjects.put("CELTYPE", reader.getCelType());
            rootObjects.put("CMATERIAL", reader.getCMaterial());
            rootObjects.put("DIMSTYLE", reader.getDimStyle());
            rootObjects.put("CMLSTYLE", reader.getCmlStyle());
            rootObjects.put("DIMTXSTY", reader.getDimTxSty());
            rootObjects.put("DIMLDRBLK", reader.getDimLdrBlk());
            rootObjects.put("DIMBLK", reader.getDimBlk());
            rootObjects.put("DIMBLK1", reader.getDimBlk1());
            rootObjects.put("DIMBLK2", reader.getDimBlk2());
            rootObjects.put("DIMLTYPE", reader.getDimLType());
            rootObjects.put("DIMLTEX1", reader.getDimLTex1());
            rootObjects.put("DIMLTEX2", reader.getDimLTex2());
            rootObjects.put("BLOCK_CONTROL_OBJECT", reader.getBlockControlObject());
            rootObjects.put("LAYER_CONTROL_OBJECT", reader.getLayerControlObject());
            rootObjects.put("STYLE_CONTROL_OBJECT", reader.getStyleControlObject());
            rootObjects.put("LINETYPE_CONTROL_OBJECT", reader.getLinetypeControlObject());
            rootObjects.put("VIEW_CONTROL_OBJECT", reader.getViewControlObject());
            rootObjects.put("UCS_CONTROL_OBJECT", reader.getUcsControlObject());
            rootObjects.put("VPORT_CONTROL_OBJECT", reader.getVPortControlObject());
            rootObjects.put("APPID_CONTROL_OBJECT", reader.getAppidControlObject());
            rootObjects.put("DIMSTYLE_CONTROL_OBJECT", reader.getDimStyleControlObject());
            rootObjects.put("DICTIONARY_ACAD_GROUP", reader.getDictionaryAcadGroup());
            rootObjects.put("DICTIONARY_ACAD_MLINESTYLE", reader.getDictionaryAcadMLineStyle());
            rootObjects.put("DICTIONARY_NAMED_OBJECTS", reader.getDictionaryNamedObjects());
            rootObjects.put("DICTIONARY_LAYOUTS", reader.getDictionaryLayouts());
            rootObjects.put("DICTIONARY_PLOTSETTINGS", reader.getDictionaryPlotsettings());
            rootObjects.put("DICTIONARY_PLOTSTYLES", reader.getDictionaryPlotstyles());
            rootObjects.put("DICTIONARY_MATERIALS", reader.getDictionaryMaterials());
            rootObjects.put("DICTIONARY_COLORS", reader.getDictionaryColors());
            rootObjects.put("DICTIONARY_VISUALSTYLE", reader.getDictionaryVisualstyle());
            rootObjects.put("UNKNOWN", reader.getUnknown());
            rootObjects.put("CPSNID", reader.getCpsnid());
            rootObjects.put("BLOCK_RECORD_PAPER_SPACE", reader.getBlockRecordPaperSpace());
            rootObjects.put("BLOCK_RECORD_MODEL_SPACE", reader.getBlockRecordModelSpace());
            rootObjects.put("LTYPE_BYLAYER", reader.getLTypeByLayer());
            rootObjects.put("LTYPE_BYBLOCK", reader.getLTypeByBlock());
            rootObjects.put("LTYPE_CONTINUOUS", reader.getLTypeContinuous());
            rootObjects.put("INTERFEREOBJVS", reader.getInterfereObjvs());
            rootObjects.put("INTERFEREVPVS", reader.getInterfereVpvs());
            rootObjects.put("DRAGVS", reader.getDragvs());

            for (String rootObjectName : rootObjects.keySet()) {
                CadObject cadObject = rootObjects.get(rootObjectName);
                parseAndPrintPossiblyNull(rootObjectName, cadObject, "");
            }
        }
    }

    private void printObject(CadObject cadObject, String indent)
    {
        // Trace out the Extended Entity Data
        Map<Appid, Object[]> extendedEntityData = cadObject.getExtendedEntityData();
        for (Appid app : extendedEntityData.keySet()) {
            Object [] data = extendedEntityData.get(app);

            StringBuffer trace = new StringBuffer();
            appendTrace(trace, data);

            System.out.println(indent + "    EED data for " + app.entryName + " = " + trace.toString());
        }

        for (CadObject reactor : cadObject.getReactors()) {
            parseAndPrintPossiblyNull("Reactor", reactor, indent);
        }
        Dictionary xdicobj = cadObject.getXdicobj();
        if (xdicobj != null) {
            parseAndPrint("xdicobj", xdicobj, indent);
        }

        if (cadObject instanceof EntityObject) {
            EntityObject entity = (EntityObject)cadObject;
            parseAndPrint("Layer", entity.getLayer(), indent);
            LType linetype = entity.getLinetype();
            if (linetype != null) {
                parseAndPrint("Line Type", linetype, indent);
            }
            CadObject material = entity.getMaterial();
            if (material != null) {
                parseAndPrint("Material", material, indent);
            }
            CadObject plotstyle = entity.getPlotstyle();
            if (plotstyle != null) {
                parseAndPrint("Plotstyle", plotstyle, indent);
            }
        }

        switch (cadObject.getClass().getSimpleName()) {
        case "Appid":
        {
            Appid appid = (Appid)cadObject;
            parseAndPrint("External Ref Block", appid.getExternalRefBlock(), indent);
        }
        break;

        case "AppidControlObj":
        {
            AppidControlObj appidControlObj = (AppidControlObj)cadObject;
            for (Appid appid : appidControlObj.getAppids()) {
                parseAndPrint("Appid", appid, indent);
            }
        }
        break;

        case "Dictionary":
        case "AcdbDictionaryWithDefault":
        {
            Dictionary dictionary = (Dictionary)cadObject;
            for (String key : dictionary.getKeys()) {
                CadObject referencedObject = dictionary.lookupObject(key);
                parseAndPrintPossiblyNull("[" + key + "]", referencedObject, indent);
            }
            if (cadObject instanceof AcdbDictionaryWithDefault) {
                CadObject defaultEntry = ((AcdbDictionaryWithDefault)dictionary).getDefaultEntry();
                parseAndPrint("defaultEntry", defaultEntry, indent);
            }
        }
        break;

        case "Attdef":
        {
            Attdef attdef = (Attdef)cadObject;
            parseAndPrint("Style", attdef.getStyle(), indent);
        }
        break;

        case "Attrib":
        {
            Attrib attrib = (Attrib)cadObject;
            parseAndPrint("Style", attrib.getStyle(), indent);
        }
        break;

        case "BlockControlObj":
        {
            BlockControlObj controlObject = (BlockControlObj)cadObject;
            for (BlockHeader blockHeader : controlObject.getBlockHeaders()) {
                parseAndPrint("Block Header", blockHeader, indent);
            }
            parseAndPrint("Model Space", controlObject.getModelSpace(), indent);
            parseAndPrint("Paper Space", controlObject.getPaperSpace(), indent);
        }
        break;

        case "BlockHeader":
        {
            BlockHeader blockHeader = (BlockHeader)cadObject;
            parseAndPrintPossiblyNull("First Entity", blockHeader.getFirstEntity(), indent);
            parseAndPrintPossiblyNull("Last Entity", blockHeader.getLastEntity(), indent);
            for (CadObject ownedObject : blockHeader.getOwnedObjects()) {
                parseAndPrint("Owned Object", ownedObject, indent);
            }
            parseAndPrint("End Block", blockHeader.getEndBlock(), indent);
            for (Insert insert : blockHeader.getInserts()) {
                parseAndPrint("Insert", insert, indent);
            }
            parseAndPrint("Layout", blockHeader.getLayout(), indent);
        }
        break;

        case "Insert":
        {
            Insert insert = (Insert)cadObject;
            parseAndPrint("Block Header", insert.getBlockHeader(), indent);
            for (CadObject ownedObject : insert.getOwnedObjects()) {
                parseAndPrint("Owned Object", ownedObject, indent);
            }
            parseAndPrintPossiblyNull("Seq End", insert.getSeqEnd(), indent);
        }
        break;

        case "LayerControlObj":
        {
            LayerControlObj layerControlObj = (LayerControlObj)cadObject;
            for (Layer layer : layerControlObj.getLayers()) {
                parseAndPrint("Layer", layer, indent);
            }
        }
        break;

        case "Layer":
        {
            Layer layer = (Layer)cadObject;

            parseAndPrint("External Ref Block", layer.getExternalReferenceBlock(), indent);
            parseAndPrint("Plot Style", layer.getPlotStyle(), indent);
            parseAndPrint("Line Type", layer.getLineType(), indent);
            parseAndPrint("Material", layer.getMaterial(), indent);
        }
        break;

        case "LTypeControlObj":
        {
            LTypeControlObj ltypeControlObj = (LTypeControlObj)cadObject;

            for (LType lineType : ltypeControlObj.getLineTypes()) {
                parseAndPrint("Line Type", lineType, indent);
            }
            parseAndPrint("By-Layer Linetype", ltypeControlObj.getBylayerLinetype(), indent);
            parseAndPrint("By-Block Linetype", ltypeControlObj.getByblockLinetype(), indent);
        }
        break;

        case "LType":
        {
            LType ltype = (LType)cadObject;
            parseAndPrint("External Reference Block", ltype.getExternalReferenceBlock(), indent);
            for (int i = 0; i < ltype.dashes.length; i++) {
                CadObject shapefileForShape = ltype.dashes[i].getShapefileForDash();
                if (shapefileForShape != null) {
                    parseAndPrint("Dash["+i+"]", shapefileForShape, indent);
                }
            }
        }
        break;

        case "Layout":
        {
            Layout layout = (Layout)cadObject;
            parseAndPrintObject("Plot View", layout.getPlotView(), indent);
            parseAndPrintObject("Visual Style", layout.getVisualStyle(), indent);
            parseAndPrintObject("Paperspace Block Record", layout.getPaperspaceBlockRecord(), indent);
            parseAndPrintObject("Last Active Viewport", layout.getLastActiveViewport(), indent);
            parseAndPrintObject("Base UCS", layout.getBaseUcs(), indent);
            parseAndPrintObject("Named UCS", layout.getNamedUcs(), indent);
            for (CadObject viewport : layout.getViewPorts()) {
                parseAndPrintObject("Viewport", viewport, indent);
            }
        }
        break;

        case "MText":
        {
            MText text = (MText)cadObject;
            parseAndPrintObject("Style", text.getStyle(), indent);
        }
        break;

        case "TwoDPolyline":
        {
            TwoDPolyline polyline = (TwoDPolyline)cadObject;
            for (CadObject  ownedObject : polyline.getOwnedObjects()) {
                parseAndPrintObject("Owned Object", ownedObject, indent);
            }
            parseAndPrintPossiblyNull("Seq End", polyline.getSeqEnd(), indent);
        }
        break;

        case "PolylinePFace":
        {
            PolylinePFace polyline = (PolylinePFace)cadObject;
            for (CadObject  ownedObject : polyline.getOwnedObjects()) {
                parseAndPrintObject("Owned Object", ownedObject, indent);
            }
            parseAndPrintPossiblyNull("Seq End", polyline.getSeqEnd(), indent);
        }
        break;

        case "PolylineMesh":
        {
            PolylineMesh polyline = (PolylineMesh)cadObject;
            for (CadObject  ownedObject : polyline.getOwnedObjects()) {
                parseAndPrintObject("Owned Object", ownedObject, indent);
            }
            parseAndPrintPossiblyNull("Seq End", polyline.getSeqEnd(), indent);
        }
        break;

        case "SortEntsTable":
        {
            SortEntsTable sortEntsTable = (SortEntsTable)cadObject;
            for (CadObject sortObject : sortEntsTable.getSortObjects()) {
                parseAndPrintObject("Sort Object", sortObject, indent);
            }
            parseAndPrintObject("Owner", sortEntsTable.getOwner(), indent);
            for (CadObject entity : sortEntsTable.getEntities()) {
                parseAndPrintObject("Entity", entity, indent);
            }
        }
        break;

        default:
            for (CadObject referencedObject : cadObject.getGenericObjects()) {
                parseAndPrintPossiblyNull("Generic", referencedObject, indent);
            }
        }
    }

    private void appendTrace(StringBuffer trace, Object[] components) {
        String separator = "";
        for (Object component : components) {
            trace.append(separator);
            if (component instanceof String) {
                trace.append('"').append(component).append('"');

            } else if (component instanceof Object[]) {
                trace.append('{');
                appendTrace(trace, (Object[])component);
                trace.append('}');

            } else if (component instanceof Handle) {
                trace.append("handle:" + ((Handle)component).offset);

            } else if (component instanceof byte[]) {
                trace.append('(');
                for (byte b : (byte[])component) {
                    trace.append(b).append(' ');
                }
                trace.append(')');

            } else if (component instanceof Point3D) {
                trace.append(component.toString());

            } else if (component instanceof Double) {
                trace.append(component);

            } else if (component instanceof Short) {
                trace.append(component);

            } else if (component instanceof Long) {
                trace.append(component);

            } else {
                throw new RuntimeException("Unexpected case");
            }
            separator = ",";
        }
    }


    private void parseAndPrintPossiblyNull(String handleName, CadObject cadObject, String indent) {
        if (cadObject == null) {
            System.out.println(indent + handleName + ": null");
        } else {
            parseAndPrint(handleName, cadObject, indent);
        }

    }

    private void parseAndPrint(String handleName, CadObject cadObject, String indent) {
        // TODO remove this at some point... above must be called if could be null
        if (cadObject == null) {
            System.out.println(indent + handleName + ": null");
            return;
        }

        if (printed.contains(cadObject)) {
            System.out.println(indent + handleName + ": " + cadObject.toString() + " already printed");
            return;
        }
        printed.add(cadObject);


        System.out.println(indent + handleName + ": " + cadObject.toString() + ":");

        printObject(cadObject, indent + "   ");
    }

    private void parseAndPrintObject(String handleName, CadObject cadObject, String indent) {
        if (cadObject == null) {
            System.out.println(indent + handleName + ": null");
            return;
        }

        if (printed.contains(cadObject)) {
            System.out.println(indent + handleName + ": " + cadObject.toString() + " already printed");
            return;
        }
        printed.add(cadObject);


        System.out.println(indent + handleName + ": " + cadObject.toString() + ":");

        printObject(cadObject, indent + "   ");
    }

}
