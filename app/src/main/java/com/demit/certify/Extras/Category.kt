package com.demit.certify.Extras

import android.content.Context
import com.android.volley.toolbox.JsonArrayRequest
import org.json.JSONArray
import com.android.volley.VolleyError
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import java.util.HashMap

class Category(var context: Context) {
    private val ads: Unit
        private get() {
            val URL = ""
            val jsonRequest: JsonArrayRequest = object : JsonArrayRequest(
                Method.GET,
                URL,
                null,
                Response.Listener { },
                Response.ErrorListener { error ->
                    Log.d(
                        "Error ResponseD",
                        "onErrorResponse: $error"
                    )
                }) {}
            jsonRequest.retryPolicy = DefaultRetryPolicy(
                500000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
            Volley.newRequestQueue(context).add(jsonRequest)
        }
}