$('#questionForm').submit(function(e){
    console.log('invoked modal.js');
    e.preventDefault();
    console.log('prevented Default from modal.js');
    $("#questionModal").modal({
        show : true,
        keyboard : false,
        backdrop : 'static'
    });
    console.log('show modal');
});