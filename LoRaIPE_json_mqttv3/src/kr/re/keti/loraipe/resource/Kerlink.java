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

/**
 * Data class for oneM2M AE resource.
 * @author NakMyoung Sung (nmsung@keti.re.kr)
 */
public class Kerlink {
	
	public boolean	set_sub = false;
	
	public String 	rx_moteeui = "";
	public String 	rx_appeui = "";
	public int 		rx_user_seqno = -1;
	public int 		rx_user_port = -1;
	public String 	rx_user_payload = "";
	public int 		rx_user_motetx_freq = -1;
	public String 	rx_user_motetx_modu = "";
	public String	rx_user_motetx_datr = "";
	public String 	rx_user_motetx_codr = "";
	
	public String	gwrx_time = "";
	public int		gwrx_chan = -1;
	public int		gwrx_rfch = -1;
	public int		gwrx_rssi = -1;
	public float	gwrx_lsnr = -1;
	
	public int		datasetID = -1;
}