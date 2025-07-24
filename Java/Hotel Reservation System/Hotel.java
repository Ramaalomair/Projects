
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Hotel implements Serializable{
private String hName;
private String Address;
private int nOfRooms;
private int nOfReservations;

public Room [] LisOfRooms;
public Reservation [] LisOfReservations;



public Hotel(String n, String address , int size) {
this.hName = n;
Address = address;
nOfRooms = 0;
nOfReservations =0;
LisOfRooms = new Room [30];
LisOfReservations = new Reservation [size];
}


public Suite[] DisplayAvailableSuite() {
Suite SuiteList [] = new Suite [nOfRooms];
int s = 0;
for (int i = 0 ; i<nOfRooms ; i++) {
	if (LisOfRooms[i] instanceof Suite && LisOfRooms[i].getAvailable() ) {
	SuiteList [s] = new Suite ((Suite) LisOfRooms[i]);
	s++;
	}
}

if (s == 0) {
	return null;
} else {
	return SuiteList;
}
}



public Regular[] DisplayAvailableRgular() {
	Regular RegleList [] = new Regular [nOfRooms];
int a = 0;
for (int i = 0 ; i<nOfRooms ; i++) {
	if (LisOfRooms[i] instanceof Regular && LisOfRooms[i].getAvailable() ) {
		RegleList [a] = new Regular ((Regular) LisOfRooms[i]);
	a++;
	}
}


if (a == 0) {
	return null;
}

	return RegleList;
}





public boolean addRoom (Room R) {
if (this.nOfRooms<LisOfRooms.length )
{
if (R instanceof Suite) {
	LisOfRooms [nOfRooms++] = new Suite ( (Suite)(R));
} else
    if (R instanceof Regular) {
	LisOfRooms [nOfRooms++] = new Regular ( (Regular)(R) );
}
return true;
}
return false;
}


public Room SearchRoom (int NRoom) {

	for (int i = 0 ; i < nOfRooms ; i++) {
		if (LisOfRooms[i].getRoomNo() == NRoom  ) {
			return LisOfRooms[i] ;
		}
	}
	return null ;
}

public boolean addReservation(Reservation r) {

	if ( nOfReservations < LisOfReservations.length) 
            if(r.getCustomerRoom() != null){
		LisOfReservations[nOfReservations++] = r ;
		return true ;
           
                
	}
	return false ;
}

public Reservation findReservation (String phoneNo) {

	for (int i = 0 ; i<nOfReservations ; i++) {
		if (LisOfReservations[i].getC().getPhoneNo().equals(phoneNo)) {
			return LisOfReservations[i] ;
		}
	}
	return null ;
}
public boolean CancelReservation(int RNo) {

for(int i = 0 ; i < this.nOfReservations ; i++) {
	if (LisOfReservations[i].getCustomerRoom().getRoomNo() == RNo )
	{
		LisOfReservations[i].getCustomerRoom().setAvailable(true);  // change room availability to true
		for(int j = 0 ; j < nOfReservations -1 ; j++) {
			LisOfReservations[j] = LisOfReservations[j+1] ;
		}
		nOfReservations-- ;
		LisOfReservations[nOfReservations] = null ;

	return true ;
	}
}

return false ;

}






@Override
public String toString () {
String str = "Hotel Name is = " + this.hName + "Number of Rooms =" +this.nOfRooms +"Adress = "+this.Address +"\n";
str+= "============================\n";
for (int i = 0 ; i<nOfRooms ; i++) {
	str += LisOfRooms [i].toString() + "\n";
}
return str;
}

/// write file:
	

	

public String gethName() {
return hName;
}

public void sethName(String hName) {
this.hName = hName;
}

public String getAddress() {
return Address;
}

public void setAddress(String address) {
Address = address;
}

public int getnOfRooms() {
return nOfRooms;
}

public void setnOfRooms(int nOfRooms) {
this.nOfRooms = nOfRooms;
}

public int getnOfReservations() {
	return nOfReservations;
}



}

