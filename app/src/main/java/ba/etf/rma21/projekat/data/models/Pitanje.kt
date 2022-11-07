package ba.etf.rma21.projekat.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Pitanje(
    @PrimaryKey val id:Int,
    @ColumnInfo(name = "naziv") val naziv :String,
    @ColumnInfo(name = "tekstPitanja") val tekstPitanja :String,
    @ColumnInfo(name = "opcije") val opcije :String,
    @ColumnInfo(name = "tacan") val tacan :Int
    )
