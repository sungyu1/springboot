// /static/js/login-form.js
window.addEventListener('DOMContentLoaded', () => {
    const signupModal = document.getElementById('modal-signup');
    if (signupModal) new bootstrap.Modal(signupModal).show();

    const badModal = document.getElementById('modal-bad');
    if (badModal) new bootstrap.Modal(badModal).show();
});
