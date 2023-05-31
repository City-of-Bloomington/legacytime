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


public class EmpList{

    String username="", fname="", lname="", id="", errors="", dept_code="",
	employee_number="";
    static final long serialVersionUID = 55L;
    static Logger logger = LogManager.getLogger(EmpList.class);
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    List<Employee> kualiEmployees = null;
    List<Employee> clockerEmployees = null;		
    //
    public EmpList(){}
    public EmpList(String val, String val2, String val3){
	setUsername(val);
	setFname(val2);
	setLname(val3);
    }


    //
    // setters
    //
    public void setUsername(String val){
	if(val != null)
	    username = val.toLowerCase();
    }
    public void setFname(String val){
	if(val != null)
	    fname = val.toLowerCase();
    }
    public void setLname(String val){
	if(val != null)
	    lname = val.toLowerCase();
    }
    public boolean hasError(){
	return !errors.isEmpty();
    }
    public String getErrors(){
	return errors;
    }
    public List<Employee> getKualiEmployees(){
	return kualiEmployees;
    }
    public List<Employee> getClockerEmployees(){
	return clockerEmployees;
    }
    public boolean hasKualiEmployees(){
	return kualiEmployees != null && kualiEmployees.size() > 0;

    }
    public boolean hasClockerEmployees(){
	return clockerEmployees != null && clockerEmployees.size() > 0;

    }
    //
    // find all matching records
    // return "" or any exception thrown by DB
    //
    /*
      select p.prncpl_id,p.prncpl_nm,e.emp_id,e.prmry_dept_cd, t.first_nm,t.last_nm from krtt.krim_prncpl_t p, krtt.krim_entity_emp_info_t e,krtt.krim_entity_nm_t t where e.entity_id=t.entity_id and p.entity_id=e.entity_id and t.dflt_ind='Y'   order by t.last_nm,t.first_nm limit 10;

      select p.prncpl_id,p.prncpl_nm,e.emp_id,e.prmry_dept_cd, concat_ws(' ',t.first_nm,t.last_nm) from krtt.krim_prncpl_t p,krtt.krim_entity_emp_info_t e,           krtt.krim_entity_nm_t t                                                         where e.entity_id=t.entity_id                                                   and p.entity_id=e.entity_id                                                     and t.dflt_ind='Y' and lower(t.first_nm) like '%rick%';
				
				
		       			 
    */
    public String find(){
	//
	if(hasError()){
	    return errors;
	}
	String back = "";
	Connection con = null, con2 = null;
	PreparedStatement pstmt = null, pstmt2=null;
	ResultSet rs = null;
	String qq = "select p.prncpl_id,"+
	    "p.prncpl_nm,"+
	    "e.emp_id,"+ // employee_number
	    "e.prmry_dept_cd, "+
	    "concat_ws(' ',t.first_nm,t.last_nm) "+
	    "from krtt.krim_prncpl_t p, "+
	    "krtt.krim_entity_emp_info_t e,"+
	    "krtt.krim_entity_nm_t t ";
	qq += "where e.entity_id=t.entity_id "+
	    "and p.entity_id=e.entity_id "+
	    "and t.dflt_ind='Y' ";
	if(!username.isEmpty()){
	    qq += " and lower(p.prncpl_nm) like ? ";
	}
	if(!fname.isEmpty()){
	    qq += " and lower(t.first_nm) like ? ";
	}
	if(!lname.isEmpty()){
	    qq += " and lower(t.last_nm) like ? ";
	}				
	qq += " order by t.last_nm,t.first_nm ";
	//
	// for clocker
	String qq2 = "select id,empid,null,null,fullname from users ";
	String qw = "";
	if(!username.isEmpty()){
	    qw += " empid like ? ";
	}
	if(!fname.isEmpty()){
	    if(!qw.isEmpty()) qw += " and ";
	    qw += " lower(fullname) like ? ";
	}
	if(!lname.isEmpty()){
	    if(!qw.isEmpty()) qw += " and ";						
	    qw += " lower(fullname) like ? ";
	}
	if(!qw.isEmpty()){
	    qq2 += " where "+qw;
	}
				
	try{
	    logger.debug(qq);
	    System.err.println(qq);
	    con = Helper.getConnectionKuali();	
	    if(con == null){
		back += "Could not connect to DB ";
		System.err.println(back);
		return back;
	    }
	    pstmt = con.prepareStatement(qq);
	    int jj=1;
	    if(!username.isEmpty()){
		pstmt.setString(jj++, username);
	    }
	    if(!fname.isEmpty()){
		pstmt.setString(jj++, "%"+fname+"%");
	    }
	    if(!lname.isEmpty()){
		pstmt.setString(jj++, "%"+lname+"%");
	    }			
	    rs = pstmt.executeQuery();
	    while(rs.next()){
		String str  = rs.getString(1); // id 
		String str2 = rs.getString(2); // username
		String str3 = rs.getString(3); // emp num 
		String str4 = rs.getString(4); // dept_code
		String str5 = rs.getString(5); // full name
		Employee emp = new Employee(str, str2, str3, str4, str5);
		System.err.println(" adding kuali "+str5);
		if(kualiEmployees == null)
		    kualiEmployees = new ArrayList<>();
		if(!kualiEmployees.contains(emp)){
		    kualiEmployees.add(emp);
		}
	    }
	    logger.debug(qq2);
	    con2 = Helper.getConnectionClocker();	
	    if(con2== null){
		back += "Could not connect to DB ";
		return back;
	    }
	    qq = qq2;
	    System.err.println(qq2);
	    pstmt2= con2.prepareStatement(qq2);
	    jj=1;
	    if(!username.isEmpty()){
		pstmt2.setString(jj++, username);
	    }
	    if(!fname.isEmpty()){
		pstmt2.setString(jj++, fname+"%");
	    }
	    if(!lname.isEmpty()){
		pstmt2.setString(jj++, "%"+lname);
	    }			
	    rs = pstmt2.executeQuery();
	    while(rs.next()){
		String str  = rs.getString(1); // id 
		String str2 = rs.getString(2); // username
		String str3 = rs.getString(3); // emp num 
		String str4 = rs.getString(4); // dept_code
		String str5 = rs.getString(5); // full name
		Employee emp = new Employee(str, str2, str3, str4, str5);
		System.err.println(" clocker "+str5);
		if(clockerEmployees == null)
		    clockerEmployees = new ArrayList<>();
		if(!clockerEmployees.contains(emp)){
		    clockerEmployees.add(emp);
		}
	    }						
	}
	catch(Exception ex){
	    back += ex+":"+qq;
	    logger.error(ex+":"+qq);
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


    */
}






















































