"use strict";

class QCUpload {
  constructor(){}

  static upload(username, elemId = 'upload'){
    if(username == null || username === ''){
      console.error('QCUpload.upload: username is invalid');
      return;
    }
    const elem = document.getElementById(`${elemId}`);
    if(elem == null){
      console.error('QCUpload.upload: input element not found');
      return;
    }
    const form = elem.closest('form');

    elem.addEventListener('change', () => {
      const file = elem.files[0];
      if(!file){
        return;
      }
      let formData = new FormData(form);

      for(let name in file){
        formData.append(name, file[name]);
      }

      fetch(`/uploads/${username}`, {
        method: 'POST',
        body: formData,
      }).then(response => {
        document.querySelector('.upload').click();
        // form.reset();
        elem.value = '';
        return response;
      }
      ).catch(error => {
        // form.reset();
        elem.value = '';
        console.error(error);
        document.querySelector('.upload').click();
      })
    });
  }
}