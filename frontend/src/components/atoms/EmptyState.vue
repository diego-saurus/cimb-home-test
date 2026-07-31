<script setup lang="ts">
interface Props {
  title?: string
  description?: string
  icon?: string
}

withDefaults(defineProps<Props>(), {
  title: 'No matching records',
  description: 'Try adjusting your search or filters.',
  icon: 'i-lucide-search-x',
})
</script>

<template>
  <div class="flex flex-col items-center justify-center gap-3 py-16 text-center">
    <UIcon :name="icon" class="size-10 text-muted" />
    <div class="space-y-1">
      <p class="text-base font-medium text-highlighted">{{ title }}</p>
      <p class="text-sm text-muted">{{ description }}</p>
    </div>
  </div>
</template>
