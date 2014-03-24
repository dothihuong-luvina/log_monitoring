require 'logstat/ruby/main/Input.rb'
pi = ProcessInput.new

dataInput = pi.getInputData(conf['input'], mapDefaultInput)
listLogs = java.util.ArrayList.new
dataInput['list_logs'].each do |e|  
  if(e.is_a? Hash)
    e = java.util.HashMap.new(e)    
  end  
  listLogs.add(e)
end
persistentData = java.util.HashMap.new(dataInput['persistent_data'])
dataInputJava = java.util.HashMap.new
dataInputJava.put("list_logs",listLogs)
dataInputJava.put("persistent_data",persistentData) 
return dataInputJava