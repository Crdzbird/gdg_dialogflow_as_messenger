package ni.devotion.gdg_bot

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import ni.devotion.gdg_bot.Chat.BotChatViewHolder
import ni.devotion.gdg_bot.Chat.UserChatViewHolder

class ChatAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val USER = 0
        const val BOT = 1
        const val CARROUSEL = 2
    }
    private var list = emptyList<Any>()
    var context: Context? = null
    private val lastPosition = -1
    private val viewPool = RecycledViewPool()

    constructor(context: Context) : this() {
        this.context = context
    }

    override fun getItemCount(): Int = list.size

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.itemView.clearAnimation()
    }

    // la vida es bella, lancom

    fun setValue(list: List<Any>){
        this.list = list
        notifyDataSetChanged()
    }

    fun clearChildrens(){
        list = emptyList()
        notifyDataSetChanged()
    }

    inline fun <T, reified R> List<T>.mapToTypedArray(transform: (T) -> R): Array<R> {
        return when (this) {
            is RandomAccess -> Array(size) { index -> transform(this[index]) }
            else -> with(iterator()) { Array(size) { transform(next()) } }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            USER -> {
                val holder_user = holder as UserChatViewHolder
                val menuItem = list[position] as ChatConvertion
                holder_user.txtSenderMessage.text = menuItem.data as String
            }
            BOT -> {
                val holder_bot = holder as BotChatViewHolder
                val menuItem = list[position] as ChatConvertion
                holder_bot.txtReceiverMessage.text = menuItem.data as String
            }
            else -> {
                val holder_user = holder as UserChatViewHolder
                val menuItem = list[position] as ChatConvertion
                holder_user.txtSenderMessage.text = menuItem.data as String
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return when (viewType) {
            USER -> {
                val menuItemLayoutView = LayoutInflater.from(parent.context).inflate(R.layout.sender_message, parent, false)
                UserChatViewHolder(menuItemLayoutView)
            }
            BOT -> {
                val menuItemLayoutView = LayoutInflater.from(parent.context).inflate(R.layout.receiver_message, parent, false)
                BotChatViewHolder(menuItemLayoutView)
            }
            else -> {
                val menuItemLayoutView = LayoutInflater.from(parent.context).inflate(R.layout.receiver_message, parent, false)
                UserChatViewHolder(menuItemLayoutView)
//                val menuItemLayoutView = LayoutInflater.from(parent.context).inflate(R.layout.fragment_card_payment, parent, false)
//                PaymentViewHolder(menuItemLayoutView)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val data = list[position] as ChatConvertion
        return data.type
    }
}
