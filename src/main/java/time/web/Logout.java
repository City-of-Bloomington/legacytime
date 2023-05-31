package time.web;
import java.util.*;
import java.sql.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import time.model.*;
import time.list.*;
import time.utils.*;

@WebServlet(urlPatterns = {"/Logout"})
public class Logout extends TopServlet{

    PrintWriter os;
    static final long serialVersionUID = 49L;
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException{

	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String name= "";
	String value = "";
	HttpSession session = req.getSession();
	User user = null;
	if(session != null){
	    user = (User)session.getAttribute("user");
	    session.removeAttribute("user");
	    session.invalidate();			
	}
	String str = cas_url+"?url="+url;
	res.sendRedirect(str);
	return;

		
    }
    
}






















































