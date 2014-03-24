package org.wiperdog.logstat.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.wiperdog.jrubyrunner.JrubyRunner;
import org.wiperdog.logstat.libs.Common;
import org.wiperdog.logstat.service.LogStat;

/**
 * Implement of LogStat service
 * @author nguyenxuanluong
 *
 */
public class LogStatImpl implements LogStat{
	Bundle bundle;
	JrubyRunner jrService;
	public LogStatImpl(Bundle bundle){
		this.bundle = bundle;
		BundleContext context = bundle.getBundleContext();
		jrService = (JrubyRunner) context.getService(context.getServiceReference(JrubyRunner.class.getName()));
	}

	/**
	 * Monitoring logs
	 * @param args : An array of paramters
	 */
	public Map<String, Object> runLogStat(String logStatDir,Map<String,Object> conf) {
		Map<String,Object> dataFinal = null;
		try {
			// Get default values
			HashMap<String, Object> mapDefaultInput = new HashMap<String, Object>();
			HashMap<String, Object> mapDefaultOutput = new HashMap<String, Object>();
			Common common = new Common(logStatDir + "/conf/defaultInput.properties");
			HashMap<String, Object> mapDefault = common.getInputConfig();
			mapDefaultInput = (HashMap<String, Object>) mapDefault.get("input");
			mapDefaultOutput = (HashMap<String, Object>) mapDefault.get("output");
			Map<String,Object> inputData = new HashMap<String,Object>();   
			// Ruby process
			System.out.println("LogStartService Running ...");
			inputData.put("conf", conf);			
			inputData.put("mapDefaultInput", mapDefaultInput);
			inputData.put("mapDefaultOutput", mapDefaultOutput);
			String procInputFile = logStatDir + "/main/process_input.rb";
			String procFilterFile = logStatDir + "/main/process_filter.rb";
			String procOutputFile = logStatDir + "/main/process_output.rb";
			Map<String,Object> dataInput = (Map<String, Object>) jrService.execute(procInputFile, inputData);
			inputData.put("dataInput", dataInput);
			Map<String,Object> dataFiltered = (Map<String, Object>) jrService.execute(procFilterFile, inputData);
			inputData.put("dataFiltered", dataFiltered);
			dataFinal = (Map<String, Object>) jrService.execute(procOutputFile, inputData);
			System.out.println("LogStartService Completed ...");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return dataFinal;
	}

}