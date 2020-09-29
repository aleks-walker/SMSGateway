package kg.kloop.android.smsgateway

data class Message(
    var isSms: Boolean = true,
    var text: String = "",
    var incomingNumber: String? = "",
    var timeStampMil: Long? = 0L
)