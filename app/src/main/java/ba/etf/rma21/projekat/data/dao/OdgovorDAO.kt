package ba.etf.rma21.projekat.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ba.etf.rma21.projekat.data.models.Odgovor
import ba.etf.rma21.projekat.data.models.Pitanje

@Dao
interface OdgovorDAO {
    @Query("SELECT * FROM Odgovor")
    suspend fun getAll(): List<Odgovor>
    @Insert
    suspend fun insertAll(vararg odgovor: Odgovor)
    @Query("DELETE FROM Odgovor")
    suspend fun delete()
}