package itamar.stern.tmdb3.ui.movies

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import itamar.stern.tmdb3.MovieApplication
import itamar.stern.tmdb3.R
import itamar.stern.tmdb3.adapters.MoviesAdapter
import itamar.stern.tmdb3.databinding.FragmentMoviesBinding
import itamar.stern.tmdb3.ui.movie_details.MovieDetailsFragment
import itamar.stern.tmdb3.utils.dp


class MyNameObserver : Observer<String> {
    override fun onChanged(t: String?) {
        MovieApplication.prefs.edit().putString("currentName", t).apply()
    }
}

class MyColorObserver : Observer<String> {
    override fun onChanged(t: String?) {
        MovieApplication.prefs.edit().putString("currentColor", t).apply()
    }
}

class MoviesFragment : Fragment() {
    companion object {
        var movies = MovieApplication.repository.movies
        var amount: LiveData<Int> = MovieApplication.db.movieDao().getAmount()
        var query: String? = null
        var searchRecyclerPosition = 0
        var searchRecyclerOffset = 0
        var recyclerPosition = 0
        var recyclerOffset = 0
    }

    private lateinit var moviesViewModel: MoviesViewModel
    private var _binding: FragmentMoviesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        moviesViewModel =
            ViewModelProvider(this)[MoviesViewModel::class.java]
        _binding = FragmentMoviesBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Save the name and color on prefs, for scenario when the user was login, and opened the app
        //offline, and now when the internet is back - sends messages, and we need the name and color.
        MovieApplication.name.observe(viewLifecycleOwner, MyNameObserver())
        MovieApplication.color.observe(viewLifecycleOwner, MyColorObserver())

        binding.recyclerView.layoutManager = GridLayoutManager(context, 2)

        //Observe the amount of the movies for the loading progress bar:
        amount.observe(viewLifecycleOwner) {
            binding.progressBarHome.visibility = View.GONE
            binding.horizontalProgressBarHome.progress = it
            binding.textViewLoading.text = "$it / 10000"
            if (it >= 9960) {
                (activity as AppCompatActivity).supportActionBar?.show()
                binding.progressBarHome.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.VISIBLE
                binding.horizontalProgressBarHome.visibility = View.GONE
                binding.textViewLoading.visibility = View.GONE
                binding.textViewTitleLoading.visibility = View.GONE
                amount.removeObservers(viewLifecycleOwner)
            }
        }

        movies.observe(viewLifecycleOwner) {
            binding.progressBarHome.visibility = View.GONE
            binding.recyclerView.adapter = MoviesAdapter(it) { position, movie, layout, imageView ->
                moviesViewModel.setLayoutsSizes(layout, imageView, binding.recyclerView.width)
                layout.setOnClickListener {
                    //Now we save data so we can return to the same place on the page, and to see again the search results.
                    //Get the location in the window in pixels where the layout had been pressed:
                    val xy = IntArray(2)
                    layout.getLocationInWindow(xy)
                    //Calculate and save the exact offset from the top of the recyclerView:
                    recyclerOffset = xy[1] - (requireActivity().findViewById<Toolbar>(R.id.toolbarMain)?.height?.plus(24.dp().toInt())!!)
                    //Save the movie position on the list:
                    recyclerPosition = position
                    //Set the focusedMovie - the movie that had been pressed by the user.
                    MovieApplication.focusedMovie = movie
                    findNavController().navigate(R.id.action_nav_home_to_movieFragment)
                }
            }
            //if we just arrived from a movieFragment we clicked on it - go back to the same position in the recyclerView:
            if (recyclerPosition > 1) {
                (binding.recyclerView.layoutManager as GridLayoutManager).scrollToPositionWithOffset(recyclerPosition, recyclerOffset)
                recyclerPosition = 0
            }
        }
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.main, menu)
        val item = menu.findItem(R.id.action_search)
        val recyclerViewSearch = binding.recyclerViewSearch
        recyclerViewSearch.layoutManager = GridLayoutManager(context, 2)
        val searchView = item.actionView as SearchView

        moviesViewModel.listenToSearching(searchView, recyclerViewSearch, binding.recyclerView, requireActivity(), findNavController())

        //When returning from MovieFragment after searching - show the previous search results:
        if (query != null) {
            searchView.isIconified = false
            searchView.setQuery(query, true)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                MovieDetailsFragment.inFavoritesNow = false
                FirebaseAuth.getInstance().signOut()
            }
            R.id.action_sort_by_title -> {
                movies = MovieApplication.repository.moviesByTitle
                MovieDetailsFragment.inFavoritesNow = false
                findNavController().navigate(R.id.action_nav_home_self)
            }
            R.id.action_sort_by_date -> {
                movies = MovieApplication.repository.moviesByDate
                MovieDetailsFragment.inFavoritesNow = false
                findNavController().navigate(R.id.action_nav_home_self)
            }
            R.id.action_sort_by_vote -> {
                movies = MovieApplication.repository.moviesByVote
                MovieDetailsFragment.inFavoritesNow = false
                findNavController().navigate(R.id.action_nav_home_self)
            }
            R.id.favorites -> {
                MovieApplication.refreshAndShowFavorites()
                MovieDetailsFragment.inFavoritesNow = true
                findNavController().navigate(R.id.action_nav_home_self)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        //Stop unnecessary works:
        if (amount.value!! >= 10000) {
            try {
                MovieApplication.workManager.cancelAllWork()
            } catch (e: Exception) {
                //WorkManager has not been initialized and it's okay, we don't have works to delete.
            }
        }
    }
}