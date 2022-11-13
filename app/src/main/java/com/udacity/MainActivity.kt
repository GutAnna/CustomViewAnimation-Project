package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var urlProcess: String
    private lateinit var downloadManager: DownloadManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            val rbuttonId = radio_group.checkedRadioButtonId

            if (rbuttonId == -1) {
                custom_button.buttonState = ButtonState.Clicked
                Toast.makeText(this, "Please, check any variant", Toast.LENGTH_SHORT)
                    .show()
            } else if (custom_button.buttonState != ButtonState.Loading) {
                custom_button.buttonState = ButtonState.Loading
                urlProcess = urlMap[rbuttonId]!!
                download(urlProcess)
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadID) {
                custom_button.buttonState = ButtonState.Completed
                sendNotifyFinish()
            }
        }
    }

    private fun sendNotifyFinish() {
        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager

        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra(Constants.DOWNLOAD_ID, downloadID)
            putExtra(Constants.STATUS, checkDownloadStatus())
            putExtra(Constants.FILE_NAME, urlProcess)
        }


        pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        } as PendingIntent

        action = NotificationCompat.Action(
            R.drawable.ic_baseline_cloud_download_24,
            getString(R.string.notification_button),
            pendingIntent
        )

        val contentIntent = Intent(DownloadManager.ACTION_VIEW_DOWNLOADS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val contentPendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_cloud_download_24)
            .setContentTitle("Your Download is Completed")
            .setContentText("Your Download from is completed successfully.")
            .setContentIntent(contentPendingIntent)
            .addAction(action)
            .setPriority(NotificationCompat.PRIORITY_HIGH).build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun download(url: String) {
        val notificationManager =
            ContextCompat.getSystemService(
                applicationContext,
                NotificationManager::class.java
            ) as NotificationManager
        notificationManager.cancelAll()

        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun checkDownloadStatus(): String {
        var status = "ERROR"
        val cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadID))
        if (cursor.moveToFirst()) {
            when(cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                DownloadManager.STATUS_SUCCESSFUL -> status = "SUCCESS"
                DownloadManager.STATUS_FAILED -> status = "FAILED"
                else -> status = "ERROR"
            }
        }
        return status
    }


    companion object {
        private val urlMap = mapOf(
            R.id.radioButton to "https://github.com/bumptech/glide/archive/master.zip",
            R.id.radioButton2 to "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/master.zip",
            R.id.radioButton3 to "https://github.com/square/retrofit/archive/master.zip"
        )


        private const val CHANNEL_ID = "channelId"
        private const val NOTIFICATION_ID = 12

    }

}
