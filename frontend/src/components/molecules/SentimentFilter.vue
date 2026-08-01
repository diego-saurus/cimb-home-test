<script lang="ts">
import type { SentimentFilter } from '@/types/call'

type Item = { label: string; value: SentimentFilter }

const items: Item[] = [
  { label: 'Semua', value: 'all' },
  { label: 'Di bawah 70%', value: 'BELOW_70' },
  { label: '70% atau lebih', value: 'AT_OR_ABOVE_70' },
]
</script>

<template>
  <USelect
    v-model="modelValue"
    :items
    value-key="value"
    variant="outline"
    size="lg"
    icon="i-ph:funnel"
    class="min-w-48"
  />
</template>

<script setup lang="ts">
const modelValue = defineModel<SentimentFilter>()
</script>
