<template>
  <ms-container>
    <ms-aside-container>
      <!--router	是否使用 vue-router 的模式，启用该模式会在激活导航时以 index 作为 path 进行路由跳转-->
      <el-menu router :default-active='$route.path' class="jobSchedulerMenu">
        <el-menu-item  :index="'/api/jobScheduler/analysis'">
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
import {noRepeat} from "../../../../common/js/utils";
export default {
  components: {MsMainContainer, MsContainer, MsAsideContainer},
  name: "jobScheduler",
  created() {
    const JenkinsInfo = JSON.parse(localStorage.getItem("JenkinsInfo"));
    this.Jenkins_Crumb = JenkinsInfo.Jenkins_Crumb;
  },
  activated(){
    this.setJenkinsInfo()
  },
  data(){
    return{
      JenkinsJobList : [],
      Jenkins_Crumb: '',
      json:{}
    }
  },
  methods:{
    setJenkinsInfo(){
      this.$axios.get("/jenkins/crumbIssuer/api/xml",
        {params:{'xpath':'concat(//crumbRequestField,":",//crumb)'},
          headers: {'Authorization': 'Basic dGVzdDoxMjM0NTY='}}).then(res =>{
        if (res.status === 200){
          this.json.Jenkins_Crumb = res.data.split(":")[1];
          localStorage.setItem("JenkinsInfo", JSON.stringify(this.json));
        }
      })
      this.$axios.post("/jenkins/api/json?tree=jobs[name,url,builds[number,result,duration,timestamp,url]{0,1}]", null,
        {headers: {'Authorization': 'Basic dGVzdDoxMjM0NTY=', 'Jenkins-Crumb': this.Jenkins_Crumb}}).then((res) => {
        if (res.status === 200) {
          this.tableData = res.data.jobs
          this.tableData.forEach(e => {
            this.JenkinsJobList.push(e.name)
          })
        }
        this.json.JenkinsJobList = noRepeat(this.JenkinsJobList);
        localStorage.setItem("JenkinsInfo", JSON.stringify(this.json));
      })
    },

  }
}
</script>

<style scoped>
.jobSchedulerMenu {
  border-right: 0;
}
</style>
