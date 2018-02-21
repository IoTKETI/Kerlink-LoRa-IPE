package kr.re.keti.loraipe.mqttclient;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

public class MqttClientRequest {
	
	public static String notificationResponse(ArrayList<String> response) {
//		String responseMessage = 
//				"<m2m:rsp\n" +
//						"xmlns:m2m=\"http://www.onem2m.org/xml/protocols\"\n" +
//						"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
//						"<rsc>2000</rsc>" + 
//						"<to>mobius-yt</to>" +
//						"<fr>nCube:Thyme</fr>" +
//						"<rqi>" + response.get(0) + "</rqi>" +
//						"<pc></pc>" +
//				"</m2m:rsp>";

		String responseMessage = "";
		
		JSONObject m2m = new JSONObject();	
		JSONObject rsp = new JSONObject();
		try {
			rsp.put("to", "mobiut-yt");
			rsp.put("fr", "lora_ipe");
			rsp.put("rqi", response.get(0));
			rsp.put("rsc", 2000);
			rsp.put("pc", "");
			m2m.put("m2m:rsp", rsp);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		responseMessage = m2m.toString();
		
		return responseMessage;
	}
}