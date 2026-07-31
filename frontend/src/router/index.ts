import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: { name: 'call-monitoring' },
    },
    {
      path: '/call-monitoring',
      name: 'call-monitoring',
      component: () => import('@/pages/CallMonitoringPage.vue'),
    },
  ],
})

export default router
