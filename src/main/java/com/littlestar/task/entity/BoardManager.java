package com.littlestar.task.entity;

import com.littlestar.task.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@IdClass(BoardManager.class)
@Getter @Setter
public class BoardManager {

    @Id
    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board; // PKかつFK
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;   // PKかつFK

    @Enumerated(EnumType.STRING) // DBのENUMとのマッピング
    private Role role; // OWNER, MODERATOR

    private LocalDateTime assignedAt = LocalDateTime.now();
}
