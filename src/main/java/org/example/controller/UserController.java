package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.repository.UserRepository;
import org.example.repository.entity.User;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping(path="/add")
    public String addNewUser(@RequestParam String name, @RequestParam String lastName) {
        User user = new User();
        user.setFirstName(name);
        user.setLastName(lastName);
        userService.createUser(user);
        return "Saved";
    }

    @DeleteMapping(path = "/delete")
    public void delete(@RequestParam Integer id) {
        userService.deleteUser(id);
    }
    @GetMapping(path = "/getUser")
    public User getUser(@RequestParam Integer id) {
        return userService.getUserById(id);
    }
    @PostMapping(path = "/update")
    public User update(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @GetMapping(path="/all")
    public Iterable<User> getAllUsers() {
        return userService.getAllUsers();
    }
}