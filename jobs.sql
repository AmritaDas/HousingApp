--------------------------------------------------------
--  DDL for Job UHA_LEASE_UPDATE
--------------------------------------------------------

begin dbms_scheduler.create_job (
  job_name => 'uha_lease_update', 
  job_type => 'STORED_PROCEDURE', 
  job_action => 'uha_lease_update_proc', 
  start_date => TRUNC(SYSDATE,'YY'), 
  repeat_interval => 'FREQ=MINUTELY;INTERVAL=5', 
  end_date => NULL, 
  job_class => 'DEFAULT_JOB_CLASS', 
  enabled => TRUE,
  auto_drop => FALSE, 
  comments => 'uha');
end;
/

--------------------------------------------------------
--  DDL for Job UHA_PARKING_UPDATE
--------------------------------------------------------

begin dbms_scheduler.create_job (
  job_name => 'uha_parking_update', 
  job_type => 'STORED_PROCEDURE', 
  job_action => 'uha_parking_update_proc', 
  start_date => TRUNC(SYSDATE,'YY'), 
  repeat_interval => 'FREQ=MINUTELY;INTERVAL=5', 
  end_date => NULL, 
  job_class => 'DEFAULT_JOB_CLASS', 
  enabled => TRUE,
  auto_drop => FALSE, 
  comments => 'uha');
end;
/

--------------------------------------------------------
--  DDL for Job UHA_TICKET_UPDATE
--------------------------------------------------------

begin dbms_scheduler.create_job (
  job_name => 'uha_ticket_update', 
  job_type => 'STORED_PROCEDURE', 
  job_action => 'uha_ticket_update_proc', 
  start_date => TRUNC(SYSDATE,'YY'), 
  repeat_interval => 'FREQ=MINUTELY;INTERVAL=5', 
  end_date => NULL, 
  job_class => 'DEFAULT_JOB_CLASS', 
  enabled => TRUE,
  auto_drop => FALSE, 
  comments => 'uha');
end;
/

--------------------------------------------------------
--  DDL for Job UHA_INVOICE_UPDATE
--------------------------------------------------------

begin dbms_scheduler.create_job (
  job_name => 'uha_invoice_update', 
  job_type => 'STORED_PROCEDURE', 
  job_action => 'uha_invoice_creation_proc', 
  start_date => TRUNC(SYSDATE,'YY'), 
  repeat_interval => 'FREQ=MINUTELY;INTERVAL=5', 
  end_date => NULL, 
  job_class => 'DEFAULT_JOB_CLASS', 
  enabled => TRUE,
  auto_drop => FALSE, 
  comments => 'uha');
end;
/

--------------------------------------------------------
--  DDL for Job UHA_INVOICE_LATE
--------------------------------------------------------

begin dbms_scheduler.create_job (
  job_name => 'uha_invoice_late', 
  job_type => 'STORED_PROCEDURE', 
  job_action => 'uha_invoice_late_fees', 
  start_date => TRUNC(SYSDATE,'YY'), 
  repeat_interval => 'FREQ=HOURLY', 
  end_date => NULL, 
  job_class => 'DEFAULT_JOB_CLASS', 
  enabled => TRUE,
  auto_drop => FALSE, 
  comments => 'uha');
end;
/

