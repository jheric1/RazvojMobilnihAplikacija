package ba.etf.rma21.projekat.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ba.etf.rma21.projekat.data.dao.*
import ba.etf.rma21.projekat.data.models.*

@Database(entities = arrayOf(Kviz::class, Pitanje::class,Odgovor::class, Grupa::class,Predmet::class,Account::class), version = 2)
abstract class AppDatabase: RoomDatabase() {
    abstract fun KvizDAO(): KvizDAO
    abstract fun PredmetDAO(): PredmetDAO
    abstract fun PitanjeDAO(): PitanjeDAO
    abstract fun GrupaDAO(): GrupaDAO
    abstract fun AccountDao(): AccountDAO
    abstract fun OdgovorDao(): OdgovorDAO
    companion object {
        private var INSTANCE: AppDatabase? = null
        val migration12 :Migration= object : Migration(1,2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Odgovor ADD COLUMN kvizId NUMBER")
            }
        }
        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = buildRoomDB(context)
                }
            }
            return INSTANCE!!
        }
        fun setInstance(appdb:AppDatabase):Unit{
            INSTANCE=appdb
        }
        private fun buildRoomDB(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "RMA21DB"
            ).addMigrations(migration12).build()


    }
}