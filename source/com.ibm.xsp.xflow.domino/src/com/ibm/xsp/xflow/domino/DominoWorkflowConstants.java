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



/**
 * Domino Workflow constants. 
 */
public class DominoWorkflowConstants {

	// ===================================================================
	//	Basic Workflow Fields
	// ===================================================================

	/**
	 * Requester of the workflow. 
	 */
	public static final String FIELD_REQUESTER		= "XFLOW_Requester"; 

	/**
	 * Stores the Locale when the document was created. 
	 */
	public static final String FIELD_LOCALE			= "XFLOW_Locale"; 
	
	/**
	 * Holds the workflow state.
	 */
	public static final String	FIELD_STATE 		= "XFLOW_State";
	
	public static final int	STATE_NOTINITIATED 		= 0;
	public static final int	STATE_RUNNING 			= 1;
	public static final int	STATE_COMPLETED 		= 2;

	/**
	 * Holds the workflow step.
	 */
	public static final String	FIELD_STEP		 	= "XFLOW_Step";
	
	/**
	 * Holds the workflow step label (can be used by views...).
	 */
	public static final String	FIELD_STEPLABEL	 	= "XFLOW_StepLabel";
	
	/**
	 * List of the participants of the current step
	 */
	public static final String	FIELD_PARTICIPANTS 	= "XFLOW_Participants";
	
	/**
	 * Form of current step
	 */
	public static final String	FIELD_FORM 	= "XFLOW_Form";
	
	/**
	 * List of the participant actions
	 */
	public static final String	FIELD_PARTICIPANTACTIONS 	= "XFLOW_ParticipantActions";
	
	/**
	 * Holds the workflow process id
	 */
	public static final String	FIELD_PROCESS_ID 	= "XFLOW_Process_ID";
	
	/**
	 * Holds the workflow process instance id
	 */
	public static final String	FIELD_PROCESS_INSTANCE_ID 	= "XFLOW_Process_Instance_ID";
	
	/**
	 * Holds the workflow task id
	 */
	public static final String	FIELD_TASK_ID 	= "XFLOW_Task_ID";
	
	/**
	 * Holds the list of readers for the current workflow.
	 */
	public static final String	FIELD_READERS	 	= "XFLOW_Readers";
	
	/**
	 * Holds the list of authors for the current workflow.
	 */
	public static final String	FIELD_AUTHORS	 	= "XFLOW_Authors";
	
	/**
	 * Holds the list of administrative readers.
	 */
	public static final String	FIELD_ADMINREADERS	 = "XFLOW_AdminReaders";
	public static final String[] DEFAULT_ADMINREADERS = new String[]{"[XFLOW_Admins]"};
	
	/**
	 * Holds the list of administrative authors.
	 */
	public static final String	FIELD_ADMINAUTHORS	 = "XFLOW_AdminAuthors";
	public static final String[] DEFAULT_ADMINAUTHORS = new String[]{"[XFLOW_Admins]"};

	/**
	 * History.
	 */
	public static final String	FIELD_HISTORY	 	= "XFLOW_History";

	
	// ===================================================================
	//	Router Based Workflow Fields
	// ===================================================================

	/**
	 * List of possible actions.
	 */
	public static final String	FIELD_ACTIONS 		= "XFLOW_Actions";
	public static final String	FIELD_ACTIONLABELS	= "XFLOW_ActionLabels";
	
	public static final String	FIELD_PROCESSORS	= "XFLOW_Processors";
	
}
