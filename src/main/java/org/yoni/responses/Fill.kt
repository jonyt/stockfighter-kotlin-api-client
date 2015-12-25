package org.yoni.responses

import java.time.LocalDateTime

/**
 * Created by yoni on 25/12/15.
 */
data class Fill(val price: Int, val qty: Int, val ts: LocalDateTime) {
}