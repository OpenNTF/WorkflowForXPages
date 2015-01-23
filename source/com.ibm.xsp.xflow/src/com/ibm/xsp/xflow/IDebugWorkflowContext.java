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
 * Workflow context debug interface.
 * <p>
 * A workflow context can implement this interface to return some extra fields
 * for debugging purposes. These fields are tied to the physical implementation
 * and can be consumed by a debugging component. 
 * </p> 
 */
public interface IDebugWorkflowContext extends IWorkflowContext {

	public String[] getDebugDataSourceFields() throws WorkflowException; 

	public Object getDebugDataSourceField(String name) throws WorkflowException; 
	
	public String[] getDebugImplementationFields() throws WorkflowException; 

	public Object getDebugImplementationField(String name) throws WorkflowException; 
}
