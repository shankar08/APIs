package org.interview.service;
import org.interview.dto.UserFilter;
import org.interview.dto.request.CreateUserRequest;
import org.interview.dto.request.UpdateUserRequest;
import org.interview.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {

    UserResponse create(CreateUserRequest request);

    Optional<UserResponse> findById(String id);

    Page<UserResponse> findAll(Pageable pageable);

    Page<UserResponse> search(UserFilter filter, Pageable pageable);

    Optional<UserResponse> update(String id, UpdateUserRequest request);

    boolean delete(String id);

    boolean existsByEmail(String email);
}