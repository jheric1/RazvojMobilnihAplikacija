package ba.etf.rma21.projekat.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Grupa (
    @PrimaryKey var id: Int,
    @ColumnInfo(name = "naziv") var naziv: String,
    @ColumnInfo(name = "PredmetId")  var PredmetId: Int
)