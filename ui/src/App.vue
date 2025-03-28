<template>
  <v-app>
    <v-app-bar color="primary">
      <v-app-bar-title data-test="app-title">Task Analyzer</v-app-bar-title>
    </v-app-bar>

    <v-main class="bg-grey-lighten-3">
      <v-container>
        <v-row>
          <v-col 
            cols="12" 
            :class="columnClasses"
          >
            <TaskForm
              ref="taskForm"
              @submit="handleSubmit"
            />

            <TaskResponse
              :response="response"
            />

            <v-alert
              v-if="serverError"
              type="error"
              class="mt-4"
              data-test="error-message"
            >
              An error occurred submitting your request. Please try again later.
            </v-alert>
          </v-col>
        </v-row>
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import TaskForm from './components/TaskForm.vue'
import TaskResponse from './components/TaskResponse.vue'

interface Response {
  analysis: string
  imageUrl: string
}

declare module '@vue/runtime-core' {
  interface ComponentCustomProperties {
    $vuetify: {
      display: {
        mdAndDown: boolean
        lgAndUp: boolean
      }
    }
  }
}

const taskForm = ref()
const isLoading = ref(false)
const serverError = ref(false)
const response = ref<Response | null>(null)

const columnClasses = computed(() => ({
  'px-4': true,
  'px-16': true
}))

const handleSubmit = async (tasks: string[]) => {
  serverError.value = false
  response.value = null
  isLoading.value = true
  taskForm.value?.setLoading(true)

  try {
    const result = await fetch('http://localhost:8080/api/tasks/analyze', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ tasks }),
    })

    if (!result.ok) {
      throw new Error('Server error')
    }

    const data = await result.json()
    response.value = data
  } catch (error) {
    serverError.value = true
  } finally {
    isLoading.value = false
    taskForm.value?.setLoading(false)
  }
}
</script>

<style>
.v-container {
  max-width: none !important;
  width: 100% !important;
}

.v-row {
  margin: 0 !important;
}

.white-space-pre-line {
  white-space: pre-line;
}
</style>
