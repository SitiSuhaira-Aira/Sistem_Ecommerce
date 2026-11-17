package com.mycompany.SistemEcommerce;
        
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class SistemToko implements Serializable {
    private static final long serialVersionUID = 1L;
    private String namaToko;
    private HashMap<String, Pelanggan> daftarPelanggan;
    private HashMap<String, Produk> daftarProduk;
    private ArrayList<Transaksi> daftarTransaksi;

    public SistemToko(String namaToko) {
        this.namaToko = namaToko;
        this.daftarPelanggan = new HashMap<>();
        this.daftarProduk = new HashMap<>();
        this.daftarTransaksi = new ArrayList<>();
    }

    public void tambahPelanggan(String id, String nama) {
        daftarPelanggan.put(id, new Pelanggan(id, nama));
    }

    public void tambahProduk(String kode, String namaProduk, int stok) {
        daftarProduk.put(kode, new Produk(kode, namaProduk, stok));
    }

    public void jalankanTransaksiDariFile(String namaFile) throws Exception {
        File file = new File(namaFile);
        if (!file.exists()) {
            throw new TransaksiException("File " + namaFile + " tidak ditemukan!");
        }

        // Baris error (FileReader::new) sudah dihapus dari sini
        BufferedReader br = new BufferedReader(new FileReader(file));
        String baris;
        
        StringBuilder content = new StringBuilder();
        while ((baris = br.readLine()) != null) {
            content.append(baris.trim());
        }
        br.close();

        // Parsing khusus Varian 1 (Strip & Pipe): ID-ACT-01/KD-OBJ-A/JML-3/|...
        String dataGabungan = content.toString();
        String[] transaksiArray = dataGabungan.split("\\|\\|"); // antisipasi pemisah pipe ganda atau tunggal
        if (transaksiArray.length <= 1) {
            transaksiArray = dataGabungan.split("\\|");
        }

        for (String item : transaksiArray) {
            if (item.trim().isEmpty()) continue;
            
            String[] parts = item.split("/");
            String idPelanggan = "";
            String kodeProduk = "";
            int jumlah = 0;

            for (String p : parts) {
                if (p.startsWith("ID-")) {
                    idPelanggan = p.substring(3);
                } else if (p.startsWith("KD-")) {
                    kodeProduk = p.substring(3);
                } else if (p.startsWith("JML-")) {
                    jumlah = Integer.parseInt(p.substring(4));
                }
            }

            if (!idPelanggan.isEmpty() && !kodeProduk.isEmpty()) {
                Pelanggan p = daftarPelanggan.get(idPelanggan);
                Produk pr = daftarProduk.get(kodeProduk);

                if (p == null) {
                    System.out.println("[WARNING] Pelanggan dengan ID " + idPelanggan + " tidak terdaftar. Transaksi dilewati.");
                    continue;
                }
                if (pr == null) {
                    System.out.println("[WARNING] Produk dengan Kode " + kodeProduk + " tidak terdaftar. Transaksi dilewati.");
                    continue;
                }

                try {
                    pr.kurangiStok(jumlah);
                    daftarTransaksi.add(new Transaksi(idPelanggan, kodeProduk, jumlah));
                    System.out.println("Berhasil memproses transaksi: Pelanggan " + p.getNama() + " membeli " + pr.getNamaProduk() + " sebanyak " + jumlah);
                } catch (TransaksiException e) {
                    System.out.println("[ERROR TRANSAKSI] " + e.getMessage());
                }
            }
        }
    }

    public void simpanLaporanAkhir() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("storage.db"))) {
            oos.writeObject(this);
            System.out.println("[INFO] State objek berhasil diserialisasi ke storage.db");
        } catch (IOException e) {
            System.out.println("[FATAL] Gagal melakukan serialisasi: " + e.getMessage());
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter("laporan.txt"))) {
            pw.println("=== LAPORAN TRANSAKSI BESAR (QTY >= 5) ====");
            int count = 0;
            for (Transaksi t : daftarTransaksi) {
                if (t.getJumlah() >= 5) {
                    pw.println("ID Pelanggan: " + t.getIdPelanggan() + " | Kode Produk: " + t.getKodeProduk() + " | Jumlah: " + t.getJumlah());
                    count++;
                }
            }
            pw.println("Total Transaksi Besar Tercatat: " + count);
            System.out.println("[INFO] Laporan teks berhasil disimpan ke laporan.txt");
        } catch (IOException e) {
            System.out.println("[FATAL] Gagal menulis file laporan teks: " + e.getMessage());
        }
    }
}