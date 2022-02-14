# TMDB - The Movie Database
This is my first android app!<br/>
This project is an Android app which displays data from <a href="https://www.themoviedb.org">The Movie Database</a> API.<br/>
<a href="https://play.google.com/store/apps/details?id=itamar.stern.tmdb3">Download the app from Google play</a>
## Description
The app displays data of 10000 movies from The Movie Database API.<br/>
You can display the movies sorted by popularity, title or release date.<br/>
You can search movies.<br/>
You can mark movies as favorites and get back to see it afterwords.<br/>
Users can write and read comments about the movies.
### Screenshots
<pre>
<img src="https://github.com/ItamarSter/tmdb/blob/main/images/screenshot1.jpeg" width="200" />        <img src="https://github.com/ItamarSter/tmdb/blob/main/images/screenshot2.jpeg" width="200" />        <img src="https://github.com/ItamarSter/tmdb/blob/main/images/screenshot3.jpeg" width="200" />
<br/>
<img src="https://github.com/ItamarSter/tmdb/blob/main/images/screenshot4.jpeg" width="200" />        <img src="https://github.com/ItamarSter/tmdb/blob/main/images/screenshot5.jpeg" width="200" />        <img src="https://github.com/ItamarSter/tmdb/blob/main/images/screenshot6.jpeg" width="200" />
</pre>

## Technologies and libraries
#### Room Database
- All the downloaded information from The Movie Database API is saved in room, to let the application to be used without an Internet connection.
- Favorites movies id's are saved in room too.
#### Firebase realtime database
- Users comments to movies are saved in firebase realtime database, and downloaded in all app opening.
#### Firebase authentication
- Register and Login to the app is done with firebase email/password authentication. (Email verification is not required)
#### ViewModel
#### LiveData
- Displaying the movies from the room with LiveData.
- Displaying downloading progressBar with LiveData of the amount of the movies in room.
- Additional uses.
#### WorkManager
- Using WorkManager for scheduling the movies download from the api.
#### Retrofit2
- Using retrofit for download the movies from the api.
#### RecyclerView
- Displaying the movies and the comments in recyclerViews with adapters.
#### Glide
- Download images with Glide.
#### SharedPreferences
- Storage and management of data required for the proper running of the app, like name and color of the current logged user.
- Storage and management of flags.
## Author
Itamar Stern<br/>
ita767@gmail.com<br/>
Israel