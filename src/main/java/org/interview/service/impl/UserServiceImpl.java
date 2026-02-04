package org.interview.service.impl;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.interview.dto.UserFilter;
import org.interview.dto.request.CreateUserRequest;
import org.interview.dto.request.UpdateUserRequest;
import org.interview.dto.response.UserResponse;
import org.interview.entity.User;
import org.interview.exception.DuplicateResourceException;
import org.interview.mapper.UserMapper;
import org.interview.repository.UserRepository;
import org.interview.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse create(CreateUserRequest request) {
        log.info("Creating user with email: {}", request.email());

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("User with email " + request.email() + " already exists");
        }

        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        User saved = userRepository.save(user);
        log.info("User created successfully with id: {}", saved.getId());

        return userMapper.toResponse(saved);
    }

    @Override
    public Optional<UserResponse> findById(String id) {
        log.debug("Finding user by id: {}", id);
        return userRepository.findById(id)
                .map(userMapper::toResponse);
    }

    @Override
    public Page<UserResponse> findAll(Pageable pageable) {
        log.debug("Finding all users with pagination: {}", pageable);
        return userRepository.findAll(pageable)
                .map(userMapper::toResponse);
    }

    @Override
    public Page<UserResponse> search(UserFilter filter, Pageable pageable) {
        log.debug("Searching users with filter: {} and pagination: {}", filter, pageable);

        Specification<User> spec = buildSpecification(filter);
        return userRepository.findAll(spec, pageable)
                .map(userMapper::toResponse);
    }

    @Override
    @Transactional
    public Optional<UserResponse> update(String id, UpdateUserRequest request) {
        log.info("Updating user with id: {}", id);

        return userRepository.findById(id)
                .map(user -> {
                    userMapper.updateEntityFromRequest(request, user);
                    User updated = userRepository.save(user);
                    log.info("User updated successfully: {}", id);
                    return userMapper.toResponse(updated);
                });
    }

    @Override
    @Transactional
    public boolean delete(String id) {
        log.info("Deleting user with id: {}", id);

        if (!userRepository.existsById(id)) {
            return false;
        }

        userRepository.deleteById(id);
        log.info("User deleted successfully: {}", id);
        return true;
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private Specification<User> buildSpecification(UserFilter filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.name() != null && !filter.name().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + filter.name().toLowerCase() + "%"
                ));
            }

            if (filter.email() != null && !filter.email().isBlank()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("email")),
                        filter.email().toLowerCase()
                ));
            }

            if (filter.active() != null) {
                predicates.add(criteriaBuilder.equal(root.get("active"), filter.active()));
            }

            if (filter.createdAfter() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("createdAt"),
                        filter.createdAfter()
                ));
            }

            if (filter.createdBefore() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("createdAt"),
                        filter.createdBefore()
                ));
            }

            // Age filters (calculated from dateOfBirth)
            if (filter.minAge() != null || filter.maxAge() != null) {
                LocalDate now = LocalDate.now();

                if (filter.minAge() != null) {
                    LocalDate maxBirthDate = now.minusYears(filter.minAge());
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(
                            root.get("dateOfBirth"),
                            maxBirthDate
                    ));
                }

                if (filter.maxAge() != null) {
                    LocalDate minBirthDate = now.minusYears(filter.maxAge() + 1);
                    predicates.add(criteriaBuilder.greaterThan(
                            root.get("dateOfBirth"),
                            minBirthDate
                    ));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}