package ba.etf.rma21.projekat.data.repositories

import android.content.Context
import ba.etf.rma21.projekat.data.AppDatabase
import ba.etf.rma21.projekat.data.models.Kviz
import ba.etf.rma21.projekat.data.models.Odgovor
import ba.etf.rma21.projekat.data.models.Pitanje
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL


class PitanjeKvizRepository {
    companion object {
        private lateinit var context:Context
        fun setContext(_context: Context){
            context=_context
        }

         fun getPitanja(idKviza: Int?): List<Pitanje> {
            val pitanja = arrayListOf<Pitanje>()
            val url = URL(ApiConfig.baseURL + "/kviz/" + idKviza.toString() + "/pitanja") //2
            val conn = url.openConnection() as HttpURLConnection
            val result = conn.inputStream.bufferedReader().readText()  //4
            val jo = JSONArray(result)//5
            for (i in 0 until jo.length()) {//7
                val pitanje = jo.getJSONObject(i)
                val id = pitanje.getInt("id")
                val naziv = pitanje.getString("naziv")
                val tekstPitanja = pitanje.getString("tekstPitanja")
                val getArray = pitanje.getJSONArray("opcije")
                var opcije = ""
                for (i in 0 until getArray.length()) {
                    opcije+=getArray[i].toString()
                    if (i != getArray.length()-1) opcije+=","
                }
                val tacan = pitanje.getInt("tacan")
                pitanja.add(Pitanje(id, naziv, tekstPitanja, opcije, tacan))

            }
            return pitanja//8
        }
        suspend fun getPitanja1(idKviza:Int?): Result<List<Pitanje>?> {
            return withContext(Dispatchers.IO) {
                val pitanja= getPitanja(idKviza)
                return@withContext Result.Success(pitanja)
            }

        }
        suspend fun writePitanje(context: Context,pitanje: Pitanje) : String?{
            return withContext(Dispatchers.IO) {
                try{
                    var db = AppDatabase.getInstance(context)
                    db!!.PitanjeDAO().insertAll(pitanje)
                    return@withContext "success"
                }
                catch(error:Exception){
                    return@withContext null
                }
            }
        }
        suspend fun getPitanja(context: Context): List<Pitanje> {
            return withContext(Dispatchers.IO) {
                var db = AppDatabase.getInstance(context)
                var movies = db!!.PitanjeDAO().getAll()
                return@withContext movies
            }
        }



    }

}
