package com.jsp.hotel_manage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class HotelManagment {
	private static final String url="jdbc:mysql://localhost:3306/hotel_db";
	private static final String username="root";
	private static final String password="root";

	public static void main(String[] args) throws SQLException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
		try {
			Connection connection=DriverManager.getConnection(url,username,password);
			while(true) {
				System.out.println();
				System.out.println("HOTEL MANAGEMNET SYSTEM");
				Scanner sc=new Scanner(System.in);
				System.out.println("1 . Reserver a Room ");
				System.out.println("2. View Reservations ");
				System.out.println("3. Get Room Number ");
				System.out.println("4. Update Reservations ");
				System.out.println("5. Delete Reservations ");
				System.out.println("0 Exit ");
				System.out.println("Choose an Option: ");
				int choice = sc.nextInt();
				switch(choice) {
				case 1:
					reserveRoom(connection,sc);
					break;
				case 2:
					viewReservations(connection);
					break;
				case 3:
					getRoomNumber(connection,sc);
					break;
				case 4:
					updateReservation(connection,sc);
					break;
				case 5:
					deleteReservation(connection,sc);
					break;
				case 0:
					exit();
					sc.close();
					return;
				default:
					System.out.println("Invalid choice. Try again...");
					}
			}
		
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
		}
		catch(InterruptedException e) {
			throw new RuntimeException(e);
		}
		}
	public static void reserveRoom(Connection connection,Scanner sc) {
		try {
			System.out.println("Enter Your Name : ");
			String guestName = sc.next();
			sc.nextLine();
			System.out.println("Enter room number :");
			int roomNumber=sc.nextInt();
			System.out.println("Enter your contact number : ");
			String contactNumber=sc.next();
			String sql="INSERT INTO reservation(guest_name,room_number,contact_number)"
					+ "VALUES('" + guestName + "'," + roomNumber + ", '" +contactNumber + "')";
			try(Statement statement =connection.createStatement()){
				int affectRows =statement.executeUpdate(sql);
				if(affectRows > 0) {
					System.out.println("Reservation Successfull");
				}
				else {
					System.out.println("Reservation false");
				}
			}
					
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	public static void viewReservations(Connection connection) throws SQLException {
		String sql= "SELECT reservation_id,guest_name,contact_number,reservation_date From reservation";
		try(Statement statment =connection.createStatement()){
				ResultSet resultset=statment.executeQuery(sql);
				
				
				System.out.println("Current Reservation :");
				System.out.println("-----------------------------------------------------------------------------");
				System.out.println("| Reservation ID  | Guest                     | Room Number | Contact Number |");
				System.out.println("------------------------------------------------------------------------------");
				
				while(resultset.next()) {
					int reservationId=resultset.getInt("reservation_id");
					String guestName=resultset.getNString("guest_name");
					int roomNumber=resultset.getInt("room_number");
					String contactNumber=resultset.getNString("contact_number");
					String reservationDate=resultset.getTimestamp("reservation_date").toString();
					
					
					System.out.printf("|%-14d | %-15s | %-13d | %-20s | %-19s |\n", 
							reservationId,guestName,roomNumber,contactNumber,reservationDate);
				}
				System.out.println("---------------------+--------------------+------------------+---------------");
		}
	}
	private static void getRoomNumber(Connection connection,Scanner sc) {
		try {
			System.out.println("Enter your reservation ID : ");
			int reservationId=sc.nextInt();
			System.out.println("Enter guest name :");
			String guestName=sc.next();
			
			String sql="SELECT room_number FROM reservation " +
			"WHERE reservation_id = " + reservationId +
			"AND guest_name = '" + guestName + "'";
			try (Statement statement = connection.createStatement();
					ResultSet resultset = statement.executeQuery(sql)){
				if(resultset.next()) {
					int roomNumber =  resultset.getInt("room_number");
					System.out.println("Room number for Reservation ID " + reservationId +
							"and Guest "+guestName + " is : " + roomNumber);
				}else {
					System.out.println("Reservation not found the given ID and guest name");
				}
				
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	private static void updateReservation(Connection connection,Scanner sc) {
		try {
			System.out.print("Enter reservation ID to update : ");
			int reservationId=sc.nextInt();
			sc.nextLine();
			
			if(!reservationExists(connection,reservationId)) {
				System.out.println("Reservation not found for the given ID .");
				return;
			}
			System.out.print("Enter new guest name : ");
			String newGuestName=sc.nextLine();
			System.out.print("Enter new room number : ");
			int newRoomNumber=sc.nextInt();
			System.out.print("Enter new contact number : ");
			String newContactnumber=sc.next();
			
			String sql = "UPDATE reservation SET guest_name = '" + newGuestName + "' , " +
			"room_number = " + newRoomNumber  + " , " +
			"contact_number = '" + newContactnumber + "' " +
			"WHERE reservation_id = " + reservationId;
			
			try(Statement statement=connection.createStatement()){
				int affectedRows =statement.executeUpdate(sql);
				
				if(affectedRows > 0) {
					System.out.println("Reservation updated successfully!");
				}else {
					System.out.println("Reservation update failed");
				}
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void deleteReservation(Connection connection,Scanner sc) {
		try {
			System.out.print("Enter reservation ID to delete: ");
			int reservationId=sc.nextInt();
			
			if(!reservationExists(connection,reservationId)) {
				System.out.println("Reservation not found for the given ID. ");
				return;
			}
			
			String sql = "DELETE FROM reservation WHERE reservation_id = " + reservationId;
			
			try (Statement statement = connection.createStatement()){
				int affectedRows = statement.executeUpdate(sql);
				
				if(affectedRows > 0 ) {
					System.out.println("Reservation delete successfully!..");
				}else {
					System.out.println("Reservation deletion failed");
				}
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static boolean reservationExists(Connection connection,int reservationId) {
		try {
			String sql="SELECT reservation_id FROM reservations WHERE reservation_id = " +reservationId;
			
			try(Statement statement = connection.createStatement();
					ResultSet resultset=statement.executeQuery(sql)){
					return resultset.next();
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	public static void exit() throws InterruptedException {
		System.out.print("Existing System ");
		int i=5;
		while(i!=0) {
			System.out.print(".");
			Thread.sleep(450);
			i--;
		}
		System.out.println();
		System.out.println("Thank you For Using Hotel Reservation System!!!");
	}
	
}




































