<template>
  <div>
    <div id="qaOnlineBugRate" style="width:90%;height:450%;margin: 20px"></div>
  </div>
</template>

<script>
import {groupArray, jiraAddress} from "@/common/js/utils";
import {GROUP_DATA, GROUP_SUNGE, GROUP_ZHOUMIN, GROUP_LILEI, GROUP_LIP, GROUP_FLEXIBLE} from "@/common/js/constants";
import * as echarts from 'echarts';

export default {
  name: "qaOnlineBugRate",
  props: ['id', 'onlineBug', 'qaBug', 'onlineBugJQL'],
  data() {
    return {
      jira_address: jiraAddress(),
      qaOnlineBugRateBarChart: null
    }
  },
  watch: {
    qaBug: { //深度监听，可监听到对象、数组的变化
      handler(newV, oldV) {
        this.qaOnlineBugRate(this.onlineBug, this.qaBug)
        // console.log(newV, oldV)
      },
      deep: true
    },
    onlineBug: { //深度监听，可监听到对象、数组的变化
      handler(newV, oldV) {
        this.qaOnlineBugRate(this.onlineBug, this.qaBug)
        // console.log(newV, oldV)
      },
      deep: true
    }
  },
  mounted() {
    this.qaOnlineBugRate(this.onlineBug, this.qaBug)
  },
  methods: {
    qaOnlineBugRate(onlineBug, qaBug) {
      let that = this
      if (that.qaOnlineBugRateBarChart != null && that.qaOnlineBugRateBarChart != "" && that.qaOnlineBugRateBarChart != undefined) {
        this.qaOnlineBugRateBarChart.dispose();
      }
      that.qaOnlineBugRateBarChart = that.$echarts.init(document.getElementById('qaOnlineBugRate'));

      let dataNumerator = groupArray(onlineBug, 'customfield_10406')
      let dataDenominator = groupArray(qaBug, 'creator')

      // js比较两个数组对象,取出key相同的做value相除
      let percentageList = [];
      for (let i = 0; i < dataDenominator.length; i++) {
        let obj = dataDenominator[i], num = obj.location, flag = false;
        for (let j = 0; j < dataNumerator.length; j++) {
          let aj = dataNumerator[j], n = aj.location, percentageObj = {};
          //如果location对应匹配
          if (n == num) {
            percentageObj["location"] = n;
            percentageObj["count"] = (aj.count * 100 / obj.count).toFixed(4);
            percentageList.push(percentageObj);
          }
        }
      }
      let xAxisData = []
      let totalData = []
      //定义QA的四个组和组内成员
      let groupData = GROUP_DATA
      let groupSunge = GROUP_SUNGE
      let groupZhoumin = GROUP_ZHOUMIN
      let groupLilei = GROUP_LILEI
      let groupLip = GROUP_LIP
      let groupFlexible = GROUP_FLEXIBLE
      let groupTotalData = [0, 0, 0, 0, 0]

      Object.values(percentageList).forEach(value => {
        xAxisData.push(value.location);
        totalData.push(value.count);
        if (groupSunge.includes(value.location)) {
          groupTotalData[0] = groupTotalData[0] + parseFloat(value.count)
        }
        if (groupZhoumin.includes(value.location)) {
          groupTotalData[1] = groupTotalData[1] + parseFloat(value.count)
        }
        if (groupLilei.includes(value.location)) {
          groupTotalData[2] = groupTotalData[2] + parseFloat(value.count)
        }
        if (groupLip.includes(value.location)) {
          groupTotalData[3] = groupTotalData[3] + parseFloat(value.count)
        }
        if (groupFlexible.includes(value.location)) {
          groupTotalData[4] = groupTotalData[4] + parseFloat(value.count)
        }
      });
      let option = {
        title: {
          text: 'ONLINE BUG_RATE (QA)',
          subtext: 'summary of the bug rate QA caused',
          link: that.jira_address + "/issues/?jql=" + this.onlineBugJQL,
          textStyle: {
            fontSize: 18,
            color: "rgba(55, 96, 186, 1)"
          }
        },
        legend: {
          data: ['按人', '按组'],
          selectedMode: 'single', // 单选
          selected: {
            按人: true,
            按组: false
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
        tooltip: {
          trigger: 'axis',
          transitionDuration: 0, //echarts防止tooltip的抖动
          formatter: '{b0}<br/>{a0}: {c0}%<br />'  // 显示百分比
        },
        xAxis: {
          data: xAxisData,
          name: '人',
          nameLocation: 'center',
          nameGap: 35,
          axisLabel: {interval: 0, rotate: 30},
          nameTextStyle: {
            padding: 20,
          }
        },
        yAxis: [
          {
            type: 'value',
            axisLabel: {
              show: true,
              interval: 'auto',
              formatter: '{value}%'
            },
          }
        ],
        grid: {
          top: '20%',
          bottom: 100
        },
        series: [
          {
            name: '按人',
            type: 'bar',
            showSymbol: false,
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
          },
          {
            name: '按组',
            type: 'bar',
            showSymbol: false,
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
            data: groupTotalData
          }
        ]
      }
      that.qaOnlineBugRateBarChart.setOption(option, true)
      // legend切换时触发，更新options.xAxis中的值
      that.qaOnlineBugRateBarChart.on('legendselectchanged', obj => {
        var options = that.qaOnlineBugRateBarChart.getOption()
        //这里是选择切换什么样的x轴，那么他会进行对Y值的切换
        if (obj.name == '按人') {
          options.xAxis = {
            data: xAxisData,
            name: '人',
            nameLocation: 'center',
            nameGap: 35,
            axisLabel: {interval: 0, rotate: 30},
            nameTextStyle: {
              padding: 20,
            }
          }
        } else if (obj.name == '按组') {
          options.xAxis = {
            data: groupData,
            name: '组',
            nameLocation: 'center',
            nameGap: 35,
            axisLabel: {interval: 0, rotate: 30},
            nameTextStyle: {
              padding: 20,
            }
            // 大多数数据更新了后，已经初始化后的图表X轴或者Y轴仍然不变。 因为xAxis或者yAxis，在初始化的时候赋值为一个对象，
            // 而刷新数据的时候只是刷新xAxis的data部分，这个时候xAxis是找不到数组中到底哪个数据在刷新，
            // 因此，你可以直接修改整个xAxis的值，而不是xAxis.data
          }
        }
        that.qaOnlineBugRateBarChart.setOption(options, true)
      })
      //echarts series.bar的点击事件，触发跳转新页面
      that.qaOnlineBugRateBarChart.on('click', 'series.bar', obj => {
        let url = that.jira_address + "/issues/?jql=" + that.onlineBugJQL + " AND tester"
        if (groupData.includes(obj.name)) {
          switch (obj.name) {
            case "QA1组(孙戈)" :
              url = url + " in (" + groupSunge + ")"
              break;
            case "QA2组(周敏)" :
              url = url + " in (" + groupZhoumin + ")"
              break;
            case "QA3组(李磊)" :
              url = url + " in (" + groupLilei + ")"
              break;
            case "QA4组(lip)" :
              url = url + " in (" + groupLip + ")"
              break;
            case "机动组" :
              url = url + " in (" + groupFlexible + ")"
              break;
          }
          window.open(url, '_blank');
        } else {
          url = url + " = " + obj.name
          window.open(url, '_blank');
        }
      });
    },

  }
}
</script>

<style scoped>

</style>
