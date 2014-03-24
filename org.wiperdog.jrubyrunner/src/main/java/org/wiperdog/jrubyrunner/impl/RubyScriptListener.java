package org.wiperdog.jrubyrunner.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.jruby.embed.ScriptingContainer;
import org.wiperdog.directorywatcher.Listener;

/**
 * 
 * @author luvina 
 * An implement of org.wiperdog.directorywatcher.Listernet Any
 * change to script of watching folder will be raised an action by this class
 */
class RubyScripListener implements Listener {
	public String directory;
	public long interval;
	public ScriptingContainer container;
	public Map<String, Object> inputData;
	private static final Logger logger = Logger.getLogger(RubyScripListener.class);

	public RubyScripListener(ScriptingContainer container, String directory,
			Map<String, Object> inputData) {
		this.container = container;
		this.directory = directory;
		this.inputData = inputData;
	}

	public String getDirectory() {

		return this.directory;
	}

	public long getInterval() {
		return 100;
	}

	public boolean filterFile(File file) {
		if (file.isFile()) {
			String fname = file.getName();
			if (fname.endsWith(".rb")) {
				return true;
			}
		}
		return false;
	}

	public boolean notifyModified(File target) throws IOException {
		logger.debug("File modified: " + target.getAbsolutePath());
		runScript(target, inputData);
		return true;
	}

	public boolean notifyAdded(File target) throws IOException {
		logger.debug("File added: " + target.getAbsolutePath());
		runScript(target, inputData);
		return true;
	}

	public boolean notifyDeleted(File target) throws IOException {
		logger.debug("File deleted: " + target.getAbsolutePath());
		return true;
	}

	public void runScript(File file, Map<String, Object> inputData) {
		try {
			Reader reader = new FileReader(file);
			if (inputData != null) {
				for (Entry<String, Object> entry : inputData.entrySet()) {
					container.put(entry.getKey(), entry.getValue());
				}
			}
			container.runScriptlet(reader, file.getName());
		} catch (FileNotFoundException e) {
			logger.debug(" File " + file.getAbsolutePath() + " not found !");
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
	}
}
