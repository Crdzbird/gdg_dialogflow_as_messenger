package ni.devotion.gdg_bot.FloatingService

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.view.*
import android.content.IntentFilter

class OverlayService : Service() {
    companion object {
        lateinit var instance: OverlayService
        var isRunning: Boolean = false
    }

    lateinit var windowManager: WindowManager
    lateinit var receiver: InnerReceiver
    lateinit var chatHeads: ChatHeads

    override fun onCreate() {
        super.onCreate()

        instance = this

        isRunning = true

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        chatHeads = ChatHeads(this)

        receiver = InnerReceiver()
        val intentFilter = IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        registerReceiver(receiver, intentFilter)

        chatHeads.add("test", "test")
//        val msg = Message("test", "test", "test")
//        chatHeads.topChatHead!!.messages.add(msg)
//
//        if (chatHeads.topChatHead!!.isActive) {
//            chatHeads.content.addMessage(msg)
//        }
    }

    fun unregisterReceiver(){
        receiver.let {
            isRunning = false
            unregisterReceiver(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //instance.windowManager.removeViewImmediate(chatHeads)
        isRunning = false
        instance.stopSelf()
        //instance.chatHeads.hideChatHeads(true)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}

class InnerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        println("ACCION DE OVERLAY-SERVICE: $action")
        if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS == action) {
            println("CERRANDO")
            //OverlayService.instance.chatHeads.hideChatHeads()
            //val reason = intent.getStringExtra("reason")
        }
    }
}