package lebah.portal.velocity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

public class VelocityEngineHolder {
	
	public static VelocityEngineHolder instance;
	private static VelocityEngine engine;
	
	private VelocityEngineHolder(ServletConfig config, ServletContext servletContext) throws FileNotFoundException, IOException {
		engine = new VelocityEngine();
		Properties p = loadConfiguration(config, servletContext);
        engine.setApplicationAttribute("javax.servlet.ServletContext", config.getServletContext());
    	engine.init(p);
	}
	
	public static VelocityEngineHolder getInstance(ServletConfig config, ServletContext servletContext) throws FileNotFoundException, IOException {
		if ( instance == null ) instance = new VelocityEngineHolder(config, servletContext);
		return instance;
	}
	
	public VelocityEngine getVelocityEngine() {
		return engine;
	}
	
	private Properties loadConfiguration(ServletConfig config, ServletContext servletContext) throws IOException, FileNotFoundException {
		/*
        *  get our properties file and load it
        */

		String propsFile = config.getInitParameter("properties");
		Properties p = new Properties();
        if ( propsFile != null ) {
			String realPath = servletContext.getRealPath(propsFile);
            if ( realPath != null ) propsFile = realPath;
            p.load( new FileInputStream(propsFile) );
        }

		String log = p.getProperty( Velocity.RUNTIME_LOG);
		if (log != null ) {
            log = servletContext.getRealPath( log );
			if (log != null) 
				p.setProperty( Velocity.RUNTIME_LOG, log );
        }

		
        String path = p.getProperty( Velocity.FILE_RESOURCE_LOADER_PATH );
        if ( path != null) {
            path = servletContext.getRealPath(  path );
            if ( path != null) {
                p.setProperty( Velocity.FILE_RESOURCE_LOADER_PATH, path );
            }
        }
        
        
        //IN DEPLOYMENT ENVIRONMENT MUST REMOVE THIS COMMENT
        //p.setProperty( Velocity.FILE_RESOURCE_LOADER_CACHE, "true" );
        
        return p;
    }	

}
