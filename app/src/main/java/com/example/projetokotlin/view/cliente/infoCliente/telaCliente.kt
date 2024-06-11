package com.example.projetokotlin.view.cliente.infoCliente

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projetokotlin.R
import com.example.projetokotlin.databinding.ActivityServicosClienteBinding
import com.example.projetokotlin.databinding.ActivityTelaClienteBinding
import com.example.projetokotlin.view.cliente.Cliente
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class telaCliente : AppCompatActivity() {
    private lateinit var binding: ActivityTelaClienteBinding
    private val db = FirebaseFirestore.getInstance()
    var idCliente:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTelaClienteBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        getId()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //fazer a lógica para as demais funcionalidades

        binding.btnEditar.setOnClickListener{
            if(idCliente != null){
                db.collection("Cliente").document(idCliente)
                    .update(
                        mapOf(
                            "Nome" to binding.editNome.text.toString(),
                            "Email" to binding.editEmail.text.toString(),
                            "Telefone" to binding.editTelefone.text.toString()
                         )
                    ).addOnSuccessListener {
                        mensagem("Cliente ${binding.editNome.text.toString()} editado com sucesso!","AVISO!")
                    }.addOnFailureListener{
                        mensagem("Não foi possivel editar o cliente","AVISO")
                    }
            }
        }
    }

    private fun mensagem(msg: String, titulo: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(titulo)
            .setMessage(msg)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    private fun getId() {
        val email = Firebase.auth.currentUser
        email?.let {
            db.collection("Cliente")
                .whereEqualTo("Email", email.email.toString())
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val cliente: Cliente? = document.toObject(Cliente::class.java)
                        if (cliente != null) {
                            if (cliente.id != null) {
                                this.idCliente = cliente.id
                                Log.w("TAG", "ID = $idCliente")
                            } else {
                                Log.w("TAG", "Erro: cliente id null")
                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("TAG", "Error getting documents: ", exception)
                }
        }
    }
}