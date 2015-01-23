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
package com.ibm.xsp.xflow.activiti_rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import lotus.domino.Directory;
import lotus.domino.DirectoryNavigator;
import lotus.domino.NotesFactory;
import lotus.domino.NotesThread;
import lotus.domino.Session;
import lotus.domino.Name;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.UserQueryImpl;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.activiti.engine.impl.persistence.entity.UserManager;
import org.apache.commons.lang.StringUtils;

public class DominoUserManager extends UserManager {
	public DominoUserManager() {
	}

	@Override
	public User createNewUser(String userId) {
		throw new ActivitiException(
				"Domino user manager doesn't support creating a new user");
	}
	
	@Override  
    public void insertUser(User user) {  
        throw new ActivitiException(  
                "Domino user manager doesn't support inserting a new user");  
    }  
  
    @Override  
    public void updateUser(UserEntity updatedUser) {  
        throw new ActivitiException(  
                "Domino user manager doesn't support updating a user");  
    }  
  
    @Override  
    public UserEntity findUserById(String userId) {  
        throw new ActivitiException(  
                "Domino user manager doesn't support finding a user by id");  
    }  
  
    @Override  
    public void deleteUser(String userId) {  
        throw new ActivitiException(  
                "Domino user manager doesn't support deleting a user");  
    } 

	@Override
	public Boolean checkPassword(String userId, String password) {
		// We are using Domino authentication, no need to check password here
		return true;
	}
	
	@Override
	public List<User> findUserByQueryCriteria(UserQueryImpl query, Page page) {
		List<User> userList = new ArrayList<User>();
		Vector<String> items = new Vector<String>();
		items.add("FirstName");
		items.add("LastName");
		items.add("FullName");
		Vector<String> names = new Vector<String>();
		if(StringUtils.isNotEmpty(query.getId())){
			// Need to search word separately, otherwise Domino could not find them.
			String[] words = query.getId().split("\\W");
			for(int i=0;i<words.length;i++){
				names.add(words[i]);
			}
		}
		if(StringUtils.isNotEmpty(query.getLastName())){
			String[] words = query.getLastName().split("\\W");
			for(int i=0;i<words.length;i++){
				names.add(words[i]);
			}
		}
		if(StringUtils.isNotEmpty(query.getFirstName())){
			String[] words = query.getFirstName().split("\\W");
			for(int i=0;i<words.length;i++){
				names.add(words[i]);
			}
		}
		Session session = null;
		Directory dir = null;
		try{
			NotesThread.sinitThread();
			session= NotesFactory.createSessionWithFullAccess();
			dir = session.getDirectory("");
			DirectoryNavigator nav = dir.lookupNames("$Users",names,items,true);
			if(nav.getCurrentMatch()>0){
				do{
					User user = new UserEntity();
					user.setFirstName((String)nav.getFirstItemValue().get(0));
					user.setLastName((String)nav.getNextItemValue().get(0));
					Name notesName = session.createName((String)nav.getNextItemValue().get(0));
					user.setId(notesName.getAbbreviated());
					userList.add(user);
				}while(nav.findNextMatch());
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				if(dir != null)
					dir.recycle();
				if(session != null)
					session.recycle();
			}catch(Exception e){
				
			}
			NotesThread.stermThread();
		}
		return userList;
	}
	
    @Override  
    public long findUserCountByQueryCriteria(UserQueryImpl query) {  
        return findUserByQueryCriteria(query, null).size();  
    } 
}