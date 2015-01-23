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

package com.ibm.xsp.xflow.domino;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import lotus.domino.local.Item;

import com.ibm.xsp.xflow.IAction;
import com.ibm.xsp.xflow.IDebugWorkflowContext;
import com.ibm.xsp.xflow.IStep;
import com.ibm.xsp.xflow.WorkflowException;
import com.ibm.xsp.xflow.history.WorkflowEndedHistory;
import com.ibm.xsp.xflow.process.CommonProcess;

abstract public class CommonWorkflowContext extends DominoWorkflowContext implements IDebugWorkflowContext{
	private String processInstanceId = null;

	public CommonWorkflowContext(Object document) {
		super(document);
	}
	
	protected HashMap<String,Object> getVariables(){
		HashMap<String,Object> variables = null;
		Vector<Item> items = null;
		try{
			items = (Vector<Item>)getDominoDocument().getDocument(true).getItems();
			if(items!=null){
				variables = new HashMap<String,Object>();
				Iterator<Item> it = items.iterator();
				Item item = null;
				SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yyyy hh:mm");
				while(it.hasNext()){
					item = it.next();
					switch(item.getType()){
					case Item.TEXT:
					case Item.USERID:
					case Item.NAMES:
					case Item.AUTHORS:
						System.out.println("Item name:"+item.getName()+":"+item.getValueString());
						variables.put(item.getName(), item.getValueString());
						break;
					case Item.DATETIMES:
						System.out.println("Item name:"+item.getName()+":"+formater.format(item.getDateTimeValue().toJavaDate()));
						variables.put(item.getName(), formater.format(item.getDateTimeValue().toJavaDate()));
						break;
					case Item.NUMBERS:
						System.out.println("Item name:"+item.getName()+":"+item.getValueDouble());
						variables.put(item.getName(), item.getValueDouble());
						break;
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return variables;
	}
	
	@Override
	public String getRequester() throws WorkflowException {
		// The field is stored in the document if it had been initiated
		// else, we use the current user name
		if(isInitiated()) {
			return super.getRequester();
		}
		return getCurrentUser();
	}

	protected void updateAdminReaders() throws WorkflowException {
		Vector<Object> v = new Vector<Object>();
		String[] participants = getAdminReaders();
		for(int i=0; i<participants.length; i++) {
			v.add(workflowToNative(participants[i]));
		}
		setReaders(DominoWorkflowConstants.FIELD_READERS, v);
	}
	protected String[] getAdminReaders() throws WorkflowException {
		return DominoWorkflowConstants.DEFAULT_ADMINREADERS;
	}

	protected void updateAdminAuthors() throws WorkflowException {
		Vector<Object> v = new Vector<Object>();
		String[] participants = getAdminAuthors();
		for(int i=0; i<participants.length; i++) {
			v.add(workflowToNative(participants[i]));
		}
		setAuthors(DominoWorkflowConstants.FIELD_AUTHORS, v);
	}
	protected String[] getAdminAuthors() throws WorkflowException {
		return DominoWorkflowConstants.DEFAULT_ADMINAUTHORS;
	}
	
	protected void termWorkflow() throws WorkflowException {
		// Mark the workflow as completed
		setItemValue(DominoWorkflowConstants.FIELD_STATE, DominoWorkflowConstants.STATE_COMPLETED);
		setItemValue(DominoWorkflowConstants.FIELD_STEP, IStep.XFLOW_STEP_DONE);
		addHistory(new WorkflowEndedHistory(this));
	}
	
	public String[] getActions() throws WorkflowException {
		return new String[]{IAction.DEFAULT_PROCESS_ACTION_NAME};
	}
	
	public String[] getActionLabels() throws WorkflowException {
		CommonProcess process = (CommonProcess)findProcess();
		return new String[]{process.getLabelProcessAction()};
	}
	
	protected void updateReaders(String[] participants) throws WorkflowException {
		if(participants==null){
			return;
		}
		Vector<Object> v = getItemValue(DominoWorkflowConstants.FIELD_READERS);
		for(int i=0; i<participants.length; i++) {
			addUnique(v, workflowToNative(participants[i]));
		}
		setReaders(DominoWorkflowConstants.FIELD_READERS, v);
	}

	protected void updateAuthors(String[] participants) throws WorkflowException {
		if(participants==null){
			return;
		}
		Vector<Object> v = new Vector<Object>();
		String[] admins = getAdminAuthors();
		for(int i=0; i<admins.length; i++) {
			v.add(workflowToNative(admins[i]));
		}
		for(int i=0; i<participants.length; i++) {
			addUnique(v,workflowToNative(participants[i]));
		}
		setAuthors(DominoWorkflowConstants.FIELD_AUTHORS, v);
	}
	
	protected void removeAuthor(IStep nextStep, String participant) throws WorkflowException {
		Vector<Object> v = getItemValue(DominoWorkflowConstants.FIELD_AUTHORS);
		v.remove(workflowToNative(participant));
		setAuthors(DominoWorkflowConstants.FIELD_AUTHORS, v);
	}
	
	public String getProcessInstanceId(){
		if(processInstanceId!=null){
			return processInstanceId;
		}
		try{
			processInstanceId = getItemValueString(DominoWorkflowConstants.FIELD_PROCESS_INSTANCE_ID);
		}catch(Exception e){
			e.printStackTrace();
		}
		return processInstanceId;
	}

}
