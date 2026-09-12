package it.uniroma3.siw.festivalcinema.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.festivalcinema.model.Director;
import it.uniroma3.siw.festivalcinema.repository.DirectorRepository;

@Service
public class DirectorService {

    private final DirectorRepository directorRepository;

    public DirectorService(DirectorRepository directorRepository) {
        this.directorRepository = directorRepository;
    }

    @Transactional(readOnly = true)
    public List<Director> findAll() {
        return directorRepository.findAllByOrderBySurnameAscNameAsc();
    }

    @Transactional(readOnly = true)
    public Page<Director> findAll(Pageable pageable) {
        return directorRepository.findAllByOrderBySurnameAscNameAsc(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Director> findById(Long id) {
        return directorRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Director> findByIdWithMovies(Long id) {
        return directorRepository.findByIdWithMovies(id);
    }

    @Transactional
    public Director save(Director director) {
        if (director.getId() == null) {
            return directorRepository.save(director);
        }
        Director existing = directorRepository.findById(director.getId()).orElseThrow();
        existing.setName(director.getName());
        existing.setSurname(director.getSurname());
        existing.setDateOfBirth(director.getDateOfBirth());
        existing.setNationality(director.getNationality());
        return existing;
    }
}
