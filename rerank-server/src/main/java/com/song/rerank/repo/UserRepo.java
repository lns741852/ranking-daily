package com.song.rerank.repo;


import com.song.rerank.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepo extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    /**
     * 根據用戶名查詢
     * @param username 用戶名
     * @return /
     */
    User findByUsername(String username);

}
