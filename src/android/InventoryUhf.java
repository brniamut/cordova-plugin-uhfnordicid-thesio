package it.dynamicid;


import com.nordicid.nurapi.*;
import com.nordicid.tdt.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.nordicid.nurapi.NurApi.BANK_EPC;
import static com.nordicid.nurapi.NurApi.BANK_TID;
import static com.nordicid.nurapi.NurApi.BANK_USER;
import static com.nordicid.nurapi.NurApi.MAX_EPC_LENGTH;

public class InventoryUhf {

    public static final String TAG = "NUR_SAMPLE";

    //Handles of these will be fetch from MainActivity
    public NurApi mNurApi;
    private static AccessoryExtension mAccExt;


    //These values will be shown in the UI
    private String mUiStatusMsg;
    private String mUiResultMsg;
    private String mUiEpcMsg;
    private int mUiStatusColor;
    private int mUiResultColor;
    private int mUiEpcColor;

    //Need to keep track when state change on trigger button
    private boolean mTriggerDown;

    //This demo (Inventory stream) just counts different tags found
    public int mTagsAddedCounter;

    //====== Global variables for ScanSingleTag thread operation ======
    //This counter add by one when single tag found after inventory. Reset to zero if multiple tags found.
    int mSingleTagFoundCount;

    //This is true while searching single tag operation ongoing.
    boolean mSingleTagDoTask;

    //This counts scan rounds and when reaching 15 it's time to stop.
    int mSingleTagRoundCount;

    //Temporary storing current TX level because single tag will be search using low TX level
    int mSingleTempTxLevel;

    //This variable hold last tag epc for making sure same tag found 3 times in row.
    static String mTagUnderReview;

    //===================================================================

	public InventoryUhf() {
		super();
		mNurApi = new NurApi();
		mNurApi.setListener(mNurApiEventListener);
		try {
			mNurApi.setSetupTxLevel(NurApi.TXLEVEL_9);
		} catch (Exception e) {
			
		} 
	}
	
	public String Inv() {
		String retval = "";
		
		 try {
			NurRespInventory resp = mNurApi.inventory(2, 4, 0);
			
			List<String> lista = new ArrayList<String>();
			for(int i=0; i<resp.numTagsFound; i++) {
				NurTag tag = mNurApi.fetchTagAt(true, 0);
				String epcString = NurApi.byteArrayToHexString(tag.getEpc());
	            if(!lista.contains(epcString)) {
	            	lista.add(epcString);
	            	retval = retval + epcString + ",";
	            }
			}
			
		} catch (Exception e) {
			retval = e.toString();
		}
		
		return retval;
	}
    
    

    /**
     * Start inventory streaming. After this you can receive InventoryStream events.
     * Inventory stream is active around 20 sec then stopped automatically. Event received about the state of streaming so you can start it immediately again.
     */
    public void StartInventoryStream()
    {

        try {
            mNurApi.clearIdBuffer(); //This command clears all tag data currently stored into the moduleâ€™s memory as well as the API's internal storage.
            mNurApi.startInventoryStream(); //Kick inventory stream on. Now inventoryStreamEvent handler offers inventory results.
            mTriggerDown = true; //Flag to indicate inventory stream running
            mTagsAddedCounter = 0;
            mUiResultMsg = "Tags:" + String.valueOf(mTagsAddedCounter);
            mUiStatusMsg = "Inventory streaming...";
        }
        catch (Exception ex)
        {
            mUiResultMsg = ex.getMessage();
        }
    }




	/**
     * Stop streaming.
     */
    public void StopInventoryStream()
    {
        try {
            if (mNurApi.isInventoryStreamRunning())
                mNurApi.stopInventoryStream();
            mTriggerDown = false;
            mUiStatusMsg = "Waiting button press...";
            //mUiStatusColor = Color.BLACK;
        }
        catch (Exception ex)
        {
            mUiResultMsg = ex.getMessage();
            //mUiResultColor = Color.RED;
        }
    }
    
    public String GetTags() {
    	String retval = "";
    	
    	/*
    	try {
			mNurApi.fetchTagAt(true, 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	*/
    	
    	List<String> lista = new ArrayList<String>();
    	
    	NurTagStorage tagStorage = mNurApi.getStorage();
    	for(int i=0; i<tagStorage.size(); i++) {
    		String epcString;
            NurTag tag = tagStorage.get(i);
            epcString = NurApi.byteArrayToHexString(tag.getEpc());
            if(!lista.contains(epcString)) {
            	lista.add(epcString);
            	retval = retval + epcString + ",";
            }
    	}   	
    	
    	return retval;
    }
    
    
    private NurApiListener mNurApiEventListener = new NurApiListener()
    {
        @Override
        public void triggeredReadEvent(NurEventTriggeredRead event) { }
        @Override
        public void traceTagEvent(NurEventTraceTag event) { }
        @Override
        public void programmingProgressEvent(NurEventProgrammingProgress event) { }
        @Override
        public void nxpEasAlarmEvent(NurEventNxpAlarm event) { }
        @Override
        public void logEvent(int level, String txt) { }
        @Override
        public void inventoryStreamEvent(NurEventInventory event) {

            try {
                if (event.stopped) {
                    //InventoryStreaming is not active for ever. It automatically stopped after ~20 sec but it can be started again immediately if needed.
                    //check if need to restart streaming
                    if (mTriggerDown)
                        mNurApi.startInventoryStream(); //Trigger button still down so start it again.

                } else {

                    if(event.tagsAdded>0) {
                        //At least one new tag found
                    	/*
                        if(MainActivity.IsAccessorySupported()){
                            mAccExt.beepAsync(20); //Beep on device
                        }else{
                            Beeper.beep(Beeper.BEEP_40MS); //Cannot beep on device so we beep on phone
                        }
                        */

                        NurTagStorage tagStorage = mNurApi.getStorage(); //Storage contains all tags found

                        //Iterate just received tags based on event.tagsAdded
                        for(int x=mTagsAddedCounter;x<mTagsAddedCounter+event.tagsAdded;x++) {
                            //Real application should handle all tags iterated here.
                            //But this just show how to get tag from storage.
                            String epcString;
                            NurTag tag = tagStorage.get(x);
                            epcString = NurApi.byteArrayToHexString(tag.getEpc());
                            //showing just EPC of last tag
                            mUiEpcMsg = epcString;
                        }

                        //Finally show count of tags found
                        mTagsAddedCounter += event.tagsAdded;
                        mUiResultMsg = "Tags:" + String.valueOf(mTagsAddedCounter);
                        //mUiResultColor = Color.rgb(0, 128, 0);
                    }
                }
            }
            catch (Exception ex)
            {
                //mStatusTextView.setText(ex.getMessage());
               // mUiStatusColor = Color.RED;
                //showOnUI();
            }
        }
        @Override
        public void inventoryExtendedStreamEvent(NurEventInventory event) {}
        @Override
        public void frequencyHopEvent(NurEventFrequencyHop event) { }
        @Override
        public void epcEnumEvent(NurEventEpcEnum event) { }
        @Override
        public void disconnectedEvent() {
            //finish(); //Device disconnected. Exit from this activity
        }
        @Override
        public void deviceSearchEvent(NurEventDeviceInfo event) { }
        @Override
        public void debugMessageEvent(String event) { }
        @Override
        public void connectedEvent() { }
        @Override
        public void clientDisconnectedEvent(NurEventClientInfo event) { }
        @Override
        public void clientConnectedEvent(NurEventClientInfo event) { }
        @Override
        public void bootEvent(String event) {}
        @Override
        public void IOChangeEvent(NurEventIOChange event) {
             //HandleIOEvent(event);
        }
        @Override
        public void autotuneEvent(NurEventAutotune event) { }
        @Override
        public void tagTrackingScanEvent(NurEventTagTrackingData event) { }
        //@Override
        public void tagTrackingChangeEvent(NurEventTagTrackingChange event) { }
    };

    
}
