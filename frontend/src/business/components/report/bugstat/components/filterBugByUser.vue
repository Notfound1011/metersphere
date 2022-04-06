<template>
  <div>
    <div slot="header">
      <span class="cardTitle" style="color: rgba(55, 96, 186, 1); margin: 10px">
        开发bug统计
      </span>
    </div>
    <!-- table主体内容 -->
    <el-table :data="tableDataNew" style="width: 100% ;margin: 10px" border height="440">
      <el-table-column type="index" label="序号" width="100"></el-table-column>
      <el-table-column prop="creator" label="developer" width="200" sortable></el-table-column>
      <el-table-column prop="total" label="总计" width="100" align="center" sortable>
        <template slot-scope="scope">
          <el-link :href='scope.row.url' target="_blank">
            <div v-if="scope.row.url !== '' && scope.row.url != null" style="font-size: 15px; color: blue">
              {{ scope.row.total }}
            </div>
          </el-link>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import {jiraAuth, jiraAddress} from "@/common/js/utils";

export default {
  name: "filterBugByUser",
  created() {
    this.filterBugByUser()
  },
  data() {
    return {
      tableData: [],
      tableDataNew: [],
      total: 0,
      jira_auth: jiraAuth(),
      jira_address: jiraAddress()
    }
  },
  methods: {
    filterBugByUser() {
      let url = "jira/rest/gadget/1.0/twodimensionalfilterstats/generate?filterId=filter-10869&xstattype=issuetype&ystattype=customfield_10300&sortDirection=desc&sortBy=total&numberToShow=1000"
      this.$axios.get(url, {headers: {'Authorization': this.jira_auth}}).then((res) => {
          if (res.status === 200) {
            this.tableData = res.data.rows
            this.total = res.data.totalRows
            // console.log(this.tableData, this.total = res.data.totalRows)

            this.tableData.forEach((value) => { //数组循环
                var valueObj = {}
                valueObj["creator"] = value["cells"][0]["markup"];
                valueObj["url"] = value["cells"][1]["markup"].split(/>|<|'/)[2];
                valueObj["total"] = value["cells"][1]["markup"].split(/>|<|'/)[4];
                this.tableDataNew.push(valueObj)
              }
            )
          }
        }
      )
    }
  }
}
</script>

<style scoped>
.cardTitle {
  color: rgba(55, 96, 186, 1);
  margin: 10px 0;
  font-size: 20px;
  font-weight: bold;
}
</style>
