<template>
  <div id="menu-bar" v-if="isRouterAlive">
    <el-row type="flex">
      <el-col :span="14">
        <el-menu class="header-menu" :unique-opened="true" mode="horizontal" router :default-active='$route.path'>

          <el-menu-item :index="'/reports/testCaseStat'" style="margin-left: 2%">
            {{ $t('commons.quality_market.testcase_statistics.title') }}
          </el-menu-item>
          <el-menu-item :index="'/reports/testPlanSummary'">
            {{ $t('commons.quality_market.testcase_pass_rate.title') }}
          </el-menu-item>
          <el-menu-item :index="'/reports/bugStat'">
            {{ $t('commons.quality_market.bug_statistics.title') }}
          </el-menu-item>
          <el-menu-item :index="'/reports/taskStat'">
            {{ $t('commons.quality_market.task_statistics.title') }}
          </el-menu-item>
          <el-menu-item :index="'/reports/apiQuantity'">
            {{ $t('commons.quality_market.api_autotest.title') }}
          </el-menu-item>
        </el-menu>
      </el-col>
    </el-row>
  </div>

</template>

<script>

import MsRecentList from "../../common/head/RecentList";
import MsShowAll from "../../common/head/ShowAll";
import MsCreateButton from "../../common/head/CreateButton";
// import MsCreateTest from "../../common/head/CreateTest";
import SearchList from "@/business/components/common/head/SearchList";
import ProjectChange from "@/business/components/common/head/ProjectSwitch";
import {mapGetters} from "vuex";

export default {
  name: "MsReportHeaderMenus",
  components: {SearchList, MsCreateButton, MsShowAll, MsRecentList, ProjectChange},
  data() {
    return {
      isProjectActivation: true,
      isRouterAlive: true,
      currentProject: ''
    };
  },
  computed: {
    ...mapGetters([
      'isNewVersion',
      'isOldVersion',
    ])
  },
  methods: {
    reload() {
      this.isRouterAlive = false;
      this.$nextTick(function () {
        this.isRouterAlive = true;
      });
    }
  },
  mounted() {

  },
  beforeDestroy() {

  }
};

</script>

<style scoped>
#menu-bar {
  border-bottom: 1px solid #E6E6E6;
  background-color: #FFF;
}

.menu-divider {
  margin: 0;
}

.blank_item {
  display: none;
}

.deactivation >>> .el-submenu__title {
  border-bottom: white !important;
}
</style>
