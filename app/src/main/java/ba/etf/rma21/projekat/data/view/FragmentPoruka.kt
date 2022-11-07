package ba.etf.rma21.projekat.data.view

import android.R.attr.defaultValue
import android.R.attr.key
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import ba.etf.rma21.projekat.R


class FragmentPoruka : Fragment() {
    private lateinit var poruka : TextView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.poruka_fragment, container, false)
        val b = this.arguments
        val grupa = b?.getString("naziv grupe")
        val predmet = b?.getString("naziv predmeta")
        val nazivKviza= b?.getString("naziv kviza")
        val rezultat= b?.getDouble("rezultat")
        val number2digits:Double = rezultat?.times(100.0)?.let { Math.round(it) }!! / 100.0
        poruka=view.findViewById(R.id.tvPoruka)
        if(nazivKviza==null)  poruka.text="Uspješno ste upisani u grupu "+ grupa +" predmeta "+ predmet+"!"
        else if(rezultat!=null){
            poruka.text="Završili ste kviz " +nazivKviza+"\nsa tačnosti "+ number2digits+"%"
        }
        return view
    }
    companion object {
        fun newInstance(): FragmentPoruka = FragmentPoruka()
    }
}