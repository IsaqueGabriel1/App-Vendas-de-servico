package com.example.projetokotlin.view.inicioEmpresa.novaSenha

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projetokotlin.R
import com.example.projetokotlin.databinding.ActivityNovaSenhaEmpresaBinding
import com.example.projetokotlin.databinding.ActivityTelaInicialEmpresaBinding
import com.example.projetokotlin.view.formlogin.FormLogin
import com.example.projetokotlin.view.inicioEmpresa.telaInicialEmpresa
import com.example.projetokotlin.view.listaServico.ListaServico
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class NovaSenhaEmpresa : AppCompatActivity() {
    private lateinit var binding: ActivityNovaSenhaEmpresaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNovaSenhaEmpresaBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val user = Firebase.auth.currentUser
        if(user?.email != null){
            binding.editEmail.setText(user?.email.toString())
        }

        binding.btnEnviar.setOnClickListener{
            val user = Firebase.auth.currentUser
            val senha1 = binding.editSenha.text.toString()
            val senha2 = binding.editConfirSenha.text.toString()
            val email = binding.editEmail.text.toString()

            if(binding.editEmail.text.toString() == "empresa@gmail.com"){
                if(senha1 == senha2){
                    user!!.updatePassword(senha1)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                caixaDeMensagem("Senha resetada com sucesso!")
                            }
                        }
                }else{
                    caixaDeMensagem("As senhas não coincidem!")
                }
            }else{
                if(binding.editEmail.text != null){
                    if(senha1 == senha2){
                        Firebase.auth.sendPasswordResetEmail(email).continueWith {
                                task ->
                            if(task.isCanceled){
                                caixaDeMensagem("Não foi possivel enviar o email, tente novamente mais tarde!")
                            }
                            caixaDeMensagem("Se o email informado for valido, enviaremos um link para redefinir sua senha!")
                        }
                    }else{
                        caixaDeMensagem("As senhas não coincidem!")
                    }
                }
            }
        }
    }
    private fun caixaDeMensagem(msg:String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alerta de alteração")
            .setMessage(msg)
            .setPositiveButton("OK"){dialog, whitch ->
                val voltarTelaLogin = Intent(this, FormLogin:: class.java)
                startActivity(voltarTelaLogin)
                finish()
            }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }
}