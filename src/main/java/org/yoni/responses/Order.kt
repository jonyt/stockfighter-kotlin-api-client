package org.yoni.responses

import org.yoni.responses.objects.Fill
import java.time.LocalDateTime

/**
 * Created by yoni on 25/12/15.
 */
class Order(ok: Boolean, error: String?, val symbol: String, val venue: String, val direction: String,
            val originalQty: Int, val qty: Int, val price: Int, val orderType: String, val id: Int,
            val account: String, val ts: LocalDateTime, val fills: Array<Fill>, val totalFilled: Int,
            val open: Boolean) : BasicResponse(ok, error) {
}