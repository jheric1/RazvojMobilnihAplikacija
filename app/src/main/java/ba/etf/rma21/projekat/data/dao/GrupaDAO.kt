package ba.etf.rma21.projekat.data.dao

import androidx.room.*
import ba.etf.rma21.projekat.data.models.Grupa
import ba.etf.rma21.projekat.data.models.Kviz


@Dao
interface GrupaDAO {
    @Query("SELECT * FROM Grupa")
    suspend fun getAll(): List<Grupa>
    @Insert
    suspend fun insertAll(vararg grupe: Grupa)
    @Query("DELETE FROM Grupa")
    suspend fun delete()
}