<template>
  <div>
    <div :id="id" style="width:95%;height:500%;margin: 20px"></div>
  </div>
</template>

<script>
import {groupArray, jiraAddress, map2List} from "@/common/js/utils";
import {GROUP_DATA, GROUP_SUNGE, GROUP_ZHOUMIN, GROUP_LILEI, GROUP_LIP, GROUP_FLEXIBLE} from "@/common/js/constants";
import * as echarts from 'echarts';

export default {
  name: "devBugStat",
  props: ['id', 'qaCreatedBugJQL', 'ldapUser'],
  data() {
    return {
      jira_address: jiraAddress(),
      devBugBarChart: null,
      url: jiraAddress() + "/issues/?jql=" + this.qaCreatedBugJQL
    }
  },
  methods: {
    devBugStat(data) {

      const developers = [];
      const users = [];
      groupArray(data, 'customfield_10300').forEach((value) => { //数组循环
          const valueObj = {};
          valueObj["user"] = value["location"];
          developers.push(valueObj["user"])
          valueObj["url"] = this.jira_address + "/issues/?jql=" + this.qaCreatedBugJQL + " AND developer = \"" + value["location"] + "\"";
          valueObj["total"] = value["count"];
          users.push(valueObj)
        }
      )

      let xAxisData = []
      let totalData = []
      Object.values(users).forEach(value => {
        xAxisData.push(value.user);
        totalData.push(value.total);
      })

      let arr = []
      Object.values(this.ldapUser).forEach(value => {
        if (developers.includes(value["user"])) {
          const valueObj = {};
          valueObj["user"] = value["user"]
          valueObj["group"] = value["group"]
          arr.push(valueObj)
        }
      });

      //用reduce时：
      //reduce:计算数组元素相加后的总和
      //reduce方法接受两个参数，第一个是函数，第二个是初始值

      let titleList = this.ldapUser.map(item => {
        return item.group
      })
      let groupPeople = titleList.reduce(function (prev, next) {
        prev[next] = (prev[next] + 1) || 1;
        return prev;
      }, {});

      // console.log(groupPeople);//获得{[CBT-Derivatives]: 4, [BD]: 20, [WEB]: 22,…}

      //合并方法
      let userMap = {}; //首先声明一个空对象作作为 map

      //遍历第一给数组
      arr.forEach((item) => {
        userMap[item.user] = {
          user: item.user,
          url: item.url,
          total: item.total,
          group: item.group,
        };
      });

      users.forEach((item) => {
        userMap[item.user] = userMap[item.user] || {}; //初始化对象
        userMap[item.user].url = item.url;
        userMap[item.user].total = item.total;
        userMap[item.user].group = userMap[item.user].group || 0;
      });

      //map 转 arr
      let arrList = map2List(userMap)

      var map = {}, dest = [];
      for (var i = 0; i < arrList.length; i++) {
        var ai = arrList[i];
        if (!map[ai.group]) {
          dest.push({
            group: ai.group,
            total: ai.total,
            people: groupPeople[ai.group],
            item: [ai]
          });
          map[ai.group] = ai;
        } else {
          for (var j = 0; j < dest.length; j++) {
            var dj = dest[j];
            if (dj.group == ai.group) {
              dj.total = dj.total + ai.total
              dj.item.push(ai);
              break;
            }
          }
        }
      }

      let xAxisGroupData = []
      let totalGroupAvgData = []
      let totalGroupData = []
      dest.forEach((item) => {
        let num = item.total / item.people
        item.avg = Math.floor(num * 100) / 100
        xAxisGroupData.push(item.group.split("[")[1].split("]")[0])
        totalGroupAvgData.push(item.avg)
        totalGroupData.push(item.total)
      })

      let that = this
      if (that.devBugBarChart != null && that.devBugBarChart != "" && that.devBugBarChart != undefined) {
        this.devBugBarChart.dispose();
      }
      that.devBugBarChart = that.$echarts.init(document.getElementById(this.id));

      let option = {
        title: {
          text: 'DEVELOPER BUG ANALYSIS',
          subtext: 'summary of developer bug',
          link: that.jira_address + "/issues/?jql=" + this.qaCreatedBugJQL,
          textStyle: {
            fontSize: 18,
            color: "rgba(55, 96, 186, 1)"
          }
        },
        legend: {
          data: ['组人均', '按组', '按人'],
          selectedMode: 'single', // 单选
          selected: {
            组人均: true,
            按组: false,
            按人: false
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
          data: xAxisGroupData,
          name: '组人均',
          nameLocation: 'center',
          nameGap: 35,
          axisLabel: {interval: 0, rotate: 30},
          nameTextStyle: {
            padding: 20,
          }
        },
        yAxis: [
          {
            name: '数量',
          },
          {
            type: 'value',
          }
        ],
        grid: {
          top: '20%',
          bottom: 100
        },
        series: [
          {
            name: '组人均',
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
            data: totalGroupAvgData
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
            data: totalGroupData
          },
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
          }
        ]
      }
      that.devBugBarChart.setOption(option, true)
      // legend切换时触发，更新options.xAxis中的值
      that.devBugBarChart.on('legendselectchanged', obj => {
        var options = that.devBugBarChart.getOption()
        //这里是选择切换什么样的x轴，那么他会进行对Y值的切换
        if (obj.name == '组人均') {
          options.xAxis = {
            data: xAxisGroupData,
            name: '组人均',
            nameLocation: 'center',
            nameGap: 35,
            axisLabel: {interval: 0, rotate: 30},
            nameTextStyle: {
              padding: 20,
            }
          }
        } else if (obj.name == '按组') {
          options.xAxis = {
            data: xAxisGroupData,
            name: '按组',
            nameLocation: 'center',
            nameGap: 35,
            axisLabel: {interval: 0, rotate: 30},
            nameTextStyle: {
              padding: 20,
            }
          }
        } else if (obj.name == '按人') {
          options.xAxis = {
            data: xAxisData,
            name: '按人',
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
        that.devBugBarChart.setOption(options, true)
      })
      //echarts series.bar的点击事件，触发跳转新页面
      that.devBugBarChart.on('click', 'series.bar', obj => {
        let url = that.jira_address + "/issues/?jql=" + that.qaCreatedBugJQL + " AND developer"
        if (xAxisData.includes(obj.name)) {
          url = url + " = " + obj.name
          window.open(url, '_blank');
        } else {
          url = url + " in membersOf(" + obj.name + ")"
          window.open(url, '_blank');
        }
      });
    },

  }
}
</script>

<style scoped>

</style>
