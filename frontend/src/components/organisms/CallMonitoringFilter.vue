<script lang="ts">
import { breakpointsTailwind, useBreakpoints } from '@vueuse/core'

import DateRangeInput from '@/components/atoms/DateRangeInput.vue'
import SentimentFilter from '@/components/molecules/SentimentFilter.vue'
import { useCallMonitoringFilterContext } from '@/providers/callMonitoringFilter'
</script>

<template>
  <div v-if="lg" class="flex flex-col gap-3 sm:flex-row sm:flex-wrap sm:items-center">
    <DateRangeInput v-model:start-date="startDate" v-model:end-date="endDate" />
    <SentimentFilter />
  </div>
</template>

<script setup lang="ts">
const { lg } = useBreakpoints(breakpointsTailwind)
const { startDate, endDate } = useCallMonitoringFilterContext()
</script>
