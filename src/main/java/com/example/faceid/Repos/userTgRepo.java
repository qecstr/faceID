package com.example.faceid.Repos;

import com.example.faceid.Entity.userTg;
import com.example.faceid.Entity.users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
@Service
public interface userTgRepo extends JpaRepository<userTg, Integer> {
    userTg save(userTg u);
    userTg findByChatId(Long chatId);
    userTg findByUserName(String username);
    userTg findByPhoneNumber(Long phoneNumber);

}
