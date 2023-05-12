package com.example.demo.Repo;
import com.example.demo.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageRepo extends JpaRepository<Message, Long> {
    Optional<Message> findByCartId(String id);
}
