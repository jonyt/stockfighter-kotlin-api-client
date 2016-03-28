package org.yoni.requests

/**
 * Created by yoni on 25/12/15.
 */
data class Order(val account: String, val venue: String, val stock: String, val qty: Int,
                 val direction: Direction, val orderType: Type, val price: Int = 0) {
    enum class Type {
        LIMIT, MARKET, FILL_OR_KILL, IMMEDIATE_OR_CANCEL;

        override fun toString() = kotlin.text.String(super.toString().toByteArray()).toLowerCase().replace('_', '-')

    }
    enum class Direction {
        BUY, SELL;

        override fun toString() = kotlin.text.String(super.toString().toByteArray()).toLowerCase()
    }
}