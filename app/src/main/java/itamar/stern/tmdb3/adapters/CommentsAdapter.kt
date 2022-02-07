package itamar.stern.tmdb3.adapters


import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import itamar.stern.tmdb3.R
import itamar.stern.tmdb3.databinding.MessageItemBinding
import itamar.stern.tmdb3.models.Comment
import itamar.stern.tmdb3.utils.dp


class CommentsAdapter(
    private val comments: List<Comment>
) : RecyclerView.Adapter<CommentsAdapter.VH>() {
    class VH(val binding: MessageItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(
            MessageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: VH, position: Int) {
        with(holder.binding){
            with(comments[position]){
                var params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                //because recyclerView is recycling the layouts which it already used,
                //we need to set all the layout settings again before each use, otherwise -
                //the previous settings like color, width and margin will remain active.
                textViewMessage.text = comment
                //if the same user wrote message after message - remove the space between the
                //messages and write his name just on the first message:
                if (position > 0 && name == comments[position - 1].name) {
                    textViewName.visibility = View.GONE
                    params.setMargins(0, 0, 0, 1.dp().toInt())
                    layoutOfCards.layoutParams = params
                } else {
                    textViewName.text = comments[position].name
                    textViewName.visibility = View.VISIBLE
                    params.setMargins(0, 8.dp().toInt(), 0, 1.dp().toInt())
                    layoutOfCards.layoutParams = params
                }
                if(color != null){
                    textViewName.setTextColor(Integer.parseInt(color))
                }
                textViewDate.text = date

                //customize the currentUser's messages:
                if (FirebaseAuth.getInstance().currentUser?.uid == userId) {
                    card.background.setTint(root.context.getColor(R.color.myRed))
                    textViewMessage.setTextColor(Color.WHITE)
                    textViewDate.setTextColor(Color.WHITE)
                    textViewName.visibility = View.GONE
                } else {
                    card.background.setTint(root.context.getColor(R.color.grayElsesMessages))
                    textViewMessage.setTextColor(Color.BLACK)
                    textViewDate.setTextColor(Color.BLACK)
                }
                //make the layout width of textViewMessage match_parent back, after he stretched from the last text he had:
                params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                textViewMessage.layoutParams = params
            }
        }

    }


    override fun getItemCount() = comments.size
}