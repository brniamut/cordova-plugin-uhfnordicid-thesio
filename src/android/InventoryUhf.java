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

import android.content.Context;

public class InventoryUhf {

    public static final String TAG = "NUR_SAMPLE";

    //Handles of these will be fetch from MainActivity
    public NurApi mNurApi;
    private static AccessoryExtension mAccExt;


    //These values will be shown in the UI
    private String mUiStatusMsg;
    public String mUiResultMsg;
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
	
    private NurApiAutoConnectTransport mAcTr;
    
    List<String> listaTags;

    //===================================================================

	public InventoryUhf(Context context, long txpower) {
		super();
		listaTags = new ArrayList<String>();
		mNurApi = new NurApi();
		
		 String specStr = "type=INT;addr=integrated_reader";
		 NurDeviceSpec spec = new NurDeviceSpec(specStr);

         if (mAcTr != null) {
             System.out.println("Dispose transport");
             mAcTr.dispose();
         }

         try {
             String strAddress;
             mAcTr = NurDeviceSpec.createAutoConnectTransport(context, mNurApi, spec);        
             strAddress = spec.getAddress();
             mAcTr.setAddress(strAddress);
         } catch (NurApiException e) {
             e.printStackTrace();
         }
		try {
		mNurApi.connect();
				
		} catch (Exception e) {
			
		} 
		
		while(!mNurApi.isConnected()){
			try {
         			Thread.sleep(100);
         		}catch (Exception e) {
				// TODO: handle exception
			}
		}
			
		try {	
			
			if(txpower==8) {
				mNurApi.setSetupTxLevel(NurApi.TXLEVEL_8);
			}else if(txpower==9) {
				mNurApi.setSetupTxLevel(NurApi.TXLEVEL_9);
			}else if(txpower==10) {
				mNurApi.setSetupTxLevel(NurApi.TXLEVEL_10);
			}else if(txpower==11) {
				mNurApi.setSetupTxLevel(NurApi.TXLEVEL_11);
			}else if(txpower==12) {
				mNurApi.setSetupTxLevel(NurApi.TXLEVEL_12);
			}else if(txpower==13) {
				mNurApi.setSetupTxLevel(NurApi.TXLEVEL_13);
			}else if(txpower==14) {
				mNurApi.setSetupTxLevel(NurApi.TXLEVEL_14);
			}else if(txpower==15) {
				mNurApi.setSetupTxLevel(NurApi.TXLEVEL_15);
			}else if(txpower==16) {
				mNurApi.setSetupTxLevel(NurApi.TXLEVEL_16);
			}else if(txpower==17) {
				mNurApi.setSetupTxLevel(NurApi.TXLEVEL_17);
			}else if(txpower==18) {
				mNurApi.setSetupTxLevel(NurApi.TXLEVEL_18);
			}else if(txpower==19) {
				mNurApi.setSetupTxLevel(NurApi.TXLEVEL_19);
			}else if(txpower==20) {
				mNurApi.setSetupTxLevel(NurApi.TXLEVEL_20);
			}else if(txpower==21) {
				mNurApi.setSetupTxLevel(NurApi.TXLEVEL_21);
			}else if(txpower==22) {
				mNurApi.setSetupTxLevel(NurApi.TXLEVEL_22);
			}else if(txpower==23) {
				mNurApi.setSetupTxLevel(NurApi.TXLEVEL_23);
			}else if(txpower==24) {
				mNurApi.setSetupTxLevel(NurApi.TXLEVEL_24);
			}else if(txpower==25) {
				mNurApi.setSetupTxLevel(NurApi.TXLEVEL_25);
			}else if(txpower==26) {
				mNurApi.setSetupTxLevel(NurApi.TXLEVEL_26);
			}else if(txpower==27) {
				mNurApi.setSetupTxLevel(NurApi.TXLEVEL_27);
			}else {
				mNurApi.setSetupTxLevel(NurApi.TXLEVEL_8);
			}

		} catch (Exception e) {
			
		} 
		
		mAccExt = new AccessoryExtension(mNurApi);
		mNurApi.setListener(mNurApiEventListener);
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
		
		try{
			mAcTr.dispose();	
		}catch(Exception e){
			
		}
		
		try{
			mNurApi.disconnect();
		}catch(Exception e){
			
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
	    
	try{
		mAcTr.dispose();	
	}catch(Exception e){

	}

	try{
		mNurApi.disconnect();
	}catch(Exception e){

	}
    }
    
    public String GetTags() {
    	String retval = "";
    	

    	for(int i=0; i<listaTags.size(); i++) {
    		String epcString;
            epcString = listaTags.get(i);
           	retval = retval + epcString + ",";           
    	}   	
    	
    	return retval;
    }
    
    
    private NurApiListener mNurApiEventListener = new NurApiListener()
    {
        @Override
        public void triggeredReadEvent(NurEventTriggeredRead event) {
                     try {
                           mAccExt.beepAsync(20); //Beep on device
                         } catch (Exception e) {
                             // TODO: handle exception
                         }
        }
        @Override
        public void traceTagEvent(NurEventTraceTag event) {

        }
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
                            
                            if(!listaTags.contains(epcString)) {
                            	listaTags.add(epcString);                            	
                            }
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
