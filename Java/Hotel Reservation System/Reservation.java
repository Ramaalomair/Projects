/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.io.Serializable;
import javax.swing.JOptionPane;

public class Reservation implements Serializable{

	private int ReservNo ;
	private int checkIn ;
	private int checkOut ;
	private Room customerRoom ;
	private Customer customer ;
	private static int num = 111 ;


	public Reservation( int chIn , int chOut , Room cRoom , Customer c) {
		ReservNo = num++;
		this.checkIn = chIn ;
		this.checkOut = chOut ;
                if(cRoom.Available){
		this.customerRoom = cRoom ;
		customerRoom.setAvailable(false);}
                else{
                    customerRoom = null;
                    JOptionPane.showMessageDialog(null, "This Room is already Booked");
                    }
		this.customer = c ;

	}


	public int duration() {
            int d = checkOut - checkIn ;
            if(d == 0)
                return 1;
            else
                return d;

	}

	public double totalPrice() {  // calculate reservation's price
		double t = customerRoom.calculatePrice();
		if(duration() == 1 || duration() == 0) {
			return  t ;
		} else {
			t =  t * this.duration();
			return t ;
		}


	}



		@Override
	public String toString() {

		return   customer.toString() + "Reservation number = " + this.ReservNo + " \n Check in Day: " + this.checkIn +  " ,   Check out Day: " + checkOut + "\n Number of days: " + duration() +" \n " + customerRoom.toString() +" \n Reservation Cost:  " + this.totalPrice() ;
	}

		public int getReservNo() {
			return ReservNo;
		}

		public int getCheckIn() {
			return checkIn;
		}

		public int getCheckOut() {
			return checkOut;
		}

		public Room getCustomerRoom() {
			return customerRoom;
		}

		public Customer getC() {
			return customer;
		}







}
