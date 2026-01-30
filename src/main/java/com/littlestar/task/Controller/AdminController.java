package com.littlestar.task.Controller;

import com.littlestar.task.entity.Board;
import com.littlestar.task.entity.Role;
import com.littlestar.task.entity.User;
import com.littlestar.task.repository.BoardRepository;
import com.littlestar.task.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    // 管理者メインページ
    @GetMapping("/boards")
    public String adminBoardPage(Model model) {
        model.addAttribute("boards", boardRepository.findAll());
        model.addAttribute("users", userRepository.findAll());
        return "admin/admin";
    }

    //IDを含むすべてのユーザーを検索
    @GetMapping("/users/search")
    public String searchUser(@RequestParam String loginId, Model model) {
        // IDを含むすべてのユーザーを検索
        List<User> searchResults = userRepository.findByLoginIdContaining(loginId);

        model.addAttribute("boards", boardRepository.findAll());
        model.addAttribute("users", searchResults); // 検索されたユーザー結果を渡す
        return "admin/admin";
    }

    // ユーザー権限変更
    @PostMapping("/users/update-role")
    @Transactional
    public String updateUserRole(@RequestParam Long userId, @RequestParam String newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("存在しないユーザーです。"));

        // 管理者権限は変更できません。
        if (user.getRole() == Role.ADMIN) {
            throw new IllegalStateException("最高管理者の権限は変更できません。");
        }

        // 受け取った文字列をRole Enumに変換して保存
        user.setRole(Role.valueOf(newRole));

        return "redirect:/admin/boards";
    }

    // 掲示板作成
    @PostMapping("/boards/create")
    public String createBoard(@RequestParam String name, @RequestParam String description) {
        Board board = new Board();
        board.setName(name);
        board.setDescription(description);
        boardRepository.save(board);
        return "redirect:/admin/boards";
    }
}