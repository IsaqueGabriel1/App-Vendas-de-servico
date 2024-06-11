package com.example.projetokotlin.view.cliente.infoCliente.CriarCliente.listarCliente

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projetokotlin.R
import com.example.projetokotlin.databinding.ActivityListarClienteBinding
import com.example.projetokotlin.databinding.ActivityTelaClienteBinding
import com.example.projetokotlin.view.cliente.MyAdapter
import com.example.projetokotlin.view.cliente.Ordem
import com.example.projetokotlin.view.inicioEmpresa.telaInicialEmpresa
import com.example.projetokotlin.view.navegacao.telaNavegacao
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class ListarCliente : AppCompatActivity() {
    private lateinit var binding: ActivityListarClienteBinding
    private var db = FirebaseFirestore.getInstance()
    private lateinit var  recyclerView: RecyclerView
    private lateinit var listarCliente: ArrayList<ClienteModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListarClienteBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnVoltar.setOnClickListener{
            telaInicial()
        }


        recyclerView = findViewById(R.id.recycleview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        listarCliente = arrayListOf()

        db = FirebaseFirestore.getInstance()

            //se o email for empresa, então poderá ver todas as ordens
            db = FirebaseFirestore.getInstance()
            db.collection("Cliente")
                .get().addOnSuccessListener {
                    if (!it.isEmpty) {
                        for (data in it.documents) {
                            val cliente: ClienteModel? = data.toObject(ClienteModel::class.java)
                            if (cliente != null) {
                                if(cliente.Nome.toString() != "" || cliente.Telefone.toString() != ""){
                                    listarCliente.add(cliente)
                                }
                            }
                        }
                        recyclerView.adapter = ClienteAdapter(listarCliente, this)
                    }else{
                        mensagem("No momento, não existe registros para apresentar","ALERTA")
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                }
        }

    private fun mensagem(msg:String, titulo:String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(titulo)
            .setMessage(msg)
            .setPositiveButton("OK"){
                    dialog, whitch ->
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

