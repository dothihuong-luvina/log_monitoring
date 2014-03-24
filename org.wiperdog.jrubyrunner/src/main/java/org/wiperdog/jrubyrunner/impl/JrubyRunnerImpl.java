/**
 * @author luvina
 * Implement for JrubyRunner service
 */
package org.wiperdog.jrubyrunner.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.jruby.embed.LocalContextScope;
import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;
import org.jruby.embed.osgi.OSGiScriptingContainer;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.wiperdog.directorywatcher.Listener;
import org.wiperdog.jrubyrunner.JrubyRunner;

public class JrubyRunnerImpl implements JrubyRunner {
	Bundle bundle;
	BundleContext context;
	ScriptingContainer container;
	private static final Logger logger = Logger.getLogger(JrubyRunnerImpl.class);
	List<ServiceRegistration> listWatcher = new ArrayList<ServiceRegistration>();

	public JrubyRunnerImpl(Bundle bundle) {
		this.bundle = bundle;
		context = this.bundle.getBundleContext();
		container = new OSGiScriptingContainer(this.bundle, LocalContextScope.CONCURRENT,
				LocalVariableBehavior.PERSISTENT);
		container.setHomeDirectory("classpath:/META-INF/jruby.home");

	}

	/**
	 * Excute a ruby script input data and gems path
	 * 
	 * @param rubyPath
	 *            Ruby script path
	 * @param inputData
	 *            input data for script running
	 * @param libpaths
	 *            gems path for script running
	 * @return dataReturn : data return from script running
	 */
	public Object execute(String scriptPath, Map<String, Object> input, List<String> libpaths) {
		this.setLibPaths(libpaths, container);
		try {
			return this.execute(scriptPath, input);
		} catch (Exception e) {
			logger.debug(e);
		}
		return null;
	}
	
	/**
	 * Excute ruby script with input data for script
	 * @param scriptPath : Ruby script path
	 * @param input		 : input data for script running
	 * @return data for script running
	 */
	public Object execute(String scriptPath, Map<String, Object> input) {
		if (input != null) {
			for (Entry<String, Object> entry : input.entrySet()) {
				container.put(entry.getKey(), entry.getValue());
			}
		}
		try {
			return this.execute(scriptPath);
		} catch (Exception e) {
			logger.debug(e);
		}
		return null;
	}
	
	/**
	 * Excute ruby script with gems paths
	 * @param scriptPath
	 * @param libpaths
	 * @return
	 */
	public Object execute(String scriptPath,List<String> libpaths) {
		this.setLibPaths(libpaths, container);
		try {
			return this.execute(scriptPath);
		} catch (Exception e) {
			logger.debug(e);
		}
		return null;
	}
	
	/**
	 * Excute ruby script
	 * @param scriptPath
	 * @return
	 */
	public Object execute(String scriptPath) {
		File rubyFile = new File(scriptPath);
		Reader r;
		Object dataReturn = null;
		try {
			r = new FileReader(rubyFile);
			dataReturn = container.runScriptlet(r, rubyFile.getName());
		} catch (FileNotFoundException e) {
			logger.debug(" File " + rubyFile.getAbsolutePath() + " not found !");
		} catch (Exception e) {
			logger.debug(e);
		}
		return dataReturn;
	}

	/**
	 * Start to watching a ruby script folder
	 * 
	 * @param rubyDirPath
	 *            : Ruby script directory will be watching
	 * @param inputData
	 *            : input data for script running
	 * @param libpaths
	 *            : gems path for script running
	 */
	public void startWatcher(String rubyDirPath, Map<String, Object> inputData,
			List<String> libpaths) {
		try {
			this.setLibPaths(libpaths, container);
			RubyScripListener rbl = new RubyScripListener(container, rubyDirPath, inputData);
			ServiceRegistration sr = context.registerService(Listener.class.getName(), rbl, null);
			listWatcher.add(sr);
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
	}

	/**
	 * Stop watching a ruby script folder
	 * 
	 * @param rubyDirPath
	 */
	public void stopWatcher(String rubyDirPath) {
		Iterator<ServiceRegistration> iter = listWatcher.iterator();
		while (iter.hasNext()) {
			ServiceRegistration sr = iter.next();
			RubyScripListener objListener = (RubyScripListener) context.getService(sr
					.getReference());
			if (objListener != null) {
				if (objListener.getDirectory().equals(rubyDirPath)) {
					sr.unregister();
					iter.remove();
				}
			}
		}
	}

	/**
	 * Set gems paths for script running
	 * 
	 * @param libpaths
	 *            : List of gems path
	 * @param container
	 *            : a container to excute script
	 */
	public void setLibPaths(List<String> libpaths, ScriptingContainer container) {
		if (libpaths != null) {
			for (String i : libpaths) {
				container.put("path", i);
				container.runScriptlet("$LOAD_PATH << path ");
			}
		}
	}
}
