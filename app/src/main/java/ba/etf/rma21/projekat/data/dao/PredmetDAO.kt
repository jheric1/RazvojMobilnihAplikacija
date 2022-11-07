package ba.etf.rma21.projekat.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ba.etf.rma21.projekat.data.models.Predmet


@Dao
interface PredmetDAO {
    @Query("SELECT * FROM Predmet")
    suspend fun getAll(): List<Predmet>
    @Insert
    suspend fun insertAll(vararg predmeti: Predmet)
    @Query("DELETE FROM Predmet")
    suspend fun deleteAll()

}