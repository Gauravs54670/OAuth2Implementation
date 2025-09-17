package com.gaurav.LMS.Repository;

import com.gaurav.LMS.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    public Optional<UserEntity> findByEmail(String email);
    public Optional<UserEntity> findByResetToken(String resetToken);
    public Optional<UserEntity> findByPassword(String password);
}
