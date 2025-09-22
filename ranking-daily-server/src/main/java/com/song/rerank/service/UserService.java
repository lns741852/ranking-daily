package com.song.rerank.service;

import com.song.rerank.repo.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class UserService {

    private final UserRepo userRepo;


}
