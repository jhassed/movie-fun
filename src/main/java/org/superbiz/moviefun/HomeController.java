package org.superbiz.moviefun;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;


@Controller
public class HomeController {

    private MoviesBean _moviesBean;

    public HomeController(MoviesBean moviesBean){

        _moviesBean = moviesBean;
    }

    @GetMapping("/setup")
    public String setup(Map<String, Object> model){

        _moviesBean.addMovie(new Movie("Al Pacino","Scar Face",1972));
        _moviesBean.addMovie(new Movie("David Dobkin","Wedding Crashers",1972));
        _moviesBean.addMovie(new Movie("Todd Phillips","Starsky & Hutch",1972));
        _moviesBean.addMovie(new Movie("David Dobkin","Shanghai Knights",2003));
        _moviesBean.addMovie(new Movie("Betty Thomas","I-Spy",1972));
        _moviesBean.addMovie(new Movie("Wes Anderson","The Royal Tenenbaums",1972));
        _moviesBean.addMovie(new Movie("Tom Dey","Shanghai Noon",1972));

        model.put("movies", _moviesBean.getMovies());

        return "setup";

    }

    @GetMapping("/")
    public String index()
    {
        return "index";
    }


}
