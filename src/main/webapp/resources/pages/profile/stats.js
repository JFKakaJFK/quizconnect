// todo set colors in theme

const areaColor = '#17A2AD';
const donutColors = ['#247291', '#11B8A3', '#17A2AD', '#09BD5A', '#C955DC'];

let areaOptions = {
  theme: {
    mode: 'dark',
  },
  colors: [areaColor],
  stroke: {
    curve: 'smooth', // 'straight'
    colors: ['#17A2AD'],
    width: 2,
  },
  fill: {
    type: 'gradient',
    gradient: {
      shade: 'dark',
      type: 'vertical',
      gradientToColors: [areaColor],
      opacityFrom: .9,
      opacityTo: .1,
    }
  },
  chart: {
    width: '100%',
    height: 'auto',
    type: 'area'
  },
  dataLabels: {
    enabled: false,
  },
  tooltip: {
    enabled: true,
  },
  toolbar: {
    show: false,
  },
  yaxis: {
    show: true,
    labels: {
      show: true,
      style: {
        color: 'var(--default-font-color)',
      },
      formatter: (value) => value,
    }
  },
  legend: {
    show: true,
    labels: {
      colors: 'var(--default-font-color)',
    }
  }
};

let donutOptions = {
  theme: {
    mode: 'dark',
  },
  colors: donutColors,
  chart: {
    width: '100%',
    height: 'auto',
    type: 'donut',
  },
  dataLabels: {
    enabled: false,
  },
  tooltip: {
    enabled: true,
    x: {
      show: false,
    }
  },
  stroke: {
    show: false,
    width: 0,
  },
  responsive: [{
    breakpoint: 480,
    options: {
      legend: {
        position: 'bottom'
      }
    }
  }],
  plotOptions: {
    pie: {
      expandOnClick: false
    }
  }
};

const URL = `/players/stats`;
const areaNode = document.querySelector('#area');
const donutNode = document.querySelector("#donut");

fetch(URL, {
  method: 'POST',
}).then(response => {
    if(!response.ok){
      throw Error(response.statusText || response.status.toString());
    }
    return response;
  })
  .then(response => response.json())
  .then(({ playedSets, setPlayCounts, lastGameScores }) => {
    console.debug(playedSets, setPlayCounts, lastGameScores);

    try {
      if(lastGameScores.reduce((acc, val) => acc + val, 0) === 0) console.warn('User has not played yet/only achieved a score of 0');

      lastGameScores = lastGameScores.map(s => s === 0 ? null : s);

      areaOptions.series = [{
        name: 'Score',
        data: lastGameScores || [],
      }];

      areaNode.innerHTML = '';
      let areaChart = new ApexCharts(areaNode, areaOptions);
      areaChart.render();
    } catch (e) {
      areaNode.parentNode.removeChild(areaNode);
    }

    try {
      if(playedSets.length !== setPlayCounts.length) throw new Error('Played sets and play counts of sets do not match.');

      donutOptions.labels = playedSets || [];
      donutOptions.series = setPlayCounts || [];

      donutNode.innerHTML = '';
      let donutChart = new ApexCharts(donutNode, donutOptions);
      donutChart.render();
    } catch (e) {
      donutNode.parentNode.removeChild(donutNode);
    }
  }).catch(e => {
    console.error(e);
    areaNode.parentNode.removeChild(areaNode);
    donutNode.parentNode.removeChild(donutNode);
  });

