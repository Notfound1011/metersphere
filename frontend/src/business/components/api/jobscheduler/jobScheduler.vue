<template>
  <ms-container>
    <ms-aside-container>
      <!--router	是否使用 vue-router 的模式，启用该模式会在激活导航时以 index 作为 path 进行路由跳转-->
      <el-menu router :default-active='$route.path' class="jobSchedulerMenu" active-text-color="rgb(0, 0, 255)"
               :default-openeds="open_list" :unique-opened="true">
        <el-submenu index="1">
          <template v-slot:title>
            <i class="el-icon-location"></i>
            <span>{{ $t('i18n.jobScheduler') }}</span>
          </template>
          <el-menu-item :index="'/api/jobScheduler/analysis'">
            <i class="el-icon-pie-chart"></i>
            <span slot="title"> 报表统计</span>
          </el-menu-item>
          <el-menu-item :index="'/api/jobScheduler/runJobs'">
            <i class="el-icon-video-play"></i>
            <span slot="title"> 任务运行</span>
          </el-menu-item>
          <el-menu-item :index="'/api/jobScheduler/reports'">
            <i class="el-icon-document"></i>
            <span slot="title"> 测试报告</span>
          </el-menu-item>
        </el-submenu>
      </el-menu>
    </ms-aside-container>
    <ms-main-container>
      <keep-alive>
        <router-view/>
      </keep-alive>
    </ms-main-container>
  </ms-container>
</template>

<script>
import MsMainContainer from "@/business/components/common/components/MsMainContainer";
import MsContainer from "@/business/components/common/components/MsContainer";
import MsAsideContainer from "@/business/components/common/components/MsAsideContainer";
import {jenkinsAuth} from "@/common/js/utils";

export default {
  components: {MsMainContainer, MsContainer, MsAsideContainer},
  name: "jobScheduler",
  created() {
    this.timer = setInterval(() => {
      setTimeout(this.setJenkinsInfo, 0)
    }, 1000 * 1800)
  },
  activated() {
    this.setJenkinsInfo()
  },
  destroyed() {
    clearInterval(this.timer);
    this.timer = null;
  },
  data() {
    return {
      JenkinsJobList: [],
      json: {
        Jenkins_Crumb:''
      },
      open_list:["1"],
      jenkins_auth: jenkinsAuth()
    }
  },
  methods: {
    setJenkinsInfo() {
      this.$axios.get("/jenkins/crumbIssuer/api/xml",
        {
          params: {'xpath': 'concat(//crumbRequestField,":",//crumb)'},
          headers: {'Authorization': this.jenkins_auth}
        }).then(res => {
        if (res.status === 200) {
          this.json.Jenkins_Crumb = res.data.split(":")[1];
          localStorage.setItem("JenkinsInfo", JSON.stringify(this.json));
        }
      })
      // 获取jobList任务列表
      // this.$axios.post("/jenkins/api/json?tree=jobs[name,url,builds[number,result,duration,timestamp,url]{0,1}]", null,
      //   {headers: {'Authorization': 'Basic dGVzdDoxMjM0NTY=', 'Jenkins-Crumb': this.Jenkins_Crumb}}).then((res) => {
      //   if (res.status === 200) {
      //     this.tableData = res.data.jobs
      //     this.tableData.forEach(e => {
      //       this.JenkinsJobList.push(e.name)
      //     })
      //   }
      //   this.json.JenkinsJobList = noRepeat(this.JenkinsJobList);
      //   localStorage.setItem("JenkinsInfo", JSON.stringify(this.json));
      // }).catch(() => {
      //   }
      // )
    }
  }
}
</script>

<style scoped>
.jobSchedulerMenu {
  border-right: 0;
}
</style>
