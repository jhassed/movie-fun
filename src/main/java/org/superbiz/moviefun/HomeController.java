package org.superbiz.moviefun;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.superbiz.moviefun.albums.Album;
import org.superbiz.moviefun.albums.AlbumFixtures;
import org.superbiz.moviefun.albums.AlbumsBean;
import org.superbiz.moviefun.movies.Movie;
import org.superbiz.moviefun.movies.MovieFixtures;
import org.superbiz.moviefun.movies.MoviesBean;


import java.util.Map;

@Controller
public class HomeController {

    private final MoviesBean moviesBean;
    private final AlbumsBean albumsBean;
    private final MovieFixtures movieFixtures;
    private final AlbumFixtures albumFixtures;
    private final PlatformTransactionManager moviesPlatformTransactionManager;
    private final PlatformTransactionManager albumsPlatformTransactionManager;


    public HomeController(MoviesBean moviesBean, AlbumsBean albumsBean,
                          MovieFixtures movieFixtures, AlbumFixtures albumFixtures,
                          PlatformTransactionManager albumsPlatformTransactionManager, PlatformTransactionManager moviesPlatformTransactionManager) {

        this.moviesBean = moviesBean;
        this.albumsBean = albumsBean;
        this.movieFixtures = movieFixtures;
        this.albumFixtures = albumFixtures;
        this.moviesPlatformTransactionManager = moviesPlatformTransactionManager;
        this.albumsPlatformTransactionManager = albumsPlatformTransactionManager;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/setup")
    public String setup(Map<String, Object> model) {

        TransactionTemplate template;
        template = new TransactionTemplate(moviesPlatformTransactionManager);
        for (Movie movie : movieFixtures.load()) {
            template.execute(new TransactionCallbackWithoutResult()
            {
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    try {
                        moviesBean.addMovie(movie);
                    } catch (Exception ex) {
                        status.setRollbackOnly();
                    }
                }
            });
        }

        template = new TransactionTemplate(albumsPlatformTransactionManager);
        for (Album album : albumFixtures.load()) {
            template.execute(new TransactionCallbackWithoutResult() {
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    try {
                        albumsBean.addAlbum(album);
                    } catch (Exception ex) {
                        status.setRollbackOnly();
                    }
                }
            });
        }
        model.put("movies", moviesBean.getMovies());
        model.put("albums", albumsBean.getAlbums());

        return "setup";
    }
}
