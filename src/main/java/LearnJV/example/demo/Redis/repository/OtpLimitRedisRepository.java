package LearnJV.example.demo.Redis.repository;

import LearnJV.example.demo.Redis.entities.OtpLimitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface OtpLimitRedisRepository extends JpaRepository<OtpLimitEntity, String> {
}
