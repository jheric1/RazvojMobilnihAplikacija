package ba.etf.rma21.projekat.data.repositories

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import ba.etf.rma21.projekat.data.AppDatabase
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class DBRepository {
    companion object{
        private lateinit var context:Context
        fun setContext(_context: Context){
            context=_context
        }
        @RequiresApi(Build.VERSION_CODES.O)
        suspend fun updateNow():Boolean{
            var db = AppDatabase.getInstance(context)
            val AccountDao = db.AccountDao()
            var hash: String? = AccountDao.getLastUpdate()
            val url = URL("https://rma21-etf.herokuapp.com/account/"+AccountRepository.getHash()+"/lastUpdate?date="+hash)
            val conn=url.openConnection() as HttpURLConnection
            //val result=conn.responseMessage
            val result = conn.inputStream.bufferedReader().readText()
            val rez=JSONObject(result)
            //val ispis=rez.getString("message")
           System.out.println(result+"RESULT")
            return result.contains("true")
        }
    }
}