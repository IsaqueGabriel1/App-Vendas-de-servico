package com.example.projetokotlin.view.cliente.infoCliente.CriarCliente.listarCliente

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projetokotlin.R
import com.example.projetokotlin.view.avaliacao.avaliacaoServico
import com.example.projetokotlin.view.cliente.Ordem
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class ClienteAdapter(private val ListaCliente:ArrayList<ClienteModel>, private val context: Context):RecyclerView.Adapter<ClienteAdapter.MyViewHolder>(){

    inner class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var Email:TextView
        var Nome: TextView
        var Telefone: TextView
        var Status: TextView
        var id:String
        init {
            Email = itemView.findViewById(R.id.Email)
            Nome = itemView.findViewById(R.id.Nome)
            Telefone = itemView.findViewById(R.id.Telefone)
            Status = itemView.findViewById(R.id.Status)
            id=""
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.lista_cliente_item, parent,false)
        return  MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return ListaCliente.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.Email.text = ListaCliente[position].Email
        holder.Nome.text = ListaCliente[position].Nome
        holder.Telefone.text = ListaCliente[position].Telefone
        holder.Status.text = ListaCliente[position].status
        holder.Email.text = ListaCliente[position].Email
    }
}