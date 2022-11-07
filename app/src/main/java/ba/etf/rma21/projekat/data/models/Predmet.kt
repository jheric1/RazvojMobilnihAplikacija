package ba.etf.rma21.projekat.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Predmet(
    @PrimaryKey val id:Int,
    @ColumnInfo(name = "naziv") val naziv: String,
    @ColumnInfo(name = "godina")val godina : Int) {
}