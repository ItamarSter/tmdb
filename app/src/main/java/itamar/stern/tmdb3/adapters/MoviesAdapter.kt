package itamar.stern.tmdb3.adapters

import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import itamar.stern.tmdb3.databinding.MovieItemBinding
import itamar.stern.tmdb3.models.Movie

class MoviesAdapter(
    private val movies: List<Movie>,
    val callback: (Int, Movie, ConstraintLayout, ImageView) -> Unit
) : RecyclerView.Adapter<MoviesAdapter.VH>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(
            MovieItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.binding.apply {
            textView1.text = movies[position].title
            callback(position, movies[position], layout, imageView1)
            //images full url example:  https://image.tmdb.org/t/p/original/VlHt27nCqOuTnuX6bku8QZapzO.jpg
            Glide
                .with(holder.binding.root.context)
                .load("https://image.tmdb.org/t/p/original" + movies[position].poster_path)
                .thumbnail(0.1f)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        textViewNoImage.visibility = View.VISIBLE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        textViewNoImage.visibility = View.INVISIBLE
                        return false
                    }
                })
                .into(imageView1)
        }
    }

    override fun getItemCount(): Int = movies.size

    class VH(val binding: MovieItemBinding) : RecyclerView.ViewHolder(binding.root)
}