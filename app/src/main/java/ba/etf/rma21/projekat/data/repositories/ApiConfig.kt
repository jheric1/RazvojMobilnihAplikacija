package ba.etf.rma21.projekat.data.repositories



class ApiConfig {

    companion object{
        var baseURL: String = "http://rma21-etf.herokuapp.com"

        fun postaviBaseURL(baseUrl:String):Unit{

            baseURL=baseUrl
        }
    }
}