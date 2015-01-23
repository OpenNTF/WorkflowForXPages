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

package com.ibm.xsp.xflow.role;

import com.ibm.xsp.xflow.IRoleResolver;
import com.ibm.xsp.xflow.WorkflowException;


/**
 * Abstract role resolver.. 
 */
public abstract class AbstractRoleResolver implements IRoleResolver {
	
	public AbstractRoleResolver() {
	}

	public boolean isRole(String role) throws WorkflowException {
		if(role.length()>2 && role.charAt(0)=='[' && role.charAt(role.length()-1)==']') {
			return true;
		}
		return false;
	}

	public String extractRole(String name) {
		return name.substring(1, name.length()-1);
	}
}
