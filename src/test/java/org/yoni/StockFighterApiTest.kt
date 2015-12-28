package org.yoni

import com.google.gson.*
import com.mashape.unirest.http.Unirest
import org.junit.Assert.*
import net.jadler.Jadler.*
import org.yoni.responses.BasicResponse
import org.yoni.responses.OrderBook
import org.yoni.responses.StocksInVenue
import org.yoni.responses.objects.Bid
import org.yoni.responses.objects.Stock
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.test.assertTrue

/**
 * Created by yoni on 26/12/15.
 */
class StockFighterApiTest {
    val baseUrl = "https://api.stockfighter.io/ob/api"

    @org.junit.Before
    fun setUp() {
        initJadler()
    }

    @org.junit.After
    fun tearDown() {
        closeJadler()
    }

    @org.junit.Test
    fun testHe(){
        val url = "http://localhost:${port()}/yo"
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/yo")
                .respond()
                .withBody("hi")
                .withStatus(200)
        val resp = Unirest.get(url).asString()
        print(resp.body)
    }

    @org.junit.Test
    fun testHeartbeat_ReturnsSuccess() {
        val responseJson = "{\"ok\": true, \"error\": \"\"}"
        val api = StockFighterApi("", "http://localhost", port())

        onRequest()
            .havingMethodEqualTo("GET")
            .havingPathEqualTo("/${api.basePath}/heartbeat")
            .respond()
            .withBody(responseJson)
            .withStatus(200)

        val response = api.heartbeat()
        assertTrue { response.isSuccess() }
    }

    @org.junit.Test
    fun testHeartbeat_ReturnsFailure() {
        val errorMessage = "An error"
        val responseJson = "{\"ok\": false, \"error\": \"$errorMessage\"}"
        val api = StockFighterApi("", "http://localhost", port())

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/${api.basePath}/heartbeat")
                .respond()
                .withBody(responseJson)
                .withStatus(200)

        val response = api.heartbeat()
        assertFalse(response.isSuccess())
        assertEquals(errorMessage, (response as BasicResponse).error)
    }

    @org.junit.Test
    fun testVenueHeartbeat_ReturnsSuccess() {
        val venue = "TESTEX"
        val responseJson = "{\"ok\": true, \"venue\": \"$venue\"}"
        val api = StockFighterApi("", "http://localhost", port())

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/${api.basePath}/venues/$venue/heartbeat")
                .respond()
                .withBody(responseJson)
                .withStatus(200)

        val response = api.venueHeartbeat(venue)
        assertTrue { response.isSuccess() }
    }

    @org.junit.Test
    fun testStocksInVenue_ReturnsExpectedResults() {
        val venue = "TESTEX"
        val expectedStocks = arrayOf(Stock("name1", "symbol1"), Stock("name2", "symbol2"))
        val gson = Gson()
        val responseJson = "{ \"ok\": true, \"symbols\": ${gson.toJson(expectedStocks)} }"
        val api = StockFighterApi("", "http://localhost", port())

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/${api.basePath}/venues/$venue/stocks")
                .respond()
                .withBody(responseJson)
                .withStatus(200)

        val response = api.stocksInVenue(venue)
        assertTrue { response.isSuccess() }
        assert(response is StocksInVenue)
        val symbols = (response as StocksInVenue).symbols
        assertArrayEquals(expectedStocks, symbols)
    }

    @org.junit.Test
    fun testOrderBook_ReturnsExpectedResults() {
        val venue = "TESTEX"
        val symbol = "FOO"
        val timestamp = LocalDateTime.of(2015, 12, 4, 9, 2, 16)
        val expectedBids = arrayOf(Bid(10, 5, true), Bid(20, 10, true))
        val expectedAsks = arrayOf(Bid(1000, 500, false), Bid(200, 100, false))
        val gson = Gson()
        val responseJson = "{ \"ok\": true, \"venue\": \"$venue\", \"symbol\": \"$symbol\", \"bids\": ${gson.toJson(expectedBids)}, \"asks\": ${gson.toJson(expectedAsks)}, \"ts\": \"2015-12-04T09:02:16.680986205Z\" }"
        val api = StockFighterApi("", "http://localhost", port())

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/${api.basePath}/venues/$venue/stocks/$symbol")
                .respond()
                .withBody(responseJson)
                .withStatus(200)

        val response = api.orderBook(venue, symbol)
        assertTrue { response.isSuccess() }
        assert(response is OrderBook)
        val orderBook = response as OrderBook
        assertArrayEquals(expectedBids, orderBook.bids)
        assertArrayEquals(expectedAsks, orderBook.asks)
        assertEquals(symbol, orderBook.symbol)
        assertEquals(venue, orderBook.venue)
    }

    @org.junit.Test
    fun testOrder() {

    }
}