$(document).ready(function() {
    // 비밀번호 변경 버튼 클릭
    $('#changePasswordBtn').on('click', function() {
        openPasswordModal();
    });

    // 회원탈퇴 버튼 클릭
    $('#deleteAccountBtn').on('click', function() {
        openDeleteModal();
    });

    // 모달 외부 클릭 시 닫기
    $('.myinfo-modal').on('click', function(e) {
        if ($(e.target).hasClass('myinfo-modal')) {
            closePasswordModal();
            closeDeleteModal();
        }
    });

    // ESC 키로 모달 닫기
    $(document).on('keydown', function(e) {
        if (e.key === 'Escape') {
            closePasswordModal();
            closeDeleteModal();
        }
    });
});

// 비밀번호 변경 모달 열기
function openPasswordModal() {
    $('#passwordModal').addClass('active');
    $('body').css('overflow', 'hidden');
}

// 비밀번호 변경 모달 닫기
function closePasswordModal() {
    $('#passwordModal').removeClass('active');
    $('body').css('overflow', 'auto');
    // 폼 초기화
    $('#passwordForm')[0].reset();
}

// 회원탈퇴 모달 열기
function openDeleteModal() {
    $('#deleteModal').addClass('active');
    $('body').css('overflow', 'hidden');
}

// 회원탈퇴 모달 닫기
function closeDeleteModal() {
    $('#deleteModal').removeClass('active');
    $('body').css('overflow', 'auto');
}