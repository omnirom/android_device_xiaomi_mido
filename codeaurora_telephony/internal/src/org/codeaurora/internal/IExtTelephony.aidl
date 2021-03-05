/*
 * Copyright (c) 2015, The Linux Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *     * Neither the name of The Linux Foundation nor the names of its
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.codeaurora.internal;

import org.codeaurora.internal.IDepersoResCallback;
import org.codeaurora.internal.IDsda;

import org.codeaurora.internal.SignalStrength;
import org.codeaurora.internal.Status;
import org.codeaurora.internal.Token;
import org.codeaurora.internal.DcParam;
import org.codeaurora.internal.INetworkCallback;
import org.codeaurora.internal.Client;
import org.codeaurora.internal.NrConfig;

/**
 * Interface used to interact with the telephony framework for
 * Telephony value adds.
 * {@hide}
 */
interface IExtTelephony {

    /**
     * Returns the current SIM Manual provision status.
     * @param slotId user preferred slotId.
     * @return Card provision status as integer, below are
     * possible return values.
     *   '0' - returned if Uicc Card is not provisioned.
     *   '1' - returned if Uicc Card provisioned.
     *  '-1'-  returned if there is an error @ below layers OR
     *         if framework does not received info from Modem yet.
     *  '-2'  returned when SIM card is not present in slot.
     * Requires Permission: android.Manifest.permission.READ_PHONE_STATE
     */
    int getCurrentUiccCardProvisioningStatus(int slotId);

    /**
     * Returns the user preferred Uicc card provision status.
     * @param slotId user preferred slotId.
     * @return User preference value as integer, below are
     * possible return values.
     *   '0' - returned if Uicc Card is not provisioned.
     *   '1' - returned if Uicc Card provisioned.
     *  '-1'-  returned if there is an error @ below layers OR
     *         if framework does not received info from Modem yet.
     *  '-2'  returned when SIM card is not present in slot.
     * Requires Permission: android.Manifest.permission.READ_PHONE_STATE
     */
    int getUiccCardProvisioningUserPreference(int slotId);

    /**
     * Activates the Uicc card.
     * @param slotId user preferred slotId.
     * @return Uicc card activation result as Integer, below are
     *         supported return values:
     *         '0' - Success
     *        '-1' -Generic Failure
     *        '-2' -Invalid input
     *        '-3  -Another request in progress
     * Requires Permission: android.Manifest.permission.MODIFY_PHONE_STATE
     */
    int activateUiccCard(int slotId);

    /**
     * Deactivates UICC card.
     * @param slotId user preferred slotId.
     * @return Uicc card deactivation result as Integer, below are
     *     supported return values:
     *     '0' - Success
     *     '-1' -Generic Failure
     *     '-2' -Invalid input
     *     '-3  -Another request in progress
     * Requires Permission: android.Manifest.permission.MODIFY_PHONE_STATE
     */
    int deactivateUiccCard(int slotId);

    /**
    * Check for Sms Prompt is Enabled or Not.
    * @return
    *        true - Sms Prompt is Enabled
    *        false - Sms prompt is Disabled
    * Requires Permission: android.Manifest.permission.READ_PHONE_STATE
    */
    boolean isSMSPromptEnabled();

    /**
    * Enable/Disable Sms prompt option.
    * @param - enabled
    *        true - to enable Sms prompt
    *        false - to disable Sms prompt
    * Requires Permission: android.Manifest.permission.MODIFY_PHONE_STATE
    */
    void setSMSPromptEnabled(boolean enabled);

    /**
    * Get logical phone id for Emergency call.
    * @param - void
    * @return phone id
    */
    int getPhoneIdForECall();

    /**
    * Set Primary card on given slot.
    * @param - slotId to be set as Primary Card.
    * @return void
    */
    void setPrimaryCardOnSlot(int slotId);

    /**
    * Check is FDN is enabled or not.
    * @param - void
    * @return true or false
    */
    boolean isFdnEnabled();

    /**
    * Check if get the icc file handler from specific application family sucessfully
    * @param slotId user preferred slotId.
    * @param family UICC application family.
    * @return true or false
    */
    boolean hasGetIccFileHandler(int slotId, int family);

    /**
    * Read the icc transparent file in the SIM card.
    * @param slotId user preferred slotId.
    * @param family UICC application family.
    * @param efId the file ID in the SIM card.
    * @return true or false
    */
    boolean readEfFromIcc(int slotId, int family, int efId);

    /**
    * Write the icc transparent file in the SIM card.
    * @param slotId user preferred slotId.
    * @param family UICC application family.
    * @param efId the file ID in the SIM card.
    * @param efdata updated data to the EF.
    * @return true - send the request to load transparent files sucessfully
    *         false - failed to get the icc file handler
    */
    boolean writeEfToIcc(int slotId, int family, int efId, in byte[] efData);

    /**
    * Get primary stack phone id.
    * @param - void
    * @return phone id
    */
    int getPrimaryStackPhoneId();

    /**
    * Check if number is emergency number or not.
    * @param - number
    * @return true or false
    */
    boolean isEmergencyNumber(String number);

    /**
    * Check if number is local emergency number or not.
    * @param - number
    * @return true or false
    */
    boolean isLocalEmergencyNumber(String number);

    /**
    * Check if number is potential emergency number or not.
    * @param - number
    * @return true or false
    */
    boolean isPotentialEmergencyNumber(String number);

    /**
    * Check if number is potential Local emergency number or not.
    * @param - number
    * @return true or false
    */
    boolean isPotentialLocalEmergencyNumber(String number);

    /**
    * Check if device in single stanby.
    * @param - void
    * @return true or false
    */
    boolean isDeviceInSingleStandby();

    /* Send local call hold request to RIL.
    * @param subId, send request on this subscription
    * @param enable
    *        true - to enable local call hold
    *        false - to disable local call hold
    * @return true or false
    */
    boolean setLocalCallHold(int subId, boolean enable);

    /**
    * Send switch to other subscription.
    * @param subId, send request on this subscription
    * @return void
    */
    void switchToActiveSub(int subId);

    /**
    * set adapter, so that IExtTelephony would have interface to send requests to service/telecom
    * @param dsdaAdapter, this adapter used by IExtTelephony as interface for requests in IDsda.
    * @return void
    */
    void setDsdaAdapter(in IDsda dsdaAdapter);

    /**
    * get active subscription.
    * @param void
    * @return subId of active subscription.
    */
    int getActiveSubscription();

    /**
    * returns device is in DSDA configuration or not.
    * @param void
    * @return true or false
    */
    boolean isDsdaEnabled();

    /**
    * supply pin to unlock sim locked on network.
    * @param - netpin - network pin to unlock the sim.
    * @param - type - PersoSubState for which the sim is locked onto.
    * @param - callback - callback to notify UI, whether the request was success or failure.
    * @param - phoneId - slot id on which the pin request is sent.
    * @return void
    */
    void supplyIccDepersonalization(String netpin, String type, in IDepersoResCallback callback,
            int phoneId);

    /**
    * Returns ID of the slot in which PrimaryCarrier SIM card is present.
    * If none of the slots contains PrimaryCarrier SIM, this would return '-1'
    * Supported values: 0, 1, -1
    */
    int getPrimaryCarrierSlotId();

    /**
    * Check if slotId has PrimaryCarrier SIM card present or not.
    * @param - slotId
    * @return true or false
    */
    boolean isPrimaryCarrierSlotId(int slotId);

    /**
    * Change SMSC address based on slotId.
    * @param - slotId
    * @param - SMSC address
    * @return true or false
    */
    boolean setSmscAddress(int slotId, String smsc);

    /**
    * Get SMSC address based on slotId.
    * @param - slotId
    * @param - SMSC address
    * @return SMSC address or null
    */
    String getSmscAddress(int slotId);

    /**
    * Perform incremental scan using QCRIL hooks.
    * @param - slotId
    *          Range: 0 <= slotId < {@link TelephonyManager#getActiveModemCount()}
    * @return true if the request has successfully been sent to the modem, false otherwise.
    * Requires permission: android.Manifest.permission.MODIFY_PHONE_STATE
    */
    boolean performIncrementalScan(int slotId);

    /**
    * Abort incremental scan using QCRIL hooks.
    * @param - slotId
    *          Range: 0 <= slotId < {@link TelephonyManager#getActiveModemCount()}
    * @return true if the request has successfully been sent to the modem, false otherwise.
    * Requires permission: android.Manifest.permission.MODIFY_PHONE_STATE
    */
    boolean abortIncrementalScan(int slotId);

    /**
    * Check if target available with given packageName.
    * @param packageName
    * @return true or false
    */
    boolean isVendorApkAvailable(String packageName);

    /**
    * Get current primary card slot Id.
    * @param - void
    * @return slot index
    */
    int getCurrentPrimaryCardSlotId();

    /**
    * Async api
    * @deprecated
    */
    Token enable5g(int slotId, in Client client);

    /**
    * Async api
    * @deprecated
    */
    Token disable5g(int slotId, in Client client);

    /**
    * Async api
    * @deprecated
    */
    Token enable5gOnly(int slotId, in Client client);

    /**
    * Async api
    * @deprecated
    */
    Token query5gStatus(int slotId, in Client client);

    /**
    * Async api
    * a.k.a NR EN-DC and restrict-DCNR.
    * @deprecated
    */
    Token queryNrDcParam(int slotId, in Client client);

    /**
    * Async api
    * @deprecated
    */
    Token queryNrBearerAllocation(int slotId, in Client client);

    /**
    * Async api
    * @deprecated
    */
    Token queryNrSignalStrength(int slotId, in Client client);

    /**
    * Async api
    * @deprecated
    */
    Token queryUpperLayerIndInfo(int slotId, in Client client);

    /**
    * Async api
    * @deprecated
    */
    Token query5gConfigInfo(int slotId, in Client client);

    /**
    * Async api
    */
    Token queryNrIconType(int slotId, in Client client);

    /**
    * Enable/disable endc on a given slotId.
    * @param - slotId
    * @param - enabled
    *        true - to enable endc
    *        false - to disable endc
    *  @param - client registered with packagename to receive
    *         callbacks.
    * @return Integer Token to be used to compare with the response.
    */
    Token enableEndc(int slotId, boolean enable, in Client client);

    /**
    * To query endc status on a given slotId.
    * @param - slotId
    * @param - client registered with packagename to receive
    *         callbacks.
    * @return Integer Token to be used to compare with the response.
    */
    Token queryEndcStatus(int slotId, in Client client);

    /**
    * Async api
    */
    Client registerCallback(String packageName, INetworkCallback callback);

    /**
    * Async api
    */
    void unRegisterCallback(INetworkCallback callback);

    /**
    * Get value assigned to vendor property
    * @param - property name
    * @param - default value of property
    * @return - integer value assigned
    */
    int getPropertyValueInt(String property, int def);

    /**
    * Get value assigned to vendor property
    * @param - property name
    * @param - default value of property
    * @return - boolean value assigned
    */
    boolean getPropertyValueBool(String property, boolean def);

    /**
    * Get value assigned to vendor property
    * @param - property name
    * @param - default value of property
    * @return - string value assigned
    */
    String getPropertyValueString(String property, String def);

    /**
    * Set nr config to NSA/SA/NSA+SA on a given slotId.
    * @param - slotId
    * @param - def
    *        NR_CONFIG_INVALID  - invalid config
    *        NR_CONFIG_COMBINED_SA_NSA - set to NSA+SA
    *        NR_CONFIG_NSA - set to NSA
    *        NR_CONFIG_SA - set to SA
    *  @param - client registered with packagename to receive
    *         callbacks.
    * @return Integer Token to be used to compare with the response.
    */
    Token setNrConfig(int slotId, in NrConfig def, in Client client);

    /**
    * Query current nr config on a given slotId.
    * @param - slotId
    *  @param - client registered with packagename to receive
    *         callbacks.
    * @return Integer Token to be used to compare with the response.
    */
    Token queryNrConfig(int slotId, in Client client);

}
