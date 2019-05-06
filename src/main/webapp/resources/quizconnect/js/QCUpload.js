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
    let render = elem.nextElementSibling;
    while(render !== null){
      if(render.classList.contains('upload-ajax-btn')){break;}
      render = render.nextElementSibling;
      if(render === null){
        return;
      }
    }
    const form = elem.closest('form');
    if(form === null){
      return;
    }

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
      }).catch(error => {
        console.error(error);
      }).finally(() => {
        // form.reset();
        elem.value = '';
        render.click()
      })
    });
  }
}