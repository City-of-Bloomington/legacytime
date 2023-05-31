package time.web;

import java.util.*;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import time.model.*;
import time.list.*;
import time.utils.*;

@WebServlet(urlPatterns = {"/Search.do"})
public class Search extends TopServlet {

    int maxlimit = 100; // limit on records
    static final long serialVersionUID = 22L;
    static Logger logger = LogManager.getLogger(Search.class);
    String bgcolor="silver";
    /**
     * Generates the form for the search engine.
     * @param req
     * @param res
     */
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	
	String name, value;
	//
	String username="", fname="", lname="", dateFrom="", dateTo="",
	    message="";
	Enumeration<String> names = req.getParameterNames();
	String [] vals;
	while (names.hasMoreElements()){
	    name = names.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();
	    if (name.equals("fname")){
		fname = value;
	    }
	    else if (name.equals("lname")) {
		lname = value;
	    }
	    else if (name.equals("dateFrom")) {
		dateFrom = value;
	    }
	    else if (name.equals("dateTo")) {
		dateTo = value;
	    }
	    else if (name.equals("username")) {
		username = value;
	    }
	    else if (name.equals("message")) {
		message = value;
	    }						
	}
	User user = null;
	HttpSession session = req.getSession(false);
	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user == null){
		String str = url+"Login";
		res.sendRedirect(str);
		return; 
	    }
	}
	else{
	    String str = url+"Login";
	    res.sendRedirect(str);
	    return; 
	}
	out.println(Inserts.xhtmlHeaderInc);
	out.println(Inserts.banner(url));
	out.println(Inserts.menuBar(url));
	out.println(Inserts.sideBar(url, user));
	out.println("<div id=\"mainContent\">");

	out.println("<script type=\"text/javascript\">");
	out.println("                            ");
	out.println("  function validateForm(){	           ");
	out.println("  with(document.myForm){              ");
	out.println("      if(username.value =='' && "+
		    "         fname.value =='' && lname.value ==''){ ");
	out.println("         alert('You need to enter username, first name or last name'); ");
	out.println("         return false; ");
	out.println("      }	         		                    ");
	out.println("   }	         		                    ");		
	out.println("  return true;			                ");
	out.println(" }	         		                    ");
	out.println(" </script>				            ");
	out.println("<center>");
	if(!message.isEmpty()){
	    out.println("<p> "+message+"</p><br />");
	}				
	out.println("<form name=\"myForm\" method=\"post\" onsubmit=\"return validateForm()\">");
	//
	out.println("<table align=\"center\" border=\"1\">");
	out.println("<tr><td align=\"center\" style=\"background-color:navy; color:white\">"+
		    "<b>Search Employee</b></td></tr>");
	out.println("<tr><td bgcolor=\""+bgcolor+"\">"); // #e0e0e0 light gray
	out.println("<table>");
	out.println("<tr><td colspan=\"2\">To search for an employee, enter at least one of the following, username, first name and/or last name</td></tr>");
	out.println("<tr><td colspan=\"2\">If there are more than one match, you will pick the employee in the next page</td></tr>");
	// 
	out.println("<tr><td align=\"left\"><b>Username: </b></td><td align=\"left\">");
	out.println("<input type=\"text\" name=\"username\" maxlength=\"30\" size=\"30\"" +
		    " tabindex=\"1\" value=\""+username+"\" /></td></tr>");
	out.println("<tr><td align=\"left\"><b>First Name: </b></td><td align=\"left\">");
	out.println("<input name=\"fname\" size=\"30\" tabindex=\"3\" "+
		    "value=\""+fname+"\" maxlength=\"50\" />");
	out.println("</td></tr>");
	out.println("<tr><td align=\"left\"><b>Last Name: </b></td><td align=\"left\">");
	out.println("<input name=\"lname\" size=\"30\" tabindex=\"4\" "+
		    "value=\""+lname+"\" maxlength=\"50\" />");
	out.println("</td></tr>");
	out.println("<tr><td colspan=\"2\">You may enter start date and/or end date, if ignored, all records of the employee will be displayed </td></tr>");
	out.println("<tr><td align=\"left\"><b>Start Date: </b></td><td align=\"left\">");
	out.println("<input name=\"dateFrom\" size=\"10\" "+
		    "value=\""+dateFrom+"\" maxlength=\"10\" />");
	out.println("</td></tr>");
	out.println("<tr><td align=\"left\"><b>End Date: </b></td><td align=\"left\">");
	out.println("<input name=\"dateTo\" size=\"10\" "+
		    "value=\""+dateTo+"\" maxlength=\"10\" />");
	out.println("</td></tr>");				
	out.println("</table></td></tr>");
	//
	out.println("<tr><td align=center><input type=\"submit\" "+
		    "name=\"Search\" "+
		    "value=\"Search\" /></td></tr>");
	// 
	out.println("</table>");
	out.println("</div>");
		
	out.print("</body></html>");
	out.close();
    }
    /**
     * Processes the search request and arranges the output in a table.
     * @param req
     * @param res
     */
    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException{
	res.setContentType("text/html");

	String name, value, message="";
	String fname="", lname="", username="", dateFrom="", dateTo="";
	String kuali_employee_id="", clocker_employee_id="";
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	EmpList empList = new EmpList();
	while (values.hasMoreElements()){

	    name = values.nextElement().trim();

	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();
	    if (name.equals("fname")){
		fname = value;
		empList.setFname(value);
	    }
	    else if (name.equals("lname")) {
		lname = value;
		empList.setLname(value);
	    }
	    else if (name.equals("dateFrom")) {
		dateFrom = value;
	    }
	    else if (name.equals("dateTo")) {
		dateTo = value;
	    }
	    else if (name.equals("clocker_employee_id")) {
		clocker_employee_id = value;
		System.err.println(" clocker emp "+value);
	    }
	    else if (name.equals("kuali_employee_id")) {
		kuali_employee_id = value;
		System.err.println(" kuali emp "+value);
	    }						
	    else if (name.equals("username")) {
		empList.setUsername(value);
		username = value;
	    }
	}
	if(url.equals("")){
	    url    = getServletContext().getInitParameter("url");
	}
	User user = null;
	HttpSession session = req.getSession(false);
	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user == null){
		String str = url+"/Login";
		res.sendRedirect(str);
		return; 
	    }
	}
	else{
	    String str = url+"/Login";
	    res.sendRedirect(str);
	    return; 
	}
	List<Employee> kualiEmployees = null;
	List<Employee> clockerEmployees = null;
	Employee kualiEmployee = null;
	Employee clockerEmployee = null;
	if(clocker_employee_id.isEmpty() && kuali_employee_id.isEmpty()){
	    String back = empList.find();
	    if(back.isEmpty()){
		if(empList.hasKualiEmployees()){
		    kualiEmployees = empList.getKualiEmployees();
		}
		if(empList.hasClockerEmployees()){
		    clockerEmployees = empList.getClockerEmployees();
		}
		if(kualiEmployees == null && clockerEmployees == null){
		    String str = url+"Search.do?", str2="";
		    if(!username.isEmpty()){
			str2 += "&username="+username;
		    }
		    if(!fname.isEmpty()){
			str2 += "&fname="+fname;
		    }
		    if(!lname.isEmpty()){
			str2 += "&lname="+lname;
		    }
		    if(!dateFrom.isEmpty()){
			str2 += "&dateFrom="+dateFrom;
		    }
		    if(!dateTo.isEmpty()){
			str2 += "&dateTo="+dateTo;
		    }										
		    str2 +="&message=No+employee+match";
		    res.sendRedirect(str+str2);
		    return; 
		}
	    }
	    if(!back.isEmpty() ||
	       (kualiEmployees == null && clockerEmployees == null)){
		String str = url+"Search.do?";
		boolean andFlag = false;
		String str2 = "";
		if(!username.isEmpty()){
		    str2 += "username="+username;
		    andFlag = true;
		}
		if(!fname.isEmpty()){
		    if(andFlag) str2 +="&";
		    str2 += "fname="+fname;
		    andFlag = true;
		}
		if(!lname.isEmpty()){
		    if(andFlag) str2 +="&";
		    str2 += "lname="+lname;
		    andFlag = true;
		}
		if(!dateFrom.isEmpty()){
		    if(andFlag) str2 +="&";
		    str2 += "dateFrom="+dateFrom;
		    andFlag = true;
		}
		if(!dateTo.isEmpty()){
		    if(andFlag) str2 +="&";
		    str2 += "dateTo="+dateTo;
		    andFlag = true;
		}
		if(andFlag) str2 +="&";
		if(!back.isEmpty()){
		    str2 += "message="+back;
		}
		else{
		    str2 += "message=No+match+found";
		}
		if(!message.isEmpty()){
		    str2 += "&message="+message;
		}
		res.sendRedirect(str+str2);
		return; 								
	    }
	    else{
		if((kualiEmployees != null && kualiEmployees.size() > 1) ||
		   (clockerEmployees != null && clockerEmployees.size() > 1)){
		    // pick from the list
										
		}
		else{
		    if(kualiEmployees != null && kualiEmployees.size() == 1){
			kualiEmployee = kualiEmployees.get(0);
		    }
		    if(clockerEmployees != null && clockerEmployees.size() == 1){
			clockerEmployee = clockerEmployees.get(0);
		    }
										
		}
	    }
	}
	String fullName = "";
	TimeBlockList tbl = null;
	List<TimeBlock> kualiBlocks = null;
	List<TimeBlock> clockerBlocks = null;
	if(kualiEmployee != null){
	    fullName = kualiEmployee.getFullName();
	    tbl = new TimeBlockList();
	    tbl.setKualiEmployeeId(kualiEmployee.getId());
	}
	if(clockerEmployee != null){
	    if(fullName.isEmpty()){
		fullName = clockerEmployee.getFullName();
	    }
	    if(tbl == null)
		tbl = new TimeBlockList();
	    tbl.setClockerEmployeeId(clockerEmployee.getId());
	}
	if(!clocker_employee_id.isEmpty()){
	    if(tbl == null)
		tbl = new TimeBlockList();
	    tbl.setClockerEmployeeId(clocker_employee_id);
	    Employee emp = new Employee();
	    emp.setClockerEmployeeId(clocker_employee_id);
	    String back = emp.doSelect();
	    if(back.isEmpty()){
		fullName = emp.getFullName();
	    }
	}
	if(!kuali_employee_id.isEmpty()){
	    if(tbl == null)
		tbl = new TimeBlockList();
	    tbl.setKualiEmployeeId(kuali_employee_id);
	    if(fullName.isEmpty()){
		Employee emp = new Employee();
		emp.setKualiEmployeeId(kuali_employee_id);
		String back = emp.doSelect();
		if(back.isEmpty()){
		    fullName = emp.getFullName();
		}
	    }
	}
	if(tbl != null){
	    tbl.setStartDate(dateFrom);
	    tbl.setEndDate(dateTo);
	    String back = tbl.find();
	    System.err.println(back);
	    if(tbl.hasClockerBlocks()){
		clockerBlocks = tbl.getClockerBlocks();
	    }
	    if(tbl.hasKualiBlocks()){
		kualiBlocks = tbl.getKualiBlocks();
	    }
	    if(clockerBlocks == null && kualiBlocks == null){
		message = "No time data found ";
	    }
	}
	PrintWriter out = res.getWriter();
	//
	out.println(Inserts.xhtmlHeaderInc);
	out.println(Inserts.banner(url));
	out.println(Inserts.menuBar(url));
	out.println(Inserts.sideBar(url, user));
	out.println("<div id=\"mainContent\">");
	//
	out.println("<center>");
	if(!message.isEmpty()){
	    out.println("<p>"+message+"</p>");
	}
	if(clockerBlocks != null || kualiBlocks != null){
	    out.println("<table align=\"center\" border>");						
	    out.println("<tr><td align=\"center\" style=\"background-color:navy; color:white\">"+
			"<b>"+fullName+" Time Entry</b></td></tr>");
	    if(clockerBlocks != null){
		out.println("<tr><td><table>");						
		out.println("<caption>Clocker Data</caption>");
		out.println("<tr><th>Earn Code</th><th>Date</th><th>Clock In</th><th>Time Out</th><th>Hours</th></tr>");
		for(TimeBlock one:clockerBlocks){
		    out.println("<tr><td>"+one.getEarnCode()+"</td>");
		    out.println("<td>"+one.getDate()+"</td>");
		    out.println("<td>"+one.getClockIn()+"</td>");
		    out.println("<td>"+one.getClockOut()+"</td>");
		    out.println("<td>"+one.getHours()+"</td>");
		    out.println("</tr>");
		}
		out.println("</table></td></tr>");
	    }				
	    if(kualiBlocks != null){
		out.println("<tr><td><table>");						
		out.println("<caption>Kuali Data</caption>");
		out.println("<tr><th>Earn Code</th><th>Date</th><th>Clock In</th><th>Time Out</th><th>Hours</th></tr>");
		for(TimeBlock one:kualiBlocks){
		    out.println("<tr><td>"+one.getEarnCode()+"</td>");
		    out.println("<td>"+one.getDate()+"</td>");
		    out.println("<td>"+one.getClockIn()+"</td>");
		    out.println("<td>"+one.getClockOut()+"</td>");
		    out.println("<td>"+one.getHours()+"</td>");
		    out.println("</tr>");
		}
		out.println("</table></td></tr>");
	    }
	}
	else if((kualiEmployees != null && kualiEmployees.size() > 1) || (clockerEmployees != null && clockerEmployees.size() > 1)){
	    out.println("<form name='selectForm' method='post' action='"+url+"Search.do' >");
	    out.println("<input type=\"hidden\" name=\"dateFrom\" value=\""+dateFrom+"\" />");
	    out.println("<input type=\"hidden\" name=\"dateTo\" value=\""+dateTo+"\" />");						
	    out.println("<table align=\"center\" border=\"1\">");
	    out.println("<tr><td>");
            out.println("<p>Multiple employees match, please pick from the list.</p><ul><li> If you want the data from Clocker, pick one from Clocker list.</li><li> If you want Kuali data pick from the Kuali list.</li><li> If both, pick one from each (if available)</li></ul></td></tr>");
	    if(clockerEmployees != null && clockerEmployees.size() > 1){
		out.println("<tr><td><table><caption>Clocker List</caption>");
		for(Employee one:clockerEmployees){
		    out.println("<tr><td><input type=\"radio\" name=\"clocker_employee_id\" value=\""+one.getId()+"\" /></td><td>"+one.getFullName()+"</td></tr>");
		}
		out.println("</table></td></tr>");
	    }
	    if(kualiEmployees != null && kualiEmployees.size() > 1){
		out.println("<tr><td><table><caption>Kuali List</caption>");
		for(Employee one:kualiEmployees){
		    out.println("<tr><td><input type=\"radio\" name=\"kuali_employee_id\" value=\""+one.getId()+"\" /></td><td>"+one.getFullName()+"</td></tr>");
		}
		out.println("</table></td></tr>");
	    }
	    out.println("</table>");
	    out.println("<input type=\"submit\" name=\"action\" value=\"Next\" />");
	    out.println("</form>");
	}
	out.println("<br /><br />");
	out.println("</center>");
	out.println("</div");
	out.println("</body>");
	out.println("</html>");


    }

}






















































