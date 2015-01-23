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

package com.ibm.xsp.xflow.activiti;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.persistence.entity.UserManager;

public class DominoUserManager extends UserManager {
	public DominoUserManager() {
	}

	@Override
	public User createNewUser(String userId) {
		throw new ActivitiException(
				"LDAP user manager doesn't support creating a new user");
	}

	@Override
	public Boolean checkPassword(String userId, String password) {
		boolean credentialsValid = true;
		return credentialsValid;
	}
}