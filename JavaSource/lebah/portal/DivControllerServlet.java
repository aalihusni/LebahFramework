package lebah.portal;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lebah.db.Labels;
import lebah.portal.db.CustomClass;
import lebah.portal.db.UserPage;
import lebah.portal.velocity.VServlet;
import lebah.portal.velocity.VTemplate;
import lebah.util.Util;

public class DivControllerServlet  extends VServlet  {
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)  throws ServletException, IOException    {
		doPost(req, res);
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException  {
		res.setContentType("text/html");
		//res.setCharacterEncoding("UTF-8");
		//res.setHeader("Content-Type", "text/html;charset=UTF-8");
		PrintWriter out = res.getWriter();
		HttpSession session = req.getSession();
		
		/*
		boolean localAccess = false;
	    if ( "127.0.0.1".equals(req.getRemoteAddr()) ) localAccess = true;
	    if ( localAccess ) {
	    	doService(req, res, out, session);
	    }
        */
		doService(req, res, out, session);
		
		
 		// CLEANUP VELOCITY CONTEXT: END
 		
 		//CleanUpVelocityContext.run(context, "DivControllerServlet");
		
	}

	private void doService(HttpServletRequest req, HttpServletResponse res, PrintWriter out, HttpSession session)
			throws ServletException {
		
		//synchronized(this) {
			context = (org.apache.velocity.VelocityContext) session.getAttribute("VELOCITY_CONTEXT");
			engine = (org.apache.velocity.app.VelocityEngine) session.getAttribute("VELOCITY_ENGINE");
			if ( context == null ) {
				initVelocity(getServletConfig());
				session.setAttribute("VELOCITY_CONTEXT", context);
				session.setAttribute("VELOCITY_ENGINE", engine);
			}
		//}


        String app_path = getServletContext().getRealPath("/");
        app_path = app_path.replace('\\', '/');		
		session.setAttribute("_portal_app_path", app_path);
		
		String uri = req.getRequestURI();
		String s1 = uri.substring(1);
		context.put("appname", s1.substring(0, s1.indexOf("/")));		
		session.setAttribute("_portal_appname", s1.substring(0, s1.indexOf("/")));		
		//get pathinfo
        String pathInfo = req.getPathInfo();
        pathInfo = pathInfo.substring(1); //get rid of the first '/'
        int slash = pathInfo.indexOf("/");
        boolean allowed = true;
        if ( allowed ){
        	
    		//LABELS Properties
        	/*
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
    		*/
        	
	        //pathInfo only contains action
	        pathInfo = pathInfo.substring(pathInfo.indexOf("/") + 1);
			String module = pathInfo != null ? pathInfo : "";	
			//if ( !"".equals(module) ) {
				try {
					Object content = null;
					try {

						String className = "";

						if (( module.indexOf(".") > 0 )) {
							className = module;
							//content = ClassLoadManager.load(className, req.getRequestedSessionId());
							content = ClassLoadManager.load(className);
							((VTemplate) content).setId(className);
						}
						else{
							className = CustomClass.getName(module);
							content = ClassLoadManager.load(className, module, req.getRequestedSessionId());
							((VTemplate) content).setId(module);
						}
						{	
							((VTemplate) content).setEnvironment(engine, context, req, res);	
							((VTemplate) content).setServletContext(getServletConfig().getServletContext());	
							((VTemplate) content).setServletConfig(getServletConfig());
							((VTemplate) content).setDiv(true);
							
							if ( content instanceof Attributable ) {
								Hashtable h = UserPage.getValuesForAttributable(module);
								if ( h != null ) {
									((Attributable) content).setValues(h);
								}	
							}
							
							try {
								if ( content != null ) {
									((VTemplate) content).setShowVM(true);
									((VTemplate) content).print(session);
								}
							} catch ( Exception ex ) {
								out.println( ex.getMessage() );
							}						
						}
						
					} catch ( ClassNotFoundException cnfex ) {
						content = new ErrorMsg(engine, context, req, res);
						((ErrorMsg) content).setError("ClassNotFoundException : " + cnfex.getMessage());				
					} catch ( InstantiationException iex ) {
						content = new ErrorMsg(engine, context, req, res);
						((ErrorMsg) content).setError("InstantiationException : " + iex.getMessage());			
					} catch ( IllegalAccessException illex ) {
						content = new ErrorMsg(engine, context, req, res);
						((ErrorMsg) content).setError("IllegalAccessException : " + illex.getMessage());			
					} catch ( Exception ex ) {
						content = new ErrorMsg(engine, context, req, res);
						((ErrorMsg) content).setError("Other Exception during class initiation : " + ex.getMessage());	
						ex.printStackTrace();					
					}	
					
				} catch ( Exception ex ) {
					System.out.println( ex.getMessage() );	
				} finally {
					//long totalMem = Runtime.getRuntime().totalMemory();
					//System.out.println("total memory = " + totalMem);	
				}
			//}
        }
	}

	

}
