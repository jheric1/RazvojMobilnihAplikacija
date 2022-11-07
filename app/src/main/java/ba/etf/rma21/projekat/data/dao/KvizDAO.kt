package ba.etf.rma21.projekat.data.dao

import androidx.room.*
import ba.etf.rma21.projekat.data.models.Kviz

@Dao
interface KvizDAO {
    @Query("SELECT * FROM Kviz")
    suspend fun getAll(): List<Kviz>
    @Insert
    suspend fun insertAll(vararg kvizovi: Kviz)
    @Query("DELETE FROM Kviz")
    suspend fun delete()

}