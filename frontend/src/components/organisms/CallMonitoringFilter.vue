<script lang="ts">
import { breakpointsTailwind, useBreakpoints } from '@vueuse/core'

import DateRangeInput from '@/components/atoms/DateRangeInput.vue'
import SentimentFilter from '@/components/molecules/SentimentFilter.vue'
</script>

<template>
  <div v-if="lg" class="flex flex-col gap-3 sm:flex-row sm:flex-wrap sm:items-center">
    <DateRangeInput />
    <SentimentFilter />
  </div>
</template>

<script setup lang="ts">
const { lg } = useBreakpoints(breakpointsTailwind)
</script>
