package de.twt.client.modbus.common;

import java.util.HashMap;

public class ModbusData {
	private final HashMap<Integer, Boolean> coils = new HashMap<Integer, Boolean>();
	private final HashMap<Integer, Boolean> discreteInputs = new HashMap<Integer, Boolean>();
	private final HashMap<Integer, Integer> holdingRegisters = new HashMap<Integer, Integer>();
	private final HashMap<Integer, Integer> inputRegisters = new HashMap<Integer, Integer>();
	
	public void setCoil(int address, boolean value){
		coils.put(address, value);
	}
	
	public void setCoils(int address, boolean[] values){
		int index = address;
		for(boolean value : values)
			coils.put(index++, value);
	}
	
	public void setCoils(HashMap<Integer, Boolean> coils){
		this.coils.putAll(coils);
	}
	
	public HashMap<Integer, Boolean> getCoils(){
		return coils;
	}
	
	public void setDiscreteInput(int address, boolean value){
		discreteInputs.put(address, value);
	}
	
	public void setDiscreteInputs(int address, boolean[] values){
		int index = address;
		for(boolean value : values)
			discreteInputs.put(index++, value);
	}
	
	public void setDiscreteInputs(HashMap<Integer, Boolean> discreteInputs){
		this.discreteInputs.putAll(discreteInputs);
	}
	
	public HashMap<Integer, Boolean> getDiscreteInputs(){
		return discreteInputs;
	}
	
	public void setHoldingRegister(int address, int value){
		holdingRegisters.put(address, value);
	}
	
	public void setHoldingRegisters(int address, int[] values){
		int index = address;
		for(int value : values)
			holdingRegisters.put(index++, value);
	}
	
	public void setHoldingRegisters(HashMap<Integer, Integer> holdingRegisters){
		this.holdingRegisters.putAll(holdingRegisters);
	}
	
	public HashMap<Integer, Integer> getHoldingRegisters(){
		return holdingRegisters;
	}
	
	public void setInputRegister(int address, int value){
		inputRegisters.put(address, value);
	}
	
	public void setInputRegisters(int address, int[] values){
		int index = address;
		for(int value : values)
			inputRegisters.put(index++, value);
	}
	
	public void setInputRegisters(HashMap<Integer, Integer> inputRegisters){
		this.inputRegisters.putAll(inputRegisters);
	}
	
	public HashMap<Integer, Integer> getInputRegisters(){
		return inputRegisters;
	}
}
