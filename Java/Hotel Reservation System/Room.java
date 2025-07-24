

import java.io.Serializable;

public abstract class Room implements Serializable {

	protected int RoomNo;
	protected int floorNo;
	protected boolean Available;
	protected double Price;
	protected boolean view;

	public Room(int rNo, int fNo , boolean view , double p) {
		RoomNo = rNo;
		floorNo = fNo;
		this.view = view ;
		Available = true;
		Price = p ;
	}

	public abstract double calculatePrice();



	@Override
	public String toString() {
		String s = "Room Number: " + RoomNo +"\n Floor Number: " + floorNo + "\n Price is:" + this.calculatePrice()  ;
		if(view) {
			s += "\n with view";
		} else {
			s += "\n whithout view";
		}

		return s;
	}

	public int getRoomNo() {
		return RoomNo;
	}

	public void setRoomNo(int roomNo) {
		RoomNo = roomNo;
	}

	public boolean getAvailable() {
		return Available;
	}

	public void setAvailable(boolean available) {
		Available = available;
	}

	public int getFloorNo() {
		return floorNo;
	}

	public void setFloorNo(int floorNo) {
		this.floorNo = floorNo;
	}




}
