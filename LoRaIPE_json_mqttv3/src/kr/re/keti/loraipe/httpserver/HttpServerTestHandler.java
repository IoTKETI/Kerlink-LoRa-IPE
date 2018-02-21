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

package kr.re.keti.loraipe.httpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import kr.re.keti.loraipe.httpclient.HttpClientRequest;
import kr.re.keti.loraipe.resource.Base64;
import kr.re.keti.loraipe.resource.Kerlink;
import kr.re.keti.loraipe.resource.ResourceRepository;

/**
 * Handler class for handling the Http server test request 
 * @author NakMyoung Sung (nmsung@keti.re.kr)
 *
 */
public class HttpServerTestHandler implements HttpHandler {
	
	/**
	 * getStringFromInputStream Method
	 * Get stream data from InputStream for data handling
	 * @param is
	 * @return
	 */
	private String getStringFromInputStream(InputStream is) {
		String bufferedString = "";
		StringBuilder sb = new StringBuilder();
		
		BufferedReader br = new BufferedReader (new InputStreamReader(is));
		try {
			while ((bufferedString = br.readLine()) != null) {
				sb.append(bufferedString);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sb.toString();
	}
	
	/**
	 * handle Method
	 * Handle the Http server test request from user
	 */
	public void handle(HttpExchange t) throws IOException {
		System.out.println("[oneM2MIPE] Receive a HTTP POST request");
		
		String requestBody = "";
		
		if (t.getRequestMethod().equals("POST")) {
			requestBody = getStringFromInputStream(t.getRequestBody());
			System.out.println(requestBody);
			
			try {
				JSONArray tempArray = new JSONArray(requestBody);
								
				for (int i = 0; i < tempArray.length(); i++) {
					Kerlink tempDataset = new Kerlink();
					JSONObject tempJSONObj = (JSONObject) tempArray.get(i);
					
					if (tempJSONObj.has("rx")) {
						JSONObject rxObj = (JSONObject) tempJSONObj.get("rx");
						tempDataset.rx_moteeui = rxObj.getString("moteeui");
						tempDataset.rx_appeui = rxObj.getString("appeui");
						
						JSONObject userObj = rxObj.getJSONObject("userdata");
						tempDataset.rx_user_payload = Base64.decode(userObj.getString("payload"));
						// add others
						
						@SuppressWarnings("unused")
						JSONArray gwrxObj = (JSONArray) rxObj.get("gwrx");
						// add others
						
						boolean duplicate = false;
						boolean replace = false;
						ArrayList<Kerlink> tempDatasets = ResourceRepository.getKerlinkDatasets();
						
						if (tempDatasets.size() > 0) {
							for (int j = 0; j < tempDatasets.size(); j++) {
								if (tempDataset.rx_moteeui.equals(tempDatasets.get(j).rx_moteeui)) {
									tempDatasets.set(j, tempDataset);
									ResourceRepository.setKerlinkDatasets(tempDatasets);
									duplicate = true;
									try {
										int cinCode = HttpClientRequest.contentInstanceCreateRequest(ResourceRepository.getCSEInfo(),
																										ResourceRepository.getAEInfo().aeId + tempDataset.datasetID,
																										tempDataset.rx_appeui,
																										tempDataset.rx_moteeui + "/up",
																										tempDataset.rx_user_payload);
										if (cinCode == 404) {
											duplicate = false;
											replace = true;
										}
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									System.out.println("[oneM2MIPE] " + tempDataset.rx_moteeui + " data is updated");
									break;
								}
							}
						}
						
						if (!duplicate) {
							if (!replace) {
								tempDatasets.add(tempDataset);
								ResourceRepository.setKerlinkDatasets(tempDatasets);
							}
							else {
								for (int j = 0; j < tempDatasets.size(); j++) {
									if (tempDataset.rx_moteeui.equals(tempDatasets.get(j).rx_moteeui)) {
										tempDataset.datasetID = j;
										tempDatasets.set(j, tempDataset);
										ResourceRepository.setKerlinkDatasets(tempDatasets);
									}
								}
							}
							
							try {
								while(true) {
									int cinCode = HttpClientRequest.contentInstanceCreateRequest(ResourceRepository.getCSEInfo(),
																									ResourceRepository.getAEInfo().aeId + tempDataset.datasetID,
																									tempDataset.rx_appeui,
																									tempDataset.rx_moteeui + "/up",
																									tempDataset.rx_user_payload);
									if (cinCode == 404) {
										while(true) {
											int conCode = HttpClientRequest.containerCreateRequest(ResourceRepository.getCSEInfo(),
																									ResourceRepository.getAEInfo().aeId + tempDataset.datasetID,
																									tempDataset.rx_appeui,
																									tempDataset.rx_moteeui);
											if (conCode == 404) {
												while(true) {
													int aeCode = HttpClientRequest.subaeCreateRequest(ResourceRepository.getCSEInfo(),
																										ResourceRepository.getAEInfo().aeId + tempDataset.datasetID,
																										tempDataset.rx_appeui);
													if (aeCode == 201 || aeCode == 409) {
														break;
													}
													else {
														Thread.sleep(100);
													}
												}
											}
											
											int subUpConCode = HttpClientRequest.subContainerCreateRequest(ResourceRepository.getCSEInfo(),
																											ResourceRepository.getAEInfo().aeId + tempDataset.datasetID,
																											tempDataset.rx_appeui,
																											tempDataset.rx_moteeui,
																											"up");
																		
											int subDwConCode = HttpClientRequest.subContainerCreateRequest(ResourceRepository.getCSEInfo(),
																											ResourceRepository.getAEInfo().aeId + tempDataset.datasetID,
																											tempDataset.rx_appeui,
																											tempDataset.rx_moteeui,
																											"down");
											if (subUpConCode != 404 && subDwConCode != 404) {
												
												int subsConCode = 0;
												
												if (subsConCode != 201) {
													while(true) {
														HttpClientRequest.subscriptionDeleteRequest(ResourceRepository.getCSEInfo(),
																										ResourceRepository.getAEInfo().aeId + tempDataset.datasetID,
																										tempDataset.rx_appeui,
																										tempDataset.rx_moteeui + "/down");
														
														Thread.sleep(100);
														
														subsConCode = HttpClientRequest.subscriptionCreateRequest(ResourceRepository.getCSEInfo(),
																													ResourceRepository.getAEInfo().aeId,
																													ResourceRepository.getAEInfo().aeId + tempDataset.datasetID,
																													tempDataset.rx_appeui,
																													tempDataset.rx_moteeui + "/down");
														if (subsConCode == 201) {
															break;
														}
														else {
															Thread.sleep(100);
														}
													}
													break;
												}			
												
												break;
											}
										}
									}
									else {
										break;
									}
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								System.out.println("[oneM2MIPE] oneM2M HTTP request failed");
							}
							System.out.println("[oneM2MIPE] New device " + tempDataset.rx_moteeui + " is registered");
							break;
						}
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("[oneM2MIPE] JSON format error");
			}
			
			String response = "";
			t.sendResponseHeaders(200, response.length());
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
		
		if (t.getRequestMethod().equals("GET")) {
			System.out.println("[oneM2MIPE] Receive a HTTP GET request");

			ArrayList<JSONObject> tempLists = ResourceRepository.getKerlinkDlDatasets();
			
			if (tempLists.size() > 0) {
				JSONArray tempArray = new JSONArray();
				
				for (int i = 0; i < tempLists.size(); i++) {
					tempArray.put(tempLists.get(i));
				}
				
				String response = tempArray.toString();
//				String response = tempLists.get(0).toString();
				
				System.out.println(response);
				t.sendResponseHeaders(200, response.length());
				OutputStream os = t.getResponseBody();
				os.write(response.getBytes());
				os.close();
				
				ResourceRepository.clearKerlinkDlDatasets();
			}
			else {
				String response = "";
				t.sendResponseHeaders(200, response.length());
				OutputStream os = t.getResponseBody();
				os.write(response.getBytes());
				os.close();
			}
		}
	}
}