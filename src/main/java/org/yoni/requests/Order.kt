package org.yoni.requests

/**
 * Created by yoni on 25/12/15.
 */
data class Order(val account: String, val venue: String, val stock: String, val qty: Int,
                 val direction: String, val orderType: String, val price: Int = 0) {
    val orderTypes = arrayOf("limit", "market", "fill-or-kill", "immediate-or-cancel")
    val directions = arrayOf("buy", "sell")

    init { // Can't use enums for these because of the hyphens in order types
        if (!orderTypes.contains(orderType))
            throw IllegalArgumentException("Unknown order type $orderType")
        if (!directions.contains(direction))
            throw IllegalArgumentException("Unknown direction $direction")
    }
}