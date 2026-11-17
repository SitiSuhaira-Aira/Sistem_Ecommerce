package com.mycompany.SistemEcommerce;

import java.io.Serializable;

public class Transaksi implements Serializable {
    private static final long serialVersionUID = 1L;
    private String idPelanggan;
    private String kodeProduk;
    private int jumlah;

    public Transaksi(String idPelanggan, String kodeProduk, int jumlah) {
        this.idPelanggan = idPelanggan;
        this.kodeProduk = kodeProduk;
        this.jumlah = jumlah;
    }

    public String getIdPelanggan() {
        return idPelanggan;
    }

    public String getKodeProduk() {
        return kodeProduk;
    }

    public int getJumlah() {
        return jumlah;
    }
}