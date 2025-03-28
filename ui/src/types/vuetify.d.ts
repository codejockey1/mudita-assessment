import 'vue'

declare module 'vue' {
  interface ComponentCustomProperties {
    $vuetify: {
      display: {
        mdAndDown: boolean
        lgAndUp: boolean
      }
    }
  }
} 