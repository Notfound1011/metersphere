<template>
  <ms-container>
    <ms-main-container v-loading="result.loading">
      <h2 style="margin-left: 20px;font-weight:bold">{{ currentYear + "年度bug统计" }}</h2>
      <el-row :gutter="10">
        <el-col :span="10" class="grid-content">
          <el-card>
            <bug-by-status-pie-stat></bug-by-status-pie-stat>
          </el-card>
        </el-col>
        <el-col :span="12" class="grid-content">
          <el-card>
            <recent-created-bug-statistics></recent-created-bug-statistics>
          </el-card>
        </el-col>
      </el-row>
      <el-row :gutter="10">
        <el-col :span="10" class="grid-content">
          <el-card>
            <bug-by-project-pie-stat></bug-by-project-pie-stat>
          </el-card>
        </el-col>
        <el-col :span="12" class="grid-content">
          <el-card>
            <filter-bug-by-user></filter-bug-by-user>
          </el-card>
        </el-col>
      </el-row>
    </ms-main-container>
  </ms-container>
</template>

<script>
import MsContainer from "@/business/components/common/components/MsContainer";
import MsMainContainer from "@/business/components/common/components/MsMainContainer";
import PieBugStatistics from "@/business/components/report/bugstat/components/bugByProjectPieStat";
import RecentCreatedBugStatistics from "@/business/components/report/bugstat/components/recentCreatedBugStatistics";
import FilterBugByUser from "@/business/components/report/bugstat/components/filterBugByUser";
import BugByProjectPieStat from "@/business/components/report/bugstat/components/bugByProjectPieStat";
import BugByStatusPieStat from "@/business/components/report/bugstat/components/bugByStatusPieStat";

export default {
  name: "BugStat",
  components: {
    BugByStatusPieStat,
    BugByProjectPieStat,
    FilterBugByUser,
    RecentCreatedBugStatistics,
    PieBugStatistics,
    MsMainContainer, MsContainer
  },
  data() {
    return {
      result: {},
      currentYear: new Date().getFullYear().toString()

    }
  },
  methods: {
    reload() {
      this.loading = true
      this.$nextTick(() => {
        this.loading = false
      });
    },

  }
}
</script>

<style scoped>

.grid-content {
  border-radius: 4px;
  min-height: 36px;
  margin: 15px;
}
</style>
