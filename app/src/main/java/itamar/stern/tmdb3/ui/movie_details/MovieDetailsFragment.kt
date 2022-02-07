package itamar.stern.tmdb3.ui.movie_details

import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.appbar.AppBarLayout
import itamar.stern.tmdb3.MovieApplication
import itamar.stern.tmdb3.R
import itamar.stern.tmdb3.adapters.CommentsAdapter
import itamar.stern.tmdb3.databinding.FragmentMovieDetailsBinding
import itamar.stern.tmdb3.models.FavoriteMovie
import itamar.stern.tmdb3.network.NetworkStatusChecker
import itamar.stern.tmdb3.utils.dp
import itamar.stern.tmdb3.utils.hideKeyboard
import java.util.*

class MovieDetailsFragment : Fragment() {
    companion object {
        //This flag lets us know if the user arrived to the movie page from the favorites page - if so,
        //if the user remove this movie from favorites - we need to refresh the favorites list now,
        //so that when he returns to the favorites page he will not see it there.
        var inFavoritesNow = false
    }

    private val messageField: Editable get() = binding.editTextMessage.text
    private lateinit var movieDetailsViewModel: MovieDetailsViewModel
    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        movieDetailsViewModel = ViewModelProvider(this)[MovieDetailsViewModel::class.java]
        _binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.hide()

        binding.appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            //Change gradually the alpha of the word "movies" from 0 when the movie
            //image is visible, to 1 when image invisible, and the
            //position - move the word slightly to the right:
            binding.textViewMovies.alpha =
                (verticalOffset * 1f / (binding.toolbar.height - binding.imageView.height))
            binding.textViewMovies.setPaddingRelative(
                (verticalOffset * 1f / (binding.toolbar.height - binding.imageView.height) * 50).dp()
                    .toInt(), 0, 0, 0
            )
            //Move the favorites FAB with the collapsing image:
            binding.buttonFavorite.translationY = verticalOffset.toFloat() + 350.dp()

        })
        binding.arrowBack.setOnClickListener {
            findNavController().navigate(R.id.action_movieFragment_to_nav_home)
        }
        binding.buttonFavorite.setOnClickListener {
            //if - if you didn't mark this movie as favorite until now - add it to favorites. else - remove it:
            with(MovieApplication.db.movieDao()) {
                if (checkFavoriteExists(MovieApplication.focusedMovie.id!!).isEmpty()) {
                    addFavorite(FavoriteMovie(MovieApplication.focusedMovie.id!!))
                    binding.buttonFavorite.setImageResource(R.drawable.ic_baseline_favorite_24)
                } else {
                    deleteFavorite(MovieApplication.focusedMovie.id!!)
                    binding.buttonFavorite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                }
            }
            if (inFavoritesNow) {
                //refresh the list if the user removed favoriteMovie from the favorites view:
                MovieApplication.refreshAndShowFavorites()
            }
        }
        //Show all the movie details:
        binding.apply {
            MovieApplication.focusedMovie.apply {
                Glide.with(requireContext())
                    .load("https://image.tmdb.org/t/p/original$backdrop_path")
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
                            return false
                        }
                    })
                    .into(imageView)
                textView15.text = title
                textView16.text = "Release Date: $release_date"
                textView17.text = "Summary"
                textView18.text = overview
                textView19.text = "Popularity: $popularity"
                textView20.text = "Vote average: $vote_average"
                if (MovieApplication.db.movieDao()
                        .checkFavoriteExists(MovieApplication.focusedMovie.id!!).isNotEmpty()
                ) {
                    buttonFavorite.setImageResource(R.drawable.ic_baseline_favorite_24)
                }
            }
        }

        binding.recyclerViewComments.layoutManager = LinearLayoutManager(requireContext())
        //if "done" it's mean we finished to fetch all the comments from firebase, so hide the progressBar:
        movieDetailsViewModel.done.observe(viewLifecycleOwner) {
            if (it) binding.progressBar.visibility = View.GONE
        }
        //Show the comments:
        movieDetailsViewModel.comments.observe(viewLifecycleOwner) { comments ->
            binding.recyclerViewComments.adapter = CommentsAdapter(comments)
        }


        //Sending comments:
        binding.buttonSend.setOnClickListener {
            hideKeyboard(this.requireActivity())
            //don't send empty comment:
            if (messageField.toString() == "") return@setOnClickListener
            //don't try to send comment with no internet connection:
            if (!NetworkStatusChecker(requireContext().getSystemService(ConnectivityManager::class.java)).hasInternetConnection()) {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            movieDetailsViewModel.sendComment(messageField)

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}