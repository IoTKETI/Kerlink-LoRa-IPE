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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import kr.re.keti.loraipe.httpclient.HttpClientRequest;
import kr.re.keti.loraipe.mqttclient.MqttClientKeti;
import kr.re.keti.loraipe.mqttclient.MqttClientKetiPub;
import kr.re.keti.loraipe.resource.AE;
import kr.re.keti.loraipe.resource.CSEBase;
import kr.re.keti.loraipe.resource.ResourceRepository;

/**
 * Class for registration about AE and container resources
 * @author NakMyoung Sung (nmsung@keti.re.kr)
 */
public class Registration {
	
	private CSEBase cse;
	private AE ae;
	
	private boolean aeCreate = false;
	
	public Registration() {
		this.cse = ResourceRepository.getCSEInfo();
		this.ae = ResourceRepository.getAEInfo();
	}

	/**
	 * registrationStart Method
	 * Start the registration procedure for AE and containers
	 * @throws Exception
	 */
	public void registrationStart() {
		System.out.println("[oneM2MIPE] oneM2MIPE registration start.......\n");
		
		try {
			BufferedReader in = new BufferedReader(new FileReader("AE_ID.back"));
			try {
				ae.aeId = in.readLine();
				
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("[oneM2MIPE] AE_ID value not found");
				try {
					in.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					System.out.println("[oneM2MIPE] AE_ID file close failed");
				}
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			System.out.println("[oneM2MIPE] Backup AE_ID file not found");
		}
		
		while (!aeCreate) {
			try {
				int response = HttpClientRequest.aeCreateRequest(cse, ae);
				if (response == 201) {
					aeCreate = true;

					File file = new File("AE_ID.back");
					FileWriter fw = new FileWriter(file, false);
					
					fw.write(ae.aeId);
					fw.flush();
					
					fw.close();
				}
				else if (response == 409) {
					response = HttpClientRequest.aeRetrieveRequest(cse, ae);
					
					if (response == 200) {
						aeCreate = true;
						
						File file = new File("AE_ID.back");
						FileWriter fw = new FileWriter(file, false);
						
						fw.write(ae.aeId);
						fw.flush();
						
						fw.close();
					}
					else {
						Thread.sleep(3000);
					}
				}
				else {
					Thread.sleep(3000);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ResourceRepository.setAEInfo(ae);
		
		LoRaIPEMain.requestClient = new MqttClientKeti("tcp://" + cse.CSEHostAddress, ae.aeId);
		LoRaIPEMain.responseClient = new MqttClientKeti("tcp://" + cse.CSEHostAddress);
		LoRaIPEMain.publishClient = new MqttClientKetiPub("tcp://" + cse.CSEHostAddress, ae.aeId);
		
		LoRaIPEMain.requestClient.subscribe("/oneM2M/req/+/" + ae.aeId + "/+");
		LoRaIPEMain.responseClient.subscribe("/oneM2M/resp/" + ae.aeId + "/+");
		
		System.out.println("[oneM2MIPE] oneM2MIPE registration complete.......\n");
	}
}