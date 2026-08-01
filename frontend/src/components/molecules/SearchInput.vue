<template>
  <UInput
    v-model="modelValue"
    icon="i-lucide-search"
    class="w-full sm:max-w-md group"
    size="lg"
    variant="none"
    :placeholder
  >
    <template v-if="modelValue" #trailing>
      <UButton
        size="xs"
        color="neutral"
        variant="ghost"
        icon="i-lucide-x"
        aria-label="Clear search"
        class="opacity-0 group-hover:opacity-100 transition-opacity group-focus-within:opacity-100"
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
