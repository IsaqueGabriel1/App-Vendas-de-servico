package com.example.projetokotlin.view.listaServico

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projetokotlin.R
import com.example.projetokotlin.databinding.ActivityListaServicoBinding
import com.example.projetokotlin.view.inicioEmpresa.telaInicialEmpresa
import com.example.projetokotlin.view.listaServico.uitel.LoadingDialog
import com.example.projetokotlin.view.navegacao.telaNavegacao
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import org.intellij.lang.annotations.Language
import java.util.Locale

class ListaServico : AppCompatActivity() {
    private lateinit var  recyclerView: RecyclerView
    private lateinit var TextViewVoltar: TextView
    private lateinit var searchView: SearchView
    private lateinit var servicoList: ArrayList<Ordem>

    private var db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lista_servico)
        val loading = LoadingDialog(this)
        loading.startLoading()
        val handler = Handler()
        handler.postDelayed(object:Runnable{
            override  fun run(){
                loading.isDismiss()
            }
        },2000)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val searchView = findViewById<SearchView>(R.id.seachView)
        searchView.setOnQueryTextListener(object : OnQueryTextListener,
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false;
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false;
            }

        })

        recyclerView = findViewById(R.id.recycleview)
        recyclerView.layoutManager =  LinearLayoutManager(this)
        servicoList = arrayListOf()

        db = FirebaseFirestore.getInstance()
        buscar("")

        TextViewVoltar = findViewById(R.id.voltar)
        TextViewVoltar.setOnClickListener{
            telaInicial()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                //buscar mudando o filtro dentro do banco
                buscar(newText)
                return true
            }
        })
    }

    private fun buscar(descricao:String?){
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
                                if (servico != null &&  (servico.status.toString() == "Aberto" || servico?.status.toString() == "Aguardando analise")) {
                                    if(descricao != ""){
                                        if(descricao == servico.descricao.toString()){
                                            servico?.let { it1 -> servicoList.add(it1) }
                                        }
                                    }else{
                                        servico?.let { it1 -> servicoList.add(it1) }
                                    }

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
    }

    private fun filterList(query:String?){
        if(query!=null){
            val filteredList = ArrayList<Ordem>()

            for (data in servicoList) {
                if (data.descricao == query) {
                    val email = Firebase.auth.currentUser
                    email?.let {
                        filteredList.add(data)
                    }
                }
            }
            if(filteredList.isEmpty()){
                Toast.makeText(this,"No data found", Toast.LENGTH_SHORT).show()
            }else{
                MyAdapter(servicoList,this).setFilteredList(filteredList)

            }
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