package com.littlestar.task.Controller;

import com.littlestar.task.domain.UserEditForm;
import com.littlestar.task.entity.User;
import com.littlestar.task.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

// ユーザーマイページおよび情報更新に関するリクエストを処理するコントローラー
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 会員情報編集フォーム表示
    @GetMapping("/user/edit")
    public String editForm(Principal principal, Model model) {

        // 現在ログインしているユーザーIDでDBからユーザー情報を取得
        User user = userService.findByLoginId(principal.getName());

        // ビューに渡すフォームオブジェクト(DTO)を作成し既存情報を設定
        UserEditForm form = new UserEditForm();
        form.setAlias(user.getAlias());
        form.setEmail(user.getEmail());
        form.setAddress(user.getAddress());
        // パスワードはセキュリティ上フォームに事前に設定しない
        form.setProfileImg(user.getProfileImg());

        // モデルにデータを注入
        model.addAttribute("userEditForm", form); // フォームバインディング用DTO
        model.addAttribute("user", user);         // 画面上部の表示用

        // レイアウトに適用するビューfragmentを指定
        model.addAttribute("content", "contents/edit/edit :: user-edit");

        return "layout";
    }

    // 会員情報更新処理
    @PostMapping("/user/edit")
    public String updateProfile(@ModelAttribute UserEditForm form, Principal principal) {

        // サービス層に更新処理を委譲
        // ニックネーム、住所、パスワード、画像などを一括更新
        userService.updateUserInfo(principal.getName(), form);

        // 更新完了後、メインページへリダイレクト
        return "redirect:/";
    }
}
