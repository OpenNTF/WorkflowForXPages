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
import com.ibm.xsp.xflow.WorkflowException;


/**
 * Basic action. 
 */
public class BasicAction implements IAction {

	private String name;
	private String label;
	private String nextStep;
	private String actionType = ACTION_TYPE_SINGLERECIPIENT;
	private String actHandler;
	
	public BasicAction(String name, String label, String nextStep) {
		this.name = name;
		this.label = label;
		this.nextStep = nextStep;
	}
	
	public BasicAction(String name, String label, String nextStep,String actionType) {
		this.name = name;
		this.label = label;
		this.nextStep = nextStep;
		this.actionType = actionType;
	}
	
	public String getName() throws WorkflowException {
		return name;
	}

	public String getLabel() throws WorkflowException {
		return label;
	}

	public String getNextStep() throws WorkflowException {
		return nextStep;
	}
	
	public String getActionType() throws WorkflowException {
		return actionType;
	}
	public String getActHandler() throws WorkflowException {
		return actHandler;
	}
}
