

// //댓글 작성 버튼 클릭 시 이벤트 핸들러
// const postBtn = document.querySelector("#post");
// postBtn.addEventListener("click", addComment);

//댓글 삭제 버튼 클릭 시 이벤트 핸들러

// const delBtns = document.querySelectorAll(".delete");
// delBtns.forEach((btn)=>{
//   btn.addEventListener("click",deleteComment);
// });

// //댓글 작성
// function addComment() {
//   const commentList = document.querySelector("#comListWrap");
//   const commentInput = document.querySelector("#postArea");
//   const commentText = commentInput.value;

//   if(commentText){
//     // 새로운 댓글 아이템 생성
//     const newCommentItem = document.createElement("li");
//     newCommentItem.classList.add("comList");

//     //댓글 내용 추가
//     const newCommentText = document.createElement("span");
//     newCommentText.classList.add("itemText");
//     newCommentText.textContent = commentText;
//     newCommentItem.appendChild(newCommentText);

//     //작성일 추가
//     const newCommentDate = document.createElement("span");
//     newCommentDate.classList.add("comDate");
//     const now = new Date();
//     const year = now.getFullYear();
//     const month = now.getMonth() +1;
//     const date = now.getDate();
//     newCommentDate.textContent = `%{month}/${data}/${year}`
//     newCommentItem.appendChild(newCommentDate);


//      //작성시간 추가
//     const newCommentTime = document.createElement("span");
//     newCommentTime.classList.add("comTime");
//     const hours = now.getHours();
//     const minutes = now.getMinutes();
//     newCommentTime.textContent = `${hours}:${minutes}`;
//     newCommentTime.appendChild(newCommentTime);

//   //   //댓글 삭제버튼 추가
//   //   const deleteBtn = document.createElement("button");
//   //   deleteBtn.classList.add("deleteBtn");
//   //   deleteBtn.textContent ="삭제";
//   //   newCommentItem.appendChild(deleteBtn);

//   //   // 댓글 아이템 댓글 목록에 추가
//   //   commentList.appendChild(newCommentItem);

//   //   //입력창 초기화
//   //   commentInput.value = "";

//   //   //삭제 버튼 이벤트 핸들러 등록
//   //   deleteBtn.addEventListener("click",deleteComment);
//   // }
// }

// //  //댓글 삭제 함수
// //  function deleteComment(event){
// //   // 삭제할 댓글 아이템 찾기
// //   const commentItem = event.target.parentElement;

// //   //댓글 목록에서 삭제
// //   commentItem.remove();
  
// }


// 댓글 목록에 대한 변수
const comListWrap = document.getElementById('comListWrap');

// 댓글 작성창에 대한 변수
const postArea = document.getElementById('postArea');
const postBtn = document.getElementById('post');

window.onload = function(){
  // 댓글 삭제 버튼 클릭 시
  comListWrap.addEventListener('click', (event) => {
    const target = event.target;
    if (target.classList.contains('deleteBtn')) {
      const commentItem = target.closest('.comList');
      comListWrap.removeChild(commentItem);
    }
  });
  // 작성 버튼 클릭 시
  postBtn.addEventListener('click', () => {
    // 작성 내용 가져오기
    const comment = postArea.value;
    console.log(comment);
  
    // 작성 내용이 비어있는 경우, 경고창 출력 후 함수 종료
    if (comment === '') {
      alert('댓글을 입력해주세요.');
      return;
    }
  
    // 새로운 댓글 항목 생성
    const newComment = document.createElement('li');
    newComment.classList.add('comList');
  
    // 익명 댓글 작성 창
    const anonymousWrap = document.createElement('div');
    anonymousWrap.classList.add('anonymousWrap');
  
    const dateWrap = document.createElement('div');
    dateWrap.classList.add('dateWrap');
  
    const anonymous = document.createElement('span');
    anonymous.classList.add('anonymous');
    anonymous.textContent = '익명';
    anonymousWrap.appendChild(anonymous);
  
    const anonymousBtn = document.createElement('button');
    anonymousBtn.classList.add('anonymousBtn');
    anonymousBtn.textContent = '댓글달기';
    anonymousWrap.appendChild(anonymousBtn);
  
    newComment.appendChild(anonymousWrap);
  
    // 댓글 내용
    const itemText = document.createElement('span');
    itemText.classList.add('itemText');
    itemText.textContent = comment;
    newComment.appendChild(itemText);
  
    // 댓글 작성일
    const date = new Date();
    const comDate = document.createElement('span');
    comDate.classList.add('comDate');
    comDate.textContent = `${date.getMonth() + 1}/${date.getDate()}`;
    dateWrap.appendChild(comDate);
  
    // 댓글 작성시간
    const comTime = document.createElement('span');
    comTime.classList.add('comTime');
    comTime.textContent = `${date.getHours()}:${date.getMinutes()}`;
    dateWrap.appendChild(comTime);
  
    newComment.appendChild(dateWrap);
    // 댓글 목록에 추가
    comListWrap.appendChild(newComment);
  
    // 작성 창 비우기
    postArea.value = '';

  
    
    
  });

  

}

// 좋아요 버튼 클릭하면 카운트 된다
const likeBtn = document.querySelector('.likeBtn');
const likeNum = document.querySelector('.likeNum');

let count = 1;

likeBtn.addEventListener('click', () => {
  likeNum.textContent = count;
  count++;
});














