import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'welcome',
      component: () => import('@/views/WelComeView.vue'),
      children: [
        {
          path: '',
          name: 'welcome-login',
          component: () => import('@/components/welcome/loginPage.vue')
        }
      ]
    }, {
    path: '/index',
      name: 'index',
      component:() => import('@/views/IndexView.vue')

    }
  ]
})

export default router
