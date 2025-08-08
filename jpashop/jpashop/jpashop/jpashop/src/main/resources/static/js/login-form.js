window.addEventListener('DOMContentLoaded', () => {
    const trigger = document.getElementById('noMemberModalTrigger');
    if (trigger) {
        const modal = new bootstrap.Modal(document.getElementById('noMemberModal'));
        modal.show();
    }
});