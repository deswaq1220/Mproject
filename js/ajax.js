
function dataSend(){
  const data =$("#postArea").val();
  const sendDTO = {
        result:data
  };
  $.ajax({
          url: "/comment",
          data: sendDTO,
          type: 'POST',
  }).done(function (data){
    $(".itemText").replaceWith(data);
  });
} 
