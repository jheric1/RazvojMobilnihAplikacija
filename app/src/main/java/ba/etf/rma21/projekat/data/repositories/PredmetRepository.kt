package ba.etf.rma21.projekat.data.repositories

import android.content.Context
import ba.etf.rma21.projekat.data.models.Predmet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class PredmetRepository {
    companion object {
        private lateinit var context:Context
        fun setContext(_context: Context){
            context=_context
        }
    fun getPredmetZaId(idPredmeta: Int): Predmet? {
        var predmet1: Predmet
        val url1 = ApiConfig.baseURL + "/predmet/" + idPredmeta.toString()
        val url = URL(url1) //2
        val conn = url.openConnection() as HttpURLConnection
        val result = conn.inputStream.bufferedReader().readText() //4
        val jo = JSONObject(result)//5
        if (jo.has("message") && jo.getString("message") == "Predmet not found") return null
        return Predmet(jo.getInt("id"), jo.getString("naziv"), jo.getInt("godina"))
    }
}

}