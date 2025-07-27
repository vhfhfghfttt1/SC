
package com.snooker.coach

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import java.nio.ByteBuffer
import java.nio.ByteOrder

class פעילותראשית : AppCompatActivity(), MessageClient.OnMessageReceivedListener {

    private lateinit var טקסטזווית: TextView
    private lateinit var כפתורהתחלה: Button
    private lateinit var כפתוראמצה: Button
    private var מאזין = false

    override fun onCreate(חבילה: Bundle?) {
        super.onCreate(חבילה)
        setContentView(R.layout.activity_main)

        טקסטזווית = findViewById(R.id.orientationText)
        כפתורהתחלה = findViewById(R.id.startButton)
        כפתוראמצה = findViewById(R.id.stopButton)

        כפתורהתחלה.setOnClickListener {
            if (!מאזין) {
                Wearable.getMessageClient(this).addListener(this)
                מאזין = true
                טקסטזווית.text = "מאזין לנתונים..."
            }
        }

        כפתוראמצה.setOnClickListener {
            if (מאזין) {
                Wearable.getMessageClient(this).removeListener(this)
                מאזין = false
                טקסטזווית.text = "נעצר."
            }
        }
    }

    override fun onMessageReceived(אירוע: MessageEvent) {
        if (אירוע.path == "/orientation") {
            val חוצץ = ByteBuffer.wrap(אירוע.data).order(ByteOrder.LITTLE_ENDIAN)
            val סבסוב = חוצץ.float
            val הטיה = חוצץ.float
            val עצה = חוצץ.float

            runOnUiThread {
                טקסטזווית.text = "סבסוב: %.2f\nהטיה: %.2f\nעצה: %.2f".format(סבסוב, הטיה, עצה)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (מאזין) {
            Wearable.getMessageClient(this).removeListener(this)
        }
    }
}
