const line = document.querySelector("#line"); // TODO refactor charts
const pie = document.querySelector("#pie")

let options = {
  theme: {
    palette: 'palette1',
    mode: 'dark',
    /*
    monochrome: {
      enabled: true,
      color: '#13CFB8',
      // color: 'var(--col-4-m)',
    }
    */
  },
  /*
  colors: [
    'var(--col-2-m)',
    'var(--col-3-m)',
    'var(--col-4-m)',
    'var(--col-5-m)',
  ],
  */
  chart: {
    width: '100%',
    height: 'auto',
    type: 'area'
  },
  series: [{
    name: 'question set 1',
    data: [30,40,35,50,49,60,70,91,125]
  },
    {
      name: 'question set 2',
      data: [60,50,22,18,23,45,56,77,69]
    },
    {
      name: 'question set3',
      data: [88, 120, 95, 80, 95, 76, 40, 50, 45]
    },
    {
      name: 'average',
      data: [60, 70, 60, 55, 45, 40, 35, 40, 50]
    },
  ],
  dataLabels: {
    enabled: false,
  },
  tooltip: {
    enabled: false,
  },
  toolbar: {
    show: false,
    tools: {
      download: false,
    }
  },
  yaxis: {
    show: true,
    labels: {
      show: true,
      style: {
        color: 'var(--fg)',
      },
      formatter: (value) => value,
    }
  },
  xaxis: {
    categories: [1991,1992,1993,1994,1995,1996,1997, 1998,1999]
  },
  legend: {
    show: true,
    labels: {
      colors: 'var(--fg)',
    }
  }
}



let options1 = {
  theme: {
    mode: 'dark',
    palette: 'palette1',
  },
  chart: {
    width: '100%',
    height: 'auto',
    type: 'donut',
  },
  dataLabels: {
    enabled: false,
  },
  tooltip: {
    enabled: false,
  },
  stroke: {
    show: false,
    width: 0,
  },
  series: [44, 55, 41, 17, 15],
  responsive: [{
    breakpoint: 480,
    options: {
      chart: {
        width: 200
      },
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
fetch(URL, {
  method: 'POST',
})
  .then(response => {
    if(!response.ok){
      throw Error(response.statusText || response.status.toString());
    }
    return response;
  })
  .then(response => response.json())
  .then(({ playedSets, setPlayCounts, lastGameScores }) => {
    console.debug(playedSets, setPlayCounts, lastGameScores);

    if(lastGameScores.reduce((acc, val) => acc + val, 0) === 0) return;

    options.series = [{
      name: 'Scores of last games',
      data: lastGameScores
    }];

    let chart = new ApexCharts(line, options);

    line.innerHTML = '';
    chart.render();

    if(playedSets.length !== setPlayCounts.length || playedSets.length === 0) return;

    // TODO handle null values
    options1.labels = playedSets;
    options1.series = setPlayCounts;

    let chart1 = new ApexCharts(
      pie,
      options1
    );
    pie.innerHTML = '';
    chart1.render();
  });

