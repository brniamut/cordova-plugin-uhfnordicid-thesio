package it.dynamicid;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Uhfnordicid extends CordovaPlugin {
	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		if ("scan".equals(action)) {
			scan(args.getString(0), args.getLong(1), callbackContext);
			return true;
		}

		return false;
	}

	private void scan(String epc, long waittime, CallbackContext callbackContext) {
		try {
			 Context context = this.cordova.getActivity().getApplicationContext();
			
			InventoryUhf iu = new InventoryUhf(context);
			
			iu.StartInventoryStream();
			try {
				Thread.sleep(waittime);
			} catch (Exception e) {

			}
			iu.StopInventoryStream();
			
			
			String result = "NO-TAGS";

			if(epc.isEmpty()) {
				result = iu.GetTags();
				//result = iu.Inv();
				
				if(result.isEmpty()){
					result = "NO-TAGS";
				}
				
				
								
			}else {
				result = iu.GetTags();
				String tags[] = result.split(",");
				List<String> lista = new ArrayList<String>();

				for(int i=0; i<tags.length; i++) {
					lista.add(tags[i]);
				}

				if(lista.contains(epc)) {
					result="OK";
				}else {
					result="KO";
				}

			}


			Toast.makeText(webView.getContext(), iu.mUiResultMsg, Toast.LENGTH_LONG).show();
			callbackContext.success(result);

		} catch (Exception e) {
			callbackContext.error(e.toString());
		}


	}
}
