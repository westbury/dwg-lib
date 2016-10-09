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

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.JulianFields;

public class Header
{

    private Value<Boolean> DIMASO = new Value<>();
    private Value<Boolean> DIMSHO = new Value<>();
    private Value<Boolean> PLINEGEN = new Value<>();
    private Value<Boolean> ORTHOMODE = new Value<>();
    private Value<Boolean> REGENMODE = new Value<>();
    private Value<Boolean> FILLMODE = new Value<>();
    private Value<Boolean> QTEXTMODE = new Value<>();
    private Value<Boolean> PSLTSCALE = new Value<>();
    private Value<Boolean> LIMCHECK = new Value<>();
    private Value<Boolean> Undocumented = new Value<>();
    private Value<Boolean> USRTIMER = new Value<>();  // User timer on/off
    private Value<Boolean> SKPOLY = new Value<>();
    private Value<Boolean> ANGDIR = new Value<>();
    private Value<Boolean> SPLFRAME = new Value<>();
    private Value<Boolean> MIRRTEXT = new Value<>();
    private Value<Boolean> WORLDVIEW = new Value<>();
    private Value<Boolean> TILEMODE = new Value<>();
    private Value<Boolean> PLIMCHECK = new Value<>();
    private Value<Boolean> VISRETAIN = new Value<>();
    private Value<Boolean> DISPSILH = new Value<>();
    private Value<Boolean> PELLIPSE = new Value<>();  // not present in DXF
    private Value<Integer> PROXYGRAPHICS = new Value<>();
    private Value<Integer> TREEDEPTH = new Value<>();
    private Value<Integer> LUNITS = new Value<>();
    private Value<Integer> LUPREC = new Value<>();
    private Value<Integer> AUNITS = new Value<>();
    private Value<Integer> AUPREC = new Value<>();
    private Value<Integer> ATTMODE = new Value<>();
    private Value<Integer> PDMODE = new Value<>();
    private Value<Integer> Unknown1 = new Value<>();
    private Value<Integer> Unknown2 = new Value<>();
    private Value<Integer> Unknown3 = new Value<>();
    private Value<Integer> USERI1 = new Value<>();
    private Value<Integer> USERI2 = new Value<>();
    private Value<Integer> USERI3 = new Value<>();
    private Value<Integer> USERI4 = new Value<>();
    private Value<Integer> USERI5 = new Value<>();
    private Value<Integer> SPLINESEGS = new Value<>();
    private Value<Integer> SURFU = new Value<>();
    private Value<Integer> SURFV = new Value<>();
    private Value<Integer> SURFTYPE = new Value<>();
    private Value<Integer> SURFTAB1 = new Value<>();
    private Value<Integer> SURFTAB2 = new Value<>();
    private Value<Integer> SPLINETYPE = new Value<>();
    private Value<Integer> SHADEDGE = new Value<>();
    private Value<Integer> SHADEDIF = new Value<>();
    private Value<Integer> UNITMODE = new Value<>();
    private Value<Integer> MAXACTVP = new Value<>();
    private Value<Integer> ISOLINES = new Value<>();
    private Value<Integer> CMLJUST = new Value<>();
    private Value<Integer> TEXTQLTY = new Value<>();
    private Value<Double> LTSCALE = new Value<>();
    private Value<Double> TEXTSIZE = new Value<>();
    private Value<Double> TRACEWID = new Value<>();
    private Value<Double> SKETCHINC = new Value<>();
    private Value<Double> FILLETRAD = new Value<>();
    private Value<Double> THICKNESS = new Value<>();
    private Value<Double> ANGBASE = new Value<>();
    private Value<Double> PDSIZE = new Value<>();
    private Value<Double> PLINEWID = new Value<>();
    private Value<Double> USERR1 = new Value<>();
    private Value<Double> USERR2 = new Value<>();
    private Value<Double> USERR3 = new Value<>();
    private Value<Double> USERR4 = new Value<>();
    private Value<Double> USERR5 = new Value<>();
    private Value<Double> CHAMFERA = new Value<>();
    private Value<Double> CHAMFERB = new Value<>();
    private Value<Double> CHAMFERC = new Value<>();
    private Value<Double> CHAMFERD = new Value<>();
    private Value<Double> FACETRES = new Value<>();
    private Value<Double> CMLSCALE = new Value<>();
    private Value<Double> CELTSCALE = new Value<>();
    private Value<LocalDateTime> TDCREATE = new Value<>();
    private Value<LocalDateTime> TDUPDATE = new Value<>();
    private Value<Integer> Unknown4 = new Value<>();
    private Value<Integer> Unknown5 = new Value<>();
    private Value<Integer> Unknown6 = new Value<>();
    private Value<Duration> TDINDWG = new Value<>();
	private Value<Duration> TDUSRTIMER = new Value<>();
	private Value<CmColor> CECOLOR = new Value<>();
	private Value<Handle> CLAYER = new Value<>();
	private Value<Handle> TEXTSTYLE = new Value<>();
	private Value<Handle> CELTYPE = new Value<>();
	private Value<Handle> CMATERIAL = new Value<>();
	private Value<Handle> DIMSTYLE = new Value<>();
	private Value<Handle> CMLSTYLE = new Value<>();
	private Value<Handle> HANDSEED = new Value<>();
	private Value<Double> PSVPSCALE = new Value<>();
	private SpaceHeader paperSpace;
	private SpaceHeader modelSpace;
	private Value<String> DIMPOST = new Value<>();
	private Value<String> DIMAPOST = new Value<>();
	private Value<Double> DIMSCALE = new Value<>();
	private Value<Double> DIMASZ = new Value<>();
	private Value<Double> DIMEXO = new Value<>();
	private Value<Double> DIMDLI = new Value<>();
	private Value<Double> DIMEXE = new Value<>();
	private Value<Double> DIMRND = new Value<>();
	private Value<Double> DIMDLE = new Value<>();
	private Value<Double> DIMTP = new Value<>();
	private Value<Double> DIMTM = new Value<>();
	private Value<Double> DIMFXL = new Value<>();
	private Value<Double> DIMJOGANG = new Value<>();
	private Value<Integer> DIMTFILL = new Value<>();
	private Value<CmColor> DIMTFILLCLR = new Value<>();
	private Value<Boolean> DIMTOL = new Value<>();
	private Value<Boolean> DIMLIM = new Value<>();
	private Value<Boolean> DIMTIH = new Value<>();
	private Value<Boolean> DIMTOH = new Value<>();
	private Value<Boolean> DIMSE1 = new Value<>();
	private Value<Boolean> DIMSE2 = new Value<>();
	private Value<Integer> DIMTAD = new Value<>();
	private Value<Integer> DIMZIN = new Value<>();
	private Value<Integer> DIMAZIN = new Value<>();
	private Value<Integer> DIMARCSYM = new Value<>();
	private Value<Double> DIMTXT = new Value<>();
	private Value<Double> DIMCEN = new Value<>();
	private Value<Double> DIMTSZ = new Value<>();
	private Value<Double> DIMALTF = new Value<>();
	private Value<Double> DIMLFAC = new Value<>();
	private Value<Double> DIMTVP = new Value<>();
	private Value<Double> DIMTFAC = new Value<>();
	private Value<Double> DIMGAP = new Value<>();
	private Value<Double> DIMALTRND = new Value<>();
	private Value<Boolean> DIMALT = new Value<>();
	private Value<Integer> DIMALTD = new Value<>();
	private Value<Boolean> DIMTOFL = new Value<>();
	private Value<Boolean> DIMSAH = new Value<>();
	private Value<Boolean> DIMTIX = new Value<>();
	private Value<Boolean> DIMSOXD = new Value<>();
	private Value<CmColor> DIMCLRD = new Value<>();
	private Value<CmColor> DIMCLRE = new Value<>();
	private Value<CmColor> DIMCLRT = new Value<>();
	private Value<Integer> DIMADEC = new Value<>();
	private Value<Integer> DIMDEC = new Value<>();
	private Value<Integer> DIMTDEC = new Value<>();
	private Value<Integer> DIMALTU = new Value<>();
	private Value<Integer> DIMALTTD = new Value<>();
	private Value<Integer> DIMAUNIT = new Value<>();
	private Value<Integer> DIMFRAC = new Value<>();
	private Value<Integer> DIMLUNIT = new Value<>();
	private Value<Integer> DIMDSEP = new Value<>();
	private Value<Integer> DIMTMOVE = new Value<>();
	private Value<Integer> DIMJUST = new Value<>();
	private Value<Boolean> DIMSD1 = new Value<>();
	private Value<Boolean> DIMSD2 = new Value<>();
	private Value<Integer> DIMTOLJ = new Value<>();
	private Value<Integer> DIMTZIN = new Value<>();
	private Value<Integer> DIMALTZ = new Value<>();
	private Value<Integer> DIMALTTZ = new Value<>();
	private Value<Boolean> DIMUPT = new Value<>();
	private Value<Integer> DIMATFIT = new Value<>();
	private Value<Boolean> DIMFXLON = new Value<>();
	private Value<Boolean> DIMTXTDIRECTION = new Value<>();
	private Value<Double> DIMALTMZF = new Value<>();
	private Value<String> DIMALTMZS = new Value<>();
	private Value<Double> DIMMZF = new Value<>();
	private Value<String> DIMMZS = new Value<>();
	private Value<Handle> DIMTXSTY = new Value<>();
	private Value<Handle> DIMLDRBLK = new Value<>();
	private Value<Handle> DIMBLK = new Value<>();
	private Value<Handle> DIMBLK1 = new Value<>();
	private Value<Handle> DIMBLK2 = new Value<>();
	private Value<Handle> DIMLTYPE = new Value<>();
	private Value<Handle> DIMLTEX1 = new Value<>();
	private Value<Handle> DIMLTEX2 = new Value<>();
	private Value<Integer> DIMLWD = new Value<>();
	private Value<Integer> DIMLWE = new Value<>();
	private Value<Handle> BLOCK_CONTROL_OBJECT = new Value<>();
	private Value<Handle> LAYER_CONTROL_OBJECT = new Value<>();
	private Value<Handle> STYLE_CONTROL_OBJECT = new Value<>();
	private Value<Handle> LINETYPE_CONTROL_OBJECT = new Value<>();
	private Value<Handle> VIEW_CONTROL_OBJECT = new Value<>();
	private Value<Handle> UCS_CONTROL_OBJECT = new Value<>();
	private Value<Handle> VPORT_CONTROL_OBJECT = new Value<>();
	private Value<Handle> APPID_CONTROL_OBJECT = new Value<>();
	private Value<Handle> DIMSTYLE_CONTROL_OBJECT = new Value<>();
	private Value<Handle> DICTIONARY_ACAD_GROUP = new Value<>();
	private Value<Handle> DICTIONARY_ACAD_MLINESTYLE = new Value<>();
	private Value<Handle> DICTIONARY_NAMED_OBJECTS = new Value<>();
	private Value<Integer> TSTACKALIGN = new Value<>();
	private Value<Integer> TSTACKSIZE = new Value<>();
	private Value<String> HYPERLINKBASE = new Value<>();
	private Value<String> STYLESHEET = new Value<>();
	private Value<Handle> DICTIONARY_LAYOUTS = new Value<>();
	private Value<Handle> DICTIONARY_PLOTSETTINGS = new Value<>();
	private Value<Handle> DICTIONARY_PLOTSTYLES = new Value<>();
	private Value<Handle> DICTIONARY_MATERIALS = new Value<>();
	private Value<Handle> DICTIONARY_COLORS = new Value<>();
	private Value<Handle> DICTIONARY_VISUALSTYLE = new Value<>();
	private Value<Handle> UNKNOWN = new Value<>();
	private Value<Integer> Flags = new Value<>();
	private Value<Integer> INSUNITS = new Value<>();
	private Value<Integer> CEPSNTYPE = new Value<>();
	private Value<Handle> CPSNID = new Value<>();
	private Value<String> FINGERPRINTGUID = new Value<>();
	private Value<String> VERSIONGUID = new Value<>();
	private Value<Integer> SORTENTS = new Value<>();
	private Value<Integer> INDEXCTL = new Value<>();
	private Value<Integer> HIDETEXT = new Value<>();
	private Value<Integer> XCLIPFRAME = new Value<>();
	private Value<Integer> DIMASSOC = new Value<>();
	private Value<Integer> HALOGAP = new Value<>();
	private Value<Integer> OBSCUREDCOLOR = new Value<>();
	private Value<Integer> INTERSECTIONCOLOR = new Value<>();
	private Value<Integer> OBSCUREDLTYPE = new Value<>();
	private Value<Integer> INTERSECTIONDISPLAY = new Value<>();
	private Value<String> PROJECTNAME = new Value<>();
	private Value<Handle> BLOCK_RECORD_PAPER_SPACE = new Value<>();
	private Value<Handle> BLOCK_RECORD_MODEL_SPACE = new Value<>();
	private Value<Handle> LTYPE_BYLAYER = new Value<>();
	private Value<Handle> LTYPE_BYBLOCK = new Value<>();
	private Value<Handle> LTYPE_CONTINUOUS = new Value<>();
	private Value<Boolean> CAMERADISPLAY = new Value<>();
	private Value<Integer> Unknown7 = new Value<>();
	private Value<Integer> Unknown8 = new Value<>();
	private Value<Double> Unknown9 = new Value<>();
	private Value<Double> STEPSPERSEC = new Value<>();
	private Value<Double> STEPSIZE = new Value<>();
	private Value<Double> ThreeDDWFPREC = new Value<>();
	private Value<Double> LENSLENGTH = new Value<>();
	private Value<Double> CAMERAHEIGHT = new Value<>();
	private Value<Integer> SOLIDHIST = new Value<>();
	private Value<Integer> SHOWHIST = new Value<>();
	private Value<Double> PSOLWIDTH = new Value<>();
	private Value<Double> PSOLHEIGHT = new Value<>();
	private Value<Double> LOFTANG1 = new Value<>();
	private Value<Double> LOFTANG2 = new Value<>();
	private Value<Double> LOFTMAG1 = new Value<>();
	private Value<Double> LOFTMAG2 = new Value<>();
	private Value<Integer> LOFTPARAM = new Value<>();
	private Value<Integer> LOFTNORMALS = new Value<>();
	private Value<Double> LATITUDE = new Value<>();
	private Value<Double> LONGITUDE = new Value<>();
	private Value<Double> NORTHDIRECTION = new Value<>();
	private Value<Integer> TIMEZONE = new Value<>();
	private Value<Integer> LIGHTGLYPHDISPLAY = new Value<>();
	private Value<Integer> TILEMODELIGHTSYNCH = new Value<>();
	private Value<Integer> DWFFRAME = new Value<>();
	private Value<Integer> DGNFRAME = new Value<>();
	private Value<Boolean> UnknownBit = new Value<>();
	private Value<CmColor> INTERFERECOLOR = new Value<>();
	private Value<Handle> INTERFEREOBJVS = new Value<>();
	private Value<Handle> INTERFEREVPVS = new Value<>();
	private Value<Handle> DRAGVS = new Value<>();
	private Value<Integer> CSHADOW = new Value<>();
	private Value<Double> Unknown10 = new Value<>();
	private Value<Integer> UnknownShort1 = new Value<>();
	private Value<Integer> UnknownShort2 = new Value<>();
	private Value<Integer> UnknownShort3 = new Value<>();
	private Value<Integer> UnknownShort4 = new Value<>();

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
        }


        
        // This is where the 412148564080.0 bits were actually found in a bit
        // scan.  This does not appear to agree with the specification so further
        // work is no doubt 
        bitBuffer.position(224);

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
        bitBuffer.localDateTime(TDCREATE);
        bitBuffer.localDateTime(TDUPDATE);
        bitBuffer.BL(Unknown4);
        bitBuffer.BL(Unknown5);
        bitBuffer.BL(Unknown6);
        bitBuffer.duration(TDINDWG);
        bitBuffer.duration(TDUSRTIMER);
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
            bitBuffer.BS(UnknownShort1); // unknown short (type 5/6 only) these do not seem to be required,
            bitBuffer.BS(UnknownShort2); // unknown short (type 5/6 only) even for type 5.
            bitBuffer.BS(UnknownShort3); // unknown short (type 5/6 only)
            bitBuffer.BS(UnknownShort4); // unknown short (type 5/6 only)
            int CRC = bitBuffer.getRS(); //for the data section, starting after the sentinel. Use 0xC0C1 for the initial

    }

}
