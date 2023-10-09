package com.example.faceid.Repos;

import com.example.faceid.Entity.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Repository
@Service
public interface usersRepo extends JpaRepository<users, Integer> {
      users  save(users u);
      List<users> findByName(String name);
      List<users> findAll();
      users findByWorkid(String workid);
}
