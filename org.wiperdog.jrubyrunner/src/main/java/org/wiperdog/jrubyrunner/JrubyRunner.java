package org.wiperdog.jrubyrunner;

import java.util.List;
import java.util.Map;

public interface JrubyRunner {
	public Object execute(String scriptPath,Map<String,Object> input,List<String> gem_paths);
	public Object execute(String scriptPath,Map<String,Object> input);
	public Object execute(String scriptPath,List<String> gem_paths);
	public Object execute(String scriptPath);
	public void startWatcher(String rubyDirPath,Map<String,Object> input,List<String> gem_paths);
	public void stopWatcher(String rubyDirPath);
}
