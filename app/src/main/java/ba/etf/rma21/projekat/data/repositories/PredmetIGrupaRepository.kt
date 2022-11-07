package ba.etf.rma21.projekat.data.repositories

import android.content.Context
import ba.etf.rma21.projekat.data.AppDatabase
import ba.etf.rma21.projekat.data.models.Grupa
import ba.etf.rma21.projekat.data.models.Kviz
import ba.etf.rma21.projekat.data.models.Pitanje
import ba.etf.rma21.projekat.data.models.Predmet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL


class PredmetIGrupaRepository {
    companion object {
        private lateinit var context: Context
        fun setContext(_context: Context) {
            context = _context
        }
        fun getPredmeti(): List<Predmet> {
            val predmeti = arrayListOf<Predmet>()
            val url = URL(ApiConfig.baseURL + "/predmet")
            val conn = url.openConnection() as HttpURLConnection
            val result = conn.inputStream.bufferedReader().use { it.readText() } //4
            val jo = JSONArray(result)//5
            for (i in 0 until jo.length()) {//7
                val pitanje = jo.getJSONObject(i)
                val id = pitanje.getInt("id")
                val naziv = pitanje.getString("naziv")
                val godina = pitanje.getInt("godina")
                predmeti.add(Predmet(id, naziv, godina))

            }
            return predmeti//8
        }
        suspend fun getSviPredmeti(): List<Predmet>? {
            return withContext(Dispatchers.IO) {
                val predmeti = getPredmeti()
                return@withContext predmeti
            }

        }

         fun getGrupe(): List<Grupa> {
            val grupe = arrayListOf<Grupa>()
            val url1 = ApiConfig.baseURL + "/grupa"
            val url = URL(url1) //2
            (url.openConnection() as? HttpURLConnection)?.run { //3
                val result = this.inputStream.bufferedReader().use { it.readText() } //4
                val jo = JSONArray(result)//5
                for (i in 0 until jo.length()) {//7
                    val pitanje = jo.getJSONObject(i)
                    val id = pitanje.getInt("id")
                    val naziv = pitanje.getString("naziv")
                    val predmetId = pitanje.getInt("PredmetId")
                    grupe.add(Grupa(id, naziv, predmetId))

                }
            }
            return grupe
        }

        suspend fun getSveGrupe(): Result<List<Grupa>?> {
            return withContext(Dispatchers.IO) {
                val grupe = getGrupe()
                return@withContext Result.Success(grupe)
            }

        }

         fun getGrupeZaPredmet(idPredmeta: Int): List<Grupa>? {
            val grupe = getGrupe()
            val rez = mutableListOf<Grupa>()
            for (i in 0 until grupe!!.size) {
                if (grupe[i].PredmetId.equals(idPredmeta)) {
                    rez.add(grupe[i])
                }
            }
            return rez
        }

        suspend fun getGrupeZaPredmet1(id: Int): Result<List<Grupa>?> {
            return withContext(Dispatchers.IO) {
                val grupe = getGrupeZaPredmet(id)
                return@withContext Result.Success(grupe)
            }

        }

         fun getGrupePredmet(naziv: String): List<Grupa>? {
            val predmeti = getPredmeti()
            for (i in 0 until predmeti.size) {
                if (predmeti[i].naziv.equals(naziv)) return getGrupeZaPredmet(predmeti[i].id)
            }
            return null
        }

        suspend fun getGrupePredmet1(naziv: String): Result<List<Grupa>?> {
            return withContext(Dispatchers.IO) {
                val rez = getGrupePredmet(naziv)
                return@withContext Result.Success(rez)
            }
        }


        suspend fun upisiUGrupu(idGrupa: Int): Boolean? {
            var mess = ""
            val url =
                URL(ApiConfig.baseURL + "/grupa/" + idGrupa.toString() + "/student/" + AccountRepository.acHash)
            val conn = url.openConnection() as HttpURLConnection
            conn.setDoOutput(true);
            conn.run {
                requestMethod = "POST"
                val wr = OutputStreamWriter(getOutputStream());
                wr.write(idGrupa)
                BufferedReader(InputStreamReader(inputStream)).use {
                    val response = StringBuffer()
                    var inputLine = it.readLine()
                    while (inputLine != null) {
                        response.append(inputLine)
                        inputLine = it.readLine()
                    }
                    mess = response.toString()
                }
            }
            if (mess.contains("Student jheric1@etf.unsa.ba je dodan u grupu")) {
                System.out.println("Student jheric1@etf.unsa.ba je dodan u grupu")
                /* val sve= getGrupe()
                val predmeti= getPredmeti()
                for(i in 0 until sve!!.size){
                    if(sve[i].id.equals(idGrupa)){
                        writeGrupa(context, sve[i])
                        for(j in 0 until predmeti.size){
                            if(sve[i].PredmetId.equals(predmeti[j].id)) writePredmet(context, predmeti[j])
                        }
                    }
                }

                */
                return true
            } else return false
        }

        suspend fun upisiUGrupu1(id: Int): Result<Boolean?> {
            return withContext(Dispatchers.IO) {
                val bool = upisiUGrupu(id)
                return@withContext Result.Success(bool)
            }

        }

        suspend fun upisiUGrupu(naziv: String): Boolean? {
            val grupe = getGrupe()!!.toMutableList()
            val upisaneGrupe = getUpisaneGrupe()!!.toMutableList()
            grupe.removeAll(upisaneGrupe)
            for (i in 0 until grupe!!.size) {
                if (grupe[i].naziv.equals(naziv)) {
                    //writeGrupa(context, grupe[i])
                    return upisiUGrupu(grupe[i].id)

                }
            }
            return null
        }

        suspend fun upisiUGrupu1(naziv: String): Result<Boolean?> {
            return withContext(Dispatchers.IO) {
                val rez = upisiUGrupu(naziv)
                return@withContext Result.Success(rez)
            }
        }

         fun getUpisaneGrupe(): List<Grupa>? {
            val grupe = arrayListOf<Grupa>()
            val url1 = ApiConfig.baseURL + "/student/" + AccountRepository.acHash + "/grupa"
            val url = URL(url1) //2
            (url.openConnection() as? HttpURLConnection)?.run { //3
                val result = this.inputStream.bufferedReader().use { it.readText() } //4
                if (result.contains("Ne postoji account gdje je hash")) return null
                val jo = JSONArray(result)//5
                for (i in 0 until jo.length()) {//7
                    val grupa = jo.getJSONObject(i)
                    val id = grupa.getInt("id")
                    val naziv = grupa.getString("naziv")
                    val predmetId = grupa.getInt("PredmetId")
                    grupe.add(Grupa(id, naziv, predmetId))
                }
            }
            return grupe//8
        }

        suspend fun getUpisaneGrupe1(): Result<List<Grupa>?> {
            return withContext(Dispatchers.IO) {
                val rez = getUpisaneGrupe()
                return@withContext Result.Success(rez)
            }

        }

        suspend fun getPredmeteZaGodinu(godina: Int): List<Predmet>? {
            val predmeti = getPredmeti().toMutableList()
            val upisani = getUpisanePredmete().toMutableList()
            predmeti.removeAll(upisani)
            val rez = mutableListOf<Predmet>()
            for (i in 0 until predmeti.size) {
                if (predmeti[i].godina.equals(godina)) rez.add(predmeti[i])
            }
            return rez
        }

        suspend fun getPredmeteZaGodinu1(godina: Int): Result<List<Predmet>?> {
            return withContext(Dispatchers.IO) {
                val rez = getPredmeteZaGodinu(godina)
                return@withContext Result.Success(rez)
            }

        }

        suspend fun getUpisanePredmete(): List<Predmet> {
            val grupe = getUpisaneGrupe()
            val predmeti = getPredmeti()
            val rez = mutableListOf<Predmet>()
            for (i in 0 until grupe!!.size) {
                for (j in 0 until predmeti.size) {
                    if (grupe[i].PredmetId.equals(predmeti[j].id))
                        rez.add(predmeti[j])
                }
            }
            return rez
        }

        suspend fun getGrupaKvizId(id: Int): Grupa? {
            val grupe = getGrupe()
            val rez = mutableListOf<Grupa>()
            for (i in 0 until grupe!!.size) {
                if (KvizRepository.getKvizoviZaGrupu(grupe[i].id).stream().filter { t -> t == id }
                        .findAny().isPresent) return grupe[i]
            }
            return null

        }

        suspend fun getPredmetById(id: Int): Predmet? {
            val url1 = ApiConfig.baseURL + "/predmet/" + id
            val url = URL(url1) //2
            val conn = url.openConnection() as HttpURLConnection//3
            val result = conn.inputStream.bufferedReader().use { it.readText() } //4
            val jo = JSONObject(result)
            if (jo.has("message") && jo.getString("message")
                    .equals("Predmet not found.")
            ) return null
            return Predmet(jo.getInt("id"), jo.getString("naziv"), jo.getInt("godina"))
        }

        fun getPredmetNaziv(naziv: String): Predmet? {
            val predmeti = getPredmeti()
            for (i in 0 until predmeti.size) {
                if (predmeti[i].naziv.equals(naziv)) return predmeti[i]
            }
            return null
        }

        suspend fun getPredmetNaziv1(naziv: String): Result<Predmet?> {
            return withContext(Dispatchers.IO) {
                val rez = getPredmetNaziv(naziv)
                return@withContext Result.Success(rez)
            }

        }
        suspend fun writePredmet(context: Context,movie:Predmet) : String?{
            return withContext(Dispatchers.IO) {
                try{
                    var db = AppDatabase.getInstance(context)
                    db!!.PredmetDAO().insertAll(movie)
                    return@withContext "success"
                }
                catch(error:Exception){
                    return@withContext null
                }
            }
        }


        suspend fun writeGrupa(context: Context, grupa: Grupa): String? {
            return withContext(Dispatchers.IO) {
                try {
                    var db = AppDatabase.getInstance(context)
                    db!!.GrupaDAO().insertAll(grupa)
                    return@withContext "success"
                } catch (error: Exception) {
                    return@withContext null
                }
            }
        }

        suspend fun getGrupaNaziv(naziv: String): Grupa? {
            val grupe = getGrupe()
            System.out.println(grupe.size.toString()+" VELICINA GRUPA")
            for (i in 0 until grupe!!.size) {
                if (grupe[i].naziv.equals(naziv)) {
                    System.out.println(naziv+ "NASO")
                    return grupe[i]
                }
            }
            return null
        }

        suspend fun getGrupaNaziv1(naziv: String): Result<Grupa?> {
            return withContext(Dispatchers.IO) {
                val rez = getGrupaNaziv(naziv)
                return@withContext Result.Success(rez)
            }


        }
    }
}