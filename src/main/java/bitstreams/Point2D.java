package bitstreams;

public class Point2D {

	public final double x;
	public final double y;
	
	public Point2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

    @Override
    public String toString() {
    	return "(" + x + ", " + y + ")";
    }
}
