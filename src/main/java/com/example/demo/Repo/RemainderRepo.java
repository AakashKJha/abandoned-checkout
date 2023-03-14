package com.example.demo.Repo;

import com.example.demo.entity.TimeStamp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RemainderRepo extends JpaRepository<TimeStamp,Long> {
}
