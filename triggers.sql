--------------------------------------------------------
--  DDL for Trigger UHA_STAFF_NO_TRIGGER
--------------------------------------------------------

  CREATE OR REPLACE  TRIGGER "SYSTEM"."UHA_STAFF_NO_TRIGGER" 
BEFORE INSERT or update ON UHA_ADMIN
FOR EACH ROW BEGIN
  IF(:NEW.STAFF_NO IS NULL) THEN
    SELECT UHA_STAFF_NO_SEQUENCE.NEXTVAL INTO :NEW.STAFF_NO FROM DUAL;
  END IF;
END;
/
ALTER TRIGGER "SYSTEM"."UHA_STAFF_NO_TRIGGER" ENABLE;
--------------------------------------------------------
--  DDL for Trigger UHA_HOUSING_TRIGGER
--------------------------------------------------------

  CREATE OR REPLACE   TRIGGER "SYSTEM"."UHA_HOUSING_TRIGGER" 
BEFORE INSERT OR UPDATE ON UHA_APARTMENT
FOR EACH ROW BEGIN
  IF(:NEW.ADDRESS_ID IS NULL) THEN
    SELECT UHA_HOUSING_ADDR_ID_SEQUENCE.NEXTVAL INTO :NEW.ADDRESS_ID FROM DUAL;
  END IF;
  
  
  IF(:NEW.ON_CAMPUS = 'N' AND :NEW.FRESHMEN_ALLOWED = 'Y') THEN
    RAISE_APPLICATION_ERROR(-20005, 'OFF CAMPUS APARTMENTS ARE NOT AVAILABLE TO FRESHMEN');
  END IF;
END;
/
ALTER TRIGGER "SYSTEM"."UHA_HOUSING_TRIGGER" ENABLE;
--------------------------------------------------------
--  DDL for Trigger UHA_FAMILY_MEMBER_ID_TRIGGER
--------------------------------------------------------

  CREATE OR REPLACE   TRIGGER "SYSTEM"."UHA_FAMILY_MEMBER_ID_TRIGGER" 
BEFORE INSERT or UPDATE ON UHA_APPLICANT_FAMILY
FOR EACH ROW BEGIN
  IF(:NEW.family_member_ID IS NULL) THEN
    SELECT UHA_FAMILY_MEMBER_ID_SEQUENCE.NEXTVAL INTO :NEW.family_member_ID FROM DUAL;
  END IF;
END;
/
ALTER TRIGGER "SYSTEM"."UHA_FAMILY_MEMBER_ID_TRIGGER" ENABLE;
--------------------------------------------------------
--  DDL for Trigger UHA_NEXT_OF_KIN_ID_TRIGGER
--------------------------------------------------------

  CREATE OR REPLACE   TRIGGER "SYSTEM"."UHA_NEXT_OF_KIN_ID_TRIGGER" 
BEFORE INSERT ON UHA_APPLICANT_NEXT_OF_KIN
FOR EACH ROW BEGIN
  IF(:NEW.NEXT_OF_KIN_ID IS NULL) THEN
    SELECT UHA_NEXT_OF_KIN_ID_SEQUENCE.NEXTVAL INTO :NEW.NEXT_OF_KIN_ID FROM DUAL;
  END IF;
END;
/
ALTER TRIGGER "SYSTEM"."UHA_NEXT_OF_KIN_ID_TRIGGER" ENABLE;
--------------------------------------------------------
--  DDL for Trigger UHA_ADDRESS_TRIGGER
--------------------------------------------------------

  CREATE OR REPLACE   TRIGGER "SYSTEM"."UHA_ADDRESS_TRIGGER" 
BEFORE INSERT OR UPDATE ON UHA_HOUSING_ADDRESS
FOR EACH ROW BEGIN
  IF(:NEW.ADDRESS_ID IS NULL) THEN
    SELECT UHA_ADDRESS_ID_SEQUENCE.NEXTVAL INTO :NEW.ADDRESS_ID FROM DUAL;
  END IF;
END;
/
ALTER TRIGGER "SYSTEM"."UHA_ADDRESS_TRIGGER" ENABLE;
--------------------------------------------------------
--  DDL for Trigger UHA_INVOICE_NO_TRIGGER
--------------------------------------------------------

  CREATE OR REPLACE   TRIGGER "SYSTEM"."UHA_INVOICE_NO_TRIGGER" 
BEFORE INSERT OR UPDATE ON UHA_INVOICE
FOR EACH ROW 
declare
tmp number;
plt number;
tfs number;
BEGIN
  IF(:NEW.INVOICE_NO IS NULL) THEN
    SELECT UHA_INVOICE_NO_SEQUENCE.NEXTVAL INTO :NEW.INVOICE_NO FROM DUAL;
  END IF;
  
  if(:new.type <> 'FINAL') then
    :new.total_fees := :new.housing_rent;
    if(:new.parking_rent is not null) then
      :new.total_fees := :new.total_fees + :new.parking_rent;
    end if;
    if(:new.penalty is not null) then
      :new.total_fees := :new.total_fees + :new.penalty;
    end if;
  end if;
  
END;
/
ALTER TRIGGER "SYSTEM"."UHA_INVOICE_NO_TRIGGER" ENABLE;
--------------------------------------------------------
--  DDL for Trigger UHA_LEASE_TRIGGER
--------------------------------------------------------

  CREATE OR REPLACE   TRIGGER "SYSTEM"."UHA_LEASE_TRIGGER" 
BEFORE INSERT OR UPDATE ON UHA_LEASE
FOR EACH ROW 
DECLARE
  mrr NUMBER;
  mt varchar2(1);
  tmpvar NUMBER;
  stst varchar2(20);
BEGIN 
  IF(:NEW.LEASE_NO IS NULL) THEN
    SELECT UHA_LEASE_NO_SEQUENCE.NEXTVAL INTO :NEW.LEASE_NO FROM DUAL;
  END IF;
  
 

  if(:new.total_rent is null) then
    IF(:NEW.APARTMENT_TYPE = 'RH') THEN
      SELECT monthly_rent_rate INTO mrr FROM uha_residence_hall_room where place_no = :NEW.PLACE_NO;
    ELSIF(:NEW.APARTMENT_TYPE = 'GA') THEN
      SELECT monthly_rent_rate INTO mrr FROM uha_general_bedroom_apartment where place_no = :NEW.PLACE_NO;
    ELSIF(:NEW.APARTMENT_TYPE = 'FA') THEN
      SELECT monthly_rent_rate INTO mrr FROM uha_family_apartment where apartment_no = :NEW.APARTMENT_NO;
    END IF;
    select (:new.duration * mrr) into :new.total_rent from dual;
  end if;
    
  if(:new.leave_date is null) then
    select :new.enter_date + (:new.duration * 30) into :new.leave_date from dual;
  end if;



  if(:new.place_no is not null) then
      if(:new.apartment_type = 'RH') then
          if(:new.room_no is null) then
              select room_no into :new.room_no from (select room_no from uha_residence_hall_room where place_no = :new.place_no);
          end if;
          if(:new.address_id is null) then
             select address_id into :new.address_id from 
             (select address_id from uha_housing_residence_hall rh, uha_residence_hall_room rhr where rhr.place_no = :new.place_no and rh.name = rhr.residence_hall_name);
          end if;
          if(:new.security_deposit is null) then
             select security_deposit into :new.security_deposit from 
             (select security_deposit from uha_housing_residence_hall rh, uha_residence_hall_room rhr  where rhr.place_no = :new.place_no and rh.name = rhr.residence_hall_name);
          end if;
      elsif(:new.apartment_type = 'GA') then
          if(:new.room_no is null) then
              select room_no into :new.room_no from (select room_no from uha_general_bedroom_apartment where place_no = :new.place_no);
          end if;
          if(:new.apartment_no is null) then
              select apartment_no into :new.apartment_no from (select apartment_no from uha_general_bedroom_apartment where place_no = :new.place_no);
          end if;
          if(:new.address_id is null) then
              select address_id into :new.address_id from 
              (select address_id from uha_apartment ap, uha_general_bedroom_apartment gba where gba.place_no = :new.place_no and ap.apartment_no = gba.apartment_no);
          end if;
          if(:new.security_deposit is null) then
             select security_deposit into :new.security_deposit from 
             (select security_deposit from uha_general_bedroom_apartment gba, uha_apartment ap where gba.place_no = :new.place_no and gba.apartment_no = ap.apartment_no); 
          end if;
      end if;
  elsif(:new.apartment_no is not null) then
      if(:new.apartment_type = 'FA') then
          if(:new.address_id is null) then
              select address_id into :new.address_id from 
              (select address_id from uha_apartment ap where ap.apartment_no = :new.apartment_no);
          end if;
      end if;
      if(:new.security_deposit is null) then
         select security_deposit into :new.security_deposit from 
          (select security_deposit from uha_apartment ap where ap.apartment_no = :new.apartment_no); 
      end if;
  end if;


  SELECT COUNT(*) INTO tmpvar FROM 
  (SELECT * FROM UHA_APARTMENT APT WHERE :NEW.APARTMENT_NO = APT.APARTMENT_NO AND :NEW.APARTMENT_NO IS NOT NULL);
   
  IF(:NEW.APARTMENT_NO IS NOT NULL AND tmpvar = 0) THEN
    RAISE_APPLICATION_ERROR(-20001, 'APARTMENT_NO MUST BE IN UHA_APARTMENT OR BE NULL');
  END IF;
  
  SELECT COUNT(*) INTO tmpvar FROM 
  (SELECT * FROM UHA_RESIDENCE_HALL_ROOM RHR WHERE :NEW.PLACE_NO = RHR.PLACE_NO AND :NEW.PLACE_NO IS NOT NULL
   UNION
   SELECT * FROM UHA_GENERAL_BEDROOM_APARTMENT GBA WHERE :NEW.PLACE_NO = GBA.PLACE_NO AND :NEW.PLACE_NO IS NOT NULL);
   
  IF(:NEW.PLACE_NO IS NOT NULL AND tmpvar = 0) THEN
    RAISE_APPLICATION_ERROR(-20002, 'PLACE_NO MUST BE IN UHA_RESIDENCE_HALL_ROOM OR IN UHA_GENERAL_BEDROOM_APARTMENT OR NULL');
  END IF;

  SELECT COUNT(*) INTO tmpvar FROM 
  (SELECT * FROM UHA_RESIDENCE_HALL_ROOM RHR WHERE :NEW.ROOM_NO = RHR.ROOM_NO AND :NEW.ROOM_NO IS NOT NULL
   UNION
   SELECT * FROM UHA_GENERAL_BEDROOM_APARTMENT GBA WHERE :NEW.ROOM_NO = GBA.PLACE_NO AND :NEW.ROOM_NO IS NOT NULL);
   
  IF(:NEW.ROOM_NO IS NOT NULL AND tmpvar = 0) THEN
    RAISE_APPLICATION_ERROR(-20003, 'ROOM_NO MUST BE IN UHA_RESIDENCE_HALL_ROOM OR IN UHA_GENERAL_BEDROOM_APARTMENT OR NULL');
  END IF;

  select ap.guest into mt from uha_applicant ap where ap.applicant_id = :new.applicant_no;
  if(mt = 'Y') then
    IF((:NEW.LEAVE_DATE - :NEW.ENTER_DATE < 30) AND (:NEW.REQUEST_STATUS <> 'INPROGRESS')) THEN
      RAISE_APPLICATION_ERROR(-20006, 'MINIMUM DURATION OF STAY IS ONE MONTH');
    END IF;
    if(:new.payment_schedule_option = 'SEMESTERLY') then
      RAISE_APPLICATION_ERROR(-20006, 'GUESTS CAN ONLY HAVE MONTHLY INVOICES');
    end if;
  elsif(mt = 'N') then
    select us.status into stst from uha_student us where us.student_id = :new.applicant_no;
    if(stst <> 'PLACED') then
      raise_application_error(-20006, 'ONLY PLACED STUDENTS CAN REQUEST A LEASE');
    end if;
    IF((:NEW.LEAVE_DATE - :NEW.ENTER_DATE < 60) AND (:NEW.REQUEST_STATUS <> 'INPROGRESS')) THEN
      RAISE_APPLICATION_ERROR(-20006, 'MINIMUM DURATION OF STAY IS ONE SEMESTER');
    END IF;
  end if;

  IF(:NEW.LEAVE_DATE - :NEW.ENTER_DATE > 365) THEN
    RAISE_APPLICATION_ERROR(-20007, 'MAXIMUM DURATION OF STAY IS ONE YEAR');
  END IF;

  IF(:NEW.APARTMENT_NO IS NULL AND :NEW.PLACE_NO IS NULL AND :NEW.REQUEST_STATUS NOT IN ('PENDING', 'WAITING', 'CANCELED')) THEN
    RAISE_APPLICATION_ERROR(-20008, 'BOTH APARTMENT_NO AND PLACE_NO CANNOT BE NULL');
  END IF;  
  
END;
/
ALTER TRIGGER "SYSTEM"."UHA_LEASE_TRIGGER" ENABLE;
--------------------------------------------------------
--  DDL for Trigger UHA_LEASE_REQUEST_TRIGGER
--------------------------------------------------------

  CREATE OR REPLACE   TRIGGER "SYSTEM"."UHA_LEASE_REQUEST_TRIGGER" 
BEFORE INSERT ON UHA_LEASE_REQUEST
FOR EACH ROW 
DECLARE
  tmp1 varchar2(1);
  tmp2 varchar2(1);
  tmp3 number;
  tmp4 varchar2(2);
  tmp5 varchar2(1);
BEGIN

  if(:NEW.RESHALL_NAME is not null) then
    SELECT RH.GRAD_UPPER_ONLY INTO tmp1 FROM uha_housing_residence_hall RH where RH.NAME = :NEW.RESHALL_NAME;
    select ap.guest into tmp2 from uha_applicant ap where ap.applicant_id = :new.applicant_no;
    
    if(tmp1 = 'Y' and tmp2 = 'Y') then
      RAISE_APPLICATION_ERROR(-20009, 'THIS RESIDENCE HALL IS ONLY OPEN TO UPPERCLASSMEN AND GRAD STUDENTS: ' || :NEW.RESHALL_NAME);
    end if;
    
    IF(tmp2 = 'N') then
      select s.category_year into tmp3 from uha_student s where s.student_id = :new.applicant_no;
      select s.category_degree into tmp4 from uha_student s where s.student_id = :new.applicant_no;
    
      if(tmp1 = 'Y' and tmp4 = 'UG' and tmp3 < 4) then
        RAISE_APPLICATION_ERROR(-20009, 'THIS RESIDENCE HALL IS ONLY OPEN TO UPPERCLASSMEN AND GRAD STUDENTS: ' || :NEW.RESHALL_NAME);
      end if;
    end if;
  end if;
  
  
  
  if(:new.apartment_no is not null) then
    SELECT apt.freshmen_allowed INTO tmp1 FROM uha_apartment apt where apt.apartment_no = :new.apartment_no;
    SELECT apt.on_campus INTO tmp5 FROM uha_apartment apt where apt.apartment_no = :new.apartment_no;
    select ap.guest into tmp2 from uha_applicant ap where ap.applicant_id = :new.applicant_no;
    
    if(tmp2 = 'N') then
      select s.category_year into tmp3 from uha_student s where s.student_id = :new.applicant_no;
      select s.category_degree into tmp4 from uha_student s where s.student_id = :new.applicant_no;
      
      if(tmp1 = 'N' and tmp4 = 'UG' and tmp3 = 1) then
        RAISE_APPLICATION_ERROR(-20010, 'THIS APARTMENT IS NOT OPEN TO FRESHMEN: ' || :new.apartment_no);
      end if;
      
      if(tmp5 = 'N' and tmp4 = 'UG' and tmp3 = 1) then
        RAISE_APPLICATION_ERROR(-20010, 'THIS APARTMENT IS NOT OPEN TO FRESHMEN: ' || :new.apartment_no);
      end if;
    end if;
  end if;
  
  
  
  if(:new.apartment_no is not null) then
    if(:new.apartment_type = 'FA') then
      select count(*) into tmp3 from uha_applicant_family apf where apf.applicant_id = :new.applicant_no;
      
      if(tmp3 < 1) then
        RAISE_APPLICATION_ERROR(-20011, 'THIS APARTMENT IS ONLY FOR APPLICANTS WITH FAMILY: ' || :new.apartment_no);
      end if;
    end if;
  end if;
  
  
END;
/
ALTER TRIGGER "SYSTEM"."UHA_LEASE_REQUEST_TRIGGER" ENABLE;
--------------------------------------------------------
--  DDL for Trigger UHA_TERMINATE_REQ_NO_TRIGGER
--------------------------------------------------------

  CREATE OR REPLACE   TRIGGER "SYSTEM"."UHA_TERMINATE_REQ_NO_TRIGGER" 
BEFORE INSERT OR UPDATE ON UHA_LEASE_TERMINATION
FOR EACH ROW 
declare 
lv_date DATE;
apttype varchar2(2);
placeno varchar2(50);
aptno varchar2(50);
mrrh number;
BEGIN
  IF(:NEW.TERMINATION_REQUEST_NO IS NULL) THEN
    SELECT UHA_TERMINATE_REQ_NO_SEQUENCE.NEXTVAL INTO :NEW.TERMINATION_REQUEST_NO FROM DUAL;
  END IF;
  
  if(:new.request_status = 'PENDING' and :new.termination_date <= (sysdate+32)) then
    raise_application_error(-20050, 'TERMINATION REQUIRES 1 MONTHS NOTICE');
  end if;
  
  select leave_date into lv_date from uha_lease where lease_no = :new.lease_no;
  if(:new.termination_date >= lv_date) then
    raise_application_error(-20050, 'TERMINATION DATE MUST BE EARLIER THAN LEAVE DATE');
  end if;
  
  select place_no, apartment_no, apartment_type into placeno, aptno, apttype from uha_lease where lease_no = :new.lease_no;
  if(apttype = 'RH') then
    select monthly_rent_rate into mrrh from uha_residence_hall_room where place_no = placeno;
  elsif(apttype = 'GA') then
    select monthly_rent_rate into mrrh from uha_general_bedroom_apartment where place_no = placeno and apartment_no = aptno;
  elsif(apttype = 'FA') then
    select monthly_rent_rate into mrrh from uha_family_apartment where apartment_no = aptno;
  end if;

if(:new.request_status = 'APPROVED') then
  update uha_lease set leave_date = :new.termination_date where lease_no = :new.lease_no;
  update uha_parking_request set end_date = :new.termination_date where lease_no = :new.lease_no;
end if;

if(:new.request_status = 'PENDING') then  
  if(:new.termination_date > '1-JAN-15' and :new.termination_date < '31-MAY-15') then
    :new.days_remaining_fees := (to_date('31-MAY-15') - :new.termination_date) * (mrrh/30);
  elsif(:new.termination_date > '1-JUN-15' and :new.termination_date < '31-JUL-15') then
    :new.days_remaining_fees := (to_date('31-JUL-15') - :new.termination_date) * (mrrh/30);
  elsif(:new.termination_date > '1-AUG-15' and :new.termination_date < '31-DEC-15') then
    :new.days_remaining_fees := (to_date('31-DEC-15') - :new.termination_date) * (mrrh/30);
  end if;
  
  if(lv_date - :new.termination_date >= 60) then
    :new.early_termination_fees := (lv_date - :new.termination_date) * mrrh/30 * 0.2;
  else
    :new.early_termination_fees := (lv_date - :new.termination_date) * mrrh/30;
  end if;
end if;

END;
/
ALTER TRIGGER "SYSTEM"."UHA_TERMINATE_REQ_NO_TRIGGER" ENABLE;
--------------------------------------------------------
--  DDL for Trigger UHA_PARKING_TRIGGER
--------------------------------------------------------

  CREATE OR REPLACE   TRIGGER "SYSTEM"."UHA_PARKING_TRIGGER" 
BEFORE INSERT OR UPDATE ON UHA_PARKING_REQUEST
FOR EACH ROW
DECLARE 
  TMPVAR NUMBER;
  sd date;
  ed date;
BEGIN

  IF(:NEW.PERMIT_ID IS NULL AND :NEW.REQUEST_STATUS IN ('APPROVED', 'INPROGRESS')) THEN
    SELECT UHA_PERMIT_ID_SEQUENCE.NEXTVAL INTO :NEW.PERMIT_ID FROM DUAL;
  END IF;
  
  IF(:NEW.PARKING_REQUEST_NO IS NULL) THEN
    SELECT UHA_PARKING_REQ_NO_SEQUENCE.NEXTVAL INTO :NEW.PARKING_REQUEST_NO FROM DUAL;
  END IF;

  if(:new.request_status = 'APPROVED' and :new.spot_id is null) then
    raise_application_error(-20200, 'REQUEST MUST HAVE A SPOT ID TO BE APPROVED');
  end if;
  
  if(:old.request_status = 'PENDING' and :new.request_status = 'INPROGRESS') then
    select leave_date into ed from uha_lease where lease_no = :new.lease_no;
    if(:new.end_date is null) then
      :new.end_date := ed;
    end if;
  end if;
  
  if(:old.request_status = 'PENDING' and :new.request_status = 'INPROGRESS') then
    select enter_date into sd from uha_lease where lease_no = :new.lease_no;
    select leave_date into ed from uha_lease where lease_no = :new.lease_no;
    :new.start_date := sd;
    :new.end_date := ed;
  end if;
  
END;
/
ALTER TRIGGER "SYSTEM"."UHA_PARKING_TRIGGER" ENABLE;
--------------------------------------------------------
--  DDL for Trigger UHA_TICKET_TRIGGER
--------------------------------------------------------

  CREATE OR REPLACE   TRIGGER "SYSTEM"."UHA_TICKET_TRIGGER" 
before insert or update on UHA_TICKET 
FOR EACH ROW
BEGIN

  IF(:NEW.TICKET_NO IS NULL) THEN
    SELECT UHA_TICKET_NO_SEQUENCE.NEXTVAL INTO :NEW.TICKET_NO FROM DUAL;
  END IF;

  IF(:NEW.TICKET_TYPE = 'WATER' OR :NEW.TICKET_TYPE = 'ELECTRICITY') THEN
    :NEW.SEVERITY := 'HIGH';
    :NEW.SEVERITY_ID := '1';
  ELSIF(:NEW.TICKET_TYPE = 'APPLIANCES' OR :NEW.TICKET_TYPE = 'INTERNET') THEN
    :NEW.SEVERITY := 'MEDIUM';
    :NEW.SEVERITY_ID := '2';
  ELSIF(:NEW.TICKET_TYPE = 'CLEANING' OR :NEW.TICKET_TYPE = 'MISCELLANEOUS') THEN
    :NEW.SEVERITY := 'LOW';
    :NEW.SEVERITY_ID := '3';
  END IF;
END;
/
ALTER TRIGGER "SYSTEM"."UHA_TICKET_TRIGGER" ENABLE;
--------------------------------------------------------
--  DDL for Trigger UHA_USER_ID_TRIGGER
--------------------------------------------------------

  CREATE OR REPLACE   TRIGGER "SYSTEM"."UHA_USER_ID_TRIGGER" 
BEFORE INSERT OR UPDATE ON UHA_USERS
FOR EACH ROW BEGIN
  IF(:NEW.USER_ID IS NULL) THEN
    SELECT UHA_USER_ID_SEQUENCE.NEXTVAL INTO :NEW.USER_ID FROM DUAL;
  END IF;
END;
/
ALTER TRIGGER "SYSTEM"."UHA_USER_ID_TRIGGER" ENABLE;
