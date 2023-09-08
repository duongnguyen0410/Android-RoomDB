package com.example.roomdemo.db

class SubscriberRepository(private val dao: SubscriberDAO) {
    val subscribers = dao.subscriberGetAll()

    suspend fun insert(subscriber: Subscriber): Long{
        return dao.subscriberInsert(subscriber)
    }

    suspend fun update(subscriber: Subscriber): Int{
        return dao.subscriberUpdate(subscriber)
    }

    suspend fun delete(subscriber: Subscriber): Int{
        return dao.subscriberDelete(subscriber)
    }

    suspend fun deleteAll(): Int{
        return dao.subscriberDeleteAll()
    }
}