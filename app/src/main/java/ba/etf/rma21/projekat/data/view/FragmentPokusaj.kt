package ba.etf.rma21.projekat.data.view

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ba.etf.rma21.projekat.MainActivity
import ba.etf.rma21.projekat.R
import ba.etf.rma21.projekat.data.models.Pitanje
import ba.etf.rma21.projekat.viewmodel.PitanjeKvizViewModel
import ba.etf.rma21.projekat.viewmodel.SharedViewModel
import com.google.android.material.navigation.NavigationView
import kotlin.properties.Delegates


class FragmentPokusaj(list: List<Pitanje>) : Fragment() {
    var lista = list
    private lateinit var framePitanje: FrameLayout
    private lateinit var navigacijaPitanja: NavigationView
    private lateinit var pitanjeKvizViewModel : PitanjeKvizViewModel
    private lateinit var model :SharedViewModel
    private var kvizTakenId=0
    private  var listaTacnih = mutableListOf<Pitanje>()
    private  var listaNetacnih = mutableListOf<Pitanje>()
     var p =lista[0]
    var kviz=""
    var rezultat by Delegates.notNull<Double>()
    var broj=0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         val mOnNavigationItemSelectedListener = NavigationView.OnNavigationItemSelectedListener { item ->
            var brojac2=0;
            for(l in lista){
                brojac2++;
                if(brojac2.toString()==item.title.toString()){
                    p=l
                    openFragment(FragmentPitanje(l))
                }
            }
            return@OnNavigationItemSelectedListener true
            false
        }
        var view = inflater.inflate(R.layout.pokusaj_fragment, container, false)
        framePitanje=view.findViewById(R.id.framePitanje)
        navigacijaPitanja=view.findViewById(R.id.navigacijaPitanja)
        pitanjeKvizViewModel=PitanjeKvizViewModel(this@FragmentPokusaj::pom, this@FragmentPokusaj::onError)
        for(i in 0 until lista.size) {
            pitanjeKvizViewModel.writeDB(
                this.requireContext(), lista[i], onSuccess = ::onSuccess1,
                onError = ::onErrorPitanja
            )
        }
        var brojac=0
        for(p in lista) {
            brojac++;
            navigacijaPitanja.menu.add(R.menu.navigation2, brojac-1, brojac-1, brojac.toString())
            val menui=navigacijaPitanja.menu.getItem(brojac-1)
            val spanString = SpannableString(menui.title.toString())
            spanString.setSpan(
                ForegroundColorSpan(Color.WHITE),
                0,
                spanString.length,
                0
            ) // fix the color to white
            menui.title = spanString
        }
        val bundle = this.arguments
        if (bundle != null) {
            kviz = bundle.getString("naziv kviza").toString()
            kvizTakenId=bundle.getInt("kvizTakenId")

        }
        val main = activity as MainActivity
        val menu = main.getBottomNav().menu
        navigacijaPitanja.setNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        menu.findItem(R.id.kvizovi).isVisible=false
        menu.findItem(R.id.predmeti).isVisible=false
        menu.findItem(R.id.zaustaviKviz).isVisible=true
        menu.findItem(R.id.predajKviz).isVisible=true

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //super.onViewCreated(view, savedInstanceState)
        model = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        var br=0
        model.message.observe(viewLifecycleOwner, Observer {
            var menuItem = navigacijaPitanja.menu.getItem(lista.indexOf(p))
            if (it.equals("tacno")) {
                broj++
                listaTacnih.add(p)
                val spanString = SpannableString(menuItem.title.toString())
                spanString.setSpan(
                    ForegroundColorSpan(Color.GREEN),
                    0,
                    spanString.length,
                    0
                ) // fix the color to white
                menuItem.title = spanString
            } else {
                listaNetacnih.add(p)
                val spanString = SpannableString(menuItem.title.toString())
                spanString.setSpan(
                    ForegroundColorSpan(Color.RED),
                    0,
                    spanString.length,
                    0
                ) // fix the color to white
                menuItem.title = spanString

            }
            rezultat=(broj/(lista.size).toDouble())*100
            (activity as MainActivity).setRezultatNaziv(kviz, rezultat)



        })
    }



    fun openFragment(fragment1: Fragment) {
        val fragment=FragmentPoruka()
        val bundle = Bundle()
        bundle.putInt("kvizTakenId", kvizTakenId)
        //bundle.putString("naziv predmeta", predmet)
        fragment.arguments = bundle
        requireActivity().supportFragmentManager.beginTransaction().replace(R.id.framePitanje, fragment1).commit()

    }
    fun onSuccess1(message:String){
        val toast = Toast.makeText(this.context, "Spaseno", Toast.LENGTH_SHORT)
        toast.show()
    }
    fun onErrorPitanja() {
        val toast = Toast.makeText(this.requireContext(), "Error", Toast.LENGTH_SHORT)
        toast.show()
    }
    fun onError() {
        val toast = Toast.makeText(this.requireContext(), "Error", Toast.LENGTH_SHORT)
        toast.show()
    }
    fun pom(pitanja : List<Pitanje>){



    }

    companion object {
        fun newInstance(list: List<Pitanje>): FragmentPokusaj = FragmentPokusaj(list)
    }
}





