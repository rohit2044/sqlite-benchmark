package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.repository.UserRepository;
import org.example.repository.entity.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

   private final UserRepository userRepository;

   public void createUser(User user) {
       userRepository.save(user);
   }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    public User updateUser(User updatedUser) {
        return userRepository.save(updatedUser);
    }

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }
}