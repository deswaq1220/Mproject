
const textarea = document.querySelector('#postArea');
const itemAdd = document.querySelector('#post');
const items = document.querySelector('#comListWrap');

const onAdd = () => {
  const text = textarea.value;
  if(text === ''){
    text.focus();
    return;
  }

  const item = document.createElement('li');
  item.setAttribute('class','comList');


  const itemText = document.createElement('li')
  itemText.setAttribute('class','itemText');
  itemText.innerHTML = text;

  const itemDel = document.createElement('button');
  itemDel.setAttribute('class','anonmousBtn');
  itemDel.innerHTML = ' <button class="anonymousBtn" font-weight : bold ;>댓글달기</button>';
  // itemDel.addEventListener('click', () => { // 본인이 달은 댓글일 경우 삭제에 써얗ㅁ
  //   items.removeChild(item);
  // });
  
  item.appendChild(itemText);
  item.appendChild(itemDel);
  items.appendChild(item);
  textarea.value = '';
  textarea.focus();
};

itemAdd.addEventListener('click', () =>{
  onAdd();
});

// textarea.addEventListener('keypress', (event) =>{
//   if(event.key === 'Enter'){
//     onAdd();
//   }
//   return;
// });



