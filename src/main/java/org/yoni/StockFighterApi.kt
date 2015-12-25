package org.yoni

import com.google.gson.*
import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.ObjectMapper
import com.mashape.unirest.http.Unirest
import org.yoni.requests.Order
import org.yoni.responses.BasicResponse
import org.yoni.responses.OrderBook
import org.yoni.responses.StocksInVenue
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Created by yoni on 24/12/15.
 */
class StockFighterApi(apiKey: String) {
    val apiKey = apiKey
    val baseUrl = "https://api.stockfighter.io/ob/api"

    init {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeDeserializer())
        val gson = gsonBuilder.create()

        Unirest.setObjectMapper(object : ObjectMapper{
            override fun <T : Any?> readValue(json: String?, objectType: Class<T>?): T {
                return gson.fromJson(json, objectType)
            }

            override fun writeValue(obj: Any?): String? {
                return gson.toJson(obj)
            }
        })
    }

    private class LocalDateTimeDeserializer : JsonDeserializer<LocalDateTime>{
        override fun deserialize(json: JsonElement?, type: Type?, context: JsonDeserializationContext?): LocalDateTime? {
            return LocalDateTime.parse(json!!.getAsJsonPrimitive().getAsString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        }

    }

    fun heartbeat(): BasicResponse {
        val url = "$baseUrl/heartbeat"
        return genericHeartbeat(url)
    }

    fun venueHeartbeat(venue: String): BasicResponse {
        val url = "$baseUrl/venues/$venue/heartbeat"
        return genericHeartbeat(url)
    }

    fun stocksInVenue(venue: String): BasicResponse {
        val url = "$baseUrl/venues/$venue/stocks"
        val response = Unirest.get(url).asObject(StocksInVenue::class.java)
        if (response.status == 200)
            return response.body
        else
            return BasicResponse(false, response.statusText)
    }

    fun orderBook(venue: String, stock: String): BasicResponse {
        val url = "$baseUrl/venues/$venue/stocks/$stock"
        val response = Unirest.get(url).asObject(OrderBook::class.java)
        if (response.status == 200)
            return response.body
        else
            return BasicResponse(false, response.statusText)
    }

    // TODO: return type should be a data class specific to this
    // TODO: direction should be enum (buy, sell), orderType should be enum (limit, market, fill-or-kill, immediate-or-cancel)
    fun order(account: String, venue: String, symbol: String, quantity: Int,
              direction: String, orderType: String, price: Int = 0): BasicResponse {

        val order = Order(account, venue, symbol, quantity, direction, orderType, price)
        val response = Unirest.post("$baseUrl/venues/$venue/stocks/$symbol/orders").
                        header("X-Starfighter-Authorization", apiKey).
                        body(order).
                        asObject(org.yoni.responses.Order::class.java)
        if (response.status == 200)
            return response.body
        else
            return BasicResponse(false, response.statusText)
    }

    private fun genericHeartbeat(url: String): BasicResponse {
        val response = Unirest.get(url).asObject(BasicResponse::class.java)
        if (response.status == 200) {
            return response.body
        } else {
            return BasicResponse(false, response.statusText)
        }
    }

//    private fun returnResponse<T>(response : HttpResponse<T>) : T {
//        if (response.status == 200)
//            return response.body
//        else
//            return BasicResponse(false, response.statusText)
//    }
}