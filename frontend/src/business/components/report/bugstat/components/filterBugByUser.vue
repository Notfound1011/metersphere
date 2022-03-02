<template>
  <div>
    <div slot="header">
      <span class="title" style="color: rgba(55, 96, 186, 1); margin: 10px">
        用户创建bug统计
      </span>
    </div>
    <!-- table主体内容 -->
    <el-table :data="tableDataNew" style="width: 100% ;margin: 10px" border height="400">
      <el-table-column type="index" width="100"></el-table-column>
      <el-table-column prop="creator" label="创建者" width="200"></el-table-column>
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
import {JIRA_ADDRESS, JIRA_AUTH} from "@/common/js/constants";

export default {
  name: "filterBugByUser",
  created() {
    this.filterBugByUser()
  },
  data() {
    return {
      tableData: [],
      tableDataNew: [],
      total: 0
    }
  },
  methods: {
    filterBugByUser() {
      let url = "jira/rest/gadget/1.0/twodimensionalfilterstats/generate?filterId=filter-10869&xstattype=issuetype&ystattype=creator&sortDirection=asc&sortBy=natural&numberToShow=1000"
      this.$axios.get(url, {headers: {'Authorization': JIRA_AUTH}}).then((res) => {
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
            console.log(this.tableDataNew);
          }
        }
      )
    }
  }
}
</script>

<style scoped>

</style>
