package time.list;

import java.util.*;
import java.sql.*;
import java.io.*;
import java.text.*;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import time.model.*;
import time.utils.*;


public class TimeBlockList{

    String kualiEmployeeId="", clockerEmployeeId="",
	startDate="", endDate="", id="", errors="";

    static final long serialVersionUID = 55L;
    static Logger logger = LogManager.getLogger(TimeBlockList.class);
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    List<TimeBlock> kualiBlocks = null;
    List<TimeBlock> clockerBlocks = null;		
    //
    public TimeBlockList(){}
    public TimeBlockList(
			 String val,
			 String val2,
			 String val3,
			 String val4){
	setKualiEmployeeId(val);
	setClockerEmployeeId(val2);
	setStartDate(val3);
	setEndDate(val4);
    }


    //
    // setters
    //
    public void setKualiEmployeeId(String val){
	if(val != null)
	    kualiEmployeeId = val;
    }
    public void setClockerEmployeeId(String val){
	if(val != null)
	    clockerEmployeeId = val;
    }
    public void setStartDate(String val){
	if(val != null)
	    startDate = val;
    }
    public void setEndDate(String val){
	if(val != null)
	    endDate = val;
    }
    public boolean hasError(){
	return !errors.isEmpty();
    }
    public String getErrors(){
	return errors;
    }
    public List<TimeBlock> getKualiBlocks(){
	return kualiBlocks;
    }
    public List<TimeBlock> getClockerBlocks(){
	return clockerBlocks;
    }
    public boolean hasKualiBlocks(){
	return kualiBlocks != null && kualiBlocks.size() > 0;

    }
    public boolean hasClockerBlocks(){
	return clockerBlocks != null && clockerBlocks.size() > 0;

    }
    //
    // find all matching records
    // return "" or any exception thrown by DB
    //
    /*
      select p.prncpl_id,p.prncpl_nm,e.emp_id,e.prmry_dept_cd, t.first_nm,t.last_nm from krtt.krim_prncpl_t p, krtt.krim_entity_emp_info_t e,krtt.krim_entity_nm_t t where e.entity_id=t.entity_id and p.entity_id=e.entity_id and t.dflt_ind='Y'  and e.prmry_dept_cd  order by t.last_nm,t.first_nm;

    */
    public String find(){
	//
	String back = "";
	Connection con = null, con2=null;
	PreparedStatement pstmt = null, pstmt2 = null;
	ResultSet rs = null;
	// for Kuali first
	String qq = "( select t.tk_time_block_id ID,"+
	    "date_format(t.begin_ts,'%m/%d/%Y') date, "+
	    "date_format(t.begin_ts,'%H:%i') clockIn,"+
	    "date_format(t.end_ts,'%H:%i') clockOut,"+
	    "t.hours hours,"+
	    "t.earn_code earnCode "+
	    " from kpme.tk_time_block_t t "+
	    " where t.principal_id = ? ";
	if(!startDate.isEmpty()){
	    qq += " and t.begin_ts >= ? ";
	}
	if(!endDate.isEmpty()){
	    qq += " and t.end_ts <= ? ";
	}				
	qq += " ) union ( ";
	qq += " select t.lm_leave_block_id ID,"+
	    " date_format(t.leave_date,'%m/%d/%Y') date,"+
	    " 0 clockIn,"+
	    " 0 clockOut,"+
	    " -t.leave_amount hours,"+			// negative amount
	    " t.earn_code earnCode "+
	    " from kpme.lm_leave_block_t t "+
	    " where (t.leave_block_type='TC' or t.leave_block_type='LC') and t.principal_id = ? ";
	if(!startDate.isEmpty()){
	    qq += " and t.leave_date >= ? ";
	}
	if(!endDate.isEmpty()){
	    qq += " and t.leave_date <= ? ";
	}								
	qq += ") order by date ";
	// System.err.println(qq);
	//
	// for clocker;
	String qq2 = "select t.id,date_format(t.dt,'%m/%d/%Y') Date,concat_ws(':',t.in_hour,t.in_minute) 'Clock IN',concat_ws(':',t.out_hour,t.out_minute) 'Clock OUT',round(((t.out_hour*60+t.out_minute)-(t.in_hour*60.+t.in_minute))/60,2) Hours,c.name 'Earn Code'   from timeinterval t join categories c on c.id =t.category_id where t.user_id=? ";
	if(!startDate.isEmpty()){
	    qq2 += " and t.dt >= ? ";
	}
	if(!endDate.isEmpty()){
	    qq2 += " and t.dt <= ? ";
	}
	qq2 += " order by 2 ";				
	if(kualiEmployeeId.isEmpty() && clockerEmployeeId.isEmpty()){
	    back = "Employee ID not set ";
	    return back;
	}
	try{				
	    if(!kualiEmployeeId.isEmpty()){
		logger.debug(qq);
		con = Helper.getConnectionKuali();	
		if(con == null){
		    back += "Could not connect to DB ";
		    System.err.println(back);
		    return back;
		}
		pstmt = con.prepareStatement(qq);
		int jj=1;
		pstmt.setString(jj++, kualiEmployeeId);
		if(!startDate.isEmpty()){
		    pstmt.setDate(jj++, new java.sql.Date(dateFormat.parse(startDate).getTime()));
		}
		if(!endDate.isEmpty()){
		    pstmt.setDate(jj++, new java.sql.Date(dateFormat.parse(endDate).getTime()));										
		}
		pstmt.setString(jj++, kualiEmployeeId);
		if(!startDate.isEmpty()){
		    pstmt.setDate(jj++, new java.sql.Date(dateFormat.parse(startDate).getTime()));
		}
		if(!endDate.isEmpty()){
		    pstmt.setDate(jj++, new java.sql.Date(dateFormat.parse(endDate).getTime()));			
		}								
		rs = pstmt.executeQuery();
		while(rs.next()){
		    String str  = rs.getString(1); // id 
		    String str2 = rs.getString(2); // date
		    String str3 = rs.getString(3); // clock-in
		    String str4 = rs.getString(4); // clock-out
		    String str5 = rs.getString(5); // hours
		    String str6 = rs.getString(6); // earn_code							
		    TimeBlock one = new TimeBlock(str, str2, str3, str4, str5, str6);
		    if(kualiBlocks == null)
			kualiBlocks = new ArrayList<>();
		    if(!kualiBlocks.contains(one)){
			kualiBlocks.add(one);
		    }
		}
	    }
	    if(!clockerEmployeeId.isEmpty()){
		logger.debug(qq2);
		System.err.println(qq2);
		qq = qq2;
		con2 = Helper.getConnectionClocker();	
		if(con2 == null){
		    back += "Could not connect to DB ";
		    System.err.println(back);
		    return back;
		}
		pstmt2 = con2.prepareStatement(qq2);
		int jj=1;
		pstmt2.setString(jj++, clockerEmployeeId);
		if(!startDate.isEmpty()){
		    pstmt2.setDate(jj++, new java.sql.Date(dateFormat.parse(startDate).getTime()));										

		}
		if(!endDate.isEmpty()){
		    pstmt2.setDate(jj++, new java.sql.Date(dateFormat.parse(endDate).getTime()));		
		}			
		rs = pstmt2.executeQuery();
		while(rs.next()){
		    String str  = rs.getString(1); // id 
		    String str2 = rs.getString(2); // date
		    String str3 = rs.getString(3); // In
		    String str4 = rs.getString(4); // out
		    String str5 = rs.getString(5); // hours
		    String str6 = rs.getString(6); // earn code
		    TimeBlock one = new TimeBlock(str, str2, str3, str4, str5, str6);
		    if(clockerBlocks == null)
			clockerBlocks = new ArrayList<>();
		    if(!clockerBlocks.contains(one)){
			clockerBlocks.add(one);
		    }
		}
	    }
	}
	catch(Exception ex){
	    back += ex+":"+qq;
	    logger.error(back);
	    System.err.println(back);
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	    Helper.databaseDisconnect(con2, pstmt2, rs);
	}
	return back;
    }
    /*
    //
    // list of all users in kpme organized by dept, work area and name
    //
    select e.prmry_dept_cd dept, a.descr area,p.prncpl_nm username,t.first_nm first_name, t.last_nm last_name                                                      	from krtt.krim_prncpl_t p,                                                      krtt.krim_entity_emp_info_t e,                                                  krtt.krim_entity_nm_t t,                                                        kpme.tk_work_area_t a,                                                          kpme.tk_assignment_t s                                                          where e.entity_id=t.entity_id                                                   and p.entity_id=e.entity_id                                                     and t.dflt_ind='Y' and p.prncpl_nm not like 'train%'                            and a.work_area =s.work_area and a.active='Y'                                   and s.principal_id=p.entity_id                                                  and s.active='Y'                                                                order by dept,area,t.last_nm,t.first_nm;

    //
    // write to a file
    //
    select e.prmry_dept_cd dept, a.descr area,p.prncpl_nm username,t.first_nm first_name, t.last_nm last_name                                                      	into outfile '/tmp/outfile.csv'                                                fields terminated by ',' lines terminated by '\n'                               from krtt.krim_prncpl_t p,                                                      krtt.krim_entity_emp_info_t e,                                                  krtt.krim_entity_nm_t t,                                                        kpme.tk_work_area_t a,                                                          kpme.tk_assignment_t s                                                          where e.entity_id=t.entity_id                                                   and p.entity_id=e.entity_id                                                     and t.dflt_ind='Y' and p.prncpl_nm not like 'train%'                            and a.work_area =s.work_area and a.active='Y'                                   and s.principal_id=p.entity_id                                                  and s.active='Y'                                                                order by dept,area,t.last_nm,t.first_nm;					

    GRANT ALL PRIVILEGES ON kpme.* TO 'kpme'@'localhost' with grant options

    identified by 'h++pd';

    */
}






















































