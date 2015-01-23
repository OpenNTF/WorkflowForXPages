/*
 * © Copyright IBM Corp. 2012
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.application.ApplicationEx;
import com.ibm.xsp.xflow.IAction;
import com.ibm.xsp.xflow.IDebugWorkflowContext;
import com.ibm.xsp.xflow.IIdentityResolver;
import com.ibm.xsp.xflow.IProcess;
import com.ibm.xsp.xflow.IRoleResolver;
import com.ibm.xsp.xflow.IStep;
import com.ibm.xsp.xflow.IWorkflowContext;
import com.ibm.xsp.xflow.WorkflowException;
import com.ibm.xsp.xflow.actionhandler.IActionHandler;
import com.ibm.xsp.xflow.identity.IdenticalIdentityResolver;
import com.ibm.xsp.xflow.process.EmptyProcess;
import com.ibm.xsp.xflow.role.EmptyRoleResolver;



/**
 * Abstract a workflow context implementation. 
 */
public abstract class AbstractWorkflowContext implements IWorkflowContext, IDebugWorkflowContext {

	private IProcess process;
	private IIdentityResolver identityResolver;
	private IRoleResolver roleResolver;
	private HashMap<String,Object> workflowData;
	
	public AbstractWorkflowContext() {
	}
	
	public IWorkflowContext getWrapped() {
		return this;
	}
	
	public String getCurrentUser() {
		// Should be retrieved by a runtime object
		return "<unknown>";
	}
	public Locale getUserLocale() {
		// Should be retrieved by a runtime object
		return Locale.getDefault();
	}


	public String getLocale() throws WorkflowException {
		return getUserLocale().toString();
	}

	public boolean isReadonly() throws WorkflowException {
		return false;
	}

	public String getHistoryAsString() throws WorkflowException {
		return "";
	}
	
	public String[] getActions() throws WorkflowException {
		IAction[] actions = getActionsArray();
		if(actions!=null && actions.length>0) {
			String[] s = new String[actions.length];
			for(int i=0; i<s.length; i++) {
				s[i] = actions[i].getName();
			}
			return s;
		}
		return StringUtil.EMPTY_STRING_ARRAY;
	}

	public String[] getActionLabels() throws WorkflowException {
		IAction[] actions = getActionsArray();
		if(actions!=null && actions.length>0) {
			String[] s = new String[actions.length];
			for(int i=0; i<s.length; i++) {
				s[i] = actions[i].getLabel();
			}
			return s;
		}
		return StringUtil.EMPTY_STRING_ARRAY;
	}
	
	private IAction[] getActionsArray() throws WorkflowException {
		IProcess p = getProcess();
		IStep step = p.getStep(getCurrentStep());
		return step.getActions();
	}
	
	abstract public void executeAction(String name) throws WorkflowException;
	
	protected IProcess getProcess() throws WorkflowException {
		if(process==null) {
			process = findProcess(); 
			if(process==null) {
				process = EmptyProcess.instance;
			}
		}
		return process;
	}
	public IIdentityResolver getIdentityResolver() throws WorkflowException {
		if(identityResolver==null) {
			identityResolver = findIdentityResolver(); 
			if(identityResolver==null) {
				identityResolver = IdenticalIdentityResolver.instance;
			}
		}
		return identityResolver;
	}
	public IRoleResolver getRoleResolver() throws WorkflowException {
		if(roleResolver==null) {
			roleResolver = findRoleResolver(); 
			if(roleResolver==null) {
				roleResolver = EmptyRoleResolver.instance;
			}
		}
		return roleResolver;
	}
	
	protected abstract IProcess findProcess() throws WorkflowException;
	protected abstract IIdentityResolver findIdentityResolver() throws WorkflowException;
	protected abstract IRoleResolver findRoleResolver() throws WorkflowException;
	
	
	
	// ===============================================================
	// Some utilities...

	protected String workflowToNative(String name) throws WorkflowException {
		if(name!=null) {
			return getIdentityResolver().workflowToNative(name);
		}
		return null;
	}
	protected String[] workflowToNative(String[] names) throws WorkflowException {
		if(names!=null) {
			IIdentityResolver r = getIdentityResolver();
			String[] s = new String[names.length];
			for(int i=0; i<s.length; i++) {
				s[i] = r.workflowToNative(names[i]);
			}
			return s;
		}
		return null;
	}

	protected String nativeToWorkflow(String name) throws WorkflowException {
		if(name!=null) {
			return getIdentityResolver().nativeToWorkflow(name);
		}
		return null;
	}	
	protected String[] nativeToWorkflow(String[] names) throws WorkflowException {
		if(names!=null) {
			IIdentityResolver r = getIdentityResolver();
			String[] s = new String[names.length];
			for(int i=0; i<s.length; i++) {
				s[i] = r.nativeToWorkflow(names[i]);
			}
			return s;
		}
		return null;
	}
	
	protected String[] resolveRecipients(String sRecipients) throws WorkflowException {
		if(StringUtil.isNotEmpty(sRecipients)) {
			String[] recipients = StringUtil.splitString(sRecipients, ',', true);
			ArrayList<String> list = new ArrayList<String>();
			IRoleResolver r = getRoleResolver();
			for(int i=0; i<recipients.length; i++) {
				String s = recipients[i];
				// Special case for the requester
				if(StringUtil.equals(s, IRoleResolver.ROLE_REQUESTER)) {
					String user = getRequester();
					list.add(user);
					continue;
				}
				// resolve the role, if it exists
				if(r.isRole(s)) {
					String[] rs = r.resolveRole(getRequester(),s);
					if(rs!=null) {
						for( int j=0; j<rs.length; j++) {
							if(!list.contains(rs[j])) {
								list.add(rs[j]);
							}
						}
					}
					continue;
				}
				// Assume it is a user if it is not a role
				list.add(s);
			}
			if(!list.isEmpty()) {
				return list.toArray(new String[list.size()]);
			}
		}
		return StringUtil.EMPTY_STRING_ARRAY;
	}	
	
	protected void handleActionBefore(IActionHandler actionHandler, IAction action, String sNextStep) throws WorkflowException{
		IProcess p = getProcess();
		IStep nextStep = p.getStep(sNextStep);
		String recipients = nextStep.getRecipients();
		String[] participants = resolveRecipients(recipients);
		actionHandler.executeBeforeAction(this, p, nextStep, action.getName(), participants);
	}
	
	protected void handleActionAfter(IActionHandler actionHandler, IAction action, String sNextStep) throws WorkflowException{
		IProcess p = getProcess();
		IStep nextStep = p.getStep(sNextStep);
		String recipients = nextStep.getRecipients();
		String[] participants = resolveRecipients(recipients);
		actionHandler.executeAfterAction(this, p, nextStep, action.getName(), participants);
			
	}
	
	protected IActionHandler findActionHandler(String actHandlerName){
		try{
			if(actHandlerName!=null && actHandlerName!=""){
				List<IActionHandler> allHandlers = ApplicationEx.getInstance().findServices(IActionHandler.ACTION_HANDLER_SERVICE);
				for(IActionHandler handler: allHandlers){
					if(handler.getClass().getName().equalsIgnoreCase(actHandlerName)){
						return handler;
					}
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public void setWorkflowData(HashMap<String,Object> data) {
		workflowData = data;
		
	}

	public HashMap<String,Object> getWorkflowData() {
		return workflowData;
	}
	
	public String getCurrentForm() throws WorkflowException {
		// TODO Auto-generated method stub
		return null;
	}
}
