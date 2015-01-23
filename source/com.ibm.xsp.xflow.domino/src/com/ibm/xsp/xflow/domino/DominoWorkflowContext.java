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

import lotus.domino.Item;
import lotus.domino.NotesException;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.model.domino.wrapped.DominoDocument;
import com.ibm.xsp.xflow.IHistoryEntry;
import com.ibm.xsp.xflow.IRouterWorkflowContext;
import com.ibm.xsp.xflow.WorkflowException;
import com.ibm.xsp.xflow.impl.AbstractWorkflowContext;


/**
 * Define a workflow context at runtime. 
 */
public abstract class DominoWorkflowContext extends AbstractWorkflowContext {
	
	private Object document;

	public DominoWorkflowContext(Object document) {
		this.document = document;
	}

	public Object getDocument() {
		return document;
	}
	protected DominoDocument getDominoDocument() {
		return (DominoDocument)document;
	}

	
	
	// ===============================================================
	// Access to the fields
	
	public String getRequester() throws WorkflowException {
		return getItemValueString(DominoWorkflowConstants.FIELD_REQUESTER);
	}

	@Override
	public String getLocale() throws WorkflowException {
		return getItemValueString(DominoWorkflowConstants.FIELD_LOCALE);
	}
	
	public boolean isInitiated() throws WorkflowException {
		int v = getItemValueInteger(DominoWorkflowConstants.FIELD_STATE);
		return v!=DominoWorkflowConstants.STATE_NOTINITIATED;
	}
	public boolean isRunning() throws WorkflowException {
		int v = getItemValueInteger(DominoWorkflowConstants.FIELD_STATE);
		return v==DominoWorkflowConstants.STATE_RUNNING;
	}
	public boolean isCompleted() throws WorkflowException {
		int v = getItemValueInteger(DominoWorkflowConstants.FIELD_STATE);
		return v==DominoWorkflowConstants.STATE_COMPLETED;
	}
	
	public String getCurrentStep() throws WorkflowException {
		return getItemValueString(DominoWorkflowConstants.FIELD_STEP);
	}
	
	public String getCurrentStepLabel() throws WorkflowException {
		return getItemValueString(DominoWorkflowConstants.FIELD_STEPLABEL);
	}
	
	public String getCurrentForm() throws WorkflowException {
		return getItemValueString(DominoWorkflowConstants.FIELD_FORM);
	}
	
	public void addHistory(IHistoryEntry entry) throws WorkflowException {
		addHistory(entry.getAsString());
	}
	
	@Override
	public String getHistoryAsString() throws WorkflowException {
		return getItemValueString(DominoWorkflowConstants.FIELD_HISTORY);
	}
	
	public void addHistory(String entry) throws WorkflowException {
		String s = getItemValueString(DominoWorkflowConstants.FIELD_HISTORY);
		String h = StringUtil.isEmpty(s) ? s : s + '\n' + entry;
		setItemValue(DominoWorkflowConstants.FIELD_HISTORY,h);
	}

	
	// ===============================================================
	// Access the document data
	protected Vector<Object> getItemValue(String name) throws WorkflowException {
		try {
			return getDominoDocument().getItemValue(name);
		} catch(NotesException ex) {
			throw new WorkflowException(ex,"Error while reading item {0}",name);
		}
	}
	protected String getItemValueString(String name) throws WorkflowException {
		try {
			return getDominoDocument().getItemValueString(name);
		} catch(NotesException ex) {
			throw new WorkflowException(ex,"Error while reading item {0}",name);
		}
	}
	protected int getItemValueInteger(String name) throws WorkflowException {
		try {
			return getDominoDocument().getItemValueInteger(name);
		} catch(NotesException ex) {
			throw new WorkflowException(ex,"Error while reading item {0}",name);
		}
	}
	protected void removeItem(String name) throws WorkflowException {
		try {
			getDominoDocument().removeItem(name);
		} catch(NotesException ex) {
			throw new WorkflowException(ex,"Error while setting item {0}",name);
		}
	}
	
	protected void setItemValue(String name, Object value) throws WorkflowException {
		try {
			getDominoDocument().replaceItemValue(name,value);
		} catch(NotesException ex) {
			throw new WorkflowException(ex,"Error while setting item {0}",name);
		}
	}
	protected void setReaders(String name, Object value) throws WorkflowException {
		try {
			getDominoDocument().replaceItemValue(name, value);
			Item item = getDominoDocument().getDocument(true).getFirstItem(name);//replaceItemValue(name,value);
			item.setReaders(true);
		} catch(NotesException ex) {
			throw new WorkflowException(ex,"Error while setting item {0}",name);
		}
	}
	protected void setAuthors(String name, Object value) throws WorkflowException {
		try {
			getDominoDocument().replaceItemValue(name, value);
			Item item = getDominoDocument().getDocument(true).getFirstItem(name);//.replaceItemValue(name,value);
			item.setAuthors(true);
		} catch(NotesException ex) {
			throw new WorkflowException(ex,"Error while setting item {0}",name);
		}
	}
	
	
	// ===============================================================
	// Some utilities...

	protected void addAll(Vector<Object> v, String[] s) {
		if(s!=null) {
			for(int i=0; i<s.length; i++) {
				v.add(s[i]);
			}
		}
	}

	protected void addUnique(Vector<Object> v, String s) {
		if(s!=null) {
			if(v.contains(s)) {
				return;
			}
			v.add(s);
		}
	}

	protected void addAllUnique(Vector<Object> v, String[] s) {
		if(s!=null) {
			for(int i=0; i<s.length; i++) {
				addUnique(v, s[i]);
			}
		}
	}
	
	
	
	// ===============================================================
	// Debug functions

	private static final String	DEBUG_READONLY	= "ReadOnly";
	
	public String[] getDebugDataSourceFields() throws WorkflowException {
		return new String[] {
				DEBUG_READONLY,
		};
	}

	public Object getDebugDataSourceField(String name) throws WorkflowException {
		if(StringUtil.equals(name, DEBUG_READONLY)) {
			return Boolean.toString(isReadonly());
		}
		return "";
	}
	
	public String[] getDebugImplementationFields() throws WorkflowException {
		if(this instanceof IRouterWorkflowContext) {
			return new String[] {
					DominoWorkflowConstants.FIELD_REQUESTER, 
					DominoWorkflowConstants.FIELD_LOCALE, 
					DominoWorkflowConstants.FIELD_STATE,
					DominoWorkflowConstants.FIELD_STEP,
					DominoWorkflowConstants.FIELD_STEPLABEL,
					DominoWorkflowConstants.FIELD_PARTICIPANTS,
					DominoWorkflowConstants.FIELD_ACTIONS,
					DominoWorkflowConstants.FIELD_ACTIONLABELS,
					DominoWorkflowConstants.FIELD_READERS,
					DominoWorkflowConstants.FIELD_AUTHORS,
					DominoWorkflowConstants.FIELD_ADMINREADERS,
					DominoWorkflowConstants.FIELD_ADMINAUTHORS,
					DominoWorkflowConstants.FIELD_HISTORY,
			};
		} else {
			return new String[] {
					DominoWorkflowConstants.FIELD_REQUESTER, 
					DominoWorkflowConstants.FIELD_LOCALE, 
					DominoWorkflowConstants.FIELD_STATE,
					DominoWorkflowConstants.FIELD_STEP,
					DominoWorkflowConstants.FIELD_STEPLABEL,
					DominoWorkflowConstants.FIELD_PARTICIPANTS,
					DominoWorkflowConstants.FIELD_READERS,
					DominoWorkflowConstants.FIELD_AUTHORS,
					DominoWorkflowConstants.FIELD_ADMINREADERS,
					DominoWorkflowConstants.FIELD_ADMINAUTHORS,
					DominoWorkflowConstants.FIELD_HISTORY,
			};
		}
	}

	public Object getDebugImplementationField(String name) throws WorkflowException {
		Vector<Object> v = getItemValue(name);
		if(v==null) {
			return "";
		}
		if(v.size()==1) {
			return v.get(0);
		}
		return v.toArray();
	}
}
