<template>
  <v-card v-if="response" class="mt-4 pa-4" data-test="response-card">
    <v-img
      :src="response.imageUrl"
      contain
      class="mb-4"
      style="width: 100%; height: auto; max-height: 600px;"
      data-test="response-image"
    ></v-img>
    <div class="text-body-1 white-space-pre-line" data-test="response-analysis">{{ response.analysis }}</div>
  </v-card>
</template>

<script setup lang="ts">
interface Response {
  analysis: string
  imageUrl: string
}

defineProps<{
  response: Response | null
}>()
</script>

<style scoped>
.white-space-pre-line {
  white-space: pre-line;
}
</style> 