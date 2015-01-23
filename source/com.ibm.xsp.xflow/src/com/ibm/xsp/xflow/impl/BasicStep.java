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

package com.ibm.xsp.xflow.impl;

import com.ibm.xsp.xflow.IAction;
import com.ibm.xsp.xflow.IStep;
import com.ibm.xsp.xflow.WorkflowException;


/**
 * Basic step. 
 */
public class BasicStep implements IStep {

	private String name;
	private String label;
	private String recipients;
	private IAction[] actions;
	
	public BasicStep(){
		
	}
	
	public BasicStep(String name, String label, String recipients, IAction... actions ) {
		this.name = name;
		this.label = label;
		this.recipients = recipients;
		this.actions = actions; 
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setRecipients(String recipients) {
		this.recipients = recipients;
	}

	public void setActions(IAction[] actions) {
		this.actions = actions;
	}

	public String getName() throws WorkflowException {
		return name;
	}

	public String getLabel() throws WorkflowException {
		return label;
	}

	public String getRecipients() throws WorkflowException {
		return recipients;
	}

	public IAction[] getActions() throws WorkflowException {
		return actions;
	}

	public boolean isWorkflowComplete() throws WorkflowException {
		return actions!=null && actions.length>0;
	}
}
