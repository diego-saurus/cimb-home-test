<template>
  <UInput v-model="modelValue" icon="i-lucide-search" class="w-full sm:max-w-sm" size="lg" :placeholder>
    <template v-if="modelValue" #trailing>
      <UButton
        size="xs"
        color="neutral"
        variant="ghost"
        icon="i-lucide-x"
        aria-label="Clear search"
        @click="modelValue = ''"
      />
    </template>
  </UInput>
</template>

<script setup lang="ts">
import type { InputProps } from '@nuxt/ui'

withDefaults(defineProps<InputProps>(), {
  placeholder: 'Search...',
})

const modelValue = defineModel<string>()
</script>
