

public class Regular extends Room{

	private int nOfBeds;

	public Regular(int rNo, int fNo , boolean view , double p ,int nOfBeds) {
		super(rNo, fNo , view , p);
		this.nOfBeds = nOfBeds ;
	}


	public Regular(Regular regular) {
	    super (regular.RoomNo , regular.floorNo , regular.view , regular.Price);
	    nOfBeds = regular.nOfBeds;
	    		}


	@Override
	public double calculatePrice() {
		double p = (50 * nOfBeds) + Price; //it depends on number of beds in a room
		if(view) {
			p += 150;
		}
		return p;

	}

	@Override
	public String toString() {
		return super.toString() + "\n nOfBeds: " + nOfBeds ;
	}





}