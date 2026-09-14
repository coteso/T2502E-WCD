// Small helper JS for UI
document.addEventListener('DOMContentLoaded', function(){
  // confirm on delete buttons
  document.querySelectorAll('form button').forEach(function(btn){
    if (btn.textContent.trim().toLowerCase().includes('xóa') || btn.textContent.trim().toLowerCase().includes('xoa')){
      btn.addEventListener('click', function(e){
        if (!confirm('Bạn chắc chắn muốn xóa?')) e.preventDefault();
      });
    }
  });
});
