package com.littlestar.task.service;

import com.littlestar.task.entity.Comment;
import com.littlestar.task.entity.Post;
import com.littlestar.task.entity.User;
import com.littlestar.task.repository.CommentRepository;
import com.littlestar.task.repository.PostRepository;
import com.littlestar.task.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    void saveComment_success() {

        // given
        Long postId = 1L;
        String loginId = "test";
        String content = "댓글 내용";

        Post post = mock(Post.class);
        User user = mock(User.class);

        when(postRepository.findById(postId))
                .thenReturn(Optional.of(post));

        when(userRepository.findByLoginId(loginId))
                .thenReturn(Optional.of(user));

        // when
        commentService.saveComment(
                postId,
                content,
                loginId,
                null
        );

        // then
        ArgumentCaptor<Comment> commentCaptor =
                ArgumentCaptor.forClass(Comment.class);

        verify(commentRepository).save(commentCaptor.capture());

        Comment savedComment = commentCaptor.getValue();

        assertEquals(content, savedComment.getContent());
        assertEquals(post, savedComment.getPost());
        assertEquals(user, savedComment.getUser());
    }
}