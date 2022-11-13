package com.udacity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val filename: String? = intent.getStringExtra(Constants.FILE_NAME)
        val status: String? = intent.getStringExtra(Constants.STATUS)

        when(status) {
            "SUCCESS" -> tv_status.setTextColor(Color.GREEN)
            else ->tv_status.setTextColor(Color.RED)
        }

        tv_filename.text = filename
        tv_status.text = status

        fab.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }
    }

}
