package mk.ukim.finki.wp.kol2023.g2.service.impl;

import mk.ukim.finki.wp.kol2023.g2.model.Director;
import mk.ukim.finki.wp.kol2023.g2.model.Genre;
import mk.ukim.finki.wp.kol2023.g2.model.Movie;
import mk.ukim.finki.wp.kol2023.g2.repository.DirectorRepository;
import mk.ukim.finki.wp.kol2023.g2.repository.MovieRepository;
import mk.ukim.finki.wp.kol2023.g2.service.MovieService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final DirectorRepository directorRepository;

    public MovieServiceImpl(MovieRepository movieRepository, DirectorRepository directorRepository) {
        this.movieRepository = movieRepository;
        this.directorRepository = directorRepository;
    }

    @Override
    public List<Movie> listAllMovies() {
        return movieRepository.findAll();
    }

    @Override
    public Movie findById(Long id) {
        return movieRepository.findById(id).get();
    }

    @Override
    public Movie create(String name, String description, Double rating, Genre genre, Long director) {
        Director d = directorRepository.findById(director).get();
        Movie movie = new Movie(name,description,rating,genre,d);
        return movieRepository.save(movie);
    }

    @Override
    public Movie update(Long id, String name, String description, Double rating, Genre genre, Long director) {
        Movie m  = movieRepository.findById(id).get();
        Director d = directorRepository.findById(director).get();
        m.setName(name);
        m.setDescription(description);
        m.setRating(rating);
        m.setGenre(genre);
        m.setDirector(d);
        return movieRepository.save(m);
    }

    @Override
    public Movie delete(Long id) {
        Movie m = movieRepository.findById(id).get();
        movieRepository.delete(m);
        return m;
    }

    @Override
    public Movie vote(Long id) {
        return null;
    }

    @Override
    public List<Movie> listMoviesWithRatingLessThenAndGenre(Double rating, Genre genre) {
        List<Movie> m = new ArrayList<>();
        if(rating == null && genre == null){
            m = movieRepository.findAll();
        }
        if(rating != null && genre == null){
            m = movieRepository.findByRatingLessThan(rating);
        }
        if(rating == null && genre != null){
            m = movieRepository.findByGenre(genre);
        }
        if(rating != null && genre != null){
            m = movieRepository.findByRatingLessThanAndGenre(rating,genre);
        }
        return m;
    }
}
