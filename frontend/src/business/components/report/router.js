export default {
  path: "/reports",
  name: "reports",
  redirect: "/reports/home",
  components: {
    content: () => import('@/business/components/report/Reports')
  },
  children: [
    {
      path: 'home',
      name: 'reportHome',
      component: () => import('@/business/components/report/homepage/homepage'),
    },
  ]
}
