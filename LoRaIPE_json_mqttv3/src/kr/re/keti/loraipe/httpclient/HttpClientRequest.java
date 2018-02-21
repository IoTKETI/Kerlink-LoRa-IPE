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

package kr.re.keti.loraipe.httpclient;

import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import kr.re.keti.loraipe.resource.AE;
import kr.re.keti.loraipe.resource.CSEBase;

/**
 * Class for sending the HTTP request to Mobius 
 * @author NakMyoung Sung (nmsung@keti.re.kr)
 *
 */
public class HttpClientRequest {
	
	private static int requestId = 0;
	
	/**
	 * aeCreateRequest Method
	 * Send to Mobius for oneM2M AE resource create
	 * @param cse
	 * @param ae
	 * @return
	 * @throws Exception
	 */
	public static int aeCreateRequest(CSEBase cse, AE ae) throws Exception {
		
		System.out.println("[oneM2MIPE] AE \"" + ae.appName + "\" create request.......");
		
		String requestBody = "";
		JSONObject headerObject = new JSONObject();
		
		JSONArray multipleLbl = new JSONArray();
		multipleLbl.put(ae.label);

		JSONArray multiplePoa = new JSONArray();
				multiplePoa.put(ae.pointOfAccess);
		
		JSONObject bodyObject = new JSONObject();
				bodyObject.put("rn", ae.appName);
				bodyObject.put("api", ae.appId);
				bodyObject.put("lbl", multipleLbl);
				bodyObject.put("rr", true);
				bodyObject.put("poa", multiplePoa);
		
		headerObject.put("m2m:ae", bodyObject);
				
		requestBody = headerObject.toString();

		StringEntity entity = new StringEntity(
						new String(requestBody.getBytes()));
		
		URI uri = new URIBuilder()
				.setScheme("http")
				.setHost(cse.CSEHostAddress + ":" + cse.CSEPort)
				.setPath("/" + cse.CSEName)
				.build();
		
		HttpPost post = new HttpPost(uri);
				post.setHeader("Content-Type", "application/vnd.onem2m-res+json;ty=2");
				post.setHeader("Accept", "application/json");
				post.setHeader("locale", "ko");
				post.setHeader("X-M2M-Origin", "S");
				post.setHeader("X-M2M-RI", Integer.toString(requestId++));
				post.setEntity(entity);
			
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		HttpResponse response = httpClient.execute(post);
		
		HttpEntity responseEntity = response.getEntity();
		
		String responseString = EntityUtils.toString(responseEntity);
		
		int responseCode = response.getStatusLine().getStatusCode();
		
		System.out.println("[oneM2MIPE] AE create HTTP Response Code : " + responseCode);
		System.out.println("[oneM2MIPE] AE create HTTP Response String : " + responseString);
		
		httpClient.close();
		
		String aei = HttpClientResponseParser.aeCreateParse(responseString);
		
		if (!aei.equals("")) {
			ae.aeId = aei;
		}
		
		return responseCode;
	}
	
	public static int aeRetrieveRequest(CSEBase cse, AE ae) throws Exception {
		
		System.out.println("[oneM2MIPE] AE \"" + ae.appName + "\" retrieve request.......");
				
		URI uri = new URIBuilder()
				.setScheme("http")
				.setHost(cse.CSEHostAddress + ":" + cse.CSEPort)
				.setPath("/" + cse.CSEName + "/" + ae.appName)
				.build();
		
		HttpGet get = new HttpGet(uri);
				get.setHeader("Content-Type", "application/vnd.onem2m-res+json;ty=2");
				get.setHeader("Accept", "application/json");
				get.setHeader("locale", "ko");
				get.setHeader("X-M2M-Origin", ae.aeId);
				get.setHeader("X-M2M-RI", Integer.toString(requestId++));
			
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		HttpResponse response = httpClient.execute(get);
		
		HttpEntity responseEntity = response.getEntity();
		
		String responseString = EntityUtils.toString(responseEntity);
		
		int responseCode = response.getStatusLine().getStatusCode();
		
		System.out.println("[oneM2MIPE] AE create HTTP Response Code : " + responseCode);
		System.out.println("[oneM2MIPE] AE create HTTP Response String : " + responseString);
		
		httpClient.close();
		
		String aei = HttpClientResponseParser.aeCreateParse(responseString);
		
		ae.aeId = aei;
		
		return responseCode;
	}
	
	public static int subaeCreateRequest(CSEBase cse, String originId, String aeName) throws Exception {
		
		System.out.println("[oneM2MIPE] AE \"" + aeName + "\" create request.......");
		
		String requestBody = "";
		JSONObject headerObject = new JSONObject();
		
		JSONArray multipleLbl = new JSONArray();
		multipleLbl.put("KETI");
		
		JSONObject bodyObject = new JSONObject();
				bodyObject.put("rn", aeName);
				bodyObject.put("api", aeName);
				bodyObject.put("rr", "true");
		
		headerObject.put("m2m:ae", bodyObject);
				
		requestBody = headerObject.toString();

		StringEntity entity = new StringEntity(
						new String(requestBody.getBytes()));
		
		URI uri = new URIBuilder()
				.setScheme("http")
				.setHost(cse.CSEHostAddress + ":" + cse.CSEPort)
				.setPath("/" + cse.CSEName)
				.build();
		
		HttpPost post = new HttpPost(uri);
				post.setHeader("Content-Type", "application/vnd.onem2m-res+json;ty=2");
				post.setHeader("Accept", "application/json");
				post.setHeader("locale", "ko");
				post.setHeader("X-M2M-Origin", originId);
				post.setHeader("X-M2M-RI", Integer.toString(requestId++));
				post.setEntity(entity);
			
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		HttpResponse response = httpClient.execute(post);
		
		HttpEntity responseEntity = response.getEntity();
		
		String responseString = EntityUtils.toString(responseEntity);
		
		int responseCode = response.getStatusLine().getStatusCode();
		
		System.out.println("[oneM2MIPE] AE create HTTP Response Code : " + responseCode);
		System.out.println("[oneM2MIPE] AE create HTTP Response String : " + responseString);
		
		httpClient.close();
		
//		String aei = HttpClientResponseParser.aeCreateParse(responseString);
//		
//		if (!aei.equals("")) {
//			ae.aeId = aei;
//		}
		
		return responseCode;
	}
	
	/**
	 * containerCreateRequest Method
	 * Send to Mobius for oneM2M container resource create
	 * @param cse
	 * @param ae
	 * @param container
	 * @return
	 * @throws Exception
	 */
	public static int containerCreateRequest(CSEBase cse, String originId, String aeName, String containerName) throws Exception {
		
		System.out.println("[oneM2MIPE] Container \"" + containerName + "\" create request.......");
		
		String requestBody = "";
		JSONObject headerObject = new JSONObject();
		
		JSONObject bodyObject = new JSONObject();
		bodyObject.put("rn", containerName);

		headerObject.put("m2m:cnt", bodyObject);
				
		requestBody = headerObject.toString();

		StringEntity entity = new StringEntity(
						new String(requestBody.getBytes()));
		
		URI uri = new URIBuilder()
				.setScheme("http")
				.setHost(cse.CSEHostAddress + ":" + cse.CSEPort)
				.setPath("/" + cse.CSEName + "/" + aeName)
				.build();
		
		HttpPost post = new HttpPost(uri);
				post.setHeader("Content-Type", "application/vnd.onem2m-res+json;ty=3");
				post.setHeader("Accept", "application/json");
				post.setHeader("locale", "ko");
				post.setHeader("X-M2M-Origin", originId);
				post.setHeader("X-M2M-RI", "nCubeThyme" + Integer.toString(requestId++));
				post.setHeader("X-M2M-NM", containerName);
				post.setEntity(entity);
			
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		HttpResponse response = httpClient.execute(post);
		
		HttpEntity responseEntity = response.getEntity();
		
		String responseString = EntityUtils.toString(responseEntity);
		
		int responseCode = response.getStatusLine().getStatusCode();
		
		System.out.println("[oneM2MIPE] Container create HTTP Response Code : " + responseCode);
		System.out.println("[oneM2MIPE] Container create HTTP Response String : " + responseString);
		
		httpClient.close();
		
		return responseCode;
	}
	
	/**
	 * containerCreateRequest Method
	 * Send to Mobius for oneM2M container resource create
	 * @param cse
	 * @param ae
	 * @param container
	 * @return
	 * @throws Exception
	 */
	public static int subContainerCreateRequest(CSEBase cse, String originId, String aeName, String containerName, String subContainerName) throws Exception {
		
		System.out.println("[oneM2MIPE] Container \"" + containerName + "\" create request.......");
		
		String requestBody = "";
		JSONObject headerObject = new JSONObject();
		
		JSONObject bodyObject = new JSONObject();
		bodyObject.put("rn", subContainerName);

		headerObject.put("m2m:cnt", bodyObject);
				
		requestBody = headerObject.toString();

		StringEntity entity = new StringEntity(
						new String(requestBody.getBytes()));
		
		URI uri = new URIBuilder()
				.setScheme("http")
				.setHost(cse.CSEHostAddress + ":" + cse.CSEPort)
				.setPath("/" + cse.CSEName + "/" + aeName + "/" + containerName)
				.build();
		
		HttpPost post = new HttpPost(uri);
				post.setHeader("Content-Type", "application/vnd.onem2m-res+json;ty=3");
				post.setHeader("Accept", "application/json");
				post.setHeader("locale", "ko");
				post.setHeader("X-M2M-Origin", originId);
				post.setHeader("X-M2M-RI", "nCubeThyme" + Integer.toString(requestId++));
				post.setHeader("X-M2M-NM", subContainerName);
				post.setEntity(entity);
			
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		HttpResponse response = httpClient.execute(post);
		
		HttpEntity responseEntity = response.getEntity();
		
		String responseString = EntityUtils.toString(responseEntity);
		
		int responseCode = response.getStatusLine().getStatusCode();
		
		System.out.println("[oneM2MIPE] Container create HTTP Response Code : " + responseCode);
		System.out.println("[oneM2MIPE] Container create HTTP Response String : " + responseString);
		
		httpClient.close();
		
		return responseCode;
	}
	
	/**
	 * subscriptionCreateRequest Method
	 * Send to Mobius for oneM2M subscription resource create
	 * @param cse
	 * @param ae
	 * @param container
	 * @return
	 * @throws Exception
	 */
	public static int subscriptionCreateRequest(CSEBase cse, String subId, String originId, String aeName, String containerName) throws Exception {
		
		System.out.println("[oneM2MIPE] Subscription \" def_sub \" create request.......");
		
		String requestBody = "";
		JSONObject headerObject = new JSONObject();
		
		JSONArray net = new JSONArray();
		net.put("3");
		
		JSONArray nu = new JSONArray();
				nu.put("mqtt://" + cse.CSEHostAddress + "/" + subId + "?ct=json");
				
		JSONObject enc = new JSONObject();
				enc.put("net", net);
		
		JSONObject bodyObject = new JSONObject();
				bodyObject.put("rn", "def_sub");
				bodyObject.put("enc", enc);
				bodyObject.put("nu", nu);
				bodyObject.put("nct", "2");
		
		headerObject.put("m2m:sub", bodyObject);
				
		requestBody = headerObject.toString();

		StringEntity entity = new StringEntity(
						new String(requestBody.getBytes()));
		
		URI uri = new URIBuilder()
				.setScheme("http")
				.setHost(cse.CSEHostAddress + ":" + cse.CSEPort)
				.setPath("/" + cse.CSEName + "/" + aeName + "/" + containerName)
				.build();
		
		HttpPost post = new HttpPost(uri);
				post.setHeader("Content-Type", "application/vnd.onem2m-res+json;ty=23");
				post.setHeader("Accept", "application/json");
				post.setHeader("locale", "ko");
				post.setHeader("X-M2M-Origin", originId);
				post.setHeader("X-M2M-RI", "nCubeThyme" + Integer.toString(requestId++));
				post.setEntity(entity);
			
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		HttpResponse response = httpClient.execute(post);
		
		HttpEntity responseEntity = response.getEntity();
		
		String responseString = EntityUtils.toString(responseEntity);
		
		int responseCode = response.getStatusLine().getStatusCode();
		
		System.out.println("[oneM2MIPE] Subscription create HTTP Response Code : " + responseCode);
		System.out.println("[oneM2MIPE] Subscription create HTTP Response String : " + responseString);
		
		httpClient.close();
		
		return responseCode;
	}
	
	/**
	 * subscriptionDeleteRequest Method
	 * Send to Mobius for delete the oneM2M subscription resource
	 * @param cse
	 * @param ae
	 * @param container
	 * @return
	 * @throws Exception
	 */
	public static int subscriptionDeleteRequest(CSEBase cse, String originId, String aeName, String containerName) throws Exception {
		
		System.out.println("[oneM2MIPE] Subscription \"def_sub\" delete request.......");
		
		URI uri = new URIBuilder()
				.setScheme("http")
				.setHost(cse.CSEHostAddress + ":" + cse.CSEPort)
				.setPath("/" + cse.CSEName + "/" + aeName + "/" + containerName + "/def_sub")
				.build();
		
		HttpDelete delete = new HttpDelete(uri);
				delete.setHeader("Accept", "application/json");
				delete.setHeader("locale", "ko");
				delete.setHeader("X-M2M-Origin", originId);
				delete.setHeader("X-M2M-RI", "nCubeThyme" + Integer.toString(requestId++));
			
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		HttpResponse response = httpClient.execute(delete);
		
		HttpEntity responseEntity = response.getEntity();
		
		String responseString = EntityUtils.toString(responseEntity);
		
		int responseCode = response.getStatusLine().getStatusCode();
		
		System.out.println("[oneM2MIPE] Subscription delete HTTP Response Code : " + responseCode);
		System.out.println("[oneM2MIPE] Subscription delete HTTP Response String : " + responseString);
		
		httpClient.close();
		
		return responseCode;
	}
	
	public static String subscriptionRetrieveMessage(CSEBase cse, String originId, String aeName, String containerName) throws Exception {
		
		URI uri = new URIBuilder()
				.setScheme("http")
				.setHost(cse.CSEHostAddress + ":" + cse.CSEPort)
				.setPath("/" + cse.CSEName + "/" + aeName + "/" + containerName + "/def_sub")
				.build();
		
		HttpGet get = new HttpGet(uri);
			get.setHeader("Accept", "application/json");
			get.setHeader("locale", "ko");
			get.setHeader("X-M2M-Origin", originId);
			get.setHeader("X-M2M-RI", Integer.toString(requestId));
			
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		HttpResponse response = httpClient.execute(get);
		
		HttpEntity responseEntity = response.getEntity();
		
		String responseString = EntityUtils.toString(responseEntity);
		
		int responseCode = response.getStatusLine().getStatusCode();
		
		System.out.println("HTTP Response Code : " + responseCode);
		System.out.println("HTTP Response String : " + responseString);
		
		httpClient.close();
		
		return responseString;
	}
	
	/**
	 * contentInstanceCreateRequest Method
	 * Send to Mobius for oneM2M contentInstance resource create
	 * @param cse
	 * @param ae
	 * @param container
	 * @param content
	 * @param contentInfo
	 * @return
	 * @throws Exception
	 */
	public static int contentInstanceCreateRequest(CSEBase cse, String originId, String aeName, String containerName, String content) throws Exception {
		
		System.out.println("[oneM2MIPE] \"" + containerName + "\"'s contentInstance create request.......");
		
		String requestBody = "";
		JSONObject headerObject = new JSONObject();
		
		JSONObject bodyObject = new JSONObject();
		bodyObject.put("con", content);

		headerObject.put("m2m:cin", bodyObject);
				
		requestBody = headerObject.toString();

		StringEntity entity = new StringEntity(
						new String(requestBody.getBytes()));
		
		URI uri = new URIBuilder()
				.setScheme("http")
				.setHost(cse.CSEHostAddress + ":" + cse.CSEPort)
				.setPath("/" + cse.CSEName + "/" + aeName + "/" + containerName)
				.build();
		
		HttpPost post = new HttpPost(uri);
				post.setHeader("Content-Type", "application/vnd.onem2m-res+json;ty=4");
				post.setHeader("Accept", "application/json");
				post.setHeader("locale", "ko");
				post.setHeader("X-M2M-Origin", originId);
				post.setHeader("X-M2M-RI", "loraipe" + Integer.toString(requestId++));
				post.setEntity(entity);
			
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		HttpResponse response = httpClient.execute(post);
		
		HttpEntity responseEntity = response.getEntity();
		
		String responseString = EntityUtils.toString(responseEntity);
		
		int responseCode = response.getStatusLine().getStatusCode();
		
		System.out.println("[oneM2MIPE] contentInstance create HTTP Response Code : " + responseCode);
		System.out.println("[oneM2MIPE] contentInstance create HTTP Response String : " + responseString);
		
		httpClient.close();
		
		return responseCode;
	}
}