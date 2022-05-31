export default {
  path: "/api",
  name: "api",
  redirect: "/api/home",
  components: {
    content: () => import('@/business/components/api/ApiTest')
  },
  children: [
    {
      path: 'home',
      name: 'fucHome',
      component: () => import('@/business/components/api/homepage/ApiTestHomePage'),
    },
    // {
    //   path: "project/:type",
    //   name: "fucProject",
    //   component: MsProject,
    // },
    {
      path: 'testCaseRecord',
      name: 'testCaseRecord',
      component: () => import('@/business/components/api/autotest/TestCaseRecord'),
    },
    {
      path: 'jobScheduler',
      name: 'jobScheduler',
      component: () => import('@/business/components/api/jobscheduler/jobScheduler'),
      children: [
        {
          path: 'analysis',
          component: () => import('@/business/components/api/jobscheduler/components/analysis')
        },
        {
          path: 'reports',
          component: () => import('@/business/components/api/jobscheduler/components/reports')
        },
        {
          path: 'runJobs',
          component: () => import('@/business/components/api/jobscheduler/components/runJobs')
        }]
    },
    {
      path: 'dataFactory',
      name: 'dataFactory',
      component: () => import('@/business/components/api/dataFactory/dataFactory'),
      children: [
        {
          path: '',
          name: "dataHome",
          component: () => import('@/business/components/api/dataFactory/dataHome'),
          meta: {}
        },
        {
          path: 'account',
          component: () => import('@/business/components/api/dataFactory/components/account')
        }
      ]
    },
    {
      path: "report/list/:testId",
      name: "ApiReportList",
      component: () => import('@/business/components/api/report/ApiReportList'),
    },
    {
      path: "report/view/:reportId",
      name: "ApiReportView",
      component: () => import('@/business/components/api/report/ApiReportView'),
    },
    {
      path: "automation/report",
      name: "ApiReportList",
      component: () => import('@/business/components/api/automation/report/ApiReportList'),
    },
    {
      path: "automation/report/view/:reportId",
      name: "ApiReportView",
      component: () => import('@/business/components/api/automation/report/ApiReportView'),

    },
    {
      path: "definition/:redirectID?/:dataType?/:dataSelectRange?/:projectId?/:type?",
      name: "ApiDefinition",
      component: () => import('@/business/components/api/definition/ApiDefinition'),
    },
    {
      path: "automation/:redirectID?/:dataType?/:dataSelectRange?/:projectId?",
      name: "ApiAutomation",
      component: () => import('@/business/components/api/automation/ApiAutomation'),
    },
    {
      path: 'monitor/view',
      name: 'ApiMonitor',
      component: () => import('@/business/components/api/monitor/ApiMonitor'),
    },
    {
      path: 'definition/edit/:definitionId',
      name: 'editCompleteContainer',
      component: () => import('@/business/components/api/definition/ApiDefinition'),
    },
  ]
};
