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
package com.ibm.xsp.xflow.process;

public class CommonProcess extends AbstractProcess{
	private String processId = null;
	private String actionHandler = null;
    private String labelProcessAction;
    
    public CommonProcess(String processId, String actionHandler){
    	this.processId = processId;
    	this.actionHandler = actionHandler;
    }
    
	public String getLabelProcessAction() {
		return labelProcessAction;
	}

	public void setLabelProcessAction(String labelProcessAction) {
		this.labelProcessAction = labelProcessAction;
	}

	
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	
	public void setActionHanlder(String actionHandler) {
		this.actionHandler = actionHandler;
	}

	/*
	 * Return process definition id
	 */
	public String getProcessId(){
		return processId;
	}
	
	/*
	 * Return a class name. This class should implement com.ibm.xsp.xflow.actionhandler.IActionHandler.
	 * The methods of this class will be called before&after an action execution.
	 */
	public String getActionHandler(){
		return actionHandler;
	}
}
