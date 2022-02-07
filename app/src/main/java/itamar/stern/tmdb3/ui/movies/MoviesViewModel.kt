package itamar.stern.tmdb3.ui.movies


import android.app.Activity
import android.app.Application
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import itamar.stern.tmdb3.MovieApplication
import itamar.stern.tmdb3.R
import itamar.stern.tmdb3.adapters.MoviesAdapter
import itamar.stern.tmdb3.utils.dp

class MoviesViewModel(application: Application) :AndroidViewModel(application) {

    fun setLayoutsSizes(
        layout: ConstraintLayout,
        imageView: ImageView,
        width: Int
    ) {
        layout.layoutParams.width = width / 2
        layout.layoutParams.height = width / 4 * 3
        imageView.layoutParams.width = width / 2 - 4
        imageView.layoutParams.height = width / 4 * 3 - 4
    }

    fun listenToSearching(searchView: SearchView, recyclerViewSearch: RecyclerView, recyclerView: RecyclerView, activity: Activity, navController: NavController){
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchMovies(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchMovies(newText)
                return true
            }

            private fun searchMovies(newText: String?) {
                if (newText == "") {
                    MoviesFragment.query = null
                    recyclerView.visibility = View.VISIBLE
                    recyclerViewSearch.visibility = View.INVISIBLE
                } else {
                    recyclerView.visibility = View.INVISIBLE
                    recyclerViewSearch.visibility = View.VISIBLE

                    recyclerViewSearch.adapter = MoviesAdapter(MovieApplication.db.movieDao().getSearchResult("%$newText%")) { position, movie, layout, imageView ->
                        setLayoutsSizes(layout, imageView, recyclerViewSearch.width)
                        layout.setOnClickListener {
                            val xy = IntArray(2)
                            layout.getLocationInWindow(xy)
                            MoviesFragment.searchRecyclerOffset = xy[1] - (activity.findViewById<Toolbar>(
                                R.id.toolbarMain)?.height?.plus(24.dp().toInt())!!)
                            MoviesFragment.searchRecyclerPosition = position
                            //Save the search query:
                            MoviesFragment.query = newText
                            MovieApplication.focusedMovie = movie
                            navController.navigate(R.id.action_nav_home_to_movieFragment)
                        }
                    }
                    //if we just arrived from a movieFragment we clicked on the search - go back to the same movie position:
                    if (MoviesFragment.searchRecyclerPosition != 0) {
                        (recyclerViewSearch.layoutManager as GridLayoutManager).scrollToPositionWithOffset(
                            MoviesFragment.searchRecyclerPosition, MoviesFragment.searchRecyclerOffset
                        )
                        MoviesFragment.searchRecyclerPosition = 0
                    }
                }
            }
        })
    }


}