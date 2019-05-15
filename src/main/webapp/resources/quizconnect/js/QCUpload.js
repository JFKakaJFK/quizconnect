"use strict";

class QCUpload {
  constructor(){}

  static upload(elemId = 'upload', ajax = true, required = false){
    const elem = document.getElementById(`${elemId}`);
    if(elem == null){
      console.error('QCUpload.upload: input element not found');
      return;
    }
    if(required){
      elem.required = true;
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

    if(ajax){
      elem.addEventListener('change', () => this._upload(elem, render, form));
    } else {
      form.addEventListener('submit', (e) => {
        e.preventDefault();
        this._upload(elem, render, form)
          .then(() => form.submit());
      }, false);
    }
  }

  static async _upload(elem, render, form){
    const file = elem.files[0];
    if(!file){
      return;
    }
    let formData = new FormData(form);

    for(let name in file){
      formData.append(name, file[name]);
    }
    await fetch(`/uploads`, {
      method: 'POST',
      body: formData,
    }).catch(error => {
      console.error(error);
    }).finally(() => {
      // form.reset();
      if(elem.required){ // not required after upload happened
        elem.required = false;
      }
      elem.value = '';
      render.click()
    })
  }
}