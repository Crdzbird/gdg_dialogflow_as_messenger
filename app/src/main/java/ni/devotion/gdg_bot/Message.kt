package ni.devotion.gdg_bot

data class Message(var messageType: Int, var message: String, var imageSrc: Int? = null) {
    companion object {
        val RECEIVER = 0
        val SENDER = 1
        val CENTERED = 2
    }
}
