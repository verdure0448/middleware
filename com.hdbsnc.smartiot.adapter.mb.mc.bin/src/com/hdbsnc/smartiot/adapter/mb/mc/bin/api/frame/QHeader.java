package com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame;

import com.hdbsnc.smartiot.adapter.mb.mc.bin.api.frame.AbstractBlocksFrame.TransMode;
import com.hdbsnc.smartiot.adapter.mb.mc.bin.util.EditUtil;

public class QHeader {

	private byte[] subHeader;
	private byte[] networkNo;
	private byte[] plcNo;
	private byte[] ioNo;
	private byte[] reqModuleNo;

	// 길이는 수정해야할 대상 -> 자동으로 해줌
	private byte[] reqDataLength;

	// 바뀌지 않아도 됨 plc Cpu 타임아웃 시간 설정
	private byte[] cpuTimer;


	public QHeader() {
		this.subHeader = new byte[] { 0x50, 0x00 };
		this.networkNo = new byte[] { 0x00 };
		this.plcNo = new byte[] { (byte) 0xFF };
		this.ioNo = new byte[] {  0x03,(byte) 0xFF };
		this.reqModuleNo = new byte[] { 0x00 };
		this.reqDataLength = new byte[] {0x00, 0x00}; // 초기 사이즈 0으로 설정
		this.cpuTimer = new byte[] { 0x00, 0x10 };

	}

	public byte[] getByte(TransMode mode, short frameLength) throws Exception {
		byte[] bSubHead = null;
		byte[] bNetworkNo = null;
		byte[] bPlcNo = null;
		byte[] bIoNo = null;
		byte[] bReqModuleNo = null;
		byte[] bReqDataLength = null;
		byte[] bCpuTimer = null;
		
		if (TransMode.BINARY == mode) {
			// CPU TIMER 길이 더하기
			frameLength += 2;
			
			bSubHead = this.subHeader;
			bNetworkNo = this.networkNo;
			bPlcNo = this.plcNo;
			bIoNo = EditUtil.byte4ToBigEndianBytes(this.ioNo);
			bReqModuleNo = this.reqModuleNo;
			bReqDataLength = EditUtil.byte4ToBigEndianBytes(EditUtil.shortToBytes(frameLength));
			bCpuTimer = EditUtil.byte4ToBigEndianBytes(this.cpuTimer);
		} else if (TransMode.ASCII == mode) {
			// CPU TIMER 길이 더하기
			frameLength += 4;
			
			bSubHead = EditUtil.bytesToHexStr(this.subHeader).getBytes();
			bNetworkNo = EditUtil.bytesToHexStr(this.networkNo).getBytes();
			bPlcNo = EditUtil.bytesToHexStr(this.plcNo).getBytes();
			bIoNo = EditUtil.bytesToHexStr(this.ioNo).getBytes();
			bReqModuleNo = EditUtil.bytesToHexStr(this.reqModuleNo).getBytes();
			
			bReqDataLength = EditUtil.bytesToHexStr(EditUtil.shortToBytes(frameLength)).getBytes();
			
			bCpuTimer = EditUtil.bytesToHexStr(this.cpuTimer).getBytes();
		}
		
		this.reqDataLength = bReqDataLength;
		
		return EditUtil.byteCopy(bSubHead, bNetworkNo, bPlcNo, bIoNo, bReqModuleNo, bReqDataLength, bCpuTimer);
	}

	public byte[] getSubHeader() {
		return subHeader;
	}

	public byte[] getNetworkNo() {
		return networkNo;
	}

	public byte[] getPlcNo() {
		return plcNo;
	}

	public byte[] getIoNo() {
		return ioNo;
	}

	public byte[] getReqModuleNo() {
		return reqModuleNo;
	}

	public byte[] getReqDataLength() {
		return reqDataLength;
	}

	public byte[] getCpuTimer() {
		return cpuTimer;
	}
}
