package com.example.projetokotlin.view.EditarExcluirOrdem

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projetokotlin.R
import com.example.projetokotlin.databinding.ActivityTelaEmpresaServicoBinding
import com.example.projetokotlin.view.listaServico.ListaServico
import com.example.projetokotlin.view.navegacao.telaNavegacao
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class EditarExcluirOrdem : AppCompatActivity() {
    private lateinit var binding: ActivityTelaEmpresaServicoBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTelaEmpresaServicoBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        recuperarDados()
        val email = Firebase.auth.currentUser
        email?.let {
                db.collection("Servico")
                    .whereEqualTo(email.email.toString(), email.email.toString())
                    .get()
                    .addOnSuccessListener { documents ->
                        if (documents != null) {
                            for (document in documents) {
                                binding.editDescricao.setText(document.getString("descricao"))
                                binding.editPorte.setText(document.getString("tipoProjeto"))
                                binding.editValor.setText(document.getString("valorApagar"))
                            }
                        } else {
                            binding.editDescricao.setText("")
                            binding.editPorte.setText("")
                            binding.editValor.setText("")
                        }

                    }
                    .addOnFailureListener { exception ->
                        Log.w("DB", "Error getting documents: ", exception)
                        msgGenerica("Erro inesperado!")
                        FirebaseAuth.getInstance().signOut()
                        val voltarTelaLogin = Intent(this, telaNavegacao::class.java)
                        startActivity(voltarTelaLogin)
                        finish()
                    }

            binding.btnEditar.setOnClickListener{
                db.collection("Servico").document(binding.editDescricao.text.toString())
                    .update(
                        "descricao",binding.editDescricao.text.toString(),
                        "porteSistema",binding.editPorte.text.toString(),
                        "valor",binding.editValor.text.toString()
                    )
                    .addOnSuccessListener{
                        msgEdit()
                    }
                    .addOnFailureListener{
                        val exe = it.message
                        msgGenerica("Não foi possivel editar este registro! $exe")
                    }
            }

            binding.btnDeslogar.setOnClickListener{
                val voltarTelaLogin = Intent(this, ListaServico:: class.java)
                startActivity(voltarTelaLogin)
                finish()
                limparInput()
            }

            binding.btnExcluir.setOnClickListener{
                caixaDeMensagem("Realmente deseja excluir este registro?")
            }
        }
    }
    private fun msgGenerica(msg: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alerta!")
            .setMessage(msg)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }
    //
    private fun msgEdit(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alerta de alteração")
            .setMessage("Registro editado com sucesso!")
            val voltarTelaLogin = Intent(this, telaNavegacao:: class.java)
            startActivity(voltarTelaLogin)
            finish()
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    //através da descricao da ordem, deleta o documento
    private fun deletarRegistro(){
        db.collection("Servico").document(binding.editDescricao.text.toString())
            .delete().addOnCompleteListener {
                val voltarTelaLogin = Intent(this, ListaServico:: class.java)
                startActivity(voltarTelaLogin)
                finish()
                limparInput()
            }
    }
    //Recebe as informações da tela de0 listar ordens
    private fun recuperarDados(){
        val descricao = intent.getStringExtra("descricao")
        val valor = intent.getStringExtra("valor")
        val porte = intent.getStringExtra("porte")

        //função para colocar dados nos inputs
        setaInput(descricao.toString(),valor.toString(),porte.toString())
    }

    //coloca nos inputs as informações vindas da tela de listar ordens
    private fun setaInput(descricao:String, valor:String, porte:String){
        binding.editDescricao.setText(descricao)
        binding.editPorte.setText(porte)
        binding.editValor.setText(valor)
    }
    private fun limparInput(){
        binding.editDescricao.setText("")
        binding.editPorte.setText("")
        binding.editValor.setText("")
    }

    //Mostra uma mensagem na tela
    private fun caixaDeMensagem(msg:String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alerta de alteração")
            .setMessage(msg)
            .setPositiveButton("Sim"){dialog, whitch ->
                Toast.makeText(this,"Sucesso!",Toast.LENGTH_SHORT).show()
                deletarRegistro()
            }
            .setNegativeButton("Não"){dialog, whitch ->
                dialog.dismiss()
            }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }
}