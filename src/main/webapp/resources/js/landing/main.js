const colors = {
    circles: "#19B8C4",
    lines: "#19B8C4",
};

const options = {
    particles: {
        number: {
            value: 50,
            density: {
                enable: true,
                value_area: 800,
            }
        },
        color: {
            value: colors.circles,
        },
        shape: {
            type: "circle",
            stroke: {
                width: 0,
                color: colors.circles,
            },
            polygon: {
                nb_sides: 5,
            },
            image: {
                src: "",
                width: 100,
                height: 100
            }
        },
        opacity: {
            value: 1,
            random: false,
            anim: {
                enable: false,
                speed: 1,
                opacity_min: 0.1,
                sync: false
            }
        },
        size: {
            value: 15,
            random: true,
            anim: {
                enable: true,
                speed: 8,
                size_min: 0.1,
                sync: false
            }
        },
        line_linked: {
            enable: true,
            distance: 300,
            color: colors.lines,
            opacity: 0.4,
            width: 2
        },
        move: {
            enable: true,
            speed: 12,
            direction: "none",
            random: false,
            straight: false,
            out_mode: "out",
            bounce: false,
            attract: {
                enable: false,
                rotateX: 600,
                rotateY: 1200
            }
        }
    },
    interactivity: {
        detect_on: "canvas",
        events: {
            onhover: {
                enable: true,
                mode: "repulse"
            },
            onclick: {
                enable: false,
                mode: "push",
            },
            resize: true
        },
        modes: {
            grab: {
                distance: 800,
                line_linked: {
                    opacity: 1
                }
            },
            bubble: {
                distance: 800,
                size: 80,
                duration: 2,
                opacity: 0.8,
                speed: 3
            },
            repulse: {
                distance: 100,
                duration: 0.4
            },
            push: {
                particles_nb: 4
            },
            remove: {
                particles_nb: 2
            }
        }
    },
    retina_detect: true
};

particlesJS("particles-js", options);

AOS.init();

const TRUMP_QUOTES = [ // mostly from https://raw.githubusercontent.com/jthurst3/trump/master/assets/quotes.json
  {"quote": "I will be the greatest jobs president that God ever created.", "author": "Donald Trump", "index": 1},
  {"quote": "When Mexico sends its people, they're not sending their best. They're sending people that have lots of problems...they're bringing drugs, they're bringing crime. They're rapists.", "author": "Donald Trump", "index": 2},
  {"quote": "[John McCain]’s not a war hero. He’s a war hero because he was captured. I like people that weren’t captured.", "author": "Donald Trump", "index": 3},
  {"quote": "When was the last time anybody saw us beating, let’s say, China in a trade deal? They kill us. I beat China all the time.", "author": "Donald Trump", "index": 4},
  {"quote": "Free trade is terrible. Free trade can be wonderful if you have smart people. But we have stupid people.", "author": "Donald Trump", "index": 5},
  {"quote": "I have a great relationship with the blacks.", "author": "Donald Trump", "index": 6},
  {"quote": "I think the only difference between me and the other candidates is that I'm more honest and my women are more beautiful.", "author": "Donald Trump", "index": 7},
  {"quote": "I'll tell you, it's Big Business. If there is one word to describe Atlantic City, it's Big Business. Or two words – Big Business.", "author": "Donald Trump", "index": 8},
  {"quote": "All of the women on 'The Apprentice' flirted with me -- consciously or unconsciously. That's to be expected.", "author": "Donald Trump", "index": 9},
  {"quote": "I don't think Ivanka would do that, although she does have a very nice figure. I've said if Ivanka weren't my daughter, perhaps I'd be dating her.", "author": "Donald Trump", "index": 10},
  {"quote": "[My wife] really has become a monster ... I mean monster in the most positive way.", "author": "Donald Trump", "index": 11},
  {"quote": "My fingers are long and beautiful, as, it has been well been documented, are various other parts of my body.", "author": "Donald Trump", "index": 12},
  {"quote": "The beauty of me is that I'm very rich.", "author": "Donald Trump", "index": 13},
  {"quote": "Hey, I'm not saying they're stupid ... I like China. I just sold an apartment for $15 million dollars to somebody from China. Am I supposed to dislike 'em?", "author": "Donald Trump", "index": 14},
  {"quote": "I'm really rich. I'll show you that in a second.", "author": "Donald Trump", "index": 15},
  {"quote": "I have a total net worth and now with the increase it will be well over $10 billion, but here total net worth of $8 billion. Net worth — not assets, not liabilities — a net worth ... I'm not doing that to brag because you know what? I don't have to brag. I don't have to. Believe it or not.", "author": "Donald Trump", "index": 16},
  {"quote": "The concept of global warming was created by and for the Chinese in order to make U.S. manufacturing non-competitive.", "author": "Donald Trump", "index": 17},
  {"quote": "It's freezing and snowing in New York--we need global warming!", "author": "Donald Trump", "index": 18},
  {"quote": "Sorry losers and haters, but my I.Q. is one of the highest -and you all know it! Please don't feel so stupid or insecure,it's not your fault", "author": "Donald Trump", "index": 19},
  {"quote": "My fingers are long and beautiful, as, it has been well documented, are various other parts of my body.", "author": "Donald Trump", "index": 20},
  {"quote": "I think the only difference between me and other candidates is that I'm more honest and my women are more beautiful.", "author": "Donald Trump", "index": 21},
  {"quote": "All of the women on The Apprentice flirted with me - consciously or unconsciously. That's to be expected.", "author": "Donald Trump", "index": 22},
  {"quote": "I've said if Ivanka weren't my daughter, perhaps I'd be dating her.", "author": "Donald Trump", "index": 23},
  {"quote": "A person who is very flat-chested is very hard to be a 10.", "author": "Donald Trump", "index": 24},
  {"quote": "Number one, I have great respect for women. I was the one that really broke the glass ceiling on behalf of women, more than anybody in the construction industry. My relationship, I think, is going to end up being very good with women.", "author": "Donald Trump", "index": 25},
  {"quote": "It's freezing and snowing in New York – we need global warming!", "author": "Donald Trump", "index": 26},
  {"quote": "The concept of global warming was created by and for the Chinese in order to make US manufacturing non-competitive.", "author": "Donald Trump", "index": 27},
  {"quote": "By the way, I have great respect for China. I have many Chinese friends. They live in my buildings all over the place.", "author": "Donald Trump", "index": 28},
  {"quote": "I could stand in the middle of Fifth Avenue and shoot somebody, and I wouldn't lose any voters, OK? It's, like, incredible.", "author": "Donald Trump", "index": 29},
  {"quote": "And did you notice that baby was crying through half of the speech and I didn't get angry? Not once. Did you notice that? That baby was driving me crazy. I didn't get angry once because I didn't want to insult the parents for not taking the kid out of the room!", "author": "Donald Trump", "index": 30},
  {"quote": "Something very important, and indeed society-changing, may come out of the Ebola epidemic that will be a very good thing: NO SHAKING HANDS!", "author": "Donald Trump", "index": 31},
  {"quote": "The worst thing a man can do is go bald. Never let yourself go bald.", "author": "Donald Trump", "index": 32},
  {"quote": "They haven't seen anything like what's coming at us in 25, 30 years. Maybe ever. It's tremendously big and tremendously wet. Tremendous amounts of water.", "author": "Donald Trump", "index": 33},
  {"quote": "North Korean Leader Kim Jong Un just stated that the “Nuclear Button is on his desk at all times.” Will someone from his depleted and food starved regime please inform him that I too have a Nuclear Button, but it is a much bigger & more powerful one than his, and my Button works!", "author": "Donald Trump", "index": 34},
  {"quote": "Actually, throughout my life, my two greatest assets have been mental stability and being, like, really smart... I went from VERY successful businessman, to top T.V. Star to President of the United States (on my first try). I think that would qualify as not smart, but genius....and a very stable genius at that!", "author": "Donald Trump", "index": 35},
  {"quote": "Right now, in a number of states, the laws allow a baby to be born from his or her mother's womb in the ninth month. It is wrong, it has to change.", "author": "Donald Trump", "index": 36},
  {"quote": "I have kept more promises than i made.", "author": "Donald Trump", "index": 37},
  {"quote": "The statement I made on Saturday, the first statement, was a fine statement.", "author": "Donald Trump", "index": 38},
  {"quote": "My use of social media is not Presidential - it’s MODERN DAY PRESIDENTIAL. Make America Great Again!", "author": "Donald Trump", "index": 39},
  {"quote": "I just fired the head of the F.B.I. He was crazy, a real nut job. I faced great pressure because of Russia. That's taken off.", "author": "Donald Trump", "index": 40},
];

let mySiema;
const initQuotes = async () => {

  await fetch('https://api.kanye.rest', { method: 'GET' })
    .then(response => response.json())
    .then(({ quote }) => document.getElementById('kanye').innerText = `"${quote}"`);

  await fetch('https://api.adviceslip.com/advice', {method: 'GET'})
    .then(response => response.json())
    .then(({ slip }) => document.getElementById('advice').innerText = `"${slip.advice}"`);

  document.getElementById('trump').innerText = `"${TRUMP_QUOTES[Math.floor(Math.random() * TRUMP_QUOTES.length)].quote}"`;

  mySiema = new Siema({
    loop: true,
    duration: 500,
  });
  // autoplay
  setInterval(() => mySiema.next(), 4500)
};
initQuotes();



