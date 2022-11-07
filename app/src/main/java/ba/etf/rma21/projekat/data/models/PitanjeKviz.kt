package ba.etf.rma21.projekat.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
class PitanjeKviz(
    @PrimaryKey val id:Int,
    @ColumnInfo(name = "naziv") val naziv: String,
    @ColumnInfo(name = "kviz") val kviz :String,
    @ColumnInfo(name = "predmet") val predmet : String) {
}