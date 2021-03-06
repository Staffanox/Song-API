package htwb.ai.steven.controller;


import htwb.ai.steven.model.Lyrics;
import htwb.ai.steven.repository.LyricsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.List;
import java.util.Optional;

/**
 * RestController which implements REST methods for lyrics-service
 */
@RestController
@RequestMapping("/lyrics")
public class LyricController {

    @Autowired
    private LyricsRepository lyricsRepository;


    /**
     * @return all currently available lyrics in MongoDB
     */
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Lyrics>> getAll() {
        List<Lyrics> lyrics = this.lyricsRepository.findAll();
        return new ResponseEntity<>(lyrics, HttpStatus.OK);
    }

    /**
     * @param id name of a song which is saved in Mongo
     * @return songId (Title) and associated lyrics
     */
    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Lyrics> getLyricsToSong(@PathVariable(value = "id") String id) {

        try {
            Optional<Lyrics> optLyrics = lyricsRepository.findById(id);


            if (optLyrics.isPresent()) {

                Lyrics lyrics = optLyrics.get();
                return new ResponseEntity<>(lyrics, HttpStatus.OK);

            }
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        } catch (
                HttpStatusCodeException ex) {
            return new ResponseEntity<>(null, ex.getStatusCode());
        }
    }


    /**
     * @param lyrics consists of Id (title) and String (lyrics)
     * @return 201 if correct, 400 if not
     */
    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<String> postLyrics(@RequestBody Lyrics lyrics) {

        try {
            if (lyrics.getLyrics() != null && lyrics.getId() != null) {
                this.lyricsRepository.save(lyrics);
                return new ResponseEntity<>("Lyric to song created", HttpStatus.CREATED);
            } else return new ResponseEntity<>("Invalid lyrics", HttpStatus.BAD_REQUEST);

        } catch (
                HttpStatusCodeException ex) {
            return new ResponseEntity<>(ex.getResponseBodyAsString(), ex.getStatusCode());
        }


    }


    /**
     * @param id LyricsId (title)
     * @return 204 if deleted, 404 if not found
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteLyrics(@PathVariable(value = "id") String id) {

        Optional<Lyrics> optionalLyrics = lyricsRepository.findById(id);

        if (optionalLyrics.isPresent()) {
            lyricsRepository.deleteById(id);
            return new ResponseEntity<>("Lyrics successfully deleted", HttpStatus.NO_CONTENT);
        } else
            return new ResponseEntity<>("Lyrics is not available in db", HttpStatus.NOT_FOUND);
    }


    /**
     * @param id Id of lyrics in DB that will be changed
     * @param lyrics new lyrics consisting of id and possibly changed lyrics
     * @return 204 if successful, 400 if not
     */
    @PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<String> putLyrics(@PathVariable(value = "id") String id, @RequestBody Lyrics lyrics) {

        if (!id.equals(lyrics.getId())) {
            return new ResponseEntity<>("URL ID doesnt match payload ID.", HttpStatus.BAD_REQUEST);
        }
        Optional<Lyrics> optionalLyrics = lyricsRepository.findById(id);

        if (!optionalLyrics.isPresent())
            return new ResponseEntity<>("No such Lyrics in DB", HttpStatus.BAD_REQUEST);
        lyricsRepository.save(lyrics);

        return new ResponseEntity<>("Lyrics with ID '" + lyrics.getId() + "' was updated.", HttpStatus.NO_CONTENT);

    }

}
