package it.dynamicid;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import android.content.Context;
import android.widget.Toast;

public class Uhfnordicid extends CordovaPlugin {
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("scan".equals(action)) {
            scan(args.getString(0), callbackContext);
            return true;
        }

        return false;
    }

    private void scan(String msg, CallbackContext callbackContext) {
       	    /*
            InventoryUhf iu = new InventoryUhf();
		
	    	iu.StartInventoryStream();
    		try {
			    Thread.sleep(5000);
		    } catch (Exception e) {

		    }
		    iu.StopInventoryStream();
		
		    String result = iu.GetTags();
	    		*/
if (msg == null || msg.length() == 0) {
            callbackContext.error("Empty message!");
        } else {
            Toast.makeText(webView.getContext(), msg, Toast.LENGTH_LONG).show();
            callbackContext.success(msg);
        }
        
    }
}
