package ni.devotion.gdg_bot.Chat

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ni.devotion.gdg_bot.R

class BotChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    internal var cardReceiverMessage: androidx.cardview.widget.CardView = itemView.findViewById(R.id.cardReceiverMessage) as androidx.cardview.widget.CardView
    var txtReceiverMessage: TextView = itemView.findViewById(R.id.txtReceiverMessage) as TextView
    // var subRvProductCategory: RecyclerView = itemView.findViewById(R.id.subRvProductCategory) as RecyclerView
}
