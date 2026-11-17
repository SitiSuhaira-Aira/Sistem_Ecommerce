package com.mycompany.SistemEcommerce;

import java.io.Serializable;

public class Produk implements Serializable {
    private static final long serialVersionUID = 1L;
    private String kode;
    private String namaProduk;
    private int stok;

    public Produk(String kode, String namaProduk, int stok) {
         this.kode = kode;
         this.namaProduk = namaProduk;
         this.stok = stok;
    }
    
    public String getKode() {
        return kode;
    }
    
    public String getNamaProduk() {
         return namaProduk;
    }
    
    public int getStok() {
         return stok;
    }

    public void kurangiStok(int jumlah) throws TransaksiException {
        if (this.stok < jumlah) {
           throw new TransaksiException("Stok tidak mencukupi untuk produk: " + namaProduk);
        }
        this.stok -= jumlah;
    }
}