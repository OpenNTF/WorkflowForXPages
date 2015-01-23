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
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import lotus.domino.Directory;
import lotus.domino.DirectoryNavigator;
import lotus.domino.Name;
import lotus.domino.NotesFactory;
import lotus.domino.NotesThread;
import lotus.domino.Session;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.identity.Group;
import org.activiti.engine.impl.GroupQueryImpl;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.GroupManager;
import org.apache.commons.lang.StringUtils;

public class DominoGroupManager extends GroupManager {
	public DominoGroupManager() {

	}

	@Override
	public Group createNewGroup(String groupId) {
		throw new ActivitiException(
				"Domino group manager doesn't support creating a new group");
	}

	@Override
	public void insertGroup(Group group) {
		throw new ActivitiException(
				"Domino group manager doesn't support inserting a new group");
	}

	@Override
	public void updateGroup(GroupEntity updatedGroup) {
		throw new ActivitiException(
				"Domino group manager doesn't support updating a new group");
	}

	@Override
	public void deleteGroup(String groupId) {
		throw new ActivitiException(
				"Domino group manager doesn't support deleting a new group");
	}

	@Override
	public List<Group> findGroupByQueryCriteria(GroupQueryImpl query, Page page) {
		Vector<String> names = new Vector<String>();
		if(StringUtils.isNotEmpty(query.getId())){
			names.add(query.getId());
		}
		if(StringUtils.isNotEmpty(query.getName())){
			names.add(query.getName());
		}
		return findGroups(names,false);
	}
	
	public List<Group> findGroups(Vector<String> names, boolean matchExact) {
		List<Group> groupList = new ArrayList<Group>(); 
		Vector<String> items = new Vector<String>();
		items.add("ListName");
		Session session = null;
		Directory dir = null;
		try{
			NotesThread.sinitThread();
			session= NotesFactory.createSessionWithFullAccess();
			dir = session.getDirectory("");
			DirectoryNavigator nav = dir.lookupNames("$Groups",names,items,matchExact);
			if(nav.getCurrentMatch()>0){
				do{
					Group group = new GroupEntity();
					String groupName = (String)nav.getFirstItemValue().get(0);
					group.setId(groupName);
					group.setName(groupName);
					group.setType("security-role");
					groupList.add(group);
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
		return groupList;
	}
	
	@Override  
    public long findGroupCountByQueryCriteria(GroupQueryImpl query) {  
        return findGroupByQueryCriteria(query, null).size();  
    }  
  
    @Override  
    public GroupEntity findGroupById(String groupId) {  
    	Vector<String> names = new Vector<String>();
		if(StringUtils.isNotEmpty(groupId)){
			names.add(groupId);
		}
		List<Group> groups = findGroups(names,true);
		if(groups.size()==1){
			return (GroupEntity)groups.get(0);
		}
		return null; 
    }  
  
    @Override  
    public List<Group> findGroupsByUser(String userId) {  
    	List<Group> groupList = new ArrayList<Group>(); 
		Session session = null;
		try{
			NotesThread.sinitThread();
			session= NotesFactory.createSessionWithFullAccess();
			Vector<Name> names = session.getUserGroupNameList();
			Iterator<Name> it = names.iterator();
			while(it.hasNext()){
				Group group = new GroupEntity();
				String groupName = it.next().getAbbreviated();
				group.setId(groupName);
				group.setName(groupName);
				group.setType("security-role");
				groupList.add(group);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				if(session != null)
					session.recycle();
			}catch(Exception e){
				
			}
			NotesThread.stermThread();
		}
		return groupList;  
    }  

}
