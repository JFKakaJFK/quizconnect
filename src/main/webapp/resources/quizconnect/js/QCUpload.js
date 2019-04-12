"use strict";

class QCUpload {
    constructor(){}

    static upload(target, endpoint, formId){
        const elem = document.querySelector(target);
        const form = document.querySelector(formId);

        elem.addEventListener('change', () => {
            const file = elem.files[0];
            if(!file){
                return;
            }
            let formData = new FormData(form);

            for(let name in file){
                formData.append(name, file[name]);
            }

            fetch(endpoint, {
                method: 'POST',
                body: formData,
            }).catch(error => console.error(error))
        });
    }
}