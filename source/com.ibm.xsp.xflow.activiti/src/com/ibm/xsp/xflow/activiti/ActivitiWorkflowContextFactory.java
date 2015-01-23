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

import java.util.Locale;

import javax.faces.context.FacesContext;

import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.designer.context.XSPContext;
import com.ibm.xsp.extlib.beans.UserBean;
import com.ibm.xsp.model.DataSource;
import com.ibm.xsp.model.domino.DominoDocumentData;
import com.ibm.xsp.xflow.IIdentityResolver;
import com.ibm.xsp.xflow.IProcess;
import com.ibm.xsp.xflow.IRoleResolver;
import com.ibm.xsp.xflow.IWorkflowContext;
import com.ibm.xsp.xflow.WorkflowException;
import com.ibm.xsp.xflow.component.AbstractWorkflow;
import com.ibm.xsp.xflow.component.UICommonWorkflow;
import com.ibm.xsp.xflow.component.identity.IdentityResolver;
import com.ibm.xsp.xflow.component.role.RoleResolver;
import com.ibm.xsp.xflow.process.CommonProcess;
import com.ibm.xsp.xflow.util.WorkflowContextFactory;

public class ActivitiWorkflowContextFactory  extends WorkflowContextFactory{
	private IIdentityResolver idResolver = null;
	private IRoleResolver roleResolver = null;
	private String actionHandler = null;
	private String url = null;
	
	@Override
	public IWorkflowContext createWorkflowContext(final FacesContext context,
			final AbstractWorkflow workflow, final DataSource dataSource, final Object data)
			throws WorkflowException {
		if(dataSource instanceof DominoDocumentData) {
			ActivitiWorkflowContext wContext = new ActivitiWorkflowContext(data) {
				@Override
				public String getCurrentUser() {
					return UserBean.get().getId();
				}
				@Override
				public Locale getUserLocale() {
					return XSPContext.getXSPContext(context).getLocale();
				}
				@Override
				public boolean isReadonly() {
					return dataSource.isReadonly();
				}
				@Override
				protected IProcess findProcess() throws WorkflowException {
					String processActionHandler = ((UICommonWorkflow)workflow).getActionHandler();
					if(processActionHandler!=null && !"".equals(processActionHandler)){
						actionHandler = processActionHandler;
					}
					CommonProcess p = new CommonProcess(((UICommonWorkflow)workflow).getProcessId(),actionHandler);
					p.setLabelProcessAction(((UICommonWorkflow)workflow).getLabelProcessAction());
					return p;
				}
				@Override
				protected IIdentityResolver findIdentityResolver() throws WorkflowException {
					IdentityResolver r = ((UICommonWorkflow)workflow).getIdentityResolver();
					IIdentityResolver result = null;
					if(r!=null) {
						result = r.findIdentityResolver(context);
					}
					if(result==null){
						return idResolver;
					}
					return result;
				}
				@Override
				protected IRoleResolver findRoleResolver() throws WorkflowException {
					RoleResolver r = ((UICommonWorkflow)workflow).getRoleResolver();
					IRoleResolver result = null;
					if(r!=null) {
						result = r.findRoleResolver(context);
					}
					if(result!=null){
						return result;
					}
					return roleResolver;
				}
				@Override
				protected String getRestUrl(){
					return url;
				}
				
			};
			return wContext;
		}
		
		throw new FacesExceptionEx(null,"Cannot run a simple workflow on top of a data source of type {0}",dataSource.getClass().getName());
	}
	
	@Override
	public void setWorkflowInfo(String server, String port, String url, String endpoint, IIdentityResolver idResolver, IRoleResolver roleResolver, String actionHandler){
		this.url = url;
		this.idResolver = idResolver;
		this.roleResolver = roleResolver;
		this.actionHandler = actionHandler;
	}
}
