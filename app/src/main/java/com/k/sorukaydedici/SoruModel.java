package com.k.sorukaydedici;

public class SoruModel {
   private String baslik;
   private String soru;
   private String cevap;

    public SoruModel(){}
    public SoruModel(String baslik, String soru,String cevap) {
        this.baslik = baslik;
        this.soru = soru;
        this.cevap=cevap;
    }

    public String getCevap() {
        return cevap;
    }

    public void setCevap(String cevap) {
        this.cevap = cevap;
    }

    public String getBaslik() {
        return baslik;
    }

    public void setBaslik(String baslik) {
        this.baslik = baslik;
    }

    public String getSoru() {
        return soru;
    }

    public void setSoru(String soru) {
        this.soru = soru;
    }
}
