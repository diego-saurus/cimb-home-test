<script setup lang="ts">
import type { SentimentFilter } from '@/types/monitoring'

type Item = { label: string; value: SentimentFilter }

const items: Item[] = [
  { label: 'Semua', value: 'all' },
  { label: 'Di bawah 70%', value: 'below_70' },
  { label: '70% atau lebih', value: 'above_70' },
]

const value = defineModel<SentimentFilter>()
</script>

<template>
  <USelectMenu v-model="value" :items value-key="value" icon="i-lucide-funnel" class="min-w-48" />
</template>
