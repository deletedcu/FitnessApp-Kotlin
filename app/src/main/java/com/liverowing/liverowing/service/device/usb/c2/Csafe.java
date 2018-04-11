package com.liverowing.liverowing.service.device.usb.c2;

import android.util.Pair;
import android.util.Log;

public class Csafe {

	private static final int MAX_BUFFER = 128;

	public static byte checksum(byte[] data){
		byte checksum = 0x00;
		for(byte b : data){
			checksum ^= b;
		}
		return checksum;
	}

	/**
	 *
	 * @param data the data to stuff
	 * @return the stuffed data of len MAX_BUFFER-1 or null on error
	 */
	public static byte[] stuff(byte[] data){
		byte[] newData = new byte[MAX_BUFFER-4];//max size
		int idx = 0;
		for(byte b : data){
			if((b & 0xFC) == 0xF0){
				newData[idx] = (byte) 0xF3;
				newData[idx+1] = (byte) (0x03 & b);
				idx+=2;
			}else{
				newData[idx] = b;
				idx++;
			}
			if(idx >= newData.length){
				logcate("too much data to stuff");
				return null;
			}
		}
		byte[] stuffed = new byte[idx];
		System.arraycopy(newData, 0, stuffed, 0, idx);
		return stuffed;
	}

	/**
	 *
	 * @param data the stuffed csafe frame contents
	 * @param len length of data
	 * @param chk the checksum of the unstuffed frame contents
	 * @return the csafe backet wraped with checksum and start/stop or null on failure
	 */

	//TEST 01 F1 94 94 F2
	public static byte[] create(byte reportID, byte[] data, int len, byte chk){
		if(data.length < len){
			logcate("data.length is smaller than len");
			return null;
		}
		if(len >= MAX_BUFFER-4){
			logcatd("Data too long! Max "+(MAX_BUFFER-4)+"!");
			return null;
		}

		byte[] newData = new byte[MAX_BUFFER-1];//max 63
		newData[0] = reportID;
		newData[1] = (byte) 0xF1;
		for(int i = 0; i < len; i++){
			newData[i+2] = data[i];
		}
		newData[len+2] = chk;
		newData[len+3] = (byte) 0xF2;
		return newData;
	}



	public static byte[] extract(byte[] frame){
		byte[] newData = new byte[MAX_BUFFER];
		int idx = 0;
		boolean start = false;
		for(int i = 0; i < frame.length; i++){
			if(start){
				if(i+1 < frame.length){
					if((frame[i+1]&0xFF) == 0xF2){
						newData[idx++] = frame[i];
						logcatd("breaking at pos "+i);
						break;
					}else{
						newData[idx++] = frame[i];
					}
				}else{
					//L.e("COULD NOT PARSE CSAFE PACKET " + toString(frame));
				}
			}
			if((frame[i]&0xFF) == 0xF1){
				start = true;
			}
		}
		if(!start)
			return null;
		byte[] strip = new byte[idx];
		System.arraycopy(newData, 0, strip, 0, idx);
		return strip;
	}



	public static Pair<byte[], Byte> destuff(byte[] data){
		byte[] newData = new byte[MAX_BUFFER];
		int idx = 0;
		for(int i = 0; i < data.length; i++){
			if(data[i] == (byte)0xF3){                                  //stuffed data
				i++;
				newData[idx] = (byte) (0xF0 | data[i]);
				idx++;
			}
			else{
				newData[idx] = data[i];
				idx++;
			}
		}

		byte[] destuffed = new byte[idx];
		System.arraycopy(newData, 0, destuffed, 0, idx);

		byte check = destuffed[destuffed.length-1];
		byte[] returnData = new byte[destuffed.length-1];
		System.arraycopy(destuffed, 0, returnData, 0, destuffed.length-1);
		return new Pair<byte[], Byte>(returnData, new Byte(check));
	}


	/**
	 *
	 * @param chk checksum
	 * @param stuffedData the stuffed data
	 * @return the destuffed data or null if verification failed
	 */
	public static boolean verify(byte[] data , Byte check){
		byte localCheck = 0;
		for(byte b : data) localCheck ^= b;

		if(localCheck == check) return true;
		else return false;
	}



	public static String toString(byte[] bytes) {
		String byteString = "";
		for (byte b : bytes) {
			byteString += String.format("%02X ", b);
			if (b== (byte)0xf2) break;
		}
		return byteString;
	}




	public static void logcatd(Object o){
//				Log.d("Csafe", String.valueOf(o));
	}
	public static void logcatd(String s, Object ... args){
//				Log.d("Csafe", String.format(s,args));
	}

	public static void logcate(Object o){
		Log.e("Csafe", String.valueOf(o));
	}



}

