<script lang="ts">
import { breakpointsTailwind, useBreakpoints } from '@vueuse/core'
import { useRouteQuery } from '@vueuse/router'
import { computed } from 'vue'

import DateRangeInput from '@/components/atoms/DateRangeInput.vue'
import SentimentFilter from '@/components/molecules/SentimentFilter.vue'
import { useCallMonitoringFilterContext } from '@/providers/callMonitoringFilter'
</script>

<template>
  <div v-if="lg" class="flex flex-col gap-3 sm:flex-row sm:flex-wrap sm:items-center">
    <DateRangeInput v-model:start-date="startDate" v-model:end-date="endDate" />
    <SentimentFilter v-model="sentiment" />

    <UButton
      :disabled
      :color="disabled ? 'neutral' : 'error'"
      variant="soft"
      icon="i-ph:arrow-counter-clockwise"
      @click="resetFilter"
    />
  </div>
</template>

<script setup lang="ts">
const { lg } = useBreakpoints(breakpointsTailwind)
const search = useRouteQuery<string>('s', '')
const { startDate, endDate, sentiment, reset } = useCallMonitoringFilterContext()

const disabled = computed(() => !startDate.value && !endDate.value && sentiment.value === 'all' && !search.value)

const resetFilter = () => {
  reset()
  search.value = ''
}
</script>
