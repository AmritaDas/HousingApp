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
	public void Housing(String app_id, String app_name, int user_type) throws SQLException{
		int choice = 0, mark = 0;
		do{
			mark = 0;
			
			System.out.println("\nDashboard of " + app_name + ": \n 1. View Invoices \n 2. View Leases \n 3. New Request \n 4. View/Cancel Request \n 5. View Vacancy \n 6. Back");
			
			try{
				choice = Integer.parseInt(scn.nextLine());
			}
			catch(Exception e){
				System.out.println("Invalid choice. Try again. ");
				mark = 1;
			}
			
			if(mark != 1){
				switch(choice){
				case 1:
					ViewInvoices(app_id, app_name, user_type);
					break;
				case 2:
					ViewLeases(app_id, app_name, user_type);
					break;
				case 3:
					NewRequest(app_id, app_name, user_type);
					break;
				case 4:
					ViewCancelRequest(app_id, app_name, user_type);
					break;
				case 5:
					Vacancy();
					break;
				case 6:
					return;
				default:
					System.out.println("Invalid choice. Try again.");
					mark = 1;
				}
				mark = 1;
			}
			
		}while(mark!=0);
	}
	
	public void ViewInvoices(String app_id, String app_name, int user_type) throws SQLException{
		int choice = 0, mark = 0;
		
		do{
			mark = 0;
			System.out.println("\nLeases of " + app_name + ": \n 1. View Current Invoice \n 2. View Former Invoices \n 3. Back ");
			try{
				choice = Integer.parseInt(scn.nextLine());
			}
			catch(Exception e){
				System.out.println("Invalid choice. Try again. ");
				mark = 1;
			}
			
			if(mark != 1){
				switch(choice){
				case 1:
					/*cstmt = con.prepareCall("{call uha_invoice_creation_proc}");
					cstmt.execute();
					cstmt = con.prepareCall("{call uha_invoice_late_fees}");
					cstmt.execute();*/
					
					String ln = "";
					ResultSet rset = stmt.executeQuery("select lease_no from uha_lease where applicant_no = '" + app_id + "' and request_status = 'INPROGRESS' ");
					if(rset.next()){
						ln = rset.getString(1);
						
						ResultSet rset1 = stmt.executeQuery("select * from (select * from uha_invoice where applicant_no = '" + app_id +
												"' and lease_no = '" + ln + "' and sysdate - date_billed <= 31 order by date_billed desc) where rownum = 1 ");
						if(rset1.next()){
							Invoice i = new Invoice();
							i.invoiceNo = rset1.getString(1);
							i.leaseNo = rset1.getString(2);
							i.type = rset1.getString(4);
							i.housing = rset1.getString(5)!=null?Double.parseDouble(rset1.getString(5)):0;
							i.parking = rset1.getString(6)!=null?Double.parseDouble(rset1.getString(6)):0;
							i.penalty = rset1.getString(7)!=null?Double.parseDouble(rset1.getString(7)):0;
							i.totfees = rset1.getString(12)!=null?Double.parseDouble(rset1.getString(12)):0;
							i.dateBilled = rset1.getString(13).substring(0, 11);
							i.dueDate = rset1.getString(8).substring(0, 11);
							i.status = rset1.getString(11);
							i.datePaid = rset1.getString(9)!=null?rset1.getString(9).substring(0, 11):" ";
							i.methodPaid = rset1.getString(10)!=null?rset1.getString(10):" ";
							
							System.out.println("Current invoice: ");
							System.out.println(" Invoice No: " + i.invoiceNo + "\n Lease No: " + i.leaseNo + "\n Invoice Type: " + i.type);
							if(i.housing != 0)
								System.out.println(" Housing: " + i.housing);
							if(i.parking != 0)
								System.out.println(" Parking: " + i.parking);
							if(i.penalty != 0) 
								System.out.println(" Penalty: " + i.penalty);
							
							System.out.println(" Total Fees: " + i.totfees + "\n Date Billed: " + i.dateBilled + "\n Due Date: " + i.dueDate + "\n Status: " + i.status);
							
							if(i.datePaid.compareToIgnoreCase(" ") != 0)
								System.out.println(" Date Paid: " + i.datePaid);
							if(i.methodPaid.compareToIgnoreCase(" ") != 0)
								System.out.println(" Method Paid: " + i.methodPaid);
						}
						else
							System.out.println("No current invoices. ");

						rset1.close();
					}
					else
						System.out.println("No current invoices. ");

					rset.close();
					
					break;
				case 2:
					/*cstmt = con.prepareCall("{call uha_invoice_creation_proc}");
					cstmt.execute();
					cstmt = con.prepareCall("{call uha_invoice_late_fees}");
					cstmt.execute();*/
					
					//find current invoice
					String civ = " "; 
					ResultSet rset2 = stmt.executeQuery("select lease_no from uha_lease where applicant_no = '" + app_id + "' and request_status = 'INPROGRESS' ");
					if(rset2.next()){
						ln = rset2.getString(1);
						ResultSet rset0 = stmt.executeQuery("select invoice_no from (select invoice_no from uha_invoice where applicant_no = '" + app_id +
								"' and lease_no = '" + ln + "' and sysdate - date_billed <= 31 order by date_billed desc) where rownum = 1 ");
						if(rset0.next()){
							civ = rset0.getString(1);
						}
					}
					
					int ni = 0;
					
					String qq = "select * from uha_invoice where applicant_no = '" + app_id + "' ";
					if(civ.compareToIgnoreCase(" ") != 0){
						qq += " and invoice_no <> '" + civ + "' ";
					}
					qq += " order by date_billed desc ";
					
					ResultSet rset1 = stmt.executeQuery(qq);
					while(rset1.next()){
						ni++;
					}
					rset1.beforeFirst();
					
					Invoice[] ii = new Invoice[ni]; 
					int nil = 0;
					
					while(rset1.next()){
						ii[nil] = new Invoice();
						ii[nil].invoiceNo = rset1.getString(1);
						ii[nil].leaseNo = rset1.getString(2);
						ii[nil].type = rset1.getString(4);
						ii[nil].housing = rset1.getString(5)!=null?Double.parseDouble(rset1.getString(5)):0;
						ii[nil].parking = rset1.getString(6)!=null?Double.parseDouble(rset1.getString(6)):0;
						ii[nil].penalty = rset1.getString(7)!=null?Double.parseDouble(rset1.getString(7)):0;
						ii[nil].totfees = rset1.getString(12)!=null?Double.parseDouble(rset1.getString(12)):0;
						ii[nil].dateBilled = rset1.getString(13).substring(0, 11);
						ii[nil].dueDate = rset1.getString(8).substring(0, 11);
						ii[nil].status = rset1.getString(11);
						ii[nil].datePaid = rset1.getString(9)!=null?rset1.getString(9).substring(0, 11):" ";
						ii[nil].methodPaid = rset1.getString(10)!=null?rset1.getString(10):" ";
						
						ii[nil].period = rset1.getString(13).substring(0, 7);
						
						nil++;
					}
					
					rset1.close();

					if(ni == 0)
						System.out.println("No former invoices. ");
					else{
						int ch = 0;
						do{
							ch = 0;
							System.out.println("Former invoices: \nChoose an invoice: ");
							for(int i = 0; i<ni; i++){
								System.out.println((i+1) + ". Invoice No: " + ii[i].invoiceNo + " (" + ii[i].period + ") ");
							}
							
							System.out.println("0. Back");
							
							int io = -1;
							
							try{
								io = Integer.parseInt(scn.nextLine());
								if(io<0 || io > ni)
									throw new NumberFormatException();
							}
							catch(NumberFormatException e){
								System.out.println("Invalid choice. Try again. ");
							}
							
							switch(io){
							case 0:
								ch = 0;
								mark = 1;
								break;
							default:
								ch = 1;
								io -= 1;
								System.out.println(" Invoice No: " + ii[io].invoiceNo + "\n Lease No: " + ii[io].leaseNo + "\n Period: " + ii[io].period + "\n Type: " + ii[io].type);
								if(ii[io].housing != 0)
									System.out.println(" Housing: " + ii[io].housing);
								if(ii[io].parking != 0)
									System.out.println(" Parking: " + ii[io].parking);
								if(ii[io].penalty != 0) 
									System.out.println(" Penalty: " + ii[io].penalty);
								
								System.out.println(" Total Fees: " + ii[io].totfees + "\n Date Billed: " + ii[io].dateBilled + "\n Due Date: " + ii[io].dueDate + "\n Status: " + ii[io].status);
								
								if(ii[io].datePaid.compareToIgnoreCase(" ") != 0)
									System.out.println(" Date Paid: " + ii[io].datePaid);
								if(ii[io].methodPaid.compareToIgnoreCase(" ") != 0)
									System.out.println(" Method Paid: " + ii[io].methodPaid);
							}
							
							if(ch != 0){
								int cc = 0;
								do{
									cc = 0;
									System.out.print("\nTo go back, press b. \n");
									String bb = scn.nextLine();
									if(bb.compareToIgnoreCase("b") == 0){
										cc = 0;
									}
									else{
										cc = 1;
										ch = 1;
									}
								}while(cc != 0);
							}
							
						}while(ch != 0);
					}

					break;
				case 3:
					mark = 2;
					break;
				default:
					System.out.println("Invalid choice. Try again. ");
					mark = 1;
				}
			}
			
			if(mark == 0){
				int m = 0;
				do{
					m = 0;
					System.out.println("\n To go back, press b. ");
					String vv = scn.nextLine();
					if(vv.compareToIgnoreCase("b") == 0){
						mark = 1;
					}
					else{
						m = 1;
					}
				}while(m != 0);
			}
			else if(mark == 2)
				mark = 0;

		}while(mark != 0);
	}
	
	public void ViewLeases(String app_id, String app_name, int user_type) throws SQLException{

		int choice = 0, mark = 0;
		
		do{
			mark = 0;
			System.out.println("\nLeases of " + app_name + ": \n 1. View Current Lease \n 2. View Former Leases \n 3. Back ");
			try{
				choice = Integer.parseInt(scn.nextLine());
			}
			catch(Exception e){
				System.out.println("Invalid choice. Try again. ");
				mark = 1;
			}
			
			if(mark != 1){
				switch(choice){
				case 1:
					ResultSet rset = stmt.executeQuery("select * from uha_lease where request_status = 'INPROGRESS' and applicant_no = '" + app_id + "' ");
					Lease l = new Lease();
					while(rset.next()){
						l.leaseNo = rset.getString(1);
						l.duration = Integer.parseInt(rset.getString(2));
						l.placeNo = rset.getString(4);
						l.roomNo = rset.getString(5);
						l.aptNo = rset.getString(6);
						l.addressID = rset.getString(7);
						l.secDep = Integer.parseInt(rset.getString(8));
						l.startDate = rset.getString(9).substring(0, 11);
						l.leaveDate = rset.getString(10).substring(0, 11);
						l.paymentSchedule = rset.getString(11);
						l.requestStatus = rset.getString(12);
						l.aptType = rset.getString(13);
						l.totalRent = Integer.parseInt(rset.getString(14));
						l.reqdOn = rset.getString(15)!=null?rset.getString(15).substring(0, 11):" ";
					}
					rset.close();
					
					if(l.leaseNo.compareTo(" ") == 0){
						System.out.println("No current lease. ");
					}
					else{
						ResultSet rset1 = stmt.executeQuery("select * from uha_housing_address where address_id = '" + l.addressID + "' ");
						while(rset1.next()){
							String st = rset1.getString(1)!=null?rset1.getString(1) + ", ":"";
							String ct = rset1.getString(2)!=null?rset1.getString(2) + ", ":"";
							String pc = rset1.getString(3)!=null?rset1.getString(3):"";
							l.addr = st + ct + pc; 
						}
						rset1.close();
						
						System.out.println("Current Lease Information: \n Lease No: " + l.leaseNo);
						if(user_type == 1){
							System.out.println(" Student No: " + app_id);
						}
						System.out.println(" Tennant Name: " + app_name + "\n Duration: " + l.duration + " months \n Address: ");
						if(l.aptType.compareToIgnoreCase("RH") == 0){
							System.out.println(" Place No: " + l.placeNo + "\n Room No: " + l.roomNo);
						}
						else if(l.aptType.compareToIgnoreCase("GA") == 0){
							System.out.println(" Apartment No: " + l.aptNo + "\n Place No: " + l.placeNo + "\n Room No: " + l.roomNo);
						}
						else{
							System.out.println(" Apartment No: " + l.aptNo);
						}
						System.out.print(" " + l.addr + "\n Enter date: " + l.startDate + "\n Leave date: " + l.leaveDate + "\n Payment Schedule: " + l.paymentSchedule
											+ "\n Security Deposit: " + l.secDep + "\n Requested On: " + l.reqdOn);
						
					}
					break;
				case 2:
					int ch = 0;
					do{
						ch = 0;
						ResultSet rset3 = stmt.executeQuery("select * from uha_lease where request_status = 'COMPLETED' and applicant_no = '" + app_id + "' ");
						
						int jj = 0;
						while(rset3.next()){
							if(jj == 0){
								System.out.println("Select lease to be viewed: ");
							}
							System.out.println(++jj + ". Lease No: " + rset3.getString(1) + " Period: " + rset3.getString(9).substring(0, 11) + " - " + rset3.getString(10).substring(0, 11));
						}
						
						rset3.beforeFirst();
						
						int mch = 0;
						
						if(jj == 0){
							System.out.println("No former leases. ");
							ch = 2;
						}
						else{
							System.out.println("0. Back ");
							
							String m = scn.nextLine();
							try{
								mch = Integer.parseInt(m);
								
								if(mch < 0 || mch > jj)
									throw new NumberFormatException();
								
								if(mch == 0){
									ch = 2;
									mark = 1;
								}
							}
							catch(Exception e){
								ch = 3;
								System.out.println("Invalid choice. Try again. \n");
							}
						}
						
						if(ch == 0){
							Lease ll = new Lease();
							int jk = 0;
							while(rset3.next()){
								jk++;
								if(jk == mch){
									ll.leaseNo = rset3.getString(1);
									ll.duration = Integer.parseInt(rset3.getString(2));
									ll.placeNo = rset3.getString(4);
									ll.roomNo = rset3.getString(5);
									ll.aptNo = rset3.getString(6);
									ll.addressID = rset3.getString(7);
									ll.secDep = Integer.parseInt(rset3.getString(8));
									ll.startDate = rset3.getString(9).substring(0, 11);
									ll.leaveDate = rset3.getString(10).substring(0, 11);
									ll.paymentSchedule = rset3.getString(11);
									ll.requestStatus = rset3.getString(12);
									ll.aptType = rset3.getString(13);
									ll.totalRent = Integer.parseInt(rset3.getString(14));
									ll.reqdOn = rset3.getString(15)!=null?rset3.getString(15).substring(0, 11):" ";
									break;
								}
							}
							
							rset3.close();
							
							ResultSet rset4 = stmt.executeQuery("select * from uha_housing_address where address_id = '" + ll.addressID + "' ");
							while(rset4.next()){
								String st = rset4.getString(1)!=null?rset4.getString(1) + ", ":"";
								String ct = rset4.getString(2)!=null?rset4.getString(2) + ", ":"";
								String pc = rset4.getString(3)!=null?rset4.getString(3):"";
								ll.addr = st + ct + pc; 
							}
							rset4.close();
							
							System.out.println("Lease Information: \n Lease No: " + ll.leaseNo);
							if(user_type == 1){
								System.out.println(" Student No: " + app_id);
							}
							System.out.println(" Tennant Name: " + app_name + "\n Duration: " + ll.duration + " months \n Address: ");
							if(ll.aptType.compareToIgnoreCase("RH") == 0){
								System.out.println(" Place No: " + ll.placeNo + "\n Room No: " + ll.roomNo);
							}
							else if(ll.aptType.compareToIgnoreCase("GA") == 0){
								System.out.println(" Apartment No: " + ll.aptNo + "\n Place No: " + ll.placeNo + "\n Room No: " + ll.roomNo);
							}
							else{
								System.out.println(" Apartment No: " + ll.aptNo);
							}
							System.out.println(" " + ll.addr + "\n Enter date: " + ll.startDate + "\n Leave date: " + ll.leaveDate + "\n Payment Schedule: " + ll.paymentSchedule
												+ "\n Security Deposit: " + ll.secDep + "\n Requested On: " + ll.reqdOn + "\n Total rent: " + ll.totalRent);

						}
						
						if(!rset3.isClosed()){
							rset3.close();
						}
						
						if(ch == 2){
							ch = 0;
						}
						else if(ch == 3){
							ch = 1;
						}
						else{
							int nn = 0;
							do{
								nn = 0;
								System.out.println("\n To go back, press b. ");
								String lk = scn.nextLine();
								if(lk.compareToIgnoreCase("b") == 0){
									ch = 1;
								}
								else{
									nn = 1;
									System.out.println("Invalid choice. Try again. ");
								}
							}while(nn != 0);
						}

					}while(ch != 0);
					break;
				case 3:
					mark = 2;
					break;
				default:
					System.out.println("Invalid choice. Try again. ");
					mark = 1;
				}
			}
			
			if(mark == 0){
				int m = 0;
				do{
					m = 0;
					System.out.println("\n To go back, press b. ");
					String vv = scn.nextLine();
					if(vv.compareToIgnoreCase("b") == 0){
						mark = 1;
					}
					else{
						m = 1;
					}
				}while(m != 0);
			}
			else if(mark == 2)
				mark = 0;

		}while(mark != 0);
	}
	
	public void NewRequest(String app_id, String app_name, int user_type) throws SQLException{
		
		int choice = 0, mark = 0;
		do{
			mark = 0;
			System.out.println(" 1. New Lease Request \n 2. Terminate Lease Request \n 3. Back ");
			try{
				choice = Integer.parseInt(scn.nextLine());
			}
			catch(Exception e){
				System.out.println("Invalid choice. Try again. ");
				mark = 1;
			}
			
			if(mark != 1){
				switch(choice){
				case 1:
					System.out.println("Enter details for new lease request: ");
					
					String dt = " ", dr = " ", ps = " ";

					if(user_type == 2){
						System.out.print("Enter start date (dd-MON-yy): ");
						dt = scn.nextLine();
						System.out.print("Enter duration (number of months): ");
						dr = scn.nextLine();
						System.out.print("Enter payment option (monthly): ");
						ps = scn.nextLine().toUpperCase();
					}
					else if(user_type == 1){
						int st = 0, dn = 0;
						System.out.print("Enter starting semester: \n 1. Fall 2. Spring ");
						st = Integer.parseInt(scn.nextLine());
						System.out.print("Enter duration (number of semesters - max 3): ");
						dn = Integer.parseInt(scn.nextLine());
						System.out.print("Enter payment option (monthly or semesterly): ");
						ps = scn.nextLine().toUpperCase();
						
						switch(st){
						case 1: 	dt = "1-AUG-15";
							switch(dn){
							case 1:		dr = "5";  break;
							case 2: 	dr = "10"; break;
							case 3: 	dr = "12"; break;
							}
							break;
						case 2:
							dt = "1-JAN-15";
							switch(dn){
							case 1:		dr = "5";  break;
							case 2: 	dr = "7"; break;
							case 3: 	dr = "12"; break;
							}
							break;
						}
						
					}
					
					String ln = " ";
					
					LeasePref[] lp = new LeasePref[3];
					
					for(int i=0; i<3; i++){
						lp[i] = new LeasePref();
						System.out.println("Enter details for preference " + (i+1) + ": \n 1. Residence Hall \n 2. General Apartment \n 3. Family Apartment");
						int at = Integer.parseInt(scn.nextLine());
						if(at == 1){
							lp[i].aptType = "RH";
							ResultSet rset = stmt.executeQuery("select rh.name from uha_housing_residence_hall rh");
							int j = 0;
							while(rset.next()){
								System.out.println(++j + ". " + rset.getString(1));
							}
							rset.beforeFirst();
							
							int rp = Integer.parseInt(scn.nextLine());
							
							int k = 0;
							while(rset.next()){
								k++;
								if(k == rp){
									lp[i].hallName = rset.getString(1);
									break;
								}
							}
							rset.close();
						}
						
						else if(at == 2){
							lp[i].aptType = "GA";
							ResultSet rset = stmt.executeQuery("select ap.apartment_no, ap.address_id from uha_apartment ap where ap.apartment_type = 'GA'");
							int j = 0;
							while(rset.next()){
								ResultSet rset1 = stmt2.executeQuery("select housing_name from uha_housing_address where address_id = '" + rset.getString(2) + "' ");
								
								if(rset1.next()){
									System.out.println(++j + ". " + rset.getString(1) + ", " + rset1.getString(1));
								}
								rset1.close();
							}
							rset.beforeFirst();
							
							int rp = Integer.parseInt(scn.nextLine());
							
							int k = 0;
							while(rset.next()){
								k++;
								if(k == rp){
									lp[i].aptNo = rset.getString(1);
									break;
								}
							}
							rset.close();
						}

						else if(at == 3){
							lp[i].aptType = "FA";
							ResultSet rset = stmt.executeQuery("select ap.apartment_no, ap.address_id from uha_apartment ap where ap.apartment_type = 'FA'");
							int j = 0;
							while(rset.next()){
								ResultSet rset1 = stmt2.executeQuery("select housing_name from uha_housing_address where address_id = '" + rset.getString(2) + "' ");
								
								if(rset1.next()){
									System.out.println(++j + ". " + rset.getString(1) + ", " + rset1.getString(1));
								}
								rset1.close();
							}
							rset.beforeFirst();
							
							int rp = Integer.parseInt(scn.nextLine());
							
							int k = 0;
							while(rset.next()){
								k++;
								if(k == rp){
									lp[i].aptNo = rset.getString(1);
									break;
								}
							}
							rset.close();
						}
					}	
					
					int no = 0;
					do{
						no = 0;
						System.out.print("To submit, press s. To go back, press b. ");
						String mm = scn.nextLine();
						if(mm.compareToIgnoreCase("s") == 0){
							int er = 0;
							try{
								con.setAutoCommit(false);
								DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
								Date dateobj = new Date();
								String reqdDate = df.format(dateobj);
								
								stmt.executeUpdate("insert into uha_lease (duration, applicant_no, enter_date, payment_schedule_option, requested_on) values ('" 
										+ dr + "', '" + app_id  + "', '" + dt + "', '" + ps + "', '" + reqdDate + "' )");
								er = 1;
								
								cstmt = con.prepareCall("{call uha_lease_overlap_proc (?, ?, ?)}");
								cstmt.setString(1, app_id);
								cstmt.setString(2, dt);
								cstmt.setString(3, dr);
								
								cstmt.execute();
								
								ResultSet rset0 = stmt.executeQuery("select lease_no from uha_lease where applicant_no = '" + app_id + "' and enter_date = '" + dt + "' ");
								
								if(rset0.next()){
									ln = rset0.getString(1);
								}
								
								for(int i=0; i<3; i++){
									if(lp[i].aptType.compareToIgnoreCase("RH") == 0){
										stmt.executeUpdate("insert into uha_lease_request (lease_no, preference, apartment_type, reshall_name, applicant_no) values ('" + 
												ln + "', '" + (i+1) + "', '" + lp[i].aptType + "', '" + lp[i].hallName + "', '" + app_id + "' )");
										
										cstmt = con.prepareCall("{call uha_lease_rooms_full_proc (?, ?, ?, ?, ?)}");
										cstmt.setString(1, dt);
										cstmt.setString(2, dr);
										cstmt.setString(3, lp[i].aptType);
										cstmt.setString(4, lp[i].hallName);
										cstmt.setNull(5, Types.VARCHAR);
									}
									else{
										stmt.executeUpdate("insert into uha_lease_request (lease_no, preference, apartment_type, apartment_no, applicant_no) values ('" + 
												ln + "', '" + (i+1) + "', '" + lp[i].aptType + "', '" + lp[i].aptNo + "', '" + app_id + "' )");
										
										cstmt = con.prepareCall("{call uha_lease_rooms_full_proc (?, ?, ?, ?, ?)}");
										cstmt.setString(1, dt);
										cstmt.setString(2, dr);
										cstmt.setString(3, lp[i].aptType);
										cstmt.setNull(4, Types.VARCHAR);
										cstmt.setString(5, lp[i].aptNo);
									}
									
									cstmt.execute();
								}
								
								con.commit();
								
								cstmt = con.prepareCall("{call uha_lease_placing_proc (?)}");
								cstmt.setString(1, ln);
								
								cstmt.execute();
							}
							catch(SQLException e){
								if(er == 0){
									System.out.println(e);
									mark = 1;
								}
								if(er == 1){
									System.out.println("Invalid entry. Try again. " + e + "\n");
									mark = 1;
								}
								con.rollback();
							}

							con.setAutoCommit(true);
						}
						else if(mm.compareToIgnoreCase("b") == 0){
							mark = 1;
						}
						else{
							no = 1;
							System.out.println("Invalid choice. Try again. ");
						}
						System.out.println("\n");
					}while(no != 0);
					
					if(mark == 1){
						continue;
					}
				
					break;
	
				case 2:
					System.out.println("Enter details for termination lease request: (Requires 1 month's notice period) ");
					
					System.out.print("Enter termination date (dd-MON-yy): ");
					String tdt = scn.nextLine();
					System.out.print("Enter termination reason (200 chars max): ");
					String tmr = scn.nextLine();
					
					int no1 = 0;
					do{
						no1 = 0;
						System.out.print("To submit, press s. To go back, press b. ");
						String mm = scn.nextLine();
						if(mm.compareToIgnoreCase("s") == 0){
							ResultSet rset = stmt.executeQuery("select lease_no from uha_lease where applicant_no = '" + app_id + "' and request_status = 'INPROGRESS' ");
							
							if(rset.next()){
								String lnn = rset.getString(1);
								try{
									stmt.executeUpdate("insert into uha_lease_termination (lease_no, termination_date, termination_reason) values ('" + lnn + 
															"', '" + tdt + "', '" + tmr + "' )");
								}
								catch(SQLException e){
									System.out.println(e);
								}
							}
							else{
								System.out.println("There is no current lease which can be terminated. ");
							}
						}
						else if(mm.compareToIgnoreCase("b") == 0){
							mark = 1;
						}
						else{
							no1 = 1;
							System.out.println("Invalid choice. Try again. ");
						}
						System.out.println("\n");
					}while(no1 != 0);
					
					if(mark == 1){
						continue;
					}
				
					break;
					
				case 3:
					mark = 2;
					break;
				default:
					System.out.println("Invalid choice. Try again. ");
					mark = 1;
				}
			}
			
			if(mark == 0){
				mark = 1;
			}
			else if(mark == 2)
				mark = 0;
			
		}while(mark != 0);
	}
	
	public void ViewCancelRequest(String app_id, String app_name, int user_type) throws SQLException{
		
		int choice = 0, mark = 0;
		do{
			mark = 0;
			System.out.println(" 1. View Request \n 2. Cancel Request \n 3. Back ");
			try{
				choice = Integer.parseInt(scn.nextLine());
			}
			catch(Exception e){
				System.out.println("Invalid choice. Try again. ");
				mark = 1;
			}
			
			if(mark != 1){
				int lt = 0;

				switch(choice){
				case 1:
					lt = 0;
					ResultSet rset1 = stmt.executeQuery("select * from uha_lease where applicant_no = '" + app_id + "' and request_status in ('PENDING', 'WAITING', 'APPROVED')");
					if(rset1.next()){
						lt++;
						Lease l = new Lease();
						l.leaseNo = rset1.getString(1);
						l.duration = Integer.parseInt(rset1.getString(2));
						l.startDate = rset1.getString(9)!=null?rset1.getString(9).substring(0, 11):" ";
						l.leaveDate = rset1.getString(10)!=null?rset1.getString(10).substring(0, 11):" ";
						l.paymentSchedule = rset1.getString(11);
						l.requestStatus = rset1.getString(12);
						l.reqdOn = rset1.getString(15)!=null?rset1.getString(15).substring(0, 11):" ";
				
						System.out.println("Lease Request Information: \n Lease No: " + l.leaseNo + "\n Request No: " + l.leaseNo + "\n Duration: " + l.duration + " months \n Applicant No: " + 
												app_id + "\n Request Status: " + l.requestStatus + "\n Start Date: " + l.startDate + 
												"\n End Date: " + l.leaveDate + "\n Payment Schedule: " + l.paymentSchedule + "\n Requested On: " + l.reqdOn);
						rset1.close();
					}
					
					ResultSet rset = stmt.executeQuery("select lease_no from uha_lease where applicant_no = '" + app_id + "' and request_status in ('INPROGRESS')");
					String ln = "";
					if(rset.next()){
						ln = rset.getString(1);
					}
					
					ResultSet rset2 = stmt.executeQuery("select * from uha_lease_termination where lease_no = '" + ln + "' and request_status in ('PENDING', 'APPROVED')");
					if(rset2.next()){
						lt++;
						TerminateLease tl = new TerminateLease();
						tl.leaseNo = ln;
						tl.requestNo = rset2.getString(1);
						tl.requestStatus = rset2.getString(3);
						tl.termntDate = rset2.getString(4)!=null?rset2.getString(4).substring(0, 11):" ";
						tl.inspecDate = rset2.getString(5)!=null?rset2.getString(5).substring(0, 11):" ";
						tl.inspecComments = rset2.getString(6);
						tl.feesLevied = rset2.getString(7);
						tl.termReason =  rset2.getString(8);
						
						System.out.println("Terminate Lease Request Information: \n Lease No: " + tl.leaseNo + "\n Request No: " + tl.requestNo + "\n Request Status: " + 
												tl.requestStatus + "\n Termination Date: " + tl.termntDate + "\n Termination Reason: " + tl.termReason + "\n Inspection Date: " +
												tl.inspecDate + "\n Inspection Comments: " + tl.inspecComments + "\n Fees Levied by Inspection: " + tl.feesLevied);
						rset2.close();
					}
					
					if(lt == 0){
						System.out.println("No lease requests or terminate lease requests raised by " + app_name);
					}
					mark = 1;
					break;
				case 2:
					lt = 0;
					int cc = 0;
					//on cancel, change status to cancelled 
					do{
						cc = 0;
						System.out.println(" 1. Cancel lease request \n 2. Cancel terminate lease request \n 3. Back ");
						String o = scn.nextLine();
						int oc = 0;
						try{
							oc = Integer.parseInt(o);
						}
						catch(Exception e){
							System.out.println("Invalid choice. Try again. ");
							cc = 1;
						}
						if(oc == 1){
							System.out.print("Enter lease request number to be canceled: ");
							String req = scn.nextLine();
							ResultSet rset3 = stmt.executeQuery("select * from uha_lease where lease_no = '" + req + "' and request_status in ('PENDING', 'WAITING', 'APPROVED') ");
							if(rset3.next()){
								try{
									stmt.executeUpdate("update uha_lease set place_no = null, room_no = null, apartment_no = null, address_id = null, " + 
											"security_deposit = null, request_status = 'CANCELED', apartment_type = null, total_rent = null where lease_no = '" + req + "' ");
									
									stmt.executeUpdate("delete from uha_lease_request where lease_no = '" + req + "' ");
									
									System.out.println("The lease with request number " + req + " has been canceled. ");
								}
								catch(Exception e){
									System.out.println(e);
									System.out.println("The request number: " + req + " cannot be cancelled. ");
								}
							}
							else{
								System.out.println("The request number: " + req + " cannot be cancelled. ");
							}
							cc = 0;
						}
						else if(oc == 2){
							System.out.print("Enter terminate lease request number to be canceled: ");
							String req = scn.nextLine();
							ResultSet rset3 = stmt.executeQuery("select * from uha_lease_termination where termination_request_no = '" + req + 
																	"' and request_status in ('PENDING', 'APPROVED') ");
							if(rset3.next()){
								try{
									stmt.executeUpdate("update uha_lease_termination set request_status = 'CANCELED' where termination_request_no = '" + req + "' ");
									
									System.out.println("The terminate lease request with request number " + req + " has been canceled. ");
								}
								catch(Exception e){
									System.out.println(e);
									System.out.println("The request number: " + req + " cannot be cancelled. ");
								}
							}
							else{
								System.out.println("The request number: " + req + " cannot be cancelled. ");
							}
							cc = 0;
						}
						else if(oc != 3)
							cc = 1;
						
					}while(cc != 0);
					mark = 1;
					break;
				case 3:
					mark = 2;
					break;
				default:
					System.out.println("Invalid choice. Try again. ");
					mark = 1;
				}
				
				if(mark == 2){
					mark = 0;
				}
				else{
					int m = 0;
					do{
						m = 0;
						System.out.println("\n To go back, press b. ");
						String vv = scn.nextLine();
						if(vv.compareToIgnoreCase("b") == 0){System.out.println();}
						else{
							m = 1;
						}
					}while(m != 0);
				}
			}
		}while(mark != 0);
	}
	
	public void Vacancy() throws SQLException{
		int choice = 0, mark = 0;
		
		do{
			mark = 0;
			System.out.println(" 1. Residence Halls \n 2. On Campus General Apartment \n 3. On Campus Family Apartment " + 
								"\n 4. Off Campus (Private) General Apartment \n 5. Off Campus (Private) Family Apartment \n 6. Back ");
			try{
				choice = Integer.parseInt(scn.nextLine());
			}
			catch(Exception e){
				System.out.println("Invalid choice. Try again. ");
				mark = 1;
			}
			
			if(mark != 1){
				switch(choice){
				case 1:
					System.out.println("Residence Halls: ");
					ResultSet rset = stmt.executeQuery("select * from uha_housing_residence_hall");
					int nRH = 0;
					while(rset.next()){
						nRH++;
					}
					rset.beforeFirst();
					ResHall[] rhs = new ResHall[nRH];
					
					int i = 0;
					while(rset.next()){
						rhs[i] = new ResHall();
						rhs[i].name = rset.getString(1);
						rhs[i].phoneNo = rset.getString(3)!=null?rset.getString(3):" ";
						rhs[i].addressID = rset.getString(2);
						rhs[i].hallManager = rset.getString(4);
						String nRooms = rset.getString(5);
						rhs[i].noOfRooms = Integer.parseInt(nRooms);
						rhs[i].rhrs = new ResHallRoom[rhs[i].noOfRooms];
						String gradUpper = rset.getString(6);
						rhs[i].gradUpperOnly = gradUpper.contains("Y")?true:false;
						rhs[i].secDep = Integer.parseInt(rset.getString(7));
						i++;
					}
					
					rset.close();
					
					for(int j=0; j<nRH; j++){
						
						ResultSet rset0 = stmt.executeQuery("select * from uha_admin where staff_no = '" + rhs[j].hallManager + "' ");
						if(rset0.next()){
							rhs[j].hallManager = rset0.getString(2) + " " + rset0.getString(7);
						}
						rset0.close();
						
						ResultSet rset1 = stmt.executeQuery("select * from uha_housing_address where address_id = '" + rhs[j].addressID + "' ");
						while(rset1.next()){
							String hn = rset1.getString(5)!=null?rset1.getString(5) + ", ":"";
							String st = rset1.getString(2)!=null?rset1.getString(2) + ", ":"";
							String ct = rset1.getString(3)!=null?rset1.getString(3) + ", ":"";
							String pc = rset1.getString(4)!=null?rset1.getString(4) + ", ":"";
							String tt = rset1.getString(6)!=null?rset1.getString(6):"";
							rhs[j].addr = hn + st + ct + pc + tt; 
						}
						rset1.close();

						ResultSet rset2 = stmt.executeQuery("select * from uha_residence_hall_room where residence_hall_name = '" + rhs[j].name + "' ");
						int k = 0;
						while(rset2.next()){
							rhs[j].rhrs[k] = new ResHallRoom();
							rhs[j].rhrs[k].placeNo = rset2.getString(1);
							rhs[j].rhrs[k].roomNo = rset2.getString(3);
							String rate = rset2.getString(4);
							rhs[j].rhrs[k].monthlyRentRate = Integer.parseInt(rate);
							k++;
						}
						rset2.close();
						
						for(int k1 = 0; k1<k; k1++){
							ResultSet r1 = stmt.executeQuery("select leave_date from (select leave_date from uha_lease where place_no = '" + rhs[j].rhrs[k1].placeNo +  
												"' and apartment_type = 'RH' and leave_date > sysdate order by leave_date desc) where rownum = 1"); 
							if(r1.next()){
								ResultSet r2 = stmt.executeQuery("WITH CTE AS (SELECT enter_DATE, leave_DATE, ROWNUM AS RN " + 
													"FROM ( SELECT enter_DATE, leave_DATE FROM uha_lease where place_no = '" + rhs[j].rhrs[k1].placeNo + 
													"' and apartment_type = 'RH' and leave_date > sysdate ORDER BY 1,2) ) " + 
													"SELECT T1.leave_DATE, T2.enter_DATE FROM CTE T1 JOIN CTE T2 ON T2.RN=T1.RN+1");
								while(r2.next()){
									rhs[j].rhrs[k1].timeps++;
								}
								r2.beforeFirst();
								
								rhs[j].rhrs[k1].timeps += 2;
								
								rhs[j].rhrs[k1].ps = new Period[rhs[j].rhrs[k1].timeps];
								rhs[j].rhrs[k1].ps[0] = new Period();
								int pps = 1;
								while(r2.next()){
									rhs[j].rhrs[k1].ps[pps] = new Period();
									rhs[j].rhrs[k1].ps[pps].from = r2.getString(1).substring(0, 11);
									rhs[j].rhrs[k1].ps[pps].to = r2.getString(2).substring(0, 11);
									pps++;
								}
								
								r2.close();

								ResultSet r3 = stmt.executeQuery("select lease_no from uha_lease where place_no = '" +  rhs[j].rhrs[k1].placeNo + 
													"' and apartment_type = 'RH' and enter_date < sysdate and leave_date > sysdate");
								if(!r3.next()){

									ResultSet r4 = stmt.executeQuery("select sysdate, enter_date from (select sysdate, enter_date from uha_lease where place_no = '" + 
														rhs[j].rhrs[k1].placeNo + "' and apartment_type = 'RH' and enter_date > sysdate order by enter_date asc) where rownum = 1");
								
									if(r4.next()){
										rhs[j].rhrs[k1].ps[0].from = r4.getString(1).substring(0, 11);
										rhs[j].rhrs[k1].ps[0].to = r4.getString(2).substring(0, 11);
									}
									
									r4.close();
								}
								
								r3.close();
								
								ResultSet r5 = stmt.executeQuery("select leave_date from (select leave_date from uha_lease where place_no = '" + 
														rhs[j].rhrs[k1].placeNo + "' and apartment_type = 'RH' and leave_date > sysdate order by leave_date desc) where rownum = 1");
										
								if(r5.next()){
									rhs[j].rhrs[k1].ps[rhs[j].rhrs[k1].timeps - 1] = new Period();
									rhs[j].rhrs[k1].ps[rhs[j].rhrs[k1].timeps - 1].from = r5.getString(1).substring(0, 11);
								}
								
								r5.close();
							}
							else{
								//vacant
								rhs[j].rhrs[k1].timeps = 0;
							}
							
							r1.close();
						}
					}
					
					int oo = 0;
					for(int m = 0; m<nRH; m++){
						System.out.println((++oo) + ". " + rhs[m].name + "\nAddress: " + rhs[m].addr + "\nPhone: " + rhs[m].phoneNo + 
												"\nHall Manager: " + rhs[m].hallManager + "\nTotal No. of rooms: " + rhs[m].noOfRooms + "\nSecurity Deposit: " + rhs[m].secDep);
						if(rhs[m].gradUpperOnly){
							System.out.println("Only for graduate students and upperclassmen. ");
						}
						
						int ll = 0;
						for(int o = 0; o<rhs[m].noOfRooms; o++){
							System.out.println("\n\t " + (++ll) + ". Place No: " + rhs[m].rhrs[o].placeNo + 
									"\t Room No: " + rhs[m].rhrs[o].roomNo + "\t Monthly Rent Rate: " + rhs[m].rhrs[o].monthlyRentRate);
							if(rhs[m].rhrs[o].timeps == 0){
								System.out.println("\tVacant. ");
							}
							else{
								System.out.println("\tVacant periods: ");
								for(int op = 0; op < rhs[m].rhrs[o].timeps - 1; op++){
									if(op == 0){
										if(rhs[m].rhrs[o].ps[0].from.compareToIgnoreCase(" ") != 0){
											System.out.println("\t" + rhs[m].rhrs[o].ps[0].from + " - " + rhs[m].rhrs[o].ps[0].to);
										}
									}
									else{
										System.out.println("\t" + rhs[m].rhrs[o].ps[op].from + " - " + rhs[m].rhrs[o].ps[op].to);
									}
								}
								System.out.println("\tVacant after " + rhs[m].rhrs[o].ps[rhs[m].rhrs[o].timeps - 1].from);
							}
						}
						System.out.println();
					}
					
					break;
					
				case 2:
				case 4:
					ResultSet rset3;
					if(choice == 2){
						System.out.println("On Campus General Apartments: ");
						rset3 = stmt.executeQuery("select * from uha_apartment where apartment_type = 'GA' and on_campus = 'Y' ");
					}
					else{
						System.out.println("Off Campus (Private) General Apartments: ");
						rset3 = stmt.executeQuery("select * from uha_apartment where apartment_type = 'GA' and on_campus = 'N' ");
					}
					
					int nGA = 0;
					while(rset3.next()){
						nGA++;
					}
					rset3.beforeFirst();
					Apartment[] gapt = new Apartment[nGA];
					
					int i1 = 0;
					while(rset3.next()){
						gapt[i1] = new Apartment();
						gapt[i1].aptType = "GA";
						
						if(choice == 2){
							gapt[i1].onCampus = true;
						}
						else{
							gapt[i1].onCampus = false;
						}
						gapt[i1].aptNo = rset3.getString(1);
						gapt[i1].addressID = rset3.getString(2);
						gapt[i1].noOfBeds = Integer.parseInt(rset3.getString(3));
						gapt[i1].aptrs = new AptRoom[gapt[i1].noOfBeds];
						gapt[i1].noOfBaths = rset3.getString(4)!=null?Integer.parseInt(rset3.getString(4)):0;
						String fa = rset3.getString(5);
						gapt[i1].freshmenAllowed = fa.contains("Y")?true:false;
						gapt[i1].secDep = Integer.parseInt(rset3.getString(8));
						gapt[i1].manager = rset3.getString(9);
						i1++;
					}
					
					rset3.close();
					
					for(int j1 = 0; j1<nGA; j1++){
						
						ResultSet rset0g = stmt.executeQuery("select * from uha_admin where staff_no = '" + gapt[j1].manager + "' ");
						if(rset0g.next()){
							gapt[j1].manager = rset0g.getString(2) + " " + rset0g.getString(7);
						}
						rset0g.close();
						
						ResultSet rset4 = stmt.executeQuery("select * from uha_housing_address where address_id = '" + gapt[j1].addressID + "' ");
						while(rset4.next()){
							String hn = rset4.getString(5)!=null?rset4.getString(5) + ", ":"";
							String st = rset4.getString(2)!=null?rset4.getString(2) + ", ":"";
							String ct = rset4.getString(3)!=null?rset4.getString(3) + ", ":"";
							String pc = rset4.getString(4)!=null?rset4.getString(4) + ", ":"";
							String tt = rset4.getString(6)!=null?rset4.getString(6):"";
							gapt[j1].addr = hn + st + ct + pc + tt; 
						}
						rset4.close();
						
						ResultSet rset5 = stmt.executeQuery("select * from uha_general_bedroom_apartment where apartment_no = '" + gapt[j1].aptNo + "' ");
						int k = 0;
						while(rset5.next()){
							gapt[j1].aptrs[k] = new AptRoom();
							gapt[j1].aptrs[k].placeNo = rset5.getString(1);
							gapt[j1].aptrs[k].roomNo = rset5.getString(2);
							gapt[j1].aptrs[k].monthlyRentRate = Integer.parseInt(rset5.getString(4));
							k++;
						}
						rset5.close();
						
						for(int k1 = 0; k1<k; k1++){
							ResultSet r1 = stmt.executeQuery("select leave_date from (select leave_date from uha_lease where place_no = '" + 
													gapt[j1].aptrs[k1].placeNo + "' and apartment_no = '" +  gapt[j1].aptNo + 
													"' and leave_date > sysdate order by leave_date desc) where rownum = 1"); 
							if(r1.next()){
								ResultSet r2 = stmt.executeQuery("WITH CTE AS (SELECT enter_DATE, leave_DATE, ROWNUM AS RN " + 
													"FROM ( SELECT enter_DATE, leave_DATE FROM uha_lease where place_no = '" + 
													gapt[j1].aptrs[k1].placeNo + "' and apartment_no = '" +  gapt[j1].aptNo + 
													"' and leave_date > sysdate ORDER BY 1,2) ) " + 
													"SELECT T1.leave_DATE, T2.enter_DATE FROM CTE T1 JOIN CTE T2 ON T2.RN=T1.RN+1");
								while(r2.next()){
									gapt[j1].aptrs[k1].timeps++;
								}
								r2.beforeFirst();
								
								gapt[j1].aptrs[k1].timeps += 2;
								
								gapt[j1].aptrs[k1].ps = new Period[gapt[j1].aptrs[k1].timeps];
								gapt[j1].aptrs[k1].ps[0] = new Period();
								int pps = 1;
								while(r2.next()){
									gapt[j1].aptrs[k1].ps[pps] = new Period();
									gapt[j1].aptrs[k1].ps[pps].from = r2.getString(1).substring(0, 11);
									gapt[j1].aptrs[k1].ps[pps].to = r2.getString(2).substring(0, 11);
									pps++;
								}
								
								r2.close();

								ResultSet r3 = stmt.executeQuery("select lease_no from uha_lease where place_no = '" +  
																gapt[j1].aptrs[k1].placeNo + "' and apartment_no = '" +  gapt[j1].aptNo + 
																"' and enter_date < sysdate and leave_date > sysdate");
								if(!r3.next()){

									ResultSet r4 = stmt.executeQuery("select sysdate, enter_date from (select sysdate, enter_date from uha_lease where place_no = '" + 
																	gapt[j1].aptrs[k1].placeNo + "' and apartment_no = '" +  gapt[j1].aptNo + 
																	"' and enter_date > sysdate order by enter_date asc) where rownum = 1");
								
									if(r4.next()){
										gapt[j1].aptrs[k1].ps[0].from = r4.getString(1).substring(0, 11);
										gapt[j1].aptrs[k1].ps[0].to = r4.getString(2).substring(0, 11);
									}
									
									r4.close();
								}
								
								r3.close();
								
								ResultSet r5 = stmt.executeQuery("select leave_date from (select leave_date from uha_lease where place_no = '" + 
																gapt[j1].aptrs[k1].placeNo + "' and apartment_no = '" +  gapt[j1].aptNo +  
																"' and leave_date > sysdate order by leave_date desc) where rownum = 1");
										
								if(r5.next()){
									gapt[j1].aptrs[k1].ps[gapt[j1].aptrs[k1].timeps - 1] = new Period();
									gapt[j1].aptrs[k1].ps[gapt[j1].aptrs[k1].timeps - 1].from = r5.getString(1).substring(0, 11);
								}
								
								r5.close();
							}
							else{
								//vacant
								gapt[j1].aptrs[k1].timeps = 0;
							}
							
							r1.close();
						}
					}
					
					int pp = 0;
					for(int m1 = 0; m1<nGA; m1++){
						System.out.println((++pp) + ". " + gapt[m1].aptNo + "\nManager: " + gapt[m1].manager + "\nAddress: " + gapt[m1].addr +  
									"\nNo. of beds: " + gapt[m1].noOfBeds + "\nSecurity Deposit: " + gapt[m1].secDep);
						if(!gapt[m1].onCampus){
							System.out.println("Off Campus Apartment. ");
						}
						if(!gapt[m1].freshmenAllowed){
							System.out.println("Freshmen not allowed. ");
						}
						
						int ll = 0;
						for(int o = 0; o<gapt[m1].noOfBeds; o++){
							System.out.println("\n\t " + (++ll) + ". Place No: " + gapt[m1].aptrs[o].placeNo + 
								"\t Room No: " + gapt[m1].aptrs[o].roomNo + "\t Monthly Rent Rate: " + gapt[m1].aptrs[o].monthlyRentRate);
							if(gapt[m1].aptrs[o].timeps == 0){
								System.out.println("\tVacant. ");
							}
							else{
								System.out.println("\tVacant periods: ");
								for(int op = 0; op < gapt[m1].aptrs[o].timeps - 1; op++){
									if(op == 0){
										if(gapt[m1].aptrs[o].ps[0].from.compareToIgnoreCase(" ") != 0){
											System.out.println("\t" + gapt[m1].aptrs[o].ps[0].from + " - " + gapt[m1].aptrs[o].ps[0].to);
										}
									}
									else{
										System.out.println("\t" + gapt[m1].aptrs[o].ps[op].from + " - " + gapt[m1].aptrs[o].ps[op].to);
									}
								}
								System.out.println("\tVacant after " + gapt[m1].aptrs[o].ps[gapt[m1].aptrs[o].timeps - 1].from);
							}
						}
						System.out.println();
					}
					
					break;
				case 3:
				case 5:
					ResultSet rset6;
					if(choice == 3){
						System.out.println("On Campus Family Apartments: ");
						rset6 = stmt.executeQuery("select * from uha_apartment where apartment_type = 'FA' and on_campus = 'Y' ");
					}
					else{
						System.out.println("Off Campus (Private) Family Apartments: ");
						rset6 = stmt.executeQuery("select * from uha_apartment where apartment_type = 'FA' and on_campus = 'N' ");
					}
					int nFA = 0;
					while(rset6.next()){
						nFA++;
					}
					rset6.beforeFirst();
					Apartment[] fapt = new Apartment[nFA];
					
					int i2 = 0;
					while(rset6.next()){
						fapt[i2] = new Apartment();
						fapt[i2].aptType = "FA";
						if(choice == 3){
							fapt[i2].onCampus = true;
						}
						else{
							fapt[i2].onCampus = false;
						}
						fapt[i2].aptNo = rset6.getString(1);
						fapt[i2].addressID = rset6.getString(2);
						fapt[i2].noOfBeds = Integer.parseInt(rset6.getString(3));
						fapt[i2].aptrs = new AptRoom[1];
						fapt[i2].noOfBaths = rset6.getString(4)!=null?Integer.parseInt(rset6.getString(4)):0;
						String fa = rset6.getString(5);
						fapt[i2].freshmenAllowed = fa.contains("Y")?true:false;
						fapt[i2].secDep = Integer.parseInt(rset6.getString(8));
						fapt[i2].manager = rset6.getString(9);
						i2++;
					}
					
					rset6.close();
					
					for(int j1 = 0; j1<nFA; j1++){
						
						ResultSet rset0f = stmt.executeQuery("select * from uha_admin where staff_no = '" + fapt[j1].manager + "' ");
						if(rset0f.next()){
							fapt[j1].manager = rset0f.getString(2) + " " + rset0f.getString(7);
						}
						rset0f.close();
						
						ResultSet rset7 = stmt.executeQuery("select * from uha_housing_address where address_id = '" + fapt[j1].addressID + "' ");
						while(rset7.next()){
							String hn = rset7.getString(5)!=null?rset7.getString(5) + ", ":"";
							String st = rset7.getString(2)!=null?rset7.getString(2) + ", ":"";
							String ct = rset7.getString(3)!=null?rset7.getString(3) + ", ":"";
							String pc = rset7.getString(4)!=null?rset7.getString(4) + ", ":"";
							String tt = rset7.getString(6)!=null?rset7.getString(6):"";
							fapt[j1].addr = hn + st + ct + pc + tt; 
						}
						rset7.close();
						
						ResultSet rset8 = stmt.executeQuery("select * from uha_family_apartment where apartment_no = '" + fapt[j1].aptNo + "' ");
						while(rset8.next()){
							fapt[j1].aptrs[0] = new AptRoom();
							fapt[j1].aptrs[0].monthlyRentRate = Integer.parseInt(rset8.getString(2));
						}
						
						rset8.close();
						
						ResultSet r1 = stmt.executeQuery("select leave_date from (select leave_date from uha_lease where apartment_no = '" +  fapt[j1].aptNo + 
								"' and leave_date > sysdate order by leave_date desc) where rownum = 1"); 
						if(r1.next()){
							ResultSet r2 = stmt.executeQuery("WITH CTE AS (SELECT enter_DATE, leave_DATE, ROWNUM AS RN " + 
												"FROM ( SELECT enter_DATE, leave_DATE FROM uha_lease where apartment_no = '" +  fapt[j1].aptNo + 
												"' and leave_date > sysdate ORDER BY 1,2) ) " + 
												"SELECT T1.leave_DATE, T2.enter_DATE FROM CTE T1 JOIN CTE T2 ON T2.RN=T1.RN+1");
							while(r2.next()){
								fapt[j1].aptrs[0].timeps++;
							}
							r2.beforeFirst();
							
							fapt[j1].aptrs[0].timeps += 2;
							
							fapt[j1].aptrs[0].ps = new Period[fapt[j1].aptrs[0].timeps];
							fapt[j1].aptrs[0].ps[0] = new Period();
							int pps = 1;
							while(r2.next()){
								fapt[j1].aptrs[0].ps[pps] = new Period();
								fapt[j1].aptrs[0].ps[pps].from = r2.getString(1).substring(0, 11);
								fapt[j1].aptrs[0].ps[pps].to = r2.getString(2).substring(0, 11);
								pps++;
							}
							
							r2.close();
				
							ResultSet r3 = stmt.executeQuery("select lease_no from uha_lease where apartment_no = '" +  fapt[j1].aptNo + 
															"' and enter_date < sysdate and leave_date > sysdate");
							if(!r3.next()){
				
								ResultSet r4 = stmt.executeQuery("select sysdate, enter_date from (select sysdate, enter_date from uha_lease where apartment_no = '" +  
																fapt[j1].aptNo + "' and enter_date > sysdate order by enter_date asc) where rownum = 1");
							
								if(r4.next()){
									fapt[j1].aptrs[0].ps[0].from = r4.getString(1).substring(0, 11);
									fapt[j1].aptrs[0].ps[0].to = r4.getString(2).substring(0, 11);
								}
								
								r4.close();
							}
							
							r3.close();
							
							ResultSet r5 = stmt.executeQuery("select leave_date from (select leave_date from uha_lease where apartment_no = '" +  fapt[j1].aptNo +  
															"' and leave_date > sysdate order by leave_date desc) where rownum = 1");
									
							if(r5.next()){
								fapt[j1].aptrs[0].ps[fapt[j1].aptrs[0].timeps - 1] = new Period();
								fapt[j1].aptrs[0].ps[fapt[j1].aptrs[0].timeps - 1].from = r5.getString(1).substring(0, 11);
							}
							
							r5.close();
						}
						else{
							//vacant
							fapt[j1].aptrs[0].timeps = 0;
						}
						
						r1.close();
					}
					
					int qq = 0;
					for(int m1 = 0; m1<nFA; m1++){
						System.out.println((++qq) + ". " + fapt[m1].aptNo + "\nManager: " + fapt[m1].manager + "\nAddress: " + fapt[m1].addr + "\nNo. of beds: " +   
								fapt[m1].noOfBeds + "\nMonthly Rent Rate: " + fapt[m1].aptrs[0].monthlyRentRate + "\nSecurity Deposit: " + fapt[m1].secDep);
								
						if(!fapt[m1].onCampus){
							System.out.println("Off Campus apartment. ");
						}
						if(!fapt[m1].freshmenAllowed){
							System.out.println("Freshmen not allowed. ");
						}
						
						if(fapt[m1].aptrs[0].timeps == 0){
							System.out.println("Vacant. ");
						}
						else{
							System.out.println("Vacant periods: ");
							for(int op = 0; op < fapt[m1].aptrs[0].timeps - 1; op++){
								if(op == 0){
									if(fapt[m1].aptrs[0].ps[0].from.compareToIgnoreCase(" ") != 0){
										System.out.println(fapt[m1].aptrs[0].ps[0].from + " - " + fapt[m1].aptrs[0].ps[0].to);
									}
								}
								else{
									System.out.println(fapt[m1].aptrs[0].ps[op].from + " - " + fapt[m1].aptrs[0].ps[op].to);
								}
							}
							System.out.println("Vacant after " + fapt[m1].aptrs[0].ps[fapt[m1].aptrs[0].timeps - 1].from);
						}
						
						System.out.println();
					}
					
					break;
				case 6:
					mark = 2;
					break;
				default:
					System.out.println("Invalid choice. Try again. ");
					mark = 1;
				}
				
				if(mark != 2){
					int m = 0;
					do{
						m = 0;
						System.out.println("To continue viewing vacancies, press v. To go back, press b. ");
						String vv = scn.nextLine();
						if(vv.compareToIgnoreCase("v") == 0){
							mark = 1;
						}
						else if(vv.compareToIgnoreCase("b") == 0){}
						else{
							m = 1;
						}
					}while(m != 0);
				}
				else
					mark = 0;
			}
		}while(mark != 0);
	}
}	
