package com.example.projetokotlin.view.listaServico

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.component1
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBindings
import com.example.projetokotlin.R
import com.example.projetokotlin.view.EditarExcluirOrdem.EditarExcluirOrdem
import com.example.projetokotlin.view.gestaoOrdem.GestaoDeOrdem
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class MyAdapter(private  val servicoLista:ArrayList<Ordem>, private val context: Context):RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    private val db = FirebaseFirestore.getInstance()
    inner class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val cliente: TextView = itemView.findViewById(R.id.editCliente)
        val descricao: TextView = itemView.findViewById(R.id.editdescricao)
        val valor: TextView = itemView.findViewById(R.id.editValor)
        val porte: TextView = itemView.findViewById(R.id.editPorteSys)
        val status: TextView = itemView.findViewById(R.id.editStatus)

        init {
            val email = Firebase.auth.currentUser

            email?.let {
                itemView.setOnClickListener {
                    if (email.email == "empresa@gmail.com") {
                        if (status.text.toString() == "Aguardando analise" || status.text.toString() == "Rejeitado" || status.text.toString() == "Em andamento") {
                            val intent = Intent(context, GestaoDeOrdem::class.java)
                            intent.putExtra("descricao", descricao.text.toString())
                            intent.putExtra("valor", valor.text.toString())
                            intent.putExtra("porte", porte.text.toString())
                            intent.putExtra("status",status.text.toString())
                            context.startActivity(intent)
                        } else {
                            mensagem("Esse serviço já foi Finalizado!", false)
                        }
                    } else {
                        //pega a referencia da atividade ListarServico para ir para a tela de edicao ser ordem
                        val intent = Intent(context, EditarExcluirOrdem::class.java)
                        intent.putExtra("descricao", descricao.text.toString())
                        intent.putExtra("valor", valor.text.toString())
                        intent.putExtra("porte", porte.text.toString())
                        context.startActivity(intent)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.lista_item_cliente, parent,false)
        return  MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return servicoLista.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.cliente.text = servicoLista[position].Cliente
        holder.descricao.text = servicoLista[position].descricao
        holder.valor.text = servicoLista[position].valor
        holder.status.text = servicoLista[position].status
        holder.porte.text = servicoLista[position].porteSistema
    }

    private fun contratarServico(descricao:String){
        db.collection("Servico").document("Ordem:"+descricao)
            .update("status","Em Andamento")
            .addOnSuccessListener {
                mensagem("Ordem aceita com sucesso!",true)
            }
    }

    private fun mensagem(msg:String, sucesso:Boolean){
        val builder = AlertDialog.Builder(context)
        if(sucesso == true){
            builder.setTitle("Alerta!")
                .setMessage(msg)
        }else{
            builder.setTitle("Alerta!")
                .setMessage(msg)
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }
}