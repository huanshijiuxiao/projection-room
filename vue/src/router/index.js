import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'MyHome',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/room',
    name: 'MyRoom',
    component: () => import('../views/Room.vue')
  },
  // {
  //   path: '/register',
  //   name: 'Register',
  //   component: () => import('../views/Register.vue')
  // },
  // {
  //   path: '/404',
  //   name: '404',
  //   component: () => import('../views/404.vue')
  // },
]

const router = new VueRouter({
  mode: 'history',
  routes
})

export default router
