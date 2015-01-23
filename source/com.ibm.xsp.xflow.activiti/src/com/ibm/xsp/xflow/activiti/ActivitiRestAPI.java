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

import java.util.HashMap;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.ibm.xsp.xflow.activiti.data.ProcessInstance;

public class ActivitiRestAPI {
	
	private ActivitiRestClient client = null;
	
	public ActivitiRestAPI(String restUrl){
		client = new ActivitiRestClient(restUrl,getCookies());
	}

	public ProcessInstance createProcess(String processId, HashMap variables){
		ProcessInstance instance = null;
		try{
			instance = client.createProcess(processId, variables);
		}catch(Exception e){
			e.printStackTrace();
		}
		return instance;
	}
	
	public ProcessInstance completeTask(String processInstanceId, String taskId,HashMap variables){
		try{
			boolean success = client.completeTask(taskId, variables);
			if(success){
				ProcessInstance instance = client.getProcessInstanceWithLastTask(processInstanceId);
				return instance;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static javax.servlet.http.Cookie[] getCookies(){
		FacesContext ctx = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest)ctx.getExternalContext().getRequest();
		return request.getCookies();
	}
}
