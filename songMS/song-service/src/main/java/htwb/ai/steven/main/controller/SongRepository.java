package htwb.ai.steven.main.controller;

import org.springframework.data.repository.CrudRepository;

public interface SongRepository extends CrudRepository<Song, Integer> {

    Iterable<Song> findAll();
}
