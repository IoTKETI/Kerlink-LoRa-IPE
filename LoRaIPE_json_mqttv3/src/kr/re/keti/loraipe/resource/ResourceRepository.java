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

package kr.re.keti.loraipe.resource;

import java.util.ArrayList;

import org.json.JSONObject;

/**
 * Data class for storing the oneM2M resource.
 * @author NakMyoung Sung (nmsung@keti.re.kr)
 */
public class ResourceRepository {
	
	private static CSEBase cse = new CSEBase();
	private static AE ae = new AE();
	private static ArrayList<AE> aes = new ArrayList<AE>();
	private static ArrayList<Container> containers = new ArrayList<Container>();
	private static ArrayList<Subscription> subscriptions = new ArrayList<Subscription>();
	private static ArrayList<Kerlink> datasets = new ArrayList<Kerlink>();
	private static ArrayList<JSONObject> dldatasets = new ArrayList<JSONObject>();
	private static long txmsgid = 0;
	
	public static CSEBase getCSEInfo() {
		return cse;
	}
	
	public static AE getAEInfo() {
		return ae;
	}
	
	public static ArrayList<AE> getKerlinkAEInfo() {
		return aes;
	}
	
	public static ArrayList<Container> getContainersInfo() {
		return containers;
	}
	
	public static ArrayList<Subscription> getSubscriptionInfo() {
		return subscriptions;
	}
	
	public static ArrayList<Kerlink> getKerlinkDatasets() {
		return datasets;
	}
	
	public static ArrayList<JSONObject> getKerlinkDlDatasets() {
		return dldatasets;
	}
	
	public static long gettxmsgid() {
		return txmsgid;
	}
	
	public static void setCSEBaseInfo(CSEBase cseInfo) {
		cse = cseInfo;
	}
	
	public static void setAEInfo(AE aeInfo) {
		ae = aeInfo;
	}
	
	public static void setKerlinkAEInfo(ArrayList<AE> aesInfo) {
		aes = aesInfo;
	}
	
	public static void setContainersInfo(ArrayList<Container> containersInfo) {
		containers = containersInfo;
	}
	
	public static void setSubscriptionsInfo(ArrayList<Subscription> subscriptionsInfo) {
		subscriptions = subscriptionsInfo;
	}
	
	public static void setKerlinkDatasets(ArrayList<Kerlink> datasetsInfo) {
		datasets = datasetsInfo;
	}
	
	public static void setKerlinkDlDatasets(JSONObject dldatasetsInfo) {
		dldatasets.add(dldatasetsInfo);
	}
	
	public static void clearKerlinkDlDatasets() {
		dldatasets.clear();
	}
	
	public static void settxmsgid(long id) {
		txmsgid = id;
	}
}