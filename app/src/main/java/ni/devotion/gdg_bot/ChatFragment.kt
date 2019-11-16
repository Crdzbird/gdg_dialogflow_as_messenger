package ni.devotion.gdg_bot

import ai.api.model.AIResponse
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.KeyEvent.KEYCODE_BACK
import android.view.View.OnFocusChangeListener
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.rebound.SimpleSpringListener
import com.facebook.rebound.Spring
import com.facebook.rebound.SpringSystem
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.dialogflow.v2beta1.*
import kotlinx.android.synthetic.main.fragment_chat.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ni.devotion.gdg_bot.Chatbot.ChatbotServiceV2
import ni.devotion.gdg_bot.FloatingService.SpringConfigs
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("ViewConstructor")
class ChatFragment(context: Context): LinearLayout(context) {
    private val springSystem = SpringSystem.create()
    private val scaleSpring = springSystem.createSpring()
    lateinit var chatAdapter: ChatAdapter
    private var isShowKeyboard = false
    private var sessionsClient: SessionsClient? = null
    var detectedResponse: DetectIntentResponse? = null
    private var session: SessionName? = null
    private var queryInput: QueryInput? = null
    private val uuid = UUID.randomUUID().toString()
    private lateinit var list: ArrayList<Any>
    val view = this

    init {
        setUpView()
        initV2Chatbot()
    }

    private fun hideKeyboard() {
        isShowKeyboard = false
        val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(messageInput.applicationWindowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    private fun initV2Chatbot() {
        try {
            val stream = resources.openRawResource(R.raw.gdg)
            val credentials = GoogleCredentials.fromStream(stream)
            val projectId = (credentials as ServiceAccountCredentials).projectId
            val settingsBuilder = SessionsSettings.newBuilder()
            val sessionsSettings = settingsBuilder.setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build()
            sessionsClient = SessionsClient.create(sessionsSettings)
            session = SessionName.of(projectId, uuid)
        } catch (e: Exception) {
            Toast.makeText(context, "Error de red", Toast.LENGTH_LONG).show()
        }
    }

    private fun setUpView() {
        context.setTheme(R.style.Theme_MaterialComponents_Light)
        inflate(context, R.layout.fragment_chat, this)
        scaleSpring.addListener(object : SimpleSpringListener() {
            override fun onSpringUpdate(spring: Spring) {
                scaleX = spring.currentValue.toFloat()
                scaleY = spring.currentValue.toFloat()
            }
        })
        scaleSpring.springConfig = SpringConfigs.CONTENT_SCALE
        scaleSpring.currentValue = 0.0

        chatAdapter = ChatAdapter(context)
        list = ArrayList()
//        chatToolbar.inflateMenu(R.menu.chat_menu)
//        chatToolbar.setOnMenuItemClickListener { item: MenuItem? ->
//            when (item!!.itemId) {
//                R.id.clear_chat -> {
//                    list.clear()
//                    chatAdapter.clearChildrens()
//                }
//            }
//            true
//        }

        messageInput.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard()
            }
        }

        messageInput.setOnKeyListener { _, _, p2 ->
            when(p2.keyCode) {
                KEYCODE_BACK -> hideKeyboard()
            }
            false
        }

        rvChat.apply {
            layoutManager = LinearLayoutManager(this@ChatFragment.context, RecyclerView.VERTICAL, false)
            adapter = chatAdapter
            //addItemDecoration(DividerItemDecorator(context.resources.getDimensionPixelSize(R.dimen.divider_size_default), SPACE_BOTTOM or SPACE_TOP or SPACE_LEFT or SPACE_RIGHT))
        }
        rvChat.layoutManager?.isMeasurementCacheEnabled = false
        sendMessage.setOnClickListener {
            messageInput?.let {
                if (messageInput.text.isNullOrBlank() || messageInput.text.isNullOrEmpty()) return@let
                list.add(ChatConvertion(type = 0, data = messageInput.text.toString()))
                chatAdapter.setValue(list)
                rvChat.smoothScrollToPosition(chatAdapter.itemCount)
                queryInput = QueryInput.newBuilder().setText(TextInput.newBuilder().setText(messageInput.text.toString()).setLanguageCode("en-US")).build()
                messageInput.text?.clear()
                requestChat()
            }
        }
    }

    fun requestChat() {
        detectedResponse = null
        GlobalScope.launch(Dispatchers.Main) {
            try {
                detectedResponse = ChatbotServiceV2(session!!, sessionsClient!!, queryInput!!).doRequest()
            } finally {
                detectedResponse ?: responseEmpty()
                detectedResponse?.let { responseNotEmpty(it) }
            }
        }
    }

    fun webhookHandler(it: DetectIntentResponse) {
        val messageList: ArrayList<Intent.Message> = ArrayList(it.queryResult.fulfillmentMessagesList)
//        println("SIZE: ${messageList.size}")
//        println(it.queryResult.fulfillmentMessagesList)
//        it.queryResult.fulfillmentMessagesList.forEach {
//            println("ciclo")
//            println(it.card.title)
//            println(it.card.subtitle)
//            println(it.card.imageUri)
//            println("fin ciclo")
//        }
        when (messageList.count()) {
            in Int.MIN_VALUE..0 -> {
                println("VALOR IGUAL O MENOR A 0")
            }
            1 -> {
                println("VALOR MAYOR A 0 MENOR QUE 1")
                println(messageList[0].text.getText(0).toString())
                if (messageList[0].text.getText(0).toString().isNotEmpty()) list.add(ChatConvertion(type = 1, data = messageList[0].text.getText(0))) else list.add(
                    ChatConvertion(type = 1, data = "error")
                )
                chatAdapter.setValue(list)
                rvChat.smoothScrollToPosition(chatAdapter.itemCount)
            }
            else -> {
                println("VALOR MAYOR A 1")
                if (messageList[0].text.getText(0).toString().isNotEmpty()) list.add(ChatConvertion(type = 1, data = messageList[0].text.getText(0))) else list.add(
                    ChatConvertion(type = 1, data = "error")
                )
                println("DATOS:")
                chatAdapter.setValue(list)
                messageList.removeAt(0)
                println(messageList)
                list.add(ChatConvertion(type = 2, arrayData = messageList))
                chatAdapter.setValue(list)
                rvChat.smoothScrollToPosition(chatAdapter.itemCount)
            }
        }
    }

    fun responseNotEmpty(it: DetectIntentResponse) {
        val botReply = it.queryResult.fulfillmentText
        if (botReply.isEmpty()) {
            webhookHandler(it)
        } else {
            botReply?.let {
                Log.d("CHAT", "V2 Bot Reply: $it")
                list.add(ChatConvertion(type = 1, data = it))
                chatAdapter.setValue(list)
                rvChat.smoothScrollToPosition(chatAdapter.itemCount)
            }
        }
    }

    fun responseEmpty() {
        list.add(ChatConvertion(type = 1, data = "Error conection"))
        chatAdapter.setValue(list)
        //rvChat.smoothScrollToPosition(chatAdapter.itemCount)
    }

    fun callback(aiResponse: AIResponse?) {
        if (aiResponse != null) {
            val botReply = aiResponse.result.fulfillment.speech
            //Log.d("CHAT", "Bot Reply: $botReply")
            // showTextView(botReply, BOT)
        }
    }

    fun hideContent() {
        scaleSpring.endValue = 0.0

        val anim = AlphaAnimation(1.0f, 0.0f)
        anim.duration = 200
        anim.repeatMode = Animation.RELATIVE_TO_SELF
        startAnimation(anim)
    }

    fun showContent() {
        scaleSpring.endValue = 1.0

        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 100
        anim.repeatMode = Animation.RELATIVE_TO_SELF
        startAnimation(anim)
    }
}
