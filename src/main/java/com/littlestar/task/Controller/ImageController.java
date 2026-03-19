package com.littlestar.task.Controller;

import com.littlestar.task.Exception.BusinessException;
import com.littlestar.task.Exception.ErrorCode;
import com.littlestar.task.entity.Image;
import com.littlestar.task.entity.User;
import com.littlestar.task.repository.UserRepository;
import com.littlestar.task.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;
    private final UserRepository userRepository;

    @PostMapping("/api/image/post")
    public String uploadPostImage(@RequestParam("file") MultipartFile file) {
        return imageService.savePostImage(file, null, 0);
    }

    @PostMapping("/api/image/profile")
    public Image uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam String loginId
    ) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND1));

        return imageService.saveProfileImage(file, user);
    }
}
