package com.codepath.apps.restclienttemplate

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class ComposeActivity : AppCompatActivity() {

    lateinit var etCompose : EditText
    lateinit var btnTweet : Button
    lateinit var wordCount : TextView

    lateinit var client: TwitterClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        etCompose = findViewById(R.id.etTweetCompose)
        btnTweet = findViewById(R.id.btnTweet)
        wordCount = findViewById(R.id.tvWordCount)

        client = TwitterApplication.getRestClient(this)

        etCompose.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Listens when the text is being changed + range of text
                val count = s.length.toString()
                wordCount.text = count + "/280"
            }

            override fun onTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Listens right before text is changing
                wordCount.setTextColor(Color.BLUE)
            }

            override fun afterTextChanged(s: Editable) {
                if (s.length > 280){
                    wordCount.setTextColor(Color.RED)
                }
            }

        })


        // Handling the user's click on the tweet button
        btnTweet.setOnClickListener {

            // Grab the content of edittext (etCompose)
            val tweetContent = etCompose.text.toString()

            // 1. Make sure the tweet isn't empty
            if (tweetContent.isEmpty()) {
                Toast.makeText(this, "Empty tweets not allowed", Toast.LENGTH_SHORT)
                    .show()
                // Look into displaying SnackBar message
            } else
            // 2. Make sure the tweet is under character count
            if (tweetContent.length > 280) {
                Toast.makeText(
                    this,
                    "Tweet is too long! Limit is 280 characters",
                    Toast.LENGTH_SHORT
                ).show()

                // Disable tweet button if char count exceeds 280
                btnTweet.isClickable = false
                btnTweet.setBackgroundColor(Color.RED)

            } else {
                // Make an api call to Twitter to publish tweet
//                Toast.makeText(this, tweetContent, Toast.LENGTH_SHORT)
//                    .show()
                client.publishTweet(tweetContent, object : JsonHttpResponseHandler() {
                    override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                        // Send the tweet back to TimelineActivity to show
                        Log.i(TAG, "Successfully published tweet!")

                        val tweet = Tweet.fromJson(json.jsonObject)

                        val intent = Intent()
                        intent.putExtra("tweet", tweet) // put tweet object inside intent as extra
                        setResult(RESULT_OK, intent) // set result to ok
                        finish()


                    }

                    override fun onFailure(
                        statusCode: Int,
                        headers: Headers?,
                        response: String?,
                        throwable: Throwable?
                    ) {
                        Log.e(TAG, "Failed to publish tweet", throwable)
                    }

                })
            }

        }
    }
    companion object {
        val TAG = "CompostActivity"
    }
}

