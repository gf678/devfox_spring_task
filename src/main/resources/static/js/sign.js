const signUpButton = document.getElementById('signUp');
const signInButton = document.getElementById('signIn');
const findPasswordButton = document.getElementById('findPassword');
const container = document.getElementById('container');

const signUpForm = document.querySelector('.sign-up-container');
const signInForm = document.querySelector('.sign-in-container');
const findPasswordForm = document.querySelector('.find-password-container'); // 소문자로 수정

// 초기 상태: find password 숨기기
findPasswordForm.style.display = 'none';

// Sign Up 버튼 클릭
signUpButton.addEventListener('click', () => {
    container.classList.add("right-panel-active");
    signUpForm.style.display = 'block';
    signInForm.style.display = 'block';
    findPasswordForm.style.display = 'none';
});

// Sign In 버튼 클릭
signInButton.addEventListener('click', () => {
    container.classList.remove("right-panel-active");
    signUpForm.style.display = 'block';
    signInForm.style.display = 'block';
    findPasswordForm.style.display = 'none';
});

// Forgot Password 클릭
findPasswordButton.addEventListener('click', () => {
    container.classList.add("right-panel-active"); // 오른쪽 패널로 이동
    signUpForm.style.display = 'none';
    signInForm.style.display = 'none';
    findPasswordForm.style.display = 'block';
});
