package com.loq.buggadooli.loq2.network

import com.loq.buggadooli.loq2.models.BlockedApplication
import io.reactivex.Observable

interface LoqService{

    fun getLoqs(): Observable<BlockedApplication>

    fun addLoq(loq: BlockedApplication): Observable<BlockedApplication>

    fun addLoqs(loqs: List<BlockedApplication>): Observable<BlockedApplication>

}

class RealLoqService: LoqService {

    override fun addLoqs(loqs: List<BlockedApplication>): Observable<BlockedApplication> {
        return Observable.create { emitter ->

        }
    }

    override fun getLoqs(): Observable<BlockedApplication> {
        return Observable.create { emitter ->

        }
    }

    override fun addLoq(loq: BlockedApplication): Observable<BlockedApplication> {
        return Observable.create { emitter ->

        }
    }
}