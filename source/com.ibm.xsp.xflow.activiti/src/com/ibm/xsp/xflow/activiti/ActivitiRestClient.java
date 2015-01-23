/*
 * © Copyright IBM Corp. 2013
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */
package com.ibm.xsp.xflow.activiti;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.json.JSONArray;
import org.apache.commons.json.JSONException;
import org.apache.commons.json.JSONObject;

import com.ibm.xsp.xflow.activiti.data.ProcessInstance;
import com.ibm.xsp.xflow.activiti.data.Task;
import com.ibm.xsp.xflow.activiti.data.TaskCandidate;
import com.ibm.xsp.xflow.activiti.util.HttpClientUtil;

public class ActivitiRestClient {

	private static final String PROCESS_INSTANCE = "/process-instance";
	private String REST_URI = "http://localhost:8080/activiti-rest/service";
	private static Logger logger = Logger.getLogger(ActivitiRestClient.class.getName());
	private javax.servlet.http.Cookie[] cookies = null;
	
	public ActivitiRestClient(String baseUri,javax.servlet.http.Cookie[] cookies){
		REST_URI = baseUri;
		this.cookies = cookies;
	}

	public ProcessInstance createProcess(String processDefinitionId,HashMap<String,Object> variables)
    throws Exception {
		ProcessInstance instance = null;
		String uri = REST_URI + PROCESS_INSTANCE;
		JSONObject jsRequest = new JSONObject();
	    jsRequest.put("processDefinitionKey",processDefinitionId);
	    addVariablesToJsonRequest(variables, jsRequest);
	    logger.finest("request json:"+jsRequest.toString());
	    JSONObject jsObj = new JSONObject(HttpClientUtil.post(uri, cookies, jsRequest.toString()));
	    logger.finest("Returning processId " + jsObj.getString("id"));
	    instance = getProcessInstance(jsObj.getString("id"));
	    return instance;
	}
	
	public ProcessInstance getProcessInstance(String processInstnceId)  throws Exception {
		String uri = REST_URI + "/process-instance/"+processInstnceId;
	    JSONObject jsObj = new JSONObject(HttpClientUtil.get(uri, cookies));
	    logger.info("Retrieve process instnce:  " + jsObj.getString("processInstanceId"));
	    ProcessInstance instance = new ProcessInstance(jsObj.getString("processInstanceId"));
	    instance.setProcessDefinitionId(jsObj.getString("processDefinitionId"));
	    instance.setComplete(jsObj.getBoolean("completed"));
	    JSONArray jsonTasks = jsObj.getJSONArray("tasks");
	    List<Task> tasks = new ArrayList<Task>();
	    int taskNum = jsonTasks.length();
	    JSONObject jsonTask = null;
	    for(int i=0;i<taskNum;i++){
	    	jsonTask = (JSONObject)jsonTasks.get(i);
	    	Task task = getTask(jsonTask.getString("taskId"));
	    	tasks.add(task);
	    }
	    instance.setTasks(tasks);
	    return instance;
	}
	
	public ProcessInstance getProcessInstanceWithLastTask(String processInstanceId)  throws Exception {
		String uri = REST_URI + "/process-instance/"+processInstanceId;
	    JSONObject jsObj = new JSONObject(HttpClientUtil.get(uri, cookies));
	    logger.finest("Retrieve process instance:  " + jsObj.getString("processInstanceId"));
	    ProcessInstance instance = new ProcessInstance(jsObj.getString("processInstanceId"));
	    instance.setProcessDefinitionId(jsObj.getString("processDefinitionId"));
	    instance.setComplete(jsObj.getBoolean("completed"));
	    if(!instance.isComplete()){
		    JSONArray jsonTasks = jsObj.getJSONArray("tasks");
		    List<Task> tasks = new ArrayList<Task>();
		    int taskNum = jsonTasks.length();
		    JSONObject jsonTask = null;
	    	jsonTask = (JSONObject)jsonTasks.get(taskNum-1);
	    	Task task = getTask(jsonTask.getString("taskId"));
	    	tasks.add(task);
		    instance.setTasks(tasks);
	    }
	    return instance;
	}

	protected void addVariablesToJsonRequest(HashMap<String,Object> variables,
			JSONObject jsRequest) throws JSONException {
		if(variables!=null){
		    Iterator<String> it = variables.keySet().iterator();
		    while(it.hasNext()){
		    	String key = it.next();
		    	Object value = (Object)variables.get(key);
			    jsRequest.put(key,value);
		    }
	    }
	}
	
	public Task getTask(String taskId) throws IOException, JSONException {
		String uri = REST_URI + "/task/" + taskId;
		Task task = null;
		JSONObject object = null;
		try{
			object = new JSONObject(HttpClientUtil.get(uri, cookies));
		}catch(Exception e){
			e.printStackTrace();
		}
		if (object != null) {
			task = new Task(object.getString("id"));
			task.setName(object.getString("name"));
			if(!object.isNull("owner"))
				task.setOwner(object.getString("owner"));
			logger.finest("Owner:"+object.get("owner"));
			if(!object.isNull("assignee"))
				task.setAssignee(object.getString("assignee"));
			if(object.has("completed"))
				task.setComplete(object.getBoolean("completed"));
	    	task.setProcessDefinitionId(object.getString("processDefinitionId"));
	    	task.setProcessInstanceId(object.getString("processInstanceId"));
	    	task.setTaskDefinitionKey(object.getString("taskDefinitionKey"));
	    	if(!object.isNull("formResourceKey"))
	    		task.setForm(object.getString("formResourceKey"));
	    	JSONObject jsonCandidate = null;
	    	if(!object.isNull("identityLinkList")){
	    		jsonCandidate = object.getJSONArray("identityLinkList").getJSONObject(0);
		    	TaskCandidate candidate = new TaskCandidate((!jsonCandidate.isNull("userId"))?jsonCandidate.getString("userId"):null,
		    			(!jsonCandidate.isNull("groupId"))?jsonCandidate.getString("groupId"):null);
		    	task.setTaskCandidate(candidate);
	    	}
	    	
		}
		return task;
	}
	
	public boolean completeTask(String taskId,HashMap<String,Object> variables) throws Exception {
		String uri = REST_URI + "/task/" + taskId + "/complete";
		JSONObject jsRequest = new JSONObject();
	    addVariablesToJsonRequest(variables, jsRequest);
		JSONObject object = new JSONObject(HttpClientUtil.post(uri, cookies,jsRequest.toString()));
		logger.finest("Completed task " + taskId + " "+ object.getBoolean("success"));
		return object.getBoolean("success");
	}
	
	public boolean claimTask(String taskId,HashMap<String,Object> variables) throws Exception {
		String uri = REST_URI + "/task/" + taskId + "/claim";
		JSONObject object = new JSONObject(HttpClientUtil.post(uri, cookies,"{}"));
		logger.finest("Completed task " + taskId + " "+ object.getBoolean("success"));
		return object.getBoolean("success");
	}

}