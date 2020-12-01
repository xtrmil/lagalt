package se.experis.com.case2020.lagalt.controller;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import se.experis.com.case2020.lagalt.objects.Person;
import se.experis.com.case2020.lagalt.service.FirebaseService;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TestController {

    @Autowired
    FirebaseService firebaseService;

    @GetMapping("/getUserDetails")
    public Person getExample(@RequestHeader String name) throws InterruptedException, ExecutionException {
        return firebaseService.getUserDetails(name);
    }

    @PostMapping("/createUser")
    public String postExample(@RequestBody Person person) throws InterruptedException, ExecutionException {
        return firebaseService.saveUserDetails(person);
    }

    @PutMapping("/updateUser")
    public String putExample(@RequestBody Person person) throws InterruptedException, ExecutionException {
        return firebaseService.updateUserDetails(person);
    }

    @DeleteMapping("/deleteUser")
    public String deleteExample(@RequestHeader String name) {
        return firebaseService.deleteUser(name);
    }
}

