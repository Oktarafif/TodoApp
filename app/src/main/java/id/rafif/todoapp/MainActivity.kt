package id.rafif.todoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    //Buat variable Firebase Refrence
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        databaseRef = FirebaseDatabase.getInstance().reference

        btn_tambah.setOnClickListener{
            val nama = input_name.text.toString()
            tambahData(nama)
        }

        btn_hapus.setOnClickListener{
            val nama : String= input_name.text.toString()
            if (nama.isBlank()) {
                toastData("Kolom Nama Harus Diisi")
            }else{
                hapusData(nama)
            }
        }
        btn_edit.setOnClickListener{
            val namaAsal: String = input_name.text.toString()
            val namaTujuan: String = edit_name.text.toString()
            if (namaAsal.isBlank() || namaTujuan.isBlank()) {
                toastData("Kolom tidak boleh kosong")
            } else {
                modifData(namaAsal, namaTujuan)
            }
        }

    //jalankan fungsi cekData di OnCreate
    cekData()

    }

    private fun cekData() {
        val dataListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.childrenCount > 0) {
                    var textData = " "
                    for (data :DataSnapshot in snapshot.children) {
                        val nilai :ModelNama = data .getValue(ModelNama::class.java) as ModelNama
                        textData += "${nilai.Nama} \n"
                    }
                    txt_name.text = textData
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        val cekData :DatabaseReference = databaseRef.child("Daftar Nama")
        cekData.addValueEventListener(dataListener)
    }

    private fun modifData(namaAsal: String, namaTujuan: String) {
        // Logika modifikasi data diletakkan di bagian ValueEventListener
        // Logikanya cek data namaAsal dlu, jika ada modif sata tsb dengan namaTujuan
        val dataTujuan = HashMap<String, Any>()
        dataTujuan["Nama"] = namaTujuan

        val dataListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.childrenCount > 0) {
                    databaseRef.child("Daftar Nama")
                        .child(namaAsal)
                        .updateChildren(dataTujuan)
                        .addOnCompleteListener {
                            if (it.isSuccessful) toastData("Data Telah diupdate")
                        }
                } else {
                    toastData("Data yg dituju tidak ada di database")
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        val dataAsal : DatabaseReference = databaseRef.child("Daftar Nama")
            .child(namaAsal)
        dataAsal.addListenerForSingleValueEvent(dataListener)
    }

    private fun toastData(pesan: String) {
        Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show()
    }

    private fun tambahData(nama: String) {
        val data = HashMap<String, Any>()
        data["Nama"] = nama

        val dataListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.childrenCount > 0) {
                    toastData("data tersebut telah ada di database")
                } else {
                    val tambahData = databaseRef.child("Daftar Nama")
                        .child(nama)
                        .setValue(data)
                    tambahData.addOnCompleteListener {
                        if (it.isSuccessful){
                            toastData("$nama telah ditambahkan dalam database")
                        }else {
                            toastData("$nama gagal ditambahkan")
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                toastData("tidak bisa menghapus data itu")
            }
        }
        databaseRef.child("Daftar Nama")
            .child(nama).addListenerForSingleValueEvent(dataListener)

    }

    private fun hapusData(nama: String) {
        // membuat Listener data Firebase
        val dataListener = object : ValueEventListener {
            // onDataChange itu untuk mengetahui aktifitas data
            // sperti penambahan, pengurangam, dan perubahan data
            override fun onDataChange(snapshot: DataSnapshot) {
                //snapshot.childrenCount untuk menjumlah data yg telah diambil
                if (snapshot.childrenCount > 0) {
                    databaseRef.child("Daftar Nama").child(nama)
                        .removeValue()
                        .addOnCompleteListener {
                            if (it.isSuccessful) toastData("$nama telah dihapus")
                        }
                } else {
                    toastData("Tidak ada data $nama")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                toastData("tidak bisa menghapus data itu")
            }
        }
        val cekData = databaseRef.child("Daftar Nama")
            .child(nama)
        cekData.addListenerForSingleValueEvent(dataListener)

    }
}