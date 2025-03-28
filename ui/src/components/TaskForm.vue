<template>
  <v-card class="pa-4">
    <div class="text-body-1 mb-4">
      Please provide the top things you want to accomplish today (e.g. go for a 20 minute run, mow the lawn, prepare for meeting with Jessica at work, help daughter with science project).
    </div>
    <v-form @submit.prevent="handleSubmit" ref="form">
      <v-textarea
        v-model="tasks"
        label="Enter your tasks (comma-separated)"
        :rules="[v => !!v || 'Please enter at least one task',
                v => v.includes(',') || 'Invalid format: tasks must be comma-separated']"
        :maxlength="1024"
        :error-messages="errorMessages"
        data-test="task-input"
      ></v-textarea>

      <v-btn
        color="primary"
        type="submit"
        :loading="isLoading"
        :disabled="isLoading"
        data-test="submit-button"
      >
        Plan
      </v-btn>

      <div v-if="errorMessages.length > 0" data-test="error-message">
        {{ errorMessages[0] }}
      </div>
    </v-form>
  </v-card>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const emit = defineEmits<{
  (e: 'submit', tasks: string[]): void
}>()

const tasks = ref('')
const isLoading = ref(false)
const errorMessages = ref<string[]>([])

const handleSubmit = () => {
  errorMessages.value = []
  const taskList = tasks.value.split(',').map(t => t.trim()).filter(t => t.length > 0)
  emit('submit', taskList)
}

defineExpose({
  setLoading: (value: boolean) => {
    isLoading.value = value
  }
})
</script> 