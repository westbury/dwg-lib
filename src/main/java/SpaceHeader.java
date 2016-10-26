import bitstreams.BitBuffer;
import bitstreams.Handle;
import bitstreams.HandleType;
import bitstreams.Point2D;
import bitstreams.Point3D;
import bitstreams.Value;

public class SpaceHeader {

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
	private Value<Handle> UCSORTHOREF = new Value<>();
	private Value<Integer> UCSORTHOVIEW = new Value<>();
	private Value<Handle> UCSBASE = new Value<>();
	private Value<Point3D> UCSORGTOP = new Value<>();
	private Value<Point3D> UCSORGBOTTOM = new Value<>();
	private Value<Point3D> UCSORGLEFT = new Value<>();
	private Value<Point3D> UCSORGRIGHT = new Value<>();
	private Value<Point3D> UCSORGFRONT = new Value<>();
	private Value<Point3D> UCSORGBACK = new Value<>();

	public SpaceHeader(BitBuffer bitBuffer, BitBuffer handleStream) {
        bitBuffer.threeBD(INSBASE);
        bitBuffer.threeBD(EXTMIN);
        bitBuffer.threeBD(EXTMAX);
        bitBuffer.twoRD(LIMMIN);
        bitBuffer.twoRD(LIMMAX);
        bitBuffer.BD(ELEVATION);
        bitBuffer.threeBD(UCSORG);
        bitBuffer.threeBD(UCSXDIR);
        bitBuffer.threeBD(UCSYDIR);
        handleStream.H(UCSNAME, HandleType.HARD_POINTER);
        handleStream.H(UCSORTHOREF, HandleType.HARD_POINTER);
        bitBuffer.BS(UCSORTHOVIEW);
        handleStream.H(UCSBASE, HandleType.HARD_POINTER);
        bitBuffer.threeBD(UCSORGTOP);
        bitBuffer.threeBD(UCSORGBOTTOM);
        bitBuffer.threeBD(UCSORGLEFT);
        bitBuffer.threeBD(UCSORGRIGHT);
        bitBuffer.threeBD(UCSORGFRONT);
        bitBuffer.threeBD(UCSORGBACK);
	}

}
