package ba.etf.rma21.projekat

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import ba.etf.rma21.projekat.data.AppDatabase
import ba.etf.rma21.projekat.data.repositories.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.io.IOException
import java.net.URL

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class DBTests {

    private val countOdgovor = "SELECT COUNT(*) AS broj_odgovora FROM Odgovor"
    private val countPredmet = "SELECT COUNT(*) AS broj_predmeta FROM Predmet"
    private val countGrupa = "SELECT COUNT(*) AS broj_grupa FROM Grupa"
    private val countKviz = "SELECT COUNT(*) AS broj_kvizova FROM Kviz"

    private val describeOdgovor = "pragma table_info('Odgovor')"
    private val describePredmet = "pragma table_info('Predmet')"
    private val describeGrupa = "pragma table_info('Grupa')"
    private val describeKviz = "pragma table_info('Kviz')"
    private val describeAccount = "pragma table_info('Account')"
    private val describePitanje = "pragma table_info('Pitanje')"
    private val kolone = mapOf(
        "Odgovor" to arrayListOf("id", "odgovoreno"),
        "Grupa" to arrayListOf("id", "naziv"),
        "Account" to arrayListOf("acHash", "lastUpdate"),
        "Predmet" to arrayListOf("id", "naziv", "godina"),
        "Kviz" to arrayListOf("id", "naziv", "datumPocetka", "datumKraj", "trajanje"),
        "Pitanje" to arrayListOf("id", "naziv", "tekstPitanja", "opcije", "tacan")
    )

    companion object {
        var pocentiHash: String = ""
        private lateinit var db: AppDatabase
        private lateinit var context: Context
        private var ktid: Int = 0

        @BeforeClass @JvmStatic
        fun createDb() = runBlocking {

            context = ApplicationProvider.getApplicationContext<Context>()
            AccountRepository.setContext(context)
            DBRepository.setContext(context)
            OdgovorRepository.setContext(context)
            PredmetIGrupaRepository.setContext(context)

            var client: OkHttpClient = OkHttpClient()
            var builder: Request.Builder = Request.Builder()
                .url(URL(ApiConfig.baseURL + "/student/" + AccountRepository.getHash() + "/upisugrupeipokusaji"))
                .delete()
            var request: Request = builder.build()
            withContext(Dispatchers.IO) {
                var response: Response = client.newCall(request).execute()
                var odgovor: String = response.body().toString()
            }

            db = Room.inMemoryDatabaseBuilder(
                context, AppDatabase::class.java
            ).build()
            AppDatabase.setInstance(db)

        }

        @AfterClass @JvmStatic
        @Throws(IOException::class)
        fun closeDb() {
            //db.close()
        }
    }

    private fun executeCountAndCheck(query: String, column: String, value: Long) {
        var rezultat = db.query(SimpleSQLiteQuery(query))
        rezultat.moveToNext()
        var brojOdgovora = rezultat.getLong(rezultat.getColumnIndex(column))
        MatcherAssert.assertThat(brojOdgovora, `is`(equalTo(value)))
    }

    private fun checkColumns(query: String, naziv: String) {
        var rezultat = db.query(SimpleSQLiteQuery(query))
        val list = (1..rezultat.count).map {
            rezultat.moveToNext()
            rezultat.getString(1)
        }
        assertThat(list, hasItems(*kolone[naziv]!!.toArray()))
    }



    @Test
    fun t00_getHash() = runBlocking {
        pocentiHash = AccountRepository.getHash()
    }

    @Test
    fun t01_firstTest() = runBlocking {
        AccountRepository.postaviHash("testHash")
        MatcherAssert.assertThat(
            AccountRepository.getHash(),
            `is`(equalTo("testHash"))
        )
    }
    @Test
    fun t02_resetACHash() = runBlocking {
        AccountRepository.postaviHash(pocentiHash)
        MatcherAssert.assertThat(
            AccountRepository.getHash(),
            `is`(equalTo(pocentiHash))
        )
    }
    @Test
    fun t03_checkUpdateWorks() = runBlocking {
        val prije = DBRepository.updateNow()
        assertThat(prije, equalTo(false))
        PredmetIGrupaRepository.upisiUGrupu(1)
        val kt = TakeKvizRepository.zapocniKviz(1)
        assertThat(kt, notNullValue())
        ktid = kt!!.id


        val poslije = DBRepository.updateNow()
        assertThat(poslije, equalTo(true))
    }

    @Test
    fun t04_postaviOdgovor() = runBlocking {
        val postotak = OdgovorRepository.postaviOdgovorKviz(ktid, 1, 0)
        assertThat(postotak, equalTo(50))
        executeCountAndCheck(countOdgovor, "broj_odgovora", 1)
    }

    @Test
    fun t05_ponovljeniOdgovorSeNeUpisuje() = runBlocking {
        val postotak = OdgovorRepository.postaviOdgovorKviz( ktid, 1, 0)
        assertThat(postotak, equalTo(50))
        executeCountAndCheck(countOdgovor, "broj_odgovora", 1)
    }

    @Test
    fun t06_tabeleImajuPotrebneKolone() = runBlocking {
        checkColumns(describePredmet, "Predmet")
        checkColumns(describeAccount, "Account")
        checkColumns(describeGrupa, "Grupa")
        checkColumns(describeKviz, "Kviz")
        checkColumns(describeOdgovor, "Odgovor")
        checkColumns(describePitanje, "Pitanje")
    }



    @Test
    @Throws(IOException::class)
    fun t07_checkIfDatabaseIsClear() = runBlocking {
        AccountRepository.postaviHash("testHash")
        executeCountAndCheck(countOdgovor, "broj_odgovora", 0)
        executeCountAndCheck(countPredmet, "broj_predmeta", 0)
        executeCountAndCheck(countKviz, "broj_kvizova", 0)
        executeCountAndCheck(countGrupa, "broj_grupa", 0)
    }


}