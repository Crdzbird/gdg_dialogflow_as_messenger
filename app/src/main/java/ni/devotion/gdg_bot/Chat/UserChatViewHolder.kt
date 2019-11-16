package ni.devotion.gdg_bot.Chat

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ni.devotion.gdg_bot.R

class UserChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    internal var cardSenderMessage: androidx.cardview.widget.CardView = itemView.findViewById(R.id.cardSenderMessage) as androidx.cardview.widget.CardView
    var txtSenderMessage: TextView = itemView.findViewById(R.id.txtSenderMessage) as TextView
    // var subRvProductCategory: RecyclerView = itemView.findViewById(R.id.subRvProductCategory) as RecyclerView
}
