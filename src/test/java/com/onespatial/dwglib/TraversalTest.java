package com.onespatial.dwglib;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import com.onespatial.dwglib.bitstreams.Handle;
import com.onespatial.dwglib.bitstreams.Point3D;
import com.onespatial.dwglib.objects.AcdbDictionaryWithDefault;
import com.onespatial.dwglib.objects.AcdbPlaceHolder;
import com.onespatial.dwglib.objects.Appid;
import com.onespatial.dwglib.objects.AppidControlObj;
import com.onespatial.dwglib.objects.Attdef;
import com.onespatial.dwglib.objects.Attrib;
import com.onespatial.dwglib.objects.BlockHeader;
import com.onespatial.dwglib.objects.CadObject;
import com.onespatial.dwglib.objects.Dictionary;
import com.onespatial.dwglib.objects.EntityObject;
import com.onespatial.dwglib.objects.LType;
import com.onespatial.dwglib.objects.LTypeControlObj;
import com.onespatial.dwglib.objects.Layer;
import com.onespatial.dwglib.objects.LayerControlObj;
import com.onespatial.dwglib.objects.Layout;
import com.onespatial.dwglib.objects.LwPolyline;
import com.onespatial.dwglib.objects.LwPolyline.VertexOfLwPolyline;
import com.onespatial.dwglib.objects.SortEntsTable;

public class TraversalTest {

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
            try (
                    ReadableByteChannel inputChannel = Channels.newChannel(urlToTestFile.openStream());
                    FileOutputStream outputStream = new FileOutputStream(testFile);
                    ) {
                outputStream.getChannel().transferFrom(inputChannel, 0, Long.MAX_VALUE);
            }
        }
    }

    @Test
    public void traversalTest() throws Exception {
        Reader reader = new Reader(testFile);

        LayerControlObj layerControl = reader.getLayerControlObject();

        for (Layer layer : layerControl.getLayers()) {
            AcdbPlaceHolder placeHolder = layer.getPlotStyle();

            for (CadObject placeHolderReactor : placeHolder.getReactors()) {
                if (placeHolderReactor instanceof AcdbDictionaryWithDefault) {

                    for (CadObject dictReactor : placeHolderReactor.getReactors()) {
                        if (dictReactor instanceof Dictionary) {
                            Dictionary dict = (Dictionary)dictReactor;
                            Dictionary dict2 = (Dictionary)dict.lookupObject("ACAD_LAYOUT");
                            CadObject model = dict2.lookupObject("Model");
                            Layout layout = (Layout)model;

                            BlockHeader blockHeader = layout.getPaperspaceBlockRecord();
                            Dictionary dict3 = blockHeader.getXDictionary();
                            if (dict3 != null) {
                                SortEntsTable table = (SortEntsTable)dict3.lookupObject("ACAD_SORTENTS");
                                for (CadObject entity : table.getEntities()) {
                                    if (entity instanceof LwPolyline) {
                                        LwPolyline polyline = (LwPolyline)entity;

                                        System.out.print("    LwPolyline: ");
                                        for (VertexOfLwPolyline point : polyline.points) {
                                            System.out.print(" " + point.vertex);
                                        }
                                        System.out.println("");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void traverseEverythingTest() throws Exception {
        Reader reader = new Reader(testFile);

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

    Set<CadObject> printed = new HashSet<>();

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

        case "BlockHeader":
        {
            BlockHeader blockHeader = (BlockHeader)cadObject;
            parseAndPrintPossiblyNull("First Entity", blockHeader.getFirstEntity(), indent);
            parseAndPrintPossiblyNull("Last Entity", blockHeader.getLastEntity(), indent);
            for (CadObject ownedObject : blockHeader.getOwnedObjects()) {
                parseAndPrint("Owned Object", ownedObject, indent);
            }
            parseAndPrint("End Block", blockHeader.getEndBlock(), indent);
            for (CadObject insert : blockHeader.getInserts()) {
                parseAndPrint("Insert", insert, indent);
            }
            parseAndPrint("Layout", blockHeader.getLayout(), indent);
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
                CadObject shapefileForDash = ltype.dashes[i].getShapefileForDash();
                if (shapefileForDash != null) {
                    parseAndPrint("Dash["+i+"](dash)", shapefileForDash, indent);
                }
                CadObject shapefileForShape = ltype.dashes[i].getShapefileForShape();
                if (shapefileForShape != null) {
                    parseAndPrint("Dash["+i+"](shape)", shapefileForShape, indent);
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
