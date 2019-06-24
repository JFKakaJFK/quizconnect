const themes = {
  default: {
    '--col-1-d': '#303e4f',
    '--col-1-m': '#4a607a',
    '--col-1-l': '#919EAD',

    '--col-2-d': '#247291',
    '--col-2-m': '#2A86AB',
    '--col-2-l': '#54A9CC',

    '--col-3-d': '#17A2AD',
    '--col-3-m': '#19B8C4',
    '--col-3-l': '#53CAD4',

    '--col-4-d': '#11B8A3',
    '--col-4-m': '#13CFB8',
    '--col-4-l': '#65E3D4',

    '--col-5-d': '#09BD5A',
    '--col-5-m': '#0AD464',
    '--col-5-l': '#5DE59A',

    '--gray-1': '#383D42',
    '--gray-2': '#5A626B',
    '--gray-3': '#7A8591',
    '--gray-4': '#B3BECD',
    '--gray-5': '#EFF2F5',
  },
  outrun: { // TODO https://i.redd.it/aepphltiqy911.png
    '--col-1-d': '#303',
    '--col-1-m': '#4a6',
    '--col-1-l': '#919',

    '--col-2-d': '#247',
    '--col-2-m': '#2A8',
    '--col-2-l': '#54A',

    '--col-3-d': '#17A',
    '--col-3-m': '#19B',
    '--col-3-l': '#53C',

    '--col-4-d': '#11B',
    '--col-4-m': '#13C',
    '--col-4-l': '#65E',

    '--col-5-d': '#09B',
    '--col-5-m': '#0AD',
    '--col-5-l': '#5DE',

    '--gray-1': '#383',
    '--gray-2': '#5A6',
    '--gray-3': '#7A8',
    '--gray-4': '#B3B',
    '--gray-5': '#EFF',
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