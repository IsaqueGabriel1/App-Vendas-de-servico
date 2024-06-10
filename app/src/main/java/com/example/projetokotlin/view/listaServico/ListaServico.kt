package com.example.projetokotlin.view.listaServico

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projetokotlin.R
import com.example.projetokotlin.databinding.ActivityListaServicoBinding
import com.example.projetokotlin.view.inicioEmpresa.telaInicialEmpresa
import com.example.projetokotlin.view.navegacao.telaNavegacao
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class ListaServico : AppCompatActivity() {
    private lateinit var  recyclerView: RecyclerView
    private lateinit var TextViewVoltar: TextView
    private lateinit var servicoList: ArrayList<Ordem>
    private var db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lista_servico)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        recyclerView = findViewById(R.id.recycleview)
        recyclerView.layoutManager =  LinearLayoutManager(this)
        servicoList = arrayListOf()

        db = FirebaseFirestore.getInstance()
        val email = Firebase.auth.currentUser
        email?.let {

            //se o email for empresa, então poderá ver todas as ordens
            if(email.email == "empresa@gmail.com"){
                db = FirebaseFirestore.getInstance()
                db.collection("Servico")
                    .get().addOnSuccessListener {
                        if (!it.isEmpty) {
                            for (data in it.documents) {
                                val servico: Ordem? = data.toObject(Ordem::class.java)
                                if (servico != null && (servico.status.toString() == "Aberto" || servico?.status.toString() == "Aguardando analise")) {
                                    servico?.let { it1 -> servicoList.add(it1) }
                                }
                            }
                            recyclerView.adapter = MyAdapter(servicoList, this)
                        }
                    }.addOnFailureListener {
                        Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                    }
            }else{
                //se o email foi qualquer outro, só verá as suas proprias ordens
                db = FirebaseFirestore.getInstance()
                db.collection("Servico")
                    .whereEqualTo("Cliente", email.email.toString())
                    .get().addOnSuccessListener {
                        if (!it.isEmpty) {
                            for (data in it.documents) {
                                val servico: Ordem? = data.toObject(Ordem::class.java)
                                if (servico != null) {
                                    val email = Firebase.auth.currentUser
                                    email?.let {
                                        servicoList.add(servico)
                                    }
                                }
                            }
                            recyclerView.adapter = MyAdapter(servicoList, this)
                        }else{
                            mensagem("No momento não existe registros para exibir!","ALERTA")
                        }
                    }.addOnFailureListener {
                        Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                    }
            }
        }

        TextViewVoltar = findViewById(R.id.voltar)
        TextViewVoltar.setOnClickListener{
            telaInicial()
        }
    }

    private fun mensagem(msg:String, titulo:String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(titulo)
            .setMessage(msg)
            .setPositiveButton("OK"){
                    dialog, whitch -> telaInicial()
            }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }
    private fun telaInicial(){
        val email = Firebase.auth.currentUser
        email?.let {
            if(email.email == "empresa@gmail.com"){
                val intent = Intent(this, telaInicialEmpresa::class.java)
                startActivity(intent)
                finish()
            }else{
                val intent = Intent(this, telaNavegacao::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}