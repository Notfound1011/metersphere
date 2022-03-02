<template>
  <div id="recentCreatedBugStatistics" style="width: 650px;height:450px;margin: 20px"></div>
</template>

<script>
import {JIRA_ADDRESS, JIRA_AUTH} from "@/common/js/constants";

export default {
  name: "recentCreatedBugStatistics",
  data() {
    return {
      recentlyCreatedData: ''
    }
  },
  created() {
    this.recentlyCreated()
  },
  methods: {
    recentlyCreated() {
      let url = "jira/rest/gadget/1.0/recentlyCreated/generate?projectOrFilterId=filter-10869&periodName=monthly&daysprevious=365&width=580&height=448&returnData=true&inline=true"
      this.$axios.get(url, {headers: {'Authorization': JIRA_AUTH}}).then((res) => {
          if (res.status === 200) {
            this.recentlyCreatedData = res.data
          }
          const recentCreatedBugChartDom = document.getElementById('recentCreatedBugStatistics');
          const recentCreatedBugBarChart = this.$echarts.init(recentCreatedBugChartDom);
          var xAxisData = []
          var resolvedData = []
          var unresolvedData = []
          for (let i = this.recentlyCreatedData.data.length - 1; i >= 0; i--) {
            xAxisData.push(this.recentlyCreatedData.data[i].key);
            resolvedData.push(this.recentlyCreatedData.data[i].resolvedValue);
            unresolvedData.push(this.recentlyCreatedData.data[i].unresolvedValue);
          }
          recentCreatedBugBarChart.setOption({
            title: {
              text: '近一年创建的bug',
              subtext: 'bug创建趋势图',
              link: JIRA_ADDRESS + this.recentlyCreatedData.filterUrl,
              textStyle: {
                fontSize: 25,
                color: "rgba(55, 96, 186, 1)"
              }
            },
            legend: {
              data: ['resolved', 'unresolved'],
              left: '35%'
            },
            toolbox: {
              show: true,
              right: '20',
              feature: {
                dataZoom: {
                  yAxisIndex: 'none'
                },
                dataView: {readOnly: false},
                magicType: {type: ['line', 'bar']},
                restore: {},
                saveAsImage: {}
              }
            },
            tooltip: {},
            xAxis: {
              data: xAxisData,
              name: '月份',
              axisLabel: {interval: 0, rotate: 30},
              nameTextStyle: {
                padding: 20,
              }
            },
            yAxis: [
              {
                name: '数量',
              }
            ],
            grid: {
              top: '20%',
              bottom: 100
            },
            series: [
              {
                name: 'resolved',
                type: 'bar',
                stack: 'Ad',
                itemStyle: {
                  color: 'rgba(33, 199, 0, 0.9)',
                },
                emphasis: {
                  itemStyle: {
                    shadowBlur: 10,
                    shadowColor: 'rgba(255, 99, 71, 1)'
                  }
                },
                data: resolvedData
              },
              {
                name: 'unresolved',
                type: 'bar',
                stack: 'Ad',
                itemStyle: {
                  color: 'rgba(255, 99, 71, 0.9)',
                },
                emphasis: {
                  itemStyle: {
                    shadowBlur: 10,
                    shadowColor: 'rgba(255, 99, 71, 1)'
                  }
                },
                data: unresolvedData
              }
            ]
          })
        }
      )
    }

  }
}
</script>

<style scoped>

</style>
