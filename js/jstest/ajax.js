function getComments() {
  $.ajax({
    url: "/comments",
    method: "GET",
    success: function(comments) {
      // 가져온 댓글 목록을 화면에 렌더링합니다.
      // ...
      alert("안녕");
    }
  });
}

$(function() {
  // 페이지가 로딩될 때, 처음 한 번 댓글 목록을 가져옵니다.
  getComments();

  // 5초마다 댓글 목록을 갱신합니다.
  setInterval(function() {
    getComments();
  }, 5000);

  // 댓글을 작성하는 로직
  $("#comment-form").submit(function(event) {
    event.preventDefault();

    var author = $("#author-input").val();
    var content = $("#content-input").val();

    var comment = { author: author, content: content };

    $.ajax({
      url: "/comments",
      type: "POST",
      contentType: "application/json",
      data: JSON.stringify(comment),
      success: function() {
        // 댓글 작성이 성공하면, 입력폼을 초기화합니다.
        $("#author-input").val("");
        $("#content-input").val("");
      }
    });
  });
});
