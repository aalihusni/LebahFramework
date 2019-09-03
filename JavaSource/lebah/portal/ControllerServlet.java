/* ************************************************************************
LEBAH PORTAL FRAMEWORK
Copyright (C) 2007  Shamsul Bahrin

* ************************************************************************ */


package lebah.portal;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lebah.db.DbException;
import lebah.portal.db.AuthenticateUser;
import lebah.portal.db.PrepareUser;
import lebah.portal.db.UserPage;
import lebah.portal.element.Tab;
import lebah.portal.velocity.VTemplate;
import lebah.util.Util;


/**
 * @author Shamsul Bahrin Abd Mutalib
 * @version 1.01
 */
public class ControllerServlet extends lebah.portal.velocity.VServlet {

	private static long icnt = 0;

	public void doGet(HttpServletRequest req, HttpServletResponse res)  throws ServletException, IOException    {
		doPost(req, res);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException  {
		System.out.println("[ControllerServlet.doPost] Begin.");
		PrintWriter out = res.getWriter();
		HttpSession session = req.getSession();
		res.setContentType("text/html");
		//res.setCharacterEncoding("UTF-8");
		
		String userAgent = req.getHeader("User-Agent");
		

		//synchronized(this) {
			context = (org.apache.velocity.VelocityContext) session.getAttribute("VELOCITY_CONTEXT");
			engine = (org.apache.velocity.app.VelocityEngine) session.getAttribute("VELOCITY_ENGINE");
			if ( context == null ) {
				initVelocity(getServletConfig());
				session.setAttribute("VELOCITY_CONTEXT", context);
				session.setAttribute("VELOCITY_ENGINE", engine);
			}
		//}
		
		/*
		context = (org.apache.velocity.VelocityContext) getServletConfig().getServletContext().getAttribute("VELOCITY_CONTEXT");
		engine = (org.apache.velocity.app.VelocityEngine) getServletConfig().getServletContext().getAttribute("VELOCITY_ENGINE");
		*/

		context.put("userAgent", userAgent);
		context.put("util", new Util());
		
		//STORE VELOCITY ENGINE IN THE SESSION OBJECT
		if ( session.getAttribute("_VELOCITY_INITIALIZED") == null ) {
			session.setAttribute("_VELOCITY_ENGINE", engine);
			session.setAttribute("_VELOCITY_CONTEXT", context);
			session.setAttribute("_VELOCITY_INITIALIZED", "true");
		}

		//to handle browser's refresh that might trigger double submission
		//get previous token
		String prev_token = session.getAttribute("form_token") != null ? (String) session.getAttribute("form_token") : "";
		//get form token
		String form_token = req.getParameter("form_token") != null ? req.getParameter("form_token") : "empty";
		//pre_token equals form_token if not refresh

		if ( prev_token.equals(form_token) ) {
			session.setAttribute("doPost", "true");
			session.setAttribute("isPost", new Boolean(true));
		}
		else if ( "empty".equals(form_token) ) {
			session.setAttribute("doPost", "false");
			session.setAttribute("isPost", new Boolean(false));
		}
		else {
			session.setAttribute("doPost", "false");
			session.setAttribute("isPost", new Boolean(false));
		}
		//create a new form token
		form_token = Long.toString(System.currentTimeMillis());
		session.setAttribute("form_token", form_token);

		//end handle browser's refresh
		
        String app_path = getServletContext().getRealPath("/");

		/* NOTE:
		 * getServletContext().getRealPath("/") will return NULL if this application
		 * is deployed as WAR file.
		 * If you deploy war files without unzipping them in a directory yourself, 
		 * then the server isn't required to return a real path, 
		 * since there isn't really a real path. 
		 */        
        app_path = app_path != null ? app_path.replace('\\', '/') : "";
        
		session.setAttribute("_portal_app_path", app_path);

		String serverName = req.getServerName();
		int serverPort = req.getServerPort();
		session.setAttribute("_portal_server", serverPort != 80 ? serverName + ":" + serverPort : serverName );
        context.put("server", serverPort != 80 ? serverName + ":" + serverPort : serverName);
		
		String reqUrl = req.getRequestURL().toString();
		String queryString = req.getQueryString();
		String portalReqUrl = reqUrl + "?" + queryString;
		session.setAttribute("_portal_reqUrl", portalReqUrl);
		String uri = req.getRequestURI();
		String s1 = uri.substring(1);
		context.put("appname", s1.substring(0, s1.indexOf("/")));
		session.setAttribute("_portal_appname", s1.substring(0, s1.indexOf("/")));
		//get pathinfo
        String pathInfo = req.getPathInfo();
        pathInfo = pathInfo.substring(1); //get rid of the first '/'
        session.setAttribute("_portal_pathInfo", pathInfo);
        //pathInfo only contains action
		String action = pathInfo != null ? pathInfo : "";

		//Set role to anonymous by default
		if ( session.getAttribute("_portal_role") == null || "".equals((String)session.getAttribute("_portal_role"))) {
			session.setAttribute("_portal_role", "anon");
			session.setAttribute("_portal_username", "Anonymous");
			session.setAttribute("_portal_login", "anon");
			session.setAttribute("_portal_islogin", "false");
			session.setAttribute("_portal_css", null);
			action = "";
		}

		//module
		String module = req.getParameter("_portal_module") != null ?
					    req.getParameter("_portal_module") : "";
		if ( session.getAttribute("_portal_module") != null )
			if ( (module == null) || ("".equals(module)) ) module = (String) session.getAttribute("_portal_module");


		//System.out.println("module=" + module);
		//------
		//To handles different visitors (for example: different languages )
		//------
		if ( session.getAttribute("_portal_visitorList") == null ) {
			Hashtable visitorList = new Hashtable();
			session.setAttribute("_portal_visitorList", visitorList);
			context.put("portalVisitorList", visitorList);
		} else {
			Hashtable visitorList = (Hashtable) session.getAttribute("_portal_visitorList");
			context.put("portalVisitorList", visitorList);
		}


		if ( "anon".equals((String) session.getAttribute("_portal_role"))
		&& "false".equals((String) session.getAttribute("_portal_islogin"))	)
		{
			String visitor = req.getParameter("visitor") != null ?
							 req.getParameter("visitor") :
							 session.getAttribute("_portal_visitor") != null ?
							 (String) session.getAttribute("_portal_visitor") : "anon";
			//this visitor user login must be of role anon
			if ( !"anon".equals(visitor) && "anon".equals(PrepareUser.getRole(visitor)) ) {
				session.setAttribute("_portal_login", visitor);
			}
			else {
				visitor = "anon";
				session.setAttribute("_portal_login", "anon");
			}

			session.setAttribute("_portal_visitor", visitor);

			//System.out.println("action=" + action);

			if ( req.getParameter("visitor") != null && !"login".equals(action) ) {
				action = "";
			}
		}
		//---
		session.setAttribute("_portal_action", action);
		session.setAttribute("_portal_module", module);
		context.put("session", session);

		//CSS
		String css = "default.css";
		try {
			if ( req.getParameter("visitor") != null ) {
				css = UserPage.getCSS((String) session.getAttribute("_portal_login") );
			}
			else {
				css = session.getAttribute("_portal_css") != null ?
				(String) session.getAttribute("_portal_css") :
				UserPage.getCSS((String) session.getAttribute("_portal_login") );
			}
		} catch ( DbException cssex ) {
			//Log.print("Error getting CSS: " + cssex.getMessage());
		}
		session.setAttribute("_portal_css", css);

		//To store content template
		VTemplate content = null;

        //**HTML
		out.println("<html>");
		out.println("<title>");
		//**HTML
        
		//TITLE
		Title cTitle = new Title(engine, context, req, res);
		try {
			cTitle.print();
		} catch ( Exception ex ) {
			out.println(ex.getMessage());
		}

        //**HTML
		out.println("</title>");

		out.println("<script type=\"text/javascript\" src=\"dropdown.js\" />");
		out.println("<style type=\"text/css\">");
		out.println("div.sample_attach, a.sample_attach");
		out.println("{ width: 100px; border: 1px solid black; background: #FFFFEE; padding: 0px 5px; font-weight: 900; color: #008000; }");
		out.println("a.sample_attach {display: block; border-bottom: none; text-decoration: none; }");
		out.println("</style>");
		
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"../css/" + css + "\" />");
		out.println("<body topmargin=\"0\" leftmargin=\"0\">");
		//out.println("<div align=\"center\">");
        out.println("<div class=\"main\">");
		out.println("<table class=\"container\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
        //**HTML

		//Handle login request
		if ( action.equalsIgnoreCase("login") ) {
			String usrlogin = req.getParameter("username");
			String password = req.getParameter("password");
			try {
				AuthenticateUser auth = new AuthenticateUser(req);
				if ( auth.lookup(usrlogin, password) ) {
					System.out.println("[ControllerServlet] auth.getRole() = "+auth.getRole());
					session.setAttribute("_portal_role", auth.getRole());
					session.setAttribute("_portal_username", auth.getUserName());
					session.setAttribute("_portal_login", auth.getUserLogin());
					session.setAttribute("_portal_islogin", "true");

					//CSS
					try {
						css = UserPage.getCSS((String) session.getAttribute("_portal_login") );
						session.setAttribute("_portal_css", css);
					} catch ( DbException cssex ) {
						//Log.print("Error getting CSS: " + cssex.getMessage());
					}
					res.sendRedirect("");
				} else {
					session.setAttribute("_portal_role", "anon");
					session.setAttribute("_portal_username", "Anonymous");
					session.setAttribute("_portal_login", "anon");
					session.setAttribute("_portal_islogin", "false");
					res.sendRedirect("../accessdenied.jsp");
				}
			} catch ( Exception ex ) {
				content = new ErrorMsg(engine, context, req, res);
				((ErrorMsg) content).setError(ex.getMessage());
			}
		}
		else if ( action.equalsIgnoreCase("logout") ) {
			res.sendRedirect("../logout1.jsp");
		}

		//TODO
		//what if user has logout but press the browser's back button
		String islogin = (String) session.getAttribute("_portal_islogin");

		//--

		//Initiate VTemplate objects
		Header cHeader = new Header(engine, context, req, res);

		//HEADER
        //**HTML
		out.println("<tr><td>");
        //**HTML
        
		try {
			cHeader.print();
		} catch ( Exception ex ) {
			out.println(ex.getMessage());
		}
        //**HTML
		out.println("</td></tr>");
		//**HTML
		if ( "customize".equalsIgnoreCase(action) && "true".equals(islogin) ) {
			//CUSTOMIZE MODULE
	

            //**HTML
			out.println("<tr><td>");
			out.println("<table width=\"100%\" cellpadding=\"1\" cellspacing=\"1\" border=\"0\">");
			out.println("<tr><td bgcolor=\"silver\" style=\"font-size:12pt; font-weight:bold\">");
			out.println("Personalization");
			out.println("</td></tr></table>");
			out.println("</td></tr>");
			out.println("<tr><td>");
            //**HTML

			Customize cCustomize = new Customize(engine, context, req, res);
			try {
				cCustomize.print();
			} catch ( Exception ex ) {
				out.println( ex.getMessage() );
			}

            //**HTML
			out.println("</td></tr>");
			//**HTML
            
			//UPDATE THEME
		}
		else if ( "pagetheme".equalsIgnoreCase(action)  && "true".equals(islogin) ) {
            

			//**HTML
			out.println("<tr><td>");
			out.println("<table width=\"100%\" cellpadding=\"1\" cellspacing=\"1\" border=\"0\">");
			out.println("<tr><td bgcolor=\"silver\" style=\"font-size:12pt; font-weight:bold\">");
			out.println("Personalization");
			out.println("</td></tr></table>");
			out.println("</td></tr>");
			out.println("<tr><td>");
            //*HTML
            
			lebah.app.UpdatePageStyleModule pageStyle = new lebah.app.UpdatePageStyleModule(engine, context, req, res);
			try {
				pageStyle.print();
			} catch ( Exception ex ) {
				out.println( ex.getMessage() );
			}
			//**HTML
			out.println("</td></tr>");
            //**HTML
		}
        else if ( "addHtmlContentModule".equalsIgnoreCase(action)  && "true".equals(islogin) ) {

            
            //**HTML
            out.println("<tr><td>");
            out.println("<table width=\"100%\" cellpadding=\"1\" cellspacing=\"1\" border=\"0\">");
            out.println("<tr><td bgcolor=\"silver\" style=\"font-size:12pt; font-weight:bold\">");
            out.println("Add HTML Module");
            out.println("</td></tr></table>");
            out.println("</td></tr>");
            out.println("<tr><td>");
            //*HTML
            
            lebah.app.NewHtmlModule newHtml = new lebah.app.NewHtmlModule(engine, context, req, res);
            try {
                newHtml.print();
            } catch ( Exception ex ) {
                out.println( ex.getMessage() );
            }
            //**HTML
            out.println("</td></tr>");
            //**HTML            
        }
        else if ( "contentManagerModule".equalsIgnoreCase(action)  && "true".equals(islogin) ) {

            //**HTML
            out.println("<tr><td>");
            out.println("<table width=\"100%\" cellpadding=\"1\" cellspacing=\"1\" border=\"0\">");
            out.println("<tr><td bgcolor=\"silver\" style=\"font-size:12pt; font-weight:bold\">");
            out.println("Content Manager");
            out.println("</td></tr></table>");
            out.println("</td></tr>");
            out.println("<tr><td>");
            //*HTML
            
            lebah.app.HtmlRepositoryModule repository = new lebah.app.HtmlRepositoryModule(engine, context, req, res);
            try {
                repository.print();
            } catch ( Exception ex ) {
                out.println( ex.getMessage() );
            }
            //**HTML
            out.println("</td></tr>");
            //**HTML            
        }		
		else  {
           
            
			Tabber cTabber = null;
			cTabber = new Tabber(engine, context, req, res);

			//If no request for action (tab), open the first tab by default
			if ( "".equals(action) ) {
				Tab firstTab = cTabber.getFirstTab();
				if ( firstTab != null )  action = firstTab.getId();
				session.setAttribute("_portal_action", action);
                session.setAttribute("_tab_id", action);
				//System.out.println("first action =" + action);
			}
            else {
                session.setAttribute("_tab_id", action);
            }

			//TABBER
            //**HTML
			out.println("<tr><td>");
            //**HTML
			try {
				if ( cTabber != null ) cTabber.print();
			} catch ( Exception ex ) {
				out.println(ex.getMessage());
			}
            
            //**HTML
			out.println("</td></tr>");
            //**HTML

			//determine how to display the page
			//TODO: need to add more display types
			try {
				//get display type, and display the page
				String usrlogin = (String) session.getAttribute("_portal_login");
				String display_type = UserPage.getDisplayType(usrlogin, action);
				if ( "left_navigation".equals(display_type) )
					DisplayContent.showNavigationType(engine, context, getServletConfig(), req, res, module, out, session);
                else if ( "top_navigation".equals(display_type) )
                    DisplayContent.showTopNavigationType(engine, context, getServletConfig(), req, res, module, out, session);
                else if ( "three_columns_with_top".equals(display_type) )
                    DisplayContent.showThreeColumnsWithSingleTop(engine, context, getServletConfig(), req, res, module, out, session);
				else if ( "single_column".equals(display_type) )
					DisplayContent.showModularType(engine, context, getServletConfig(), req, res, module, out, session);
				else if ( "narrow_wide".equals(display_type) )
					DisplayContent.showNarrowWideType(engine, context, getServletConfig(), req, res, module, out, session);
				else if ( "two_columns".equals(display_type) )
					DisplayContent.showTwoColumnsType(engine, context, getServletConfig(), req, res, module, out, session);
				else if ( "three_columns".equals(display_type) )
					DisplayContent.showThreeColumnsType(engine, context, getServletConfig(), req, res, module, out, session);
				else
					DisplayContent.showNavigationType(engine, context, getServletConfig(), req, res, module, out, session);
			} catch ( Exception ex ) {
				out.println( ex.getMessage() );
			} finally {
				//long totalMem = Runtime.getRuntime().totalMemory();
				//long freeMem = Runtime.getRuntime().freeMemory();
				//System.gc();
				//icnt++;
				//System.out.println(icnt + ") free memory = " + freeMem + "/" + totalMem);
			}
		}


		//FOOTER
		//Initiate VTemplate object
		Footer cFooter = new Footer(engine, context, req, res);
        
        //**HTML
		out.println("<tr><td>");
        //**HTML
        
		try {
			cFooter.print();
		} catch ( Exception ex ) {
			out.println( ex.getMessage() );
		}
        
        //**HTML
		out.println("</td></tr>");
		out.println("</table>");
		out.println("</div>");
		out.println("</body>");
		out.println("</html>");
        //**HTML
		
		
		
		// CLEANUP VELOCITY CONTEXT: BEGIN 
		//CleanUpVelocityContext.run(context, "ControllerServlet");
	}

	private void showError(String err, HttpServletRequest req, HttpServletResponse res) {
		ErrorMsg emsg = new ErrorMsg(engine, context, req, res);
		emsg.setError(err);
		try {
			emsg.print();
		} catch ( Exception ex ) {
			//error while displaying error!!
			System.out.println("ERROR WHILE SHOW ERROR");
		}
	}

	private static boolean getx(int y, int m) {
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		calendar.set(y, m-1, 1);
		long expireMillis = calendar.getTime().getTime();
		long currentMillis = System.currentTimeMillis();
		return expireMillis < currentMillis;
	}
}
