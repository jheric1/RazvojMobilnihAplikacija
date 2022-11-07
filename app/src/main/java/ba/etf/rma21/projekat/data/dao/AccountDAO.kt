package ba.etf.rma21.projekat.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ba.etf.rma21.projekat.data.models.Account
import retrofit2.http.DELETE

@Dao
interface AccountDAO {
    @Query("SELECT acHash FROM Account")
    suspend fun getHash() : String
    @Query("SELECT lastUpdate FROM Account")
    suspend fun getLastUpdate() : String
    @Insert
    suspend fun insertAccount(acc: Account)
    @Query("DELETE FROM Account")
    suspend fun delete()
}