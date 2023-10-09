package com.example.faceid.Service;


import com.example.faceid.Entity.users;
import com.example.faceid.Repos.usersRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class userService {

     usersRepo userRepository;
    public void userSave(users u){
        userRepository.save(u);
    }
    public List<users> getUser(String name){
       return userRepository.findByName(name);

    }
}
