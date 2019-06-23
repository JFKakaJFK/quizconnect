"use strict";
/**
 * A simple countdown, destroys itself after finishing and allows for a callback
 *
 * Currently uses a easeInCubic easing function for the animations.
 */
class Countdown {

  /**
   * Creates a new Countdown instance, needs the inital time, a selector to render inside and an optional callback
   *
   * The areaOptions are:
   *
   * - selector ... any valid css Selector (required)
   * - values   ... either an array of values or the number of values to count down
   * - callback ... a callback which is executed after the countdown finished
   * - easing   ... the easing to use ('linear'|'easeInQuad'|'easeInCubic'|'easeInQuart'|'easeInQuint')
   *                the default easing is 'easeInCubic'
   *
   * @param options
   */
  constructor(options){
    const defaults = {
      selector: null,
      values: [],
      callback: () => {},
    };

    const settings = Object.assign(defaults, options);
    const easings = ['linear', 'easeInQuad', 'easeInCubic', 'easeInQuart', 'easeInQuint'];

    this._from = Array.isArray(settings.values) ? settings.values.length : parseInt(settings.values);
    if(isNaN(this._from) || this._from < 0){
      throw new Error(`The values are invalid`);
    }
    this._current = this._from;
    if(!settings.selector){
      throw new Error(`Selector '${settings.selector}' is invalid`);
    }
    this._selector = settings.selector;
    this._callback = settings.callback;
    this._initialized = false;
    this._MINSIZE = 0;
    this._MAXSIZE = 10;
    this._easingExp = easings.indexOf(settings.easing);
    if(this._easingExp === -1) this._easingExp = 3; // default is easeInCubic
    this._values = settings.values;
  }

  /**
   * Starts the countdown
   */
  start(){
    this.destroy();
    this._current = this._from;
    this._start = false;
    this._createElement();
    this._initialized = true;
    window.requestAnimationFrame(this._countdown.bind(this));
  }

  /**
   * Restores the initial HTML state
   */
  destroy(){
    this._initialized = false;
    if(!this._element) return;
    let pn = this._element.parentNode;
    if(pn) pn.removeChild(this._element);
    this._element = null;
  }

  /**
   * Creates the countdown element
   *
   * @private
   */
  _createElement(){
    this._element = document.createElement('span');
    this._element.style.display = 'inline-block';
    this._element.style.textTransform = 'uppercase';
    this._element.innerText = this._values[0] !== undefined ? this._values[0] : this._from - 1;
    document.querySelector(this._selector).appendChild(this._element);
  }

  /**
   * Animates the countdown
   *
   * @param timestamp
   * @private
   */
  _countdown(timestamp){
    if(!this._initialized) return;
    if(this._current < 0) return;
    // set initial time
    if(!this._start) this._start = timestamp;

    let delta = timestamp - this._start;
    let elapsed = delta / 1000; // elapsed time in seconds
    let progress = delta % 1000; // progress of this second 0-999ms

    let easing = Math.pow(progress / 1000, this._easingExp); // ^3 = easeInCubic
    // increase size from MINSIZE to MAXSIZE according to easing function
    this._element.style.transform = `scale(${((this._MAXSIZE - this._MINSIZE) * easing) + this._MINSIZE})`;
    // decrease opacity according to easing function
    this._element.style.opacity = 1 - easing;

    // if a second passed, decrease the displayed value
    let oldVal = this._current;
    this._current = this._from - Math.floor(elapsed);
    let text = this._values[this._from - this._current];
    if(oldVal > this._current) this._element.innerText = text !== undefined ? text : this._current - 1;

    // keep _emptying or restore initial markup and execute the callback
    if(this._from > elapsed){
      window.requestAnimationFrame(this._countdown.bind(this));
    } else {
      if(this._element){
        this.destroy.bind(this)();
      }
      this._callback();
    }
  }
}

export default Countdown;
