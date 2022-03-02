<template>
  <ms-container>
    <ms-main-container v-loading="result.loading">
      <el-row :gutter="10">
        <el-col :span="10" class="grid-content">
          <el-card>
            <pie-bug-statistics></pie-bug-statistics>
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
import PieBugStatistics from "@/business/components/report/bugstat/components/pieBugStatistics";
import RecentCreatedBugStatistics from "@/business/components/report/bugstat/components/recentCreatedBugStatistics";
import FilterBugByUser from "@/business/components/report/bugstat/components/filterBugByUser";

export default {
  name: "BugStat",
  components: {
    FilterBugByUser,
    RecentCreatedBugStatistics,
    PieBugStatistics,
    MsMainContainer, MsContainer
  },
  data() {
    return {
      result: {}
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
  margin: 20px;
}
</style>
