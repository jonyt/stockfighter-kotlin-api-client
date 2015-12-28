package org.yoni

import com.google.gson.*
import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.ObjectMapper
import com.mashape.unirest.http.Unirest
import org.yoni.requests.Order
import org.yoni.responses.BasicResponse
import org.yoni.responses.OrderBook
import org.yoni.responses.Response
import org.yoni.responses.StocksInVenue
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Created by yoni on 24/12/15.
 */
class StockFighterApi(apiKey: String, host: String = "https://api.stockfighter.io", port: Int = 443) {
    val apiKey = apiKey
    val basePath = "ob/api"
    val baseUrl = "$host:$port/$basePath"

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

    fun heartbeat(): Response {
        val url = "$baseUrl/heartbeat"

        return genericHeartbeat(url)
    }

    fun venueHeartbeat(venue: String): Response {
        val url = "$baseUrl/venues/$venue/heartbeat"

        return genericHeartbeat(url)
    }

    fun stocksInVenue(venue: String): Response {
        val url = "$baseUrl/venues/$venue/stocks"
        val response = Unirest.get(url).asObject(StocksInVenue::class.java)

        return getResponse(response)
    }

    fun orderBook(venue: String, stock: String): Response {
        val url = "$baseUrl/venues/$venue/stocks/$stock"
        val response = Unirest.get(url).asObject(OrderBook::class.java)

        return getResponse(response)
    }

    fun order(account: String, venue: String, symbol: String, quantity: Int,
              direction: String, orderType: String, price: Int = 0): Response {

        val order = Order(account, venue, symbol, quantity, direction, orderType, price)
        val response = Unirest.post("$baseUrl/venues/$venue/stocks/$symbol/orders").
                        header("X-Starfighter-Authorization", apiKey).
                        body(order).
                        asObject(org.yoni.responses.Order::class.java)

        return getResponse(response)
    }

    private fun genericHeartbeat(url: String): Response {
        val response = Unirest.get(url).asObject(BasicResponse::class.java)

        return getResponse(response)
    }

    private fun getResponse<T: BasicResponse>(response : HttpResponse<T>) : Response {
        if (response.status == 200)
            return response.body
        else
            return BasicResponse(false, response.statusText)
    }
}