package com.hdbsnc.smartiot.adapter.mb.mc.bin.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.BitSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

public class EditUtil {

	
//	/**
//	 * 10진수 숫자형 문자열의 선두에 제로'0' 채우기
//	 * 
//	 * @param num
//	 * @param size
//	 * @return
//	 */
//
//	
//	16진수가 들어올 경우 처리가되지 않아 변경함.
//	public static String fillZeroDecimal(String num, int size) {
//		int iNum = DatatypeConverter.parseInt(num);		
//		return String.format("%0" + size + "d", iNum);
//	}
//	
	/**
	 * 16진수 숫자형 문자열의 선두에 '0' 채우기
	 * @param num
	 * @param size
	 * @return
	 */
	public static String fillZero(String num, int size) {
		StringBuilder sb = new StringBuilder();
		if (num.length() != size) {
			for (int i = 0; i < size - num.length(); i++) {
				sb.append("0");
			}
		}
		sb.append(num);
		return sb.toString();
	}


	/**
	 * HEX문자열을 바이트 배열로 변환한다.
	 * 
	 * @param hexStr
	 * @return
	 */
	public static byte[] hexStrToBytes(String hexStr) {
		return DatatypeConverter.parseHexBinary(hexStr);
	}

	/**
	 * 바이트 배열을 HEX 문자열로 변환한다.
	 * 
	 * @param buf
	 * @return
	 */
	// public static String byteToHexStr(byte buf[]) {
	// StringBuilder sb = new StringBuilder();
	// for (int i = 0; i < buf.length; i++){
	// sb.append(String.format("%02X ", buf[i]));
	// }
	// return sb.toString();
	// }
	public static String bytesToHexStr(byte[] bVal) {
		return DatatypeConverter.printHexBinary(bVal);
	}

	/**
	 * 6자리 에디안 변환(WORD단위의 BYTE 순서 변환)
	 * 
	 * @param deviceNum
	 * @return byte 배열
	 * @throws Exception
	 */
	public static byte[] hexStr6ToBigEndianBytes(String val) throws Exception {
		if (val.length() % 6 != 0) {
			throw new Exception("6자리의 에디안만 변경가능 합니다.");
		}

		StringBuilder preSb = new StringBuilder();
		String temp;
		for (int i = 0, s = val.length(); i < s; i = i + 6) {
			temp = val.substring(i, i + 6);
			preSb.append(temp.substring(4, 6));
			preSb.append(temp.substring(2, 4));
			preSb.append(temp.substring(0, 2));
		}
		return DatatypeConverter.parseHexBinary(preSb.toString());
	}

	/**
	 * 4자리 에디안 변환(WORD단위의 BYTE 순서 변환)
	 * 
	 * @param src
	 * @return
	 * @throws Exception
	 */
	public static byte[] hexStr4ToBigEndianBytes(String val) throws Exception {
		if (val.length() % 4 != 0) {
			throw new Exception("4자리의 에디안만 변경가능 합니다.");
		}

		byte[] src = hexStrToBytes(val);
		byte[] result = new byte[src.length];

		for (int i = 0; i < src.length / 2; i++) {
			result[i * 2] = src[i * 2 + 1];
			result[i * 2 + 1] = src[i * 2];
		}

		return result;
	}
	
	/**
	 * 4자리 에디안 변환(WORD단위의 BYTE 순서 변환)
	 * 
	 * @param src
	 * @return
	 * @throws Exception
	 */
	public static byte[] byte4ToBigEndianBytes(byte[] bVal) throws Exception {

		byte[] result = new byte[bVal.length];
		if (bVal.length %2 != 0) {
			throw new Exception("4자리의 에디안만 변경가능 합니다.");
		}

		for (int i = 0; i < bVal.length / 2; i++) {
			result[i * 2] = bVal[i * 2 + 1];
			result[i * 2 + 1] = bVal[i * 2];
		}
		
		return result;
//		byte[] result = new byte[bVal.length];
//		if (bVal.length % 2 != 0) {
//			throw new Exception();
//		}
//
//		for (int i = 0; i < bVal.length / 2; i++) {
//			result[i * 2] = bVal[i * 2 + 1];
//			result[i * 2 + 1] = bVal[i * 2];
//		}
//
//		return result;
	}
	
	/**
	 * 8자리 에디안 변환(WORD단위의 BYTE 순서 변환) 
	 * 
	 * @param src
	 * @return
	 * @throws Exception
	 */
	public static byte[] word8ToBigEndianBytes(byte[] bVal) throws Exception {

		byte[] result = new byte[bVal.length];

		int length;
		//바이트가 짝수인 경우 
		if (bVal.length % 4 == 0) {
			length = bVal.length / 4; 
		}else{
			// 바이트가 홀수인 경우
			length = (bVal.length / 4) - 1;
		}
		
		for (int i = 0; i < length; i++) {
			result[i * 4] = bVal[i * 4 + 3];
			result[i * 4 + 1] = bVal[i * 4 + 2];
			result[i * 4 + 2] = bVal[i * 4 + 1];
			result[i * 4 + 3] = bVal[i * 4 + 0];
		}
		
		return result;
	}

	/**
	 * 바이트 배열 내부의 null(0x00)을 대체값으로 변환
	 * 
	 * @param src
	 * @param replace
	 * @return
	 */
	public static byte[] removeNull(byte[] src, byte replace) {
		for (int i = 0; i < src.length; i++) {
			if (src[i] == 0x00)
				src[i] = replace;
		}

		return src;
	}
	

	/**
	 * 16진문자열을 10진 문자열로 변환
	 * 
	 * @param val
	 * @return
	 */
	public static String hexStrToDecStr(String val) {
		return String.valueOf(Long.parseLong(val, 16));
	}


	/**
	 * 10진수 문자열을 16진수 문자열로 변환
	 * 
	 * @param val
	 * @return
	 */
	public static String decStrToHexStr(String val) {

		Long intDec = Long.parseLong(val);
		return Long.toHexString(intDec).toUpperCase();
	}

	/**
	 * 바이트 배열 데이터를 숫자형으로 변환한다. int형(4바이트)초과의 데이터는 제외하고 계산한다.
	 * 
	 * @param bVal
	 * @return
	 */
	public static String bytesToIntStr(byte[] bVal, String split) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < bVal.length / 4; i++) {
			if (sb.length() > 0)
				sb.append(split);
			int int32 = (int) ((bVal[i * 2] & 0xff)<<24 | (bVal[i * 2+1] & 0xff)<<16 | ((bVal[i * 2+2] & 0xFF) << 8) | (bVal[i * 2 + 3] & 0xFF));
			sb.append(String.valueOf(int32));
		}

		return sb.toString();
	}

	/**
	 * 바이트 배열 데이터를 숫자형으로 변환한다. Short형(2바이트)초과의 데이터는 제외하고 계산한다.
	 * 
	 * @param bVal
	 * @param split
	 * @return
	 */
	public static String bytesToShortStr(byte[] bVal, String split) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < bVal.length / 2; i++) {
			if (sb.length() > 0)
				sb.append(split);
			int int16 = (int) (((bVal[i * 2] & 0xFF) << 8) | (bVal[i * 2 + 1] & 0xFF));
			sb.append(String.valueOf(int16));
		}
		return sb.toString();
	}

	/**
	 * 바이트 배열을 멜섹에서 사용하는 바이너리 문자열로 변환한다.
	 * 
	 * @param bVal
	 * @param split
	 * @return
	 */
	public static String bytesToMelsecBinStr(byte[] bVal, String split) {

		StringBuffer sb = new StringBuffer();
		BitSet bitset = BitSet.valueOf(bVal);
		
		for(int i=0; i<bVal.length*8; i++){
			if (sb.length() > 0)
				sb.append(split);
			
			if (bitset.get(i) == false) {
				sb.append("0");
			} else {
				sb.append("1");
			}
		}
		
		return sb.toString();
	}

	/**
	 * 2byte 배열에 해당 index자리의 BIT 값을 구한다.
	 * 
	 * @param bVal
	 *            %반드시 2바이트 배열
	 * @return
	 * @throws Exception
	 */
	public static String getBitStrFromHexStr(byte[] bVal, int idex) throws Exception {

		if (bVal.length != 2)
			throw new Exception("byte배열 length 오류.");
		char[] chars = new char[16];

		for (int i = 0; i < 8; i++) {
			chars[i] = (((bVal[1] >>> (i)) & 0x01) == 0x01) ? '1' : '0';
		}

		for (int i = 0; i < 8; i++) {
			chars[i + 8] = (((bVal[0] >>> (i)) & 0x01) == 0x01) ? '1' : '0';
		}

		return String.valueOf(chars[idex]);
	}

	/**
	 * PLC로 부터 받은 데이터를 포멧형식에 따라 변환를 한다.
	 * 
	 * @param data
	 * @param formatter
	 *            ex) "0:1:BINARY" "1:3:BIT"
	 * @return
	 * @throws Exception
	 */
	public static Pattern p1 = Pattern.compile("(.*):(.*):(BINARY|BIT|ASCII|UINT|SHORT|HEX)");

	public static String parserRecvData(String data, String formatter) throws Exception {
		Matcher m1 = p1.matcher(formatter);
		if (!m1.find()) {
			throw new ParseException(formatter, 0);
		}

		// index, length
		int first = Integer.parseInt(m1.group(1));
		int second = Integer.parseInt(m1.group(2));
		String type = m1.group(3);

		String result = null;
		String pData = null;
		byte[] bytes = null;
		switch (type) {
		case "BINARY":
			// 대상(범위) 데이터 추출
			pData = data.substring(first * 4, first * 4 + 4 * second);
			bytes = hexStrToBytes(pData);
			result = bytesToMelsecBinStr(bytes, "");
			break;
		case "BIT":
			// 대상(범위) 데이터 추출
			pData = data.substring(first * 4, first * 4 + 4);
			bytes = hexStr4ToBigEndianBytes(pData);
			result = getBitStrFromHexStr(bytes, second);
			break;
		case "UINT":
			// 대상(범위) 데이터 추출
			pData = data.substring(first * 4, first * 4 + 4 * second);
			bytes = word8ToBigEndianBytes(hexStrToBytes(pData));
			result = bytesToIntStr(bytes, " ");
			break;
		case "SHORT":
			// 대상(범위) 데이터 추출
			pData = data.substring(first * 4, first * 4 + 4 * second);
			bytes = hexStr4ToBigEndianBytes(pData);
			result = bytesToShortStr(bytes, " ");
			break;
		case "ASCII":
			// 대상(범위) 데이터 추출
			pData = data.substring(first * 4, first * 4 + 4 * second);
			bytes = hexStrToBytes(pData);
			// 바이트 배열에 0x00(null)이 존재하는 경우 제거후 아스키 문자열로 변환
			result = (new String(removeNull(bytes, (byte) 0x20))).replace(" ", "");
			break;
		case "HEX":
			pData = data.substring(first * 4, first * 4 + 4 * second);
			bytes = hexStr4ToBigEndianBytes(pData);
			result = bytesToHexStr(bytes);
			break;
		}
		return result;
	}
	
	/**
	 * short를 바이트 배열로 변환한다.
	 * 
	 * @param buf
	 * @return
	 * @throws Exception
	 */
	public static byte[] shortToBytes(short value){
		return new byte[] {  (byte) ((value & 0xFF00) >> 8) , (byte) (value & 0x00FF)};
	}

	/**
	 * 바이트를 short 배열로 변환한다.
	 * 
	 * @param buf
	 * @return
	 * @throws Exception
	 */
	public static short bytesToShort(byte[] bVal){
		return (short)(((bVal[0] & 0xFF) << 8) | (bVal[1] & 0xFF));
	}
	
	/**
	 * 바이트 배열을 하나의 바이트 배열로 만들어 준다.
	 * @param args
	 * @return
	 * @throws IOException 
	 */
	public static byte[] byteCopy(byte[]... args) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		for (byte[] bytes : args) {
			try {
				baos.write(bytes);
			} catch (IOException e) {
				throw e;
			}
		}
		return baos.toByteArray();
	}

	/**
	 * @param bVal
	 * Binary 바이트배열 데이터를  ASCII로 변환하기 위해 사용됨
	 * @return
	 */
	public static String asciiToBin(byte[] bVal){
		StringBuilder sb =new StringBuilder();

		for(int i=bVal.length-1; i>=0; i--)
			sb.append(asciiToBin(bVal[i]));
		
		return sb.toString();
	}

	/**
	 * @param bVal
	 * Binary 바이트 데이터를  ASCII로 변환하기 위해 사용됨 
	 * @return
	 */
	public static String asciiToBin(byte bVal){

		StringBuilder sb =new StringBuilder();
		
		if(bVal<10)
			sb.append("0"+String.valueOf((short) (bVal & 0xFF)));
		else if(bVal<100)
			sb.append(String.valueOf((short) (bVal & 0xFF)));
		else
			sb.append(("00").getBytes());			
		
		return sb.toString();
	}

	
	
	
}
