package de.twt.client.modbus.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

public class ModbusReadRequestDTO implements Serializable {
	private static final long serialVersionUID = 3436469316836838408L;
	private final String id = UUID.randomUUID().toString();
	private HashMap<Integer, Integer> coilsAddressMap = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> discreteInputsAddressMap = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> holdingRegistersAddressMap = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> inputRegistersAddressMap = new HashMap<Integer, Integer>();
	
	public String getID() {
		return id;
	}
	
	public HashMap<Integer, Integer> getCoilsAddressMap() {
		return coilsAddressMap;
	}
	public void setCoilsAddressMap(HashMap<Integer, Integer> coilsAddressMap) {
		this.coilsAddressMap = coilsAddressMap;
	}
	public HashMap<Integer, Integer> getDiscreteInputsAddressMap() {
		return discreteInputsAddressMap;
	}
	public void setDiscreteInputsAddressMap(
			HashMap<Integer, Integer> discreteInputsAddressMap) {
		this.discreteInputsAddressMap = discreteInputsAddressMap;
	}
	public HashMap<Integer, Integer> getHoldingRegistersAddressMap() {
		return holdingRegistersAddressMap;
	}
	public void setHoldingRegistersAddressMap(
			HashMap<Integer, Integer> holdingRegistersAddressMap) {
		this.holdingRegistersAddressMap = holdingRegistersAddressMap;
	}
	public HashMap<Integer, Integer> getInputRegistersAddressMap() {
		return inputRegistersAddressMap;
	}
	public void setInputRegistersAddressMap(
			HashMap<Integer, Integer> inputRegistersAddressMap) {
		this.inputRegistersAddressMap = inputRegistersAddressMap;
	}
	
	
}
