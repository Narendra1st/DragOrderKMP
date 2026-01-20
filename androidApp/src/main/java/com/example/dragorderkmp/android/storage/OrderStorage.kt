package com.example.dragorderkmp.android.storage

import android.content.Context
import com.example.dragorderkmp.Order
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object OrderStorage {
    private const val KEY = "ORDERS"

    fun save(context: Context, orders: List<Order>) {
        val prefs = context.getSharedPreferences("order_app", Context.MODE_PRIVATE)
        val json = Gson().toJson(orders)
        prefs.edit().putString(KEY, json).apply()
    }

    fun load(context: Context): MutableList<Order> {
        val prefs = context.getSharedPreferences("order_app", Context.MODE_PRIVATE)
        val json = prefs.getString(KEY, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Order>>() {}.type
        return Gson().fromJson(json, type)
    }
}
