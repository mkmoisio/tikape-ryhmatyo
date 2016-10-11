package tikape.runko.domain;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Aihe {
    private Integer tunnus;
    private Alue alue;
    private String aloittaja;
    private String sisalto;
    private String otsikko;
    private Timestamp luotu;
    private List<Viesti> viestit;
    
    public Aihe(String aloittaja, String sisalto, String otsikko, Timestamp luotu) {
        this(null, aloittaja, sisalto, otsikko, luotu);
    }
    
    public Aihe(Integer tunnus, String aloittaja, String sisalto, 
                String otsikko, Timestamp luotu) {
        
        this.tunnus = tunnus;
        this.alue = null;
        this.aloittaja = aloittaja;
        this.sisalto = sisalto;
        this.otsikko = otsikko;
        this.luotu = luotu;
        this.viestit = new ArrayList<>();
    }
    
    public Integer getTunnus() {
        return tunnus;
    }
    
    public void setTunnus(Integer tunnus) {
        this.tunnus = tunnus;
    }
    
    public Alue getAlue() {
        return alue;
    }
    
    public void setAlue(Alue alue) {
        this.alue = alue;
    }
    
    public String getAloittaja() {
        return aloittaja;
    }
    
    public void setAloittaja(String aloittaja) {
        this.aloittaja = aloittaja;
    }
    
    public String getSisalto() {
        return sisalto;
    }
    
    public void setSisalto(String sisalto) {
        this.sisalto = sisalto;
    }

    public String getOtsikko() {
        return otsikko;
    }
    
    public void setOtsikko(String otsikko) {
        this.otsikko = otsikko;
    }

    public Timestamp getLuotu() {
        return luotu;
    }
    
    public void setLuotu(Timestamp luotu) {
        this.luotu = luotu;
    }

    public List<Viesti> getViestit() {
        return viestit;
    }
    
    public void setViestit(List<Viesti> viestit) {
        this.viestit = viestit;
    }
}