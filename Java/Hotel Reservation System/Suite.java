

public class Suite extends Room{

	private char type;

	public Suite(int rNo, int fNo , boolean view , double p ,char t) {
		super(rNo, fNo , view , p );
		this.type = t ;
	}




	public Suite(Suite s) {

    super (s.RoomNo , s.floorNo , s.view , s.Price);
    type = s.type;

	}


	@Override
	public double calculatePrice() {
		double p = Price;
		switch(type) {
		case 'S' : case 's':  //Superior Suite
			p = Price + 1000;
			break;
		case 'J' : case 'j':  //Junior Suite
			p = Price + 700;
			break;
		case 'D' : case 'd':  //Deluxe Suite
			p = Price + 500;
			break;
		}
		if(view) {
			p += 300;
		}
		return p;
	}

	@Override
	public String toString() {
		String s = super.toString() + " \n Type: ";
		if(type == 'S' || type == 's') {
			s +=  "Superior";
		} else
			if(type == 'J' || type == 'j') {
				s += "Junior" ;
			} else
				if(type == 'D' || type == 'd') {
					s += "Deluxe";
				}
		return  s ;
	}





}

