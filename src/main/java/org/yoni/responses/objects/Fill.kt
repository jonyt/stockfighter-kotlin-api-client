package org.yoni.responses.objects

import java.time.ZonedDateTime

/**
 * Created by yoni on 25/12/15.
 */
data class Fill(val price: Int, val qty: Int, val ts: ZonedDateTime) {
}