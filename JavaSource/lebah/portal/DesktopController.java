/* ************************************************************************
LEBAH PORTAL FRAMEWORK
Copyright (C) 2007  Shamsul Bahrin

* ************************************************************************ */

package lebah.portal;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import lebah.db.DbException;
import lebah.db.Labels;
import lebah.portal.db.AuthenticateUser;
import lebah.portal.db.PrepareUser;
import lebah.portal.db.Role;
import lebah.portal.db.User;
import lebah.portal.db.UserData;
import lebah.portal.db.UserModuleDb;
import lebah.portal.db.UserPage;
import lebah.portal.element.Tab;
import lebah.portal.velocity.VTemplate;
import lebah.util.Util;



/**
 * @author Shamsul Bahrin Abd Mutalib
 * @version 1.01
 */
public class DesktopController extends lebah.portal.velocity.VServlet {
	//private static long icnt = 0;
	private LoginIntercept loginIntercept = null;
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)  throws ServletException, IOException    {
		doPost(req, res);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException  {
		try {
			doBody(req, res);
			
		} catch ( Exception e) {
	        PrintWriter out = res.getWriter();
			res.setContentType("text/html");
			
			out.println("<font style=\"color:red; font-size:14pt; font-weight:bold\">");
			out.println("ERROR during initiation of the application!!<br>");
			out.println("The stack trace for this error are as below:<br><br>");
			out.println("</font>");
			out.println("<font style=\"color:red; font-size:10pt; font-weight:bold\">");
			e.printStackTrace(out);
			out.println("</font>");
		}

	}
	
	public void doBody(HttpServletRequest req, HttpServletResponse res) throws Exception {

		res.setContentType("text/html");
        //res.setCharacterEncoding("UTF-8");

        PrintWriter out = res.getWriter();
		HttpSession session = req.getSession();
		
		
		//synchronized(this) {
			context = (VelocityContext) session.getAttribute("VELOCITY_CONTEXT");
			engine = (VelocityEngine) session.getAttribute("VELOCITY_ENGINE");
			
			if (context == null) {
				System.out.println("... INITIALIZE VELOCITY");
				super.initVelocity(getServletConfig());
				session.setAttribute("VELOCITY_CONTEXT", context);
				session.setAttribute("VELOCITY_ENGINE", engine);
			}
			
		//}
			
		
		/*
		context = (org.apache.velocity.VelocityContext) getServletConfig().getServletContext().getAttribute("VELOCITY_CONTEXT");
		engine = (org.apache.velocity.app.VelocityEngine) getServletConfig().getServletContext().getAttribute("VELOCITY_ENGINE");
		*/
		context.put("relativeDir",  "../");
		String userAgent = req.getHeader("User-Agent");
		context.put("userAgent", userAgent);
        if ( userAgent.indexOf("MSIE") > 0 ) context.put("browser", "ie");
        else if ( userAgent.indexOf("Firefox") > 0 ) context.put("browser", "firefox");
        else if ( userAgent.indexOf("Netscape") > 0 ) context.put("browser", "netscape");
        else if ( userAgent.indexOf("Safari") > 0 ) context.put("browser", "safari");
        else if ( userAgent.indexOf("MIDP") > 0 ) context.put("browser", "midp");
        else {

        }
		
		//HEADER, CACHE CONTROL
		//Format formatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z");
		//Date date = new Date();
		//String s = formatter.format(date);
        
		res.setHeader("Expires", "Tue, 25 Dec 1993 23:59:59 GMT"); //this date needs to be in the past.
		res.setHeader("Pragma", "no-cache");
		res.setHeader("Cache-control", "no-cache");
		res.setHeader("Last-Modified", "FRI, JUN 26 2020 23:59:59 GMT"); // this date needs to be in the future
		//
		  
		//handy utilities
		context.put("util", new Util());
		
		//LABELS Properties
		Labels label = null;
		String language = req.getParameter("lang");
		if ( language != null && !"".equals(language)) {
			session.setAttribute("_portal_language", language);
		}
		language = (String) session.getAttribute("_portal_language");
		if ( language == null || "".equals(language)) {
			label = lebah.db.Labels.getInstance();
			context.put("label", label.getTitles());
			language = label.getDefaultLanguage();
			session.setAttribute("_portal_language", language);
		} else {
			label = lebah.db.Labels.getInstance(language);
			context.put("label", label.getTitles());
		}
		context.put("lang", language);
		
		//Security token for ajax servlet (controller3)
		String securityToken = (String) session.getAttribute("securityToken");
		if ( securityToken == null || "".equals(securityToken) ) {
			securityToken = lebah.db.UniqueID.getUID();
			session.setAttribute("securityToken", securityToken);
		}
        context.put("securityToken", securityToken);
        
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
        app_path = app_path != null ? app_path.replace('\\', '/') : "";
        if ( "".equals(app_path)) {
        	System.out.println("[DesktopController] WARNING: app_path is null.");
        }
        session.setAttribute("_portal_app_path", app_path);
        
        //SERVER REQUEST INFORMATION
		lebah.util.Util.getServerRequestInfo(req, session, context);
		
		//get pathinfo
        String pathInfo = req.getPathInfo();
        pathInfo = pathInfo.substring(1); //get rid of the first '/'
        session.setAttribute("_portal_pathInfo", pathInfo);
        //pathInfo only contains action
		String action = pathInfo != null ? pathInfo : "";
		
	
		if ( "logout".equals(action)) {
			res.sendRedirect("../logout1.jsp");
			return;
		}

		//Set role to anonymous by default
		if ( session.getAttribute("_portal_role") == null || "".equals((String)session.getAttribute("_portal_role"))) {
			session.setAttribute("_portal_role", "anon");
			session.setAttribute("_portal_username", "Anonymous");
			session.setAttribute("_portal_login", "anon");
			session.setAttribute("_portal_islogin", "false");
			session.setAttribute("_portal_css", null);
		}

		//module
		String module = req.getParameter("_portal_module") != null ?
					    req.getParameter("_portal_module") : "";
		if ( session.getAttribute("_portal_module") != null )
			if ( (module == null) || ("".equals(module)) ) module = (String) session.getAttribute("_portal_module");


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


		if ( "false".equals((String) session.getAttribute("_portal_islogin")) ){

			
			String visitor = req.getParameter("visitor") != null ?
							 req.getParameter("visitor") :
							 session.getAttribute("_portal_visitor") != null ?
							 (String) session.getAttribute("_portal_visitor") : "anon";
							 
			//this visitor user login must be of role anon
			if ( !"anon".equals(visitor) && "anon".equals(PrepareUser.getRole(visitor)) ) {
				session.setAttribute("_portal_login", visitor);
			}
			else {
				session.setAttribute("_portal_role", visitor);
			}
			
			session.setAttribute("_portal_visitor", visitor);
			session.setAttribute("_portal_login", visitor);
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
				css = session.getAttribute("_portal_css") != null ? (String) session.getAttribute("_portal_css") : UserPage.getCSS((String) session.getAttribute("_portal_login") );
			}
		} catch ( DbException cssex ) {
			System.out.println("[DesktopController] got error in getting css");
			//throw new Exception("Database Error:");
		}
		session.setAttribute("_portal_css", css);
		//To store content template
		VTemplate content = null;

        //**HTML
		out.println("<!DOCTYPE html>");
		out.println("<html>");
		out.println("<head>");
		out.println("<title>");
		//**HTML
		
		//TITLE
		Title cTitle = new Title(engine, context, req, res);
		try {
			cTitle.print();
		} catch ( Exception ex ) {
			System.out.println("[DesktopController] Error while doing TITLE");
			ex.printStackTrace();
			
		}

        //**HTML
		out.println("</title>");
		out.println("<meta http-equiv=\"Content-type\" content=\"text/html; charset=utf-8\" />");
		
		out.println("<meta charset=\"utf-8\">");
		out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
		

		context.put("user_css", css);
		
		
		//js_css
		JS_CSS js_css = new JS_CSS(engine, context, req, res);
		try {
			js_css.print();
		} catch ( Exception ex ) {
			ex.printStackTrace();
		} 
		
		out.println("<link rel=\"shortcut icon\" href=\"../favicon.ico\" />");
		
		out.println("<style media=\"all\" type=\"text/css\">");
		out.println("html, body { height: 100%; overflow: hidden; }");
		out.println("#lebah-container { height: 100%; overflow: auto; position: relative; z-index:0; }");
		out.println("</style>");
		
		out.println("</head>");
		out.println("<body>");
		
		//
		if ( ((String) req.getSession().getAttribute("__expired_")).equals("yes")) {
			String days = req.getSession().getAttribute("__days") != null ? (String) req.getSession().getAttribute("__days") : "";
			out.println("<div style=\"padding:5px;text-align:center;background:#EF1313;color:#FFFF00;font-family:Verdana;font-size:11pt\">");
			out.println("&gt;&gt;&gt;&gt;This copy shall expire within " + days + " days.  Please contact us for legal copy of this system - <a style=\"color:#FFFF00;text-decoration:none\" href=\"http://www.iqwan.my\">http://www.iqwan.my</a>&nbsp;&lt;&lt;&lt;&lt;");
			out.println("</div>");
		}
		//

		out.println("<div id=\"lebah-container\" class=\"lebah-container\">");
		//**
		out.println("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\"><tr><td  align=\"center\" style=\"padding:0px\">");
		//**
		
		out.println("<table class=\"lebah-container\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"padding:0px\">");
        //**HTML

		//Handle login request
		
		boolean isLoginSuccess = false;
		boolean isLogout = false;
		boolean isAccessDenied = false;
		
		if ( action.equalsIgnoreCase("login") ) {
			String usrlogin = req.getParameter("username");
			String password = req.getParameter("password");
			try {
				AuthenticateUser auth = new AuthenticateUser(req);

				if ( auth.lookup(usrlogin, password) ) {
					
					//for chat module
					session.setAttribute("nickname", usrlogin);
					
					session.setAttribute("_portal_role", auth.getRole());
					session.setAttribute("_portal_username", auth.getUserName());
					session.setAttribute("_portal_login", auth.getUserLogin());
					session.setAttribute("_portal_islogin", "true");
					

					//CSS
					try {
						css = UserPage.getCSS((String) session.getAttribute("_portal_login") );
						session.setAttribute("_portal_css", css);
					} catch ( DbException cssex ) {
						System.out.println("[DesktopController] Can't get CSS");
					}
					isLoginSuccess = true;
					

				} else {
					session.setAttribute("_portal_role", "anon");
					session.setAttribute("_portal_username", "Anonymous");
					session.setAttribute("_portal_login", "anon");
					session.setAttribute("_portal_islogin", "false");

					
					isAccessDenied = true;
				}
				
			
			} catch ( Exception ex ) {
				content = new ErrorMsg(engine, context, req, res);
				((ErrorMsg) content).setError(ex.getMessage());
			}
		}
		else if ( action.equalsIgnoreCase("logout") ) {
			context.put("secondaryRoles", new Vector());			
			isLogout = true;
		}
		
		if ( isLoginSuccess ) {
			res.sendRedirect("");
		} else if ( isLogout ) {
			String command = req.getParameter("portalCommand");
			if ( "save_portlets_sequence".equals(command)) {
				savePortletsSequence(req, action);
			}
			else {
				res.sendRedirect("../logout1.jsp");
			}
		} else if ( isAccessDenied ) {
			res.sendRedirect("../accessdenied.jsp");
		} else {
			
			String command = req.getParameter("portalCommand");
			if ( "save_portlets_sequence".equals(command)) {
				savePortletsSequence(req, action);
			}
			else {
				if ( !createPortalPage(req, res, out, session, action, module) ) {
					System.out.println("ERROR IN CREATING PORTAL PAGE..");
					if ( !res.isCommitted() ) {
						res.sendRedirect("../logout1.jsp");
					}
					else {
						System.out.println("Response already committed!");
					}
				}
			}
		}
	}

	


	private void savePortletsSequence(HttpServletRequest req, String action) {
		String portlets = req.getParameter("POrder");
		String usrlogin = (String) req.getSession().getAttribute("_portal_login");
		String usrrole = (String) req.getSession().getAttribute("_portal_role");
		String sessionId = req.getSession().getId();
		try {
			UserModuleDb.savePortletsSequence(usrlogin, portlets, action, usrrole, sessionId);
		} catch ( Exception e ) {
			System.out.println("****Exception in Saving Portlets Sequence: " + e.getMessage());
			e.printStackTrace();
		}
		
	}

	private boolean createPortalPage(HttpServletRequest req, HttpServletResponse res, 
			PrintWriter out, HttpSession session, String action, String module) throws Exception {

		//TODO
		//what if user has logout but press the browser's back button
		String islogin = session.getAttribute("_portal_islogin") != null ? (String) session.getAttribute("_portal_islogin") : "";
		String login = session.getAttribute("_portal_login") != null ? (String) session.getAttribute("_portal_login") : "";
		
		User user = UserData.getUser(login);
		context.put("secondaryRoles", user.getSecondaryRoles());
		context.put("numRoles", user.getSecondaryRoles().length);
		Role primaryRole = user.getRole();
		context.put("primaryRole", primaryRole);
		
		//myrole management
		
		String myrole = req.getParameter("myrole");
		if ( myrole == null ) {
			myrole = (String) session.getAttribute("_portal_role"); 
		}
		else {
			if ("".equals(myrole)) {
				session.setAttribute("_portal_role", primaryRole.getName());
				myrole = primaryRole.getName();
			} else {
				session.setAttribute("_portal_role", myrole);
			}
			action = ""; //change of role so new action
		}
		// for a particular role.
		session.setAttribute("myrole", myrole);
		context.put("myrole", myrole);
		
		//recheck myrole if it was hacked
		boolean rolehacked = true;
		if ( !myrole.equals(primaryRole.getName()) ) {
			Role[] roles = user.getSecondaryRoles();
			for ( Role role : roles ) {
				if ( myrole.equals(role.getName())) {
					rolehacked = false;
					break;
				}
			}
		}
		if ( rolehacked ) {
			myrole = primaryRole.getName(); //return back to primary role
			session.setAttribute("_portal_role", myrole);
			session.setAttribute("myrole", myrole);
			context.put("myrole", myrole);
		}
		
		//##PROCESS LOGIN INTERCEPTOR
		loginIntercept = (LoginIntercept) Props.item.get("LOGIN_INTERCEPT");
		if ( loginIntercept != null ) {
			loginIntercept.process(req, res, context);
		}
		
		//Initiate VTemplate objects
		Header cHeader = new Header(engine, context, req, res);

		//HEADER
        //**HTML
		out.println("<tr><td style=\"padding:0px;\">");
		
		cHeader.print();
        //**HTML
		out.println("</td></tr>");
		
		
		//**HTML
		if ( "customize".equalsIgnoreCase(action) && "true".equals(islogin) ) {
			//CUSTOMIZE MODULE
			
			//START 
			out.println("<tr><td style=\"padding:0px\">");
			out.println("<table class=\"modules\">");
			
			out.println("<tr><td style=\"padding:0px\">");
			out.println("<table width=\"100%\" cellpadding=\"1\" cellspacing=\"1\" border=\"0\">");
			out.println("<tr><td bgcolor=\"silver\" style=\"font-size:12pt; font-weight:bold\">");
			out.println("Personalization");
			out.println("</td></tr></table>");
			out.println("</td></tr>");
			out.println("<tr><td style=\"padding:0px\">");
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
			
			//START 
			out.println("<tr><td style=\"padding:0px\">");
			out.println("<table class=\"modules\">");
			
			out.println("<tr><td style=\"padding:0px\">");
			out.println("<table bgcolor=\"silver\" width=\"100%\" cellpadding=\"1\" cellspacing=\"1\" border=\"0\">");
			out.println("<tr><td style=\"font-size:12pt; font-weight:bold\">");
			out.println("Personalization");
            out.println("</td><td align=\"right\"><input class=\"linkbutton\" type=\"button\" value=\"Back\" onclick=\"document.location.href='customize'\"></td></tr></table>");
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
		//This is CONTENT MANAGEMENT SYSTEM (CMS)
        else if ( "addHtmlContentModule".equalsIgnoreCase(action)  && "true".equals(islogin) ) {

            
            //**HTML
        	
			//START 
			out.println("<tr><td>");
			out.println("<table class=\"modules\">");
			
            out.println("<tr><td>");
            out.println("<table bgcolor=\"silver\" width=\"100%\" cellpadding=\"1\" cellspacing=\"1\" border=\"0\">");
            out.println("<tr style=\"height:50px\"><td style=\"font-size:12pt; font-weight:bold\">");
            out.println("Add Content");
            out.println("</td><td align=\"right\"><input class=\"linkbutton\" type=\"button\" value=\"Back\" onclick=\"document.location.href='../c/'\"></td></tr></table>");
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
        	
			//START 
			out.println("<tr><td>");
			out.println("<table class=\"modules\">");
			
            out.println("<tr><td>");
            out.println("<table bgcolor=\"silver\" width=\"100%\" cellpadding=\"1\" cellspacing=\"1\" border=\"0\">");
            out.println("<tr><td style=\"font-size:12pt; font-weight:bold\">");
            out.println("Content Manager");
            out.println("</td><td align=\"right\"><input type=\"button\"  value=\"Back\" onclick=\"document.location.href='../c/'\"></td></tr></table>");
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
        else if ( "CSSManagerModule".equalsIgnoreCase(action)  && "true".equals(islogin) ) {

            //**HTML
        	
			//START 
			out.println("<tr><td>");
			out.println("<table class=\"modules\">");
			
            out.println("<tr><td>");
            out.println("<table bgcolor=\"silver\" width=\"100%\" cellpadding=\"1\" cellspacing=\"1\" border=\"0\">");
            out.println("<tr><td style=\"font-size:12pt; font-weight:bold\">");
            out.println("CSS File Manager");
            out.println("</td><td align=\"right\"><input type=\"button\" value=\"Back\" onclick=\"document.location.href='pagetheme'\"></td></tr></table>");
            out.println("</td></tr>");
            out.println("<tr><td>");
            //*HTML
            
            lebah.util.CSSFileManagerModule repository = new lebah.util.CSSFileManagerModule(engine, context, req, res);
            try {
                repository.print();
            } catch ( Exception ex ) {
                out.println( ex.getMessage() );
            }
            //**HTML
            out.println("</td></tr>");
            //**HTML            
        }
        else if ( "CSSRegistry".equalsIgnoreCase(action)  && "true".equals(islogin) ) {

            //**HTML
        	
			//START 
			out.println("<tr><td>");
			out.println("<table class=\"modules\">");
			
            out.println("<tr><td>");
            out.println("<table bgcolor=\"silver\" width=\"100%\" cellpadding=\"1\" cellspacing=\"1\" border=\"0\">");
            out.println("<tr><td style=\"font-size:12pt; font-weight:bold\">");
            out.println("CSS File Manager");
            out.println("</td><td align=\"right\"><input type=\"button\"  value=\"Back\" onclick=\"document.location.href='pagetheme'\"></td></tr></table>");
            out.println("</td></tr>");
            out.println("<tr><td>");
            //*HTML
            
            lebah.module.theme.PageStyleManagerModule repository = new lebah.module.theme.PageStyleManagerModule(engine, context, req, res);
            try {
                repository.print();
            } catch ( Exception ex ) {
                out.println( ex.getMessage() );
            }
            //**HTML
            out.println("</td></tr>");
            //**HTML            
        }		
		//ELSE INSTANTIATES AND DISPLAY THE MODULES
		else  {
			
			String usrlogin = (String) session.getAttribute("_portal_login");
			String display_type =  UserPage.getDisplayType(usrlogin, action, myrole);

			//System.out.println("display type = " + display_type);
			//TabId NOT FOUND
			if ( !"".equals(action) && "".equals(display_type)) {
				action = "";
				//res.sendRedirect("../logout1.jsp");
				//return false;
			}
			
			Tabber cTabber = new Tabber(engine, context, req, res);
			//If no request for action (tab), open the first tab by default by setting action to first tab id
			if ( "".equals(action) ) {
				Tab firstTab = cTabber.getFirstTab();
				if ( firstTab != null )  action = firstTab.getId();  //set action to first tab
				session.setAttribute("_portal_action", action);
                session.setAttribute("_tab_id", action);
                display_type =  UserPage.getDisplayType(usrlogin, action, myrole);
			}
            else {
                session.setAttribute("_tab_id", action);
            }
			
			//TABBER
            //**HTML
			out.println("<tr><td style=\"padding:0px\">");
            //**HTML
			try {
				if ( cTabber != null ) cTabber.print();
				else {
					System.out.println("THERE ARE NO TABS DEFINED!!");
				}
			} catch ( Exception ex ) {
				out.println(ex.getMessage());
			}
            
            //**HTML
			out.println("</td></tr>");
			out.println("<tr><td class=\"tabs_modules_gap\">");
			out.println("</td></tr>");
			
			//START 
			out.println("<tr><td>");
			out.println("<table class=\"modules\">");

            //**HTML
			try {
				if ( "left_navigation".equals(display_type) )
					DisplayContent.showNavigationType(engine, context, getServletConfig(), req, res, module, out, session);
				else if ( "single_column".equals(display_type) )
					DisplayContent.showModularType(engine, context, getServletConfig(), req, res, module, out, session, true);
				else if ( "two_columns".equals(display_type) )
					DisplayContent.showTwoColumnsResponsiveType(engine, context, getServletConfig(), req, res, module, out, session);
				else if ( "three_columns".equals(display_type) ) 
					DisplayContent.showThreeColumnsResponsiveType(engine, context, getServletConfig(), req, res, module, out, session);
				else if ( "narrow_left_three_columns".equals(display_type) ) 
					DisplayContent.showNarrowLeftThreeColumnsType(engine, context, getServletConfig(), req, res, module, out, session);
				else if ( "narrow_wide".equals(display_type) ) 
					DisplayContent.showNarrowWideResponsiveType(engine, context, getServletConfig(), req, res, module, out, session);
				else if ( "wide_narrow".equals(display_type) )
					DisplayContent.showWideNarrowResponsiveType(engine, context, getServletConfig(), req, res, module, out, session);
				else if ( "narrow_wide_narrow".equals(display_type) )
					DisplayContent.showNarrowWideNarrow(engine, context, getServletConfig(), req, res, module, out, session);			
				else if ( "top_navigation".equals(display_type) )
                    DisplayContent.showTopNavigationType(engine, context, getServletConfig(), req, res, module, out, session);
				else if ( "pulldown_menu".equals(display_type) )
					DisplayContent.showPullDownMenuType(engine, context, getServletConfig(), req, res, module, out, session);
				else if ( "windows".equals(display_type) )
					DisplayContent.showWindowsType(engine, context, getServletConfig(), req, res, module, out, session);
                else if ( "top_navigation_with_title".equals(display_type) )
                    DisplayContent.showTopNavigationTitleType(engine, context, getServletConfig(), req, res, module, out, session);
                else if ( "two_columns_with_bottom".equals(display_type) )
                    DisplayContent.showTwoColumnsWithSingleBottom(engine, context, getServletConfig(), req, res, module, out, session);
                else if ( "two_columns_with_top".equals(display_type) )
                    DisplayContent.showTwoColumnsWithSingleTop(engine, context, getServletConfig(), req, res, module, out, session);
                else if ( "three_columns_with_top".equals(display_type) )
                    DisplayContent.showThreeColumnsWithSingleTop(engine, context, getServletConfig(), req, res, module, out, session);
                else if ( "three_columns_with_bottom".equals(display_type) )
                    DisplayContent.showThreeColumnsWithSingleBottom(engine, context, getServletConfig(), req, res, module, out, session);
				else if ( "single_column_no_title".equals(display_type) )
					DisplayContent.showModularType(engine, context, getServletConfig(), req, res, module, out, session, false);
				else
					DisplayContent.showNavigationType(engine, context, getServletConfig(), req, res, module, out, session);
			} catch ( Exception ex ) {
				out.println( ex.getMessage() );
				ex.printStackTrace();
			} finally {
				//long totalMem = Runtime.getRuntime().totalMemory();
				//long freeMem = Runtime.getRuntime().freeMemory();
				//System.gc();
			}
		}

		
		//END MODULES SECTION
		out.println("</table>");
		out.println("</td></tr>");

		//FOOTER
		//Initiate VTemplate object
		Footer cFooter = new Footer(engine, context, req, res);
		out.println("<tr><td>");
		try {
			cFooter.print();
		} catch ( Exception ex ) {
			out.println( ex.getMessage() );
		}
		out.println("</td></tr>");
		
		
		//out.println("<tr><td align=\"right\" style=\"font-size:8pt;color:#EFEFEF\">http://www.iqwan.my");
		//out.println("</td></tr>");
		out.println("</table>");
		
		
		// PORTLET SEQUENCE FORM
		//out.println("<form id=\"form_portlets_sequence\" name=\"form_portlets_sequence\"><input type=\"hidden\" id=\"portlets_sequence\" name=\"portlets_sequence\"></form>");
		
		//**
		out.println("</td></tr></table>");
		//**
		
		
		//CALENDAR DIV
		//out.println("<div id=\"CalenDarDiv1\" style=\"position:absolute;visibility:hidden;background-color:white;z-index:10;\"></div>");
		
		/*
		ModulePopWindow popwin = new ModulePopWindow(engine, context, req, res);
		try {
			popwin.print();
		} catch ( Exception ex ) {
			out.println( ex.getMessage() );
		}
		*/
		
		//if ( ((String) req.getSession().getAttribute("__expired_")).equals("yes")) {
		
			/*
			String days = req.getSession().getAttribute("__days") != null ? (String) req.getSession().getAttribute("__days") : "";
			out.println("<div style=\"padding:5px;text-align:right;color:#FFF;font-family:arial;font-size:1pt\">");
			out.println("&nbsp;" + days + "&nbsp;");
			out.println("</div>");
			*/
			
			
		//}		
		
		
		out.println("</div>"); //lebah_container
		
		//out.println("<div class=\"lebah-status-bar\" id=\"lebah-status-bar\">");
		//out.println("status-bar");		
		//out.println("</div>");
		
		
		
		//JIXEDBAR
		//Initiate VTemplate object
		/*
		Jixedbar jixedbar = new Jixedbar(engine, context, req, res);
		try {
			jixedbar.print();
		} catch ( Exception ex ) {
			out.println( ex.getMessage() );
		}
		*/
		
		out.println("</body>");
		out.println("</html>");
        //**HTML
		
		// CLEANUP VELOCITY CONTEXT: BEGIN 
		
		//CleanUpVelocityContext.run(context, "DesktopController");
		
		return true;
	}




}
