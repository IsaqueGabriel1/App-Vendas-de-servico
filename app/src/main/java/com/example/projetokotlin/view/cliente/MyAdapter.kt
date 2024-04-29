package com.example.projetokotlin.view.cliente

import android.content.Context
import android.content.Intent
import android.media.Rating
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.projetokotlin.R
import com.example.projetokotlin.view.EditarExcluirOrdem.EditarExcluirOrdem
import com.example.projetokotlin.view.avaliacao.avaliacaoServico
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random


class MyAdapter(private  val OrdemServico:ArrayList<Ordem>, private val context: Context):RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    inner class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        private val db = FirebaseFirestore.getInstance()
        val cliente: TextView = itemView.findViewById(R.id.editCliente)
        val descricao: TextView = itemView.findViewById(R.id.editdescricao)
        val comentario: TextView = itemView.findViewById(R.id.editComentario)
        val numeroStars: RatingBar = itemView.findViewById(R.id.ratingbar)
        init {
            itemView.setOnClickListener{
                val intent = Intent(context, avaliacaoServico::class.java)
                intent.putExtra("cliente", cliente.text.toString())
                intent.putExtra("descricao", descricao.text.toString())
                context.startActivity(intent)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.lista_avaliacoes_cliente, parent,false)
        return  MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return OrdemServico.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.cliente.text = OrdemServico[position].Cliente
        holder.descricao.text = OrdemServico[position].descricao
        holder.comentario.text = OrdemServico[position].comentario
        holder.numeroStars.rating = OrdemServico[position].numeroStars?.toFloat()!!
    }

    private fun mensagem(msg:String){

        val builder = AlertDialog.Builder(context)
            builder.setTitle("Alerta!")
                .setMessage(msg)

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }
}