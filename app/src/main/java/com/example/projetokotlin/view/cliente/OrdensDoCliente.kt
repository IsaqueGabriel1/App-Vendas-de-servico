package com.example.projetokotlin.view.cliente

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projetokotlin.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.firestore

class OrdensDoCliente : AppCompatActivity() {
    private lateinit var  recyclerView: RecyclerView
    private lateinit var listaServicoContratado: ArrayList<Ordem>

    private var db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_servicos_cliente)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recycleview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        listaServicoContratado = arrayListOf()

        db = FirebaseFirestore.getInstance()
        val email = Firebase.auth.currentUser
        email?.let {
            //se o email for empresa, então poderá ver todas as ordens
            db = FirebaseFirestore.getInstance()
            db.collection("Servico")
                .get(Source.CACHE).addOnSuccessListener {
                    if (!it.isEmpty) {
                        for (data in it.documents) {
                            val servico: Ordem? = data.toObject(Ordem::class.java)
                            if (servico != null) {
                                listaServicoContratado.add(servico)
                            }
                        }
                        recyclerView.adapter = MyAdapter(listaServicoContratado, this)
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                }
        }
    }
}