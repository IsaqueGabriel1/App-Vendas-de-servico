package com.example.projetokotlin.view.formlogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projetokotlin.R
import com.example.projetokotlin.databinding.ActivityFormLoginBinding
import com.example.projetokotlin.view.avaliacao.avaliacaoServico
import com.example.projetokotlin.view.formcadastro.FormCadastro
import com.example.projetokotlin.view.navegacao.telaNavegacao
import com.google.firebase.auth.FirebaseAuth
import java.text.Normalizer.Form

class FormLogin : AppCompatActivity() {

    private lateinit var binding: ActivityFormLoginBinding
    private val auth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btbEntrar.setOnClickListener{
            val email = binding.editEmail.text.toString()
            val senha = binding.editSenha.text.toString()

            if(email.isEmpty() || senha.isEmpty()){
                mensagem("Preencha todos os campos!!!", "Aviso")
            }else{
                auth.signInWithEmailAndPassword(email,senha).addOnCompleteListener{autentic ->
                    if(autentic.isSuccessful){
                        navegarTelainicial()
                    }else{
                        mensagem("Informações invalidas, preencha os campos novamente", "Aviso")
                        binding.editEmail.setText("")
                        binding.editSenha.setText("")
                    }
                }
                    .addOnFailureListener{
                        Log.d("TAG","Não foi possivel realizar fazer login, tente novamente mais tarde!")
                    }
            }
        }

        binding.telaCadastrar.setOnClickListener{view ->
            val intent = Intent(this,FormCadastro::class.java)
            startActivity(intent )
        }

        binding.btnRecuperarSenha.setOnClickListener{task->
            val email = binding.editEmail.text.toString()
            if(email == ""){
                mensagem("O campo email está vazio. Por favor, digite um email valido!", "Mensagem de erro")
            }else{
               redefinirSenha(email)
            }
        }
    }

    //redefine a senha do usuario se existir
    private fun redefinirSenha(email:String){
        if(auth != null){
            auth.sendPasswordResetEmail(email).continueWith{
                    task ->
                if(task.isCanceled){
                    mensagem("Não foi possivel enviar o email, tente novamente mais tarde!", "AVISO")
                }
                mensagem("Se o email informado for valido, enviaremos um link para redefinir sua senha!", "AVISO!")
            }
        }
    }
    private fun navegarTelainicial(){
        val intent = Intent(this,telaNavegacao::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStart(){
        super.onStart()

        val usuarioAtual = FirebaseAuth.getInstance().currentUser
        if(usuarioAtual != null){
            navegarTelainicial()
        }
    }

    private fun mensagem(msg:String, titulo:String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(titulo)
            .setMessage(msg)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }
}