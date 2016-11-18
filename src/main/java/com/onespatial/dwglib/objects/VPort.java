package com.onespatial.dwglib.objects;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.CmColor;

public class VPort extends NonEntityObject {

    @Override
	public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
        // 19.4.62 VPORT 65 page 169    

	    // Similar to LTYPE 57
	    
        String entryName = stringStream.getTU();
        
        boolean sixtyFourFlag = dataStream.getB();
//        int xRefOrdinal = dataStream.getBS();
        boolean xDep = dataStream.getB();
      double viewHeight = dataStream.getBD();
      double aspectRatio = dataStream.getBD();
      double viewCenter1 = dataStream.getRD();
      double viewCenter2 = dataStream.getRD();
      double viewTarget1 = dataStream.getBD();
      double viewTarget2 = dataStream.getBD();
      double viewTarget3 = dataStream.getBD();
      double viewDir1 = dataStream.getBD();
      double viewDir2 = dataStream.getBD();
      double viewDir3 = dataStream.getBD();
      double viewTwist = dataStream.getBD();
      double lensLength = dataStream.getBD();
      double frontClip = dataStream.getBD();
      double backClip = dataStream.getBD();
      boolean viewMode0 = dataStream.getB();
      boolean viewMode1 = dataStream.getB();
      boolean viewMode2 = dataStream.getB();
      boolean viewMode3 = dataStream.getB();
      int renderMode = dataStream.getRC();
      boolean useDefaultLights = dataStream.getB();
      int defaultLightingType = dataStream.getRC();
      double brightness = dataStream.getBD();
      double contrast = dataStream.getBD();
      CmColor ambientColor = dataStream.getCMC();
      double lowerLeft1 = dataStream.getRD(); 
      double lowerLeft2 = dataStream.getRD(); 
      double upperRight1 = dataStream.getRD(); 
      double upperRight2 = dataStream.getRD(); 
      boolean UCSFOLLOW = dataStream.getB();
      int circleZoom = dataStream.getBS(); 
      boolean fastZoom = dataStream.getB();
      boolean ucsIcon1 = dataStream.getB();
      boolean ucsIcon2 = dataStream.getB();
      boolean gridFlag = dataStream.getB();
      double gridSpacing1 = dataStream.getRD(); 
      double gridSpacing2 = dataStream.getRD(); 
      boolean snapFlag = dataStream.getB();
      boolean snapStyle = dataStream.getB();
      int snapIsopair = dataStream.getBS(); 
      double snapRot = dataStream.getBD(); 
      double snapBase1 = dataStream.getRD(); 
      double snapBase2 = dataStream.getRD(); 
      double snapSpacing1 = dataStream.getRD(); 
      double snapSpacing2 = dataStream.getRD(); 
      boolean unknown = dataStream.getB();
      boolean ucsPerViewport = dataStream.getB();
      double ucsOrigin1 = dataStream.getBD(); 
      double ucsOrigin2 = dataStream.getBD(); 
      double ucsOrigin3 = dataStream.getBD(); 
      double ucsXAxis1 = dataStream.getBD(); 
      double ucsXAxis2 = dataStream.getBD(); 
      double ucsXAxis3 = dataStream.getBD(); 
      double ucsYAxis1 = dataStream.getBD(); 
      double ucsYAxis2 = dataStream.getBD(); 
      double ucsYAxis3 = dataStream.getBD(); 
      double ucsElevation = dataStream.getBD(); 
      int ucsOrthographicType = dataStream.getBS(); 
      int gridFlags = dataStream.getBS(); 
      int gridMajor = dataStream.getBS(); 
      
      dataStream.assertEndOfStream();
	}

	public String toString() {
		return "VPORT";
	}

}
