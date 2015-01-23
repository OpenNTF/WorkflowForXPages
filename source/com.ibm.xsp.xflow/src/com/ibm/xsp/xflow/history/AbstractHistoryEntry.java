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

package com.ibm.xsp.xflow.history;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.xflow.IHistoryEntry;
import com.ibm.xsp.xflow.IWorkflowContext;
import com.ibm.xsp.xflow.WorkflowException;


/**
 * Define an abstract history entry. 
 */
public abstract class AbstractHistoryEntry implements IHistoryEntry  {

	// Forced, to get the same result regardless of the locale
	public static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm z");
	public static String formatString(String s) {
		return StringUtil.format("{0} - {1}", dateFormat.format(new Date()), s);
	}
	
	
	public AbstractHistoryEntry(IWorkflowContext context) {
	}

	public final String getAsString() throws WorkflowException {
		return formatString(getString());
	}
	
	public abstract String getString() throws WorkflowException;
}
