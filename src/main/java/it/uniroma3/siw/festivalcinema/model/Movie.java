package it.uniroma3.siw.festivalcinema.model;

import java.util.List;
import java.util.Objects;

import it.uniroma3.siw.festivalcinema.validation.NotFutureYear;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @NotNull
    @Min(1888)
    @NotFutureYear
    @Column(nullable = false)
    private Integer year;

    @NotNull
    @Min(1)          //minuti
    @Column(nullable = false)
    private Integer duration;

    @NotBlank
    @Column(nullable = false)
    private String genre;

    @NotBlank
    @Column(nullable = false)
    private String productionCountry;

    //associazioni

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "director_id", nullable = false)
    private Director director;

    @ManyToMany(mappedBy = "movies", fetch = FetchType.LAZY)
    private List<Festival> festivals;

    @OneToMany(mappedBy = "movie")
    private List<Screening> screenings;

    @OneToMany(mappedBy = "movie")
    private List<Review> reviews;

    //get e set

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getProductionCountry() {
        return productionCountry;
    }

    public void setProductionCountry(String productionCountry) {
        this.productionCountry = productionCountry;
    }

    public Director getDirector() {
        return director;
    }

    public void setDirector(Director director) {
        this.director = director;
    }

    public List<Festival> getFestivals() {
        return festivals;
    }

    public void setFestivals(List<Festival> festivals) {
        this.festivals = festivals;
    }

    public List<Screening> getScreenings() {
        return screenings;
    }

    public void setScreenings(List<Screening> screenings) {
        this.screenings = screenings;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Movie other = (Movie) obj;
        return Objects.equals(id, other.id);
    }
}
