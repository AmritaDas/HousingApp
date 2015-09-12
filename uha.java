package uha.db;

import java.sql.*;
import java.util.Scanner;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

class Staff{
	String name, haddress, dob, gender, location, position;
	Staff(){
		name = haddress = dob = gender = location = position = " ";
	}
}

class Guest{
	String name, homeaddress, dob, gender, status, coach_course;
	String smoker, phone, altphone, nation, special_needs, comments;
	FamilyMember fm[];
	NextOfKin nk[];
	Guest(){
		name = homeaddress = dob = gender = status = coach_course = " ";
		smoker = phone = altphone = nation = special_needs = comments = " ";
		fm = new FamilyMember[5];
		nk = new NextOfKin[5];
	}
}

class Student{
	String name, homeaddress, dob, gender, course, status;
	String smoker, catg, phone, altphone, nation, special_needs, comments;
	String nore, nonv, emailaddress;
	int match = 0;
	FamilyMember fm[];
	NextOfKin nk[];
	Student(){
		name = homeaddress = dob = gender = course = status = " ";
		smoker = catg = phone = altphone = nation = special_needs = comments = " ";
		nore = nonv = emailaddress;
		fm = new FamilyMember[5];
		nk = new NextOfKin[5];
	}
}

class FamilyMember{
	String name, relation, dob;
	FamilyMember(){
		name = relation = dob = " ";
	}
}

class NextOfKin{
	String name, relation, contact, haddress;
	NextOfKin(){
		name = relation = contact = haddress = " ";
	}
}

class ResHall{
	String name, addressID, phoneNo, hallManager, addr;
	int noOfRooms, secDep;
	boolean gradUpperOnly;
	ResHallRoom[] rhrs;
	ResHall(){
		name = addressID = phoneNo = hallManager = addr = " ";
		noOfRooms = secDep = 0;
		gradUpperOnly = true;
	}
}

class Period{
	String from, to;
	Period(){
		from = to = " ";
	}
}

class ResHallRoom{
	String placeNo, roomNo;
	int monthlyRentRate, timeps;
	Period[] ps;
	ResHallRoom(){
		placeNo = roomNo = " ";
		monthlyRentRate = timeps = 0;
	}
}

class Apartment{
	String aptNo, addressID, aptType, addr, manager;
	int noOfBeds, noOfBaths, secDep;
	boolean onCampus, freshmenAllowed;
	AptRoom[] aptrs;
	Apartment(){
		aptNo = addressID = aptType = addr = manager = " ";
		noOfBeds = noOfBaths = secDep = 0;
		onCampus = freshmenAllowed = true;
	}
}

class AptRoom{
	String placeNo, roomNo;
	int monthlyRentRate, timeps;
	Period[] ps;
	AptRoom(){
		placeNo = roomNo = " ";
		monthlyRentRate = timeps = 0;
	}
}

class Lease{
	String leaseNo, applicantNo, placeNo, roomNo, aptNo, addressID, addr, startDate, leaveDate, paymentSchedule, requestStatus, aptType, reqdOn, requestNo;
	int duration, secDep, totalRent;
	Lease(){
		duration = secDep = totalRent = 0;
		leaseNo = applicantNo = placeNo = roomNo = aptNo = addressID = addr = startDate = leaveDate = paymentSchedule = requestStatus = aptType = reqdOn = requestNo = " ";
	}
}

class TerminateLease{
	String requestNo, leaseNo, requestStatus, termntDate, inspecDate, inspecComments, feesLevied, termReason;   
	TerminateLease(){
		requestNo = leaseNo = requestStatus = termntDate = inspecDate = inspecComments = feesLevied = termReason = " ";
	}
}

class LeasePref{
	String aptType, aptNo, hallName;
	LeasePref(){
		aptType = aptNo = hallName = " ";
	}
}

class Invoice{
	String invoiceNo, leaseNo, dueDate, dateBilled, status, datePaid, methodPaid, period, type;
	double housing, parking, penalty, totfees;
	Invoice(){
		invoiceNo = leaseNo = dueDate = dateBilled = status = datePaid = methodPaid = period = type = " ";
		housing = parking = penalty = totfees = 0;
	}
}

public class uha {

	Connection con;
	Statement stmt, stmt2, stmt3, stmt4, stmt5;
	CallableStatement cstmt;
	Scanner scn;
	
	public uha() throws SQLException, ClassNotFoundException{
		Class.forName("oracle.jdbc.driver.OracleDriver");
		con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl1", "system", "manager");	//change as required
		stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		stmt2 = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		stmt3 = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		stmt4 = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		stmt5 = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		scn = new Scanner(System.in);
	}
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException{
		// TODO Auto-generated method stub
		
		uha u = new uha();
		
		u.Login();

	}

	public void Login()  throws SQLException {
		Scanner sc = new Scanner(System.in); 
		int choice = 0;
		
		do{
			System.out.println("\nSelect one of the following options: ");
			System.out.println(" 1. Login as Student or Supervisor \n 2. Login as Guest \n 3. Try RoomSync \n 4. Exit");
			
			try{
				choice = Integer.parseInt(sc.nextLine());
			}
			catch(NumberFormatException e){
				System.out.println("Invalid choice. Try again. ");
				continue;
			}
			
			switch(choice){
			case 4: System.out.println("Exited Application"); System.exit(0); break;
			case 1: 
				int mark = 0;
				do{
					mark = 0;
					String user_type = "";
					System.out.print("Enter username: ");
					String un = scn.nextLine();
					System.out.print("Enter password: ");
					String pw = scn.nextLine();
					
					if(un.contains("\'") || un.contains("\"") || pw.contains("\'") || pw.contains("\"")){
							System.out.println("Usernames and passwords may not contain quotes. ");
							mark = 1;
					}	

					if(mark != 1){
						
						ResultSet rset = stmt.executeQuery ("SELECT * FROM UHA_USERS where user_id = '" + un + "' and password = '" + pw + "' ");
						if(rset.next()){
							user_type = rset.getString(2);
						}
						else{
							mark = 1;
							System.out.println("Login incorrect.");
						}
						
						if(user_type.compareToIgnoreCase("student") == 0){
							String id1 = rset.getString(3);
							ResultSet rset1 = stmt.executeQuery("select first_name from uha_applicant where applicant_id = '" + rset.getString(3)+ "' ");
							if(rset1.next()){
								StudentMode(id1, rset1.getString(1));
							}	
							else{
								System.out.println("Student does not exist in database");
								mark = 1;
							}
							rset1.close();
						}
						else if(user_type.compareToIgnoreCase("supervisor") == 0){
							String id2 = rset.getString(4);
							ResultSet rset2 = stmt.executeQuery("select first_name from uha_admin where staff_no = '" + rset.getString(4)+ "' ");
							if(rset2.next()){
								SupervisorMode(id2, rset2.getString(1));
							}
							else{
								System.out.println("Supervisor does not exist in database");
								mark = 1;
							}
							rset2.close();
						}
						
						rset.close();
					}

					if(mark == 1){
						String p = "";
						do{
							System.out.println("To try again, type 'y'. To go to main menu, type 'm'");
							p = scn.nextLine();
							if(p.compareToIgnoreCase("y") == 0){
								mark = 1;
							}
							else if(p.compareToIgnoreCase("m") == 0){
								mark = 3;
							}
							else{
								mark = 2;
							}
						}while(mark==2);
					}
					
				}while(mark == 1);

				break;
			case 3:
				RoomSync();
				break;
			case 2:
				int marker = 0;
				do{
					marker = 0;
					System.out.print("Enter approval id: ");
					String aid = scn.nextLine();
					
					if(aid.contains("\'") || aid.contains("\"")){
						System.out.println("Approval ID may not contain quotes. ");
						marker = 1;
					}	

					if(marker != 1){
						ResultSet rset = stmt.executeQuery ("SELECT * FROM UHA_APPLICANT where applicant_id = '" + aid + "' and guest = 'Y'");
					
						if(rset.next()){
							GuestMode(rset.getString(1), rset.getString(2));
						}
						else{
							marker = 1;
							System.out.println("Login incorrect.");
						}
						
						rset.close();
					}
					
					if(marker == 1){
						String p = "";
						do{
							System.out.println("To try again, type 'y'. To go to main menu, type 'm'");
							p = scn.nextLine();
							if(p.compareToIgnoreCase("y") == 0){
								marker = 1;
							}
							else if(p.compareToIgnoreCase("m") == 0){
								marker = 3;
							}
							else{
								marker = 2;
							}
						}while(marker==2);
					}
					
				} while(marker == 1);
 
				break;
			default:
				System.out.println("Invalid choice. Try again. \n");
			}
			
		}while(choice!=4);
		
		sc.close();
		
	}

	public void StudentMode(String app_id, String app_name) throws SQLException{
		
		int choice = 0, mark = 0;
		
		do{
			mark = 0;
			System.out.println("\n Welcome, " + app_name + ". Choose one of the following options: " + 
								"\n\n 1. Housing Options \n 2. Parking Options \n 3. Maintenance \n 4. Profile \n 5. Back");
			
			try{
				choice = Integer.parseInt(scn.nextLine());
			}
			catch(NumberFormatException e){
				System.out.println("Invalid choice. Try again. ");
				mark = 1;
			}
			
			if(mark != 1){
				switch(choice){
				case 5: return;
				case 1: 
					Housing(app_id, app_name, 1);
					break;
				case 2: 
					Parking(app_id, app_name, 1);
					break;
				case 3: 
					Maintenance(app_id, app_name, 1);
					break;
				case 4:
					Profile(app_id, app_name, 1);	//user type : student = 1, guest = 2, supervisor = 3
					break;
				default: 
					System.out.println("Invalid choice. Try again"); 
					mark = 1; 
				}
			}
			
			mark = 1;

		}while(mark!=0);			
	}
	
	public void GuestMode(String app_id, String app_name) throws SQLException{
		int choice = 0, mark = 0;
		
		do{
			mark = 0;
			System.out.println("\n Welcome, " + app_name + ". Choose one of the following options: " + 
								"\n\n 1. Housing Options \n 2. Parking Options \n 3. Maintenance \n 4. Profile \n 5. Back");
			
			try{
				choice = Integer.parseInt(scn.nextLine());
			}
			catch(NumberFormatException e){
				System.out.println("Invalid choice. Try again. ");
				mark = 1;
			}
			
			if(mark != 1){
				switch(choice){
				case 5: return;
					//break;
				case 1: 
					Housing(app_id, app_name, 2);
					break;
				case 2: 
					Parking(app_id, app_name, 2);
					break;
				case 3: 
					Maintenance(app_id, app_name, 2);
					break;
				case 4:
					Profile(app_id, app_name, 2);	//user type : student = 1, guest = 2, supervisor = 3
					break;
				default: 
					System.out.println("Invalid choice. Try again"); 
					mark = 1; 
				}
			}
			
			mark = 1;

		}while(mark!=0);			
	}
	
	public void SupervisorMode(String app_id, String app_name) throws SQLException {
		int choice = 0, mark = 0;
		
		do{
			mark = 0;
			System.out.println("\n Welcome, " + app_name + ". Choose one of the following options: " + 
								"\n\n 1. View new Lease Requests \n 2. View Terminate Lease requests " + 
								"\n 3. View Maintenance Tickets \n 4. View Parking Request Tickets " + 
								"\n 5. Profile \n 6. Back");
			
			try{
				choice = Integer.parseInt(scn.nextLine());
			}
			catch(NumberFormatException e){
				System.out.println("Invalid choice. Try again. ");
				mark = 1;
			}
			
			if(mark != 1){
				switch(choice){
				case 6: return;
				case 1: 
					ViewNewLeasesasSupervisor(app_id, app_name);
					break;
				case 2: 
					veiwTerminateLeaseRequestAsSupervisor(app_id, app_name);
					break;
				case 3: 
					viewMaintenanceTicket(app_id, app_name);
					break;
				case 4:
					viewParkingRequestAsSupervisor(app_id, app_name);
					break;
				case 5:
					Profile(app_id, app_name, 3);	//user type : student = 1, guest = 2, supervisor = 3
					break;
				default: 
					System.out.println("Invalid choice. Try again"); 
					mark = 1; 
				}
			}
			
			mark = 1;

		}while(mark!=0);			
	}
	
	public void RoomSync() throws SQLException{
		int m=0;
		do
		{
			m = 0;
			System.out.println("Welcome to RoomSync! \n1.Register \n2.Login \n3.Back");
			int ch = Integer.parseInt(scn.nextLine());
			switch(ch){
			case 1:
				System.out.println("Press y to agree to share you email address with a probable roommate or press 3 to exit");
				String a = scn.nextLine();
				if(a.contains("y"))
				{	
					System.out.println("Please enter your full name ");
					String a1 = scn.nextLine();
					System.out.println("Please enter your student id ");
					String a7 = scn.nextLine();
					System.out.println("Enter email address");
					String a2= scn.nextLine();
					System.out.println("Gender, enter M or F");
					String a3=scn.nextLine().toUpperCase();
					System.out.println("Smoker, enter y or n");
					String a4= scn.nextLine().toUpperCase();
					System.out.println("Do you like to stay up late press l or if you like to get up early press e");
					String a5=scn.nextLine().toUpperCase();
					System.out.println("Do you prefer a vegetarian roommate press v or press n if either is fine");
					String a6=scn.nextLine().toUpperCase();
					ResultSet roomsync = stmt.executeQuery ("insert into uha_room_sync(student_id, name,email_address,gender,smoker,NORE,VEG_OR_NON_VEG) values( '" + a7 + "', '" + a1 + 
															"', '" + a2 + "', '" + a3 +"', '" + a4 + "', '" + a5 +"', '" + a6 + "')");
					System.out.println("Please wait while we find a perfect roommate for you");
					ResultSet rs1 = stmt.executeQuery("Select email_address from uha_room_sync where gender = '"+a3+"' and smoker = '"+a4+"' and student_id <> '" + a7 + "' ");
					ResultSet rs2 = stmt2.executeQuery("Select email_address from uha_room_sync where gender = '"+a3+"' and nore = '"+a5+"' and student_id <> '" + a7 + "' ");
					ResultSet rs3 = stmt3.executeQuery("Select email_address from uha_room_sync where gender = '"+a3+"' and veg_or_non_veg = '"+a6+"' and student_id <> '" + a7 + "' ");
					int i1 = 0;
					while(rs1.next()){
						i1++;
					}
					rs1.beforeFirst();
					Student[] s1 = new Student[i1];
					int j1 = 0;
					while(rs1.next()){
						s1[j1] = new Student();
						s1[j1].emailaddress = rs1.getString(1);
						s1[j1].gender = a3;
						s1[j1].smoker = a4;
						s1[j1].match = 1;
						j1++;
					}
					rs1.close();
					
					int i2 = 0;
					while(rs2.next()){
						i2++;
					}
					rs2.beforeFirst();
					Student[] s2 = new Student[i2];
					int j2 = 0;
					while(rs2.next()){
						s2[j2] = new Student();
						s2[j2].emailaddress = rs2.getString(1);
						s2[j2].gender = a3;
						s2[j2].nore = a5;
						s2[j2].match = 1;
						j2++;
					}
					rs2.close();
					
					int i3 = 0;
					while(rs3.next()){
						i3++;
					}
					rs3.beforeFirst();
					Student[] s3 = new Student[i3];
					int j3 = 0;
					while(rs3.next()){
						s3[j3] = new Student();
						s3[j3].emailaddress = rs3.getString(1);
						s3[j3].gender = a3;
						s3[j3].nonv = a6;
						s3[j3].match = 1;
						j3++;
					}
					rs3.close();
					
					for(int k1=0; k1<i1; k1++){
						for(int k2 = 0; k2<i2; k2++){ 
							if(s1[k1].emailaddress.compareToIgnoreCase(s2[k2].emailaddress) == 0){
								s1[k1].match++;
							}
						}
						for(int k3 = 0; k3<i3; k3++){ 
							if(s1[k1].emailaddress.compareToIgnoreCase(s3[k3].emailaddress) == 0){
								s1[k1].match++;
							}
						}
					}
					
					int l = 0;
					for(int k1 = 0; k1<i1; k1++){
						if(k1 == 0){
							System.out.println("Contact any of the students for a roommate. ");
							System.out.println("Your best matches are: ");
						}
						
						if(s1[k1].match >= 2) 
							System.out.println(++l + ". " + s1[k1].emailaddress);
					}
					
					if(l == 0){
						System.out.println("We have not found any roommates right now. Try again later. ");
					}
					System.out.println();
				}
				m = 1;
				break;
			case 2:
				System.out.println("Enter email address: ");
				String a2 = scn.nextLine();
				ResultSet rs0 = stmt.executeQuery("select * from uha_room_sync where email_address = '" + a2 + "' ");
				if(rs0.next()){
					String a3, a4, a5, a6, a7;
					a3 = rs0.getString(3);
					a4 = rs0.getString(4);
					a5 = rs0.getString(5);
					a6 = rs0.getString(6);
					a7 = rs0.getString(7);
					
					System.out.println("Please wait while we find a perfect roommate for you");
					ResultSet rs1 = stmt.executeQuery("Select email_address from uha_room_sync where gender = '"+a3+"' and smoker = '"+a4+"' and student_id <> '" + a7 + "' ");
					ResultSet rs2 = stmt2.executeQuery("Select email_address from uha_room_sync where gender = '"+a3+"' and nore = '"+a5+"' and student_id <> '" + a7 + "' ");
					ResultSet rs3 = stmt3.executeQuery("Select email_address from uha_room_sync where gender = '"+a3+"' and veg_or_non_veg = '"+a6+"' and student_id <> '" + a7 + "' ");
					int i1 = 0;
					while(rs1.next()){
						i1++;
					}
					rs1.beforeFirst();
					Student[] s1 = new Student[i1];
					int j1 = 0;
					while(rs1.next()){
						s1[j1] = new Student();
						s1[j1].emailaddress = rs1.getString(1);
						s1[j1].gender = a3;
						s1[j1].smoker = a4;
						s1[j1].match = 1;
						j1++;
					}
					rs1.close();
					
					int i2 = 0;
					while(rs2.next()){
						i2++;
					}
					rs2.beforeFirst();
					Student[] s2 = new Student[i2];
					int j2 = 0;
					while(rs2.next()){
						s2[j2] = new Student();
						s2[j2].emailaddress = rs2.getString(1);
						s2[j2].gender = a3;
						s2[j2].nore = a5;
						s2[j2].match = 1;
						j2++;
					}
					rs2.close();
					
					int i3 = 0;
					while(rs3.next()){
						i3++;
					}
					rs3.beforeFirst();
					Student[] s3 = new Student[i3];
					int j3 = 0;
					while(rs3.next()){
						s3[j3] = new Student();
						s3[j3].emailaddress = rs3.getString(1);
						s3[j3].gender = a3;
						s3[j3].nonv = a6;
						s3[j3].match = 1;
						j3++;
					}
					rs3.close();
					
					for(int k1=0; k1<i1; k1++){
						for(int k2 = 0; k2<i2; k2++){ 
							if(s1[k1].emailaddress.compareToIgnoreCase(s2[k2].emailaddress) == 0){
								s1[k1].match++;
							}
						}
						for(int k3 = 0; k3<i3; k3++){ 
							if(s1[k1].emailaddress.compareToIgnoreCase(s3[k3].emailaddress) == 0){
								s1[k1].match++;
							}
						}
					}
					
					int l = 0;
					for(int k1 = 0; k1<i1; k1++){
						if(k1 == 0){
							System.out.println("Contact any of the students for a roommate. ");
							System.out.println("Your best matches are: ");
						}
						
						if(s1[k1].match >= 2) 
							System.out.println(++l + ". " + s1[k1].emailaddress);
					}
					
					if(l == 0){
						System.out.println("We have not found any roommates right now. Try again later. ");
					}
					System.out.println();
					m = 1;
				}
				else{
					System.out.println("Please Register first. ");
					m = 0;
				}
				
				break;
			case 3:
				m = 1;
				break;
			default:
				System.out.println("Invalid choice. Try again. ");
			}
			
		}while(m==0);
	}
}