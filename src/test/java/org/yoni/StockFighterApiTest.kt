package org.yoni

import com.google.gson.*
import org.junit.Assert.*
import net.jadler.Jadler.*
import org.yoni.requests.Order
import org.yoni.responses.BasicResponse
import org.yoni.responses.OrderBook
import org.yoni.responses.StocksInVenue
import org.yoni.responses.objects.Bid
import org.yoni.responses.objects.Fill
import org.yoni.responses.objects.Stock
import java.time.ZonedDateTime

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
        assertTrue(response.isSuccess())
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
        assertTrue(response.isSuccess())
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
        assertTrue(response.isSuccess())
        assert(response is StocksInVenue)
        val symbols = (response as StocksInVenue).symbols
        assertArrayEquals(expectedStocks, symbols)
    }

    @org.junit.Test
    fun testOrderBook_ReturnsExpectedResults() {
        val venue = "TESTEX"
        val symbol = "FOO"
        val expectedBids = arrayOf(Bid(10, 5, true), Bid(20, 10, true))
        val expectedAsks = arrayOf(Bid(1000, 500, false), Bid(200, 100, false))
        val time = ZonedDateTime.now()
        val api = StockFighterApi("", "http://localhost", port())
        println(api.gson.toJson(time))
        val responseJson = """
                {
                    "ok": true,
                    "venue": "$venue",
                    "symbol": "$symbol",
                    "bids": ${api.gson.toJson(expectedBids)},
                    "asks": ${api.gson.toJson(expectedAsks)},
                    "ts": "${api.gson.toJson(time)}"
                }
            """

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/${api.basePath}/venues/$venue/stocks/$symbol")
                .respond()
                .withBody(responseJson)
                .withStatus(200)

        val response = api.orderBook(venue, symbol)
        assertTrue(response.isSuccess())
        assert(response is OrderBook)
        val orderBook = response as OrderBook
        assertArrayEquals(expectedBids, orderBook.bids)
        assertArrayEquals(expectedAsks, orderBook.asks)
        assertEquals(symbol, orderBook.symbol)
        assertEquals(venue, orderBook.venue)
    }

    @org.junit.Test
    fun testFillSerialization(){
        val fill = Fill(100, 50, ZonedDateTime.now())
        val api = StockFighterApi("", "http://localhost", port())
        val json = api.gson.toJson(fill, fill.javaClass)
        println(json)
    }

    @org.junit.Test
    fun testOrder() {
        val gson = Gson()

        val venue = "TESTEX"
        val symbol = "FOO"
        val account = "OGB12345"
        val direction = Order.Direction.BUY
        val orderType = Order.Type.LIMIT
        val quantity = 50
        val originalQuantity = 100
        val price = 5100
        val now = ZonedDateTime.now()
        val fills = arrayOf(Fill(100, 50, now))

        val api = StockFighterApi("", "http://localhost", port())

        val responseJson = """
            {
              "ok": true,
              "symbol": "$symbol",
              "venue": "$venue",
              "direction": "$direction",
              "originalQty": $originalQuantity,
              "qty": $quantity,
              "price":  $price,
              "orderType": "$orderType",
              "id": 12345,
              "account" : "$account",
              "ts": "2015-07-05T22:16:18+00:00",
              "fills": ${api.gson.toJson(fills)},
              "totalFilled": 80,
              "open": true
            }
        """

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/${api.basePath}/venues/$venue/stocks/$symbol/orders")
                .respond()
                .withBody(responseJson)
                .withStatus(200)
        val response = api.order(account, venue, symbol, quantity, direction, orderType)
        assertTrue(response.isSuccess())
        assertTrue(response is org.yoni.responses.Order)
        val order = response as org.yoni.responses.Order
        assertEquals(account, order.account)
        assertEquals(venue, order.venue)
        assertEquals(symbol, order.symbol)
        assertEquals(quantity, order.qty)
        assertEquals(originalQuantity, order.originalQty)
        assertEquals(orderType.toString(), order.orderType)
        assertEquals(price, order.price)

    }
}