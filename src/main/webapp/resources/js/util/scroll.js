"use strict";

const LOAD_OFFSET = window.innerHeight * .75; // how much distance to bottom of page until more content is loaded?
const SELECTOR = '.load';

const loadMore = () => {
  console.debug('loading more in ', document.body.scrollHeight - document.documentElement.scrollTop - window.innerHeight - LOAD_OFFSET, 'px');
  if(window.innerHeight + LOAD_OFFSET >= document.body.scrollHeight - document.documentElement.scrollTop ) {

    document.body.scrollTo(0, -window.innerHeight);

    let loader = document.querySelector(SELECTOR);
    if (loader !== null) {
      console.debug('loading more');
      loader.click();
    } else {
      console.debug('loaded all elements');
      document.removeEventListener('scroll', loadMore)
    }
  }
};

document.addEventListener('scroll', loadMore);