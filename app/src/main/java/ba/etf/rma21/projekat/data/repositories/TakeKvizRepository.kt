package ba.etf.rma21.projekat.data.repositories

import android.content.Context
import ba.etf.rma21.projekat.data.models.Grupa
import ba.etf.rma21.projekat.data.models.Kviz
import ba.etf.rma21.projekat.data.models.KvizTaken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class TakeKvizRepository {
    companion object {
        private lateinit var context:Context
        fun setContext(_context: Context){
            context=_context
        }
        suspend fun zapocniKviz(idKviza: Int): KvizTaken? {
                var poceti= getPocetiKvizovi()
                if(poceti==null){
                    poceti= listOf()
                }
                val pokusaj=poceti.stream().filter{t->t.KvizId==idKviza}.findAny()
                System.out.println("nasaoo")
                if(pokusaj.isPresent) return pokusaj.get()
                var rez: KvizTaken? = null
                val url = URL(ApiConfig.baseURL + "/student/" + AccountRepository.acHash + "/kviz/" + idKviza.toString())
                val conn=url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                val data=conn.inputStream.bufferedReader().use { it.readText() }
                    if (data.contains("not found") || data.contains("nije upisan u grupu za kviz")) return null
            System.out.println("ZAPOCEO")
            val kviz = JSONObject(data)
                        val id = kviz.getInt("id")
                        val osvojeniBodovi = kviz.getInt("osvojeniBodovi")
                        //val student = kviz.getString("student")
                        val datumRada = kviz.getString("datumRada")
                        val date1: Date = SimpleDateFormat("yyyy-MM-dd").parse(datumRada)
                var kvizId: Int?=null
                if(!kviz.isNull("KvizId"))
                    kvizId=kviz.getInt("KvizId")
                        rez = KvizTaken(
                            id,
                            AccountRepository.acHash,
                            osvojeniBodovi,
                            date1,
                            idKviza
                        )
                return rez
        }
        suspend fun zapocniKviz1(idKviza: Int): Result<KvizTaken?> {
            return withContext(Dispatchers.IO) {
                val rez= zapocniKviz(idKviza)
                return@withContext Result.Success(rez)
            }

        }

        suspend fun getPocetiKvizovi(): List<KvizTaken>? {
                val kvizovi = arrayListOf<KvizTaken>()
                val url1 = ApiConfig.baseURL + "/student/" + AccountRepository.acHash + "/kviztaken"
                val url = URL(url1) //2
                (url.openConnection() as? HttpURLConnection)?.run { //3
                    val result = this.inputStream.bufferedReader().use { it.readText() } //4
                    val jo = JSONArray(result)//5
                    System.out.println(jo.length().toString()+"VELICINA")
                    for (i in 0 until jo.length()) {//7
                        val kviz = jo.getJSONObject(i)
                        val id = kviz.getInt("id")
                        val osvojeniBodovi = kviz.getInt("osvojeniBodovi")
                        val student = kviz.getString("student")
                        val datumRada = kviz.getString("datumRada")
                        val date1: Date = SimpleDateFormat("yyyy-MM-dd").parse(datumRada)
                        val KvizId = kviz.getInt("KvizId")
                        kvizovi.add(
                            KvizTaken(
                                id,
                                student,
                                osvojeniBodovi,
                                date1,
                                KvizId
                            )
                        )
                    }
                }
                if (kvizovi.size != 0) return kvizovi//8
                else return null
        }
        suspend fun getPocetiKvizovi1(): Result<List<KvizTaken>?> {
            return withContext(Dispatchers.IO) {
                val rez= getPocetiKvizovi()
                return@withContext Result.Success(rez)
            }

        }
        suspend fun getPokusajId(id : Int) : KvizTaken?{
            val pokusaji= getPocetiKvizovi()
            for(i in 0 until pokusaji!!.size){
                if(pokusaji[i].id!!.equals(id)) return pokusaji[i]
            }
            return null
        }

    }
}


