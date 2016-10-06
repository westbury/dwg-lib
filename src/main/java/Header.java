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
	private Value<Point3D> INSBASE = new Value<>();
	private Value<Point3D> EXTMIN = new Value<>();
	private Value<Point3D> EXTMAX = new Value<>();
	private Value<Point2D> LIMMIN = new Value<>();
	private Value<Point2D> LIMMAX = new Value<>();
	private Value<Double> ELEVATION = new Value<>();
	private Value<Point3D> UCSORG = new Value<>();
	private Value<Point3D> UCSXDIR = new Value<>();
	private Value<Point3D> UCSYDIR = new Value<>();
	private Value<Handle> UCSNAME = new Value<>();
	private Value<Handle> PUCSORTHOREF = new Value<>();
	private Value<Integer> PUCSORTHOVIEW = new Value<>();
	private Value<Handle> PUCSBASE = new Value<>();
	private Value<Point3D> PUCSORGTOP = new Value<>();
	private Value<Point3D> PUCSORGBOTTOM = new Value<>();
	private Value<Point3D> PUCSORGLEFT = new Value<>();
	private Value<Point3D> PUCSORGRIGHT = new Value<>();
	private Value<Point3D> PUCSORGFRONT = new Value<>();
	private Value<Point3D> PUCSORGBACK = new Value<>();

    public Header(BitBuffer bitBuffer, FileVersion version) {
        int sizeOfTheSection = bitBuffer.getRL();

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

//        String unknownTextString1 = bitBuffer.getTU();
//        String unknownTextString2 = bitBuffer.getTU();
//        String unknownTextString3 = bitBuffer.getTU();
//        String unknownTextString4 = bitBuffer.getTU();

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
//        Open Design Specification for .dwg files 71
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
//        bitBuffer.H(CLAYER, HandleType.HARD_POINTER);
//        bitBuffer.H(TEXTSTYLE, HandleType.HARD_POINTER);
////        Open Design Specification for .dwg files 72
//        bitBuffer.H(CELTYPE, HandleType.HARD_POINTER);
//        bitBuffer.H(CMATERIAL, HandleType.HARD_POINTER);
//        bitBuffer.H(DIMSTYLE, HandleType.HARD_POINTER);
//        bitBuffer.H(CMLSTYLE, HandleType.HARD_POINTER);
        bitBuffer.BD(PSVPSCALE);
        bitBuffer.threeBD(INSBASE); // (PSPACE)
        bitBuffer.threeBD(EXTMIN); // (PSPACE)
        bitBuffer.threeBD(EXTMAX); // (PSPACE)
        bitBuffer.twoRD(LIMMIN); // (PSPACE)
        bitBuffer.twoRD(LIMMAX); // (PSPACE)
        bitBuffer.BD(ELEVATION); // (PSPACE)
        bitBuffer.threeBD(UCSORG); // (PSPACE)
        bitBuffer.threeBD(UCSXDIR); // (PSPACE)
        bitBuffer.threeBD(UCSYDIR); // (PSPACE)
//        bitBuffer.H(UCSNAME, HandleType.HARD_POINTER); // (PSPACE)
//        bitBuffer.H(PUCSORTHOREF, HandleType.HARD_POINTER);
        bitBuffer.BS(PUCSORTHOVIEW);
//        bitBuffer.H(PUCSBASE, HandleType.HARD_POINTER);
        bitBuffer.threeBD(PUCSORGTOP);
        bitBuffer.threeBD(PUCSORGBOTTOM);
        bitBuffer.threeBD(PUCSORGLEFT);
        bitBuffer.threeBD(PUCSORGRIGHT);
        bitBuffer.threeBD(PUCSORGFRONT);
        bitBuffer.threeBD(PUCSORGBACK);
        bitBuffer.threeBD(INSBASE); // (MSPACE)
        bitBuffer.threeBD(EXTMIN); // (MSPACE)
        bitBuffer.threeBD(EXTMAX); // (MSPACE)
        bitBuffer.twoRD(LIMMIN); // (MSPACE)
        bitBuffer.twoRD(LIMMAX); // (MSPACE)
        bitBuffer.BD(ELEVATION); // (MSPACE)
        bitBuffer.threeBD(UCSORG); // (MSPACE)
        bitBuffer.threeBD(UCSXDIR); // (MSPACE)
        bitBuffer.threeBD(UCSYDIR); // (MSPACE)
//        bitBuffer.H(UCSNAME, HandleType.HARD_POINTER); // (MSPACE)
/*        
        bitBuffer.H(UCSORTHOREF, HandleType.HARD_POINTER);
//        Open Design Specification for .dwg files 73
        bitBuffer.BS(UCSORTHOVIEW);
        bitBuffer.H(UCSBASE, HandleType.HARD_POINTER);
        bitBuffer.threeBD(UCSORGTOP);
        bitBuffer.threeBD(UCSORGBOTTOM);
        bitBuffer.threeBD(UCSORGLEFT);
        bitBuffer.threeBD(UCSORGRIGHT);
        bitBuffer.threeBD(UCSORGFRONT);
        bitBuffer.threeBD(UCSORGBACK);
        
        bitBuffer.doTV(DIMPOST);
        bitBuffer.doTV(DIMAPOST);
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
        T : DIMALTMZS);
        bitBuffer.BD(DIMMZF);
        T : DIMMZS);
        R2000+ Only:
        bitBuffer.H(DIMTXSTY, HandleType.HARD_POINTER);
        bitBuffer.H(DIMLDRBLK, HandleType.HARD_POINTER);
        bitBuffer.H(DIMBLK, HandleType.HARD_POINTER);
bitBuffer.H(DIMBLK1, HandleType.HARD_POINTER);
bitBuffer.H(DIMBLK2, HandleType.HARD_POINTER);
bitBuffer.H(DIMLTYPE, HandleType.HARD_POINTER);
bitBuffer.H(DIMLTEX1, HandleType.HARD_POINTER);
bitBuffer.H(DIMLTEX2, HandleType.HARD_POINTER);
bitBuffer.BS(DIMLWD);
bitBuffer.BS(DIMLWE);
bitBuffer.H(BLOCK_CONTROL_OBJECT, HandleType.HARD_OWNER);
bitBuffer.H(LAYER_CONTROL_OBJECT, HandleType.HARD_OWNER);
bitBuffer.H(STYLE_CONTROL_OBJECT, HandleType.HARD_OWNER);
bitBuffer.H(LINETYPE_CONTROL_OBJECT, HandleType.HARD_OWNER);
bitBuffer.H(VIEW_CONTROL_OBJECT, HandleType.HARD_OWNER);
bitBuffer.H(UCS_CONTROL_OBJECT, HandleType.HARD_OWNER);
bitBuffer.H(VPORT_CONTROL_OBJECT, HandleType.HARD_OWNER);
bitBuffer.H(APPID_CONTROL_OBJECT, HandleType.HARD_OWNER);
bitBuffer.H(DIMSTYLE CONTROL OBJECT, HandleType.HARD_OWNER);
        DICTIONARY (ACAD_GROUP, HandleType.HARD_POINTER);
        bitBuffer.H(DICTIONARY (ACAD_MLINESTYLE, HandleType.HARD_POINTER);
        bitBuffer.H(DICTIONARY (NAMED OBJECTS, HandleType.HARD_OWNER);
        bitBuffer.BS(TSTACKALIGN, default = 1 (not present in DXF)
        bitBuffer.BS(TSTACKSIZE, default = 70 (not present in DXF)
        bitBuffer.doTV(HYPERLINKBASE);
        bitBuffer.doTV(STYLESHEET);
        bitBuffer.H(DICTIONARY (LAYOUTS, HandleType.HARD_POINTER);
        bitBuffer.H(DICTIONARY (PLOTSETTINGS, HandleType.HARD_POINTER);
        bitBuffer.H(DICTIONARY (PLOTSTYLES, HandleType.HARD_POINTER);
        bitBuffer.H(DICTIONARY (MATERIALS, HandleType.HARD_POINTER);
        bitBuffer.H(DICTIONARY (COLORS, HandleType.HARD_POINTER);
        bitBuffer.H(DICTIONARY (VISUALSTYLE, HandleType.HARD_POINTER);

             if (version2013OrLater()) {
                 bitBuffer.H(UNKNOWN, HandleType.HARD_POINTER);
             }

        bitBuffer.BL(Flags:
            CELWEIGHT Flags & 0x001F
            ENDCAPS Flags & 0x0060
            JOINSTYLE Flags & 0x0180
            LWDISPLAY !(Flags & 0x0200)
            XEDIT !(Flags & 0x0400)
            EXTNAMES Flags & 0x0800
            PSTYLEMODE Flags & 0x2000
            OLESTARTUP Flags & 0x4000
            bitBuffer.BS(INSUNITS);
            bitBuffer.BS(CEPSNTYPE);
            bitBuffer.H(CPSNID, HandleType.HARD_POINTER); // present only if CEPSNTYPE == 3
            bitBuffer.doTV(FINGERPRINTGUID);
            bitBuffer.doTV(VERSIONGUID);
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
            bitBuffer.doTV(PROJECTNAME);
            bitBuffer.H(BLOCK_RECORD (*PAPER_SPACE, HandleType.HARD_POINTER);
            bitBuffer.H(BLOCK_RECORD (*MODEL_SPACE, HandleType.HARD_POINTER);
            bitBuffer.H(LTYPE (BYLAYER, HandleType.HARD_POINTER);
            bitBuffer.H(LTYPE (BYBLOCK, HandleType.HARD_POINTER);
            bitBuffer.H(LTYPE (CONTINUOUS, HandleType.HARD_POINTER);
            bitBuffer.B(CAMERADISPLAY);
            bitBuffer.BL(unknown);
            bitBuffer.BL(unknown);
            bitBuffer.BD(unknown);
            bitBuffer.BD(STEPSPERSEC);
            bitBuffer.BD(STEPSIZE);
            bitBuffer.BD(3DDWFPREC);
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
            bitBuffer.B(unknown);
            bitBuffer.CMC(INTERFERECOLOR);
            bitBuffer.H(INTERFEREOBJVS, HandleType.HARD_POINTER);
            bitBuffer.H(INTERFEREVPVS, HandleType.HARD_POINTER);
            bitBuffer.H(DRAGVS, HandleType.HARD_POINTER);
            bitBuffer.RC(CSHADOW);
            bitBuffer.BD(unknown);
            bitBuffer.BS(unknown short (type 5/6 only) these do not seem to be required,
            bitBuffer.BS(unknown short (type 5/6 only) even for type 5.
            bitBuffer.BS(unknown short (type 5/6 only)
            bitBuffer.BS(unknown short (type 5/6 only)
           	bitBuffer.RS(CRC); //for the data section, starting after the sentinel. Use 0xC0C1 for the initial
*/
    }

}
