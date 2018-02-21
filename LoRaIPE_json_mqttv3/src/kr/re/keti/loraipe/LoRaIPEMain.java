/*
 * ------------------------------------------------------------------------
 * Copyright 2014 Korea Electronics Technology Institute
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------------------
 */

package kr.re.keti.loraipe;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpServer;

import kr.re.keti.loraipe.httpserver.HttpServerTestHandler;
import kr.re.keti.loraipe.mqttclient.MqttClientKeti;
import kr.re.keti.loraipe.mqttclient.MqttClientKetiPub;
import kr.re.keti.loraipe.resource.AE;
import kr.re.keti.loraipe.resource.CSEBase;
import kr.re.keti.loraipe.resource.ResourceRepository;

/**
 * Main Class
 * @author NakMyoung Sung (nmsung@keti.re.kr)
 */
public class LoRaIPEMain {
	
	private static CSEBase hostingCSE = new CSEBase();
	private static AE hostingAE = new AE();
	private static InetAddress ip;
	
	private static boolean windows = true;
	
	public static MqttClientKeti requestClient;
	public static MqttClientKeti responseClient;
	public static MqttClientKetiPub publishClient;
	
	/**
	 * configurationFileLoader Method
	 * Load the XML profile named 'thyme_conf.xml' from local storage for create the AE and container resources. 
	 * @throws Exception
	 */
	private static void configurationFileLoader() throws Exception {
		
		System.out.println("[oneM2MIPE] Configuration file loading...");
		
		String jsonString = "";
		
		BufferedReader br = new BufferedReader(new FileReader("./conf.json"));
		while(true) {
			String line = br.readLine();
			if (line == null) break;
			jsonString += line;
		}
		br.close();
				
		JSONObject conf = new JSONObject(jsonString);
		
		JSONObject cseObj = conf.getJSONObject("cse");
		hostingCSE.CSEHostAddress = cseObj.getString("cbhost");
		System.out.println("[oneM2MIPE] CSE - cbhost : " + hostingCSE.CSEHostAddress);
		hostingCSE.CSEPort = cseObj.getString("cbport");
		System.out.println("[oneM2MIPE] CSE - cbport : " + hostingCSE.CSEPort);
		hostingCSE.CSEName = cseObj.getString("cbname");
		System.out.println("[oneM2MIPE] CSE - cbname : " + hostingCSE.CSEName);
		hostingCSE.CSEId = cseObj.getString("cbcseid");
		System.out.println("[oneM2MIPE] CSE - cbcseid : " + hostingCSE.CSEId);
		hostingCSE.mqttPort = cseObj.getString("mqttport");
		System.out.println("[oneM2MIPE] CSE - mqttPort : " + hostingCSE.mqttPort);
		ResourceRepository.setCSEBaseInfo(hostingCSE);

		JSONObject aeObj = conf.getJSONObject("ae");
		hostingAE.aeId = aeObj.getString("aeid");
		System.out.println("[oneM2MIPE] AE - aeId : " + hostingAE.aeId);
		hostingAE.appId = aeObj.getString("appid");
		System.out.println("[oneM2MIPE] AE - appid : " + hostingAE.appId);
		hostingAE.appName = aeObj.getString("appname");
		System.out.println("[oneM2MIPE] AE - appname : " + hostingAE.appName);
		hostingAE.bodyType = aeObj.getString("bodytype");
		System.out.println("[oneM2MIPE] AE - bodytype : " + hostingAE.bodyType);
		hostingAE.webPort = aeObj.getString("webport");
		System.out.println("[oneM2MIPE] AE - webport : " + hostingAE.webPort);
		ResourceRepository.setAEInfo(hostingAE);
	}
	
	/**
	 * main Method
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		System.out.println("[oneM2MIPE] oneM2MIPE SW start.......\n");
		
		// Load the oneM2MIPE configuration file
		configurationFileLoader();

		// Initialize the HTTP server for receiving the notification messages
		System.out.println("[oneM2MIPE] oneM2MIPE initialize.......\n");
		
		if (windows) {
			HttpServer server = HttpServer.create(new InetSocketAddress(Integer.parseInt(hostingAE.webPort)), 0);
			server.createContext("/loraipe", new HttpServerTestHandler()); // HTTP server test url
			server.setExecutor(null); // creates a default executor
			server.start();
		}
		else {
			InetSocketAddress serverSocketAddress = new InetSocketAddress(ip.getHostAddress(), Integer.parseInt(hostingAE.webPort));
			HttpServer server = HttpServer.create(serverSocketAddress, 0);
			server.createContext("/loraipe", new HttpServerTestHandler()); // HTTP server test url
			server.setExecutor(null); // creates a default executor
			server.start();
		}
		
		// Registration sequence
		Registration regi = new Registration();
		regi.registrationStart();
	}
}