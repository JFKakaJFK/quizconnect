"use strict";

const LOAD_DISTANCE = window.innerHeight * .75; // how much distance to bottom of page until more content is loaded?
const SELECTOR = '.load';

const loadMore = () => {
  console.log('loading more in ', document.body.scrollHeight - document.documentElement.scrollTop - window.innerHeight - LOAD_DISTANCE, 'px');
  if(window.innerHeight + LOAD_DISTANCE >= document.body.scrollHeight - document.documentElement.scrollTop ) {

    document.body.scrollTo(0, -window.innerHeight);

    let loader = document.querySelector(SELECTOR);
    if (loader !== null) {
      console.log('loading more');
      loader.click();
    } else {
      console.log('loaded all elements');
      document.removeEventListener('scroll', loadMore)
    }
  }
};

document.addEventListener('scroll', loadMore);