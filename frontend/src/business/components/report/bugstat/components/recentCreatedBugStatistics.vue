<template>
  <div :id="id" style="width: 600px;height:480px;margin: 20px"></div>
</template>

<script>
import {groupArray, groupByMonth, jiraAddress, jiraAuth} from "@/common/js/utils";
import * as echarts from "echarts";

export default {
  name: "recentCreatedBugStatistics",
  props: ['id', 'qaCreatedBugJQL'],
  data() {
    return {
      recentlyCreatedData: '',
      jira_auth: jiraAuth(),
      jira_address: jiraAddress(),
      props: ['id', 'qaCreatedBugJQL'],
      recentCreatedBugBarChart: null
    }
  },
  methods: {
    recentlyCreated(data) {
      if (this.recentCreatedBugBarChart != null && this.recentCreatedBugBarChart != "" && this.recentCreatedBugBarChart != undefined) {
        this.recentCreatedBugBarChart.dispose();
      }
      this.recentCreatedBugBarChart = this.$echarts.init(document.getElementById(this.id));

      var xAxisData = []
      var totalData = []
      let datas = groupArray(data, 'created')
      let map = new Map()
      for (let i = 0; i < datas.length; i++) {
        try {
          const s = JSON.stringify(datas[i].location.split('-')[0] + '-' + datas[i].location.split('-')[1]);
          if (!map.has(s)) {
            map.set(s, {
              location: datas[i].location.split('-')[0] + '-' + datas[i].location.split('-')[1],
              count: 1,
            });
          } else {
            map.get(s).count++;
          }
        } catch (e) {
          console.log(e);
        }
      }
      let data_new = Array.from(map.values())
      for (let i = data_new.length - 1; i >= 0; i--) {
        xAxisData.push(data_new[i].location);
        totalData.push(data_new[i].count);
      }
      let option = {
        title: {
          text: '测试环境bug趋势',
          // subtext: '本年度bug',
          link: this.jira_address + "/issues/?jql=" + this.qaCreatedBugJQL,
          textStyle: {
            fontSize: 25,
            color: "rgba(55, 96, 186, 1)"
          }
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
          nameGap: 40,
          nameLocation: 'center',
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
            name: 'task',
            type: 'bar',
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                {offset: 0, color: '#83bff6'},
                {offset: 0.5, color: '#188df0'},
                {offset: 1, color: '#188df0'}
              ])
            },
            emphasis: {
              itemStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  {offset: 0, color: '#2378f7'},
                  {offset: 0.7, color: '#2378f7'},
                  {offset: 1, color: '#83bff6'}
                ])
              }
            },
            data: totalData
          }
        ]
      }
      this.recentCreatedBugBarChart.setOption(option, true)
      // //echarts series.bar的点击事件，触发跳转新页面
      // this.recentCreatedBugBarChart.on('click', 'series.bar', obj => {
      //   let url = this.jira_address + "/issues/?jql=" + this.qaCreatedBugJQL + " AND created"
      //   url = url + " > " + obj.name
      //   window.open(url, '_blank');
      // });
    }
  }
}
</script>

<style scoped>

</style>
