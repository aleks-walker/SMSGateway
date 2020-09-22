package kg.kloop.android.smsgateway

data class Message(
    var text: String = "",
    var incomingNumber: String? = "",
    var timeStampMil: Long? = 0L
)