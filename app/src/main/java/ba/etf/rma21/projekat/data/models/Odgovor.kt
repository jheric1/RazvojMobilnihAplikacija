package ba.etf.rma21.projekat.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Odgovor(
    @PrimaryKey val id:Int,
    @ColumnInfo(name = "odgovoreno") val odgovoreno : Int,
    @ColumnInfo(name = "pitanjeId") val pitanjeId : Int,
    @ColumnInfo(name="kvizId") val kvizId : Int?
)