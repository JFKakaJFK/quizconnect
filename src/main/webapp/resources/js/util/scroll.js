"use strict";

class ScrollLoader{
  constructor(selector = '.load'){
    this.selector = selector;
    this.LOAD_OFFSET = window.innerHeight * .75; // how much distance to bottom of page until more content is loaded?
  }

  init(){
    document.addEventListener('scroll', this._loadMore);
  }

  _loadMore = () => {
    console.debug('loading more in ', (document.body.scrollHeight - document.documentElement.scrollTop - window.innerHeight - this.LOAD_OFFSET).toFixed(0), 'px');
    if(window.innerHeight + this.LOAD_OFFSET >= document.body.scrollHeight - document.documentElement.scrollTop ) {

      document.body.scrollTo(0, -window.innerHeight);

      let loader = document.querySelector(this.selector);
      if (loader !== null) {
        console.debug('loading more');
        loader.click();
      } else {
        console.debug('loaded all elements');
        document.removeEventListener('scroll', this._loadMore)
      }
    }
  };
}