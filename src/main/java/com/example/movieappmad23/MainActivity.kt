package com.example.movieappmad23

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.movieappmad23.models.Movie
import com.example.movieappmad23.models.getMovies
import com.example.movieappmad23.ui.theme.MovieAppMAD23Theme

class MainActivity : ComponentActivity() {
    private val movieViewModel by viewModels<MovieViewModel>()
    private val tabs = listOf(
        "Home",
        "Favorites"
    )
    var currentTab by mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MovieAppMAD23Theme {
                // A surface container using the 'background' color from the theme.
                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    Column {
                        TabRow(
                            selectedTabIndex = currentTab,
                            backgroundColor = MaterialTheme.colors.primary,
                            contentColor = MaterialTheme.colors.onPrimary
                        ) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    text = { Text(title) },
                                    selected = currentTab == index,
                                    onClick = { currentTab = index }
                                )
                            }
                        }

                        // Select tab to display MovieList or FavoriteScreen
                        if (currentTab == 0) {
                            val movies = getMovies()
                            MovieList(movies = movies, movieViewModel = movieViewModel)
                        } else {
                            FavoriteScreen(movieViewModel = movieViewModel)
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun FavoriteScreen(movieViewModel: MovieViewModel) {
    val favoriteMovies = movieViewModel.favoriteMovies // gets list of favorite movies

    LazyColumn {    // for each favorite movie it creates a FavoriteMovieRow composable
        items(favoriteMovies) { movie ->
            FavoriteMovieRow(movie = movie, movieViewModel = movieViewModel)
        }
    }
}

@Composable
fun FavoriteMovieRow(movie: Movie, movieViewModel: MovieViewModel) {
    val isFavorite = movieViewModel.favoriteMovies.contains(movie) // checking if it is present in the favoriteMovies list of movieViewModel

    // layout of Tab "Favorite"
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        shape = RoundedCornerShape(corner = CornerSize(15.dp)),
        elevation = 5.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.avatar2),
                contentDescription = "Movie Poster",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .padding(10.dp)
            )
            Text(
                text = movie.title,
                style = MaterialTheme.typography.h6,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
            Icon(
                tint = MaterialTheme.colors.secondary,
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Add to favorites",
                modifier = Modifier.clickable {
                    movieViewModel.toggleFavorite(movie)
                }
            )
        }
    }
}

class MovieViewModel : ViewModel() {
    private val _favoriteMovies = mutableStateListOf<Movie>()
    val favoriteMovies: List<Movie> = _favoriteMovies

    // function to mark a movie as favorite
    fun toggleFavorite(movie: Movie) {
        if (_favoriteMovies.contains(movie)) {
            _favoriteMovies.remove(movie)
        } else {
            _favoriteMovies.add(movie)
        }
    }
}

@Composable
fun MovieList(movies: List<Movie>, movieViewModel: MovieViewModel) {
    val favoriteMovies = movieViewModel.favoriteMovies
    val isFavorite: (Movie) -> Boolean = { movie -> favoriteMovies.contains(movie) }

    LazyColumn {
        items(movies) { movie ->
            MovieRow(movie, isFavorite(movie)) {
                movieViewModel.toggleFavorite(movie)
            }
        }
    }
}

@Composable
fun MovieRow(movie: Movie, isFavorite: Boolean, onToggleFavorite: (Movie) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        shape = RoundedCornerShape(corner = CornerSize(15.dp)),
        elevation = 5.dp
    ) {
        Column {
            Box(
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.avatar2),
                    contentDescription = "Movie Poster",
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Icon(
                        tint = MaterialTheme.colors.secondary,
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Add to favorites",
                        modifier = Modifier.clickable {
                            onToggleFavorite(movie)
                        }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(movie.title, style = MaterialTheme.typography.h6)
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Show details"
                )
            }
        }
    }
}