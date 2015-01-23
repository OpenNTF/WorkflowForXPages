/*
 * © Copyright IBM Corp. 2010
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

package com.ibm.xsp.xflow;

import java.util.HashMap;





/**
 * Define a workflow context at runtime. 
 */
public interface IWorkflowContext {

	/**
	 * Get a property from the workflow context.
	 */
	public String getRequester() throws WorkflowException;
	public String getLocale() throws WorkflowException;
	public boolean isInitiated() throws WorkflowException;
	public boolean isRunning() throws WorkflowException;
	public boolean isCompleted() throws WorkflowException;
	public String getCurrentStep() throws WorkflowException;
	public String getCurrentStepLabel() throws WorkflowException;
	
	/*
	 * Get form name of current step, this name can be mapped to real form
	 */
	public String getCurrentForm() throws WorkflowException;
	
	public boolean isReadonly() throws WorkflowException;
	
	/*
	 * Workflow data can be used to integrate with other workflow engine.
	 * Call setWorkflowData to pass workflow data to external workflow engine
	 * @return name,value paires
	 */
	public void setWorkflowData(HashMap<String,Object> data);
	public HashMap<String,Object> getWorkflowData();
	
	/**
	 * Get the list of possible actions from the current step
	 * @return
	 * @throws WorkflowException
	 */
	public String[] getActions() throws WorkflowException;
	public String[] getActionLabels() throws WorkflowException;
	
	/**
	 * Execute an action from the current step.
	 */
	public void executeAction(String name) throws WorkflowException;
	
	/**
	 * Get the people resolver.
	 */
	public IIdentityResolver getIdentityResolver() throws WorkflowException;
	
	/**
	 * Get the role resolver.
	 */
	public IRoleResolver getRoleResolver() throws WorkflowException;
	
	/**
	 * Return the actual context when wrapped.
	 */
	public IWorkflowContext getWrapped();
	
	/**
	 * Get the workflow history as a string.
	 */
	public String getHistoryAsString() throws WorkflowException;
	
	/**
	 * Add an entry to the history.
	 */
	public void addHistory(IHistoryEntry entry) throws WorkflowException;
}
