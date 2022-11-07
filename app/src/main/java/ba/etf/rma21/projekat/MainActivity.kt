package ba.etf.rma21.projekat

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.room.Room
import ba.etf.rma21.projekat.data.AppDatabase
import ba.etf.rma21.projekat.data.repositories.*
import ba.etf.rma21.projekat.data.view.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.net.URL
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigation: BottomNavigationView
    var nazivKviza = ""
    var rezultat = 8.5
    fun getBottomNav(): BottomNavigationView {
        return bottomNavigation;
    }

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.kvizovi -> {
                    val kvizovi: FragmentKvizovi = FragmentKvizovi.newInstance()
                    var poruka = supportFragmentManager.findFragmentByTag("poruka")
                    if (poruka != null && poruka.isVisible) openFragment(kvizovi)
                    else
                        openFragment(kvizovi)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.predmeti -> {
                    var poruka = supportFragmentManager.findFragmentByTag("poruka")
                    val kvizovi: FragmentKvizovi = FragmentKvizovi.newInstance()
                    if (poruka != null && poruka.isVisible) openFragment(kvizovi)
                    else {
                        var predmeti = FragmentPredmeti.newInstance()
                        openFragment(predmeti)
                    }
                    return@OnNavigationItemSelectedListener true
                }
                R.id.predajKviz -> {

                    if (rezultat == 8.5) {
                        val fragment = FragmentPoruka()
                        val bundle = Bundle()
                        bundle.putString("naziv kviza", nazivKviza)
                        bundle.putDouble("rezultat", 0.toDouble())
                        fragment.arguments = bundle
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.framePitanje, fragment).commit()
                    } else {
                        val fragment = FragmentPoruka()
                        val bundle = Bundle()
                        bundle.putString("naziv kviza", nazivKviza)
                        bundle.putDouble("rezultat", rezultat)
                        fragment.arguments = bundle
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.framePitanje, fragment).commit()
                    }
                    return@OnNavigationItemSelectedListener true
                }
                R.id.zaustaviKviz -> {
                    openFragment(FragmentKvizovi.newInstance())
                }
            }
            false
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val kvizovi: FragmentKvizovi = FragmentKvizovi.newInstance()
        setContentView(R.layout.activity_main)
        bottomNavigation = findViewById(R.id.bottomNav)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        bottomNavigation.selectedItemId = R.id.kvizovi
        supportFragmentManager.beginTransaction().replace(R.id.container, kvizovi).commit();
        val menu = bottomNavigation.menu
        menu.findItem(R.id.kvizovi).isVisible = true
        menu.findItem(R.id.predmeti).isVisible = true
        menu.findItem(R.id.zaustaviKviz).isVisible = false
        menu.findItem(R.id.predajKviz).isVisible = false
        AccountRepository.setContext(this.applicationContext)
        DBRepository.setContext(this.applicationContext)
        KvizRepository.setContext(this.applicationContext)
        OdgovorRepository.setContext(this.applicationContext)
        PitanjeKvizRepository.setContext(this.applicationContext)
        PredmetIGrupaRepository.setContext(this.applicationContext)
        PredmetRepository.setContext(this.applicationContext)
        TakeKvizRepository.setContext(this.applicationContext)
        CoroutineScope(Dispatchers.Main).launch {
        val action: String? = intent?.action
        val data: Uri? = intent?.data
        val pom = intent?.getStringExtra("payload")

        if (pom != null) {
                AccountRepository.postaviHash(pom)
            }

        }


    }

    fun openFragment(fragment1: Fragment) {

        supportFragmentManager.beginTransaction().replace(R.id.container, fragment1).commit()

    }

    override fun onBackPressed() {
        val kvizovi: FragmentKvizovi = FragmentKvizovi.newInstance()
        var poruka = supportFragmentManager.findFragmentByTag("poruka")
        if (poruka != null && poruka.isVisible) supportFragmentManager.beginTransaction()
            .replace(R.id.container, kvizovi).commit()
        if (bottomNavigation.selectedItemId == R.id.kvizovi) {
        }
        if (bottomNavigation.menu.findItem(R.id.zaustaviKviz).isVisible) openFragment(
            FragmentKvizovi.newInstance()
        )
        else if (bottomNavigation.selectedItemId == R.id.predajKviz)
        else bottomNavigation.setSelectedItemId(R.id.kvizovi)

    }

    fun setRezultatNaziv(naziv: String, rez: Double) {
        this.nazivKviza = naziv
        this.rezultat = rez
    }
}



