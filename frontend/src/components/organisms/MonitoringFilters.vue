<script setup lang="ts">
import DateInput from '@/components/atoms/DateInput.vue'
import SearchInput from '@/components/molecules/SearchInput.vue'
import SentimentFilter from '@/components/molecules/SentimentFilter.vue'
</script>

<template>
  <div class="flex flex-col gap-3 lg:flex-row lg:flex-wrap lg:items-center lg:justify-between">
    <SearchInput />

    <div class="flex flex-col gap-3 sm:flex-row sm:flex-wrap sm:items-center">
      <DateInput trigger-placeholder="Select Start Date" />
      <DateInput trigger-placeholder="Select End Date" />

      <SentimentFilter />

      <UButton color="neutral" variant="ghost" icon="i-lucide-rotate-ccw" label="Reset" size="sm" />
    </div>
  </div>
</template>
