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

package com.ibm.xsp.xflow.domino;

import java.util.Vector;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.xflow.IAction;
import com.ibm.xsp.xflow.IDebugWorkflowContext;
import com.ibm.xsp.xflow.IProcess;
import com.ibm.xsp.xflow.ISimpleWorkflowContext;
import com.ibm.xsp.xflow.IStep;
import com.ibm.xsp.xflow.WorkflowException;
import com.ibm.xsp.xflow.actionhandler.IActionHandler;
import com.ibm.xsp.xflow.history.ActionHistory;
import com.ibm.xsp.xflow.history.MoveToStepHistory;
import com.ibm.xsp.xflow.history.WorkflowEndedHistory;
import com.ibm.xsp.xflow.history.WorkflowStartedHistory;



/**
 * Workflow context used by the synchronous state machine workflow. 
 */
public abstract class SimpleDominoWorkflowContext extends DominoWorkflowContext implements ISimpleWorkflowContext, IDebugWorkflowContext {
	
	public SimpleDominoWorkflowContext(Object document) throws WorkflowException {
		super(document);
		
		// Initialize the workflow, if not already initiated
		if(!isInitiated()) {
			initWorkflow();
		}
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

	
	// ===============================================================
	// Workflow initialization
	
	protected void initWorkflow() throws WorkflowException {
		IStep nextStep = getProcess().getStep("");
		if(nextStep==null) {
			throw new WorkflowException(null,"The workflow process is missing a starting step with an empty name");
		}
		
		// Mark the workflow as started
		setItemValue(DominoWorkflowConstants.FIELD_STATE, DominoWorkflowConstants.STATE_RUNNING);

		// Set the current workflow step
		String name = nextStep.getName();
		setItemValue(DominoWorkflowConstants.FIELD_STEP, name);
		String label = nextStep.getLabel();
		setItemValue(DominoWorkflowConstants.FIELD_STEPLABEL, StringUtil.isNotEmpty(label)?label:name);
		
		// Set the requester field
		String requester = getCurrentUser();
		setItemValue(DominoWorkflowConstants.FIELD_REQUESTER, requester);
		
		// Set the requester Locale
		setItemValue(DominoWorkflowConstants.FIELD_LOCALE, getUserLocale().toString());
		
		// Update the admin readers/authors
		updateAdminReaders(nextStep);
		updateAdminAuthors(nextStep);

		updateReaders(nextStep,new String[]{requester});
		updateAuthors(nextStep,new String[]{requester});
		
		addHistory(new WorkflowStartedHistory(this));
		
	}
	
	public void executeAction(String name) throws WorkflowException {
		IProcess p = getProcess();
		if(!executeAction(p,getCurrentStep(),name)) {
			throw new WorkflowException(null,"Cannot execute action {0}",name);
		}
	}
	
	protected boolean executeAction(IProcess p, String currentStep, String name) throws WorkflowException {
		IStep step = p.getStep(currentStep);
		IAction[] actions = step.getActions();
		if(actions!=null && actions.length>0) {
			for( IAction action: actions ) {
				if(StringUtil.equals(action.getName(), name)) {
					if(executeAction(p, step, action)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	protected boolean executeAction(IProcess p, IStep step, IAction action) throws WorkflowException {
		String nextStep = action.getNextStep();
		addHistory(new ActionHistory(this,getCurrentUser(),action.getName()));
		String actHandlerName = action.getActHandler();
		IActionHandler actionHandler = null;
		if(actHandlerName!=null && !"".equals(actHandlerName)){
			actionHandler = findActionHandler(actHandlerName);
		}
		if(actionHandler!=null){
			handleActionBefore(actionHandler,action, nextStep);
		}
		if(IAction.ACTION_TYPE_SINGLERECIPIENT.equalsIgnoreCase(action.getActionType()) || canMoveToNextStep(p,step,action)){
			moveToStep(nextStep,step.getName(),action.getName());
		}
		if(actionHandler!=null){
			handleActionAfter(actionHandler,action, nextStep);
		}
		return true;
	}
	
	// Need to change, lq
	public boolean canMoveToNextStep(IProcess p, IStep step, IAction action) throws WorkflowException{
		System.out.println("Begin canMoveToNextStep");
		String[] recipients = resolveRecipients(step.getRecipients());
		System.out.println("Begin canMoveToNextStep recipients:"+recipients);
		// If there is only one recipient, can move to next step directly
		if(recipients==null || recipients.length<=1){
			System.out.println("In canMoveToNextStep recipients <1, return true");
			return true;
		}
		// If processed recipients number is one less than all, can move to next step
		Vector<Object> v = getItemValue(DominoWorkflowConstants.FIELD_PROCESSORS);
		System.out.println("In canMoveToNextStep, current processor:"+v);
		if(v!=null && v.size() == recipients.length - 1){
			System.out.println("In canMoveToNextStep, processor number is enough, return true");
			return true;
		}
		
		//  Add current user to process field
		addUnique(v, workflowToNative(getCurrentUser()));
		System.out.println("In canMoveToNextStep, set processor:"+v);
		setItemValue(DominoWorkflowConstants.FIELD_PROCESSORS, v);
		
		// Remove requester from author field
		removeAuthor(step,getCurrentUser());
		return false;
	}

	
	// ===============================================================
	// Access the document data

	public void moveToStep(String step, String currentStep, String action) throws WorkflowException {
		// Find the corresponding step in the process
		IStep nextStep = getProcess().getStep(step);
		if(nextStep==null) {
			throw new WorkflowException(null,"Unknown step {0} for action {1} from step {2}",nextStep,action,currentStep);
		}
		
		// Move to the desired step
		setWorkflowStep(nextStep);
		
		// Mark the workflow as completed, if the step is a terminal one
		if(nextStep.isWorkflowComplete()) {
			termWorkflow(nextStep);
		}
	}
	
	protected void updateAdminReaders(IStep nextStep) throws WorkflowException {
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

	protected void updateAdminAuthors(IStep nextStep) throws WorkflowException {
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
	
	protected void termWorkflow(IStep nextStep) throws WorkflowException {
		// Mark the workflow as completed
		setItemValue(DominoWorkflowConstants.FIELD_STATE, DominoWorkflowConstants.STATE_COMPLETED);
		addHistory(new WorkflowEndedHistory(this));
	}
	
	protected void setWorkflowStep(IStep nextStep) throws WorkflowException {
		// Store the new step name
		String name = nextStep.getName();
		setItemValue(DominoWorkflowConstants.FIELD_STEP, name);
		String label = nextStep.getLabel();
		if(StringUtil.isEmpty(label)) {
			label = name;
		}
		setItemValue(DominoWorkflowConstants.FIELD_STEPLABEL, label);
		
		// Get the recipients and convert it to a list of people
		String recipients = nextStep.getRecipients();
		String[] participants = resolveRecipients(recipients);

		addHistory(new MoveToStepHistory(this,nextStep.getName()));
		
		removeItem(DominoWorkflowConstants.FIELD_PROCESSORS);
		
		// Update the action list
		updateActions(nextStep,participants);
		
		// Update the readers
		updateReaders(nextStep,participants);
		
		// Update the authors
		updateAuthors(nextStep,participants);
	}

	protected void updateActions(IStep nextStep, String[] participants) throws WorkflowException {
		// We create here 2 fields that hold the participants and the associated actions
		Vector<Object> p = new Vector<Object>();
		Vector<Object> a = new Vector<Object>();
		if(participants!=null) {
			for(int i=0; i<participants.length; i++) {
				p.add(participants[i]);
				a.add("");
			}
		}
		setItemValue(DominoWorkflowConstants.FIELD_PARTICIPANTS, p);
		setItemValue(DominoWorkflowConstants.FIELD_PARTICIPANTACTIONS, a);
	}
	
	protected void updateReaders(IStep nextStep, String[] participants) throws WorkflowException {
		Vector<Object> v = getItemValue(DominoWorkflowConstants.FIELD_READERS);
		for(int i=0; i<participants.length; i++) {
			addUnique(v, workflowToNative(participants[i]));
		}
		setReaders(DominoWorkflowConstants.FIELD_READERS, v);
	}

	protected void updateAuthors(IStep nextStep, String[] participants) throws WorkflowException {
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
	
}
