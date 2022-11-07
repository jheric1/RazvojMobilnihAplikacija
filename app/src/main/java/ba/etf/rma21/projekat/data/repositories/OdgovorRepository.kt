package ba.etf.rma21.projekat.data.repositories

import android.content.Context
import ba.etf.rma21.projekat.data.AppDatabase
import ba.etf.rma21.projekat.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.stream.Collectors


class OdgovorRepository {
    companion object {
        private lateinit var context: Context
        fun setContext(_context: Context) {
            context = _context
        }

        suspend fun getOdgovoriKviz(idKviza: Int?): List<Odgovor>? {
            val kvizovi = TakeKvizRepository.getPocetiKvizovi()
            val odgovori = arrayListOf<Odgovor>()
            var idTaken = 0
            for (i in 0 until kvizovi!!.size) {
                if (kvizovi[i].KvizId!!.equals(idKviza)) idTaken = kvizovi[i].id
            }
            System.out.println(idTaken.toString() + "ID TAKEN")
            val url1 =
                ApiConfig.baseURL + "/student/" + AccountRepository.acHash + "/kviztaken/" + idTaken + "/odgovori"
            val url = URL(url1) //2
            (url.openConnection() as? HttpURLConnection)?.run { //3
                val result = this.inputStream.bufferedReader().use { it.readText() } //4
                val jo = JSONArray(result)//5
                for (i in 0 until jo.length()) {//7
                    val odgovor = jo.getJSONObject(i)
                    val id = odgovor.getInt("KvizTakenId")
                    val pitanje = odgovor.getInt("PitanjeId")
                    val odgovoreno = odgovor.getInt("odgovoreno")
                    odgovori.add(Odgovor(i, odgovoreno, pitanje, idKviza))
                }
            }
            /* for(i in 0 until odgovori.size){
                writeOdgovor(context,odgovori[i])
            }

            */
            return odgovori//8
        }

        suspend fun postaviOdgovorKvizAPI(idKvizTaken: Int, idPitanje: Int, odgovor: Int): Int {
            System.out.println(odgovor.toString() + "INDEKS")
            val url =
                URL(ApiConfig.baseURL + "/student/" + AccountRepository.acHash + "/kviztaken/" + idKvizTaken + "/odgovor")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("Accept", "*/*")
            conn.doOutput = true
            conn.doInput = true
            val pokusaj = TakeKvizRepository.getPokusajId(idKvizTaken)
            var bodovi = pokusaj?.osvojeniBodovi
            val kviz = KvizRepository.getById(pokusaj?.KvizId!!)
            val pitanja = PitanjeKvizRepository.getPitanja(kviz?.id!!)
            for (i in 0 until pitanja.size) {
                if (pitanja[i].id == idPitanje && pitanja[i].tacan == odgovor) bodovi =
                    bodovi?.plus(((1.0 / pitanja.size) * 100).toInt())
            }
            val data = JSONObject()
            data.put("odgovor", odgovor)
            data.put("pitanje", idPitanje)
            data.put("bodovi", bodovi)
            val output = conn.getOutputStream()
            output.use { output ->
                val input: ByteArray = data.toString().toByteArray(Charsets.UTF_8)
                output.write(input, 0, input.size)
            }
            val data2 = conn.inputStream.bufferedReader().readText()
            System.out.println(data2 + "DATA")
            if (JSONObject(data2).isNull("odgovoreno") || JSONObject(data2).isNull("KvizTakenId")) return 0
            return bodovi!!
        }


        suspend fun getOdgovoriKvizTaken(id: Int): List<Odgovor> {
            val odgovori = mutableListOf<Odgovor>()

            val url1 =
                ApiConfig.baseURL + "/student/" + AccountRepository.acHash + "/kviztaken/" + id + "/odgovori"
            val url = URL(url1) //2
            (url.openConnection() as? HttpURLConnection)?.run { //3
                val result = this.inputStream.bufferedReader().use { it.readText() } //4
                val jo = JSONArray(result)//5
                for (i in 0 until jo.length()) {//7
                    val odgovor = jo.getJSONObject(i)
                    //val id = odgovor.getInt("id")
                    val odgovoreno = odgovor.getInt("odgovoreno")
                    //odgovori.add(Odgovor(i, odgovoreno, 1))
                }
            }
            return odgovori
        }

        suspend fun getOdgovoriKviz1(idKviza: Int?): Result<List<Odgovor>?> {
            return withContext(Dispatchers.IO) {
                val rez = getOdgovoriKviz(idKviza)
                return@withContext Result.Success(rez)
            }

        }

        suspend fun predajOdgovore(kvizId: Int) {
            val db = AppDatabase.getInstance(context)
            val kvizovi = db.KvizDAO().getAll()
            var kviz = Kviz(1, "", "", "", "", "", 30, "", null)
            val taken = TakeKvizRepository.getPocetiKvizovi()
            var kvizTaken = KvizTaken(-1, "", 10, Date(0, 0, 0), -1)
            for (i in 0 until taken!!.size) {
                if (taken[i].KvizId!!.equals(kvizId)) kvizTaken = taken[i]
            }
            for (i in 0 until kvizovi.size) {
                if (kvizovi[i].id.equals(kvizId)) kviz = kvizovi[i]
            }
            val odgovori = db.OdgovorDao().getAll()
            var odgovori2 = mutableListOf<Odgovor>()
            for (i in 0 until odgovori.size) {
                if (odgovori[i].kvizId!!.equals(kvizId)) odgovori2.add(odgovori[i])
            }
            for (i in 0 until odgovori2.size) {
                postaviOdgovorKvizAPI(kvizTaken.id, kviz.id, odgovori2[i].odgovoreno)
            }
            /* val odg = getOdgovori(context)
            var odgovor :Odgovor= Odgovor(-1,-1,-1,-1)
            val poceti=TakeKvizRepository.getPocetiKvizovi()
            var idKvizTaken =0
            for(i in 0 until poceti!!.size){
                if(poceti[i].KvizId!!.equals(kvizId))
                    idKvizTaken=poceti[i].id
            }
            for (i in 0 until odg.size) {
                if (odg[i].kvizId!!.equals(kvizId)) odgovor=odg[i]
            }
                    val url =
                        URL(ApiConfig.baseURL + "/student/" + AccountRepository.acHash + "/kviztaken/" + idKvizTaken + "/odgovor")
                    val conn = url.openConnection() as HttpURLConnection
                    conn.requestMethod = "POST"
                    conn.setRequestProperty("Content-Type", "application/json")
                    conn.setRequestProperty("Accept", "*")
                    conn.doOutput = true
                    conn.doInput = true
                    System.out.println(idKvizTaken.toString() + "POKUSAJ")
                    val pokusaj = TakeKvizRepository.getPokusajId(idKvizTaken)
                    var bodovi = pokusaj?.osvojeniBodovi
                    System.out.println(bodovi.toString() + "BODOVI")
                    if (pokusaj == null) System.out.println("greska")
                    val kviz = KvizRepository.getById(pokusaj?.KvizId!!)
                    val pitanja = PitanjeKvizRepository.getPitanja(kviz?.id!!)
                    var odgDoSad = getOdgovori(context)
                    var vel = odgDoSad.size
                    val data = JSONObject()
                    data.put("odgovor", odgovor.odgovoreno)
                    data.put("pitanje", odgovor.pitanjeId)
                    data.put("bodovi", bodovi)
                    val output = conn.getOutputStream()
                    output.use { output ->
                        val input: ByteArray = data.toString().toByteArray(Charsets.UTF_8)
                        output.write(input, 0, input.size)
                    }

           val data2 = conn.inputStream.bufferedReader().readText()
            System.out.println(data2 + "DATA")
            if (JSONObject(data2).isNull("odgovoreno") || JSONObject(data2).isNull("KvizTakenId")) return 0
            return bodovi!!

            */
        }

        suspend fun postaviOdgovorKviz1(
            idKvizTaken: Int,
            idPitanje: Int,
            odgovor: Int
        ): Result<Int?> {
            return withContext(Dispatchers.IO) {
                val rez = postaviOdgovorKviz(idKvizTaken, idPitanje, odgovor)
                return@withContext Result.Success(rez)
            }

        }

        /*
            suspend fun predajOdgovorKviz1(idKviz: Int): Result<Int?> {
                return withContext(Dispatchers.IO) {
                    val rez = predajOdgovorKviz(idKviz)
                    return@withContext Result.Success(rez)
                }

            }
*/
        suspend fun writeOdgovor(context: Context, odgovor: Odgovor): String? {
            return withContext(Dispatchers.IO) {
                try {
                    var db = AppDatabase.getInstance(context)
                    db!!.OdgovorDao().insertAll(odgovor)
                    return@withContext "success"
                } catch (error: Exception) {
                    return@withContext null
                }
            }
        }

        suspend fun postaviOdgovorKviz(idKvizTaken: Int, idPitanje: Int, odgovor: Int): Int {
            val db = AppDatabase.getInstance(context)
            val pokusaj = TakeKvizRepository.getPokusajId(idKvizTaken)
            val kviz = KvizRepository.getById(pokusaj?.KvizId!!)
            var kvizId :Int?= kviz!!.id
            if (kvizId == null) kvizId = -1
            val odgovori = getOdgovori(context)
            var brojac = 0
            for (i in 0 until odgovori.size) {
                if (odgovori[i].kvizId!!.equals(kvizId) && odgovori[i].pitanjeId.equals(idPitanje)) brojac++
            }
            if (brojac == 0)
                writeOdgovor(context,Odgovor(db.OdgovorDao().getAll().size + 1, odgovor, idPitanje, kvizId))
            //val pitanja = PitanjeKvizRepository.getPitanja(context)
            if (kvizId != -1) {
                odgovori.stream().filter { t -> t.kvizId == kvizId }.collect(Collectors.toList())
            }
            //val kviz = KvizRepository.getById(pokusaj?.KvizId!!)
            var bodovi = 0
            val pitanja = PitanjeKvizRepository.getPitanja(kviz.id)
            for (i in 0 until pitanja.size) {
                if (pitanja[i].id == idPitanje && pitanja[i].tacan == odgovor) bodovi =
                    bodovi?.plus(((1.0 / pitanja.size) * 100).toInt())
            }
            return bodovi
            /*  val odg = getOdgovori(context)
            val pokusaj = TakeKvizRepository.getPokusajId(idKvizTaken)
            var bodovi = pokusaj?.osvojeniBodovi
            System.out.println(bodovi.toString() + "BODOVI")
            if (pokusaj == null) System.out.println("greska")
            val kviz = KvizRepository.getById(pokusaj?.KvizId!!)
            val pitanja = PitanjeKvizRepository.getPitanja(kviz?.id!!)
            for (i in 0 until odg.size) {
                System.out.println(odg[i].id.toString()+" ODGOVORI U BAZI")
                if (odg[i].pitanjeId.equals(idPitanje) && odg[i].kvizId!!.equals(kviz.id)) {
                    val pokusaj = TakeKvizRepository.getPokusajId(idKvizTaken)
                    return pokusaj!!.osvojeniBodovi
                }
            }
            for (i in 0 until pitanja.size) {
                if (pitanja[i].id == idPitanje && pitanja[i].tacan.equals(odgovor)) {
                    bodovi = bodovi?.plus(((100 / pitanja.size)).toInt())
                    System.out.println(bodovi.toString() + "BODOVI")
                }
                writeOdgovor(context, Odgovor(getOdgovori(context).size + 1, odgovor, idPitanje,kviz.id))
            }
            return bodovi!!

           */
        }

        suspend fun getOdgovori(context: Context): List<Odgovor> {
            return withContext(Dispatchers.IO) {
                var db = AppDatabase.getInstance(context)
                var movies = db!!.OdgovorDao().getAll()
                return@withContext movies
            }
        }
    }
}

