package objects;

public class GenericObject extends NonEntityObject {

	private String objectType;

	public GenericObject(int objectType) {
		this.objectType = Integer.toString(objectType);
	}

	public GenericObject(String classdxfname) {
		this.objectType = classdxfname;
	}

	public String toString() {
		return "unknown object of type " + objectType;
	}

}
