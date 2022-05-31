export default {
  path: "/reports",
  name: "reports",
  redirect: "/reports/testCaseStat",
  components: {
    content: () => import('@/business/components/report/Reports')
  },
  children: [
    {
      path: 'testCaseStat',
      name: 'testCaseStat',
      component: () => import('@/business/components/report/testcasestat/TestCaseStat'),
    },
    {
      path: 'testPlanSummary',
      name: 'testPlanSummary',
      component: () => import('@/business/components/report/testcasepassrate/TestPlanSummary'),
    },
    {
      path: 'bugStat',
      name: 'bugStat',
      component: () => import('@/business/components/report/bugstat/BugStat'),
    },
    {
      path: 'taskStat',
      name: 'taskStat',
      component: () => import('@/business/components/report/taskstat/TaskStat'),
    },
    {
      path: 'apiQuantity',
      name: 'apiQuantity',
      component: () => import('@/business/components/report/apiautotest/ApiAutoTestQuantity'),
    }
  ]
}
