package ba.etf.rma21.projekat.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Account(
    @PrimaryKey var acHash:String,
    @ColumnInfo(name = "lastUpdate") var lastUpdate:String) {
}