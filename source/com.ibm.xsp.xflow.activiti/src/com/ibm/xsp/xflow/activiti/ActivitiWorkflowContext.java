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

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.xflow.IStep;
import com.ibm.xsp.xflow.WorkflowException;
import com.ibm.xsp.xflow.actionhandler.IActionHandler;
import com.ibm.xsp.xflow.activiti.data.ProcessInstance;
import com.ibm.xsp.xflow.activiti.data.Task;
import com.ibm.xsp.xflow.activiti.data.TaskCandidate;
import com.ibm.xsp.xflow.domino.CommonWorkflowContext;
import com.ibm.xsp.xflow.domino.DominoWorkflowConstants;
import com.ibm.xsp.xflow.history.MoveToStepHistory;
import com.ibm.xsp.xflow.history.WorkflowStartedHistory;
import com.ibm.xsp.xflow.impl.BasicStep;
import com.ibm.xsp.xflow.process.CommonProcess;

abstract public class ActivitiWorkflowContext extends CommonWorkflowContext {

	public ActivitiWorkflowContext(Object document) {
		super(document);
	}
	
	private ProcessInstance initWorkflow(ActivitiRestAPI restAPI, HashMap<String,Object> variables) throws WorkflowException{
		ProcessInstance instance = restAPI.createProcess(findProcess().getProcessId(),variables);
		String requester = getCurrentUser();
		if(instance==null){
			throw new WorkflowException(null,"Failed to create process instance:"+getProcessInstanceId(),requester);
		}
		setItemValue(DominoWorkflowConstants.FIELD_PROCESS_ID, findProcess().getProcessId());
		
		setItemValue(DominoWorkflowConstants.FIELD_PROCESS_INSTANCE_ID, instance.getId());
		
		setItemValue(DominoWorkflowConstants.FIELD_STATE, DominoWorkflowConstants.STATE_RUNNING);
		// Set the requester field
		setItemValue(DominoWorkflowConstants.FIELD_REQUESTER, requester);
		
		// Set the requester Locale
		setItemValue(DominoWorkflowConstants.FIELD_LOCALE, getUserLocale().toString());
		

		
		updateAdminReaders();
		
		updateAdminAuthors();
		
		// Update the readers
		updateReaders(new String[]{requester});
		
		// Update the authors
		updateAuthors(new String[]{requester});
		
		addHistory(new WorkflowStartedHistory(this));
		
		return instance;
	}
	
	
	public void executeAction(String actionName) throws WorkflowException {
		ActivitiRestAPI restAPI = new ActivitiRestAPI(getRestUrl());
		Task task = null;
		ProcessInstance processInst = null;
		IActionHandler actionHandler = null;
		CommonProcess process = (CommonProcess)getProcess();
		BasicStep step = new BasicStep();
		String actHandlerName = process.getActionHandler();
		if(actHandlerName!=null && !"".equals(actHandlerName)){
			actionHandler = findActionHandler(actHandlerName);
		}
		HashMap<String,Object> variables = getVariables();
		step.setName("");
		if(!isInitiated()){
			if(actionHandler!=null){
				actionHandler.executeBeforeAction(this, getProcess(), step, actionName, null);
			}
			processInst = initWorkflow(restAPI,variables);
		}else{
			// Process action handler
			if(actionHandler!=null){
				step.setName(getItemValueString(DominoWorkflowConstants.FIELD_STEP));
				actionHandler.executeBeforeAction(this, getProcess(), step, actionName, null);
			}
			
			processInst = restAPI.completeTask(getProcessInstanceId(), getTaskId(), variables);
		}
		if(processInst!=null && processInst.isComplete()){
			if(actionHandler!=null){
				step.setName(IStep.XFLOW_STEP_DONE);
				actionHandler.executeAfterAction(this, getProcess(), step, actionName, new String[]{getRequester()});
			}
			termWorkflow();
		}else{
			task = processInst.getTasks().get(0);
			String taksDefId = task.getTaskDefinitionKey();
			setItemValue(DominoWorkflowConstants.FIELD_STEP, taksDefId);
			String form = task.getForm();
			setItemValue(DominoWorkflowConstants.FIELD_FORM, form);
			String label = task.getName();
			if(StringUtil.isEmpty(label)) {
				label = taksDefId;
			}
			setItemValue(DominoWorkflowConstants.FIELD_STEPLABEL, label);
			
			setItemValue(DominoWorkflowConstants.FIELD_TASK_ID, task.getId());
			
			String[] participants = null;
			
			// Get the recipients and convert it to a list of people
			if(task.getAssignee()!=null){
				participants = new String[]{workflowToNative(task.getAssignee())};
			}else{
				TaskCandidate candidate = task.getTaskCandidate();
				if(candidate.getUserId()!=null){
					String[] recipients = resolveRecipients(candidate.getUserId());
					participants = new String[recipients.length];
					for(int i=0;i<recipients.length;i++){
						participants[i] = workflowToNative(recipients[i]);
					}
				}else if(candidate.getGroupId()!=null){
					participants = new String[]{workflowToNative(candidate.getGroupId())};
				}
			}
			
			addHistory(new MoveToStepHistory(this,taksDefId));
			
			// Update the readers
			if(participants!=null && participants.length>0)
				updateReaders(participants);
			
			// Update the authors
			if(participants!=null && participants.length>0)
				updateAuthors(participants);
			
			if(actionHandler!=null){
				step.setName(taksDefId);
				actionHandler.executeAfterAction(this, getProcess(), step, actionName, participants);
			}
		}
	}
	
	public String getTaskId(){
		String taskId = null;
		try{
			taskId = getItemValueString(DominoWorkflowConstants.FIELD_TASK_ID);
		}catch(Exception e){
			e.printStackTrace();
		}
		return taskId;
	}
	
	abstract protected String getRestUrl();
	
}
