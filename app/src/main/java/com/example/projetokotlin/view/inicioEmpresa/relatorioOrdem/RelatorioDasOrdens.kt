package com.example.projetokotlin.view.inicioEmpresa.relatorioOrdem

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.SearchView
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
import com.example.projetokotlin.databinding.ActivityRelatorioDasOrdensBinding
import com.example.projetokotlin.view.inicioEmpresa.telaInicialEmpresa
import com.example.projetokotlin.view.listaServico.MyAdapter
import com.example.projetokotlin.view.listaServico.Ordem
import com.example.projetokotlin.view.listaServico.uitel.LoadingDialog
import com.example.projetokotlin.view.navegacao.telaNavegacao
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class RelatorioDasOrdens : AppCompatActivity() {
    private lateinit var binding: ActivityRelatorioDasOrdensBinding
    private lateinit var  recyclerView: RecyclerView
    private lateinit var TextViewVoltar: TextView
    private lateinit var servicoList: ArrayList<Ordem>
    private var filtro:String = ""
    private var db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRelatorioDasOrdensBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
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

        recyclerView = findViewById(R.id.recycleview)
        recyclerView.layoutManager =  LinearLayoutManager(this)
        servicoList = arrayListOf()

        db = FirebaseFirestore.getInstance()

        TextViewVoltar = findViewById(R.id.voltar)
        TextViewVoltar.setOnClickListener{
            telaInicial()
        }

        //opções do dropdown
        val item = listOf("Finalizado", "Cancelado")
        //busca ele no layout
        val autoComplete:AutoCompleteTextView = findViewById(R.id.auto_completeText)
        //passa pra ele os teins e o contexto(TELA)
        val adapter = ArrayAdapter(this,R.layout.list_item,item)

        //seta o adapter no dropdowm
        autoComplete.setAdapter(adapter)

        //quando for selecionado
        autoComplete.onItemClickListener = AdapterView.OnItemClickListener{ adapterView,view,i,l
            ->
            val itemSelected = adapterView.getItemAtPosition(i)
            Toast.makeText(this, "Item $itemSelected", Toast.LENGTH_SHORT).show()
                //muda o valor de filtro, limpe a lista de serviço e faça uma nova busca no BD
                filtro = itemSelected.toString()
                servicoList.clear()
            buscarParaEmpresa()
        }
        buscarParaEmpresa()
    }

    //faz a busca das ordens de serviço com status Finalizado e Cancelado
    private fun buscarParaEmpresa(){
        db = FirebaseFirestore.getInstance()
        db.collection("Servico")
            .get().addOnSuccessListener {
                //verifica se o documento está vazio
                if (!it.isEmpty) {
                    //se o documento não estiver vazio, será percorrido e colocado cada objeto na variavel data
                    for (data in it.documents) {
                        //servico recebe as ordens
                        val servico: Ordem? = data.toObject(Ordem::class.java)
                        //verifica se o serviço é diferente de nulo e é finalizado ou cancelado, diferente disso não será exibido
                        if (servico != null &&  (servico.status.toString() == "Finalizado" || servico?.status.toString() == "Cancelado")) {
                            if(filtro != ""){
                                if(filtro == servico.status){
                                    servico?.let { it1 -> servicoList.add(it1) }
                                }
                            }else{
                                servico?.let { it1 -> servicoList.add(it1) }
                            }

                        }
                    }
                    recyclerView.adapter = RelatorioAdapter(servicoList, this)
                }else{
                    mensagem("Não existe registros para exibir, volte para tela inicial", "AVISO")
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