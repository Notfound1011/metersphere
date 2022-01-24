<template>
  <el-col>
    <ms-report-header-menus/>
    <div>
      <transition>
        <keep-alive>
          <router-view :baseUrl="baseUrl"/>
        </keep-alive>
      </transition>
    </div>
  </el-col>
</template>

<script>

  import ReportHeaderMenus from "./head/ReportHeaderMenus";
  import MsReportHeaderMenus from "./head/ReportHeaderMenus";

  export default {
    name: "Reports",
    components: {MsReportHeaderMenus, ReportHeaderMenus},
    data() {
      return {
        baseUrl: "reports",
        reportType: "track",
        testCaseTrendDrawer: false,
        testCaseCountDrawer: false,
        historyReportId:"",
        reportTypes: [{id: 'track', name: this.$t('test_track.test_track')}],
      }
    },
    methods: {
    },
  }
</script>

<style scoped>

</style>
