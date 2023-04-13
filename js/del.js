// 댓글 작성 버튼을 누르면 실행됩니다.
function postComment() {
  // 입력된 댓글을 가져옵니다.
  var commentText = document.getElementById("postArea").value;

  // 댓글이 비어 있으면 경고창을 띄우고 함수를 종료합니다.
  if (commentText == "") {
    alert("댓글을 입력해주세요.");
    return;
  }

  // 새로운 댓글을 만듭니다.
  var newComment = document.createElement("li");
  newComment.className = "comList";

  // 작성자 정보를 추가합니다.
  var anonymousWrap = document.createElement("div");
  anonymousWrap.className = "anonymousWrap";
  var anonymous = document.createElement("span");
  anonymous.className = "anonymous";
  anonymous.appendChild(document.createTextNode("익명"));
  anonymousWrap.appendChild(anonymous);
  var delBtn = document.createElement("button");
  delBtn.className = "anonymousBtn delBtn";
  delBtn.style.fontWeight = "bold";
  delBtn.appendChild(document.createTextNode("삭제"));
  delBtn.addEventListener("click", deleteComment);
  anonymousWrap.appendChild(delBtn);
  newComment.appendChild(anonymousWrap);

  // 댓글 내용을 추가합니다.
  var commentTextElement = document.createElement("p");
  commentTextElement.appendChild(document.createTextNode(commentText));
  newComment.appendChild(commentTextElement);

  // 작성일, 작성시간을 추가합니다.
  var dateElement = document.createElement("span");
  dateElement.className = "comDate";
  var now = new Date();
  var month = now.getMonth() + 1;
  var day = now.getDate();
  var hour = now.getHours();
  var minute = now.getMinutes();
  dateElement.appendChild(document.createTextNode(month + "/" + day));
  newComment.appendChild(dateElement);
  var timeElement = document.createElement("span");
  timeElement.className = "comTime";
  timeElement.appendChild(document.createTextNode(hour + ":" + minute));
  newComment.appendChild(timeElement);

  // 댓글 목록에 추가합니다.
  var comListWrap = document.getElementById("comListWrap");
  comListWrap.appendChild(newComment);

  // 입력 창을 초기화합니다.
  document.getElementById("postArea").value = "";
}

// 댓글 삭제 버튼을 누르면 실행됩니다.
function deleteComment() {
  var comList = this.parentNode.parentNode;
  comList.parentNode.removeChild(comList);
}

// 작성하기 버튼에 이벤트를 추가합니다.
var postButton = document.getElementById("post");
postButton.addEventListener("click", postComment);

// 삭제 버튼에 이벤트를 추가합니다.
var delButtons = document.getElementsByClassName("delBtn");
for (var i = 0; i < delButtons.length; i++) {
  delButtons[i].addEventListener("click", deleteComment);
}

// 좋아요 버튼 클릭하면 카운트 된다
const likeBtn = document.querySelector('.likeBtn');
const likeNum = document.querySelector('.likeNum');

let count = 1;

likeBtn.addEventListener('click', () => {
  likeNum.textContent = count;
  count++;
});