--------------------------------------------------------
--  DDL for Procedure UHA_GBA_FULL
--------------------------------------------------------
set define off;

  CREATE OR REPLACE   PROCEDURE "SYSTEM"."UHA_GBA_FULL" 
    (apt_no varchar2, sd_date DATE, lv_date DATE, ful out number)
is
no_full number;
total_no number;
tmpval number;
cursor gba_cur is select place_no from uha_general_bedroom_apartment where apartment_no = apt_no;
gba_rec gba_cur%rowtype;
begin
  no_full:= 0;
  ful:= 0;
  select no_of_beds into total_no from uha_apartment where apartment_no = apt_no;
  open gba_cur;
  loop
    FETCH gba_cur INTO gba_rec;
    EXIT WHEN gba_cur%NOTFOUND;
    SELECT COUNT(*) INTO TMPVAL FROM 
      (SELECT * FROM UHA_LEASE L2 WHERE apt_no = L2.APARTMENT_NO AND gba_rec.place_no = L2.PLACE_NO AND 
        ((sd_date > L2.ENTER_DATE AND sd_date <L2.LEAVE_DATE) OR 
           (sd_date < L2.ENTER_DATE AND lv_date > L2.ENTER_DATE)) );
    if(tmpval > 0) then
      no_full := no_full + 1;
    end if;
  end loop;
  close gba_cur;
  
  if(no_full = total_no) then
    ful:= 1;
  end if;

end;

/
--------------------------------------------------------
--  DDL for Procedure UHA_INVOICE_FINAL_PARKING
--------------------------------------------------------
set define off;

  CREATE OR REPLACE   PROCEDURE "SYSTEM"."UHA_INVOICE_FINAL_PARKING" (leaseno in varchar2, lv_date in date, totfees in number)
is
cursor inv_cur is select lease_no, type, parking_rent, date_billed 
       from uha_invoice where status = 'BILLED' order by date_billed asc;
inv_rec inv_cur%rowtype;
tmpvar number;
psd date;
ped date;
psi varchar2(50);
mrrp number;
pmr number;
begin
  select count(*) into tmpvar from uha_parking_request where lease_no = leaseno;
  
  if(tmpvar > 0) then
    pmr := 0;
    open inv_cur;
    loop
    FETCH inv_cur INTO inv_rec;
    EXIT WHEN inv_cur%NOTFOUND;
      if(inv_rec.type <> 'FINAL') then
        if(inv_rec.parking_rent is not null) then
          pmr := pmr + inv_rec.parking_rent;
        end if;
      end if;
    end loop;
    close inv_cur;
    
    if(pmr = 0) then
      select start_date, end_date, spot_id into psd, ped, psi from uha_parking_request where lease_no = leaseno;
      select monthly_rent into mrrp from uha_parking_spot where spot_no = psi;
    
      pmr := (ped - psd) * (mrrp/30);
      update uha_invoice set parking_rent = pmr, total_fees = totfees+pmr where lease_no = leaseno and date_billed = lv_date;
    end if;
    
  end if;
end;

/
--------------------------------------------------------
--  DDL for Procedure UHA_INVOICE_LATE_FEES
--------------------------------------------------------
set define off;

  CREATE OR REPLACE   PROCEDURE "SYSTEM"."UHA_INVOICE_LATE_FEES" 
is
cursor inv_cur is select lease_no, penalty, total_fees, due_date, date_billed, type, late from uha_invoice where status = 'BILLED';
inv_rec inv_cur%rowtype;
cursor fv_cur is select lease_no from uha_invoice where status = 'PAID' and type = 'FINAL';
fv_rec fv_cur%rowtype;
pnlt number;
lll number;
fl number;
tfs number;
begin

open fv_cur;
loop
FETCH fv_cur INTO fv_rec;
EXIT WHEN fv_cur%NOTFOUND;
  update uha_invoice set status = 'PAID' where lease_no = fv_rec.lease_no;
end loop;
close fv_cur;


open inv_cur;
loop
FETCH inv_cur INTO inv_rec;
EXIT WHEN inv_cur%NOTFOUND;
  if(inv_rec.late = 'N' and sysdate > inv_rec.due_date) then
    if(inv_rec.penalty is null) then
      pnlt := 50;
    else
      pnlt := inv_rec.penalty + 50;
    end if;
    if(inv_rec.type <> 'FINAL') then
      update uha_invoice set late = 'Y', penalty = pnlt where lease_no = inv_rec.lease_no and date_billed = inv_rec.date_billed;
      select count(*) into lll from uha_invoice where lease_no = inv_rec.lease_no and type = 'FINAL';
      if(lll > 0) then
        select penalty, total_fees into fl, tfs from uha_invoice where lease_no = inv_rec.lease_no and type = 'FINAL';
        update uha_invoice set penalty = fl+pnlt, total_fees = tfs+pnlt where lease_no = inv_rec.lease_no and type = 'FINAL';
      end if;
    elsif(inv_rec.type = 'FINAL') then
      select penalty, total_fees into fl, tfs from uha_invoice where lease_no = inv_rec.lease_no and type = 'FINAL' and date_billed = inv_rec.date_billed;
      update uha_invoice set penalty = fl+50, total_fees = tfs+50 where lease_no = inv_rec.lease_no and type = 'FINAL' and date_billed = inv_rec.date_billed;
    end if;
    
  end if;
end loop;
close inv_cur;

end;

/
--------------------------------------------------------
--  DDL for Procedure UHA_INVOICE_PARKING_RENT
--------------------------------------------------------
set define off;

  CREATE OR REPLACE   PROCEDURE "SYSTEM"."UHA_INVOICE_PARKING_RENT" (leaseno in varchar2, typ in varchar2, curr_date_billed in date, nmn in number, nmnls in number)
is
tmpvar number;
psd date;
ped date;
psi varchar2(50);
mrrp number;
pmr number;
tm number;
ldb date;
ndb date;
begin
  select count(*) into tmpvar from uha_parking_request where lease_no = leaseno;
  if(tmpvar > 0) then
    select start_date, end_date, spot_id into psd, ped, psi from uha_parking_request where lease_no = leaseno;
    select monthly_rent into mrrp from uha_parking_spot where spot_no = psi;
    
    if(typ = 'MONTHLY') then
      pmr := 0;
      select add_months(trunc(curr_date_billed, 'MM'), 1) into ndb from dual;
      select add_months(trunc(curr_date_billed, 'MM'), -1) into ldb from dual;
      
      if(psd < curr_date_billed and psd > ldb) then
        tm := curr_date_billed - psd + 1;
        if(tm = 28 or tm = 29 or tm = 31) then
          tm := 30;
        elsif(tm = 1) then tm := 0; 
        end if;
        pmr := pmr + (tm * (mrrp/30));
      end if;
      
      if(psd <= curr_date_billed and ped > ndb) then
        pmr := pmr + mrrp;
      elsif(psd < curr_date_billed and ped > curr_date_billed and ped <= ndb) then
        tm := ped - curr_date_billed + 1;
        if(tm = 28 or tm = 29 or tm = 31) then
          tm := 30;
        elsif(tm = 1) then tm := 0; 
        end if;
        pmr := pmr + (tm * (mrrp/30));
      end if;
      
      update uha_invoice set parking_rent = pmr where lease_no = leaseno and date_billed = curr_date_billed;
      commit;
      
    elsif (typ = 'SEMESTERLY') then
      pmr := 0;
      select add_months(trunc(curr_date_billed, 'MM'), nmn) into ndb from dual;
      select add_months(trunc(curr_date_billed, 'MM'), 0-nmnls) into ldb from dual;
      
      if(psd < curr_date_billed and psd > ldb) then
        pmr := pmr + ((curr_date_billed - psd) * (mrrp/30));
      end if;
      
      if(psd <= curr_date_billed and ped >= ndb) then
        pmr := pmr + mrrp*nmn;
      elsif(psd < curr_date_billed and ped > curr_date_billed and ped <= ndb) then
        pmr := pmr + ((ped - curr_date_billed) * (mrrp/30));
      end if;
      
      update uha_invoice set parking_rent = pmr where lease_no = leaseno and date_billed = curr_date_billed;
      commit;
    end if;
    
  end if;
end;

/
--------------------------------------------------------
--  DDL for Procedure UHA_LEASE_OVERLAP_PROC
--------------------------------------------------------
set define off;

  CREATE OR REPLACE   PROCEDURE "SYSTEM"."UHA_LEASE_OVERLAP_PROC" 
    (app_id varchar2, st_date DATE, drn NUMBER) 
is
lv_date DATE;
tmpval NUMBER;
begin

  select st_date + (drn * 30) into lv_date from dual;
  
  select count(*) into tmpval from (SELECT * FROM uha_lease l2 where l2.applicant_no = app_id 
  and ((st_date < l2.enter_date and lv_date > l2.enter_date) 
  or (st_date > l2.enter_date and st_date < l2.leave_date)) );
  
  if(tmpval > 0) then
      RAISE_APPLICATION_ERROR(-20000, 'APPLICANT ALREADY HAS A LEASE DURING THIS TIME PERIOD. ');
  end if;
  
end;

/
--------------------------------------------------------
--  DDL for Procedure UHA_LEASE_PLACE_FA
--------------------------------------------------------
set define off;

  CREATE OR REPLACE   PROCEDURE "SYSTEM"."UHA_LEASE_PLACE_FA" 
  (leaseno varchar2, apt_no varchar2, placed out number, sd_date DATE, lv_date DATE)
is
tmpval number;
begin
  placed:= 0;
  SELECT COUNT(*) INTO TMPVAL FROM 
    (SELECT * FROM UHA_LEASE L2 WHERE apt_no = L2.APARTMENT_NO AND
      ((sd_date > L2.ENTER_DATE AND sd_date <L2.LEAVE_DATE) OR 
         (sd_date < L2.ENTER_DATE AND lv_date > L2.ENTER_DATE)) );
  if(tmpval = 0) then
    placed := 1;
  end if;
end;

--exec uha_lease_placing_proc(61);

/
--------------------------------------------------------
--  DDL for Procedure UHA_LEASE_PLACE_GA
--------------------------------------------------------
set define off;

  CREATE OR REPLACE   PROCEDURE "SYSTEM"."UHA_LEASE_PLACE_GA" 
  (leaseno varchar2, apt_no varchar2, placed out number, 
       placeno out varchar2, sd_date DATE, lv_date DATE)
is
cursor ga_cur is select place_no from uha_general_bedroom_apartment where apartment_no = apt_no;
ga_rec ga_cur%rowtype;
tmpval number;
begin
  placed:= 0;
  open ga_cur;
  loop
    FETCH ga_cur INTO ga_rec;
    EXIT WHEN ga_cur%NOTFOUND;
    if(placed = 0) then
      SELECT COUNT(*) INTO TMPVAL FROM 
        (SELECT * FROM UHA_LEASE L2 WHERE apt_no = L2.APARTMENT_NO AND ga_rec.place_no = L2.PLACE_NO AND 
          ((sd_date > L2.ENTER_DATE AND sd_date <L2.LEAVE_DATE) OR 
             (sd_date < L2.ENTER_DATE AND lv_date > L2.ENTER_DATE)) );
      if(tmpval = 0) then
        placed := 1;
        placeno := ga_rec.place_no;
      end if;
    end if;
  end loop;
  close ga_cur;
end;

/
--------------------------------------------------------
--  DDL for Procedure UHA_LEASE_PLACE_RH
--------------------------------------------------------
set define off;

  CREATE OR REPLACE   PROCEDURE "SYSTEM"."UHA_LEASE_PLACE_RH" 
  (leaseno varchar2, reshall varchar2, placed out number, 
       placeno out varchar2, sd_date DATE, lv_date DATE)
is
cursor rh_cur is select place_no from uha_residence_hall_room where residence_hall_name = reshall;
rh_rec rh_cur%rowtype;
tmpval number;
begin
  placed:= 0;
  open rh_cur;
  loop
    FETCH rh_cur INTO rh_rec;
    EXIT WHEN rh_cur%NOTFOUND;
    if(placed = 0) then
      SELECT COUNT(*) INTO TMPVAL FROM 
        (SELECT * FROM UHA_LEASE L2 WHERE rh_rec.place_no = L2.PLACE_NO AND 
          ((sd_date > L2.ENTER_DATE AND sd_date <L2.LEAVE_DATE) OR 
             (sd_date < L2.ENTER_DATE AND lv_date > L2.ENTER_DATE)) );
      if(tmpval = 0) then
        placed := 1;
        placeno := rh_rec.place_no;
      end if;
    end if;
  end loop;
  close rh_cur;
end;

/
--------------------------------------------------------
--  DDL for Procedure UHA_LEASE_PLACING_PROC
--------------------------------------------------------
set define off;

  CREATE OR REPLACE   PROCEDURE "SYSTEM"."UHA_LEASE_PLACING_PROC" (leaseno varchar2)
is
apt_type varchar2(2);
reshall varchar2(50);
apt_no varchar2(50);
sts varchar2(50);
sd_date DATE;
lv_date DATE;
placed number;
placeno varchar2(50);
begin
  select request_status, enter_date, leave_date into sts, sd_date, lv_date from uha_lease where lease_no = leaseno;
  if(sts = 'PENDING') then
    select apartment_type, reshall_name, apartment_no into apt_type, reshall, apt_no 
       from uha_lease_request where lease_no = leaseno and preference = 1;
    if(apt_type = 'RH') then
      uha_lease_place_rh(leaseno, reshall, placed, placeno, sd_date, lv_date);
    elsif(apt_type = 'GA') then
      uha_lease_place_ga(leaseno, apt_no, placed, placeno, sd_date, lv_date);
    elsif(apt_type = 'FA') then
      uha_lease_place_fa(leaseno, apt_no, placed, sd_date, lv_date);
    end if;
    
    if(placed = 0) then
      select apartment_type, reshall_name, apartment_no into apt_type, reshall, apt_no 
       from uha_lease_request where lease_no = leaseno and preference = 2;
      if(apt_type = 'RH') then
        uha_lease_place_rh(leaseno, reshall, placed, placeno, sd_date, lv_date);
      elsif(apt_type = 'GA') then
        uha_lease_place_ga(leaseno, apt_no, placed, placeno, sd_date, lv_date);
      elsif(apt_type = 'FA') then
        uha_lease_place_fa(leaseno, apt_no, placed, sd_date, lv_date);
      end if;
    end if;

    if(placed = 0) then
      select apartment_type, reshall_name, apartment_no into apt_type, reshall, apt_no 
       from uha_lease_request where lease_no = leaseno and preference = 3;
      if(apt_type = 'RH') then
        uha_lease_place_rh(leaseno, reshall, placed, placeno, sd_date, lv_date);
      elsif(apt_type = 'GA') then
        uha_lease_place_ga(leaseno, apt_no, placed, placeno, sd_date, lv_date);
      elsif(apt_type = 'FA') then
        uha_lease_place_fa(leaseno, apt_no, placed, sd_date, lv_date);
      end if;
    end if;

    if(placed = 1) then
      if(apt_type = 'RH') then
        update uha_lease set place_no = placeno, apartment_type = 'RH' where lease_no = leaseno;
      elsif(apt_type = 'GA') then
        update uha_lease set place_no = placeno, apartment_type = 'GA' where lease_no = leaseno;
      elsif(apt_type = 'FA') then
        update uha_lease set apartment_no = apt_no, apartment_type = 'FA' where lease_no = leaseno;
      end if;
      commit;
    end if;

  end if;
end;

/
--------------------------------------------------------
--  DDL for Procedure UHA_LEASE_UPDATE_PROC
--------------------------------------------------------
set define off;

  CREATE OR REPLACE   PROCEDURE "SYSTEM"."UHA_LEASE_UPDATE_PROC" 
is
cursor ls_cur is select lease_no, request_status, enter_date, leave_date from uha_lease;
ls_rec ls_cur%rowtype;
begin
  open ls_cur;
  loop
  FETCH ls_cur INTO ls_rec;
  EXIT WHEN ls_cur%NOTFOUND;
    if(ls_rec.request_status = 'APPROVED' and ls_rec.enter_date <= sysdate) then
      update uha_lease set request_status = 'INPROGRESS' where lease_no = ls_rec.lease_no;
      commit;
    end if;
    if(ls_rec.request_status = 'INPROGRESS' and ls_rec.leave_date <= sysdate) then
      update uha_lease set request_status = 'COMPLETE' where lease_no = ls_rec.lease_no;
      commit;
    end if;
  end loop;
  close ls_cur;  
end;

/
--------------------------------------------------------
--  DDL for Procedure UHA_PARKING_UPDATE_PROC
--------------------------------------------------------
set define off;

  CREATE OR REPLACE   PROCEDURE "SYSTEM"."UHA_PARKING_UPDATE_PROC" 
is
cursor pk_cur is select lease_no, request_status, start_date, end_date from uha_parking_request;
pk_rec pk_cur%rowtype;
begin
  open pk_cur;
  loop
  FETCH pk_cur INTO pk_rec;
  EXIT WHEN pk_cur%NOTFOUND;
    if(pk_rec.request_status = 'APPROVED' and pk_rec.start_date <= sysdate) then
      update uha_parking_request set request_status = 'INPROGRESS' where lease_no = pk_rec.lease_no;
      commit;
    end if;
    if(pk_rec.request_status = 'INPROGRESS' and pk_rec.end_date <= sysdate) then
      update uha_parking_request set request_status = 'COMPLETE' where lease_no = pk_rec.lease_no;
      commit;
    end if;
  end loop;
  close pk_cur;  
end;

/
--------------------------------------------------------
--  DDL for Procedure UHA_RH_FULL
--------------------------------------------------------
set define off;

  CREATE OR REPLACE   PROCEDURE "SYSTEM"."UHA_RH_FULL" 
   (reshall varchar2, sd_date date, lv_date date, ful out number)
is
no_full number;
total_no number;
tmpval number;
cursor rh_cur is select place_no from uha_residence_hall_room where residence_hall_name = reshall;
rh_rec rh_cur%rowtype;
begin
  no_full:= 0;
  ful:= 0;
  select hrh.no_of_rooms into total_no from uha_housing_residence_hall hrh where hrh.name = reshall;
  open rh_cur;
  loop
    FETCH rh_cur INTO rh_rec;
    EXIT WHEN rh_cur%NOTFOUND;
    SELECT COUNT(*) INTO TMPVAL FROM 
      (SELECT * FROM UHA_LEASE L2 WHERE rh_rec.place_no = L2.PLACE_NO AND 
        ((sd_date > L2.ENTER_DATE AND sd_date <L2.LEAVE_DATE) OR 
           (sd_date < L2.ENTER_DATE AND lv_date > L2.ENTER_DATE)) );
    if(tmpval > 0) then
      no_full := no_full + 1;
    end if;
  end loop;
  close rh_cur;
  
  if(no_full = total_no) then
    ful:= 1;
  end if;

end;

/
--------------------------------------------------------
--  DDL for Procedure UHA_TICKET_UPDATE_PROC
--------------------------------------------------------
set define off;

  CREATE OR REPLACE   PROCEDURE "SYSTEM"."UHA_TICKET_UPDATE_PROC" 
is
cursor act_cur is select ticket_no, action_datetime from uha_admin_action_on_ticket where action_taken = 'PROCESSED';
act_rec act_cur%rowtype;
mint number;
begin
open act_cur;
  loop
  FETCH act_cur INTO act_rec;
  EXIT WHEN act_cur%NOTFOUND;
    select extract(minute from (systimestamp - act_rec.action_datetime)) into mint from dual;
    if(mint >= 30) then
      update uha_ticket set status = 'COMPLETED' where ticket_no = act_rec.ticket_no;
      update uha_admin_action_on_ticket set action_taken = 'COMPLETED' where ticket_no = act_rec.ticket_no;
    end if;
  end loop;
close act_cur;
end;

/
--------------------------------------------------------
--  DDL for Procedure UHA_INVOICE_CREATION_PROC
--------------------------------------------------------
set define off;

  CREATE OR REPLACE   PROCEDURE "SYSTEM"."UHA_INVOICE_CREATION_PROC" 
is
cursor ls_cur is select lease_no, request_status, duration, 
  enter_date, leave_date, applicant_no, apartment_type, place_no, 
  apartment_no, payment_schedule_option, total_rent, security_deposit
  from uha_lease where request_status in ('INPROGRESS', 'COMPLETE');
ls_rec ls_cur%rowtype;
cursor inv_cur is select lease_no, total_fees from uha_invoice where status = 'BILLED' and sysdate > due_date;
inv_rec inv_cur%rowtype;
tmpvar number;
nmnls number;
nmn number;
fall varchar2(10);
spring varchar2(10);
tm number;
hr number;
pmr number;
tfs number;
tmp1 number;
etf number;
fli number;
drm number;
drn number;
mrrh number;
pnlt number;
fmon date;
nmon date;
ldb date;
begin
open ls_cur;
  loop
  FETCH ls_cur INTO ls_rec;
  EXIT WHEN ls_cur%NOTFOUND;
    if(ls_rec.apartment_type = 'RH') then
      select monthly_rent_rate into mrrh from uha_residence_hall_room where place_no = ls_rec.place_no;
    elsif (ls_rec.apartment_type = 'GA') then
      select monthly_rent_rate into mrrh from uha_general_bedroom_apartment where place_no = ls_rec.place_no;
    elsif (ls_rec.apartment_type = 'FA') then
      select monthly_rent_rate into mrrh from uha_family_apartment where apartment_no = ls_rec.apartment_no;
    end if;
       
    if(ls_rec.payment_schedule_option = 'MONTHLY') then
      for i in 1..ls_rec.duration loop
        hr := 0;   
        select count(*) into tmpvar from uha_invoice where lease_no = ls_rec.lease_no;
        if(tmpvar = 0) then
           select trunc(ls_rec.enter_date, 'MM') into fmon from dual;
           tm := fmon - ls_rec.enter_date;
           if(tm = 28 or tm = 29 or tm = 31) then
             tm := 30;
           end if;
           hr:= tm * (mrrh/30);
           select add_months(trunc(fmon, 'MM'), 1) into nmon from dual;
           if(ls_rec.leave_date < nmon) then
              tm := ls_rec.leave_date - fmon + 1;
              if(tm = 28 or tm = 29 or tm = 31) then
                tm := 30;
              end if;
              hr := hr + tm * (mrrh/30);
           else
              hr := hr + mrrh;
           end if;
           if(sysdate >= fmon) then
             insert into uha_invoice (lease_no, applicant_no, type, housing_rent, due_date, status, date_billed) 
                        values (ls_rec.lease_no, ls_rec.applicant_no, 'MONTHLY', hr, fmon+10, 'BILLED', fmon);
             commit;
             uha_invoice_parking_rent(ls_rec.lease_no, 'MONTHLY', fmon, 1, 1);
           end if;
        else
           select date_billed into ldb from 
            (select date_billed from uha_invoice 
             where lease_no = ls_rec.lease_no order by date_billed desc) 
           where rownum = 1;
           if((ls_rec.leave_date - ldb) >= 30) then
             select add_months(trunc(ldb, 'MM'), 1) into fmon from dual;
             select add_months(trunc(fmon, 'MM'), 1) into nmon from dual;
             if(ls_rec.leave_date >= nmon) then
               hr := mrrh;
             else
               tm := ls_rec.leave_date - fmon + 1;
               if(tm = 28 or tm = 29 or tm = 31) then
                 tm := 30;
               end if;
               hr := tm * (mrrh/30);
             end if;
             if(sysdate >= fmon) then
               insert into uha_invoice (lease_no, applicant_no, type, housing_rent, due_date, status, date_billed) 
                        values (ls_rec.lease_no, ls_rec.applicant_no, 'MONTHLY', hr, fmon+10, 'BILLED', fmon);
               commit;
               uha_invoice_parking_rent(ls_rec.lease_no, 'MONTHLY', fmon, 1, 1);
             end if;
           end if;
        end if;
      end loop;
    end if;
    
    if(ls_rec.payment_schedule_option = 'SEMESTERLY') then
      fall := '1-AUG-15';
      spring := '1-JAN-15';
      if(ls_rec.enter_date = spring) then
        if(ls_rec.duration = 5) then
          drn := 1;
        elsif(ls_rec.duration = 7) then
          drn := 2;
        elsif(ls_rec.duration = 12) then
          drn := 3;
        end if;
      elsif(ls_rec.enter_date = fall) then
        if(ls_rec.duration = 5) then
          drn := 1;
        elsif(ls_rec.duration = 10) then
          drn := 2;
        elsif(ls_rec.duration = 12) then
          drn := 3;
        end if;
      end if;

      for j in 1..drn loop
        hr := 0;
        select count(*) into tmpvar from uha_invoice where lease_no = ls_rec.lease_no;
         if(ls_rec.enter_date = spring) then
            if(j = 1) then
               nmn := 5;
            elsif(j = 2) then
               nmn := 2;
               nmnls := 5;
            elsif(j = 3) then
               nmn := 5;
               nmnls := 2;
            end if;
         elsif(ls_rec.enter_date = fall) then
            if(j = 1) then
               nmn := 5;
            elsif(j = 2) then
               nmn := 5;
               nmnls := 5;
            elsif(j = 3) then
               nmn := 2;
               nmnls := 5;
            end if;
         end if;

        if(tmpvar = 0) then
           select trunc(ls_rec.enter_date, 'MM') into fmon from dual;
           hr:= (fmon - ls_rec.enter_date) * (mrrh/30);
           select add_months(trunc(fmon, 'MM'), nmn) into nmon from dual;
           if(ls_rec.leave_date < nmon) then
              hr := hr + (ls_rec.leave_date - fmon) * (mrrh/30);
           else
              hr := hr + mrrh*nmn;
           end if;
           if(sysdate >= fmon) then
             insert into uha_invoice (lease_no, applicant_no, type, housing_rent, due_date, status, date_billed) 
                        values (ls_rec.lease_no, ls_rec.applicant_no, 'SEMESTERLY', hr, fmon+10, 'BILLED', fmon);
             commit;
             uha_invoice_parking_rent(ls_rec.lease_no, 'SEMESTERLY', fmon, nmn, 1);
           end if;
        else
           select date_billed into ldb from 
            (select date_billed from uha_invoice 
             where lease_no = ls_rec.lease_no order by date_billed desc) 
           where rownum = 1;
           if((ls_rec.leave_date - ldb) >= 30*nmnls) then
             select add_months(trunc(ldb, 'MM'), nmnls) into fmon from dual;
             select add_months(trunc(fmon, 'MM'), nmn) into nmon from dual;
             if(ls_rec.leave_date >= nmon) then
               hr := mrrh*nmn;
             else
               hr := (ls_rec.leave_date - fmon) * (mrrh/30);
             end if;
             if(sysdate >= fmon) then
               insert into uha_invoice (lease_no, applicant_no, type, housing_rent, due_date, status, date_billed) 
                          values (ls_rec.lease_no, ls_rec.applicant_no, 'SEMESTERLY', hr, fmon+10, 'BILLED', fmon);
               commit;
               uha_invoice_parking_rent(ls_rec.lease_no, 'SEMESTERLY', fmon, nmn, nmnls);
             end if;
           end if;
        end if;
      end loop;
    end if;
    
    --final
    select count(*) into tmpvar from uha_invoice where lease_no = ls_rec.lease_no and type = 'FINAL';
    tfs := 0;
    if(tmpvar = 0) then
      pnlt := 0;
      open inv_cur;
      loop
      FETCH inv_cur INTO inv_rec;
      EXIT WHEN inv_cur%NOTFOUND;
        if(inv_rec.lease_no = ls_rec.lease_no) then
          pnlt := pnlt + inv_rec.total_fees;
        end if;
      end loop;
      close inv_cur;
      
      select count(*) into tmp1 from uha_lease_termination where lease_no = ls_rec.lease_no and request_status in ('APPROVED', 'COMPLETED');
      if(tmp1 > 0) then
        select ltr.days_remaining_fees, ltr.early_termination_fees, ltr.fees_levied_on_inspection into drm, etf, fli from uha_lease_termination ltr where ltr.lease_no = ls_rec.lease_no;
        pnlt := pnlt + etf + fli - drm;
      end if;
      tfs := pnlt - ls_rec.security_deposit;
      
      if(sysdate >= ls_rec.leave_date) then
          insert into uha_invoice (lease_no, applicant_no, type, penalty, due_date, status, total_fees, date_billed) 
                values (ls_rec.lease_no, ls_rec.applicant_no, 'FINAL', pnlt, ls_rec.leave_date+10, 'BILLED', tfs, ls_rec.leave_date);
          commit;
          uha_invoice_final_parking(ls_rec.lease_no, ls_rec.leave_date, tfs);
      end if;
    end if;
    
  end loop;
close ls_cur;
end;

/
--------------------------------------------------------
--  DDL for Procedure UHA_LEASE_ROOMS_FULL_PROC
--------------------------------------------------------
set define off;

  CREATE OR REPLACE   PROCEDURE "SYSTEM"."UHA_LEASE_ROOMS_FULL_PROC" 
    (sd_date DATE, drn NUMBER, apt_type varchar2, reshall varchar2, apt_no varchar2) 
is
lv_date DATE;
tmpval NUMBER;
ful NUMBER;
begin
  ful := 0;
    
  select sd_date + (drn * 30) into lv_date from dual;
  
  if(apt_type = 'FA') then
    SELECT COUNT(*) INTO TMPVAL FROM 
     (SELECT * FROM UHA_LEASE L2 WHERE apt_no = L2.APARTMENT_NO AND 
      ((sd_date > L2.ENTER_DATE AND sd_date <L2.LEAVE_DATE) OR 
        (sd_date < L2.ENTER_DATE AND lv_date > L2.ENTER_DATE)) );
        
    if(tmpval > 0) then
      RAISE_APPLICATION_ERROR(-20015, 'THIS APARTMENT IS FULL: ' || apt_no);
    end if;
  end if;
  
  if(apt_type = 'GA') then
    uha_gba_full(apt_no, sd_date, lv_date, ful);
    if(ful = 1) then
      RAISE_APPLICATION_ERROR(-20015, 'THIS APARTMENT IS FULL: ' || apt_no);
    end if;
  end if;
  
  if(apt_type = 'RH') then
    uha_rh_full(reshall, sd_date, lv_date, ful);
    if(ful = 1) then
      RAISE_APPLICATION_ERROR(-20015, 'THIS RESIDENCE HALL IS FULL: ' || reshall);
    end if;
  end if;

end;

/
