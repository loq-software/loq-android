package com.loq.buggadooli.loq2.network.api

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.loq.buggadooli.loq2.models.BlockedApplication
import io.reactivex.Observable

interface LoqService{

    fun getLoqs(): Observable<List<BlockedApplication>>

    fun addLoq(loq: BlockedApplication): Observable<BlockedApplication>

    fun addLoqs(loqs: List<BlockedApplication>): Observable<List<BlockedApplication>>

}

class RealLoqService(private val database: DatabaseReference): LoqService {

    override fun addLoqs(loqs: List<BlockedApplication>): Observable<List<BlockedApplication>> {
        return Observable.create { emitter ->
            for (loq in loqs){
                var key = loq.id
                if (key.isBlank()) {
                    key = database.push().key.toString()
                }
                database.child(key).setValue(loq)
            }
            emitter.onNext(loqs)
        }
    }

    override fun getLoqs(): Observable<List<BlockedApplication>> {
        return Observable.create { emitter ->
            val applications = ArrayList<BlockedApplication>()
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapShot in dataSnapshot.children){
                        val application = snapShot?.getValue(BlockedApplication::class.java)?: continue
                        application.id = snapShot.key?: ""
                        applications.add(application)
                    }
                    emitter.onNext(applications)
                }

                override fun onCancelled(error: DatabaseError) {
                    emitter.onError(error.toException())
                }
            })
        }
    }

    override fun addLoq(loq: BlockedApplication): Observable<BlockedApplication> {
        return Observable.create { emitter ->
            var key = loq.id
            if (key.isBlank()) {
                key = database.push().key.toString()
            }
            database.child(key).setValue(loq)
            emitter.onNext(loq)
        }
    }
}