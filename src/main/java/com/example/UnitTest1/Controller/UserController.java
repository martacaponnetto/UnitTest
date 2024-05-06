package com.example.UnitTest1.Controller;

import com.example.UnitTest1.Entities.User;
import com.example.UnitTest1.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    UserRepository userRepository;

    @GetMapping("/")
    public ResponseEntity<List<User>> getAll(){
        return userRepository.findAll().isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok().body(userRepository.findAll());
    }
    @GetMapping("/{id}")
    public ResponseEntity<User> getSingle(@PathVariable Long id) {
        return userRepository.findById(id).isPresent() ? ResponseEntity.ok().body(userRepository.findById(id).get()) :
                ResponseEntity.notFound().build();
    }
    @PostMapping("/")
    public ResponseEntity<User> create(@RequestBody User user) {
        return ResponseEntity.ok().body(userRepository.save(user));
    }
    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User user) {
        Optional<User> optionalUser = userRepository.findById(id);
        optionalUser.get().setFullName(user.getFullName());
        optionalUser.get().setEmail(user.getEmail());
        return ResponseEntity.of(optionalUser);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<User> delete(@PathVariable Long id){
            userRepository.deleteById(id);
            return ResponseEntity.ok().build();
    }
}


