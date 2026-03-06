package com.eduflow.eduflow.user;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.eduflow.eduflow.common.exception.ResourceNotFoundException;
import com.eduflow.eduflow.common.service.S3Service;
import com.eduflow.eduflow.user.dto.UpdateProfileRequest;
import com.eduflow.eduflow.user.dto.UserProfileResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final S3Service s3Service;

    public UserProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToResponse(user);
    }

    public UserProfileResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setName(request.getName());
        userRepository.save(user);
        return mapToResponse(user);
    }

    public UserProfileResponse updateProfilePicture(String email, MultipartFile file) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Delete old picture if exists
        if (user.getProfilePicture() != null) {
            s3Service.deleteFile(user.getProfilePicture());
        }

        String url = s3Service.uploadFile(file, "profiles");
        user.setProfilePicture(url);
        userRepository.save(user);

        return mapToResponse(user);
    }

    private UserProfileResponse mapToResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .profilePicture(user.getProfilePicture())
                .build();
    }
}