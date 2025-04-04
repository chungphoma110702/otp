package LearnJV.example.demo.Redis.repository;

import LearnJV.example.demo.Redis.entities.RegisterUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface RegisterUserRedisRepository extends JpaRepository<RegisterUserEntity, String> {
}
