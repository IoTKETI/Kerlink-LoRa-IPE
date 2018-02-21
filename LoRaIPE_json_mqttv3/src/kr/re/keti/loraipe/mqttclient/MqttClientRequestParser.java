package kr.re.keti.loraipe.mqttclient;

import java.util.ArrayList;

import org.json.JSONObject;

public class MqttClientRequestParser {

	public static ArrayList<String> notificationParse(String json) throws Exception {
		String rqi = "";
		String content = "";
		String subr = "";
		
		JSONObject response = new JSONObject(json);
		
		if (response.has("m2m:rqp")) {
			JSONObject m2mrqp = response.getJSONObject("m2m:rqp");
			rqi = m2mrqp.getString("rqi");
			JSONObject pc = m2mrqp.getJSONObject("pc");
			JSONObject m2msgn = pc.getJSONObject("m2m:sgn");
			subr = m2msgn.getString("sur");
			JSONObject nev = m2msgn.getJSONObject("nev");
			JSONObject rep = nev.getJSONObject("rep");
			JSONObject m2mcin = rep.getJSONObject("m2m:cin");
			content = m2mcin.getString("con");
		}
		else {
			rqi = response.getString("rqi");
			JSONObject pc = response.getJSONObject("pc");
			JSONObject m2msgn = new JSONObject();
			
			if (pc.has("m2m:sgn")) {
				m2msgn = pc.getJSONObject("m2m:sgn");
			}
			else {
				m2msgn = pc.getJSONObject("sgn");
			}
			subr = m2msgn.getString("sur");
			JSONObject nev = m2msgn.getJSONObject("nev");
			JSONObject rep = nev.getJSONObject("rep");
			JSONObject m2mcin = rep.getJSONObject("m2m:cin");
			content = m2mcin.getString("con");
		}
				
		ArrayList<String> returnArray = new ArrayList<String>();
		returnArray.add(rqi);
		returnArray.add(content);
		returnArray.add(subr);
		
		return returnArray;
	}
}