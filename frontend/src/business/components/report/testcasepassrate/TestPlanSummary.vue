<template>
  <ms-container>
    <ms-main-container>
      <test-plan-summary-list
        @openTestPlanEditDialog="openTestPlanEditDialog"
        @testPlanEdit="openTestPlanEditDialog"
        ref="testPlanSummaryList"/>
    </ms-main-container>
    <test-plan-edit ref="testPlanEditDialog" @refresh="refreshTestPlanList"/>
  </ms-container>
</template>

<script>

  import TestPlanSummaryList from './components/TestPlanSummaryList';
  import TestPlanEdit from '@/business/components/track/plan/components/TestPlanEdit';
  import MsContainer from "../../common/components/MsContainer";
  import MsMainContainer from "../../common/components/MsMainContainer";
  import {getCurrentProjectID} from "@/common/js/utils";

  export default {
    name: "TestPlanSummary",
    components: {MsMainContainer, MsContainer, TestPlanSummaryList, TestPlanEdit},
    data() {
      return {
      }
    },
    computed: {
      projectId() {
        return getCurrentProjectID();
      },
    },
    mounted() {
      if (this.$route.path.indexOf("/track/plan/create") >= 0){
        this.openTestPlanEditDialog();
        this.$router.push('/track/plan/all');
      }
    },
    watch: {
      '$route'(to, from) {
        if (to.path.indexOf("/track/plan/create") >= 0){
          if (!this.projectId) {
            this.$warning(this.$t('commons.check_project_tip'));
            return;
          }
          this.openTestPlanEditDialog();
          this.$router.push('/track/plan/all');
        }
      }
    },
    methods: {
      openTestPlanEditDialog(data) {
        this.$refs.testPlanEditDialog.openTestPlanEditDialog(data);
      },
      refreshTestPlanList() {
        this.$refs.testPlanSummaryList.condition = {};
        this.$refs.testPlanSummaryList.initTableData();
      }
    }
  }
</script>

<style scoped>

</style>
