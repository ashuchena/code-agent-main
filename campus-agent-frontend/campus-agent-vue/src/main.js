import Vue from 'vue'
import App from './App.vue'
import router from './router'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'
import axios from 'axios'
import VueCookies from 'vue-cookies'
import { hydrateAuthCookies } from './utils/auth'

Vue.config.productionTip = false

Vue.use(VueCookies)
hydrateAuthCookies()

axios.defaults.baseURL = '/'
axios.defaults.withCredentials = true
Vue.prototype.$axios = axios

function dispatchMessage(type, text) {
  window.dispatchEvent(new CustomEvent('campus-message', {
    detail: {
      id: `${Date.now()}-${Math.random().toString(16).slice(2)}`,
      type,
      text
    }
  }))
}

Vue.prototype.$message = {
  info(text) {
    dispatchMessage('info', text)
  },
  success(text) {
    dispatchMessage('success', text)
  },
  warning(text) {
    dispatchMessage('warning', text)
  },
  error(text) {
    dispatchMessage('error', text)
  }
}

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
