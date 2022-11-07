package ba.etf.rma21.projekat.data.repositories

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import ba.etf.rma21.projekat.data.AppDatabase
import ba.etf.rma21.projekat.data.dao.*
import ba.etf.rma21.projekat.data.models.Account
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class AccountRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    companion object {
        var acHash: String = ""
        private lateinit var context: Context
       init{
            CoroutineScope(Dispatchers.Main).launch {
                var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss")
                val dateTime = LocalDateTime.of(1999, 12, 31, 12, 17, 43)
                var db = AppDatabase.getInstance(context)
                val AccountDao = db.AccountDao()
                System.out.println("OVDJE TREBA")
                var hash: String? = AccountDao.getHash()
                    acHash="0e4afb4c-5bac-4c5d-bd7d-56f20da55850"
                if(hash==null)
                    AccountDao.insertAccount(Account("0e4afb4c-5bac-4c5d-bd7d-56f20da55850",dateTime.format(formatter)))
                }
            }

        fun setContext(_context: Context){
            context=_context
        }
        @RequiresApi(Build.VERSION_CODES.O)
        suspend fun postaviHash(acHash: String): Boolean {
            CoroutineScope(Dispatchers.Main).launch {
                var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss")
                val dateTime = LocalDateTime.of(1999, 12, 31, 12, 17, 43)
                var db = AppDatabase.getInstance(context)
                val AccountDao = db.AccountDao()
                var hash: String? = AccountDao.getHash()
                if (hash != acHash && hash != null) {
                    System.out.println("ovdje treba uci")
                    db.OdgovorDao().delete()
                    db.AccountDao().delete()
                    db.KvizDAO().delete()
                    db.PitanjeDAO().delete()
                    db.PredmetDAO().deleteAll()
                    db.GrupaDAO().delete()
                    AccountDao.insertAccount(Account(acHash, dateTime.format(formatter)))
                }

                if (hash.equals(null)) {
                    AccountDao.insertAccount(
                        Account(
                            acHash,
                            dateTime.format(formatter)
                        )
                    )
                }
            }
            this.acHash = acHash
            return true
        }

        fun getHash(): String {
            return acHash
        }

    }
}