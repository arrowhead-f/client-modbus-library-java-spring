package de.twt.client.modbus.master;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intelligt.modbus.jlibmodbus.Modbus;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusNumberException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusProtocolException;
import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import com.intelligt.modbus.jlibmodbus.master.ModbusMasterFactory;
import com.intelligt.modbus.jlibmodbus.tcp.TcpParameters;

import de.twt.client.modbus.common.ModbusReadRequestDTO;
import de.twt.client.modbus.common.ModbusWriteRequestDTO;
import de.twt.client.modbus.common.cache.ModbusDataCacheManager;
import de.twt.client.modbus.common.cache.ModbusReadRequestCacheManager;
import de.twt.client.modbus.common.cache.ModbusWriteRequestCacheManager;
import de.twt.client.modbus.common.constants.ModbusConstants;
import de.twt.client.modbus.master.MasterTCPConfig.Data.Range;
import de.twt.client.modbus.master.MasterTCPConfig.Data.Read;


public class MasterTCP {
	private MasterTCPConfig masterTCPConfig;
	private ModbusMaster master;
	private String slaveAddress;
	private int slaveId = 1;
	private final HashMap<String, Thread> threads = new HashMap<String, Thread>();
	private boolean stopReadingData = false;
	private boolean stopWritingData = false;
	
	private static final Logger logger = LoggerFactory.getLogger(MasterTCP.class);
	
	public MasterTCP(MasterTCPConfig masterTCPConfig) {
		this.masterTCPConfig = masterTCPConfig;
		slaveAddress = masterTCPConfig.getSlave().getAddress();
		ModbusDataCacheManager.createModbusData(slaveAddress);
		logger.info("MasterTCP: modbus master (connected with slave \"{}\") start...", slaveAddress);
	}
	
	public void readDataThreadForRequest() {
		logger.debug("start reading data thread for read request...");
		Thread thread = new Thread() {
			public void run() {
				while(!stopReadingData){
					if (ModbusReadRequestCacheManager.isEmpty(slaveAddress)) {
						continue;
					}
					
					ModbusReadRequestDTO request = ModbusReadRequestCacheManager.getFirstReadRequest(slaveAddress);
					if (!request.getCoilsAddressMap().isEmpty()){
						readDataForRequest(ModbusConstants.MODBUS_DATA_TYPE.coil, request.getCoilsAddressMap());
					}
					if (!request.getDiscreteInputsAddressMap().isEmpty()){
						readDataForRequest(ModbusConstants.MODBUS_DATA_TYPE.discreteInput, request.getDiscreteInputsAddressMap());
					}
					if (!request.getHoldingRegistersAddressMap().isEmpty()){
						readDataForRequest(ModbusConstants.MODBUS_DATA_TYPE.holdingRegister, request.getHoldingRegistersAddressMap());
					}
					if (!request.getInputRegistersAddressMap().isEmpty()){
						readDataForRequest(ModbusConstants.MODBUS_DATA_TYPE.inputRegister, request.getInputRegistersAddressMap());
					}
					ModbusReadRequestCacheManager.deleteReadRequest(slaveAddress, request.getID());
				}
			}
		};
		threads.put(MasterTCPConstants.THREAD_READ, thread);
		thread.start();
	}
	
	private void readDataForRequest(ModbusConstants.MODBUS_DATA_TYPE type, HashMap<Integer, Integer> addressMap){
		for(Map.Entry<Integer, Integer> entry: addressMap.entrySet()){
			int offset = (int) entry.getKey();
			int quantity = (int) entry.getValue();
			try {
				readData(type, offset, quantity);
			} catch (ModbusProtocolException | ModbusNumberException | ModbusIOException e) {
				// TODO Auto-generated catch block
				logger.warn("there is no info on the slave. (type: {}, offset: {}, quantity: {})", type, offset, quantity);
				e.printStackTrace();
			}
		}
	}
	
	public void readDataThreadForEvent() {
		logger.debug("start reading data thread for event...");
		Thread thread = new Thread() {
			public void run() {
				while(!stopReadingData){
					long startTime=System.currentTimeMillis(); 
					readDataForEvent();
					long endTime=System.currentTimeMillis(); 
					long intervalTime = endTime - startTime;
					if (intervalTime > masterTCPConfig.getPeriodTime()) {
						logger.warn("MasterTCP.readDataThread: the running time of one period is longer than the setting period time.");
					} else {
						try {
							TimeUnit.MILLISECONDS.sleep(masterTCPConfig.getPeriodTime() - intervalTime);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		};
		threads.put(MasterTCPConstants.THREAD_READ, thread);
		thread.start();
	}
	
	public void readDataForEvent() {
		logger.debug("start reading data...");
		Read dataToRead = masterTCPConfig.getData().getRead();
		readData(ModbusConstants.MODBUS_DATA_TYPE.coil, dataToRead.getCoils());
		readData(ModbusConstants.MODBUS_DATA_TYPE.discreteInput, dataToRead.getDiscreteInputs());
		readData(ModbusConstants.MODBUS_DATA_TYPE.holdingRegister, dataToRead.getHoldingRegisters());
		readData(ModbusConstants.MODBUS_DATA_TYPE.inputRegister, dataToRead.getInputRegisters());
	}
	
	private void readData(ModbusConstants.MODBUS_DATA_TYPE type, List<Range> ranges) {
		for (int idx = 0; idx < ranges.size(); idx++) {
			int offset = ranges.get(idx).getStart();
			int quantity = ranges.get(idx).getEnd() - offset + 1;
			try {
				readData(type, offset, quantity);
			} catch (ModbusProtocolException | ModbusNumberException
					| ModbusIOException e) {
				// e.printStackTrace();
				logger.warn("MasterTCP.readData: There is no value at the selected address in slave!");
			}
		}
	}
	
	private void readData(ModbusConstants.MODBUS_DATA_TYPE type, int offset, int quantity) 
			throws ModbusProtocolException, ModbusNumberException, ModbusIOException {
		if (!master.isConnected()){
			master.connect();
		}
		switch(type) {
		case coil: 
			ModbusDataCacheManager.setCoils(slaveAddress, offset, master.readCoils(slaveId, offset, quantity)); break;
		case discreteInput: 
			ModbusDataCacheManager.setDiscreteInputs(slaveAddress, offset, master.readDiscreteInputs(slaveId, offset, quantity)); break;
		case holdingRegister:
			ModbusDataCacheManager.setHoldingRegisters(slaveAddress, offset, master.readHoldingRegisters(slaveId, offset, quantity)); break;
		case inputRegister: 
			ModbusDataCacheManager.setInputRegisters(slaveAddress, offset, master.readInputRegisters(slaveId, offset, quantity));break;
		default: break;
		}
	}
	
	// TODO: wait time definition, delete first request (uncomment)
	public void writeDataThread() {
		logger.info("writeDataThread: start writing data thread...");
		Thread thread = new Thread() {
			public void run() {
				while(!stopWritingData){
					long startTime=System.currentTimeMillis();
					if (ModbusWriteRequestCacheManager.isImplemented(slaveAddress)) {
						continue;
					}
					ModbusWriteRequestDTO request = ModbusWriteRequestCacheManager.getWriteRequestToImplement(slaveAddress);
					writeRequestData(request);
					long endTime=System.currentTimeMillis(); 
					long intervalTime = endTime - startTime;
					if (intervalTime > masterTCPConfig.getPeriodTime()) {
						logger.warn("MasterTCP.writeDataThread: the running time of one period is longer than the setting period time.");
					}
					try {
						TimeUnit.MILLISECONDS.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		threads.put(MasterTCPConstants.THREAD_WRITE, thread);
		thread.start();
	}
	
	public void writeRequestData(ModbusWriteRequestDTO request){
		logger.debug("writeFirstRequestData...");
		int address = request.getAddress();
		int quantity = request.getQuantity();
		boolean[] coils = request.getCoils();
		int[] registers = request.getHoldingRegisters();
		
		try {
			if (coils.length != 0) {
				writeCoils(address, quantity, coils);
			}
			if (registers.length != 0) {
					writeHoldingRegisters(address, quantity, registers);
			}
		} catch (ModbusProtocolException | ModbusNumberException
				| ModbusIOException e) {
			e.printStackTrace();
			logger.warn("MasterTCP.writeFirstRequestData: the data from the write request is not written to the slave and dace cache!");
		}
	}

	public void writeCoils(int address, int quantity, boolean[] coils) 
			throws ModbusProtocolException, ModbusNumberException, ModbusIOException{
		logger.debug("writeCoils...");
		if (!master.isConnected()){
			master.connect();
		}
		boolean[] coilsWrite = new boolean[quantity];
		for (int idx = 0; idx < quantity; idx++){
			coilsWrite[idx] = coils[idx];
		}
		master.writeMultipleCoils(slaveId, address, coilsWrite);
		ModbusDataCacheManager.setCoils(slaveAddress, address, coilsWrite);
	}
	
	public void writeHoldingRegisters(int address, int quantity, int[] registers) 
			throws ModbusProtocolException, ModbusNumberException, ModbusIOException{	
		if (!master.isConnected()){
			master.connect();
		}
		int[] registersWrite = new int[quantity];
		for (int idx = 0; idx < quantity; idx++){
			registersWrite[idx] = registers[idx];
		}
		master.writeMultipleRegisters(slaveId, address, registersWrite);
		ModbusDataCacheManager.setHoldingRegisters(slaveAddress, address, registersWrite);
	}
	
	private TcpParameters setTCPParameters(){
		TcpParameters tcpParameters = new TcpParameters();
		String[] nums = slaveAddress.split("\\.");
		byte[] ip = {0, 0, 0, 0};
		if (nums.length == 4){
			for (int idx = 0; idx < nums.length ; idx++)
				ip[idx] = (byte) Integer.parseInt(nums[idx]);
		} else {
			logger.error("MasterTCP: the slave address in properties file is not set correctly!");
		}
		try {
			tcpParameters.setHost(InetAddress.getByAddress(ip));
		} catch (UnknownHostException e) {
			e.printStackTrace();
			logger.error("MasterTCP: the slave address in properties file is not set correctly!");
		}
        tcpParameters.setKeepAlive(true);
        tcpParameters.setPort(masterTCPConfig.getSlave().getPort());
        return tcpParameters;
	}
	
	public void init(){
		TcpParameters tcpParameters = setTCPParameters();
		master = ModbusMasterFactory.createModbusMasterTCP(tcpParameters);
        Modbus.setAutoIncrementTransactionId(true);
        try {
	        if (!master.isConnected()) {
	        	master.connect();
	        }
	        logger.info("MasterTCP.init: master is connected with slave ({}).", slaveAddress);
        } catch (ModbusIOException e) {
			logger.error("MasterTCP.init: master cannot be connected with slave ({}).", slaveAddress);
			e.printStackTrace();
		}
	}
	
}
