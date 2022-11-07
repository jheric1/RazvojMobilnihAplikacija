package ba.etf.rma21.projekat.data.repositories


import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import ba.etf.rma21.projekat.data.AppDatabase
import ba.etf.rma21.projekat.data.models.Grupa
import ba.etf.rma21.projekat.data.models.Kviz
import ba.etf.rma21.projekat.data.models.Odgovor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

sealed class Result<out R> {

    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}

class KvizRepository {
    companion object {
        private lateinit var context: Context
        fun setContext(_context: Context) {
            context = _context
        }

        suspend fun getAl1l(context: Context): List<Kviz> {
            return withContext(Dispatchers.IO) {
                var db = AppDatabase.getInstance(context)
                var movies = db!!.KvizDAO().getAll()
                return@withContext movies
            }
        }

        suspend fun getMyKvizes(): Result<List<Kviz>?> {
            return withContext(Dispatchers.IO) {
                val kvizovi = getUpisani()
                if(kvizovi==null) return@withContext Result.Error(Exception("Cannot open HttpURLConnection"))
                else
                return@withContext Result.Success(kvizovi)
            }

        }

        fun getDone(): List<Kviz> {
            return emptyList()
        }

        fun getFuture(): List<Kviz> {
            return emptyList()
        }

        fun getNotTaken(): List<Kviz> {
            return emptyList()
        }

        suspend fun getSve(): Result<List<Kviz>?> {
            return withContext(Dispatchers.IO) {
                val kvizovi = getAll()
                if(kvizovi==null) return@withContext Result.Error(Exception("Cannot open HttpURLConnection"))
                else
                return@withContext Result.Success(kvizovi)
            }
        }
        @RequiresApi(Build.VERSION_CODES.O)
        suspend fun getUpisaniDB() : List<Kviz>{
            DBRepository.updateNow()
            val db=AppDatabase.getInstance(context)
            return db.KvizDAO().getAll()
        }

        suspend fun getAll(): List<Kviz> {
            val kvizovi = mutableListOf<Kviz>()
            val url1 = ApiConfig.baseURL + "/kviz"
            val url = URL(url1) //2
            (url.openConnection() as? HttpURLConnection)?.run { //3
                val result = this.inputStream.bufferedReader().use { it.readText() } //4
                val jo = JSONArray(result)//5
                for (i in 0 until jo.length()) {//7
                    val kviz = jo.getJSONObject(i)
                    val id = kviz.getInt("id")
                    val naziv = kviz.getString("naziv")
                    val datumPocetak = kviz.getString("datumPocetak")
                    val date1: Date = SimpleDateFormat("yyyy-MM-dd").parse(datumPocetak)
                    var datumKraj = ""
                    if (!kviz.isNull("datumKraj"))
                        datumKraj = kviz.getString("datumKraj")
                    val trajanje = kviz.getInt("trajanje")
                    val grupa = PredmetIGrupaRepository.getGrupaKvizId(id)
                    val predmet = PredmetIGrupaRepository.getPredmetById(grupa!!.PredmetId)
                    kvizovi.add(
                        Kviz(
                            id,
                            naziv,
                            predmet!!.naziv,
                            datumPocetak,
                            datumKraj,
                            "null",
                            trajanje,
                            grupa.naziv,
                            null
                        )
                    )
                }
            }
            return kvizovi
        }

        suspend fun getById(id: Int): Kviz? {
            val url1 = ApiConfig.baseURL + "/kviz/" + id
            val url = URL(url1) //2
            val conn = url.openConnection() as HttpURLConnection
            val result = conn.inputStream.bufferedReader().readText()  //4
            if (result.contains("Kviz not found")) return null
            val kviz = JSONObject(result)
            val id = kviz.getInt("id")
            val naziv = kviz.getString("naziv")
            val datumPocetak = kviz.getString("datumPocetak")
            var datumKraj = ""
            if (!kviz.isNull("datumKraj"))
                datumKraj = kviz.getString("datumKraj")
            val trajanje = kviz.getInt("trajanje")
            val grupa = PredmetIGrupaRepository.getGrupaKvizId(id)
            val predmet = PredmetIGrupaRepository.getPredmetById(grupa!!.PredmetId)
            return Kviz(
                id,
                naziv,
                predmet!!.naziv,
                datumPocetak,
                datumKraj,
                "null",
                trajanje,
                grupa.naziv,
                null
            )
        }

        suspend fun getUpisani(): List<Kviz>? {
            val grupe = PredmetIGrupaRepository.getUpisaneGrupe()
            val kvizovi = arrayListOf<Kviz>()
            if(grupe==null) return null
            for (i in 0 until grupe!!.size) {
                val url2 = ApiConfig.baseURL + "/grupa/" + grupe[i].id + "/kvizovi"
                val url3 = URL(url2) //2
                (url3.openConnection() as? HttpURLConnection)?.run { //3
                    val result = this.inputStream.bufferedReader().use { it.readText() } //4
                    val jo = JSONArray(result)//5
                    for (i in 0 until jo.length()) {//7
                        val kviz = jo.getJSONObject(i)
                        val id = kviz.getInt("id")
                        val naziv = kviz.getString("naziv")
                        val datumPocetak = kviz.getString("datumPocetak")
                        val date1: Date = SimpleDateFormat("yyyy-MM-dd").parse(datumPocetak)
                        var datumKraj: Date? = null
                        if (!kviz.isNull("datumKraj"))
                            datumKraj =
                                SimpleDateFormat("yyyy-mm-ddThh:mm:ss").parse(kviz.getString("datumKraj"))
                        val trajanje = kviz.getInt("trajanje")
                        val grupa = PredmetIGrupaRepository.getGrupaKvizId(id)
                        val predmet = PredmetIGrupaRepository.getPredmetById(grupa!!.PredmetId)
                        kvizovi.add(
                            Kviz(
                                id,
                                naziv,
                                predmet!!.naziv,
                                date1.toString(),
                                datumKraj.toString(),
                                "null",
                                trajanje,
                                grupa.naziv,
                                null
                            )
                        )
                    }
                }
            }
            return kvizovi
        }

        suspend fun getKvizoviZaGrupu(id: Int): List<Int> {
            val ids = mutableListOf<Int>()
            val url2 = ApiConfig.baseURL + "/grupa/" + id + "/kvizovi"
            val url3 = URL(url2) //2
            (url3.openConnection() as? HttpURLConnection)?.run { //3
                val result = this.inputStream.bufferedReader().use { it.readText() } //4
                val jo = JSONArray(result)//5
                for (i in 0 until jo.length()) {//7
                    val kviz = jo.getJSONObject(i)
                    ids.add(kviz.getInt("id"))
                }
            }
            return ids
        }
        suspend fun getKvizoviZaGrupu2(naziv: String): List<Kviz> {
            val grupa=PredmetIGrupaRepository.getGrupaNaziv(naziv)
            val kvizovi = arrayListOf<Kviz>()
            val url2 = ApiConfig.baseURL + "/grupa/" + grupa!!.id + "/kvizovi"
            val url3 = URL(url2) //2
            (url3.openConnection() as? HttpURLConnection)?.run { //3
                val result = this.inputStream.bufferedReader().use { it.readText() } //4
                val jo = JSONArray(result)//5
                for (i in 0 until jo.length()) {//7
                    val kviz = jo.getJSONObject(i)
                    val id = kviz.getInt("id")
                    val naziv = kviz.getString("naziv")
                    val datumPocetak = kviz.getString("datumPocetak")
                    val date1: Date = SimpleDateFormat("yyyy-MM-dd").parse(datumPocetak)
                    var datumKraj: Date? = null
                    if (!kviz.isNull("datumKraj"))
                        datumKraj =
                            SimpleDateFormat("yyyy-mm-ddThh:mm:ss").parse(kviz.getString("datumKraj"))
                    val trajanje = kviz.getInt("trajanje")
                    val grupa = PredmetIGrupaRepository.getGrupaKvizId(id)
                    val predmet = PredmetIGrupaRepository.getPredmetById(grupa!!.PredmetId)
                    kvizovi.add(
                        Kviz(
                            id,
                            naziv,
                            predmet!!.naziv,
                            date1.toString(),
                            datumKraj.toString(),
                            "null",
                            trajanje,
                            grupa.naziv,
                            null
                        )
                    )
                }
            }
            return kvizovi
        }
        suspend fun getKvizoviZaGrupu3(naziv : String): Result<List<Kviz>>{
            return withContext(Dispatchers.IO) {
                val kvizovi = getKvizoviZaGrupu2(naziv)
                System.out.println(kvizovi.size.toString()+"VELICINA KVIZOVAAAAAA")
                    return@withContext Result.Success(kvizovi)
            }

        }

        suspend fun writeKviz(context: Context,kvizovi: Kviz) : String?{
            return withContext(Dispatchers.IO) {
                try{
                    var db = AppDatabase.getInstance(context)
                    db!!.KvizDAO().insertAll(kvizovi)
                    return@withContext "success"
                }
                catch(error:Exception){
                    return@withContext null
                }
            }
        }
        suspend fun getKvizovi(context: Context) : List<Kviz> {
            return withContext(Dispatchers.IO) {
                var db = AppDatabase.getInstance(context)
                var movies = db!!.KvizDAO().getAll()
                return@withContext movies
            }
        }
    }


    }
