const themes = {
  default: {
    // default variables are in vars css -> nothing to do
  },
  outrun: { // TODO https://i.redd.it/aepphltiqy911.png
    '--text-glow-color': '#F6019D',
    '--default-font-color': '#FFF',
    '--background-primary': '#261447',
    '--background-lighter': '#241734',
    '--background-darker': '#0D0221',
    '--sidebar-background': '#0D0221',
    '--sidebar-background-gradient': 'linear-gradient(145deg, #0D0221 25%, #241734 100%)',
    '--default-box-shadow': '1px 1px 8px -4px hsla(0, 0%, 100%, .75), 4px 4px 16px 0 #540D6E',
    '--default-active-color': '#FFF',
    '--active-glow-color': '#f706cf',// todo
    '--default-active-background': 'hsla(310, 95%, 50%, .2)',
    '--default-accent-gradient': 'linear-gradient(35deg, #FF3864 25%, #FF6C11 100%)',
    '--game-timer-color': '#FF3864',
    '--chat-message-background': '#D40078',
    '--chat-message-out-background': '#2De2e6',
    '--chat-message-info-background': '#023788',
    // todo
    '--danger': '#ff6c11',
    '--danger-gradient': 'linear-gradient(35deg, #FF3864 25%, #FF6C11 100%)',
    '--success': '#2de2e6',
    '--success-gradient': 'linear-gradient(35deg, #2de2e6 25%, #F706cf 100%)',
    '--error': '#FD1D53',
    '--main-panel-background': 'url("https://i.imgur.com/M8Qj9bu.jpg")', // todo
  },
  light: {
    '--default-font-color': '#424242',
    '--background-primary': '#f2f2f2',
    '--background-lighter': '#ffffff',
    '--background-darker': '#e6e6e6',
    '--sidebar-background': '#ffffff',
    '--sidebar-background-gradient': 'linear-gradient(145deg, #ffffff 25%, #f2f2f2 100%)',
    '--default-box-shadow': '2px 2px 16px 4px hsla(0, 0%, 75%, .2), 4px 4px 32px 8px hsla(0, 0%, 50%, .05)',
    '--default-active-color': '#19B8C4',
    '--default-active-background': 'hsla(173, 69%, 64%, .1)',
    '--default-accent-gradient': 'linear-gradient(35deg, #19B8C4 25%, #54A9CC 100%)',
    '--game-timer-color': '#19B8C4',
    '--chat-message-background': '#d2d2d2',
    '--chat-message-out-background': '#8BD7F3',
    '--chat-message-info-background': '#35d6c7',
  }
};

/**
 * Load the theme from localStorage, if no theme is stored, use the default theme.
 */
const loadTheme = () => {
  let themeName = localStorage.getItem('theme');
  if(themeName === null || !themes.hasOwnProperty(themeName)){
    localStorage.setItem('theme', 'default');
    themeName = 'default';
  }

  document.documentElement.setAttribute('data-theme-name', themeName);
  document.documentElement.style.cssText = '';
  const theme = themes[themeName];
  for(let key of Object.keys(theme)){
    document.documentElement.style.setProperty(key, theme[key])
  }
};

/**
 * Change the currently stored theme.
 *
 * @param name
 *    The name of the new theme.
 */
const changeTheme = (name) => {
  if(themes.hasOwnProperty(name)){
    localStorage.setItem('theme', name);
  }
  loadTheme();
};

// load the theme on page load
loadTheme();