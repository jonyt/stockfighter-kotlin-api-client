package org.yoni.responses

import java.time.LocalDateTime

/**
 * Created by yoni on 26/12/15.
 */
class OrderBook(ok: Boolean, error: String?, val venue: String, val symbol: String,
                val bids: Array<Bid>, val asks: Array<Bid>, val ts: LocalDateTime) : BasicResponse(ok, error) {
}