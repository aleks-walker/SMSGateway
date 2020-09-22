package kg.kloop.android.smsgateway

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class GatewayBroadcastReceiver : BroadcastReceiver() {

    private val TAG: String = GatewayBroadcastReceiver::class.java.simpleName

    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.extras
        val sms = getSms(bundle)
        saveToFirestore(sms)
    }

    private fun getSms(bundle: Bundle?): SmsMessage? {
        val pdu_type = "pdus"
        // Get the SMS message.
        val msgs: Array<SmsMessage?>
        var strMessage = ""
        val format = bundle!!.getString("format")
        // Retrieve the SMS message received.
        val pdus = bundle[pdu_type] as Array<*>?
        if (pdus != null) {
            // Check the Android version.
            val isVersionM = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            // Fill the msgs array.
            msgs = arrayOfNulls<SmsMessage>(pdus.size)
            for (i in msgs.indices) {
                // Check Android version and use appropriate createFromPdu.
                if (isVersionM) {
                    // If Android version M or newer:
                    msgs[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray, format)
                } else {
                    // If Android version L or older:
                    msgs[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                }
                // Build the message to show.
                strMessage += "SMS from " + msgs[i]?.originatingAddress
                strMessage += """ :${msgs[i]?.messageBody.toString()}"""
                // Log and display the SMS message.
                Log.d(TAG, "onReceive: $strMessage")
//                Toast.makeText(context, strMessage, Toast.LENGTH_LONG).show()
                return msgs[i]
            }
        }
        return null
    }

    private fun saveToFirestore(smsMessage: SmsMessage?) {
        val db = Firebase.firestore
        val message = Message()
        message.apply {
            text = smsMessage?.messageBody.toString()
            incomingNumber = smsMessage?.originatingAddress
            timeStampMil = smsMessage?.timestampMillis
        }
        db.collection("messages").add(message).addOnSuccessListener { documentReference ->
            Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
        }.addOnFailureListener { e ->
            Log.w(TAG, "Error adding document", e)
        }
    }
}

