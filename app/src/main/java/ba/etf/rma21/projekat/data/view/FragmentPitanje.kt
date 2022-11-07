package ba.etf.rma21.projekat.data.view

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ba.etf.rma21.projekat.R
import ba.etf.rma21.projekat.data.models.Kviz
import ba.etf.rma21.projekat.data.models.Odgovor
import ba.etf.rma21.projekat.data.models.Pitanje
import ba.etf.rma21.projekat.viewmodel.OdgovorViewModel
import ba.etf.rma21.projekat.viewmodel.PitanjeKvizViewModel
import ba.etf.rma21.projekat.viewmodel.SharedViewModel
import com.google.android.material.navigation.NavigationView


class FragmentPitanje(p: Pitanje) : Fragment() {
    var pitanje=p
    private lateinit var tekstPitanja : TextView
    private lateinit var odgovoriLista : ListView
    private lateinit var navigationView: NavigationView
    private lateinit var pitanjeKvizViewModel : PitanjeKvizViewModel
    private lateinit var odgovorViewModel: OdgovorViewModel
    private var kvizTakenId=0
    private lateinit var model :SharedViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.pitanje_fragment, container, false)
        tekstPitanja=view.findViewById(R.id.tekstPitanja)
        tekstPitanja.text=pitanje.tekstPitanja
        odgovoriLista=view.findViewById(R.id.odgovoriLista)
        odgovorViewModel= OdgovorViewModel(this@FragmentPitanje::searchDone, this@FragmentPitanje::searchDone2, this@FragmentPitanje::onError)
        model = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        val adapter = ArrayAdapter(view.context, android.R.layout.simple_list_item_1, pitanje.opcije.split(",").toTypedArray())
        odgovoriLista.adapter=adapter
        model.id.observe(
            viewLifecycleOwner,
            Observer {
                kvizTakenId = it

            })
                    odgovoriLista . setOnItemClickListener { parent, view, position, id ->
                odgovorViewModel.postaviOdgovorKviz(kvizTakenId, pitanje.id, position)
                if (position == pitanje.tacan) {
                    odgovoriLista[position].setBackgroundColor(Color.parseColor("#3DDC84"))
                    model.sendMessage("tacno")
                } else {
                    odgovoriLista[position].setBackgroundColor(Color.parseColor("#DB4F3D"))
                    odgovoriLista[pitanje.tacan].setBackgroundColor(Color.parseColor("#3DDC84"))
                    model.sendMessage("netacno")
                }
                        odgovoriLista.isEnabled = false
            }
            return view
    }
    companion object {
        fun newInstance(p: Pitanje):  FragmentPitanje = FragmentPitanje(p)
    }
    fun onError() {
        val toast = Toast.makeText(this.requireContext(), "Error", Toast.LENGTH_SHORT)
        toast.show()
    }

    fun searchDone(kvizovi: Int) {

    }
    fun searchDone2(kvizovi: List<Odgovor>) {

        }
}

