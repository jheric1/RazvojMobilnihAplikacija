package ba.etf.rma21.projekat.data.models


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*
@Entity
data class KvizTaken(
    @PrimaryKey val id:Int,
    @ColumnInfo(name = "student") val student : String,
    @ColumnInfo(name = "osvojeniBodovi") val osvojeniBodovi:Int,
    @ColumnInfo(name = "datumRada") val datumRada :Date,
    @ColumnInfo(name = "KvizId")val KvizId: Int?
)
