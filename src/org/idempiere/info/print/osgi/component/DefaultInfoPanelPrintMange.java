/**********************************************************************
* This file is part of Idempiere ERP Bazaar                           *
* http://www.idempiere.org                                            *
*                                                                     *
* Copyright (C) Idempiere                                             *
*                                                                     *
* This program is free software; you can redistribute it and/or       *
* modify it under the terms of the GNU General Public License         *
* as published by the Free Software Foundation; either version 2      *
* of the License, or (at your option) any later version.              *
*                                                                     *
* This program is distributed in the hope that it will be useful,     *
* but WITHOUT ANY WARRANTY; without even the implied warranty of      *
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the        *
* GNU General Public License for more details.                        *
*                                                                     *
* You should have received a copy of the GNU General Public License   *
* along with this program; if not, write to the Free Software         *
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,          *
* MA 02110-1301, USA.                                                 *
*                                                                     *
**********************************************************************/

package org.idempiere.info.print.osgi.component;

import java.util.ArrayList;
import java.util.Collection;

import org.adempiere.webui.apps.ProcessModalDialog;
import org.adempiere.webui.apps.WProcessCtl;
import org.adempiere.webui.component.ConfirmPanel;
import org.adempiere.webui.component.ProcessInfoDialog;
import org.adempiere.webui.event.DialogEvents;
import org.adempiere.webui.factory.IInfoButtonSetting;
import org.adempiere.webui.factory.IInfoPrintHandle;
import org.adempiere.webui.panel.InfoPanel;
import org.compiere.model.MInfoWindow;
import org.compiere.model.MPInstance;
import org.compiere.model.MProcess;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Trx;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

/**
 * 
 * @author hieplq
 *
 */
public class DefaultInfoPanelPrintMange implements IInfoPrintHandle, IInfoButtonSetting {

	public static final String COLUMNNAME_AllowDefaultPrint = "AllowDefaultPrint";
	public static final String COLUMNNAME_Process_ID = "AD_Process_ID";
	
	@Override
	public void settingButton(InfoPanel infoPanel, int adInfoWindowID, ConfirmPanel confirmPanel) {
		MInfoWindow mInfoWindow = MInfoWindow.get(adInfoWindowID, null);
		if (mInfoWindow == null)
			return;// generic infoWindow
		
		if (mInfoWindow.get_ValueAsBoolean(COLUMNNAME_AllowDefaultPrint) || mInfoWindow.get_ValueAsInt(COLUMNNAME_Process_ID) > 0) {
			confirmPanel.setVisible(ConfirmPanel.A_PRINT, true);
		}
	}

	@Override
	public void handlePrintClick(int adInfoWindowID, InfoPanel infoPanel) {
		MInfoWindow mInfoWindow = MInfoWindow.get(adInfoWindowID, null);
		if (mInfoWindow == null)
			return;// generic infoWindow
		
		if (mInfoWindow.get_ValueAsBoolean(COLUMNNAME_AllowDefaultPrint) && mInfoWindow.get_ValueAsInt(COLUMNNAME_Process_ID) == 0) {
			throw new UnsupportedOperationException("default print isn't yet implement");
		}else if (mInfoWindow.get_ValueAsInt(COLUMNNAME_Process_ID) > 0){
			runProcess (mInfoWindow.get_ValueAsInt(COLUMNNAME_Process_ID), mInfoWindow, infoPanel);
		}
	}
	
	protected void runProcess (final int processId, MInfoWindow mInfoWindow, InfoPanel infoPanel){
    	final MProcess m_process = MProcess.get(Env.getCtx(), processId);
    	final ProcessInfo m_pi = new ProcessInfo(m_process.getName(), processId);
		m_pi.setAD_User_ID(Env.getAD_User_ID(Env.getCtx()));
		m_pi.setAD_Client_ID(Env.getAD_Client_ID(Env.getCtx()));

		MPInstance instance = new MPInstance(Env.getCtx(), processId, 0);
		instance.saveEx();
		final int pInstanceID = instance.getAD_PInstance_ID();
		// Execute Process
		m_pi.setAD_PInstance_ID(pInstanceID);		
		m_pi.setAD_InfoWindow_ID(mInfoWindow.getAD_InfoWindow_ID());
		m_pi.setAD_InfoWindow_ID(mInfoWindow.getAD_InfoWindow_ID());
		Collection<KeyNamePair> selectedKeypairs = infoPanel.getSelectedKeyForPrint (false);
		
		Collection<Integer> selectedKeys = new ArrayList<>(); 
		for (KeyNamePair keypair : selectedKeypairs) {
			selectedKeys.add(keypair.getKey());
		}		
		ProcessInfoParameter [] paraSelectedKeys = m_pi.getParameter();
		ProcessInfoParameter infoSelectedPara = new ProcessInfoParameter("INFO_SELECTED_KEYS", selectedKeys, null, null, null);
		if (paraSelectedKeys == null) {
			paraSelectedKeys = new ProcessInfoParameter [] {infoSelectedPara};
		}else {
			paraSelectedKeys [paraSelectedKeys.length] = infoSelectedPara;
		}
		
		m_pi.setParameter(paraSelectedKeys);
		
		WProcessCtl.process(infoPanel.getWindowNo(), m_pi, (Trx)null, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				ProcessModalDialog processModalDialog = (ProcessModalDialog)event.getTarget();
				if (DialogEvents.ON_BEFORE_RUN_PROCESS.equals(event.getName())){
					// store in T_Selection table selected rows for Execute Process that retrieves from T_Selection in code.
					//DB.createT_SelectionNew(pInstanceID, getSaveKeys(getInfoColumnIDFromProcess(processModalDialog.getAD_Process_ID())), null);					
				}else if (ProcessModalDialog.ON_WINDOW_CLOSE.equals(event.getName())){ 
					if (processModalDialog.isCancel()){
						// enable or disable control button rely selected record status 
						// enableButtons();
					}else if (m_pi.isError()){
						ProcessInfoDialog.showProcessInfo(m_pi, infoPanel.getWindowNo(), infoPanel, true);
						// enable or disable control button rely selected record status 
						// enableButtons();
					}else if (!m_pi.isError()){
						//ProcessInfoDialog.showProcessInfo(m_pi, infoPanel.getWindowNo(), infoPanel, true);	
						//isRequeryByRunSuccessProcess = true;
						//Clients.response(new AuEcho(InfoPanel.this, "onQueryCallback", null));
					}
					
				}
			}
		});   		
    }

}
