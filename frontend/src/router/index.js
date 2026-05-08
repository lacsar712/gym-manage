import { createRouter, createWebHistory } from 'vue-router'

import AdminLayout from '../layouts/AdminLayout.vue'
import Login from '../views/Login.vue'
import Members from '../views/Members.vue'
import Coaches from '../views/Coaches.vue'
import Courses from '../views/Courses.vue'
import Checkins from '../views/Checkins.vue'
import Equipments from '../views/Equipments.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', component: Login },
    {
      path: '/',
      component: AdminLayout,
      redirect: '/members',
      children: [
        { path: 'members', component: Members },
        { path: 'coaches', component: Coaches },
        { path: 'courses', component: Courses },
        { path: 'checkins', component: Checkins },
        { path: 'equipments', component: Equipments }
      ]
    }
  ]
})

router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) return '/login'
  if (to.path === '/login' && token) return '/members'
  return true
})

export default router

