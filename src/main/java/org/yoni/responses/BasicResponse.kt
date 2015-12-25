package org.yoni.responses

/**
 * Created by yoni on 25/12/15.
 */
open class BasicResponse(val ok: Boolean, val error: String?) {
    fun isSuccess() = ok
}