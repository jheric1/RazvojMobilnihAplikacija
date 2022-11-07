package ba.etf.rma21.projekat.data.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ba.etf.rma21.projekat.R
import ba.etf.rma21.projekat.data.models.Kviz
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
class KvizListAdapter( private var kvizovi : List<Kviz>,
                       private val onItemClicked: (kviz:Kviz) -> Unit
) : RecyclerView.Adapter<KvizListAdapter.KvizViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KvizViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.element, parent, false)
        return KvizViewHolder(view)
    }
    override fun getItemCount(): Int = kvizovi.size
    override fun onBindViewHolder(holder: KvizViewHolder, position: Int) {

        val formatter: DateFormat = SimpleDateFormat("dd.MM.yyyy")
        holder.nazivKviza.text = kvizovi[position].naziv;
        holder.trajanje.text = kvizovi[position].trajanje.toString() + " min"
        holder.datum.text = kvizovi[position].datumPocetka
        holder.nazivPredmeta.text=kvizovi[position].nazivPredmeta
        if(kvizovi[position].osvojeniBodovi==null)
            holder.osvojeniBodovi.text=""
        else holder.osvojeniBodovi.text=kvizovi[position].osvojeniBodovi.toString()
        val context: Context = holder.Image.getContext()
        val pom: Int = pomocna(kvizovi[position])
        if (pom == 1) {
            val m = context.getResources().getIdentifier("blue", "drawable", context.getPackageName())
            holder.Image.setImageResource(m)
            holder.datum.text = kvizovi[position].datumRada
        }
        if (pom == 2) {
            val m = context.getResources().getIdentifier("green", "drawable", context.getPackageName())
            holder.Image.setImageResource(m)
            if(kvizovi[position].datumKraj==null) holder.datum.text="unlimited"
            else
                holder.datum.text = kvizovi[position].datumKraj
        }
        if (pom == 3) {
            val m = context.getResources().getIdentifier("yellow", "drawable", context.getPackageName())
            holder.Image.setImageResource(m)
            holder.datum.text = kvizovi[position].datumPocetka
        }
        if (pom == 4) {
            val m = context.getResources().getIdentifier("red", "drawable", context.getPackageName())
            holder.Image.setImageResource(m)
            if(kvizovi[position].datumKraj==null) holder.datum.text="unlimited"
            else
                holder.datum.text = kvizovi[position].datumKraj
        }
        holder.itemView.setOnClickListener{ onItemClicked(kvizovi[position]) }
    }

    fun pomocna(kviz : Kviz) : Int{
        if(kviz.datumRada!=null) return 1;
        /*
        else if(kviz.datumPocetka.after(Calendar.getInstance().time)) return 3;
        else if(kviz.datumKraj==null || kviz.datumKraj.after(Calendar.getInstance().time)) return 2;
        else if(kviz.datumKraj.before(Calendar.getInstance().time)) return 4;

         */
        else return 0;
    }

    fun updateKvizovi(kvizovi: List<Kviz>) {
        this.kvizovi = kvizovi
        notifyDataSetChanged()
    }
    inner class KvizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val Image: ImageView = itemView.findViewById(R.id.imageView22)
        val nazivPredmeta: TextView = itemView.findViewById(R.id.nazivPredmeta)
        val nazivKviza : TextView=itemView.findViewById((R.id.nazivKviza))
        val datum : TextView=itemView.findViewById(R.id.datum)
        val trajanje : TextView=itemView.findViewById(R.id.trajanje)
        val osvojeniBodovi :TextView=itemView.findViewById(R.id.osvojeniBodovi)
    }
}

