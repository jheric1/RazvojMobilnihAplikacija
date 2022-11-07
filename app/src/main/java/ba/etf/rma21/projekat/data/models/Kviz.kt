package ba.etf.rma21.projekat.data.models


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*
@Entity
data class Kviz(
    @PrimaryKey val id :Int,
    @ColumnInfo(name = "naziv") val naziv: String,
    @ColumnInfo(name = "nazivPredmeta") val nazivPredmeta :String,
    @ColumnInfo(name = "datumPocetka") val datumPocetka: String,
    @ColumnInfo(name = "datumKraj") val datumKraj: String,
    @ColumnInfo(name = "datumRada") val datumRada : String,
    @ColumnInfo(name = "trajanje") val trajanje: Int,
    @ColumnInfo(name = "nazivGrupe") val nazivGrupe :String,
    @ColumnInfo(name = "osvojeniBodovi")val osvojeniBodovi : Int?
    )
