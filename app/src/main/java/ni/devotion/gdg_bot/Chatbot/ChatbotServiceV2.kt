package ni.devotion.gdg_bot.Chatbot

import com.google.cloud.dialogflow.v2beta1.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChatbotServiceV2(session: SessionName, sessionsClient: SessionsClient, queryInput: QueryInput) {
    private val session: SessionName = session
    private val sessionsClient: SessionsClient = sessionsClient
    private val queryInput: QueryInput = queryInput

    suspend fun doRequest(): DetectIntentResponse? = withContext(Dispatchers.Default) {
        try {
            val detectIntentRequest = DetectIntentRequest.newBuilder()
                .setSession(session.toString())
                .setQueryInput(queryInput)
                .build()
            sessionsClient.detectIntent(detectIntentRequest)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
