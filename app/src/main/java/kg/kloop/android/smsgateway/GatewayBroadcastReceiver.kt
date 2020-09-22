package kg.kloop.android.smsgateway

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class GatewayBroadcastReceiver : BroadcastReceiver() {

    private val TAG: String = GatewayBroadcastReceiver::class.java.simpleName

    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.extras
        val sms = getSms(bundle)

        saveToFirestore(sms, context)
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

    private fun saveToFirestore(smsMessage: SmsMessage?, context: Context) {
        val db = Firebase.firestore
        val message = Message()
        message.apply {
            text = smsMessage?.messageBody.toString()
            incomingNumber = smsMessage?.originatingAddress
            timeStampMil = smsMessage?.timestampMillis
        }
        if (FirebaseAuth.getInstance().currentUser != null) {
            db.collection("messages").add(message).addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                Toast.makeText(context, context.getString(R.string.message_sent), Toast.LENGTH_LONG).show()
            }.addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                Toast.makeText(context, context.getString(R.string.error_sending_the_message), Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(context, context.getString(R.string.login_is_required), Toast.LENGTH_LONG).show()
        }
    }
}

