package de.twt.client.modbus.common;

import java.io.Serializable;
import java.util.HashMap;

public class ModbusWriteRequestDTO implements Serializable {
	private static final long serialVersionUID = -8476623473551110412L;
	private int address;
	private int quantity;
	private int[] holdingRegisters = new int[0];
	private boolean[] coils = new boolean[0];
	
	public int getAddress() {
		return address;
	}

	public int getQuantity() {
		return quantity;
	}
	
	public void setCoil(int address, boolean value){
		this.address = address;
		quantity = 1;
		coils = new boolean[1];
		coils[0] = value;
	}

	public void setCoils(int address, int quantity, boolean[] values){
		this.address = address;
		this.quantity = quantity;
		coils = values;
	}
	
	public boolean[] getCoils(){
		return coils;
	}
	
	public void setHoldingRegister(int address, int value){
		this.address = address;
		quantity = 1;
		holdingRegisters = new int[1];
		holdingRegisters[0] = value;
	}
	
	public void setHoldingRegisters(int address, int quantity, int[] values){
		this.address = address;
		this.quantity = quantity;
		holdingRegisters = values;
	}
	
	public int[] getHoldingRegisters(){
		return holdingRegisters;
	}
	
}
