

// const express = require('express');
// const app = express();

// app.use(express.urlencoded({ extended: true }));
// app.use(express.json());

// app.post('/boards/comment', (req, res) => {
//   // POST 요청을 처리하는 코드 작성
// });

// app.listen(3000, () => {
//   console.log('서버가 시작되었습니다.');
// });



var itemText =
 $(".itemText").val().replace("\n", "<br>"); //개행처리
//값 셋팅
var objParams = { 
  depth           : "0",
  itemText        : itemText
};

var reply_id;


$.ajax({
  url         :   "/boards/comment",
  dataType    :   "json",
  contentType :   "application/x-www-form-urlencoded; charset=UTF-8",
  type        :   'POST',
  async       :   false, //동기: false, 비동기: ture
  data   :   objParams,
  success     :   function(retVal){
 
    if(retVal.code != "OK") {
      alert(retVal.message);
    }else{
     reply_id = retVal.reply_id;
    }
                             
  },
    error       :   function(request, status, error){
    console.log("AJAX_ERROR");
    }
});

const http = require('http');

const server = http.createServer((req, res) => {
  if (req.method === 'POST') {
    // POST 요청을 처리하는 코드
    // ...
  } else {
    // POST 요청이 아닌 경우 405 에러를 반환
    res.writeHead(405, { 'Content-Type': 'text/plain' });
    res.end('Method Not Allowed');
  }
});

server.listen(3000, () => {
  console.log('Server is running on port 3000');
});