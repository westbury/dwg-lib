package com.onespatial.dwglib;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.junit.BeforeClass;
import org.junit.Test;

import objects.AcdbPlaceHolder;
import objects.BlockHeader;
import objects.CadObject;
import objects.Dictionary;
import objects.GenericObject;
import objects.Layer;
import objects.LayerControlObj;
import objects.Layout;
import objects.LwPolyline;
import objects.LwPolyline.VertexOfLwPolyline;
import objects.SortEntsTable;

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
                if (placeHolderReactor instanceof GenericObject
                        && ((GenericObject)placeHolderReactor).objectType.equals("ACDBDICTIONARYWDFLT")) {

                    for (CadObject dictReactor : placeHolderReactor.getReactors()) {
                        if (dictReactor instanceof Dictionary) {
                            Dictionary dict = (Dictionary)dictReactor;
                            Dictionary dict2 = (Dictionary)dict.lookupObject("ACAD_LAYOUT");
                            CadObject model = dict2.lookupObject("Model");
                            Layout l = (Layout)model;
                            
                            BlockHeader blockHeader = l.getPaperspaceBlockRecord();
                            Dictionary dict3 = blockHeader.getXDictionary();
                            SortEntsTable table = (SortEntsTable)dict3.lookupObject("ACAD_SORTENTS");
                            for (CadObject x : table.getEntities()) {
                                if (x instanceof LwPolyline) {
                                    LwPolyline polyline = (LwPolyline)x;
                                    
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
