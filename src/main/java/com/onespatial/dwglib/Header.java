package com.onespatial.dwglib;
/*
 * Copyright (c) 2016, 1Spatial Group Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.BitStreams;
import com.onespatial.dwglib.bitstreams.CmColor;
import com.onespatial.dwglib.bitstreams.Handle;
import com.onespatial.dwglib.bitstreams.HandleType;
import com.onespatial.dwglib.bitstreams.Value;

public class Header
{

    public final Value<Boolean> DIMASO = new Value<>();
    public final Value<Boolean> DIMSHO = new Value<>();
    public final Value<Boolean> PLINEGEN = new Value<>();
    public final Value<Boolean> ORTHOMODE = new Value<>();
    public final Value<Boolean> REGENMODE = new Value<>();
    public final Value<Boolean> FILLMODE = new Value<>();
    public final Value<Boolean> QTEXTMODE = new Value<>();
    public final Value<Boolean> PSLTSCALE = new Value<>();
    public final Value<Boolean> LIMCHECK = new Value<>();
    public final Value<Boolean> Undocumented = new Value<>();
    public final Value<Boolean> USRTIMER = new Value<>();  // User timer on/off
    public final Value<Boolean> SKPOLY = new Value<>();
    public final Value<Boolean> ANGDIR = new Value<>();
    public final Value<Boolean> SPLFRAME = new Value<>();
    public final Value<Boolean> MIRRTEXT = new Value<>();
    public final Value<Boolean> WORLDVIEW = new Value<>();
    public final Value<Boolean> TILEMODE = new Value<>();
    public final Value<Boolean> PLIMCHECK = new Value<>();
    public final Value<Boolean> VISRETAIN = new Value<>();
    public final Value<Boolean> DISPSILH = new Value<>();
    public final Value<Boolean> PELLIPSE = new Value<>();  // not present in DXF
    public final Value<Integer> PROXYGRAPHICS = new Value<>();
    public final Value<Integer> TREEDEPTH = new Value<>();
    public final Value<Integer> LUNITS = new Value<>();
    public final Value<Integer> LUPREC = new Value<>();
    public final Value<Integer> AUNITS = new Value<>();
    public final Value<Integer> AUPREC = new Value<>();
    public final Value<Integer> ATTMODE = new Value<>();
    public final Value<Integer> PDMODE = new Value<>();
    public final Value<Integer> Unknown1 = new Value<>();
    public final Value<Integer> Unknown2 = new Value<>();
    public final Value<Integer> Unknown3 = new Value<>();
    public final Value<Integer> USERI1 = new Value<>();
    public final Value<Integer> USERI2 = new Value<>();
    public final Value<Integer> USERI3 = new Value<>();
    public final Value<Integer> USERI4 = new Value<>();
    public final Value<Integer> USERI5 = new Value<>();
    public final Value<Integer> SPLINESEGS = new Value<>();
    public final Value<Integer> SURFU = new Value<>();
    public final Value<Integer> SURFV = new Value<>();
    public final Value<Integer> SURFTYPE = new Value<>();
    public final Value<Integer> SURFTAB1 = new Value<>();
    public final Value<Integer> SURFTAB2 = new Value<>();
    public final Value<Integer> SPLINETYPE = new Value<>();
    public final Value<Integer> SHADEDGE = new Value<>();
    public final Value<Integer> SHADEDIF = new Value<>();
    public final Value<Integer> UNITMODE = new Value<>();
    public final Value<Integer> MAXACTVP = new Value<>();
    public final Value<Integer> ISOLINES = new Value<>();
    public final Value<Integer> CMLJUST = new Value<>();
    public final Value<Integer> TEXTQLTY = new Value<>();
    public final Value<Double> LTSCALE = new Value<>();
    public final Value<Double> TEXTSIZE = new Value<>();
    public final Value<Double> TRACEWID = new Value<>();
    public final Value<Double> SKETCHINC = new Value<>();
    public final Value<Double> FILLETRAD = new Value<>();
    public final Value<Double> THICKNESS = new Value<>();
    public final Value<Double> ANGBASE = new Value<>();
    public final Value<Double> PDSIZE = new Value<>();
    public final Value<Double> PLINEWID = new Value<>();
    public final Value<Double> USERR1 = new Value<>();
    public final Value<Double> USERR2 = new Value<>();
    public final Value<Double> USERR3 = new Value<>();
    public final Value<Double> USERR4 = new Value<>();
    public final Value<Double> USERR5 = new Value<>();
    public final Value<Double> CHAMFERA = new Value<>();
    public final Value<Double> CHAMFERB = new Value<>();
    public final Value<Double> CHAMFERC = new Value<>();
    public final Value<Double> CHAMFERD = new Value<>();
    public final Value<Double> FACETRES = new Value<>();
    public final Value<Double> CMLSCALE = new Value<>();
    public final Value<Double> CELTSCALE = new Value<>();
    public final Value<Integer> TDCREATEday = new Value<>();
    public final Value<Integer> TDCREATEmilliseconds = new Value<>();
    public final Value<Integer> TDUPDATEday = new Value<>();
    public final Value<Integer> TDUPDATEmilliseconds = new Value<>();
    public final Value<Integer> Unknown4 = new Value<>();
    public final Value<Integer> Unknown5 = new Value<>();
    public final Value<Integer> Unknown6 = new Value<>();
    public final Value<Integer> TDINDWGday = new Value<>();
    public final Value<Integer> TDINDWGmilliseconds = new Value<>();
	public final Value<Integer> TDUSRTIMERday = new Value<>();
    public final Value<Integer> TDUSRTIMERmilliseconds = new Value<>();
	public final Value<CmColor> CECOLOR = new Value<>();
	public final Value<Handle> CLAYER = new Value<>();
	public final Value<Handle> TEXTSTYLE = new Value<>();
	public final Value<Handle> CELTYPE = new Value<>();
	public final Value<Handle> CMATERIAL = new Value<>();
	public final Value<Handle> DIMSTYLE = new Value<>();
	public final Value<Handle> CMLSTYLE = new Value<>();
	public final Value<Handle> HANDSEED = new Value<>();
	public final Value<Double> PSVPSCALE = new Value<>();
	public final SpaceHeader paperSpace;
	public final SpaceHeader modelSpace;
	public final Value<String> DIMPOST = new Value<>();
	public final Value<String> DIMAPOST = new Value<>();
	public final Value<Double> DIMSCALE = new Value<>();
	public final Value<Double> DIMASZ = new Value<>();
	public final Value<Double> DIMEXO = new Value<>();
	public final Value<Double> DIMDLI = new Value<>();
	public final Value<Double> DIMEXE = new Value<>();
	public final Value<Double> DIMRND = new Value<>();
	public final Value<Double> DIMDLE = new Value<>();
	public final Value<Double> DIMTP = new Value<>();
	public final Value<Double> DIMTM = new Value<>();
	public final Value<Double> DIMFXL = new Value<>();
	public final Value<Double> DIMJOGANG = new Value<>();
	public final Value<Integer> DIMTFILL = new Value<>();
	public final Value<CmColor> DIMTFILLCLR = new Value<>();
	public final Value<Boolean> DIMTOL = new Value<>();
	public final Value<Boolean> DIMLIM = new Value<>();
	public final Value<Boolean> DIMTIH = new Value<>();
	public final Value<Boolean> DIMTOH = new Value<>();
	public final Value<Boolean> DIMSE1 = new Value<>();
	public final Value<Boolean> DIMSE2 = new Value<>();
	public final Value<Integer> DIMTAD = new Value<>();
	public final Value<Integer> DIMZIN = new Value<>();
	public final Value<Integer> DIMAZIN = new Value<>();
	public final Value<Integer> DIMARCSYM = new Value<>();
	public final Value<Double> DIMTXT = new Value<>();
	public final Value<Double> DIMCEN = new Value<>();
	public final Value<Double> DIMTSZ = new Value<>();
	public final Value<Double> DIMALTF = new Value<>();
	public final Value<Double> DIMLFAC = new Value<>();
	public final Value<Double> DIMTVP = new Value<>();
	public final Value<Double> DIMTFAC = new Value<>();
	public final Value<Double> DIMGAP = new Value<>();
	public final Value<Double> DIMALTRND = new Value<>();
	public final Value<Boolean> DIMALT = new Value<>();
	public final Value<Integer> DIMALTD = new Value<>();
	public final Value<Boolean> DIMTOFL = new Value<>();
	public final Value<Boolean> DIMSAH = new Value<>();
	public final Value<Boolean> DIMTIX = new Value<>();
	public final Value<Boolean> DIMSOXD = new Value<>();
	public final Value<CmColor> DIMCLRD = new Value<>();
	public final Value<CmColor> DIMCLRE = new Value<>();
	public final Value<CmColor> DIMCLRT = new Value<>();
	public final Value<Integer> DIMADEC = new Value<>();
	public final Value<Integer> DIMDEC = new Value<>();
	public final Value<Integer> DIMTDEC = new Value<>();
	public final Value<Integer> DIMALTU = new Value<>();
	public final Value<Integer> DIMALTTD = new Value<>();
	public final Value<Integer> DIMAUNIT = new Value<>();
	public final Value<Integer> DIMFRAC = new Value<>();
	public final Value<Integer> DIMLUNIT = new Value<>();
	public final Value<Integer> DIMDSEP = new Value<>();
	public final Value<Integer> DIMTMOVE = new Value<>();
	public final Value<Integer> DIMJUST = new Value<>();
	public final Value<Boolean> DIMSD1 = new Value<>();
	public final Value<Boolean> DIMSD2 = new Value<>();
	public final Value<Integer> DIMTOLJ = new Value<>();
	public final Value<Integer> DIMTZIN = new Value<>();
	public final Value<Integer> DIMALTZ = new Value<>();
	public final Value<Integer> DIMALTTZ = new Value<>();
	public final Value<Boolean> DIMUPT = new Value<>();
	public final Value<Integer> DIMATFIT = new Value<>();
	public final Value<Boolean> DIMFXLON = new Value<>();
	public final Value<Boolean> DIMTXTDIRECTION = new Value<>();
	public final Value<Double> DIMALTMZF = new Value<>();
	public final Value<String> DIMALTMZS = new Value<>();
	public final Value<Double> DIMMZF = new Value<>();
	public final Value<String> DIMMZS = new Value<>();
	public final Value<Handle> DIMTXSTY = new Value<>();
	public final Value<Handle> DIMLDRBLK = new Value<>();
	public final Value<Handle> DIMBLK = new Value<>();
	public final Value<Handle> DIMBLK1 = new Value<>();
	public final Value<Handle> DIMBLK2 = new Value<>();
	public final Value<Handle> DIMLTYPE = new Value<>();
	public final Value<Handle> DIMLTEX1 = new Value<>();
	public final Value<Handle> DIMLTEX2 = new Value<>();
	public final Value<Integer> DIMLWD = new Value<>();
	public final Value<Integer> DIMLWE = new Value<>();
	public final Value<Handle> BLOCK_CONTROL_OBJECT = new Value<>();
	public final Value<Handle> LAYER_CONTROL_OBJECT = new Value<>();
	public final Value<Handle> STYLE_CONTROL_OBJECT = new Value<>();
	public final Value<Handle> LINETYPE_CONTROL_OBJECT = new Value<>();
	public final Value<Handle> VIEW_CONTROL_OBJECT = new Value<>();
	public final Value<Handle> UCS_CONTROL_OBJECT = new Value<>();
	public final Value<Handle> VPORT_CONTROL_OBJECT = new Value<>();
	public final Value<Handle> APPID_CONTROL_OBJECT = new Value<>();
	public final Value<Handle> DIMSTYLE_CONTROL_OBJECT = new Value<>();
	public final Value<Handle> DICTIONARY_ACAD_GROUP = new Value<>();
	public final Value<Handle> DICTIONARY_ACAD_MLINESTYLE = new Value<>();
	public final Value<Handle> DICTIONARY_NAMED_OBJECTS = new Value<>();
	public final Value<Integer> TSTACKALIGN = new Value<>();
	public final Value<Integer> TSTACKSIZE = new Value<>();
	public final Value<String> HYPERLINKBASE = new Value<>();
	public final Value<String> STYLESHEET = new Value<>();
	public final Value<Handle> DICTIONARY_LAYOUTS = new Value<>();
	public final Value<Handle> DICTIONARY_PLOTSETTINGS = new Value<>();
	public final Value<Handle> DICTIONARY_PLOTSTYLES = new Value<>();
	public final Value<Handle> DICTIONARY_MATERIALS = new Value<>();
	public final Value<Handle> DICTIONARY_COLORS = new Value<>();
	public final Value<Handle> DICTIONARY_VISUALSTYLE = new Value<>();
	public final Value<Handle> UNKNOWN = new Value<>();
	public final Value<Integer> Flags = new Value<>();
	public final Value<Integer> INSUNITS = new Value<>();
	public final Value<Integer> CEPSNTYPE = new Value<>();
	public final Value<Handle> CPSNID = new Value<>();
	public final Value<String> FINGERPRINTGUID = new Value<>();
	public final Value<String> VERSIONGUID = new Value<>();
	public final Value<Integer> SORTENTS = new Value<>();
	public final Value<Integer> INDEXCTL = new Value<>();
	public final Value<Integer> HIDETEXT = new Value<>();
	public final Value<Integer> XCLIPFRAME = new Value<>();
	public final Value<Integer> DIMASSOC = new Value<>();
	public final Value<Integer> HALOGAP = new Value<>();
	public final Value<Integer> OBSCUREDCOLOR = new Value<>();
	public final Value<Integer> INTERSECTIONCOLOR = new Value<>();
	public final Value<Integer> OBSCUREDLTYPE = new Value<>();
	public final Value<Integer> INTERSECTIONDISPLAY = new Value<>();
	public final Value<String> PROJECTNAME = new Value<>();
	public final Value<Handle> BLOCK_RECORD_PAPER_SPACE = new Value<>();
	public final Value<Handle> BLOCK_RECORD_MODEL_SPACE = new Value<>();
	public final Value<Handle> LTYPE_BYLAYER = new Value<>();
	public final Value<Handle> LTYPE_BYBLOCK = new Value<>();
	public final Value<Handle> LTYPE_CONTINUOUS = new Value<>();
	public final Value<Boolean> CAMERADISPLAY = new Value<>();
	public final Value<Integer> Unknown7 = new Value<>();
	public final Value<Integer> Unknown8 = new Value<>();
	public final Value<Double> Unknown9 = new Value<>();
	public final Value<Double> STEPSPERSEC = new Value<>();
	public final Value<Double> STEPSIZE = new Value<>();
	public final Value<Double> ThreeDDWFPREC = new Value<>();
	public final Value<Double> LENSLENGTH = new Value<>();
	public final Value<Double> CAMERAHEIGHT = new Value<>();
	public final Value<Integer> SOLIDHIST = new Value<>();
	public final Value<Integer> SHOWHIST = new Value<>();
	public final Value<Double> PSOLWIDTH = new Value<>();
	public final Value<Double> PSOLHEIGHT = new Value<>();
	public final Value<Double> LOFTANG1 = new Value<>();
	public final Value<Double> LOFTANG2 = new Value<>();
	public final Value<Double> LOFTMAG1 = new Value<>();
	public final Value<Double> LOFTMAG2 = new Value<>();
	public final Value<Integer> LOFTPARAM = new Value<>();
	public final Value<Integer> LOFTNORMALS = new Value<>();
	public final Value<Double> LATITUDE = new Value<>();
	public final Value<Double> LONGITUDE = new Value<>();
	public final Value<Double> NORTHDIRECTION = new Value<>();
	public final Value<Integer> TIMEZONE = new Value<>();
	public final Value<Integer> LIGHTGLYPHDISPLAY = new Value<>();
	public final Value<Integer> TILEMODELIGHTSYNCH = new Value<>();
	public final Value<Integer> DWFFRAME = new Value<>();
	public final Value<Integer> DGNFRAME = new Value<>();
	public final Value<Boolean> UnknownBit = new Value<>();
	public final Value<CmColor> INTERFERECOLOR = new Value<>();
	public final Value<Handle> INTERFEREOBJVS = new Value<>();
	public final Value<Handle> INTERFEREVPVS = new Value<>();
	public final Value<Handle> DRAGVS = new Value<>();
	public final Value<Integer> CSHADOW = new Value<>();
	public final Value<Double> Unknown10 = new Value<>();
	public final Value<Integer> UnknownShort1 = new Value<>();
	public final Value<Integer> UnknownShort2 = new Value<>();
	public final Value<Integer> UnknownShort3 = new Value<>();
	public final Value<Integer> UnknownShort4 = new Value<>();

    public Header(BitStreams bitStreams, FileVersion version) {
    	
    	BitBuffer bitBuffer = bitStreams.getDataStream();
    	BitBuffer stringStream = bitStreams.getStringStream();
    	BitBuffer handleStream = bitStreams.getHandleStream();

    	// This is a hack to get the strings to line up.  They are otherwise off by one
    	// (for example, see the two GUIDs), and an extra unread string would otherwise exist at
    	// the end of the stream.
    	String unknownString = stringStream.getTU();
        
        if (version.is2013OrLater()) {
            // Read-only
            long REQUIREDVERSIONS = bitBuffer.getBLL();
            
            // It seems we need to read two more bits to get to the BD 412148564080.0 value.  
            // Is this because 3B is incorrectly understood?
            bitBuffer.getB();
            bitBuffer.getB();
        }

        bitBuffer.expectBD(412148564080.0);
        bitBuffer.expectBD(1.0);
        bitBuffer.expectBD(1.0);
        bitBuffer.expectBD(1.0);

        String unknownTextString1 = stringStream.getTU();
        String unknownTextString2 = stringStream.getTU();
        String unknownTextString3 = stringStream.getTU();
        String unknownTextString4 = stringStream.getTU();

        long unknownLong1 = bitBuffer.getBL();  // 0x24
        long unknownLong2 = bitBuffer.getBL();  // 0x00

        bitBuffer.B(DIMASO);
        bitBuffer.B(DIMSHO);
        bitBuffer.B(PLINEGEN);
        bitBuffer.B(ORTHOMODE);
        bitBuffer.B(REGENMODE);
        bitBuffer.B(FILLMODE);
        bitBuffer.B(QTEXTMODE);
        bitBuffer.B(PSLTSCALE);
        bitBuffer.B(LIMCHECK);
        bitBuffer.B(Undocumented);
        bitBuffer.B(USRTIMER);
        bitBuffer.B(SKPOLY);
        bitBuffer.B(ANGDIR);
        bitBuffer.B(SPLFRAME);
        bitBuffer.B(MIRRTEXT);
        bitBuffer.B(WORLDVIEW);
        bitBuffer.B(TILEMODE);
        bitBuffer.B(PLIMCHECK);
        bitBuffer.B(VISRETAIN);
        bitBuffer.B(DISPSILH);
        bitBuffer.B(PELLIPSE);
        bitBuffer.BS(PROXYGRAPHICS);
        bitBuffer.BS(TREEDEPTH);
        bitBuffer.BS(LUNITS);
        bitBuffer.BS(LUPREC);
        bitBuffer.BS(AUNITS);
        bitBuffer.BS(AUPREC);
        bitBuffer.BS(ATTMODE);
        bitBuffer.BS(PDMODE);
        bitBuffer.BL(Unknown1);
        bitBuffer.BL(Unknown2);
        bitBuffer.BL(Unknown3);
        bitBuffer.BS(USERI1);
        bitBuffer.BS(USERI2);
        bitBuffer.BS(USERI3);
        bitBuffer.BS(USERI4);
        bitBuffer.BS(USERI5);
        bitBuffer.BS(SPLINESEGS);
        bitBuffer.BS(SURFU);
        bitBuffer.BS(SURFV);
        bitBuffer.BS(SURFTYPE);
        bitBuffer.BS(SURFTAB1);
        bitBuffer.BS(SURFTAB2);
        bitBuffer.BS(SPLINETYPE);
        bitBuffer.BS(SHADEDGE);
        bitBuffer.BS(SHADEDIF);
        bitBuffer.BS(UNITMODE);
        bitBuffer.BS(MAXACTVP);
        bitBuffer.BS(ISOLINES);
        bitBuffer.BS(CMLJUST);
        bitBuffer.BS(TEXTQLTY);
        bitBuffer.BD(LTSCALE);
        bitBuffer.BD(TEXTSIZE);
        bitBuffer.BD(TRACEWID);
        bitBuffer.BD(SKETCHINC);
        bitBuffer.BD(FILLETRAD);
        bitBuffer.BD(THICKNESS);
        bitBuffer.BD(ANGBASE);
        bitBuffer.BD(PDSIZE);
        bitBuffer.BD(PLINEWID);
        bitBuffer.BD(USERR1);
        bitBuffer.BD(USERR2);
        bitBuffer.BD(USERR3);
        bitBuffer.BD(USERR4);
        bitBuffer.BD(USERR5);
        bitBuffer.BD(CHAMFERA);
        bitBuffer.BD(CHAMFERB);
        bitBuffer.BD(CHAMFERC);
        bitBuffer.BD(CHAMFERD);
        bitBuffer.BD(FACETRES);
        bitBuffer.BD(CMLSCALE);
        bitBuffer.BD(CELTSCALE);
        bitBuffer.BL(TDCREATEday);
        bitBuffer.BL(TDCREATEmilliseconds);
        bitBuffer.BL(TDUPDATEday);
        bitBuffer.BL(TDUPDATEmilliseconds);
        bitBuffer.BL(Unknown4);
        bitBuffer.BL(Unknown5);
        bitBuffer.BL(Unknown6);
        bitBuffer.BL(TDINDWGday);
        bitBuffer.BL(TDINDWGmilliseconds);
        bitBuffer.BL(TDUSRTIMERday);
        bitBuffer.BL(TDUSRTIMERmilliseconds);
        bitBuffer.CMC(CECOLOR);
        bitBuffer.H(HANDSEED); // The next handle, with an 8-bit length specifier preceding the handle bytes (standard hex handle form) (code 0). The HANDSEED is not part of the handle stream, but of the normal data stream (relevant for R21 and later).
        handleStream.H(CLAYER, HandleType.HARD_POINTER);
        handleStream.H(TEXTSTYLE, HandleType.HARD_POINTER);
        handleStream.H(CELTYPE, HandleType.HARD_POINTER);
        handleStream.H(CMATERIAL, HandleType.HARD_POINTER);
        handleStream.H(DIMSTYLE, HandleType.HARD_POINTER);
        handleStream.H(CMLSTYLE, HandleType.HARD_POINTER);
        bitBuffer.BD(PSVPSCALE);

        paperSpace = new SpaceHeader(bitBuffer, handleStream);
        modelSpace = new SpaceHeader(bitBuffer, handleStream);
        
        stringStream.TU(DIMPOST);
        stringStream.TU(DIMAPOST);
        bitBuffer.BD(DIMSCALE);
        bitBuffer.BD(DIMASZ);
        bitBuffer.BD(DIMEXO);
        bitBuffer.BD(DIMDLI);
        bitBuffer.BD(DIMEXE);
        bitBuffer.BD(DIMRND);
        bitBuffer.BD(DIMDLE);
        bitBuffer.BD(DIMTP);
        bitBuffer.BD(DIMTM);
        bitBuffer.BD(DIMFXL);
        bitBuffer.BD(DIMJOGANG);
        bitBuffer.BS(DIMTFILL);
        bitBuffer.CMC(DIMTFILLCLR);
        bitBuffer.B(DIMTOL);
        bitBuffer.B(DIMLIM);
        bitBuffer.B(DIMTIH);
        bitBuffer.B(DIMTOH);
        bitBuffer.B(DIMSE1);
        bitBuffer.B(DIMSE2);
        bitBuffer.BS(DIMTAD);
        bitBuffer.BS(DIMZIN);
        bitBuffer.BS(DIMAZIN);
        bitBuffer.BS(DIMARCSYM);
        bitBuffer.BD(DIMTXT);
        bitBuffer.BD(DIMCEN);
        bitBuffer.BD(DIMTSZ);
        bitBuffer.BD(DIMALTF);
        bitBuffer.BD(DIMLFAC);
        bitBuffer.BD(DIMTVP);
        bitBuffer.BD(DIMTFAC);
        bitBuffer.BD(DIMGAP);
        bitBuffer.BD(DIMALTRND);
        bitBuffer.B(DIMALT);
        bitBuffer.BS(DIMALTD);
        bitBuffer.B(DIMTOFL);
        bitBuffer.B(DIMSAH);
        bitBuffer.B(DIMTIX);
        bitBuffer.B(DIMSOXD);
        bitBuffer.CMC(DIMCLRD);
        bitBuffer.CMC(DIMCLRE);
        bitBuffer.CMC(DIMCLRT);
        bitBuffer.BS(DIMADEC);
        bitBuffer.BS(DIMDEC);
        bitBuffer.BS(DIMTDEC);
        bitBuffer.BS(DIMALTU);
        bitBuffer.BS(DIMALTTD);
        bitBuffer.BS(DIMAUNIT);
        bitBuffer.BS(DIMFRAC);
        bitBuffer.BS(DIMLUNIT);
        bitBuffer.BS(DIMDSEP);
        bitBuffer.BS(DIMTMOVE);
        bitBuffer.BS(DIMJUST);
        bitBuffer.B(DIMSD1);
        bitBuffer.B(DIMSD2);
        bitBuffer.BS(DIMTOLJ);
        bitBuffer.BS(DIMTZIN);
        bitBuffer.BS(DIMALTZ);
        bitBuffer.BS(DIMALTTZ);
        bitBuffer.B(DIMUPT);
        bitBuffer.BS(DIMATFIT);
        bitBuffer.B(DIMFXLON);
        bitBuffer.B(DIMTXTDIRECTION);
        bitBuffer.BD(DIMALTMZF);
        stringStream.TU(DIMALTMZS);
        bitBuffer.BD(DIMMZF);
        stringStream.TU(DIMMZS);
        handleStream.H(DIMTXSTY, HandleType.HARD_POINTER);
        handleStream.H(DIMLDRBLK, HandleType.HARD_POINTER);
        handleStream.H(DIMBLK, HandleType.HARD_POINTER);
        handleStream.H(DIMBLK1, HandleType.HARD_POINTER);
        handleStream.H(DIMBLK2, HandleType.HARD_POINTER);
        handleStream.H(DIMLTYPE, HandleType.HARD_POINTER);
        handleStream.H(DIMLTEX1, HandleType.HARD_POINTER);
        handleStream.H(DIMLTEX2, HandleType.HARD_POINTER);
        bitBuffer.BS(DIMLWD);
        bitBuffer.BS(DIMLWE);
        handleStream.H(BLOCK_CONTROL_OBJECT, HandleType.HARD_OWNER);
        handleStream.H(LAYER_CONTROL_OBJECT, HandleType.HARD_OWNER);
        handleStream.H(STYLE_CONTROL_OBJECT, HandleType.HARD_OWNER);
        handleStream.H(LINETYPE_CONTROL_OBJECT, HandleType.HARD_OWNER);
        handleStream.H(VIEW_CONTROL_OBJECT, HandleType.HARD_OWNER);
        handleStream.H(UCS_CONTROL_OBJECT, HandleType.HARD_OWNER);
        handleStream.H(VPORT_CONTROL_OBJECT, HandleType.HARD_OWNER);
        handleStream.H(APPID_CONTROL_OBJECT, HandleType.HARD_OWNER);
        handleStream.H(DIMSTYLE_CONTROL_OBJECT, HandleType.HARD_OWNER);
        handleStream.H(DICTIONARY_ACAD_GROUP, HandleType.HARD_POINTER);
        handleStream.H(DICTIONARY_ACAD_MLINESTYLE, HandleType.HARD_POINTER);
        handleStream.H(DICTIONARY_NAMED_OBJECTS, HandleType.HARD_OWNER);
        bitBuffer.BS(TSTACKALIGN); // default = 1 (not present in DXF)
        bitBuffer.BS(TSTACKSIZE); // default = 70 (not present in DXF)
        stringStream.TU(HYPERLINKBASE);
        stringStream.TU(STYLESHEET);
        handleStream.H(DICTIONARY_LAYOUTS, HandleType.HARD_POINTER);
        handleStream.H(DICTIONARY_PLOTSETTINGS, HandleType.HARD_POINTER);
        handleStream.H(DICTIONARY_PLOTSTYLES, HandleType.HARD_POINTER);
        handleStream.H(DICTIONARY_MATERIALS, HandleType.HARD_POINTER);
        handleStream.H(DICTIONARY_COLORS, HandleType.HARD_POINTER);
        handleStream.H(DICTIONARY_VISUALSTYLE, HandleType.HARD_POINTER);

        if (version.is2013OrLater()) {
        	handleStream.H(UNKNOWN, HandleType.HARD_POINTER);
        }

        bitBuffer.BL(Flags);
//            CELWEIGHT Flags & 0x001F
//            ENDCAPS Flags & 0x0060
//            JOINSTYLE Flags & 0x0180
//            LWDISPLAY !(Flags & 0x0200)
//            XEDIT !(Flags & 0x0400)
//            EXTNAMES Flags & 0x0800
//            PSTYLEMODE Flags & 0x2000
//            OLESTARTUP Flags & 0x4000

        	bitBuffer.BS(INSUNITS);
            bitBuffer.BS(CEPSNTYPE);
            if (CEPSNTYPE.get() == 3) {
            	handleStream.H(CPSNID, HandleType.HARD_POINTER); // present only if CEPSNTYPE == 3
            }
            stringStream.TU(FINGERPRINTGUID);
            stringStream.TU(VERSIONGUID);
            bitBuffer.RC(SORTENTS);
            bitBuffer.RC(INDEXCTL);
            bitBuffer.RC(HIDETEXT);
            bitBuffer.RC(XCLIPFRAME); // before R2010 the value can be 0 or 1 only.
            bitBuffer.RC(DIMASSOC);
            bitBuffer.RC(HALOGAP);
            bitBuffer.BS(OBSCUREDCOLOR);
            bitBuffer.BS(INTERSECTIONCOLOR);
            bitBuffer.RC(OBSCUREDLTYPE);
            bitBuffer.RC(INTERSECTIONDISPLAY);
            stringStream.TU(PROJECTNAME);
            handleStream.H(BLOCK_RECORD_PAPER_SPACE, HandleType.HARD_POINTER);
            handleStream.H(BLOCK_RECORD_MODEL_SPACE, HandleType.HARD_POINTER);
            handleStream.H(LTYPE_BYLAYER, HandleType.HARD_POINTER);
            handleStream.H(LTYPE_BYBLOCK, HandleType.HARD_POINTER);
            handleStream.H(LTYPE_CONTINUOUS, HandleType.HARD_POINTER);
            bitBuffer.B(CAMERADISPLAY);
            bitBuffer.BL(Unknown7);
            bitBuffer.BL(Unknown8);
            bitBuffer.BD(Unknown9);
            bitBuffer.BD(STEPSPERSEC);
            bitBuffer.BD(STEPSIZE);
            bitBuffer.BD(ThreeDDWFPREC);
            bitBuffer.BD(LENSLENGTH);
            bitBuffer.BD(CAMERAHEIGHT);
            bitBuffer.RC(SOLIDHIST);
            bitBuffer.RC(SHOWHIST);
            bitBuffer.BD(PSOLWIDTH);
            bitBuffer.BD(PSOLHEIGHT);
            bitBuffer.BD(LOFTANG1);
            bitBuffer.BD(LOFTANG2);
            bitBuffer.BD(LOFTMAG1);
            bitBuffer.BD(LOFTMAG2);
            bitBuffer.BS(LOFTPARAM);
            bitBuffer.RC(LOFTNORMALS);
            bitBuffer.BD(LATITUDE);
            bitBuffer.BD(LONGITUDE);
            bitBuffer.BD(NORTHDIRECTION);
            bitBuffer.BL(TIMEZONE);
            bitBuffer.RC(LIGHTGLYPHDISPLAY);
            bitBuffer.RC(TILEMODELIGHTSYNCH);
            bitBuffer.RC(DWFFRAME);
            bitBuffer.RC(DGNFRAME);
            bitBuffer.B(UnknownBit);
            bitBuffer.CMC(INTERFERECOLOR);
            handleStream.H(INTERFEREOBJVS, HandleType.HARD_POINTER);
            handleStream.H(INTERFEREVPVS, HandleType.HARD_POINTER);
            handleStream.H(DRAGVS, HandleType.HARD_POINTER);
            bitBuffer.RC(CSHADOW);
            bitBuffer.BD(Unknown10);
            
            bitBuffer.assertEndOfStream();
            
            // So where are these??????
//            bitBuffer.BS(UnknownShort1); // unknown short (type 5/6 only) these do not seem to be required,
//            bitBuffer.BS(UnknownShort2); // unknown short (type 5/6 only) even for type 5.
//            bitBuffer.BS(UnknownShort3); // unknown short (type 5/6 only)
//            bitBuffer.BS(UnknownShort4); // unknown short (type 5/6 only)
//            int CRC = bitBuffer.getRS(); //for the data section, starting after the sentinel. Use 0xC0C1 for the initial

    }

}
