import { createRouter, createWebHistory } from 'vue-router'
import {isUnauthorized} from "@/net/index.js";

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
        }, {
          path: '/register',
          name: 'welcome-register',
          component: () => import('@/components/welcome/RegisterPage.vue')
        }, {
          path: '/reset',
          name: 'welcome-reset',
          component: () => import('@/components/welcome/ResetPage.vue')
        }

      ]
    }, {
    path: '/index',
      name: 'index',
      component:() => import('@/views/IndexView.vue')
    }
  ]
})

// 路由首位
router.beforeEach((to, from, next)  => {
  const isUnauthorize = isUnauthorized()
  if(to.name.startsWith("welcome-") && !isUnauthorize) {
    next('/index')
  } else if(to.fullPath.startsWith('/index') && isUnauthorize) {
    next('/')
  } else {
    next()
  }
})


export default router
