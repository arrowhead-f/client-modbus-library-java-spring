package de.twt.client.modbus.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ModbusResponseDTO implements Serializable {
	private static final long serialVersionUID = -252001450490780411L;
	private String bn;
    private double bt;
    private String bu;
    private int ver;
    private List<ModbusData> e = new ArrayList<>();

    public ModbusResponseDTO() {
    }

    public ModbusResponseDTO(String bn, double bt, String bu, int ver) {
        this.bn = bn;
        this.bt = bt;
        this.bu = bu;
        this.ver = ver;
    }

    public ModbusResponseDTO(String bn, double bt, String bu, int ver, List<ModbusData> e) {
        super();
        this.bn = bn;
        this.bt = bt;
        this.bu = bu;
        this.ver = ver;
        this.e = e;
    }

    public String getBn() {
        return bn;
    }

    public void setBn(String bn) {
        this.bn = bn;
    }

    public double getBt() {
        return bt;
    }

    public void setBt(double bt) {
        this.bt = bt;
    }

    public String getBu() {
        return bu;
    }

    public void setBu(String bu) {
        this.bu = bu;
    }

    public int getVer() {
        return ver;
    }

    public void setVer(int ver) {
        this.ver = ver;
    }

    public List<ModbusData> getE() {
        return e;
    }

    public void setE(List<ModbusData> e) {
        this.e = e;
    }
    
    public void addE(ModbusData e) {
        this.e.add(e);
    }
}
