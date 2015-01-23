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
 * Define an action. 
 */
public interface IAction {
	public static String ACTION_TYPE_ALLRECIPIENTS = "AllRecipients";  // Need all recipients to process before move to next step
	public static String ACTION_TYPE_SINGLERECIPIENT = "SingleRecipient";// Need only one recipient to process before move to next step
	
//	public static String DEFAULT_SAVE_ACTION_NAME = "XFLOW_SAVE_ACTION";
	public static String DEFAULT_PROCESS_ACTION_NAME = "XFLOW_PROCESS_ACTION";
//	public static String DEFAULT_CANCEL_ACTION_NAME = "XFLOW_CANCEL_ACTION";
	
	public String getName() throws WorkflowException;
	
	public String getLabel() throws WorkflowException;
	
	public String getNextStep() throws WorkflowException;
	
	public String getActionType() throws WorkflowException;

	public String getActHandler() throws WorkflowException;
}
