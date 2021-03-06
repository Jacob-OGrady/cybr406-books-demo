package com.cybr406.bookdemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@RestController
public class BookDemoController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    AuthorRepository authorRepository;

    @PostMapping("/authors")
    public ResponseEntity<Author> createAuthor(@RequestBody Author author) {
        Author created = authorRepository.save(author);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/authors")
    public List<Author> findAuthors() {
        return jdbcTemplate.query(
                "select * from author",
                (resultSet, rowNum) -> {
                    // This row mapper is written as a lambda and converts
                    // raw database results into Java objects.
                    Author author = new Author();
                    author.setId(resultSet.getLong("id"));
                    author.setUsername(resultSet.getString("username"));
                    author.setName(resultSet.getString("name"));
                    author.setBio(resultSet.getString("bio"));
                    return author;
                });
    }

    @GetMapping("/authors/{id}")
    public ResponseEntity<Author> findAuthor(@PathVariable Long id) {
        Author result = jdbcTemplate.queryForObject(
                "select * from author where id = ?",
                (resultSet, rowNum) -> {
                    Author author = new Author();
                    author.setId(resultSet.getLong("id"));
                    author.setUsername(resultSet.getString("username"));
                    author.setName(resultSet.getString("name"));
                    author.setBio(resultSet.getString("bio"));
                    return author;
                },
                id); // This id will be used in the place holder.

        return result == null ? new ResponseEntity<>(HttpStatus.NOT_FOUND) : new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/authors/name/{name}")
    public ResponseEntity<Author> findAuthor(@PathVariable String name) {
        Optional<Author> author = authorRepository.findByUsername(name);
        return null;
    }

  @PostMapping("/authors/update-name-dangerous")
  public ResponseEntity<Author> updateNameDangerous(@RequestParam Long id, @RequestParam String name) { 
      String sql = "update author set name = '" + name + "' where id = " + id;
      jdbcTemplate.update(sql);
      return findAuthor(id);
  }

  @PostMapping("/authors/update-name-safe")
  public ResponseEntity<Author> updateNameSafe(@RequestParam Long id, @RequestParam String name) {
      jdbcTemplate.update("update author set name = ? where id = ?", name, id);
      return findAuthor(id);
  }

}
