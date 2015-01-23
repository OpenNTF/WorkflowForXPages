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
 * Define a process. 
 */
public interface IProcess {

	/*
	 * Return the corresponding step
	 */
	public IStep getStep(String step) throws WorkflowException;
	
	/*
	 * Return process definition id
	 */
	public String getProcessId();
	
	/*
	 * Return a class name. This class should implement com.ibm.xsp.xflow.actionhandler.IActionHandler.
	 * The methods of this class will be called before&after an action execution.
	 */
	public String getActionHandler();
}
