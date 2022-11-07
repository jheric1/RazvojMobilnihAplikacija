package ba.etf.rma21.projekat.data.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.room.Room
import ba.etf.rma21.projekat.R
import ba.etf.rma21.projekat.data.AppDatabase
import ba.etf.rma21.projekat.data.models.Grupa
import ba.etf.rma21.projekat.data.models.Korisnik
import ba.etf.rma21.projekat.data.models.Kviz
import ba.etf.rma21.projekat.data.models.Predmet
import ba.etf.rma21.projekat.data.repositories.*
import ba.etf.rma21.projekat.viewmodel.GrupeViewModel
import ba.etf.rma21.projekat.viewmodel.KvizListViewModel
import ba.etf.rma21.projekat.viewmodel.PredmetIGrupaViewModel
import ba.etf.rma21.projekat.viewmodel.PredmetViewModel
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.properties.Delegates



class FragmentPredmeti : Fragment() {
    lateinit var odabirGodina: Spinner
    lateinit var odabirPredmet: Spinner
    lateinit var odabirGrupa: Spinner
    lateinit var  arrayAdapter2 : ArrayAdapter<String>
    lateinit var  arrayAdaptergrupe : ArrayAdapter<String>
    lateinit var predmet1 : Predmet
    lateinit var grupa1 :Grupa
    lateinit var kvizovi1: List<Kviz>
    private lateinit var db: AppDatabase
    val predmetiStrings2 = mutableListOf<String>()
    val grupeStrings = mutableListOf<String>()
    val godine = arrayOf("", "1", "2", "3", "4", "5")
    val predmetViewModel =PredmetViewModel(this@FragmentPredmeti::predmetNadjen, this@FragmentPredmeti::onError1)
    val kvizViewModel =KvizListViewModel(null, null,this@FragmentPredmeti::kvizoviPronadjeni, null)
    val grupaViewModel = GrupeViewModel(this@FragmentPredmeti::grupaNadjena, this@FragmentPredmeti::onError1)
    val predmetIGrupaViewModel =PredmetIGrupaViewModel(this@FragmentPredmeti::searchDone, this@FragmentPredmeti::onError2,this@FragmentPredmeti::searchDone3, this@FragmentPredmeti::searchDone2)
    lateinit var upisiMe : Button
    lateinit var godina : String
    lateinit var predmet : String
    lateinit var grupa : String
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.upisi_predmet_fragment, container, false)
        val arrayAdapter = ArrayAdapter(view.context, android.R.layout.simple_spinner_item, godine)
        arrayAdapter2 = ArrayAdapter(view.context, android.R.layout.simple_spinner_item, predmetiStrings2)
        arrayAdaptergrupe = ArrayAdapter(view.context, android.R.layout.simple_spinner_item, grupeStrings)

        upisiMe = view.findViewById(R.id.dodajPredmetDugme)
        odabirGodina = view.findViewById(R.id.odabirGodina)
        odabirPredmet = view.findViewById(R.id.odabirPredmet)
        odabirGrupa = view.findViewById(R.id.odabirGrupa)
        odabirGodina.adapter = arrayAdapter

        if (Korisnik.godina != null) {
            odabirGodina.setSelection(Korisnik.godina!!)
        }

        odabirGodina.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    odabirPredmet.isEnabled = false; odabirGrupa.isEnabled =
                        false; upisiMe.isClickable = false
                }
                if (position != 0) {
                    odabirPredmet.isEnabled = true
                    predmetIGrupaViewModel.getPredmeteZaGodinu(godine[position].toInt())
                    odabirPredmet.adapter = arrayAdapter2;
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
        odabirPredmet.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) odabirGrupa.isEnabled = false
                if (position != 0) {
                    odabirGrupa.isEnabled = true
                    predmetIGrupaViewModel.getGrupePredmet(odabirPredmet.getItemAtPosition(position).toString())
                    predmet=odabirPredmet.getItemAtPosition(position).toString()
                    predmetViewModel.getPredmetNaziv(predmet)
                    odabirGrupa.adapter = arrayAdaptergrupe;
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        odabirGrupa.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) upisiMe.isClickable = false
                if (position != 0) {
                    upisiMe.isClickable = true
                    grupa = odabirGrupa.getItemAtPosition(position).toString()
                    grupaViewModel.getGrupaNaziv(odabirGrupa.getItemAtPosition(position).toString())
                    System.out.println(
                        odabirGrupa.getItemAtPosition(position).toString() + "NAZIV GRUPE"
                    )
                    System.out.println("prvo")
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
        upisiMe.setOnClickListener {
            /* Korisnik.godina = godina.toInt()
             val novi = kvizListViewModel.getAll().minus(kvizListViewModel.getMojiKvizovi())
                 .filter { kviz -> kviz.nazivPredmeta.equals(predmet) }
                 .filter { kviz -> kviz.nazivGrupe.equals(grupa) }
             Korisnik.mojiKvizovi = Korisnik.mojiKvizovi.plus(novi)
             val noviPredmeti =
                 predmetViewModel.getAll().filter { predmet1 -> predmet1.naziv.equals(predmet) }
                     .filter { predmet1 -> predmet1.godina.equals(godina.toInt()) }
             val noveGrupe = Grupa(grupa, predmet)
             predmetViewModel.getUpisani().toMutableList().add(noviPredmeti.last())
             Korisnik.predmeti = Korisnik.predmeti.plus(noviPredmeti)
             Korisnik.grupe = Korisnik.grupe.plus(noveGrupe)
             odabirGrupa.setSelection(0)
             odabirPredmet.setSelection(0)

             */
            kvizViewModel.getKvizoviZaGrupu(grupa)
            predmetIGrupaViewModel.UpisiUGrupu(grupa)
            writeDB()
            writeDB2()



        }
        return view
    }

    fun save() {
        odabirPredmet.setSelection(Korisnik.pozicija2!!)
        odabirGrupa.setSelection(Korisnik.pozicija3!!)
    }

    fun openFragment(fragment1: Fragment, fragment2: Fragment) {
        if (!fragment2.isHidden) {
            requireActivity().supportFragmentManager.beginTransaction().hide(fragment2).commit()
            if (fragment1.isHidden)
                requireActivity().supportFragmentManager.beginTransaction().show(fragment1).commit()
        }
    }
    companion object {
        fun newInstance(): FragmentPredmeti = FragmentPredmeti()
    }
    fun searchDone(predmeti: List<Predmet>) {
        predmetiStrings2.clear()
        predmetiStrings2.add(" ")
        odabirPredmet.setSelection(0)
        for(i in 0 until predmeti.size) {
            predmetiStrings2.add(predmeti[i].naziv)
        }
        arrayAdapter2.notifyDataSetChanged()
    }
    fun searchDone2(grupe: List<Grupa>) {
        grupeStrings.clear()
        grupeStrings.add(" ")
        odabirGrupa.setSelection(0)
        for(i in 0 until grupe.size) {
            grupeStrings.add(grupe[i].naziv)
        }
        arrayAdaptergrupe.notifyDataSetChanged()

    }

    fun predmetNadjen(predmet: Predmet) {
        predmet1=predmet

    }
    fun grupaNadjena(grupa: Grupa) {
        grupa1=grupa

    }
    fun kvizoviPronadjeni(kvizovi: List<Kviz>) {
        kvizovi1=kvizovi

    }
    fun searchDone3(bool: Boolean) {
        if(bool==true){
            val fragment = FragmentPoruka()
            val bundle = Bundle()
            bundle.putString("naziv grupe", grupa)
            bundle.putString("naziv predmeta", predmet)
            fragment.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
        }

    }
    fun writeDB(){
        predmetIGrupaViewModel.writeDB(
            requireContext(),predmet1,onSuccess = ::onSuccess1,
            onError = ::onError)
    }
    fun onSuccess1(message:String){
        val toast = Toast.makeText(this.requireContext(), "Spaseno", Toast.LENGTH_SHORT)
        toast.show()
        //addFavorite.visibility= View.GONE
    }
    fun onError() {
        val toast = Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT)
        toast.show()
    }
    fun writeDB2(){
        predmetIGrupaViewModel.writeDB2(this.requireContext(), grupa1, onSuccess = ::onSuccess1,
            onError = ::onError3)
    }
    fun onError1() {
        val toast = Toast.makeText(this.context, "Error predmet", Toast.LENGTH_SHORT)
        toast.show()
    }
    fun onError3() {
        val toast = Toast.makeText(this.context, "Error grupa", Toast.LENGTH_SHORT)
        toast.show()
    }
    fun onError2() {
        val toast = Toast.makeText(context, "Search error", Toast.LENGTH_SHORT)
        toast.show()
    }

}



