package de.twt.client.modbus.dataWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.twt.client.modbus.common.cache.ModbusDataCacheManager;
import eu.arrowhead.common.CommonConstants;

@Service
public class ModbusDataWriter {
	FileWriter csvWriter;
	
	@Autowired
	private ModbusDataRecordContent modbusDataRecordContent;
	
	@Value("${record_period: 200}")
	private int recordPeriod;
	
	private final Logger logger = LogManager.getLogger(ModbusDataWriter.class);
	
	public void startRecord() {
		if (modbusDataRecordContent.getFileName() == null) {
			logger.error("Please set the record content in the application.properties file!");
		}
		
		try {
			initCSV();
		} catch (IOException e) {
			logger.error("The csv file cannot be created!");
			e.printStackTrace();
		}

		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			String slaveAddress = modbusDataRecordContent.getSlaveAddress();
			List<String> recordContents = modbusDataRecordContent.getContent();
			
			@Override
			public void run() {
				List<String> record = new ArrayList<String>();
				HashMap<String, String> recordMap = ModbusDataCacheManager.convertModbusDataToCSVRecord(slaveAddress);
				for (int i = 0; i < recordContents.size(); i++) {
					record.add(recordMap.get(recordContents.get(i)));
				}
			    	
				try {
					csvWriter.append(String.join(",", record));
					csvWriter.append("\n");
					csvWriter.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, 0, recordPeriod);
		
	}
	
	private void initCSV() throws IOException {
		String fileName = modbusDataRecordContent.getFileName();
		if (!fileName.contains(".csv")) {
			fileName += ".csv";
		}
		csvWriter = new FileWriter(fileName);
		
		csvWriter.append(String.join(",", modbusDataRecordContent.getContent()));
	    csvWriter.append("\n");
		
	    csvWriter.flush();
	}
}
