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



/**
 * Define a state. 
 */
public interface IStep {
	public static final String	XFLOW_STEP_DONE 	= "XFLOW_STEP_DONE";
	
	public String getName() throws WorkflowException;
	
	public String getLabel() throws WorkflowException;
	
	public String getRecipients() throws WorkflowException;

	public IAction[] getActions() throws WorkflowException;
	
	public boolean isWorkflowComplete() throws WorkflowException;
}
