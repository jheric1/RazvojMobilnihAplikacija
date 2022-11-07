package ba.etf.rma21.projekat.data.view

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ba.etf.rma21.projekat.MainActivity
import ba.etf.rma21.projekat.R
import ba.etf.rma21.projekat.data.models.Korisnik
import ba.etf.rma21.projekat.data.models.Kviz
import ba.etf.rma21.projekat.data.models.KvizTaken
import ba.etf.rma21.projekat.data.models.Pitanje
import ba.etf.rma21.projekat.data.repositories.AccountRepository
import ba.etf.rma21.projekat.data.repositories.DBRepository
import ba.etf.rma21.projekat.data.repositories.KvizRepository
import ba.etf.rma21.projekat.viewmodel.KvizListViewModel
import ba.etf.rma21.projekat.viewmodel.PitanjeKvizViewModel
import ba.etf.rma21.projekat.viewmodel.SharedViewModel
import ba.etf.rma21.projekat.viewmodel.TakeKvizViewModel
import java.util.*

class FragmentKvizovi : Fragment() {
    private lateinit var listaKvizova: RecyclerView
    private lateinit var kvizListAdapter: KvizListAdapter
    private lateinit var kvizListViewModel: KvizListViewModel
    private lateinit var filterKvizova: Spinner
    private lateinit var pitanjeKvizViewModel : PitanjeKvizViewModel
    private lateinit var takeKvizViewModel: TakeKvizViewModel
    private var nazivKviza=""
    private lateinit var kvizovi1 :List<Kviz>
    private lateinit var model : SharedViewModel
    private var kvizTakenId=0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.kvizovi_fragment, container, false)
        val main = activity as MainActivity
        val menu = main.getBottomNav().menu
        model = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        kvizListViewModel = KvizListViewModel(this@FragmentKvizovi::searchDone, this@FragmentKvizovi::searchDone2,null,this@FragmentKvizovi::onError)
        takeKvizViewModel= TakeKvizViewModel(this@FragmentKvizovi::pom2,this@FragmentKvizovi::onError)
        menu.findItem(R.id.kvizovi).isVisible = true
        menu.findItem(R.id.predmeti).isVisible = true
        menu.findItem(R.id.zaustaviKviz).isVisible = false
        menu.findItem(R.id.predajKviz).isVisible = false
        listaKvizova = view.findViewById(R.id.listaKvizova)
        listaKvizova.layoutManager = GridLayoutManager(
            activity,
            2
        )

        KvizListAdapter(arrayListOf()) {
                kviz -> showFragmentPokusaj(kviz)
            takeKvizViewModel.zapocniKviz(kviz.id)}


        pitanjeKvizViewModel=PitanjeKvizViewModel(this@FragmentKvizovi::pom, this@FragmentKvizovi::onError)
        kvizListAdapter = KvizListAdapter(arrayListOf()) {
                kviz -> showFragmentPokusaj(kviz)
              takeKvizViewModel.zapocniKviz(kviz.id)
        }
        listaKvizova.adapter = kvizListAdapter
/*
        context?.let {
            kvizListViewModel.getFavorites(
                it,onSuccess = ::onSuccess,
                onError = ::onError)
        }

 */

        //kvizListViewModel.getMojiKvizovi()
        //writeDB()
        //kvizListAdapter.updateKvizovi(kvizListViewModel.getAll())
        val strings = arrayOf(
            "Svi moji kvizovi",
            "Svi kvizovi",
            "Urađeni kvizovi",
            "Budući kvizovi",
            "Prošli kvizovi"
        )
        val arrayAdapter = ArrayAdapter(view.context, android.R.layout.simple_spinner_item, strings)
        filterKvizova = view.findViewById(R.id.filterKvizova)
        filterKvizova.adapter = arrayAdapter

        filterKvizova.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                if (position == 0) kvizListViewModel.getMojiKvizovi()
                if (position == 1) kvizListViewModel.getAll()
                if (position == 2) kvizListAdapter.updateKvizovi(kvizListViewModel.getDone())
                if (position == 3) kvizListAdapter.updateKvizovi((kvizListViewModel.getFuture()))
                if (position == 4) kvizListAdapter.updateKvizovi(kvizListViewModel.getNotTaken())


            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
        return view
    }

/*
       override fun onResume() {
           super.onResume()

           if (filterKvizova.selectedItemPosition == 0) kvizListAdapter.updateKvizovi(kvizListViewModel.getMojiKvizovi())
           else if (filterKvizova.selectedItemPosition == 1) searchDone()
           else if (filterKvizova.selectedItemPosition == 2) kvizListAdapter.updateKvizovi(kvizListViewModel.getDone())
           else if (filterKvizova.selectedItemPosition == 3) kvizListAdapter.updateKvizovi((kvizListViewModel.getFuture()))
           else if (filterKvizova.selectedItemPosition == 4) kvizListAdapter.updateKvizovi(kvizListViewModel.getNotTaken())
       }
   */

    companion object {
        fun newInstance(): FragmentKvizovi = FragmentKvizovi()
    }



    private fun showFragmentPokusaj(kviz: Kviz) {
        nazivKviza=kviz.naziv
        pitanjeKvizViewModel.getPitanja(kviz.id)

    }
    fun pom(pitanja : List<Pitanje>){
        val fragment = FragmentPokusaj.newInstance(pitanja)
        val bundle = Bundle()
        bundle.putString("naziv kviza", nazivKviza)
        bundle.putInt("kvizTakenId", kvizTakenId)
        fragment.arguments = bundle
        requireActivity().supportFragmentManager.beginTransaction().replace(R.id.container, fragment)
            .commit()

    }
    fun pom2(kviz: KvizTaken){
       model.kvizID(kviz.id)

    }
    fun writeDB(){
        for(i in 0 until kvizovi1.size) {
            kvizListViewModel.writeDB3(
                this.requireContext(), kvizovi1[i], onSuccess = ::onSuccessPitanja,
                onError = ::onError1
            )
        }
    }
    fun onSuccess(kvizovi: List<Kviz>) {
        kvizListAdapter.updateKvizovi(kvizovi)
    }
    fun onError() {
        val toast = Toast.makeText(context, "Search error", Toast.LENGTH_SHORT)
        toast.show()
    }
    fun onSuccess1(message:String){
        val toast = Toast.makeText(this.context, "Spaseno", Toast.LENGTH_SHORT)
        toast.show()
    }
    fun onSuccessPitanja(message:String){
        val toast = Toast.makeText(this.context, "Spaseno", Toast.LENGTH_SHORT)
        toast.show()
    }
    fun onError1() {
        val toast = Toast.makeText(this.requireContext(), "Error", Toast.LENGTH_SHORT)
        toast.show()
    }

    fun searchDone(kvizovi: List<Kviz>) {
        kvizListAdapter.updateKvizovi(kvizovi)
    }
    fun searchDone2(kvizovi: List<Kviz>) {
        kvizListAdapter.updateKvizovi(kvizovi)
        for(i in 0 until kvizovi.size) {
            kvizListViewModel.writeDB3(
                this.requireContext(), kvizovi[i], onSuccess = ::onSuccess1,
                onError = ::onError1
            )
        }
    }
}
