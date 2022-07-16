package com.example.application.data.service
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.springframework.stereotype.Service

@Service
class Service {
    private var client: OkHttpClient? = null
    private var response: Response? = null
    private var cityName: String = ""
    private var API: String = "050fa07f013d2262ae290dd5c7c25e29"

    private fun getwhether(): JSONObject? {
        client = OkHttpClient()
        var recuest: Request = Request.Builder()
            .url("https://api.openweathermap.org/data/2.5/weather?q="+getCityName()+"&units=metric&appid=050fa07f013d2262ae290dd5c7c25e29")
            .build()
        try {
            response = client!!.newCall(recuest).execute()
            return JSONObject(response!!.body()!!.string())
        }
        catch (e: JSONException){
            e.printStackTrace()
        }
        return null
    }
    fun weatherMain(): JSONObject {
        return getwhether()!!.getJSONObject("main")
    }

    private fun getCityName(): String {
        return cityName
    }
     fun setCityName(cityname : String){
        cityName = cityname
    }

}
