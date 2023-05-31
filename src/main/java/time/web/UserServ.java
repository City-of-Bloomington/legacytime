package time.web;

import java.util.*;
import java.sql.*;
import java.io.*;
import java.text.*;
import javax.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import time.model.*;
import time.list.*;
import time.utils.*;

@WebServlet(urlPatterns = {"/User.do"})
public class UserServ extends TopServlet{

    String url="",url2="";
    static final long serialVersionUID = 143L;
    static Logger logger = LogManager.getLogger(UserServ.class);
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
	doPost(req,res);
    }
    /**
     * @link #doGetost
     */

    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException {
    
	//
	String message="", action="";
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String name, value, id="";
	boolean success = true;
	User user = null; // logged in user
	User user2 = new User(); // to add/update/delete
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
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();
	    if (name.equals("id")) {
		id = value;
		user2.setId(value);
	    }						
	    else if (name.equals("role")) {
		user2.setRole(value);
	    }						
	    else if (name.equals("username")){ 
		user2.setUsername(value);  
	    }
	    else if (name.equals("fname")){ 
		user2.setFullName(value);  
	    }
	    else if (name.equals("dept")){ 
		user2.setDept(value);  
	    }						
	    else if (name.equals("action")){ 
		action = value;  
	    }
	}
	if(action.equals("Save")){
	    String back = user2.doSave();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	    else{
		id = user2.getId();
		message = "Save successfully";
	    }
	}
	else if(action.equals("Update")){
	    String back = user2.doUpdate();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	    else{
		message = "Updated successfully";
	    }
	}
	else if(action.startsWith("New")){
	    user2 = new User();
	    id="";
	}
	else if(!id.equals("")){
	    String back = user2.doSelect();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }						
	}
	List<User> users = null;
	if(true){
	    UserList ul = new UserList();
	    String back = ul.find();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	    else{
		users = ul.getUsers();
	    }
	}
	//
	out.println(Inserts.xhtmlHeaderInc);
	out.println(Inserts.banner(url));
	out.println(Inserts.menuBar(url));
	out.println(Inserts.sideBar(url, user));
	out.println("<div id=\"mainContent\">");
	out.println("<script type=\"text/javascript\">");
	out.println("//<![CDATA[  ");
	out.println("  function validateForm(){	            ");
	//
	// checking dates and numeric values
	// check the numbers
	//
	out.println("  return true;			                ");
	out.println(" }	         		                    ");
	out.println("  function doSubmit(){	            ");
	out.println("  document.getElementById(\"myForm\").submit()");				
	out.println("  return true;			                ");
	out.println(" }	         		                    ");				
	out.println(" //]]>                                 ");
	out.println(" </script>				                ");
	//
	// delete startNew
	//
	out.println("<div style=\"text-align:center\">");
	out.println("<h2>Authorized Timewarp Users </h2>");
	out.println("</div>"); 		
	if(!message.equals("")){
	    if(success)
		out.println("<p align=\"center\">"+message+"</p>");
	    else
		out.println("<p class=\"warning center\">"+message+"</p>");
	}
	out.println("<form name=\"myForm\" method=\"post\" id=\"myForm\" "+
		    " action=\""+url+"User.do?\""+
		    " onsubmit=\"return validateForm()\" >");
	out.println("<fieldset><legend>Add/Update Users </legend>");
	out.println("<table border=\"1\" width=\"90%\">");
	out.println("<tr><td align=\"center\">");
	//
	// Add/Edit record
	//
	out.println("<table width=\"100%\">");
	out.println("<tr><td colspan=\"2\" align=\"left\">* Required</td></tr>");
	out.println("<tr><td align=\"right\"><b>Current users:</b></td>");
	out.println("<td align=\"left\">");
	out.println("<select name=\"id\" onchange=\"doSubmit()\">");
	out.println("<option value=\"\">Pick User To Update</option>");
	if(users != null){
	    for(User one:users){
		String selected="";
		if(user2.getId().equals(one.getId()))
		    selected="selected=\"selected\"";
		out.println("<option "+selected+" value=\""+one.getId()+"\">"+one.getFullName()+"</option>");
	    }
	}
	out.println("</select></td></tr>");
	if(id.equals("")){
	    out.println("<tr><td colspan=\"2\" align=\"center\">For new users fill the following fields:</td></tr>");
	}
	out.println("<tr><td align=\"right\"><b>Username: </b></td>");		
	out.println("<td align=\"left\">");
	out.println("<input name=\"username\" value=\""+user2.getUsername()+"\" size=\"10\" maxlength=\"10\" /> *");
	out.println("</td></tr>");
	out.println("<tr><td align=\"right\"><b>Full Name: </b></td>");		
	out.println("<td align=\"left\">");
	out.println("<input name=\"fullName\" value=\""+user2.getFullName()+"\" size=\"50\" maxlength=\"70\" /> *");
	out.println("</td></tr>");
	out.println("<tr><td align=\"right\"><b>Department: </b></td>");		
	out.println("<td align=\"left\">");
	out.println("<input name=\"dept\" value=\""+user2.getDept()+"\" size=\"50\" maxlength=\"70\" /> *");
	out.println("</td></tr>");				
	out.println("<tr><td align=\"right\"><b>Role: </b></td>");		
	out.println("<td align=\"left\">");
	String checked = "";
	if(user2.canEdit() || id.equals("")) // for new users
	    checked="checked=\"checked\"";
	out.println("<input type=\"radio\" name=\"role\" value=\"Edit\" "+checked+" />Edit");
	checked="";
	if(user2.isAdmin())
	    checked="checked=\"checked\"";
	out.println("<input type=\"radio\" name=\"role\" value=\"Edit:Admin\" "+checked+" />Admin");				
	out.println("</td></tr>");
	out.println("</table></td></tr>");
	out.println("<tr><td><table width=\"100%\">");
	if(id.equals(""))
	    out.println("<tr><td align=\"center\"><input type=\"submit\" "+
			" name=\"action\" value=\"Save\" />");
	else{
	    out.println("<tr><td align=\"center\"><input type=\"submit\" "+
			" name=\"action\" value=\"Update\" /></td>");
	    out.println("<td align=\"right\"><input type=\"submit\" "+
			" name=\"action\" value=\"New User\" /></td>");						
	}
	out.println("</td></tr>");
	out.println("</table></td></tr>");			
	out.println("</table>");
	out.println("</form>");
	out.println("</fieldset>");
	out.print("<br /><br />");
	out.print("</div>");
	out.print("</body></html>");
		
	out.flush();
	out.close();
    }

}






















































