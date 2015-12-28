package org.yoni.responses

import org.yoni.responses.objects.Stock

/**
 * Created by yoni on 25/12/15.
 */
class StocksInVenue(ok: Boolean, error: String?, val symbols: Array<Stock>) : BasicResponse(ok, error){
}