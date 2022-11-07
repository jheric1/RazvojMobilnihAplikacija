package ba.etf.rma21.projekat.data.models

import ba.etf.rma21.projekat.data.*

object Korisnik {
    lateinit var predmeti :List<Predmet>
    lateinit var grupe : List<Grupa>
    lateinit var mojiKvizovi : List<Kviz>
    var godina : Int?=null
    var pozicija2 : Int?=null
    var pozicija3 : Int?=null

}
